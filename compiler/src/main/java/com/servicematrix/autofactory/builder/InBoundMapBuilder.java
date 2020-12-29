package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.InBoundMessageInfo;
import com.servicematrix.msg.RequestMessage;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class InBoundMapBuilder {
    private Filer filer;
    private Map<Name, InBoundMessageInfo> map;

    public InBoundMapBuilder(Filer filer, Map<Name, InBoundMessageInfo> map) {
        this.filer = filer;
        this.map = map;
    }

    public void generateInBoundMap() throws IOException {
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(HashMap.class),
                ClassName.get(String.class),
                ClassName.get(String.class)
        );
        TypeSpec.Builder builder = TypeSpec.classBuilder("InBoundMessageMap")
                .addModifiers(Modifier.PUBLIC);
        FieldSpec fieldSpec = FieldSpec.builder(inputMapTypeOfGroup,"inBoundMessageMap",Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .initializer("new $T<>()",HashMap.class).build();
        builder.addField(fieldSpec);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC);
        for(Name name:map.keySet()){
            methodBuilder.addStatement("inBoundMessageMap.put($S,$S)",map.get(name).getMessage()
                    ,name.toString());
        }
        MethodSpec methodSpec = methodBuilder.build();
        builder.addMethod(methodSpec);

        methodSpec = MethodSpec.methodBuilder("getAPI")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(String.class)
                .addParameter(String.class,"inBoundMessage")
                .addStatement("return $L.get(inBoundMessage)","inBoundMessageMap")
                .build();

        builder.addMethod(methodSpec);
        TypeSpec spec = builder.build();
        JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", spec)
                .build();

        javaFile.writeTo(filer);

    }
}
