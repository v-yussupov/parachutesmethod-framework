package org.parachutesmethod.framework.generation;

import com.github.javaparser.ast.CompilationUnit;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.parachutesmethod.framework.extraction.explorers.java.JavaParachuteProjectExplorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
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
import java.util.List;
import java.util.stream.Collectors;

public class ParachuteGenerator {
    private static Logger LOGGER = LoggerFactory.getLogger(ParachuteGenerator.class);

    private Path path;
    private List<JavaParachuteProjectExplorer> parachuteProjectExplorers = new ArrayList<>();

    public ParachuteGenerator(String path) {
        this.path = Paths.get(path);
    }

    public Path getPath() {
        return path;
    }

    public void parseParachuteProject() throws IOException {
        List<Path> parachuteProjectFolders = Files.list(path)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

        parachuteProjectFolders.forEach(parachuteProjectFolder -> {
            try {
                LOGGER.info("Parsing parachute project name: " + parachuteProjectFolder.getFileName().toString());
                JavaParachuteProjectExplorer explorer = new JavaParachuteProjectExplorer(parachuteProjectFolder);
                parachuteProjectExplorers.add(explorer);

                String mainClass = parachuteProjectFolder.getFileName().toString();
                explorer.getProjectClassByName(mainClass);
                LOGGER.info("parsedPrimaryFile:" + explorer.getProjectClassByName(mainClass).getClassDeclaration());

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void generateParachuteBundles() throws IOException {
        Path bundlesDir = path.getParent().resolve(Constants.DEPLOYMENT_BUNDLES_FOLDER);
        Files.createDirectories(bundlesDir);
        parachuteProjectExplorers.forEach(e -> {
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
    }

    public List<JavaParachuteProjectExplorer> getParachuteProjectExplorers() {
        return parachuteProjectExplorers;
    }
}