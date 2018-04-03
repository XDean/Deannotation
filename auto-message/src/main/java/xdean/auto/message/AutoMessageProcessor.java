package xdean.auto.message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import xdean.annotation.processor.toolkit.AssertException;
import xdean.annotation.processor.toolkit.XAbstractProcessor;
import xdean.annotation.processor.toolkit.annotation.SupportedAnnotation;

@SupportedAnnotation(AutoMessage.class)
public class AutoMessageProcessor extends XAbstractProcessor {
  @Override
  public boolean processActual(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws AssertException {
    if (roundEnv.processingOver()) {
      return true;
    }
    annotations.forEach(e -> handle(e));
    return true;
  }

  private void handle(TypeElement type) {
    assertThat(type.getKind() == ElementKind.CLASS).todo(() -> error().log("Can only annotated on class.", type));
    assertThat(type.getNestingKind() == NestingKind.TOP_LEVEL)
        .todo(() -> error().log("Can only annotated on top-level class", type));
    AutoMessage am = type.getAnnotation(AutoMessage.class);
    String file = am.file();
    try {
      Path path = Paths.get(processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, "", file).toUri());
      // processingEnv.getFiler().createSourceFile(name, originatingElements)
      Builder builder = TypeSpec.interfaceBuilder(am.generatedName())
          .addAnnotation(Generated.class)
          .addModifiers(Modifier.PUBLIC);
      Files.readAllLines(path)
          .stream()
          .map(this::extractKey)
          .map(s -> {
            String name = dotToUnder(s).toUpperCase();
            return FieldSpec.builder(String.class, name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", s)
                .build();
          })
          .forEach(builder::addField);
      JavaFile.builder(type.getEnclosingElement().asType().toString(), builder.build())
          .build()
          .writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      error().log("Fail to read " + file + " because " + e.getMessage(), type);
      return;
    }
  }

  private String extractKey(String line) {
    return line;
  }

  private String dotToUnder(String key) {
    return key;
  }
}
