package com.ren.simpleintent.generate;


import com.ren.simpleintent.entity.IEntity;
import com.ren.simpleintent.entity.impl.ResultIntentEntity;
import com.ren.simpleintent.entity.impl.SimpleIntentEntity;
import com.ren.simpleintent.generate.impl.ResultIntentGenerate;
import com.ren.simpleintent.generate.impl.SimpleIntentGenerate;

import javax.annotation.processing.Messager;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public class GenerateFactory {

    private Messager messager;

    public GenerateFactory(Messager messager){
        this.messager=messager;
    }


    public IGenerate getGenerateByEntity(IEntity iEntity){
        if (iEntity instanceof SimpleIntentEntity){
            return SimpleIntentGenerate.getInstance(messager);
        }else if (iEntity instanceof ResultIntentEntity){
            return ResultIntentGenerate.getInstance(messager);
        }
        return null;
    }

}
