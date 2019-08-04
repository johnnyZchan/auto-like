package com.johnny.tools.autolike.utils;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class IDUtil {

    public static String uuid() {
        UUID id = Generators.timeBasedGenerator().generate();
        return id.toString().replaceAll("-", "");
    }

    public static String originalUUID() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
