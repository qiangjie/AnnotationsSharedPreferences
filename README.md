# AnnotationsSharedPreferences

AnnotationsSharedPreferences是基于android sharedPreferences的快速持久化框架，它简化了SharePreferences的使用，支持SharePreferences的初始化、基础类型数据的设置和获取。AnnotationsSharedPreferences使用android apt(annotation process tool)技术实现，在编译期间实现代码的生成。

## 特点
1、支持boolean、int、float、string、long五种基本类型的快速持久化;
2、支持多个持久化类的定义和生成；
3、编译期间生成代码，不影响运行时速度。

## 如何定义
```java
@AnnotationPrefs(javaFileName = "MySharedPreferences", prefsFileName = "mysp")
public class SharedPreferencesUtils {
    @StringEntity(key = "name", defaultValue = "hah")
    String NAME;

    @BooleanEntity(key = "is_chinese", defaultValue = true)
    boolean IS_CHINESE;

    @IntEntity(key = "age", defaultValue = 16)
    int KEY_AGE;

    @FloatEntity(key = "hight", defaultValue = 16.6f)
    float KEY_HEIGHT;

    @LongEntity(key = "number", defaultValue = 188L)
    long KEY_NUMBER;
}
```

## 如何引用

```
dependencies {
    compile 'com.github.qiangjie:aptsharedpreferences-annotations:1.0.3'
    annotationProcessor 'com.github.qiangjie:aptsharedpreferences-compiler:1.0.3'

}
```

## 使用方式
一般建议在application的onCreate中进程初始化。

    MySharedPreferences.init(getApplicationContext());

读取和设置方法如下：
 
    MySharedPreferences.get().setAge(19);
    
    MySharedPreferences.get().getAge();
    
    

## 项目地址
[https://github.com/qiangjie/AnnotationsSharedPreferences][1]


  [1]: https://github.com/qiangjie/AnnotationsSharedPreferences