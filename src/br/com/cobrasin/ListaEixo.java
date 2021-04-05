package br.com.cobrasin;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.dao.CaracterizacaoDAO;
import br.com.cobrasin.dao.EixoDAO;
import br.com.cobrasin.tabela.Caracterizacao;
import br.com.cobrasin.tabela.Eixo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ListaEixo extends Activity {

	ListView lstEixo;
	Button btnFiltrar,btnConfirma_Eixo;
	EditText txtEixo;
	private List<Eixo> Lista_Eixo;
	private AdapterList_Eixo adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eixo);

		btnFiltrar = (Button) findViewById(R.id.btnFiltrarEixo);
		btnConfirma_Eixo = (Button) findViewById(R.id.btnConfirma_Eixo);
		txtEixo = (EditText) findViewById(R.id.txtEixo);
		lstEixo = (ListView) findViewById(R.id.lstEixoSel);
		CarregaLista(txtEixo.getText().toString());

		btnFiltrar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CarregaLista(txtEixo.getText().toString());
			}
		});
		
		btnConfirma_Eixo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ArrayList<String> Lista_Selecionado = adapter.getSelecionado();
				if(Lista_Selecionado.size() > 2 || Lista_Selecionado.size() < 2)
				{
					Toast.makeText(getBaseContext(),
							"Selecione apenas 2 Eixos!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent i = new Intent(ListaEixo.this,
						PreencheAitExcesso.class);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("QFV", "Eixo");
				i.putStringArrayListExtra("Lista_Selecionado", (ArrayList<String>) Lista_Selecionado);
				startActivity(i);
			}
		});

		lstEixo
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						/*String Id = Lista_Eixo.get(arg2).getId();

						Intent i = new Intent(Caracterizacao_Sel.this,
								PreencheAitExcesso.class);
						i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.putExtra("QFV", "Caracterizacao");
						i.putExtra("IdCaracterizacao", Id);
						startActivity(i);*/
					}
				});
	}

	private void CarregaLista(String Eixo) {
		EixoDAO EiDAO = new EixoDAO(ListaEixo.this);
		Lista_Eixo = EiDAO.GetTodosEixos(Eixo);
		EiDAO.close();

		adapter = new AdapterList_Eixo(this,
				R.layout.lst_eixo_item, Lista_Eixo,true);
		
		lstEixo.setAdapter(adapter);

	}
}