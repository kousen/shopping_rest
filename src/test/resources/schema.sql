drop table if exists products;
create table products
(
    id    int         not null auto_increment,
    name  varchar(50) not null,
    price decimal     not null,
    primary key (id)
)