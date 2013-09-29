package com.cloudliner.integration.msdc11.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.cloudliner.integration.msdc11.MSDCJavaConnectConstants;

public class MSDCSoapHelper {
	
	public static String postSOAPRequest(String serviceUrl, String soapEnvelope) throws ParseException, IOException {
		
		return postSOAPRequest(URI.create(serviceUrl), soapEnvelope, MSDCJavaConnectConstants.MSDC_DFT_CONNECTION_TIMEOUT);         
    }
	
	public static String postSOAPRequest(String serviceUrl, String soapEnvelope, int connectionTimeOutMs) throws ParseException, IOException {
		
		return postSOAPRequest(URI.create(serviceUrl), soapEnvelope, connectionTimeOutMs);         
    }

	public static String postSOAPRequest(URI serviceUri, String soapEnvelope) throws ParseException, IOException {
		
		return postSOAPRequest(serviceUri, soapEnvelope, MSDCJavaConnectConstants.MSDC_DFT_CONNECTION_TIMEOUT);         
    }
	
	public static String postSOAPRequest(URI serviceUri, String soapEnvelope, int connectionTimeOutMs) throws ParseException, IOException {
		
		HttpResponse response = null;
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeOutMs);
		HttpClient client = new DefaultHttpClient(params);
        HttpPost post = new HttpPost(serviceUri);
        StringEntity entity = new StringEntity(soapEnvelope);
        post.setHeader("Content-Type", MSDCJavaConnectConstants.MSDC_DFT_CONTENT_TYPE);
        post.setEntity(entity);

        response = client.execute(post);

        return EntityUtils.toString(response.getEntity());           
    }
	
	public static Document parseXMLtoDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException{
		
		// Create a Java DOM XML Parser
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		// Parse XML with Java DOM XML Parser
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));

		return xmlDocument;
	}

	public static XPath instantiateXPath(){
		
		return XPathFactory.newInstance().newXPath();
	}
		
	public static String readStringValFromXmlDocument(Document xmlDocument, String xpathQueryExpression, XPath xpathInstance) throws XPathExpressionException {
	        
		return xpathInstance.compile(xpathQueryExpression).evaluate(xmlDocument);	
	}
	
	public static Node readNodeValFromXmlDocument(Document xmlDocument, String xpathQueryExpression, XPath xpathInstance) throws XPathExpressionException {
        
		return (Node)xpathInstance.compile(xpathQueryExpression).evaluate(xmlDocument, XPathConstants.NODE);	
	}
	
	public static NodeList readNodelistValFromXmlDocument(Document xmlDocument, String xpathQueryExpression, XPath xpathInstance) throws XPathExpressionException {
        
		return (NodeList)xpathInstance.compile(xpathQueryExpression).evaluate(xmlDocument, XPathConstants.NODESET);	
	}

}
