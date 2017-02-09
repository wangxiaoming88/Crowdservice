package com.crowdserviceinc.crowdservice.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.crowdserviceinc.crowdservice.util.http.HttpPostConnector;
import com.crowdserviceinc.crowdservice.model.ResultData;

public class DataPostParser {

	ResultData resultData = null;
	String strUrl;

	public DataPostParser(String url) {
		strUrl = url;
	}

	public String convertStreamToString(InputStream is) 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			Log.e("HttpReaderException",">>>"+e.getMessage());
		} 
		
		return sb.toString();
	}
	
	public ResultData getParseData(ArrayList<NameValuePair> params) {
				  		
		InputStream stream = null;
		
		try {
			HttpPostConnector conn = new HttpPostConnector(strUrl, params);
			//String response = conn.getResponseData();
			
			HttpClient httpclient=new DefaultHttpClient();
			HttpPost httppost=new HttpPost(strUrl); 
			httppost.setEntity(new UrlEncodedFormEntity(params)); 
			// Execute HTTP Post Request  
			HttpResponse httpResponse = httpclient.execute(httppost);  
			HttpEntity entity=httpResponse.getEntity();		
			stream=entity.getContent();			
			String response = convertStreamToString(stream);
			
			Header cookie = httpResponse.getLastHeader("Set-Cookie");
			
			Log.e("TAGG", "URL Res > " + strUrl);
			Log.e("TAGG", "Res > " + response);
			if (response != null) {
				JSONObject jsonresponse;
				try {
					jsonresponse = new JSONObject(response);
					Log.e("Login response", "" + jsonresponse);
					
					resultData = new ResultData();
					
					if(cookie != null)
						resultData.setCookie(cookie.getValue());
					resultData.setAuthenticated(jsonresponse
							.getString("status"));
					try {
						resultData
								.setMessage(jsonresponse.getString("message"));
						if (jsonresponse.has("id"))
							resultData.setUserid(jsonresponse.getString("id"));
						if (jsonresponse.has("user_type"))
							resultData.setUserType(Integer
									.parseInt(jsonresponse
											.getString("user_type")));
						if (jsonresponse.has("aprox_address"))
							resultData.setApproxAddress(jsonresponse
									.getString("aprox_address")
									+ ", "
									+ jsonresponse.getString("aprox_address2")
									+ ", "
									+ jsonresponse.getString("aprox_address3")
									+ ", "
									+ jsonresponse.getString("aprox_address4")
									+ ", "
									+ jsonresponse.getString("aprox_address5"));
						if (jsonresponse.has("email"))
							resultData.setUserEmail(jsonresponse
									.getString("email"));
						if (jsonresponse.has("amount"))
							resultData.setAmount(jsonresponse
									.getString("amount"));
						if (jsonresponse.has("paypal_id"))
							resultData.setPaypalId(jsonresponse
									.getString("paypal_id"));
						
						if (jsonresponse.has("reason"))
							resultData.setReason(jsonresponse
									.getString("reason"));
						if (jsonresponse.has("billing_add1"))
							resultData.setTrueAddress(jsonresponse
									.getString("billing_add1")
									+ ", "
									+ jsonresponse.getString("address2")
									+ ", "
									+ jsonresponse.getString("address3")
									+ ", "
									+ jsonresponse.getString("address4")
									+ ", "
									+ jsonresponse.getString("address5"));
					} catch (Exception ex) {
						ex.printStackTrace();
						Log.e("TAGG", "1 >" + ex.getMessage().toString());
					}

				} catch (Exception ex) {
					Log.e("TAGG", "2 >" + ex.getMessage().toString());
				}
			}

		} catch (Exception e) {
			Log.e("ParserException", ">>>" + e.getMessage());
		}
		return resultData;
	}
}
