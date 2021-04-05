package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.NotaFiscalDAO;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaNf_ExibeAit_Excesso extends Activity{

	private Button btnVoltarExibeAit_excesso;
	private ListView lstNF_ait;
	private long idAit = 0;
	private List<br.com.cobrasin.tabela.NotaFiscal> Lista_NF;
	private ArrayAdapter<br.com.cobrasin.tabela.NotaFiscal> adapter;
	private String Id, NumeroNF;


	private void CarregaListaNF() {
		NotaFiscalDAO NfDAO = new NotaFiscalDAO(ListaNf_ExibeAit_Excesso.this);
		Lista_NF = NfDAO.GetNotasAit(idAit);
		NfDAO.close();
		adapter = new ArrayAdapter<br.com.cobrasin.tabela.NotaFiscal>(this,
				android.R.layout.simple_list_item_1, Lista_NF);

		lstNF_ait.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exibenf_excesso);

		idAit = (Long) getIntent().getSerializableExtra("idAit");

		btnVoltarExibeAit_excesso = (Button) findViewById(R.id.btnVoltarExibeAit_excesso);
		lstNF_ait = (ListView) findViewById(R.id.lstNF_ait);
		registerForContextMenu(lstNF_ait);
		CarregaListaNF();
		btnVoltarExibeAit_excesso.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lstNF_ait.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
		NotaFiscalDAO NfDAO = new NotaFiscalDAO(ListaNf_ExibeAit_Excesso.this);
		Id = Lista_NF.get(index).getId();
		if (NfDAO.ExisteFoto(Id)) {
			menu.add(0, v.getId(), 0, "Visualizar Foto");
		}
		NfDAO.close();

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
			Intent i = new Intent(ListaNf_ExibeAit_Excesso.this, NotaFiscal_Cadastro.class);
			i.putExtra("idAit", idAit);
			i.putExtra("Id", Id);
			i.putExtra("TipoChamada", "Visualizar");
			startActivity(i);
			finish();
			// ------------------------------
		}
		 else if (item.getTitle() == "Visualizar Foto") {
			Intent i = new Intent(ListaNf_ExibeAit_Excesso.this, NotaFiscal_Foto.class);
			try {
				i.putExtra("NumeroNF", NumeroNF);
				i.putExtra("idAit", idAit);
				i.putExtra("Id", Id);
				i.putExtra("TipoChamada", "Visualizar");
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
}
