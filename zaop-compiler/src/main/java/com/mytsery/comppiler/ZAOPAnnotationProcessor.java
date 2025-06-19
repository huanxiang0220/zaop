package com.mytsery.comppiler;

import com.google.auto.service.AutoService;
import com.mystery.zaop.annation.Permission;
import com.mystery.zaop.annation.PermissionBefore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {
        "com.mystery.zaop.annation.Permission",
        "com.mystery.zaop.annation.PermissionBefore",
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ZAOPAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotation processor started");

        // 用于存储已经出现过注解的方法名；//方法对应请求码
        Map<String, Map<ExecutableElement, Integer>> classMethodMap = new HashMap<>();

        //-------------------处理PermissionBefore这个注解
        // 遍历所有使用了 Permission 注解的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Permission.class);
        for (Element element : elements) {
            if (element instanceof ExecutableElement) {
                ExecutableElement methodElement = (ExecutableElement) element;
                // 获取方法所在的类
                TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
                String className = classElement.getSimpleName().toString();
                String methodName = methodElement.getSimpleName().toString();

                TypeMirror returnType = methodElement.getReturnType();
                if (returnType.getKind() != TypeKind.VOID) {
                    //被@Permission注解的方法不能带有返回值
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "被 @Permission注解的方法" + methodName +
                                    "' in class '" + className + "'不能带有返回值", methodElement);
                    return true;
                }

                //请求码
                int requestCode = methodElement.getAnnotation(Permission.class).requestCode();

                // 获取该类下已经出现过注解的方法集合
                Map<ExecutableElement, Integer> map = classMethodMap.get(className);
                if (map == null) {
                    map = new HashMap<>();
                    map.put(methodElement, methodElement.getAnnotation(Permission.class).requestCode());
                    classMethodMap.put(className, map);
                    continue;
                }
                for (Integer value : map.values()) {
                    if (requestCode == value) {
                        //存在一个请求码相同的注解，同样的请求码不允许通过编译
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "The @Permission is applied to the method '" + methodName +
                                        "' in class '" + className + "' more than once.", methodElement);
                        return true;
                    }
                }
                map.put(methodElement, requestCode);
            }
        }
        //-------------------处理PermissionBefore这个注解

        //处理PermissionBefore这个注解
        boolean before = processPermissionBefore(roundEnv);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Annotation processor completed");
        return before;
    }

    private boolean processPermissionBefore(RoundEnvironment roundEnv) {
        // 用于存储该类中是否已经出现过PermissionBefore注解；
        HashSet<String> beforeCountSet = new HashSet<>();

        // 遍历所有使用了 PermissionBefore 注解的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(PermissionBefore.class);
        if (elements.isEmpty()) {
            //当前类没有被@PermissionBefore注解的方法
            return false;
        }

        for (Element element : elements) {
            if (element instanceof ExecutableElement) {
                // 检查元素是否为可执行元素（方法或构造函数）
                ExecutableElement methodElement = (ExecutableElement) element;
                // 获取方法所在的类
                TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
                String className = classElement.getSimpleName().toString();
                String methodName = methodElement.getSimpleName().toString();
                if (beforeCountSet.contains(className)) {
                    //一个类只能定义一个被@PermissionBefore注解的方法，同样的请求码不允许通过编译
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "一个类只能定义一个被@PermissionBefore注解的方法", element);
                    return true;
                } else {
                    beforeCountSet.add(className);
                }

                //判断参数
                TypeMirror rationaleType = processingEnv.getElementUtils().getTypeElement("com.mystery.zaop.permission.IRationale").asType();
                TypeMirror intType = processingEnv.getTypeUtils().getPrimitiveType(TypeKind.INT);

                List<? extends VariableElement> parameters = methodElement.getParameters();
                int parameterCount = parameters.size();
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "parameterCount == " + parameterCount);

                if (parameterCount == 1) {
                    VariableElement parameter = parameters.get(0);
                    //获取参数的 TypeMirror
                    TypeMirror paramTypeMirror = parameter.asType();
                    //判断类型是否是IRationale
                    if (!paramTypeMirror.equals(rationaleType)) {
                        //存在一个请求码相同的注解，同样的请求码不允许通过编译
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "The @PermissionBefore is applied to the method '" + methodName +
                                        " 只有一个参数时，只能是IRationale类型的参数 " +
                                        "' in class '" + className, methodElement);
                        return true;
                    }
                } else if (parameterCount == 2) {
                    VariableElement parameter = parameters.get(0);
                    //获取参数的 TypeMirror
                    TypeMirror paramTypeMirror = parameter.asType();
                    //判断类型是否是IRationale
                    if (!paramTypeMirror.equals(intType)) {
                        //第一个参数只能是int类型，该参数代表的是请求码，否则不允许通过编译
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "The @PermissionBefore is applied to the method '" + methodName +
                                        "第一个参数是int类型：意为请求码 " +
                                        "' in class '" + className, methodElement);
                        return true;
                    }
                    parameter = parameters.get(0);
                    //获取参数的 TypeMirror
                    paramTypeMirror = parameter.asType();
                    //判断类型是否是IRationale
                    if (!paramTypeMirror.equals(rationaleType)) {
                        //第二个参数只能是IRationale类型，该参数代表的是库开发的接口回调，用于是否继续申请权限
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "The @PermissionBefore is applied to the method '" + methodName +
                                        "第二个参数是IRationale类型的接口：用于是否继续申请权限 " +
                                        "' in class '" + className, methodElement);
                        return true;
                    }
                } else {
                    //第二个参数只能是IRationale类型，该参数代表的是库开发的接口回调，用于是否继续申请权限
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "The @PermissionBefore is applied to the method '" + methodName +
                                    "只能是一个IRationale类型的参数的方法；或者第一个参数是int类型的请求码和第二个参数是IRationale类型的两个参数的方法 " +
                                    "' in class '" + className, methodElement);
                    return true;
                }
            }
        }
        return false;
    }

}