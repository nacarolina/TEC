package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;


import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.Utilitarios;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.tabela.Tipo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class ParametroDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "parametro";
	private static final int VERSAO = 1;
	private String info = Utilitarios.getInfo();
	private static final String[] COLS = { "proximoait","aitinicial","aitfinal","seriepda","prefeitura","sigla","orgaoautuador","serieait","servidorftp","usuarioftp","senhaftp","arquivobaseftp","impressora","servidorweb","imprimeobs","usuariowebtrans","senhawebtrans","ativo","prefativa","modweb","modgps","modocr"};
	
	public ParametroDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(proximoait TEXT,  ");
		sb.append("aitinicial TEXT,   ");
		sb.append("aitfinal TEXT,     ");
		sb.append("seriepda TEXT,     ");
		sb.append("prefeitura TEXT,   ");
		sb.append("sigla TEXT,        ");
		sb.append("orgaoautuador TEXT,");
		sb.append("serieait TEXT,     ");
		sb.append("servidorftp TEXT,  ");
		sb.append("usuarioftp TEXT,   ");
		sb.append("senhaftp TEXT,     ");
		sb.append(" arquivobaseftp,   ");
		sb.append(" impressoraMAC TEXT,  ");
		sb.append(" impressoraPatrimonio TEXT, ");
		sb.append(" imprimeobs TEXT, ");
		sb.append(" usuariowebtrans TEXT, ");
		sb.append(" senhawebtrans TEXT, ");
		sb.append(" idwebtrans TEXT, ");
		sb.append(" ativo TEXT, ");
		sb.append(" prefativa TEXT); ");		
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	//****************************
	// carrega dados do pda atual
	//****************************
	public  Cursor getParametros()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		Cursor c = null;
		try
		{
			c = s.rawQuery("SELECT * from parametro", null);
					
			c.moveToNext();
				
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return c; 
		
	}
	//public String ObtemModWeb (String pda)
	//{
		//String xcod = codEsp;
	//	String retorno = "";
	//	try
	//	{
	//		Cursor c = null ;
			
			
	//		c = getReadableDatabase().rawQuery("select * from parametro where seriepda = ?" ,new String [] { pda });
			
	//		while ( c.moveToNext() )
	//		{
			//	retorno = c.getString(0);
	//		}
					
	//	}
	//	catch ( SQLiteException e)
	//	{
		//	Log.e("Erro=",e.getMessage());
		//}
	//	return retorno; 
		
//	}
	
		//****************************************************
		// carrega dados do pda atual e verifica se esta ativo
		//****************************************************
		public  boolean pdaAtivo()
		{
			boolean retorno = false;
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT ativo from parametro", null);
						
				c.moveToNext();
					
				try {
					if ( SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("ativo"))).contains("S"))
					{
						retorno = true;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		public  String getIMEI()
		{
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
			String retorno = "";
			
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT IMEI from parametro", null);
						
				c.moveToNext();
					
				try {
		
						//retorno = SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("IMEI")));
						retorno = c.getString(c.getColumnIndex("IMEI"));
						
					c.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			catch ( SQLiteException e)
			{
				Log.e("Erro=",e.getMessage());
			}
			
			
			s.close();
			return retorno; 
			
		}		
		public  boolean bDados()
		{
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
			boolean retorno = false;
			
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT bDados from parametro", null);
						
				c.moveToNext();
					
				try {
					if ( SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("bDados"))).contains("true"))
					{
						retorno = true;
					}
					c.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			catch ( SQLiteException e)
			{
				Log.e("Erro=",e.getMessage());
			}
			
			s.close();
			
			return retorno; 
			
		}
		
		//***************************************************************
		// carrega dados do pda atual e verifica se prefeitura esta ativa
		//***************************************************************
		public  boolean prefeituraAtiva()
		{
			boolean retorno = false;
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT * from parametro", null);
						
				c.moveToNext();
					
				try {
					if (SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("prefativa"))).contains("S"))
					{
						retorno = true;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		
	//***********************
	// atualiza proximoait
	//***********************
	public void gravaParam( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("proximoait",SimpleCrypto.encrypt(info, param.getProximoait()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			s.update(TABELA, valores, "seriepda=?",new String[] { SimpleCrypto.encrypt(info, param.getSeriepda().toString() )});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}
	
	//*******************************************
	// atualiza endereco impressora e patrimonio
	//*******************************************	
	public void gravaImpressora( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
			ContentValues valores = new ContentValues();
			try {
				valores.put("impressoraMAC",  SimpleCrypto.encrypt(info,param.getImpressoraMAC()));
			valores.put("impressoraPatrimonio", SimpleCrypto.encrypt(info,param.getImpressoraPatrimonio()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
			s.close();
	}


	//***********************
	// atualiza observacoes
	//***********************
	public void gravaImprimeObs( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("imprimeobs",  SimpleCrypto.encrypt(info,param.getImprimeobs()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}
	
		public void gravaModoWeb( Parametro param )
	{
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("modweb", SimpleCrypto.encrypt(info,param.getmodweb()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}
	
	public void gravaModoGps( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("modgps",SimpleCrypto.encrypt(info, param.getmodgps()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}
	
	public void gravaModoOCR( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("modocr",SimpleCrypto.encrypt(info, param.getmodocr()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
        s.close();
	}		
	
	public void gravaModoPdf( Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("modpdf",SimpleCrypto.encrypt(info,param.getmodpdf()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}
	
	public void gravaTipoLeituraTAG( Parametro param )
	{
			SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("TipoLeituraTAG", SimpleCrypto.encrypt(info,param.getTipoLeituraTAG()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}

	public void gravaModeloPrint(Parametro param )
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("modeloImpressora", SimpleCrypto.encrypt(info,param.getModeloImpressora()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "seriepda=?",new String[] { param.getSeriepda().toString() });
		s.close();
	}
	
	//***********************
	// inicia o PDA  
	//***********************
	public void iniciapda(Parametro parx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
				ContentValues valores = new ContentValues();
				int tipo = 0;
				
				try {
					valores.put("proximoait",parx.getProximoait());
					valores.put("aitinicial",parx.getAitinicial());
					valores.put("aitfinal",parx.getAitfinal());
					valores.put("seriepda",parx.getSeriepda());
					valores.put("prefeitura",parx.getPrefeitura());
					valores.put("sigla",parx.getSigla());
					valores.put("orgaoautuador",parx.getOrgaoautuador());
					valores.put("serieait",parx.getSerieait());
					valores.put("servidorftp",parx.getServidorftp());
					valores.put("usuarioftp",parx.getUsuarioftp());
					valores.put("senhaftp", parx.getSenhaftp());
					valores.put("arquivobaseftp",parx.getArquivobaseftp());
					valores.put("impressoraMAC",parx.getImpressoraMAC());
					
					//if ( cx.getString(13).length() > 0 )
					//valores.put("servidorweb", SimpleCrypto.decrypt(Utilitarios.getInfo(),cx.getString(13)));
					
					valores.put("imprimeobs",parx.getImprimeobs());
					valores.put("usuariowebtrans",parx.getUsuariowebtrans());
					valores.put("senhawebtrans",parx.getSenhawebtrans());
					valores.put("idwebtrans",parx.getIdwebtrans());
					valores.put("ativo",parx.getAtivo());
					valores.put("prefativa",parx.getPrefativa());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String xSerie = "" ;
				
				try {
					xSerie = SimpleCrypto.decrypt(Utilitarios.getInfo(),parx.getSeriepda());
					tipo = 1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tipo = 0;
				} 
				
				if ( tipo == 0 )
					s.insert(TABELA, null, valores);
				else
					s.update(TABELA, valores, "seriepda=?",new String[] { xSerie }); // cx.getString(3)
				
				s.close();
	}

	public void setParamReceivedByIMEI(Parametro parx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
				ContentValues valores = new ContentValues();
				
				try 
				{
					//estes dados vem do serviço imei
					valores.put("proximoait",parx.getProximoait());
					valores.put("aitinicial",parx.getAitinicial());
					valores.put("aitfinal",parx.getAitfinal());
					valores.put("orgaoautuador",parx.getOrgaoautuador());
					valores.put("serieait",parx.getSerieait());
					valores.put("usuariowebtrans",parx.getUsuariowebtrans());
					valores.put("senhawebtrans",parx.getSenhawebtrans());
					valores.put("idwebtrans",parx.getIdwebtrans());
					valores.put("ativo",parx.getAtivo());
					valores.put("IMEI",parx.getIMEI());
					valores.put("impressoraMAC",parx.getImpressoraMAC());
					valores.put("impressoraPatrimonio",parx.getImpressoraPatrimonio());
					valores.put("imprimeobs",SimpleCrypto.encrypt(info, "1"));
					valores.put("modgps",SimpleCrypto.encrypt(info, "FALSE"));
					valores.put("modpdf",SimpleCrypto.encrypt(info, parx.getmodpdf()));
					//estes dados vem do service cliente
					valores.put("seriepda",parx.getSeriepda());
					valores.put("prefeitura",parx.getPrefeitura());
					valores.put("sigla",parx.getSigla());
					valores.put("usuariowebtrans",parx.getUsuariowebtrans());
					valores.put("senhawebtrans",parx.getSenhawebtrans());
					//valores.put("idwebtrans",parx.getIdwebtrans());
					valores.put("prefativa",parx.getPrefativa());
					valores.put("tipoLeituraTAG",parx.getTipoLeituraTAG());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				s.insert(TABELA, null, valores);
				s.close();
			
	}
	//******************************
	// limpa parametro
	//******************************
	public void limpareg()
	{ 
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		try
		{
			s.delete(TABELA,null,null );
		}
		catch( SQLiteException e)
		{
			
		}
		s.close();
	}

	public void atualizastatus(Parametro parx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();

		valores.put("ativo",parx.getAtivo());
		valores.put("prefativa", parx.getPrefativa());
		valores.put("aitinicial", parx.getAitinicial());
		valores.put("aitfinal", parx.getAitfinal());
		
		
		s.update(TABELA, valores, "seriepda=?",new String[] { parx.getSeriepda()  });
		s.close();
	}

	
	public void SetSequencialAIT(Parametro parx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();
		valores.put("aitinicial", parx.getAitinicial());
		valores.put("aitfinal", parx.getAitfinal());
		valores.put("proximoait", parx.getProximoait());
		s.update(TABELA, valores, "seriepda=?",new String[] { parx.getSeriepda()  });
		s.close();
	}
	
	public void SetStatusAtivo(String Ativo,String PrefAtiva,String SeriePDA)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();

		valores.put("ativo",Ativo);
		valores.put("prefativa", PrefAtiva);
				
		s.update(TABELA, valores, "seriepda=?",new String[] { SeriePDA  });
		s.close();
	}
	
	public void SetPdaAtivo(String Ativo,String SeriePDA)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();

		valores.put("ativo",Ativo);
				
		s.update(TABELA, valores, "seriepda=?",new String[] { SeriePDA  });
		s.close();
	}
	
	public void atualizaWebTrans(Parametro parx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();

		valores.put("usuariowebtrans",parx.getUsuariowebtrans());
		valores.put("senhawebtrans", parx.getSenhawebtrans());
		
		
		s.update(TABELA, valores, "seriepda=?",new String[] { parx.getSeriepda()  });
		s.close();
		
	}

	
	public void UpdateFTP(String servidorftp,String usuarioftp,String senhaftp,String arquivobaseftp)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();

		valores.put("servidorftp",servidorftp);
		valores.put("usuarioftp",usuarioftp);
		valores.put("senhaftp", senhaftp);
		valores.put("arquivobaseftp",arquivobaseftp);		
		
		s.update(TABELA, valores, null,null);
		s.close();
	}

	public void SetSincObsObrigatorio(String dtHr)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();


		valores.put("SincObsObrigatorio",dtHr);

		s.update(TABELA, valores, null,null);
		s.close();

	}
	public void SetDados(String bool)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/parametro", null, 0);
		ContentValues valores = new ContentValues();


		valores.put("bDados",bool);		
		
		s.update(TABELA, valores, null,null);
		s.close();
		
	}
}
