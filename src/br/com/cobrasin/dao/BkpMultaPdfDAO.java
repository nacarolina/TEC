package br.com.cobrasin.dao;

import br.com.cobrasin.SimpleCrypto;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BkpMultaPdfDAO  extends SQLiteOpenHelper{
	
	private String info = "2012ANCOBRA";
	private static final String TABELA = "bkpmultapdf";
	private static final int VERSAO = 1;
	
	public BkpMultaPdfDAO(Context context) {
		super(context, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	
	public void SalvaMulta(String ait,String Multa) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/bkpmultapdf", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("ait", SimpleCrypto.encrypt(info,ait));
			valores.put("multa",SimpleCrypto.encrypt(info,Multa));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
}
