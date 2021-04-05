package br.com.cobrasin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Logs;
import br.com.cobrasin.tabela.Parametro;

import com.thingmagic.Gen2;
import com.thingmagic.Reader;
import com.thingmagic.ReadListener;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VeiculosRestricaoRFID extends Activity implements ReadListener {
	private ProgressDialog progress;
	private ArrayList<String> mylist;
	private ArrayAdapter<String> myarrayAdapter;
	private ArrayList<String> mylistg0obu;
	private ArrayAdapter<String> myarrayAdapterg0obu;
	private ArrayList<String> mylistg0fp1;
	private ArrayAdapter<String> myarrayAdapterg0fp1;
	private ArrayList<String> mylistg0fp2;
	private ArrayAdapter<String> myarrayAdapterg0fp2;
	private ArrayList<String> mylistpaobu;
	private ArrayAdapter<String> myarrayAdapterpaobu;
	private ArrayList<String> mylistpa64;
	private ArrayAdapter<String> myarrayAdapterpa64;

	private static final String OTHER_TYPE = "Wrong";
	private float volume = 50;
	private AudioManager vibe;
	private TagReadData[] trd;
	private boolean isPA = false;
	private boolean isGen2 = false;
	private boolean isG0 = false;
	private boolean reading = false;
	private boolean connected = false;
	private AcuraIAV acura = new AcuraIAV();
	public Reader rdr = null;
	private String G0command = "OBUAUTHID";
	private String PAcommand = "OBUAUTHENTICATE";

	private String seriepda = "";
	private String tipoLeituraTAG = "";
	private String info = Utilitarios.getInfo();

	private TextView lblTAGDetectada;
	private TextView lblTipoLeitura;

	private TextView lblVeiculoEncontrado;
	private TextView lblPlaca;
	private TextView lblModelo;
	private TextView lblMarca;
	private TextView lblCor;
	private TextView lblAnoLicenciamento;

	private Button btnNovoAIT;

	private LinearLayout pnlDadosVeiculoRFID;

	private Handler handler = new Handler();
	
	private String agente = "";

	private String PlacaDetectada = "";
	private String MarcaModeloDetectada = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.veiculos_restricao_rfid);
		agente = (String) getIntent().getSerializableExtra("agente");
		lblTAGDetectada = (TextView) findViewById(R.id.lblTagDetectada);
		lblTipoLeitura = (TextView) findViewById(R.id.lblTipoLeitura);
		lblVeiculoEncontrado = (TextView) findViewById(R.id.lblVeiculoEncontradoRFID);
		lblPlaca = (TextView) findViewById(R.id.lblPlacaRFID);
		lblModelo = (TextView) findViewById(R.id.lblModeloRFID);
		lblMarca = (TextView) findViewById(R.id.lblMarcaRFID);
		lblCor = (TextView) findViewById(R.id.lblCorRFID);
		lblAnoLicenciamento = (TextView) findViewById(R.id.lblAnoLicenciamentoRFID);
		btnNovoAIT = (Button) findViewById(R.id.btnNovoAitRFID);
		pnlDadosVeiculoRFID = (LinearLayout) findViewById(R.id.pnlDadosVeiculoRFID);

		lblTAGDetectada.setVisibility(View.INVISIBLE);
		lblTipoLeitura.setVisibility(View.INVISIBLE);
		lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
		btnNovoAIT.setVisibility(View.INVISIBLE);
		pnlDadosVeiculoRFID.setVisibility(View.INVISIBLE);

		btnNovoAIT.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(VeiculosRestricaoRFID.this,
						ListaTipoAit.class);
				i.putExtra("agente", agente);
				i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
				i.putExtra("PlacaDetectada",PlacaDetectada);
				startActivity(i);
				finish();
			}
		});
		
		
		ReaderConfig();

		vibe = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mylist = new ArrayList<String>();
		myarrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, mylist);
		mylistg0obu = new ArrayList<String>();
		myarrayAdapterg0obu = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, mylistg0obu);
		mylistg0fp1 = new ArrayList<String>();
		myarrayAdapterg0fp1 = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, mylistg0fp1);
		mylistg0fp2 = new ArrayList<String>();
		myarrayAdapterg0fp2 = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, mylistg0fp2);
		mylistpaobu = new ArrayList<String>();
		myarrayAdapterpaobu = new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, mylistpaobu);
		mylistpa64 = new ArrayList<String>();
	}

	public void ReaderConfig() {
		try {
			Parametro param = new Parametro();
			ParametroDAO pardao = new ParametroDAO(VeiculosRestricaoRFID.this);
			Cursor cpar = pardao.getParametros();
			pardao.close();

			seriepda = cpar.getString(cpar.getColumnIndex("seriepda"));
			try {
				tipoLeituraTAG = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("tipoLeituraTAG")));
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						VeiculosRestricaoRFID.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Configure o Tipo de Leitura da TAG para iniciar o Modo RFID!");
				aviso.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						});

				aviso.show();
				return;
			}

			try {
				Runtime.getRuntime().exec("su -c 'chmod 666 /dev/ttyS0'");
			} catch (IOException e) {
				e.printStackTrace();
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						VeiculosRestricaoRFID.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Esse dispositivo não suporta Modo RFID!");
				aviso.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						});

				aviso.show();
				return;
			}
			rdr = Reader.create("tmr://dev/ttyS3");
			// bt_Open.setEnabled(true);

			rdr.connect();
			rdr.addReadListener(this);
			// Reader Initialization Parameters
			rdr.paramSet("/reader/region/id", Reader.Region.NA);
			rdr.paramSet("/reader/tagop/protocol", TagProtocol.GEN2);
			rdr.paramSet("/reader/tagop/antenna", 2);
			rdr.paramSet("/reader/gen2/session", Gen2.Session.S0);
			rdr.paramSet("/reader/gen2/target", Gen2.Target.AB);
			rdr.paramSet("/reader/gen2/q", new Gen2.DynamicQ());
			rdr.paramSet("/reader/gen2/BLF", Gen2.LinkFrequency.LINK320KHZ);
			// Reader Read Parameters
			rdr.paramSet("/reader/radio/readPower", 3000);
			rdr.paramSet("/reader/read/asyncOnTime", 100);
			rdr.paramSet("/reader/read/asyncOffTime", 20);

			// Only antenna 2 is connected
			SimpleReadPlan srp = new SimpleReadPlan(new int[] { 2 },
					TagProtocol.GEN2, null, null, 100);
			rdr.paramSet("/reader/read/plan", srp);
			if (tipoLeituraTAG == null) {
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						VeiculosRestricaoRFID.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Configure o tipo de leitura da TAG!");
				aviso.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						});
				aviso.show();

			} else {
				if (tipoLeituraTAG.contains("GEN2")) {
					tipoLeituraTAG = "GEN2";
					lblTipoLeitura.setText("Tipo de Leitura: GEN2");
					isPA = false;
					isGen2 = true;
					isG0 = false;
				}
				if (tipoLeituraTAG.contains("G0")) {
					tipoLeituraTAG = "G0";
					lblTipoLeitura.setText("Tipo de Leitura: G0");
					isPA = false;
					isGen2 = false;
					isG0 = true;

					Gen2.Denatran.IAV.OBUAuthID obuaid = new Gen2.Denatran.IAV.OBUAuthID(
							(byte) 0x80);
					// Only antenna 2 is connected
					SimpleReadPlan srpobuaid = new SimpleReadPlan(
							new int[] { 2 }, TagProtocol.GEN2, null, obuaid, 10);
					rdr.paramSet("/reader/read/plan", srpobuaid);
					G0command = "OBUAUTHID";
					acura.KeyAK("01010101010101010101010101010101");

				}
			}
			Toast.makeText(this, "Dispositivo Conectado!", Toast.LENGTH_SHORT)
					.show();
			// TagCount.setEnabled(true);
			// TagRead.setEnabled(true);
			// TotalCount.setEnabled(true);
			// cbMute.setEnabled(true);
			// bt_Gen2.setEnabled(true);
			// bt_Clear.setEnabled(true);
			// bt_G0.setEnabled(true);
			// bt_PA.setEnabled(true);
			// cbContinue.setEnabled(true);
			// lv.setEnabled(true);
			// bt_Save.setEnabled(true);
			// bt_Open.setEnabled(true);
			// TotalRead.setEnabled(true);
			connected = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void tagRead(Reader arg0, TagReadData t) {
		// TODO Auto-generated method stub

		handler2.sendMessage(Message.obtain(handler2, 1000, t));
	}

	private Handler handler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				ListTag((TagReadData) msg.obj);

				break;
			}
		}

	};

	public void ListTag(TagReadData tag) {
		if (isPA == true) {
			if (PAcommand == "OBUAUTHENTICATE") {
				if ((acura.getPA_OBUID(tag.getData()) != OTHER_TYPE && (acura
						.getPA_OBUID(tag.getData()) != null))) {
					if (mylistpaobu.size() != 0) {
						boolean exist = false;
						for (int o = 0; o <= mylistpaobu.size() - 1; o++) {
							if (!(mylistpaobu.get(o).substring(0, 10)
									.contains(" "))
									&& mylistpaobu
											.get(o)
											.substring(0, 10)
											.compareTo(
													acura.getPA_OBUID(tag
															.getData())) == 0) {
								int c = Integer.parseInt(mylistpaobu.get(o)
										.substring(14,
												mylistpaobu.get(o).length()));
								c = c + tag.getReadCount();
								mylistpaobu.set(o,
										mylistpaobu.get(o).substring(0, 10)
												+ "    " + String.valueOf(c));
								myarrayAdapterpaobu.notifyDataSetChanged();
								// TotalCount
								// .setText(String.valueOf(Integer
								// .parseInt((String) TotalCount
								// .getText())
								// + tag.getReadCount()));
								vibe.playSoundEffect(
										AudioManager.FX_KEYPRESS_STANDARD,
										volume);

								exist = true;
								break;
							}
						}
						if (exist == false) {
							mylistpaobu.add(acura.getPA_OBUID(tag.getData())
									+ "    "
									+ String.valueOf(tag.getReadCount()));
							myarrayAdapterpaobu.notifyDataSetChanged();
							// TotalCount.setText(String.valueOf(Integer
							// .parseInt((String) TotalCount.getText())
							// + tag.getReadCount()));
							vibe.playSoundEffect(
									AudioManager.FX_KEYPRESS_STANDARD, volume);
							// countpa = mylistpaobu.size();
							// TagCount.setText(String.valueOf(countpa));
						}
					} else {
						mylistpaobu.add(acura.getPA_OBUID(tag.getData())
								+ "    " + String.valueOf(tag.getReadCount()));
						myarrayAdapterpaobu.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						// countpa = mylistpaobu.size();
						// TagCount.setText(String.valueOf(countpa));
					}
				} else {

				}
			} else if (PAcommand == "OBUDATA64") {
				if ((acura.getPA_DATA64(tag.getData()) != OTHER_TYPE && (acura
						.getPA_DATA64(tag.getData()) != null))) {
					if (mylistpa64.size() != 0) {
						boolean exist = false;
						for (int o = 0; o <= mylistpa64.size() - 1; o++) {
							if (!(mylistpa64.get(o).substring(0, 16)
									.contains(" "))
									&& mylistpa64
											.get(o)
											.substring(0, 16)
											.compareTo(
													acura.getPA_DATA64(tag
															.getData())) == 0) {
								int c = Integer.parseInt(mylistpa64.get(o)
										.substring(20,
												mylistpa64.get(o).length()));
								c = c + tag.getReadCount();
								mylistpa64.set(o,
										mylistpa64.get(o).substring(0, 16)
												+ "    " + String.valueOf(c));
								myarrayAdapterpa64.notifyDataSetChanged();
								// TotalCount
								// .setText(String.valueOf(Integer
								// .parseInt((String) TotalCount
								// .getText())
								// + tag.getReadCount()));
								vibe.playSoundEffect(
										AudioManager.FX_KEYPRESS_STANDARD,
										volume);
								exist = true;
								break;
							}
						}
						if (exist == false) {
							mylistpa64.add(acura.getPA_DATA64(tag.getData())
									+ "    "
									+ String.valueOf(tag.getReadCount()));
							myarrayAdapterpa64.notifyDataSetChanged();
							// TotalCount.setText(String.valueOf(Integer
							// .parseInt((String) TotalCount.getText())
							// + tag.getReadCount()));
							vibe.playSoundEffect(
									AudioManager.FX_KEYPRESS_STANDARD, volume);
							// countpa = mylistpa64.size();
							// TagCount.setText(String.valueOf(countpa));
						}
					} else {
						mylistpa64.add(acura.getPA_DATA64(tag.getData())
								+ "    " + String.valueOf(tag.getReadCount()));
						myarrayAdapterpa64.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						// countpa = mylistpa64.size();
						// TagCount.setText(String.valueOf(countpa));
					}
				} else {

				}
			}
		} else if (isG0 == true) {
			if (G0command == "OBUAUTHID") {
				if ((acura.getOBU_AuthID(tag.getData()) != OTHER_TYPE)
						&& (acura.getOBU_AuthID(tag.getData()) != null)) {
					if (mylistg0obu.size() != 0) {
						boolean exist = false;
						for (int o = 0; o <= mylistg0obu.size() - 1; o++) {
							if (!(mylistg0obu.get(o).substring(0, 12)
									.contains(" "))
									&& mylistg0obu
											.get(o)
											.substring(0, 12)
											.compareTo(
													acura.getOBU_AuthID(tag
															.getData())) == 0) {
								int c = Integer.parseInt(mylistg0obu.get(o)
										.substring(16,
												mylistg0obu.get(o).length()));
								c = c + tag.getReadCount();
								mylistg0obu.set(o,
										mylistg0obu.get(o).substring(0, 12)
												+ "    " + String.valueOf(c));
								myarrayAdapterg0obu.notifyDataSetChanged();
								// TotalCount
								// .setText(String.valueOf(Integer
								// .parseInt((String) TotalCount
								// .getText())
								// + tag.getReadCount()));
								lblTAGDetectada.setVisibility(View.VISIBLE);
								lblTipoLeitura.setVisibility(View.VISIBLE);
								lblTAGDetectada.setText("TAG Detectada - "
										+ acura.getOBU_AuthID(tag.getData()));

								ConsultaVeiculo(
										acura.getOBU_AuthID(tag.getData()),
										"G0");
								vibe.playSoundEffect(
										AudioManager.FX_KEYPRESS_STANDARD,
										volume);
								exist = true;
								break;
							}
						}
						if (exist == false) {
							mylistg0obu.add(acura.getOBU_AuthID(tag.getData())
									+ "    "
									+ String.valueOf(tag.getReadCount()));
							myarrayAdapterg0obu.notifyDataSetChanged();
							/*
							 * TotalCount.setText(String.valueOf(Integer
							 * .parseInt((String) TotalCount.getText()) +
							 * tag.getReadCount()));
							 */
							lblTAGDetectada.setVisibility(View.VISIBLE);
							lblTipoLeitura.setVisibility(View.VISIBLE);
							lblTAGDetectada.setText("TAG Detectada - "
									+ acura.getOBU_AuthID(tag.getData()));
							ConsultaVeiculo(acura.getOBU_AuthID(tag.getData()),
									"G0");
							vibe.playSoundEffect(
									AudioManager.FX_KEYPRESS_STANDARD, volume);
							// countg0 = mylistg0obu.size();
							// TagCount.setText(String.valueOf(countg0));
						}
					} else {
						mylistg0obu.add(acura.getOBU_AuthID(tag.getData())
								+ "    " + String.valueOf(tag.getReadCount()));
						myarrayAdapterg0obu.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						lblTAGDetectada.setVisibility(View.VISIBLE);
						lblTipoLeitura.setVisibility(View.VISIBLE);
						lblTAGDetectada.setText("TAG Detectada - "
								+ acura.getOBU_AuthID(tag.getData()));
						ConsultaVeiculo(acura.getOBU_AuthID(tag.getData()),
								"G0");
						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						// countg0 = mylistg0obu.size();
						// TagCount.setText(String.valueOf(countg0));
					}
				} else {

				}
			} else if (G0command == "OBUFULLPASS1") {
				if ((acura.getOBU_FullPass1(tag.getData()) != OTHER_TYPE)
						&& (acura.getOBU_FullPass1(tag.getData()) != null)) {
					if (mylistg0fp1.size() != 0) {
						boolean exist = false;
						for (int o = 0; o <= mylistg0fp1.size() - 1; o++) {
							if (!(mylistg0fp1.get(o).substring(0, 16)
									.contains(" "))
									&& mylistg0fp1
											.get(o)
											.substring(0, 16)
											.compareTo(
													acura.getOBU_FullPass1(tag
															.getData())) == 0) {
								int c = Integer.parseInt(mylistg0fp1.get(o)
										.substring(20,
												mylistg0fp1.get(o).length()));
								c = c + tag.getReadCount();
								mylistg0fp1.set(o,
										mylistg0fp1.get(o).substring(0, 16)
												+ "    " + String.valueOf(c));
								myarrayAdapterg0fp1.notifyDataSetChanged();
								// TotalCount
								// .setText(String.valueOf(Integer
								// .parseInt((String) TotalCount
								// .getText())
								// + tag.getReadCount()));
								lblTAGDetectada.setVisibility(View.VISIBLE);
								lblTipoLeitura.setVisibility(View.VISIBLE);
								lblTAGDetectada
										.setText("TAG Detectada - "
												+ acura.getOBU_FullPass1(tag
														.getData()));
								ConsultaVeiculo(
										acura.getOBU_FullPass1(tag.getData()),
										"G0");
								vibe.playSoundEffect(
										AudioManager.FX_KEYPRESS_STANDARD,
										volume);
								exist = true;
								break;
							}
						}
						if (exist == false) {
							mylistg0fp1.add(acura.getOBU_FullPass1(tag
									.getData())
									+ "    "
									+ String.valueOf(tag.getReadCount()));
							myarrayAdapterg0fp1.notifyDataSetChanged();
							// TotalCount.setText(String.valueOf(Integer
							// .parseInt((String) TotalCount.getText())
							// + tag.getReadCount()));
							lblTAGDetectada.setVisibility(View.VISIBLE);
							lblTipoLeitura.setVisibility(View.VISIBLE);
							lblTAGDetectada.setText("TAG Detectada - "
									+ acura.getOBU_FullPass1(tag.getData()));
							ConsultaVeiculo(
									acura.getOBU_FullPass1(tag.getData()), "G0");
							vibe.playSoundEffect(
									AudioManager.FX_KEYPRESS_STANDARD, volume);
							// countg0 = mylistg0fp1.size();
							// TagCount.setText(String.valueOf(countg0));
						}
					} else {
						mylistg0fp1.add(acura.getOBU_FullPass1(tag.getData())
								+ "    " + String.valueOf(tag.getReadCount()));
						myarrayAdapterg0fp1.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						lblTAGDetectada.setVisibility(View.VISIBLE);
						lblTipoLeitura.setVisibility(View.VISIBLE);
						lblTAGDetectada.setText("TAG Detectada - "
								+ acura.getOBU_FullPass1(tag.getData()));
						ConsultaVeiculo(acura.getOBU_FullPass1(tag.getData()),
								"G0");
						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						// countg0 = mylistg0fp1.size();
						// TagCount.setText(String.valueOf(countg0));
					}
				} else {

				}
			} else if (G0command == "OBUFULLPASS2") {
				if ((acura.getOBU_FullPass2(tag.getData()) != OTHER_TYPE)
						&& (acura.getOBU_FullPass2(tag.getData()) != null)) {
					if (mylistg0fp2.size() != 0) {
						boolean exist = false;
						for (int o = 0; o <= mylistg0fp2.size() - 1; o++) {
							if (!(mylistg0fp2.get(o).substring(0, 16)
									.contains(" "))
									&& mylistg0fp2
											.get(o)
											.substring(0, 16)
											.compareTo(
													acura.getOBU_FullPass2(tag
															.getData())) == 0) {
								int c = Integer.parseInt(mylistg0fp2.get(o)
										.substring(20,
												mylistg0fp2.get(o).length()));
								c = c + tag.getReadCount();
								mylistg0fp2.set(o,
										mylistg0fp2.get(o).substring(0, 16)
												+ "    " + String.valueOf(c));
								myarrayAdapterg0fp2.notifyDataSetChanged();
								// TotalCount
								// .setText(String.valueOf(Integer
								// .parseInt((String) TotalCount
								// .getText())
								// + tag.getReadCount()));
								lblTAGDetectada.setVisibility(View.VISIBLE);
								lblTipoLeitura.setVisibility(View.VISIBLE);
								lblTAGDetectada
										.setText("TAG Detectada - "
												+ acura.getOBU_FullPass2(tag
														.getData()));
								ConsultaVeiculo(
										acura.getOBU_FullPass2(tag.getData()),
										"G0");
								vibe.playSoundEffect(
										AudioManager.FX_KEYPRESS_STANDARD,
										volume);
								exist = true;
								break;
							}
						}
						if (exist == false) {
							mylistg0fp2.add(acura.getOBU_FullPass2(tag
									.getData())
									+ "    "
									+ String.valueOf(tag.getReadCount()));
							myarrayAdapterg0fp2.notifyDataSetChanged();
							// TotalCount.setText(String.valueOf(Integer
							// .parseInt((String) TotalCount.getText())
							// + tag.getReadCount()));
							lblTAGDetectada.setVisibility(View.VISIBLE);
							lblTipoLeitura.setVisibility(View.VISIBLE);
							lblTAGDetectada.setText("TAG Detectada - "
									+ acura.getOBU_FullPass2(tag.getData()));

							ConsultaVeiculo(
									acura.getOBU_FullPass2(tag.getData()), "G0");

							vibe.playSoundEffect(
									AudioManager.FX_KEYPRESS_STANDARD, volume);
							// countg0 = mylistg0fp2.size();
							// TagCount.setText(String.valueOf(countg0));
						}
					} else {
						mylistg0fp2.add(acura.getOBU_FullPass2(tag.getData())
								+ "    " + String.valueOf(tag.getReadCount()));
						myarrayAdapterg0fp2.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						lblTAGDetectada.setVisibility(View.VISIBLE);
						lblTipoLeitura.setVisibility(View.VISIBLE);
						lblTAGDetectada.setText("TAG Detectada - "
								+ acura.getOBU_FullPass2(tag.getData()));

						ConsultaVeiculo(acura.getOBU_FullPass2(tag.getData()),
								"G0");

						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						// countg0 = mylistg0fp2.size();
						// TagCount.setText(String.valueOf(countg0));
					}
				} else {

				}
			} else if (G0command == "OBUFULLPASS") {
			}
		} else {
			String epcStr = null;
			if (tag.epcString().length() != 24) {
				epcStr = tag.epcString();
				for (int k = 0; k <= 24 - tag.epcString().length(); k++)
					epcStr = epcStr + " ";
			} else {
				epcStr = tag.epcString();
			}
			if (mylist.size() != 0) {
				boolean exist = false;
				for (int o = 0; o <= mylist.size() - 1; o++) {
					if ((mylist.get(o).length() < 29)) {
						break;
					}
					if (mylist.get(o).substring(0, 24).compareTo(epcStr) == 0) {
						int c = Integer.parseInt(mylist.get(o).substring(28,
								mylist.get(o).length()));
						c = c + tag.getReadCount();
						mylist.set(o, mylist.get(o).substring(0, 24) + "    "
								+ String.valueOf(c));
						myarrayAdapter.notifyDataSetChanged();
						// TotalCount.setText(String.valueOf(Integer
						// .parseInt((String) TotalCount.getText())
						// + tag.getReadCount()));
						lblTAGDetectada.setVisibility(View.VISIBLE);
						lblTipoLeitura.setVisibility(View.VISIBLE);
						lblTAGDetectada.setText("TAG Detectada - "
								+ tag.getTag().epcString());
						ConsultaVeiculo(tag.getTag().epcString(), "GEN2");

						vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
								volume);
						exist = true;
						break;
					}
				}
				if (exist == false) {
					mylist.add(epcStr + "    "
							+ String.valueOf(tag.getReadCount()));
					myarrayAdapter.notifyDataSetChanged();
					// TotalCount.setText(String.valueOf(Integer
					// .parseInt((String) TotalCount.getText())
					// + tag.getReadCount()));
					lblTAGDetectada.setVisibility(View.VISIBLE);
					lblTipoLeitura.setVisibility(View.VISIBLE);
					lblTAGDetectada.setText("TAG Detectada - "
							+ tag.getTag().epcString());

					ConsultaVeiculo(tag.getTag().epcString(), "GEN2");

					vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,
							volume);
					// countgen2 = mylist.size();
					// TagCount.setText(String.valueOf(countgen2));
				}
				// mylist.add("EPC: " + tag.epcString() + "  Phase: " +
				// String.valueOf(tag.getPhase()));
				// myarrayAdapter.notifyDataSetChanged();
			} else {
				mylist.add(epcStr + "    " + String.valueOf(tag.getReadCount()));
				myarrayAdapter.notifyDataSetChanged();
				// TotalCount.setText(String.valueOf(Integer
				// .parseInt((String) TotalCount.getText())
				// + tag.getReadCount()));
				lblTAGDetectada.setVisibility(View.VISIBLE);
				lblTipoLeitura.setVisibility(View.VISIBLE);
				lblTAGDetectada.setText("TAG Detectada - "
						+ tag.getTag().epcString());

				ConsultaVeiculo(tag.getTag().epcString(), "GEN2");

				vibe.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume);
				// countgen2 = mylist.size();
				// TagCount.setText(String.valueOf(countgen2));
			}

		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (connected == true) {
			if (!reading)
				if (keyCode == KeyEvent.KEYCODE_SOFT_RIGHT
						|| keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
						|| keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
					/*
					 * bt_Gen2.setEnabled(false); bt_G0.setEnabled(false);
					 * bt_PA.setEnabled(false); bt_Clear.setEnabled(false);
					 * cbContinue.setEnabled(false); bt_Open.setEnabled(false);
					 * bt_Save.setEnabled(false); KeyAK.setEnabled(false);
					 */
					rdr.startReading();
					reading = true;
				}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (connected && reading) {
			if (keyCode == KeyEvent.KEYCODE_SOFT_RIGHT
					|| keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
					|| keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
				// if(cbContinue.isChecked()==true){
				rdr.stopReading();
				reading = false;
				/*
				 * bt_Gen2.setEnabled(true); bt_G0.setEnabled(true);
				 * bt_PA.setEnabled(true); bt_Clear.setEnabled(true);
				 * cbContinue.setEnabled(true); bt_Open.setEnabled(true);
				 * bt_Save.setEnabled(true); KeyAK.setEnabled(true);
				 */
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	private void ConsultaVeiculo(final String EPC, String TipoLeitura) {

		String Placa = "";
		String Marca = "";
		String Modelo = "";
		String Cor = "";
		String AnoLicenciamento = "";
		String IdMarca = "";
		String IdCor = "";

		if (TipoLeitura.contains("GEN2")) {

			SQLiteDatabase s = SQLiteDatabase.openDatabase(
					"mnt/sdcard/veiculos_rodizio.SDB", null, 0);

			try {
				Cursor cus = null;

				// Pesquisa Veiculo
				cus = s.rawQuery("Select * from veiculos where GEN2 = '" + EPC
						+ "'", null);
				if (cus.getCount() > 0) {
					while (cus.moveToNext()) {
						Placa = cus.getString(0);
						IdMarca = cus.getString(1);
						IdCor = cus.getString(cus.getColumnIndex("IdCor"));
						AnoLicenciamento = cus.getString(cus
								.getColumnIndex("AnoLicenciamento"));
						// IdTipo = cus.getString(2);
						// IdEspecie = cus.getString(3);
					}

					// Pesquisa
					// Marca-------------------------------------------------------------------------
					cus = null;
					cus = s.rawQuery("select * from marcas where Id= ?",
							new String[] { IdMarca });
					while (cus.moveToNext()) {
						Marca = cus.getString(1);
					}

					// Pesquisa
					// Cor----------------------------------------------------------------------------

					try {
						cus = null;
						cus = s.rawQuery("select * from cor where Id = "
								+ IdCor, null);
						while (cus.moveToNext()) {
							Cor = cus.getString(cus.getColumnIndex("Cor"));
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					lblPlaca.setText("Placa: " + Placa);
					PlacaDetectada = Placa;
					lblMarca.setText("Marca: " + Marca);
					MarcaModeloDetectada = Marca;
					lblModelo.setText("Modelo: ");
					lblCor.setText("Cor: " + Cor);
					lblVeiculoEncontrado.setText("Veículo encontrado!");

					lblAnoLicenciamento.setText("Ano Licenciamento: "
							+ AnoLicenciamento);
					btnNovoAIT.setVisibility(View.VISIBLE);
					lblVeiculoEncontrado.setVisibility(View.VISIBLE);
					pnlDadosVeiculoRFID.setVisibility(View.VISIBLE);
					return;
				} else {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							VeiculosRestricaoRFID.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Veículo não encontrado! Deseja pesquisar os dados do veículo online?");
					aviso.setNeutralButton("Não", null);
					aviso.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ConsultarDadosGEN2(EPC);
								}
							});

					aviso.show();
				}
				s.close();
			} catch (SQLiteException e) {
				Log.e("Erro=", e.getMessage());

			}

		} else if (TipoLeitura.contains("G0")) {

			SQLiteDatabase s = SQLiteDatabase.openDatabase(
					"mnt/sdcard/veiculos_rodizio.SDB", null, 0);

			try {
				Cursor cus = null;

				// Pesquisa Veiculo
				cus = s.rawQuery("Select * from veiculos where G0 = '" + EPC
						+ "'", null);
				if (cus.getCount() > 0) {
					while (cus.moveToNext()) {
						Placa = cus.getString(0);
						IdMarca = cus.getString(1);
						IdCor = cus.getString(cus.getColumnIndex("IdCor"));
						AnoLicenciamento = cus.getString(cus
								.getColumnIndex("AnoLicenciamento"));
						// IdTipo = cus.getString(2);
						// IdEspecie = cus.getString(3);
					}

					// Pesquisa
					// Marca-------------------------------------------------------------------------
					cus = null;
					cus = s.rawQuery("select * from marcas where Id= ?",
							new String[] { IdMarca });
					while (cus.moveToNext()) {
						Marca = cus.getString(1);
					}

					// Pesquisa
					// Cor----------------------------------------------------------------------------

					try {
						cus = null;
						cus = s.rawQuery("select * from cor where Id = "
								+ IdCor, null);
						while (cus.moveToNext()) {
							Cor = cus.getString(cus.getColumnIndex("Cor"));
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					lblPlaca.setText("Placa: " + Placa);
					PlacaDetectada = Placa;
					lblMarca.setText("Marca: " + Marca);
					MarcaModeloDetectada = Marca;
					lblModelo.setText("Modelo: ");
					lblCor.setText("Cor: " + Cor);
					lblVeiculoEncontrado.setText("Veículo encontrado!");

					lblAnoLicenciamento.setText("Ano Licenciamento: "
							+ AnoLicenciamento);
					btnNovoAIT.setVisibility(View.VISIBLE);
					lblVeiculoEncontrado.setVisibility(View.VISIBLE);
					pnlDadosVeiculoRFID.setVisibility(View.VISIBLE);
					return;
				} else {
					AlertDialog.Builder aviso = new AlertDialog.Builder(
							VeiculosRestricaoRFID.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("TEC");
					aviso.setMessage("Veículo não encontrado! Deseja pesquisar os dados do veículo online?");
					aviso.setNeutralButton("Não", null);
					aviso.setPositiveButton("Sim",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									ConsultarDadosG0(EPC);
								}
							});

					aviso.show();
				}
				s.close();
			} catch (SQLiteException e) {
				Log.e("Erro=", e.getMessage());
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						VeiculosRestricaoRFID.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Veículo não encontrado! Deseja pesquisar os dados do veículo online?");
				aviso.setNeutralButton("Não", null);
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ConsultarDadosG0(EPC);
							}
						});

				aviso.show();
			}

		} else {
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					VeiculosRestricaoRFID.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Tipo de Leitura não Permitido!");
			aviso.setNeutralButton("OK", null);
			aviso.show();
		}
	}

	public void ConsultarDadosG0(final String EPC) {

		progress = ProgressDialog.show(VeiculosRestricaoRFID.this,
				"Aguarde...", "Consultando Dados na Web!!!", true);
		// aviso = Toast.makeText(context, "Todos aits enviados com sucesso!",
		// Toast.LENGTH_LONG);

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				try {
					WebService web = new WebService();
					JSONArray dt = web
							.ExecuteReaderQuery("Select v.Placa,(Select C.Descricao from [veiculos_rodizio].[dbo].Cores C Where C.Cor = v.CorDEN) Cor,"
									+ "(Select Ma.Descricao from [veiculos_rodizio].[dbo].MarcasDENATRAN Ma WHERE Marca = v.MarcaDEN)'Marca'"
									+ "from [veiculos_rodizio].[dbo].veiculos v where v.G0 = '"
									+ EPC + "'");
					if (dt != null) {
						JSONObject dr = dt.getJSONObject(0);
						final String Placa = dr.getString("Placa");
						PlacaDetectada = Placa;
						final String Marca = dr.getString("Marca");
						MarcaModeloDetectada = Marca;
						final String Cor = dr.getString("Cor");
						String AnoLicenciamento = "";
						try {
							JSONArray dt2 = web
									.ExecuteReaderQuery("Select Convert(Date,prd_veiculo.data_licenciamento,101)AnoLicenciamento "
											+ "from [prd_multas].[dbo].veiculo prd_veiculo "
											+ "WHERE placa_letra = '"
											+ dr.getString("Placa").substring(
													0, 3)
											+ "' and placa_numero = '"
											+ dr.getString("Placa").substring(
													3, 7) + "'");
							if (dt2 != null) {
								JSONObject dr2 = dt2.getJSONObject(0);
								AnoLicenciamento = dr2
										.getString("AnoLicenciamento");
							} else {
								AnoLicenciamento = "";
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							AnoLicenciamento = "";
						}

						VeiculoEncontradoWeb(Placa, Marca, Cor,
								AnoLicenciamento);
					} else {
						mostraMensagem("Veículo não encontrado!");
						AtualizaDadosTela();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mostraMensagem("Veículo não encontrado!");
					AtualizaDadosTela();
				}

				progress.dismiss();

			}
		}).start();
	}

	public void ConsultarDadosGEN2(final String EPC) {

		progress = ProgressDialog.show(VeiculosRestricaoRFID.this,
				"Aguarde...", "Consultando Dados na Web!!!", true);
		// aviso = Toast.makeText(context, "Todos aits enviados com sucesso!",
		// Toast.LENGTH_LONG);

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				try {
					WebService web = new WebService();
					JSONArray dt = web
							.ExecuteReaderQuery("Select v.Placa,(Select C.Descricao from [veiculos_rodizio].[dbo].Cores C Where C.Cor = v.CorDEN) Cor,"
									+ "(Select Ma.Descricao from [veiculos_rodizio].[dbo].MarcasDENATRAN Ma WHERE Marca = v.MarcaDEN)'Marca'"
									+ "from [veiculos_rodizio].[dbo].veiculos v where v.GEN2 = '"
									+ EPC + "'");
					if (dt != null) {
						JSONObject dr = dt.getJSONObject(0);
						final String Placa = dr.getString("Placa");
						PlacaDetectada = Placa;
						final String Marca = dr.getString("Marca");
						MarcaModeloDetectada = Marca;
						final String Cor = dr.getString("Cor");
						String AnoLicenciamento = "";
						try {
							JSONArray dt2 = web
									.ExecuteReaderQuery("Select Convert(Date,prd_veiculo.data_licenciamento,101)AnoLicenciamento "
											+ "from [prd_multas].[dbo].veiculo prd_veiculo "
											+ "WHERE placa_letra = '"
											+ dr.getString("Placa").substring(
													0, 3)
											+ "' and placa_numero = '"
											+ dr.getString("Placa").substring(
													3, 7) + "'");
							if (dt2 != null) {
								JSONObject dr2 = dt2.getJSONObject(0);
								AnoLicenciamento = dr2
										.getString("AnoLicenciamento");
							} else {
								AnoLicenciamento = "";
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							AnoLicenciamento = "";
						}

						VeiculoEncontradoWeb(Placa, Marca, Cor,
								AnoLicenciamento);
					} else {
						mostraMensagem("Veículo não encontrado!");
						AtualizaDadosTela();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mostraMensagem("Veículo não encontrado!");
					AtualizaDadosTela();
				}

				progress.dismiss();

			}
		}).start();
	}

	private void mostraMensagem(final String mensagem) {
		handler.post(new Runnable() {

			@Override
			public void run() {

				AlertDialog.Builder aviso1 = new AlertDialog.Builder(
						VeiculosRestricaoRFID.this);
				aviso1.setIcon(android.R.drawable.ic_dialog_alert);
				aviso1.setTitle("TEC");
				aviso1.setMessage(mensagem);
				aviso1.setPositiveButton("OK", null);
				aviso1.show();

			}
		});
	}

	private void AtualizaDadosTela() {
		handler.post(new Runnable() {

			@Override
			public void run() {

				lblVeiculoEncontrado.setText("Veículo não encontrado!");
				lblVeiculoEncontrado.setVisibility(View.VISIBLE);
				btnNovoAIT.setVisibility(View.INVISIBLE);
				pnlDadosVeiculoRFID.setVisibility(View.INVISIBLE);

			}
		});
	}

	private void VeiculoEncontradoWeb(final String Placa, final String Marca,
			final String Cor, final String AnoLicenciamento) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				PlacaDetectada = Placa;
				lblPlaca.setText("Placa: " + Placa);
				lblMarca.setText("Marca: " + Marca);
				MarcaModeloDetectada = Marca;
				lblModelo.setText("Modelo: ");
				lblCor.setText("Cor: " + Cor);
				lblAnoLicenciamento.setText("Ano Licenciamento: "
						+ AnoLicenciamento);

				lblVeiculoEncontrado.setText("Veículo encontrado!");

				btnNovoAIT.setVisibility(View.VISIBLE);
				lblVeiculoEncontrado.setVisibility(View.VISIBLE);
				pnlDadosVeiculoRFID.setVisibility(View.VISIBLE);

			}
		});
	}
}
