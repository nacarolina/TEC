package br.com.cobrasin;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.tabela.Ait;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ListaLogradouro1 extends Activity {

	private long idAit = 0;
	private String codLogSelec = "";
	private String snumeroLog = "";
	private String tipLogSelec = "";
	private TextView edNumeroLog;
	ListView listaLogradouros;
	private String logradouroGps;
	boolean ModoBlitz=false;
	private String info = Utilitarios.getInfo();

	// grava a selecao dos RadioButton
	private void trataRadio(String radiosel) {
		// TODO Auto-generated method stub
		// grava o codigo do logradouro
		Ait aitx = new Ait();
		aitx.setId(idAit);
		aitx.setLogradourotipo(radiosel);

		AitDAO aitdao = new AitDAO(getBaseContext());
		aitdao.gravaLocalTipo(aitx);
		aitdao.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu me) {
		me.add("Editar Logradouro");
		// me.add("Status do AIT");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mt) {
		if (mt.getTitle() == "Editar Logradouro") {
			Intent i = null;
			AitDAO aitdao = new AitDAO(ListaLogradouro1.this);
			Cursor c = aitdao.getAit(idAit);
			logradouroGps = (String) getIntent().getSerializableExtra(
					"logradouroGps");
			i = new Intent(this, ListaLogradouro.class);
			try {
				i.putExtra(
						"codLogradouro",
						SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro"))));
				i.putExtra(
						"numLogradouro",
						SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouronum"))));
				i.putExtra("tipLogradouro", SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradourotipo"))));
				i.putExtra("logradouroGps", logradouroGps);
				i.putExtra("idAit", idAit);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(mt);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.endereco1);

		// pega o Id do AIT
		idAit = (Long) getIntent().getSerializableExtra("idAit");

		edNumeroLog = (TextView) findViewById(R.id.edNumeroLog1);

		// pegao o numero do logradouro
		snumeroLog = (String) getIntent().getSerializableExtra("numLogradouro");
		try{
			ModoBlitz= (boolean) getIntent().getSerializableExtra("ModoBlitz");
		}catch(Exception e){
		}

		edNumeroLog.setText(snumeroLog);

		// recupera o codigo do logradouro
		codLogSelec = (String) getIntent()
				.getSerializableExtra("codLogradouro");
		Button btnAddCruzamento = (Button) findViewById(R.id.btnAddCruzamento);
		// String Salvou = (String) getIntent().getSerializableExtra("salvou");
		// if (Salvou.contains("S")) {
		btnAddCruzamento.setVisibility(View.VISIBLE);
		// }
		// if (Salvou.contains("N")) {
		// btnAddCruzamento.setVisibility(View.INVISIBLE);
		// }
		btnAddCruzamento.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaLogradouro1.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Logradouro - Cruzamento");
				aviso.setMessage("Deseja adicionar esse logradouro como um cruzamento?");
				aviso.setNeutralButton("Não",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// finish();
							}
						});
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent i = new Intent(ListaLogradouro1.this,
										ListaLogradouro2.class);
								i.putExtra("idAit", idAit);
								i.putExtra("numLogradouro", snumeroLog);
								i.putExtra("codLogradouro", codLogSelec);
								i.putExtra("tipLogradouro", tipLogSelec);
								startActivity(i);

								finish();
							}
						});

				aviso.show();
			}
		});

		// informa novamente a selecao
		LogradouroDAO logdao = new LogradouroDAO(this);

		String sdescricao = logdao.buscaDescLog(String.valueOf(codLogSelec));

		logdao.close();

		TextView txtLogSelec = (TextView) findViewById(R.id.txtLogSelec1);
		txtLogSelec.setText("Selecionado:" + sdescricao);

		final Intent i = new Intent(this, ListaLogradouro.class);
		i.putExtra("codLogradouro", codLogSelec);
		i.putExtra("numLogradouro", snumeroLog);
		i.putExtra("tipLogradouro", "0");
		i.putExtra("logradouroGps", logradouroGps);
		i.putExtra("idAit", idAit);
		i.putExtra("ModoBlitz", ModoBlitz);

		Button btnEditLogradouro = (Button) findViewById(R.id.btnEditLogradouro);

		btnEditLogradouro.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaLogradouro1.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Edição de Logradouro");
				aviso.setMessage("Deseja editar o Logradouro ?");
				aviso.setNeutralButton("Não", null);
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								startActivity(i);
								finish();
							}
						});

				aviso.show();
			}
		});

		Button btGrava = (Button) findViewById(R.id.btGravaLogNum1);

		btGrava.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edNumeroLog = (TextView) findViewById(R.id.edNumeroLog1);
				if (edNumeroLog.getText().toString().trim().equals("")) {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							ListaLogradouro1.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Logradouro");
					aviso.setMessage("Preencha o número!");
					aviso.setNeutralButton("OK", null);
					aviso.show();
					return;
				}
				Ait aitx = new Ait();
				aitx.setId(idAit);

				aitx.setLogradouronum(edNumeroLog.getEditableText().toString());

				AitDAO aitdao = new AitDAO(getBaseContext());
				aitdao.gravaLocalNumero(aitx);
				aitdao.close();
				finish();
			}
		});

		// Button btRetornaLog = ( Button ) findViewById(R.id.btRetornalog1);
		// btRetornaLog.setOnClickListener(new View.OnClickListener() {

		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub

		// finish();
		// }
		// });

		RadioButton radio0 = (RadioButton) findViewById(R.id.radio00);

		radio0.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				trataRadio("1");
			}

		});

		RadioButton radio1 = (RadioButton) findViewById(R.id.radio11);

		radio1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				trataRadio("2");
			}
		});

		RadioButton radio2 = (RadioButton) findViewById(R.id.radio22);

		radio2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				trataRadio("3");
			}
		});

		// indica para o usuario a selecao anterior
		// recupera o codigo do logradouro
		tipLogSelec = (String) getIntent()
				.getSerializableExtra("tipLogradouro");

		// int ntipLogSelec = Integer.parseInt(tipLogSelec);
		switch (Integer.parseInt(tipLogSelec)) {
		case 1:
			radio0.setChecked(true);
			break;
		case 2:
			radio1.setChecked(true);
			break;
		case 3:
			radio2.setChecked(true);
			break;
		}

	}

}
