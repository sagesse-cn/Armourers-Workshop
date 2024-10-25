package com.apple.library.impl;

import moe.plushie.armourers_workshop.core.utils.Objects;

public class ObjectImpl {

    public static <S, T> T unsafeCast(S src) {
        // noinspection unchecked
        return (T) src;
    }

    // "<%s: 0x%x; arg1 = arg2; ...; argN-1 = argN>"
    public static String makeDescription(Object obj, Object... arguments) {
        return Objects.toString(obj, arguments);
    }
}
