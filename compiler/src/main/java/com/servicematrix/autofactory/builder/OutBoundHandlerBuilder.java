package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.servicematrix.client.ClientMessageSender;
import com.servicematrix.msg.RequestMessage;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class OutBoundHandlerBuilder {
    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;

    public OutBoundHandlerBuilder(Filer filer,Map<ClassName, List<ElementInfo>> input) {
        this.filer = filer;
        this.input = input;
    }

    public void generateOutBoundHandler() throws IOException {
        for(ClassName name:input.keySet()){
            TypeSpec.Builder builder = TypeSpec.classBuilder("OutBoundMessageHandler")
                    .addSuperinterface(Runnable.class)
                    .addModifiers(Modifier.PUBLIC);
            FieldSpec fieldSpec = FieldSpec.builder(boolean.class,"stopped",Modifier.PRIVATE)
                    .addModifiers(Modifier.VOLATILE)
                    .initializer("false").build();
            builder.addField(fieldSpec);

            ClassName name1 = ClassName.get("com.serviceMatrix.autofactory", "MessageController");
            fieldSpec = FieldSpec.builder(name1,"messageController",Modifier.PRIVATE)
                    .build();
            builder.addField(fieldSpec);

            fieldSpec = FieldSpec.builder(ClientMessageSender.class,"clientMessageSender",Modifier.PRIVATE)
                    .build();
            builder.addField(fieldSpec);

            MethodSpec methodSpec = MethodSpec.methodBuilder("run")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .beginControlFlow("while(!stopped)")
                    .addStatement("$T requestMessage = $L.$L()", RequestMessage.class,name.simpleName()+"MessageQueue","getRequestMessage")
                    .beginControlFlow("if(requestMessage!=null)")
                    .beginControlFlow("try")
                    .addStatement("Class clazz = $L.getClass()","messageController")
                    .addStatement("$T method = clazz.getDeclaredMethod(InBoundMessageMap.getAPI(requestMessage.getRequestBody().getBody()))", Method.class)
                    .addStatement("method.invoke($L)","messageController")
                    .addStatement("clientMessageSender.sendMessage(requestMessage.getRequestBody())")
                    .endControlFlow()
                    .beginControlFlow("catch (NoSuchMethodException | IllegalAccessException\n" +
                            "        | $T | $T e)",InvocationTargetException.class,UnsupportedEncodingException.class)
                    .addStatement("e.printStackTrace()")
                    .endControlFlow()
                    .endControlFlow()
                    .endControlFlow()
                    .beginControlFlow("try")
                    .addStatement("Thread.sleep(1000)")
                    .endControlFlow()
                    .beginControlFlow("catch(InterruptedException e)")
                    .addStatement("e.printStackTrace()")
                    .endControlFlow()
                    .build();
            builder.addMethod(methodSpec);

            methodSpec = MethodSpec.constructorBuilder().addParameter(ClientMessageSender.class,"clientMessageSender")
                    .addParameter(name1,"messageController")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.clientMessageSender = clientMessageSender")
                    .addStatement("this.$L = $L","messageController","messageController")
                    .build();
            builder.addMethod(methodSpec);

            TypeSpec spec = builder.build();
            JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", spec)
                    .build();

            javaFile.writeTo(filer);

        }
    }
}
