package com.yurnero.processor.method;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Modifier;

/**
 * description:.
 *
 * @author：qiangjie @version：17/7/9 下午11:12
 */

public class SetterMethod extends BaseMethod {
    private static final ClassName EDITOR = ClassName.get("android.content.SharedPreferences", "Editor");
    private static final String METHOD_PREFIX = "set";
    private String methodName;
    private String setMethodName;
    private TypeName parameterType;

    public SetterMethod(String annotationName, String key, String instanceName) {
        super(key, instanceName);
        this.instanceName = instanceName;
        this.key = key;
        this.methodName = METHOD_PREFIX + baseMethodName;
        parseReturnType(annotationName);
    }

    private void parseReturnType(String annotationName) {
        if (annotationName.endsWith("StringEntity")) {
            parameterType = TypeName.get(String.class);
            setMethodName = "putString";
        } else if (annotationName.endsWith("BooleanEntity")) {
            parameterType = TypeName.BOOLEAN;
            setMethodName = "putBoolean";
        } else if (annotationName.endsWith("FloatEntity")) {
            parameterType = TypeName.FLOAT;
            setMethodName = "putFloat";
        } else if (annotationName.endsWith("IntEntity")) {
            parameterType = TypeName.INT;
            setMethodName = "putInt";
        } else if (annotationName.endsWith("LongEntity")) {
            parameterType = TypeName.LONG;
            setMethodName = "putLong";
        } else {
            throw new IllegalArgumentException("Annotation class is not exist");
        }
    }

    @Override
    public MethodSpec render() {
        MethodSpec method = MethodSpec.methodBuilder(methodName)//
                .addModifiers(Modifier.PUBLIC)//
                .returns(TypeName.VOID)//
                .addParameter(parameterType, "value")//
                .addStatement("$T edit = $L.edit()", EDITOR, instanceName)//
                .addStatement("edit.$L($S, value)", setMethodName, key)//
                .addStatement("edit.apply()")//
                .build();
        return method;
    }
}
