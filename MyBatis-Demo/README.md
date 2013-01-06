# MyBatis Demo

*转载请注明：Powered by li3huo.com*

## What is MyBatis?

MyBatis是一个一流的持久性框架，支持自定义的SQL、存储过程和高级映射：
MyBatis几乎消除了所有的JDBC代码、手工设置参数及获取结果；
MyBatis可以使用简单的XML或注解（Annotations）的方式配置和映射基本数据类型（primitives）、
映射接口和Java的POJO （普通Java对象）到数据库中的记录。

<img src#"http://mybatis.org/images/flow.png" >

### History of MyBatis

2002年, Clinton Begin开始编写代码，其后将其捐献给了Apache基金会，成立了 iBatis 项目；
2010年5月，将代码库迁致 Google Code，并更名为 MyBatis；
2012年7月，MyBatis迎来了十岁生日

refer to http://blog.mybatis.org/2012/07/happy-birthday-to-mybatis-10-years.html

## Basic Conceptions

refer to mybatis-3.1.1.pdf: 2 Getting Started

### Building SqlSessionFactory
 1. from XML
 2. by coding in Java
 
### Acquiring a SqlSession from SqlSessionFactory

### Exploring Mapped SQL Statements

### 关键对象的作用域和生命周期

<table>
    <tr>
        <td>SqlSessionFactoryBuilder</td>
        <td>初始化后即可丢弃</td>
    </tr>
    <tr>
        <td>SqlSessionFactory</td>
        <td>应用级的单例</td>
    </tr>
    <tr>
        <td>SqlSession</td>
        <td>方法级，需要主动关闭</td>
    </tr>
    <tr>
        <td>Mapper Instances</td>
        <td>方法级，自动关闭</td>
    </tr>
</table>

## 手工建立MyBatis应用程序

### DB Initialising（using hsqldb）
run org.hsqldb.util.DatabaseManager to config db
add schema.sql&init_data.sql to jdbc:hsqldb:file:db/test
提交的代码中已经完成

### Create MyBatis Configuration
hsqldb.properties
``
jdbc.driverClassName=org.hsqldb.jdbcDriver
# hsqldb engine standalone
jdbc.url=jdbc:hsqldb:file:db/test
jdbc.username=sa
jdbc.password=
``
mybatis-config.xml
此时还没有引入`typeAliases`和`Mappers`元素

### Create Domain and Mapper
see package com.li3huo.mybatis.domain
一个实体对象需要有一个Domain类、一个DomainMapper接口和一个DomainMapper.xml配置文件

完成以上设置后再往mybatis-config.xml中添加响应的`typeAliases`和`Mappers`元素

### Create DaoTest

### 
