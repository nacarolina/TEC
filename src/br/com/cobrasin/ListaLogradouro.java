package br.com.cobrasin;

import java.util.List;



import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.Parametro;


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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



public class ListaLogradouro extends Activity {

	private long idAit = 0 ;
	private String  codLogSelec = "" ;
	private String codLogSelecAnterior = "";
	private String snumeroLog = "";
	private String tipLogSelec = "";
	private String descLogAnt = "";
	private String logradouroGps;
	private TextView edNumeroLog;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton radio2;

	private String info = Utilitarios.getInfo();
	
	ListView listaLogradouros ;

	private void chamaLista(String scodigo)
	{
		 
		Intent i = new Intent(this, ListaLogradouro1.class);
		i.putExtra("codLogradouro",scodigo);
		i.putExtra("numLogradouro", edNumeroLog.getText().toString());
		i.putExtra("tipLogradouro", "0");
		if (radio0.isChecked()) i.putExtra("tipLogradouro", "1");
		if (radio1.isChecked()) i.putExtra("tipLogradouro", "2");
		if (radio2.isChecked()) i.putExtra("tipLogradouro", "3");
		i.putExtra("idAit", idAit);
		startActivity(i);
		finish();
		
	}
	private void trataPesquisa() {
		// TODO Auto-generated method stub
					
		EditText edLogradouro = (EditText) findViewById(R.id.edLogradouro); 
		
		if (edLogradouro.getText().toString() != null)
		{

			listaLogradouros = (ListView) findViewById(R.id.listaLogradouros);
					
			LogradouroDAO dao = new LogradouroDAO(this);
			
			//dao.buscaLogs(edLogradouro.getText());
			final List<Logradouro> logradouro = dao.getLista(edLogradouro.getText().toString());

			if (logradouro.size() == 0)
			{
				Toast.makeText(getBaseContext(), "Nenhum logradouro localizado !", Toast.LENGTH_SHORT).show();
			}
			
			ArrayAdapter<Logradouro> adapter = new ArrayAdapter<Logradouro>(this,android.R.layout.simple_list_item_1,logradouro);
			
			listaLogradouros.setAdapter(adapter);
			
			dao.close();
			
			listaLogradouros.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
					  Object listItem = listaLogradouros.getItemAtPosition(arg2);
					  	  	  
					  final String scodigo = logradouro.get(arg2).getCodigo(); 
					  String sdescricao = logradouro.get(arg2).getDescricao();
					  /*
					  // pega o codigo 
					  String scodigo = listItem.toString(),scodigo1 = "";
					  String sdescricao ="";
					  
					  for ( int nx = 0 ; nx < scodigo.length() ; nx++)
					  {
						  if ( scodigo.charAt(nx) == ' ') {
							  
							  // pega a descricao
							  sdescricao = scodigo.substring(nx+3, scodigo.length());
							  nx = scodigo.length();
						
						  }
						  else
						  {
							  scodigo1 = scodigo.substring(0,nx+1);
						  }
					  }
					  */
					  TextView txtLogSelec = (TextView) findViewById(R.id.txtLogSelec);
					  txtLogSelec.setText("Selecionado:"+ sdescricao);
					  
					  //grava o codigo do logradouro
					  Ait aitx = new Ait();
					  aitx.setId(idAit);
					  aitx.setLogradouro(scodigo) ;
					  
					  AitDAO aitdao = new AitDAO(getBaseContext());
					  aitdao.gravaLocal(aitx);
					  Cursor c = aitdao.getAit(idAit);
					  aitdao.close(); 
					  
					  try {
						if (!SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("logradouro2")))
									.contains("NAO")) {
							LogradouroDAO ldao= new LogradouroDAO(ListaLogradouro.this);
							String descricao = ldao.buscaDescLog(SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("logradouro2"))));
									
							if (descricao.contains(sdescricao)) {
								AlertDialog.Builder aviso = new AlertDialog.Builder(
										ListaLogradouro.this);
								aviso.setIcon(android.R.drawable.ic_dialog_alert);
								aviso.setTitle("Logradouro - Cruzamento");
								aviso.setMessage("Não é possível selecionar o mesmo logradouro!");
								aviso.setNeutralButton("OK", null);

								aviso.show();
								return;
							} else {
							Intent i = new Intent(ListaLogradouro.this,
									ListaLogradouro3.class);
							i.putExtra("idAit", idAit);
							i.putExtra("numLogradouro", snumeroLog);
							i.putExtra("codLogradouro", scodigo);
							i.putExtra("tipLogradouro", tipLogSelec);
							i.putExtra("codLogradouro2", SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("logradouro2"))));
							startActivity(i);

							finish();
							return;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  codLogSelec = scodigo;
					  
					  chamaLista(scodigo);
					  /*
					  	AlertDialog.Builder aviso = new AlertDialog.Builder(ListaLogradouro.this);
				         aviso.setIcon(android.R.drawable.ic_dialog_alert);
				         aviso.setTitle("Seleção de Logradouro");
				         aviso.setMessage("Vai para Outra tela ?");
				         aviso.setNegativeButton("Não", null);
				         aviso.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
						
								  chamaLista(scodigo);								
							}
						});
				         
				         aviso.show();
				        */	
					  
					  //finish();
					  
				}
			});
		}
	}
	
	// grava a selecao dos RadioButton
	private void trataRadio(String radiosel) {
		// TODO Auto-generated method stub
				
	    //grava o codigo do logradouro
		  Ait aitx = new Ait();
		  aitx.setId(idAit);
		  aitx.setLogradourotipo(radiosel);
		  
		  AitDAO aitdao = new AitDAO(getBaseContext());
		  aitdao.gravaLocalTipo(aitx);
		  aitdao.close(); 
		  
		
	}
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.endereco);
	        
	        // pega o Id do AIT 
	        idAit = (Long) getIntent().getSerializableExtra("idAit");
	     	
	        edNumeroLog = (TextView) findViewById(R.id.edNumeroLog);
	        
	        // pegao o numero do logradouro
			snumeroLog = (String) getIntent().getSerializableExtra("numLogradouro");
			
	        edNumeroLog.setText(snumeroLog);
	        
	        //recupera o codigo do logradouro
	        codLogSelec = (String) getIntent().getSerializableExtra("codLogradouro");
	        
	        logradouroGps= (String) getIntent().getSerializableExtra("logradouroGps");

	        if (logradouroGps == null) {
				
			}
	        else {
		        if (logradouroGps.contains("\"")) {
					logradouroGps = logradouroGps.replaceAll("\"","");
				}
			}
	        // se não escolheu ainda pergunta se quer pegar do AIT anterior
	        buscaCodLogAnt(codLogSelec);
	        
	        EditText edLogradouro = (EditText) findViewById(R.id.edLogradouro); 
	        edLogradouro.setText(logradouroGps);
	        
        	// informa novamente a selecao
	        LogradouroDAO logdao = new LogradouroDAO(this);
	        String sdescricao = logdao.buscaDescLog(String.valueOf(codLogSelec));
	        logdao.close();
	        
	        TextView txtLogSelec = (TextView) findViewById(R.id.txtLogSelec);
			txtLogSelec.setText("Selecionado:"+ sdescricao);
				
		//	if (snumeroLog.length() == 0) {
				
		//	}
		//	else{			
		//		Intent i = new Intent(this, ListaLogradouro1.class);
			//		i.putExtra("codLogradouro",codLogSelec);
			//		i.putExtra("numLogradouro", edNumeroLog.getText().toString());
				
			//	try {
			//	if (radio0.isChecked())
					//	{
			//		i.putExtra("tipLogradouro", "1");
			//	}
			//if (radio1.isChecked()) 
			//	{
			//	i.putExtra("tipLogradouro", "2");
			//}
			//if (radio2.isChecked())
			//{
			//	i.putExtra("tipLogradouro", "3");
			//}
			//} catch (Exception e) {
			//	// TODO: handle exception
			//	i.putExtra("tipLogradouro", "0");
			//}
			//
			//i.putExtra("idAit", idAit);
			//startActivity(i);
			//finish();
			//
			//}		
			
	//		Button btPrxTela = (Button) findViewById(R.id.btProximaLog);
			
		/*	btPrxTela.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					chamaLista(codLogSelec );
				}
			});*/
			
	        Button btPesquisa = (Button) findViewById(R.id.btPesquisa);
	        
	        btPesquisa.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					trataPesquisa();
				}

				
			} );
	        
	        
	        Button btGrava = ( Button ) findViewById(R.id.btGravaLogNum);
	        
	        btGrava.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					  				  
					  Ait aitx = new Ait();
					  aitx.setId(idAit);
					  
					  edNumeroLog = (TextView) findViewById(R.id.edNumeroLog);
					  aitx.setLogradouronum(edNumeroLog.getEditableText().toString());
					  
					  AitDAO aitdao = new AitDAO(getBaseContext());
					  aitdao.gravaLocalNumero(aitx);
					  aitdao.close();
					  
					  finish();
				}
			});
	        
	        
	        Button btRetornaLog = ( Button ) findViewById(R.id.btRetornalog);
	        btRetornaLog.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				
						finish();
				}
			});
	         
	        radio0 = (RadioButton) findViewById(R.id.radio0);
	        
	        radio0.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					trataRadio("1");
				}
				
			});
	        
	        radio1 = (RadioButton) findViewById(R.id.radio1);
	        
	        radio1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					trataRadio("2");
				}
			});
	        
	        radio2 = (RadioButton) findViewById(R.id.radio2);
	        
	        radio2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					trataRadio("3");
				}
			});
	        
	        // indica para o usuario a selecao anterior
	        //recupera o codigo do logradouro
	        radio0.setChecked(false);
	        radio1.setChecked(false);
	        radio2.setChecked(false);
	        tipLogSelec = (String) getIntent().getSerializableExtra("tipLogradouro");

	        int ntipLogSelec = Integer.parseInt(tipLogSelec);
	        switch( ntipLogSelec )
	        {
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
	 
 
	private void buscaCodLogAnt(String codLogSelec2) {
		// TODO Auto-generated method stub

		//*****************
		// nao selecionou ?
		//*****************
		if (codLogSelec2.contains("00000"))
		{
			
			// levanta o campo proximo ait 
			ParametroDAO pardao = new ParametroDAO(ListaLogradouro.this);
			Cursor c = pardao.getParametros();
			
			// obtem o anteior ( pega o ID ) 
			long atual = 0;
			try {
				atual = Long.parseLong(SimpleCrypto.decrypt(info,c.getString(0)));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c.close();
			atual--;
			String ultimoait = String.format("%07d", atual); 
			AitDAO aitdao = new AitDAO(ListaLogradouro.this);
			
			if ( atual > 0 )
			{
			
				// obtem o ultimo logradouro
				c = aitdao.getAit1(ultimoait);
				try {
					codLogSelecAnterior = SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradouro")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				c.close();
				
				LogradouroDAO logdao = new LogradouroDAO(ListaLogradouro.this);
				descLogAnt = logdao.buscaDescLog(codLogSelecAnterior);
				
				logdao.close();
				aitdao.close();
				
				//*************************************************************************************
				// Quando o usuário não selecionou o Logradouro no Auto Anterior , descrição vem Zerada
				//*************************************************************************************
				if ( descLogAnt.length() > 0  )
				{
					AlertDialog.Builder aviso = new AlertDialog.Builder(ListaLogradouro.this);
			        aviso.setIcon(android.R.drawable.ic_dialog_alert);
			        aviso.setTitle("Logradouro");
			        aviso.setMessage("Utiliza Logradouro do AIT anterior ? / " + descLogAnt);
			        aviso.setNegativeButton("Não", null);
			        aviso.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
						
						@Override
					public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
		
							codLogSelec = codLogSelecAnterior;
							
							 TextView txtLogSelec = (TextView) findViewById(R.id.txtLogSelec);
							 txtLogSelec.setText("Selecionado:"+ descLogAnt);
							  
							  //grava o codigo do logradouro
							  Ait aitx = new Ait();
							  aitx.setId(idAit);
							  aitx.setLogradouro(codLogSelec) ;
							  
							  AitDAO aitdao = new AitDAO(getBaseContext());
							  aitdao.gravaLocal(aitx);
							  aitdao.close(); 
		
							  
							  chamaLista(codLogSelec );
							  
							  // inserido em 08.05.2012
							  //finish();
							
						}
					});
			    
			        aviso.show();
				}
			}
		}
		
		

	}
	 
	 
	 /*
	 protected void onListItemClick(ListView l, View v, int position, long id) {
	  // TODO Auto-generated method stub
	  super.onListItemClick(l, v, position, id);
	  
	  // Get the data associated with selected item
	  Object item = l.getItemAtPosition(position);
	  // Display data/text of the item/row clicked
	  //Toast.makeText(this,"Selection: "+ item.toString(), Toast.LENGTH_SHORT).show();
	  	  	  
	  // pega o codigo 
	  String scodigo = item.toString(),scodigo1 = "";
	  int codigolog = 0 ;
	  
	  for ( int nx = 0 ; nx < scodigo.length() ; nx++)
	  {
		  if ( scodigo.charAt(nx) == '-') {
			  nx = scodigo.length();
		  }
		  else
		  {
			  scodigo1 = scodigo.substring(0,nx+1);
		  }
	  }
	  
	  codigolog = Integer.parseInt(scodigo1);
	  
	  Ait aitx = new Ait();
	  aitx.setId(idAit);
	  aitx.setLogradouro(codigolog ) ;
	  
	  AitDAO aitdao = new AitDAO(this);
	  aitdao.gravaLocal(aitx);
	  aitdao.close();
	  
	  finish();
	 }
	 */
}
