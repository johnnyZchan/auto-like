package com.johnny.tools.autolike.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtil implements java.io.Serializable {
    public static boolean isMatch(String content, String regExp) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(regExp)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(content);
        return matcher.matches();
    }

    public static boolean hasMatch(String content, String regExp) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(regExp)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static String matcherText(String value, String regEx, int group) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(regEx)) {
            return null;
        }

        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(value);
        while (mat.find()) {
            return mat.group(group);
        }

        return null;
    }

    public static String getTextByReg(String content, String regExp, int group) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(regExp)) {
            return null;
        }

        Pattern pat = Pattern.compile(regExp);
        Matcher mat = pat.matcher(content);
        while (mat.find()) {
            return mat.group(group);
        }
        return null;
    }

    public static List<String> getTextsByReg(String content, String regExp, int group) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(regExp)) {
            return null;
        }

        Pattern pat = Pattern.compile(regExp);
        Matcher mat = pat.matcher(content);
        List<String> result = null;
        while (mat.find()) {
            if (result == null) {
                result = new ArrayList<String>();
            }
            result.add(mat.group(group));
        }
        return result;
    }

    public static String getMaxLengthText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength);
    }

    public static String  replaceAllBlank(String content) {
        if (StringUtils.isNotBlank(content)) {
            return content.replaceAll("\\t", "").replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        }

        return content;
    }
}
