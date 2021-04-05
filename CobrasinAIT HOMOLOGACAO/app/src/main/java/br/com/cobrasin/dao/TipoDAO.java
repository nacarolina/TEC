package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Tipo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipoDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "tipo";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "codigo","descricao"};
	
	
	public TipoDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(codigo TEXT, ");
		sb.append(" descricao TEXT);  ");
		
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public List<Tipo> getLista( ){
		
		List<Tipo> tipo = new ArrayList<Tipo>();
	
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/tipo", null, 0);
		
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[1]);
		
		while ( c.moveToNext()) {
			
			Tipo tipo1 = new Tipo();
			
			try {
				
				tipo1.setCodigo(c.getString(0));	
				tipo1.setDescricao(c.getString(1));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			tipo.add(tipo1);
		}
		
		c.close();
		s.close();
		return tipo;
	}

	public String buscaDescTip( String codTip ) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/tipo", null, 0);
		String xcod = codTip;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select * from tipo where codigo = ?" ,new String [] { codTip });
			
			while ( c.moveToNext() )
			{
				retorno  = c.getString(1);
			}
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return retorno; 
		
	}
	
	
	// limpa todos os registro de tipo 
	public void delete()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/tipo", null, 0);
		s.delete(TABELA, null,null);
		s.close();
	}

	public void insere(Tipo tipox)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/tipo", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",tipox.getCodigo());
		valores.put("descricao", tipox.getDescricao()); 
		
		s.insert(TABELA, null, valores);
		s.close();

	}
	public String ObtemQuantidadeTipo()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/tipo", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from tipo",null);
			
			while ( c.moveToNext() )
			{
				retorno = c.getString(0);
			}
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return retorno; 
		
	}
	
}
