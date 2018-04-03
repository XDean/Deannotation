# `@Aggregation`
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-aggregation/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-aggregation)

Since 0.2<br>
<sub>**Not support java9**</sub>

## Goal
Aggregate multiple annotations as one.

Also see [this question](https://stackoverflow.com/questions/26910008/grouping-multiple-annotations)

## Usage

### Example

For instance, there is [a spring application](http://projects.spring.io/spring-framework/#quick-start):<br>
<sub>Since Spring can handle its [meta-annotations](https://docs.spring.io/spring/docs/5.0.0.RELEASE/spring-framework-reference/core.html#beans-meta-annotations), This is just an example</sub>

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

Then use the annotation on the application instead of the original two:

```java
@ScanConfig
public class SpringApplication {
...
}
```

But above is not enough, you have two choices to  expand the aggregated annotations:

1. Use `AggregationReflectHandler.expand` before use the class:

```java
Class<SpringApplication> clz = AggregationReflectHandler.expand(SpringApplication.class);
ApplicationContext context = new AnnotationConfigApplicationContext(clz);
```

2. Use `AggregationClassLoader` to load the class. For example, you can set the default class loader by set property `-Djava.system.class.loader=xdean.annotation.handler.AggregationClassLoader`. Note if you use this way, add the optional  `javassist` to your dependencies.

Finally, we can run our application:

> **Hello World!**

### Use `@Attribute`

You can use `@Attribute` to declare the aggregated annotation's attribute in the aggregate annotation. For example:

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
  
  @Attribute(type = Configuration.class)
  String name() default "";
}
```

the attribute `name` in `ScanConfig` will be equal `value` in `Configuration`.

```java
@ScanConfig(name = "MyName")
public class SpringApplication {
...
}
```

will equivalence to

```java
@ComponentScan
@Configuration("MyName")
public class SpringApplication {
...
}
```