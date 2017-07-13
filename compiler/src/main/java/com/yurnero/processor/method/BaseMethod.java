package com.yurnero.processor.method;

import com.squareup.javapoet.MethodSpec;

/**
 * description:.
 *
 * @author：qiangjie @version：17/7/9 下午11:17
 */

public abstract class BaseMethod {
    protected String key;
    protected String baseMethodName;
    protected String instanceName;

    public abstract MethodSpec render();

    public BaseMethod(String key, String instanceName) {
        this.key = key;
        this.instanceName = instanceName;
        this.baseMethodName = formatKey(key);
    }

    private String formatKey(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key is null");
        }
        StringBuilder sb = new StringBuilder();
        if (key.contains("_")) {
            String[] separatedKeys = key.split("_");
            for (String separatedKey : separatedKeys) {
                sb.append(separatedKey.substring(0, 1).toUpperCase() + separatedKey.substring(1));
            }
        } else {
            sb.append(key.substring(0, 1).toUpperCase() + key.substring(1));
        }
        return sb.toString();
    }
}
