package com.yurnero.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Definition {
    String javaFileName() default "SharedPreferences";
    String packageName() default "";
    String spFileName() default "a_pref";
}
