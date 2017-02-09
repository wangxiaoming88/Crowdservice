package com.crowdserviceinc.crowdservice.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.crowdserviceinc.crowdservice.util.Constants;

public class Mail implements Serializable {
	private String jobId = "";
	private String status = "";
	private String jobName = "";
	private String toUserId = "";
	private String toUserName = "";
	private String toUserImage = "";
	private String fromUserId = "";
	private String fromUserName = "";
	private String fromUserImage = "";
	private String message = "";
	private String imageUrl = "";
	private Date date = null;
	private String questionId = "";
	private String jobCreator = ""; 
	private String parentId = "";
	private ArrayList<Mail> answers = new ArrayList<Mail>();
		
	public Mail(){
		
	}
	public Mail(JSONObject jObj){
		parseJSONData(jObj);
	}
	
	private void parseJSONData(JSONObject jObj){
		try {
			JSONObject jPushData = jObj.getJSONObject("Pushnotification");
			JSONObject jJobData = jObj.getJSONObject("job");
			
			this.status = jPushData.getString("status");
			this.jobId = jPushData.getString("job_id");
			this.jobName = jJobData.getString("name");
			this.jobCreator = jJobData.getString("user_id");
			this.message = jPushData.getString("message");
			this.setDate(Constants.serverFormate.parse(jPushData.getString("date")) );
			this.setQuestionId(jPushData.getString("question_id"));
			this.setParentId(jPushData.getString("parent_id"));
			
			this.imageUrl = jPushData.getString("image");
			
			JSONObject jFromUser = jObj.getJSONObject("from_user");
			JSONObject jToUser = jObj.getJSONObject("to_user");
			
//			{"from_user":{"id":null,"user_name":null,"image":null},"to_user":{"image":"http:\/\/admin.crowdserviceinc.com\/profile\/big\/c46c5e_image.png","id":"270","user_name":"Michael Steve-o"},"job":{"user_id":"270","name":"Starbucks"},"Pushnotification":{"id":"389","from_user_id":"0","to_user_id":"270","job_id":"851","message":"pptest wishes to complete the job, 'Starbucks'","image":"","date":"2016-04-14 04:21:19","status":"1","question_id":null,"parent_id":null}}
			
			this.setToUserName(jToUser.getString("user_name"));
			this.setToUserId(jToUser.getString("id"));
			this.setToUserImage(jToUser.getString("image"));
			
			if(jPushData.getString("from_user_id").equals("0")){
				this.status = "3";
			}else{
				this.setFromUserName(jFromUser.getString("user_name"));
				this.setFromUserId(jFromUser.getString("id"));
				this.setFromUserImage(jFromUser.getString("image"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getJobCreator() {
		return jobCreator;
	}

	public void setJobCreator(String jobCreator) {
		this.jobCreator = jobCreator;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public ArrayList<Mail> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<Mail> answers) {
		if(answers != null)
			this.answers = answers;
		else
			this.answers = new ArrayList<Mail>();
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getToUserImage() {
		return toUserImage;
	}
	public void setToUserImage(String toUserImage) {
		this.toUserImage = toUserImage;
	}
	public String getFromUserImage() {
		return fromUserImage;
	}
	public void setFromUserImage(String fromUserImage) {
		this.fromUserImage = fromUserImage;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	
}
