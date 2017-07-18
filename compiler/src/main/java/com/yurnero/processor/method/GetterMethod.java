package com.yurnero.processor.method;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Modifier;


public class GetterMethod extends BaseMethod {
    private final static String METHOD_PREFIX = "get";
    private String methodName;
    private String getMethodName;
    private TypeName returnType;
    private String defaultValue;

    public GetterMethod(String annotationName, String key, String instanceName, String defaultValue) {
        super(key, instanceName);
        this.instanceName = instanceName;
        this.key = key;
        this.methodName = METHOD_PREFIX + baseMethodName;
        this.defaultValue = defaultValue;
        parseAnnotation(annotationName);
    }

    private void parseAnnotation(String annotationName) {
        if (annotationName.endsWith("StringEntity")) {
            returnType = TypeName.get(String.class);
            getMethodName = "getString";
        } else if (annotationName.endsWith("BooleanEntity")) {
            returnType = TypeName.BOOLEAN;
            getMethodName = "getBoolean";
        } else if (annotationName.endsWith("FloatEntity")) {
            returnType = TypeName.FLOAT;
            getMethodName = "getFloat";
        } else if (annotationName.endsWith("IntEntity")) {
            returnType = TypeName.INT;
            getMethodName = "getInt";
        } else if (annotationName.endsWith("LongEntity")) {
            returnType = TypeName.LONG;
            getMethodName = "getLong";
        } else {
            throw new IllegalArgumentException("Annotation class is not exist");
        }
    }

    @Override
    public MethodSpec render() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)//
                .addModifiers(Modifier.PUBLIC)//
                .returns(returnType);
        if (getMethodName.equals("getString")) {
            builder.addStatement("return $L.$L($S, $S)", instanceName, getMethodName, key, defaultValue);
        } else if (getMethodName.equals("getFloat")) {
            builder.addStatement("return $L.$L($S, $Lf)", instanceName, getMethodName, key, defaultValue);
        } else if (getMethodName.equals("getInt")) {
            builder.addStatement("return $L.$L($S, $L)", instanceName, getMethodName, key, defaultValue);
        } else if (getMethodName.equals("getLong")) {
            builder.addStatement("return $L.$L($S, $LL)", instanceName, getMethodName, key, defaultValue);
        } else if (getMethodName.equals("getBoolean")) {
            builder.addStatement("return $L.$L($S, $L)", instanceName, getMethodName, key, defaultValue);
        }
        return builder.build();
    }
}
