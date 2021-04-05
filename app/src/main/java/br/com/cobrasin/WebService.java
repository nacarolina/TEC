package br.com.cobrasin;

import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.util.Xml;

public class WebService {
	public String ExecuteScalarQuery(String Query) {
		String retornoweb = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://sistemas.cobrasin.com.br/JsonWcfFont/JsonWcfService.svc/ExecuteQuery");

			JSONStringer json = new JSONStringer();
			json.object();
			json.key("p");
			json.object();
			json.key("Query").value(Query);
			json.key("Type").value("SCALAR");
			json.endObject();

			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8

			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			retornoweb = EntityUtils.toString(response.getEntity());
			try {
				retornoweb = retornoweb.replace("\"", "").replace("{", "").replace("}", "").replace("ExecuteQueryResult", "").replace(":","");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			/*
			 * mostraMensagem(String.valueOf(statusCode) );
			 * mostraMensagem(retornoweb);
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retornoweb;
	}
	
	public JSONArray ExecuteReaderQuery(String Query) {
		String retornoweb = "";
		JSONArray dt=null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/ExecuteTestJsonQFV");
					//"http://sistemas.cobrasin.com.br:9090/WcfPesqSat/wcfPesqSatService.svc/ExecuteQuery");

			JSONStringer json = new JSONStringer();
			json.object();
			json.key("p");
			json.object();
			json.key("Query").value(Query);
			json.key("Type").value("READER");
			json.endObject();

			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8

			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			retornoweb = EntityUtils.toString(response.getEntity());

			JSONObject obj = new JSONObject(retornoweb);
			
			Object res=obj.get("ExecuteTestJsonQFVResult");
			
			dt=new JSONArray(res.toString());
			
			//JSONObject dr=dt.getJSONObject(0);
			
			//JSONArray array=obj.getJSONArray("ExecuteTestJsonResult");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}
	public JSONArray ExecuteReaderQuery_PrdMultas(String Query) {
		String retornoweb = "";
		JSONArray dt=null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://sistemas.cobrasin.com.br/JsonWcfFont/JsonWcfService.svc/ExecuteTestJson_PrdMultas");
					//"http://sistemas.cobrasin.com.br:9090/WcfPesqSat/wcfPesqSatService.svc/ExecuteQuery");

			JSONStringer json = new JSONStringer();
			json.object();
			json.key("p");
			json.object();
			json.key("Query").value(Query);
			json.key("Type").value("READER");
			json.endObject();

			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8

			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			retornoweb = EntityUtils.toString(response.getEntity());

			JSONObject obj = new JSONObject(retornoweb);
			
			Object res=obj.get("ExecuteTestJson_PrdMultasResult");
			
			dt=new JSONArray(res.toString());
			
			//JSONObject dr=dt.getJSONObject(0);
			
			//JSONArray array=obj.getJSONArray("ExecuteTestJsonResult");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}
	
	public boolean ExecuteNonQuery(String Query) {
		boolean Consiguiu = false;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/ExecuteQuery");

			JSONStringer json = new JSONStringer();
			json.object();
			json.key("p");
			json.object();
			json.key("Query").value(Query);
			json.key("Type").value("NONQUERY");
			json.endObject();

			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8

			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			String retornoweb = EntityUtils.toString(response.getEntity());
			Consiguiu = true;
			/*
			 * mostraMensagem(String.valueOf(statusCode) );
			 * mostraMensagem(retornoweb);
			 */
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Consiguiu = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Consiguiu = false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Consiguiu = false;
		}

		return Consiguiu;
	}
}
