package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.tabela.Caracterizacao;
import br.com.cobrasin.tabela.Modelo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class CaracterizacaoDAO extends SQLiteOpenHelper{
	
	private static final String TABELA = "Caracterizacao";
	private static final int VERSAO = 1;
	
	public CaracterizacaoDAO(Context ctx) {
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
	
	public void InsereCaracterizacao(Caracterizacao Ca) 
	{	
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("Silhueta_Foto",Ca.getSilhueta_Foto());
			valores.put("Grupo_N_Eixos",Ca.getGrupo_N_Eixos());
			valores.put("PBT_PBTC",Ca.getPBT_PBTC());
			valores.put("Caracterizacao_Titulo",Ca.getCaracterizacao_Titulo());
			valores.put("Caracterizacao_Desc",Ca.getCaracterizacao_Desc());
			valores.put("Classe",Ca.getClasse());
			valores.put("Codigo",Ca.getCodigo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public void ApagaTudo()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		s.execSQL("Delete from Caracterizacao");
		s.close();
	}
	
	public Cursor getDetalhesCaracterizacao(String Id)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		Cursor c = null;
		try
		{
			
			c = s.rawQuery("SELECT * from Caracterizacao where Id = '"+Id+"'", null);
			
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
	
	public List<Caracterizacao> GetTodasCaracterizacao(String Caracterizacao_Titulo)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		List<Caracterizacao> Lista_Caracte = new ArrayList<Caracterizacao>();
		Cursor c = null;
		Caracterizacao Ca;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Caracterizacao Where Caracterizacao_Titulo Like  '%"+Caracterizacao_Titulo+"%'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Ca = new Caracterizacao(); 
				
				try {
					Ca.setId(c.getString(c.getColumnIndex("Id")));
					Ca.setSilhueta_Foto(c.getBlob(c.getColumnIndex("Silhueta_Foto")));
					Ca.setGrupo_N_Eixos(c.getString(c.getColumnIndex("Grupo_N_Eixos")));
					Ca.setPBT_PBTC(c.getString(c.getColumnIndex("PBT_PBTC")));
					Ca.setCaracterizacao_Titulo(c.getString(c.getColumnIndex("Caracterizacao_Titulo")));
					Ca.setCaracterizacao_Desc(c.getString(c.getColumnIndex("Caracterizacao_Desc")));
					Ca.setClasse(c.getString(c.getColumnIndex("Classe")));
					Ca.setCodigo(c.getString(c.getColumnIndex("Codigo")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Caracte.add(Ca);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Caracte; 
	}

}
