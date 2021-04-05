package br.com.cobrasin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class bloqueio_user_adm extends Activity {
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.bloqueio_user_adm);
	        
	 }

	public void AutenticarAdm(View view) {
		EditText edPasswordADM = (EditText)this.findViewById(R.id.edPasswordADM);
		String passwordAdm = edPasswordADM.getText().toString();
		if(passwordAdm.equals("cobra3522")) {
			Intent aviso1 = new Intent(this, ListaParametros.class);
			this.startActivity(aviso1);
			this.finish();
		} else {
			AlertDialog.Builder aviso = new AlertDialog.Builder(this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Erro na Autenticação do Administrador!");
			aviso.setPositiveButton("OK", null);
			aviso.show();
		}

	}

	 
}
