package org.parachutesmethod.framework.generation.generators.aws;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.parachutesmethod.framework.common.BuildScript;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.models.java.JavaConfiguration;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.parachutesmethod.framework.models.java.parachutedescriptors.ParachuteTypeDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AWSLambdaPackagesGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(AWSLambdaPackagesGenerator.class);

    private Path bundlesDirectory;
    private List<BundleDescriptor> parachuteDescriptors;
    private Map<String, String> resourcePaths = new HashMap<>();

    public AWSLambdaPackagesGenerator(Path bundlesDirectory, List<BundleDescriptor> parachuteDescriptors) {
        this.bundlesDirectory = bundlesDirectory;
        this.parachuteDescriptors = parachuteDescriptors;
    }

    private static void generateJavaLambda(Path parachuteProjectDir, BundleDescriptor descriptor) throws IOException {
        Path parachuteFile = parachuteProjectDir.resolve(StringUtils.capitalize(descriptor.getParachuteName()) + FileExtension.JAVA.extension());
        Files.createFile(parachuteFile);

        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(JavaConfiguration.PARACHUTE_PACKAGE.value());

        cu.addImport(Constants.AWS_IMPORT_CONTEXT_OBJECT);
        cu.addImport(Constants.AWS_IMPORT_LAMBDA_LOGGER);

        ClassOrInterfaceDeclaration containingClass = (ClassOrInterfaceDeclaration) JavaParser.parseTypeDeclaration(descriptor.getParachuteContainingClass());
        MethodDeclaration parachuteMethod = (MethodDeclaration) JavaParser.parseBodyDeclaration(descriptor.getParachuteMethodDeclaration());

        ClassOrInterfaceDeclaration classDeclaration = cu.addClass(StringUtils.capitalize(descriptor.getParachuteName())).setPublic(true);

        // TODO retain annotations if specified in parachute annotations
        parachuteMethod.setAnnotations(new NodeList<>());
        classDeclaration.addMember(parachuteMethod);

        if (!descriptor.getSameClassMethodDependencies().isEmpty()) {
            descriptor.getSameClassMethodDependencies().forEach(m -> {
                classDeclaration.addMember(JavaParser.parseBodyDeclaration(m));
            });
        }

        if (!descriptor.getInnerClasses().isEmpty()) {
            descriptor.getInnerClasses().forEach(innerClass ->
                    classDeclaration.addMember(JavaParser.parseTypeDeclaration(innerClass)));
        }

        Map<String, String> importChanges = new HashMap<>();
        descriptor.getInputTypes().forEach(inputType -> generateTypes(parachuteProjectDir, inputType, importChanges));
        generateTypes(parachuteProjectDir, descriptor.getReturnType(), importChanges);

        descriptor.getImports().forEach(i -> {
            ImportDeclaration im = JavaParser.parseImport(i);
            if (importChanges.containsKey(im.getNameAsString())) {
                cu.addImport(importChanges.get(im.getNameAsString()));
            } else {
                cu.addImport(im);
            }
        });

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parachuteFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write(cu.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void generateTypes(Path parachuteProjectDir, ParachuteTypeDependency t, Map<String, String> importChanges) {
        if (Objects.nonNull(t.getTypeBody()) && !t.getTypeBody().isEmpty()) {
            CompilationUnit inputTypeCU = new CompilationUnit();
            inputTypeCU.setPackageDeclaration(JavaConfiguration.PARACHUTE_PACKAGE.value());
            ClassOrInterfaceDeclaration inputClassDeclaration = (ClassOrInterfaceDeclaration) JavaParser.parseTypeDeclaration(t.getTypeBody());

            inputTypeCU.addType(inputClassDeclaration);
            t.getImports().forEach(i -> inputTypeCU.addImport(JavaParser.parseImport(i)));

            importChanges.put(
                    t.getTypeName(),
                    JavaConfiguration.PARACHUTE_PACKAGE.value().concat(".").concat(inputClassDeclaration.getNameAsString())
            );

            try {
                Path inputTypePath = parachuteProjectDir.resolve(inputClassDeclaration.getNameAsString().concat(FileExtension.JAVA.extension()));
                Files.createFile(inputTypePath);
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputTypePath.toFile()), StandardCharsets.UTF_8))) {
                    writer.write(inputTypeCU.toString());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if (!t.getTypeDependencies().isEmpty()) {
                t.getTypeDependencies().forEach(dep -> generateTypes(parachuteProjectDir, dep, importChanges));
            }
        }
    }

    private static void generateAWSCompliantJavaBuildScript(Path parachuteDir, BundleDescriptor descriptor) throws IOException {
        Path pom = parachuteDir.resolve(BuildScript.MAVEN.value());
        Files.createFile(pom);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pom.toFile()), StandardCharsets.UTF_8))) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new ByteArrayInputStream(descriptor.getBuildScript().getContent().getBytes(StandardCharsets.UTF_8)));
            Dependency aws = new Dependency();
            aws.setGroupId(Constants.AWS_MAVEN_DEP_GROUPID);
            aws.setArtifactId(Constants.AWS_MAVEN_DEP_ARTIFACTID);
            aws.setVersion(Constants.AWS_MAVEN_DEP_VERSION);
            model.addDependency(aws);

            Build build = new Build();

            Plugin compilerPlugin = new Plugin();
            compilerPlugin.setArtifactId(Constants.COMPILER_MAVEN_PLUGIN_ARTIFACTID);
            compilerPlugin.setVersion(Constants.COMPILER_MAVEN_PLUGIN_VERSION);
            Xpp3Dom configuration = new Xpp3Dom("configuration");
            Xpp3Dom sourceConfig = new Xpp3Dom(Constants.COMPILER_MAVEN_PLUGIN_CONF_SOURCE);
            sourceConfig.setValue(Constants.COMPILER_MAVEN_PLUGIN_CONF_VERSION);
            configuration.addChild(sourceConfig);
            Xpp3Dom targetConfig = new Xpp3Dom(Constants.COMPILER_MAVEN_PLUGIN_CONF_TARGET);
            targetConfig.setValue(Constants.COMPILER_MAVEN_PLUGIN_CONF_VERSION);
            configuration.addChild(targetConfig);
            compilerPlugin.setConfiguration(configuration);

            Plugin fatJar = new Plugin();
            fatJar.setArtifactId(Constants.SHADE_MAVEN_PLUGIN_ARTIFACTID);
            fatJar.setVersion(Constants.SHADE_MAVEN_PLUGIN_VERSION);

            Xpp3Dom dom = new Xpp3Dom("configuration");
            Xpp3Dom child = new Xpp3Dom(Constants.SHADE_MAVEN_PLUGIN_CONF);
            child.setValue("false");
            dom.addChild(child);
            fatJar.setConfiguration(dom);

            PluginExecution ex = new PluginExecution();
            ex.setPhase(Constants.SHADE_MAVEN_PLUGIN_EXECUTION_PHASE);
            ex.addGoal(Constants.SHADE_MAVEN_PLUGIN_EXECUTION_GOAL);
            fatJar.setExecutions(Collections.singletonList(ex));

            List<Plugin> plugins = new ArrayList<>();
            plugins.add(fatJar);
            plugins.add(compilerPlugin);

            build.setPlugins(plugins);
            model.setBuild(build);

            new MavenXpp3Writer().write(writer, model);
        } catch (XmlPullParserException e1) {
            e1.printStackTrace();
        }
    }

    private static void runJavaBuildScript(Path parachuteDir) {
        Path pom = parachuteDir.resolve(BuildScript.MAVEN.value());
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pom.toFile());
        request.setGoals(Arrays.asList("clean", "package"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M2_HOME")));
        try {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0) {
                throw new IllegalStateException("Build failed.");
            }
        } catch (MavenInvocationException ex) {
            ex.printStackTrace();
        }
    }

    public void generate() throws IOException {
        Path parachutesDir = bundlesDirectory.resolve(Constants.PARACHUTES_FOLDER);
        Files.createDirectories(bundlesDirectory);
        Files.createDirectories(parachutesDir);

        parachuteDescriptors.forEach(descriptor -> {
            try {
                Path parachuteDir = parachutesDir.resolve(descriptor.getParachuteName().toLowerCase());
                Path parachuteJavaProjectDir = Paths.get(
                        parachuteDir.toString(),
                        JavaConfiguration.JAVA_PROJECT_FILES_PATH.value(),
                        JavaConfiguration.PARACHUTE_PACKAGE.value().replace(".", "/")
                );
                Files.createDirectory(parachuteDir);
                Files.createDirectories(parachuteJavaProjectDir);

                if (Objects.nonNull(descriptor.getProgrammingLanguage())) {
                    if (descriptor.getProgrammingLanguage().equals(SupportedLanguage.JAVA.getName())) {
                        LOGGER.info("Generating and building AWS Lambda for a parachuteName: " + descriptor.getParachuteName());

                        generateJavaLambda(parachuteJavaProjectDir, descriptor);
                        generateAWSCompliantJavaBuildScript(parachuteDir, descriptor);
                        runJavaBuildScript(parachuteDir);

                        resourcePaths.put(descriptor.getParachuteName(), descriptor.getEndpointPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Path getBundlesDirectory() {
        return bundlesDirectory;
    }

    public List<BundleDescriptor> getParachuteDescriptors() {
        return parachuteDescriptors;
    }

    public Map<String, String> getResourcePaths() {
        return resourcePaths;
    }
}