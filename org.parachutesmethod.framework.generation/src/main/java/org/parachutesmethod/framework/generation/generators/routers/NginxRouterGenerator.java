package org.parachutesmethod.framework.generation.generators.routers;

import freemarker.template.TemplateException;
import org.parachutesmethod.framework.generation.Constants;
import org.parachutesmethod.framework.generation.TemplateManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NginxRouterGenerator {

    /**
     * Generate a set of Nginx router configuration files one for each parachute provided in the map.
     *
     * @param resultPath    the result path to write the generated Nginx configuration files
     * @param parachuteURIs a map of parachutes and their relative URIs
     * @return a map of parachute names and file paths to their corresponding nginx configuration files
     * @throws Exception an exception occurred while generating nginx configuration files
     */
    public static Map<String, String> generateNginxRouterConfigurationFiles(String resultPath,
    Map<String, String> parachuteURIs) throws IOException, TemplateException {
        Path targetLocation = Paths.get(resultPath);
        Files.createDirectories(targetLocation);

        Map<String, String> result = new HashMap<>();

        // Create configuration files for parachutes
        Map<String, Object> templateData = new HashMap<>();
        for (String parachuteName : parachuteURIs.keySet()) {
            String configFile = targetLocation.resolve(parachuteName + ".conf").toString();

            templateData.put("parachuteName", parachuteName);
            templateData.put("uri", parachuteURIs.get(parachuteName));

            TemplateManager.INSTANCE.processTemplateToFile(Constants.TEMPLATES_TYPE_ROUTER_CONF,
                    Constants.TEMPLATE_ROUTER_CONF_AWS, templateData,
                    configFile);

            result.put(parachuteName, configFile);
            templateData.clear();
        }

        // Create an additional configuration file for the original application
        String defaultConfigFile = targetLocation.resolve(Constants.NGINX_ROUTER_DEFAULT_CONF + ".conf").toString();

        templateData.put("parachuteName", Constants.NGINX_ROUTER_DEFAULT_CONF);
        templateData.put("uri", "/");
        templateData.put("default", true);

        TemplateManager.INSTANCE.processTemplateToFile(Constants.TEMPLATES_TYPE_ROUTER_CONF,
                Constants.TEMPLATE_ROUTER_CONF_AWS, templateData, defaultConfigFile);
        result.put(Constants.NGINX_ROUTER_DEFAULT_CONF, defaultConfigFile);

        return result;
    }

}
