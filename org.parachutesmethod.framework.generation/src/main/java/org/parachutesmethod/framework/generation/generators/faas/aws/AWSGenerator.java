package org.parachutesmethod.framework.generation.generators.faas.aws;

import freemarker.template.TemplateException;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWSGenerator {

    public static void generate(Path path, List<BundleDescriptor> parachuteDescriptors) throws IOException {
        Path bundlesDir = path.getParent().resolve(Constants.DEPLOYMENT_BUNDLES_FOLDER);
        Files.createDirectories(bundlesDir);
        Map<String, String> resourcePaths = new HashMap<>();

        parachuteDescriptors.forEach(descriptor -> {
            resourcePaths.put(descriptor.getParachuteName(), descriptor.getEndpointPath());
        });

        /*parachuteProjectExplorers.forEach(e -> {
            try {
                String parachuteName = e.getProjectPath().getFileName().toString();
                Path parachuteDir = bundlesDir.resolve(parachuteName);
                Path parachuteProjectDir = Paths.get(parachuteDir.toString(), "/src/main/java/org/parachutesmethod/extractedparachutes");
                Files.createDirectory(parachuteDir);
                Files.createDirectories(parachuteProjectDir);

                // Initial AWS-compliant lambda
                Path parachuteFile = parachuteProjectDir.resolve(parachuteName + ".java");
                Files.createFile(parachuteFile);
                e.getProjectFiles().forEach(f -> {
                    if (parachuteName.equals(f.getFileName().replace(".java", ""))) {

                        f.getFileMethods().forEach(javaMethod -> {
                            if (javaMethod.getName().equalsIgnoreCase(parachuteName)) {
                                resourcePaths.put(parachuteName, javaMethod.getResourcePath());
                            }
                        });

                        CompilationUnit cu = f.getParsedFile();
                        cu.addImport(Constants.AWS_IMPORT_CONTEXT_OBJECT);
                        cu.addImport(Constants.AWS_IMPORT_LAMBDA_LOGGER);

                        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parachuteFile.toFile()), StandardCharsets.UTF_8))) {
                            writer.write(cu.toString());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Path additional = parachuteProjectDir.resolve(f.getFileName());
                        try {
                            Files.createFile(additional);
                            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(additional.toFile()), StandardCharsets.UTF_8))) {
                                writer.write(f.getParsedFile().toString());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                // Generate AWS-compliant pom
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
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
*/
        Map<String, String> routerConfigurations = generateRouterConfiguration(bundlesDir, resourcePaths);

        Path cloudFormationTemplatesDir = bundlesDir.resolve("deployment-models");
        Files.createDirectory(cloudFormationTemplatesDir);
        try {
            CloudFormationGenerator.generateCloudFormationTemplate(cloudFormationTemplatesDir.toString(), routerConfigurations);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
