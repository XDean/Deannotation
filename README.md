# Deannotation
[![Build Status](https://travis-ci.org/XDean/Deannotation.svg?branch=master)](https://travis-ci.org/XDean/Deannotation)
[![codecov.io](http://codecov.io/github/XDean/Deannotation/coverage.svg?branch=master)](https://codecov.io/gh/XDean/Deannotation/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.XDean/deannotation-parent)

# Release

See in each module.

# Features
- [@MethodRef](method-reference)
- [@Aggregation](aggregation)
- [@AutoMessage](https://github.com/XDean/auto-message)
- [Use in Eclipse](#use-in-eclipse)
- [Version changes](doc/ChangesNote.md)


# Use in Eclipse
1. Right click on your project, select `Java Compiler -> Annotation Processing`, enable the 3 options.
![eclipse-setting-1](doc/snapshot/eclipse-setting-1.jpg)
2. Right click on your project, select `Java Compiler -> Annotation Processing -> Factory Path`, add jars.
Note the dependencies order.
![eclipse-setting-2](doc/snapshot/eclipse-setting-2.jpg)

Or you can use [m2e-apt](https://marketplace.eclipse.org/content/m2e-apt) plugin.