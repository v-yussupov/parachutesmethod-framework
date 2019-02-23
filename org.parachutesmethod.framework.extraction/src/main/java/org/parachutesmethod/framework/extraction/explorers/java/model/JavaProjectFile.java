package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.google.common.base.Strings;
import org.parachutesmethod.framework.extraction.Constants;
import org.parachutesmethod.framework.extraction.explorers.java.visitors.MethodDeclarationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
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
    private List<JavaMethod> methods;

    private CompilationUnit parsedFile;

    JavaProjectFile(Path filePath) {
        this.filePath = filePath;
        this.fileName = filePath.getFileName().toString();
        this.methods = new ArrayList<>();

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setAttributeComments(false)
                .setDoNotAssignCommentsPrecedingEmptyLines(true);
        try (FileInputStream in = new FileInputStream(this.filePath.toString())) {
            LOGGER.info(String.format("Parsing project file %s", this.filePath.getFileName().toString()));
            JavaParser.setStaticConfiguration(parserConfiguration);
            parsedFile = JavaParser.parse(in);
            if (parsedFile.getPackageDeclaration().isPresent()) {
                packageName = parsedFile.getPackageDeclaration().get().getNameAsString();
            } else {
                packageName = Constants.FILE_WITHOUT_PACKAGE;
            }
            classOrInterfaceName = parsedFile.getPrimaryTypeName().toString();

            collectJavaMethods();


        } catch (IOException e) {
            e.printStackTrace();
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

    private void collectJavaMethods() {
        List<MethodDeclaration> methodDeclarations = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodDeclarationCollector = new MethodDeclarationCollector();
        methodDeclarationCollector.visit(parsedFile, methodDeclarations);
        if (!methodDeclarations.isEmpty()) {
            methodDeclarations.forEach(md -> methods.add(new JavaMethod(this, md)));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.repeat("=", filePath.toString().length()));
        sb.append(System.lineSeparator());
        sb.append(String.format("Filename: %s,\nPath: %s,\nPackage: %s,\nMethods_Count: %d,\n", fileName, filePath, packageName, methods.size()));
        sb.append(Strings.repeat("=", filePath.toString().length()));
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}