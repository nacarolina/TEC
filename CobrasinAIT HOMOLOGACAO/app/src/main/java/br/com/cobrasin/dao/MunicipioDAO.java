package br.com.cobrasin.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.SimpleCrypto;
import br.com.cobrasin.tabela.Municipio;
import br.com.cobrasin.tabela.NotaFiscal;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class MunicipioDAO extends SQLiteOpenHelper {

	private static final String TABELA = "municipio";
	private static final int VERSAO = 1;

	public MunicipioDAO(Context ctx) {
		super(ctx, TABELA, null, VERSAO);
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

	public void ApagaTudo() {
		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		s.execSQL("Delete from municipio");
		s.close();
	}

	public void InsereMunicipio(Municipio Mu) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		ContentValues valores = new ContentValues();

		try {
			// valores.put("Fabricante",SimpleCrypto.encrypt(info,Fbr.getFabricante()));
			valores.put("UF", Mu.getUF());
			valores.put("IdProdesp", Mu.getIdProdesp());
			valores.put("Cidade", Mu.getCidade());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		s.insert(TABELA, null, valores);
		s.close();

	}

	public List<String> GetListaUF() {
		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		List<String> Lista_UF = new ArrayList<String>();
		Lista_UF.add("Selecione o Estado");
		Cursor c = null;

		try {
			c = s.rawQuery("SELECT UF from municipio group by UF order by UF",
					null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (c.moveToNext()) {
			Lista_UF.add(c.getString(c.getColumnIndex("UF")));
		}

		return Lista_UF;
	}

	public List<String> GetListaCidade(String UF) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		List<String> Lista_UF = new ArrayList<String>();
		Lista_UF.add("Selecione a Cidade");
		Cursor c = null;

		try {
			c = s.rawQuery("SELECT Cidade from municipio where Uf='" + UF
					+ "' order by Cidade", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (c.moveToNext()) {
			Lista_UF.add(c.getString(c.getColumnIndex("Cidade")));
		}

		return Lista_UF;
	}

	public String GetIdCidade(String UF, String Cidade) {
		String Id = "";

		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		Cursor c = null;
		try {

			c = s.rawQuery("SELECT Id from municipio where UF = '" + UF
					+ "' and Cidade = '" + Cidade + "'", null);

			if (c.moveToFirst()) {

				Id = c.getString(c.getColumnIndex("Id"));
			}

		} catch (SQLiteException e) {
			Log.e("Erro=", e.getMessage());
		}
		s.close();

		return Id;
	}
	
	public String GetIdProdesp(String Id) {
		String IdProdesp = "";

		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		Cursor c = null;
		try {

			c = s.rawQuery("SELECT IdProdesp from municipio where Id = "+Id, null);

			if (c.moveToFirst()) {

				IdProdesp = c.getString(c.getColumnIndex("IdProdesp"));
			}

		} catch (SQLiteException e) {
			Log.e("Erro=", e.getMessage());
		}
		s.close();

		return IdProdesp;
	}

	public List<Municipio> GetCidade(String Id) {
		SQLiteDatabase s = SQLiteDatabase.openDatabase(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/municipio", null, 0);
		List<Municipio> Lista_Municipio = new ArrayList<Municipio>();
		Cursor c = null;
		Municipio Mu;
		try {
			try {
				c = s.rawQuery("SELECT * from municipio where Id = " + Id,
						null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while (c.moveToNext()) {
					Mu = new Municipio();

					Mu.setId(c.getString(c.getColumnIndex("Id")));
					try {
						Mu.setCidade(c.getString(c.getColumnIndex("Cidade")));
						Mu.setIdProdesp(c.getString(c
								.getColumnIndex("IdProdesp")));
						Mu.setUF(c.getString(c.getColumnIndex("UF")));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // número do ait

					Lista_Municipio.add(Mu);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (SQLiteException e) {
			Log.e("Erro=", e.getMessage());
		}
		s.close();
		return Lista_Municipio;
	}

}
