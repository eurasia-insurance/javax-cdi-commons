package tech.lapsa.javax.cdi.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD })
public @interface CDIToEJBDelegate {

    public static final Annotation ANNOTATION_INSTANCE = new CDIToEJBDelegate() {

	@Override
	public Class<? extends Annotation> annotationType() {
	    return CDIToEJBDelegate.class;
	}
    };

}
