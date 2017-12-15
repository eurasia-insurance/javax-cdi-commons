package tech.lapsa.javax.cdi.utility;

import java.lang.annotation.Annotation;
import java.util.Optional;

import tech.lapsa.javax.cdi.commons.MyBeans;

@Deprecated
public final class BeanUtils {

    private BeanUtils() {
    }

    @Deprecated
    public static final <T> T getBean(final Class<T> clazz) {
	return MyBeans.getBean(clazz);
    }

    @Deprecated
    public static <T> Optional<T> lookup(final Class<T> clazz) {
	return MyBeans.lookup(clazz);
    }

    @Deprecated
    public static <T> Optional<T> lookupCDI(final Class<T> clazz, final Annotation... qualifiers) {
	return MyBeans.lookupCDI(clazz, qualifiers);
    }

    @Deprecated
    public static <T> Optional<T> lookupNaming(final Class<T> clazz) {
	return MyBeans.lookupNaming(clazz);
    }

    @Deprecated
    public static <T> Optional<T> lookupNaming(final Class<T> clazz, final String name) {
	return MyBeans.lookupNaming(clazz, name);
    }

}
