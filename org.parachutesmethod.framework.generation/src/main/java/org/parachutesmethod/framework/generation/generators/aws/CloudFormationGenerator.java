package org.parachutesmethod.framework.generation.generators.aws;

import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.generation.TemplateManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CloudFormationGenerator {

    /**
     * Generates a AWS Cloud Formation template with inline nginx router configuration for the parachutes provided in
     * the map.
     *
     * @param resultPath        the result path to write the generated Nginx configuration files
     * @param routerConfigFiles a map of parachute names and file paths to their corresponding nginx
     *                          router configuration files
     * @return the target file path of the generated AWS Cloud Formation template file
     * @throws Exception an exception occurred while generating a AWS Cloud Formation template
     */
    public static String generateCloudFormationTemplate(String resultPath,
                                                        Map<String, String> routerConfigFiles) throws Exception {
        Path targetLocation = Paths.get(resultPath);
        Files.createDirectories(targetLocation);

        // Prepare template data
        Map<String, Object> configs = new HashMap<>();
        for (String parachuteName : routerConfigFiles.keySet()) {
            // Extract configuration from file as escaped string
            String routerConfig = parseFileAsEscapedString(routerConfigFiles.get(parachuteName));

            configs.put(parachuteName, routerConfig);
        }

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("configs", configs);

        // Create Cloud Formation template for parachutes
        String template =
                targetLocation.resolve(Constants.AWS_CLOUD_FORMATION_TEMPLATE_FILE).toString();

        TemplateManager.INSTANCE.processTemplateToFile(Constants.TEMPLATES_TYPE_DEPLOY_MODEL,
                Constants.TEMPLATE_DEPLOY_MODEL_AWS, templateData, template);

        configs.clear();
        templateData.clear();

        return template;
    }

    private static String parseFileAsEscapedString(String filePath) throws Exception {
        StringBuilder builder = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = br.readLine()) != null) {
            // Wrap the line with quotes, add an escaped line break character and a comma to separate the lines in
            // the string array within the Cloud Formation Template
            builder.append("\"");
            builder.append(line);
            builder.append("\\n");
            builder.append("\"");
            builder.append(",");
            // Add a non-escaped line break to preserve the layout
            builder.append("\n");
        }

        // Remove the last comma separator
        builder.deleteCharAt(builder.lastIndexOf(","));

        br.close();

        return builder.toString();
    }

}
