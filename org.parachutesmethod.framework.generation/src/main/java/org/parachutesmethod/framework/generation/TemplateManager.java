package org.parachutesmethod.framework.generation;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.Map;

public enum TemplateManager {
    INSTANCE;

    private Configuration configuration;

    TemplateManager() {
        configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setClassForTemplateLoading(this.getClass(), "/" + Constants.TEMPLATES_FOLDER);

        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);

        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setObjectWrapper(owb.build());
    }

    /**
     * Gets a template based on the specified template type (Constants.TEMPLATES_TYPE_XXX, e.g.,
     * {@link Constants#TEMPLATES_TYPE_ROUTER_CONF}) and template name (Constants.TEMPLATE_[TYPE]_[PLATFORM], e.g.,
     * {@link Constants#TEMPLATE_ROUTER_CONF_AWS}).
     *
     * @param templateType the template type
     * @param templateName the template name
     * @return the freemarker template
     * @throws IOException if the template could not be loaded properly
     */
    public Template getTemplate(String templateType, String templateName) throws IOException {
        return this.configuration.getTemplate(templateType + File.separator + templateName);
    }

    /**
     * Process a given template by merging data into it and return the result as string.
     *
     * @param templateType the template type
     * @param templateName the template name
     * @param templateData the template data as map
     * @return the result data as string
     * @throws Exception an exception occurred while loading or processing the template
     */
    public String processTemplateToString(String templateType, String templateName,
                                          Map<String, Object> templateData) throws Exception {
        String result;

        // Resolve the correct template
        Template temp = getTemplate(templateType, templateName);

        // Merge the data with the template and store the result in a string
        Writer out = new StringWriter();
        temp.process(templateData, out);
        result = out.toString();

        return result;
    }

    /**
     * Process a given template by merging data into it and store the result into a file.
     *
     * @param templateType the template type
     * @param templateName the template name
     * @param templateData the template data as map
     * @param filePath     the path of the file to store the result in
     * @throws Exception an exception occurred while loading or processing the template
     */
    public void processTemplateToFile(String templateType, String templateName, Map<String, Object> templateData,
                                      String filePath) throws Exception {
        // Resolve the correct template
        Template temp = getTemplate(templateType, templateName);

        // Merge the data with the template and write the result in a file
        Writer out = new FileWriter(filePath);
        temp.process(templateData, out);

        out.flush();
        out.close();
    }
}
