package org.parachutesmethod.framework.models.java.parachutedescriptors;

public enum ParachuteAnnotation {
    ROUTING_BACKUP_ROUTE("backupRoute"),
    ROUTING_OVERPROVISIONING_FACTOR("overProvisioningFactor"),
    CONF_KEEP_PARACHUTE_ANNOTATIONS("retainParachuteAnnotations"),
    CONF_KEEP_OTHER_ANNOTATIONS("retainAnnotations");

    private final String value;

    ParachuteAnnotation(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
