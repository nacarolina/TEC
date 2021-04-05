package br.com.cobrasin.dao;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



import br.com.cobrasin.CobrasinAitActivity;
import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.Utilitarios;
import br.com.cobrasin.tabela.Logs;
import br.com.cobrasin.tabela.Agente;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
//import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
//import android.os.UserHandle;
import android.util.Log;
import android.view.Display;



public class LogDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "logs";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "id","orgao","agente","pda","datahora","status","operacao"};
	
	public LogDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(id Long, ");
		sb.append("(orgao TEXT, ");
		sb.append("agente TEXT, ");
		sb.append("pda TEXT, ");
		sb.append("datahora TEXT,");
		sb.append("status T" +
				"EXT, ");
		sb.append("operacao TEXT");
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
	// grava log
	public void gravalog(String status,String operacao,String orgao,String pda,String agente,Context ctx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		//ParametroDAO pardao = new ParametroDAO(ctx);		
		ContentValues valores = new ContentValues();
//        Cursor par = pardao.getParametros();
		
        //Cursor c = pardao.getParametros();
		
		//c.moveToFirst();
		
		try 
		{
			valores.put("orgao",orgao);
			valores.put("pda",pda);
		} 
		catch (Exception e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		valores.put("agente",agente);
		valores.put("datahora",Utilitarios.getDataHora(1));
		valores.put("status",status);
		valores.put("operacao",operacao);
		
		
		Cursor c;
		String id="";
		
		try
		{
			c =  s.rawQuery("select ifnull(max(id),0)+1 idmax from logs ",null);
			
			while ( c.moveToNext()) 
			{ 	
				id=c.getString(0);
			}
			
			 c.close();
		}
		catch (SQLiteException e)
		{
		}
		
		
		valores.put("id",id);
			
		s.insert(TABELA, null, valores);
        s.close();
		
	}
	
	
	public List<Logs> getLogs()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		List<Logs> logs = new ArrayList<Logs>();
		
		Cursor c =  s.rawQuery("select id,orgao,agente,pda,datahora,status,operacao from logs where operacao <> 'Login' and operacao <> 'Logoff'",null);
		
		while ( c.moveToNext()) 
		{ 	
			
			Logs logss = new Logs(); 
			
			logss.setId(c.getLong(0));
			logss.setOrgao(c.getString(1));
			logss.setAgente(c.getString(2));
			logss.setPda(c.getString(3));
			logss.setDataHora(c.getString(4));
			logss.setStatus(c.getString(5));
			logss.setOperacao(c.getString(6));
			logs.add(logss);
		}
		
		c.close();
		s.close();
		return logs;
		
	}
	public List<Logs> getLogsImpressao()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		List<Logs> logs = new ArrayList<Logs>();
		
		Cursor c =  s.rawQuery("select id,orgao,agente,pda,datahora,status,operacao from logs where status Like '%Gerou Impressão AIT - %'",null);
		
		while ( c.moveToNext()) 
		{ 	
			
			Logs logss = new Logs(); 
			
			logss.setId(c.getLong(0));
			logss.setOrgao(c.getString(1));
			logss.setAgente(c.getString(2));
			logss.setPda(c.getString(3));
			logss.setDataHora(c.getString(4));
			logss.setStatus(c.getString(5));
			logss.setOperacao(c.getString(6));
			logs.add(logss);
		}
		
		c.close();
		s.close();
		return logs;
		
	}
	
	
	public List<Logs> getLogsLogin()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		List<Logs> logs = new ArrayList<Logs>();
		
		Cursor c =  s.rawQuery("select id,orgao,agente,pda,datahora,status,operacao from logs where operacao='Login' or operacao='Logoff'",null);
		
		while ( c.moveToNext()) 
		{ 	
			
			Logs logss = new Logs(); 
			
			logss.setId(c.getLong(0));
			logss.setOrgao(c.getString(1));
			logss.setAgente(c.getString(2));
			logss.setPda(c.getString(3));
			logss.setDataHora(c.getString(4));
			logss.setStatus(c.getString(5));
			logss.setOperacao(c.getString(6));
			logs.add(logss);
		}
		
		c.close();
		s.close();
		return logs;
		
	}
	
	
	public List<Logs> getLogsLogoff()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		List<Logs> logs = new ArrayList<Logs>();
		
		Cursor c =  s.rawQuery("select id,orgao,agente,pda,datahora,status,operacao from logs where operacao='Logoff'",null);
		
		while ( c.moveToNext()) 
		{ 	
			
			Logs logss = new Logs(); 
			
			logss.setId(c.getLong(0));
			logss.setOrgao(c.getString(1));
			logss.setAgente(c.getString(2));
			logss.setPda(c.getString(3));
			logss.setDataHora(c.getString(4));
			logss.setStatus(c.getString(5));
			logss.setOperacao(c.getString(6));
			logs.add(logss);
		}
		
		c.close();
		s.close();
		return logs;
		
	}
	
	public  Cursor ObtemLogs()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
		Cursor c = null;
		try
		{
			c = s.rawQuery("SELECT * from logs", null);
					
			c.moveToNext();
				
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return c; 
		
	}
	
	// Deleta o log pelo id 
		public void DeleteLog(Long id)
		{
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/logs", null, 0);
			String sid = String.valueOf(id);
			//getWritableDatabase().delete(TABELA, "id=?", new String[] { xidait  });
			s.delete(TABELA,"id=?",new String[] {sid} );
			s.close();
		}

}
