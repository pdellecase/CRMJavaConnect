package com.cloudliner.integration.msdc11.connect;

public interface IMSDCSecurityHeader {
	
	public void authenticateUser() throws MSDCIntegrationException;
	
	public String getUserName();
		
	public String getSecurityHeader() throws MSDCIntegrationException;
	
	public boolean isAuthenticated();
	
	public String getCrmOrgUrl();
	
	public String getOrganizationServiceUrl();

}
