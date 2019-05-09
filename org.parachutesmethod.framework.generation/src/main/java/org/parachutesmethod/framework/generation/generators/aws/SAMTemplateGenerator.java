package org.parachutesmethod.framework.generation.generators.aws;

import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.parachutesmethod.framework.common.FileExtension;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.generation.TemplateManager;
import org.parachutesmethod.framework.models.aws.SAMFunction;
import org.parachutesmethod.framework.models.java.JavaConfiguration;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAMTemplateGenerator {
    /**
     * Generate a set of Nginx router configuration files one for each parachute provided in the map.
     *
     * @param targetLocation the temporary path to write the generated SAM template
     * @param descriptors    a list of parachute descriptors
     * @return the target file path of the generated AWS SAM template file
     * @throws Exception an exception occurred while generating SAM template
     */
    public static String generate(String tempProjectName, List<BundleDescriptor> descriptors, Path targetLocation) throws IOException, TemplateException {
        Files.createDirectories(targetLocation);

        List<SAMFunction> functions = new ArrayList<>();
        Map<String, Object> templateData = new HashMap<>();
        descriptors.forEach(d -> {
            functions.add(new SAMFunction(
                    StringUtils.capitalize(d.getParachuteName()).concat("Function"),
                    tempProjectName,
                    d.getBuildScript().getArtifactName(),
                    JavaConfiguration.PARACHUTE_PACKAGE.value(),
                    StringUtils.capitalize(d.getParachuteName()),
                    d.getParachuteName(),
                    "java8",
                    d.getEndpointPath()
            ));
        });
        templateData.put("functions", functions);

        // Create Cloud Formation template for parachutes
        String template = targetLocation.resolve(Constants.AWS_SAM_TEMPLATE.concat(FileExtension.YAML.extension())).toString();

        TemplateManager.INSTANCE.processTemplateToFile(
                Constants.TEMPLATES_TYPE_DEPLOY_MODEL,
                Constants.TEMPLATE_AWS_SAM,
                templateData,
                template
        );

        return template;
    }

}
