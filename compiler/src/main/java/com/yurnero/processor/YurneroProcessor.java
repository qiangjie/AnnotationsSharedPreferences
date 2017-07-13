package com.yurnero.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yurnero.annotations.BooleanEntity;
import com.yurnero.annotations.Definition;
import com.yurnero.annotations.FloatEntity;
import com.yurnero.annotations.IntEntity;
import com.yurnero.annotations.LongEntity;
import com.yurnero.annotations.StringEntity;
import com.yurnero.processor.method.GetterMethod;
import com.yurnero.processor.method.SetterMethod;


@AutoService(Processor.class)
public class YurneroProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private static final String INSTANCE_NAME = "mMainSharedPreferences";
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName SHAREDPREFERENCES = ClassName.get("android.content", "SharedPreferences");
    private static final ClassName EDITOR = ClassName.get("android.content.SharedPreferences", "Editor");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, SharedPreferencesSet> bindingMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, SharedPreferencesSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            SharedPreferencesSet binding = entry.getValue();

            JavaFile javaFile = binding.brewJava(1);
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        // for (Element element : roundEnv.getElementsAnnotatedWith(Definition.class)) {
        // System.out.println("------------------------------");
        // // 判断元素的类型为Class
        // if (element.getKind() == ElementKind.CLASS) {
        // // 显示转换元素类型
        // TypeElement typeElement = (TypeElement) element;
        // // 输出元素名称
        // System.out.println(typeElement.getSimpleName());
        // // 输出注解属性值
        // System.out.println(typeElement.getAnnotation(Definition.class).javaFileName());
        // }
        // System.out.println("------------------------------");
        // }
        return false;
    }

    private Map<TypeElement, SharedPreferencesSet> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, SharedPreferencesSet> builderMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(Definition.class)) {
            try {
                paserDefinition(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, Definition.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(StringEntity.class)) {
            try {
                parseStringEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, StringEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(BooleanEntity.class)) {
            try {
                parseBooleanEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, BooleanEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(FloatEntity.class)) {
            try {
                parseFloatEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, FloatEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(IntEntity.class)) {
            try {
                parseIntEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, IntEntity.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(LongEntity.class)) {
            try {
                parseLongEntity(element, builderMap);
            } catch (Exception e) {
                logParsingError(element, LongEntity.class, e);
            }
        }
        return builderMap;
    }

    private void paserDefinition(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        if (element.getKind() != ElementKind.CLASS) {
            return;
        }
        TypeElement typeElement = (TypeElement) element;
        String fileName = typeElement.getAnnotation(Definition.class).javaFileName();
        String spFileName = typeElement.getAnnotation(Definition.class).spFileName();
        String packageName = typeElement.getAnnotation(Definition.class).packageName();

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, typeElement);
        sharedPreferencesSet.setJavafileName(fileName);
        sharedPreferencesSet.setSharedPreferencesFileName(spFileName);
        sharedPreferencesSet.setPackageName(packageName);
    }

    private void parseStringEntity(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(StringEntity.class).key();
        String defaultValue = element.getAnnotation(StringEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(StringEntity.class.getCanonicalName(), key, INSTANCE_NAME, defaultValue);
        SetterMethod setterMethod = new SetterMethod(StringEntity.class.getCanonicalName(), key, INSTANCE_NAME);

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, enclosingElement);
        sharedPreferencesSet.addMethod(getterMethod);
        sharedPreferencesSet.addMethod(setterMethod);
    }

    private void parseBooleanEntity(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(BooleanEntity.class).key();
        boolean defaultValue = element.getAnnotation(BooleanEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(BooleanEntity.class.getCanonicalName(), key, INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(BooleanEntity.class.getCanonicalName(), key, INSTANCE_NAME);

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, enclosingElement);
        sharedPreferencesSet.addMethod(getterMethod);
        sharedPreferencesSet.addMethod(setterMethod);
    }

    private void parseFloatEntity(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(FloatEntity.class).key();
        float defaultValue = element.getAnnotation(FloatEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(FloatEntity.class.getCanonicalName(), key, INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(FloatEntity.class.getCanonicalName(), key, INSTANCE_NAME);

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, enclosingElement);
        sharedPreferencesSet.addMethod(getterMethod);
        sharedPreferencesSet.addMethod(setterMethod);
    }

    private void parseIntEntity(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(IntEntity.class).key();
        int defaultValue = element.getAnnotation(IntEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(IntEntity.class.getCanonicalName(), key, INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(IntEntity.class.getCanonicalName(), key, INSTANCE_NAME);

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, enclosingElement);
        sharedPreferencesSet.addMethod(getterMethod);
        sharedPreferencesSet.addMethod(setterMethod);
    }

    private void parseLongEntity(Element element, Map<TypeElement, SharedPreferencesSet> builderMap) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        String key = element.getAnnotation(LongEntity.class).key();
        long defaultValue = element.getAnnotation(LongEntity.class).defaultValue();

        GetterMethod getterMethod = new GetterMethod(LongEntity.class.getCanonicalName(), key, INSTANCE_NAME, String.valueOf(defaultValue));
        SetterMethod setterMethod = new SetterMethod(LongEntity.class.getCanonicalName(), key, INSTANCE_NAME);

        SharedPreferencesSet sharedPreferencesSet = getOrCreateSet(builderMap, enclosingElement);
        sharedPreferencesSet.addMethod(getterMethod);
        sharedPreferencesSet.addMethod(setterMethod);
    }

    private SharedPreferencesSet getOrCreateSet(Map<TypeElement, SharedPreferencesSet> builderMap, TypeElement enclosingElement) {
        SharedPreferencesSet sharedPreferencesSet = builderMap.get(enclosingElement);
        if (sharedPreferencesSet == null) {
            sharedPreferencesSet = new SharedPreferencesSet(enclosingElement);
            builderMap.put(enclosingElement, sharedPreferencesSet);
        }
        return sharedPreferencesSet;
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(Definition.class.getCanonicalName());
        annotataions.add(BooleanEntity.class.getCanonicalName());
        annotataions.add(FloatEntity.class.getCanonicalName());
        annotataions.add(IntEntity.class.getCanonicalName());
        annotataions.add(LongEntity.class.getCanonicalName());
        annotataions.add(StringEntity.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
