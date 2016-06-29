package com.example.bmob;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private EditText mName,mFeedback,mQuery;
	private Button mSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bmob.initialize(this, "583a9d430646eec1cd452cb17a71f42d");
		BmobInstallation.getCurrentInstallation(this).save();;
		BmobPush.startWork(this, "583a9d430646eec1cd452cb17a71f42d");
		
		mName = (EditText) findViewById(R.id.name);
		mFeedback = (EditText) findViewById(R.id.feedback);
		mQuery = (EditText) findViewById(R.id.et_query);
	}
    public void submit(View view){
    	String name = mName.getText().toString();
    	String feedback = mFeedback.getText().toString();
    	if(name.equals("")||feedback.equals("")){
    		return;
    	}
    	Feedback feedbackObj = new Feedback();
    	feedbackObj.setName(name);
    	feedbackObj.setFeedback(feedback);
    	feedbackObj.save(MainActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "submit success", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "submit failure", Toast.LENGTH_LONG).show();
			}
		});
    }
    
    public void queryAll(View view){
    	BmobQuery<Feedback> query = new BmobQuery<Feedback>();
    	query.findObjects(MainActivity.this, new FindListener<Feedback>() {

			@Override
			public void onError(int i, String s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<Feedback> feedbacks) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Query");
				String str = "";
				for(Feedback feedback :feedbacks){
					str += feedback.getName()+":"+feedback.getFeedback()+"\n";
				}
				builder.setMessage(str);
				builder.create().show();
			}
		});
    }
    
    public void queryFeedback(View view){
    	String str = mQuery.getText().toString();
    	if(str.equals("")){
    		return;
    	}
    	BmobQuery<Feedback> query = new BmobQuery<Feedback>();
    	query.addWhereEqualTo("name", str);
    	query.findObjects(MainActivity.this, new FindListener<Feedback>() {

			@Override
			public void onError(int i, String s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<Feedback> feedbacks) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Query");
				String str = "";
				for(Feedback feedback :feedbacks){
					str += feedback.getName()+":"+feedback.getFeedback()+"\n";
				}
				builder.setMessage(str);
				builder.create().show();
			}
		});
    }
    
    public void pushAll(View view){
    	BmobPushManager push = new BmobPushManager(MainActivity.this);
    	push.pushMessage("Test");
    }
	
}
