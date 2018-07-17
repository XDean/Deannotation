package xdean.annotation.methodref;

import static xdean.annotation.processor.toolkit.ElementUtil.getAnnotationClassValue;
import static xdean.annotation.processor.toolkit.ElementUtil.getAnnotationMirror;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.google.auto.service.AutoService;

import xdean.annotation.methodref.MethodRef.Type;
import xdean.annotation.processor.toolkit.AssertException;
import xdean.annotation.processor.toolkit.CommonUtil;
import xdean.annotation.processor.toolkit.NestCompileFile;
import xdean.annotation.processor.toolkit.XAbstractProcessor;
import xdean.annotation.processor.toolkit.annotation.SupportedAnnotation;

@AutoService(Processor.class)
@SupportedAnnotation(MethodRef.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MethodRefProcessor extends XAbstractProcessor {
  /**
   * Use a file to record which annotations used {@link MethodRef} for nested dependency.
   */
  private static final String RECORD_FILE = "META-INF/xdean/annotation/MethodRef";
  private final NestCompileFile methodRefRecord = new NestCompileFile(RECORD_FILE);
  private TypeMirror classType, methodRefType, voidType;
  private Set<TypeElement> visitedClassAndMethod = new HashSet<>();
  private Set<String> allMethodRefAnnotations = new HashSet<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    classType = types.erasure(elements.getTypeElement(Class.class.getCanonicalName()).asType());
    methodRefType = elements.getTypeElement(MethodRef.class.getCanonicalName()).asType();
    voidType = types.getNoType(TypeKind.VOID);
  }

  @Override
  public boolean processActual(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      generateConfigFiles(roundEnv);
    } else {
      valid(annotations, roundEnv);
    }
    return false;
  }

  private void generateConfigFiles(RoundEnvironment roundEnv) {
    try {
      PrintStream writer = methodRefRecord.getPrintStream(filer);
      allMethodRefAnnotations.forEach(writer::println);
      writer.flush();
    } catch (IOException e) {
      error().log("Unable to create " + RECORD_FILE + " because " + e.getMessage() + ":\n" + CommonUtil.getStackTraceString(e));
    }
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>(super.getSupportedAnnotationTypes());
    try {
      methodRefRecord.readLines().forEach(set::add);
    } catch (IOException e) {
      error().log("error happened when read record file: " + e.getMessage() + ":\n" + CommonUtil.getStackTraceString(e));
    }
    return set;
  }

  private void valid(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Stream.concat(
        annotations.stream()
            .flatMap(typeElement -> ElementFilter.methodsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(ee -> ee.getAnnotation(MethodRef.class) != null)),
        roundEnv.getElementsAnnotatedWith(MethodRef.class)
            .stream())
        .distinct()
        .forEach(e -> handleAssert(() -> valid(assertType(e, ExecutableElement.class)
            .todo(() -> error().log("MethodRef can only annotate on method element.", e)), roundEnv)));
  }

  private void valid(ExecutableElement annotatedMethod, RoundEnvironment roundEnv) throws AssertException {
    debug().log("To validate: " + annotatedMethod);
    MethodRef mr = annotatedMethod.getAnnotation(MethodRef.class);
    TypeElement annotatedClass = assertType(annotatedMethod.getEnclosingElement(), TypeElement.class)
        .todo(() -> error().log("@MethodRef method must defined in a class.", annotatedMethod));
    assertThat(annotatedClass.getKind() == ElementKind.ANNOTATION_TYPE)
        .todo(() -> error().log("@MethodRef can only annotated on @interface class's method.", annotatedMethod));
    BiFunction<Element, AnnotationMirror, String[]> getClassAndMethod;
    if (mr.type() == Type.ALL) {
      getClassAndMethod = useAll(annotatedMethod, mr);
    } else if (mr.type() == Type.METHOD && mr.findInEnclosing()) {
      getClassAndMethod = useEnclosing(annotatedMethod, mr);
    } else if (mr.type() == Type.METHOD
        && !types.isSameType(getAnnotationClassValue(elements, mr, MethodRef::defaultClass), voidType)) {
      getClassAndMethod = useDefaultClass(annotatedMethod, mr);
    } else if (mr.type() == Type.METHOD
        && !types.isSameType(getAnnotationClassValue(elements, mr, MethodRef::parentClass), methodRefType)) {
      getClassAndMethod = useParentClass(annotatedMethod, mr);
    } else {
      getClassAndMethod = useClassAndMethod(annotatedClass);
    }
    valid(annotatedClass, getClassAndMethod, roundEnv);
    allMethodRefAnnotations.add(annotatedClass.getQualifiedName().toString());
  }

  private BiFunction<Element, AnnotationMirror, String[]> useEnclosing(ExecutableElement annotatedMethod, MethodRef mr) {
    return (e, am) -> new String[] { e.getEnclosingElement().asType().toString(),
        elements.getElementValuesWithDefaults(am).get(annotatedMethod).getValue().toString() };
  }

  private BiFunction<Element, AnnotationMirror, String[]> useAll(ExecutableElement annotatedMethod, MethodRef mr) {
    char splitor = mr.splitor();
    return (e, am) -> {
      AnnotationValue av = elements.getElementValuesWithDefaults(am).get(annotatedMethod);
      String value = av.getValue().toString();
      String[] split = value.split(Pattern.quote(Character.toString(splitor)));
      assertThat(split.length == 2)
          .todo(() -> error().log("The method reference must be $ClassName" + splitor + "$MethodName", e, am, av));
      return split;
    };
  }

  private BiFunction<Element, AnnotationMirror, String[]> useDefaultClass(ExecutableElement annotatedMethod, MethodRef mr) {
    String className = getAnnotationClassValue(elements, mr, MethodRef::defaultClass).toString();
    return (e, am) -> new String[] { className,
        elements.getElementValuesWithDefaults(am).get(annotatedMethod).getValue().toString() };
  }

  private BiFunction<Element, AnnotationMirror, String[]> useParentClass(ExecutableElement annotatedMethod, MethodRef mr) {
    TypeMirror parentClass = getAnnotationClassValue(elements, mr, MethodRef::parentClass);
    ExecutableElement valueMethod = assertParentDefine(annotatedMethod, parentClass);
    return (e, am) -> {
      Element enclosingElement = e.getEnclosingElement();
      AnnotationMirror defaultAnnotation = assertNonNull(getAnnotationMirror(enclosingElement, parentClass).orElse(null))
          .todo(() -> {
            error().log("Should annotated by @" + parentClass.toString(), enclosingElement);
            error().log("Can't find parent annotation @" + parentClass.toString() + " on enclosing element. ", e, am);
          });
      return new String[] {
          elements.getElementValuesWithDefaults(defaultAnnotation).get(valueMethod).getValue().toString(),
          elements.getElementValuesWithDefaults(am).get(annotatedMethod).getValue().toString() };
    };
  }

  private BiFunction<Element, AnnotationMirror, String[]> useClassAndMethod(TypeElement annotatedClass) {
    assertThat(visitedClassAndMethod.add(annotatedClass))
        .todo(() -> debug().log("This annotation has been visisted: " + annotatedClass));
    ExecutableElement[] refMethods = ElementFilter.methodsIn(elements.getAllMembers(annotatedClass))
        .stream()
        .filter(e -> {
          MethodRef methodRef = e.getAnnotation(MethodRef.class);
          if (methodRef == null) {
            return false;
          }
          return methodRef.type() == Type.CLASS || (methodRef.type() == Type.METHOD &&
              methodRef.findInEnclosing() == false &&
              types.isSameType(getAnnotationClassValue(elements, methodRef, MethodRef::defaultClass), voidType) &&
              types.isSameType(getAnnotationClassValue(elements, methodRef, MethodRef::parentClass), methodRefType));
        })
        .toArray(ExecutableElement[]::new);
    assertThat(refMethods.length == 2)
        .todo(() -> error().log(
            "When use @MethodRef.Type.CLASS&METHOD, the annotation must have and only have 2 methods with @MethodRef, "
                + "one is @MethodRef(type=CLASS), one is @MethodRef(type=METHOD)",
            annotatedClass));
    ExecutableElement ee1 = refMethods[0];
    ExecutableElement ee2 = refMethods[1];
    MethodRef mr1 = ee1.getAnnotation(MethodRef.class);
    MethodRef mr2 = ee2.getAnnotation(MethodRef.class);
    ExecutableElement clazz;
    ExecutableElement method;
    if (mr1.type() == Type.CLASS && mr2.type() == Type.METHOD) {
      clazz = ee1;
      method = ee2;
    } else if (mr1.type() == Type.METHOD && mr2.type() == Type.CLASS) {
      clazz = ee2;
      method = ee1;
    } else {
      error().log(
          "When use @MethodRef.Type.CLASS&METHOD, the annotation must have 1 method with Type.CLASS and 1 method with Type.METHOD",
          annotatedClass);
      throw new AssertException();
    }
    MethodRef mr = clazz.getAnnotation(MethodRef.class);
    TypeMirror parentClass = getAnnotationClassValue(elements, mr, MethodRef::parentClass);
    ExecutableElement parentValueMethod;
    if (!types.isSameType(parentClass, methodRefType)) {
      parentValueMethod = assertParentDefine(clazz, parentClass);
    } else {
      parentValueMethod = null;
    }
    assertThat(types.isAssignable(clazz.getReturnType(), classType))
        .todo(() -> error().log("Method with @MethodRef(type=CLASS) must return Class", clazz));
    return (e, am) -> {
      Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(am);
      String parentClzValue = null;
      if (parentValueMethod != null) {
        Element enclosingElement = e.getEnclosingElement();
        Optional<AnnotationMirror> defaultAnnotation = getAnnotationMirror(enclosingElement, parentClass);
        if (defaultAnnotation.isPresent()) {
          parentClzValue = elements.getElementValuesWithDefaults(defaultAnnotation.get()).get(parentValueMethod).getValue()
              .toString();
        }
      }
      String clzValue = values.get(clazz).getValue().toString();
      if (clzValue.equals(void.class.getCanonicalName()) || clzValue.equals(Void.class.getCanonicalName())) {
        assertNonNull(parentClzValue).todo(() -> error().log("Parent and itself both don't define a Class value.", e, am));
        clzValue = parentClzValue;
      }
      String methodValue = values.get(method).getValue().toString();
      return new String[] { clzValue, methodValue };
    };
  }

  private void valid(TypeElement theAnnotation, BiFunction<Element, AnnotationMirror, String[]> getClassAndMethod,
      RoundEnvironment roundEnv) {
    TypeMirror annoType = theAnnotation.asType();
    roundEnv.getElementsAnnotatedWith(theAnnotation).forEach(e -> handleAssert(() -> {
      AnnotationMirror anno = getAnnotationMirror(e, annoType).get();
      String[] pair = getClassAndMethod.apply(e, anno);
      TypeElement clz = elements.getTypeElement(pair[0]);
      if (clz == null) {
        try {
          clz = elements.getTypeElement(Class.forName(pair[0]).getCanonicalName());
        } catch (ClassNotFoundException e1) {
          error().log("Can't find the given class: " + pair[0], e, anno);
          return;
        }
      }
      if (!elements.getAllMembers(clz)
          .stream()
          .filter(ExecutableElement.class::isInstance)
          .map(Element::getSimpleName)
          .anyMatch(n -> n.contentEquals(pair[1]))) {
        error().log("Can't find the given method: " + pair[1] + " in " + pair[0], e, anno);
      }
    }));
  }

  /**
   * Assert the parent class well defined.
   *
   * @param annotatedMethod the attribute annotated
   * @param parentClass the parent class
   * @return parentClass's value method if well defined
   * @throws AssertException if not well defined.
   */
  private ExecutableElement assertParentDefine(ExecutableElement annotatedMethod, TypeMirror parentClass) throws AssertException {
    return assertNonNull(
        ElementFilter.methodsIn(types.asElement(parentClass).getEnclosedElements()).stream()
            .filter(ee -> ee.getSimpleName().contentEquals("value"))
            .filter(ee -> types.isAssignable(ee.getReturnType(), classType))
            .findAny()
            .orElse(null))
                .todo(() -> error().log("The parent annotation class must have an attribute named 'value' with type Class",
                    annotatedMethod));
  }
}
