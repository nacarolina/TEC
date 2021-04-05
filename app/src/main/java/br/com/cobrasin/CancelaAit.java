package br.com.cobrasin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;


import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.Parametro;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class CancelaAit extends Activity {

    private long idAit;
    private String agente;
    private EditText edMotivo;
    private TextView txtMotivo;
    private String info = Utilitarios.getInfo();
    private AitDAO aitdao;
    private String tela;
    Handler handler = new Handler();;
    boolean ModoBlitz = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cancela);

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");


        agente = (String) getIntent().getSerializableExtra("agente");

        tela = (String) getIntent().getSerializableExtra("tela");

        edMotivo = (EditText) findViewById(R.id.edMotivo);

        txtMotivo = (TextView) findViewById(R.id.txtMotivo);

        aitdao = new AitDAO(getBaseContext());
/*
        String IMEI = "";
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                SubscriptionManager subsManager = (SubscriptionManager) getBaseContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

                if (subsList != null) {
                    for (SubscriptionInfo subsInfo : subsList) {
                        if (subsInfo != null) {
                            IMEI = subsInfo.getNumber().replace("+", "");

                            //region valida chip
                            try {

                                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/NmrCelular.txt");
                                //Se for possivel ler  o arquivo
                                if (f.canRead()) {
                                    BufferedReader br = null;
                                    br = new BufferedReader(new FileReader(f));

                                    String texto;
                                    while ((texto = br.readLine()) != null) {
                                        if (texto.equals(IMEI) == false) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                                                            CancelaAit.this);
                                                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                                    aviso.setTitle("Número do Registro inválido");
                                                    aviso.setMessage("Deseja instalar novamente os dados do banco de dados?");
                                                    aviso.setNegativeButton("Não",
                                                            new DialogInterface.OnClickListener() {

                                                                @Override
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                    finish();

                                                                }
                                                            });
                                                    aviso.setPositiveButton("Sim",
                                                            new DialogInterface.OnClickListener() {

                                                                @Override
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                    String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

                                                                    Intent i = new Intent(getBaseContext(), DownloadFTP.class);
                                                                    startActivity(i);
                                                                    finish();

                                                                }
                                                            });
                                                    aviso.show();
                                                }
                                            });

                                        }
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //endregion
                        }
                    }
                    if (subsList.isEmpty()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder aviso = new AlertDialog.Builder(
                                        CancelaAit.this);
                                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                aviso.setTitle("Erro ao obter número do registro");
                                aviso.setMessage("Número do Registro inválido!");
                                aviso.setNegativeButton("OK",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();

                                            }
                                        });
                                aviso.show();
                            }
                        });
                    }
                }
            }
        }*/
        Cursor cur = aitdao.getAit(idAit);
        try {
            ModoBlitz = (boolean) getIntent().getSerializableExtra("ModoBlitz");
        } catch (Exception e) {

        }
        try {

            AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(CancelaAit.this);
            Cursor c1 = aitenq.getLista1(idAit);

            String enquads = " ";
            while (c1.moveToNext()) {
                //enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(this);
                List<Enquadramento> enquadramento = null;
                try {
                    enquadramento = dao.getLista(SimpleCrypto.decrypt(info, c1.getString(c1.getColumnIndex("codigo"))), CancelaAit.this, "");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dao.close();

                enquads += enquadramento.get(0).toString() + " / ";

            }

            c1.close();

            String xinfo = " Placa: " + SimpleCrypto.decrypt(info, cur.getString(cur.getColumnIndex("placa"))) + String.format("\n") + String.format("\n");
            xinfo += " Data/Hora:" + SimpleCrypto.decrypt(info, cur.getString(cur.getColumnIndex("data"))) + "-" + SimpleCrypto.decrypt(info, cur.getString(cur.getColumnIndex("hora"))) + String.format("\n") + String.format("\n");
            xinfo += " Agente: " + SimpleCrypto.decrypt(info, cur.getString(cur.getColumnIndex("agente"))) + String.format("\n") + String.format("\n") + String.format("\n");
            xinfo += " Enquadramento: " + enquads + String.format("\n") + String.format("\n") + String.format("\n");
            xinfo += " Motivo Cancelamento:";

            txtMotivo.setText(xinfo);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        aitdao.close();

        Button btGrava = (Button) findViewById(R.id.btGravaMotivo);

        btGrava.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (edMotivo.length() > 0) {

                    AlertDialog.Builder aviso = new AlertDialog.Builder(CancelaAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Cancelamento de AIT");
                    aviso.setMessage("Confirma ?");
                    aviso.setNeutralButton("Não", null);
                    aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            AitDAO aitdao = new AitDAO(getBaseContext());

                            Ait aitx = new Ait();

                            //*************************************************************************
                            // AIT esta aberto ?  , agente solicitou cancelamento ou saiu da aplicacao
                            //
                            // 26.01.2012
                            //*************************************************************************

                            Cursor cur = aitdao.getAit(idAit);

                            if (cur.getString(cur.getColumnIndex("flag")).equals("A")) {

                                ParametroDAO pardao = new ParametroDAO(CancelaAit.this);
                                Cursor cz = pardao.getParametros();

                                Parametro param = new Parametro();
                                try {
                                    param.setProximoait(SimpleCrypto.decrypt(info, cz.getString(0)));
                                    param.setSeriepda(SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("seriepda"))));

                                } catch (Exception e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }


                                // Atencao: Todos os dados já foram criptografados...


                                Ait aitz = new Ait();
                                aitz.setId(idAit);        // id
                                aitz.setFlag("F");                        // flag
                                aitz.setAit(param.getProximoait());        // numero do ait
                                aitz.setEncerrou(Utilitarios.getDataHora(1));
                                aitz.setSeriepda(param.getSeriepda());
                                try {
                                    aitz.setAgente(SimpleCrypto.decrypt(info, cur.getString(cur.getColumnIndex("agente"))));
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                aitz.setCancelou(Utilitarios.getDataHora(1));
                                aitz.setMotivo(edMotivo.getText().toString());
                                aitdao.fechaAitDAO(aitz);
                                LogDAO l = new LogDAO(CancelaAit.this);

                                // atualiza proxait do parametro
                                long prox = Long.parseLong(param.getProximoait());

                                try {
                                    l.gravalog("Cancelou AIT- " + prox, "UPDATE", SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("orgaoautuador"))),
                                            SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("seriepda"))), agente, CancelaAit.this);

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                prox++;
                                String formatado = String.format("%07d", prox);
                                param.setProximoait(formatado);
                                try {
                                    param.setSeriepda(SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("seriepda"))));
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                pardao.gravaParam(param);
                                pardao.close();

                                cz.close();

                                //ListaAit l = new ListaAit();

                                cur.close();

                                aitdao.close();

                                if (tela == null) {
                                    Intent i = new Intent();
                                    i = new Intent(getBaseContext(), ListaAit.class);
                                    i.putExtra("ModoBlitz", ModoBlitz);
                                    i.putExtra("agente", agente);
                                    startActivity(i);
                                    finish();
                                } else {
                                    if (tela.contains("login")) {
                                        Intent in = new Intent(getBaseContext(), ListaAit.class);
                                        in.putExtra("agente", agente);
                                        startActivity(in);
                                    }
                                }


                                //l.carregaLista();
                            } else {

                                aitx.setMotivo(edMotivo.getText().toString());
                                aitdao.gravaCancelamento(aitx, idAit);

                                cur.close();

                                aitdao.close();


                                /*Intent i = new Intent();
                                i = new Intent(getBaseContext(), ListaAit.class);
                                i.putExtra("ModoBlitz", ModoBlitz);
                                i.putExtra("agente", agente);
                                startActivity(i);*/
                                finish();

                            }

                            cur.close();

                            aitdao.close();

                            /*Intent i = new Intent();
                            i = new Intent(getBaseContext(), ListaAit.class);
                            i.putExtra("ModoBlitz", ModoBlitz);
                            i.putExtra("agente", agente);
                            startActivity(i);*/

                            finish();

                        }
                    });

                    aviso.show();
                } else {
                    Toast.makeText(getBaseContext(), "Falta preencher o motivo !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //      Button btRetornaCan = ( Button ) findViewById(R.id.btRetornaCan);
        //     btRetornaCan.setOnClickListener(new View.OnClickListener() {

        //		@Override
        //		public void onClick(View arg0) {
        // TODO Auto-generated method stub

        //	finish();
        //	}

        //});


    }


}
