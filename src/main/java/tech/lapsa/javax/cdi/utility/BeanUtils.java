package com.lapsa.utils;

import java.util.Iterator;
import java.util.Optional;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class BeanUtils {

    private BeanUtils() {
    }

    public static final <T> T getBean(Class<T> clazz) {
	try {
	    return getBean11(clazz);
	} catch (Throwable e) {
	    try {
		return getBean10(clazz);
	    } catch (Throwable e1) {
		return null;
	    }
	}
    }

    private static <T> T getBean11(Class<T> clazz) {
	return CDI.current().select(clazz).get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean10(Class<T> clazz) {
	BeanManager bm = CDI.current().getBeanManager();
	Bean<T> bean = (Bean<T>) bm.getBeans(clazz).iterator().next();
	CreationalContext<T> ctx = bm.createCreationalContext(bean);
	T ref = (T) bm.getReference(bean, clazz, ctx);
	return ref;
    }

    public static <T> Optional<T> lookup(final Class<T> clazz) {
	Optional<T> res = Optional.empty();
	if (!res.isPresent())
	    res = lookupCDI(clazz);
	if (!res.isPresent())
	    res = lookupNaming(clazz);
	return res;
    }

    public static <T> Optional<T> lookupCDI(final Class<T> clazz) {
	Optional<T> res = Optional.empty();
	if (!res.isPresent())
	    res = lookupCDI11(clazz);
	if (!res.isPresent())
	    res = lookupCDI10(clazz);
	return res;
    }

    private static <T> Optional<T> lookupCDI11(final Class<T> clazz) {
	try {
	    return Optional.ofNullable(CDI.current().select(clazz).get());
	} catch (Exception e) {
	    return Optional.empty();
	}
    }

    private static <T> Optional<T> lookupCDI10(final Class<T> clazz) {
	try {
	    final BeanManager bm = CDI.current().getBeanManager();
	    final Iterator<Bean<?>> iter = bm.getBeans(clazz).iterator();
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

    public static <T> Optional<T> lookupNaming(final Class<T> clazz) {
	return lookupNaming(clazz, clazz.getCanonicalName());
    }

    public static <T> Optional<T> lookupNaming(final Class<T> clazz, String name) {
	try {
	    final InitialContext ic = new InitialContext();
	    final Object object = ic.lookup(name);
	    @SuppressWarnings("unchecked")
	    T obj = (T) object;
	    return Optional.of(obj);
	} catch (NamingException | ClassCastException | NullPointerException e) {
	    return Optional.empty();
	}
    }

}
