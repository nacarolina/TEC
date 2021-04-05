package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.tabela.Eixo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EixoDAO extends SQLiteOpenHelper{
	
	private static final String TABELA = "Eixo";
	private static final int VERSAO = 1;
	
	public EixoDAO(Context ctx) {
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
	
	public void InsereEixo(Eixo Ei) 
	{	
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("Eixo_Titulo",Ei.getEixo_Titulo());
			valores.put("Eixo_Desc",Ei.getEixo_Desc());
			valores.put("Eixo_Peso",Ei.getEixo_Peso());
			valores.put("Foto",Ei.getFoto());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public void ApagaTudo()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		s.execSQL("Delete from Eixo");
		s.close();
	}
	
	public Cursor getDetalhesEixo(String Id)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		Cursor c = null;
		try
		{
			
			c = s.rawQuery("SELECT * from Eixo where Id = '"+Id+"'", null);
			
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
	
	public List<Eixo> GetTodosEixos(String Eixo_Titulo)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		List<Eixo> Lista_Eixo = new ArrayList<Eixo>();
		Cursor c = null;
		Eixo Ei;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Eixo Where Eixo_Titulo Like  '%"+Eixo_Titulo+"%'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Ei = new Eixo(); 
				
				try {
					Ei.setId(c.getString(c.getColumnIndex("Id")));
					Ei.setEixo_Titulo(c.getString(c.getColumnIndex("Eixo_Titulo")));
					Ei.setEixo_Desc(c.getString(c.getColumnIndex("Eixo_Desc")));
					Ei.setEixo_Peso(c.getString(c.getColumnIndex("Eixo_Peso")));
					Ei.setFoto(c.getBlob(c.getColumnIndex("Foto")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Eixo.add(Ei);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Eixo; 
	}
	
	public List<Eixo> GetEixosSelecionado(String Ids)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		List<Eixo> Lista_Eixo = new ArrayList<Eixo>();
		Cursor c = null;
		Eixo Ei;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Eixo Where Id in ("+Ids+")", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Ei = new Eixo(); 
				
				try {
					Ei.setId(c.getString(c.getColumnIndex("Id")));
					Ei.setEixo_Titulo(c.getString(c.getColumnIndex("Eixo_Titulo")));
					Ei.setEixo_Desc(c.getString(c.getColumnIndex("Eixo_Desc")));
					Ei.setEixo_Peso(c.getString(c.getColumnIndex("Eixo_Peso")));
					Ei.setFoto(c.getBlob(c.getColumnIndex("Foto")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Eixo.add(Ei);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Eixo; 
	}
	
	public int CapacidadeEixoSomado(String Ids)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/QFV", null, 0);
		int ValorTotal = 0;
		Cursor c = null;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Eixo Where Id in ("+Ids+")", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{			
				try {
					int Valor = Integer.parseInt(c.getString(c.getColumnIndex("Eixo_Peso")).replace("kg", "").trim().replace(".", ""));
					ValorTotal = ValorTotal + Valor;
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
		return ValorTotal; 
	}
}