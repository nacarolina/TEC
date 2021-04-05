package br.com.cobrasin;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;

public class ListaUfVeiculo extends ListActivity {

    private long idAit = 0 ;
    List<String> lstUf;
    String selecao ="";
    private String info = Utilitarios.getInfo();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");
        try{
             selecao = (String) getIntent().getSerializableExtra("selUfVeiculo");
             if (selecao !=null && selecao.equals("")==false){
                 selecao= SimpleCrypto.decrypt(info, (selecao));
             }
        }catch (Exception e){

        }
        TipoDAO tipodao = new TipoDAO(getBaseContext());

    lstUf =new ArrayList<>();

    lstUf.add("AC");
        lstUf.add("AL");  lstUf.add("AP"); lstUf.add("AM");  lstUf.add("BA");  lstUf.add("CE");  lstUf.add("DF");  lstUf.add("ES");
        lstUf.add("GO"); lstUf.add("MA"); lstUf.add("MT"); lstUf.add("MS"); lstUf.add("MG");  lstUf.add("PA"); lstUf.add("PB");  lstUf.add("PR");
        lstUf.add("PE"); lstUf.add("PI"); lstUf.add("RJ"); lstUf.add("RN");  lstUf.add("RS"); lstUf.add("RO"); lstUf.add("RR"); lstUf.add("SC");
        lstUf.add("SP"); lstUf.add("SE");  lstUf.add("TO");

        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,lstUf));

        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // percorre a lista para encontrar o que foi selecionado
        Iterator<String> nx = lstUf.iterator();

        try
        {

            int nz = 0 ;
            while ( nx.hasNext())
            {

                if (selecao.contains(lstUf.get(nz).toString()))
                {
                    listView.setItemChecked(nz, true);
                }
                nz++;
            }
        }
        catch( Exception e)
        {

        }

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        // Get the data associated with selected item
        Object item = l.getItemAtPosition(position);

        Ait aitx = new Ait();
        aitx.setId(idAit);
        aitx.setUfVeiculo(lstUf.get(position).toString());

        AitDAO aitdao = new AitDAO(this);
        aitdao.gravaUfVeiculo(aitx);
        aitdao.close();

        finish();
    }
}