package xdean.annotation.methodRef;

import static com.google.testing.compile.CompilationSubject.assertThat;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import xdean.annotation.processor.MethodRefProcessor;

public class MethodRefTest {
  private static final JavaFileObject GOLDEN = JavaFileObjects
      .forResource(MethodRefTest.class.getResource("MethodRefGoldenDefine.java"));

  @Test
  public void test() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN);
    assertThat(compile).succeededWithoutWarnings();
  }
}
