# @MethodRef
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-methodReference/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-methodReference)

Since 0.1

## Goal
Provide a compile safe method reference when use annotation.

## Usage

There are 4 modes.

### 1. Referenced by Full Name

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


### 2. Class and Method
Use `@MethodRef(type = Type.CLASS)` on a `Class<?>` attribute and `@MethodRef(type = Type.METHOD)` on a `String` attribute together.
Reference method from the class by the method attribute's value. 
The `Class` attribute can also set a parent class, if the class attribute value is `void.class`,
the parent annotation value will be used. 

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

With parent class

```java
//define
@interface UseClassAndMethodWithParent {
  @interface Parent {
    Class<?> value();
  }

  @MethodRef(type = Type.CLASS, parentClass = Parent.class)
  Class<?> type() default void.class;

  @MethodRef(type = Type.METHOD)
  String method();
}

//usage
@Parent(Integer.class)
class Bar{
  @UseClassAndMethodWithParent(method = "intValue")
  void func();
  
  @UseClassAndMethodWithParent(method = "isNaN", type = Double.class)
  void foo();
}
```

### 3. DefaultClass 
Use `Type.METHOD` with `defaultClass`.
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

### 4. ParentClass
Use `Type.METHOD` with `parentClass`
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

## Eclipse snapshot
![eclipse-use-methodref](../doc/snapshot/eclipse-use-methodref.jpg)