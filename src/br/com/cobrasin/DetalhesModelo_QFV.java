package br.com.cobrasin;

import br.com.cobrasin.dao.ModeloDAO;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class DetalhesModelo_QFV extends Activity{

	String IdFabricante,IdModelo,Fabricante,Modelo;
	TextView lblFabricante_DetalhesQFV,lblModelo_DetalhesQFV,lblModeloPBT_DetalhesQFV,
	lblValorPBT_DetalhesQFV,lblCMT_DetalhesQFV,lblObservacoes_DetalhesQFV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detalhes_modelo_qfv);
		
		IdFabricante = (String) getIntent().getSerializableExtra("IdFabricante");
		Fabricante = (String) getIntent().getSerializableExtra("Fabricante");
		IdModelo = (String) getIntent().getSerializableExtra("IdModelo");
		Modelo = (String) getIntent().getSerializableExtra("Modelo");
		
		
	}
}
