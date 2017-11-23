package xdean.annotation.processor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

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
import javax.lang.model.type.TypeMirror;

import com.google.auto.service.AutoService;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;
import xdean.annotation.processor.annotation.SupportedAnnotation;

@AutoService(Processor.class)
@SupportedAnnotation(MethodRef.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MethodRefProcessor extends XAbstractProcessor {
  private TypeMirror classType;
  private Set<TypeElement> visited = new HashSet<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    classType = types.erasure(elements.getTypeElement(Class.class.getCanonicalName()).asType());
  }

  @Override
  public boolean processActual(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getElementsAnnotatedWith(MethodRef.class)
        .forEach(e -> handleAssert(() -> valid(assertType(e, ExecutableElement.class)
            .todo(() -> error().log("MethodRef can only annotate on method element.", e)), roundEnv)));
    return true;
  }

  private void valid(ExecutableElement annotatedMethod, RoundEnvironment roundEnv) throws AssertException {
    debug().log("To validate: " + annotatedMethod);
    MethodRef mr = annotatedMethod.getAnnotation(MethodRef.class);
    TypeElement annotatedClass = assertType(annotatedMethod.getEnclosingElement(), TypeElement.class)
        .todo(() -> error().log("Except @MethodRef method defined in a class.", annotatedMethod));
    assertThat(annotatedClass.getKind() == ElementKind.ANNOTATION_TYPE)
        .todo(() -> error().log("@MethodRef can only annotated on @interface class's method.", annotatedMethod));
    assertThat(visited.add(annotatedClass))
        .todo(() -> debug().log("This annotation has been visisted: " + annotatedClass));
    BiFunction<Element, AnnotationMirror, String[]> getClassAndMethod;
    if (mr.type() == Type.ALL) {
      char splitor = mr.splitor();
      getClassAndMethod = (e, am) -> {
        AnnotationValue av = elements.getElementValuesWithDefaults(am).get(annotatedMethod);
        String value = av.getValue().toString();
        String[] split = value.split(Pattern.quote(Character.toString(splitor)));
        assertThat(split.length == 2)
            .todo(() -> error().log("The method reference must be $ClassName" + splitor + "$MethodName", e, am, av));
        return split;
      };
    } else {
      ExecutableElement[] refMethods = elements.getAllMembers(annotatedClass)
          .stream()
          .filter(ExecutableElement.class::isInstance)
          .filter(e -> e.getAnnotation(MethodRef.class) != null)
          .toArray(ExecutableElement[]::new);
      assertThat(refMethods.length == 2)
          .todo(() -> error().log(
              "When use @MethodRef.Type.CLASS&METHOD, the annotation must have and only have 2 methods with @MethodRef",
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
        return;
      }
      assertThat(types.isAssignable(clazz.getReturnType(), classType))
          .todo(() -> error().log("Method with @MethodRef(type=CLASS) must return Class", clazz));
      getClassAndMethod = (e, am) -> {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(am);
        String clzValue = values.get(clazz).getValue().toString();
        String methodValue = values.get(method).getValue().toString();
        return new String[] { clzValue, methodValue };
      };
    }
    TypeMirror annoType = annotatedClass.asType();
    roundEnv.getElementsAnnotatedWith(annotatedClass).forEach(e -> handleAssert(() -> {
      AnnotationMirror anno = ElementUtil.getAnnotationMirror(e, annoType).get();
      String[] pair = getClassAndMethod.apply(e, anno);
      if (pair != null) {
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
      }
    }));
  }
}
