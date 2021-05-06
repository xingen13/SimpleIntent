package com.ren.simpleintent.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author renheng
 * @Description
 * @date 2021/4/29
 */

public abstract class BaseProcessor extends AbstractProcessor {

    protected Filer mFiler;
    protected Messager mMessager;
    protected Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }



    protected void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
