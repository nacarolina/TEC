package br.com.cobrasin;


import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Tipo;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaTipo extends ListActivity {
	
	private long idAit = 0 ;
	List<Tipo> tipo;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        String selecao = (String) getIntent().getSerializableExtra("seltipo");
	 	   
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	    
	        TipoDAO tipodao = new TipoDAO(getBaseContext());
	        
	  	  	tipo = tipodao.getLista() ;
	  	  	
	  	  	setListAdapter(new ArrayAdapter<Tipo>(this,android.R.layout.simple_list_item_single_choice,tipo));
	        
	        final ListView listView = getListView();
	 
	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        // recebeu a especie selecionada pelo usuario ?
	        if (selecao.length() > 0)
	        {
	        	int posicao = Integer.parseInt(selecao.substring(0, 2));
	        	listView.setItemChecked(posicao-1, true);
	        }
			
	     // percorre a lista para encontrar o que foi selecionado
	        Iterator<Tipo> nx = tipo.iterator();
	        
	        try
	        {
	        	
	        	int nz = 0 ;
	        	while ( nx.hasNext())
	        	{
	        				
	        		if (selecao.contains(tipo.get(nz).getCodigo()))
	        			{
	        				listView.setItemChecked(nz, true);
	        			}
	        		nz++;
	        	}
	        }
	        catch( Exception e)
	        {
	        	
	        }
	        
	    }

	    
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
	  // TODO Auto-generated method stub
	  super.onListItemClick(l, v, position, id);
	  
	  // Get the data associated with selected item
	  Object item = l.getItemAtPosition(position);
	   
	  Ait aitx = new Ait();
	  aitx.setId(idAit);
	  aitx.setTipo(tipo.get(position).getCodigo()); 
	
	  AitDAO aitdao = new AitDAO(this);
	  aitdao.gravaTipo(aitx);
	  aitdao.close();
	  
	  finish();
	 }
}
