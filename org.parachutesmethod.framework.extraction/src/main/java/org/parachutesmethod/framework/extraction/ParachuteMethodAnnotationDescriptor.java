package org.parachutesmethod.framework.extraction;

public class ParachuteMethodAnnotationDescriptor {
    private boolean retainParachuteAnnotations;
    private String rerouteOnDay;
    private String overProvisioningFactor;

    public ParachuteMethodAnnotationDescriptor(boolean retainParachuteAnnotations, String rerouteOnDay, String overProvisioningFactor) {
        this.retainParachuteAnnotations = retainParachuteAnnotations;
        this.rerouteOnDay = rerouteOnDay;
        this.overProvisioningFactor = overProvisioningFactor;
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
