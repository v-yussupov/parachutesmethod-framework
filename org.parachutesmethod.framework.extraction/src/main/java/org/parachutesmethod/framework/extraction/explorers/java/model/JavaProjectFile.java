package org.parachutesmethod.framework.extraction.explorers.java.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.google.common.base.Strings;
import org.parachutesmethod.framework.extraction.Constants;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.ClassOrInterfaceDeclarationCollector;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.ImportDeclarationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaProjectFile {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaProjectFile.class);

    private Path filePath;
    private CompilationUnit parsedFile;
    private String packageName;
    private String classOrInterfaceName;
    private boolean primaryTypeInterface;
    private List<JavaImport> imports = new ArrayList<>();
    private List<JavaClass> classes = new ArrayList<>();
    private List<JavaInterface> interfaces = new ArrayList<>();
    private boolean withParachutes;

    public JavaProjectFile(Path filePath, CompilationUnit parsedFile) {
        this.filePath = filePath;
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
        findJavaImports();
        findJavaClassesAndInterfaces();
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

    private void findJavaClassesAndInterfaces() {
        List<ClassOrInterfaceDeclaration> classDeclarations = new ArrayList<>();
        VoidVisitor<List<ClassOrInterfaceDeclaration>> classDeclarationCollector = new ClassOrInterfaceDeclarationCollector();
        classDeclarationCollector.visit(parsedFile, classDeclarations);
        if (!classDeclarations.isEmpty()) {
            classDeclarations.forEach(cd -> {
                if (!cd.isInterface()) {
                    JavaClass javaClass = new JavaClass(this, cd);
                    withParachutes |= javaClass.isWithParachutes();

                    List<JavaClass> innerClasses = new ArrayList<>();
                    for (Node n : cd.getChildNodes()) {
                        if (n instanceof ClassOrInterfaceDeclaration) {
                            innerClasses.add(new JavaClass(this, (ClassOrInterfaceDeclaration) n));
                        }
                    }
                    javaClass.setInnerClasses(innerClasses);
                    classes.add(javaClass);
                } else {
                    interfaces.add(new JavaInterface(this, cd));
                }
            });
        }
    }

    Path getFilePath() {
        return filePath;
    }

    String getFileName() {
        return filePath.getFileName().toString();
    }

    public CompilationUnit getParsedFile() {
        return parsedFile;
    }

    String getPackageName() {
        return packageName;
    }

    public String getClassOrInterfaceName() {
        return classOrInterfaceName;
    }

    public boolean isPrimaryTypeInterface() {
        return primaryTypeInterface;
    }

    public List<JavaMethod> getFileMethods() {
        List<JavaMethod> result = new ArrayList<>();
        classes.forEach(c -> result.addAll(c.getMethods()));
        return result;
    }

    public List<JavaClass> getClasses() {
        return classes;
    }

    public List<JavaInterface> getInterfaces() {
        return interfaces;
    }

    public Optional<JavaClass> getClassByName(String name) {
        return classes.stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    public List<JavaImport> getImports() {
        return imports;
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.repeat("=", filePath.toString().length() + 10));
        sb.append(System.lineSeparator());
        sb.append(String.format("Filename: %s,\nPath: %s,\nPackage: %s,\n", filePath.getFileName().toString(), filePath, packageName));
        sb.append(String.format("Classes_Count: %d\n", classes.size()));
        sb.append(String.format("ImportStatements_Count: %d\n", imports.size()));
        sb.append(Strings.repeat("=", filePath.toString().length() + 10));
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}