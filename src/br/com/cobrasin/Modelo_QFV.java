package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.ModeloDAO;
import br.com.cobrasin.tabela.Modelo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Modelo_QFV extends Activity{
	
	ListView lstModelo;
	Button btnFiltrar,btnSelFabricante_QFV;
	EditText txtModelo;
	private List<Modelo> Lista_Modelo;
	private ArrayAdapter<Modelo> adapter;
	TextView lblFabricanteQFV;
	
	TextView lblFabricante_DetalhesQFV,lblModelo_DetalhesQFV,lblModeloPBT_DetalhesQFV,
	lblValorPBT_DetalhesQFV,lblCMT_DetalhesQFV,lblObservacoes_DetalhesQFV;
	
	private String IdFabricante,Fabricante;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modelo_qfv);
		
		IdFabricante = (String) getIntent().getSerializableExtra("IdFabricante");
		Fabricante = (String) getIntent().getSerializableExtra("Fabricante");
		
		lblFabricanteQFV = (TextView) findViewById(R.id.lblFabricanteQFV);
		lblFabricanteQFV.setText("Fabricante: "+Fabricante);
		
		btnFiltrar = (Button) findViewById(R.id.btnFiltrarModelo_QFV);
		btnSelFabricante_QFV = (Button) findViewById(R.id.btnSelFabricante_QFV);
		txtModelo = (EditText) findViewById(R.id.txtModelo_QFV);
		lstModelo = (ListView) findViewById(R.id.lstModelo_QFV);
		CarregaListaModelo(txtModelo.getText().toString(),IdFabricante);
		
		btnSelFabricante_QFV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Modelo_QFV.this, Fabricante_QFV.class);
				startActivity(i);
				finish();
			}
		});
		
		btnFiltrar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CarregaListaModelo(txtModelo.getText().toString(),IdFabricante);
			}
		});
		
		lstModelo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String IdModelo = Lista_Modelo.get(arg2).getId(); 
				String Modelo = Lista_Modelo.get(arg2).getModelo(); 
				
				Intent i = new Intent(Modelo_QFV.this,PreencheAitExcesso.class);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("QFV", "ModeloCaminhao");
				i.putExtra("IdFabricante", IdFabricante);
				i.putExtra("Fabricante", Fabricante);
				i.putExtra("IdModelo", IdModelo);
				i.putExtra("Modelo", Modelo);
				startActivity(i);
			}
		});
	}
	
	private void CarregaListaModelo(String Fabricante,String IdFabricante) {
		ModeloDAO MoDAO = new ModeloDAO(Modelo_QFV.this);
		Lista_Modelo = MoDAO.GetTodosModelos(Fabricante,IdFabricante);
		MoDAO.close();
		adapter = new ArrayAdapter<Modelo>(this,
				android.R.layout.simple_list_item_1, Lista_Modelo);

		lstModelo.setAdapter(adapter);
	}
}
