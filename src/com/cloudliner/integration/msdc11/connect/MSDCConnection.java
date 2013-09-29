package com.cloudliner.integration.msdc11.connect;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.log4j.Logger;

import com.cloudliner.integration.msdc11.MSDCJavaConnectConstants;
import com.cloudliner.integration.msdc11.stubs.organization.IOrganizationService_Create_OrganizationServiceFaultFault_FaultMessage;
import com.cloudliner.integration.msdc11.stubs.organization.OrganizationServiceStub;

public class MSDCConnection {
	
	// Logger for this class
    protected final Logger logger = Logger.getLogger(this.getClass());
	
	private IMSDCSecurityHeader securityHeader;
	private OrganizationServiceStub organizationServiceStub = null;
	private int soapRequestValidityMinutes = MSDCJavaConnectConstants.MSDC_DFT_SOAP_REQUEST_VALIDITY_MINUTES;
	
	public MSDCConnection(IMSDCSecurityHeader securityHeader){
		this.securityHeader = securityHeader;
	}
	
	public MSDCConnection(IMSDCSecurityHeader securityHeader, int soapRequestValidityMinutes){
		this.securityHeader = securityHeader;
		this.soapRequestValidityMinutes = soapRequestValidityMinutes;
	}
	
	public void authenticateUser() throws MSDCIntegrationException {
		
		securityHeader.authenticateUser();
		
		// Clear eventual existing Organization Service stub so it get created again
		organizationServiceStub = null;
		logger.debug("Security header >>> " + securityHeader.getSecurityHeader());
			
	}
	
	public OrganizationServiceStub createOrganizationService() throws MSDCIntegrationException{
		// Clear eventual existing Organization Service stub so it get created again
		organizationServiceStub = null;
		return getRefreshedOrganizationService(); 
	}
	
	public OrganizationServiceStub getRefreshedOrganizationService() throws MSDCIntegrationException{
		
		// check first that authentication was done successfully prior to use the stub
		if (!securityHeader.isAuthenticated()) throw new MSDCIntegrationException(
				MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_REQUIRED, 
				MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION);
		
		ServiceClient serviceClient = null;
		
		// Create OrganizationServiceStub if null otherwise it get recycled with fresh header
		if(organizationServiceStub==null){
			logger.debug("Creating organizationServiceStub");
			try {
				String fileSeperator = System.getProperty("file.separator");
				String userDir = System.getProperty("user.dir");
				String axis2ConfigFilePath = userDir + fileSeperator + "src" + fileSeperator + "axis2.xml";
				logger.debug("Creating Axis2 configuration context from conf.file: " + axis2ConfigFilePath);

				ConfigurationContext ctx = ConfigurationContextFactory.createConfigurationContextFromFileSystem(userDir, axis2ConfigFilePath);
				organizationServiceStub = new OrganizationServiceStub(ctx, securityHeader.getOrganizationServiceUrl());
				
				// Get service client implementation used by this stub.
				serviceClient = organizationServiceStub._getServiceClient();
				
			} catch (AxisFault af) {
				throw new MSDCIntegrationException(
						MSDCIntegrationException.ERR_MSDC_WEB_SERVICE_ERROR, 
						MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION,
						af);
			}
		} else {
			logger.debug("Recycling organizationServiceStub");
			// Get service client implementation used by this stub.
			serviceClient = organizationServiceStub._getServiceClient();
			// Remove existing headers in order to produce a fresh one
			serviceClient.removeHeaders();
		}
			
		try {
			Options scOptions = serviceClient.getOptions();
			scOptions.setMessageId("urn:uuid:" + UUID.randomUUID().toString());
			EndpointReference endPoint = new EndpointReference("http://www.w3.org/2005/08/addressing/anonymous");
			scOptions.setReplyTo(endPoint);
			serviceClient.setOptions(scOptions);
			
			// Add fresh Security SOAP Header block
			serviceClient.addHeader(generateFreshSecuritySoapHeaderBlock(securityHeader.getSecurityHeader()));    
			serviceClient.engageModule("addressing");
			
		} catch (AxisFault af) {
			throw new MSDCIntegrationException(
					MSDCIntegrationException.ERR_MSDC_WEB_SERVICE_ERROR, 
					MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION,
					af);
		}
            
		
		
		return organizationServiceStub;
	}
	
	public OrganizationServiceStub getCurrentOrganizationService() throws MSDCIntegrationException{
		
		// check first that authentication was done successfully prior to use the stub
				if (!securityHeader.isAuthenticated()) throw new MSDCIntegrationException(
						MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_REQUIRED, 
						MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION);
				
		if (organizationServiceStub != null) return organizationServiceStub;
		else return createOrganizationService();
	}	
	
	public String creatEntity(String entityLogicalName, HashMap<String,String> attributesHash) throws MSDCIntegrationException {
		
		// Get a fresh Organization service stub 
		OrganizationServiceStub organizationService = getRefreshedOrganizationService();
		return creatEntity(entityLogicalName, attributesHash, organizationService);
	}
	
	public String creatEntity(String entityLogicalName, HashMap<String,String> attributesHash, OrganizationServiceStub organizationService) throws MSDCIntegrationException {
		
		// Transfer attributes from HashMap to Stub attribute collection
		OrganizationServiceStub.AttributeCollection attributeCollection = new OrganizationServiceStub.AttributeCollection();
		Iterator<String> it = attributesHash.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			OrganizationServiceStub.KeyValuePairOfstringanyType KeyValuePair = new OrganizationServiceStub.KeyValuePairOfstringanyType();
	        KeyValuePair.setKey(key);
	        KeyValuePair.setValue(attributesHash.get(key));
	        attributeCollection.addKeyValuePairOfstringanyType(KeyValuePair);
		}
        
		// Create Entity with attributes
        OrganizationServiceStub.Entity entity = new OrganizationServiceStub.Entity();
        entity.setLogicalName(entityLogicalName);
        entity.setAttributes(attributeCollection);
        OrganizationServiceStub.Create createEntity = new OrganizationServiceStub.Create();
        createEntity.setEntity(entity);

        // Send Create command to Organization web service
        String resultGuid = null;
        try {
        	OrganizationServiceStub.CreateResponse createResponse;
			createResponse = organizationService.create(createEntity);
			OrganizationServiceStub.Guid createResultGuid = createResponse.getCreateResult();
			resultGuid = createResultGuid.getGuid();
			
		} catch (RemoteException
				| IOrganizationService_Create_OrganizationServiceFaultFault_FaultMessage e) {
			throw new MSDCIntegrationException(
					MSDCIntegrationException.ERR_MSDC_WEB_SERVICE_ERROR, 
					MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION,
					e);
			}
        
        logger.debug("Entity '" +  entityLogicalName + "' created successfully with GUID =  " + resultGuid);
        return resultGuid;
	}
	
	private SOAPHeaderBlock generateFreshSecuritySoapHeaderBlock (String securityHeaderStr) throws MSDCIntegrationException {
		
		SOAPHeaderBlock securitySoapHeaderBlock = null;
		try {
		
			OMFactory omFactory = OMAbstractFactory.getOMFactory();
	        OMNamespace securitySecextNS = omFactory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "o");
	        OMNamespace securityUtilityNS = omFactory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "u");
	        
	        
	        // Create fresh Time stamp element for the SOAP header block
	        
	        TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			formatter.setTimeZone(gmtTZ);
	        Calendar calendar = Calendar.getInstance(gmtTZ);
	        Date timestampRequest = calendar.getTime();
	        calendar.add(Calendar.MINUTE, soapRequestValidityMinutes);
	        Date timestampExpiryRequest = calendar.getTime();
	        String timestampRequestStr = formatter.format(timestampRequest);
	        String timestampExpiryRequestStr = formatter.format(timestampExpiryRequest);
	        
	        OMElement timeStampElement = omFactory.createOMElement("Timestamp", securityUtilityNS);
	        timeStampElement.addAttribute("Id", "_0", securityUtilityNS);
	        OMElement createdElement = omFactory.createOMElement("Created", securityUtilityNS);
	        OMText createdTime = omFactory.createOMText(timestampRequestStr + "Z");
	        createdElement.addChild(createdTime);
	        OMElement expiresElement = omFactory.createOMElement("Expires", securityUtilityNS);
	        OMText expiresTime = omFactory.createOMText(timestampExpiryRequestStr + "Z");
	        expiresElement.addChild(expiresTime);
	        timeStampElement.addChild(createdElement);
	        timeStampElement.addChild(expiresElement);

	        // Create the Security SOAP header block and add, as a child, Time stamp element
	        securitySoapHeaderBlock = OMAbstractFactory.getSOAP12Factory().createSOAPHeaderBlock("Security", securitySecextNS);
	        securitySoapHeaderBlock.setMustUnderstand(true);
	        securitySoapHeaderBlock.addChild(timeStampElement);
	        securitySoapHeaderBlock.addChild(AXIOMUtil.stringToOM(omFactory, securityHeaderStr)); 
	        
		} catch (XMLStreamException xe) {
			throw new MSDCIntegrationException(
						MSDCIntegrationException.ERR_MSDC_XML_STREAM_ERROR, 
						MSDCIntegrationException.CAT_MSDC_CONNECTION_EXCEPTION,
						xe);
		}
		
		return securitySoapHeaderBlock;
	}

}
