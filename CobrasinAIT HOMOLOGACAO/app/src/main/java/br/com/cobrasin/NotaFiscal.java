package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.AitEnquadramento;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NotaFiscal extends Activity {

	private Button btnAdicionarNotaFiscal;
	private ListView lstNF;
	private long idAit = 0;
	private List<br.com.cobrasin.tabela.NotaFiscal> Lista_NF;
	private ArrayAdapter<br.com.cobrasin.tabela.NotaFiscal> adapter;
	private String Id, NumeroNF,PBT_Valor;

	private String arquivofoto;
	byte[] imagemBytes;

	private void CarregaListaNF() {
		NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal.this);
		Lista_NF = NfDAO.GetNotasAit(idAit);
		NfDAO.close();
		adapter = new ArrayAdapter<br.com.cobrasin.tabela.NotaFiscal>(this,
				android.R.layout.simple_list_item_1, Lista_NF);

		lstNF.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notafiscal);

		idAit = (Long) getIntent().getSerializableExtra("idAit");
		//PBT_Valor = (String) getIntent().getSerializableExtra("PBT_Valor");

		btnAdicionarNotaFiscal = (Button) findViewById(R.id.btnAdicionarNotaFiscal);
		lstNF = (ListView) findViewById(R.id.lstNF);
		registerForContextMenu(lstNF);
		CarregaListaNF();
		Button btnVoltar = (Button) findViewById(R.id.btnVoltarNF);
		btnVoltar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NotaFiscal.this,PreencheAitExcesso.class);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("QFV", "Nenhum");
				//i.putExtra("IdCaracterizacao", Id);
				startActivity(i);
				finish();
			}
		});
		btnAdicionarNotaFiscal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NotaFiscal.this,
						NotaFiscal_Cadastro.class);
				i.putExtra("idAit", idAit);
				//i.putExtra("PBT_Valor", PBT_Valor);
				startActivity(i);
				finish();
			}
		});
		lstNF.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				// repete pois nao acha aitenq
				// AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(
				// getBaseContext());
				// List<AitEnquadramento> aitenq = daoenq.getLista(idait);

				// daoenq.close();
				// long xid = aitenq.get(arg2).getId();
				// excluiReg(xid);

			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Opções - NF");
		menu.add(0, v.getId(), 0, "Detalhes");
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		int index = info.position;
		NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal.this);
		Id = Lista_NF.get(index).getId();
		if (NfDAO.ExisteFoto(Id)) {
			menu.add(0, v.getId(), 0, "Visualizar Foto");
		}
		NfDAO.close();
		menu.add(0, v.getId(), 0, "Fotografar");
		menu.add(0, v.getId(), 0, "Excluir");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		ContextMenuInfo menuinfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int index = info.position;

		Id = Lista_NF.get(index).getId();
		NumeroNF = Lista_NF.get(index).getNumeroNota();
		String NumeroNotaFiscal = Lista_NF.get(index).getNumeroNota();

		if (item.getTitle() == "Detalhes") {
			// ------------------------------
			Intent i = new Intent(NotaFiscal.this, NotaFiscal_Cadastro.class);
			i.putExtra("idAit", idAit);
			i.putExtra("Id", Id);
			startActivity(i);
			finish();
			// ------------------------------
		} else if (item.getTitle() == "Fotografar") {
			fotografa(idAit, Id);
		} else if (item.getTitle() == "Excluir") {
			
			NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal.this);
			NfDAO.Deleta(Id);
			CarregaListaNF();
			NfDAO.close();
			Toast.makeText(this, "Nota Fiscal excluída com Sucesso!",
					Toast.LENGTH_SHORT).show();
			
		} else if (item.getTitle() == "Visualizar Foto") {
			Intent i = new Intent(NotaFiscal.this, NotaFiscal_Foto.class);
			try {
				i.putExtra("NumeroNF", NumeroNF);
				i.putExtra("idAit", idAit);
				i.putExtra("Id", Id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			startActivity(i);
			finish();
		}

		else {
			return false;
		}
		return true;
	}

	private int chamafoto = 102;
	private Uri uriImagem = null;

	private void fotografa(long idAit, String Id) {

		// Cria uma intent para capturar uma imagem e retorna o controle
		// para quem o chamou (NAO PRECISA DECLARAR PERMISSAO NO MANIFESTO
		// PARA ACESSAR A CAMERA POIS O FAZEMOS VIA INTENT).
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Cria um arquivo para salvar a imagem.
		uriImagem = ProcessaImagens.getOutputMediaFileUri(
				ProcessaImagens.MEDIA_TYPE_IMAGE, NotaFiscal.this);
		// Passa para intent um objeto URI contendo o caminho e o nome do
		// arquivo onde desejamos salvar a imagem. Pegaremos atraves do
		// parametro data do metodo onActivityResult().
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem);
		// Inicia a intent para captura de imagem e espera pelo resultado.
		startActivityForResult(intent, chamafoto);

		arquivofoto = Environment.getExternalStorageDirectory() + "/imagens/"
				+ idAit + "-" + Id + ".jpg";// System.currentTimeMillis()

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == chamafoto) {
			if (resultCode == RESULT_OK) {

				try {

					// Vou compactar a imagem, leia o javadoc do médoto e verá
					// que ela retorna tanto um bitmap como um array de bytes.
					List<Object> imagemCompactada = ProcessaImagens
							.compactarImagem(uriImagem.getPath());
					Bitmap imagemBitmap = (Bitmap) imagemCompactada.get(0);
					imagemBytes = (byte[]) imagemCompactada.get(1);

					br.com.cobrasin.tabela.NotaFiscal Nf = new br.com.cobrasin.tabela.NotaFiscal();
					NotaFiscalDAO NfDAO = new NotaFiscalDAO(NotaFiscal.this);

					Nf.setId(Id);
					Nf.setImagem(imagemBytes);
					NfDAO.SalvaFoto(Nf);

					NfDAO.close();

					Intent i = new Intent(NotaFiscal.this,
							NotaFiscal_Foto.class);
					try {
						i.putExtra("NumeroNF", NumeroNF);
						i.putExtra("idAit", idAit);
						i.putExtra("Id", Id);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					startActivity(i);
					Toast.makeText(this, "Foto salva com Sucesso!",
							Toast.LENGTH_SHORT).show();
					finish();
					// btnVisualizarFotoNF.setEnabled(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					AlertDialog.Builder aviso = new AlertDialog.Builder(
							NotaFiscal.this);
					aviso.setIcon(android.R.drawable.ic_dialog_alert);
					aviso.setTitle("Foto");
					aviso.setMessage("Erro ao salvar fotografia, tente novamente");
					aviso.setNeutralButton("OK", null);
					aviso.show();
					// btnVisualizarFotoNF.setEnabled(false);
					// Toast.makeText(getBaseContext(),
					// "Foto não foi salva , mantenha o aparelho na mesma posição ao salvar!",
					// Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				
				Intent i = new Intent(NotaFiscal.this,PreencheAitExcesso.class);
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("QFV", "Nenhum");
				//i.putExtra("IdCaracterizacao", Id);
				startActivity(i);
		
			}

		return super.onKeyDown(keyCode, event);
	}
}
