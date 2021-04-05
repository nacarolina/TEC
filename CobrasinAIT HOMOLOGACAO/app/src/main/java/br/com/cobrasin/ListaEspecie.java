package br.com.cobrasin;


import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Especie;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaEspecie extends ListActivity {
	
	private long idAit = 0 ;
	
	List<Especie> espec; 
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        String selecao = (String) getIntent().getSerializableExtra("selespecie");
	        
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	        
	  	  	EspecieDAO especiedao = new EspecieDAO(getBaseContext());
	        
	  	  	espec = especiedao.getLista() ;
	  	  	
	  	  	setListAdapter(new ArrayAdapter<Especie>(this,android.R.layout.simple_list_item_single_choice,espec));
	        	  	  	
	        final ListView listView = getListView();

	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        // percorre a lista para encontrar o que foi selecionado
	        Iterator<Especie> nx = espec.iterator();
	        
	        try
	        {
	        	
	        	int nz = 0 ;
	        	while ( nx.hasNext())
	        	{
	        				
	        		if (selecao.contains(espec.get(nz).getCodigo()))
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
	  aitx.setEspecie(espec.get(position).getCodigo());
	  
	  AitDAO aitdao = new AitDAO(this);
	  aitdao.gravaEspecie(aitx);
	  aitdao.close();
	  
	  finish();
	 }
	   
}
