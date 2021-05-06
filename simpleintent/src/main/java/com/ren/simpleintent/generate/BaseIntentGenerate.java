package com.ren.simpleintent.generate;

import com.ren.simpleintent.entity.IEntity;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public abstract class BaseIntentGenerate implements IGenerate {

    private Messager mMessager;

    public BaseIntentGenerate(Messager messager){
        mMessager = messager;
    }

    protected void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }


    /**
     * 根据annotion组织数据
     * @return
     */
    public abstract IEntity createIntentEntity(Element element);

    /**
     * 创建跳转方法
     * @return
     */
    public abstract  MethodSpec genertaIntentMethod(TypeMirror clazz, IEntity intentEntity);
}
