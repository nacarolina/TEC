package br.com.cobrasin;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.tabela.Ait;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ListaLogradouro3 extends Activity {

	TextView lblLogradouro1, lblLogradouro2;
	Button btnEditLogra1, btnEditLogra2, btnRemoverLogra2, btnVoltarAIT;
	private long idAit = 0;
	private String IdLogra1 = "";
	private String DescLogra1 = "";
	private String IdLogra2 = "";
	private String TipoLog = "";
	private String info = Utilitarios.getInfo();
	boolean ModoBlitz=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endereco3);
		findViewById();
		RecuperaValores();
		LogradouroDAO logdao = new LogradouroDAO(this);
		final String Logra1 = logdao.buscaDescLog(String.valueOf(IdLogra1));
		lblLogradouro1.setText(Logra1);
		final String Logra2 = logdao.buscaDescLog(String.valueOf(IdLogra2));
		try{
			ModoBlitz= (boolean) getIntent().getSerializableExtra("ModoBlitz");
		}catch(Exception e){
		}
		lblLogradouro2.setText(Logra2);
		btnEditLogra1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ListaLogradouro3.this,
						ListaLogradouro.class);
				i.putExtra("codLogradouro", IdLogra1);
				i.putExtra("numLogradouro", DescLogra1);
				i.putExtra("tipLogradouro", TipoLog);
				i.putExtra("idAit", idAit);
				startActivity(i);
				finish();
			}
		});
		btnEditLogra2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ListaLogradouro3.this,
						ListaLogradouro2.class);
				i.putExtra("codLogradouro", IdLogra1);
				i.putExtra("numLogradouro", DescLogra1);
				i.putExtra("tipLogradouro", TipoLog);
				i.putExtra("salvou", "S");
				i.putExtra("idAit", idAit);
				startActivity(i);
				finish();
			}
		});
		btnVoltarAIT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnRemoverLogra2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaLogradouro3.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Logradouro - Cruzamento");
				aviso.setMessage("Ao remover o 2° logradouro irá deixar de ser um Cruzamento!\r\nDeseja remover o 2° logradouro?");
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Ait aitx = new Ait();
								aitx.setId(idAit);

								aitx.setLogradouro2("NAO");
								AitDAO aitdao = new AitDAO(getBaseContext());
								aitdao.gravaLocal2(aitx);
								aitdao.close();

								Intent i = new Intent(ListaLogradouro3.this,
										ListaLogradouro1.class);
								i.putExtra("idAit", idAit);
								i.putExtra("numLogradouro", DescLogra1);
								i.putExtra("codLogradouro", IdLogra1);
								i.putExtra("tipLogradouro", TipoLog);
								startActivity(i);

								finish();
							}
						});
				aviso.setNegativeButton("Não", null);
				aviso.show();
			}
		});
	}

	private void findViewById() {
		lblLogradouro1 = (TextView) findViewById(R.id.lblLogra1);
		lblLogradouro2 = (TextView) findViewById(R.id.lblLogra2);
		btnEditLogra1 = (Button) findViewById(R.id.btnEditLogra1);
		btnEditLogra2 = (Button) findViewById(R.id.btnEditLogra2);
		btnRemoverLogra2 = (Button) findViewById(R.id.btnRemoverLogra2);
		btnVoltarAIT = (Button) findViewById(R.id.btnVoltarAIT);
	}

	private void RecuperaValores() {
		idAit = (Long) getIntent().getSerializableExtra("idAit");
		DescLogra1 = (String) getIntent().getSerializableExtra("numLogradouro");
		IdLogra1 = (String) getIntent().getSerializableExtra("codLogradouro");
		IdLogra2 = (String) getIntent().getSerializableExtra("codLogradouro2");
		TipoLog = (String) getIntent().getSerializableExtra("tipLogradouro");
	}
}
