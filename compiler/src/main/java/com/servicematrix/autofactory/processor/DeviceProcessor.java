package com.servicematrix.autofactory.processor;

import com.google.auto.service.AutoService;
import com.servicematrix.autofactory.builder.*;
import com.servicematrix.autofactory.annotationdomain.DeviceElementInfo;
import com.servicematrix.autofactory.annotationdomain.ElementInfo;
import com.servicematrix.autofactory.annotations.Device;
import com.servicematrix.autofactory.annotations.DeviceElement;
import com.squareup.javapoet.ClassName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedAnnotationTypes("com.servicematrix.autofactory.annotations.Device")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DeviceProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<ClassName,List<ElementInfo>> deviceClassMap = new HashMap<>();
        Map<Name, DeviceElementInfo> fieldsMap = new HashMap<>();
        for(Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(DeviceElement.class)){
            DeviceElement deviceElement = annotatedElement.getAnnotation(DeviceElement.class);
            VariableElement field = (VariableElement)annotatedElement;
            TypeMirror fieldType = field.asType();
            DeviceElementInfo deviceElementInfo = new DeviceElementInfo(deviceElement.api(),fieldType.toString());
            fieldsMap.put(field.getSimpleName(),deviceElementInfo);
        }
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Device.class)){
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error("Only class can be annotated with Device", annotatedElement);
                return true;
            }
            TypeElement typeElement = (TypeElement) annotatedElement;
            ClassName className = ClassName.get(typeElement);
            if (!deviceClassMap.containsKey(className)) {
                deviceClassMap.put(className, new ArrayList<>());
            }
        }
        try {
            new APIBuilder(filer,deviceClassMap,fieldsMap).generateDeviceController();
            new LocalQueueBuilder(filer,deviceClassMap).generateLocalQueue();
            new InBoundHandlerBuilder(filer,deviceClassMap).generateInBoundHandler();
            new OutBoundHandlerBuilder(filer,deviceClassMap).generateOutBoundHandler();
            new DeviceBuilder(filer,deviceClassMap,fieldsMap).generateDeviceClass();
            new MainBuilder(filer,deviceClassMap).generateMain();
        } catch (IOException e) {
            error(e.getMessage());
        }
        return true;
    }

    private ClassName getName(TypeMirror typeMirror) {
        String rawString = typeMirror.toString();
        int dotPosition = rawString.lastIndexOf(".");
        String packageName = rawString.substring(0, dotPosition);
        String className = rawString.substring(dotPosition + 1);
        return ClassName.get(packageName, className);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Device.class.getCanonicalName());
        annotations.add(DeviceElement.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(String message, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    private void note(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }
}
