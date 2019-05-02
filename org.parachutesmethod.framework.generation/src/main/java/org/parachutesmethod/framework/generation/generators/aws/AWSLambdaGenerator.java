package org.parachutesmethod.framework.generation.generators.aws;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import freemarker.template.TemplateException;
import org.parachutesmethod.framework.extraction.languages.SupportedLanguage;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.generation.generators.routers.NginxRouterGenerator;
import org.parachutesmethod.framework.models.java.JavaConfiguration;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AWSLambdaGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(AWSLambdaGenerator.class);

    public static void generate(Path path, List<BundleDescriptor> parachuteDescriptors) throws IOException {
        Path bundlesDir = path.getParent().resolve(Constants.DEPLOYMENT_BUNDLES_FOLDER);
        Files.createDirectories(bundlesDir);
        Map<String, String> resourcePaths = new HashMap<>();

        parachuteDescriptors.forEach(descriptor -> {
            try {
                Path parachuteDir = bundlesDir.resolve(descriptor.getParachuteName().toLowerCase());
                Path parachuteProjectDir = Paths.get(parachuteDir.toString(), "/src/main/java/org/parachutesmethod/extractedparachutes");
                Files.createDirectory(parachuteDir);
                Files.createDirectories(parachuteProjectDir);

                if (Objects.nonNull(descriptor.getProgrammingLanguage())) {
                    if (descriptor.getProgrammingLanguage().equals(SupportedLanguage.JAVA.getName())) {
                        LOGGER.info("Generating and building AWS Lambda for a parachuteName: " + descriptor.getParachuteName());

                        generateJavaLambda(parachuteProjectDir, descriptor);
                        generateAWSCompliantJavaBuildScript(descriptor);
                        runJavaBuildScript();

                        resourcePaths.put(descriptor.getParachuteName(), descriptor.getEndpointPath());

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Map<String, String> routerConfigurations = generateRouterConfiguration(bundlesDir, resourcePaths);

        Path cloudFormationTemplatesDir = bundlesDir.resolve("deployment-models");
        Files.createDirectory(cloudFormationTemplatesDir);
        try {
            CloudFormationGenerator.generateCloudFormationTemplate(cloudFormationTemplatesDir.toString(), routerConfigurations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateJavaLambda(Path parachuteProjectDir, BundleDescriptor descriptor) throws IOException {
        Path parachuteFile = parachuteProjectDir.resolve(descriptor.getParachuteName() + ".java");
        Files.createFile(parachuteFile);

        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(JavaConfiguration.EXTRACTED_PARACHUTE_PACKAGE_NAME.value());

        cu.addImport(Constants.AWS_IMPORT_CONTEXT_OBJECT);
        cu.addImport(Constants.AWS_IMPORT_LAMBDA_LOGGER);
        descriptor.getImports().forEach(i -> cu.addImport(JavaParser.parseImport(i)));
        ClassOrInterfaceDeclaration parachuteClass = cu.addClass(descriptor.getParachuteName()).setPublic(true);

        ClassOrInterfaceDeclaration containingClass = (ClassOrInterfaceDeclaration) JavaParser.parseTypeDeclaration(descriptor.getParachuteContainingClass());

        //containingClass.getMethods().stream().filter(methodDeclaration -> methodDeclaration.getN)

        //parachuteClass.getMethods().add();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parachuteFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write(cu.toString());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //e.getProjectFiles().forEach(f -> {
        // dependency classes
            /*else {
                Path additional = parachuteProjectDir.resolve(f.getFileName());
                try {
                    Files.createFile(additional);
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(additional.toFile()), StandardCharsets.UTF_8))) {
                        writer.write(f.getParsedFile().toString());
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }*/
        //});

    }

    private static void generateAWSCompliantJavaBuildScript(BundleDescriptor descriptor) {
        /*// Generate AWS-compliant pom
                Path pom = parachuteDir.resolve("pom.xml");
                Files.createFile(pom);
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pom.toFile()), StandardCharsets.UTF_8))) {
                    Model model = e.getPomFiles().get(0).getPom();
                    Dependency aws = new Dependency();
                    aws.setGroupId(Constants.AWS_MAVEN_DEP_GROUPID);
                    aws.setArtifactId(Constants.AWS_MAVEN_DEP_ARTIFACTID);
                    aws.setVersion(Constants.AWS_MAVEN_DEP_VERSION);
                    model.addDependency(aws);

                    Build build = new Build();
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

                    build.setPlugins(Collections.singletonList(fatJar));
                    model.setBuild(build);

                    new MavenXpp3Writer().write(writer, model);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                */
    }

    private static void runJavaBuildScript() {
        /*
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
                */
    }

    private static Map<String, String> generateRouterConfiguration(Path bundlesDir, Map<String, String> parachuteURIs) {
        Map<String, String> routerConfigurations = new HashMap<>();
        Path parachuteDir = bundlesDir.resolve("router-configurations");
        try {
            Files.createDirectory(parachuteDir);
            routerConfigurations = NginxRouterGenerator.generateNginxRouterConfigurationFiles(parachuteDir.toString(), parachuteURIs);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return routerConfigurations;
    }

    private void draftMethod() {
        /*parachuteProjectDirectories.forEach(parachuteProjectFolder -> {
                        try {
                            LOGGER.info("Parsing parachute project name: " + parachuteProjectFolder.getFileName().toString());
                            JavaProjectExplorer explorer = new JavaProjectExplorer(parachuteProjectFolder);
                            parachuteProjectExplorers.add(explorer);

                            String mainClass = parachuteProjectFolder.getFileName().toString();
                            explorer.getProjectClassByName(mainClass);
                            LOGGER.info("parsedPrimaryFile:" + explorer.getProjectClassByName(mainClass).getClassDeclaration());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });*/
    }
}