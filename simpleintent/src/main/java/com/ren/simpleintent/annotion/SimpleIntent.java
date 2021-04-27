package com.ren.simpleintent.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author renheng
 * @Description
 * @date 2021/4/7
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface SimpleIntent {

    Class[] paramTypes() default {};

    int[] flags() default {};

}
