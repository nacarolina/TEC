package br.com.cobrasin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.crypto.ExemptionMechanism;
import javax.crypto.ExemptionMechanismException;
import javax.crypto.ExemptionMechanismSpi;

import android.R.string;
import android.os.*;

import br.com.cobrasin.ImprimeAit.ThreadConexao;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Municipio;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ImprimeAit extends Activity {

	/** Called when the activity is first created. */
	private String info = Utilitarios.getInfo();
	private List<Ait> aitvisualiza;
	private String salvaAgente;
	private ListView listaait;
	// private boolean termina = false;
	private ArrayAdapter<Ait> adapter;
	private String tipoait;
	private ProgressDialog progress;
	private byte[] buffer;
	private Toast aviso, avisoerro;
	private String saida, impressora, ativo;

	private String cancelou;

	private String desclog;
	private String ctiplog;
	private String enquads;
	private String especie;
	private String tipo;
	private String medidaadm;
	private Button btnImprimirSel;
	private String exibe[] = new String[27];

	private String tipoinfrator;

	private String spIni;
	private String spFinalSel;

	private static final String TAG = "CobrasinAitBt";

	private ThreadConexao tconx;

	private long idAit;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public void ImprimeAits() {
		setContentView(R.layout.imprimeait);
		final Spinner spInicial = (Spinner) findViewById(R.id.spInicial);
		final Spinner spFinal = (Spinner) findViewById(R.id.spFinal);
		String salvaAgente = "";

		AitDAO a = new AitDAO(ImprimeAit.this);
		salvaAgente = getIntent().getStringExtra("agente");// .getSerializableExtra("agente");
		List<Ait> ls = a.getListaAitPrint(salvaAgente); // ((String)
														// getIntent().getSerializableExtra(salvaAgente));

		if (ls.size() > 0) {
			String[] s = new String[ls.size()];
			String[] s2 = new String[ls.size()];
			int i = 0;// ;//
			int i2 = 0;
			// Toast t=new Toast(getBaseContext());

			for (Ait b : ls) {
				// Toast.makeText(this,b.getAit(), Toast.LENGTH_LONG);
				s[i] = (String) b.getAit();
				i++;
			}

			i--;
			while (ls.size() > i2) {
				s2[i2] = s[i];
				i--;
				i2++;
			}
			// s[0]="1";
			// s[1]="2";
			ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, s);
			ArrayAdapter<String> adaptadorSpinner2 = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, s2);

			spInicial.setAdapter(adaptadorSpinner2);

			spFinal.setAdapter(adaptadorSpinner);
			// spFinal.

		}

		spInicial
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent, View v,
							int posicao, long id) {

						String AitSel;
						AitSel = parent.getItemAtPosition(
								spInicial.getSelectedItemPosition()).toString();
						spIni = AitSel;
						// mensagemExibir("Mesa","Mesa selecionada: " +
						// mesa_selecionada);
						// Toast.makeText(getBaseContext(),
						// "Ait selecionado: "+AitSel,
						// Toast.LENGTH_LONG).show();

					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		spFinal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int posicao, long id) {

				String AitSel;
				AitSel = parent.getItemAtPosition(
						spFinal.getSelectedItemPosition()).toString();
				spFinalSel = AitSel;
				// mensagemExibir("Mesa","Mesa selecionada: " +
				// mesa_selecionada);
				// Toast.makeText(getBaseContext(), "Ait selecionado: "+AitSel,
				// Toast.LENGTH_LONG).show();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void carregaSpinner(Spinner spInicial, List<String> listaSpinnerAit) {
		final ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, listaSpinnerAit);
		// spInicial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spInicial.setAdapter(adaptadorSpinner);
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// setContentView(R.layout.imprimeait);
		ImprimeAits();
		btnImprimirSel = (Button) findViewById(R.id.btnImprimirSel);
		btnImprimirSel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				chamaImpressao();
			}
		});
	}

	public void chamaImpressao() {

		aviso = Toast.makeText(ImprimeAit.this, "Dados enviados com sucesso!",
				Toast.LENGTH_LONG);
		avisoerro = Toast.makeText(ImprimeAit.this,
				"Não consegui enviar dados...", Toast.LENGTH_LONG);
		// Toast.makeText(ListaAit.this, "Não consegui enviar dados...",
		// Toast.LENGTH_LONG);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		ParametroDAO pardao = new ParametroDAO(ImprimeAit.this);
		Cursor cpar = pardao.getParametros();
		pardao.close();
		try {
			impressora = SimpleCrypto.decrypt(info,
					cpar.getString(cpar.getColumnIndex("impressoraMAC")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BluetoothDevice mmDevice;
		boolean passou;
		try {
			mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
			passou = true;
			progress = ProgressDialog.show(ImprimeAit.this, "Aguarde...",
					"Enviando dados para a Impressora!!!", true, true);
		} catch (Exception e) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ImprimeAit.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Falha ao imprimir!\nImpressora não instalada!");
			aviso.setNeutralButton("OK", null);
			aviso.show();
			passou = false;
			return;
		}
		if (passou == true) {
			tconx = new ThreadConexao(mmDevice);
			tconx.start();
		} else {

		}
	}

	public class ThreadConexao extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private OutputStream mmOutStream = null;
		private String mens;
		private String desclog;
		private String ctiplog;

		public ThreadConexao(BluetoothDevice device) {
			// mens = texto;
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

			} catch (IOException e) {

				progress.dismiss();
				avisoerro.show();
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();

				mmOutStream = mmSocket.getOutputStream();

				AitDAO a = new AitDAO(ImprimeAit.this);
				List<Ait> ls = a.getListaAitPrint((String) getIntent()
						.getSerializableExtra("agente"));

				for (Ait b : ls) {
					// Spinner spIni = (Spinner) findViewById(R.id.spInicial);
					// Spinner spFinal = (Spinner) findViewById(R.id.spFinal);

					// long aitini=
					// Long.parseLong(spIni.getSelectedItem().toString());
					// long aitfinal=
					// Long.parseLong(spFinal.getSelectedItem().toString());
					long ait = (Long.parseLong(b.getAit()));
					String aitC = b.getAit().toString();// .substring(4).toString();
					long Idait = (b.getId());
					long aitini = Long.parseLong(ImprimeAit.this.spIni);
					long aitfin = Long.parseLong(ImprimeAit.this.spFinalSel);// spFinalSel);

					if (aitini <= ait) {
						if (aitfin >= ait) {

							montaimpressao(Idait);
							buffer = mens.getBytes();
							mmOutStream.write(buffer, 0, buffer.length);

							try {
								Thread.sleep(12000);
								Ait ait1 = new Ait();
								ait1.setImpressao(mens);
								ait1.setId(Idait);
								AitDAO aitdao = new AitDAO(ImprimeAit.this);
								aitdao.gravaImpressao(ait1);
								aitdao.close();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				mmSocket.close();

				progress.dismiss();
				aviso.show();

			} catch (IOException e) {

				progress.dismiss();
				avisoerro.show();
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close() socket during connection failure",
							e2);
				}
				// Start the service over to restart listening mode

				return;
			}

		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}

		private void montaimpressao(long idAit) {

			// String impressora ="00:08:1B:95:6B:AF";

			AitDAO aitdao = new AitDAO(ImprimeAit.this);
			Cursor c = aitdao.getAit(idAit);

			// grava data e hora do envio para a impressora
			aitdao.atualizaImpressao(idAit, c);
			aitdao.close();

			ParametroDAO pardao = new ParametroDAO(ImprimeAit.this);
			Cursor cpar = pardao.getParametros();
			pardao.close();

			try {
				impressora = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("impressoraMAC")));
				ativo = SimpleCrypto.decrypt(
						info,
						cpar.getString(cpar
								.getColumnIndex("impressoraPatrimonio")))
						.toUpperCase();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				cancelou = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("cancelou")));
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (cancelou.contains("NAO")) {
				try {

					// Obtem , Logradouro ,Especie, Tipo
					;

					EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
					especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("especie"))));
					espdao.close();

					TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
					tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("tipo"))));
					tipdao.close();

					MedidaAdmDAO medidadao = new MedidaAdmDAO(ImprimeAit.this);
					medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(
							info, c.getString(c.getColumnIndex("medidaadm"))));
					medidadao.close();

					// pega tipo do logradouro
					ctiplog = " ";

					int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradourotipo"))));

					switch (nx) {
					case 1:
						ctiplog = "OPOSTO";
						break;
					case 2:
						ctiplog = "DEFRONTE";
						break;
					case 3:
						ctiplog = "AO LADO DE";
						break;
					}
					;
					LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouro2"))).contains(
							"NAO")) {
						desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro"))));
						desclog += (" " + ctiplog);
						desclog += (" " + SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouronum"))));
					}
					else {
						desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro"))));
						desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro2")))));
					}
					saida = "";
					// CHR(7) & chr(29) & chr(47) & chr(1) // logo

					// **********************************
					// verifica se impressora é P25
					// **********************************
					Character cx = 0x1D;
					if (ativo.contains("P25"))
						cx = 0x1B;
					saida += cx;

					cx = '/';
					if (ativo.contains("P25"))
						cx = 'f';
					saida += cx;

					cx = 0x00;
					saida += cx;

					saida += String.format("\r\n");

					saida += SimpleCrypto.decrypt(info,
							cpar.getString(cpar.getColumnIndex("prefeitura")))
							+ String.format("\r\n");
					saida += "Orgao Autuador:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("orgaoautuador")))
							+ String.format("\r\n");

					saida += "------------------------" + String.format("\r\n");
					saida += "   Dados da Infracao" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += "Ait:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("serieait")))
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("ait")))
							+ String.format("\r\n");
					saida += "Placa:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("placa")))
							+ String.format("\r\n");
					saida += "Marca:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("marca")))
							+ String.format("\r\n");

                    PaisDAO paisDao = new PaisDAO(ImprimeAit.this);
                    String Pais = paisDao.buscaDescPais(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pais"))));
                    if(Pais !="")
                    {
                    saida += "Pais:" + Pais + String.format("\r\n");
                    }
                    
					saida += "Especie:" + especie + String.format("\r\n");
					saida += "Tipo:" + tipo + String.format("\r\n");
					saida += "Data:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("dtEdit")))
							+ "-"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("hrEdit")))
							+ String.format("\r\n");
					saida += "Equipamento:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("seriepda")))
							+ String.format("\r\n");
					String TipoAIT = SimpleCrypto
							.decrypt(info, c.getString(c
									.getColumnIndex("tipoait")));
					if (TipoAIT.equals("5")) {	
					
						int Limite = 0;
						int Tara = 0;
						int PesoDeclarado = 0;
						int ExcessoConstatado = 0;
						try {
							Limite = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("limitePermitido_excesso"))));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							Tara = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tara_excesso"))));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							PesoDeclarado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pesoDeclarado_excesso"))));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							ExcessoConstatado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("excessoConstatado_excesso"))));
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						String LimiteString = NumberFormat.getNumberInstance().format(Limite);
						String TaraString = NumberFormat.getNumberInstance().format(Tara);
						String PesoDeclaradoString = NumberFormat.getNumberInstance().format(PesoDeclarado);
						String ExcessoConstatadoString = NumberFormat.getNumberInstance().format(ExcessoConstatado);
						
						saida += "Limite de Peso:"
								+ LimiteString + " Kg" 
								+ String.format("\r\n");
						
						saida += "Tara:"
								+ TaraString + " Kg" + String.format("\r\n");
						
						saida += "Peso Declarado:"
								+ PesoDeclaradoString + " Kg" + String.format("\r\n");
						
						saida += "Excesso Const.:"
								+ ExcessoConstatadoString + " Kg"+ String.format("\r\n");
					}
					saida += "" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					saida += "   Local da Infracao " + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					if (TipoAIT.equals("5")) {
					saida += this.desclog + String.format("\r\n");
					saida += "Posto:"+SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("Posto_Agente")))+ String.format("\r\n");
					MunicipioDAO MuD = new MunicipioDAO(ImprimeAit.this);
					List<Municipio> Lista_Municipio_Agente = MuD.GetCidade(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("IdMunicipio_Agente"))));
					String UF_Agente = "";
					String Cidade_Agente = "";
					String CodigoMunicipioProdesp_Agente = "";
					if (Lista_Municipio_Agente.size() > 0) {
						try {
							UF_Agente = Lista_Municipio_Agente.get(0).getUF();
							Cidade_Agente = Lista_Municipio_Agente.get(0).getCidade();
							CodigoMunicipioProdesp_Agente = Lista_Municipio_Agente.get(0).getIdProdesp();

						} catch (Exception e) {
							// TODO: handle exception
							String Erro = e.getMessage();
						}
					}
					saida += "Codigo do Municipio:" + CodigoMunicipioProdesp_Agente+ String.format("\r\n");
					saida += "Cidade/UF:" + Cidade_Agente + " - " + UF_Agente + String.format("\r\n");
					// saida += this.ctiplog+ String.format("\r\n");
					}
					else
					{
						saida += this.desclog + String.format("\r\n");
					}
					saida += "" + String.format("\r\n");

					AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
							ImprimeAit.this);
					Cursor c1 = aitenq.getLista1(idAit);

					enquads = " ";
					c1.moveToNext();

					// enquads += c1.getString(c1.getColumnIndex("codigo")) +
					// " ";

					EnquadramentoDAO dao = new EnquadramentoDAO(ImprimeAit.this);
					List<Enquadramento> enquadramento = dao.getLista(
							SimpleCrypto.decrypt(info,
									c1.getString(c1.getColumnIndex("codigo"))),
							ImprimeAit.this);
					dao.close();

					enquads += enquadramento.get(0).toString();

					// enquads = Utilitarios.quebraLinha(enquads);

					c1.close();

					saida += "------------------------" + String.format("\r\n");
					saida += "    Enquadramento" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					saida += enquads + String.format("\r\n");

					saida += "" + String.format("\r\n");
					if (TipoAIT.equals("5")) {	
						saida += "------------------------" + String.format("\r\n");
						saida += "  Ident. do Condutor   " + String.format("\r\n");
						saida += "------------------------" + String.format("\r\n");
					}
					else
					{
					saida += "------------------------" + String.format("\r\n");
					saida += "  Identf. do Infrator   " + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					}
					try {
						tipoinfrator = SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("tipoinfrator")));
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (tipoinfrator == null) {
						saida += "Nome:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("nome")))
								+ String.format("\r\n");
						saida += "CPF:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("cpf")))
								+ String.format("\r\n");
						if (TipoAIT.equals("5")) {
							saida += "CNH:"
									+ SimpleCrypto.decrypt(info,
											c.getString(c.getColumnIndex("pgu")))
									+ " "
									+ SimpleCrypto.decrypt(info,
											c.getString(c.getColumnIndex("uf")))
									+ String.format("\r\n");
						}
						else
						{
							saida += "PGU:"
									+ SimpleCrypto.decrypt(info,
											c.getString(c.getColumnIndex("pgu")))
									+ " "
									+ SimpleCrypto.decrypt(info,
											c.getString(c.getColumnIndex("uf")))
									+ String.format("\r\n");
						}
						
						
						if (TipoAIT.equals("5")) {
							String PPD = SimpleCrypto.decrypt(info,
									c.getString(c
											.getColumnIndex("ppd_condutor")));
							if (PPD.equals("S")) {
								saida += "PPD:SIM" + String.format("\r\n");
							}
							if (PPD.equals("N")) {
								saida += "PPD:NÃO" + String.format("\r\n");
							}
						}
						
						
					} else {
						if (tipoinfrator.contains("CNH")) {
							saida += "Nome:"
									+ SimpleCrypto.decrypt(info, c.getString(c
											.getColumnIndex("nome")))
									+ String.format("\r\n");
							saida += "CPF:"
									+ SimpleCrypto.decrypt(info, c.getString(c
											.getColumnIndex("cpf")))
									+ String.format("\r\n");
							if (TipoAIT.equals("5")) {
								saida += "CNH:"
										+ SimpleCrypto.decrypt(info,
												c.getString(c.getColumnIndex("pgu")))
										+ " "
										+ SimpleCrypto.decrypt(info,
												c.getString(c.getColumnIndex("uf")))
										+ String.format("\r\n");
							}
							else
							{
								saida += "PGU:"
										+ SimpleCrypto.decrypt(info,
												c.getString(c.getColumnIndex("pgu")))
										+ " "
										+ SimpleCrypto.decrypt(info,
												c.getString(c.getColumnIndex("uf")))
										+ String.format("\r\n");
							}

						}
						if (tipoinfrator.contains("PID")) {
							saida += "Nome:"
									+ SimpleCrypto.decrypt(info, c.getString(c
											.getColumnIndex("nome")))
									+ String.format("\r\n");
							saida += "Doc. de Ident.:"
									+ SimpleCrypto.decrypt(info, c.getString(c
											.getColumnIndex("passaporte")))
									+ String.format("\r\n");
							saida += "Pid:"
									+ SimpleCrypto.decrypt(info, c.getString(c
											.getColumnIndex("pid")))
									+ " "
									+ SimpleCrypto
											.decrypt(info, c.getString(c
													.getColumnIndex("uf")))
									+ String.format("\r\n");
						}
					}
					

					saida += "" + String.format("\r\n");
					saida += "________________________" + String.format("\r\n");
					saida += "      Assinatura" + String.format("\r\n");
					// saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+
					// String.format("\r\n");
					
					saida += "" + String.format("\r\n");
					
					NotaFiscalDAO NfDAO = new NotaFiscalDAO(ImprimeAit.this);
					List<br.com.cobrasin.tabela.NotaFiscal> NfLista = NfDAO
							.GetNotasAit(idAit);

					if (NfLista.size() == 1) {
						try {
							saida += "------------------------"
									+ String.format("\r\n");
							saida += "   Dados do Embarcador  "
									+ String.format("\r\n");
							saida += "------------------------"
									+ String.format("\r\n");

							String Nome = SimpleCrypto.decrypt(info, c.getString(c
									.getColumnIndex("nome_embarcador")));
							String CPFCNPJ = SimpleCrypto.decrypt(info, c
									.getString(c
											.getColumnIndex("cpfCnpj_embarcador")));
							String Endereco = SimpleCrypto
									.decrypt(info, c.getString(c
											.getColumnIndex("endereco_embarcador")));
							
							String Bairro = SimpleCrypto
									.decrypt(info, c.getString(c
											.getColumnIndex("bairro_embarcador")));
							
							String IdMunicipio = SimpleCrypto
									.decrypt(info, c.getString(c
											.getColumnIndex("IdMunicipio_embarcador")));
							
							MunicipioDAO MuDAO = new MunicipioDAO(ImprimeAit.this);
							List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
							String UF = "";
							String Cidade = "";
							if (Lista_Municipio.size() > 0) {
								try {
									UF = Lista_Municipio.get(0).getUF();
									Cidade = Lista_Municipio.get(0).getCidade();

								} catch (Exception e) {
									// TODO: handle exception
									String Erro = e.getMessage();
								}
							}

							saida += "Nome:" + Nome + String.format("\r\n");

							saida += "CPF/CNPJ:" + CPFCNPJ + String.format("\r\n");

							saida += "Endereco:" + Endereco + String.format("\r\n");
							
							saida += "Cidade/UF:" + Cidade + " - " + UF + String.format("\r\n");
							
							saida += "Bairro:" + Bairro + String.format("\r\n");
							

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
					if (NfLista.size() > 1) {
						try {
							saida += "------------------------"
									+ String.format("\r\n");
							saida += "      Transportador  "
									+ String.format("\r\n");
							saida += "------------------------"
									+ String.format("\r\n");

							String Nome = SimpleCrypto.decrypt(info, c.getString(c
									.getColumnIndex("nome_transportador")));
							String CPFCNPJ = SimpleCrypto
									.decrypt(
											info,
											c.getString(c
													.getColumnIndex("cpfCnpj_transportador")));
							String Endereco = SimpleCrypto
									.decrypt(
											info,
											c.getString(c
													.getColumnIndex("endereco_transportador")));
							
							String Bairro = SimpleCrypto
									.decrypt(info, c.getString(c
											.getColumnIndex("bairro_transportador")));
							
							String IdMunicipio = SimpleCrypto
									.decrypt(info, c.getString(c
											.getColumnIndex("IdMunicipio_transportador")));
							
							MunicipioDAO MuDAO = new MunicipioDAO(ImprimeAit.this);
							List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
							String UF = "";
							String Cidade = "";
							if (Lista_Municipio.size() > 0) {
								try {
									UF = Lista_Municipio.get(0).getUF();
									Cidade = Lista_Municipio.get(0).getCidade();

								} catch (Exception e) {
									// TODO: handle exception
									String Erro = e.getMessage();
								}
							}

							saida += "Nome:" + Nome + String.format("\r\n");

							saida += "CPF/CNPJ:" + CPFCNPJ + String.format("\r\n");

							saida += "Endereco:" + Endereco + String.format("\r\n");

							saida += "Cidade/UF:" + Cidade + " - " + UF
									+ String.format("\r\n");

							saida += "Bairro:" + Bairro + String.format("\r\n");

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					NfDAO.close();

					saida += "" + String.format("\r\n");
					saida += "-----------------------" + String.format("\r\n");
					saida += "Identificacao do Agente" + String.format("\r\n");
					saida += "-----------------------" + String.format("\r\n");
					saida += "Matric.(AG):"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("agente")))
							+ String.format("\r\n");

					saida += "" + String.format("\r\n");
					saida += "________________________" + String.format("\r\n");
					saida += "     Lavrado por" + String.format("\r\n");

					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("observacoes")))
							.length() > 0) {

						if (SimpleCrypto.decrypt(
								info,
								cpar.getString(cpar
										.getColumnIndex("imprimeobs")))
								.contains("1")) {
							saida += String.format("\r\n");
							saida += "------------------------"
									+ String.format("\r\n");
							saida += "Observacoes:" + String.format("\r\n");
							saida += SimpleCrypto.decrypt(info, c.getString(c
									.getColumnIndex("observacoes")))
									+ String.format("\r\n");
							;

						}

					}

					// ***********************************************
					// 28.07.2012
					//
					// imprime medida administrativa se foi definida
					// ***********************************************
					if (!medidaadm.contains(("Nao definido"))) {
						saida += String.format("\r\n");
						saida += "------------------------"
								+ String.format("\r\n");
						saida += "Medida Administrativa:"
								+ String.format("\r\n");
						saida += medidaadm + String.format("\r\n");

					}

					// **************************************************************************************
					// 08.03.2012
					// Preencheu dados equipamento, exemplo decibelímetro ?
					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("equipamento")))
							.length() > 0) {
						saida += String.format("\r\n");
						saida += "Equipamento:" + String.format("\r\n");
						saida += SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("equipamento")))
								+ String.format("\r\n");
						saida += "Medicao Registrada:" + String.format("\r\n");
						saida += SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("medicaoreg")))
								+ String.format("\r\n");
						saida += "Medicao Considerada:" + String.format("\r\n");
						saida += SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("medicaocon")))
								+ String.format("\r\n");
						saida += "Limite Regulamentado:"
								+ String.format("\r\n");
						saida += SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("limitereg")))
								+ String.format("\r\n");

					}

					saida += String.format("\r\n");
					saida += String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					saida += "E obrigatoria a presenca" + String.format("\r\n");
					saida += "do codigo INFRAEST ou" + String.format("\r\n");
					saida += "RENAINF nas notificacoes" + String.format("\r\n");
					saida += "sob pena de invalidade" + String.format("\r\n");
					saida += "da multa." + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += String.format("\r\n");
					saida += String.format("\r\n");
					saida += String.format("\r\n");
					saida += String.format("\r\n");
					cpar.close();

					c.close();
					mens = saida;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				try {

					impressora = SimpleCrypto.decrypt(info, cpar.getString(cpar
							.getColumnIndex("impressoraMAC")));
					ativo = SimpleCrypto.decrypt(
							info,
							cpar.getString(cpar
									.getColumnIndex("impressoraPatrimonio")))
							.toUpperCase();
					// Obtem , Logradouro ,Especie, Tipo
					ctiplog = " ";

					int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradourotipo"))));

					switch (nx) {
					case 1:
						ctiplog = "OPOSTO";
						break;
					case 2:
						ctiplog = "DEFRONTE";
						break;
					case 3:
						ctiplog = "AO LADO DE";
						break;
					}
					;
					LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouro2"))).contains(
							"NAO")) {
						desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro"))));
						desclog += (" " + ctiplog);
						desclog += (" " + SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouronum"))));
					}
					else {
						desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro"))));
						desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro2")))));
					}

					EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
					especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("especie"))));
					espdao.close();

					TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
					tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("tipo"))));
					tipdao.close();

					saida = "";
					// CHR(7) & chr(29) & chr(47) & chr(1) // logo

					// **********************************
					// verifica se impressora é P25
					// **********************************
					Character cx = 0x1D;
					if (ativo.contains("P25"))
						cx = 0x1B;
					saida += cx;

					cx = '/';
					if (ativo.contains("P25"))
						cx = 'f';
					saida += cx;

					cx = 0x00;
					saida += cx;

					saida += String.format("\r\n");

					saida += SimpleCrypto.decrypt(info,
							cpar.getString(cpar.getColumnIndex("prefeitura")))
							+ String.format("\r\n");
					saida += "Orgao Autuador:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("orgaoautuador")))
							+ String.format("\r\n");

					saida += "------------------------" + String.format("\r\n");
					saida += "   Dados da Infracao" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += "Ait:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("serieait")))
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("ait")))
							+ String.format("\r\n");
					saida += "Placa:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("placa")))
							+ String.format("\r\n");
					saida += "Marca:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("marca")))
							+ String.format("\r\n");
					saida += "Especie:" + especie + String.format("\r\n");
					saida += "Tipo:" + tipo + String.format("\r\n");
					saida += "Data:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("data")))
							+ "-"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("hora")))
							+ String.format("\r\n");
					saida += "Equipamento:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("seriepda")))
							+ String.format("\r\n");
					saida += String.format("\r\n");

					saida += "------------------------" + String.format("\r\n");
					saida += " Dados do Cancelamento" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += cancelou + String.format("\r\n");
					saida += "Motivo: "
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("motivo")))
							+ String.format("\r\n");
					saida += String.format("\r\n");
					saida += String.format("\r\n");
					saida += String.format("\r\n");
					saida += String.format("\r\n");

					mens = saida;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

}
