package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.Utilitarios;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.tabela.Tipo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UrlsWebTransDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "urlswebtrans";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "tipo","url","leu"};
	
	public UrlsWebTransDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(tipo TEXT, ");
		sb.append("url TEXT, ");
		sb.append("leu TEXT); ");
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
	public  String geturl(String tipo)
	{
		String url = "" ;
		Cursor c = null;
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/urlswebtrans", null, 0);
		
		try {
			
			tipo = SimpleCrypto.encrypt(Utilitarios.getInfo(), tipo);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try
		{
			c = s.rawQuery("select * from urlswebtrans where tipo = '"+tipo+"'" ,null);
		//	c = getReadableDatabase().rawQuery("select * from urlswebtrans where tipo = ?" ,new String [] { tipo });
				
			while ( c.moveToNext() )
			{
				url  = c.getString(c.getColumnIndex("url"));
				
				try {
					
					url = SimpleCrypto.decrypt(Utilitarios.getInfo(),url ) ;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		
		s.close();
		return url; 
		
	}


	//************************************************************
	// 29.06.2012
	// Recadastra todas as URL criptografando
	//************************************************************
	public void insere()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/urlswebtrans", null, 0);
		try 
		{

			Cursor c = s.rawQuery("select * from urlswebtrans" ,null );
						
			String aUrl[] = new String[18];
			String aTipo[] = new String[18];
			
			int nx = 0 ; 
			while ( c.moveToNext() )
			{
				// guarda
				aUrl[nx] = c.getString(1);
				aTipo[nx] =  c.getString(0);
				nx++;
			}
			
			c.close();
			
			// limpa todas as URL
			s.delete(TABELA, null,null);
		
			for (  nx = 0 ; nx < 18 ; nx++)
			{
				
			
				ContentValues valores = new ContentValues();
			
				try 	
				{
					
					valores.put("tipo",aTipo[nx]);
					valores.put("url", aUrl[nx]); 
					valores.put("leu", "N");
				
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				s.insert(TABELA, null, valores);
				
			}
			
		}
		catch ( Exception ex)
		{
			
		}
		s.close();
	}	
		
		
	

}
