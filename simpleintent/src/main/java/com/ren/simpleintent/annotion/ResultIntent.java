package com.ren.simpleintent.annotion;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public @interface ResultIntent {

    Class[] paramTypes() default {};

    int[] flags() default {};

    int requestCode();
}
