package br.com.cobrasin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Parametro;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConsultaPlaca extends Activity {

	TrataPlaca txtConsultarPlaca;

	private ProgressDialog progress;

	private TextView lblVeiculoEncontrado;
	private TextView lblPlaca;
	private TextView lblModelo;
	private TextView lblMarca;
	private TextView lblCor;
	private TextView lblAnoLicenciamento;

	private Button btnNovoAIT, btnConsultarPlaca;

	private LinearLayout pnlDadosVeiculoCP;

	private String agente = "";

	private String PlacaDetectada = "";
	private String MarcaModeloDetectada = "";

	private Handler handler = new Handler();

	// variaveis de comunicacao webtrans
	List<NameValuePair> nvps;
	String retornoweb;
	private JSONObject json1;
	private JSONArray jsonArray;

	private String info = Utilitarios.getInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consulta_placa);

		agente = (String) getIntent().getSerializableExtra("agente");

		txtConsultarPlaca = (TrataPlaca) findViewById(R.id.txtConsultarPlaca);
		lblVeiculoEncontrado = (TextView) findViewById(R.id.lblVeiculoEncontradoCP);
		lblPlaca = (TextView) findViewById(R.id.lblPlacaCP);
		lblModelo = (TextView) findViewById(R.id.lblModeloCP);
		lblMarca = (TextView) findViewById(R.id.lblMarcaCP);
		lblCor = (TextView) findViewById(R.id.lblCorCP);
		lblAnoLicenciamento = (TextView) findViewById(R.id.lblAnoLicenciamentoCP);
		btnNovoAIT = (Button) findViewById(R.id.btnNovoAitCP);
		btnConsultarPlaca = (Button) findViewById(R.id.btnConsultarPlaca);
		pnlDadosVeiculoCP = (LinearLayout) findViewById(R.id.pnlDadosVeiculoCP);

		lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
		btnNovoAIT.setVisibility(View.INVISIBLE);
		pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

		btnNovoAIT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ConsultaPlaca.this, ListaTipoAit.class);
				i.putExtra("agente", agente);
				i.putExtra("PlacaDetectada", PlacaDetectada);
				i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
				startActivity(i);
				finish();
			}
		});

		btnConsultarPlaca.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConsultaPlaca(txtConsultarPlaca.getText().toString());
			}
		});
	}

	private void ConsultaPlaca(final String Placa) {

		
		try {
			SQLiteDatabase s = SQLiteDatabase.openDatabase(
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/veiculos_rodizio.SDB", null, 0);
		String IdMarca = "";
		String IdCor = "";
		String AnoLicenciamento = "";
		String Marca = "";
		String Cor = "";
			Cursor cus = null;

			// Pesquisa Veiculo
			cus = s.rawQuery("Select * from veiculos where Placa = '" + Placa
					+ "'", null);
			if (cus.getCount() > 0) {
				while (cus.moveToNext()) {
					//Placa = cus.getString(0);
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
					cus = s.rawQuery("select * from cor where Id = " + IdCor,
							null);
					while (cus.moveToNext()) {
						Cor = cus.getString(cus.getColumnIndex("Cor"));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				lblPlaca.setText("Placa: " + Placa);
				PlacaDetectada = Placa;
				lblMarca.setText("Marca: " + Marca);
				lblModelo.setText("Modelo: ");
				lblCor.setText("Cor: " + Cor);
				lblVeiculoEncontrado.setText("Ve�culo encontrado!");

				lblAnoLicenciamento.setText("Ano Licenciamento: "
						+ AnoLicenciamento);
				btnNovoAIT.setVisibility(View.VISIBLE);
				lblVeiculoEncontrado.setVisibility(View.VISIBLE);
				pnlDadosVeiculoCP.setVisibility(View.VISIBLE);
				PlacaDetectada = Placa;
                MarcaModeloDetectada = Marca;
				return;
			} else {
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ConsultaPlaca.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("Ve�culo n�o encontrado! Deseja pesquisar os dados do ve�culo online?");
				aviso.setNeutralButton("N�o", null);
				aviso.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ConsultaPlacaWEB(Placa);
							}
						});

				aviso.show();

				btnNovoAIT.setVisibility(View.INVISIBLE);
				lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
				pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);
			}
			s.close();
		} catch (SQLiteException e) {
			Log.e("Erro=", e.getMessage());

			AlertDialog.Builder aviso = new AlertDialog.Builder(
					ConsultaPlaca.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Banco offline n�o encontrado! Deseja pesquisar os dados do ve�culo online?");
			aviso.setNeutralButton("N�o", null);
			aviso.setPositiveButton("Sim",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							ConsultaPlacaWEB(Placa);
						}
					});


			aviso.show();
			btnNovoAIT.setVisibility(View.INVISIBLE);
			lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
			pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

		}

	}


	public void  ConsultaPlacaWEB(final String Placa) {

		progress = ProgressDialog.show(ConsultaPlaca.this,
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
			
				if (Utilitarios.conectado(getBaseContext())) {
					// cria JSON do parametro

					/*
					 * ParametroDAO pardao = new
					 * ParametroDAO(getBaseContext());
					 * 
					 * Cursor c = pardao.getParametros();
					 * 
					 * c.moveToFirst();
					 * 
					 * // dados para comunica��o com o WebTrans String
					 * usuarioWebTrans =
					 * c.getString(c.getColumnIndex("usuariowebtrans"));
					 * String senhaWebTrans =
					 * c.getString(c.getColumnIndex("senhawebtrans"));
					 * String codMunicipio =
					 * c.getString(c.getColumnIndex("orgaoautuador"
					 * )).subSequence(1, 5).toString(); //265810 OrgA=
					 * c.getString(c.getColumnIndex("orgaoautuador"));
					 * c.close();
					 * 
					 * pardao.close();
					 */
					ParametroDAO pardao = new ParametroDAO(getBaseContext());

					Cursor c = pardao.getParametros();

					c.moveToFirst();

				
					// dados para comunica��o com o WebTrans
					String usuarioWebTrans = "";
					try {
						usuarioWebTrans = SimpleCrypto.decrypt(info, c
								.getString(c.getColumnIndex("usuariowebtrans")));
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String senhaWebTrans = c.getString(c
							.getColumnIndex("senhawebtrans"));
					String codMunicipio = "";
					try {
						codMunicipio = SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("orgaoautuador")))
								.toString();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} // 265810
											// subSequence(1,
											// 5).

					nvps = new ArrayList<NameValuePair>();
					
					// data da solicitacao
					String dataHoraInfracao = Utilitarios.getDataHora(2);

					// 30/03/2012
					dataHoraInfracao = dataHoraInfracao.substring(0, 2)
							+ dataHoraInfracao.substring(3, 5)
							+ dataHoraInfracao.substring(6, 10);

					String horaInfracao = Utilitarios.getDataHora(3);

					dataHoraInfracao += horaInfracao.substring(0, 2)
							+ horaInfracao.substring(3, 5)
							+ "00";

					nvps.add(new BasicNameValuePair("dataSolicitacao",
							dataHoraInfracao));

					// Parametros de autenticacao
					nvps.add(new BasicNameValuePair("cliente", codMunicipio));
					nvps.add(new BasicNameValuePair("placa", Placa));
					nvps.add(new BasicNameValuePair("user", usuarioWebTrans));
					nvps.add(new BasicNameValuePair("password",
							senhaWebTrans)); // "E10ADC3949BA59ABBE56E057F20F883E"

					// transmite para o WebTrans
					boolean leu = carregaDados("veiculo");

					if (leu) {
						try {
							if (jsonArray.length() > 0) {
								// recupera dados do veiculo
								json1 = jsonArray.getJSONObject(0);

								// *************************************************
								// 02.07.2012 - Verifica se o retorno n�o �
								// null
								// *************************************************
								if (!json1.getString("marca_modelo")
										.toUpperCase().contains("NULL")) {

									if (json1.getString("marca_modelo")
											.length() > 30) {
										handler.post(new Runnable() {

											@Override
											public void run() {
										lblPlaca.setText("Placa: " + Placa);
										PlacaDetectada = Placa;
						                
										try {
											MarcaModeloDetectada =json1.getString(
													"marca_modelo")
													.substring(0, 31);
											lblMarca.setText("Marca: " + json1.getString(
													"marca_modelo")
													.substring(0, 31));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										lblModelo.setText("Modelo: ");
										lblCor.setText("Cor: ");
										lblVeiculoEncontrado.setText("Ve�culo encontrado!");

										lblAnoLicenciamento.setText("Ano Licenciamento: ");
										btnNovoAIT.setVisibility(View.VISIBLE);
										lblVeiculoEncontrado.setVisibility(View.VISIBLE);
										pnlDadosVeiculoCP.setVisibility(View.VISIBLE);
											}});

									} else {
										handler.post(new Runnable() {

										@Override
										public void run() {
										lblPlaca.setText("Placa: " + Placa);
										PlacaDetectada = Placa;
										try {
											MarcaModeloDetectada =json1.getString(
													"marca_modelo");
											lblMarca.setText("Marca: " + json1.getString(
													"marca_modelo"));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										lblModelo.setText("Modelo: ");
										lblCor.setText("Cor: ");
										lblVeiculoEncontrado.setText("Ve�culo encontrado!");

										lblAnoLicenciamento.setText("Ano Licenciamento: ");
										btnNovoAIT.setVisibility(View.VISIBLE);
										lblVeiculoEncontrado.setVisibility(View.VISIBLE);
										pnlDadosVeiculoCP.setVisibility(View.VISIBLE);
										}
									});
									}
								}
								else
								{
									mostraMensagem("Ve�culo n�o encontrado!");
									handler.post(new Runnable() {

										@Override
										public void run() {
											btnNovoAIT.setVisibility(View.INVISIBLE);
											lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
											pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

										}
									});
								}
							}
							else
							{
								mostraMensagem("Ve�culo n�o encontrado!");
								handler.post(new Runnable() {

									@Override
									public void run() {
										btnNovoAIT.setVisibility(View.INVISIBLE);
										lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
										pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

									}
								});
							}
						} catch (Exception e) {
							mostraMensagem("Ve�culo n�o encontrado!");
							handler.post(new Runnable() {

								@Override
								public void run() {
									btnNovoAIT.setVisibility(View.INVISIBLE);
									lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
									pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

								}
							});

						}

					}
				}
				else
				{
					mostraMensagem("Falha ao conectar com a internet!");
					handler.post(new Runnable() {

						@Override
						public void run() {
							btnNovoAIT.setVisibility(View.INVISIBLE);
							lblVeiculoEncontrado.setVisibility(View.INVISIBLE);
							pnlDadosVeiculoCP.setVisibility(View.INVISIBLE);

						}
					});
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
						ConsultaPlaca.this);
				aviso1.setIcon(android.R.drawable.ic_dialog_alert);
				aviso1.setTitle("TEC");
				aviso1.setMessage(mensagem);
				aviso1.setPositiveButton("OK", null);
				aviso1.show();

			}
		});
	}

	private boolean carregaDados(String tipotransacao) {
		boolean ret = true;

		String urlBase;

		UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(getBaseContext());
		urlBase = urlswebtrans.geturl(tipotransacao);

		String url = urlswebtrans.geturl("urlcripto").replace(":8080","");
		urlswebtrans.close();

		// debug
		List<NameValuePair> nvps1 = nvps;

		// ***********************************************************************
		// TESTE DE CRIPTOGRAFIA - 10.04.2012
		// ***********************************************************************
		urlBase = urlBase + "?";

		for (int nx = 0; nx < nvps1.size(); nx++) {
			urlBase += nvps1.get(nx).toString() + "&";
		}

		urlBase = urlBase.substring(0, urlBase.length() - 1);

		nvps.add(new BasicNameValuePair("checkSum", MD5Util
				.criptografar(urlBase)));

		try {
			urlBase = SimpleCrypto.encrypt(info, urlBase);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));
		// *****************************************************************************
		// .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
		// *****************************************************************************

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 8000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 8000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// buscar em parametros!!!!!
		// List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {

			ret = false;
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			// HttpResponse response = httpclient.execute(httpget);
			HttpResponse response = httpclient.execute(post);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			retornoweb = EntityUtils.toString(response.getEntity());

			String retz = retornoweb;

			if (statusCode == 200) {

				try {

					// retornoweb = "[" + retornoweb + "]";

					jsonArray = new JSONArray(retornoweb);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ret = false;
					e.printStackTrace();
				}

			} else {
				ret = false;
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = false;
		}

		// limpa
		nvps.clear();

		return ret;
	}

}
