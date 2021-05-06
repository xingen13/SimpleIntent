package com.ren.simpleintent.entity;


import com.ren.simpleintent.generate.IGenerate;

import java.lang.annotation.Annotation;

/**
 * @author renheng
 * @Description
 * @date 2021/4/30
 */

public class Node {


    public Class<? extends Annotation> annotationClazz;

    public IGenerate generate;

    public Class<? extends IEntity> entityClazz;

    public Node(Class<? extends Annotation> annotationClazz, IGenerate generate, Class<? extends IEntity> entityClazz) {
        this.annotationClazz = annotationClazz;
        this.generate = generate;
        this.entityClazz = entityClazz;
    }
}
