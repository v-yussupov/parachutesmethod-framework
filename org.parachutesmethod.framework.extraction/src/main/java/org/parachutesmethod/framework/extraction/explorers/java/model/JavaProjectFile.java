package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.google.common.base.Strings;
import org.parachutesmethod.framework.extraction.Constants;
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
    private String packageName;
    private String classOrInterfaceName;
    private boolean isInterface;
    private List<JavaMethod> methods = new ArrayList<>();
    private boolean withParachutes;
    private CompilationUnit parsedFile;

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
            isInterface = ((ClassOrInterfaceDeclaration) parsedFile.getPrimaryType().get()).isInterface();
        }
        findJavaMethods();
    }

    private void findJavaMethods() {
        List<MethodDeclaration> methodDeclarations = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodDeclarationCollector = new MethodDeclarationCollector();
        methodDeclarationCollector.visit(parsedFile, methodDeclarations);
        if (!methodDeclarations.isEmpty()) {
            methodDeclarations.forEach(md -> {
                JavaMethod method = new JavaMethod(this, md);
                withParachutes |= method.isParachuteMethod();
                methods.add(new JavaMethod(this, md));
            });
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
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

    public boolean isInterface() {
        return isInterface;
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.repeat("=", filePath.toString().length()));
        sb.append(System.lineSeparator());
        sb.append(String.format("Filename: %s,\nPath: %s,\nPackage: %s,\nMethods_Count: %d,\nHasParachutes: %s\n", fileName, filePath, packageName, methods.size(), withParachutes));
        sb.append(Strings.repeat("=", filePath.toString().length()));
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }
}