
/**
 * IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.cloudliner.integration.msdc11.stubs.organization;

public class IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage extends java.lang.Exception{

    private static final long serialVersionUID = 1379107194973L;
    
    private com.cloudliner.integration.msdc11.stubs.organization.OrganizationServiceStub.OrganizationServiceFaultE faultMessage;

    
        public IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage() {
            super("IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage");
        }

        public IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage(java.lang.String s) {
           super(s);
        }

        public IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public IOrganizationService_Disassociate_OrganizationServiceFaultFault_FaultMessage(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.cloudliner.integration.msdc11.stubs.organization.OrganizationServiceStub.OrganizationServiceFaultE msg){
       faultMessage = msg;
    }
    
    public com.cloudliner.integration.msdc11.stubs.organization.OrganizationServiceStub.OrganizationServiceFaultE getFaultMessage(){
       return faultMessage;
    }
}
    