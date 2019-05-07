package org.parachutesmethod.framework.generation;

public class Constants {
    public static final String DEPLOYMENT_BUNDLES_FOLDER = "deployment-bundles";
    public static final String DEPLOYMENT_MODELS_FOLDER = "deployment-models";
    public static final String ROUTER_CONFIGURATIONS_FOLDER = "router-configurations";
    public static final String PARACHUTES_FOLDER = "parachutes";

    public static final String AWS_MAVEN_DEP_GROUPID = "com.amazonaws";
    public static final String AWS_MAVEN_DEP_ARTIFACTID = "aws-lambda-java-core";
    public static final String AWS_MAVEN_DEP_VERSION = "1.1.0";

    public static final String AWS_IMPORT_CONTEXT_OBJECT = "com.amazonaws.services.lambda.runtime.Context";
    public static final String AWS_IMPORT_LAMBDA_LOGGER = "com.amazonaws.services.lambda.runtime.LambdaLogger";

    public static final String COMPILER_MAVEN_PLUGIN_ARTIFACTID = "maven-compiler-plugin";
    public static final String COMPILER_MAVEN_PLUGIN_VERSION = "3.8.0";
    public static final String COMPILER_MAVEN_PLUGIN_CONF_SOURCE = "source";
    public static final String COMPILER_MAVEN_PLUGIN_CONF_TARGET = "target";
    public static final String COMPILER_MAVEN_PLUGIN_CONF_VERSION = "1.8";

    public static final String SHADE_MAVEN_PLUGIN_ARTIFACTID = "maven-shade-plugin";
    public static final String SHADE_MAVEN_PLUGIN_VERSION = "2.3";
    public static final String SHADE_MAVEN_PLUGIN_CONF = "createDependencyReducedPom";
    public static final String SHADE_MAVEN_PLUGIN_EXECUTION_PHASE = "package";
    public static final String SHADE_MAVEN_PLUGIN_EXECUTION_GOAL = "shade";

    public static final String TEMPLATES_FOLDER = "templates";

    public static final String TEMPLATES_TYPE_ROUTER_CONF = "routerConf";
    public static final String TEMPLATE_ROUTER_CONF_AWS = "aws-nginx.ftl";

    public static final String NGINX_ROUTER_DEFAULT_CONF = "default";

    public static final String TEMPLATES_TYPE_DEPLOY_MODEL = "deploymentModel";
    public static final String TEMPLATE_DEPLOY_MODEL_AWS = "aws-cloud-formation.ftl";

    public static final String AWS_CLOUD_FORMATION_TEMPLATE_FILE = "aws-cf-template.json";
}
