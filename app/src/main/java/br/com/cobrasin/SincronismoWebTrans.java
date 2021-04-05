package br.com.cobrasin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Especie;
import br.com.cobrasin.tabela.Fabricante;
import br.com.cobrasin.tabela.Logradouro;
import br.com.cobrasin.tabela.MedidaAdm;
import br.com.cobrasin.tabela.Municipio;
import br.com.cobrasin.tabela.Pais;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.tabela.Tipo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;

public class SincronismoWebTrans {

    private Context context;
    private ProgressDialog progress;
    private JSONObject json1;
    private JSONArray jsonArray;
    private String usuarioWebTrans, senhaWebTrans, codMunicipio;
    private String mensErro = "Ok...";
    private boolean errofatal = false;
    private String tipoperacao;
    private String info = Utilitarios.getInfo();
    private boolean lok = false;
    private boolean tentar = true;
    private Handler handler = new Handler();
    private String IMEI;


    public SincronismoWebTrans(Context context, String tipox) {

        this.context = context;
        tipoperacao = tipox;


    }

    public boolean carregaDados(String tipotransacao) {
        boolean ret = true;


        //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

        //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

        String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

        String urlBase = urlswebtrans.geturl(tipotransacao);

        urlswebtrans.close();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // buscar em parametros!!!!!
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();


        //***********************************************************************
        // TESTE DE CRIPTOGRAFIA - 10.04.2012
        //***********************************************************************
        urlBase += "?cliente=" + codMunicipio + "&user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4);


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

                    if (tipotransacao.equals("clientes")) {
                        retornoweb = "[" + retornoweb + "]";
                    }

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
                informUsr(mensz);
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

    public String InstalImei() {
        String ret = "ok";


        //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

        //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

        senhaWebTrans = MD5Util.criptografar("cobratalonario");
        usuarioWebTrans = "talonario";

        //trataEquipamentos("3");

        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

        String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

        String urlBase = urlswebtrans.geturl("imei");

        urlswebtrans.close();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // buscar em parametros!!!!!
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();


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


    public boolean trataEspecies() {

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
                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EspecieDAO especiedao = new EspecieDAO(context);

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

    public boolean trataLogradouros() {
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
                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }


        }

        if (ret) {
            LogradouroDAO logradourodao = new LogradouroDAO(context);

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
                    errofatal = true;
                    e.printStackTrace();
                }


            }

            logradourodao.close();
        }

        return ret;

    }

    public boolean trataTipos() {
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
                if (tipoperacao.contains("1")) errofatal = true;
                ret = false;
                e.printStackTrace();
            }

        }

        if (ret) {

            TipoDAO tipodao = new TipoDAO(context);

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

    public boolean trataPais() {

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
                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            PaisDAO paisdao = new PaisDAO(context);

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

    public boolean trataEnquadramentos() {

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
                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(context);

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
                    errofatal = true;
                }


            }

            enquadramentodao.close();
        }
        return ret;
    }

    public boolean trataEquipamentos(String tipx) {

        boolean ret = true;

        // Pega dados antes de excluir todas os registros
        ParametroDAO paDao = new ParametroDAO(context);

        Context ctx = context;

        Cursor c = paDao.getParametros();

        //  dados para comunicação com servidor ftp

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

			/*servidorftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("servidorftp")));
			usuarioftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuarioftp")));
	    	senhaftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhaftp")));
	    	arquivobaseftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("arquivobaseftp")));
	    	impressora = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("impressora")));
	    	imprimeobs = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("imprimeobs")));

	    	sigla = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("sigla")));
	    		*/
            // dados para comunicação com o WebTrans
            //usuarioWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuariowebtrans")));
            //senhaWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhawebtrans")));

            //******************************************************
            // 09.08.2012
            //
            // Define Usuario/Senha para leitura de Tabelas
            //******************************************************
            senhaWebTrans = MD5Util.criptografar("cobratalonario");

            String xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(), c.getString(c.getColumnIndex("orgaoautuador")));

            codMunicipio = xcod.subSequence(1, 5).toString();  //265810

            usuarioWebTrans = "talonario"; //+ codMunicipio;

            //codMunicipio = c.getString(c.getColumnIndex("orgaoautuador")).subSequence(1, 5).toString();  //265810

            //cxseriePDA = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("seriepda")));

            // 16.05.2012
            // Retirar , vira do Webtrans
            //orgaoautuador = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("orgaoautuador")));


        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String c1, c2, c3;
        c1 = usuarioWebTrans;
        c2 = senhaWebTrans;
        c3 = codMunicipio;

        c.close();

        if (InstalImei() == "ok") {
            // salva
            JSONArray jsonArray1 = jsonArray;

            //String orgaoautuador = "",
            String prefeitura = "";


            // Pega dados do Cliente
            if (carregaDados("clientes")) {

                String clienteativo = "N";


                for (int nx = 0; nx < jsonArray.length(); nx++) {
                    try {


                        json1 = jsonArray.getJSONObject(nx);
                        // antigo
                        //orgaoautuador = json1.getString("orgao_transito").subSequence(0, 6).toString();

                        // atual , ainda nao foi feito o deploy
                        orgaoautuador = "2" + json1.getString("orgaoAutuador").toString() + "0";
                        prefeitura = json1.getString("prefixo");
                        sigla = json1.getString("sigla");

                        clienteativo = "S";
                        // Prefeitura Ativa ?
                        if (json1.getString("status").toUpperCase().contains("INATIVO"))
                            clienteativo = "N";

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        ret = false;
                        errofatal = true;
                        e.printStackTrace();
                    }
                }

                // limpa tabela quando está inicializando o PDA
                //if ( tipx.contains("1")) definedao.delete();

                //definedao.delete();

                for (int nx = 0; nx < jsonArray1.length(); nx++) {

                    Parametro define = new Parametro();

                    try {

                        // nao é necessario validar o município pois no baseandroid.zip
                        // deve ser colocado o municipio correto de operação
                        //
                        // conferir somente a SERIE do PDA

                        json1 = jsonArray1.getJSONObject(nx);

                        long faixaAitInicial = Long.parseLong(json1.getString("faixaAitInicial"));
                        long faixaAitFinal = Long.parseLong(json1.getString("faixaAitFinal"));

                        // debug  - - retirar
                        //define.setProximoait("0000525");

                        //define.setProximoait(String.valueOf(faixaAitInicial));
                        //define.setAitinicial(String.valueOf(faixaAitInicial));
                        //define.setAitfinal(String.valueOf(faixaAitFinal));

                        // somente define proximo ait na inicialização...
                        try {

                            define.setProximoait(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitInicial)));
                            define.setAitinicial(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitFinal)));
                            define.setAitfinal(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitFinal)));


                            define.setSeriepda(SimpleCrypto.encrypt(info, json1.getString("numero")));
                            define.setPrefeitura(SimpleCrypto.encrypt(info, prefeitura));
                            define.setSigla(SimpleCrypto.encrypt(info, sigla));
                            define.setOrgaoautuador(SimpleCrypto.encrypt(info, orgaoautuador));
                            define.setSerieait(SimpleCrypto.encrypt(info, json1.getString("serieAit")));

                            define.setServidorftp(SimpleCrypto.encrypt(info, servidorftp));
                            define.setUsuarioftp(SimpleCrypto.encrypt(info, usuarioftp));
                            define.setSenhaftp(SimpleCrypto.encrypt(info, senhaftp));
                            define.setArquivobaseftp(SimpleCrypto.encrypt(info, arquivobaseftp));

                            define.setImpressoraMAC(SimpleCrypto.encrypt(info, impressora));
                            define.setImprimeobs(SimpleCrypto.encrypt(info, imprimeobs));

                            // para acessos futuros
                            define.setUsuariowebtrans(SimpleCrypto.encrypt(info, usuarioWebTrans));
                            define.setSenhawebtrans(SimpleCrypto.encrypt(info, senhaWebTrans));

                            // id nao é encriptado
                            define.setIdwebtrans(Long.toString(json1.getLong("idequipamento")));

                            // PDA Ativo ?
                            define.setAtivo(SimpleCrypto.encrypt(info, "S"));
                            if (json1.getString("status").toUpperCase().contains("INATIVO")) {
                                define.setAtivo(SimpleCrypto.encrypt(info, "N"));
                            }

                            // Prefeitura Ativa ?
                            define.setPrefativa(SimpleCrypto.encrypt(info, "N"));
                            if (clienteativo.equals("S"))
                                define.setPrefativa(SimpleCrypto.encrypt(info, "S"));

                            //if ( tipx.contains("1"))
                            paDao.atualizastatus(define);
                            paDao.iniciapda(define);
                            //else
                            // Atualiza o Status de Ativo/Desativo
                            //	definedao.atualizastatus(define);
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
        ParametroDAO paDao = new ParametroDAO(context);

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
            ret = false;
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

                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }
        }

        if (ret) {
            AgenteDAO agentedao = new AgenteDAO(context);

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
                    errofatal = true;
                    e.printStackTrace();
                }
            }

            agentedao.close();
        }
        return ret;
    }

    public boolean trataMedidaAdm() {

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
                if (tipoperacao.contains("1")) errofatal = true;
                e.printStackTrace();
            }

        }

        if (ret) {
            MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(context);

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
                    errofatal = true;
                }


            }

            medidaadmdao.close();
        }
        return ret;
    }

    private void buscaPDA(String xpda) {

        xpda = xpda.toUpperCase();

        ParametroDAO paDao = new ParametroDAO(context);

        // le todos os pdas do orgao autuador
        Cursor cx = paDao.getParametros();

        paDao.close();

        cx.moveToFirst();

        ParametroDAO pardao = new ParametroDAO(context);

        while (cx.isAfterLast() == false) {

            // achou pda
            try {

                String xserie = SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("seriepda")));

                if (xserie.contains(xpda)) {

                    Parametro parx = new Parametro();

                    parx.setSeriepda(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("seriepda"))));
                    parx.setAtivo(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("ativo"))));
                    parx.setPrefativa(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("prefativa"))));
                    parx.setAitinicial(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("aitinicial"))));
                    parx.setAitfinal(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("aitfinal"))));

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

    private String Orgao = "";


    private void informUsr(final String mens) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                progress.setMessage(mens);
            }
        });
    }

    public void Sincronizar() {
        if (tipoperacao.contains("3"))
            progress = ProgressDialog.show(context, "Aguarde...", "Obtendo dados do WebTrans!!!", true, false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                informUsr("Autenticando Talonário...");

                //********************************************************
                // Chama SincronismoWebTrans para carregar arqs. webtrans
                //********************************************************
                UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
                String url = urlswebtrans.geturl("urlcripto").replace(":8080","");
                urlswebtrans.close();
                //DownloadTabelasWebtrans DownloadTabWeb = new DownloadTabelasWebtrans();
                //String s=DownloadTabWeb.InstalImei(IMEI);
                ParametroDAO p = new ParametroDAO(context);
                String ret = "ok";
                IMEI = p.getIMEI();
                ;//"861305045809374"
                p.close();
                //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";
                //IMEI = "3535DD0749458D8";
                //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

                senhaWebTrans = MD5Util.criptografar("cobratalonario");
                usuarioWebTrans = "talonario";

                //UrlsWebTransDAO urlswebtrans = new  UrlsWebTransDAO(DownloadTabelasWebtrans.this);
                //url = urlswebtrans.geturl("urlcripto");
                String urlBase = urlswebtrans.geturl("imei");
                urlswebtrans.close();

                urlBase += "?user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4) + "&imei=" + IMEI;


                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;
                String retornoweb = "";
                int statusCode = 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    try {
                        response = httpclient.execute(new HttpGet(urlBase));
                        StatusLine statusLine = response.getStatusLine();
                        statusCode = statusLine.getStatusCode();
                        retornoweb = EntityUtils.toString(response.getEntity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    HttpPost post = new HttpPost(urlBase);

                    // buscar em parametros!!!!!
                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();


                    //***********************************************************************
                    // TESTE DE CRIPTOGRAFIA - 10.04.2012
                    //*********************************************************************** nvps.add(new BasicNameValuePair("checkSum", MD5Util.criptografar(urlBase)));

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
                    //progress = ProgressDialog.show( CobrasinAitActivity.this, "Aguarde..." , "Autenticando IMEI!!!",true,true);

                    try {
                        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    } catch (UnsupportedEncodingException e1) {

                        ret = "service error";
                        // TODO Auto-generated catch block
                        e1.printStackTrace();

                    }


                    HttpParams httpParameters = new BasicHttpParams();
                    // Set the timeout in milliseconds until a connection is established.
                    int timeoutConnection = 20000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    // Set the default socket timeout (SO_TIMEOUT)
                    // in milliseconds which is the timeout for waiting for data.
                    int timeoutSocket = 20000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                    try {
                        response = httpclient.execute(post);
                        retornoweb = EntityUtils.toString(response.getEntity());
                        StatusLine statusLine = response.getStatusLine();
                        statusCode = statusLine.getStatusCode();
                    } catch (ClientProtocolException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
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

                        if (retornoweb.equals("[]")) {
                            informUsr("IMEI não cadastrado!");
                            return;
                        } else {
                            jsonArray = new JSONArray(retornoweb);
                            JSONObject j = jsonArray.getJSONObject(0);
                            //ParametroDAO p =new ParametroDAO(CobrasinAitActivity.this);
                            Parametro pa = new Parametro();

                            if (tipoperacao.equals("4") == false) {
                                p.limpareg();
                            }


                            try {

                                pa.setIdwebtrans(SimpleCrypto.encrypt(info, j.getString("idequipamento")));
                                pa.setOrgaoautuador(SimpleCrypto.encrypt(info, j.getString("cliente")));

                                pa.setmodpdf("TRUE");
                                pa.setTipoLeituraTAG("GEN2");
                                codMunicipio = j.getString("cliente");
                                pa.setSeriepda(SimpleCrypto.encrypt(info, j.getString("numero")));

                                long faixaAitInicial = Long.parseLong(j.getString("faixaAitInicial"));
                                long faixaAitFinal = Long.parseLong(j.getString("faixaAitFinal"));

                                pa.setAitinicial(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitInicial)));
                                pa.setAitfinal(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitFinal)));
                                long nr = 0;
                                if (j.getString("ultimoAitDigitado").equals("null")) {
                                    nr = (Integer.parseInt(j.getString("faixaAitInicial")));
                                } else {
                                    nr = (Integer.parseInt(j.getString("ultimoAitDigitado")) + 1);
                                }

                                pa.setProximoait(SimpleCrypto.encrypt(info, String.format("%07d", nr)));

                                pa.setSerieait(SimpleCrypto.encrypt(info, j.getString("serieAit")));
                                pa.setSeriepda(SimpleCrypto.encrypt(info, j.getString("numero")));
                                pa.setImpressoraMAC(SimpleCrypto.encrypt(info, j.getString("impressoraMac")));
                                pa.setImpressoraPatrimonio(SimpleCrypto.encrypt(info, j.getString("impressoraPatrimonio")));
                                //p.setParamReceivedByIMEI(pa);
                                pa.setIMEI(SimpleCrypto.encrypt(info, IMEI));
                                if (GetServiceClientes()) {
                                    json1 = jsonArray.getJSONObject(0);

                                    pa.setPrefeitura(SimpleCrypto.encrypt(info, (json1.getString("prefixo"))));
                                    pa.setSigla((SimpleCrypto.encrypt(info, json1.getString("sigla"))));
                                    pa.setUsuariowebtrans(SimpleCrypto.encrypt(info, usuarioWebTrans));
                                    pa.setSenhawebtrans(senhaWebTrans);
                                    Orgao = json1.getString("sigla");
                                    //pa.setIdwebtrans(SimpleCrypto.encrypt(info,json1.getString("idequipamento")));

                                    String clienteativo = "S";
                                    // Prefeitura Ativa ?
                                    if (json1.getString("status").toUpperCase().contains("INATIVO"))
                                        clienteativo = "N";

                                    pa.setPrefativa(SimpleCrypto.encrypt(info, clienteativo));
                                    clienteativo = "S";
                                    if (j.getString("status").toUpperCase().contains("INATIVO"))
                                        clienteativo = "N";

                                    pa.setAtivo(SimpleCrypto.encrypt(info, clienteativo));
                                } else {
                                    informUsr("Serviço WebTrans não responde!");
                                    return;
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                informUsr("Erro na tabela Parametros!");
                                return;
                            }


                            p.setParamReceivedByIMEI(pa);


                        }

                    } catch (JSONException e) {
                        informUsr("Json Erro");
                        e.printStackTrace();
                        return;
                    }

                } else {
                    informUsr(retornoweb);
                    return;
                }

                //limpaArqWebTrans();
                //ParametroDAO paDao = new ParametroDAO(context);

                //Cursor c = paDao.getParametros();
                senhaWebTrans = MD5Util.criptografar("2015RES");


                //codMunicipio =  xcod;//.subSequence(1, 5).toString();  //265810
                //	usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;


                usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    FTPClient con = null;

                    try {
                        File arqFoto = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/");
                        if (!arqFoto.exists())
                            arqFoto.mkdirs();


                        con = new FTPClient();
                        con.connect("189.57.47.194");

                        if (con.login("androidcobra", "androidcobra2014")) {
                            con.enterLocalPassiveMode(); // important!
                            con.setFileType(FTP.BINARY_FILE_TYPE);

                            String srcFile = "/logos/" + codMunicipio + ".jpg";
                            if (arqFoto.exists()) {
                                arqFoto = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/" + codMunicipio + ".jpg");
                                if (!arqFoto.exists())
                                    arqFoto.createNewFile();
                            }

                            //Cria o outputStream para ser passado como parametro
                            FileOutputStream desFileStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/" + codMunicipio + ".jpg");
                            //Faz o download do arquivo
                            boolean status = con.retrieveFile(srcFile, desFileStream);

                            //Fecho o output
                            desFileStream.close();

                            if (status == false) {
                                lok=false;
                            }
                            con.logout();
                            con.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {


                        URL urlimg = new URL(
                                "http://sistemas.cobrasin.com.br/logos/" + codMunicipio + ".jpg");
                        HttpURLConnection connection = (HttpURLConnection) urlimg.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap ImagemPrefeitura = BitmapFactory.decodeStream(input);

                        File direct = new File(Environment.getExternalStorageDirectory() + "/db/BrasaoPrefeitura");

                        if (!direct.exists()) {
                            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura");
                            wallpaperDirectory.mkdirs();
                        }

                        File file = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/"), codMunicipio + ".jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            ImagemPrefeitura.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                lok = true;//trataEquipamentos(tipoperacao);

                //************************************************************************************
                // 16.05.2012
                //
                // nao consegui baixar do WebTrans ( exemplo não está cadastrado agente corretamente )
                // gera erro falta, senão não carrega agente e acaba pedindo o login sem ter agente cadastrado na
                // tabela de agentes
                //************************************************************************************
                if (!lok) {
                    if (tipoperacao.contains("1")) {
                        errofatal = true;
                    }

                }


                if (lok) {
                    informUsr("Baixando Agentes...");
                    tentar = true;
                    lok = false;
                    if (carregaDados("agentes")) {
                        lok = trataAgentes();
                    } else {
                        while (tentar == true) {
                            if (carregaDados("agentes")) {
                                lok = trataAgentes();
                                tentar = false;
                            } else {
                                tentar = false;
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                        aviso.setTitle("Tabelas do WebTrans");
                                        aviso.setMessage("Falha ao baixar tabela de Agente!\nDeseja tentar baixar novamente?");
                                        aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                                lok = false;
                                                tentar = false;
                                            }
                                        });

                                        aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                                if (carregaDados("agentes")) {
                                                    lok = trataAgentes();
                                                    tentar = false;
                                                } else {
                                                    tentar = true;
                                                }
                                            }

                                        });

                                        aviso.show();
                                    }
                                });

                            }
                        }
                    }
                }

                //*************************************************************************************************
                //Somente sincroniza com WebTrans para tipo
                //1 - Inicialização
                //2 - Transmissão de aits e logs para o webtrans
                //3 - carga de tabelas
                //*************************************************************************************************
                if (tipoperacao.contains("1") || tipoperacao.contains("3")) {
                    if (lok) {
                        informUsr("Baixando Tipos...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("tipos")) {
                            lok = trataTipos(); // ok
                        } else {
                            while (tentar == true) {
                                if (carregaDados("tipos")) {
                                    lok = trataTipos();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Tipos!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("tipos")) {
                                                        lok = trataTipos();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }

                    }

                    if (lok) {
                        informUsr("Baixando Espécies...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("especies")) {
                            lok = trataEspecies(); // ok
                        } else {
                            while (tentar == true) {
                                if (carregaDados("especies")) {
                                    lok = trataEspecies();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Espécies!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("especies")) {
                                                        lok = trataEspecies();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    if (lok) {
                        informUsr("Baixando Logradouros...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("logradouros")) {
                            lok = trataLogradouros(); // ok

                        } else {
                            while (tentar == true) {
                                if (carregaDados("logradouros")) {
                                    lok = trataLogradouros();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Logradouros!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("logradouros")) {
                                                        lok = trataLogradouros();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    if (lok) {
                        informUsr("Baixando Enquadramentos...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("enquadramentos")) {
                            lok = trataEnquadramentos(); // ok
                        } else {
                            while (tentar == true) {
                                if (carregaDados("enquadramentos")) {
                                    lok = trataEnquadramentos();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {

                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Enquadramentos!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("enquadramentos")) {
                                                        lok = trataEnquadramentos();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    if (lok) {
                        informUsr("Baixando Enquadramentos Obs obrigatoria...");
                        lok = false;
                        tentar = true;
                        if (carregaEnquadramentosObsObrigatorio()) {
                            lok = true; // ok
                        } else {
                            while (tentar == true) {
                                if (carregaEnquadramentosObsObrigatorio()) {
                                    lok = true;
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {

                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar Enquadramentos com Obs!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaEnquadramentosObsObrigatorio()) {
                                                        lok = true;
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }


                    if (lok) {
                        informUsr("Baixando País...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("pais")) {
                            lok = trataPais(); // ok
                        } else {
                            while (tentar == true) {
                                if (carregaDados("pais")) {
                                    lok = trataPais();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de País!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("pais")) {
                                                        lok = trataPais();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    if (lok) {
                        informUsr("Baixando Medida Administrativa...");
                        lok = false;
                        tentar = true;
                        if (carregaDados("medidaadm")) {
                            lok = trataMedidaAdm(); // ok
                        } else {
                            while (tentar == true) {
                                if (carregaDados("medidaadm")) {
                                    lok = trataMedidaAdm();
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Medida Administrativa!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (carregaDados("medidaadm")) {
                                                        lok = trataMedidaAdm();
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }

                    if (lok) {
                        informUsr("Baixando Município...");
                        lok = false;
                        tentar = true;
                        if (BaixaMunicipio()) {
                            lok = true; // ok
                        } else {
                            while (tentar == true) {
                                if (BaixaMunicipio()) {
                                    lok = true;
                                    tentar = false;
                                } else {
                                    tentar = false;
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            AlertDialog.Builder aviso = new AlertDialog.Builder(context);
                                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                            aviso.setTitle("Tabelas do WebTrans");
                                            aviso.setMessage("Falha ao baixar tabela de Município!\nDeseja tentar baixar novamente?");
                                            aviso.setNeutralButton("Não", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    lok = false;
                                                    tentar = false;
                                                }
                                            });

                                            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    if (BaixaMunicipio()) {
                                                        lok = true;
                                                        tentar = false;
                                                    } else {
                                                        tentar = true;
                                                    }
                                                }

                                            });

                                            aviso.show();
                                        }
                                    });

                                }
                            }
                        }
                    }
                }
                //******************************************
                // conseguiu carregar todas as tabelas ?
                //******************************************
                if (lok) {
                    try {
                        criaArqFimWebTrans();
                        //ParametroDAO p=new ParametroDAO(context);
                        try {
                            p.SetDados(SimpleCrypto.encrypt(info, "true"));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        p.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    //******************************************************************
                    // ocorreu algum erro fatal ?  nos JSONs ou na gravação das tabelas
                    //******************************************
                    if (errofatal) {
                        // fatal ?
                        try {
                            criaArqErroWebTrans();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            criaArqFimWebTrans();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

                if (tipoperacao.contains("3")) progress.dismiss();

            }

            private boolean BaixaMunicipio() {
                boolean Baixou = true;

                WebService web = new WebService();
                MunicipioDAO MuDAO = new MunicipioDAO(context);

                MuDAO.ApagaTudo();

                final JSONArray dt = web.ExecuteReaderQuery_PrdMultas("Select Mu_UF.nome UF,Mu.cod_prodesp IdProdesp,Mu.nome Cidade From municipio Mu JOIN municipio_uf Mu_UF ON Mu_UF.idmunicipio_uf = Mu.idmunicipio_uf");

                if (dt != null && dt.length() > 0) {
                    int i = 0;
                    // Here you should write your time consuming task...
                    while (i <= dt.length()) {

                        JSONObject dr;
                        try {
                            dr = dt.getJSONObject(i);
                            Municipio Mu = new Municipio();
                            Mu.setUF(dr
                                    .getString("UF"));
                            Mu.setIdProdesp(dr
                                    .getString("IdProdesp"));
                            Mu.setCidade(dr
                                    .getString("Cidade"));

                            MuDAO.InsereMunicipio(Mu);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        i++;
                    }
                } else {
                    Baixou = false;
                }

                MuDAO.close();


                return Baixou;
            }

            //************************************************************************************************
            //Lipa arquivos de erro
            //************************************************************************************************
            private void limpaArqWebTrans() {
                // caminho onde estão os arquivos
                String root = Environment.getDataDirectory().getAbsolutePath() + "/data/br.com.cobrasin/databases";

                try {
                    File file = new File(root, "errowebtrans");
                    file.delete();
                } catch (Exception e) {

                }

                //----------------------------------------------------------------------------------
                try {
                    File file = new File(root, "fimwebtrans");
                    file.delete();
                } catch (Exception e) {

                }
            }

            //************************************************************************************************
            // será verifica antes de criar ait se existe arquivo de erro de ultima carga de dados do webtrans
            //************************************************************************************************
            private void criaArqErroWebTrans() throws IOException {
                // caminho onde estão os arquivos
                String root = Environment.getDataDirectory().getAbsolutePath() + "/data/br.com.cobrasin/databases";

                File file = new File(root, "errowebtrans");

                FileOutputStream fout = new FileOutputStream(root + "/errowebtrans");
                fout.write(mensErro.getBytes());
                fout.close();

            }

            //************************************
            // indica que acabou operacao
            //************************************
            private void criaArqFimWebTrans() throws IOException {
                // caminho onde estão os arquivos
                String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

                File file = new File(root, "fimwebtrans");

                FileOutputStream fout = new FileOutputStream(root + "/fimwebtrans");
                fout.write(mensErro.getBytes());
                fout.close();

            }

            //***************************************
            //Carrega tabela "PARAMETRO" ,  Gera erro fatal quando nao consegue resultado !!!
            //***************************************
            public boolean trataEquipamentos(String tipx) {

                boolean ret = true;

                // Pega dados antes de excluir todas os registros
                ParametroDAO paDao = new ParametroDAO(context);

                Context ctx = context;

                Cursor c = paDao.getParametros();

                //  dados para comunicação com servidor ftp

                String servidorftp = "";
                String usuarioftp = "";
                String senhaftp = "";
                String arquivobaseftp = "";
                String impressora = "";
                String imprimeobs = "";
                String sigla = "";
                String orgaoautuador = "";
                String cxseriePDA = "";
                //try {

                //servidorftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("servidorftp")));
                //usuarioftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuarioftp")));
                //senhaftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhaftp")));
                //arquivobaseftp = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("arquivobaseftp")));
                //impressora = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("impressora")));
                //imprimeobs = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("imprimeobs")));

                //sigla = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("sigla")));

                // dados para comunicação com o WebTrans
                //usuarioWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("usuariowebtrans")));
                //senhaWebTrans = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("senhawebtrans")));

                //******************************************************
                // 09.08.2012
                //
                // Define Usuario/Senha para leitura de Tabelas
                //******************************************************
                senhaWebTrans = MD5Util.criptografar("cobratalonario");

                //String xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(), c.getString(c.getColumnIndex("orgaoautuador")) );

                //codMunicipio =  xcod.subSequence(1, 5).toString();  //265810


                //codMunicipio = c.getString(c.getColumnIndex("orgaoautuador")).subSequence(1, 5).toString();  //265810

                //cxseriePDA = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("seriepda")));

                // 16.05.2012
                // Retirar , vira do Webtrans
                //orgaoautuador = SimpleCrypto.decrypt(Utilitarios.getInfo(),c.getString(c.getColumnIndex("orgaoautuador")));


                //}
                //catch (Exception e1)
                //{
                // TODO Auto-generated catch block
                //	e1.printStackTrace();
                //}

                String c1, c2, c3;
                c1 = usuarioWebTrans;
                c2 = senhaWebTrans;
                c3 = codMunicipio;

                c.close();

                //if (carregaDados("equipamentos"))
                //{
                // salva
                JSONArray jsonArray1 = jsonArray;
                try {
                    json1 = jsonArray1.getJSONObject(0);
                    codMunicipio = json1.getString("cliente");
                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //String orgaoautuador = "",
                String prefeitura = "";

                //usuarioWebTrans = "LOGWEBTRANS"+ codMunicipio;
                usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;

                senhaWebTrans = MD5Util.criptografar("2015RES");
                // Pega dados do Cliente
                if (carregaDados("clientes")) {

                    String clienteativo = "N";


                    for (int nx = 0; nx < jsonArray.length(); nx++) {
                        try {


                            json1 = jsonArray.getJSONObject(nx);
                            // antigo
                            //orgaoautuador = json1.getString("orgao_transito").subSequence(0, 6).toString();

                            // atual , ainda nao foi feito o deploy
                            orgaoautuador = "2" + json1.getString("orgaoAutuador").toString() + "0";
                            prefeitura = json1.getString("prefixo");
                            sigla = json1.getString("sigla");

                            clienteativo = "S";
                            // Prefeitura Ativa ?
                            if (json1.getString("status").toUpperCase().contains("INATIVO"))
                                clienteativo = "N";

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            ret = false;
                            errofatal = true;
                            e.printStackTrace();
                        }
                    }

                    // limpa tabela quando está inicializando o PDA
                    //if ( tipx.contains("1")) definedao.delete();

                    //definedao.delete();

                    for (int nx = 0; nx < jsonArray1.length(); nx++) {

                        Parametro define = new Parametro();

                        try {

                            // nao é necessário validar o município pois no baseandroid.zip
                            // deve ser colocado o municipio correto de operação
                            //
                            // conferir somente a SERIE do PDA

                            json1 = jsonArray1.getJSONObject(nx);

                            long faixaAitInicial = Long.parseLong(json1.getString("faixaAitInicial"));
                            long faixaAitFinal = Long.parseLong(json1.getString("faixaAitFinal"));

                            // debug  - - retirar
                            //define.setProximoait("0000525");

                            //define.setProximoait(String.valueOf(faixaAitInicial));
                            //define.setAitinicial(String.valueOf(faixaAitInicial));
                            //define.setAitfinal(String.valueOf(faixaAitFinal));

                            // somente define proximo ait na inicialização...
                            try {

                                define.setProximoait(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitInicial)));
                                define.setAitinicial(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitFinal)));
                                define.setAitfinal(SimpleCrypto.encrypt(info, String.format("%07d", faixaAitFinal)));


                                define.setSeriepda(SimpleCrypto.encrypt(info, json1.getString("numero")));
                                define.setPrefeitura(SimpleCrypto.encrypt(info, prefeitura));
                                define.setSigla(SimpleCrypto.encrypt(info, sigla));
                                define.setOrgaoautuador(SimpleCrypto.encrypt(info, orgaoautuador));
                                define.setSerieait(SimpleCrypto.encrypt(info, json1.getString("serieAit")));

                                define.setServidorftp(SimpleCrypto.encrypt(info, servidorftp));
                                define.setUsuarioftp(SimpleCrypto.encrypt(info, usuarioftp));
                                define.setSenhaftp(SimpleCrypto.encrypt(info, senhaftp));
                                define.setArquivobaseftp(SimpleCrypto.encrypt(info, arquivobaseftp));

                                define.setImpressoraMAC(SimpleCrypto.encrypt(info, impressora));
                                define.setImprimeobs(SimpleCrypto.encrypt(info, imprimeobs));

                                // para acessos futuros
                                define.setUsuariowebtrans(SimpleCrypto.encrypt(info, usuarioWebTrans));
                                define.setSenhawebtrans(SimpleCrypto.encrypt(info, senhaWebTrans));

                                // id nao é encriptado
                                define.setIdwebtrans(Long.toString(json1.getLong("idequipamento")));

                                // PDA Ativo ?
                                define.setAtivo(SimpleCrypto.encrypt(info, "S"));
                                if (json1.getString("status").toUpperCase().contains("INATIVO")) {
                                    define.setAtivo(SimpleCrypto.encrypt(info, "N"));
                                }

                                // Prefeitura Ativa ?
                                define.setPrefativa(SimpleCrypto.encrypt(info, "N"));
                                if (clienteativo.equals("S"))
                                    define.setPrefativa(SimpleCrypto.encrypt(info, "S"));

                                //if ( tipx.contains("1"))
                                paDao.atualizastatus(define);
                                //else
                                // Atualiza o Status de Ativo/Desativo
                                //	definedao.atualizastatus(define);
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

                paDao.close();

                //buscaPDA(cxseriePDA);

                // criptografa definePDA...

                //}
                //else
                //{
                //	errofatal =true;
                //	ret = false;
                //}

                return ret;

            }


            private void buscaPDA(String xpda) {

                xpda = xpda.toUpperCase();

                ParametroDAO paDao = new ParametroDAO(context);

                // le todos os pdas do orgao autuador
                Cursor cx = paDao.getParametros();

                paDao.close();

                cx.moveToFirst();

                ParametroDAO pardao = new ParametroDAO(context);

                while (cx.isAfterLast() == false) {

                    // achou pda
                    try {

                        String xserie = SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("seriepda")));

                        if (xserie.contains(xpda)) {

                            Parametro parx = new Parametro();

                            parx.setSeriepda(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("seriepda"))));
                            parx.setAtivo(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("ativo"))));
                            parx.setPrefativa(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("prefativa"))));
                            parx.setAitinicial(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("aitinicial"))));
                            parx.setAitfinal(SimpleCrypto.decrypt(info, cx.getString(cx.getColumnIndex("aitfinal"))));

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


            //************************************************************************************
            // Demais tratamentos geram erro fatal somente para operacao 1 ( inicialização do PDA )
            // ou quando não conseguem gravar ( tabela incopmleta )
            //*************************************************************************************

            //***************************************
            //Carrega tabela "TIPOS"
            //***************************************
            private boolean trataAgentes() {
                // TODO Auto-generated method stub

                boolean ret = true;

                // Pega dados antes de excluir todas os registros
                ParametroDAO paDao = new ParametroDAO(context);

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
                    codMunicipio = xcod;//.subSequence(1, 5).toString();  //265810

                    //usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;
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

                        if (Orgao.equals("DNIT")) {
                            try {
                                agente.setDNIT(SimpleCrypto.encrypt(info, "true"));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                agente.setDNIT(SimpleCrypto.encrypt(info, "false"));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if (json1.getString("status").toUpperCase().contains("INATIVO"))
                            agente.setAtivo("N");


                    } catch (JSONException e) {

                        // TODO Auto-generated catch block
                        ret = false;

                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }
                }

                if (ret) {
                    AgenteDAO agentedao = new AgenteDAO(context);

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

                                if (Orgao.equals("DNIT")) {
                                    agente.setDNIT(SimpleCrypto.encrypt(info, "true"));
                                } else {
                                    agente.setDNIT(SimpleCrypto.encrypt(info, "false"));
                                }
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
                        if (tipoperacao.contains("1")) errofatal = true;
                        ret = false;
                        e.printStackTrace();
                    }

                }

                if (ret) {

                    TipoDAO tipodao = new TipoDAO(context);

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
                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }

                }

                if (ret) {
                    EspecieDAO especiedao = new EspecieDAO(context);

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
                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }


                }

                if (ret) {
                    LogradouroDAO logradourodao = new LogradouroDAO(context);

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
                            errofatal = true;
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
                        if (json1.getString("competencia").contains("Municipal")) {

                            enquadramento.setCodigo(json1.getString("cod_prodesp"));
                            enquadramento.setDescricao(json1
                                    .getString("nomeEnquadramento"));
                        }
                        //enquadramento.setCodigo( json1.getString("cod_prodesp") );
                        //enquadramento.setDescricao(json1.getString("nomeEnquadramento"));


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        ret = false;
                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }

                }

                if (ret) {
                    EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(context);

                    // limpa tabela
                    enquadramentodao.delete();

                    for (int nx = 0; nx < jsonArray.length(); nx++) {

                        Enquadramento enquadramento = new Enquadramento();

                        try {

                            json1 = jsonArray.getJSONObject(nx);

                            if (json1.getLong("ufirs") > 0) {
                                if (json1.getString("cod_prodesp").equals("70302")) {
                                    nx++;

                                    json1 = jsonArray.getJSONObject(nx);
                                }
                                enquadramento.setCodigo(json1.getString("cod_prodesp"));
                                enquadramento.setDescricao(json1.getString("nomeEnquadramento"));
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
                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }

                }

                if (ret) {
                    PaisDAO paisdao = new PaisDAO(context);

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
                        if (tipoperacao.contains("1")) errofatal = true;
                        e.printStackTrace();
                    }

                }

                if (ret) {
                    MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(context);

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
                            errofatal = true;
                        }


                    }

                    medidaadmdao.close();
                }
                return ret;
            }


            private boolean carregaEnquadramentosObsObrigatorio() {
                boolean ret = false;

                HttpParams httpParameters = new BasicHttpParams();
                // Set the timeout in milliseconds until a connection is
                // established.
                int timeoutConnection = 20000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for
                // data.
                int timeoutSocket = 20000;
                HttpConnectionParams.setSoTimeout(httpParameters,
                        timeoutSocket);

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();

                UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
                HttpGet httpPost = new HttpGet(
                        "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/GetEnquadramentosObsObrigatorio/" + codMunicipio);// urlswebtrans.geturl("foto")
                urlswebtrans.close();

                try {

                    HttpResponse response = httpClient.execute(httpPost,
                            localContext);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    String retornoweb = EntityUtils.toString(response.getEntity());
                    //String retz = retornoweb;

                    if (statusCode == 200) {

                        EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(context);
                        enquadramentodao.UpdateEnquadramentosObsObrigatorio(retornoweb);
                        ret = true;
                    } else {
                        ret = false;
                    }
                    System.out.println(response.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    ret = false;
                }
                return ret;
            }

            //*****************************************************
            // Quando nao consegue ler da web NAO gera erro Fatal !
            //*****************************************************
            private boolean carregaDados(String tipotransacao) {
                boolean ret = true;

                //String url = "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

                //url = "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

                UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

                String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

                String urlBase = urlswebtrans.geturl(tipotransacao);

                urlBase += "?cliente=" + codMunicipio + "&user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4);
                urlswebtrans.close();

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = null;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    try {
                        response = httpclient.execute(new HttpGet(urlBase));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
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
                        response = httpclient.execute(post);

                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        ret = false;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        ret = false;
                    }
                }
                try {
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

                            if (tipotransacao.equals("clientes"))
                                retornoweb = "[" + retornoweb + "]";

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

                        Utilitarios.gravaLog(mensz, context);

                        ret = false;
                    }
                }catch(Exception e)
                {
                    ret=false;
                }
                return ret;
            }

            public boolean GetServiceClientes() {
                boolean ret = true;

                UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

                String url = urlswebtrans.geturl("urlcripto").replace(":8080","");

                String urlBase = urlswebtrans.geturl("clientes");

                urlswebtrans.close();

                HttpClient httpclient = new DefaultHttpClient();
                senhaWebTrans = MD5Util.criptografar("2015RES");

	    		/*String xcod="";
	    		try {
	    			xcod = SimpleCrypto.decrypt(Utilitarios.getInfo(), c.getString(c.getColumnIndex("orgaoautuador")));
	    		} catch (Exception e3) {
	    			// TODO Auto-generated catch block
	    			e3.printStackTrace();
	    		}
	    		*/
                //codMunicipio =  xcod;//.subSequence(1, 5).toString();  //265810

                usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;


                //***********************************************************************
                // TESTE DE CRIPTOGRAFIA - 10.04.2012
                //***********************************************************************
                urlBase += "?cliente=" + codMunicipio + "&user=" + usuarioWebTrans + "&password=" + senhaWebTrans + "&dataSolicitacao=" + Utilitarios.getDataHora(4);

                HttpResponse response = null;
                int statusCode = 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    try {
                        response = httpclient.execute(new HttpGet(urlBase));
                        StatusLine statusLine = response.getStatusLine();
                        statusCode = statusLine.getStatusCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    HttpPost post = new HttpPost(urlBase);
                    try {
                        urlBase = SimpleCrypto.encrypt(info, urlBase);
                    } catch (Exception e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }



                    // buscar em parametros!!!!!
                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("checkSum", MD5Util.criptografar(urlBase)));
                    nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));

                    //*****************************************************************************
                    //.multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
                    //*****************************************************************************

                    try {
                        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                        HttpParams httpParameters = new BasicHttpParams();
                        // Set the timeout in milliseconds until a connection is established.
                        int timeoutConnection = 20000;
                        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                        // Set the default socket timeout (SO_TIMEOUT)
                        // in milliseconds which is the timeout for waiting for data.
                        int timeoutSocket = 20000;
                        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                        //HttpResponse response = httpclient.execute(httpget);
                        response = httpclient.execute(post);

                        StatusLine statusLine = response.getStatusLine();
                        statusCode = statusLine.getStatusCode();
                    } catch (UnsupportedEncodingException e1) {
                        ret = false;
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                try {

                    String retornoweb = EntityUtils.toString(response.getEntity());

                    if (statusCode == 200) {
                        try {
                            try {
                                retornoweb = SimpleCrypto.decrypt(Utilitarios.getInfo(), retornoweb);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            retornoweb = "[" + retornoweb + "]";
                            jsonArray = new JSONArray(retornoweb);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            ret = false;
                            e.printStackTrace();
                        }

                    } else {
                        informUsr(retornoweb);
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
        }).start();

    }
}
