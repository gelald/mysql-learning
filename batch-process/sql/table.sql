create table batch_maintain
(
    maintain_id       int           null,
    maintain_num      varchar(255)  null,
    maintain_name     varchar(1024) null,
    equipment_num     bigint        null,
    maintain_type     int           null,
    functionary       varchar(255)  null,
    maintain_duration varchar(255)  null,
    start_time        datetime      null,
    end_time          datetime      null,
    maintain_status   tinyint       null
) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_general_ci;

