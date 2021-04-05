package br.com.cobrasin;


import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.ModeloDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Modelo;
import br.com.cobrasin.tabela.Tipo;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaModelo extends ListActivity {

    private long idAit = 0 ;
    List<Modelo> modelo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String selecao = (String) getIntent().getSerializableExtra("selmodelo");

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");

        ModeloDAO modeloDAO = new ModeloDAO(getBaseContext());

        modelo = modeloDAO.GetTodosModelosVerificacao() ;

        setListAdapter(new ArrayAdapter<Modelo>(this,android.R.layout.simple_list_item_single_choice,modelo));

        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // recebeu a especie selecionada pelo usuario ?
        if (selecao.length() > 0)
        {
            int posicao = Integer.parseInt(selecao.substring(0, 2));
            listView.setItemChecked(posicao-1, true);
        }

        // percorre a lista para encontrar o que foi selecionado
        Iterator<Modelo> nx = modelo.iterator();

        try
        {

            int nz = 0 ;
            while ( nx.hasNext())
            {

                if (selecao.contains(modelo.get(nz).getModelo()))
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
        aitx.setTipo(modelo.get(position).getModelo());

        AitDAO aitdao = new AitDAO(this);
        aitdao.gravaTipo(aitx);
        aitdao.close();

        finish();
    }
}
