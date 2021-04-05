package br.com.cobrasin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Agente;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ExibeDadosAit extends Activity {

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
	private String mens;
	private String especie;
	private String tipo;
	private String medidaadm;

	private String dtEdit;
	private String hrEdit;

	private String tipoinfrator;

	Ait aitPendente;

	// Mostra String
	private String exibe[] = new String[29];

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
		// AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
		// Cursor c = aitdao.getAit(idAit);
		// aitdao.close();
		// Intent i = null ;

		// i = new Intent(this, EditDataHora.class);
		// try {
		// dtEdit = SimpleCrypto.decrypt(info,
		// c.getString(c.getColumnIndex("dtEdit")));
		// i.putExtra("dtEdit",dtEdit);
		// } catch (Exception e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// try {
		// hrEdit = SimpleCrypto.decrypt(info,
		// c.getString(c.getColumnIndex("hrEdit")));
		// i.putExtra("hrEdit",hrEdit);
		// } catch (Exception e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// i.putExtra("idAit", idAit);
		// startActivity(i);

		// finish();
		// }
		if (mt.getTitle() == "Medidas Administrativas") {
			Intent i = null;
			// Ait aitx = new Ait();
			// aitx.setId(aitPendente.getId());
			AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
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
				MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAit.this);
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
						ExibeDadosAit.this);
				aviso.setIcon(android.R.drawable.ic_dialog_alert);
				aviso.setTitle("TEC");
				aviso.setMessage("A Medida Administrativa n�o pode ser modificada depois de ter transmitido o AIT!");
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
		Button btCancela = (Button) findViewById(R.id.btCancelaAit);
		// pega o Id do AIT
		idAit = (Long) getIntent().getSerializableExtra("idAit");

		AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
		Cursor c = aitdao.getAit(idAit);
		aitdao.close();
		// Obtem , Logradouro ,Especie, Tipo

		try {
			LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAit.this);
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

			EspecieDAO espdao = new EspecieDAO(ExibeDadosAit.this);
			especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("especie"))));
			espdao.close();

			TipoDAO tipdao = new TipoDAO(ExibeDadosAit.this);
			tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("tipo"))));
			tipdao.close();

			MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAit.this);
			medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(info,
					c.getString(c.getColumnIndex("medidaadm"))));
			medidaadmdao.close();

			ParametroDAO pardao = new ParametroDAO(ExibeDadosAit.this);
			Cursor ch = pardao.getParametros();
			ch.moveToFirst();
			serieAit = SimpleCrypto.decrypt(info,
					ch.getString(ch.getColumnIndex("serieait")));
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
			exibe[3] = "PLACA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("placa")));
			exibe[4] = "DATA-HORA LAVRATURA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("data")))
					+ "-"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("hora")));
			exibe[5] = "ENCERROU:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("encerrou")));
			exibe[6] = "MARCA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("marca")));
			exibe[7] = "ESPECIE:" + especie; // c.getString(8);
			exibe[8] = "TIPO:" + tipo;// c.getString(9);
			exibe[9] = "LOGRADOURO:" + desclog;// c.getString(10);
			exibe[10] = "NUMERO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouronum")));
			exibe[11] = "TIPO:" + ctiplog;// c.getString(12);

			try {
				tipoinfrator = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipoinfrator")));
			} catch (Exception e1) {
				tipoinfrator = "";
			}

			// if (tipoinfrator == null) {
			// exibe[12] = "NOME:" + SimpleCrypto.decrypt(info,
			// c.getString(c.getColumnIndex("nome")));
			// exibe[13] = "PGU:" +SimpleCrypto.decrypt(info,
			// c.getString(c.getColumnIndex("pgu")));
			// exibe[14] = "UF:" + SimpleCrypto.decrypt(info,
			// c.getString(c.getColumnIndex("uf")));
			// }

			exibe[12] = "NOME:";
			exibe[13] = "PGU:";
			exibe[14] = "DOCUMENTO DE INDENTIFICA��O:";

			if (tipoinfrator.contains("CNH")) {
				exibe[12] = "NOME:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("nome")));
				exibe[13] = "PGU:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("pgu")));
				exibe[14] = "UF:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("uf")));

			}
			if (tipoinfrator.contains("PID")) {
				exibe[12] = "NOME:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("nome")));
				exibe[13] = "PID:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("pid")));
				exibe[14] = "DOCUMENTO DE INDENTIFICA��O:"
						+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("passaporte")));
			}

			exibe[15] = "OBS:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("observacoes")));
			exibe[16] = "IMPRIMIU:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("impresso")));
			exibe[17] = "SERIEPDA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("seriepda")));
			exibe[18] = "TRANSMITIDO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("transmitido")));

			// grava cancelamento
			try {
				cancelou = SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("cancelou")));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			exibe[19] = "CANCELOU:" + cancelou;

			if (!cancelou.equals("NAO")) {
				btCancela.setVisibility(4);
			}

			exibe[20] = "MOTIVO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("motivo")));
			exibe[21] = "MEDIDA ADM:" + medidaadm;

			AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
					ExibeDadosAit.this);
			Cursor c1 = aitenq.getLista1(idAit);

			enquads = " ";
			while (c1.moveToNext()) {
				// enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

				EnquadramentoDAO dao = new EnquadramentoDAO(this);
				List<Enquadramento> enquadramento = null;
				try {
					enquadramento = dao.getLista(
							SimpleCrypto.decrypt(info,
									c1.getString(c1.getColumnIndex("codigo"))),
							ExibeDadosAit.this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dao.close();

		//		enquads += enquadramento.get(0).toString();// + " / ";

			}

			c1.close();

			exibe[22] = "ENQUADRAMENTOS:";// + enquads;

			exibe[23] = "EQUIPAMENTO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("equipamento")));
			exibe[24] = "MEDICAO REGISTRADA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("medicaoreg")));
			exibe[25] = "MEDICAO CONSIDERADA:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("medicaocon")));
			exibe[26] = "LIMITE REGULAMENTADO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("limitereg")));

		} catch (Exception e) {
			String Erro = e.getMessage();
		}

		// String DtEdit;
		// DtEdit = aitdao.ObtemDataModificada(Long.toString(idAit));
		// exibe[27] = "DATA MODIFICADA:" + DtEdit;

		try {
			exibe[27] = "DATA DO COMETIMENTO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("dtEdit")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exibe[27] = "DATA DO COMETIMENTO:";
		}
		try {
			exibe[28] = "HORA DO COMETIMENTO:"
					+ SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("hrEdit")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exibe[28] = "HORA DO COMETIMENTO:";
		}

		ListView exibeait = (ListView) findViewById(R.id.listExibeAit);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, exibe);
		exibeait.setAdapter(adapter1);

		c.close();

		btCancela.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ait j� foi cancelado ?
				AitDAO aitda = new AitDAO(ExibeDadosAit.this);
				Cursor cu = aitda.getAit(idAit);
				String Cancelar = "";
				try {
					Cancelar = SimpleCrypto.decrypt(info,
							cu.getString(cu.getColumnIndex("cancelou")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (Cancelar.contains("NAO"))
					chama(1);
				else
					Toast.makeText(getBaseContext(), "AIT j� cancelado !",
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

		if (opcao == 1)
			finish();

	}

	private void montaimpressao(long idAit) {

		// String impressora ="00:08:1B:95:6B:AF";

		AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
		Cursor c = aitdao.getAit(idAit);

		// grava data e hora do envio para a impressora
		aitdao.atualizaImpressao(idAit, c);
		aitdao.close();

		ParametroDAO pardao = new ParametroDAO(ExibeDadosAit.this);
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
				impressora = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("impressoraMAC")));
				ativo = SimpleCrypto.decrypt(
						info,
						cpar.getString(cpar
								.getColumnIndex("impressoraPatrimonio")))
						.toUpperCase();
				// Obtem , Logradouro ,Especie, Tipo

				EspecieDAO espdao = new EspecieDAO(ExibeDadosAit.this);
				especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("especie"))));
				espdao.close();

				TipoDAO tipdao = new TipoDAO(ExibeDadosAit.this);
				tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipo"))));
				tipdao.close();

				MedidaAdmDAO medidadao = new MedidaAdmDAO(ExibeDadosAit.this);
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
				LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAit.this);
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
				// verifica se impressora � P25
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
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
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
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
										.getColumnIndex("seriepda")))
						+ String.format("\r\n");
				saida += "" + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");
				saida += "   Local da Infracao " + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");

				saida += this.desclog + String.format("\r\n");
				// saida += this.ctiplog+ String.format("\r\n");

				saida += "" + String.format("\r\n");

				AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
						ExibeDadosAit.this);
				Cursor c1 = aitenq.getLista1(idAit);

				enquads = " ";
				c1.moveToNext();

				// enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

				EnquadramentoDAO dao = new EnquadramentoDAO(ExibeDadosAit.this);
				List<Enquadramento> enquadramento = dao.getLista(
						SimpleCrypto.decrypt(info,
								c1.getString(c1.getColumnIndex("codigo"))),
						ExibeDadosAit.this);
				dao.close();

				enquads += enquadramento.get(0).toString();

				// enquads = Utilitarios.quebraLinha(enquads);

				c1.close();

				saida += "------------------------" + String.format("\r\n");
				saida += "    Enquadramento" + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");
				saida += enquads + String.format("\r\n");

				saida += "" + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");
				saida += "  Identif. do Infrator  " + String.format("\r\n");
				saida += "------------------------" + String.format("\r\n");

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
					saida += "PGU:"
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("pgu")))
							+ " "
							+ SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("uf")))
							+ String.format("\r\n");
				} else {
					if (tipoinfrator.contains("CNH")) {
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

					}
					if (tipoinfrator.contains("PID")) {
						saida += "Nome:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("nome")))
								+ String.format("\r\n");
						saida += "Doc. de Ident.:"
								+ SimpleCrypto.decrypt(info, c.getString(c
										.getColumnIndex("passaporte")))
								+ String.format("\r\n");
						saida += "Pid:"
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("pid")))
								+ " "
								+ SimpleCrypto.decrypt(info,
										c.getString(c.getColumnIndex("uf")))
								+ String.format("\r\n");
					}
				}

				saida += "" + String.format("\r\n");
				saida += "________________________" + String.format("\r\n");
				saida += "      Assinatura" + String.format("\r\n");
				// saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+
				// String.format("\r\n");

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
						c.getString(c.getColumnIndex("observacoes"))).length() > 0) {

					if (SimpleCrypto.decrypt(info,
							cpar.getString(cpar.getColumnIndex("imprimeobs")))
							.contains("1")) {
						saida += String.format("\r\n");
						saida += "------------------------"
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
					saida += "------------------------" + String.format("\r\n");
					saida += "Medida Administrativa:" + String.format("\r\n");
					saida += medidaadm + String.format("\r\n");

				}

				// **************************************************************************************
				// 08.03.2012
				// Preencheu dados equipamento, exemplo decibel�metro ?
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

				impressora = SimpleCrypto.decrypt(info,
						cpar.getString(cpar.getColumnIndex("impressoraMAC")));
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

				LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAit.this);
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

				EspecieDAO espdao = new EspecieDAO(ExibeDadosAit.this);
				especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("especie"))));
				espdao.close();

				TipoDAO tipdao = new TipoDAO(ExibeDadosAit.this);
				tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("tipo"))));
				tipdao.close();

				saida = "";
				// CHR(7) & chr(29) & chr(47) & chr(1) // logo

				// **********************************
				// verifica se impressora � P25
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
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
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
						+ SimpleCrypto
								.decrypt(info, cpar.getString(cpar
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
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	private void chamaImpressao() {

		// progress = ProgressDialog.show(ExibeDadosAit.this, "Aguarde..." ,
		// "Enviando dados para a Impressora!!!",true,true);
		aviso = Toast.makeText(ExibeDadosAit.this,
				"Dados enviados com sucesso!", Toast.LENGTH_LONG);
		avisoerro = Toast.makeText(ExibeDadosAit.this,
				"N�o consegui enviar dados...", Toast.LENGTH_LONG);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		BluetoothDevice mmDevice;

		boolean passou;
		try {
			mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
			passou = true;
			progress = ProgressDialog.show(ExibeDadosAit.this, "Aguarde...",
					"Enviando dados para a Impressora!!!", true, true);
		} catch (Exception e) {
			AlertDialog.Builder aviso = new AlertDialog.Builder(
					ExibeDadosAit.this);
			aviso.setIcon(android.R.drawable.ic_dialog_alert);
			aviso.setTitle("TEC");
			aviso.setMessage("Falha ao imprimir!\nImpressora n�o instalada!");
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
				mmOutStream.write(buffer, 0, buffer.length);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// mmOutStream.write(buffer );

				mmSocket.close();

				progress.dismiss();
				aviso.show();
				Ait ait = new Ait();
				ait.setImpressao(mens);
				ait.setId(idAit);
				AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
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
	 * String.format("\r\n"); saida += "Orgao Autuador:" +
	 * cpar.getString(cpar.getColumnIndex("orgaoautuador")) +
	 * String.format("\r\n");
	 * 
	 * saida += "------------------"+ String.format("\r\n"); saida +=
	 * "Dados da Infracao"+ String.format("\r\n"); saida +=
	 * "------------------"+ String.format("\r\n");
	 * 
	 * saida += "Ait:" + cpar.getString(cpar.getColumnIndex("serieait")) +
	 * c.getString(c.getColumnIndex("ait"))+ String.format("\r\n"); saida +=
	 * "Placa:" + c.getString(c.getColumnIndex("placa"))+ String.format("\r\n");
	 * saida += "Marca:" + c.getString(c.getColumnIndex("marca"))+
	 * String.format("\r\n"); saida += "Especie:" + especie +
	 * String.format("\r\n"); saida += "Tipo:" + tipo + String.format("\r\n");
	 * 
	 * saida += "-----------------"+ String.format("\r\n"); saida +=
	 * "Local da Infracao "+ String.format("\r\n"); saida +=
	 * "-----------------"+ String.format("\r\n");
	 * 
	 * saida +=this.desclog+ String.format("\r\n"); saida += this.ctiplog+
	 * String.format("\r\n");
	 * 
	 * saida += "--------------------------"+ String.format("\r\n"); saida +=
	 * "Data:" + c.getString(c.getColumnIndex("data")) + "-"
	 * +c.getString(c.getColumnIndex("hora"))+ String.format("\r\n"); saida +=
	 * "--------------------------"+ String.format("\r\n");
	 * 
	 * saida += "Enquadramentos:" + enquads + String.format("\r\n");
	 * 
	 * saida += "-------------------------"+ String.format("\r\n"); saida +=
	 * "Identificacao do Infrator" + String.format("\r\n"); saida +=
	 * "-------------------------"+ String.format("\r\n"); saida += "Nome:" +
	 * c.getString(c.getColumnIndex("nome"))+ String.format("\r\n"); saida +=
	 * "PGU:" + c.getString(c.getColumnIndex("pgu"))+ " / " +
	 * c.getString(c.getColumnIndex("uf")) + String.format("\r\n"); saida +=
	 * "Assinatura ______________"+ String.format("\r\n"); //saida += "CPF:" +
	 * c.getString(c.getColumnIndex("uf"))+ String.format("\r\n");
	 * 
	 * saida += "-----------------------"+ String.format("\r\n"); saida +=
	 * "Identificacao do Agente" + String.format("\r\n"); saida +=
	 * "-----------------------"+ String.format("\r\n"); saida += "Matric.(AG):"
	 * + c.getString(c.getColumnIndex("agente")) + String.format("\r\n"); saida
	 * += "Lavrado por _______________"+ String.format("\r\n"); saida +=
	 * "-----------------------"+ String.format("\r\n");
	 * 
	 * if (cpar.getString(cpar.getColumnIndex("imprimeobs")).contains("1")) {
	 * saida +=String.format("\r\n");
	 * 
	 * saida += "Observacoes:"+ String.format("\r\n"); try { saida +=
	 * SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("observacoes"))) +
	 * String.format("\r\n"); } catch (Exception e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); };
	 * 
	 * }
	 * 
	 * saida +=String.format("\r\n"); saida +=String.format("\r\n");
	 * 
	 * saida +="E obrigatoria a presenca do"+ String.format("\r\n"); saida
	 * +="codigo INFRAEST ou RENAINF nas "+ String.format("\r\n"); saida
	 * +="notificacoes sob pena de " ; saida +="invalidade da multa."+
	 * String.format("\r\n");
	 * 
	 * cpar.close();
	 * 
	 * c.close(); progress = ProgressDialog.show(ExibeDadosAit.this,
	 * "Aguarde..." , "Enviado dados para a Impressora!!!",true,true); aviso =
	 * Toast.makeText(ExibeDadosAit.this, "Dados enviados com sucesso!",
	 * Toast.LENGTH_LONG); avisoerro = Toast.makeText(ExibeDadosAit.this,
	 * "N�o consegui enviar dados...", Toast.LENGTH_LONG);
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
	 * String.format("\r\n"); //} tconx = new ThreadConexao(mmDevice,saida);
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