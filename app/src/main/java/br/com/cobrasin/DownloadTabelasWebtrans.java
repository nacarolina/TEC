package br.com.cobrasin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.MedidaAdm;
import br.com.cobrasin.tabela.Pais;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.tabela.Tipo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadTabelasWebtrans extends Activity {

    private boolean lok = false;
    private JSONObject json1;
    private String usuarioWebTrans, senhaWebTrans, codMunicipio;
    private boolean tentar = true;
    private ProgressDialog progress;
    private String info = Utilitarios.getInfo();
    private JSONArray jsonArray;
    private boolean errofatal = false;
    private String tipoperacao;
    private Handler handler = new Handler();
    private String modweb;
    private String imei;
    private String return_RET = "ok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.downloadwebtrans);

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Parametro param = new Parametro();
        ParametroDAO pardao = new ParametroDAO(DownloadTabelasWebtrans.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();
        try {
            modweb = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modweb")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Button btnBaixarTablesWebtrans = (Button) findViewById(R.id.btnBaixarSeqAit);
        btnBaixarTablesWebtrans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                long nr = 0;
                AitDAO a = new AitDAO(DownloadTabelasWebtrans.this);
                if (modweb.contains("TRUE"))
                    if (a.getListaCompleta().size() > 0) {
                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                DownloadTabelasWebtrans.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("Sequêncial de AIT");
                        aviso.setMessage("Não é possivel atualizar o sequencial com AITs pendentes de transmissão");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        return;
                    }
                InstalImei(imei);
                if (return_RET.equals("ok")) {
                    Parametro pa = new Parametro();
                    try {
                        JSONObject j = jsonArray.getJSONObject(0);
                        long faixaAitInicial = Long.parseLong(j
                                .getString("faixaAitInicial"));
                        long faixaAitFinal = Long.parseLong(j
                                .getString("faixaAitFinal"));

                        pa.setAitinicial(SimpleCrypto.encrypt(info,
                                String.format("%07d", faixaAitInicial)));
                        pa.setAitfinal(SimpleCrypto.encrypt(info,
                                String.format("%07d", faixaAitFinal)));

                        if (j.getString("ultimoAitDigitado").equals("null")) {
                            nr = (Integer.parseInt(j
                                    .getString("faixaAitInicial")));
                        } else {
                            nr = (Integer.parseInt(j
                                    .getString("ultimoAitDigitado")) + 1);
                        }

                        pa.setProximoait(SimpleCrypto.encrypt(info,
                                String.format("%07d", nr)));
                        pa.setSeriepda(SimpleCrypto.encrypt(info,
                                j.getString("numero")));
                        ParametroDAO p = new ParametroDAO(
                                DownloadTabelasWebtrans.this);
                        p.SetSequencialAIT(pa);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Sequêncial de AIT");
                    aviso.setMessage("Atualizado para: " + nr);
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                } else {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Atualização");
                    aviso.setMessage("Erro ao obter sequêncial!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaLogradouro = (Button) findViewById(R.id.btTabelaLogradouro);
        btTabelaLogradouro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                AlertDialog.Builder avi = new AlertDialog.Builder(
                        DownloadTabelasWebtrans.this);
                if (modweb.contains("TRUE")) {
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Logradouros?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("Logradouros",
                                            "logradouros");
                                }
                            });
                    avi.show();

                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaEnquadramento = (Button) findViewById(R.id.btTabelaEnquadramento);
        btTabelaEnquadramento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder avi = new AlertDialog.Builder(
                        DownloadTabelasWebtrans.this);
                avi.setIcon(android.R.drawable.ic_dialog_alert);
                if (modweb.contains("TRUE")) {
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Enquadramentos?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("Enquadramentos",
                                            "enquadramentos");
                                }
                            });
                    avi.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaTipos = (Button) findViewById(R.id.btTabelaTipos);
        btTabelaTipos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (modweb.contains("TRUE")) {
                    AlertDialog.Builder avi = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Tipos?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("Tipos", "tipos");
                                }
                            });
                    avi.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaAgente = (Button) findViewById(R.id.btTabelaAgente);
        btTabelaAgente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (modweb.contains("TRUE")) {
                    AlertDialog.Builder avi = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Agentes?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("Agentes",
                                            "agentes");
                                }
                            });
                    avi.show();

                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaPais = (Button) findViewById(R.id.btTabelaPais);
        btTabelaPais.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (modweb.contains("TRUE")) {
                    AlertDialog.Builder avi = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de País?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("País", "pais");
                                }
                            });
                    avi.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }

        });

        Button btTabelaMedAdm = (Button) findViewById(R.id.btTabelaMedAdm);
        btTabelaMedAdm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (modweb.contains("TRUE")) {
                    AlertDialog.Builder avi = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Medida Administrativa?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans(
                                            "Medida Administrativa",
                                            "medidaadm");
                                }
                            });
                    avi.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        Button btTabelaEspecies = (Button) findViewById(R.id.btTabelaEspecies);
        btTabelaEspecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (modweb.contains("TRUE")) {
                    AlertDialog.Builder avi = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    avi.setIcon(android.R.drawable.ic_dialog_alert);
                    avi.setTitle("Tabelas do WebTrans");
                    avi.setMessage("Deseja baixar tabela de Espécies?");
                    avi.setNeutralButton("Não", null);
                    avi.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DownloadTabelasWebtrans("Espécies",
                                            "especies");
                                }
                            });
                    avi.show();
                }
                if (modweb.contains("FALSE")) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            DownloadTabelasWebtrans.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Tabelas do WebTrans");
                    aviso.setMessage("Você está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }
            }
        });

        TextView lblTotalLogradouro = (TextView) findViewById(R.id.lblTotalLogradouro);
        TextView lblTotalEnquadramento = (TextView) findViewById(R.id.lblTotalEnquadramento);
        TextView lblTotalTipos = (TextView) findViewById(R.id.lblTotalTipos);
        TextView lblTotalAgentes = (TextView) findViewById(R.id.lblTotalAgentes);
        TextView lblTotalPais = (TextView) findViewById(R.id.lblTotalPais);
        TextView lblTotalMdsAdm = (TextView) findViewById(R.id.lblTotalMdsAdm);
        TextView lblTotalEspecies = (TextView) findViewById(R.id.lblSelecione);

        LogradouroDAO ldao = new LogradouroDAO(DownloadTabelasWebtrans.this);
        String quantidadeLogradouro = ldao.ObtemQuantidadeLogradouro();
        lblTotalLogradouro.setText("Logradouros " + quantidadeLogradouro
                + " registros");
        ldao.close();

        EnquadramentoDAO enqdao = new EnquadramentoDAO(
                DownloadTabelasWebtrans.this);
        String quantidadeEnquadramento = enqdao.ObtemQuantidadeEnquadramento();
        lblTotalEnquadramento.setText("Enquadramentos "
                + quantidadeEnquadramento + " registros");
        enqdao.close();

        TipoDAO Tipodao = new TipoDAO(DownloadTabelasWebtrans.this);
        String quantidadeTipos = Tipodao.ObtemQuantidadeTipo();
        lblTotalTipos.setText("Tipos " + quantidadeTipos + " registros");
        Tipodao.close();

        AgenteDAO agdao = new AgenteDAO(DownloadTabelasWebtrans.this);
        String quantidadeAgente = agdao.ObtemQuantidadeAgente();
        lblTotalAgentes.setText("Agentes " + quantidadeAgente + " registros");
        agdao.close();

        PaisDAO paisdao = new PaisDAO(DownloadTabelasWebtrans.this);
        String quantidadePais = paisdao.ObtemQuantidadePais();
        lblTotalPais.setText("País " + quantidadePais + " registros");
        paisdao.close();

        MedidaAdmDAO medidadao = new MedidaAdmDAO(DownloadTabelasWebtrans.this);
        String quantidadeMedida = medidadao.ObtemQuantidadeMedidaAmd();
        lblTotalMdsAdm.setText("Medidas Administrativas " + quantidadeMedida
                + " registros");
        medidadao.close();

        EspecieDAO especiedao = new EspecieDAO(DownloadTabelasWebtrans.this);
        String quantidadeEspecies = especiedao.ObtemQuantidadeEspecie();
        lblTotalEspecies.setText("Espécies " + quantidadeEspecies
                + " registros");
        especiedao.close();
    }

    private void MostraMensagem(final String mensagem) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder aviso1 = new AlertDialog.Builder(
                        DownloadTabelasWebtrans.this);
                aviso1.setIcon(android.R.drawable.ic_dialog_alert);
                aviso1.setTitle("Transmissão");
                aviso1.setMessage(mensagem);
                aviso1.setPositiveButton("OK", null);
                aviso1.show();

            }
        });
    }

    public void DownloadTabelasWebtrans(final String NomeTabela,
                                        final String NomeTabelaDownload) {
        progress = ProgressDialog.show(DownloadTabelasWebtrans.this,
                "Aguarde...", "Baixando tabela de " + NomeTabela + "...", true,
                true);
        new Thread(new Runnable() {

            @Override
            public void run() {

                // final SincronismoWebTrans sw = new
                // SincronismoWebTrans(DownloadTabelasWebtrans.this,"3");
                trataEquipamentos();
                if (carregaDados(NomeTabelaDownload)) {
                    if (NomeTabela.equals("Espécies")) {
                        lok = trataEspecies();
                    }
                    if (NomeTabela.equals("Tipos")) {
                        lok = trataTipos();
                    }
                    if (NomeTabela.equals("Enquadramentos")) {
                        lok = trataEnquadramentos();
                    }
                    if (NomeTabela.equals("Logradouros")) {
                        lok = trataLogradouros();
                    }
                    if (NomeTabela.equals("Agentes")) {
                        lok = trataAgentes();
                    }
                    if (NomeTabela.equals("País")) {
                        lok = trataPais();
                    }
                    if (NomeTabela.equals("Medida Administrativa")) {
                        lok = trataMedidaAdm();
                    }

                    MostraMensagem("Tabela de " + NomeTabela
                            + " baixado com sucesso!");
                    progress.dismiss();
                } else {
                    while (tentar == true) {
                        if (carregaDados(NomeTabelaDownload)) {
                            if (NomeTabela.equals("Espécies")) {
                                lok = trataEspecies();
                            }
                            if (NomeTabela.equals("Tipos")) {
                                lok = trataTipos();
                            }
                            if (NomeTabela.equals("Enquadramentos")) {
                                lok = trataEnquadramentos();
                            }
                            if (NomeTabela.equals("Logradouros")) {
                                lok = trataLogradouros();
                            }
                            if (NomeTabela.equals("Agentes")) {
                                lok = trataAgentes();
                            }
                            if (NomeTabela.equals("País")) {
                                lok = trataPais();
                            }
                            if (NomeTabela.equals("Medida Administrativa")) {
                                lok = trataMedidaAdm();
                            }
                            tentar = false;
                            MostraMensagem("Tabela de " + NomeTabela
                                    + " baixado com sucesso!");
                            progress.dismiss();
                        } else {

                            tentar = false;
                            MostraMensagem("Falha ao baixar Tabela de "
                                    + NomeTabela + "!");
                            progress.dismiss();

                        }
                    }
                }
            }
        }).start();
        // }
        // });
        // avi.show();
    }

    private boolean carregaDados(String tipotransacao) {
        boolean ret = true;
        ParametroDAO paDao = new ParametroDAO(DownloadTabelasWebtrans.this);

        Cursor c = paDao.getParametros();
        senhaWebTrans = MD5Util.criptografar("2015RES");
        String xcod = "";
        try {
            xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(),
                    c.getString(c.getColumnIndex("orgaoautuador")));
        } catch (Exception e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        codMunicipio = xcod;// .subSequence(1, 5).toString(); //265810
        usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;
        // String url =
        // "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

        // url =
        // "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(
                DownloadTabelasWebtrans.this);

        String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");

        String urlBase = urlswebtrans.geturl(tipotransacao).replace(":8080", "");


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // buscar em parametros!!!!!
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        // nvps.add(new BasicNameValuePair("cliente", codMunicipio ));

        // nvps.add(new BasicNameValuePair("user", usuarioWebTrans));

        // nvps.add(new BasicNameValuePair("password", senhaWebTrans)); //
        // "E10ADC3949BA59ABBE56E057F20F883E"

        // nvps.add(new BasicNameValuePair("dataSolicitacao",
        // Utilitarios.getDataHora(4)));

        // ***********************************************************************
        // TESTE DE CRIPTOGRAFIA - 10.04.2012
        // ***********************************************************************
        urlBase += "?cliente=" + codMunicipio + "&user=" + usuarioWebTrans
                + "&password=" + senhaWebTrans + "&dataSolicitacao="
                + Utilitarios.getDataHora(4);

        // for ( int nx = 0 ; nx < nvps.size() ; nx++ )
        // {
        // parBase += nvps.get(nx).toString() + "&";
        // }

        /*
         * try { urlBase = URLEncoder.encode(urlBase, "ISO-8859-1"); } catch
         * (UnsupportedEncodingException e3) { // TODO Auto-generated catch
         * block e3.printStackTrace(); }
         */
        // parBase = parBase.substring(0, parBase.length()-1);

        nvps.add(new BasicNameValuePair("checkSum", MD5Util
                .criptografar(urlBase)));

        try {
            urlBase = SimpleCrypto.encrypt(info, urlBase);
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        /*
         * String urlteste = "" ;
         *
         * try { urlteste = SimpleCrypto.decrypt(info,urlBase); } catch
         * (Exception e2) { // TODO Auto-generated catch block
         * e2.printStackTrace(); }
         */
        nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));

        // *****************************************************************************
        // .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
        // *****************************************************************************

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {

            ret = false;
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            // HttpResponse response = httpclient.execute(httpget);
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

                        ret = false;
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

                        retornoweb = SimpleCrypto.decrypt(
                                Utilitarios.getInfo(), retornoweb);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (tipotransacao.equals("clientes"))
                        retornoweb = "[" + retornoweb + "]";

                    jsonArray = new JSONArray(retornoweb);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = false;
                    e.printStackTrace();
                }

            } else {

                // ******************************
                // 18.05.2012
                // grava resposta do WEBTRANS
                // ******************************
                String mensz = "Sinc.Tabelas Retorno: "
                        + String.format("%d", statusCode) + " - " + retornoweb;

                Utilitarios.gravaLog(mensz, DownloadTabelasWebtrans.this);

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

        urlswebtrans.close();
        return ret;
    }

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
                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EspecieDAO especiedao = new EspecieDAO(DownloadTabelasWebtrans.this);

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
                    errofatal = true;
                }

            }

            especiedao.close();
        }

        return ret;
    }

    private boolean trataLogradouros() {
        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Logradouro logradouro = new Logradouro();

            try {

                json1 = jsonArray.getJSONObject(nx);

                logradouro.setCodigo(String.valueOf(json1
                        .getString("idlogradouro")));
                logradouro.setDescricao(json1.getString("nomeLogradouro"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            LogradouroDAO logradourodao = new LogradouroDAO(
                    DownloadTabelasWebtrans.this);

            // limpa tabela
            logradourodao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Logradouro logradouro = new Logradouro();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    logradouro.setCodigo(String.valueOf(json1
                            .getString("idlogradouro")));
                    logradouro.setDescricao(json1.getString("nomeLogradouro"));

                    logradourodao.insere(logradouro);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = false;
                    errofatal = true;
                    e.printStackTrace();
                }

            }

            logradourodao.close();
        }

        return ret;

    }

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
                if (tipoperacao.contains("1"))
                    errofatal = true;
                ret = false;
                e.printStackTrace();
            }

        }

        if (ret) {

            TipoDAO tipodao = new TipoDAO(DownloadTabelasWebtrans.this);

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
                    errofatal = true;
                    e.printStackTrace();
                }

            }

            tipodao.close();
        }

        return ret;
    }

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
                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            PaisDAO paisdao = new PaisDAO(DownloadTabelasWebtrans.this);

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
                    errofatal = true;
                }

            }

            paisdao.close();
        }
        return ret;
    }

    private boolean trataEnquadramentos() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            Enquadramento enquadramento = new Enquadramento();

            try {

                json1 = jsonArray.getJSONObject(nx);
                if (json1.getString("competencia").contains("Municipal")) {

                    enquadramento.setCodigo(json1.getString("cod_prodesp"));
                    enquadramento.setDescricao(json1
                            .getString("nomeEnquadramento"));
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(
                    DownloadTabelasWebtrans.this);

            // limpa tabela
            enquadramentodao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Enquadramento enquadramento = new Enquadramento();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    if (json1.getLong("ufirs") > 0) {
                        if (json1.getString("cod_prodesp").equals("70302")) {
                            nx++;
                        }
                        enquadramento.setCodigo(json1.getString("cod_prodesp"));
                        enquadramento.setDescricao(json1
                                .getString("nomeEnquadramento"));
                        enquadramentodao.insere(enquadramento);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    errofatal = true;
                }

            }

            enquadramentodao.close();
        }
        return ret;
    }

    public boolean trataEquipamentos() {

        boolean ret = true;

        // Pega dados antes de excluir todas os registros
        ParametroDAO paDao = new ParametroDAO(DownloadTabelasWebtrans.this);

        Context ctx = DownloadTabelasWebtrans.this;

        Cursor c = paDao.getParametros();

        // dados para comunicação com servidor ftp

        String servidorftp = "";
        String usuarioftp = "";
        String senhaftp = "";
        String arquivobaseftp = "";
        String impressora = "";
        String imprimeobs = "";
        String sigla = "";
        String orgaoautuador = "";
        String cxseriePDA = "";

        try {

            /*
             * servidorftp =
             * SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString
             * (c.getColumnIndex("servidorftp"))); usuarioftp =
             * SimpleCrypto.decrypt
             * (Utilitarios.getInfo(),c.getString(c.getColumnIndex
             * ("usuarioftp"))); senhaftp =
             * SimpleCrypto.decrypt(Utilitarios.getInfo
             * (),c.getString(c.getColumnIndex("senhaftp"))); arquivobaseftp =
             * SimpleCrypto
             * .decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex
             * ("arquivobaseftp"))); impressora =
             * SimpleCrypto.decrypt(Utilitarios
             * .getInfo(),c.getString(c.getColumnIndex("impressora")));
             * imprimeobs =
             * SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString
             * (c.getColumnIndex("imprimeobs")));
             *
             * sigla = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.
             * getColumnIndex("sigla")));
             */
            // dados para comunicação com o WebTrans
            // usuarioWebTrans =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuariowebtrans")));
            // senhaWebTrans =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhawebtrans")));

            // ******************************************************
            // 09.08.2012
            //
            // Define Usuario/Senha para leitura de Tabelas
            // ******************************************************
            senhaWebTrans = MD5Util.criptografar("cobratalonario");

            String xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(),
                    c.getString(c.getColumnIndex("orgaoautuador")));

            codMunicipio = xcod;// .subSequence(1, 5).toString(); //265810

            usuarioWebTrans = "talonario"; // + codMunicipio;

            // codMunicipio =
            // c.getString(c.getColumnIndex("orgaoautuador")).subSequence(1,
            // 5).toString(); //265810

            // cxseriePDA =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("seriepda")));

            // 16.05.2012
            // Retirar , vira do Webtrans
            // orgaoautuador =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("orgaoautuador")));

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String c1, c2, c3;
        c1 = usuarioWebTrans;
        c2 = senhaWebTrans;
        c3 = codMunicipio;

        c.close();


        InstalImei(imei);
        if (return_RET.equals("ok")) {

            // String orgaoautuador = "",
            String prefeitura = "";

            // salva
            JSONArray jsonArray1 = jsonArray;

            // Pega dados do Cliente
            if (carregaDados("clientes")) {

                String clienteativo = "N";

                for (int nx = 0; nx < jsonArray.length(); nx++) {
                    try {

                        json1 = jsonArray.getJSONObject(nx);
                        // antigo
                        // orgaoautuador =
                        // json1.getString("orgao_transito").subSequence(0,
                        // 6).toString();

                        // atual , ainda nao foi feito o deploy
                        orgaoautuador = "2"
                                + json1.getString("orgaoAutuador").toString()
                                + "0";
                        prefeitura = json1.getString("prefixo");
                        sigla = json1.getString("sigla");

                        clienteativo = "S";
                        // Prefeitura Ativa ?
                        if (json1.getString("status").toUpperCase()
                                .contains("INATIVO"))
                            clienteativo = "N";

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        ret = false;
                        errofatal = true;
                        e.printStackTrace();
                    }
                }

                // limpa tabela quando está inicializando o PDA
                // if ( tipx.contains("1")) definedao.delete();

                // definedao.delete();
                if (jsonArray1 != null) {

                    for (int nx = 0; nx < jsonArray1.length(); nx++) {

                        Parametro define = new Parametro();

                        try {

                            // nao é necessário validar o município pois no
                            // baseandroid.zip
                            // deve ser colocado o municipio correto de operação
                            //
                            // conferir somente a SERIE do PDA

                            json1 = jsonArray1.getJSONObject(nx);

                            long faixaAitInicial = Long.parseLong(json1
                                    .getString("faixaAitInicial"));
                            long faixaAitFinal = Long.parseLong(json1
                                    .getString("faixaAitFinal"));

                            // debug - - retirar
                            // define.setProximoait("0000525");

                            // define.setProximoait(String.valueOf(faixaAitInicial));
                            // define.setAitinicial(String.valueOf(faixaAitInicial));
                            // define.setAitfinal(String.valueOf(faixaAitFinal));

                            // somente define proximo ait na inicialização...
                            try {

                                define.setProximoait(SimpleCrypto.encrypt(info,
                                        String.format("%07d", faixaAitInicial)));
                                define.setAitinicial(SimpleCrypto.encrypt(info,
                                        String.format("%07d", faixaAitFinal)));
                                define.setAitfinal(SimpleCrypto.encrypt(info,
                                        String.format("%07d", faixaAitFinal)));

                                define.setSeriepda(SimpleCrypto.encrypt(info,
                                        json1.getString("numero")));
                                define.setPrefeitura(SimpleCrypto.encrypt(info,
                                        prefeitura));
                                define.setSigla(SimpleCrypto.encrypt(info, sigla));
                                define.setOrgaoautuador(SimpleCrypto.encrypt(info,
                                        orgaoautuador));
                                define.setSerieait(SimpleCrypto.encrypt(info,
                                        json1.getString("serieAit")));

                                define.setServidorftp(SimpleCrypto.encrypt(info,
                                        servidorftp));
                                define.setUsuarioftp(SimpleCrypto.encrypt(info,
                                        usuarioftp));
                                define.setSenhaftp(SimpleCrypto.encrypt(info,
                                        senhaftp));
                                define.setArquivobaseftp(SimpleCrypto.encrypt(info,
                                        arquivobaseftp));

                                define.setImpressoraMAC(SimpleCrypto.encrypt(info,
                                        impressora));
                                define.setImprimeobs(SimpleCrypto.encrypt(info,
                                        imprimeobs));

                                // para acessos futuros
                                define.setUsuariowebtrans(SimpleCrypto.encrypt(
                                        info, usuarioWebTrans));
                                define.setSenhawebtrans(SimpleCrypto.encrypt(info,
                                        senhaWebTrans));

                                // id nao é encriptado
                                define.setIdwebtrans(Long.toString(json1
                                        .getLong("idequipamento")));

                                // PDA Ativo ?
                                define.setAtivo(SimpleCrypto.encrypt(info, "S"));
                                if (json1.getString("status").toUpperCase()
                                        .contains("INATIVO")) {
                                    define.setAtivo(SimpleCrypto.encrypt(info, "N"));
                                }

                                // Prefeitura Ativa ?
                                define.setPrefativa(SimpleCrypto.encrypt(info, "N"));
                                if (clienteativo.equals("S"))
                                    define.setPrefativa(SimpleCrypto.encrypt(info,
                                            "S"));

                                // if ( tipx.contains("1"))
                                paDao.atualizastatus(define);
                                paDao.iniciapda(define);
                                // else
                                // Atualiza o Status de Ativo/Desativo
                                // definedao.atualizastatus(define);
                            } catch (Exception ex) {

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            ret = false;
                            errofatal = true;
                        }

                    }
                }

            }

            paDao.close();

            buscaPDA(cxseriePDA);

            // criptografa definePDA...

        } else {
            errofatal = true;
            ret = false;
        }

        return ret;

    }

    public boolean trataAgentes() {
        // TODO Auto-generated method stub

        boolean ret = true;

        // Pega dados antes de excluir todas os registros
        ParametroDAO paDao = new ParametroDAO(DownloadTabelasWebtrans.this);

        Cursor c = paDao.getParametros();

        // dados para comunicação com o WebTrans
        // pega novamente aqui pois quando a classe é chamada com parâmetro "2"
        // não carrega os equipamentos
        try {
            // ********************************************
            // 09.08.2012
            //
            // Define usuario fixo para leitura de Tabelas
            //
            // ********************************************

            senhaWebTrans = MD5Util.criptografar("2015RES");

            // usuarioWebTrans =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuariowebtrans")));
            // senhaWebTrans =
            // SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhawebtrans")));

            String xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(),
                    c.getString(c.getColumnIndex("orgaoautuador")));
            codMunicipio = xcod;// .subSequence(1, 5).toString(); //265810

            usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;
        } catch (Exception ex) {

        }

        // codMunicipio =
        // c.getString(c.getColumnIndex("orgaoautuador")).subSequence(1,
        // 5).toString(); //265810

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

                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }
        }

        if (ret) {
            AgenteDAO agentedao = new AgenteDAO(DownloadTabelasWebtrans.this);

            // limpa tabela de agente
            agentedao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                Agente agente = new Agente();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    try {

                        // ***************
                        // 29.06.2012
                        // ***************
                        agente.setCodigo(SimpleCrypto.encrypt(info,
                                json1.getString("matricula")));
                        agente.setNome(SimpleCrypto.encrypt(info,
                                json1.getString("nome")));

                        // senha ja esta em MD5
                        agente.setSenha(json1.getString("passoword"));

                        agente.setLogin(SimpleCrypto.encrypt(info,
                                json1.getString("user")));

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
                    errofatal = true;
                    e.printStackTrace();
                }
            }

            agentedao.close();
        }
        return ret;
    }

    private boolean trataMedidaAdm() {

        boolean ret = true;

        for (int nx = 0; nx < jsonArray.length(); nx++) {

            MedidaAdm medidaadm = new MedidaAdm();

            try {

                json1 = jsonArray.getJSONObject(nx);

                medidaadm.setCodigo(json1.getString("idmedidaAdministrativa"));
                medidaadm.setDescricao(json1
                        .getString("nomeMedidaAdministrativa"));

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ret = false;
                if (tipoperacao.contains("1"))
                    errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(
                    DownloadTabelasWebtrans.this);

            // limpa tabela
            medidaadmdao.delete();

            for (int nx = 0; nx < jsonArray.length(); nx++) {

                MedidaAdm medidaadm = new MedidaAdm();

                try {

                    json1 = jsonArray.getJSONObject(nx);

                    medidaadm.setCodigo(json1
                            .getString("idmedidaAdministrativa"));
                    medidaadm.setDescricao(json1
                            .getString("nomeMedidaAdministrativa"));

                    medidaadmdao.insere(medidaadm);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = false;
                    errofatal = true;
                }

            }

            medidaadmdao.close();
        }
        return ret;
    }

    private void buscaPDA(String xpda) {

        xpda = xpda.toUpperCase();

        ParametroDAO paDao = new ParametroDAO(DownloadTabelasWebtrans.this);

        // le todos os pdas do orgao autuador
        Cursor cx = paDao.getParametros();

        paDao.close();

        cx.moveToFirst();

        ParametroDAO pardao = new ParametroDAO(DownloadTabelasWebtrans.this);

        while (cx.isAfterLast() == false) {

            // achou pda
            try {

                String xserie = SimpleCrypto.decrypt(info,
                        cx.getString(cx.getColumnIndex("seriepda")));

                if (xserie.contains(xpda)) {

                    Parametro parx = new Parametro();

                    parx.setSeriepda(SimpleCrypto.decrypt(info,
                            cx.getString(cx.getColumnIndex("seriepda"))));
                    parx.setAtivo(SimpleCrypto.decrypt(info,
                            cx.getString(cx.getColumnIndex("ativo"))));
                    parx.setPrefativa(SimpleCrypto.decrypt(info,
                            cx.getString(cx.getColumnIndex("prefativa"))));
                    parx.setAitinicial(SimpleCrypto.decrypt(info,
                            cx.getString(cx.getColumnIndex("aitinicial"))));
                    parx.setAitfinal(SimpleCrypto.decrypt(info,
                            cx.getString(cx.getColumnIndex("aitfinal"))));

                    // atualiza parametro
                    pardao.atualizastatus(parx);
                    pardao.close();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            cx.moveToNext();
        }
        cx.close();

        pardao.close();

    }

    public void InstalImei(final String IMEI) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String ret = "ok";

                senhaWebTrans = MD5Util.criptografar("cobratalonario");
                usuarioWebTrans = "talonario";
                UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(
                        DownloadTabelasWebtrans.this);
                String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");
                String urlBase = urlswebtrans.geturl("imei");
                urlswebtrans.close();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                // ***********************************************************************
                // TESTE DE CRIPTOGRAFIA - 10.04.2012
                // ***********************************************************************
                urlBase += "?user=" + usuarioWebTrans + "&password=" + senhaWebTrans
                        + "&dataSolicitacao=" + Utilitarios.getDataHora(4) + "&imei="
                        + IMEI;
                nvps.add(new BasicNameValuePair("checkSum", MD5Util
                        .criptografar(urlBase)));

                try {
                    urlBase = SimpleCrypto.encrypt(info, urlBase);
                } catch (Exception e2) {
                    ret = "erro";
                    e2.printStackTrace();
                }

                nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));
                // *****************************************************************************
                // .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
                // *****************************************************************************

                try {
                    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                } catch (UnsupportedEncodingException e1) {
                    ret = "erro";
                }

                try {
                    HttpParams httpParameters = new BasicHttpParams();
                    int timeoutConnection = 20000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters,
                            timeoutConnection);
                    int timeoutSocket = 20000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
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
                                ret = "erro";
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
                                retornoweb = SimpleCrypto.decrypt(
                                        Utilitarios.getInfo(), retornoweb);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ret = "erro";
                            }
                            jsonArray = new JSONArray(retornoweb);
                        } catch (JSONException e) {
                            ret = "erro";
                        }
                    } else {
                        ret = "erro";
                    }

                } catch (ClientProtocolException e) {
                    ret = "erro";
                } catch (IOException e) {
                    ret = "erro";
                }

                return_RET = ret;

            }

        }).start();

    }
}
