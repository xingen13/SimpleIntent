# simpleIntent

针对Activity的跳转，进行了简化及统一处理，通过注解标记Activity，统一自动生成Intent的跳转的工具类IntentManager。


# 使用

@SimpleIntent(paramTypes = {int.class,String.class},flags = Intent.FLAG_ACTIVITY_CLEAR_TOP)
public class Test1Activity extends Activity {
}

SimpleIntent标记的类会生成startActivity跳转方法

@ResultIntent(paramTypes = {int.class,String.class},flags = Intent.FLAG_ACTIVITY_CLEAR_TOP,requestCode = 1)
public class Test2Activity extends Activity {  
}

ResultIntent标记的类会生成startActivityForResult跳转方法
