package com.example.bmob;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject {
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	private String feedback;

}
