package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.Tipo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

public class LogradouroDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "logradouro";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "CODIGO","DESCRICAO"};
	
	public LogradouroDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(CODIGO TEXT, ");
		sb.append("DESCRICAO TEXT ,");
		
		
		//db.execSQL(sb.toString());
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE IF EXISTS  + TABELA");
		db.execSQL(sb.toString());
		onCreate(db);
		
	}
	// bus o endereco selecionado pelo codigo
	public String buscaDescLog( String codLoc ) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/logradouro", null, 0);
		String xcod = codLoc;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select * from logradouro where codigo = ?" ,new String [] { codLoc });
			
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
	
	public String ObtemQuantidadeLogradouro()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/logradouro", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from logradouro",null);
			
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
	
	
	public List<Logradouro> getLista( String  sbusca){
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/logradouro", null, 0);
		List<Logradouro> logradouro = new ArrayList<Logradouro>();
	
		String busca = "%" + sbusca + "%";
		
		try
		{
			
			//Cursor c = getWritableDatabase().query(TABELA, COLS, null, null, null, null, COLS[0]);
			Cursor c = s.rawQuery("Select * from logradouro where DESCRICAO LIKE  ? ", new String[] {busca});
			
			while ( c.moveToNext()) {
				
				Logradouro logradouro1 = new Logradouro();
				
				logradouro1.setCodigo(c.getString(0));
				logradouro1.setDescricao(c.getString(1));
							
				logradouro.add(logradouro1);
			}
			
			c.close();
		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return logradouro;
	}
	
	// limpa todos os registro de logradouro 
	public void delete()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/logradouro", null, 0);
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

	public void insere(Logradouro logradouro)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/logradouro", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",logradouro.getCodigo());
		valores.put("descricao", logradouro.getDescricao()); 
		
		s.insert(TABELA, null, valores);
        s.close();
	}

}
