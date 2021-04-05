package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.NotaFiscal;
import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotaFiscalDAO extends SQLiteOpenHelper{
	
	private String info = "2012ANCOBRA";
	private static final String TABELA = "notafiscal";
	private static final int VERSAO = 1;

	public NotaFiscalDAO(Context context) {
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
	public void ApagaNovaNota()
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
			try {
				s.delete(TABELA, "idait=?", new String[] { SimpleCrypto.encrypt(info,"0")  });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.close();
	}

	public int getPesoDeclaradoAIT(long idAit)
	{
		int Total = 0;
		
		List<NotaFiscal> Lista_NotaFiscal = GetNotasAit(idAit);
		
		for (NotaFiscal nf : Lista_NotaFiscal) {
			Total = Total + Integer.valueOf(nf.getPesoDeclarado());
		}
		
		return Total;
	}
	
	public Cursor getDadosNF(String Id)
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		Cursor c = null;
		try
		{
			
			c = s.rawQuery("SELECT * from notafiscal where id = ?", new String[]{ Id });
			
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
	
	public void SalvaNotaFiscal(NotaFiscal Nota) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("idait", SimpleCrypto.encrypt(info,String.valueOf(Nota.getIdait())));
			valores.put("NumeroNota",SimpleCrypto.encrypt(info,Nota.getNumeroNota()));
			valores.put("PesoDeclarado",SimpleCrypto.encrypt(info,Nota.getPesoDeclarado()));
			valores.put("PesoExcesso",SimpleCrypto.encrypt(info,Nota.getPesoExcesso()));
			valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			//valores.put("Foto",Nota.getImagem());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public void SalvaNotaFiscal_ComFoto(NotaFiscal Nota) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("idait", SimpleCrypto.encrypt(info,String.valueOf(Nota.getIdait())));
			valores.put("NumeroNota",SimpleCrypto.encrypt(info,Nota.getNumeroNota()));
			valores.put("PesoDeclarado",SimpleCrypto.encrypt(info,Nota.getPesoDeclarado()));
		//	valores.put("PesoExcesso",SimpleCrypto.encrypt(info,Nota.getPesoExcesso()));
		//	valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			//valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			valores.put("Foto",Nota.getImagem());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}
	
	public void SalvarAlteracao(NotaFiscal Nota) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("idait", SimpleCrypto.encrypt(info,String.valueOf(Nota.getIdait())));
			valores.put("NumeroNota",SimpleCrypto.encrypt(info,Nota.getNumeroNota()));
			valores.put("PesoDeclarado",SimpleCrypto.encrypt(info,Nota.getPesoDeclarado()));
			valores.put("PesoExcesso",SimpleCrypto.encrypt(info,Nota.getPesoExcesso()));
			valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			valores.put("PesoVeiculo",SimpleCrypto.encrypt(info,Nota.getPesoVeiculo()));
			//valores.put("Foto",Nota.getImagem());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.update(TABELA, valores, "id=?",new String[] { Nota.getId()  });
		s.close();

	}
	
	public void SalvaFoto(NotaFiscal Nota) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			valores.put("Foto",Nota.getImagem());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			s.update(TABELA, valores, "id=?",new String[] { Nota.getId()});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		s.close();
	}
	
	public List<NotaFiscal> GetNotasAit(long IdAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		List<NotaFiscal> Lista_NotaFiscal = new ArrayList<NotaFiscal>();
		Cursor c = null;
		NotaFiscal Nf;
		try
		{
			try {
				c = s.rawQuery("SELECT * from notafiscal where idait = '"+ SimpleCrypto.encrypt(info,String.valueOf(IdAit))+"'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Nf = new NotaFiscal(); 
				
				Nf.setId(c.getString(0));// Id
				try {
	            Nf.setIdait(Long.parseLong(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("idait")))));
				Nf.setNumeroNota(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("NumeroNota"))));
				Nf.setPesoDeclarado(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("PesoDeclarado"))));
				//Nf.setPesoExcesso(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("PesoExcesso"))));
			//	Nf.setPesoVeiculo(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("PesoVeiculo"))));
				Nf.setImagem(c.getBlob(c.getColumnIndex("Foto")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	// número do ait
				
				
				Lista_NotaFiscal.add(Nf);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_NotaFiscal; 
	}
	
	public boolean ExisteFoto(String Id)
	{
		boolean ExiteFoto = false;
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		Cursor c = null;
		NotaFiscal Nf;
		try
		{
			try {
				c = s.rawQuery("SELECT Foto from notafiscal where Id = '"+ Id+"'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				try {
			    if(c.getBlob(c.getColumnIndex("Foto"))==null)
			    {
			    	ExiteFoto = false;
			    }
			    else		
			    {
			    	ExiteFoto = true;
			    }
			    
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
		return ExiteFoto; 
	}
	
	public void AlteraNfNovoAIT(long IdAit)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		
		try {
			s.execSQL("Update notafiscal set idait = '"+SimpleCrypto.encrypt(info,String.valueOf(IdAit))+"' where idait = '"+SimpleCrypto.encrypt(info,"0")+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}
	
	public void Deleta( String Id )
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/notafiscal", null, 0);
		s.delete(TABELA, "id=?", new String[] { Id  });
		s.close();
	}

}
