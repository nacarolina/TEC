package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Especie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EspecieDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "especie";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "codigo","descricao"};
	
	public EspecieDAO(Context ctx ) {
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
		
	public List<Especie> getLista( ){
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/especie", null, 0);
		List<Especie> especie = new ArrayList<Especie>();
	
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[1]);
		
		while ( c.moveToNext()) { 	
			
			Especie especie1 = new Especie();
			
			especie1.setCodigo(c.getString(0));
			especie1.setDescricao(c.getString(1));
			
			especie.add(especie1);
		}
		
		c.close();
		s.close();
		return especie;
	}

	public String buscaDescEsp( String codEsp ) {
			
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/especie", null, 0);
			//String xcod = codEsp;
			String retorno = "";
			try
			{
				Cursor c = null ;
				
				
				c = s.rawQuery("select * from especie where codigo = ?" ,new String [] { codEsp });
				
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
		
	// limpa todos os registro de especie 
	public void delete()
	{

		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/especie", null, 0);
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

	public void insere(Especie especiex)
	{

		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/especie", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",especiex.getCodigo());
		valores.put("descricao", especiex.getDescricao()); 
		
		s.insert(TABELA, null, valores);
		s.close();

	}
	public String ObtemQuantidadeEspecie()
	{

		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/especie", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from especie",null);
			
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
