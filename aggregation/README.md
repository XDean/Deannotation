# @Aggregation
Since 0.2
<sub>**Not support java9**</sub>

## Goal
Aggregate multiple annotations as one.

Also see [this question](https://stackoverflow.com/questions/33345605/java-custom-annotation-aggregate-multiple-annotations)

## Usage

For instance, there is [a spring application](http://projects.spring.io/spring-framework/#quick-start):

```java
@Configuration
@ComponentScan
public class SpringApplication {
...
}
```

We want to aggregate the two annotations together. Use `@Aggregation`, we define:

```java
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Aggregation(template = Template.class)
public @interface ScanConfig {
  @ComponentScan
  @Configuration
  class Template {
  }
}
```

then use the annotation on the application instead of the original two:

```java
@ScanConfig
public class SpringApplication {
...
}
```

But above is not enough, you have two choices to  expand the aggregated annotations:

1. Use `AggregationHandler.handle` before use the class:

```java
Class<SpringApplication> clz = AggregationHandler.handle(SpringApplication.class);
ApplicationContext context = new AnnotationConfigApplicationContext(clz);
```

2. Use `AggregationLoader` to load the class. For example, you can set the default class loader by set property `-Djava.system.class.loader=xdean.annotation.handler.AggregationLoader`. 

Finally, we can run our application:

> **Hello World!**
