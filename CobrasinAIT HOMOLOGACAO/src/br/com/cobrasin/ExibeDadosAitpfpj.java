package br.com.cobrasin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
//import br.com.cobrasin.dao.EnquadramentoDAOpf;
//import br.com.cobrasin.dao.EnquadramentoDAOpj;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ExibeDadosAitpfpj extends Activity {

	/*
	 * 
	 * 
	 * 04.12.2012 Obtem o Ativo da Impressora e durante a montagem da impressao
	 * verifica: P25 -> impressao especifica para imp.bamboo
	 */

	private String info = Utilitarios.getInfo();

	private ProgressDialog progress;

	private Toast aviso, avisoerro;

	private String cancelou;

	private String desclog;
	private String ctiplog;
	private String enquads;
	private String tipo;
	private String medidaadm;
	Ait aitPendente;

	// Mostra String
	private String exibe[] = new String[20];

	private static final String TAG = "CobrasinAitBt";

	private ThreadConexao tconx;

	private long idAit;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static final int INVISIBLE = 4;

	private String saida, impressora, ativo;

	// private ThreadConexao tconx;

	@Override
	public boolean onCreateOptionsMenu(Menu me) {
		// me.add("Editar Data e Hora");
		me.add("Medidas Administrativas");
		me.add("Retorna");
		// me.add("Status do AIT");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mt) {
		// if(mt.getTitle()=="Editar Data e Hora")
		// {
		// AitDAO aitdao = new AitDAO(ExibeDadosAitpfpj.this);
		// Cursor c = aitdao.getAit(idAit);
		// aitdao.close();
		// Intent i = null ;

		// i = new Intent(this, EditDataHora.class);
		// try {
		// String dtEdit = SimpleCrypto.decrypt(info,
		// c.getString(c.getColumnIndex("dtEdit")));
		// i.putExtra("dtEdit",dtEdit);
		// } catch (Exception e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// try {
		// String hrEdit = SimpleCrypto.decrypt(info,
		// c.getString(c.getColumnIndex("hrEdit")));
		// i.putExtra("hrEdit",hrEdit);
		// } catch (Exception e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// i.putExtra("idAit", idAit);
		// startActivity(i);
		//
		// finish();
		// }
		if (mt.getTitle() == "Medidas Administrativas") {
			Intent i = null;
			// Ait aitx = new Ait();
			// aitx.setId(aitPendente.getId());
			AitDAO aitdao = new AitDAO(ExibeDadosAitpfpj.this);
			Cursor c = aitdao.getAit(idAit);
			aitdao.close();
			String transmitiu = "";
			try {
				transmitiu = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("transmitido")));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (transmitiu.contains("NAO")) {
				String id;

				i = new Intent(this, ListaMedidaAdm.class);
				MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(
						ExibeDadosAitpfpj.this);
				try {
					medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(
							info, c.getString(c.getColumnIndex("medidaadm"))));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				medidaadmdao.close();

				id = medidaadmdao.ObtemId(medidaadm);
				i.putExtra("selmedidaadm", id);
				i.putExtra("idAit", idAit);
				startActivity(i);

				setResult(RESULT_OK);
				finish();
			} else {
				AlertDialog.Builder aviso = new AlertDialog.Builder(
						ExibeDadosAitpfpj.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("A Medida Administrativa não pode ser modificada depois de ter transmitido o AIT!");
				aviso.setNeutralButton("OK", null);
				aviso.show();

			}
		}
		if (mt.getTitle() == "Retorna") {

			setResult(RESULT_OK);
			finish();
		}
		return super.onOptionsItemSelected(mt);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String serieAit = "";

		setContentView(R.layout.exibeait);

		// pega o Id do AIT
		idAit = (Long) getIntent().getSerializableExtra("idAit");
		Button btCancela = (Button) findViewById(R.id.btCancelaAit);

		AitDAO aitdao = new AitDAO(ExibeDadosAitpfpj.this);
		Cursor c = aitdao.getAit(idAit);
		aitdao.close();

		// Obtem , Logradouro

		try {
			LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitpfpj.this);
			if (SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("logradouro2"))).contains(
					"NAO")) {
				desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradouro"))));
			}
			else {
				desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradouro")))) + " X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("logradouro2"))));
			}

			// desclog += " " +
			// SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradouronum")));

			logdao.close();

			MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAitpfpj.this);
			medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("medidaadm"))));
			medidaadmdao.close();

			ParametroDAO pardao = new ParametroDAO(ExibeDadosAitpfpj.this);
			Cursor ch = pardao.getParametros();
			ch.moveToFirst();
			serieAit = ch.getString(c.getColumnIndex("serieait"));
			ch.close();
			pardao.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// pega tipo do logradouro
		ctiplog = "NAO DEFINIDO";

		try {
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

			// Mostra String
			// String exibe [] = new String[24];

			exibe[0] = "AIT:"
					+ serieAit
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("ait")));
			exibe[1] = "AGENTE:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("agente")));
			exibe[2] = "FLAG:" + c.getString(c.getColumnIndex("flag"));

			exibe[3] = "NOME:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("nome")));

			if (SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("tipoait"))).equals("2"))
				exibe[4] = "CPF:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("cpf")));
			else
				exibe[4] = "CNPJ:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("cpf")));

			exibe[5] = "DATA-HORA LAVRATURA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("data")))
					+ "-"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("hora")));
			exibe[6] = "ENCERROU:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("encerrou")));
			exibe[7] = "LOGRADOURO:" + desclog;// c.getString(10);
			exibe[8] = "NUMERO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouronum")));
			exibe[9] = "TIPO LOGRADOURO:" + ctiplog;// c.getString(12);

			exibe[10] = "OBS:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("observacoes")));
			exibe[11] = "IMPRIMIU:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("impresso")));
			exibe[12] = "SERIEPDA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("seriepda")));
			exibe[13] = "TRANSMITIDO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("transmitido")));
			// exibe[14] = "CANCELOU:" + SimpleCrypto.decrypt(info,
			// c.getString(c.getColumnIndex("cancelou")));

			// grava cancelamento
			try {
				cancelou = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("cancelou")));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			exibe[14] = "CANCELOU:" + cancelou;

			if (!cancelou.equals("NAO")) {
				btCancela.setVisibility(4);
			}
			exibe[15] = "MOTIVO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("motivo")));
			exibe[16] = "MEDIDA ADM:" + medidaadm;

		} catch (Exception e) {

		}

		AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
				ExibeDadosAitpfpj.this);
		Cursor c1 = aitenq.getLista1(idAit);

		enquads = " ";
		while (c1.moveToNext()) {
			// enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

			EnquadramentoDAO daopf = null;
			EnquadramentoDAO daopj = null;

			List<Enquadramento> enquadramento = null;
			try {

				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipoait"))).equals("2")) {
					daopf = new EnquadramentoDAO(this);
				} else {
					daopj = new EnquadramentoDAO(this);
				}

				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipoait"))).equals("2")) {
					enquadramento = daopf.getLista(
							SimpleCrypto.decrypt(info,
									c1.getString(c1.getColumnIndex("codigo"))),
							ExibeDadosAitpfpj.this);
				} else {
					enquadramento = daopj.getLista(
							SimpleCrypto.decrypt(info,
									c1.getString(c1.getColumnIndex("codigo"))),
							ExibeDadosAitpfpj.this);
				}

				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipoait"))).equals("2")) {
					daopf.close();
				} else {
					daopj.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			enquads += enquadramento.get(0).toString();// + " / ";

		}

		c1.close();

		exibe[17] = "ENQUADRAMENTOS:" + enquads;

		try {
			exibe[18] = "DATA DO COMETIMENTO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("dtEdit")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exibe[18] = "";
		}
		try {
			exibe[19] = "HORA DO COMETIMENTO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("hrEdit")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exibe[19] = "";
		}

		ListView exibeait = (ListView) findViewById(R.id.listExibeAit);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, exibe);
		exibeait.setAdapter(adapter1);

		c.close();

		// Button btCancela = (Button) findViewById(R.id.btCancelaAit);

		btCancela.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ait já foi cancelado ?

				if (cancelou.contains("NAO"))
					chama(1);
				else
					Toast.makeText(getBaseContext(), "AIT já cancelado !",
							Toast.LENGTH_LONG).show();

			}

		});

		Button btImprime = (Button) findViewById(R.id.btImprime);

		btImprime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				montaimpressao(idAit);
				chamaImpressao();

			}

		});

		/*
		 * // FOI tirada alguma foto ? FotoDAO fotodao = new
		 * FotoDAO(getBaseContext()); Cursor cx = fotodao.getImagens(idAit);
		 * 
		 * cx.moveToFirst();
		 * 
		 * if (cx.getCount() > 0) {
		 * 
		 * byte[] data = cx.getBlob(cx.getColumnIndex("imagem"));
		 * 
		 * Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length); bm =
		 * Bitmap.createScaledBitmap(bm, 100,100,true); ImageButton buttonx = (
		 * ImageButton) findViewById(R.id.fotoAIT); buttonx.setImageBitmap(bm);
		 * 
		 * 
		 * 
		 * }
		 * 
		 * cx.close(); fotodao.close();
		 */

		Button btMostraFotos = (Button) findViewById(R.id.btFotos);

		btMostraFotos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FotoDAO fotodao = new FotoDAO(getBaseContext());

				if (fotodao.getQtde(idAit) > 0) {
					fotodao.close();
					chama(2);
				} else {
					Toast.makeText(getBaseContext(), "Sem fotos para exibir",
							Toast.LENGTH_SHORT).show();
					fotodao.close();
				}

			}
		});
	}

	private void chama(int opcao) {
		Intent i = null;
		switch (opcao) {

		case 1:
			i = new Intent(this, CancelaAit.class);
			break;
		case 2:
			i = new Intent(this, MostraFotos.class);
			break;
		}

		i.putExtra("idAit", idAit);

		startActivity(i);

	}

	private void montaimpressao(long idAit) {

		ParametroDAO pardao = new ParametroDAO(ExibeDadosAitpfpj.this);
		// String impressora ="00:01:90:E7:E6:CE";

		AitDAO aitdao = new AitDAO(ExibeDadosAitpfpj.this);
		Cursor c = aitdao.getAit(idAit);

		// grava data e hora do envio para a impressora
		aitdao.atualizaImpressao(idAit, c);
		aitdao.close();

		Cursor cpar = pardao.getParametros();

		pardao.close();
		try {
			cancelou = SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("cancelou")));
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (cancelou.contains("NAO")) {
			try {

				try {
					impressora = SimpleCrypto.decrypt(info, cpar.getString(cpar
							.getColumnIndex("impressoraMAC")));
					ativo = SimpleCrypto.decrypt(
							info,
							cpar.getString(cpar
									.getColumnIndex("impressoraPatrimonio")))
							.toUpperCase();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {

					// Obtem , Logradouro ,Especie, Tipo

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

					LogradouroDAO logdao = new LogradouroDAO(
							ExibeDadosAitpfpj.this);
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
					logdao.close();
					saida = "";

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
					saida += "   Dados da Infracao   " + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += "Ait:"
							+ SimpleCrypto.decrypt(info, cpar.getString(cpar
									.getColumnIndex("serieait")))
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("ait")))
							+ String.format("\r\n");
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

					saida += "------------------------" + String.format("\r\n");
					saida += "   Local da Infracao   " + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += this.desclog + String.format("\r\n");
					saida += this.ctiplog + String.format("\r\n");

					// AitEnquadramentoDAO aitenq = new
					// AitEnquadramentoDAO(ExibeDadosAitpfpj.this);
					// Cursor c1 = aitenq.getLista1(idAit);

					// enquads = " ";
					// c1.moveToNext();

					// enquads += c1.getString(c1.getColumnIndex("codigo")) +
					// " ";

					// EnquadramentoDAO dao = new
					// EnquadramentoDAO(ExibeDadosAitpfpj.this);
					// List<Enquadramento> enquadramento =
					// dao.getLista(SimpleCrypto.decrypt(info,c1.getString(c1.getColumnIndex("codigo"))),ExibeDadosAitpfpj.this);
					// dao.close();

					// enquads += enquadramento.get(0).toString() + " / ";

					// c1.close();
					saida += "------------------------" + String.format("\r\n");
					saida += "     Enquadramento" + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");
					saida += enquads + String.format("\r\n");
					
					saida += "------------------------" + String.format("\r\n");
					saida += "  Identf. do Infrator   " + String.format("\r\n");
					saida += "------------------------" + String.format("\r\n");

					saida += "Nome:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("nome")))
							+ String.format("\r\n");
					


					if (SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("tipoait"))).equals(
							"2"))
						saida += "CPF:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("cpf")))
								+ String.format("\r\n");
					else
						saida += "CNPJ:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("cpf")))
								+ String.format("\r\n");

					saida += "" + String.format("\r\n");
					saida += "________________________" + String.format("\r\n");
					saida += "      Assinatura" + String.format("\r\n");

					saida += "-----------------------" + String.format("\r\n");
					saida += "Identificacao do Agente" + String.format("\r\n");
					saida += "-----------------------" + String.format("\r\n");
					saida += "Matric.(AG):"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("agente")))
							+ String.format("\r\n");

					saida += "" + String.format("\r\n");
					saida += "________________________" + String.format("\r\n");
					saida += "     Lavrado por        " + String.format("\r\n");

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
							saida += "     Observacoes:"
									+ String.format("\r\n");
							saida += SimpleCrypto.decrypt(info, c.getString(c
									.getColumnIndex("observacoes")))
									+ String.format("\r\n");

						}
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

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {

			try {
				impressora = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("impressoraMAC")));
				ativo = SimpleCrypto.decrypt(
						info,
						cpar.getString(cpar
								.getColumnIndex("impressoraPatrimonio")))
						.toUpperCase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				// Obtem , Logradouro ,Especie, Tipo
				LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitpfpj.this);
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
				logdao.close();

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
				saida += "   Dados da Infracao   " + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");

				saida += "Ait:"
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
										.getColumnIndex("serieait")))
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("ait")))
						+ String.format("\r\n");
				saida += "Data:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("data")))
						+ "-"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("hora")))
						+ String.format("\r\n");
				saida += "Equipamento:"
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
										.getColumnIndex("seriepda")))
						+ String.format("\r\n");
				saida += "Nome:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("nome")))
						+ String.format("\r\n");

				if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipoait"))).equals("2"))
					saida += "CPF:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("cpf")))
							+ String.format("\r\n");
				else
					saida += "CNPJ:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("cpf")))
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

				cpar.close();

				c.close();

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	private void chamaImpressao() {

		// progress = ProgressDialog.show(ExibeDadosAitpfpj.this, "Aguarde..." ,
		// "Enviado dados para a Impressora!!!",true,true);
		aviso = Toast.makeText(ExibeDadosAitpfpj.this,
				"Dados enviados com sucesso!", Toast.LENGTH_LONG);
		avisoerro = Toast.makeText(ExibeDadosAitpfpj.this,
				"Não consegui enviar dados...", Toast.LENGTH_LONG);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		BluetoothDevice mmDevice;

		boolean passou;
		try {
			mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
			passou = true;
			progress = ProgressDialog.show(ExibeDadosAitpfpj.this,
					"Aguarde...", "Enviando dados para a Impressora!!!", true,
					true);
		} catch (Exception e) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					ExibeDadosAitpfpj.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Falha ao imprimir!\nImpressora não instalada!");
			aviso.setNeutralButton("OK", null);
			aviso.show();
			passou = false;
			return;
		}
		if (passou == true) {
			tconx = new ThreadConexao(mmDevice, saida);
			tconx.start();
		} else {

		}

	}

	private class ThreadConexao extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private OutputStream mmOutStream = null;
		private String mens;

		public ThreadConexao(BluetoothDevice device, String texto) {
			mens = texto;
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

				byte[] buffer = mens.getBytes();

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// mmOutStream.write(buffer );
				mmOutStream.write(buffer, 0, buffer.length);

				mmSocket.close();

				progress.dismiss();
				aviso.show();
				Ait ait = new Ait();
				ait.setImpressao(mens);
				ait.setId(idAit);
				AitDAO aitdao = new AitDAO(ExibeDadosAitpfpj.this);
				aitdao.gravaImpressao(ait);
				aitdao.close();

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
	}

	/*
	 * private class ThreadConexao extends Thread {
	 * 
	 * private final BluetoothSocket mmSocket; private final BluetoothDevice
	 * mmDevice; private OutputStream mmOutStream = null; private String mens ;
	 * 
	 * public ThreadConexao(BluetoothDevice device,String texto ) { mens =
	 * texto; mmDevice = device; BluetoothSocket tmp = null;
	 * 
	 * // Get a BluetoothSocket for a connection with the // given
	 * BluetoothDevice try { tmp =
	 * device.createRfcommSocketToServiceRecord(MY_UUID);
	 * 
	 * } catch (IOException e) {
	 * 
	 * progress.dismiss(); avisoerro.show(); Log.e(TAG, "create() failed", e); }
	 * mmSocket = tmp; }
	 * 
	 * public void run() { Log.i(TAG, "BEGIN mConnectThread");
	 * 
	 * 
	 * // Make a connection to the BluetoothSocket try { // This is a blocking
	 * call and will only return on a // successful connection or an exception
	 * mmSocket.connect();
	 * 
	 * mmOutStream = mmSocket.getOutputStream();
	 * 
	 * byte[] buffer = mens.getBytes();
	 * 
	 * mmOutStream.write(buffer );
	 * 
	 * mmSocket.close();
	 * 
	 * progress.dismiss(); aviso.show();
	 * 
	 * 
	 * //try { // sleep(2000); //} catch (InterruptedException e) { // // TODO
	 * Auto-generated catch block // e.printStackTrace(); //}
	 * 
	 * 
	 * } catch (IOException e) {
	 * 
	 * progress.dismiss(); avisoerro.show(); // Close the socket try {
	 * mmSocket.close(); } catch (IOException e2) { Log.e(TAG,
	 * "unable to close() socket during connection failure", e2); } // Start the
	 * service over to restart listening mode
	 * 
	 * return; }
	 * 
	 * 
	 * }
	 * 
	 * public void cancel() { try { mmSocket.close(); } catch (IOException e) {
	 * Log.e(TAG, "close() of connect socket failed", e); } } }
	 */

	/*
	 * private void imprime() {
	 * 
	 * ParametroDAO pardao = new ParametroDAO(ExibeDadosAit.this); //String
	 * impressora ="00:01:90:E7:E6:CE";
	 * 
	 * AitDAO aitdao = new AitDAO(ExibeDadosAit.this); Cursor c =
	 * aitdao.getAit(idAit); aitdao.close();
	 * 
	 * Cursor cpar = pardao.getParametros(); pardao.close();
	 * 
	 * String impressora = cpar.getString(cpar.getColumnIndex("impressora"));
	 * 
	 * String saida = "" ;
	 * 
	 * saida += cpar.getString(cpar.getColumnIndex("prefeitura")) +
	 * String.format("\n\r"); saida += "Orgao Autuador:" +
	 * cpar.getString(cpar.getColumnIndex("orgaoautuador")) +
	 * String.format("\n\r");
	 * 
	 * saida += "------------------"+ String.format("\n\r"); saida +=
	 * "Dados da Infracao"+ String.format("\n\r"); saida +=
	 * "------------------"+ String.format("\n\r");
	 * 
	 * saida += "Ait:" + cpar.getString(cpar.getColumnIndex("serieait")) +
	 * c.getString(c.getColumnIndex("ait"))+ String.format("\n\r"); saida +=
	 * "Placa:" + c.getString(c.getColumnIndex("placa"))+ String.format("\n\r");
	 * saida += "Marca:" + c.getString(c.getColumnIndex("marca"))+
	 * String.format("\n\r"); saida += "Especie:" + especie +
	 * String.format("\n\r"); saida += "Tipo:" + tipo + String.format("\n\r");
	 * 
	 * saida += "-----------------"+ String.format("\n\r"); saida +=
	 * "Local da Infracao "+ String.format("\n\r"); saida +=
	 * "-----------------"+ String.format("\n\r");
	 * 
	 * saida +=this.desclog+ String.format("\n\r"); saida += this.ctiplog+
	 * String.format("\n\r");
	 * 
	 * saida += "--------------------------"+ String.format("\n\r"); saida +=
	 * "Data:" + c.getString(c.getColumnIndex("data")) + "-"
	 * +c.getString(c.getColumnIndex("hora"))+ String.format("\n\r"); saida +=
	 * "--------------------------"+ String.format("\n\r");
	 * 
	 * saida += "Enquadramentos:" + enquads + String.format("\n\r");
	 * 
	 * saida += "-------------------------"+ String.format("\n\r"); saida +=
	 * "Identificacao do Infrator" + String.format("\n\r"); saida +=
	 * "-------------------------"+ String.format("\n\r"); saida += "Nome:" +
	 * c.getString(c.getColumnIndex("nome"))+ String.format("\n\r"); saida +=
	 * "PGU:" + c.getString(c.getColumnIndex("pgu"))+ " / " +
	 * c.getString(c.getColumnIndex("uf")) + String.format("\n\r"); saida +=
	 * "Assinatura ______________"+ String.format("\n\r"); //saida += "CPF:" +
	 * c.getString(c.getColumnIndex("uf"))+ String.format("\n\r");
	 * 
	 * saida += "-----------------------"+ String.format("\n\r"); saida +=
	 * "Identificacao do Agente" + String.format("\n\r"); saida +=
	 * "-----------------------"+ String.format("\n\r"); saida += "Matric.(AG):"
	 * + c.getString(c.getColumnIndex("agente")) + String.format("\n\r"); saida
	 * += "Lavrado por _______________"+ String.format("\n\r"); saida +=
	 * "-----------------------"+ String.format("\n\r");
	 * 
	 * if (cpar.getString(cpar.getColumnIndex("imprimeobs")).contains("1")) {
	 * saida +=String.format("\n\r");
	 * 
	 * saida += "Observacoes:"+ String.format("\n\r"); try { saida +=
	 * SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("observacoes"))) +
	 * String.format("\n\r"); } catch (Exception e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); };
	 * 
	 * }
	 * 
	 * saida +=String.format("\n\r"); saida +=String.format("\n\r");
	 * 
	 * saida +="E obrigatoria a presenca do"+ String.format("\n\r"); saida
	 * +="codigo INFRAEST ou RENAINF nas "+ String.format("\n\r"); saida
	 * +="notificacoes sob pena de " ; saida +="invalidade da multa."+
	 * String.format("\n\r");
	 * 
	 * cpar.close();
	 * 
	 * c.close(); progress = ProgressDialog.show(ExibeDadosAit.this,
	 * "Aguarde..." , "Enviado dados para a Impressora!!!",true,true); aviso =
	 * Toast.makeText(ExibeDadosAit.this, "Dados enviados com sucesso!",
	 * Toast.LENGTH_LONG); avisoerro = Toast.makeText(ExibeDadosAit.this,
	 * "Não consegui enviar dados...", Toast.LENGTH_LONG);
	 * 
	 * 
	 * BluetoothAdapter mBluetoothAdapter =
	 * BluetoothAdapter.getDefaultAdapter();
	 * 
	 * BluetoothDevice mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
	 * 
	 * BluetoothSocket tmp = null;
	 * 
	 * //String saida = "";
	 * 
	 * //for ( int nx = 0 ; nx < 22 ; nx ++) //{ // saida += exibe[nx] +
	 * String.format("\n\r"); //} tconx = new ThreadConexao(mmDevice,saida);
	 * tconx.start();
	 * 
	 * //mmOutStream = mmSocket.getOutputStream();
	 * 
	 * //String texto = String.format("%s\n\r","Teste");
	 * 
	 * //byte[] buffer = texto.getBytes();
	 * 
	 * //mmOutStream.write(buffer );
	 * 
	 * //mmSocket.close();
	 * 
	 * }
	 */

}