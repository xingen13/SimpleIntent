package com.ren.simpleintent.activity;

import android.app.Activity;
import android.content.Intent;

import com.ren.simpleintent.annotion.SimpleIntent;

/**
 * @author renheng
 * @Description
 * @date 2021/4/27
 */

@SimpleIntent(paramTypes = {int.class,String.class},flags = Intent.FLAG_ACTIVITY_CLEAR_TOP)
public class Test1Activity extends Activity {


}
