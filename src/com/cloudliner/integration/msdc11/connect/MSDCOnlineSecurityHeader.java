package com.cloudliner.integration.msdc11.connect;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.ParseException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.cloudliner.integration.msdc11.util.MSDCSoapHelper;
import com.cloudliner.integration.msdc11.MSDCJavaConnectConstants;

public class MSDCOnlineSecurityHeader implements IMSDCSecurityHeader{
	
	// Logger for this class
    protected final Logger logger = Logger.getLogger(this.getClass());

    // Input authentication parameters
	private String userName;
	private String userPassword;
	private String crmOrgUrl;
	
	// Output authentication parameters
	private String securityToken0;
	private String securityToken1;
	private String keyIdentifier;
	private String securityHeader = null;
	private boolean isAuthenticated = false;
	
	
	
	public MSDCOnlineSecurityHeader(String userName, String userPassword, String crmOrgUrl) {
		super();
		this.userName = userName;
		this.userPassword = userPassword;
		this.crmOrgUrl = crmOrgUrl;
	}
	
	@Override
	public void authenticateUser() throws MSDCIntegrationException {
		
		logger.debug("MSDC Online Authenticating user '" + userName + "' ...");
		
		// Check if all required parameters are provided
		if((userName==null)||(userName.length()<=0)) throw new MSDCIntegrationException(
				MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_MISSING_PARAMETER + ": userName.", 
			MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_FAILURE);
		if((userPassword==null)||(userPassword.length()<=0)) throw new MSDCIntegrationException(
				MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_MISSING_PARAMETER + ": userPassword.", 
				MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_FAILURE);
		
		// Prepare extra input parameter for CRM Authentication Request
		// > Random Message Id
		String paramMessageId = UUID.randomUUID().toString();  
		// > Request Timestamp and +5 minutes validity 
		TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		formatter.setTimeZone(gmtTZ);
        Calendar calendar = Calendar.getInstance(gmtTZ);
        Date timestampRequest = calendar.getTime();
        calendar.add(Calendar.MINUTE, MSDCJavaConnectConstants.MSDC_DFT_AUTHENTICATION_REQUEST_VALIDITY_MINUTES);
        Date timestampExpiryRequest = calendar.getTime();
        String paramTimestampRequest = formatter.format(timestampRequest);
        String paramTimestampExpiryRequest = formatter.format(timestampExpiryRequest);
        // > Random Token Id
        String paramTokenId = "uuid-" + UUID.randomUUID().toString() + "-1";
        
		// Prepare online CRM authentication SOAP request
        String onlineCRMAuthSOAPEnvelope = String.format(
        		MSDCJavaConnectConstants.MSDC_ONLINE_AUTH_SOAP_ENVELOPE_TEMPLATE,
				paramMessageId,
				MSDCJavaConnectConstants.MSDC_ONLINE_STS_URL,
				paramTimestampRequest,
				paramTimestampExpiryRequest,
				paramTokenId,
				userName,
				userPassword);

        logger.debug("Online CRM Authentication SOAP Envelope >>> " + onlineCRMAuthSOAPEnvelope);
		     
		try {
			 // Send CRM Online authentication SOAP request to Microsoft online STS
			String onlineCRMAuthResponseXML = MSDCSoapHelper.postSOAPRequest(
					MSDCJavaConnectConstants.MSDC_ONLINE_STS_URL, 
					onlineCRMAuthSOAPEnvelope);
			logger.debug("Online CRM Authentication SOAP Response >>> " + onlineCRMAuthResponseXML);
			
			// Parse the CRM Online authentication SOAP response from STS
			Document xmlDocument = MSDCSoapHelper.parseXMLtoDocument(onlineCRMAuthResponseXML);
			
			// Retrieve security tokens and key identifier from security token response.
			XPath xpath = MSDCSoapHelper.instantiateXPath();
			securityToken0 = MSDCSoapHelper.readStringValFromXmlDocument(xmlDocument, "//*[local-name()='CipherValue']",xpath);
			// If first token is blank, search eventual authentication failure message
			if((securityToken0==null)||(securityToken0.isEmpty())){
				String errorReason = MSDCSoapHelper.readStringValFromXmlDocument(xmlDocument, "//*[local-name()='Reason']",xpath);
				String errorDetail = MSDCSoapHelper.readStringValFromXmlDocument(xmlDocument, "//*[local-name()='Detail']",xpath).substring(20);
			
				if((errorReason!=null)&&(errorReason.equalsIgnoreCase("Authentication Failure"))){
					logger.debug("... Failed authentication for User '" + userName + "'. Reason is '" + errorReason + 
							"' and Detail is " + errorDetail);
					throw new MSDCIntegrationException(
							errorDetail, 
							MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_FAILURE);
				} else {
					logger.debug("... Failed authentication for User '" + userName + "' but cannot parse the reasons. Reason is '" + errorReason + 
							"' and Detail is " + errorDetail);
					throw new MSDCIntegrationException(
							MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_UNKNOWN_ANSWER_FORMAT, 
							MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_FAILURE);
				}
			}
			securityToken1 = MSDCSoapHelper.readStringValFromXmlDocument(xmlDocument, "(//*[local-name()='CipherValue'])[2]",xpath);
	        keyIdentifier = MSDCSoapHelper.readStringValFromXmlDocument(xmlDocument, "//*[local-name()='KeyIdentifier']", xpath);
	        
	        // Generate security header
	        securityHeader = String.format(
					MSDCJavaConnectConstants.MSDC_SECURITY_HEADER_TEMPLATE,
					keyIdentifier,
					securityToken0,
					securityToken1);
	        
	        isAuthenticated = true;
	    	logger.debug("... User '" + userName + " is authenticated and security header is generated with success");
	    	
			
		} catch (IOException ioe) {
			throw new MSDCIntegrationException(
					MSDCIntegrationException.ERR_MSDC_IO_ERROR, 
					MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_EXCEPTION, ioe);
		} catch (ParseException | ParserConfigurationException | SAXException | XPathExpressionException pe){
			throw new MSDCIntegrationException(
					MSDCIntegrationException.ERR_MSDC_PARSING_ERROR, 
					MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_EXCEPTION, pe);
		}
		
	}
	
	
	/*** Getters and Setters ***/
	
	@Override
	public String getUserName() {
		return userName;
	}
	
	@Override
	public String getSecurityHeader() throws MSDCIntegrationException {
		
		if((!isAuthenticated)||(securityHeader==null)) throw new MSDCIntegrationException(
				MSDCIntegrationException.ERR_MSDC_AUTHENTICATION_REQUIRED, 
				MSDCIntegrationException.CAT_MSDC_AUTHENTICATION_EXCEPTION);
		
		return securityHeader;
	}

	@Override
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	
	public String getCrmOrgUrl(){
		return crmOrgUrl;
	}
	
	public String getOrganizationServiceUrl(){
		return crmOrgUrl + MSDCJavaConnectConstants.MSDC_ORGANIZATION_SERVICE_URL_SUFFIX;
	}
	
}
