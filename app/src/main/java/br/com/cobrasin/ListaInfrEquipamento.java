package br.com.cobrasin;


import java.text.NumberFormat;
import java.util.Locale;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ListaInfrEquipamento extends Activity {

	
	private long idAit = 0 ;
	
	private EditText edEquipamento;
	private EditText edMedicaoreg;
	private EditText edMedicaocon;
	private EditText edLimitereg;
		
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.equipamento);
	
	        edEquipamento = (EditText) findViewById(R.id.edEquipamento);
	        edMedicaoreg = (EditText) findViewById(R.id.edMedicaoReg);
	        edMedicaocon = (EditText) findViewById(R.id.edMedicaoCon);
	        edLimitereg = (EditText) findViewById(R.id.edLimiteReg);
	        
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	        
	        // pega os outros dados
	        String equipamento = (String) getIntent().getSerializableExtra("equipamento");
	        String medicaoreg = (String) getIntent().getSerializableExtra("medicaoreg");
	        String medicaocon = (String) getIntent().getSerializableExtra("medicaocon");
	        String limitereg =  (String) getIntent().getSerializableExtra("limitereg");
	        
	        edEquipamento.setText(equipamento);
	        edMedicaoreg.setText(medicaoreg);
	        edMedicaocon.setText(medicaocon);
	        edLimitereg.setText(limitereg);
	        edEquipamento.requestFocus();
	        
		try {
			edMedicaoreg.setText(edMedicaoreg.getText().toString().replace(",", "."));
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			edMedicaocon.setText(edMedicaocon.getText().toString().replace(",", "."));

		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			edLimitereg.setText(edLimitereg.getText().toString().replace(",", "."));
		} catch (Exception e) {
			// TODO: handle exception
		}  
	        //edCPF.setInputType(InputType.TYPE_CLASS_NUMBER);
	        //edPGU.setInputType(InputType.TYPE_CLASS_NUMBER);
	        
	  //      Button btretorna = (Button) findViewById(R.id.btRetornaEquip);
	  //      btretorna.setOnClickListener(new View.OnClickListener() {
				
	//			@Override
	//			public void onClick(View arg0) {
	//				// TODO Auto-generated method stub
//
		//			finish();
		//		}
		//	});
	        
	        
	        Button btgrava = (Button ) findViewById(R.id.btGravaEquip);
	        btgrava.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
						  
						  
					//grava infração feita por equipamento
					if (edEquipamento.equals("") == false || edMedicaoreg.equals("") == false ) {
											Ait aitx = new Ait();
					aitx.setId(idAit);
					
					aitx.setEquipamento(edEquipamento.getText().toString());
					aitx.setMedicaoreg(Utilitarios.formatar(edMedicaoreg.getText().toString()));
					aitx.setMedicaocon(Utilitarios.formatar(edMedicaocon.getText().toString()));
					aitx.setLimitereg(Utilitarios.formatar(edLimitereg.getText().toString()));
					
					AitDAO aitdao = new AitDAO(getBaseContext());
					aitdao.gravaEquip(aitx);
					aitdao.close(); 
					}
			  
					finish();
					
				}
			});
	 }
	 
}
