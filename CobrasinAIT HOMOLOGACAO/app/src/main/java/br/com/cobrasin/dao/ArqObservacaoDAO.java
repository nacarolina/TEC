package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.ArqObservacao;
import br.com.cobrasin.tabela.Especie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class ArqObservacaoDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "arqobs";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "id","observacao"};
	
	public ArqObservacaoDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append(" (id INTEGER PRIMARY KEY, " );
		sb.append("observacao TEXT); ");
		
		db.execSQL(sb.toString());

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS  + TABELA");
		db.execSQL(sb.toString());
		onCreate(db);
	}
		
	public List<ArqObservacao> getLista( ){
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/arqobs", null, 0);
		List<ArqObservacao> arqobservacoes = new ArrayList<ArqObservacao>();
	
		Cursor c = s.query(TABELA, COLS, null, null, null, null, COLS[1]);
		
		while ( c.moveToNext()) { 	
			
			ArqObservacao arqobs = new ArqObservacao();
			
			arqobs.setId(c.getInt(0));
			arqobs.setDescricao(c.getString(1));
			
			arqobservacoes.add(arqobs);
		}
		
		c.close();
		s.close();
		return arqobservacoes;
	}

	
	public void insere( ArqObservacao arqobs )  // 1 altera , 2 insere
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/arqobs", null, 0);
		ContentValues valores = new ContentValues();
		valores.put("observacao", arqobs.getDescricao());
		s.insert(TABELA, null, valores);
		s.close();
	}
	
	public boolean ExisteObs(String Obs)
	{
		boolean Existe = false;
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/arqobs", null, 0);
		Cursor c = null;
		try {
			c = s.rawQuery("SELECT * from arqobs where observacao = '"+ Obs+"'", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while ( c.moveToNext() )
		{
			try {
		    if(c.getString(c.getColumnIndex("observacao"))==Obs)
		    {
		    	Existe = true;
		    }
		    
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		s.close();
		
		return Existe;
	}
	
	// exclui 1 unico registro
	public void deletereg( long id)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/arqobs", null, 0);
		String xid = String.valueOf(id);
		s.delete(TABELA, "id=?", new String[] { xid  });
		s.close();
	}
}
