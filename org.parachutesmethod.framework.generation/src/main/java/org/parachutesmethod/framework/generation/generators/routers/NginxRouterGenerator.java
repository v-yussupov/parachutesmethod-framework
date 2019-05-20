package org.parachutesmethod.framework.generation.generators.routers;

import freemarker.template.TemplateException;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.generation.TemplateManager;
import org.parachutesmethod.framework.models.java.parachutedescriptors.BundleDescriptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NginxRouterGenerator {

    /**
     * Generate a set of Nginx router configuration files one for each parachute provided in the map.
     *
     * @param resultPath  the result path to write the generated Nginx configuration files
     * @param descriptors a list of parachute descriptors
     * @return a map of parachute names and file paths to their corresponding nginx configuration files
     * @throws Exception an exception occurred while generating nginx configuration files
     */
    public static Map<String, String> generateNginxRouterConfigurationFiles(String resultPath, List<BundleDescriptor> descriptors) throws Exception {
        Path targetLocation = Paths.get(resultPath);
        Files.createDirectories(targetLocation);

        Map<String, String> result = new HashMap<>();

        // Create configuration files for parachutes
        Map<String, Object> templateData = new HashMap<>();
        descriptors.forEach(d -> {
            String configFile = targetLocation.resolve(d.getParachuteName() + ".conf").toString();
            templateData.put("parachuteName", d.getParachuteName());
            templateData.put("uri", d.getEndpointPath());

            try {
                TemplateManager.INSTANCE.processTemplateToFile(Constants.TEMPLATES_TYPE_ROUTER_CONF,
                        Constants.TEMPLATE_ROUTER_CONF_AWS, templateData,
                        configFile);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            }

            result.put(d.getParachuteName(), configFile);
            templateData.clear();
        });

        return result;
    }

}
