package br.com.cobrasin;


import java.util.Iterator;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.ArqObservacao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class InfratorInternacional extends Activity {

    private long idAit = 0;

    private EditText edNome;
    private EditText edPasaporte;
    private EditText edPID;
    private Button btnAssinatura;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CarregaTela();
    }

    private void CarregaTela() {
        setContentView(R.layout.infratorinternacional);
        edNome = (EditText) findViewById(R.id.edNomeInfratorEs);
        edPasaporte = (EditText) findViewById(R.id.edPasaporte);
        edPID = (EditText) findViewById(R.id.edPID);

        btnAssinatura = (Button) findViewById(R.id.btnAssInfratorInter);
        btnAssinatura.setEnabled(false);

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");

        // pega os outros dados
        String nome = (String) getIntent().getSerializableExtra("nome");
        String passaporte = (String) getIntent().getSerializableExtra("passaporte");
        String pid = (String) getIntent().getSerializableExtra("pid");
        // String uf =  (String) getIntent().getSerializableExtra("uf");

        edNome.setText(nome);
        edPasaporte.setText(passaporte);
        edPID.setText(pid);
        //  edUF.setText(uf);

        // edPasaporte.setInputType(InputType.);
        //edPID.setInputType(InputType.TYPE_CLASS_NUMBER);
        edNome.requestFocus();
        edNome.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                // grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                // *********************************************
                // 27.06.2012 - alteração : remover acentos
                // *********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText()
                        .toString().toUpperCase()));
                aitx.setPassaporte(edPasaporte.getText().toString());
                aitx.setPid(edPID.getText().toString());
                aitx.setTipoinfrator("PID");
                // aitx.setUf(edUF.getText().toString().toUpperCase());

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfratorPID(aitx);
                aitdao.close();
                return false;
            }
        });

        edPasaporte.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                // grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                // *********************************************
                // 27.06.2012 - alteração : remover acentos
                // *********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText()
                        .toString().toUpperCase()));
                aitx.setPassaporte(edPasaporte.getText().toString());
                aitx.setPid(edPID.getText().toString());
                aitx.setTipoinfrator("PID");
                // aitx.setUf(edUF.getText().toString().toUpperCase());

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfratorPID(aitx);
                aitdao.close();
                return false;
            }
        });

        edPID.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                // grava dados do infrator
                Ait aitx = new Ait();
                aitx.setId(idAit);
                // *********************************************
                // 27.06.2012 - alteração : remover acentos
                // *********************************************
                aitx.setNome(Utilitarios.removeAcentos(edNome.getText()
                        .toString().toUpperCase()));
                aitx.setPassaporte(edPasaporte.getText().toString());
                aitx.setPid(edPID.getText().toString());
                aitx.setTipoinfrator("PID");
                // aitx.setUf(edUF.getText().toString().toUpperCase());

                AitDAO aitdao = new AitDAO(getBaseContext());
                aitdao.gravaInfratorPID(aitx);
                aitdao.close();
                return false;
            }
        });

        Button btgrava = (Button) findViewById(R.id.btGravaInfratorIn);
        btgrava.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (edPasaporte.getText().toString().trim().length() > 0) {
                    if (edPID.getText().toString().trim().length() > 0) {
                        if (edNome.getText().toString().trim().length() > 0) {
                            //grava dados do infrator
                            Ait aitx = new Ait();
                            aitx.setId(idAit);
                            //*********************************************
                            // 27.06.2012 - alteração : remover acentos
                            //*********************************************
                            aitx.setNome(Utilitarios.removeAcentos(edNome.getText().toString().toUpperCase()));
                            aitx.setPassaporte(edPasaporte.getText().toString());
                            aitx.setPid(edPID.getText().toString());
                            aitx.setTipoinfrator("PID");
                            //aitx.setUf(edUF.getText().toString().toUpperCase());

                            AitDAO aitdao = new AitDAO(getBaseContext());
                            aitdao.gravaInfratorPID(aitx);
                            aitdao.close();

                            btnAssinatura.setEnabled(true);
                            //finish();
                        } else {
                            Toast.makeText(InfratorInternacional.this, "Nome não preenchido !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InfratorInternacional.this, "PID não preenchido !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InfratorInternacional.this, "Documento de Identificação não preenchido !", Toast.LENGTH_SHORT).show();
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