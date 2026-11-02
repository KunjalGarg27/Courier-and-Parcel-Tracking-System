package com.courier.model;

public class ServiceType {

    private final int serviceTypeId;
    private final String serviceName;

    public ServiceType(int serviceTypeId, String serviceName) {
        this.serviceTypeId = serviceTypeId;
        this.serviceName = serviceName;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public String getServiceName() {
        return serviceName;
    }

    // Used by JComboBox to display the item.
    @Override
    public String toString() {
        return serviceName.replace('_', ' ');
    }
}
