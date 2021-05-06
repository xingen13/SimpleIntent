package com.ren.simpleintent.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public class LogUtil {

    public static void log(Messager messager,String msg){
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
