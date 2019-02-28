package org.parachutesmethod.framework.models.java.projectmodel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.google.common.base.Strings;
import org.parachutesmethod.framework.models.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaProjectFile {
    private static Logger LOGGER = LoggerFactory.getLogger(JavaProjectFile.class);

    private Path filePath;
    private CompilationUnit parsedFile;
    private String packageName;
    private String primaryClassOrInterfaceName;
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

        primaryClassOrInterfaceName = filePath.getFileName().toString().replace(Constants.EXTENSION_JAVA, "");
    }

    public void processJavaClassesAndInterfaces(List<ClassOrInterfaceDeclaration> classDeclarations) {
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

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return filePath.getFileName().toString();
    }

    public CompilationUnit getParsedFile() {
        return parsedFile;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPrimaryClassOrInterfaceName() {
        return primaryClassOrInterfaceName;
    }

    public List<JavaMethod> getFileMethods() {
        List<JavaMethod> result = new ArrayList<>();
        classes.forEach(c -> result.addAll(c.getMethods()));
        return result;
    }

    public List<JavaClass> getClasses() {
        return classes;
    }

    public void setClasses(List<JavaClass> classes) {
        this.classes = classes;
    }

    public List<JavaInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<JavaInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Optional<JavaClass> getClassByName(String name) {
        return classes.stream().filter(c -> c.getName().equals(name)).findFirst();
    }

    public List<JavaImport> getImports() {
        return imports;
    }

    public void setImports(List<JavaImport> imports) {
        this.imports = imports;
    }

    public boolean isWithParachutes() {
        return withParachutes;
    }

    public void setWithParachutes(boolean withParachutes) {
        this.withParachutes = withParachutes;
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