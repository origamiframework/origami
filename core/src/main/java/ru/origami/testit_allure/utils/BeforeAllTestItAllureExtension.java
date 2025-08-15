package ru.origami.testit_allure.utils;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.testit_allure.annotations.DisplayName;

@Slf4j
@Deprecated
public class BeforeAllTestItAllureExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private List<Class> classes = new ArrayList<>();

    private List<Method> methods = new ArrayList<>();

    public BeforeAllTestItAllureExtension() {
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        getAllByAnnotation();

        List<Method> displayNameMethods = findMethodsByAnnotation(DisplayName.class);
//        displayNameMethods.forEach(m -> addAnnotationToMethod(m, DisplayName.class, m.getAnnotation(DisplayName.class).value()));
//        displayNameMethods.forEach(m -> addAnnotationToMethod(m, ru.origami.testit_allure.test_it.testit.annotations.DisplayName.class, m.getAnnotation(DisplayName.class).value()));

    }

    @Override
    public void close() {
    }

    public void getAllByAnnotation() {
        this.classes = findAllClassesUsingClassLoader(Paths.get("src").toFile().listFiles());;
        this.methods = getMethods(classes);
    }

    private List<Method> getMethods(List<Class> classes) {
        List<Method> methods = new ArrayList<>();

        for(Class classObj : classes) {
            methods.addAll(List.of(classObj.getMethods()));
        }

        return methods;
    }

    public List<Class> findAllClassesUsingClassLoader(File[] files) {
        Set<Class> classes = new HashSet<>();

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findAllClassesUsingClassLoader(file.listFiles()));
            } else if (file.getName().endsWith(".java")) {
                classes.add(getClass(file.getPath()));
            }
        }

        return new ArrayList<>(classes);
    }

    private Class getClass(String packageClassName) {
        try {
            String className = packageClassName.substring(0, packageClassName.lastIndexOf(".java"))
                    .replaceAll("/", ".")
                    .replaceFirst("^src\\.main\\.java\\.", "")
                    .replaceFirst("^src\\.test\\.java\\.", "");

            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.info("TestIT Allure Lib class found error: {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private List<Method> findMethodsByAnnotation(Class<? extends Annotation> annotation) {
        return this.methods.stream()
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public static void addAnnotationToMethod(Method method, Class<? extends Annotation> annotation,
                                             String value) {

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.getCtClass(method.getDeclaringClass().getName());
            CtMethod sayHelloMethodDescriptor = cc.getDeclaredMethod(method.getName());
            ClassFile ccFile = cc.getClassFile();
            ConstPool constpool = ccFile.getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            javassist.bytecode.annotation.Annotation annot = new javassist.bytecode.annotation.Annotation(annotation.getName(), constpool);
            annot.addMemberValue("value", new StringMemberValue(value, ccFile.getConstPool()));
            attr.addAnnotation(annot);
            sayHelloMethodDescriptor.getMethodInfo().addAttribute(attr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
