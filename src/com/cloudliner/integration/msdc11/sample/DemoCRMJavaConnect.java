package com.cloudliner.integration.msdc11.sample;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.cloudliner.integration.msdc11.connect.MSDCConnection;
import com.cloudliner.integration.msdc11.connect.MSDCIntegrationException;
import com.cloudliner.integration.msdc11.connect.MSDCOnlineSecurityHeader;
import com.cloudliner.integration.msdc11.MSDCJavaConnectConstants;



public class DemoCRMJavaConnect {

	// Logger for this class
    protected final Logger logger = Logger.getLogger(this.getClass());
	
	int crmOrgType;
	String userName;
	String userPassword;
	String crmOrgUrl;
	
	
	public DemoCRMJavaConnect(int crmOrgType, String userName, String userPassword, String crmOrgUrl) {
		super();
		this.crmOrgType = crmOrgType;
		this.userName = userName;
		this.userPassword = userPassword;
		this.crmOrgUrl = crmOrgUrl;
	}

	public void run(){
		logger.info(">>> Demo CRM Java Connect Demo started ...");
		
		MSDCConnection connection = new MSDCConnection(new MSDCOnlineSecurityHeader(userName, userPassword, crmOrgUrl));
		try {
			// Authenticate user
			connection.authenticateUser();
			
			// Create 10 Accounts
			long startDate = new Date().getTime();
			for(int i=0;i<10;i++){	
				HashMap<String,String> attributesHash = new HashMap<String, String>();
				attributesHash.put("name", "Test Account " + i);
				attributesHash.put("address1_city", "Paris");
				attributesHash.put("telephone1", "012345678" + i);
				connection.creatEntity("account", attributesHash);
				
			}
			long endDate = new Date().getTime();
			logger.debug("-> Created 10 accounts in " + (endDate - startDate)/1000 + " sec.");
	
			
	       
		} catch (MSDCIntegrationException e) {
			e.printStackTrace();
			exit();
		}
		  
		logger.info("...Demo CRM Java Connect Demo completed <<<");
	}
	
	public static void exit(){
		System.out.println("Demo stopped !");
		System.exit(0);
	}	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		//String param = args[0];
		int crmOrgType = 0;
		String userName = null;
		String userPassword = null;
		String crmOrgUrl = null;
		
		System.out.println("\n=*=*=*=  Demo CRM Java Connect  =*=*=*=\n");

		// Instantiate BufferReader class to read user input data
		// Specify the reader variable to be a standard input buffer
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(System.in));
		
		// Ask user what type of CRM Organization to connect to
		System.out.println("Please enter CRM Organization type:");
		System.out.println(" 1 - " + MSDCJavaConnectConstants.MSDC_CRM_ORG_TYPE_ONLINE_O365 + "\n");

		try {
			crmOrgType = new Integer(reader.readLine()).intValue();
		} catch (NumberFormatException e) {
			System.out.println("Error, you must enter a numeric value between 1 and 2 !\n");
			exit();
		} catch (IOException e) {
			System.out.println("Unexpected Error !\n\n");
			e.printStackTrace();
			exit();
		}
		if((crmOrgType<1)||(crmOrgType>2)){
			System.out.println("Error, you must enter a numeric value between 1 and 2 !\n");
			exit();
		}
		
		
		try {
			// Ask user what type of CRM Organization to connect to
			System.out.println("Please enter your Dynamics CRM Org Username (e.g. admin@myCRMOrg.onmicrosoft.com):\n");
			userName = reader.readLine();
			
			// Ask user what type of CRM Organization to connect to
			System.out.println("Please enter your Dynamics CRM Org Password:\n");
			userPassword = reader.readLine();		
			// Ask user what type of CRM Organization to connect to
			System.out.println("Please enter your Dynamics CRM Org URL (e.g. https://myCRMOrg.crm.dynamics.com):\n1");
			crmOrgUrl = reader.readLine();
			
		} catch (IOException e) {
			System.out.println("Unexpected Error !\n\n");
			e.printStackTrace();
			exit();
		}
		
		
		
		
		DemoCRMJavaConnect obj = new DemoCRMJavaConnect(crmOrgType, userName, userPassword,  crmOrgUrl);
		
		obj.run();
	}

}
