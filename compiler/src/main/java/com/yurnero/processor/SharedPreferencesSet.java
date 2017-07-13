package com.yurnero.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yurnero.processor.method.BaseMethod;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * description:.
 *
 * @author：qiangjie @version：17/7/6 下午4:37
 */

public class SharedPreferencesSet {
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName SHAREDPREFERENCES = ClassName.get("android.content", "SharedPreferences");

    private TypeElement enclosingElement;
    private String javafileName;
    private String sharedPreferencesFileName;
    private String packageName;
    private List<BaseMethod> methodList;

    public SharedPreferencesSet(TypeElement enclosingElement) {
        this.enclosingElement = enclosingElement;
        this.methodList = new ArrayList<>();
    }

    JavaFile brewJava(int sdk) {
        return JavaFile.builder(packageName, createType(sdk)).addFileComment("Generated code from Butter Knife. Do not modify!").build();
    }

    private TypeSpec createType(int sdk) {
        TypeSpec.Builder result = TypeSpec.classBuilder(javafileName).addModifiers(PUBLIC);

        FieldSpec instanceField = FieldSpec.builder(SHAREDPREFERENCES, "mMainSharedPreferences", Modifier.PRIVATE).build();
        result.addField(instanceField);

        result.addMethod(createConstructor());
        result.addMethod(createNewInstanceMethod());
        result.addMethod(createInitMethod());

        for (BaseMethod method : methodList) {
            result.addMethod(method.render());
        }

        return result.build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(PRIVATE);
        return builder.build();
    }

    private MethodSpec createNewInstanceMethod() {
        MethodSpec newInstanceMethd = MethodSpec.methodBuilder("newInstance")//
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)//
                .returns(ClassName.get(packageName, javafileName))//
                .addParameter(CONTEXT, "context")//
                .addStatement(javafileName + " instance= new " + javafileName + "()")//
                .addStatement("instance.init(context)")//
                .addStatement("return instance")//
                .build();
        return newInstanceMethd;
    }

    private MethodSpec createInitMethod() {
        MethodSpec initMethd = MethodSpec.methodBuilder("init")//
                .addModifiers(Modifier.PRIVATE)//
                .returns(TypeName.VOID)//
                .addParameter(CONTEXT, "context")//
                .addStatement("mMainSharedPreferences = context.getSharedPreferences($S,Context.MODE_PRIVATE)", sharedPreferencesFileName)//
                .build();
        return initMethd;
    }

    public void addMethod(BaseMethod method) {
        methodList.add(method);
    }

    public String getJavafileName() {
        return javafileName;
    }

    public void setJavafileName(String javafileName) {
        this.javafileName = javafileName;
    }

    public String getSharedPreferencesFileName() {
        return sharedPreferencesFileName;
    }

    public void setSharedPreferencesFileName(String sharedPreferencesFileName) {
        this.sharedPreferencesFileName = sharedPreferencesFileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
