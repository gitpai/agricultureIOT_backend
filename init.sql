create table sys_role(id int AUTO_INCREMENT PRIMARY KEY , name char(100));
create table sys_user(id int AUTO_INCREMENT PRIMARY KEY , username char(100), password char(100));
create table sys_user_roles(sys_user_id int, roles_id int);


insert  into `sys_role`(`id`,`name`) values (1,'ROLE_CREATOR'),(2,'ROLE_ADMIN'),(3,'ROLE_EXPERT');
INSERT INTO `sys_role`(`id`,`name`) values(4,'ROLE_USER');
insert  into `sys_user`(`id`,`password`,`username`) values (1,'root','root'),(2,'sang','sang');

insert  into `sys_user_roles`(`sys_user_id`,`roles_id`) values (1,1),(2,2);

create table t_gateway(id int AUTO_INCREMENT PRIMARY KEY ,name char(250), ip char(20), port int, max_nodes int, max_channels int);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 1','192.168.1.1', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 2','192.168.1.2', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 3','192.168.1.3', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 4','192.168.1.4', 505, 64,32);
insert into `t_gateway`(`name`,`ip`, `port`, `max_nodes`, `max_channels`) values ('gateway 5','192.168.1.5', 505, 64,32);

alter table t_zigbee_node add gateway_id int(10) default '1';