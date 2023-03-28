# MySQL-Learning

> 这个项目主要记录了一些操作 MySQL 的各类典型场景，另外还有记录持久层框架的常用方法
>
> 适合入门学习的小伙伴，也可以作为平时开发持久层代码时参考的资源
>
> 我的知识库👉[Javrin](https://github.com/gelald/javrin)，希望可以帮到你！希望可以给我一个Star，亮起小星星🌟～

## 模块说明

> 这里主要介绍父模块和公用模块，其他模块在各自模块中再介绍

### mysql-learning

这个是所有模块的父工程，主要是统一子工程使用的依赖版本，统一打包配置

本项目使用的主要依赖的版本:

| 🔧依赖                         | 📖版本    |
|------------------------------|---------|
| spring-boot                  | 2.6.1   |
| mysql-connector-java         | 8.0.27  |
| mybatis                      | 3.5.2   |
| mybatis-plus                 | 3.5.9   |
| spring-boot-starter-data-jpa | 2.6.1   |

### mysql-base

这个模块负责了一些基础相关的工作，能让其他模块更专注持久层的使用：

- 统一异常处理
- 统一包装响应
- knife4j 统一配置

### mybatis-plus-common

这个模块主要介绍一些 MyBatis-Plus 框架中的常用操作，具体请看 [mybatis-plus-common](mybatis-plus-common/README.md)