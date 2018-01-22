package xdean.annotation.processor;

import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import xdean.annotation.MetaInfo;
import xdean.annotation.processor.toolkit.AssertException;
import xdean.annotation.processor.toolkit.XAbstractProcessor;
import xdean.annotation.processor.toolkit.annotation.SupportedAnnotation;

@AutoService(Processor.class)
@SupportedAnnotation(MetaInfo.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MetaInfoProcessor extends XAbstractProcessor {

  @Override
  public boolean processActual(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws AssertException {
    return false;
  }
}
