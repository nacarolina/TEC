package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.Pais;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PaisDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "pais";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "codigo","descricao"};
	
	public PaisDAO(Context ctx ) {
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
		
	public List<Pais> getLista( ){
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/pais", null, 0);
		List<Pais> pais = new ArrayList<Pais>();
	
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[1]);
		
		while ( c.moveToNext()) { 	
			
			Pais pais1 = new Pais();
			
			pais1.setCodigo(c.getString(0));
			pais1.setDescricao(c.getString(1));
			
			pais.add(pais1);
		}
		
		c.close();
		s.close();
		return pais;
	}

	public String buscaDescPais( String codPais ) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/pais", null, 0);
			//String xcod = codEsp;
			String retorno = "";
			try
			{
				Cursor c = null ;
				
				
				c = s.rawQuery("select * from pais where codigo = ?" ,new String [] { codPais });
				
				while ( c.moveToNext() )
				{
					retorno = c.getString(1);
				}
						
			}
			catch ( SQLiteException e)
			{
				Log.e("Erro=",e.getMessage());
			}
			s.close();
			return retorno; 
			
	}
	
 
	public void delete()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/pais", null, 0);
		try
		{
			s.delete(TABELA, null,null);
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
	}

	public void insere(Pais paisz)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/pais", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",paisz.getCodigo());
		valores.put("descricao", paisz.getDescricao()); 
		
		s.insert(TABELA, null, valores);
		s.close();

	}
	public String ObtemQuantidadePais()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/pais", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from pais",null);
			
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
