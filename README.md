# Annotation-EX
[![Build Status](https://travis-ci.org/XDean/Annotation-EX.svg?branch=master)](https://travis-ci.org/XDean/Annotation-EX)
[![codecov.io](http://codecov.io/github/XDean/Annotation-EX/coverage.svg?branch=master)](https://codecov.io/gh/XDean/Annotation-EX/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/Annotation-EX/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/Annotation-EX)

# Release

**maven**

```xml
<dependency>
    <groupId>com.github.XDean</groupId>
    <artifactId>Annotation-EX</artifactId>
    <version>0.1</version>
</dependency>
```

# Features
- [@MethodRef](#methodref)
- [Use in Eclipse](#use-in-eclipse)


## @MethodRef
### Goal
Provide a compile safe method reference when use annotation.

### Usage

There are 4 usage modes.

#### Referenced by Full Name

Annotated on a String attribute, its value will be parsed to class name and method name.
Note that the default splitor is ':'.

Example

```java
//define
@interface UseAll {
  @MethodRef //default type is All
  String value();
}

//usage
@UseAll("java.lang.String:length")
void func();
```


#### Class and Method
Use `@MethodRef(type = Type.CLASS)` on a `Class<?>` attribute and `@MethodRef(type = Type.METHOD)` on a String attribute together.
Reference method from the class by the method attribute's value.

Example

```java
//define
@interface UseClassAndMethod {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();
}

//usage
@UseClassAndMethod(type = Integer.class, method = "intValue")
void func();
```

#### DefaultClass 
Use `Type.METHOD` with `defaultClass()`.
Reference method from the determined class.

```java
//define
@interface UseDefaultClass {
  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method();
}

//usage
@UseDefaultClass(method = "length")
void func();
```

#### ParentClass
Use `Type.METHOD` with `parentClass()`
Reference method from class by its EnclosingElement(usually a class)'s annotation's value.
Note that the parent annotation must have a Class attribute named 'value'
 
```java
//define
@interface UseParentClass {
  @interface Parent {
    Class<?> value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();
}

//usage
@Parent(String.class)
class Bar{
  @UseParentClass(method = "length")
  void func();
}
```

### Eclipse use snapshot
![eclipse-use-methodref](doc/snapshot/eclipse-use-methodref.jpg)
  
## Use in Eclipse
1. Right click on your project, select `Java Compiler -> Annotation Processing`, enable the 3 options.
![eclipse-setting-1](doc/snapshot/eclipse-setting-1.jpg)
2. Right click on your project, select `Java Compiler -> Annotation Processing -> Factory Path`, add jars.
Note the dependencies order.
![eclipse-setting-2](doc/snapshot/eclipse-setting-2.jpg)

Or you can use [m2e-apt](https://marketplace.eclipse.org/content/m2e-apt) plugin.