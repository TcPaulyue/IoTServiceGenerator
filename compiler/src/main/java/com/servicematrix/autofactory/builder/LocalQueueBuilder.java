package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.servicematrix.msg.RequestMessage;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class LocalQueueBuilder {

    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;

    public LocalQueueBuilder(Filer filer,Map<ClassName, List<ElementInfo>> input) {
        this.filer = filer;
        this.input = input;
    }

    public void generateLocalQueue() throws IOException {
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(ArrayBlockingQueue.class),
                ClassName.get(RequestMessage.class)
        );

        for(ClassName name:input.keySet()){
            TypeSpec.Builder builder = TypeSpec.classBuilder(name.simpleName()+"MessageQueue")
                    .addModifiers(Modifier.PUBLIC);
            String queueName = "ArrayBlockingQueue";
            FieldSpec fieldSpec = FieldSpec.builder(inputMapTypeOfGroup,name.simpleName()+"MessageQueue",Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .initializer("new $L<>(100)",queueName).build();
            builder.addField(fieldSpec);

            MethodSpec methodSpec = MethodSpec.methodBuilder("get"+name.simpleName()+"MessageQueue")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(inputMapTypeOfGroup)
                    .addStatement("return $L",name.simpleName()+"MessageQueue").build();
            builder.addMethod(methodSpec);

            methodSpec = MethodSpec.methodBuilder("push"+name.simpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(Boolean.TYPE)
                    .addParameter(RequestMessage.class,"requestMessage")
                    .addStatement("return $L.offer($L)",name.simpleName()+"MessageQueue","requestMessage")
                    .build();
            builder.addMethod(methodSpec);

            methodSpec = MethodSpec.methodBuilder("getRequestMessage")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(RequestMessage.class)
                    .addStatement("return $L.poll()",name.simpleName()+"MessageQueue")
                    .build();
            builder.addMethod(methodSpec);
            TypeSpec spec = builder.build();
            JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", spec)
                    .build();

            javaFile.writeTo(filer);
        }


    }
}
