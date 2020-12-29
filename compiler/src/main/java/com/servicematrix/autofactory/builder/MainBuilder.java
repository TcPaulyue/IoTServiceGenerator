package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.servicematrix.client.ClientMessageSender;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.vertx.core.Vertx;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainBuilder {
    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;

    public MainBuilder(Filer filer, Map<ClassName, List<ElementInfo>> input) {
        this.filer = filer;
        this.input = input;
    }

    public void generateMain() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder("Main")
                .addModifiers(Modifier.PUBLIC);
        ClassName name = null;
        for(ClassName className:input.keySet()){
            name = className;
        }
        ClassName name1 = ClassName.get("com.servicematrix.autofactory", name.simpleName());
        ClassName name2 = ClassName.get("com.servicematrix.autofactory", name.simpleName()+"Controller");

        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(String[].class,"args")
                .beginControlFlow("try")
                .addStatement("$T vertx = $T.vertx()", Vertx.class,Vertx.class)
                .addStatement("vertx.deployVerticle(new $L(new $L()))",name2.simpleName(),name1.simpleName())
                .addStatement("InBoundMessageMap.init()")
                .addStatement("$T executorService = $T.newFixedThreadPool(1)", ExecutorService.class, Executors.class)
                .addStatement("$T clientMessageSender = new $T(\"localhost\",8082, new $L())", ClientMessageSender.class
                ,ClientMessageSender.class,"InBoundMessageHandler")
                .addStatement("executorService.execute(new $L(clientMessageSender,new MessageController()))","OutBoundMessageHandler")
                .endControlFlow()
                .beginControlFlow("catch(Exception e)")
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .build();
        builder.addMethod(methodSpec);
        TypeSpec spec = builder.build();
        JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", spec)
                .build();

        javaFile.writeTo(filer);

    }
}
