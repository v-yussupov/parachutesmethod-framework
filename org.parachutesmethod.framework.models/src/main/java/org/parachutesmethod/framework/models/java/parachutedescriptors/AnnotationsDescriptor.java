package org.parachutesmethod.framework.models.java.parachutedescriptors;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class AnnotationsDescriptor {
    @JsonProperty
    private String overProvisioningFactorDirective;
    @JsonProperty
    private boolean backupRoute;
    @JsonProperty
    private boolean parachuteAnnotationRetained;
    @JsonProperty
    private boolean otherAnnotationRetained;

    public AnnotationsDescriptor(Map<String, String> parameters) {
        if (parameters.containsKey(ParachuteAnnotation.ROUTING_OVERPROVISIONING_FACTOR.value())) {
            this.overProvisioningFactorDirective = parameters.get(ParachuteAnnotation.ROUTING_OVERPROVISIONING_FACTOR.value());
        }
        if (parameters.containsKey(ParachuteAnnotation.ROUTING_BACKUP_ROUTE.value())) {
            this.backupRoute = Boolean.valueOf(parameters.get(ParachuteAnnotation.ROUTING_BACKUP_ROUTE.value()));
        }
        if (parameters.containsKey(ParachuteAnnotation.CONF_KEEP_PARACHUTE_ANNOTATIONS.value())) {
            this.parachuteAnnotationRetained = Boolean.valueOf(parameters.get(ParachuteAnnotation.CONF_KEEP_PARACHUTE_ANNOTATIONS.value()));
        }
        if (parameters.containsKey(ParachuteAnnotation.CONF_KEEP_OTHER_ANNOTATIONS.value())) {
            this.otherAnnotationRetained = Boolean.valueOf(parameters.get(ParachuteAnnotation.CONF_KEEP_OTHER_ANNOTATIONS.value()));
        }
    }

    public boolean isParachuteAnnotationRetained() {
        return parachuteAnnotationRetained;
    }

    public boolean isOtherAnnotationRetained() {
        return otherAnnotationRetained;
    }
}
