package tech.lapsa.javax.cdi.commons;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import tech.lapsa.java.commons.function.MyExceptions;

public final class MyBeans {

    private static final String DEFAULT_APPLICATION_SUFFIX = "-ear";

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
	if (!res.isPresent())
	    res = lookupNaming(clazz);
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

    public static <T> Optional<T> lookupNaming(final Class<T> clazz) {
	return lookupNaming(clazz, clazz.getCanonicalName());
    }

    public static <T> Optional<T> lookupNaming(final Class<T> clazz, final String name) {
	try {
	    final InitialContext ic = new InitialContext();
	    final Object object = ic.lookup(name);
	    @SuppressWarnings("unchecked")
	    final T obj = (T) object;
	    return Optional.of(obj);
	} catch (NamingException | ClassCastException | NullPointerException e) {
	    return Optional.empty();
	}
    }

    public static enum EJBBeanType {
	LOCAL("Local"), REMOTE("Remote");

	private final String suffix;

	private EJBBeanType(final String prefix) {
	    this.suffix = prefix;
	}
    }

    public static <T> T lookupEJB(final String applicationName, final String module, final String beanName,
	    final Class<? extends T> interfaceClazz, final Class<T> typeClazz)
	    throws NamingException, ClassCastException {
	return lookupEJB(applicationName, module, beanName, interfaceClazz.getName(), typeClazz);
    }

    public static <T> T lookupEJB(final String applicationName, final String module,
	    final Class<? extends T> interfaceClazz, final Class<T> typeClazz)
	    throws NamingException, ClassCastException {
	final String beanName = typeClazz.getSimpleName() + DEFAULT_BEAN_SUFFIX;
	return lookupEJB(applicationName, module, beanName, interfaceClazz.getName(), typeClazz);
    }

    public static <T, X extends Throwable> T lookupEJB(final BiFunction<String, Throwable, X> creator,
	    final String applicationName, final String module,
	    final Class<? extends T> interfaceClazz, final Class<T> typeClazz)
	    throws X {
	final String beanName = typeClazz.getSimpleName() + DEFAULT_BEAN_SUFFIX;
	try {
	    return lookupEJB(applicationName, module, beanName, interfaceClazz.getName(), typeClazz);
	} catch (ClassCastException | NamingException e) {
	    throw MyExceptions.format(creator, e, "Can't instantiate ejb for %1$s", typeClazz.getSimpleName());
	}
    }

    private static final String DEFAULT_BEAN_SUFFIX = "Bean";

    public static <T> T lookupEJB(final String module, final EJBBeanType type, final Class<T> typeClazz)
	    throws NamingException, ClassCastException {
	final String applicationName = module + DEFAULT_APPLICATION_SUFFIX;
	final String beanName = typeClazz.getSimpleName() + DEFAULT_BEAN_SUFFIX;
	final String interfaceName = typeClazz.getName() + "$" + typeClazz.getSimpleName() + type.suffix;
	return lookupEJB(applicationName, module, beanName, interfaceName, typeClazz);
    }

    public static <T> T lookupEJB(final String applicationName, final String module, final String beanName,
	    final String interfaceName, final Class<T> typeClazz) throws NamingException, ClassCastException {
	final String pathPattern = "java:global/%1$s/%2$s/%3$s!%4$s";
	final String path = String.format(pathPattern,
		applicationName, // 1
		module, // 2
		beanName, // 3
		interfaceName // 4
	);
	final InitialContext ic = new InitialContext();
	final Object object = ic.lookup(path);
	return typeClazz.cast(object);
    }

}
