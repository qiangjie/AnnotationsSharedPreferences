package com.yurnero.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AnnotationPrefs {
    String javaFileName() default "SharedPreferencesUtil";
    String prefsFileName() default "a_prefs";
}
