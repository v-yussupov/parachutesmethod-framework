package org.parachutesmethod.framework.extraction;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParachuteMethodAnnotationDescriptor {
    @JsonProperty
    private String rerouteOnDay;
    @JsonProperty
    private String overProvisioningFactor;
    @JsonProperty
    private boolean retainParachuteAnnotations;
    @JsonProperty
    private boolean retainAnnotations;

    public ParachuteMethodAnnotationDescriptor(Map<String, String> parameters) {
        if (parameters.containsKey(Constants.CONF_ROUTING_OVERPROVISIONING)) {
            this.overProvisioningFactor = parameters.get(Constants.CONF_ROUTING_OVERPROVISIONING);
        }
        if (parameters.containsKey(Constants.CONF_ROUTING_DATE)) {
            this.rerouteOnDay = parameters.get(Constants.CONF_ROUTING_DATE);
        }
        if (parameters.containsKey(Constants.CONF_KEEP_PARACHUTE_ANNOTATIONS)) {
            this.retainParachuteAnnotations = Boolean.valueOf(parameters.get(Constants.CONF_KEEP_PARACHUTE_ANNOTATIONS));
        }
        if (parameters.containsKey(Constants.CONF_KEEP_OTHER_ANNOTATIONS)) {
            this.retainAnnotations = Boolean.valueOf(parameters.get(Constants.CONF_KEEP_OTHER_ANNOTATIONS));
        }
    }

    public String getRerouteOnDay() {
        return rerouteOnDay;
    }

    public String getOverProvisioningFactor() {
        return overProvisioningFactor;
    }

    public boolean isRetainParachuteAnnotations() {
        return retainParachuteAnnotations;
    }

    public boolean isRetainAnnotations() {
        return retainAnnotations;
    }
}
