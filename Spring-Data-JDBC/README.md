Spring Service Guide
=====================
转载请注明：**Powered by li3huo.com**  
技术交流请关注新浪微博 @li3huo

# Spring Framework

## What is Spring Framework?

//TODO add more desc
Spring是一个著名的企业应用程序开发框架， 百万计的开发者使用Spring框架创造高性能、  
易测试、高重用且非侵入的代码。

##[FEATURES](http://www.springsource.org/features)  
* Modern Web  
Complete support for modern applications including REST, HTML 5,   
conversations and AJAX.

* Data Access
Supports traditional RDBMS as well as new NoSQL solutions,   
map-reduce frameworks and cloud based data services.

* Integration
Enterprise orchestration and adapters for distributed applications,   
asynchronous message-based applications, and batch applications.

* Security
Authorization control for all tiers and authentication integration   
to dozens of providers.

[TUTORIALS](http://www.springsource.org/tutorials)


## 应用开发框架

### 代码结构


## Spring学习之基于注解的容器配置
* Refer to <Spring Framework Reference Documentation> 5.9. Annotation-based container configuration

### 5.9.2 @Autowired  
``
  @Autowired
  private MovieCatalog movieCatalog;

  private CustomerPreferenceDao customerPreferenceDao;

  @Autowired
  public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
      this.customerPreferenceDao = customerPreferenceDao;
  }
  @Autowired
  public void prepare(MovieCatalog movieCatalog,
                      CustomerPreferenceDao customerPreferenceDao) {
      this.movieCatalog = movieCatalog;
      this.customerPreferenceDao = customerPreferenceDao;
  }
``
其它可以Autowired的对象：
BeanFactory, ApplicationContext, Environment, ResourceLoader,   
ApplicationEventPublisher, and MessageSource

###  用@Qualifier控制 @Autowiring
5.9.3. Fine-tuning annotation-based autowiring with qualifiers



5.9.4. CustomAutowireConfigurer

### @Resource
5.9.5. @Resource

``
  @Resource(name="myMovieFinder")
  public void setMovieFinder(MovieFinder movieFinder) {
      this.movieFinder = movieFinder;
  }
``

5.9.6. @PostConstruct and @PreDestroy
``

``

## 设计模式：DAO
* 

## Snapshot与异常处理体系
* 

## 其他参考

* [Spring Framework Reference Documentation]

## 后续计划

* 
