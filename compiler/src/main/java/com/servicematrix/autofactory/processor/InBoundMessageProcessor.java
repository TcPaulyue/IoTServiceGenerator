package com.servicematrix.autofactory.processor;

import com.servicematrix.autofactory.annotationdomain.APIConfigInfo;
import com.servicematrix.autofactory.annotations.APIConfigration;
import com.servicematrix.autofactory.builder.InBoundMapBuilder;
import com.servicematrix.autofactory.builder.InBoundControllerBuilder;
import com.servicematrix.autofactory.builder.MethodScanner;
import com.servicematrix.autofactory.annotationdomain.InBoundMessageInfo;
import com.servicematrix.autofactory.annotations.InBoundMessage;
import com.squareup.javapoet.ClassName;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InBoundMessageProcessor  extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<Name, InBoundMessageInfo> map = new HashMap<>();
        APIConfigInfo apiConfigInfo = new APIConfigInfo();
        for(Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(APIConfigration.class)){
            APIConfigration apiConfigration = annotatedElement.getAnnotation(APIConfigration.class);
            apiConfigInfo.setUrl(apiConfigration.url());
            apiConfigInfo.setEntity_id(apiConfigration.entity_id());
            apiConfigInfo.setAuthorization(apiConfigration.Authorization());
        }
        for(Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(InBoundMessage.class)){
            InBoundMessage inBoundMessage = annotatedElement.getAnnotation(InBoundMessage.class);
            ExecutableElement executableElement = (ExecutableElement)annotatedElement;
            Name name = executableElement.getSimpleName();
            TypeMirror typeMirror = executableElement.asType();
            String message = inBoundMessage.message();

            MethodScanner methodScanner = new MethodScanner();
            MethodTree methodTree = methodScanner.scan(executableElement, this.trees);
            BlockTree blockTree = methodTree.getBody();
            List<String> res = new ArrayList<>();
            List<? extends StatementTree> list = blockTree.getStatements();
            list.forEach(s-> {
                String tmp = s.toString();
                res.add(tmp.substring(0,tmp.lastIndexOf(';')));
            });
            InBoundMessageInfo inBoundMessageInfo = new InBoundMessageInfo(message,res);
            map.put(name,inBoundMessageInfo);
        }
        try {
            new InBoundControllerBuilder(filer,map,apiConfigInfo).generateCode();
            new InBoundMapBuilder(filer,map).generateInBoundMap();
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
        annotations.add(InBoundMessage.class.getCanonicalName());
        annotations.add(APIConfigration.class.getCanonicalName());
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
