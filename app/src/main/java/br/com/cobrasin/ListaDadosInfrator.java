package br.com.cobrasin;


import java.util.Iterator;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.ArqObservacao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListaDadosInfrator extends Activity {

    private String ufs[] = {"  -SEM IDENTIFICAÇÃO",
            "AC-ACRE",
            "AL-ALAGOAS",
            "AP-AMAPÁ",
            "AM-MANAUS",
            "BA-BAHIA",
            "CE-CEARÁ",
            "DF-DISTRITO FEDERAL",
            "ES-ESPÍRITO SANTO",
            "GO-GOIÁS",
            "MA-MARANHÃO",
            "MT-MATO GROSSO",
            "MS-MATO GROSSO DO SUL",
            "MG-MINAS GERAIS",
            "PA-PARÁ",
            "PB-PARAIÍBA",
            "PR-PARANÁ",
            "PE-PERNAMBUCO",
            "PI-PAUÍ",
            "RJ-RIO DE JANEIRO",
            "RN-RIO GRANDE DO NORTE",
            "RS-RIO GRANDE DO SUL",
            "RO-RONDÔNIA",
            "RR-RORAIMA",
            "SC-SANTA CATARINA",
            "SP-SÃO PAULO",
            "SE-SERGIPE",
            "TO-TOCANTINS"};


    private long idAit = 0;

    private EditText edNome;
    private EditText edCPF;
    private EditText edPGU;
    private EditText edUF;
    private Button btnAssinatura;

    private CheckBox chkPPD;

    private String TipoAIT;

    private TextView lblPGU;

    private boolean verificaLista() {
        boolean retorno = false;

        String cx = edUF.getText().toString().trim();

        //if (cx.length() == 0) retorno = true;

        if (edUF.getText().toString().length() == 2) {

            for (int nx = 1; nx < ufs.length; nx++) {
                String selecao = ufs[nx].substring(0, 2).toUpperCase();

                if (selecao.equals(edUF.getText().toString().toUpperCase())) {
                    edUF.setText(edUF.getText().toString().toUpperCase());
                    retorno = true;
                }
            }

        }

        //if (!retorno ) Toast.makeText(getBaseContext(), "UF inválida !", Toast.LENGTH_SHORT).show();
        return retorno;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dadosinfrator);

        edNome = (EditText) findViewById(R.id.edNomeInfratorEs);
        edCPF = (EditText) findViewById(R.id.edCPF);
        edPGU = (EditText) findViewById(R.id.edPasaporte);
        edUF = (EditText) findViewById(R.id.edPID);
        chkPPD = (CheckBox) findViewById(R.id.chkPPD);
        lblPGU = (TextView) findViewById(R.id.lblPGU);
        btnAssinatura = (Button) findViewById(R.id.btnAssInfrator);

        btnAssinatura.setEnabled(false);

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");

        // pega os outros dados
        String nome = (String) getIntent().getSerializableExtra("nome");
        String cpf = (String) getIntent().getSerializableExtra("cpf");
        String pgu = (String) getIntent().getSerializableExtra("pgu");
        String uf = (String) getIntent().getSerializableExtra("uf");
        String ppd_condutor = (String) getIntent().getSerializableExtra("ppd_condutor");

        if (ppd_condutor != null) {
            if (ppd_condutor.equals("S")) {
                chkPPD.setChecked(true);
            }
            if (ppd_condutor.equals("N")) {
                chkPPD.setChecked(false);
            }
        }

        TipoAIT = (String) getIntent().getSerializableExtra("TipoAIT");
        if (TipoAIT != null) {
            chkPPD.setVisibility(View.VISIBLE);
            lblPGU.setText("CNH:");
        } else {
            chkPPD.setVisibility(View.INVISIBLE);
            lblPGU.setText("PGU:");
        }

        edNome.setText(nome);
        edCPF.setText(cpf);
        edPGU.setText(pgu);
        edUF.setText(uf);
        if (nome !=null && nome.equals("")==false)
            btnAssinatura.setEnabled(true);

        edCPF.setInputType(InputType.TYPE_CLASS_NUMBER);
        edPGU.setInputType(InputType.TYPE_CLASS_NUMBER);
        edNome.requestFocus();
        //   Button btretorna = (Button) findViewById(R.id.btRetornaInfrator);
        //    btretorna.setOnClickListener(new View.OnClickListener() {

        //		@Override
        //		public void onClick(View arg0) {
        // TODO Auto-generated method stub

        //		finish();
        //		}
        //});

        edNome.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                //grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                //*********************************************
                // 27.06.2012 - alteração : remover acentos
                //*********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                aitx.setCpf(edCPF.getText().toString());
                aitx.setPgu(edPGU.getText().toString());
                aitx.setPid("");
                aitx.setPassaporte("");
                aitx.setUf(edUF.getText().toString().toUpperCase());
                aitx.setTipoinfrator("CNH");
                if (TipoAIT != null) {
                    if (chkPPD.isChecked()) {
                        aitx.setPpd_condutor("S");
                    } else {
                        aitx.setPpd_condutor("N");
                    }
                } else {
                    aitx.setPpd_condutor("");
                }

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfrator(aitx);
                aitdao.close();
                return false;
            }
        });

        edCPF.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                //grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                //*********************************************
                // 27.06.2012 - alteração : remover acentos
                //*********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                aitx.setCpf(edCPF.getText().toString());
                aitx.setPgu(edPGU.getText().toString());
                aitx.setPid("");
                aitx.setPassaporte("");
                aitx.setUf(edUF.getText().toString().toUpperCase());
                aitx.setTipoinfrator("CNH");
                if (TipoAIT != null) {
                    if (chkPPD.isChecked()) {
                        aitx.setPpd_condutor("S");
                    } else {
                        aitx.setPpd_condutor("N");
                    }
                } else {
                    aitx.setPpd_condutor("");
                }

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfrator(aitx);
                aitdao.close();
                return false;
            }
        });
        edPGU.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                //grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                //*********************************************
                // 27.06.2012 - alteração : remover acentos
                //*********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                aitx.setCpf(edCPF.getText().toString());
                aitx.setPid("");
                aitx.setPassaporte("");
                aitx.setPgu(edPGU.getText().toString());
                aitx.setUf(edUF.getText().toString().toUpperCase());
                aitx.setTipoinfrator("CNH");
                if (TipoAIT != null) {
                    if (chkPPD.isChecked()) {
                        aitx.setPpd_condutor("S");
                    } else {
                        aitx.setPpd_condutor("N");
                    }
                } else {
                    aitx.setPpd_condutor("");
                }

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfrator(aitx);
                aitdao.close();
                return false;
            }
        });

        edUF.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                //grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                //*********************************************
                // 27.06.2012 - alteração : remover acentos
                //*********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                aitx.setCpf(edCPF.getText().toString());
                aitx.setPgu(edPGU.getText().toString());
                aitx.setPid("");
                aitx.setPassaporte("");
                aitx.setUf(edUF.getText().toString().toUpperCase());
                aitx.setTipoinfrator("CNH");
                if (TipoAIT != null) {
                    if (chkPPD.isChecked()) {
                        aitx.setPpd_condutor("S");
                    } else {
                        aitx.setPpd_condutor("N");
                    }
                } else {
                    aitx.setPpd_condutor("");
                }

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfrator(aitx);
                aitdao.close();
                return false;
            }
        });

        Button btgrava = (Button) findViewById(R.id.btGravaInfrator);
        btgrava.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (verificaLista()) {

                    if (Utilitarios.validaCPF(edCPF.getText().toString())) {

                        if (Utilitarios.validaPGU(edPGU.getText().toString())) {

                            if (edNome.getText().toString().trim().length() > 0) {
                                //grava dados do infrator
                                Ait aitx = new Ait();
                                aitx.setId(idAit);
                                //*********************************************
                                // 27.06.2012 - alteração : remover acentos
                                //*********************************************
                                aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                                aitx.setCpf(edCPF.getText().toString());
                                aitx.setPid("");
                                aitx.setPassaporte("");
                                aitx.setPgu(edPGU.getText().toString());
                                aitx.setUf(edUF.getText().toString().toUpperCase());
                                aitx.setTipoinfrator("CNH");
                                if (TipoAIT != null) {
                                    if (chkPPD.isChecked()) {
                                        aitx.setPpd_condutor("S");
                                    } else {
                                        aitx.setPpd_condutor("N");
                                    }
                                } else {
                                    aitx.setPpd_condutor("");
                                }


                                AitDAO aitdao = new AitDAO(getBaseContext());
                                aitdao.gravaInfrator(aitx);
                                aitdao.close();

                                btnAssinatura.setEnabled(true);
                                //finish();
                            } else {
                                Toast.makeText(ListaDadosInfrator.this, "Nome não preenchido !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ListaDadosInfrator.this, "PGU inválido ou não preenchido !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ListaDadosInfrator.this, "CPF inválido ou não preenchido !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListaDadosInfrator.this, "UF inválida ou não preenchida !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnAssinatura.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getBaseContext(), AssinaturaInfratorActivity.class);
                i.putExtra("idAit", idAit);
                startActivity(i);
            }
        });
    }

}


