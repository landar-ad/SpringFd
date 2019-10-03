package ru.landar.spring;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringFdApplicationTests {
	@Test
	public void test() throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/address");
		//HttpPost httpPost = new HttpPost("https://cleaner.dadata.ru/api/v1/clean/address");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Authorization", "Token 7b44af0c95037342460a83a7b219a55f3eb684d8");
		httpPost.addHeader("X-Secret", "9b45d1d3fddc915cf71b16811a1fdf4613a756f8");
		String r = "{ \"query\": \"7bd8f91c-e8cc-4e61-b524-fcb7061abc96\" }";
		//String r = "[ \"Реутов Юбилейный 37 76\" ]";
		httpPost.setEntity(new StringEntity(r, Charset.forName("UTF-8")));
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
		    HttpEntity entity = response.getEntity();
	    	InputStream is = entity.getContent();
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	JsonNode json = objectMapper.readTree(is);
	    	//String addr = json.findValue("result").asText();
	    	//String fias_id = json.findValue("fias_id").asText();
		    EntityUtils.consume(entity);
		} 
		catch (Exception e) {
		}
		finally {
		    response.close();
		}
	}
}