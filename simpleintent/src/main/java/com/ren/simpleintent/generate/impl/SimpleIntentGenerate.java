package com.ren.simpleintent.generate.impl;

import com.ren.simpleintent.annotion.SimpleIntent;
import com.ren.simpleintent.entity.IEntity;
import com.ren.simpleintent.entity.impl.SimpleIntentEntity;
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

public class SimpleIntentGenerate extends BaseIntentGenerate {

    private static SimpleIntentGenerate intentGenerate;

    public static SimpleIntentGenerate getInstance(Messager messager){
        if (intentGenerate==null){
            intentGenerate = new SimpleIntentGenerate(messager);
        }
        return intentGenerate;
    }

    private SimpleIntentGenerate(Messager messager) {
        super(messager);
        note("SimpleIntentGenerate .......");
    }

    @Override
    public IEntity createIntentEntity(Element element) {
        SimpleIntent simpleIntent = element.getAnnotation(SimpleIntent.class);
        List<? extends TypeMirror> paramClazzType = null;
        try {
            simpleIntent.paramTypes();
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

        int[] flags = simpleIntent.flags();
        List<Integer> flagsList = new ArrayList();
        for (int i=0;i<flags.length;i++){
            flagsList.add(flags[i]);
        }
        SimpleIntentEntity entity = new SimpleIntentEntity();
        entity.paramTypesList=paramTypesList;
        entity.flagsList=flagsList;
        return entity;
    }

    @Override
    public MethodSpec genertaIntentMethod(TypeMirror clazz, IEntity intentEntity) {
        if (intentEntity instanceof SimpleIntentEntity){
            SimpleIntentEntity value = (SimpleIntentEntity) intentEntity;
            List<TypeMirror> paramsTypeList = value.paramTypesList;
            String clazzSimpleName = StringUtil.getClassSimpleName(clazz.toString());
            ClassName intentClass = ClassName.get("android.content","Intent");
            ClassName contextClass = ClassName.get("android.content","Context");

            ParameterSpec contextParam = ParameterSpec.builder(contextClass, "context").build();
            MethodSpec.Builder intentMethodBuilder = MethodSpec.methodBuilder("startTo" + clazzSimpleName)
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

            intentMethodBuilder.addStatement("context.startActivity(intent)");
            return intentMethodBuilder.build();
        }
        return null;

    }
}
