package xdean.annotation.processor;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import xdean.annotation.processor.toolkit.AssertException;
import xdean.annotation.processor.toolkit.XAbstractProcessor;

public class AggregationChecker extends XAbstractProcessor {
  @Override
  public boolean processActual(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws AssertException {
    if (roundEnv.processingOver()) {
      return false;
    }

    return false;
  }
}
