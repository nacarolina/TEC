package br.com.cobrasin;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Municipio;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PostoAgente extends Activity{
	
	private String info = Utilitarios.getInfo();
	
	private EditText txtPosto;
	private Spinner spUF,spCidade;
	private Button btnConfirmar;
	
	private String UF,Cidade,IdMunicipio,agente,Posto;
	
	private  List<String> Lista_Cidade = new ArrayList<String>();
	
	private boolean CarregaCidadeSel = false;
	private int PosicaoCidade = 0;
	private String UFSel = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posto_agente);
		
		txtPosto = (EditText) findViewById(R.id.txtPosto);
		spUF = (Spinner) findViewById(R.id.spUF_Posto);
		spCidade = (Spinner) findViewById(R.id.spMunicipio_Posto);
		btnConfirmar = (Button) findViewById(R.id.btnConfirma_Posto);
		
		Posto = (String) getIntent().getSerializableExtra("Posto");
		
		IdMunicipio = (String) getIntent().getSerializableExtra("IdMunicipio");
		agente = (String) getIntent().getSerializableExtra("agente");
		
		txtPosto.setText(Posto);
		
		final MunicipioDAO MuDAO = new MunicipioDAO(PostoAgente.this);
		final List<String> Lista_UF = MuDAO.GetListaUF();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Lista_UF);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spUF.setAdapter(dataAdapter);
		spUF.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if(CarregaCidadeSel = false)
		    	{
		    		spCidade.setAdapter(null);
					Lista_Cidade = MuDAO.GetListaCidade(Lista_UF.get(position)
							.toString());
					UF = Lista_UF.get(position).toString();
					ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(
							PostoAgente.this,
							android.R.layout.simple_spinner_item, Lista_Cidade);

					dataAdapterCidade
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spCidade.setAdapter(dataAdapterCidade);
		    	}
		    	else
		    	{
		    		UF = Lista_UF.get(position).toString();
		    		if(!UFSel.equals(UF))
		    		{
		    			CarregaCidadeSel = false;
		    			spCidade.setAdapter(null);
						Lista_Cidade = MuDAO.GetListaCidade(Lista_UF.get(position)
								.toString());
						ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(
								PostoAgente.this,
								android.R.layout.simple_spinner_item, Lista_Cidade);

						dataAdapterCidade
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						spCidade.setAdapter(dataAdapterCidade);
		    		}
		    	}
		    	
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		spCidade.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) { 
		    	try {
					 Cidade = Lista_Cidade.get(position).toString();
				} catch (Exception e) {
					// TODO: handle exception
				}
               
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
		if(Lista_Municipio.size() >0)
		{
		try {
			
			spUF.setSelection(Lista_UF.indexOf(Lista_Municipio.get(0).getUF()));
			UFSel = Lista_Municipio.get(0).getUF();
			spCidade.setAdapter(null);  
	    	Lista_Cidade = MuDAO.GetListaCidade(Lista_Municipio.get(0).getUF().toString());
            UF = Lista_Municipio.get(0).getUF().toString();
			ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(PostoAgente.this,android.R.layout.simple_spinner_item, Lista_Cidade);

			dataAdapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spCidade.setAdapter(dataAdapterCidade);
			PosicaoCidade = Lista_Cidade.indexOf(Lista_Municipio.get(0).getCidade());
			CarregaCidadeSel = true;
			spCidade.setSelection(Lista_Cidade.indexOf(Lista_Municipio.get(0).getCidade()));
			
		} catch (Exception e) {
			// TODO: handle exception
			String Erro = e.getMessage();

			
		}
		}

		
		btnConfirmar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (txtPosto.getText().toString().equals("")) {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							PostoAgente.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Posto do Agente");
					aviso.setMessage("Preencha o Posto!");
					aviso.setNeutralButton("OK", null);

					aviso.show();
					return;
				}
				if(UF.equals("Selecione o Estado"))
				{
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							PostoAgente.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Posto do Agente");
					aviso.setMessage("Selecione o estado!");
					aviso.setNeutralButton("OK", null);

					aviso.show();
					return;
				}
				if(Cidade.equals("Selecione a Cidade"))
				{
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							PostoAgente.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Posto do Agente");
					aviso.setMessage("Selecione a cidade!");
					aviso.setNeutralButton("OK", null);

					aviso.show();
					return;
				}
				
				AgenteDAO aDAO = new AgenteDAO(PostoAgente.this);
				Agente a = new Agente();
				String IdMunicipio = MuDAO.GetIdCidade(UF, Cidade);
				try {
					a.setPosto(SimpleCrypto.encrypt(info,txtPosto.getText().toString()));
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					a.setIdMunicipio(SimpleCrypto.encrypt(info,IdMunicipio));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					a.setCodigo(SimpleCrypto.encrypt(info, agente));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				aDAO.altera(a);
				aDAO.close();
				Toast.makeText( getBaseContext() , "Posto do Agente registrado!",Toast.LENGTH_SHORT).show();
			     	finish();
			}
		});
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final MunicipioDAO MuDAO = new MunicipioDAO(PostoAgente.this);
		if (txtPosto.getText().toString().equals("")) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					PostoAgente.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Posto do Agente");
			aviso.setMessage("Preencha o Posto!");
			aviso.setNeutralButton("OK", null);

			aviso.show();
			//return;
		}
		else if(UF.equals("Selecione o Estado"))
		{
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					PostoAgente.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Posto do Agente");
			aviso.setMessage("Selecione o estado!");
			aviso.setNeutralButton("OK", null);

			aviso.show();
			//return;
		}
		else if(Cidade.equals("Selecione a Cidade"))
		{
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					PostoAgente.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Posto do Agente");
			aviso.setMessage("Selecione a cidade!");
			aviso.setNeutralButton("OK", null);

			aviso.show();
			//return;
		}
		else
		{
		AgenteDAO aDAO = new AgenteDAO(PostoAgente.this);
		Agente a = new Agente();
		String IdMunicipio = MuDAO.GetIdCidade(UF, Cidade);
		try {
			a.setPosto(SimpleCrypto.encrypt(info,txtPosto.getText().toString()));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			a.setIdMunicipio(SimpleCrypto.encrypt(info,IdMunicipio));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			a.setCodigo(SimpleCrypto.encrypt(info, agente));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aDAO.altera(a);
		aDAO.close();
		Toast.makeText( getBaseContext() , "Posto do Agente registrado!",Toast.LENGTH_SHORT).show();
	     	finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
