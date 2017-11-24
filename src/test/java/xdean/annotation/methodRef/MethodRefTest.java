package xdean.annotation.methodRef;

import static com.google.testing.compile.CompilationSubject.assertThat;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import xdean.annotation.processor.MethodRefProcessor;

public class MethodRefTest {
  private static final JavaFileObject GOLDEN = getSource("GoldenDefine.java");

  private static JavaFileObject getSource(String source) {
    return JavaFileObjects.forResource(MethodRefTest.class.getResource(source));
  }

  @Test
  public void testGoldenDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN);
    assertThat(compile).succeededWithoutWarnings();
  }

  @Test
  public void testGoldenUse() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("GoldenUsage.java"));
    assertThat(compile).succeededWithoutWarnings();
  }

  @Test
  public void testBadClassMethodDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("bad/BadClassMethodDefine.java"));
    assertThat(compile).hadErrorCount(4);
  }
}
