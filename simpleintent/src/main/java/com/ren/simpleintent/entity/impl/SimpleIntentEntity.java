package com.ren.simpleintent.entity.impl;


import com.ren.simpleintent.entity.IEntity;

import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * @author renheng
 * @Description
 * @date 2021/4/27
 */

public class SimpleIntentEntity implements IEntity {

    public List<TypeMirror> paramTypesList;

    public List<Integer> flagsList;
}
