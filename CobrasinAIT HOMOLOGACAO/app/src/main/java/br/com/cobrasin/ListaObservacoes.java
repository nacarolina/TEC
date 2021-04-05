package br.com.cobrasin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.ArqObservacaoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.ArqObservacao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListaObservacoes extends Activity {

    private long idAit = 0;
    private EditText edObservacoes;
    private String obsSelecionada;

    List<ArqObservacao> larqobs;

    private int idarquivado;

    ArrayAdapter<ArqObservacao> adapter1;
    ListView listaArqObs;

    private void carregaLista() {
        // mostra o arquivo de observacoes
        ArqObservacaoDAO arqobsdao = new ArqObservacaoDAO(getBaseContext());
        larqobs = arqobsdao.getLista();
        arqobsdao.close();

        listaArqObs = (ListView) findViewById(R.id.listaObservacoes);
        adapter1 = new ArrayAdapter<ArqObservacao>(this,
                android.R.layout.simple_list_item_multiple_choice, larqobs);

        adapter1 = new ArrayAdapter<ArqObservacao>(this,
                android.R.layout.simple_list_item_multiple_choice, larqobs) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                ViewGroup.LayoutParams layoutparams = view.getLayoutParams();

                if (((String) ((CheckedTextView) view).getText()).length() > 81) {
                    //Define your height here.
                    layoutparams.height = 380;
                }
                else{
                    layoutparams.height=132;
                }
                view.setLayoutParams(layoutparams);

                return view;
            }
        };

        listaArqObs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listaArqObs.setAdapter(adapter1);
        listaArqObs.setMinimumHeight(70);
        listaArqObs.setItemsCanFocus(false);
        // listaArqObs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    private boolean verificaLista() {
        boolean retorno = false;
        Iterator<ArqObservacao> nx = larqobs.iterator();

        try {

            int nz = 0;
            while (nx.hasNext()) {
                String info = edObservacoes.getText().toString();
                if (info.equals(larqobs.get(nz).getDescricao())) {
                    retorno = true;
                }
                nz++;
            }
        } catch (Exception e) {

        }

        return retorno;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // limite tamanho da observacao
        Utilitarios.GravaPreferencias("200", getBaseContext());

        setContentView(R.layout.observacoes);

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");
        final Button btnSel = (Button) findViewById(R.id.btnSelObs);
        final Button btnApagar = (Button) findViewById(R.id.btnApagarObs);
        btnSel.setVisibility(View.INVISIBLE);
        btnApagar.setVisibility(View.INVISIBLE);
        // carrega observacoes arquivadas...
        carregaLista();
        final ArrayList<ArqObservacao> selectedItems = new ArrayList<ArqObservacao>();
        btnSel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                edObservacoes.setText(larqobs.get(idarquivado).getDescricao());

                // grava o selecionado
                Ait aitx = new Ait();
                aitx.setId(idAit);

                // ********************************************* //
                // 27.06.2012 - alteração : remover acentos
                // *********************************************
                aitx.setObservacoes(Utilitarios.removeAcentos(edObservacoes
                        .getText().toString()));

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaObservacoes(aitx);
                aitdao.close();
                carregaLista();
                finish();
            }
        });
        btnApagar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ArqObservacao[] outputStrArr = new ArqObservacao[selectedItems
                        .size()];

                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                    ArqObservacao arqobs = new ArqObservacao();
                    // int idx = (int) larqobs.get(idarquivado).getId();

                    ArqObservacaoDAO arqobsdao = new ArqObservacaoDAO(
                            getBaseContext());
                    arqobsdao.deletereg(outputStrArr[i].getId());
                    arqobsdao.close();

                }
                carregaLista();

            }
        });
        listaArqObs
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        // TODO Auto-generated method stub

                        idarquivado = arg2;
                        selectedItems.clear();
                        SparseBooleanArray checked = listaArqObs
                                .getCheckedItemPositions();

                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i))
                                selectedItems.add(adapter1.getItem(position));
                        }

                        /*
                         * ArqObservacao[] outputStrArr = new
                         * ArqObservacao[selectedItems .size()];
                         *
                         * for (int i = 0; i < selectedItems.size(); i++) {
                         * outputStrArr[i] = selectedItems.get(i); }
                         */
                        if (selectedItems.size() > 1) {
                            btnSel.setVisibility(View.INVISIBLE);
                            btnApagar.setVisibility(View.VISIBLE);
                        }
                        if (selectedItems.size() == 1) {
                            btnSel.setVisibility(View.VISIBLE);
                            btnApagar.setVisibility(View.VISIBLE);
                        }
                        if (selectedItems.size() == 0) {
                            btnSel.setVisibility(View.INVISIBLE);
                            btnApagar.setVisibility(View.INVISIBLE);
                        }

                        // pergunta se quer pegar do arquivo
                        /*
                         * AlertDialog.Builder builder = new
                         * AlertDialog.Builder(ListaObservacoes.this);
                         * builder.setMessage("Seleciona esta observação ?");
                         * builder.setNegativeButton("Não", new
                         * DialogInterface.OnClickListener() {
                         *
                         * @Override public void onClick(DialogInterface dialog,
                         * int which) { // TODO Auto-generated method stub
                         *
                         *
                         * AlertDialog.Builder builder1 = new
                         * AlertDialog.Builder(ListaObservacoes.this);
                         * builder1.setMessage
                         * ("Exclui este Registro Arquivado de observação ?");
                         * builder1.setNegativeButton("Não",null);
                         * builder1.setPositiveButton("Sim", new
                         * DialogInterface.OnClickListener() {
                         *
                         * @Override public void onClick(DialogInterface dialog,
                         * int which) { // TODO Auto-generated method stub
                         *
                         * ArqObservacao arqobs = new ArqObservacao(); int idx =
                         * (int) larqobs.get(idarquivado).getId();
                         *
                         * ArqObservacaoDAO arqobsdao = new
                         * ArqObservacaoDAO(getBaseContext());
                         * arqobsdao.deletereg(idx); arqobsdao.close();
                         *
                         * // carrega observacoes arquivadas... carregaLista();
                         * } });
                         *
                         * builder1.show();
                         *
                         * }
                         *
                         * }); builder.setPositiveButton("Sim", new
                         * DialogInterface.OnClickListener() {
                         *
                         * public void onClick(DialogInterface dialog, int
                         * which) { // TODO Auto-generated method stub
                         *
                         * edObservacoes.setText(larqobs.get(idarquivado).
                         * getDescricao());
                         *
                         * // grava o selecionado Ait aitx = new Ait();
                         * aitx.setId(idAit);
                         *
                         * //********************************************* //
                         * 27.06.2012 - alteração : remover acentos
                         * //*********************************************
                         * aitx.setObservacoes
                         * (Utilitarios.removeAcentos(edObservacoes
                         * .getText().toString()));
                         *
                         * AitDAO aitdao = new AitDAO(getBaseContext());
                         * aitdao.gravaObservacoes(aitx); aitdao.close();
                         *
                         * } });
                         *
                         * builder.show();
                         */

                    }

                });
        edObservacoes = (EditText) findViewById(R.id.edObservacoes);
        edObservacoes.setMaxLines(3);

        // pega a observacao do AIT
        String obsgravada = (String) getIntent().getSerializableExtra(
                "obsgravada");
        edObservacoes.setText(obsgravada);

        Button btGrava = (Button) findViewById(R.id.btGravaObs);

        btGrava.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // grava a observacao
                Ait aitx = new Ait();
                aitx.setId(idAit);

                aitx.setObservacoes(Utilitarios.removeAcentos(edObservacoes
                        .getText().toString()));

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaObservacoes(aitx);
                aitdao.close();

                // if (verificaLista()) return;

                // ***************************************
                // 10.08.2012 após selecionar Retorna
                // ***************************************
                if (verificaLista())
                    finish();

                if (edObservacoes.getText().length() == 0)
                    return;
                // pergunta se quer salvar as observacoes no arquivo
                // pergunta se quer pegar do arquivo
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ListaObservacoes.this);
                builder.setMessage("Arquiva esta observação para uso futuro em outros AIT's ?");

                // *******************************************
                // 10.08.2012 se não selecionou então Retorna
                // *******************************************
                builder.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                finish();
                            }
                        });

                builder.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                ArqObservacao arqobs = new ArqObservacao();

                                // *********************************************
                                // 27.06.2012 - alteração : remover acentos
                                // *********************************************
                                arqobs.setDescricao(Utilitarios
                                        .removeAcentos(edObservacoes.getText()
                                                .toString()));

                                ArqObservacaoDAO arqobsdao = new ArqObservacaoDAO(
                                        getBaseContext());
                                arqobsdao.insere(arqobs);
                                arqobsdao.close();

                                // carrega observacoes arquivadas...
                                carregaLista();

                                // ***************************************
                                // 10.08.2012 após gravar Retorna
                                // ***************************************
                                finish();

                            }
                        });

                builder.show();

            }
        });

    }

    /*
     * @Override protected void onListItemClick(ListView l, View v, int
     * position, long id) { // TODO Auto-generated method stub
     * super.onListItemClick(l, v, position, id);
     *
     * idarquivado = position;
     *
     * // pergunta se quer pegar do arquivo AlertDialog.Builder builder = new
     * AlertDialog.Builder(this);
     * builder.setMessage("Seleciona esta observação ?");
     * builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface dialog, int which) { //
     * TODO Auto-generated method stub
     *
     *
     * AlertDialog.Builder builder1 = new AlertDialog.Builder(getBaseContext());
     * builder1.setMessage("Exclui este Registro Arquivado de observação ?");
     * builder1.setNegativeButton("Não",null); builder1.setPositiveButton("Sim",
     * new DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface dialog, int which) { //
     * TODO Auto-generated method stub
     *
     * ArqObservacaoDAO arqobsdao = new ArqObservacaoDAO(getBaseContext());
     * arqobsdao.deletereg(idarquivado); arqobsdao.close(); } });
     *
     * } }); builder.setPositiveButton("Sim", new
     * DialogInterface.OnClickListener() {
     *
     * public void onClick(DialogInterface dialog, int which) { // TODO
     * Auto-generated method stub
     *
     * edObservacoes.setText(larqobs.get(idarquivado).getDescricao()); } });
     *
     * builder.show();
     *
     *
     * }
     */
}
