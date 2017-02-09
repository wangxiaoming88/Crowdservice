package com.crowdserviceinc.crowdservice.model;

public class ResultData {

	String authenticated="";
	String message="";
	String user_email="";
	String userid="";
	String amount = "";
	String reason = "";
	int userType = -1;
	String approxAddress = "";
	String trueAddress = "";
	String job_name = "";
	String cookie = "";
	String paypalId = "";
	
	public String getApproxAddress() {
		return approxAddress;
	}

	public void setApproxAddress(String approxAddress) {
		this.approxAddress = approxAddress;
	}
	
	public String getTrueAddress() {
		return trueAddress;
	}

	public void setTrueAddress(String trueAddress) {
		this.trueAddress = trueAddress;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public ResultData() {
		// TODO Auto-generated constructor stub
	}

	public String getAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(String authenticated) {
		this.authenticated = authenticated;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getUserEmail() {
		return user_email;
	}

	public void setUserEmail(String user_email) {
		this.user_email = user_email;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getJobName() {
		return job_name;
	}

	public void setJobName(String jobName) {
		this.job_name = jobName;
	}
	
	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public String getPaypalId(){
		return paypalId;
	}
	
	public void setPaypalId(String paypalId){
		this.paypalId = paypalId;
	}
}
