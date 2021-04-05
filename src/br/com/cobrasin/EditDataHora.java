package br.com.cobrasin;

import java.util.Date;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditDataHora extends Activity {
	
	private String dtEdit;
	private String hrEdit;
	private Long idAit;
	private String info = Utilitarios.getInfo();
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.editdthr);
	        
	        idAit =  (Long) getIntent().getSerializableExtra("idAit");
	        dtEdit =  (String) getIntent().getSerializableExtra("dtEdit");
	        hrEdit=  (String) getIntent().getSerializableExtra("hrEdit");
	        
	        Button btSalvar = (Button) findViewById(R.id.btSalvarDtHrMod);
	        final EditText edDtEdit = (EditText) findViewById(R.id.edDataModificada);
	        final EditText edHrEdit = (EditText) findViewById(R.id.edHoraModificada);
	        
	        edDtEdit.setText(dtEdit);
	        edHrEdit.setText(hrEdit);
	        
	     //  final Intent i = new Intent(this, ExibeDadosAit.class);	
	        
	        btSalvar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String Data = edDtEdit.getText().toString();
					String Hora = edHrEdit.getText().toString();
					int Retorno;
					if (Data.contains(".")) {
						Data = Data.replace('.','/');	
					}
					if (Data.contains("-"))
					{
						Data = Data.replace('-','/');	
					}
					
						Utilitarios u = new Utilitarios();
						AitDAO aitdao = new AitDAO(EditDataHora.this);
						Cursor c = aitdao.getAit(idAit);
						String DataAIT = "";
						String HoraAIT ="";
						try {
							DataAIT = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("data")));
							HoraAIT = SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora")));
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
						long dataAitcompara = Date.parse(DataAIT);
						long datacompara = Date.parse(Data);
						if (datacompara > dataAitcompara) {
							 AlertDialog.Builder aviso = new AlertDialog.Builder(EditDataHora.this);
						        aviso.setIcon(android.R.drawable.ic_dialog_alert);
						        aviso.setTitle("TEC");
						        aviso.setMessage("Data Hora não pode ser maior que a data do lavramento!"); 
						        aviso.setNeutralButton("OK", null);
						        aviso.show();
						}
						else {
						try {
						Retorno = (int) u.calculaHoraAit(Data +" "+ Hora,DataAIT + " "+HoraAIT);
						int segundos = Retorno / 1000; //Pegamos em segundos  
						int minutos = segundos / 60; 
						int horas = minutos / 60;
						
					int hrAIT = Integer.parseInt(HoraAIT.substring(0,2));
					int hrModificada = Integer.parseInt(Hora.substring(0,2));
					int mnAIT = Integer.parseInt(HoraAIT.substring(3,5));
					int mnModificado = Integer.parseInt(Hora.substring(3,5));
						
						if (horas < 24 && horas >0 || horas == 0 && hrAIT > hrModificada || hrAIT == hrModificada && mnAIT > mnModificado || mnAIT == mnModificado) {
							Ait ait = new Ait();
							AitDAO aitDao = new AitDAO(EditDataHora.this);
							ait.setId(idAit);
							ait.setdtEdit(Data);
							ait.sethrEdit(Hora);
							aitDao.gravaDtEdit(ait);
							aitDao.gravaHrEdit(ait);

							finish();	
						}
						else {
							 AlertDialog.Builder aviso = new AlertDialog.Builder(EditDataHora.this);
						        aviso.setIcon(android.R.drawable.ic_dialog_alert);
						        aviso.setTitle("TEC");
						        aviso.setMessage("Data Hora não pode ser maior que a data do lavramento!"); 
						        aviso.setNeutralButton("OK", null);
						        aviso.show();
						}
					}
					catch (Exception e) 
					{
						// TODO: handle exception
						 AlertDialog.Builder aviso = new AlertDialog.Builder(EditDataHora.this);
					        aviso.setIcon(android.R.drawable.ic_dialog_alert);
					        aviso.setTitle("TEC");
					        aviso.setMessage("Data ou hora incorreta!"); 
					        aviso.setNeutralButton("OK", null);
					        aviso.show();
					
					}
				}
			}
		});

   }
}
