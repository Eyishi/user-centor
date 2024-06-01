#### ant desgin pro右下角的悬浮窗不显示

$ husky install
fatal: not a git repository (or any of the parent directories): .git
Done in 26.34s.

原因：没有git init初始化管理仓库





### mysql表

```sql
-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userName     varchar(256)                       not null comment '用户名',
    userAcount   varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        int                                null comment '邮箱',
    userStatus   int      default 0                 null comment '状态0正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 null comment '是否删除'
)
    comment '用户';
```



3.登录 / 注册

1. 后端

   1. 实现基本数据库的操作(user表)

      实体类user对应=》数据库user（直接插件MyBatisX生成domain实体对象）把对应的mapper，service文件移动就行，mapper.xml（定义了mapper对象和数据库的关系）

      generateAllSetter插件，自动set数据

      **注意：**mybatis plus会自动把驼峰解析成下划线到数据库中查找，

      **解决**（在配置文件中加）：

      ```
      mybatis:
        configuration:
          map-underscore-to-camel-case: false
      ```

   2. 登录逻辑和注册

      - 前端页面输入账号密码，以及校验码
      - 检验用户的账号、密码、校验密码
        1. 账户**不小于**4位
        2. 密码**不小于**8位
        3. 账号不能重复
        4. 账户不包含特殊字符
        5. 密码和校验码相同

   3. 对密码进行加密

   4. 向数据库插入数据

   ​		
   
   ### 登录接口
   
   接收参数：用户账户、密码
   
   请求类型：POST
   
   请求体：json格式
   
   **逻辑**
   
   1.检验用户账户和密码是否合法
   
   1. 账户**不小于**4位
   2. 密码**不小于**8位
   3. 非空
   4. 账户不包含特殊字符
   
   2.检验密码是否输入正确，要和数据库中的密文密码去对比
   
   3.用户信息脱敏，隐藏敏感信息
   
   3.记录用户的登录态（session），将其存到服务器
   
   4.返回用户信息（脱敏，隐藏敏感信息）
   
   ### 如何知道是哪个用户登录了
   
   1.连接到服务器后，得到一个session1状态，返回前端
   
   2.登录成功后，得到了登录成功的session，并且给session设置一些值，返回前端一个设置cookie的“命令”
   
   session=》cookie
   
   3.前端收到后端命令后，设置cookie，保存到浏览器
   
   4.前端再次请求后端的时候（相同域名），在请求头中带上cookie请求
   
   5.后端拿到cookie，找到对应的session
   
   6.后端从session中取出基于该session存储的变量（用户登录信息，登录名）
   
   

### 		控制层contrller封装请求

```
@Restcontroller 适用于编写restful风格api，返回值默认json
```

​	controller层倾向于请求参数本身的检验，不涉及业务逻辑本身

service层是对业务逻辑的校验（可被controller之外的调用）

### 用户管理接口

1.查询用户

​		根据用户名查询

2.删除用户







获取用户当前用户信息接口





3.用户注销接口

直接在session中移除这个用户就行了



### 后端优化

1.通用返回对象

```
{
	"code":0 //业务状态码
	"data":{
		"id":
		"name":
	}
}
```

1. 返回错误，正确的信息

2.封装全局异常处理

​	1.封装业务异常类

​	2.全局异常处理器

​		1.捕获服务器内部的服务，不把服务器的  错误返回给前端

​			实现：spring aop 调用前后做处理

application-prod.yml：表示生产环境

## 前端

yarn start 启动



退出登录在 HeaderDropdown



## 多环境

  部署到服务器

centos密码：wjzwjz520..

安装：nginx

本地centos： nginx：/home/ikun/service/nginx-1.21.6

/usr/local/nginx/sbin/nginx

记住在修改了环境变量后要执行source /etc/profile





把项目压缩包打包到centos，更改nginx配置信息，在usr/local/nginx/conf  里面的配置

还要把里面的工作用户设置为：root

##### 后端

yum install -y java-1.8.0-openjdk

curl -o apache-maven-3.9.7-bin.tar.gz  https://dlcdn.apache.org/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.tar.gz

tar -zxvf apache-maven-3.9.7-bin.tar.gz 