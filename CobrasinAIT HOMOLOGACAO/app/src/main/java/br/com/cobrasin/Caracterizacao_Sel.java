package br.com.cobrasin;

import java.util.LinkedList;
import java.util.List;


import br.com.cobrasin.dao.CaracterizacaoDAO;
import br.com.cobrasin.dao.FabricanteDAO;
import br.com.cobrasin.tabela.Caracterizacao;
import br.com.cobrasin.tabela.Fabricante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Caracterizacao_Sel extends Activity{
	
	ListView lstCaracterizacao;
	Button btnFiltrar;
	EditText txtCaracterizacao;
	private List<Caracterizacao> Lista_Caracterizacao;
	private ArrayAdapter<Caracterizacao> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caracterizacao_sel);
		
		btnFiltrar = (Button) findViewById(R.id.btnFiltrarCaracterizacao);
		txtCaracterizacao = (EditText) findViewById(R.id.txtCaracterizacao);
		lstCaracterizacao = (ListView) findViewById(R.id.lstCaracterizacao);
		CarregaLista(txtCaracterizacao.getText().toString());
		
		btnFiltrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CarregaLista(txtCaracterizacao.getText().toString());
			}
		});
		
		lstCaracterizacao.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String Id = Lista_Caracterizacao.get(arg2).getId(); 
				
				Intent i = new Intent(Caracterizacao_Sel.this,PreencheAitExcesso.class);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("QFV", "Caracterizacao");
				i.putExtra("IdCaracterizacao", Id);
				startActivity(i);
			}
		});
	}
	
	private void CarregaLista(String Caracterizacao) {
		CaracterizacaoDAO CaDAO = new CaracterizacaoDAO(Caracterizacao_Sel.this);
		Lista_Caracterizacao = CaDAO.GetTodasCaracterizacao(Caracterizacao);
		CaDAO.close();
		/*adapter = new ArrayAdapter<Caracterizacao>(this,
				android.R.layout.simple_list_item_1, Lista_Caracterizacao);*/
		
     
        AdapterList_QFV adapter = new AdapterList_QFV(this, R.layout.lst_caracterizacao_item, Lista_Caracterizacao);
        lstCaracterizacao.setAdapter(adapter);

	}
}