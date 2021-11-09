package com.zhongbin.miaoshademo.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(?:(?:\\+|00)86)?1[3-9]\\d{9}$");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile))
            return false;
        Matcher matcher = MOBILE_PATTERN.matcher(mobile);
        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("13166666666"));
    }
}
