package com.ren.simpleintent.processor;

import com.ren.simpleintent.annotion.ResultIntent;
import com.ren.simpleintent.annotion.SimpleIntent;
import com.ren.simpleintent.entity.IEntity;
import com.ren.simpleintent.entity.Node;
import com.ren.simpleintent.entity.impl.ResultIntentEntity;
import com.ren.simpleintent.entity.impl.SimpleIntentEntity;
import com.ren.simpleintent.generate.BaseIntentGenerate;
import com.ren.simpleintent.generate.GenerateFactory;
import com.ren.simpleintent.generate.impl.ResultIntentGenerate;
import com.ren.simpleintent.generate.impl.SimpleIntentGenerate;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author renheng
 * @Description
 * @date 2021/4/7
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class IntentProcessor extends BaseProcessor {

    private GenerateFactory generateFactory;
    private List<Node> list = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        generateFactory=new GenerateFactory(mMessager);
        Node simpleNode = new Node(SimpleIntent.class, SimpleIntentGenerate.getInstance(mMessager), SimpleIntentEntity.class);
        Node resultNode = new Node(ResultIntent.class, ResultIntentGenerate.getInstance(mMessager), ResultIntentEntity.class);
        list=createList(simpleNode,resultNode);
    }

    private List<Node> createList(Node... nodes){
        return Arrays.asList(nodes);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        for (Node node:list){
            annotations.add(node.annotationClazz.getCanonicalName());
        }
        return annotations;
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeMirror, IEntity> map = new HashMap<>();

        for (Node node:list){
            putAnnotion(map,roundEnvironment,node);
        }

        try {
            brewCode(map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void putAnnotion(Map<TypeMirror, IEntity> map, RoundEnvironment roundEnvironment, Node node){
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(node.annotationClazz);
        for (Element element : bindViewElements) {
            BaseIntentGenerate intentGenerate = (BaseIntentGenerate) node.generate;
            IEntity intentEntity = intentGenerate.createIntentEntity(element);
            map.put(element.asType(), intentEntity);
        }
    }


    private void brewCode( Map<TypeMirror, IEntity> map) throws IOException, ClassNotFoundException {
        String classNameStr = "IntentManager";
        List<MethodSpec> methodSpecList = new ArrayList<>();

        /**
         * 创建跳转方法
         */
        for (Map.Entry<TypeMirror, IEntity> entry : map.entrySet()) {
            TypeMirror clazz = entry.getKey();
            IEntity intentEntity = entry.getValue();
            BaseIntentGenerate intentGenerate = (BaseIntentGenerate) generateFactory.getGenerateByEntity(intentEntity);
            methodSpecList.add(intentGenerate.genertaIntentMethod(clazz,intentEntity));
        }

        TypeSpec clazzType = TypeSpec.classBuilder(classNameStr)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methodSpecList)
                .build();

        note("clazzType="+clazzType);
        JavaFile javaFile = JavaFile.builder("com.ren.simpleintent", clazzType)
                .build();

        javaFile.writeTo(mFiler);

    }





}
