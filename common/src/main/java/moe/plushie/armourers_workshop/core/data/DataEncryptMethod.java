package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.utils.Objects;

public enum DataEncryptMethod {

    PASSWORD("password"),
    AUTH("auth");

    private final String method;

    DataEncryptMethod(String method) {
        this.method = method;
    }

    public String key(String text) {
        return Objects.md5(method + ";" + Objects.md5(String.format("%s(%s)", method, text)) + ";" + "aw");
    }

    public String signature(String key) {
        return method + ";" + Objects.md5(String.format("signature(%s)", key));
    }

    public String method() {
        return method;
    }

}
