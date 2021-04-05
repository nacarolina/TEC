package br.com.cobrasin;

import java.io.IOException;
import java.io.InputStream;

import br.com.cobrasin.dao.NotaFiscalDAO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NotaFiscal_Foto extends Activity{
	
	private long idAit = 0;
    private String Id,NumeroNF = "";	
    private String TipoChamada;
    byte buffer[][] = new byte[1][];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notafiscal_foto);
		
		 idAit = (Long) getIntent().getSerializableExtra("idAit");
		 Id = (String) getIntent().getSerializableExtra("Id");
		 NumeroNF = (String) getIntent().getSerializableExtra("NumeroNF");
		 TipoChamada = (String) getIntent().getSerializableExtra("TipoChamada");
		 
		 ImageView imgNF = (ImageView) findViewById(R.id.imgNF);
		 TextView lblNumeroNF = (TextView) findViewById(R.id.lblNumeroNF_Foto);
		 lblNumeroNF.setText("Nº da Nota: "+NumeroNF);
		 
		 NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal_Foto.this);
		 
		 Cursor cx = NfDAO.getDadosNF(Id);
		 int pos = 0 ;
	  
	        	// aloca e preenche
	        	byte data [] = cx.getBlob(cx.getColumnIndex("Foto"));
	        	buffer[pos] = new byte[data.length];
	            buffer[pos] = data; 
	            
	       
	    	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[0], 0, buffer[0].length);
	    	
		 imgNF.setImageBitmap(bm);
		Button btnVolta = (Button) findViewById(R.id.btnVoltarImgNF);
		btnVolta.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(TipoChamada != null)
				{
					Intent i = new Intent(NotaFiscal_Foto.this, ListaNf_ExibeAit_Excesso.class);
					i.putExtra("idAit", idAit);
					startActivity(i);
					finish();
				}
				else
				{
				Intent i = new Intent(NotaFiscal_Foto.this, NotaFiscal.class);
				i.putExtra("idAit", idAit);
				startActivity(i);
				finish();
				}
			}
		});
	}

}
