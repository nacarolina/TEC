package br.com.cobrasin;


import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Pais;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaPais extends ListActivity {
	
	private long idAit = 0 ;
	
	List<Pais> pais; 
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        String selecao = (String) getIntent().getSerializableExtra("selpais");
	        
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	        
	  	  	PaisDAO paisdao = new PaisDAO(ListaPais.this);
	        
	  	  	pais = paisdao.getLista() ;
	  	  	
	  	  	setListAdapter(new ArrayAdapter<Pais>(this,android.R.layout.simple_list_item_single_choice,pais));
	        	  	  	
	        final ListView listView = getListView();

	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        // percorre a lista para encontrar o que foi selecionado
	        Iterator<Pais> nx = pais.iterator();
	        
	        try
	        {
	        	
	        	int nz = 0 ;
	        	while ( nx.hasNext())
	        	{
	        				
	        		if (selecao.contains(pais.get(nz).getCodigo()))
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
	  aitx.setPais(pais.get(position).getCodigo());
	  
	  AitDAO aitdao = new AitDAO(this);
	  aitdao.gravaPais(aitx);
	  aitdao.close();
	  
	  finish();
	 }
	   
}
