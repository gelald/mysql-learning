CREATE DATABASE `mysql_learning` CHARSET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE `mpcommon_employee`
(
    `id`          bigint(20) NOT NULL COMMENT '主键',
    `user_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
    `age`         int(11)                                                       DEFAULT NULL COMMENT '年龄',
    `gender`      int(11)                                                       DEFAULT '0' COMMENT '性别',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                      DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '上次更新时间',
    `create_user` bigint(20)                                                    DEFAULT NULL COMMENT '创建人',
    `update_user` bigint(20)                                                    DEFAULT NULL COMMENT '修改人',
    `is_delete`   tinyint(4) NOT NULL                                           DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

INSERT INTO `mpcommon_employee` (`id`, `user_name`, `age`, `gender`, `create_time`, `update_time`, `create_user`, `update_user`, `is_delete`) VALUES (1, 'super_admin', 20, 1, '2023-01-01 09:00:15', '2023-01-01 09:00:27', NULL, NULL, 0);
