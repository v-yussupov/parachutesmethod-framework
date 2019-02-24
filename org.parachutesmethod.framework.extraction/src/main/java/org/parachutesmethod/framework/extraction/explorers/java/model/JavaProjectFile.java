package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.google.common.base.Strings;
import org.parachutesmethod.framework.extraction.Constants;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.ClassOrInterfaceDeclarationCollector;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.ImportDeclarationCollector;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.MethodDeclarationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaProjectFile {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaProjectFile.class);

    private Path filePath;
    private String fileName;
    private CompilationUnit parsedFile;
    private String packageName;
    private String classOrInterfaceName;
    private boolean primaryTypeInterface;
    private boolean withParachutes;
    private List<JavaMethod> methods = new ArrayList<>();
    private List<JavaClass> classes = new ArrayList<>();
    private List<JavaImport> imports = new ArrayList<>();


    public JavaProjectFile(Path filePath, CompilationUnit parsedFile) {
        this.filePath = filePath;
        this.fileName = filePath.getFileName().toString();
        this.parsedFile = parsedFile;

        if (parsedFile.getPackageDeclaration().isPresent()) {
            packageName = parsedFile.getPackageDeclaration().get().getNameAsString();
        } else {
            packageName = Constants.FILE_WITHOUT_PACKAGE;
        }

        if (parsedFile.getPrimaryType().isPresent()) {
            classOrInterfaceName = parsedFile.getPrimaryTypeName().toString();
            primaryTypeInterface = ((ClassOrInterfaceDeclaration) parsedFile.getPrimaryType().get()).isInterface();
        }
        findJavaMethods();
        findJavaClasses();
        findJavaImports();
    }

    private void findJavaMethods() {
        List<MethodDeclaration> methodDeclarations = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodDeclarationCollector = new MethodDeclarationCollector();
        methodDeclarationCollector.visit(parsedFile, methodDeclarations);
        if (!methodDeclarations.isEmpty()) {
            methodDeclarations.forEach(md -> {
                JavaMethod method = new JavaMethod(this, md);
                withParachutes |= method.isParachuteMethod();
                methods.add(method);
            });
        }
    }

    private void findJavaClasses() {
        List<ClassOrInterfaceDeclaration> classDeclarations = new ArrayList<>();
        VoidVisitor<List<ClassOrInterfaceDeclaration>> classDeclarationCollector = new ClassOrInterfaceDeclarationCollector();
        classDeclarationCollector.visit(parsedFile, classDeclarations);
        if (!classDeclarations.isEmpty()) {
            classDeclarations.forEach(cd -> {
                if (!cd.isInterface()) {
                    JavaClass javaClass = new JavaClass(cd, packageName);

                    List<JavaClass> innerClasses = new ArrayList<>();
                    for (Node n : cd.getChildNodes()) {
                        if (n instanceof ClassOrInterfaceDeclaration) {
                            innerClasses.add(new JavaClass((ClassOrInterfaceDeclaration) n, packageName));
                        }
                    }
                    javaClass.setDirectInnerClasses(innerClasses);
                    classes.add(javaClass);
                }
            });
        }
    }

    private void findJavaImports() {
        List<ImportDeclaration> importDeclarations = new ArrayList<>();
        VoidVisitor<List<ImportDeclaration>> importDeclarationCollector = new ImportDeclarationCollector();
        importDeclarationCollector.visit(parsedFile, importDeclarations);
        if (!importDeclarations.isEmpty()) {
            importDeclarations.forEach(
                    importDeclaration -> imports.add(new JavaImport(importDeclaration))
            );
        }
    }

    Path getFilePath() {
        return filePath;
    }

    String getFileName() {
        return fileName;
    }

    public CompilationUnit getParsedFile() {
        return parsedFile;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassOrInterfaceName() {
        return classOrInterfaceName;
    }

    public boolean isPrimaryTypeInterface() {
        return primaryTypeInterface;
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    public List<JavaClass> getClasses() {
        return classes;
    }

    public List<JavaImport> getImports() {
        return imports;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.repeat("=", filePath.toString().length() + 10));
        sb.append(System.lineSeparator());
        sb.append(String.format("Filename: %s,\nPath: %s,\nPackage: %s,\nHasParachutes: %s\n", fileName, filePath, packageName, withParachutes));
        sb.append(String.format("Methods_Count: %d\n", methods.size()));
        sb.append(String.format("Classes_Count: %d\n", classes.size()));
        sb.append(String.format("ImportStatements_Count: %d\n", imports.size()));
        sb.append(Strings.repeat("=", filePath.toString().length() + 10));
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }
}