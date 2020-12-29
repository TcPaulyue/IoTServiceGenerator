package com.servicematrix.autofactory.builder;

import com.alibaba.fastjson.JSONObject;
import com.servicematrix.autofactory.annotationdomain.APIConfigInfo;
import com.servicematrix.autofactory.annotationdomain.InBoundMessageInfo;
import com.squareup.javapoet.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class InBoundControllerBuilder {
    private Filer filer;
    private Map<Name, InBoundMessageInfo> map;
    private APIConfigInfo apiConfigInfo;

    public InBoundControllerBuilder(Filer filer, Map<Name, InBoundMessageInfo> map, APIConfigInfo apiConfigInfo) {
        this.filer = filer;
        this.map = map;
        this.apiConfigInfo = apiConfigInfo;
    }

    public void generateCode() throws IOException {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("MessageController")
                .addModifiers(Modifier.PUBLIC);
        for(Name name:map.keySet()){
            CodeBlock.Builder builder = CodeBlock.builder();
            builder.addStatement("String url = $S",apiConfigInfo.getUrl())
                    .addStatement("$T client = $T.createDefault()", CloseableHttpClient.class, HttpClients.class)
                    .addStatement("$T httpPost = new $T(url)", HttpPost.class,HttpPost.class)
                    .addStatement("$T jsonObject = new $T()",JSONObject.class,JSONObject.class)
                    .addStatement("jsonObject.put($S,$S)","entity_id",apiConfigInfo.getEntity_id())
                    .addStatement("$T entity = null", StringEntity.class)
                    .beginControlFlow("try")
                    .addStatement("entity = new $T(jsonObject.toString())",StringEntity.class)
                    .addStatement("httpPost.setEntity(entity)")
                    .addStatement("httpPost.setHeader($S,$S)","Authorization",apiConfigInfo.getAuthorization())
                    .addStatement("httpPost.setHeader($S,$S)","Content-type","application/json")
                    .addStatement("$T response = client.execute(httpPost)", HttpResponse.class)
                    .addStatement("client.close()")
                    .addStatement("System.out.println(response.toString())")
                    .endControlFlow()
                    .beginControlFlow("catch($T e)", IOException.class)
                    .addStatement("e.printStackTrace()")
                    .endControlFlow();
            for(String s : map.get(name).getCodes()){
                builder.addStatement(s);
            }
            CodeBlock codeBlock = builder.build();
            MethodSpec methodSpec = MethodSpec.methodBuilder(name.toString())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Boolean.TYPE)
                    .addCode(codeBlock)
                    .build();

            typeSpecBuilder.addMethod(methodSpec);
        }
        TypeSpec typeSpec = typeSpecBuilder.build();
        JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", typeSpec)
                .build();
        javaFile.writeTo(filer);
    }
}
