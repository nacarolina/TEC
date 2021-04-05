package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.FabricanteDAO;
import br.com.cobrasin.tabela.AitEnquadramento;
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


public class Fabricante_QFV extends Activity{
	
	ListView lstFabricante;
	Button btnFiltrar;
	EditText txtFabricante;
	private List<Fabricante> Lista_Fabricantes;
	private ArrayAdapter<Fabricante> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fabricante_qfv);
		
		btnFiltrar = (Button) findViewById(R.id.btnFiltrarFabricante_QFV);
		txtFabricante = (EditText) findViewById(R.id.txtFabricante_QFV);
		lstFabricante = (ListView) findViewById(R.id.lstFabricante_QFV);
		CarregaListaFabricante(txtFabricante.getText().toString());
		
		btnFiltrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CarregaListaFabricante(txtFabricante.getText().toString());
			}
		});
		
		lstFabricante.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String IdFabricante = Lista_Fabricantes.get(arg2).getId(); 
				String Fabricante = Lista_Fabricantes.get(arg2).getFabricante(); 
				Intent i = new Intent(Fabricante_QFV.this, Modelo_QFV.class);
				i.putExtra("IdFabricante", IdFabricante);
				i.putExtra("Fabricante", Fabricante);
				startActivity(i);
				finish();
			}
		});
	}
	
	private void CarregaListaFabricante(String Fabricante) {
		FabricanteDAO FaDAO = new FabricanteDAO(Fabricante_QFV.this);
		Lista_Fabricantes = FaDAO.GetTodosFabricantes(Fabricante);
		FaDAO.close();
		adapter = new ArrayAdapter<Fabricante>(this,
				android.R.layout.simple_list_item_1, Lista_Fabricantes);

		lstFabricante.setAdapter(adapter);
	}
}
