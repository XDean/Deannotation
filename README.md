# Deannotation
[![Build Status](https://travis-ci.org/XDean/Annotation-EX.svg?branch=master)](https://travis-ci.org/XDean/Annotation-EX)
[![codecov.io](http://codecov.io/github/XDean/Annotation-EX/coverage.svg?branch=master)](https://codecov.io/gh/XDean/Annotation-EX/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/Annotation-EX/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/Annotation-EX)

# Release

**maven**

```xml
<dependency>
    <groupId>com.github.XDean</groupId>
    <artifactId>Annotation-EX</artifactId>
    <version>0.1.2</version>
</dependency>
```

Or

```xml
<dependency>
  <groupId>com.github.XDean</groupId>
  <artifactId>Annotation-EX-api</artifactId>
  <version>0.1.2</version>
</dependency>
<dependency>
  <groupId>com.github.XDean</groupId>
  <artifactId>Annotation-EX-processor</artifactId>
  <version>0.1.2</version>
</dependency>
```

# Features
- [@MethodRef](method-reference/README.md)
- [Use in Eclipse](#use-in-eclipse)
- [Version changes](doc/ChangesNote.md)


# Use in Eclipse
1. Right click on your project, select `Java Compiler -> Annotation Processing`, enable the 3 options.
![eclipse-setting-1](doc/snapshot/eclipse-setting-1.jpg)
2. Right click on your project, select `Java Compiler -> Annotation Processing -> Factory Path`, add jars.
Note the dependencies order.
![eclipse-setting-2](doc/snapshot/eclipse-setting-2.jpg)

Or you can use [m2e-apt](https://marketplace.eclipse.org/content/m2e-apt) plugin.