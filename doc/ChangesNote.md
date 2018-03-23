# 0.1
- `@MethodRef`
  - 4 basic modes
  - nested dependent(use file to record annotations use `@MethodRef`)
  - test by using `com.google:compile-testing`

# 0.1.1
- Reorganize, import modules `Annotation-EX-parent`. Now can dependent `Annotation-EX` directly 
- `@MethodRef` support `Class&Method+Parent` mode
- Use `AnnotationProcessorToolkit:0.1.2`

# 0.1.2
- `@MethodRef` support `findInEnclosing`

# 0.2.1
- import `@Aggregation`

# 0.2.2
 - `@Aggregation`
   - allow add attribute by `@Attribute`
   - > add compile checker
   - > Handle nested situation