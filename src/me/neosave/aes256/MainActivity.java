/**
 * Copyright 2014
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.neosave.aes256;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.gmail.cserock.aes256.R;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements OnClickListener {
	
	// key for encrypt
	private static final String ENCRYPT_KEY = "abcdefghijklmnopqrstuvwxyz012345";
	
	// server url
	private static final String SERVER_URL	=  "http://test.com";
	
	// sample text
	private static final String SAMPLE_PLAIN_TEXT	=  "[{" +
														"\"id\":\"2\"," +
														"\"nickname\":\"cserock\"," +
														"\"gender\":\"M\"," +
														"\"age\":\"33\"" +
														"}]";
	
	// tag for log
	private static final String TAG = "aes256";
	
	private EditText plainEditText;
	private TextView resultEncryptView;
	private TextView resultDataView;
	
	private Button encryptButton;
	private Button sendButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		plainEditText = (EditText)this.findViewById(R.id.editTextPlainText);
		plainEditText.setText(SAMPLE_PLAIN_TEXT);
		
		resultEncryptView = (TextView)this.findViewById(R.id.textViewEncryptResult);
		encryptButton = (Button) findViewById(R.id.buttonEncrypt);
		encryptButton.setOnClickListener(this);
    	
    	sendButton = (Button) findViewById(R.id.buttonSend);
    	sendButton.setOnClickListener(this);
    	resultDataView = (TextView)this.findViewById(R.id.textViewResult);
	}
	
	/**
	* encrypt
	* @param null
	*/
	public void encrypt() {
		
		String plainText = plainEditText.getText().toString();
		Log.i(TAG, "plainText : " + plainText);
		String encryptedText = null;
		
		try {
			encryptedText = AES256Cipher.AES_Encode(plainText, ENCRYPT_KEY);
			encryptedText = URLEncoder.encode(encryptedText, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        Log.i(TAG, "encryptedText : " + encryptedText);
        resultEncryptView.setText(encryptedText);
        return;
	}
	
	/**
	* send to server
	* @param null
	*/
	public void send() {
		try {
			String data = resultEncryptView.getText().toString().trim();
			
			if(data.equals("")){
				Toast toast = Toast.makeText(getApplicationContext(), "data to send is null.", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
				return;
			}
			
			new connectToServerTask().execute(SERVER_URL+"/aes_test.php?token="+ data);
        } catch(Exception e){
        	e.printStackTrace();
        }
        return;
	}
	
	/**
	* button click event
	* @param View v
	*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonEncrypt:
			resultEncryptView.setText("");
			encrypt();
			break;
		
		case R.id.buttonSend:
			resultDataView.setText("");
			send();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	* connect to server
	* @param params request url with data
	*/
	private class connectToServerTask extends AsyncTask<String, Void, String>{
		 
		@Override
		protected String doInBackground(String... params) {
		 
			StringBuilder result = new StringBuilder();
 	       	try{
 	       		URL url = new URL(params[0]);
 	           
 	       		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
 	           
 	       		if(conn!=null){
 	       			conn.setConnectTimeout(10000);
 	       			conn.setUseCaches(false);
 	               
 	       			if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
 	       				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
 	       				String line;
 	       				while ((line = br.readLine()) != null) {
 	       					result.append(line);
 	       				}
 	       				br.close();
 	       			}
 	       			conn.disconnect();
 	       		}
 	       	} catch(Exception ex){
 	       		ex.printStackTrace();
 	       	}
 	       	return result.toString();
		}
	 
	 	@Override
        protected void onPostExecute(String result) {
	 		Log.i(TAG, "resultText from server: " + result);
	 		resultDataView.setText(result);
	 	}
	}
}