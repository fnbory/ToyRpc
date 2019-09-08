package Server.spring.Annotation;

import Server.spring.bean.ReferenceBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @Author: fnbory
 * @Date: 2019/8/27 15:11
 */
public class ReferenceBeanBuilder {

    private Class interfaceClass;

    private Reference annotation;

    private ClassLoader classLoader;

    private ApplicationContext applicationContext;

    public ReferenceBeanBuilder interfaceClass(Class interfaceClass){
        this.interfaceClass=interfaceClass;
        return this;
    }

    public ReferenceBeanBuilder(Reference annotation, ClassLoader classLoader, ApplicationContext applicationContext) {
        this.annotation = annotation;
        this.classLoader = classLoader;
        this.applicationContext = applicationContext;
    }

    public static  ReferenceBeanBuilder create(Reference annotation,ClassLoader classLoader,ApplicationContext applicationContext){
        return new ReferenceBeanBuilder(annotation,classLoader,applicationContext);
    }

    public final ReferenceBean build() throws  Exception{
        ReferenceBean referenceBean=doBuild();
        configureBean(referenceBean);
        return referenceBean;
    }

    public void configureBean(ReferenceBean bean) throws  Exception{
        postConfigureBean(annotation,bean);
    }

    public ReferenceBean doBuild(){
        return new ReferenceBean();
    }

    public void postConfigureBean(Reference annotation,ReferenceBean bean) throws Exception{
        bean.setApplicationContext(applicationContext);
        configureInterface(annotation,bean);
        bean.afterPropertiesSet();
    }


    private void configureInterface(Reference reference, ReferenceBean referenceBean){
        Class interfaceClass=reference.interfaceClass();
        if (void.class.equals(interfaceClass)) {
            interfaceClass = null;
            String interfaceClassName = reference.interfaceName();
            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = ClassUtils.resolveClassName(interfaceClassName, classLoader);
                }
            }
        }
        if (interfaceClass == null) {
            interfaceClass = this.interfaceClass;
        }
        Assert.isTrue(interfaceClass.isInterface(), "The class of field or method that was annotated @Reference is not an interface!");
        referenceBean.setInterfaceName(interfaceClass.getName());
        referenceBean.setVersion(reference.version());
    }
}
