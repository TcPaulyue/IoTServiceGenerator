package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.DeviceElementInfo;
import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.squareup.javapoet.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

public class APIBuilder {
    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;
    private Map<Name,DeviceElementInfo> fields;

    public APIBuilder(Filer filer, Map<ClassName, List<ElementInfo>> input, Map<Name, DeviceElementInfo> fields) {
        this.filer = filer;
        this.input = input;
        this.fields = fields;
    }


    public void generateDeviceController() throws IOException{
        for (ClassName key : input.keySet()) {
            FieldSpec airPurifier = FieldSpec.builder(key,key.simpleName().toLowerCase())
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            List<MethodSpec> methodSpecs = new ArrayList<>();
            MethodSpec constructor = MethodSpec.constructorBuilder().addParameter(key,key.simpleName().toLowerCase())
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.$L = $L",key.simpleName().toLowerCase(),key.simpleName().toLowerCase()).build();

            MethodSpec.Builder vertBuilder = MethodSpec.methodBuilder("start")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Future.class, "fut")
                    .addStatement("$T router = $T.router(vertx)", Router.class,Router.class);

            for(Name name:fields.keySet()){
                MethodSpec methodSpec = MethodSpec.methodBuilder("get"+name)
//                        .addAnnotation(AnnotationSpec.builder(GetMapping.class)
//                                .addMember("value","$S",fields.get(name).getApi())
//                                .build())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(RoutingContext.class,"routingContext")
                        .addStatement("routingContext.response().putHeader($S,$S).end($L.$L)"
                                ,"content-type","application/json; charset=utf-8",key.simpleName().toLowerCase(),name)
                        .build();
                methodSpecs.add(methodSpec);
                vertBuilder.addStatement("router.get($S).handler(this::$L)",fields.get(name).getApi(),"get"+name);
                methodSpec = MethodSpec.methodBuilder("set"+name)
                       // .addAnnotation(AnnotationSpec.builder(PostMapping.class)
                       //         .addMember("value","$S",fields.get(name).getApi())
                       //         .build())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(RoutingContext.class,"routingContext")
                        .addStatement("$T s = routingContext.getBodyAsString()",String.class)
                        .addStatement("this.$L.$L = s",key.simpleName().toLowerCase(),name)
                        .addStatement("routingContext.response().setStatusCode(201).putHeader($S,$S).end(s)"
                                ,"content-type","application/json; charset=utf-8")
                        .build();
                methodSpecs.add(methodSpec);
                vertBuilder.addStatement("router.post($S).handler(this::$L)",fields.get(name).getApi(),"set"+name);
            }

            vertBuilder.addStatement("vertx.createHttpServer().requestHandler(router::accept).listen(config()." +
                    "getInteger(\"http.port\",8080),result->{   if (result.succeeded()) {\n" +
                    "                                fut.complete();\n" +
                    "                            } else {\n" +
                    "                                fut.fail(result.cause());\n" +
                    "                            }})");


            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(key.simpleName()+"Controller")
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(AbstractVerticle.class)
                    .addField(airPurifier);
            typeSpecBuilder.addMethod(constructor);
            typeSpecBuilder.addMethod(vertBuilder.build());
            for(MethodSpec methodSpec:methodSpecs){
                typeSpecBuilder.addMethod(methodSpec);
            }
            TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", typeSpec)
                    .build();

            javaFile.writeTo(filer);
        }
    }

}
