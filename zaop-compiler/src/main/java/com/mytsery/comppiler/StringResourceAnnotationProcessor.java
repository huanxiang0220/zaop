package com.mytsery.comppiler;

import com.google.auto.service.AutoService;
import com.mystery.zaop.annation.Permission;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {
        "com.mystery.zaop.annation.Permission",
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({"moduleResPaths"})
public class StringResourceAnnotationProcessor extends AbstractProcessor {

    private final Set<String> allStringResources = new HashSet<>();
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        // 1. 扫描所有strings文件收集资源
        collectStringResources();
    }

    // 收集所有模块中以"values"开头的目录下的字符串资源
    private void collectStringResources() {
        try {
            String modulePaths = processingEnv.getOptions().get("moduleResPaths");
            if (modulePaths == null || modulePaths.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.WARNING, "No module resource paths provided");
                return;
            }

            for (String path : modulePaths.split(File.pathSeparator)) {
                File resDir = new File(path);
                if (resDir.exists() && resDir.isDirectory()) {
                    // 查找所有以"values"开头的子目录
                    findValuesDirs(resDir);
                }
            }
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to collect resources: " + e.getMessage());
        }
    }

    // 递归查找所有以"values"开头的目录
    private void findValuesDirs(File dir) {
        if (!dir.isDirectory()) return;

        // 查找当前目录中以"values"开头的子目录
        File[] valuesDirs = dir.listFiles(file ->
                file.isDirectory() && file.getName().startsWith("values")
        );

        if (valuesDirs != null) {
            for (File valuesDir : valuesDirs) {
                parseValuesDir(valuesDir);
            }
        }

        // 递归搜索子目录（深度优先）
        File[] subDirs = dir.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                findValuesDirs(subDir);
            }
        }
    }

    // 解析values目录下的XML文件
    private void parseValuesDir(File valuesDir) {
        File[] xmlFiles = valuesDir.listFiles(
                (dir, name) -> name.endsWith(".xml")
        );

        if (xmlFiles == null) return;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            for (File xmlFile : xmlFiles) {
                try {
                    Document doc = builder.parse(xmlFile);
                    NodeList stringNodes = doc.getElementsByTagName("string");
                    for (int i = 0; i < stringNodes.getLength(); i++) {
                        org.w3c.dom.Element element = (org.w3c.dom.Element) stringNodes.item(i);
                        String name = element.getAttribute("name");
                        if (!name.isEmpty()) {
                            allStringResources.add(name);
                        }
                    }
                } catch (Exception e) {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            "Failed to parse XML: " + xmlFile.getPath() + ", error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "DocumentBuilder error: " + e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "StringResource Annotation processor started");
        if (roundEnv.processingOver()) {
            return false;
        }
        String modulePaths = processingEnv.getOptions().get("moduleResPaths");
        if (modulePaths == null || modulePaths.isEmpty()) {
            return false;
        }
        try {
            String[] paths = modulePaths.split(";");
            for (String path : paths) {
                messager.printMessage(Diagnostic.Kind.WARNING, ">>>>>>>>>>>>>>>> 找到的路径：" + path);
            }
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING, modulePaths);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Permission.class)) {
            Permission annotation = element.getAnnotation(Permission.class);
            String prompt = annotation.prompt();

            if (!allStringResources.contains(prompt)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "String resource '" + prompt + "' not found in any values directory",
                        element);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("moduleResPaths");
    }

}