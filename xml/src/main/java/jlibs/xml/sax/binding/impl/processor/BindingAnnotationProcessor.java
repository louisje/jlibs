/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.xml.sax.binding.impl.processor;

import jlibs.core.annotation.processing.AnnotationError;
import jlibs.core.annotation.processing.AnnotationProcessor;
import jlibs.core.annotation.processing.Printer;
import jlibs.core.graph.Visitor;
import jlibs.core.lang.model.ModelUtil;
import jlibs.xml.sax.binding.SAXContext;
import jlibs.xml.sax.binding.impl.Delegate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static jlibs.core.annotation.processing.Printer.*;

/**
 * @author Santhosh Kumar T
 */
@SuppressWarnings({"unchecked"})
@SupportedAnnotationTypes("jlibs.xml.sax.binding.Binding")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BindingAnnotationProcessor extends AnnotationProcessor{
    private static final String SUFFIX = "Impl";
    public static final String FORMAT = "${package}.${class}"+SUFFIX;

    private static final BindingAnnotation BINDING_ELEMENT = new ElementAnnotation();
    private static final BindingAnnotation BINDING_START = new BindingStartAnnotation();
    private static final BindingAnnotation BINDING_TEXT = new BindingTextAnnotation();
    private static final BindingAnnotation BINDING_FINISH = new BindingFinishAnnotation();
    private static final BindingAnnotation RELATION_START = new RelationAnnotation(true);
    private static final BindingAnnotation RELATION_FINISH = new RelationAnnotation(false);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        for(TypeElement annotation: annotations){
            for(Element elem: roundEnv.getElementsAnnotatedWith(annotation)){
                Binding binding = new Binding();

                try{
                    TypeElement c = (TypeElement)elem;

                    if(c.getModifiers().contains(Modifier.FINAL))
                        throw new AnnotationError(c, ModelUtil.getAnnotationMirror(c, Visitor.Implement.class), "final class can't be annotated with "+jlibs.xml.sax.binding.Binding.class.getName());

                    while(c!=null && !c.getQualifiedName().contentEquals(Object.class.getName())){
                        process(binding, c);
                        c = ModelUtil.getSuper(c);
                    }

                    binding.handleStar();
                    binding.initID(0);
                    
                    Printer pw = null;
                    try{
                        pw = Printer.get((TypeElement)elem, jlibs.xml.sax.binding.Binding.class, FORMAT);
                        generateClass(binding, pw);
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }finally{
                        if(pw!=null)
                            pw.close();
                    }
                }catch(AnnotationError error){
                    error.report();
                }
            }
        }
        return true;
    }

    private void process(Binding binding, TypeElement c){
        for(ExecutableElement method: ElementFilter.methodsIn(c.getEnclosedElements())){
            for(AnnotationMirror mirror: method.getAnnotationMirrors()){
                if(BINDING_ELEMENT.matches(mirror))
                    BINDING_ELEMENT.consume(binding, method, mirror);
                else if(BINDING_START.matches(mirror))
                    BINDING_START.consume(binding, method, mirror);
                else if(BINDING_TEXT.matches(mirror))
                    BINDING_TEXT.consume(binding, method, mirror);
                else if(BINDING_FINISH.matches(mirror))
                    BINDING_FINISH.consume(binding, method, mirror);
                else if(RELATION_START.matches(mirror))
                    RELATION_START.consume(binding, method, mirror);
                else if(RELATION_FINISH.matches(mirror))
                    RELATION_FINISH.consume(binding, method, mirror);
            }
        }
    }

    private void generateClass(Binding binding, Printer pw) throws IOException{
        TypeElement clazz = pw.clazz;

        pw.printPackage();

        pw.importPackage(Delegate.class);
        pw.importClass(SAXContext.class);
        pw.importClass(Attributes.class);
        pw.importClass(SAXException.class);
        pw.importClass(QName.class);
        pw.println();

        pw.printClassDoc();

        pw.printlns(
            "@SuppressWarnings({\"unchecked\"})",
            "public class "+pw.generatedClazz +" extends BindingCumRelation{",
                PLUS,
                "public static final "+pw.generatedClazz +" INSTANCE = new "+pw.generatedClazz +"(new "+clazz.getSimpleName()+"());",
                "static{",
                    PLUS,
                    "INSTANCE.init();",
                    MINUS,
                "}"
        );
        pw.emptyLine(true);

        pw.print("public static final QName ELEMENT = ");
        AnnotationMirror bindingMirror = ModelUtil.getAnnotationMirror(clazz, jlibs.xml.sax.binding.Binding.class);
        if(bindingMirror==null)
            pw.println("null;");
        else{
            String element = ModelUtil.getAnnotationValue(clazz, bindingMirror, "value");
            if(element.indexOf('/')!=-1)
                throw new AnnotationError(clazz, bindingMirror, "value of "+jlibs.xml.sax.binding.Binding.class+" should be single element, but not element path");
            pw.println(Binding.toJava(Binding.toQName(clazz, bindingMirror, element))+";");
        }
        pw.emptyLine(true);

        pw.printlns(
            "private final "+clazz.getSimpleName()+" handler;",
            "private "+pw.generatedClazz +"("+clazz.getSimpleName()+" handler){",
                PLUS,
                "this.handler = handler;",
                MINUS,
            "}"
        );
        pw.emptyLine(true);

        pw.print("private void init()");
        if(binding.registry.size()>0)
            generateInitMethod(binding, pw);
        else{
            pw.println("{}");
            pw.emptyLine(true);
        }

        BINDING_START.printMethod(pw, binding);
        BINDING_TEXT.printMethod(pw, binding);
        BINDING_FINISH.printMethod(pw, binding);
        RELATION_START.printMethod(pw, binding);
        RELATION_FINISH.printMethod(pw, binding);

        pw.indent--;
        pw.emptyLine(false);
        pw.println("}");
    }

    private void generateInitMethod(Binding binding, Printer pw){
        if(binding.registry.size()>0){
            pw.println("{");
            pw.indent++;
            if(binding.id==0)
                pw.println("Delegate leaf = new Delegate(this);");
            for(Map.Entry<QName, BindingRelation> entry: binding.registry.entrySet()){
                QName qname = entry.getKey();
                BindingRelation bindingRelation = entry.getValue();
                Binding childBinding = bindingRelation.binding;

                boolean useDelegate = childBinding.registry.size()>0;

                if(useDelegate)
                    pw.print("Registry registry"+childBinding.id+" = ");
                pw.print("registry"+(binding.id>0?binding.id:"")+".register(");
                if(childBinding.element!=null){
                    pw.print(Binding.toJava(qname)+", 0, ");
                    String className = ModelUtil.getPackage(childBinding.element).equals(ModelUtil.getPackage(pw.clazz))
                                            ? childBinding.element.getSimpleName().toString()
                                            : childBinding.element.getQualifiedName().toString();
                    pw.println(className+SUFFIX+".INSTANCE, "+childBinding.id+", this);");
                }else{
                    pw.print(Binding.toJava(qname)+", "+childBinding.id+", "+(useDelegate ?"new Delegate(this)":"leaf")+", ");
                    pw.println(childBinding.id+", this);");
                    generateInitMethod(childBinding, pw);
                }
            }
            pw.indent--;
            pw.println("}");
            pw.emptyLine(true);
        }
    }
}
