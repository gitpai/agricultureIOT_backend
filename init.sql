create table sys_role(id int AUTO_INCREMENT PRIMARY KEY , name char(100));
create table sys_user(id int AUTO_INCREMENT PRIMARY KEY , username char(100), password char(100));
create table sys_user_roles(sys_user_id int, roles_id int);

insert  into `sys_role`(`id`,`name`) values (1,'ROLE_CREATOR'),(2,'ROLE_ADMIN'),(3,'ROLE_EXPERT');
INSERT INTO `sys_role`(`id`,`name`) values(4,'ROLE_USER');
insert  into `sys_user`(`id`,`password`,`username`) values (1,'root','root'),(2,'sang','sang');

insert  into `sys_user_roles`(`sys_user_id`,`roles_id`) values (1,1),(2,2);