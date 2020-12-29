package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.servicematrix.client.netty.ServerMessageHandler;
import com.servicematrix.msg.RequestMessage;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InBoundHandlerBuilder {
    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;

    public InBoundHandlerBuilder(Filer filer,Map<ClassName, List<ElementInfo>> input) {
        this.filer = filer;
        this.input = input;
    }

    public void generateInBoundHandler() throws IOException {
        for(ClassName name:input.keySet()){
            TypeSpec.Builder builder = TypeSpec.classBuilder("InBoundMessageHandler")
                    .superclass(ServerMessageHandler.class)
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec methodSpec = MethodSpec.methodBuilder("responseMessage")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(RequestMessage.class,"requestMessage")
                    .addStatement("$L.$L($L)",name.simpleName()+"MessageQueue","push"+name.simpleName(),"requestMessage")
                    .build();
            builder.addMethod(methodSpec);

            methodSpec = MethodSpec.methodBuilder("ackMessage")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(String.class,"s")
                    .addStatement("System.out.println(s)")
                    .build();
            builder.addMethod(methodSpec);
            TypeSpec spec = builder.build();
            JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", spec)
                    .build();

            javaFile.writeTo(filer);
        }

    }
}
