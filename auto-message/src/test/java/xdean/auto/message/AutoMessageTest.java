package xdean.auto.message;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

public class AutoMessageTest {
  @Test
  public void test() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new AutoMessageProcessor())
        .compile(getSource("Golden.java"));
    assertThat(compile).generatedSourceFile("xdean.auto.message.Messages");
    JavaFileObject f = compile.generatedSourceFiles().get(0);
    String result = CharStreams.toString(new InputStreamReader(f.openInputStream(), Charsets.UTF_8));
    List<String> expected = Files.readAllLines(Paths.get(AutoMessageTest.class.getResource("/golden-output").toURI()));
    assertEquals(expected.stream().collect(Collectors.joining("\n")), result);
  }

  @Test
  public void testPackage() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new AutoMessageProcessor())
        .compile(getSource("pkg/package-info.java"));
    assertThat(compile).generatedSourceFile("xdean.auto.message.pkg.Msg");
    JavaFileObject f = compile.generatedSourceFiles().get(0);
    String result = CharStreams.toString(new InputStreamReader(f.openInputStream(), Charsets.UTF_8));
    List<String> expected = Files.readAllLines(Paths.get(AutoMessageTest.class.getResource("pkg/golden-pkg-output").toURI()));
    assertEquals(expected.stream().collect(Collectors.joining("\n")), result);
  }

  @Test
  public void testNotFound() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new AutoMessageProcessor())
        .compile(getSource("NotFound.java"));
    assertThat(compile).hadErrorContaining("Fail to read");
  }

  @Test
  public void testBadProp() throws Exception {
    Compilation compile = Compiler.javac()
        .withProcessors(new AutoMessageProcessor())
        .compile(getSource("BadProp.java"));
    assertThat(compile).hadErrorContaining("Bad define at line 2 of bad.properties");
  }

  private static JavaFileObject getSource(String source) {
    return JavaFileObjects.forResource(AutoMessageTest.class.getResource(source));
  }
}
