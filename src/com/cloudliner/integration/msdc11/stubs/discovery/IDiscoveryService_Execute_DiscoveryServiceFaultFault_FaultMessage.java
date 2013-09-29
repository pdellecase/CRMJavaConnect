
/**
 * IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.cloudliner.integration.msdc11.stubs.discovery;

public class IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage extends java.lang.Exception{

    private static final long serialVersionUID = 1379107189587L;
    
    private com.cloudliner.integration.msdc11.stubs.discovery.DiscoveryServiceStub.DiscoveryServiceFaultE faultMessage;

    
        public IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage() {
            super("IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage");
        }

        public IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage(java.lang.String s) {
           super(s);
        }

        public IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public IDiscoveryService_Execute_DiscoveryServiceFaultFault_FaultMessage(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.cloudliner.integration.msdc11.stubs.discovery.DiscoveryServiceStub.DiscoveryServiceFaultE msg){
       faultMessage = msg;
    }
    
    public com.cloudliner.integration.msdc11.stubs.discovery.DiscoveryServiceStub.DiscoveryServiceFaultE getFaultMessage(){
       return faultMessage;
    }
}
    