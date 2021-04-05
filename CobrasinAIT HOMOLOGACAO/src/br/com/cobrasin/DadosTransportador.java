package br.com.cobrasin;

import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Municipio;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class DadosTransportador extends Activity{
	
	private EditText txtNome, txtCPFCNPJ, txtEndereco, txtBairro;;
	private String info = Utilitarios.getInfo();
	private long idAit = 0 ;
	
	private Spinner spUF, spMunicipio;
	
	private String Nome,CPFCNPJ,Endereco,UF,Cidade,Bairro,IdMunicipio;
	
    private  List<String> Lista_Cidade = new ArrayList<String>();
	
	private boolean CarregaCidadeSel = false;
	private int PosicaoCidade = 0;
	private String UFSel = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.dados_transportador);
		
		idAit = (Long) getIntent().getSerializableExtra("idAit");
		Nome = (String) getIntent().getSerializableExtra("Nome");
		CPFCNPJ = (String) getIntent().getSerializableExtra("CPFCNPJ");
		Endereco = (String) getIntent().getSerializableExtra("Endereco");
		Bairro = (String) getIntent().getSerializableExtra("Bairro");
		IdMunicipio = (String) getIntent().getSerializableExtra("IdMunicipio");
		
		txtNome = (EditText) findViewById(R.id.txtNome_Transportador);
		txtCPFCNPJ = (EditText) findViewById(R.id.txtCPFCNPJ_Transportador);
		txtEndereco = (EditText) findViewById(R.id.txtEndereco_Transportador);
		txtBairro = (EditText) findViewById(R.id.txtBairro_Transportador);

		spUF = (Spinner) findViewById(R.id.spUF_Transportador);
		spMunicipio = (Spinner) findViewById(R.id.spMunicipio_Transportador);
		
		final MunicipioDAO MuDAO = new MunicipioDAO(DadosTransportador.this);
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
					spMunicipio.setAdapter(null);
					Lista_Cidade = MuDAO.GetListaCidade(Lista_UF.get(position)
							.toString());
					UF = Lista_UF.get(position).toString();
					ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(
							DadosTransportador.this,
							android.R.layout.simple_spinner_item, Lista_Cidade);

					dataAdapterCidade
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spMunicipio.setAdapter(dataAdapterCidade);
		    	}
		    	else
		    	{
		    		UF = Lista_UF.get(position).toString();
		    		if(!UFSel.equals(UF))
		    		{
		    			CarregaCidadeSel = false;
		    			spMunicipio.setAdapter(null);
						Lista_Cidade = MuDAO.GetListaCidade(Lista_UF.get(position)
								.toString());
						ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(
								DadosTransportador.this,
								android.R.layout.simple_spinner_item, Lista_Cidade);

						dataAdapterCidade
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						spMunicipio.setAdapter(dataAdapterCidade);
		    		}
		    	}
		    	
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		spMunicipio.setOnItemSelectedListener(new OnItemSelectedListener() {
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

		txtNome.setText(Nome);
		txtCPFCNPJ.setText(CPFCNPJ);
		txtEndereco.setText(Endereco);
		txtBairro.setText(Bairro);
		
		
		List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
		if(Lista_Municipio.size() >0)
		{
		try {
			
			spUF.setSelection(Lista_UF.indexOf(Lista_Municipio.get(0).getUF()));
			UFSel = Lista_Municipio.get(0).getUF();
			spMunicipio.setAdapter(null);  
	    	Lista_Cidade = MuDAO.GetListaCidade(Lista_Municipio.get(0).getUF().toString());
            UF = Lista_Municipio.get(0).getUF().toString();
			ArrayAdapter<String> dataAdapterCidade = new ArrayAdapter<String>(DadosTransportador.this,android.R.layout.simple_spinner_item, Lista_Cidade);

			dataAdapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spMunicipio.setAdapter(dataAdapterCidade);
			PosicaoCidade = Lista_Cidade.indexOf(Lista_Municipio.get(0).getCidade());
			CarregaCidadeSel = true;
			spMunicipio.setSelection(Lista_Cidade.indexOf(Lista_Municipio.get(0).getCidade()));
			
		} catch (Exception e) {
			// TODO: handle exception
			String Erro = e.getMessage();

			
		}
		}

		
		txtNome.setText(Nome);
		txtCPFCNPJ.setText(CPFCNPJ);
		txtEndereco.setText(Endereco);
		
		Button btnConfirma = (Button) findViewById(R.id.btnConfirma_Transportador);
		btnConfirma.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Ait ait = new Ait();
				AitDAO aitDAO = new AitDAO(DadosTransportador.this);
				MunicipioDAO MuDAO = new MunicipioDAO(DadosTransportador.this);
				/*if(UF.equals("Selecione o Estado"))
				{
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							DadosTransportador.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Dados do Transportador");
					aviso.setMessage("Selecione o estado!");
					aviso.setNeutralButton("OK", null);

					aviso.show();
					return;
				}
				if(Cidade.equals("Selecione a Cidade"))
				{
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							DadosTransportador.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Dados do Transportador");
					aviso.setMessage("Selecione a cidade!");
					aviso.setNeutralButton("OK", null);

					aviso.show();
					return;
				}*/
				
				if(txtCPFCNPJ.getText().toString().length() < 14)// Quantidade Minima de digitos do CPF
				{
					boolean ErroValidacao = !Utilitarios.validaCPF(txtCPFCNPJ.getText().toString());
					
					//errocpf = !Utilitarios.validaCPF(edCPF.getText().toString());
					
					if (ErroValidacao)
					{
						AlertDialog.Builder aviso = new AlertDialog.Builder(
								DadosTransportador.this);
						aviso.setIcon(android.R.drawable.ic_dialog_alert);
						aviso.setTitle("Dados do Transportador");
						aviso.setMessage("CPF Inválido!");
						aviso.setNeutralButton("OK", null);

						aviso.show();
						return;
					}
					
				}
				if(txtCPFCNPJ.getText().toString().length() >= 14)// Quantidade Minima de digitos do CNPJ
				{
					boolean ErroValidacao = !Utilitarios.validaCNPJ(txtCPFCNPJ.getText().toString());
					
					//errocpf = !Utilitarios.validaCPF(edCPF.getText().toString());
					
					if (ErroValidacao)
					{
						AlertDialog.Builder aviso = new AlertDialog.Builder(
								DadosTransportador.this);
						aviso.setIcon(android.R.drawable.ic_dialog_alert);
						aviso.setTitle("Dados do Transportador");
						aviso.setMessage("CNPJ Inválido!");
						aviso.setNeutralButton("OK", null);

						aviso.show();
						return;
					}
					
				}
				try {
					String IdMunicipio = MuDAO.GetIdCidade(UF, Cidade);
					ait.setNome_transportador(txtNome.getText().toString());
					ait.setCpfCnpj_transportador(txtCPFCNPJ.getText().toString());
					ait.setEndereco_transportador(txtEndereco.getText().toString());
					ait.setBairro_transportador(txtBairro.getText().toString());
					ait.setIdMunicipio_transportador(IdMunicipio);
					ait.setId(idAit);
					aitDAO.gravaTransportador(ait);
					finish();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}

}
