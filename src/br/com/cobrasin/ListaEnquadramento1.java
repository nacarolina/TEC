package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.tabela.AitEnquadramento;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListaEnquadramento1 extends Activity {

	private long idait = 0 ;
	private List<AitEnquadramento> aitenq;
	
	private void excluiReg(final long id)
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Exclui Registro ?");
		builder.setNegativeButton("Não", null);
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
				daoenq.deletereg(id);
				daoenq.close();
				
				carregaSelec();
			}
		});
				
		builder.show();
		
	}

	private void excluiTodosRegs( final long idait)
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Exclui Registro ?");
		builder.setNegativeButton("Não", null);
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
				daoenq.delete(idait);
				daoenq.close();
				
				carregaSelec();
			}
		});
				
		builder.show();
		
	}
	private void carregaSelec()
	{
		AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
		aitenq = daoenq.getLista(idait);
		daoenq.close();
		
		final ArrayAdapter<AitEnquadramento> adapter1 = new ArrayAdapter<AitEnquadramento>(this,android.R.layout.simple_list_item_1,aitenq);
		
		ListView listaEnquadraSelec = (ListView) findViewById(R.id.listaEnquadSelecionados1);
	
		listaEnquadraSelec.setAdapter(adapter1);
		
		listaEnquadraSelec.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
								
				// repete pois nao acha aitenq 
				AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
				List <AitEnquadramento> aitenq = daoenq.getLista(idait);
			 	
				daoenq.close();
				long xid = aitenq.get(arg2).getId(); 
				excluiReg(xid);
				
			}
		});

	}

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.enquadramento1);
	        
	        // pega o Id do AIT 
	        idait = (Long) getIntent().getSerializableExtra("idAit");
	        
	       carregaSelec();
	       
	       Button btLimpa = (Button) findViewById(R.id.btLimpaEnquadramento1);
	       btLimpa.setOnClickListener(new View.OnClickListener() {

	    	   @Override
	    	   public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
	    		   excluiTodosRegs(idait);
	    	   }
			
	       });
	       
	       Button btRetorna = (Button) findViewById(R.id.btRetornaEnquadramento1);
	       btRetorna.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				finish();
			}
		});
	 }
	 
}
