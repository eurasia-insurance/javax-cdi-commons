package tech.lapsa.javax.cdi.qualifiers;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD })
public @interface QDelegateToEJB {

    public static final Annotation DEFAULT_INSTANCE = new QDelegateToEJB() {

	@Override
	public Class<? extends Annotation> annotationType() {
	    return QDelegateToEJB.class;
	}
    };

}
