package br.com.cobrasin.dao;

import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.Utilitarios;
import br.com.cobrasin.tabela.Agente;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;
import android.util.Log;

public class AgenteDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "agente";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "CODIGO","NOME","SENHA","LOGIN","ATIVO"};
	private String info = Utilitarios.getInfo();
	public AgenteDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(CODIGO TEXT, ");
		sb.append("NOME TEXT ,");
		sb.append(" SENHA TEXT,  ");
		sb.append(" LOGIN TEXT,  ");
		sb.append(" ATIVO TEXT);  ");
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

	// metodo de pesquisa de Agente -> retorna a senha ou "" ( caso nao exista o agente ) 
	

	public String validaAgente( Editable  editable ) {
		
		String retorno = "";
		String agente = editable.toString();
		
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		//--------------------------------
		// 29.06.2012
		//
		// criptografa matricula do agente
		//--------------------------------
		try {
			agente = SimpleCrypto.encrypt(Utilitarios.getInfo(),agente);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cursor c = null; 
		try
		{
			
		
			//Cursor c = getWritableDatabase().query(TABELA, COLS, "CODIGO = ?", new String[] {agente}, null, null, COLS[0]);
		c =s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
		//	c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
			if ( c.moveToFirst())
			{
				// retorna a senha em MD5
				retorno = c.getString(2);
			}
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		c.close();
		return retorno; 
		
	}
	
	public boolean verificaAgenteDNIT(String agente ) {
		
		boolean DNIT = false;
		
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		//--------------------------------
		// 29.06.2012
		//
		// criptografa matricula do agente
		//--------------------------------
		try {
			agente = SimpleCrypto.encrypt(Utilitarios.getInfo(),agente);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cursor c = null; 
		try
		{
			
		
			//Cursor c = getWritableDatabase().query(TABELA, COLS, "CODIGO = ?", new String[] {agente}, null, null, COLS[0]);
		c =s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
		//	c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
			if ( c.moveToFirst())
			{
				try {
					DNIT = Boolean.parseBoolean(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("DNIT"))));
				} catch (Exception e) {
					// TODO: handle exception
					DNIT = false;
				}
			}
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		c.close();
		return DNIT; 
		
	}
	
	public Cursor GetDadosAgente(String agente ) {
		
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		//--------------------------------
		// 29.06.2012
		//
		// criptografa matricula do agente
		//--------------------------------
		try {
			agente = SimpleCrypto.encrypt(Utilitarios.getInfo(),agente);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cursor c = null; 
		try
		{
			
		
			//Cursor c = getWritableDatabase().query(TABELA, COLS, "CODIGO = ?", new String[] {agente}, null, null, COLS[0]);
		c =s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
		if ( c.moveToFirst())
		{
			String retorno = "XX";
		}
		//	c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		//c.close();
		return c; 
		
	}
	
	//***********************************************************************************************
	// retorno qtde de agentes , indica se a base foi inicializada corretamente ( 1a. inicializacao ) 
	//***********************************************************************************************
	public int qtdeAgentes()
	{
		int retorno = 0 ; 
		Cursor cn = null ;
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		
		try
		{
			cn = s.rawQuery("Select * from AGENTE",null);
			//cn = getReadableDatabase().rawQuery("Select * from AGENTE",null);
			if ( cn.moveToFirst()) retorno = cn.getCount();
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		
		s.close();
		cn.close();
		
		return retorno;
		
	}
	public boolean agenteAtivo( String codagente ) {
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		boolean retorno = false;
 
		//--------------------------------
		// 29.06.2012
		//
		// criptografa matricula do agente
		//--------------------------------
		try {
			codagente = SimpleCrypto.encrypt(Utilitarios.getInfo(),codagente);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Cursor c = null; 
		try
		{
			c = s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {codagente});
			//c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {codagente});
		
			if ( c.moveToFirst())
			{
				String ativo = c.getString(c.getColumnIndex("ATIVO"));
				
				try {
					ativo = SimpleCrypto.decrypt(Utilitarios.getInfo(),ativo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (ativo.equals("S"))	retorno = true;
			}
					
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		
		c.close();
		s.close();
		return retorno; 
		
	}
	
	
	// limpa todos os registro de agente 
	public void delete()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);

		try
		{
		    s.delete(TABELA, null,null);
			//getWritableDatabase().delete(TABELA, null,null);
		}
		catch ( SQLiteException e )
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
	}
		
	public void insere(Agente agentex)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",agentex.getCodigo());
		valores.put("nome", agentex.getNome()); 
		valores.put("senha", agentex.getSenha());
		valores.put("login",agentex.getLogin());
		valores.put("ativo",agentex.getAtivo());
		valores.put("DNIT",agentex.getDNIT());
		
		s.insert(TABELA, null, valores);
		//getWritableDatabase().insert(TABELA, null, valores);
        s.close();
	}
	
	public void altera(Agente agentex)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		ContentValues valores = new ContentValues();
		
		//valores.put("codigo",agentex.getCodigo());
		//valores.put("nome", agentex.getNome()); 
		//valores.put("senha", agentex.getSenha());
		//valores.put("login",agentex.getLogin());
		//valores.put("ativo",agentex.getAtivo());
		valores.put("POSTO",agentex.getPosto());
		valores.put("IdMunicipio",agentex.getIdMunicipio());
		int alterou = s.update(TABELA, valores, "CODIGO=?",new String[] { agentex.getCodigo().toString()  });
		//String altera = String.valueOf(alterou);
        s.close();
	}
	
	//**********************************************************
	// 09.08.2012
	//
	// recupera o login do Agente para transação com o WebTrans
	//**********************************************************
	public String getLoginAgente( String codagente ) {
		
		String retorno = "";
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		//--------------------------------
		// criptografa matricula do agente
		//--------------------------------
		try {
			codagente = SimpleCrypto.encrypt(Utilitarios.getInfo(),codagente);
					
		} catch (Exception e1) 
		{
					// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		Cursor c = null; 
		try
		{
			c = s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {codagente});
			//c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {codagente});
			
			if ( c.moveToFirst())
			{
				retorno = c.getString(c.getColumnIndex("LOGIN"));
				
				
				try {
					retorno = SimpleCrypto.decrypt(Utilitarios.getInfo(),retorno);
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
		c.close();
		
		return retorno; 
		
	}

		
	//**********************************************************
	// 11.04.2013
	// recupera o senha do Agente a partir do Login
	//**********************************************************
	public String [] getLoginSenha( String agente )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		String []retorno = { "",""};

		//--------------------------------
		// criptografa matricula do agente
		//--------------------------------
		try {
			agente = SimpleCrypto.encrypt(Utilitarios.getInfo(),agente);

		} catch (Exception e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Cursor c = null; 
		try
		{
			c = s.rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
          //  c = getReadableDatabase().rawQuery("Select * from AGENTE where CODIGO = ?", new String[] {agente});
			
            if ( c.moveToFirst())
			{
				try {
					retorno[0]  =  SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("LOGIN")));
					retorno[0] = retorno[0].trim();
					retorno[1] =  c.getString(c.getColumnIndex("SENHA"));
					
					
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
		c.close();
		return retorno; 
	}
	
	public String ObtemQuantidadeAgente()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/agente", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			c = s.rawQuery("Select count(0) from agente",null);
			//c = getReadableDatabase().rawQuery("Select count(0) from agente",null);
			
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
