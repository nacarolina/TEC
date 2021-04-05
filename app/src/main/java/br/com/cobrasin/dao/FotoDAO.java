package br.com.cobrasin.dao;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.Utilitarios;
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
import android.os.Environment;
import android.util.Log;

public class FotoDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "aitfoto";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "id","idait","imagem"};

	
	public FotoDAO(Context ctx ) {
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
		sb.append("imagem blob );");		
		
		db.execSQL(sb.toString());
	}

	
	
	public void gravaFoto(long idait,byte imagem[] )
	{

		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/aitfoto", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("idait", idait);
		valores.put("imagem", imagem);
		s.insert(TABELA, null, valores);
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
	

	// limpa todos os registro do ait 
	public void delete( long idait )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/aitfoto", null, 0);
		String xidait = String.valueOf(idait);
		s.delete(TABELA, "idait=?", new String[] { xidait  });
		s.close();
	}
	
	public Cursor getImagens(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/aitfoto", null, 0);
		String xidAit = String.valueOf(idAit);
		
		Cursor c = s.query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, null);
		//s.close();
		return c;
		
	}
	
	
	
	public int getQtde(long idAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/aitfoto", null, 0);
		String xidAit = String.valueOf(idAit);
		
		int qtde = 0 ; 
		Cursor c = s.query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, null);
		
		c.moveToFirst();
		
		qtde = c.getCount();
		
		c.close();
		s.close();
		return qtde;
		
	}
	
}
