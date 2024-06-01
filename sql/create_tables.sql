-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userName     varchar(256) default '用户'            not null comment '用户名',
    userAcount   varchar(256)                           null comment '账号',
    avatarUrl    varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userPassword varchar(512)                           not null comment '密码',
    phone        varchar(128)                           null comment '电话',
    email        varchar(64)                            null comment '邮箱',
    userStatus   int          default 0                 null comment '状态0正常',
    createTime   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 null comment '是否删除',
    userRole     int          default 0                 not null comment '用户的角色，0普通用户，1管理员',
    planetCode   varchar(512)                           null comment '星球编号'
)
    comment '用户';

