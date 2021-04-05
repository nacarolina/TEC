package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.MedidaAdm;
import br.com.cobrasin.tabela.Pais;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MedidaAdmDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "medidasadm";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "codigo","descricao"};
	
	public MedidaAdmDAO(Context ctx ) {
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
		
	public List<MedidaAdm> getLista( ){
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
		List<MedidaAdm> medidaadm = new ArrayList<MedidaAdm>();
	
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[1]);
		
		while ( c.moveToNext()) { 	
			
			MedidaAdm medidaadm1 = new MedidaAdm();
			
			
			medidaadm1.setCodigo(c.getString(0));
			medidaadm1.setDescricao(c.getString(1));
			
			medidaadm.add(medidaadm1);
		}
		
		c.close();
		s.close();
		return medidaadm;
	}

	public String ObtemId (String descricao)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select * from medidasadm where descricao = ?" ,new String [] { descricao });
			
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
	
	public String buscaDescMed( String codMed ) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
			//String xcod = codEsp;
			String retorno = "";
			try
			{
				Cursor c = null ;
				
				
				c = s.rawQuery("select * from medidasadm where codigo = ?" ,new String [] { codMed });
				
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
			SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
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

		public void insere(MedidaAdm medidaadm)
		{
			SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
			ContentValues valores = new ContentValues();
			
			valores.put("codigo",medidaadm.getCodigo());
			valores.put("descricao", medidaadm.getDescricao()); 
			
			s.insert(TABELA, null, valores);
			s.close();

		}
		public String ObtemQuantidadeMedidaAmd()
		{
			SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/medidasadm", null, 0);
			//String xcod = codEsp;
			String retorno = "";
			try
			{
				Cursor c = null ;
				
				
				c = s.rawQuery("Select count(0) from medidasadm",null);
				
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
