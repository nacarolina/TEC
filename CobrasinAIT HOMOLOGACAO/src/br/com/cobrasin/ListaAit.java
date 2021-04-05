package br.com.cobrasin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Parametro;

public class ListaAit extends Activity implements LocationListener {

	private String info = Utilitarios.getInfo();
	private List<Ait> aitvisualiza;
	private String salvaAgente;
	private ListView listaait;
	private boolean termina = false;
	private ArrayAdapter<Ait> adapter;
	private String tipoait;
	private ProgressDialog progress;
	private Toast aviso, avisoerro;
	private String saida, impressora, ativo;
	private boolean ConctadoInternet;

	private String ModPDF;
	private String cancelou;

	private String desclog;
	private String ctiplog;
	private String enquads;
	private String especie;
	private String tipo;
	private String medidaadm;
	private String seriepda;
	private static final int NOTIFY_ME_ID = 1337;
	private String exibe[] = new String[27];

	private String teste;

	private Integer contamensagem;

	private static final String TAG = "CobrasinAitBt";

	private String modweb;
	private String modgps;
	private LocationManager locationManager;
	private ThreadConexao tconx;

	private long idAit;
	private String retornoweb;

	private String logradouroGps;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static final int INVISIBLE = 4;

	private void chama(int ntip, long idAit) {
		Intent i = null;
		switch (ntip) {
		case 1:

			// i = new Intent(this,PreencheAit.class);
			i = new Intent(this, ListaTipoAit.class);
			i.putExtra("agente", salvaAgente);
			i.putExtra("logradouroGps", logradouroGps);
			break;

		case 2:

			switch (Integer.parseInt(tipoait)) {
			case 1:
				i = new Intent(this, ExibeDadosAit.class); // veiculo placa
															// nacional
				break;
			case 2:
				i = new Intent(this, ExibeDadosAitpfpj.class); // pessoa fisica
				break;
			case 3:
				i = new Intent(this, ExibeDadosAitpfpj.class); // pessoa
																// juridica
				break;
			case 4:
				i = new Intent(this, ExibeDadosAitPlest.class);
				break;
			case 5:
				i = new Intent(this, ExibeDadosAitExcesso.class);
			}
			
			i.putExtra("idAit", idAit);
			break;
		case 3:

			i = new Intent(this, ListaParametros.class);

			break;
		}

		startActivity(i);
		// finish();

	}

	private void carregaLista() {
		AitDAO aitdao = new AitDAO(ListaAit.this);
		aitvisualiza = aitdao.getLista(salvaAgente);

		aitdao.close();

		adapter = new ArrayAdapter<Ait>(this,
				android.R.layout.simple_list_item_1, aitvisualiza);

		listaait = (ListView) findViewById(R.id.listaAit);
		listaait.setAdapter(adapter);

	}

	// **************************************************
	// Verifica condições para permitir novos Aits
	// **************************************************
	public boolean podeEnquadrar(Context ctxx, String agentew) {
		boolean retornoaits = true;
		boolean retornoreset = true;
		boolean retornowebtrans = true;

		ParametroDAO pardaoz = new ParametroDAO(ctxx);

		Cursor cursorpx = pardaoz.getParametros();

		pardaoz.close();

		String prefAtiva = "";
		String pdaAtivo = "";
		try {
			pdaAtivo = SimpleCrypto.decrypt(info,
					cursorpx.getString(cursorpx.getColumnIndex("ativo")));
			prefAtiva = SimpleCrypto.decrypt(info,
					cursorpx.getString(cursorpx.getColumnIndex("prefativa")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			if (Long.parseLong(SimpleCrypto.decrypt(info,
					cursorpx.getString((cursorpx.getColumnIndex("proximoait"))))) > Long
					.parseLong(SimpleCrypto.decrypt(info, cursorpx
							.getString((cursorpx.getColumnIndex("aitfinal")))))) {
				AlertDialog.Builder aviso = new AlertDialog.Builder(ctxx);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Criação de Novo AIT");
				aviso.setMessage("Não tenho mais AIT's disponíveis ! Entre em contato com a COBRASIN !");
				aviso.setNeutralButton("AITS", null);
				aviso.show();

				retornoaits = false;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cursorpx.close();

		// verifica se existe arquivo definepda / quando ocorre o Reset pelo
		// usuário o arquivo é apagado
		String root = Environment.getDataDirectory().getAbsolutePath()
				+ "/data/br.com.cobrasin/databases";

		/*
		 * File filereset = new File(root,"definepda");
		 * 
		 * if (!filereset.exists()) { AlertDialog.Builder aviso = new
		 * AlertDialog.Builder(ctxx);
		 * aviso.setIcon(android.R.drawable.ic_dialog_alert);
		 * aviso.setTitle("Criação de Novo AIT"); aviso.setMessage(
		 * "RESET-foi executado. Não posso mais criar AIT's ! Encerre a aplicação e execute novamente para baixar os arquivos ! Entre em contato com a COBRASIN"
		 * ); aviso.setNeutralButton("AITS",null); aviso.show();
		 * 
		 * retornoreset = false;
		 * 
		 * }
		 */
		File filereset = new File(root, "errowebtrans");

		if (filereset.exists()) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ctxx);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Criação de Novo AIT");
			aviso.setMessage("Última leitura dos dados do Sistema Webtrans incompleto. Não posso mais criar AIT's ! Execute a transmissão de AIT´s para  baixar os arquivos do WebTrans ! Entre em contato com a COBRASIN");
			aviso.setNeutralButton("AITS", null);
			aviso.show();

			retornowebtrans = false;

		}

		if (pdaAtivo.contains("N")) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ctxx);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Criação de Novo AIT");
			aviso.setMessage("Equipamento não está Ativo ! Entre em contato com o Departamento de Trânsito");
			aviso.setNeutralButton("AITS", null);
			aviso.show();

			retornowebtrans = false;
		}

		if (prefAtiva.contains("N")) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ctxx);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Criação de Novo AIT");
			aviso.setMessage("Prefeitura não está ativa ! Entre em contato com o Departamento de Trânsito");
			aviso.setNeutralButton("AITS", null);
			aviso.show();

			retornowebtrans = false;

		}

		if (!Utilitarios.agenteAtivo(ctxx, agentew)) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ctxx);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Criação de Novo AIT");
			aviso.setMessage("Agente Desativado ! Entre em contato com o Departamento de Trânsito");
			aviso.setNeutralButton("AITS", null);
			aviso.show();

			retornowebtrans = false;
		}
		return (retornoaits & retornoreset & retornowebtrans);

	}
	
    
	@Override
	public boolean onCreateOptionsMenu(Menu me) {
		if (Agente_DNIT) {
			me.add("Ponto do Agente");
			
		}
		me.add("Configurações");
		me.add("Estatística");
		// me.add("Sincronizar GPS");
		me.add("Modo RFID");
		me.add("Modo OCR");
		me.add("Consultar Placa");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mt) {
		if (mt.getTitle() == "Ponto do Agente") {
			  AgenteDAO Adao = new AgenteDAO(ListaAit.this);
			  Cursor cAgente = Adao.GetDadosAgente((String) getIntent()
							.getSerializableExtra("agente"));
			  String Posto = "";
			  String IdMunicipio = "";
			 // if (cAgente.moveToFirst())
			//	{
					try {
						Posto = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("POSTO")));
					} catch (Exception e) {
						// TODO: handle exception

					}
					
                  try {
                  	IdMunicipio = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("IdMunicipio")));
					} catch (Exception e) {
						// TODO: handle exception

					}
			//	}
			  Adao.close();
			  

				  Intent i = null;
				  i = new Intent(ListaAit.this, PostoAgente.class);
				  i.putExtra("agente", (String) getIntent()
							.getSerializableExtra("agente"));
				  i.putExtra("Posto",Posto);
				  i.putExtra("IdMunicipio",IdMunicipio);
				  startActivity(i);
		}
		if (mt.getTitle() == "Modo OCR") {
			Intent i;
			i = new Intent(getBaseContext(), VeiculosRestricaoOCR.class);
			i.putExtra("agente", salvaAgente);
			startActivity(i);
		}
		if (mt.getTitle() == "Modo RFID") {
			Intent i;
			i = new Intent(getBaseContext(), VeiculosRestricaoRFID.class);
			i.putExtra("agente", salvaAgente);
			startActivity(i);
		}
		if (mt.getTitle() == "Consultar Placa") {
			Intent i;
			i = new Intent(getBaseContext(), ConsultaPlaca.class);
			i.putExtra("agente", salvaAgente);
			startActivity(i);
		}
		if (mt.getTitle() == "Configurações") {
			if (Utilitarios.agenteAtivo(getBaseContext(), salvaAgente)) {
				chama(3, 0);
			} else {
				Toast.makeText(getBaseContext(),
						">Agente Desativado pelo Orgão de Trânsito!",
						Toast.LENGTH_SHORT).show();
			}

		}
		if (mt.getTitle() == "Estatística") {
			Intent i;
			i = new Intent(getBaseContext(), Estatistica.class);
			i.putExtra("agente",
					(String) getIntent().getSerializableExtra("agente"));
			startActivity(i);
		}
		if (mt.getTitle() == "Sincronizar GPS") {
			ParametroDAO pardao = new ParametroDAO(ListaAit.this);
			Parametro param = new Parametro();
			Cursor c = pardao.getParametros();
			try {
				modgps = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("modgps")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (modgps == null) {
				param.setmodgps("FALSE");
				param.setSeriepda(seriepda);
				pardao.gravaModoGps(param);
			}
			try {
				modgps = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("modgps")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (modgps.contains("TRUE")) {
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Obter Logradouro");
				aviso.setMessage("Deseja obter o logradouro?");
				aviso.setNeutralButton("Não", null);
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								ObtemLatitudeLongitude();
							}
						});

				aviso.show();
			}
			if (modgps.contains("FALSE")) {
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("Obter Logradouro");
				aviso.setMessage("O modo GPS está desativado!");
				aviso.setNeutralButton("OK", null);

				aviso.show();
			}
		}
		return super.onOptionsItemSelected(mt);
	}

	private void chama1() {
		startActivity(new Intent(this, CobrasinAitActivity.class));
		finish();
	}

	protected void criarNotificacao(String titulo, String subtitulo,
			String descricao) {

		final NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification note = new Notification(R.drawable.icon, titulo,
				System.currentTimeMillis());

		// note.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,
		// "6");
		note.flags |= Notification.FLAG_INSISTENT;
		note.flags |= Notification.FLAG_AUTO_CANCEL;

		// This pending intent will open after notification click
		PendingIntent i = PendingIntent.getActivity(getBaseContext(), 0,
				new Intent(getBaseContext(), ListaParametros.class), 0);

		note.setLatestEventInfo(getBaseContext(), subtitulo, descricao, i);

		// After uncomment this line you will see number of notification arrived
		// note.number=2;
		mgr.notify(NOTIFY_ME_ID, note);
	}

	private void ObtemLatitudeLongitude() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("network,gps")) {
			contamensagem = 0;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 3000, 10, this);
			progress = ProgressDialog.show(ListaAit.this, "TEC",
					"Obtendo Logradouro...");
			return;
		}
		if (provider.contains("network")) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(ListaAit.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("Obter Logradouro");
			aviso.setMessage("GPS Desligado!\nPara obter o logradouro ligue o GPS!");
			aviso.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 1);
						}
					});
			aviso.show();

		}
	}

	boolean Agente_DNIT = false;
	private String Agente = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		termina = false;

		setContentView(R.layout.listaait);

		final Utilitarios u = new Utilitarios();
		Parametro param = new Parametro();
		ParametroDAO pardao = new ParametroDAO(ListaAit.this);
		Cursor cpar = pardao.getParametros();
		// pardao.close();
		try {
			Agente = ((String) getIntent()
					.getSerializableExtra("agente"));
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}

		try {
			seriepda = cpar.getString(cpar.getColumnIndex("seriepda"));
			ModPDF = SimpleCrypto.decrypt(info,
					cpar.getString(cpar.getColumnIndex("modpdf")));
			modweb = SimpleCrypto.decrypt(info,
					cpar.getString(cpar.getColumnIndex("modweb")));

		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		if (ModPDF == null) {
			param.setmodpdf("FALSE");
			param.setSeriepda(seriepda);
			pardao.gravaModoPdf(param);
		}
		cpar = pardao.getParametros();

		try {
			ModPDF = SimpleCrypto.decrypt(info,
					cpar.getString(cpar.getColumnIndex("modpdf")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (ModPDF.contains("TRUE")) {
			param.setmodpdf("TRUE");
			param.setSeriepda(seriepda);
			pardao.gravaModoPdf(param);
		}
		if (ModPDF.contains("FALSE")) {
			param.setmodpdf("FALSE");
			param.setSeriepda(seriepda);
			pardao.gravaModoPdf(param);
		}
		if (modweb == null) {
			param.setmodweb("TRUE");
			param.setSeriepda(seriepda);
			pardao.gravaModoWeb(param);
		}

		cpar = pardao.getParametros();
		try {
			modweb = SimpleCrypto.decrypt(info,
					cpar.getString(cpar.getColumnIndex("modweb")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (modweb.contains("TRUE")) {
			ConctadoInternet = u.conectado(ListaAit.this);
			if (ConctadoInternet == true) {
				param.setmodweb("TRUE");
				param.setSeriepda(seriepda);
				pardao.gravaModoWeb(param);

				criarNotificacao("TEC", "TEC", "Você está em modo online!");
			}
			if (ConctadoInternet == false) {
				param.setmodweb("FALSE");
				param.setSeriepda(seriepda);
				pardao.gravaModoWeb(param);
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Falha ao conectar na internet!\nVocê está em modo offline!");
				aviso.setNeutralButton("OK", null);
				aviso.show();

				param.setmodgps("FALSE");
				param.setSeriepda(seriepda);
				pardao.gravaModoGps(param);

				criarNotificacao("TEC", "TEC", "Falha ao conectar na internet!");
			}
		}

		if (modweb.contains("FALSE")) {
			param.setmodweb("FALSE");
			param.setSeriepda(seriepda);
			pardao.gravaModoWeb(param);

			param.setmodgps("FALSE");
			param.setSeriepda(seriepda);
			pardao.gravaModoGps(param);

			criarNotificacao("TEC", "TEC", "Você está em modo offline!");
		}
		// Button btnSincronizaGPS = (Button)
		// findViewById(R.id.btSincronizaGps);
		/*
		 * btnSincronizaGPS.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * } });
		 */
		 AgenteDAO Adao = new AgenteDAO(ListaAit.this);
		//Agente_DNIT = (Boolean) getIntent().getSerializableExtra("Agente_DNIT");
		Agente_DNIT = Adao.verificaAgenteDNIT((String) getIntent()
				.getSerializableExtra("agente"));
		if (Agente_DNIT) {
			
			  Cursor cAgente = Adao.GetDadosAgente((String) getIntent()
							.getSerializableExtra("agente"));
			  String Posto = "";
			  String IdMunicipio = "";
			 // if (cAgente.moveToFirst())
			//	{
					try {
						Posto = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("POSTO")));
					} catch (Exception e) {
						// TODO: handle exception

					}
					
                    try {
                    	IdMunicipio = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("IdMunicipio")));
					} catch (Exception e) {
						// TODO: handle exception

					}
			//	}
			  
			  
			  if(Posto.equals("") || IdMunicipio.equals(""))
			  {
				  Intent i = null;
				  i = new Intent(ListaAit.this, PostoAgente.class);
				  i.putExtra("agente", (String) getIntent()
							.getSerializableExtra("agente"));
				  i.putExtra("Posto",Posto);
				  i.putExtra("IdMunicipio",IdMunicipio);
				  startActivity(i);
			  }
		}
		Adao.close();
		
		Button btnPrint = (Button) findViewById(R.id.btPrintAll);

		btnPrint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AitDAO a = new AitDAO(ListaAit.this);
				List<Ait> ls = a.getListaAitPrint((String) getIntent()
						.getSerializableExtra("agente"));

				if (ls.size() > 0) {
					Intent mIntent = new Intent(getBaseContext(),
							ImprimeAit.class);
					mIntent.putExtra("agente", (String) getIntent()
							.getSerializableExtra("agente"));
					startActivity(mIntent);

					// startActivity(new Intent(getBaseContext(),
					// ImprimeAit.class));
				}
			}
		});

		Button btnConsutarPlacaLISTA = (Button) findViewById(R.id.btnConsutarPlacaLISTA);

		btnConsutarPlacaLISTA.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i;
				i = new Intent(getBaseContext(), ConsultaPlaca.class);
				i.putExtra("agente", salvaAgente);
				startActivity(i);
			}
		});

		salvaAgente = (String) getIntent().getSerializableExtra("agente");

		carregaLista();

		listaait.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				// long teste = aitvisualiza.get(arg2).getId();

				if (Utilitarios.agenteAtivo(getBaseContext(), salvaAgente)) {
					if (Utilitarios.pdaAtivo(getBaseContext())) {

						if (Utilitarios.prefeituraAtiva(getBaseContext())) {
							tipoait = aitvisualiza.get(arg2).getTipoait();
							chama(2, aitvisualiza.get(arg2).getId());
						} else {
							Toast.makeText(getBaseContext(),
									">Prefeitura Desativada!",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getBaseContext(),
								">PDA Desativado pelo Orgão de Trânsito!",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getBaseContext(),
							">Agente Desativado pelo Orgão de Trânsito!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Button btParametros = (Button)findViewById(R.id.btParametros);
		// btParametros.setOnClickListener(new View.OnClickListener() {

		// @Override
		// public void onClick(View v) {
		// TODO Auto-generated method stub

		// if (Utilitarios.agenteAtivo(getBaseContext(), salvaAgente))
		// {
		// chama(3,0);
		// }
		// else
		// {
		// Toast.makeText( getBaseContext() ,
		// ">Agente Desativado pelo Orgão de Trânsito!",Toast.LENGTH_SHORT).show();
		// }

		// }
		// });

		Button btEncerra = (Button) findViewById(R.id.btEncerraAit);
		btEncerra.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				termina = true;
				handler.removeCallbacks(runnable);

				chama1();
				ParametroDAO pardao = new ParametroDAO(ListaAit.this);
				Cursor par = pardao.getParametros();
				LogDAO l = new LogDAO(ListaAit.this);
				// txtNomePda.getText().toString()
				try {
					l.gravalog("Efetuado com sucesso", "Logoff", SimpleCrypto
							.decrypt(info, par.getString(par
									.getColumnIndex("orgaoautuador"))),
							SimpleCrypto.decrypt(info, par.getString(par
									.getColumnIndex("seriepda"))), salvaAgente,
							ListaAit.this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		Button btNovoAit = (Button) findViewById(R.id.btNovoAit);
		btNovoAit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// handler.removeCallbacks(runnable);

				if (podeEnquadrar(ListaAit.this, salvaAgente)) {
					termina = false;
					chama(1, 0);
				}

			}
		});
		Button btTransmite = (Button) findViewById(R.id.btTransmite);
		btTransmite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// if ( listaait.getCount() > 0 )
				// {
				Parametro param = new Parametro();
				ParametroDAO pardao = new ParametroDAO(ListaAit.this);
				Cursor cpar = pardao.getParametros();
				pardao.close();
				try {
					modweb = SimpleCrypto.decrypt(info,
							cpar.getString(cpar.getColumnIndex("modweb")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (modweb.contains("TRUE")) {
					if (Utilitarios.agenteAtivo(getBaseContext(), salvaAgente)) {
						if (Utilitarios.conectado(getBaseContext())) {
							confirmaTx();
						} else {
							Toast.makeText(
									getBaseContext(),
									">Não existe Rede Disponível para Transmitir os AITs!",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getBaseContext(),
								">Agente Desativado pelo Orgão de Trânsito!",
								Toast.LENGTH_SHORT).show();
					}
				}

				if (modweb.contains("FALSE")) {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							ListaAit.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Transmitir AIT");
					aviso.setMessage("Você está em modo offline!");
					aviso.setNeutralButton("OK", null);
					aviso.show();
				}

				// }
				// else
				// {
				// Toast.makeText(ListaAit.this,
				// "Sem Registro(s) para transmitir!",Toast.LENGTH_SHORT).show();
				// }
			}
		});
	}

	private void confirmaTx() {
		AlertDialog.Builder aviso = new AlertDialog.Builder(ListaAit.this);
		aviso.setIcon(android.R.drawable.ic_dialog_alert);
		aviso.setTitle("Transmissão de AIT");
		aviso.setMessage("Confirma ?");
		aviso.setNeutralButton("Não", null);
		aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				// ****************************
				// 29.06.2012
				//
				// informa qual é o Agente
				// ****************************
				Sincronismo s = new Sincronismo(ListaAit.this, salvaAgente);
				s.sincronizar(Agente);

				//
				// ***************************************************************
				// NAO ATUALIZA mais após a transmissão , somente após 24
				// horas...
				// ***************************************************************
				/*
				 * try { atualiza = true ; runnable.run(); } catch( Exception
				 * e){
				 * 
				 * }
				 */

			}
		});

		aviso.show();

	}

	private Handler handler = new Handler();

	private Runnable runnable = new Runnable() {
		public void run() {

			// if ( atualiza )
			// {
			// carregaLista();

			// handler.postDelayed(this, 10000);
			// }
		}
	};

	@Override
	public void onBackPressed() {
		// /finish();
		ParametroDAO pardao = new ParametroDAO(ListaAit.this);
		Cursor par = pardao.getParametros();
		LogDAO l = new LogDAO(ListaAit.this);
		// txtNomePda.getText().toString()
		try {
			l.gravalog(
					"Efetuado com sucesso",
					"Logoff",
					SimpleCrypto.decrypt(info,
							par.getString(par.getColumnIndex("orgaoautuador"))),
					SimpleCrypto.decrypt(info,
							par.getString(par.getColumnIndex("seriepda"))),
					salvaAgente, ListaAit.this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

	protected void onResume() {
		super.onResume();

		if (!termina) {
			carregaLista();
		} else {
			ParametroDAO pardao = new ParametroDAO(ListaAit.this);
			Cursor par = pardao.getParametros();
			LogDAO l = new LogDAO(ListaAit.this);
			// txtNomePda.getText().toString()
			try {
				l.gravalog("Efetuado com sucesso", "Logoff", SimpleCrypto
						.decrypt(info, par.getString(par
								.getColumnIndex("orgaoautuador"))),
						SimpleCrypto.decrypt(info,
								par.getString(par.getColumnIndex("seriepda"))),
						salvaAgente, ListaAit.this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			// atualiza = false;
		}
		// //if ( termina ) finish();
		// //atualiza = false;
	}

	public void chamaImpressao() {

		progress = ProgressDialog.show(ListaAit.this, "Aguarde...",
				"Enviando dados para a Impressora!!!", true, true);
		aviso = Toast.makeText(ListaAit.this, "Dados enviados com sucesso!",
				Toast.LENGTH_LONG);
		avisoerro = Toast.makeText(ListaAit.this,
				"Não consegui enviar dados...", Toast.LENGTH_LONG);
		// Toast.makeText(ListaAit.this, "Não consegui enviar dados...",
		// Toast.LENGTH_LONG);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		ParametroDAO pardao = new ParametroDAO(ListaAit.this);
		Cursor cpar = pardao.getParametros();
		pardao.close();
		impressora = cpar.getString(cpar.getColumnIndex("impressora"));
		BluetoothDevice mmDevice = mBluetoothAdapter
				.getRemoteDevice(impressora);

		cpar.close();

		tconx = new ThreadConexao(mmDevice);
		tconx.start();

	}

	private class ThreadConexao extends Thread {

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
			LogDAO l = new LogDAO(ListaAit.this);
			ParametroDAO pardao = new ParametroDAO(ListaAit.this);
			Cursor cpar = pardao.getParametros();
			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();

				mmOutStream = mmSocket.getOutputStream();

				AitDAO a = new AitDAO(ListaAit.this);
				List<Ait> ls = a.getListaAitPrint((String) getIntent()
						.getSerializableExtra("agente"));

				for (Ait b : ls) {

					montaimpressao(b.getId());
					byte[] buffer = mens.getBytes();
					mmOutStream.write(buffer, 0, buffer.length);
					try {
						l.gravalog(
								"Imprimiu AIT- " + b.getAit(),
								"UPDATE",
								SimpleCrypto.decrypt(info, cpar.getString(cpar
										.getColumnIndex("orgaoautuador"))),
								SimpleCrypto.decrypt(info, cpar.getString(cpar
										.getColumnIndex("seriepda"))),
								(String) getIntent().getSerializableExtra(
										"agente"), ListaAit.this);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(12000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// mmOutStream.write(buffer );
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

			AitDAO aitdao = new AitDAO(ListaAit.this);
			Cursor c = aitdao.getAit(idAit);

			// grava data e hora do envio para a impressora
			aitdao.atualizaImpressao(idAit, c);
			aitdao.close();

			ParametroDAO pardao = new ParametroDAO(ListaAit.this);
			Cursor cpar = pardao.getParametros();
			pardao.close();

			try {
				impressora = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("impressora")));
				ativo = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("servidorweb")))
						.toUpperCase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				// Obtem , Logradouro ,Especie, Tipo
				LogradouroDAO logdao = new LogradouroDAO(ListaAit.this);
				desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradouro"))));

				desclog += (" " + SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradouronum"))));
				logdao.close();

				EspecieDAO espdao = new EspecieDAO(ListaAit.this);
				especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("especie"))));
				espdao.close();

				TipoDAO tipdao = new TipoDAO(ListaAit.this);
				tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipo"))));
				tipdao.close();

				MedidaAdmDAO medidadao = new MedidaAdmDAO(ListaAit.this);
				medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("medidaadm"))));
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

				saida += cpar.getString(cpar.getColumnIndex("prefeitura"))
						+ String.format("\r\n");
				saida += "Orgao Autuador:"
						+ cpar.getString(cpar.getColumnIndex("orgaoautuador"))
						+ String.format("\r\n");

				saida += "------------------" + String.format("\r\n");
				saida += "Dados da Infracao" + String.format("\r\n");
				saida += "------------------" + String.format("\r\n");

				saida += "Ait:"
						+ cpar.getString(cpar.getColumnIndex("serieait"))
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

				saida += "-----------------" + String.format("\r\n");
				saida += "Local da Infracao " + String.format("\r\n");
				saida += "-----------------" + String.format("\r\n");

				saida += this.desclog + String.format("\r\n");
				saida += this.ctiplog + String.format("\r\n");

				saida += "--------------------------" + String.format("\r\n");
				saida += "Data:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("data")))
						+ "-"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("hora")))
						+ String.format("\r\n");
				saida += "--------------------------" + String.format("\r\n");

				AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
						ListaAit.this);
				Cursor c1 = aitenq.getLista1(idAit);

				enquads = " ";
				c1.moveToNext();

				// enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

				EnquadramentoDAO dao = new EnquadramentoDAO(ListaAit.this);
				List<Enquadramento> enquadramento = dao.getLista(
						SimpleCrypto.decrypt(info,
								c1.getString(c1.getColumnIndex("codigo"))),
						ListaAit.this);
				dao.close();

				enquads += enquadramento.get(0).toString();

				// enquads = Utilitarios.quebraLinha(enquads);

				c1.close();

				saida += "Enquadramento:" + enquads + String.format("\r\n");

				saida += "-------------------------" + String.format("\r\n");
				saida += "Identificacao do Infrator" + String.format("\r\n");

				saida += "-------------------------" + String.format("\r\n");

				saida += "Nome:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("nome")))
						+ String.format("\r\n");
				saida += "CPF:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("cpf")))
						+ String.format("\r\n");
				saida += "PGU:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("pgu")))
						+ " "
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("uf")))
						+ String.format("\r\n");
				saida += "Assinatura:______________" + String.format("\r\n");
				// saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+
				// String.format("\r\n");

				saida += "-----------------------" + String.format("\r\n");
				saida += "Identificacao do Agente" + String.format("\r\n");
				saida += "-----------------------" + String.format("\r\n");
				saida += "Matric.(AG):"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("agente")))
						+ String.format("\r\n");
				saida += "Lavrado por _______________" + String.format("\r\n");

				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("observacoes"))).length() > 0) {

					if (SimpleCrypto.decrypt(info,
							cpar.getString(cpar.getColumnIndex("imprimeobs")))
							.contains("1")) {
						saida += String.format("\r\n");
						saida += "-----------------------"
								+ String.format("\r\n");
						saida += "Observacoes:" + String.format("\r\n");
						saida += SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("observacoes")))
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
					saida += "-----------------------" + String.format("\r\n");
					saida += "Medida Administrativa:" + String.format("\r\n");
					saida += medidaadm + String.format("\r\n");

				}

				// **************************************************************************************
				// 08.03.2012
				// Preencheu dados equipamento, exemplo decibelímetro ?
				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("equipamento"))).length() > 0) {
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
					saida += "Limite Regulamentado:" + String.format("\r\n");
					saida += SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("limitereg")))
							+ String.format("\r\n");

				}

				saida += String.format("\r\n");
				saida += String.format("\r\n");
				saida += "----------------------------" + String.format("\r\n");
				saida += "E obrigatoria a presenca do" + String.format("\r\n");
				saida += "codigo INFRAEST ou RENAINF nas "
						+ String.format("\r\n");
				saida += "notificacoes sob pena de " + String.format("\r\n");
				saida += "invalidade da multa." + String.format("\r\n");
				saida += "----------------------------" + String.format("\r\n");

				saida += String.format("\r\n");
				saida += String.format("\r\n");

				cpar.close();

				c.close();
				mens = saida;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public String EnviaCoordenadasPost(String Latitude, String Longitude) {

		String ret = "ok";

		// String url =
		// "http://cobrasin.no-ip.biz:9090/JsonWcf/JsonWcfService.svc/MakePdf";
		// "http://192.168.1.108/JsonWcf/JsonWcfService.svc/MakePdf";//
		UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(ListaAit.this);

		// String url = urlswebtrans.geturl("urlcripto");

		// String url =
		// "http://192.168.2.103/JsonWcf/JsonWcfService.svc/InsertLogC";
		// String url =
		// "http://192.168.2.103/JsonWcf/JsonWcfService.svc/GetEndC";

		String url = urlswebtrans.geturl("gps");// .replace("6742", "9090");

		urlswebtrans.close();
		Cryptor cr = new Cryptor();

		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url.substring(0, url.length() - 1));

			JSONStringer json = new JSONStringer();
			json.object();
			json.key("p");
			json.object();
			json.key("Latitude").value(cr.encrypt(Latitude));
			json.key("Longitude").value(cr.encrypt(Longitude));
			json.key("Patrimonio").value(
					cr.encrypt(SimpleCrypto.decrypt(info, seriepda)));
			json.key("Agente").value(cr.encrypt(salvaAgente));

			// .replace("/", "BARRA") +
			// ";"+SimpleCrypto.decrypt(info,seriepda)+";"+salvaAgente;

			json.endObject();

			// post.setEntity(new StringEntity(json.toString(), "UTF-8"));
			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			post.setEntity(entity);
			// post.setEntity(new StringEntity(pdf,"UTF-8"));
			HttpResponse response = client.execute(post);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			retornoweb = EntityUtils.toString(response.getEntity());

			if (statusCode == 200) {
				JSONArray jsonArray = new JSONArray("[" + retornoweb + "]");
				JSONObject json1 = jsonArray.getJSONObject(0);
				String e = json1.getString("GetLogradouroResult");
				retornoweb = e;

				if (contamensagem == 0) {
					contamensagem = 1;
					progress.cancel();
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							ListaAit.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Você está em " + cr.decrypt(retornoweb)
							+ "?");
					aviso.setNeutralButton("Não", null);
					aviso.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Cryptor cr = new Cryptor();
									try {
										logradouroGps = cr.decrypt(retornoweb);
									} catch (KeyException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InvalidAlgorithmParameterException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalBlockSizeException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (BadPaddingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (GeneralSecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
					aviso.show();
				} else {

				}
			} else {
				progress.cancel();
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Erro ao obter o logradouro!\nTente Novamente!");
				aviso.setNeutralButton("OK", null);
				aviso.show();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalBlockSizeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BadPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	public void EnviaCoordenadas(double latitude, double longitude) {
		String url = "http://189.109.35.10:9090/JsonWcf/JsonWcfService.svc/GetLogradouroC/";

		UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(ListaAit.this);

		// String url =
		// "http://192.168.2.103/JsonWcf/JsonWcfService.svc/GetLogradouro/";//urlswebtrans.geturl("gps");

		// String urlBase = urlswebtrans.geturl("imei");

		urlswebtrans.close();
		HttpClient httpclient = new DefaultHttpClient();

		try {
			HttpParams httpParameters = new BasicHttpParams();

			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			String urlLog = "";
			Cryptor cripto = new Cryptor();
			try {
				urlLog = cripto.encrypt(String.valueOf(latitude)) + ";"
						+ cripto.encrypt(String.valueOf(longitude)) + ";"
						+ cripto.encrypt(SimpleCrypto.decrypt(info, seriepda))
						+ ";" + cripto.encrypt(salvaAgente);
				// urlLog = url+ String.valueOf(latitude).replace("/", "BARRA")
				// + ";" + String.valueOf(longitude).replace("/", "BARRA") +
				// ";"+SimpleCrypto.decrypt(info,seriepda)+";"+salvaAgente;
				url = url
						+ urlLog.replace("/", "BARRA").replace(":", "-")
								.replace(" ", "%20").replace("\n", "")
								.replace("=", "IGUAL");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet httpget = new HttpGet(urlLog);
			HttpResponse response = httpclient.execute(httpget);
			// HttpResponse response = httpclient.execute(post);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			try {
				retornoweb = cripto.decrypt(EntityUtils.toString(response
						.getEntity()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (statusCode == 200) {
				if (contamensagem == 0) {
					contamensagem = 1;
					progress.cancel();
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							ListaAit.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Você está em " + retornoweb + "?");
					aviso.setNeutralButton("Não", null);
					aviso.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									logradouroGps = retornoweb;
								}
							});
					aviso.show();
				} else {

				}
			} else {
				progress.cancel();
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ListaAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Erro ao obter o logradouro!\nTente Novamente!");
				aviso.setNeutralButton("OK", null);
				aviso.show();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// ret = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// ret = false;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// String str =
		// "Latitude: "+location.getLatitude()+" \nLongitude: "+location.getLongitude();
		// Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
		EnviaCoordenadasPost(String.valueOf(location.getLatitude()),
				String.valueOf(location.getLongitude()));
	}

	@Override
	public void onProviderDisabled(String provider) {

		/******** Called when User off Gps *********/

		Toast.makeText(getBaseContext(), "Gps Desativado!", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onProviderEnabled(String provider) {

		/******** Called when User on Gps *********/

		Toast.makeText(getBaseContext(), "Gps Ativado!", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
