package br.com.cobrasin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.finger.FingerClient;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.AitEnquadramento;
import br.com.cobrasin.tabela.Enquadramento;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListaEnquadramento extends Activity {

    private long idait = 0;
    private List<AitEnquadramento> aitenq;
    private String salvaAgente;
    EditText edEnquadramento;
    Object[] enquadramentosUsados;
    List<Enquadramento> enquadramento = new ArrayList<Enquadramento>();
    String ExcessoCarga = "";
    boolean ModoBlitz = false;
    Integer ValorExcedidoCMT = 0;
    private String info = Utilitarios.getInfo();

    private void excluiReg(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exclui Registro ?");
        builder.setNegativeButton("Não", null);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
                daoenq.deletereg(id);
                daoenq.close();

                carregaSelec();
            }
        });

        builder.show();

    }

    private void excluiTodosRegs(final long idait) {


        AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
        daoenq.delete(idait);
        daoenq.close();

		
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Exclui Registro ?");
		builder.setNegativeButton("Não", null);
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
				daoenq.delete(idait);
				daoenq.close();
				
				carregaSelec();
			}
		});
				
		builder.show();
		
		*/

    }

    private void carregaSelec() {
        AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
        aitenq = daoenq.getLista(idait);
        daoenq.close();

        final ArrayAdapter<AitEnquadramento> adapter1 = new ArrayAdapter<AitEnquadramento>(this, android.R.layout.simple_list_item_1, aitenq);

        ListView listaEnquadraSelec = (ListView) findViewById(R.id.listaEnquadSelecionados);

        listaEnquadraSelec.setAdapter(adapter1);
        TextView txvExcluir = (TextView) findViewById(R.id.txvExcluir);
        if (adapter1.isEmpty() == true) {
            txvExcluir.setVisibility(4);
        }


        listaEnquadraSelec.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                // repete pois nao acha aitenq
                AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
                List<AitEnquadramento> aitenq = daoenq.getLista(idait);

                daoenq.close();
                long xid = aitenq.get(arg2).getId();
                excluiReg(xid);

            }
        });
    }

    private void chama() {
        Intent i = new Intent(this, ListaEnquadramento1.class);
        i.putExtra("idAit", idait);
        startActivity(i);
        finish();
    }

    private void incluiEnquad(String codigo, boolean fechaTela) {
        // tem aits disponiveis
        ListaAit listait = new ListaAit();

        if (listait.podeEnquadrar(ListaEnquadramento.this, salvaAgente)) {
            AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());

            if (!daoenq.getEnquadramento(idait, codigo)) {
                if (enquadramentosUsados != null) {
                    int i = 0;
                    while (i < enquadramentosUsados.length) {
                        String item = enquadramentosUsados[i].toString();
                        if (item.equals(codigo)) {
                            AlertDialog.Builder aviso = new AlertDialog.Builder(ListaEnquadramento.this);
                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                            aviso.setTitle("Enquadramento inválido");
                            aviso.setMessage("Esse enquadramento já foi usado anteriormente");
                            aviso.setNegativeButton("OK", null);
                            aviso.show();
                            return;
                        }
                        i++;
                    }
                }
                //*************************************************************
                // 26.01.2011 , somente 1 enquadramento por Auto de Infração
                //*************************************************************
                excluiTodosRegs(idait);
                daoenq.Insere(idait, codigo);


            }

            daoenq.close();

            //************************
            //retirado em 08.05.2012
            //chama();

            //************************
            if (fechaTela) {

                carregaSelec();
                finish();
            }


			/*
			// Pergunta se deseja ir para outra tela

		  	AlertDialog.Builder aviso = new AlertDialog.Builder(ListaEnquadramento.this);
	        aviso.setIcon(android.R.drawable.ic_dialog_alert);
	        aviso.setTitle("Seleção de Enquadramento");
	        aviso.setMessage("Vai para Outra tela ?");
	        aviso.setNegativeButton("Não", null);
	        aviso.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
			
					chama();
					
				}
			});
	        
	        aviso.show();
	        */
        }
    }

    private void trataPesquisa() {
        // TODO Auto-generated method stub


        if (edEnquadramento.getText().toString() != null) {

            ListView listaEnquadramentos = (ListView) findViewById(R.id.listaEnquadramentos);

            EnquadramentoDAO dao = new EnquadramentoDAO(this);

            if (ExcessoCarga != null) {
                if (ValorExcedidoCMT != null) {
                    if (ValorExcedidoCMT != 0) {
                        if (ValorExcedidoCMT <= 600) {
                            enquadramento = dao.getLista("68820",
                                    getBaseContext(), "");

                        }
                        if (ValorExcedidoCMT >= 601 && ValorExcedidoCMT <= 1000) {
                            enquadramento = dao.getLista("68900",
                                    getBaseContext(), "");

                        }
                        if (ValorExcedidoCMT >= 1001) {
                            enquadramento = dao.getLista("69040",
                                    getBaseContext(), "");

                        }
                    } else {
                        enquadramento = dao.getLista_Excesso(edEnquadramento
                                .getText().toString(), getBaseContext());
                    }
                } else {
                    enquadramento = dao.getLista_Excesso(edEnquadramento
                            .getText().toString(), getBaseContext());
                }

            } else {
                enquadramento = dao.getLista(edEnquadramento.getText().toString(), getBaseContext(), "");
            }


            dao.close();

            if (enquadramento.size() == 0) {
                Toast.makeText(getBaseContext(), "Nenhum enquadramento localizado !", Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<Enquadramento> adapter = new ArrayAdapter<Enquadramento>(this, android.R.layout.simple_list_item_1, enquadramento);

            listaEnquadramentos.setAdapter(adapter);


            listaEnquadramentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // TODO Auto-generated method stub

                    // pega o enquadramento selecionado da lista
                    incluiEnquad(enquadramento.get(arg2).getCodigo(),true);
                }
            });

        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enquadramento);
        edEnquadramento = (EditText) findViewById(R.id.edEnquadramento);

        // pega o Id do AIT
        idait = (Long) getIntent().getSerializableExtra("idAit");

        salvaAgente = (String) getIntent().getSerializableExtra("agente");

        ExcessoCarga = (String) getIntent().getSerializableExtra("ExcessoCarga");

        ValorExcedidoCMT = (Integer) getIntent().getSerializableExtra("ValorExcedidoCMT");
        try {
            ModoBlitz = (boolean) getIntent().getSerializableExtra("ModoBlitz");
        } catch (Exception e) {
            enquadramentosUsados = null;
        }

        try {
            enquadramentosUsados = (Object[]) getIntent().getSerializableExtra("enquadramentosUsados");
        } catch (Exception e) {
            enquadramentosUsados = null;
        }

        carregaSelec();
        //Button btProxTela = (Button) findViewById(R.id.btProxTelaEnquad);
        //btProxTela.setOnClickListener(new View.OnClickListener() {

        //	@Override
        //	public void onClick(View v) {
        // TODO Auto-generated method stub

        //chama();
        //}
        //});

        Button btPesquisa = (Button) findViewById(R.id.btPesquisaEnq);

        btPesquisa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                trataPesquisa();
            }


        });

        //Button btRetorna = (Button) findViewById(R.id.btRetornaEnquadramento);
        //btRetorna.setOnClickListener(new View.OnClickListener() {

        //@Override
        //public void onClick(View arg0) {
        // TODO Auto-generated method stub

        //finish();
        //}
        //});
    }

}
