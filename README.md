# Annotation-EX
[![Build Status](https://travis-ci.org/XDean/Annotation-EX.svg?branch=master)](https://travis-ci.org/XDean/Annotation-EX)
[![codecov.io](http://codecov.io/github/XDean/Annotation-EX/coverage.svg?branch=master)](https://codecov.io/gh/XDean/Annotation-EX/branch/master)

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

- [@MethodRef](#MethodRef)


## @MethodRef
### Goal
Provide a compile safe method reference when use annotation.

### Usage
There are 4 types of MethodRef:

- Type.ALL

Annotated on a String attribute, its value will be parsed to class name and method name. 

Example:

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

- Type.METHOD 

Annotated on a String attribute, it has following modes:

1. Class and Method. Use with another attribute with Type.CLASS, reference method from class by the other attribute's value.
Note that if you use Class and Method, there must have and only have 2 attribute with @MethodRef 

Example

```
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

2. DefaultClass. Use with MethodRef.defaultClass(), reference method from the determined class

```
//define
@interface UseDefaultClass {
  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method();
}

//usage
@UseDefaultClass(method = "length")
void func();
```

3. ParentClass. Use with MethodRef.parentClass(), reference method from class by its EnclosingElement(usually a class)'s annotation's value.
Note that the parent annotation must have a Class attribute named 'value'
 
```
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
  
### Use in Eclipse
