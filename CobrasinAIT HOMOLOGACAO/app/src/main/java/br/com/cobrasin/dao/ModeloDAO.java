package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.tabela.Fabricante;
import br.com.cobrasin.tabela.Modelo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class ModeloDAO extends SQLiteOpenHelper{
	
	private static final String TABELA = "Modelo";
	private static final int VERSAO = 1;
	
	public ModeloDAO(Context ctx) {
		super(ctx, TABELA, null , VERSAO );
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
	
	public void ApagaTudo()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		s.execSQL("Delete from Modelo");
		s.close();
	}
	
	public void InsereModelo(Modelo Mo) 
	{	
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			//valores.put("Fabricante",SimpleCrypto.encrypt(info,Fbr.getFabricante()));
			valores.put("IdFabricante",Mo.getIdFabricante());
			valores.put("Modelo",Mo.getModelo());
			valores.put("PBT_Modelo",Mo.getPBT_Modelo());
			valores.put("PBT_Valor",Mo.getPBT_Valor());
			valores.put("CMT",Mo.getCMT());
			valores.put("Observacoes",Mo.getObservacoes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public List<Modelo> GetTodosModelos(String Modelo,String IdFabricante)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		List<Modelo> Lista_Modelo = new ArrayList<Modelo>();
		Cursor c = null;
		Modelo Mo;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Modelo Where Modelo Like  '%"+Modelo+"%' and IdFabricante='"+IdFabricante+"'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Mo = new Modelo(); 
				
				try {
					Mo.setId(c.getString(c.getColumnIndex("Id")));
					Mo.setIdFabricante(c.getInt(c.getColumnIndex("IdFabricante")));
					Mo.setModelo(c.getString(c.getColumnIndex("Modelo")));
                    Mo.setPBT_Modelo(c.getString(c.getColumnIndex("PBT_Modelo")));
                    Mo.setPBT_Valor(c.getString(c.getColumnIndex("PBT_Valor")));
                    Mo.setCMT(c.getString(c.getColumnIndex("CMT")));
                    Mo.setObservacoes(c.getString(c.getColumnIndex("Observacoes")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Modelo.add(Mo);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Modelo; 
	}
	
	public List<Modelo> GetTodosModelosVerificacao()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		List<Modelo> Lista_Modelo = new ArrayList<Modelo>();
		Cursor c = null;
		Modelo Mo;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Modelo", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Mo = new Modelo(); 
				
				try {
					Mo.setId(c.getString(c.getColumnIndex("Id")));
					Mo.setIdFabricante(c.getInt(c.getColumnIndex("IdFabricante")));
					Mo.setModelo(c.getString(c.getColumnIndex("Modelo")));
                    Mo.setPBT_Modelo(c.getString(c.getColumnIndex("PBT_Modelo")));
                    Mo.setPBT_Valor(c.getString(c.getColumnIndex("PBT_Valor")));
                    Mo.setCMT(c.getString(c.getColumnIndex("CMT")));
                    Mo.setObservacoes(c.getString(c.getColumnIndex("Observacoes")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Modelo.add(Mo);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Modelo; 
	}
	
	public Cursor getDetalhesModelo(String Id)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		Cursor c = null;
		try
		{
			
			c = s.rawQuery("SELECT * from Modelo where Id = '"+Id+"'", null);
			
			if ( c.moveToFirst())
			{
				
				return c;
			}

		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return c; 
		
	}
}
