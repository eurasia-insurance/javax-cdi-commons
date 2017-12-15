package tech.lapsa.javax.cdi.commons;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.enterprise.inject.spi.Annotated;

import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;

public final class MyAnnotated {

    private MyAnnotated() {
    }

    public static <T extends Annotation> T getAnnotation(final Annotated annotated, final Class<T> annotationClazz) {
	return MyObjects.requireNonNull(annotated, "annotated") //
		.getAnnotation(MyObjects.requireNonNull(annotationClazz, "annotationClazz"));
    }

    public static <T extends Annotation> Optional<T> optAnnotation(final Annotated annotated,
	    final Class<T> annotationClazz) {
	return MyOptionals.of(getAnnotation(annotated, annotationClazz));
    }

    public static <T extends Annotation> T requireAnnotation(final Annotated annotated,
	    final Class<T> annotationClazz) {
	return optAnnotation(annotated, annotationClazz) //
		.orElseThrow(MyExceptions.illegalStateSupplier("Require annotation of %1$s at %2$s", annotationClazz,
			annotated));
    }

}
