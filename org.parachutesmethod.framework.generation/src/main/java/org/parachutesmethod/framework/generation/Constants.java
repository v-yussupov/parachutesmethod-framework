package org.parachutesmethod.framework.generation;

public class Constants {
    public static final String DEPLOYMENT_BUNDLES_FOLDER = "parachute-deployment-bundles";


    public static final String AWS_MAVEN_DEP_GROUPID = "com.amazonaws";
    public static final String AWS_MAVEN_DEP_ARTIFACTID = "aws-lambda-java-core";
    public static final String AWS_MAVEN_DEP_VERSION = "1.1.0";

    public static final String AWS_IMPORT_CONTEXT_OBJECT = "com.amazonaws.services.lambda.runtime.Context";
    public static final String AWS_IMPORT_LAMBDA_LOGGER = "com.amazonaws.services.lambda.runtime.LambdaLogger";

    public static final String SHADE_MAVEN_PLUGIN_ARTIFACTID = "maven-shade-plugin";
    public static final String SHADE_MAVEN_PLUGIN_VERSION = "2.3";
    public static final String SHADE_MAVEN_PLUGIN_CONF = "createDependencyReducedPom";
    public static final String SHADE_MAVEN_PLUGIN_EXECUTION_PHASE = "package";
    public static final String SHADE_MAVEN_PLUGIN_EXECUTION_GOAL = "shade";

}
