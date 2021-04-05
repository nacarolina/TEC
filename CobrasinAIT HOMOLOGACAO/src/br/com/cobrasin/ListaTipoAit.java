package br.com.cobrasin;


import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaTipoAit extends ListActivity {
	
	private String salvaAgente ; 

	private String info = Utilitarios.getInfo();
	
	private String logradouroGps;
	
	private String PlacaDetectada = "";
	private String MarcaModeloDetectada = "";
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        String selecao = (String) getIntent().getSerializableExtra("seltipo");
	 	   
	        salvaAgente = (String) getIntent().getSerializableExtra("agente");
	        
	        logradouroGps= (String) getIntent().getSerializableExtra("logradouroGps");
	        
	        PlacaDetectada = (String) getIntent().getSerializableExtra("PlacaDetectada");
	        MarcaModeloDetectada = (String) getIntent().getSerializableExtra("MarcaModeloDetectada");
	        
	  	  	String opcoes[] = new String[] { "Veículo Placa Nacional","Veículo Placa Estrangeira","Pessoa Física","Pessoa Jurídica","Excesso de Carga","Retorna"};
	  	  	
	  	  	setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,opcoes));
	        
	        final ListView listView = getListView();
	 
	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	
	        
	        //******************************************
			//	26.01.2012
			// existe ait Aberta ? entao cancela
			//
			//******************************************
			
			AitDAO aitdao = new AitDAO(getBaseContext());
			Cursor ch = null;
			try {
				ch = aitdao.aitAberta(SimpleCrypto.encrypt(info,salvaAgente));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aitdao.close();
			
			Intent i ;
			if ( ch.getCount() > 0 )
			{
				
				i = new Intent(this, CancelaAit.class);
				i.putExtra("idAit", ch.getLong(ch.getColumnIndex("id")));
				startActivity(i);
				
				ch.close();
				
				finish();
			}
			else
			{
				ch.close();
			}
			
	    }

	    
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) 
	 {
	  // TODO Auto-generated method stub
	  super.onListItemClick(l, v, position, id);
	  
	  Intent i = null;
	  
	  switch( position )
	  {

	  	
	  	case 	0:			// veiculo
	  		i = new Intent(this,PreencheAit.class);
	  		i.putExtra("PlacaDetectada", PlacaDetectada);
	  		i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
			i.putExtra("logradouroGps", logradouroGps);
	  		break;
	  		
	  	case 	1:			// veiculo placa estrangeira
	  		i = new Intent(this,PreencheAitplest.class);
	  		i.putExtra("PlacaDetectada", PlacaDetectada);
	  		i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
	  		i.putExtra("logradouroGps", logradouroGps);
	  		break;
	  		
	  	case 	2:			// pessoa fisica
	  		i = new Intent(this,PreencheAitpfpj.class);
	  		i.putExtra("PlacaDetectada", PlacaDetectada);
	  		i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
	  		i.putExtra("logradouroGps", logradouroGps);
	  		i.putExtra("tipoait", "2");
	  		break;
	  		
	  	case 	3:			// pessoa juridica
	  		
	  		i = new Intent(this,PreencheAitpfpj.class);
	  		i.putExtra("PlacaDetectada", PlacaDetectada);
	  		i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
	  		i.putExtra("logradouroGps", logradouroGps);
	  		i.putExtra("tipoait", "3");
	  		break;
	  	case 	4:			// excesso de carga
	  		i = new Intent(this,PreencheAitExcesso.class);
	  		i.putExtra("PlacaDetectada", PlacaDetectada);
	  		i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
			i.putExtra("logradouroGps", logradouroGps);
			NotaFiscalDAO NfDAO = new NotaFiscalDAO(ListaTipoAit.this);
	        NfDAO.ApagaNovaNota();
	  		break;
	  	
	  }
	  
	  if ( position != 5 )
	  {
		  i.putExtra("agente", salvaAgente);
		  startActivity(i);
	  }
	  else
	  {
		  // correção 04.09.2012
		  //i = new Intent(this,ListaAit.class);
	  }

	  //startActivity(i);
	  
	  finish();
	  
	 }
}
