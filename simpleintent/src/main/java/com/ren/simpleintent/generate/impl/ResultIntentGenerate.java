package com.ren.simpleintent.generate.impl;

import com.ren.simpleintent.annotion.ResultIntent;
import com.ren.simpleintent.entity.IEntity;
import com.ren.simpleintent.entity.impl.ResultIntentEntity;
import com.ren.simpleintent.generate.BaseIntentGenerate;
import com.ren.simpleintent.utils.StringUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public class ResultIntentGenerate extends BaseIntentGenerate {

    private static ResultIntentGenerate intentGenerate;

    public static ResultIntentGenerate getInstance(Messager messager){
        if (intentGenerate==null){
            intentGenerate = new ResultIntentGenerate(messager);
        }
        return intentGenerate;
    }

    private ResultIntentGenerate(Messager messager) {
        super(messager);
        note("ResultIntentGenerate .......");
    }


    @Override
    public IEntity createIntentEntity(Element element) {
        ResultIntent intent = element.getAnnotation(ResultIntent.class);
        List<? extends TypeMirror> paramClazzType = null;
        try {
            intent.paramTypes();
        } catch (MirroredTypesException mte) {
            paramClazzType = mte.getTypeMirrors();
        }
        List<TypeMirror> paramTypesList = new ArrayList<>();
        int size = paramClazzType.size();
        for (int i = 0; i < size; i++) {
            TypeMirror typeMirror = paramClazzType.get(i);
            String className = typeMirror.toString();
            paramTypesList.add(typeMirror);
        }

        int[] flags = intent.flags();
        List<Integer> flagsList = new ArrayList();
        for (int i=0;i<flags.length;i++){
            flagsList.add(flags[i]);
        }

        int requestCode = intent.requestCode();
        ResultIntentEntity entity = new ResultIntentEntity();
        entity.paramTypesList=paramTypesList;
        entity.flagsList=flagsList;
        entity.requestCode = requestCode;
        return entity;
    }

    @Override
    public MethodSpec genertaIntentMethod(TypeMirror clazz, IEntity intentEntity) {
        if (intentEntity instanceof ResultIntentEntity){
            ResultIntentEntity value = (ResultIntentEntity) intentEntity;
            List<TypeMirror> paramsTypeList = value.paramTypesList;
            String clazzSimpleName = StringUtil.getClassSimpleName(clazz.toString());
            ClassName intentClass = ClassName.get("android.content","Intent");
            ClassName contextClass = ClassName.get("android.app","Activity");

            ParameterSpec contextParam = ParameterSpec.builder(contextClass, "activity").build();
            MethodSpec.Builder intentMethodBuilder = MethodSpec.methodBuilder("startResultTo" + clazzSimpleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.FINAL)
                    .addModifiers(Modifier.STATIC)
                    .addParameter(contextParam)
                    .addStatement("$T intent = new $T($N,$T.class)", intentClass,intentClass,contextParam,clazz);

            /**
             * 生成方法里的参数
             */
            for (int i = 0; i < paramsTypeList.size(); i++) {
                TypeMirror paramType = paramsTypeList.get(i);
                String simpleName = StringUtil.getClassSimpleName(paramType.toString());
//                note("simpleName=" + simpleName);
                TypeName paramTypeName = ClassName.get(paramType);
                String arg = "p"+i;
                ParameterSpec parameterSpec = ParameterSpec.builder(paramTypeName, arg).build();
                intentMethodBuilder.addParameter(parameterSpec);
                intentMethodBuilder.addStatement("intent.putExtra($S,$N)",arg,parameterSpec);
            }

            List<Integer> flagsList = value.flagsList;

            for (int i=0;i<flagsList.size();i++){
                int flag = flagsList.get(i);
                intentMethodBuilder.addStatement("intent.addFlags($L)",flag);
            }
            intentMethodBuilder.addStatement("activity.startActivityForResult(intent,$L)",value.requestCode);
            return intentMethodBuilder.build();
        }
        return null;
    }
}
