package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.tabela.Enquadramento;
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
import android.widget.Toast;

public class EnquadramentoDAO extends SQLiteOpenHelper {
	
	private static final String TABELA = "enquadramento";
	private static final int VERSAO = 1;
	private static final String[] COLS = { "CODIGO","DESCRICAO"};
	
	public EnquadramentoDAO(Context ctx ) {
		super(ctx, TABELA, null , VERSAO );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
				
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABELA + " " );
		sb.append("(CODIGO INT, ");
		sb.append("DESCRICAO TEXT ,");
		
		
		//db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public List<Enquadramento> getLista( String  sbusca,Context ctx){
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/enquadramento", null, 0);
		List<Enquadramento> enquadramento = new ArrayList<Enquadramento>();
	
		String busca = "%" + sbusca + "%",  menserro= "";
		
		Cursor c = null ;
		
		// foi digitado algum digito ?
		
		Character caractere ;
		 
		boolean erro = false,letra = false , digito = false;
		for ( int nx = 0 ;nx <= (sbusca.length()-1) ;nx++)
		{
			caractere = sbusca.charAt(nx);

			// nao � digito ou caractere
			if ( (!Character.isLetter(caractere)) && (!Character.isDigit(caractere))) erro = true;
			 
			if  (Character.isLetter(caractere)) letra = true ;
			if  (Character.isDigit(caractere)) digito = true ;
		}
		
		// se nao tinha digito inv�lido ?
		if ( !erro )
		{
			if (( letra ) && (digito) )
			{
				menserro = ">Entrada inv�lida, V�lido: Digitos ou Letras !";
				erro = true;
			}
			else
			{
				if ( letra )
				{
					// maxlen = 15
					if ( sbusca.length() > 15 )
					{
						menserro = ">Entrada inv�lida, no m�ximo 15 caracteres!";
						erro = true;
					}	
				}
				
				if ( digito )
				{
					// maxlen = 5
					if ( sbusca.length() > 15 )
					{
						menserro = ">Entrada inv�lida, no m�ximo 5 d�gitos!";
						erro = true;
					}
				}

			}
		}
				
		if (!erro)
		{
			
			try
			{
				
				//Cursor c = getWritableDatabase().query(TABELA, COLS, null, null, null, null, COLS[0]);
				
				// DIGITO
				String sqlpesq = "Select * from enquadramento where CODIGO LIKE  ?" ;
				
				if ( letra ) sqlpesq = "Select * from enquadramento  where DESCRICAO LIKE  ?";
				
				c = s.rawQuery(sqlpesq , new String[] {busca});
				
				while ( c.moveToNext()) {
					
					Enquadramento enquadramento1 = new Enquadramento();
					
					enquadramento1.setCodigo(c.getString(0));
					enquadramento1.setDescricao(c.getString(1));
								
					enquadramento.add(enquadramento1);
				}
				
				c.close();
			
			}
			catch ( SQLiteException e)
			{
				Log.e("Erro=",e.getMessage());
			}
			
		}
		else
		{
			Toast.makeText( ctx , menserro,Toast.LENGTH_SHORT).show();
		}
		s.close();
		return enquadramento;
	}
	
	public List<Enquadramento> getLista_Excesso( String  sbusca,Context ctx){
		
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/enquadramento", null, 0);
		List<Enquadramento> enquadramento = new ArrayList<Enquadramento>();
	
		String busca = "%" + sbusca + "%",  menserro= "";
		
		Cursor c = null ;
		
		// foi digitado algum digito ?
		
		Character caractere ;
		 
		boolean erro = false,letra = false , digito = false;
		for ( int nx = 0 ;nx <= (sbusca.length()-1) ;nx++)
		{
			caractere = sbusca.charAt(nx);

			// nao � digito ou caractere
			if ( (!Character.isLetter(caractere)) && (!Character.isDigit(caractere))) erro = true;
			 
			if  (Character.isLetter(caractere)) letra = true ;
			if  (Character.isDigit(caractere)) digito = true ;
		}
		
		// se nao tinha digito inv�lido ?
		if ( !erro )
		{
			if (( letra ) && (digito) )
			{
				menserro = ">Entrada inv�lida, V�lido: Digitos ou Letras !";
				erro = true;
			}
			else
			{
				if ( letra )
				{
					// maxlen = 15
					if ( sbusca.length() > 15 )
					{
						menserro = ">Entrada inv�lida, no m�ximo 15 caracteres!";
						erro = true;
					}	
				}
				
				if ( digito )
				{
					// maxlen = 5
					if ( sbusca.length() > 15 )
					{
						menserro = ">Entrada inv�lida, no m�ximo 5 d�gitos!";
						erro = true;
					}
				}

			}
		}
				
		if (!erro)
		{
			
			try
			{
				
				//Cursor c = getWritableDatabase().query(TABELA, COLS, null, null, null, null, COLS[0]);
				
				// DIGITO
				String sqlpesq = "Select * from enquadramento where CODIGO in ('58350','68310','68402','68820','68900','69040','60681','60682','67500','69710','69120','69800') and CODIGO LIKE  ?" ;
				
				if ( letra ) sqlpesq = "Select * from enquadramento  where CODIGO in ('58350','68310','68402','68820','68900','69040','60681','60682','67500','69710','69120','69800') and DESCRICAO LIKE  ?";
				
				c = s.rawQuery(sqlpesq , new String[] {busca});
				
				while ( c.moveToNext()) {
					
					Enquadramento enquadramento1 = new Enquadramento();
					
					enquadramento1.setCodigo(c.getString(0));
					enquadramento1.setDescricao(c.getString(1));
								
					enquadramento.add(enquadramento1);
				}
				
				c.close();
			
			}
			catch ( SQLiteException e)
			{
				Log.e("Erro=",e.getMessage());
			}
			
		}
		else
		{
			Toast.makeText( ctx , menserro,Toast.LENGTH_SHORT).show();
		}
		s.close();
		return enquadramento;
	}
    
	// limpa todos os registros de enquadramento  
	public void delete()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/enquadramento", null, 0);
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

	public void insere(Enquadramento enquadramento)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/enquadramento", null, 0);
		ContentValues valores = new ContentValues();
		
		valores.put("codigo",enquadramento.getCodigo());
		valores.put("descricao", enquadramento.getDescricao()); 
		
		s.insert(TABELA, null, valores);
        s.close();
	}
	public String ObtemQuantidadeEnquadramento()
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase("mnt/sdcard/db/enquadramento", null, 0);
		//String xcod = codEsp;
		String retorno = "";
		try
		{
			Cursor c = null ;
			
			
			c = s.rawQuery("Select count(0) from enquadramento",null);
			
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
