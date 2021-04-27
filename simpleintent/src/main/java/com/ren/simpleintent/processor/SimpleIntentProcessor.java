package com.ren.simpleintent.processor;

import com.ren.simpleintent.annotion.SimpleIntent;
import com.ren.simpleintent.entity.SimpleIntentEntity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author renheng
 * @Description
 * @date 2021/4/7
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleIntentProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(SimpleIntent.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeMirror, SimpleIntentEntity> map = new HashMap<>();
        String pkName = null;
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(SimpleIntent.class);
        for (Element element : bindViewElements) {
            //1.获取包名
            PackageElement packageElement = mElementUtils.getPackageOf(element);
            pkName = packageElement.getQualifiedName().toString();
            note(String.format("package = %s", pkName));

            //2.获取包装类类型
            TypeElement enclosingElement = (TypeElement) element;
            String enclosingName = enclosingElement.getQualifiedName().toString();
            note(String.format("enclosindClass = %s", enclosingElement));


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
                note("clazzType=" + className);
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

            map.put(element.asType(), entity);
        }
        if (pkName == null) {
            return false;
        }
        try {
            note("pkName="+pkName);
            brewCode(pkName, map);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void brewCode(String packageName, Map<TypeMirror, SimpleIntentEntity> map) throws IOException, ClassNotFoundException {
        String classNameStr = "IntentManager";
        List<MethodSpec> methodSpecList = new ArrayList<>();


        /**
         * 给每个activity创建跳转方法
         */
        for (Map.Entry<TypeMirror, SimpleIntentEntity> entry : map.entrySet()) {
            TypeMirror clazz = entry.getKey();
            SimpleIntentEntity value = entry.getValue();
            List<TypeMirror> paramsTypeList = value.paramTypesList;
            String clazzSimpleName = getClassSimpleName(clazz.toString());
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
                String simpleName = getClassSimpleName(paramType.toString());
                note("simpleName=" + simpleName);
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
            methodSpecList.add(intentMethodBuilder.build());
        }

        TypeSpec clazzType = TypeSpec.classBuilder(classNameStr)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methodSpecList)
                .build();

        note("packageName="+packageName);
        note("clazzType="+clazzType);
        JavaFile javaFile = JavaFile.builder("com.ren.simpleintent", clazzType)
                .build();

        javaFile.writeTo(mFiler);

    }

    private String getClassSimpleName(String classFullName) {
        String[] split = classFullName.split("\\.");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return classFullName;
    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }



}
