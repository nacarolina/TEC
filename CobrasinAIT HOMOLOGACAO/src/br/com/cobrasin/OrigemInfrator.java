package br.com.cobrasin;


import java.util.Iterator;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.ArqObservacao;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OrigemInfrator extends Activity {
	private long idAit = 0 ;
	private String info = Utilitarios.getInfo();
	@Override
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       CarregaTela();
	}
	private String TipoAIT;
	private void CarregaTela(){
		   setContentView(R.layout.origeminfrator);
	        // pega o Id do AIT 
		   
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	        
	        // pega os outros dados
	        String nome = (String) getIntent().getSerializableExtra("nome");
	        String cpf = (String) getIntent().getSerializableExtra("cpf");
	        String pgu = (String) getIntent().getSerializableExtra("pgu");
	        String uf =  (String) getIntent().getSerializableExtra("uf");
	        String ppd_condutor =  (String) getIntent().getSerializableExtra("ppd_condutor");
	        
	        TipoAIT =  (String) getIntent().getSerializableExtra("TipoAIT");
	        
	        String passaporte = (String) getIntent().getSerializableExtra("passaporte");
	        String pid = (String) getIntent().getSerializableExtra("pid");
	        
	        AitDAO aitDAO = new AitDAO(OrigemInfrator.this);
	        Cursor c = aitDAO.getAit(idAit);
	        String Origem = "";
	    	//while ( c.moveToNext())
			//{
			   try {
				Origem = SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipoinfrator")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//}
			}
	        if (Origem.contains("PID")) {

				Intent i;
				i = new Intent(this, InfratorInternacional.class);
			    i.putExtra("nome",nome);
				i.putExtra("passaporte",passaporte);
				i.putExtra("pid",pid);
			//	i.putExtra("uf",uf);
				i.putExtra("idAit",idAit);
					startActivity(i);
					
					finish();
				}
	        if (Origem.contains("CNH")) {
	        	Intent i; 
				i = new Intent(this, ListaDadosInfrator.class);
				i.putExtra("nome",nome);
				i.putExtra("cpf",cpf);
				i.putExtra("pgu",pgu);
				i.putExtra("uf",uf);
				i.putExtra("ppd_condutor",ppd_condutor);
				i.putExtra("idAit",idAit);
				i.putExtra("TipoAIT",TipoAIT);
				startActivity(i);
				
				finish();
			}
	        
	        Button btnInfratorNacional = (Button) findViewById(R.id.btnInfratorNacional);
	        
	        btnInfratorNacional.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					Intent i; 
					i = new Intent(getBaseContext(), ListaDadosInfrator.class);
					i.putExtra("idAit",idAit);
					i.putExtra("TipoAIT",TipoAIT);
					startActivity(i);
					finish();
				}
			});
	        
	        Button btnInfratorEstrangeiro = (Button)findViewById(R.id.btnInfratorEstrangeiro);
	        
	        btnInfratorEstrangeiro.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent i; 
					i = new Intent(getBaseContext(), InfratorInternacional.class);
					i.putExtra("idAit",idAit);
					
					startActivity(i);
					finish();
				}
			});
	}
	
}
