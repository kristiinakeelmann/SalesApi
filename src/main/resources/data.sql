drop table if exists replacement_product;
drop table if exists order_product;
drop table if exists sales_order;
drop table if exists product;

create table product
(
    id    int primary key,
    name  varchar(25),
    price decimal
);

create table sales_order
(
    id       uuid primary key,
    status   varchar(25),
    discount decimal,
    paid     decimal,
    returns  decimal,
    total    decimal
);

create table order_product
(
    id           uuid primary key,
    quantity     int,
    productId    int not null,
    salesOrderId uuid not null,
    foreign key (productId) references product (id),
    foreign key (salesOrderId) references sales_order (id)
);

create table replacement_product
(
    id             uuid primary key,
    quantity       int,
    productId      int not null,
    orderProductId uuid not null,
    foreign key (productId) references product (id),
    foreign key (orderProductId) references order_product (id)
);

insert into product
values (123, 'Ketchup', 0.45);
insert into product
values (456, 'Beer', 2.33);
insert into product
values (879, 'Õllesnäkk', 0.42);
insert into product
values (999, '75" OLED TV', 1333.37);
