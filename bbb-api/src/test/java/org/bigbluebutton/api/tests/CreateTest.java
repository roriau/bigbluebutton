package org.bigbluebutton.api.tests;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bigbluebutton.api.Create;
import org.bigbluebutton.api.tests.helpers.TestParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class CreateTest {

	private Create create;
	private HttpClient client;
	
	@Before
	public void setUp() throws Exception {
		create = new Create();
		client = new DefaultHttpClient();
	}

	@After
	public void tearDown() throws Exception {
		create = null;
	}
	
	@Test
	public void testQueryMissing(){
		String url = TestParams.OLD_API + "/bigbluebutton/api/create";
		HttpGet request = new HttpGet(url);
		try{
			HttpResponse response = client.execute(request);
			System.out.println(response.toString());
			
			HttpEntity body = response.getEntity();
			if (body == null) Assert.fail();
			
			InputStream in = body.getContent();
			System.out.println(EntityUtils.toString(body));
			
		} catch (Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void testCreate1(){
		
	}

}
