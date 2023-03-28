# MyBatis-Plus-Common

> 这个模块主要介绍 MyBaits-Plus 常用的操作

## 简单的增删查改

MyBatis-Plus 提供了 `BaseMapper`、`BaseService` 等基础接口及其实现类，对于一些常用的方法，都做了封装，我们不需要再写这些简单操作的
SQL 语句了

| 方法名          | 含义         |
|--------------|------------|
| `insert`     | 插入一条数据     |
| `deleteById` | 根据id删除数据   |
| `selectById` | 根据id查询一条数据 |
| `updateById` | 根据id更新这条数据 |

另外也保留了编写 SQL 语句的方式，在模块的 Mapper 接口中定义方法，然后找到对应的 mapper.xml 文件，就可以编写 SQL 语句

## 枚举类型的字段

Employee 中有一个 `gender`
性别字段，在数据库存储中，我们都希望尽可能使用占用空间小的数据类型来存储数据，所以使用了 `Integer` 类型，
但是又想在数据查询中方便地知道(中文)
这个员工的性别，那么这个时候可以使用枚举类型，然后在枚举类中的存储到数据库中的字段加上一个 `@EnumValue`
注解，声明这个枚举类型的字段最终会作为数据库存储的字段

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("mpcommon_employee")
public class Employee {
    /**
     * 主键字段
     */
    @TableId
    private Long id;
    // 省略其他信息
    /**
     * 枚举值字段
     */
    private GenderEnum gender;
    // 省略其他信息
}
```

```java

@Getter
@AllArgsConstructor
public enum GenderEnum {
    FEMALE(0, "女"),
    MALE(1, "男");

    /**
     * 标记存储时使用的时code
     */
    @EnumValue
    private final Integer code;
    private final String sexName;
}
```

## 逻辑删除

如果需要使用逻辑删除，当删除时不进行实际删除，更新逻辑删除字段；在查询时自动过滤被逻辑删除的数据

可以使用 MyBatis-Plus 提供的 `@TableLogic` 注解，能帮助我们自动完成上述任务

```java
public class Employee {
    /**
     * 逻辑删除字段
     * 布尔型字段不使用is开头，所以要配合TableField
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Boolean logicDelete;
}
```

## 插入数据时自动填充

创建人、修改人、创建时间、修改时间这些通用的字段，在每次插入/修改数据时都需要进行赋值，我们希望这部分的操作可以自动完成

MyBatis-Plus 也提供了自动填充的机制，在字段上的 `@TableField` 注解中指定 `fill` 属性，声明是在插入还是修改数据时需要自动填充

另外实现 `MetaObjectHandler` 接口即可实现
```java
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入数据时自动填充公共字段");
        // 两种写法都可以
        // this.strictInsertFill(metaObject, "createdDate", LocalDate.class, LocalDate.now());
        // this.strictInsertFill(metaObject, "updatedDate", LocalDate.class, LocalDate.now());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", CurrentUserContext.getId());
        metaObject.setValue("updateUser", CurrentUserContext.getId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新数据时自动填充公共字段");
        // 两种写法都可以
        // this.strictUpdateFill(metaObject, "updatedDate", LocalDate.class, LocalDate.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", CurrentUserContext.getId());
    }
}
```

## 分页插件

分页插件需要在项目启动时注册到插件列表中，然后在开发中构建分页对象 `Page(long current, long size)`

```java
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @GetMapping("/page")
    public Page<Employee> page(@RequestParam Integer currentPage,
                               @RequestParam Integer pageSize) {
        log.info("分页查询，当前页码:{}，每页{}条数据", currentPage, pageSize);
        Page<Employee> page = new Page<>(currentPage, pageSize);
        return this.employeeService.page(page);
    }
}
```
