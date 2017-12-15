package tech.lapsa.javax.cdi.commons;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Optional;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public final class MyBeans {

    private MyBeans() {
    }

    public static final <T> T getBean(final Class<T> clazz) {
	try {
	    return getBean11(clazz);
	} catch (final Throwable e) {
	    try {
		return getBean10(clazz);
	    } catch (final Throwable e1) {
		return null;
	    }
	}
    }

    private static <T> T getBean11(final Class<T> clazz) {
	return CDI.current().select(clazz).get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean10(final Class<T> clazz) {
	final BeanManager bm = CDI.current().getBeanManager();
	final Bean<T> bean = (Bean<T>) bm.getBeans(clazz).iterator().next();
	final CreationalContext<T> ctx = bm.createCreationalContext(bean);
	final T ref = (T) bm.getReference(bean, clazz, ctx);
	return ref;
    }

    public static <T> Optional<T> lookup(final Class<T> clazz) {
	Optional<T> res = Optional.empty();
	if (!res.isPresent())
	    res = lookupCDI(clazz);
	return res;
    }

    public static <T> Optional<T> lookupCDI(final Class<T> clazz, final Annotation... qualifiers) {
	Optional<T> res = Optional.empty();
	if (!res.isPresent())
	    res = lookupCDI11(clazz, qualifiers);
	if (!res.isPresent())
	    res = lookupCDI10(clazz, qualifiers);
	return res;
    }

    private static <T> Optional<T> lookupCDI11(final Class<T> clazz, final Annotation... qualifiers) {
	try {
	    return Optional.ofNullable(CDI.current().select(clazz, qualifiers).get());
	} catch (final Exception e) {
	    return Optional.empty();
	}
    }

    private static <T> Optional<T> lookupCDI10(final Class<T> clazz, final Annotation... qualifiers) {
	try {
	    final BeanManager bm = CDI.current().getBeanManager();
	    final Iterator<Bean<?>> iter = bm.getBeans(clazz, qualifiers).iterator();
	    if (!iter.hasNext())
		return Optional.empty();
	    @SuppressWarnings("unchecked")
	    final Bean<T> bean = (Bean<T>) iter.next();
	    final CreationalContext<T> ctx = bm.createCreationalContext(bean);
	    @SuppressWarnings("unchecked")
	    final T obj = (T) bm.getReference(bean, clazz, ctx);
	    return Optional.of(obj);
	} catch (ClassCastException | NullPointerException e) {
	    return Optional.empty();
	}
    }
}
