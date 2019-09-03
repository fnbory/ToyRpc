package Server.spring.Annotation;

import Server.spring.bean.AddressBean;
import Server.spring.bean.PortBean;
import Server.spring.bean.ServiceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * @Author: fnbory
 * @Date: 2019/8/26 14:14
 */
public class ServiceAnnotationProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware
, ResourceLoaderAware, BeanClassLoaderAware {

    private BeanDefinitionRegistry registry;

    private Environment environment;

    private Set<String> packages;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public ServiceAnnotationProcessor(Set<String> packages) {
        this.packages = packages;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.registry=beanDefinitionRegistry;
        if(StringUtils.hasText(environment.getProperty("port"))){
            BeanDefinitionBuilder builder=BeanDefinitionBuilder.rootBeanDefinition(PortBean.class);
            builder.addPropertyValue("port",environment.getProperty("port","12345"));
            registry.registerBeanDefinition("portbean",builder.getBeanDefinition());
        }

        BeanDefinitionBuilder registryBean=BeanDefinitionBuilder.rootBeanDefinition(AddressBean.class);
        registryBean.addPropertyValue("address",environment.getProperty("registry.address","127.0.0.1:2181"));
        registry.registerBeanDefinition("addressBean",registryBean.getBeanDefinition());

        Set<String> resolvedPackageToScan=resolvePackageToScan(packages);
        if(!CollectionUtils.isEmpty(resolvedPackageToScan)){
            registerServiceBeans(resolvedPackageToScan,registry);
        }

    }

    private void registerServiceBeans(Set<String> resolvedPackageToScan, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner=new ClassPathBeanDefinitionScanner(registry,true,
                this.environment,this.resourceLoader);
        BeanNameGenerator beanNameGenerator=resolveBeanNameGenerator(registry);
        scanner.setBeanNameGenerator(beanNameGenerator);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        for(String packageToScan:resolvedPackageToScan){
            scanner.scan(packageToScan);
            Set<BeanDefinitionHolder> beanDefinitionHolders=findServiceBeanDefinationHolders(scanner,
                    packageToScan,registry,beanNameGenerator);
            if(!CollectionUtils.isEmpty(beanDefinitionHolders)){
                for(BeanDefinitionHolder holder:beanDefinitionHolders){
                    registerServiceBean(holder,registry);
                }
            }
        }
    }

    private void registerServiceBean(BeanDefinitionHolder holder, BeanDefinitionRegistry registry) {
        Class beanClass=resolveClass(holder);
        Service service=findAnnotation(beanClass,Service.class);
        if(service==null){
            return ;
        }
        Class interfaceClass=resolveInterfaceClass(beanClass,service);
        String annotationedServiceBeanName=holder.getBeanName();
        AbstractBeanDefinition serviceBeanDefination=buildServiceBeanDefination(service,interfaceClass,annotationedServiceBeanName);
        String beanName=generateServiceBeanName(service,interfaceClass,annotationedServiceBeanName);
        if(check(beanName,serviceBeanDefination)){
            registry.registerBeanDefinition(beanName,serviceBeanDefination);
        }

    }

    private boolean check(String beanName, AbstractBeanDefinition serviceBeanDefination) {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        existingDef = existingDef.getOriginatingBeanDefinition();
        if (isCompatible(serviceBeanDefination, existingDef)) {
            return false;
        }
        throw new RuntimeException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + serviceBeanDefination.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    private boolean isCompatible(AbstractBeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return !(existingDefinition instanceof ScannedGenericBeanDefinition) || newDefinition.getSource().equals(existingDefinition.getSource()) || newDefinition.equals(existingDefinition);
    }

    private static final String SEPARATOR = ":";

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

    private String generateServiceBeanName(Service service, Class interfaceClass, String annotationedServiceBeanName) {
        StringBuilder beanNameBuilder = new StringBuilder(ServiceBean.class.getSimpleName());
        beanNameBuilder.append(SEPARATOR).append(annotationedServiceBeanName);
        String interfaceClassName = interfaceClass.getName();
        beanNameBuilder.append(SEPARATOR).append(interfaceClassName);
        String version = service.version();
        if (StringUtils.hasText(version)) {
            beanNameBuilder.append(SEPARATOR).append(version);
        }
        return beanNameBuilder.toString();
    }

    public void addValue(BeanDefinitionBuilder builder,String propertyName,Object beanName){
        String resolveBeanName= environment.resolvePlaceholders(beanName.toString());
        builder.addPropertyValue(propertyName,resolveBeanName);
    }
    private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }

    private AbstractBeanDefinition buildServiceBeanDefination(Service service, Class interfaceClass, String annotationedServiceBeanName) {
        BeanDefinitionBuilder builder=BeanDefinitionBuilder.rootBeanDefinition(ServiceBean.class);
        addValue(builder, "impl", annotationedServiceBeanName);
        service.interfaceClass();
        addValue(builder, "interfaceName", interfaceClass.getName());
        addValue(builder, "version", service.version());
        addValue(builder, "weight", service.weight());
        addValue(builder, "serialization", service.serialization());

        addPropertyReference(builder, "protocolPort", "portBean");
        addPropertyReference(builder, "register", "addressBean");
        return builder.getBeanDefinition();
    }

    private Class resolveInterfaceClass(Class annotateServicedBeanClass, Service service) {
        Class interfaceClass=service.interfaceClass();
        if(void.class.equals(interfaceClass)){
            interfaceClass=null;
            String interfaceClassName=service.interfaceName();
            if(StringUtils.hasText(interfaceClassName)){
                if(ClassUtils.isPresent(interfaceClassName,classLoader)){
                    interfaceClass=resolveClassName(interfaceClassName,classLoader);
                }
            }
        }
        if(interfaceClass==null){
            Class[] allInterfaces=annotateServicedBeanClass.getInterfaces();
            if(allInterfaces.length>0){
                interfaceClass=allInterfaces[0];
            }
        }
        Assert.notNull(interfaceClass, "@Service interfaceClass() or interfaceName() or interface class must be present!");
        Assert.isTrue(interfaceClass.isInterface(),
                "The type that was annotated @Service is not an interface!");

        return interfaceClass;
    }


    private Class resolveClass(BeanDefinitionHolder holder) {
        BeanDefinition beanDefinition=holder.getBeanDefinition();
        return resolveClass(beanDefinition);
    }

    private Class resolveClass(BeanDefinition beanDefinition) {
        String beanClassName=beanDefinition.getBeanClassName();
        return resolveClassName(beanClassName,classLoader);
    }


    private Set<BeanDefinitionHolder> findServiceBeanDefinationHolders(ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        Set<BeanDefinition> beanDefinitions=scanner.findCandidateComponents(packageToScan);
        Set<BeanDefinitionHolder> beanDefinitionHolders=new LinkedHashSet<>(beanDefinitions.size());
        for(BeanDefinition beanDefinition:beanDefinitions){
            String beanName=beanNameGenerator.generateBeanName(beanDefinition,registry);
            BeanDefinitionHolder beanDefinitionHolder=new BeanDefinitionHolder(beanDefinition,beanName);
            beanDefinitionHolders.add(beanDefinitionHolder);
        }
        return beanDefinitionHolders;
    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {
        BeanNameGenerator beanNameGenerator=null;
        if(registry instanceof SingletonBeanRegistry){
            SingletonBeanRegistry singletonBeanRegistry=SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator=(BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }
        if(beanNameGenerator==null){
            beanNameGenerator=new AnnotationBeanNameGenerator();
        }
        return beanNameGenerator;
    }

    private Set<String> resolvePackageToScan(Set<String> packages) {
        Set<String> resolvePackagesToScan=new LinkedHashSet<>(packages.size());
        for(String Apackage:packages){
            if(StringUtils.hasText(Apackage)){
                String resolvePackage=environment.resolvePlaceholders(Apackage.trim());
                resolvePackagesToScan.add(resolvePackage);
            }
        }
        return resolvePackagesToScan;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader= classLoader;
    }
}
