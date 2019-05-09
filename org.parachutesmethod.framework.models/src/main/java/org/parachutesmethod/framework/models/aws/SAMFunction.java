package org.parachutesmethod.framework.models.aws;

public class SAMFunction {
    private String bucketName;
    private String objectKey;
    private String name;
    private String codeuri;
    private String packageName;
    private String className;
    private String handler;
    private String runtime;
    private String endpoint;

    public SAMFunction() {
    }

    public SAMFunction(String name, String bucketName, String objectKey, String packageName, String className, String handler, String runtime, String endpoint) {
        this.name = name;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.packageName = packageName;
        this.className = className;
        this.handler = handler;
        this.runtime = runtime;
        this.endpoint = endpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeuri() {
        return codeuri;
    }

    public void setCodeuri(String codeuri) {
        this.codeuri = codeuri;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
