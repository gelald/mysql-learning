# Batch-Process

> 这个模块主要介绍 MySQL 数据批量保存和导出

## 批量保存

在数据量很大的情况下，如果还是使用简单的 for 循环 + 保存方法，那么本批数据的保存时间一定是非常长的，因为数据 IO
次数就由数据量来决定，磁盘 IO 是非常消耗资源的操作，所以我们要尽可能地降低磁盘 IO 次数。
由于批量提交 SQL 到数据库的操作是需要客户端的内存空间的，所以也要对这一批需要保存的数据进行切分，切分成一批批「大小适合」的数据块，然后每次提交这些数据块。
另外，数据保存也涉及到事务提交，一次事务提交一个 insert 语句，显然资源利用也不够充分，所以批处理也要把手动事务提交打开，一次提交一批数据的事务。
**事务大小是否能被 MySQL 接收也要看 MySQL 关于事务的缓存大小，感兴趣的小伙伴可以研究一下，究竟提交多大的事务，才是最优的方案。
**

批量保存有 3 种方式，原生 JDBC 、MyBatis、MyBatis-Plus，我们三种都试一试效果。
在程序中 strategy 目录下有 3 种方式的策略：`JDBCBatchStrategy`、`MyBatisBatchStrategy`、`MyBatisPlusBatchStrategy`

### JDBC

JDBC 的方式是先把每次填充完参数的 SQL 缓存下来，最后由 prepareStatement 进行批量地提交到数据库中，
感兴趣的小伙伴也可以试试使用 insert into values() () () 的方式。

因为是使用原生 JDBC 方式，所以我们需要自己获取连接，自己关闭连接，这一步相对繁琐。

```java
public class JDBCBatchStrategy {
    public void doImport(List<Maintain> maintains) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            // 开启事务手动提交
            connection.setAutoCommit(false);
            String sql = "insert into batch_maintain (maintain_num, maintain_name, equipment_num, maintain_type, functionary, maintain_duration, start_time, end_time, maintain_status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            int count = 0;
            for (Maintain maintain : maintains) {
                // 填充数据
                count++;
                if (count % BATCH_SIZE == 0) {
                    // 一次性提交缓存区中的SQL语句
                    preparedStatement.executeBatch();
                    log.info("本次提交{}条数据", BATCH_SIZE);
                    count = 0;
                }
            }
            if (count != 0) {
                preparedStatement.executeBatch();
                log.info("最后提交{}条数据", BATCH_SIZE);
            }
            // 提交事务
            connection.commit();
            log.info("事务提交");
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

### MyBatis

MyBatis 的批量方式其实和 JDBC 的批量方式差异不大，也是对 SQL 语句的封装，在 Mapper.xml 文件中编写好 SQL 语句。

比较重要的一点是，MyBatis 如果要进行批处理的话需要使用一个可以批处理的 SqlSession，
然后用这个 SqlSession 来获取 Mapper 的代理对象，这样调用才能使用批量处理模式。

```java
public class MyBatisBatchStrategy {
    public void doImport(List<Maintain> maintains) {
        // 按每一批大小切割原集合
        List<List<Maintain>> lists = this.splitList(maintains);
        // 获取一个批量处理模式、关闭自动提交事务的SqlSession
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            // 用这个创建出来的sqlSession获取Mapper，否则配置不生效
            MaintainMapper maintainMapper = sqlSession.getMapper(MaintainMapper.class);
            for (List<Maintain> list : lists) {
                // 每次插入一批数据
                maintainMapper.insertBatchMaintain(list);
                log.info("本次提交{}条数据", list.size());
                // 清除statementList
                sqlSession.flushStatements();
            }
            // 提交事务
            sqlSession.commit();
            log.info("事务提交");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```

### MyBatis-Plus

MyBatis-Plus 的批量方式在框架中已经封装好了，`saveBatch` 方法中，默认也会对传来的数据进行二次分批，
如果是用 BaseService 中的方法，那么我们无法控制事务是一次性提交的，
框架的方式是采用每提交一小批数据到缓存中就提交一次事务并清空缓存，所以性能上是稍微欠缺一点。

```java
public class MyBatisPlusBatchStrategy {
    public void doImport(List<Maintain> maintains) {
        try {
            // 使用MyBatis-Plus封装的批处理方法
            this.maintainService.saveBatch(maintains, BATCH_SIZE);
            log.info("本次提交{}条数据", maintains.size());
            log.info("事务提交");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```

### rewriteBatchedStatements 参数

在 JDBC 连接上添加这个参数，作用是将一批
insert into xxx values (), insert into xxx values (), ... 转换拼接成 insert into xxx values (),(),()...
这样一条语句的形式然后执行，这样一来跟拼接 sql 的效果是一样的。

那为什么默认不给这个参数设置为 true 呢？

1. 如果批量语句中的某些语句失败，则默认重写会导致所有语句都失败。
2. 批量语句的某些语句参数不一样，则默认重写会使得查询缓存未命中。

但是如果本来调用的 Mapper 就是用 SQL 拼接的方式，那么添加这个参数是会更慢的，**所以如果使用 SQL 拼接的方式这个参数需要关闭
**。

| 保存方式                                     | 数据量（条） | 耗时（秒） |
|------------------------------------------|--------|-------|
| JDBC 原生                                  | 30000  | 3.3   |
| JDBC 原生（添加 rewrite 参数）                   | 30000  | 2.2   |
| MyBatis 底层使用拼接 SQL 语句                    | 30000  | 3.5   |
| MyBatis 底层使用拼接 SQL 语句（添加 rewrite 参数）     | 30000  | 6.8   |
| MyBatis-Plus saveBatch 方法                | 30000  | 9.8   |
| MyBatis-Plus saveBatch 方法（添加 rewrite 参数） | 30000  | 7.2   |

## Excel 数据批量导出、导入

其实想到数据的导入导出，理所当然的会想到apache的poi技术，以及Excel的版本问题。

### 传统 POI 版本优缺点比较

> 这里引用网上大佬的资料

WorkBook 接口有 3 个实现类

- HSSFWorkbook

这个实现类是我们早期使用最多的对象，它是POI版本中最常用的方式，它可以操作Excel2003以前（包含2003）的所有Excel版本。在2003以前Excel的版本后缀还是.xls

缺点：最多只能导出 65535行，也就是导出的数据函数超过这个数据就会报错；

优点：不会报内存溢出。（因为数据量还不到7w所以内存一般都够用，首先你得明确知道这种方式是将数据先读取到内存中，然后再操作）

- XSSFWorkbook

这个实现类现在在很多公司都可以发现还在使用，它是操作的Excel2003--Excel2007之间的版本，Excel的扩展名是.xlsx

优点：这种形式的出现是为了突破HSSFWorkbook的65535行局限，是为了针对Excel2007版本的1048576行，16384列，最多可以导出104w条数据；

缺点：伴随的问题来了，虽然导出数据行数增加了好多倍，但是随之而来的内存溢出问题也成了噩梦。因为你所创建的book，Sheet，row，cell等在写入到Excel之前，都是存放在内存中的（这还没有算Excel的一些样式格式等等），可想而知，内存不溢出就有点不科学了！！！

- SXSSFWorkbook

这个实现类是POI3.8之后的版本才有的，提供了一种基于XSSF的低内存占用的SXSSF方式，它可以操作Excel2007以后的所有版本Excel，扩展名是.xlsx

优点：

这种方式不会一般不会出现内存溢出（它使用了硬盘来换取内存空间，
也就是当内存中数据达到一定程度这些数据会被持久化到硬盘中存储起来，而内存中存的都是最新的数据），
并且支持大型Excel文件的创建（存储百万条数据绰绰有余）。

缺点：

既然一部分数据持久化到了硬盘中，且不能被查看和访问那么就会导致，
在同一时间点我们只能访问一定数量的数据，也就是内存中存储的数据；
sheet.clone()方法将不再支持，还是因为持久化的原因；
不再支持对公式的求值，还是因为持久化的原因，在硬盘中的数据没法读取到内存中进行计算；
在使用模板方式下载数据的时候，不能改动表头，还是因为持久化的问题，写到了硬盘里就不能改变了；

### 阿里巴巴 EasyExcel

> EasyExcel 官网是这么描述自己的

easyexcel重写了poi对07版Excel的解析，一个3M的excel用POI sax解析依然需要100M左右内存，改用easyexcel可以降低到几M，
并且再大的excel也不会出现内存溢出；03版依赖POI的sax模式，在上层做了模型转换的封装，让使用者更加简单方便

EasyExcel 既高效又不会产生内存溢出，所以我们选择它作为 Excel 的操作工具库。

### Excel 导出

Excel 导出这个过程没有太多需要注意的，按格式、顺序地写入到 Excel 文件即可，只是要把文件写入到响应中

```java
// 下载EXCEL
response.setHeader("Content-Disposition","attachment;filename=ExportResult.xlsx");
        response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
```

### Excel 导入

Excel 导入需要使用 ReadListener 来完成，需要注意的是，这个接口的实现类不能被 spring 管理，要每次读取 excel 都要 new,然后里面用到
bean 等属性需要使用构造方法来传

```java
public class ExcelDataListener implements ReadListener<Maintain> {
    /**
     * 每次读取数据调用的方法
     */
    @Override
    public void invoke(Maintain data, AnalysisContext context) {
        excelDataList.add(data);
        //size大于预定义的数量就执行一次批量插入
        if (excelDataList.size() >= savePerScanRow) {
            saveData(excelDataList);
            //清理集合便于GC回收
            excelDataList.clear();
        }
    }

    /**
     * Excel文件读取完触发
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //如果最后遍历完，集合还有数据，那么就需要把这一部分的数据也写入
        if (!CollectionUtils.isEmpty(excelDataList)) {
            saveData(excelDataList);
            excelDataList.clear();
        }
    }
}
```