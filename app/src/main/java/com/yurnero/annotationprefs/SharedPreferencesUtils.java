package com.yurnero.annotationprefs;

import com.yurnero.annotations.BooleanEntity;
import com.yurnero.annotations.AnnotationPrefs;
import com.yurnero.annotations.FloatEntity;
import com.yurnero.annotations.IntEntity;
import com.yurnero.annotations.LongEntity;
import com.yurnero.annotations.StringEntity;

/**
 * description:.
 *
 * @author：qiangjie @version：17/7/4 上午12:17
 */
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
