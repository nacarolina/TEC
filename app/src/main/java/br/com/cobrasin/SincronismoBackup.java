package br.com.cobrasin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.*;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.AitEnquadramento;
import br.com.cobrasin.tabela.Logs;

//import net.sf.json.*;


public class SincronismoBackup {

	//private String endereco = "http://www.caelum.com.br/mobile?dado=";
	private String endereco ;//=  "http://sistemas.cobrasin.com.br:8080/JSONTeste/";
	private Context context;
	private ProgressDialog progress;
	//private Toast aviso;
	private String  retornoweb = "";
	private String info = Utilitarios.getInfo();
	private Handler handler = new Handler();
	
	private byte buffer []; 

	private JSONObject json1; 
	private JSONArray jsonArray;
	private SincronismoWebTrans sinc;
	
	public SincronismoBackup(Context context){
		
		this.context = context;
		
	}


    private void mostraMensagem( final String mensagem ){
        handler.post(new Runnable() {

            @Override
            public void run() {
                
                AlertDialog.Builder aviso1 = new AlertDialog.Builder(
                        context);
                aviso1.setIcon(android.R.drawable.ic_dialog_alert);
                aviso1.setTitle("Transmissão");
                aviso1.setMessage(mensagem);
                aviso1.setPositiveButton("OK", null);
                aviso1.show();

            }
        });
    }

    
    public boolean txFotoftp(Ait aitx)
    {
    	long idAit = aitx.getId();
    	
    	boolean retorno = true; 
    	int qtdtx = 0 ; 
    	
    	FotoDAO fotodao = new FotoDAO(context);
    	
    	// tem fotos para transmitir ?
    	if (fotodao.getQtde(idAit) > 0)
    	{
    		retorno = false; 
    		
    		ParametroDAO pardao = new ParametroDAO(context);
    	
    		Cursor cpar = pardao.getParametros();
    	
    		FTPClient ftp = new FTPClient();
	    	
            //Faz a conexão com o servidor ftp
            try {
				
            	//ftp.connect(cpar.getString(cpar.getColumnIndex("servidorftp")));
            	ftp.connect("sistemas.cobrasin.com.br");
	            ftp.login(cpar.getString(cpar.getColumnIndex("usuarioftp")),cpar.getString(cpar.getColumnIndex("senhaftp")));  
	
	            // tenta criar a pasta fotos
	            try
	            {	
	            	ftp.makeDirectory("fotos");
	            }
	            catch(Exception e)
	            {
	            	
	            }
	            
	            // tenta subir todas as fotos
				Cursor cfotos = fotodao.getImagens(idAit);
				
				while ( cfotos.moveToNext())
				{
					
					
					// foto = orgao+serieait+ait
					String arquivofoto = null;
					try {
						
						/*arquivofoto = Environment.getExternalStorageDirectory()
						+ "/imagens/" +
						cpar.getString(cpar.getColumnIndex("orgaoautuador")) + "_" +
						cpar.getString(cpar.getColumnIndex("serieait")) + 
						SimpleCrypto.decrypt(info, aitx.getAit()) + 
						".jpg";*/
						
						arquivofoto = Environment.getExternalStorageDirectory()
								+ "/imagens/saida.jpg"; 
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String arquivofoto1 = null;
					try {
						
						arquivofoto1 = "fotos/" + 
						cpar.getString(cpar.getColumnIndex("orgaoautuador")) + "_" +
								cpar.getString(cpar.getColumnIndex("serieait")) + 
								SimpleCrypto.decrypt(info,aitx.getAit()) + 
								".jpg";
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					buffer = cfotos.getBlob((cfotos.getColumnIndex("imagem")));
					
					this.StoreByteImage(context, buffer, 90, "saida.jpg");
					
					FileInputStream fis = new FileInputStream(arquivofoto);
				
					//FTPClient ftp = new FTPClient();

					//ftp.connect("sistemas.cobrasin.com.br");
		            //ftp.login(cpar.getString(cpar.getColumnIndex("usuarioftp")),cpar.getString(cpar.getColumnIndex("senhaftp")));  
		
		            // tenta criar a pasta fotos
		            //try
		            //{	
		            //	ftp.makeDirectory("fotos");
		            //}
		            //catch(Exception e)
		            //{
		            	
		            //}
					
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					
					if (ftp.storeFile(arquivofoto1, fis)) qtdtx++ ;
					
					fis.close();
				
					
					//try
		            //{
						//ftp.logout();
		         	  // ftp.disconnect();
		         	 
		            //}
		            //catch(Exception e)
		            //{
		            	//
		            //}
		            
					
					
				}
				
				cfotos.close();
				
				// transmitiu todas ?
				if ( qtdtx == fotodao.getQtde(idAit))  retorno = true ;
				
	            try
	            {
	         	   ftp.disconnect();
	            }
	            catch(Exception e)
	            {
	         	   
	            }
	            
	            // logout
	            ftp.logout();
	            
	            //Disconecta do ftp  
	            ftp.disconnect();  

	            
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
             
          
    		
    		cpar.close();
    				
    		pardao.close();
    	}
    	
    	fotodao.close();
    	
		return retorno;
    }
    
    
    public static boolean StoreByteImage(Context mContext, byte[] imageData,
			int quality, String expName) {

        File sdImageMainDirectory = new File(Environment.getExternalStorageDirectory()
				+ "/imagens" );
        
		FileOutputStream fileOutputStream = null;
		
		//String nameFile = expName;
		try {

			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 1;
			
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length,options);

			
			fileOutputStream = new FileOutputStream(
					sdImageMainDirectory.toString() + "/" + expName);
			
			
			//fileOutputStream = new FileOutputStream(
				//	expName);
  
			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}


	public void  sincronizar() {
		
		progress = ProgressDialog.show(context, "Aguarde..." , "Enviando dados para a web!!!",true);
		//aviso = Toast.makeText(context, "Todos aits enviados com sucesso!", Toast.LENGTH_LONG);
		
		
		new Thread( new Runnable() {
			
			boolean errotx = false;
			
			public void run() {
				// TODO Auto-generated method stub
				
				
				try { 
						Thread.sleep(1000);
						
					}catch (InterruptedException e){
						throw new RuntimeException(e);
					}
				
				
				// cria JSON do parametro 
				ParametroDAO pardao = new ParametroDAO(context);
				
				Cursor c = pardao.getParametros();
				
				c.moveToFirst();
								
				endereco = c.getString(c.getColumnIndex("servidorweb"));
				
				endereco =  "http://sistemas.cobrasin.com.br/JSONTeste/";
				
				JSONObject obj = new JSONObject();
				
				try {
					
					obj.put("orgaoautuador", c.getString(c.getColumnIndex("orgaoautuador")));
					obj.put("serieait", c.getString(c.getColumnIndex("serieait")));
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				c.close();
				
				pardao.close();
				
				
				// cria JSON dos AITS
				AitDAO dao = new AitDAO(context);
				List<Ait> lista = dao.getListaCompleta();
				dao.close();
				
				// percorre cada elemento da lista 
				 
				
				//Iterator<Ait> i = lista.iterator();
				
				int nx = 0 ;
				for ( nx = 0 ; nx < lista.size() ; nx++) // while ( i.hasNext())
				{
					
					
					Ait ait1 = lista.get(nx);
					
					
					long idAit = ait1.getId(); 
					
					JSONStringer j = new JSONStringer();
	
					
					try {
						
						//j.array();
						j.object().key("ait").array();
						
						//for ( Ait  ait : lista1) {
						//	j.value(ait.toJSON());
					//	}
						
						j.value(ait1.toJSON());
						
						j.endArray().endObject();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					// cria JSON dos Enquadramentos
					AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(context);
					 
					//List<AitEnquadramento> listaenq = aitenq.getListaCompleta();
					List<AitEnquadramento> listaenq = aitenq.getLista2(idAit);
					aitenq.close();
					
					JSONStringer j1 = new JSONStringer();
					
					try {
						
						//j.array();
						j1.object().key("enquadramento").array();
						
						for ( AitEnquadramento  aitenq1 : listaenq) {
							j1.value(aitenq1.toJSON());
						}
						
						
						j1.endArray().endObject();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					HttpClient  httpclient = new DefaultHttpClient();
					
					HttpPost post = new HttpPost(endereco);
				
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					
					nvps = new ArrayList<NameValuePair>(); 
					
					nvps.add(new BasicNameValuePair("varait", j.toString())); 
					
					nvps.add(new BasicNameValuePair("varenq", j1.toString()));
					
					nvps.add(new BasicNameValuePair("varpar", obj.toString()));
					try {
						post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					
					try {
						
						//HttpResponse response = httpclient.execute(httpget);
						HttpResponse response = httpclient.execute(post);

						retornoweb  = EntityUtils.toString(response.getEntity());

						
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
					
					AitDAO aitdao = new AitDAO(context);
					
					// verifica se insercao foi correta 
					if (retornoweb.contains("OK INSERCAO")) 
					{
						//*******************************************
						// 07.01.2012
						// Transmite a foto para o Servidor de FTP
						//*******************************************

						if (txFotoftp(ait1))
						{
							// 	atualiza FLAG para transmitido
							// 	atualiza campo TRANSMITIDO
							//aitdao.atualizaTx(idAit,true);
						}
						else
						{
							errotx = true;
							// atualiza somente o campo TRANSMITIDO
							//aitdao.atualizaTx(idAit,false);
						}
					}
					else
					{
						errotx = true;
						// atualiza somente o campo TRANSMITIDO
						//aitdao.atualizaTx(idAit,false);
					}
					
					aitdao.close();
				}
				
				
				//---------------------------------------------
				// 09.03.2012
				// Cria array JSON dos logs  
				//---------------------------------------------
				/*
				LogDAO logdao = new LogDAO(context);
				List<Logs>lista1 = logdao.getLista();
				JSONStringer j = new JSONStringer();
				
				try {
					
					j.object().key("logs").array();
					
					for ( Logs  logs : lista1) {
						j.value(logs.toJSON());
					}
					j.endArray().endObject();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			
				HttpClient httpclient = new DefaultHttpClient();
			
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				
				HttpPost post = new HttpPost(endereco);
			
				nvps = new ArrayList<NameValuePair>(); 
				
				//nvps.add(new BasicNameValuePair("varlog", j.toString())); 
				
				try {
					post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
				try {
					
					//HttpResponse response = httpclient.execute(httpget);
					HttpResponse response = httpclient.execute(post);

					retornoweb  = EntityUtils.toString(response.getEntity());

					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// verifica se insercao foi correta 
				//if (retornoweb.contains("OK INSERCAO")) 
				//{
					// limpa todos logs
				///	logdao.limpaReg();
				//}
				
				//logdao.close();
				
				//************************************************************
				// 07.02.2011 -> limpa todos os registros com mais de 24 horas
				//************************************************************
				AitDAO aitdao = new AitDAO(context);
				lista = aitdao.getListaTransmitida();
				
				FotoDAO fotodao = new FotoDAO(context);
				
				nx = 0 ;
				
				for ( nx = 0 ; nx < lista.size() ; nx++) 
				{
					aitdao.delete(lista.get(nx).getId());
					fotodao.delete(lista.get(nx).getId());
				}
				
				dao.close();
				fotodao.close();
				
				//*******************************
				// Carrega as tabelas do Webtrans
				//*******************************
				sinc = new SincronismoWebTrans(context,"2");
				
				progress.dismiss();
				
				
				if ( errotx )
				{
					mostraMensagem("Não consegui transmitir todos os AIT's");
				}
				else
				{
					mostraMensagem("Todos aits foram enviados com sucesso!");
				}
			}
		})  .start();
		
		
	}
}
