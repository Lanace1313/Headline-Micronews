-- 创建数据库
create database wx_news;

-- 使用数据库
use wx_news;

create table user (
  id int unsigned primary key auto_increment comment 'ID',
  username varchar(20) comment '昵称',
  password varchar(32) not null comment '密码',
  phone varchar(11) not null unique comment '手机号',
  role enum('ROOT','USER') not null default 'USER' comment '身份',
  email varchar(128) default '' comment '邮箱',
  avatar varchar(128) default '' comment '头像',
  create_time datetime not null comment '创建时间'
) comment '用户表';

create table category(
id int unsigned primary key auto_increment comment 'ID',
 category_name varchar(32) not null comment '分类名称',
articles_num int not null default 0 comment '文章数',
)comment '分类表';

create table article(
id int unsigned primary key auto_increment comment 'ID',
title varchar(30) not null comment '文章标题',
content varchar(10000) not null comment '文章内容',
cover_img varchar(128) not null  comment '文章封面',
state varchar(3) default '草稿' comment '文章状态: 只能是[已发布] 或者 [草稿]',
category_id int unsigned comment '文章分类ID',
user_id int unsigned not null comment '创建人ID',
publish_time datetime not null comment '发布时间',
collection_num int not null default 0 comment '收藏数',
constraint fk_article_category foreign key (category_id) references category(id),-- 外键约束
constraint fk_article_user foreign key (user_id) references user(id) -- 外键约束
)comment '文章表';

create table collection(
id int unsigned primary key auto_increment comment 'ID',
user_id int unsigned not null comment '用户ID',
article_id int  unsigned not null comment '文章ID',
constraint fk_collection_article foreign key (article_id) references article(id),-- 外键约束
constraint fk_collection_user foreign key (user_id) references user(id) -- 外键约束
)comment '收藏表';