package br.com.cobrasin;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.Logs;
import br.com.cobrasin.tabela.MedidaAdm;
import br.com.cobrasin.tabela.Pais;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.tabela.Tipo;


public class CobrasinAitActivity extends Activity {
    protected static final int ACTIVITY_CREATE = 0;

    private TrataAgente edAgente;
    private EditText edSenha;
    private String agenteEscolhido = "";
    private String info = Utilitarios.getInfo();
    private String usuarioWebTrans, senhaWebTrans, codMunicipio;
    static ProgressDialog progress;
    private JSONObject json1;
    private Context ctxx;
    private boolean lok = false;
    private boolean tentar = false;
    TextView txtNomePda;
    LogDAO l = new LogDAO(CobrasinAitActivity.this);
    private String IMEI;
    private JSONArray jsonArray;
    private String tabelas[] = {"agente", "enquadramento", "enquadramentopf", "enquadramentopj", "especie", "tipo", "logradouro", "ait", "aitenquadramento", "parametro", "medidasadm", "pais", "urlswebtrans", "municipio"};
    private Handler handler = new Handler();
    ProgressDialog dialog;
    public boolean ModoBlitz = false;

    public String InstalImei() {
        String ret = "ok";


        //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

        //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

        senhaWebTrans = MD5Util.criptografar("cobratalonario");
        usuarioWebTrans = "talonario";

        //trataEquipamentos("3");

        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(CobrasinAitActivity.this);

        String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

        String urlBase = urlswebtrans.geturl("imei");

        urlswebtrans.close();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // buscar em parametros!!!!!
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();


        IMEI="13274918857";
        //***********************************************************************
        // TESTE DE CRIPTOGRAFIA - 10.04.2012
        //***********************************************************************
        urlBase += "?user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4) + "&imei=" + IMEI;

        nvps.add(new BasicNameValuePair("checkSum", MD5Util.criptografar(urlBase)));


        try {
            urlBase = SimpleCrypto.encrypt(info, urlBase);
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }


        nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));


        //*****************************************************************************
        //.multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
        //*****************************************************************************

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {

            ret = "service error";
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            //HttpResponse response = httpclient.execute(httpget);
            HttpResponse response = httpclient.execute(post);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            String retornoweb = EntityUtils.toString(response.getEntity());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (statusCode == 403 || statusCode == 302 || statusCode == 301) {
                    url = response.getHeaders("Location")[0].getElements()[0].getName() + "=" + response.getHeaders("Location")[0].getElements()[0].getValue();

                    post = new HttpPost(url);

                    try {
                        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    } catch (UnsupportedEncodingException e1) {

                        ret = "service error";
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // HttpResponse response = httpclient.execute(httpget);
                    response = httpclient.execute(post);

                    statusLine = response.getStatusLine();
                    statusCode = statusLine.getStatusCode();

                    retornoweb = EntityUtils.toString(response.getEntity());
                }
            }
            if (statusCode == 200) {

                try {

                    try {
                        retornoweb = SimpleCrypto.decrypt(Utilitarios.getInfo(), retornoweb);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

					/*if (tipotransacao.equals("clientes"))
					{
						retornoweb = "[" + retornoweb + "]";
					}*/

                    jsonArray = new JSONArray(retornoweb);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = "json error";
                    e.printStackTrace();
                }

            } else {

                //******************************
                // 18.05.2012
                // grava resposta do WEBTRANS
                //******************************
				/*String mensz = "Sinc.Tabelas Retorno: " +
				String.format("%d",statusCode) +
				" - "+
				retornoweb;*/

                //Utilitarios.gravaLog(mensz, context);
                ret = "service error";
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "service error";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "service error";
        }


        return ret;
    }

    private void chama() {


        AgenteDAO dao = new AgenteDAO(CobrasinAitActivity.this);

        String retorno = dao.validaAgente(edAgente.getText());

        dao.close();

        // achou agente ?
        if (retorno.length() > 0) {
            // compara a senha
            Editable edsenha = edSenha.getText();
            String senha = edsenha.toString();

            senha = MD5Util.criptografar(senha);

            //String senha
            if (senha.equals(retorno)) {
	    			/*
	    			try {
						String teste = SimpleCrypto.encrypt(Utilitarios.getInfo(), "teste");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    			*/
                //******************************************
                //	26.01.2012
                // existe ait Aberta ? entao cancela
                //******************************************
                AitDAO aitdao = new AitDAO(getBaseContext());
                Cursor ch = null;
                try {
                    ch = aitdao.aitAberta(SimpleCrypto.encrypt(info, edAgente.getText().toString()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                aitdao.close();
                boolean DNIT = dao.verificaAgenteDNIT(edAgente.getText().toString());
                Intent i;
                if (ch.getCount() > 0) {
                    //i = new Intent(this, PreencheAit.class);
                    i = new Intent(getBaseContext(), CancelaAit.class);
                    i.putExtra("idAit", ch.getLong(ch.getColumnIndex("id")));
                    i.putExtra("tela", "login");
                    //Bundle param = new Bundle();
                    //param.putString("agente",edAgente.getText().toString());
                    i.putExtra("agente", edAgente.getText().toString());
                    i.putExtra("Agente_DNIT", DNIT);
                } else {
                    i = new Intent(this, ListaAit.class);
                    i.putExtra("agente", edAgente.getText().toString());
                    i.putExtra("ModoBlitz", ModoBlitz);
                    i.putExtra("Agente_DNIT", DNIT);
                }

                ch.close();

                //*************************************
                // 27.06.2012
                // verifica prefeitura e pda bloqueados
                //*************************************
                ParametroDAO pardao = new ParametroDAO(CobrasinAitActivity.this);

                Cursor c = pardao.getParametros();

                c.moveToFirst();

                String pdaAtivo = "";
                String prefAtiva = "";
                String pda = "";
                try {
                    pdaAtivo = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("ativo")));
                    prefAtiva = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("prefativa")));
                    pda = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("seriepda")));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


                //**********************************************************************
                //09.08.2012
                //
                //Atualiza em Parametros a Senha do Agente para transacao com o WebTrans
                //**********************************************************************
                Parametro parz = new Parametro();

                AgenteDAO agentedao = new AgenteDAO(CobrasinAitActivity.this);

                try {
                    parz.setSeriepda(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("seriepda"))));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    parz.setUsuariowebtrans(SimpleCrypto.encrypt(info, agentedao.getLoginAgente(edAgente.getText().toString())));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                parz.setSenhawebtrans(retorno);

                pardao.atualizaWebTrans(parz);

                c.close();

                pardao.close();


                if (pdaAtivo.contains("N")) {
                    Toast.makeText(getBaseContext(), ">PDA bloqueado consultando servidor!", Toast.LENGTH_LONG).show();

                    if (InstalImei().equals("ok")) {
                        JSONObject j;
                        try {
                            j = jsonArray.getJSONObject(0);

                            String StatusPDA = "S";
                            if (j.getString("status").toUpperCase().contains("INATIVO"))
                                StatusPDA = "N";
                            ParametroDAO p = new ParametroDAO(CobrasinAitActivity.this);

                            p.SetPdaAtivo(SimpleCrypto.encrypt(info, StatusPDA), SimpleCrypto.encrypt(info, pda));
                            pdaAtivo = StatusPDA;


                            if (pdaAtivo.equals("N"))
                                Toast.makeText(getBaseContext(), ">PDA bloqueado pelo Órgão de Trânsito!", Toast.LENGTH_LONG).show();
                            else {
                                agenteEscolhido = edAgente.getText().toString();

                                edAgente.setText("");
                                edSenha.setText("");

                                startActivity(i);

                                Cursor par = pardao.getParametros();

                                try {
                                    l.gravalog("Efetuado com sucesso", "Login",
                                            SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("orgaoautuador"))),
                                            SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("seriepda"))), agenteEscolhido, this.ctxx);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                finish();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(getBaseContext(), ">Não foi possivel obter os dados do equipamento, tente novamente!", Toast.LENGTH_LONG).show();

                    }

                } else {
                    if (prefAtiva.contains("N")) {
                        Toast.makeText(getBaseContext(), ">Prefeitura bloqueada !", Toast.LENGTH_SHORT).show();
                    } else {

                        // Agente pode multar ?
                        if (Utilitarios.agenteAtivo(CobrasinAitActivity.this, edAgente.getText().toString())) {
                            agenteEscolhido = edAgente.getText().toString();

                            // 	indica que o usuario entrou no sistema
                            //Utilitarios.gravaLogon(agenteEscolhido, "E", getBaseContext());

                            //i.putExtra("agente", edAgente.getText().toString());

                            edAgente.setText("");
                            edSenha.setText("");

                            startActivity(i);

                            //DefinepdaDAO dfdao = new DefinepdaDAO(CobrasinAitActivity.this);

                            //Cursor cx = dfdao.getParametros();

                            //ParametroDAO p = new ParametroDAO(CobrasinAitActivity.this);
                            Cursor par = pardao.getParametros();

                            //txtNomePda.getText().toString()
                            try {
                                l.gravalog("Efetuado com sucesso", "Login",
                                        SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("orgaoautuador"))),
                                        SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("seriepda"))), agenteEscolhido, this.ctxx);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            //List<Logs>  lg= l.getLogs();

                            //for (Logs b:lg)
                            //{
                            //	Toast.makeText( getBaseContext() ,b.getId().toString()+",Ag:"+b.getAgente()
                            //	+",Dt:"+b.getDataHora()+",Op:"+b.getOperacao()+",Og:"+b.getOrgao()
                            //		+",Pda:"+b.getPda()+",Status:"+b.getStatus(),Toast.LENGTH_SHORT).show();
                            // b.getStatus()
                            //}
                            //************************************
                            // 02.07.2012
                            // termina após chama a outra activity
                            //************************************
                            finish();

                        } else {
                            Toast.makeText(getBaseContext(), ">Agente Bloqueado pelo Órgão de Trânsito!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), ">Senha não confere!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), ">Agente não cadastrado!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //boolean ret = Utilitarios.validaPGU("02861918210");

        // boolean dix = Utilitarios.cancelouAntes("18/05/2012 13:09:10", "18/05/2012 13:10:20");
        // boolean diz = Utilitarios.cancelouAntes("18/05/2012 18:13:20", "19/05/2012 13:10:20");

        setContentView(R.layout.main);
        //ParametroDAO par=new ParametroDAO(this);
        //Boolean b =par.pdaAtivo();

        try {
            String hora = SimpleCrypto.decrypt(info, "2ACB95147CB874BCD7EDD7EC60FC8850");
            String horaCerta = SimpleCrypto.encrypt(info, "15:06");
            String teste = "";
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TextView txtIMEI = (TextView) findViewById(R.id.edIMEI);
        TextView lblIMEI = (TextView) findViewById(R.id.lblIMEI);
        try {
            String IMEI = "";
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    lblIMEI.setText("CPF");
                    IMEI="";
                } else {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    IMEI = tm.getDeviceId();//"869817032062827";
                }
            }
            txtIMEI.setText(IMEI);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (txtIMEI.getText().length() > 0) {
            //eqp sem imei em alguns casos retorna 000000000000
            //txtIMEI.setVisibility(4);
            //lblIMEI.setVisibility(4);
        }
        //Utilitarios.gravaLog("teste...", CobrasinAitActivity.this);
        edAgente = (TrataAgente) findViewById(R.id.edAgente);
        edAgente.setMaxLines(1);
        //    edAgente.setText("4448");  // debug  - retirar
        //edAgente.setText("1234567");  // debug  - retirar
        edSenha = (EditText) findViewById(R.id.edSenha);
        edSenha.setMaxLines(1);
        //edSenha.setText("8415"); // debug  - retirar
        //      edSenha.setText("4666"); // debug  - retirar
        //   edSenha.setInputType(InputType.TYPE_CLASS_NUMBER);

        TextView lblPda = (TextView) findViewById(R.id.nomepda);
        Button btEntrar = (Button) findViewById(R.id.btEntrar);
        Button btDelDb = (Button) findViewById(R.id.btDelDb);
        Button btRxWebTrans = (Button) findViewById(R.id.btRxWebTrans);


        atualizaNomes();
        ParametroDAO p = new ParametroDAO(CobrasinAitActivity.this);
        if (p.bDados()) {
            btDelDb.setVisibility(4);
            btRxWebTrans.setVisibility(4);
            txtIMEI.setVisibility(4);
            lblIMEI.setVisibility(4);
        } else {
            btEntrar.setVisibility(4);
            lblPda.setText("Instalação do Talonário");
            edAgente.setVisibility(4);
            edSenha.setVisibility(4);
            TextView lblAgente = (TextView) findViewById(R.id.txvAgente);
            lblAgente.setVisibility(4);
            TextView lblSenhaAgente = (TextView) findViewById(R.id.lblSenhaAgente);
            lblSenhaAgente.setVisibility(4);
        }


        btEntrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama();

            }
        });

        Button btEncerra = (Button) findViewById(R.id.btEncerraLogon);

        btEncerra.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                ctxx = getBaseContext();
                finish();
                //progress = ProgressDialog.show( CobrasinAitActivity.this, "Aguarde..." , "Salvando uma cópia de segurança dos dados!!!",true,true);

                //new Thread() {
                //     public void run() {

                //     	Utilitarios.copiaBase(0,ctxx);  // backup

                //         progress.dismiss();


                //      }
                // }.start();

            }
        });


        btDelDb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder aviso = new AlertDialog.Builder(CobrasinAitActivity.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Exclusão do Banco de Dados");
                aviso.setMessage("Confirma ?");
                aviso.setNeutralButton("Não", null);
                aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    //@Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluiBase();
                        startActivity(new Intent(CobrasinAitActivity.this, DownloadFTP.class));
                        finish();
                    }
                });
                aviso.show();

            }
        });


        btRxWebTrans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                AlertDialog.Builder aviso = new AlertDialog.Builder(CobrasinAitActivity.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Recebimento de dados do WebTrans");
                aviso.setMessage("Confirma ?");
                aviso.setNeutralButton("Não", null);
                aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParametroDAO p = new ParametroDAO(CobrasinAitActivity.this);
                        Parametro pa = new Parametro();
                        try {
                            p.limpareg();
                            //pa.setIMEI(SimpleCrypto.encrypt(info, IMEI));
                            TextView txtIMEI = (TextView) findViewById(R.id.edIMEI);
                            //IMEI=txtIMEI.getText().toString();
                            if (txtIMEI.getText().length() == 0) {
                                AlertDialog.Builder aviso = new AlertDialog.Builder(CobrasinAitActivity.this);
                                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                aviso.setTitle("Autenticação");
                                aviso.setMessage("Insira o IMEI do equipamento!");
                                aviso.setNeutralButton("OK", null);
                                aviso.show();
                                return;
                            }
							/*else
							{
								
								if(IMEI.length()>0)
									txtIMEI.setText(IMEI);
								else
									IMEI= txtIMEI.getText().toString();
							}
							*/
                            pa.setIMEI(txtIMEI.getText().toString());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        p.setParamReceivedByIMEI(pa);
                        String I = p.getIMEI();
                        p.close();

                        lok = true;
                        if (lok) {

                            if (Utilitarios.conectado(CobrasinAitActivity.this)) {
                                SincronismoWebTrans s = new SincronismoWebTrans(CobrasinAitActivity.this, "3");
                                s.Sincronizar();
                                Button btDelDb = (Button) findViewById(R.id.btDelDb);
                                btDelDb.setVisibility(0);
                                Button btRxWebTrans = (Button) findViewById(R.id.btRxWebTrans);
                                btRxWebTrans.setVisibility(0);
                                Button btEntrar = (Button) findViewById(R.id.btEntrar);
                                btEntrar.setVisibility(0);
                                TextView lblPda = (TextView) findViewById(R.id.nomepda);
                                lblPda.setText("Login");
                                edAgente.setVisibility(0);
                                edSenha.setVisibility(0);
                                TextView lblAgente = (TextView) findViewById(R.id.txvAgente);
                                lblAgente.setVisibility(0);
                                TextView lblSenhaAgente = (TextView) findViewById(R.id.lblSenhaAgente);
                                lblSenhaAgente.setVisibility(0);
                            } else {
                                Toast.makeText(getBaseContext(), ">Não existe Rede Disponível para Receber os dados!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });
                aviso.show();
            }
        });

    }

    private void informUsr(final String mens) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                dialog.setMessage(mens);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        atualizaNomes();

        if (agenteEscolhido.length() > 0) {
            // 	indica que o usuario saiu do sistema, forçar o logon novamente
            //Utilitarios.gravaLogon(agenteEscolhido, "S", getBaseContext());
            agenteEscolhido = "";
        }
        /*String IMEI = "";
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                SubscriptionManager subsManager = (SubscriptionManager) getBaseContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

                if (subsList != null) {
                    for (SubscriptionInfo subsInfo : subsList) {
                        if (subsInfo != null) {

                            //region valida chip
                            try {
                                IMEI = subsInfo.getNumber().replace("+", "");

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
                                                            CobrasinAitActivity.this);
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
                                                                    File f = new File(root);
                                                                    if(f.isDirectory())
                                                                        f.delete();

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
                                        CobrasinAitActivity.this);
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

    }

    private void atualizaNomes() {
        //******************************************
        // 16.05.2012 - escreve o nome do PDA...
        //******************************************
        ParametroDAO pardao = new ParametroDAO(CobrasinAitActivity.this);
        Cursor par = pardao.getParametros();

        txtNomePda = (TextView) findViewById(R.id.nomepda);
        try {
            txtNomePda.setText(SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("seriepda"))));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //******************************************
        // 24.05.2012 - escreve o patrimonio
        //******************************************
        try {
            if (SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("servidorweb"))) != null) {
                txtNomePda.setText(txtNomePda.getText().toString() + " \\ IMPRESSORA:" + SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("servidorweb"))));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            txtNomePda.setText(SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("seriepda"))));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //******************************************
        // 24.05.2012 - escreve o patrimonio
        //******************************************
        try {
            if (SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("servidorweb"))) != null) {
                txtNomePda.setText(txtNomePda.getText().toString() + " \\ IMPRESSORA:" + SimpleCrypto.decrypt(info, par.getString(par.getColumnIndex("servidorweb"))));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        txtNomePda.setTypeface(Typeface.DEFAULT_BOLD);
        par.close();
        pardao.close();

    }

    private void excluiBase() {
        // caminho onde estão os arquivos
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

        File file;
        // tenta excluir todas
        for (int nx = 0; nx < tabelas.length; nx++) {

            // caminho onde estão os arquivos
            try {
                file = new File(root, tabelas[nx]);
                file.delete();

            } catch (Exception e) {

            }

        }

        //*****************************************
        // o aitfoto é criado em tempo de execução!
        //*****************************************
        try {
            file = new File(root, "aitfoto");
            file.delete();

        } catch (Exception e) {

        }


        //*****************************************
        // o logs é criado em tempo de execução!
        //*****************************************
        try {
            file = new File(root, "logs");
            file.delete();

        } catch (Exception e) {

        }

    }


    private boolean carregaDados(String tipotransacao) {
        boolean ret = true;

        //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

        //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(this);

        String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

        String urlBase = urlswebtrans.geturl(tipotransacao);

        urlswebtrans.close();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // buscar em parametros!!!!!
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        //nvps.add(new BasicNameValuePair("cliente", codMunicipio ));

        //nvps.add(new BasicNameValuePair("user", usuarioWebTrans));

        //nvps.add(new BasicNameValuePair("password", senhaWebTrans)); // "E10ADC3949BA59ABBE56E057F20F883E"

        //nvps.add(new BasicNameValuePair("dataSolicitacao", Utilitarios.getDataHora(4)));

        //***********************************************************************
        // TESTE DE CRIPTOGRAFIA - 10.04.2012
        //***********************************************************************
        urlBase += "?cliente=" + codMunicipio + "&user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4);

        //for ( int nx = 0 ; nx < nvps.size() ; nx++ )
        //{
        //	 parBase += nvps.get(nx).toString() + "&";
        //}
	
		 /*
		 try {
			urlBase = URLEncoder.encode(urlBase, "ISO-8859-1");
		} catch (UnsupportedEncodingException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		*/
        //parBase = parBase.substring(0, parBase.length()-1);

        nvps.add(new BasicNameValuePair("checkSum", MD5Util.criptografar(urlBase)));


        try {
            urlBase = SimpleCrypto.encrypt(info, urlBase);
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
		
		/*
		String urlteste = "" ;
		
		try {
			urlteste = SimpleCrypto.decrypt(info,urlBase);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		*/
        nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));


        //*****************************************************************************
        //.multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
        //*****************************************************************************


        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {

            ret = false;
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            //HttpResponse response = httpclient.execute(httpget);
            HttpResponse response = httpclient.execute(post);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            String retornoweb = EntityUtils.toString(response.getEntity());


            if (statusCode == 200) {

                try {

                    try {

                        retornoweb = SimpleCrypto.decrypt(Utilitarios.getInfo(), retornoweb);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (tipotransacao.equals("clientes")) retornoweb = "[" + retornoweb + "]";

                    jsonArray = new JSONArray(retornoweb);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = false;
                    e.printStackTrace();
                }

            } else {

                //******************************
                // 18.05.2012
                // grava resposta do WEBTRANS
                //******************************
                String mensz = "Sinc.Tabelas Retorno: " +
                        String.format("%d", statusCode) +
                        " - " +
                        retornoweb;

                //Utilitarios.gravaLog(mensz, context);

                ret = false;
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = false;
        }


        return ret;
    }

    private boolean trataAgentes() {
        // TODO Auto-generated method stub

        boolean ret = true;

        // Pega dados antes de excluir todas os registros
        ParametroDAO paDao = new ParametroDAO(this);

        Cursor c = paDao.getParametros();

        // dados para comunicação com o WebTrans
        // pega novamente aqui pois quando a classe é chamada com parâmetro "2" não carrega os equipamentos
        try {
            //********************************************
            // 09.08.2012
            //
            // Define usuario fixo para leitura de Tabelas
            //
            //********************************************

            senhaWebTrans = MD5Util.criptografar("2015RES");

            //usuarioWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuariowebtrans")));
            //senhaWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhawebtrans")));

            String xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(), c.getString(c.getColumnIndex("orgaoautuador")));
            codMunicipio = xcod.subSequence(1, 5).toString();  //265810

            usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;
        } catch (Exception ex) {

        }


        //codMunicipio = c.getString(c.getColumnIndex("orgaoautuador")).subSequence(1, 5).toString();  //265810

        String c1, c2, c3;
        c1 = usuarioWebTrans;
        c2 = senhaWebTrans;
        c3 = codMunicipio;

        c.close();

        paDao.close();


        // tenta decodificar todos o array primeiro

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Agente agente = new Agente();

            try {

                json1 = jsonArray.getJSONObject(nx);

                agente.setCodigo(json1.getString("matricula"));
                agente.setNome(json1.getString("nome"));
                agente.setSenha(json1.getString("passoword"));
                agente.setLogin(json1.getString("user"));

                // ativo ?
                agente.setAtivo("S");

                if (json1.getString("status").toUpperCase().contains("INATIVO"))
                    agente.setAtivo("N");


            } catch (JSONException e) {

                // TODO Auto-generated catch block
                ret = false;

                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }
        }

        if (ret) {
            AgenteDAO agentedao = new AgenteDAO(this);

            // limpa tabela de agente
            agentedao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Agente agente = new Agente();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    try {

                        //***************
                        // 29.06.2012
                        //***************
                        agente.setCodigo(SimpleCrypto.encrypt(info, json1.getString("matricula")));
                        agente.setNome(SimpleCrypto.encrypt(info, json1.getString("nome")));

                        // senha ja esta em MD5
                        agente.setSenha(json1.getString("passoword"));

                        agente.setLogin(SimpleCrypto.encrypt(info, json1.getString("user")));

                        // ativo ?
                        agente.setAtivo(SimpleCrypto.encrypt(info, "S"));
                        if (json1.getString("status").contains("INATIVO"))
                            agente.setAtivo(SimpleCrypto.encrypt(info, "N"));


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    agentedao.insere(agente);


                } catch (JSONException e) {

                    // TODO Auto-generated catch block
                    ret = false;
                    //errofatal =true;
                    e.printStackTrace();
                }
            }

            agentedao.close();
        }
        return ret;
    }


    //***************************************
    //Carrega tabela "TIPO"
    //***************************************
    private boolean trataTipos() {
        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Tipo tipo = new Tipo();

            try {

                json1 = jsonArray.getJSONObject(nx);

                if (json1.getString("cod_prodesp").length() < 2) {
                    tipo.setCodigo("0" + json1.getString("cod_prodesp"));
                } else {
                    tipo.setCodigo(json1.getString("cod_prodesp"));
                }

                tipo.setDescricao(json1.getString("nomeTipo"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //if ( tipoperacao.contains("1") ) errofatal =true;
                ret = false;
                e.printStackTrace();
            }

        }

        if (ret) {

            TipoDAO tipodao = new TipoDAO(this);

            // limpa tabela
            tipodao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Tipo tipo = new Tipo();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    if (json1.getString("cod_prodesp").length() < 2) {
                        tipo.setCodigo("0" + json1.getString("cod_prodesp"));
                    } else {
                        tipo.setCodigo(json1.getString("cod_prodesp"));
                    }

                    tipo.setDescricao(json1.getString("nomeTipo"));

                    tipodao.insere(tipo);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = false;
                    //errofatal =true;
                    e.printStackTrace();
                }

            }

            tipodao.close();
        }

        return ret;
    }


    //***************************************
    //Carrega tabela "ESPECIE"
    //***************************************
    private boolean trataEspecies() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Especie especie = new Especie();

            try {

                json1 = jsonArray.getJSONObject(nx);

                if (json1.getString("cod_prodesp").length() < 2) {
                    especie.setCodigo("0" + json1.getString("cod_prodesp"));
                } else {
                    especie.setCodigo(json1.getString("cod_prodesp"));
                }

                especie.setDescricao(json1.getString("nomeEspecie"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EspecieDAO especiedao = new EspecieDAO(this);

            // limpa tabela
            especiedao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Especie especie = new Especie();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    if (json1.getString("cod_prodesp").length() < 2) {
                        especie.setCodigo("0" + json1.getString("cod_prodesp"));
                    } else {
                        especie.setCodigo(json1.getString("cod_prodesp"));
                    }

                    especie.setDescricao(json1.getString("nomeEspecie"));

                    especiedao.insere(especie);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    //errofatal =true;
                }

            }

            especiedao.close();
        }

        return ret;
    }


    //***************************************
    //Carrega tabela "LOGRADOURO"
    //***************************************
    private boolean trataLogradouros() {
        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Logradouro logradouro = new Logradouro();

            try {

                json1 = jsonArray.getJSONObject(nx);

                logradouro.setCodigo(String.valueOf(json1.getString("idlogradouro")));
                logradouro.setDescricao(json1.getString("nomeLogradouro"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }


        }

        if (ret) {
            LogradouroDAO logradourodao = new LogradouroDAO(this);

            // limpa tabela
            logradourodao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Logradouro logradouro = new Logradouro();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    logradouro.setCodigo(String.valueOf(json1.getString("idlogradouro")));
                    logradouro.setDescricao(json1.getString("nomeLogradouro"));

                    logradourodao.insere(logradouro);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = false;
                    //errofatal = true ;
                    e.printStackTrace();
                }


            }

            logradourodao.close();
        }

        return ret;

    }


    //***************************************
    //Carrega tabela "ENQUADRAMENTO"
    //***************************************
    private boolean trataEnquadramentos() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Enquadramento enquadramento = new Enquadramento();

            try {

                json1 = jsonArray.getJSONObject(nx);

                enquadramento.setCodigo(json1.getString("cod_prodesp"));
                enquadramento.setDescricao(json1.getString("nomeEnquadramento"));


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(this);

            // limpa tabela
            enquadramentodao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Enquadramento enquadramento = new Enquadramento();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    if (json1.getLong("ufirs") > 0) {
                        enquadramento.setCodigo(json1.getString("cod_prodesp"));
                        enquadramento.setDescricao(json1.getString("nomeEnquadramento"));
                        enquadramentodao.insere(enquadramento);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    //errofatal = true ;
                }


            }

            enquadramentodao.close();
        }
        return ret;
    }


    //***************************************
    //Carrega tabela "PAIS"
    //***************************************
    private boolean trataPais() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Pais pais = new Pais();

            try {

                json1 = jsonArray.getJSONObject(nx);

                pais.setCodigo(json1.getString("cod_prodesp"));
                pais.setDescricao(json1.getString("nomePais"));


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }

        }

        if (ret) {
            PaisDAO paisdao = new PaisDAO(this);

            // limpa tabela
            paisdao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Pais pais = new Pais();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    pais.setCodigo(json1.getString("cod_prodesp"));
                    pais.setDescricao(json1.getString("nomePais"));

                    paisdao.insere(pais);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    //errofatal = true ;
                }


            }

            paisdao.close();
        }
        return ret;
    }


    //***************************************
    //Carrega tabela "MEDIDAADM"
    //***************************************
    private boolean trataMedidaAdm() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            MedidaAdm medidaadm = new MedidaAdm();

            try {

                json1 = jsonArray.getJSONObject(nx);

                medidaadm.setCodigo(json1.getString("idmedidaAdministrativa"));
                medidaadm.setDescricao(json1.getString("nomeMedidaAdministrativa"));


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                //if ( tipoperacao.contains("1") ) errofatal =true;
                e.printStackTrace();
            }

        }

        if (ret) {
            MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(this);

            // limpa tabela
            medidaadmdao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                MedidaAdm medidaadm = new MedidaAdm();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    medidaadm.setCodigo(json1.getString("idmedidaAdministrativa"));
                    medidaadm.setDescricao(json1.getString("nomeMedidaAdministrativa"));

                    medidaadmdao.insere(medidaadm);


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    //errofatal = true ;
                }


            }

            medidaadmdao.close();
        }
        return ret;
    }
}
