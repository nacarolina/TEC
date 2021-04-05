package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.tabela.Fabricante;
import br.com.cobrasin.tabela.NotaFiscal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class FabricanteDAO extends SQLiteOpenHelper{
	
	private static final String TABELA = "Fabricante";
	private static final int VERSAO = 1;
	private String info = "2012ANCOBRA";
	
	public FabricanteDAO(Context ctx) {
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
	
	public List<Fabricante> GetTodosFabricantes(String Fabricante)
	{
		SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		List<Fabricante> Lista_Fabricante = new ArrayList<Fabricante>();
		Cursor c = null;
		Fabricante Fb;
		try
		{
			try {
				c = s.rawQuery("SELECT * from Fabricante Where Fabricante Like  '%"+Fabricante+"%'", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while ( c.moveToNext() )
			{
				Fb = new Fabricante(); 
				
				try {
					Fb.setId(c.getString(c.getColumnIndex("Id")));
					Fb.setFabricante(c.getString(c.getColumnIndex("Fabricante")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				Lista_Fabricante.add(Fb);
			}		
		}
		catch ( SQLiteException e)
		{
			Log.e("Erro=",e.getMessage());
		}
		s.close();
		return Lista_Fabricante; 
	}
	
	public void ApagaTudo()
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		s.execSQL("Delete from Fabricante");
		s.close();
	}
	
	public void InsereFabricante(Fabricante Fbr) 
	{
		 SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/QFV", null, 0);
		ContentValues valores = new ContentValues();
		
		try {
			//valores.put("Fabricante",SimpleCrypto.encrypt(info,Fbr.getFabricante()));
			valores.put("Id",Fbr.getId());
			valores.put("Fabricante",Fbr.getFabricante());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}

}
