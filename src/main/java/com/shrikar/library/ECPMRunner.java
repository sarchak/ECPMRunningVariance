package com.shrikar.library;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ECPMRunner {

	private class ECPMContainer{
		public String timestamp;
		int total;
		double ecpm;
		double spread;
		double ecpmFloor;
	};

	public class ECPMContainerDeserializer implements JsonDeserializer<ECPMContainer>{

		public ECPMContainer deserialize(JsonElement json, Type arg1,
				JsonDeserializationContext ctx) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			Set<Entry<String, JsonElement>> s = obj.entrySet();
			Iterator<Entry<String, JsonElement>> it = s.iterator();
			ECPMContainer container = new ECPMContainer();
			while(it.hasNext()){
				Entry<String, JsonElement> ent = it.next();
				if(ent.getKey().equals("timestamp")){
					container.timestamp = ent.getValue().getAsString();
				} else if(ent.getKey().equals("total")) {
					container.total = ent.getValue().getAsInt();
				} else if(ent.getKey().equals("ecpm")){
					container.ecpm = ent.getValue().getAsDouble();
				} else if(ent.getKey().equals("spread")) {
					container.spread = ent.getValue().getAsDouble();
				} else if(ent.getKey().equals("ecpmFloor")) {
					container.ecpmFloor = ent.getValue().getAsDouble();
				} 
			}
			return container;
		}

	}

	ECPMStats ecpmStats;
	/**
	 * @param args
	 */
	public ECPMRunner(){
		ecpmStats = new ECPMStats();
	}

	public void getStats() throws ClientProtocolException, IOException{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://labs.metamx.com/now");
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		byte[] tmp = new byte[2048];
		if (entity != null) {
			InputStream instream = entity.getContent();
			int l;

			while ((l = instream.read(tmp)) != -1) {
			}
		}
		Gson gson = new GsonBuilder().registerTypeAdapter(ECPMContainer.class, new ECPMContainerDeserializer()).create();
		ECPMContainer container = gson.fromJson(new String(tmp), ECPMContainer.class);

		ecpmStats.add(container.ecpm);
		System.out.println(container.timestamp + "       " + ecpmStats.sum()+ "    " + ecpmStats.ecpmMean() + "     " + ecpmStats.ecpmVariance() );

	}

	public static void main(String[] args) {
		ECPMRunner runner = new ECPMRunner();

		System.out.println( " TIMESTAMP                           SUM                MEAN              VARIANCE");
		for(int i = 0; i < 600; i++){
			try {

				runner.getStats();
				Thread.sleep(1000);

			} catch (ClientProtocolException e) {
				i--;
			} catch (IOException e) {
				i--;
			} catch (InterruptedException e) {
				i--;
			} catch (JsonParseException e){
				i--;
			}
		}
	}

}
