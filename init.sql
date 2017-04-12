create database gateway;
use gateway;
create table sys_role(id int AUTO_INCREMENT PRIMARY KEY , name char(100));
create table sys_user(id int AUTO_INCREMENT PRIMARY KEY , username char(100), password char(100));
create table sys_user_roles(sys_user_id int, roles_id int);


insert  into `sys_role`(`id`,`name`) values (1,'ROLE_CREATOR'),(2,'ROLE_ADMIN'),(3,'ROLE_EXPERT');
INSERT INTO `sys_role`(`id`,`name`) values(4,'ROLE_USER');
insert  into `sys_user`(`id`,`password`,`username`) values (1,'root','root'),(2,'sang','sang');

insert  into `sys_user_roles`(`sys_user_id`,`roles_id`) values (1,1),(2,2);

create table t_gateway(id BIGINT(20) AUTO_INCREMENT PRIMARY KEY ,name char(250), ip char(20), port int, max_nodes int, max_channels int);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 1','192.168.1.1', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 2','192.168.1.2', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 3','192.168.1.3', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 4','192.168.1.4', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 5','192.168.1.5', 505, 64,32);


alter table t_gateway add poll_interval int default 30;
alter table t_gateway add  X FLOAT DEFAULT 0;
ALTER TABLE t_gateway add Y FLOAT DEFAULT 0;
ALTER TABLE t_gateway add desc_string char(250);
ALTER TABLE t_gateway add pic char(250);
create table t_nodeinfo(id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        gateway_id BIGINT(20),
                        node_addr TINYINT(3),
                        node_name char(250),
                        X FLOAT DEFAULT 0,
                        Y FLOAT DEFAULT 0,
                        desc_string char(250),
                        pic char(250));

#==============================


alter table t_zigbee_node add gateway_id int(10) default '1';

alter table t_sensor add gateway_id int(10) default '1';
alter table t_sensor add node_addr tinyint (4) default '3';


alter table t_sensor add created datetime DEFAULT CURRENT_TIMESTAMP;
update  t_sensor,t_zigbee_node set t_sensor.created=t_zigbee_node.created where t_sensor.readout_id=t_zigbee_node.id;




create table t_warning(id bigint(20) NOT NULL PRIMARY KEY ,
                        threashold_id bigint(20) NOT NULL ,
                        warn_type TINYINT(1) NOT NULL DEFAULT '0',
                        warn_status TINYINT(1) NOT NULL DEFAULT '1',
                        created DATETIME NOT NULL ,
                        closed DATETIME DEFAULT NULL,
                        readout_id BIGINT(20));

create table t_thresholdinfo(
  id BIGINT(20) NOT NULL PRIMARY KEY ,
  gateway_id BIGINT(20) NOT NULL ,
  node_addr TINYINT(3) NOT NULL ,
  channel TINYINT(3) NOT NULL ,
  upper_limit FLOAT DEFAULT NULL,
  lower_limit FLOAT DEFAULT NULL
)