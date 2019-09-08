package Server.spring.Annotation;

import Server.spring.bean.ReferenceBean;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * @Author: fnbory
 * @Date: 2019/8/27 14:13
 */
public class ReferenceAnnotationProcessor extends InstantiationAwareBeanPostProcessorAdapter implements
        MergedBeanDefinitionPostProcessor, PriorityOrdered, ApplicationContextAware, BeanClassLoaderAware, DisposableBean {

    private  final ConcurrentHashMap<String,ReferenceBean> referenceBeansCache=new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String,ReferenceInjectionMetadata> injectionMetadataCache=new ConcurrentHashMap<>(256);

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {

        InjectionMetadata metadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @Reference dependencies failed", ex);
        }
        return pvs;
    }

    private ReferenceInjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {
        Collection<ReferenceFieldElement> fieldElements = findFieldReferenceMetadata(beanClass);
        return new ReferenceInjectionMetadata(beanClass, fieldElements);
    }


    private List<ReferenceFieldElement> findFieldReferenceMetadata(final Class<?> beanClass) {
        final List<ReferenceFieldElement> elements = new LinkedList<ReferenceFieldElement>();
        ReflectionUtils.doWithFields(beanClass, field -> {
            Reference reference = getAnnotation(field, Reference.class);
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            elements.add(new ReferenceFieldElement(field, reference));
        });
        return elements;
    }


    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        ReferenceInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private class ReferenceInjectionMetadata extends InjectionMetadata{

        @Getter
        private final Collection<ReferenceFieldElement> fieldElements;

        public ReferenceInjectionMetadata(Class targetClass,Collection<ReferenceFieldElement> fieldElements){
            super(targetClass,combine(fieldElements));
            this.fieldElements=fieldElements;
        }
    }

    private static <T> Collection<T> combine(Collection<? extends T>... elements) {
        List<T> allenelemts=new ArrayList<>();
        for(Collection<? extends T> e:elements){
            allenelemts.addAll(e);
        }
        return allenelemts;
    }

    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement{
        private final Field field;

        private final Reference reference;

        private volatile ReferenceBean referenceBean;

        public ReferenceFieldElement(Field field,Reference reference){
            super(field,null);
            this.field=field;
            this.reference=reference;
        }

        @Override
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            Class referenceClass=field.getType();
            if(reference==null) return;
            referenceBean=buildReferenceBean(referenceClass,reference);
            ReflectionUtils.makeAccessible(field);
            field.set(target,referenceBean.getObject());
        }

        private ReferenceBean buildReferenceBean(Class referenceClass,Reference reference) throws Exception{
            String referenceBeanCacheKey=generateReferenceBeanCacheKey(reference,referenceClass);
            ReferenceBean referenceBean=referenceBeansCache.get(referenceBeanCacheKey);
            if(referenceBean==null){
                ReferenceBeanBuilder beanBuilder=ReferenceBeanBuilder.create(reference,classLoader,applicationContext).interfaceClass(referenceClass);
                referenceBean=beanBuilder.build();
                referenceBeansCache.putIfAbsent(referenceBeanCacheKey,referenceBean);
            }
            return referenceBean;
        }

        private String generateReferenceBeanCacheKey(Reference reference, Class referenceClass) {
            String interfaceName=referenceClass.getName();
            String key=interfaceName+"/"+reference.version();
            key=applicationContext.getEnvironment().resolvePlaceholders(key);
            return key;
        }
    }
}
