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
import android.util.Log;

public class AitDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "ait";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "id","ait","flag","agente","placa","data","hora",
		"marca","especie","tipo","logradouro","logradouronum","logradourotipo","nome","cpf",
		"pgu","uf","observacoes","impresso","transmitido","seriepda","encerrou","cancelou","motivo",
		"medidaadm","tipoait","pais","equipamento","medicaoreg","medicaocon","limitereg","idwebtrans","dtEdit","hrEdit","tipoinfrator","flagMedida","impressao","sendPdf"};

	private String info = "2012ANCOBRA";
	
	public AitDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append(" (id INTEGER PRIMARY KEY, " );
		sb.append("ait TEXT, ");
		sb.append("flag TEXT ,");		// A - aberta F - fechada C - cancelada
		sb.append("agente TEXT,");
		sb.append("placa TEXT, ");
		sb.append("data TEXT, ");
		sb.append("hora TEXT, ");
		sb.append("marca TEXT, ");
		sb.append("especie TEXT, ");
		sb.append("tipo TEXT, ");
		sb.append("logradouro TEXT, ");
		sb.append("logradouronum TEXT, ");
		sb.append("logradourotipo TEXT, ");
		sb.append("nome TEXT, ");
		sb.append("cpf TEXT, ");
		sb.append("pgu TEXT, ");
		sb.append("uf TEXT, ");
		sb.append("observacoes TEXT, ");
		sb.append("impresso TEXT, ");
		sb.append("transmitido TEXT, ");
		sb.append("seriepda TEXT, ");
		sb.append("encerrou TEXT, ");
		sb.append("cancelou TEXT, ");
		sb.append("motivo TEXT, ");
		sb.append("medidaadm TEXT,");
		sb.append("tipoait TEXT, ");
		sb.append("pais TEXT,");
		sb.append("equipamento TEXT,");
		sb.append("medicaoreg TEXT,");
		sb.append("medicaocon TEXT,");
		sb.append("limitereg TEXT,");
		sb.append("idwebtrans integer,");
		sb.append("dtEdit,");
		sb.append("hrEdit,");
		sb.append("flagMedida,");
		sb.append("sendPdf,");
		sb.append("tipoinfrator);");
		db.execSQL(sb.toString());
	}

	//**********************************************************
	// Grava  dados de multa por equipamento , ex: decibelimetro 
	//**********************************************************
	public void gravaEquip( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("equipamento", SimpleCrypto.encrypt(info,aitx.getEquipamento()));
			valores.put("medicaoreg", SimpleCrypto.encrypt(info,aitx.getMedicaoreg()));
			valores.put("medicaocon", SimpleCrypto.encrypt(info,aitx.getMedicaocon()));
			valores.put("limitereg", SimpleCrypto.encrypt(info,aitx.getLimitereg()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
		
	}
	
	//**********************************
	// Grava  especie
	//**********************************
	public void gravaEspecie( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("especie", SimpleCrypto.encrypt(info,aitx.getEspecie()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	public void gravaImpressao( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("impressao", SimpleCrypto.encrypt(info,aitx.getImpressao()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString() });
		s.close();
	}
	
	public void gravaDtEdit( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("dtEdit", SimpleCrypto.encrypt(info,aitx.getdtEdit()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	public void gravaHrEdit( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("hrEdit", SimpleCrypto.encrypt(info,aitx.gethrEdit()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}	
	public void gravaFlagMedida( Ait aitx,String idAit )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("flagMedida", SimpleCrypto.encrypt(info,aitx.getFlagMedida()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] {idAit});
		s.close();
	}
	//**********************************
	// Grava  tipo
	//**********************************
	public void gravaTipo( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("tipo", SimpleCrypto.encrypt(info,aitx.getTipo()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
		public void gravaSendPdf( Ait aitx )
	{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("sendPDF", SimpleCrypto.encrypt(info,aitx.getSendPdf()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	//**********************************
	// Grava  marca
	//**********************************
	public void gravaMarca( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("marca", SimpleCrypto.encrypt(info,aitx.getMarca()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	
	//**********************************
	// Grava  logradouro
	//**********************************
	public void gravaLocalTipo( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("logradourotipo", SimpleCrypto.encrypt(info,aitx.getLogradourotipo()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	public void gravaLocal( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("logradouro", SimpleCrypto.encrypt(info,aitx.getLogradouro()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	public void gravaLocal2( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("logradouro2", SimpleCrypto.encrypt(info,aitx.getLogradouro2()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	
	//**********************************
	// Grava numero do local
	//**********************************
	public void gravaLocalNumero( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("logradouronum", SimpleCrypto.encrypt(info,aitx.getLogradouronum()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}


	//**********************************
	// Grava observacoes
	//**********************************
	public void gravaObservacoes( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("observacoes", SimpleCrypto.encrypt(info,aitx.getObservacoes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	

	//**********************************
	// Grava dados do infrator
	//**********************************
	public void gravaInfrator( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("nome", SimpleCrypto.encrypt(info,aitx.getNome()));
			valores.put("cpf", SimpleCrypto.encrypt(info,aitx.getCpf()));
			valores.put("pgu", SimpleCrypto.encrypt(info,aitx.getPgu()));
			valores.put("uf", SimpleCrypto.encrypt(info,aitx.getUf()));
			valores.put("tipoinfrator",SimpleCrypto.encrypt(info,aitx.getTipoinfrator()));
			valores.put("ppd_condutor",SimpleCrypto.encrypt(info,aitx.getPpd_condutor()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	public void gravaInfratorPID( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("nome", SimpleCrypto.encrypt(info,aitx.getNome()));
			valores.put("passaporte", SimpleCrypto.encrypt(info,aitx.getPassaporte()));
			valores.put("pid", SimpleCrypto.encrypt(info,aitx.getPid()));
			valores.put("tipoinfrator",SimpleCrypto.encrypt(info,aitx.getTipoinfrator()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	

	//**********************************
	// Grava Medida Administrativa
	//**********************************
	public void gravaMedidaAdm( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("medidaadm", SimpleCrypto.encrypt(info,aitx.getMedidaadm()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	

	//**********************************
	// Grava codigo do pais
	//**********************************
	public void gravaPais( Ait aitx )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("pais", SimpleCrypto.encrypt(info,aitx.getPais()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
	}
	
	    //**********************************
		// Grava Dados do Embarcador
		//**********************************
		public void gravaEmbarcador( Ait aitx )
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			ContentValues valores = new ContentValues();
			try {
				valores.put("nome_embarcador", SimpleCrypto.encrypt(info,aitx.getNome_embarcador()));
				valores.put("cpfCnpj_embarcador", SimpleCrypto.encrypt(info,aitx.getCpfCnpj_embarcador()));
				valores.put("endereco_embarcador", SimpleCrypto.encrypt(info,aitx.getEndereco_embarcador()));
				valores.put("IdMunicipio_embarcador", SimpleCrypto.encrypt(info,aitx.getIdMunicipio_embarcador()));
				valores.put("bairro_embarcador", SimpleCrypto.encrypt(info,aitx.getBairro_embarcador()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
			s.close();
		}
		
	// **********************************
	// Grava Posto do Agente
	// **********************************
	public void gravaPostoAgente(Ait aitx) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait",
				null, 0);
		ContentValues valores = new ContentValues();
		try {
			valores.put("Posto_Agente",
					SimpleCrypto.encrypt(info, aitx.getPosto_Agente()));
			valores.put("IdMunicipio_Agente",
					SimpleCrypto.encrypt(info, aitx.getIdMunicipio_Agente()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.update(TABELA, valores, "id=?", new String[] { aitx.getId()
				.toString() });
		s.close();
	}
	
		//**********************************
				// Grava Dados do Transportador
				//**********************************
				public void gravaTransportador( Ait aitx )
				{
					 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
					ContentValues valores = new ContentValues();
					try {
						valores.put("nome_transportador", SimpleCrypto.encrypt(info,aitx.getNome_transportador()));
						valores.put("cpfCnpj_transportador", SimpleCrypto.encrypt(info,aitx.getCpfCnpj_transportador()));
						valores.put("endereco_transportador", SimpleCrypto.encrypt(info,aitx.getEndereco_transportador()));
						valores.put("IdMunicipio_transportador", SimpleCrypto.encrypt(info,aitx.getIdMunicipio_transportador()));
						valores.put("bairro_transportador", SimpleCrypto.encrypt(info,aitx.getBairro_transportador()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
					s.close();
				}
	//**********************************
	// Encerra o ait
	//**********************************
	public void fechaAitDAO( Ait aitx)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		try {
			
			valores.put("ait", SimpleCrypto.encrypt(info,aitx.getAit()));
			valores.put("flag",aitx.getFlag());
			valores.put("agente",SimpleCrypto.encrypt(info,aitx.getAgente()));
			valores.put("encerrou",SimpleCrypto.encrypt(info,aitx.getEncerrou()));
			valores.put("seriepda",SimpleCrypto.encrypt(info,aitx.getSeriepda()));
			valores.put("cancelou",SimpleCrypto.encrypt(info,aitx.getCancelou()));
			valores.put("motivo",SimpleCrypto.encrypt(info,aitx.getMotivo()));
			valores.put("tipoait", SimpleCrypto.encrypt(info,aitx.getTipoait()));
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });	
		s.close();
	}
	
	
	//***************************************************************************
	//Grava flag de transmissão
	//***************************************************************************
	public void atualizaTx(long idAit,boolean conseguiugravar,long idWebTrans)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		String xidait = String.valueOf(idAit);
		
		ContentValues valores = new ContentValues();
		
		if (conseguiugravar) valores.put("flag","T");
		
		// marca quando realizou a ultima transmissao
		//String transmitiu =new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()));
		//transmitiu += "-" + new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()));
		
		String transmitiu = Utilitarios.getDataHora(1);
		
		try {
			valores.put("transmitido",SimpleCrypto.encrypt(info, transmitiu));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		valores.put("idwebtrans",idWebTrans);
		
		s.update(TABELA, valores, "id=?",new String[] { xidait });	
		s.close();
		
	}
	
	//***************************************************************************
	//Grava flag de impressao
	//***************************************************************************
	public void atualizaImpressao(long idAit,Cursor curx)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		//***************************************************************************
		// verifica se está ocorrendo reimpressao !
		//***************************************************************************
		String imprimiu = "";
		
		try {
			imprimiu = SimpleCrypto.decrypt(info, curx.getString(curx.getColumnIndex("impresso")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String reimprimiu = "" ;
		
		if ( !imprimiu.contains("NAO")) reimprimiu = " / R";
			
		String xidait = String.valueOf(idAit);
		
		ContentValues valores = new ContentValues();
		
		// marca quando realizou a ultima transmissao
		//String imprimiu =new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()));
		//imprimiu += "-" + new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()));
		
		imprimiu = Utilitarios.getDataHora(1);
		try {
			
			/* 18.04.2012
			 * volta flag PARA F - fechado quando imprime para forçar uma nova transmissão
			 */
			
			valores.put("flag","F");
			
			valores.put("impresso",SimpleCrypto.encrypt(info, imprimiu + reimprimiu));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		s.update(TABELA, valores, "id=?",new String[] { xidait });	
		s.close();
		
	}

	//***************************************************************************
	//Grava cancelamento do ait
	//***************************************************************************
	public void gravaCancelamento(Ait aitx, long idAit)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		String xidait = String.valueOf(idAit);
		
		ContentValues valores = new ContentValues();
		
		// marca quando realizou a ultima transmissao
		//String cancelou =new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()));
		//cancelou += "-" + new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()));
		
		String cancelou = Utilitarios.getDataHora(1);
		
		
		try {
			
			//***************************************************************************
			// 26.01.2011
			// cancelou vira flag para F - fechado , indicando para transmitir novamente
			//***************************************************************************
			valores.put("flag", "F");
			valores.put("motivo", SimpleCrypto.encrypt(info,aitx.getMotivo()));
			valores.put("cancelou",SimpleCrypto.encrypt(info, cancelou));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		s.update(TABELA, valores, "id=?",new String[] { xidait });
		s.close();
		
	}

	public void AlteraExcessoPeso (Ait aitx)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("limitePermitido_excesso", aitx.getLimitePermitido_excesso());
		valores.put("pesoDeclarado_excesso", aitx.getPesoDeclarado_excesso());
		valores.put("excessoConstatado_excesso", aitx.getExcessoConstatado_excesso());
		valores.put("tara_excesso", aitx.getTara_excesso());
		
		int alterou = s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		String altera = String.valueOf(alterou);
	}
	
	//**************************
	// grava ait
	//**************************
	public void alteraInsere( Ait aitx , int tipo )  // 1 altera , 2 insere
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("ait", aitx.getAit());
		valores.put("flag", aitx.getFlag() );
		valores.put("agente",aitx.getAgente());
		valores.put("placa", aitx.getPlaca()); 
		valores.put("data", aitx.getData());
		valores.put("hora", aitx.getHora());
		valores.put("marca", aitx.getMarca());
		valores.put("especie", aitx.getEspecie());
		valores.put("tipo", aitx.getTipo());
		valores.put("logradouro", aitx.getLogradouro());
		valores.put("logradouronum", aitx.getLogradouronum());
		valores.put("logradourotipo",aitx.getLogradourotipo());
		valores.put("nome", aitx.getNome());
		valores.put("cpf",aitx.getCpf());
		valores.put("pgu", aitx.getPgu());
		valores.put("uf", aitx.getUf());
		valores.put("observacoes", aitx.getObservacoes());
		valores.put("impresso",aitx.getImpresso());
		valores.put("transmitido",aitx.getTransmitido());
		valores.put("seriepda",aitx.getSeriepda());
		valores.put("medidaadm", aitx.getMedidaadm());
		valores.put("tipoait", aitx.getTipoait());
		try {
			valores.put("tipoinfrator", aitx.getTipoinfrator());
		} catch (Exception e) {
			// TODO: handle exception
		}
		valores.put("pais", aitx.getPais());
		valores.put("equipamento",aitx.getEquipamento());
		valores.put("medicaoreg",aitx.getMedicaoreg());
		valores.put("medicaocon",aitx.getMedicaocon());
		valores.put("limitereg",aitx.getLimitereg());
		valores.put("idwebtrans",aitx.getIdWebTrans());
		valores.put("logradouro2",aitx.getLogradouro2());
		
		valores.put("nome_embarcador",aitx.getNome_embarcador());
		valores.put("cpfCnpj_embarcador",aitx.getCpfCnpj_embarcador());
		valores.put("endereco_embarcador",aitx.getEndereco_embarcador());
		
		valores.put("IdMunicipio_embarcador", aitx.getIdMunicipio_embarcador());
		valores.put("bairro_embarcador", aitx.getBairro_embarcador());

		valores.put("nome_transportador",aitx.getNome_transportador());
		valores.put("cpfCnpj_transportador",aitx.getCpfCnpj_transportador());
		valores.put("endereco_transportador",aitx.getEndereco_transportador());
		
		valores.put("IdMunicipio_transportador", aitx.getIdMunicipio_transportador());
		valores.put("bairro_transportador", aitx.getBairro_transportador());
		
		valores.put("limitePermitido_excesso", aitx.getLimitePermitido_excesso());
		valores.put("pesoDeclarado_excesso", aitx.getPesoDeclarado_excesso());
		valores.put("excessoConstatado_excesso", aitx.getExcessoConstatado_excesso());
		valores.put("tara_excesso", aitx.getTara_excesso());
		
		valores.put("ppd_condutor", aitx.getPpd_condutor());
		
		//Posto_Agente,IdMunicipio_Agente
		
		valores.put("Posto_Agente", aitx.getPosto_Agente());
		valores.put("IdMunicipio_Agente", aitx.getIdMunicipio_Agente());

		if (tipo == 2 )
		{	
			s.insert(TABELA, null, valores);
		}
		else
		{
			s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		}
			
        s.close();
	}

	//**************************
	// altera AIT 
	//**************************
	public void altera( Ait aitx  ) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("ait", aitx.getAit());
		valores.put("agente",aitx.getAgente());
		valores.put("placa", aitx.getPlaca()); 
		valores.put("data", aitx.getData());
		valores.put("hora", aitx.getHora());
		valores.put("marca", aitx.getMarca());
		valores.put("especie", aitx.getEspecie());
		valores.put("tipo", aitx.getTipo());
		valores.put("logradouro", aitx.getLogradouro());
		valores.put("logradouronum", aitx.getLogradouronum());
		valores.put("logradourotipo",aitx.getLogradourotipo());
		valores.put("nome", aitx.getNome());
		valores.put("cpf",aitx.getCpf());
		valores.put("pgu", aitx.getPgu());
		valores.put("uf", aitx.getUf());
		valores.put("observacoes", aitx.getObservacoes());
		valores.put("impresso",aitx.getImpresso());
		valores.put("transmitido",aitx.getTransmitido());
		valores.put("seriepda",aitx.getSeriepda());
		valores.put("encerrou",aitx.getEncerrou());
		valores.put("cancelou",aitx.getCancelou());
		valores.put("motivo",aitx.getMotivo());
		valores.put("medidaadm", aitx.getMedidaadm());
		valores.put("tipoait", aitx.getTipoait());
		valores.put("pais", aitx.getPais());
		valores.put("equipamento",aitx.getEquipamento());
		valores.put("medicaoreg",aitx.getMedicaoreg());
		valores.put("medicaocon",aitx.getMedicaocon());
		valores.put("limitereg",aitx.getLimitereg());
		valores.put("idwebtrans",aitx.getIdWebTrans());
		
		s.update(TABELA, valores, "id=?",new String[] { aitx.getId().toString()  });
		s.close();
		
	}

	//***********************
	// limpa registro pelo id
	//************************
	public void delete( long idait )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		String xidait = String.valueOf(idait);
		s.delete(TABELA, "id=?", new String[] { xidait  });
		s.close();
	}
	
	//***********************
	// limpa registro pelo id
	//************************
	public void deleteall()
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			s.delete(TABELA, null,null);
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
	
	//***************************************
	// verifica se existe ait em edição 
	//***************************************
	public  Cursor aitAberta(String cagente)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		Cursor c = null;
		try
		{
			//c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ?", new String[]{ "A" });

			//******************************
			//
			// Procura AIT aberta por agente 
			// alteração 30.01.2012
			//
			//******************************
			c = s.rawQuery("SELECT * from ait where flag = 'A' and agente = '" + cagente + "'", null);
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
	
	public String ObtemDataModificada (String idAit)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select dtEdit from ait where id = ?" ,new String [] { idAit });
			
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
	public String ObtemImpressao (String idAit)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select impressao from ait where id = ?" ,new String [] { idAit });
			
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
	public String ObtemFlagMedida (String idAit)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("select flagMedida from ait where id = ?" ,new String [] { idAit });
			
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
	
	public String ObtemTotalTransferido ()
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c =  s.rawQuery("select count(0) from ait where flag = ?",new String[]{ "T" });
			
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
		//****************************************
		// devolve cursor pelo id 
		//****************************************
		public  Cursor getAit(long idAit)
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			String xidait="";
			try {
				xidait = String.valueOf(idAit);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT * from ait where id = ?", new String[]{ xidait });
				
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

		// pesquisa o ait pelo numero , não pelo ID 
		public  Cursor getAit1(String xait)
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			try {
				xait = SimpleCrypto.encrypt(info,xait);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Cursor c = null;
			try
			{
				c = s.rawQuery("SELECT * from ait where ait = ?", new String[]{ xait });
				
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

		
		// pesquisa o ait pelo numero , não pelo ID 
		public  Cursor getListAit()
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			Cursor c = null;
			try 
			{
				String cancelou = SimpleCrypto.encrypt(info,"NAO");
				
				try
				{
					c = s.rawQuery("SELECT * from ait where cancelou = ?", new String[]{ cancelou });
					
					if ( c.moveToFirst())
					{
						return c;
					}
				}
				catch ( SQLiteException e)
				{
					Log.e("Erro=",e.getMessage());
				}
				
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			s.close();
			return c; 
		}

		
		
	//****************************************
	// lista para tela de AITS fechados
	//****************************************
	public List<Ait> getLista(String cagente )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		Ait aitx;
		
		List<Ait> lait = new ArrayList<Ait>();
		
		////Cursor c = getWritableDatabase().query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);

		//Cursor c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ? order by id desc", new String[]{ "F" });
		
		
		
		try {
			cagente  = SimpleCrypto.encrypt(info, cagente);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cursor c = s.rawQuery("SELECT * from ait where flag = 'F' and agente = '"+ cagente + "' order by id desc", null);
		
		while ( c.moveToNext()) { 	
			
			aitx = new Ait(); 
			
			aitx.setId( c.getLong(0));		// id
			try {
				
			
				aitx.setAit(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("ait"))));
				aitx.setAgente(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("agente")))); // agente
				
				//aitx.setPlaca(SimpleCrypto.decrypt(info,c.getString(4)));	// placa
				
				aitx.setData(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))));
				aitx.setHora(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora"))));
				aitx.setTipoait(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoait"))));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	// número do ait
			
			
			lait.add(aitx);
		}
		
		c.close();
		
		
		//*****************************************************************************
		// Procura transmitidos nas ultimas 24 horas...
		//
		//
		//
		//*****************************************************************************
		//c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ?", new String[]{ "T" });
		
		c = s.rawQuery("SELECT * from ait where flag = 'T' and agente = '" + cagente + "' order by id desc", null);
		
		while ( c.moveToNext()) { 	

			// transmissão > 24 horas
			try {
				if (Utilitarios.calculaDias(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("transmitido")))) == 0 )
				{	
					aitx = new Ait();
				
					aitx.setId( c.getLong(0));		// id
					try {
						
					
						aitx.setAit(SimpleCrypto.decrypt(info,c.getString(1)));
						aitx.setAgente(SimpleCrypto.decrypt(info,c.getString(3))); // agente
						aitx.setPlaca(SimpleCrypto.decrypt(info,c.getString(4)));	// placa
						aitx.setData(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))));
						aitx.setHora(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora"))));
						aitx.setTipoait(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoait"))));
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	// número do ait
				
					lait.add(aitx);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		c.close();
		s.close();
		return lait;
		
	}
	
	
	//****************************************
	// lista para tela de AITS fechados
	//****************************************
	public List<Ait> getListaAitPrint(String cagente )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		Ait aitx;
		String cancelou="";
		
		List<Ait> lait = new ArrayList<Ait>();
		
		////Cursor c = getWritableDatabase().query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);

		//Cursor c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ? order by id desc", new String[]{ "F" });
		
		
		
		try {
			cagente  = SimpleCrypto.encrypt(info, cagente);
			 cancelou = SimpleCrypto.encrypt(info,"NAO");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Cursor c = s.rawQuery("SELECT * from ait where flag = 'F' " +
				"and agente = '"+ cagente + "' order by id desc", null);//and cancelou = '"+ cancelou +"'
		
		while ( c.moveToNext()) { 	
			
			aitx = new Ait(); 
			
			aitx.setId( c.getLong(0));		// id
			try {
				
			
				aitx.setAit(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("ait"))));
				aitx.setAgente(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("agente")))); // agente
				
				//aitx.setPlaca(SimpleCrypto.decrypt(info,c.getString(4)));	// placa
				
				aitx.setData(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))));
				aitx.setHora(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora"))));
				aitx.setTipoait(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoait"))));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	// número do ait
			
			
			lait.add(aitx);
		}
		
		c.close();
		
		
		//*****************************************************************************
		// Procura transmitidos nas ultimas 24 horas...
		//
		//
		//
		//*****************************************************************************
		//c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ?", new String[]{ "T" });
		
		c = s.rawQuery("SELECT * from ait where flag = 'T' and agente = '" + cagente + 
				"'and cancelou = '"+ cancelou +"'  order by id desc", null);
		
		while ( c.moveToNext()) { 	

			// transmissão > 24 horas
			try {
				if (Utilitarios.calculaDias(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("transmitido")))) == 0 )
				{	
					aitx = new Ait();
				
					aitx.setId( c.getLong(0));		// id
					try {
						
					
						aitx.setAit(SimpleCrypto.decrypt(info,c.getString(1)));
						aitx.setAgente(SimpleCrypto.decrypt(info,c.getString(3))); // agente
						aitx.setPlaca(SimpleCrypto.decrypt(info,c.getString(4)));	// placa
						aitx.setData(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))));
						aitx.setHora(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora"))));
						aitx.setTipoait(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoait"))));
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	// número do ait
				
					lait.add(aitx);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		c.close();
		s.close();
		return lait;
		
	}
	
	//***************************************************************
	// lista para tela de AITS transmitidos com > 1 dia para exclusão
	//***************************************************************
	public List<Ait> getListaTransmitida( )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
		Ait aitx;
			
		List<Ait> lait = new ArrayList<Ait>();
			
			
		//*****************************************************************************
		// Procura transmitidos nas ultimas 24 horas...
		//
		//
		//
		//*****************************************************************************
		//c = getReadableDatabase().rawQuery("SELECT * from ait where flag = ?", new String[]{ "T" });
			
		Cursor c = null;
		try {
			c = s.rawQuery("SELECT * from ait where flag = 'T' and sendPdf='"+SimpleCrypto.encrypt(info, "TRUE")+"' order by id desc", null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		while ( c.moveToNext()) { 	

		// transmissão > 24 horas
		try {
				if (Utilitarios.calculaDias(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("transmitido")))) > 0 )
				{	
					aitx = new Ait();
					
					aitx.setId( c.getLong(0));		// id
					try 
					{
							
						
							aitx.setAit(SimpleCrypto.decrypt(info,c.getString(1)));
							aitx.setAgente(SimpleCrypto.decrypt(info,c.getString(3))); // agente
							aitx.setPlaca(SimpleCrypto.decrypt(info,c.getString(4)));	// placa
							aitx.setData(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))));
							aitx.setHora(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora"))));
							aitx.setTipoait(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoait"))));
							
					} catch (Exception e) 
					{
							// TODO Auto-generated catch block
							e.printStackTrace();
					}	// número do ait
					
						lait.add(aitx);
				}
			} catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
		c.close();
		s.close();
		return lait;
			
	}
	
		//**********************************************
		// lista para transmissao de AITS fechados
		//**********************************************
		public List<Ait> getListaCompleta() 
		{
			 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
			List<Ait> lait = new ArrayList<Ait>();
			
			//Cursor c = getWritableDatabase().query(TABELA, COLS, "idait = ?", new String[] { xidAit }, null, null, COLS[2]);

			Cursor c = s.rawQuery("SELECT * from ait where flag = ?", new String[]{ "F" });
			
			while ( c.moveToNext()) { 	
				
				Ait aitx = new Ait(); 
				
				aitx.setId(c.getLong(0));		// id
				aitx.setAit(c.getString(c.getColumnIndex("ait")));
				aitx.setFlag(c.getString(c.getColumnIndex("flag")));
				aitx.setAgente((c.getString(c.getColumnIndex("agente"))));
				aitx.setPlaca((c.getString(c.getColumnIndex("placa"))));
				aitx.setData(c.getString(c.getColumnIndex("data")));
				aitx.setHora(c.getString(c.getColumnIndex("hora")));
				aitx.setMarca(c.getString(c.getColumnIndex("marca")));
				aitx.setEspecie(c.getString(c.getColumnIndex("especie")));
				aitx.setTipo(c.getString(c.getColumnIndex("tipo")));
				aitx.setLogradouro(c.getString(c.getColumnIndex("logradouro")));
				aitx.setLogradouronum(c.getString(c.getColumnIndex("logradouronum")));
				aitx.setLogradourotipo(c.getString(c.getColumnIndex("logradourotipo")));
				aitx.setNome(c.getString(c.getColumnIndex("nome")));
				aitx.setCpf(c.getString(c.getColumnIndex("cpf")));
				aitx.setPgu(c.getString(c.getColumnIndex("pgu")));
				aitx.setUf(c.getString(c.getColumnIndex("uf")));
				aitx.setObservacoes(c.getString(c.getColumnIndex("observacoes")));
				aitx.setImpresso(c.getString(c.getColumnIndex("impresso")));
				aitx.setTransmitido(c.getString(c.getColumnIndex("transmitido")));
				aitx.setSeriepda(c.getString(c.getColumnIndex("seriepda")));
				aitx.setEncerrou(c.getString(c.getColumnIndex("encerrou")));
				aitx.setCancelou(c.getString(c.getColumnIndex("cancelou")));
				aitx.setMotivo(c.getString(c.getColumnIndex("motivo")));
				aitx.setMedidaadm(c.getString(c.getColumnIndex("medidaadm")));
				aitx.setTipoait(c.getString(c.getColumnIndex("tipoait")));
				aitx.setPais(c.getString(c.getColumnIndex("pais")));
				aitx.setEquipamento(c.getString(c.getColumnIndex("equipamento")));
				aitx.setMedicaoreg(c.getString(c.getColumnIndex("medicaoreg")));
				aitx.setMedicaocon(c.getString(c.getColumnIndex("medicaocon")));
				aitx.setLimitereg(c.getString(c.getColumnIndex("limitereg")));
				aitx.setIdWebTrans(c.getLong(c.getColumnIndex("idwebtrans")));
				aitx.setdtEdit(c.getString(c.getColumnIndex("dtEdit")));
				aitx.sethrEdit(c.getString(c.getColumnIndex("hrEdit")));
				aitx.setTipoinfrator(c.getString(c.getColumnIndex("tipoinfrator")));
				aitx.setFlagMedida(c.getString(c.getColumnIndex("flagMedida")));
				aitx.setImpressao(c.getString(c.getColumnIndex("impressao")));
				aitx.setLogradouro2(c.getString(c.getColumnIndex("logradouro2")));
				aitx.setPid(c.getString(c.getColumnIndex("pid")));
				aitx.setPassaporte(c.getString(c.getColumnIndex("passaporte")));
				
				aitx.setNome_embarcador(c.getString(c.getColumnIndex("nome_embarcador")));
				aitx.setCpfCnpj_embarcador(c.getString(c.getColumnIndex("cpfCnpj_embarcador")));
				aitx.setEndereco_embarcador(c.getString(c.getColumnIndex("endereco_embarcador")));
				aitx.setIdMunicipio_embarcador(c.getString(c.getColumnIndex("IdMunicipio_embarcador")));
				aitx.setBairro_embarcador(c.getString(c.getColumnIndex("bairro_embarcador")));
				
				aitx.setNome_transportador(c.getString(c.getColumnIndex("nome_transportador")));
				aitx.setCpfCnpj_transportador(c.getString(c.getColumnIndex("cpfCnpj_transportador")));
				aitx.setEndereco_transportador(c.getString(c.getColumnIndex("endereco_transportador")));
				aitx.setIdMunicipio_transportador(c.getString(c.getColumnIndex("IdMunicipio_transportador")));
				aitx.setBairro_transportador(c.getString(c.getColumnIndex("bairro_transportador")));
				
				
				aitx.setLimitePermitido_excesso(c.getString(c.getColumnIndex("limitePermitido_excesso")));
				aitx.setPesoDeclarado_excesso(c.getString(c.getColumnIndex("pesoDeclarado_excesso")));
				aitx.setExcessoConstatado_excesso(c.getString(c.getColumnIndex("excessoConstatado_excesso")));
				aitx.setTara_excesso(c.getString(c.getColumnIndex("tara_excesso")));
				
				aitx.setPpd_condutor(c.getString(c.getColumnIndex("ppd_condutor")));
				
				//Posto_Agente,IdMunicipio_Agente
				aitx.setPosto_Agente(c.getString(c.getColumnIndex("Posto_Agente")));
				aitx.setIdMunicipio_Agente(c.getString(c.getColumnIndex("IdMunicipio_Agente")));
				
				lait.add(aitx);
			}
			
			c.close();
			s.close();
			
			return lait;
			
		}
	
		//**********************************************
				// lista para transmissao de AITS impressos
				//**********************************************
				public List<Ait> getListaAitImpresso() 
				{
					 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/ait", null, 0);
					List<Ait> lait = new ArrayList<Ait>();
					
					//Cursor c = getWritableDatabase().query(TABELA, COLS);

					Cursor c = s.rawQuery("SELECT * from ait where idwebtrans != '0'", null);
					
					while ( c.moveToNext()) { 	
						
						Ait aitx = new Ait(); 
						aitx.setId(c.getLong(0));		// id
						aitx.setAit(c.getString(c.getColumnIndex("ait")));
						aitx.setFlag(c.getString(c.getColumnIndex("flag")));
						aitx.setAgente((c.getString(c.getColumnIndex("agente"))));
						aitx.setPlaca((c.getString(c.getColumnIndex("placa"))));
						aitx.setData(c.getString(c.getColumnIndex("data")));
						aitx.setHora(c.getString(c.getColumnIndex("hora")));
						aitx.setMarca(c.getString(c.getColumnIndex("marca")));
						aitx.setEspecie(c.getString(c.getColumnIndex("especie")));
						aitx.setTipo(c.getString(c.getColumnIndex("tipo")));
						aitx.setLogradouro(c.getString(c.getColumnIndex("logradouro")));
						aitx.setLogradouronum(c.getString(c.getColumnIndex("logradouronum")));
						aitx.setLogradourotipo(c.getString(c.getColumnIndex("logradourotipo")));
						aitx.setNome(c.getString(c.getColumnIndex("nome")));
						aitx.setCpf(c.getString(c.getColumnIndex("cpf")));
						aitx.setPgu(c.getString(c.getColumnIndex("pgu")));
						aitx.setUf(c.getString(c.getColumnIndex("uf")));
						aitx.setObservacoes(c.getString(c.getColumnIndex("observacoes")));
						aitx.setImpresso(c.getString(c.getColumnIndex("impresso")));
						aitx.setTransmitido(c.getString(c.getColumnIndex("transmitido")));
						aitx.setSeriepda(c.getString(c.getColumnIndex("seriepda")));
						aitx.setEncerrou(c.getString(c.getColumnIndex("encerrou")));
						aitx.setCancelou(c.getString(c.getColumnIndex("cancelou")));
						aitx.setMotivo(c.getString(c.getColumnIndex("motivo")));
						aitx.setMedidaadm(c.getString(c.getColumnIndex("medidaadm")));
						aitx.setTipoait(c.getString(c.getColumnIndex("tipoait")));
						aitx.setPais(c.getString(c.getColumnIndex("pais")));
						aitx.setEquipamento(c.getString(c.getColumnIndex("equipamento")));
						aitx.setMedicaoreg(c.getString(c.getColumnIndex("medicaoreg")));
						aitx.setMedicaocon(c.getString(c.getColumnIndex("medicaocon")));
						aitx.setLimitereg(c.getString(c.getColumnIndex("limitereg")));
						aitx.setIdWebTrans(c.getLong(c.getColumnIndex("idwebtrans")));
						aitx.setLimitereg(c.getString(c.getColumnIndex("dtEdit")));
						aitx.setImpressao(c.getString(c.getColumnIndex("hrEdit")));
						aitx.setImpressao(c.getString(c.getColumnIndex("tipoinfrator")));
						aitx.setImpressao(c.getString(c.getColumnIndex("flagMedida")));
						aitx.setImpressao(c.getString(c.getColumnIndex("impressao")));
						aitx.setSendPdf(c.getString(c.getColumnIndex("sendPDF")));
						
						lait.add(aitx);
					}
					
					c.close();
					s.close();
					
					return lait;
					
				}
}
