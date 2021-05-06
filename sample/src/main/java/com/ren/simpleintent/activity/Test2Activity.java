package com.ren.simpleintent.activity;

import android.app.Activity;
import android.content.Intent;

import com.ren.simpleintent.annotion.ResultIntent;

/**
 * @author renheng
 * @Description
 * @date 2021/4/27
 */

@ResultIntent(paramTypes = {int.class,String.class},flags = Intent.FLAG_ACTIVITY_CLEAR_TOP,requestCode = 1)
public class Test2Activity extends Activity {


}
