package com.cloudliner.integration.msdc11;

public class MSDCJavaConnectConstants {

	/** ---------------------- CRM Organization types ---------------------- **/
	
	static public final String MSDC_CRM_ORG_TYPE_ONLINE_O365 = "Dynamics CRM Online (Office 365)";
	static public final String MSDC_CRM_ORG_TYPE_ONPREM_AD = "Dynamics CRM On premise (AD/Windows Auth.)";
	static public final String MSDC_CRM_ORG_TYPE_ONPREM_IFD = "Dynamics CRM On premise (STS/IFD)";
	
	
	/** ---------------------- Connect parameters ---------------------- **/
	
	// MSDC Organization URL suffix 
	static public final String MSDC_ORGANIZATION_SERVICE_URL_SUFFIX = "/XRMServices/2011/Organization.svc";
	
	// MSDC Online Secure Token Service URL
	static public final String MSDC_ONLINE_STS_URL = "https://login.microsoftonline.com/RST2.srf";
	
	// HTTP SOAP request's header content type
	static public final String MSDC_DFT_CONTENT_TYPE = "application/soap+xml; charset=UTF-8";
	
	// Determines the default connection timeout in milliseconds until a connection is established
	static public final int MSDC_DFT_CONNECTION_TIMEOUT  = 180000;
	
	static public final int MSDC_DFT_AUTHENTICATION_REQUEST_VALIDITY_MINUTES = 5;
	static public final int MSDC_DFT_SOAP_REQUEST_VALIDITY_MINUTES = 5;
	
	
	/** ---------------------- SOAP templates ---------------------- **/
	
	// OASIS Security Namespace URL
	static public final String MSDC_SECURITY_NAMESPACE_URL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	// Security header template
	static public final String MSDC_SECURITY_HEADER_TEMPLATE = 
			"<EncryptedData xmlns=\"http://www.w3.org/2001/04/xmlenc#\" Id=\"Assertion0\" Type=\"http://www.w3.org/2001/04/xmlenc#Element\">" +
       		"	<EncryptionMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#tripledes-cbc\"/>" +
       		"	<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
       		"		<EncryptedKey>" +
       		"			<EncryptionMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\"/>" +
       		"			<ds:KeyInfo Id=\"keyinfo\">" +
       		"				<wsse:SecurityTokenReference xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
       		"					<wsse:KeyIdentifier EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\" " +
       		"						ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509SubjectKeyIdentifier\">%s</wsse:KeyIdentifier>" +
       		"				</wsse:SecurityTokenReference>" +
       		"			</ds:KeyInfo>" +
       		"			<CipherData>" +
       		"				<CipherValue>%s</CipherValue>" +
       		"			</CipherData>" +
       		"		</EncryptedKey>" +
       		"	</ds:KeyInfo>" +
       		"	<CipherData>" +
       		"		<CipherValue>%s</CipherValue>" +
       		"	</CipherData>" +
       		"</EncryptedData>";
	
	// SOAP envelope template for MSDC Online STS authentication (O365 online platform) 
	static public final String MSDC_ONLINE_AUTH_SOAP_ENVELOPE_TEMPLATE = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" " +
			"xmlns:a=\"http://www.w3.org/2005/08/addressing\" " +
			"xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
			" <s:Header>" +
			"  <a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>" +
			"  <a:MessageID>urn:uuid:%s</a:MessageID>" +
			"  <a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo>" +
			"  <VsDebuggerCausalityData xmlns=\"http://schemas.microsoft.com/vstudio/diagnostics/servicemodelsink\">uIDPo2V68j15KH9PqGf9DWiAfGQAAAAA/Dr1z6qvqUGzr5Yv4aMcdIr9AKDFU7VHn7lpNp0zeXEACQAA</VsDebuggerCausalityData>" +
			"  <a:To s:mustUnderstand=\"1\">%s</a:To>" +
			"  <o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
			"   <u:Timestamp u:Id=\"_0\">" +
			"    <u:Created>%s</u:Created>" +
			"    <u:Expires>%s</u:Expires>" +
			"   </u:Timestamp>" +
			"   <o:UsernameToken u:Id=\"%s\">" +
			"    <o:Username>%s</o:Username>" +
			"    <o:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">%s</o:Password>" +
			"   </o:UsernameToken>" +
			"  </o:Security>" +
			" </s:Header>" +
			" <s:Body>" +
			"  <t:RequestSecurityToken xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">" +
			"   <wsp:AppliesTo xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">" +
			"    <a:EndpointReference>" +
			"     <a:Address>urn:crmna:dynamics.com</a:Address>" +
			"    </a:EndpointReference>" +
			"   </wsp:AppliesTo>" +
			"   <t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType>" +
			"  </t:RequestSecurityToken>" +
			" </s:Body>" +
			"</s:Envelope>";
}
