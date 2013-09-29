package com.cloudliner.integration.msdc11.connect;


public class MSDCIntegrationException extends Exception{

	private static final long serialVersionUID = 1L;

	// Error messages Constants
	public static final String ERR_MSDC_AUTHENTICATION_REQUIRED = "Any operation over MSDC connection requires a user authentication first";
	public static final String ERR_MSDC_AUTHENTICATION_MISSING_PARAMETER = "Missing required parameter(s) for user authentication";
	public static final String ERR_MSDC_AUTHENTICATION_UNKNOWN_ANSWER_FORMAT = "Cannot parse authentication answer, assuming the authentication is failed";
	public static final String ERR_MSDC_PARSING_ERROR = "Unexpected parsing error";
	public static final String ERR_MSDC_XML_STREAM_ERROR = "Unexpected XML stream error";
	public static final String ERR_MSDC_IO_ERROR = "Unexpected IO error";
	public static final String ERR_MSDC_WEB_SERVICE_ERROR = "Unexpected Web Service error";
	
	// Error type constants
	public static final int CAT_MSDC_GENERAL = 0;
	public static final int CAT_MSDC_AUTHENTICATION_FAILURE = 1;
	public static final int CAT_MSDC_AUTHENTICATION_EXCEPTION = 2;
	public static final int CAT_MSDC_CONNECTION_EXCEPTION = 3;

	
	// Error Category
	private int errorCategory;
	
	
	public int getErrorCategory() {
		return errorCategory;
	}

	/**
     * Constructor with message
     *
     * @param msg Message describing the problem
     */
	public MSDCIntegrationException(String msg) {
		super(msg);
		this.errorCategory = CAT_MSDC_GENERAL;
	}
	
	/**
     * Constructor with message and error category
     *
     * @param msg Message describing the problem
     */
	public MSDCIntegrationException(String msg, int errorCategory) {
		super(msg);
		this.errorCategory = errorCategory;
	}
	
	/**
     * Constructor with message and origin of the exception
     *
     * @param msg Message describing the problem
     * @param origin Exception causing the problem
     */
	public MSDCIntegrationException(String msg, Throwable origin){
		super(msg, origin);
		this.errorCategory = CAT_MSDC_GENERAL;
	}
	
	/**
     * Constructor with message, error category,  and origin of the exception
     *
     * @param msg Message describing the problem
     * @param origin Exception causing the problem
     */
	public MSDCIntegrationException(String msg, int errorCategory,  Throwable origin){
		super(msg, origin);
		this.errorCategory = errorCategory;
	}
}
