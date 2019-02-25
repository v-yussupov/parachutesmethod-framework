package org.parachutesmethod.framework.extraction;

import java.util.Map;

public class ParachuteMethodAnnotationDescriptor {
    private boolean retainParachuteAnnotations;
    private String rerouteOnDay;
    private String overProvisioningFactor;

    public ParachuteMethodAnnotationDescriptor(Map<String, String> parameters) {
        if (parameters.containsKey(Constants.ROUTING_CONDITION_OVERPROVISIONING)) {
            this.overProvisioningFactor = parameters.get(Constants.ROUTING_CONDITION_OVERPROVISIONING);
        }
        if (parameters.containsKey(Constants.ROUTING_CONDITION_DATE)) {
            this.rerouteOnDay = parameters.get(Constants.ROUTING_CONDITION_DATE);
        }
        if (parameters.containsKey(Constants.CONFIGURATION_RETAIN_ANNOTATIONS)) {
            this.retainParachuteAnnotations = Boolean.valueOf(parameters.get(Constants.CONFIGURATION_RETAIN_ANNOTATIONS));
        }
    }

    public boolean isRetainParachuteAnnotations() {
        return retainParachuteAnnotations;
    }

    public String getRerouteOnDay() {
        return rerouteOnDay;
    }

    public String getOverProvisioningFactor() {
        return overProvisioningFactor;
    }
}
