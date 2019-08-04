package com.johnny.tools.autolike.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

    private static ResourceBundleMessageSource messageSource;

    @Autowired
    MessageUtil(ResourceBundleMessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    public static String getMessage(int code) {
        return getMessage(code, null);
    }

    public static String getMessage(int code, Object arg) {
        Object[] args = new Object[1];
        args[0] = arg;
        return getMessage(code, args);
    }

    public static String getMessage(int code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("code." + code, args, locale);
    }
}
