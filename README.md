# FirstCrawler - 58同城招聘信息爬虫

## 项目简介
这是一个基于Selenium WebDriver的Java爬虫项目，主要用于抓取58同城网站上的招聘信息。

## 功能特点
- 开箱即用，自动抓取58同城招聘页面的工作信息
- GUI界面，用户友好，上手简单
- 数据自动导出为CSV格式，同时存储到mysql数据库
- 基本的用户登录/注册/更改密码功能，用户信息存储在mysql数据库
- 管理员用户可在会话中执行SQL语句进行查询和更新，并获得即时反馈
- 易于扩展其他功能，本项目保留了除58同城招聘爬取的其他两个示例
- 内置浏览器静默模式，减少干扰

## 环境要求
- JDK 11+
- Maven
- Chrome浏览器
- ChromeDriver (与Chrome浏览器版本对应)
- Edge浏览器 (可选)
- EdgeDriver (可选)

## 快速开始
1. 克隆项目到本地
```git
git clone https://github.com/darkwilldie/FirstCrawler.git
```
2. 安装依赖
```shell
mvn clean install
```
3. 配置MySQL数据库
```sql
-- 创建数据库
CREATE DATABASE 58tongcheng;
-- 创建用户表
USE 58tongcheng;
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- 插入管理员用户
INSERT INTO users (username, password, name, role) 
VALUES ('admin', '111111', '管理员', 'admin');
-- 创建招聘信息表
CREATE TABLE job_info (
id INT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
title VARCHAR(100) COMMENT '职位名称',
company VARCHAR(100) COMMENT '公司名称',
salary VARCHAR(50) COMMENT '薪资范围',
location VARCHAR(100) COMMENT '工作地点',
experience VARCHAR(50) COMMENT '经验要求',
education VARCHAR(50) COMMENT '学历要求',
headcount INT COMMENT '招聘人数',
publish_date VARCHAR(50) COMMENT '发布日期',
)
```
4. 运行程序
```shell
java -jar target/FirstCrawler.jar
```

## 项目结构

```tree
FirstCrawler/
├─crawler
│     JobCrawler.java
│     NewsThread.java
├─dao
│     JobDAO.java
│     PswdDAO.java
│     SqlDAO.java
│     UserDAO.java
├─test
│     JobCrawlerTest.java
│     SeleniumTest.java
│     WTUNewsCrawlerTest.java
├─tools
│     CrawlerTools2.java
│     CSVTools.java
│     JDBCTools.java
├─view
│     LoginUI.java
│     Main.java
│     MysqlUI.java
│     OprationUI.java
│     PswdUI.java
└─vo
      JobInfo.java
      News.java
      User.java
```


## 注意事项
- 本项目的开发环境为 Windows11 JDK17.0.10 maven3.9.9 VScode
- 使用前请确保ChromeDriver版本与本地Chrome浏览器版本匹配，或使用edge
- 确保已正确配置Mysql数据库
- 需要手动完成网站验证码，程序检测到验证码会进入30秒休眠
- 建议设置适当的爬取间隔，避免对目标网站造成压力
