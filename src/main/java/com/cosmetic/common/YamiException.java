package com.cosmetic.common;



public class YamiException extends RuntimeException { 
    private static final long serialVersionUID = 1L;  
	private String errorId;
	private String enerrorMessage;
	private String cnerrorMessage;
	private String aderrorMessage;
	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getEnerrorMessage() {
		return enerrorMessage;
	}

	public void setEnerrorMessage(String enerrorMessage) {
		this.enerrorMessage = enerrorMessage;
	}

	public String getCnerrorMessage() {
		return cnerrorMessage;
	}

	public void setCnerrorMessage(String cnerrorMessage) {
		this.cnerrorMessage = cnerrorMessage;
	}

	public String getAderrorMessage() {
		return aderrorMessage;
	}

	public void setAderrorMessage(String aderrorMessage) {
		this.aderrorMessage = aderrorMessage;
	}


	public YamiException() {
		super();
	}

	public YamiException(String errorId, String errorMessage[]) {
		super(errorMessage[0]);
		this.errorId = errorId;
		this.enerrorMessage = errorMessage[0];
		this.cnerrorMessage = errorMessage[1];
	}

	public YamiException(String errorId, String errorMessage[], String adMessage) {
		super(errorMessage[0]);
		this.errorId = errorId;
		this.enerrorMessage = errorMessage[0];
		this.cnerrorMessage = errorMessage[1];
		this.aderrorMessage = adMessage;
	}

}
