package org.parachutesmethod.framework.extraction;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParachuteMethodAnnotationsDescriptor {
    @JsonProperty
    private String rerouteOnDayDirective;
    @JsonProperty
    private String overProvisioningFactorDirective;
    @JsonProperty
    private boolean backupRoute;
    @JsonProperty
    private boolean parachuteAnnotationRetained;
    @JsonProperty
    private boolean otherAnnotationRetained;

    public ParachuteMethodAnnotationsDescriptor(Map<String, String> parameters) {
        if (parameters.containsKey(Constants.CONF_ROUTING_OVERPROVISIONING)) {
            this.overProvisioningFactorDirective = parameters.get(Constants.CONF_ROUTING_OVERPROVISIONING);
        }
        if (parameters.containsKey(Constants.CONF_ROUTING_DATE)) {
            this.rerouteOnDayDirective = parameters.get(Constants.CONF_ROUTING_DATE);
        }
        if (parameters.containsKey(Constants.CONF_ROUTING_BACKUP_ROUTE)) {
            this.backupRoute = Boolean.valueOf(parameters.get(Constants.CONF_ROUTING_BACKUP_ROUTE));
        }
        if (parameters.containsKey(Constants.CONF_KEEP_PARACHUTE_ANNOTATIONS)) {
            this.parachuteAnnotationRetained = Boolean.valueOf(parameters.get(Constants.CONF_KEEP_PARACHUTE_ANNOTATIONS));
        }
        if (parameters.containsKey(Constants.CONF_KEEP_OTHER_ANNOTATIONS)) {
            this.otherAnnotationRetained = Boolean.valueOf(parameters.get(Constants.CONF_KEEP_OTHER_ANNOTATIONS));
        }
    }

    public boolean isParachuteAnnotationRetained() {
        return parachuteAnnotationRetained;
    }

    public boolean isOtherAnnotationRetained() {
        return otherAnnotationRetained;
    }
}
