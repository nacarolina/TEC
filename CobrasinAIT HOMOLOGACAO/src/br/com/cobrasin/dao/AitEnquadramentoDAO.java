package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.AitEnquadramento;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Tipo;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AitEnquadramentoDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "aitenquadramento";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "id","idait","codigo"};

	private String info = "2012ANCOBRA";
	
	public AitEnquadramentoDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append(" (id INTEGER PRIMARY KEY, " );
		sb.append("idait LONG, ");
		sb.append("codigo TEXT );");		
		
		
		db.execSQL(sb.toString());
	}

	public void Insere( long idait , String enquad ) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("idait", idait);
		try {
			valores.put("codigo",SimpleCrypto.encrypt(info,enquad));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public String ObtemQuantidadeEnquadramento()
	{
		//String xcod = codEsp;
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from aitenquadramento",null);
			
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
	
	// limpa todos os enquadramentos  
		public void deleteall( )
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
			getWritableDatabase().delete(TABELA, null, null);
			s.close();
		}
		
		
	// limpa todos os enquadramentos do ait especifico 
	public void delete( long idait )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidait = String.valueOf(idait);
		s.delete(TABELA, "idait=?", new String[] { xidait  });
		s.close();
	}
	
	// exclui 1 unico registro
	public void deletereg( long id)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xid = String.valueOf(id);
		s.delete(TABELA, "id=?", new String[] { xid  });
		s.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS  + TABELA");
		db.execSQL(sb.toString());
		onCreate(db);
	}
	
	// retorna todos os enquadramentos cadastrados
	public  boolean getEnquadramento(long idait,String enquad)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidait = String.valueOf(idait);
		Cursor c = null;
		try
		{
			c = s.rawQuery("SELECT * from aitenquadramento where idait = ?", new String[]{ xidait });

			if ( c.moveToFirst())
			{
				// enquadramento já cadastrado ?
				do
				{
					try {
						if (c.getString(2).contains(SimpleCrypto.encrypt(info, enquad))) return true;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while ( c.moveToNext());
				
			}
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return false; 
		
	}
	
	public List<AitEnquadramento> getLista(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidAit = String.valueOf(idAit);
		
		List<AitEnquadramento> aitenquad = new ArrayList<AitEnquadramento>();
		
		Cursor c = s.query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);
		
		while ( c.moveToNext()) { 	
			
			AitEnquadramento aitenq = new AitEnquadramento(); 
			
			// 0    1     2
			// id idait codigo
			
			aitenq.setId(c.getLong(0));
			aitenq.setIdait(c.getLong(1));
			try {
				aitenq.setCodigo(SimpleCrypto.decrypt(info, c.getString(2)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			aitenquad.add(aitenq);
		}
		
		c.close();
		s.close();
		return aitenquad;
		
	}
	
	
	//utlizado pela classe Sincronismo.java
	public List<AitEnquadramento> getLista2(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidAit = String.valueOf(idAit);
		
		List<AitEnquadramento> aitenquad = new ArrayList<AitEnquadramento>();
		
		Cursor c = s.query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);
		
		while ( c.moveToNext()) { 	
			
			AitEnquadramento aitenq = new AitEnquadramento(); 
			
			// 0    1     2
			// id idait codigo
			
			aitenq.setId(c.getLong(0));
			aitenq.setIdait(c.getLong(1));
			aitenq.setCodigo(c.getString(2));
			
			//try {
				//aitenq.setCodigo(SimpleCrypto.decrypt(info, c.getString(2)));
			
		//	} catch (Exception e) {
			//	// TODO Auto-generated catch block
			//	e.printStackTrace();
		//	}
						
			aitenquad.add(aitenq);
		}
		
		c.close();
		s.close();
		return aitenquad;
		
	}
	public List<AitEnquadramento> getListaCompleta()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		List<AitEnquadramento> aitenquad = new ArrayList<AitEnquadramento>();
		
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[0]);
		
		while ( c.moveToNext()) { 	
			
			AitEnquadramento aitenq = new AitEnquadramento(); 
			
			// 0    1     2
			// id idait codigo
			
			aitenq.setId(c.getLong(0));
			aitenq.setIdait(c.getLong(1));
			try {
				
				aitenq.setCodigo(SimpleCrypto.decrypt(info, c.getString(2)));
				//aitenq.setCodigo(c.getString(2));
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			aitenquad.add(aitenq);
		}
		
		c.close();
		s.close();
		return aitenquad;
		
	}
	public int qtdeEnquad(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidAit = String.valueOf(idAit);
		
		int qtde = 0 ;
		
		Cursor c = s.query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);
		
		qtde = c.getCount();
		
		c.close();
		s.close();
		return qtde;
		
	}
	
	public Cursor getLista1(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/aitenquadramento", null, 0);
		String xidAit = String.valueOf(idAit);
		
		Cursor c = s.rawQuery("select * from aitenquadramento where idait = ?" ,new String [] { xidAit });
		//s.close();
		return c;
		
	}
}
