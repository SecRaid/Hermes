package fr.secraid.hermes.utils;

import org.jetbrains.annotations.Nullable;

public class StringUtils {
    public static boolean isEmpty(@Nullable String s) {
        return s == null || s.isEmpty();
    }
}
