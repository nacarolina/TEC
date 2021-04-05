package br.com.cobrasin;


import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.MedidaAdm;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaMedidaAdm extends ListActivity {
	
	private long idAit = 0 ;
	
	List<MedidaAdm> medidaadm; 
	String selecao;
	private String verifica;
	private String encerrouAit;
	private String cancelouAit;
	private String info = Utilitarios.getInfo();
	private String flagAit;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        selecao = (String) getIntent().getSerializableExtra("selmedidaadm");
	        
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	        
	  	  	MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(getBaseContext());
	        
	  	  	medidaadm = medidaadmdao.getLista() ;
	  	  	
	  	  	setListAdapter(new ArrayAdapter<MedidaAdm>(this,android.R.layout.simple_list_item_single_choice,medidaadm));
	        	  	  	
	        final ListView listView = getListView();

	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        
	        // percorre a lista para encontrar o que foi selecionado
	        Iterator<MedidaAdm> nx = medidaadm.iterator();
	        
	        try
	        {
	        	
	        	int nz = 0 ;
	        	while ( nx.hasNext())
	        	{
	        				
	        		if (selecao.contains(medidaadm.get(nz).getCodigo()))
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
	  AitDAO aitdao = new AitDAO(this);
	  Ait aitx = new Ait();
	  try {
		verifica = SimpleCrypto.decrypt(info,aitdao.ObtemFlagMedida(Long.toString(idAit)));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  Cursor c = aitdao.getAit(idAit);
	  try {
		encerrouAit = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("encerrou")));
	    cancelouAit = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cancelou")));
	  } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  if (encerrouAit != null || cancelouAit != null) 
	  {
		  if (verifica == null) 
		  {
			aitx.setFlagMedida("A");
			aitdao.gravaFlagMedida(aitx,Long.toString(idAit));
		}
	  }

	  aitx.setId(idAit);
	  aitx.setMedidaadm(medidaadm.get(position).getCodigo());
	  aitdao.gravaMedidaAdm(aitx);
	  aitdao.close();
	  
	  finish();
	 }
	   
}
