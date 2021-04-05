package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Logradouro;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListaLogradouro2 extends Activity {

	private long idAit = 0;
	TextView lblLogradouroSel;
	private String codLogSelec = "";
	private String snumeroLog = "";
	private String tipLogSelec = "";
	private ListView lstLogradouros;
	private Button btnPesquisa, btnVoltar;
	private EditText txtLogradouro;
	private List<Logradouro> logradouro;
	private String info = Utilitarios.getInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.endereco2);
		findViewById();
		RecuperaValores();
		CarregaListaLogradouro();
		LogradouroDAO logdao = new LogradouroDAO(this);
		final String descricao = logdao.buscaDescLog(String
				.valueOf(codLogSelec));
		lblLogradouroSel.setText("End. Selecionado:" + descricao);
		btnPesquisa.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarregaListaLogradouro();
			}
		});
		lstLogradouros
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						Object listItem = lstLogradouros
								.getItemAtPosition(arg2);

						final String scodigo = logradouro.get(arg2).getCodigo();
						String sdescricao = logradouro.get(arg2).getDescricao();

						if (sdescricao.contains(descricao)) {
							AlertDialog.Builder aviso = new AlertDialog.Builder(
									ListaLogradouro2.this);
							aviso.setIcon(android.R.drawable.ic_dialog_alert);
							aviso.setTitle("Logradouro - Cruzamento");
							aviso.setMessage("Não é possível selecionar o mesmo logradouro!");
							aviso.setNeutralButton("OK", null);

							aviso.show();
						} else {
							Ait aitx = new Ait();
							aitx.setLogradourotipo("0");
							aitx.setLogradouronum("");
							aitx.setId(idAit);
							aitx.setLogradouro2(scodigo);

							AitDAO aitdao = new AitDAO(getBaseContext());
							aitdao.gravaLocal2(aitx);
							aitdao.gravaLocalTipo(aitx);
							aitdao.gravaLocalNumero(aitx);
							aitdao.close();

							Intent i = new Intent(ListaLogradouro2.this,
									ListaLogradouro3.class);
							i.putExtra("codLogradouro", codLogSelec);
							i.putExtra("numLogradouro", "");
							i.putExtra("codLogradouro2", scodigo);
							i.putExtra("tipLogradouro", "0");
							i.putExtra("idAit", idAit);
							startActivity(i);
							finish();
						}
					}
				});
		btnVoltar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AitDAO aitdao = new AitDAO(ListaLogradouro2.this);
				Cursor c = aitdao.getAit(idAit);
				try {
					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouro2")))
							.contains("NAO")) {
						Intent i = new Intent(ListaLogradouro2.this,
								ListaLogradouro1.class);
						i.putExtra("codLogradouro", codLogSelec);
						i.putExtra("numLogradouro", snumeroLog);
						i.putExtra("tipLogradouro", tipLogSelec);
						i.putExtra("idAit", idAit);
						startActivity(i);
					} else {
						Intent i = new Intent(ListaLogradouro2.this,
								ListaLogradouro.class);
						i.putExtra("codLogradouro", codLogSelec);
						i.putExtra("numLogradouro", snumeroLog);
						i.putExtra("tipLogradouro", tipLogSelec);
						i.putExtra("idAit", idAit);
						startActivity(i);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				finish();
			}
		});
	}

	private void findViewById() {
		lblLogradouroSel = (TextView) findViewById(R.id.lblLogradouroSel);
		lstLogradouros = (ListView) findViewById(R.id.lstLogradouros2);
		btnPesquisa = (Button) findViewById(R.id.btPesquisaEnq);
		btnVoltar = (Button) findViewById(R.id.btnVoltarLogra);
		txtLogradouro = (EditText) findViewById(R.id.txtLogradouro2);
	}

	private void RecuperaValores() {
		idAit = (Long) getIntent().getSerializableExtra("idAit");
		// pegao o numero do logradouro
		snumeroLog = (String) getIntent().getSerializableExtra("numLogradouro");
		// recupera o codigo do logradouro
		codLogSelec = (String) getIntent()
				.getSerializableExtra("codLogradouro");
		tipLogSelec = (String) getIntent()
				.getSerializableExtra("tipLogradouro");
	}

	private void CarregaListaLogradouro() {
		LogradouroDAO dao = new LogradouroDAO(this);

		// dao.buscaLogs(edLogradouro.getText());
		logradouro = dao.getLista(txtLogradouro.getText().toString());

		if (logradouro.size() == 0) {
			Toast.makeText(getBaseContext(), "Nenhum logradouro localizado !",
					Toast.LENGTH_SHORT).show();
		}

		ArrayAdapter<Logradouro> adapter = new ArrayAdapter<Logradouro>(this,
				android.R.layout.simple_list_item_1, logradouro);

		lstLogradouros.setAdapter(adapter);

		dao.close();
	}
}
