package com.ren.simpleintent.utils;

/**
 * @author renheng
 * @Description
 * @date 2021/4/7
 */

public class StringUtil {


    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String firstLowCase(String str) {
        if (str!=null&&str!=""){
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return str;
    }


    /**
     * 得到Class的SimpleName
     * @param classFullName
     * @return
     */
    public static String getClassSimpleName(String classFullName) {
        String[] split = classFullName.split("\\.");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return classFullName;
    }
}
