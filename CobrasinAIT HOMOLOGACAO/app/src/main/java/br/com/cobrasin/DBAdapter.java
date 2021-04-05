package br.com.cobrasin;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

final Context context;
DatabaseHelper DBHelper;
SQLiteDatabase db;

public DBAdapter(Context ctx) {
    this.context = ctx;
    DBHelper = new DatabaseHelper(context);
}

private static class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context) {
        super(context, "veiculos_rodizio", null, 1);
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}

public DBAdapter open(String path, String dbName) throws SQLException {
        db = DBHelper.getWritableDatabase();
        String destPath = path + "/" + dbName;
        db = db.openDatabase(destPath, null, 0);
        return this;
    }
}