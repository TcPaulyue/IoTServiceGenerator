package com.servicematrix.autofactory.builder;

import com.servicematrix.autofactory.annotationdomain.DeviceElementInfo;
import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DeviceBuilder {
    private Filer filer;
    private Map<ClassName, List<ElementInfo>> input;
    private Map<Name, DeviceElementInfo> fields;

    public DeviceBuilder(Filer filer, Map<ClassName, List<ElementInfo>> input, Map<Name, DeviceElementInfo> fields) {
        this.filer = filer;
        this.input = input;
        this.fields = fields;
    }

    public void generateDeviceClass() throws IOException {
        for (ClassName key : input.keySet()) {
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(key.simpleName())
                    .addModifiers(Modifier.PUBLIC);
            for(Name name:fields.keySet()){
                String type = fields.get(name).getType();
                FieldSpec fieldSpec = FieldSpec.builder(type.getClass(),name.toString(), Modifier.PUBLIC)
                        .build();
                typeSpecBuilder.addField(fieldSpec);
                MethodSpec methodSpec = MethodSpec.methodBuilder("get"+name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(fields.get(name).getType().getClass())
                        .addStatement("return this.$L",name)
                        .build();
                typeSpecBuilder.addMethod(methodSpec);
                methodSpec = MethodSpec.methodBuilder("set"+name)
                        .addParameter(fields.get(name).getType().getClass(),name.toString())
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.$L = $L",name,name.toString())
                        .build();
                typeSpecBuilder.addMethod(methodSpec);
            }
            TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder("com.serviceMatrix.autofactory", typeSpec)
                    .build();

            javaFile.writeTo(filer);
        }

    }
}
