package com.crowdserviceinc.crowdservice.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.crowdserviceinc.crowdservice.util.Constants;

public class Category implements Serializable{

	private String id="";
	private String name="";
	private int type;
	
	public Category(String id,String name) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.name=name;
		this.type=0;
	}
	public Category(String id,String name, int type) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.name=name;
		this.type=type;
	}	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Category(JSONObject jobj){
		try {
			this.id = jobj.getString("id");
			this.name = jobj.getString("name");
			
			String strType = jobj.getString("category_type");
			if(strType.equalsIgnoreCase("courier"))
				this.type = Constants.CATEGORY_TYPE_COURIER;
			else if(strType.equalsIgnoreCase("home"))
				this.type = Constants.CATEGORY_TYPE_HOME;
			else if(strType.equalsIgnoreCase("auto"))
				this.type = Constants.CATEGORY_TYPE_AUTO;
			else
				this.type = 0;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
