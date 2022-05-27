
drop table if exists `user`;

create table `user` (
  id varchar(36) not null,
  username varchar(100) not null,
  password varchar(200) not null,
  name varchar(200) not null,
  email varchar(200) not null,
  address varchar(200) null,
  city varchar(200) null,
  country varchar(200) null,
  postcode varchar(20) null,
  coordinates varchar(200) null,
  timezone varchar(200) null,
  nationality char(2) null,
  created_by varchar(200) not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

alter table `user` add constraint user_pk primary key (id);
