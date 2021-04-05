package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.dao.ParametroDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotaFiscal_Cadastro extends Activity {

	private EditText txtNumeroNota, txtPesoDeclarado;
	private long idAit = 0;

	private Button btnSalvarNF;

	private String arquivofoto, Id,TipoChamada,PBT_Valor;
	byte[] imagemBytes;
	private String info = "2012ANCOBRA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.notafiscal_cadastro);

		idAit = (Long) getIntent().getSerializableExtra("idAit");
		Id = (String) getIntent().getSerializableExtra("Id");
		TipoChamada = (String) getIntent().getSerializableExtra("TipoChamada");
		PBT_Valor = (String) getIntent().getSerializableExtra("PBT_Valor");

		txtNumeroNota = (EditText) findViewById(R.id.txtNumeroNota);
		txtPesoDeclarado = (EditText) findViewById(R.id.txtPesoDeclarado);
	//	txtPesoExcesso = (EditText) findViewById(R.id.txtPesoExcesso);
	//	txtPesoVeiculo = (EditText) findViewById(R.id.txtPesoVeiculo);
		btnSalvarNF = (Button) findViewById(R.id.btnSalvarNF);
		
	//	txtPesoVeiculo.setText(PBT_Valor);

		if (Id != null) {
			NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal_Cadastro.this);
			Cursor c = NfDAO.getDadosNF(Id);
			try {
				txtNumeroNota.setText(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("NumeroNota"))));
				txtPesoDeclarado.setText(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("PesoDeclarado"))));
		//		txtPesoExcesso.setText(SimpleCrypto.decrypt(info,
		//				c.getString(c.getColumnIndex("PesoExcesso"))));
		//		txtPesoVeiculo.setText(SimpleCrypto.decrypt(info,
		//				c.getString(c.getColumnIndex("PesoVeiculo"))));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			btnSalvarNF.setText("Salvar Alterações");
		} else {
			btnSalvarNF.setText("Salvar Nota Fiscal");
		}
		
		if(TipoChamada != null)
		{
			NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal_Cadastro.this);
			Cursor c = NfDAO.getDadosNF(Id);
			try {
				txtNumeroNota.setText(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("NumeroNota"))));
				txtPesoDeclarado.setText(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("PesoDeclarado"))));
				//txtPesoExcesso.setText(SimpleCrypto.decrypt(info,
				//		c.getString(c.getColumnIndex("PesoExcesso"))));
				//txtPesoVeiculo.setText(SimpleCrypto.decrypt(info,
				//		c.getString(c.getColumnIndex("PesoVeiculo"))));
				
				txtNumeroNota.setEnabled(false);
				txtPesoDeclarado.setEnabled(false);
				//txtPesoExcesso.setEnabled(false);
				//txtPesoVeiculo.setEnabled(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			btnSalvarNF.setText("Voltar");
		}

		// btnVisualizarFotoNF = (Button)
		// findViewById(R.id.btnVisualizarFotoNF);
		/*
		 * btnVisualizarFotoNF.setEnabled(false);
		 * btnVisualizarFotoNF.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent i = new Intent(NotaFiscal_Cadastro.this,
		 * NotaFiscal_Foto.class); String NumeroNota =
		 * txtNumeroNota.getText().toString(); try { i.putExtra("idAit",
		 * SimpleCrypto.encrypt(info,String.valueOf(idAit)));
		 * i.putExtra("NumeroNF",SimpleCrypto.encrypt(info, NumeroNota)); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * startActivity(i); } });
		 */

		txtNumeroNota.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// here is your code
				String NumeroNota = txtNumeroNota.getText().toString();
				/*
				 * if (NumeroNota.equals("")) {
				 * btnFotografarNF.setEnabled(false); } else {
				 * btnFotografarNF.setEnabled(true); }
				 */
				// btnVisualizarFotoNF.setEnabled(false);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		btnSalvarNF.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String NumeroNota = txtNumeroNota.getText().toString();
				if (NumeroNota.equals("")) {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							NotaFiscal_Cadastro.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Preencha o Número da Nota Fiscal!");
					aviso.setNeutralButton("OK", null);
					aviso.show();
					return;
				}
				
				String PesoDeclarado = txtPesoDeclarado.getText().toString();
				if (PesoDeclarado.equals("")) {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							NotaFiscal_Cadastro.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Preencha o Peso Declarado da Nota Fiscal!");
					aviso.setNeutralButton("OK", null);
					aviso.show();
					return;
				}
				
				if (btnSalvarNF.getText().toString()
						.equals("Voltar")) {
					Intent i = new Intent(NotaFiscal_Cadastro.this, ListaNf_ExibeAit_Excesso.class);
					i.putExtra("idAit", idAit);
					startActivity(i);
					finish();
					return;
				}

				br.com.cobrasin.tabela.NotaFiscal Nf = new br.com.cobrasin.tabela.NotaFiscal();
				NotaFiscalDAO NfDAO = new NotaFiscalDAO(
						NotaFiscal_Cadastro.this);

				Nf.setIdait(idAit);
				Nf.setNumeroNota(txtNumeroNota.getText().toString());
				Nf.setPesoDeclarado(txtPesoDeclarado.getText().toString());
				//Nf.setPesoExcesso(txtPesoExcesso.getText().toString());
				//Nf.setPesoVeiculo(txtPesoVeiculo.getText().toString());

				if (btnSalvarNF.getText().toString()
						.equals("Salvar Nota Fiscal")) {
					NfDAO.SalvaNotaFiscal(Nf);
				}
				if (btnSalvarNF.getText().toString()
						.equals("Salvar Alterações")) {
					Nf.setId(Id);
					NfDAO.SalvarAlteracao(Nf);

				}

				txtNumeroNota.setText("");
				txtPesoDeclarado.setText("");
				//txtPesoExcesso.setText("");
				//txtPesoVeiculo.setText("");

				NfDAO.close();
				Intent i = new Intent(NotaFiscal_Cadastro.this,
						NotaFiscal.class);
				i.putExtra("idAit", idAit);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (btnSalvarNF.getText().toString()
					.equals("Voltar")) {
				Intent i = new Intent(NotaFiscal_Cadastro.this, ListaNf_ExibeAit_Excesso.class);
				i.putExtra("idAit", idAit);
				startActivity(i);
				finish();
			}
			else
			{
			Intent i = new Intent(NotaFiscal_Cadastro.this, NotaFiscal.class);
			i.putExtra("idAit", idAit);
			startActivity(i);
			finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
