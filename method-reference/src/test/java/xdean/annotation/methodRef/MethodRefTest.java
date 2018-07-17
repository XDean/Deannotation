package xdean.annotation.methodRef;

import static com.google.testing.compile.CompilationSubject.assertThat;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.junit.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import xdean.annotation.methodref.MethodRefProcessor;

public class MethodRefTest {
  private static final JavaFileObject GOLDEN = getSource("GoldenDefine.java");
  private static final String RECORD_FILE = "META-INF/xdean/annotation/MethodRef";

  private static JavaFileObject getSource(String source) {
    return JavaFileObjects.forResource(MethodRefTest.class.getResource(source));
  }

  @Test
  public void testGoldenDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN);
    assertThat(compile).succeededWithoutWarnings();
    // TODO: add content compare, https://github.com/google/compile-testing/issues/139
    assertThat(compile).generatedFile(StandardLocation.CLASS_OUTPUT, RECORD_FILE);
  }

  @Test
  public void testGoldenUse() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("GoldenUsage.java"));
    assertThat(compile).succeededWithoutWarnings();
  }

  @Test
  public void testBadDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(getSource("BadDefine.java"));
    assertThat(compile).hadErrorCount(1);
  }

  @Test
  public void testBadAllUsage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("BadAllUsage.java"));
    assertThat(compile).hadErrorCount(3);
  }

  @Test
  public void testBadClassMethodDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(getSource("BadClassMethodDefine.java"));
    assertThat(compile).hadErrorCount(5);
  }

  @Test
  public void testBadClassMethodUsage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("BadClassMethodUsage.java"));
    assertThat(compile).hadErrorCount(1);
  }

  @Test
  public void testBadParentDefine() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(getSource("BadParentDefine.java"));
    assertThat(compile).hadErrorCount(2);
  }

  @Test
  public void testBadParentUsage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("BadParentUsage.java"));
    assertThat(compile).hadErrorCount(5);
  }

  @Test
  public void testBadEnclosingUsage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("BadEnclosingUsage.java"));
    assertThat(compile).hadErrorCount(1);
  }

  @Test
  public void testBadDefaultUsage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new MethodRefProcessor())
        .compile(GOLDEN, getSource("BadDefaultUsage.java"));
    assertThat(compile).hadErrorCount(1);
  }

  @Test
  public void testNestedDependency() throws Exception {
    Compiler.javac()
        .withProcessors(new MethodRefProcessor());
  }
}
