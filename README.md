## 用户指南
<a href="/">访问首页</a>
### 操作界面
![](https://xiafei-web.oss-cn-hangzhou.aliyuncs.com/file-server/1546587344873.png)


### 设计思想
1. 根据pom三坐标去maven仓库获取jar包  
2. 通过Java `ClassLoader`机制远程获取`Class`对象  
3. 反射遍历对象,生成随机数据  

### 使用方式
1. 通过http接口使用
2. java代码引入使用

### 服务端使用
<a href="/">访问首页</a>  

1. 填入pom坐标  
2. 选择要造数据的对象
3. 点击Mock接口按钮

最终会跳转到类似下面的接口地址  
 [/com.qccr.shprod/shprod-facade/3.9.9.9-SNAPSHOT/com.qccr.shprod.facade.entity.employee.EmployeeTeamRO]()

#### 可选配置
##### 一、strut
数据接口类型

1. `Result<RO>` 普通对象  bean
2. `Result<List<RO>` 数组  list
3. `Result<Page<RO>` 分页  page


##### 二、listSize
数组大小
##### 三、depth
递归深度

### 程序代码使用
引入
```
<dependency>
  <groupId>org.wing.mocker</groupId>
  <artifactId>mocker-core</artifactId>
  <version>2.0</version>
</dependency>
```
手动生成数据
``` java
//生成mock数据
new org.wing.mocker.core.MockData().mock(Class);
```
### 自定义Mock数据
pom引入注解jar包
```xml
<dependency>
  <groupId>org.wing.mocker</groupId>
  <artifactId>annotation</artifactId>
  <version>1.0</version>
</dependency>
```

```java
public class ProductRO  {
    //测试数据将从@MockValue随机取值
    @MockValue({"精细洗车","普通洗车"})
    private String productName;

    @MockValue({"2500"})
    private Integer productPrice;
}
```
### 服务端安装
##### 1.修改maven仓库地址
修改 `mocker-http/src/main/resources/application.properties`
nexus.server
##### 2.启动
spring-boot 启动`org.wing.mocker.http.MockerHttpApplication#main`




### 注意事项
1. java对象的字段命名要规范 例如：
    2. boolean不要写is  
    3. 字段首字母不要大写
2. facade不要相互依赖
3. facade不要依赖三方包 例如
    4. apache-commons

