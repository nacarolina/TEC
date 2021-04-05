package br.com.cobrasin;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.sf.json.JSONFunction;

import org.apache.commons.net.ftp.FTPClient;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.RequestContent;
import org.apache.http.util.EntityUtils;

import org.json.*;
import org.json.simple.JSONStreamAware;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.UrlsWebTransDAO;
import br.com.cobrasin.tabela.Agente;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.AitEnquadramento;
import br.com.cobrasin.tabela.Logs;
import br.com.cobrasin.tabela.NotaFiscal;
import br.com.cobrasin.tabela.Parametro;

//import net.sf.json.*;

public class Sincronismo {

    // private String endereco = "http://www.caelum.com.br/mobile?dado=";
    private String endereco;// = "http://sistemas.cobrasin.com.br:8080/JSONTeste/";
    private Context context;
    private ProgressDialog progress;
    // private Toast aviso;
    private String retornoweb = "";
    private String info = Utilitarios.getInfo();
    private Handler handler = new Handler();

    private byte buffer[];

    private JSONObject json1;
    private JSONArray jsonArray;
    private SincronismoWebTrans sinc;
    private List<NameValuePair> nvps;

    private String usuarioWebTrans, senhaWebTrans, codMunicipio, url, cequip,
            sidWebTrans, serieait, Foto_base64, Foto_base64Infrator;
    private Long idEquipamento, idAit;

    private Ait ait1;
    private List<Ait> lista;
    private List<AitEnquadramento> listaenq;
    private AitEnquadramento aitenq1;

    private String OrgA, Pda, agente;

    boolean errotx;

    private String salvaAgente = "";
    private String IMEI = "";

    public Sincronismo(Context context, String agentex) {

        this.context = context;
        salvaAgente = agentex;
        // IMEI=imei;
    }

    // **************************************
    // Mostra mensagem atraves de um handler
    // ***************************************
    private void mostraMensagem(final String mensagem) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder aviso1 = new AlertDialog.Builder(context);
                aviso1.setIcon(android.R.drawable.ic_dialog_alert);
                aviso1.setTitle("Transmissão");
                aviso1.setMessage(mensagem);
                aviso1.setPositiveButton("OK", null);
                aviso1.show();

            }
        });
    }


    /*
     * //*************************************** // Transmite fotos do ait para
     * o Servidor //*************************************** public boolean
     * txFotoftp(Ait aitx) { long idAit = aitx.getId();
     *
     * boolean retorno = true; int qtdtx = 0 ;
     *
     * FotoDAO fotodao = new FotoDAO(context);
     *
     * // tem fotos para transmitir ? if (fotodao.getQtde(idAit) > 0) { retorno
     * = false;
     *
     * ParametroDAO pardao = new ParametroDAO(context);
     *
     * Cursor cpar = pardao.getParametros();
     *
     * FTPClient ftp = new FTPClient();
     *
     * //Faz a conexão com o servidor ftp try {
     *
     * ftp.connect(cpar.getString(cpar.getColumnIndex("servidorftp")));
     * //ftp.connect("sistemas.cobrasin.com.br");
     * ftp.login(cpar.getString(cpar.getColumnIndex
     * ("usuarioftp")),cpar.getString(cpar.getColumnIndex("senhaftp")));
     *
     * // tenta criar a pasta fotos try { ftp.makeDirectory("fotos"); }
     * catch(Exception e) {
     *
     * }
     *
     * // tenta subir todas as fotos Cursor cfotos = fotodao.getImagens(idAit);
     *
     * while ( cfotos.moveToNext()) {
     *
     *
     * // foto = orgao+serieait+ait String arquivofoto = null; try {
     *
     * ///arquivofoto = Environment.getExternalStorageDirectory() //+
     * "/imagens/" + //cpar.getString(cpar.getColumnIndex("orgaoautuador")) +
     * "_" + //cpar.getString(cpar.getColumnIndex("serieait")) +
     * //SimpleCrypto.decrypt(info, aitx.getAit()) + //".jpg";
     *
     * arquivofoto = Environment.getExternalStorageDirectory() +
     * "/imagens/saida.jpg";
     *
     * } catch (Exception e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     *
     * String arquivofoto1 = null; try {
     *
     * arquivofoto1 = "fotos/" +
     * cpar.getString(cpar.getColumnIndex("orgaoautuador")) + "_" +
     * cpar.getString(cpar.getColumnIndex("serieait")) +
     * SimpleCrypto.decrypt(info,aitx.getAit()) + ".jpg";
     *
     * } catch (Exception e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     *
     * buffer = cfotos.getBlob((cfotos.getColumnIndex("imagem")));
     *
     * this.StoreByteImage(context, buffer, 90, "saida.jpg");
     *
     * FileInputStream fis = new FileInputStream(arquivofoto);
     *
     * //FTPClient ftp = new FTPClient();
     *
     * //ftp.connect("sistemas.cobrasin.com.br");
     * //ftp.login(cpar.getString(cpar.getColumnIndex
     * ("usuarioftp")),cpar.getString(cpar.getColumnIndex("senhaftp")));
     *
     * // tenta criar a pasta fotos //try //{ // ftp.makeDirectory("fotos"); //}
     * //catch(Exception e) //{
     *
     * //}
     *
     * ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
     *
     * if (ftp.storeFile(arquivofoto1, fis)) qtdtx++ ;
     *
     * fis.close();
     *
     *
     * //try //{ //ftp.logout(); // ftp.disconnect();
     *
     * //} //catch(Exception e) //{ // //}
     *
     *
     *
     * }
     *
     * cfotos.close();
     *
     * // transmitiu todas ? if ( qtdtx == fotodao.getQtde(idAit)) retorno =
     * true ;
     *
     * try { ftp.disconnect(); } catch(Exception e) {
     *
     * }
     *
     * // logout ftp.logout();
     *
     * //Disconecta do ftp ftp.disconnect();
     *
     *
     * } catch (SocketException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); }
     *
     *
     *
     * cpar.close();
     *
     * pardao.close(); }
     *
     * fotodao.close();
     *
     * return retorno; }
     */

    // ------------------------------------------------------------------------------------------------------------------
    // Thread de Sincronismo
    // ------------------------------------------------------------------------------------------------------------------
//    @TargetApi(Build.VERSION_CODES.FROYO)
    public class sincronizar extends AsyncTask<String, String, String> {

        private String resp;
        String Agente = "";
        ProgressDialog progressDialog;

        private void informUsr(final String mens) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    progressDialog.setMessage(mens);
                }
            });
        }

        private void SincronizaEnquadramentoObsObrigatorio() {

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

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();

            String DtHrSincroniaObsObrigatorio = "", IdMunicipio = "";
            ParametroDAO paDao = new ParametroDAO(context);
            Cursor c = paDao.getParametros();
            try {
                IdMunicipio = SimpleCrypto.decrypt(Utilitarios.getInfo(), c.getString(c.getColumnIndex("orgaoautuador")));
                c.close();
            } catch (Exception e) {

            }
            SQLiteDatabase s = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/enquadramento", null, 0);
            try {
                c = null;
                String sqlpesq = "Select DtHrSincroniaObsObrigatorio from enquadramento  where  EnquadramentoObsObrigatorio ='1'";
                c = s.rawQuery(sqlpesq, null);

                c.moveToFirst();
                DtHrSincroniaObsObrigatorio = c.getString(c.getColumnIndex("DtHrSincroniaObsObrigatorio"));

                c.close();

            } catch (SQLiteException e) {
                Log.e("Erro=", e.getMessage());
            }

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
            HttpGet httpPost = new HttpGet(
                    "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/GetAtualizaEnquadramentosObsObrigatorio/" + DtHrSincroniaObsObrigatorio + ";" + IdMunicipio);// urlswebtrans.geturl("foto")
            urlswebtrans.close();

            try {
                httpClient.setRedirectHandler(new DefaultRedirectHandler() {
                    @Override
                    public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                        return super.isRedirectRequested(httpResponse, httpContext);
                    }

                    @Override
                    public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                        return sanitizeUrl(httpResponse.getFirstHeader("location").getValue());
                    }
                });
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                String retornoweb = EntityUtils.toString(response.getEntity()).replace('"', ' ').trim();
                //String retz = retornoweb;

                if (statusCode == 200) {

                    if (retornoweb.equals("") == false) {
                        EnquadramentoDAO enquadramentodao = new EnquadramentoDAO(context);
                        enquadramentodao.UpdateEnquadramentoObsObrigatorioRemoveAll();
                        enquadramentodao.UpdateEnquadramentosObsObrigatorio(retornoweb);
                    }
                } else {
                    ret = false;
                }
                System.out.println(response.getStatusLine());
            } catch (IOException e) {
                e.printStackTrace();
                ret = false;
            }

        }

        // ************************************************
        // Pega a imagem do campo blob e grava em arquivo
        // ************************************************
        private boolean StoreByteImage(Context mContext, byte[] imageData,
                                       int quality, String expName) {

            File sdImageMainDirectory = new File(Environment
                    .getExternalStorageDirectory() + "/imagens");

            FileOutputStream fileOutputStream = null;

            // String nameFile = expName;
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;

                Bitmap myImage = BitmapFactory.decodeByteArray(imageData,
                        0, imageData.length, options);

                fileOutputStream = new FileOutputStream(
                        sdImageMainDirectory.toString() + "/" + expName);

                // fileOutputStream = new FileOutputStream(
                // expName);

                BufferedOutputStream bos = new BufferedOutputStream(
                        fileOutputStream);

                myImage.compress(CompressFormat.JPEG, quality, bos);

                bos.flush();
                bos.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return true;
        }

        private boolean SincronizaAssinaturaAIT(Ait aitx) {
            idAit = aitx.getId();

            boolean retorno = true;
            try {
                int qtdtx = 0;

                try {
                    SQLiteDatabase Base = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);

                    Cursor cursor = Base.rawQuery("Select arquivoAssinatura,idAit from aitAssinatura where idAit= " + idAit, null);
                    //tem assinatura
                    while (cursor.moveToNext()) {
                        File imgFile = new File(cursor.getString(cursor.getColumnIndex("arquivoAssinatura")));
                        if (imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            retorno = false;
                            ;
                            sidWebTrans = String.valueOf(aitx.getIdWebTrans());


                            //idAit = Long.parseLong( cursor.getString(cursor.getColumnIndex("idAit")));
                            String arquivofoto = null;
                            try {

                                arquivofoto = cursor.getString(cursor.getColumnIndex("arquivoAssinatura"));

                                buffer = cursor.getBlob((cursor
                                        .getColumnIndex("arquivoAssinatura")));
                                // recupera foto e grava na pasta imagens
                                this.StoreByteImage(context, buffer, 90,
                                        arquivofoto);

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                qtdtx++;// conta como upload valido, mas na
                                // verdade o sistema é conseguiu salvar
                                // a imagem
                            }

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

                            DefaultHttpClient httpClient = new DefaultHttpClient();
                            HttpContext localContext = new BasicHttpContext();

                            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
                            HttpPost httpPost = new HttpPost(
                                    "http://sistemas.cobrasin.com.br/multas-web/talonario/enviarAssinatura.action");
                            urlswebtrans.close();

                            try {

                                MultipartEntity entity = new MultipartEntity(
                                        HttpMultipartMode.BROWSER_COMPATIBLE);
                                entity.addPart("user", new StringBody(
                                        usuarioWebTrans));
                                entity.addPart("password", new StringBody(
                                        senhaWebTrans));
                                entity.addPart(
                                        "dataSolicitacao",
                                        new StringBody(Utilitarios
                                                .getDataHora(4)));
                                entity.addPart("equipamento", new StringBody(
                                        cequip));
                                entity.addPart("idmulta", new StringBody(
                                        sidWebTrans));
                                entity.addPart("upload", new FileBody(new File(
                                        arquivofoto)));
                                httpPost.setEntity(entity);
                                httpClient.setRedirectHandler(new DefaultRedirectHandler() {
                                    @Override
                                    public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                                        return super.isRedirectRequested(httpResponse, httpContext);
                                    }

                                    @Override
                                    public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                                        return sanitizeUrl(httpResponse.getFirstHeader("location").getValue());
                                    }
                                });

                                HttpResponse response = httpClient.execute(
                                        httpPost, localContext);

                                StatusLine statusLine = response
                                        .getStatusLine();
                                int statusCode = statusLine.getStatusCode();

                                retornoweb = EntityUtils.toString(response
                                        .getEntity());

                                String retz = retornoweb;

                                if (statusCode == 200) {
                                    qtdtx++; // indica transmissao ok
                                }

                                System.out.println(response.getStatusLine());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    cursor.close();
                    Base.close();

                } catch (SQLiteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return retorno;
        }

        // ************************************************
        // Transmite fotos via http do ait para o WebTrans
        // ************************************************
        private boolean txFotohttp(Ait aitx) {
            idAit = aitx.getId();

            boolean retorno = true;
            try {
                int qtdtx = 0;

                FotoDAO fotodao = new FotoDAO(context);

                // tem fotos para transmitir ?
                if (fotodao.getQtde(idAit) > 0) {
                    retorno = false;

                    cequip = String.valueOf(idEquipamento);
                    sidWebTrans = String.valueOf(aitx.getIdWebTrans());

                    ParametroDAO pardao = new ParametroDAO(context);
                    Cursor cpar = pardao.getParametros();

                    // tenta subir todas as fotos
                    Cursor cfotos = fotodao.getImagens(idAit);

                    int ntipo = 1;
                    String posfixo = "";
                    while (cfotos.moveToNext()) {
                        // foto = orgao+serieait+ait

                        // tipo de foto para o Webtrans
                        switch (ntipo) {
                            case 1:
                                posfixo = "Z";
                                break;
                            case 2:
                                posfixo = "P";
                                break;
                            case 3:
                                posfixo = "T";
                                break;
                            case 4:
                                posfixo = "F";
                                break;
                        }

                        String snumait = "";
                        try {
                            snumait = SimpleCrypto.decrypt(info,
                                    aitx.getAit());
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        Integer numait = Integer.parseInt(snumait);

                        String arquivofoto = null;
                        try {

                            arquivofoto = SimpleCrypto.decrypt(info, cpar
                                    .getString(cpar
                                            .getColumnIndex("serieait")))
                                    + String.valueOf(numait)
                                    + posfixo
                                    + ".jpg";

                            buffer = cfotos.getBlob((cfotos
                                    .getColumnIndex("imagem")));
                            // recupera foto e grava na pasta imagens
                            this.StoreByteImage(context, buffer, 90,
                                    arquivofoto);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            qtdtx++;// conta como upload valido, mas na
                            // verdade o sistema é conseguiu salvar
                            // a imagem
                        }

                        arquivofoto = Environment
                                .getExternalStorageDirectory()
                                + "/imagens/" + arquivofoto;

                        HttpParams httpParameters = new BasicHttpParams();
                        // Set the timeout in milliseconds until a
                        // connection is established.
                        int timeoutConnection = 20000;
                        HttpConnectionParams.setConnectionTimeout(
                                httpParameters, timeoutConnection);
                        // Set the default socket timeout (SO_TIMEOUT)
                        // in milliseconds which is the timeout for waiting
                        // for data.
                        int timeoutSocket = 20000;
                        HttpConnectionParams.setSoTimeout(httpParameters,
                                timeoutSocket);

                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpContext localContext = new BasicHttpContext();

                        UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(
                                context);
                        HttpPost httpPost = new HttpPost(urlswebtrans
                                .geturl("foto"));
                        urlswebtrans.close();

                        try {

                            MultipartEntity entity = new MultipartEntity(
                                    HttpMultipartMode.BROWSER_COMPATIBLE);
                            entity.addPart("user", new StringBody(
                                    usuarioWebTrans));
                            entity.addPart("password", new StringBody(
                                    senhaWebTrans));
                            entity.addPart(
                                    "dataSolicitacao",
                                    new StringBody(Utilitarios
                                            .getDataHora(4)));
                            entity.addPart("equipamento", new StringBody(
                                    cequip));
                            entity.addPart("cliente", new StringBody(
                                    codMunicipio));
                            entity.addPart("idmulta", new StringBody(
                                    sidWebTrans));
                            entity.addPart("upload", new FileBody(new File(
                                    arquivofoto)));
                            httpPost.setEntity(entity);

                            httpClient.setRedirectHandler(new DefaultRedirectHandler() {
                                @Override
                                public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                                    return super.isRedirectRequested(httpResponse, httpContext);
                                }

                                @Override
                                public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                                    return sanitizeUrl(httpResponse.getFirstHeader("location").getValue());
                                }
                            });

                            HttpResponse response = httpClient.execute(
                                    httpPost, localContext);

                            StatusLine statusLine = response
                                    .getStatusLine();
                            int statusCode = statusLine.getStatusCode();

                            retornoweb = EntityUtils.toString(response
                                    .getEntity());

                            String retz = retornoweb;

                            if (statusCode == 200) {
                                qtdtx++; // indica transmissao ok
                            }

                            System.out.println(response.getStatusLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ntipo++;

                    }

                    cfotos.close();

                    // transmitiu todas ?
                    if (qtdtx == fotodao.getQtde(idAit))
                        retorno = true;

                    cpar.close();

                    pardao.close();
                }

                fotodao.close();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return retorno;
        }

        private boolean txFotohttpOcr(Ait aitx) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(
                        "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/obj");
                // post.setHeader("Content-type", "application/json");
                // post.setHeader("Accept", "application/json");
                // JSONStringer obj = new JSONStringer();

                // try {
                // obj.("teste");
                // } catch (JSONException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                // }

                JSONStringer json = new JSONStringer();
                json.object();
                json.key("userWebTrans").value("userWebTrans");
                json.key("password").value("password");
                json.key("dataSolicitacao").value("dataSolicitacao");
                json.key("equipamento").value("equipamento");
                json.key("idMulta").value("idMulta");
                json.key("pdf").value("pdf");
                json.key("patrimonio").value("patrimonio");

                json.endObject();

                // post.setEntity(new StringEntity(json.toString(),
                // "UTF-8"));
                StringEntity entity = new StringEntity(json.toString(),
                        "UTF-8");
                entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8
                entity.setContentEncoding(new BasicHeader(
                        HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                retornoweb = EntityUtils.toString(response.getEntity());

                String retz = retornoweb;
                mostraMensagem(String.valueOf(statusCode));
                mostraMensagem(retz);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            idAit = aitx.getId();

            boolean retorno = true;
            int qtdtx = 0;

            FotoDAO fotodao = new FotoDAO(context);

            // tem fotos para transmitir ?
            // if (fotodao.getQtde(idAit) > 0)
            // {
            retorno = false;

            cequip = String.valueOf(idEquipamento);
            sidWebTrans = String.valueOf(aitx.getIdWebTrans());

            ParametroDAO pardao = new ParametroDAO(context);
            Cursor cpar = pardao.getParametros();

            // tenta subir todas as fotos
            Cursor cfotos = fotodao.getImagens(idAit);

            int ntipo = 1;
            String posfixo = "";
            while (cfotos.moveToNext()) {
                // foto = orgao+serieait+ait

                // tipo de foto para o Webtrans
                switch (ntipo) {
                    case 1:
                        posfixo = "Z";
                        break;
                    case 2:
                        posfixo = "P";
                        break;
                    case 3:
                        posfixo = "T";
                        break;
                    case 4:
                        posfixo = "F";
                        break;
                }

                String snumait = "";
                try {
                    snumait = SimpleCrypto.decrypt(info, aitx.getAit());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                Integer numait = Integer.parseInt(snumait);

                String arquivofoto = null;
                try {

                    arquivofoto = cpar.getString(cpar
                            .getColumnIndex("serieait"))
                            + String.valueOf(numait) + posfixo + ".jpg";

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                buffer = cfotos.getBlob((cfotos.getColumnIndex("imagem")));

                // recupera foto e grava na pasta imagens
                this.StoreByteImage(context, buffer, 90, arquivofoto);

                arquivofoto = Environment.getExternalStorageDirectory()
                        + "/imagens/" + arquivofoto;

                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(
                        "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/testa");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                InputStream is = null;
                byte[] buffer = null;
                try {
                    is = new FileInputStream(arquivofoto);

                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                try {
                    buffer = new byte[is.available()];
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    is.read(buffer);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    is.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // Transformando array de bytes em String para enviar ao
                // servidor

                String imagemBase64 = Base64.encodeToString(buffer,
                        Base64.DEFAULT);
                // This is the new shit to deal with MIME
                // MultipartEntity entity = new MultipartEntity();
                // try {

                // StringEntity entity = new StringEntity("teste");
                // entity.setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
                // entity.setContentEncoding(new
                // BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
                // httppost.setHeader(HTTP.TARGET_HOST,"sistemas.cobrasin.com.br");//http:///JsonWcf/JsonWcfService.svc/testa"");
                // httppost.setHeader(HTTP.CONTENT_LEN,"7");
                // httppost.setHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
                // httppost.setEntity(entity);
                // Send request to WCF service
                // DefaultHttpClient httpClient = new DefaultHttpClient();
                // HttpResponse response;
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(
                            "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/st");
                    post.setHeader("Content-type", "application/json");
                    post.setHeader("Accept", "application/json");
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("o", imagemBase64);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
                    HttpResponse response = client.execute(post);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    retornoweb = EntityUtils.toString(response.getEntity());

                    String retz = retornoweb;
                    mostraMensagem(String.valueOf(statusCode));
                    mostraMensagem(retz);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // httppost.setHeader("Content-Type: application/json; charset=utf-8")
                // entity.addPart("x",new StringBody("a"));
                // } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // httppost.setEntity(entity);

                try {
                    String responseString = httpclient.execute(httppost,
                            responseHandler);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
                        "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/GetDataTime/asdf");// urlswebtrans.geturl("foto")
                urlswebtrans.close();

                try {

                    HttpResponse response = httpClient.execute(httpPost,
                            localContext);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    retornoweb = EntityUtils.toString(response.getEntity());
                    String retz = retornoweb;

                    if (statusCode == 200) {
                        qtdtx++; // indica transmissao ok
                    } else {
                        mostraMensagem("Erro:" + statusCode);
                        mostraMensagem(retz);

                    }
                    System.out.println(response.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ntipo++;

            }

            cfotos.close();

            // transmitiu todas ?
            if (qtdtx == fotodao.getQtde(idAit))
                retorno = true;

            cpar.close();

            pardao.close();
            // }

            fotodao.close();

            return retorno;
        }

        // *******************************************************
        // Transmite fotos do ait para o Servidor de FTP COBRASIN
        // *******************************************************
        public void txLogftp() {

            ParametroDAO pardao = new ParametroDAO(context);
            Cursor cpar = pardao.getParametros();
            String nomeArquivoLogtx = "";
            String nomeArquivoLog = "";
            try {
                nomeArquivoLog = "log_"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + "_"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("seriepda"))) + ".txt";
                nomeArquivoLogtx = "log_"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + "_"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("seriepda"))) + "_"
                        + Utilitarios.getDataHora(4) + ".txt";
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String root = Environment.getDataDirectory().getAbsolutePath()
                    + "/data/br.com.cobrasin/databases";

            File fil = new File(root, nomeArquivoLog);

            if (fil.exists()) {

                FTPClient ftp = new FTPClient();

                // Faz a conexão com o servidor ftp
                try {

                    // ftp.connect(cpar.getString(cpar.getColumnIndex("servidorftp")));
                    // // ftp.connect("sistemas.cobrasin.com.br");
                    // 11.04.2013
                    // o correto é a informação vir do WebTrans

                    // Nelson
                    // 31/08/2013 Cristiano pediu pra trocar IP, user e pass
                    // ftp.connect("189.111.250.8");

                    ftp.connect("200.219.198.26");

                    // String senha = "";
                    // try
                    // {
                    // senha =
                    // SimpleCrypto.decrypt(Utilitarios.getInfo(),cpar.getString(cpar.getColumnIndex("senhaftp"))
                    // );
                    // }
                    // catch (Exception ex)
                    // {

                    // }
                    // ftp.login(cpar.getString(cpar.getColumnIndex("usuarioftp")),senha);
                    ftp.login("androidcobra", "androidcobra2014");

                    // tenta criar a pasta fotos
                    try {
                        ftp.makeDirectory("logs");
                    } catch (Exception e) {

                    }

                    FileInputStream fis = new FileInputStream(root + "/"
                            + nomeArquivoLog);

                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

                    boolean conseguiu = false;

                    if (ftp.storeFile("logs/" + nomeArquivoLogtx, fis)) {

                        // se conseguiu transmitir limpa log
                        conseguiu = true;
                        fis.close();
                        fil.delete();
                    } else {
                        fis.close();
                    }

                    try {
                        ftp.disconnect();
                        ftp.logout();
                        ftp.disconnect();

                    } catch (Exception e) {

                    }

                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            cpar.close();
            pardao.close();
        }

        // *******************************************************
        // Transmite os Logs do Talonario p/ o portal Talonario
        // *******************************************************
        public void txLog() {
            LogDAO l = new LogDAO(context);
            // String url =
            // "http://192.168.2.103/JsonWcf/JsonWcfService.svc/InsertLog/";

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

            // String url = urlswebtrans.geturl("urlcripto");

            String url = urlswebtrans.geturl("log");

            urlswebtrans.close();

            HttpClient httpclient = new DefaultHttpClient();
            // HttpPost post = new HttpPost(url);

            try {

                HttpParams httpParameters = new BasicHttpParams();

                // Set the timeout in milliseconds until a connection is
                // established.
                int timeoutConnection = 8000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for
                // data.
                int timeoutSocket = 8000;
                HttpConnectionParams.setSoTimeout(httpParameters,
                        timeoutSocket);

                List<Logs> lg = l.getLogs();
                String urlLog = "";
                Cryptor crip = new Cryptor();
                for (Logs b : lg) {
                    try {
                        urlLog = crip.encrypt(b.getPda())
                                + ";"
                                + crip.encrypt(b.getDataHora().substring(0,
                                10))
                                + ";"
                                + crip.encrypt(b.getDataHora().substring(
                                11, 19)
                                + "-000") + ";"
                                + crip.encrypt(b.getOrgao()) + ";"
                                + crip.encrypt(b.getAgente()) + ";"
                                + crip.encrypt(b.getOperacao()) + ";"
                                + crip.encrypt(b.getStatus());

                        // urlLog=b.getPda()+";"+b.getDataHora().substring(0,10)
                        // +";"+
                        // b.getDataHora().substring(11,19)+"-000"+ ";"+
                        // b.getOrgao()+";"+b.getAgente()+";"+b.getOperacao()+";"+b.getStatus();

                        // url="http://192.168.2.103/JsonWcf/JsonWcfService.svc/getdatatime/teste";
                        url = url
                                + urlLog.replace("/", "BARRA")
                                .replace(":", "-")
                                .replace(" ", "%20")
                                .replace("\n", "")
                                .replace("=", "IGUAL");
                        // l.DeleteLog(b.getId());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    HttpGet httpget = new HttpGet(url);
                    HttpResponse response = httpclient.execute(httpget);

                    // HttpResponse response = httpclient.execute(post);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    // String retornoweb =
                    // EntityUtils.toString(response.getEntity());
                    if (statusCode == 200) {
                        // mostraMensagem(retornoweb);
                        l.DeleteLog(b.getId());
                    } else {

                        try {
                            l.gravalog("Transmissao de log erro- "
                                            + statusCode, "Erro", b.getOrgao(),
                                    b.getPda(), b.getAgente(), context);
                            l.DeleteLog(b.getId());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // ret = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // ret = false;
            }

        }

        private boolean EnviarNotaFiscal(NotaFiscal Nf, Ait aitx) {
            idAit = aitx.getId();

            boolean retorno = true;
            try {
                int qtdtx = 0;

                NotaFiscalDAO Nfdao = new NotaFiscalDAO(context);
                cequip = String.valueOf(idEquipamento);
                sidWebTrans = String.valueOf(aitx.getIdWebTrans());
                // tem fotos para transmitir ?
                // if (Nfdao.ExisteFoto(Nf.getId()) == true)
                // {
                retorno = false;

                ParametroDAO pardao = new ParametroDAO(context);
                Cursor cpar = pardao.getParametros();

                String snumait = "";
                try {
                    snumait = SimpleCrypto.decrypt(info, aitx.getAit());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                Integer numait = Integer.parseInt(snumait);

                String arquivofoto = null;
                try {
                    NotaFiscalDAO NfDAO = new NotaFiscalDAO(context);

                    Cursor cx = NfDAO.getDadosNF(Nf.getId());
                    arquivofoto = idAit + "-" + Nf.getId() + ".jpg";

                    buffer = cx.getBlob(cx.getColumnIndex("Foto"));
                    // recupera foto e grava na pasta imagens
                    this.StoreByteImage(context, buffer, 90, arquivofoto);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    qtdtx++;// conta como upload valido, mas na verdade o
                    // sistema é conseguiu salvar a imagem
                }

                arquivofoto = Environment.getExternalStorageDirectory()
                        + "/imagens/" + arquivofoto;

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
                HttpPost httpPost = new HttpPost(
                        "http://sistemas.cobrasin.com.br/multas-web/talonario/enviarNotaFiscal.action");
                urlswebtrans.close();

                try {

                    MultipartEntity entity = new MultipartEntity(
                            HttpMultipartMode.BROWSER_COMPATIBLE);
                    entity.addPart("user", new StringBody(usuarioWebTrans));
                    entity.addPart("password",
                            new StringBody(senhaWebTrans));
                    entity.addPart("dataSolicitacao", new StringBody(
                            Utilitarios.getDataHora(4)));
                    entity.addPart("equipamento", new StringBody(cequip));
                    // entity.addPart("cliente" , new
                    // StringBody(codMunicipio));
                    entity.addPart("idmulta", new StringBody(sidWebTrans));
                    if (Nfdao.ExisteFoto(Nf.getId()) == true) {
                        entity.addPart("upload", new FileBody(new File(
                                arquivofoto)));
                    }
                    entity.addPart("numeroNota",
                            new StringBody(Nf.getNumeroNota()));
                    entity.addPart("pesoDeclarado",
                            new StringBody(Nf.getPesoDeclarado()));
                    //entity.addPart("pesoExcesso",
                    //		new StringBody(Nf.getPesoExcesso()));
                    //entity.addPart("pesoVeiculo",
                    //		new StringBody(Nf.getPesoVeiculo()));
                    httpPost.setEntity(entity);

                    HttpResponse response = httpClient.execute(httpPost,
                            localContext);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    retornoweb = EntityUtils.toString(response.getEntity());

                    String retz = retornoweb;

                    if (statusCode == 200) {
                        qtdtx++; // indica transmissao ok
                    } else {
                        qtdtx++; // indica transmissao ok
                    }

                    System.out.println(response.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cpar.close();

                pardao.close();
                // }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return retorno;
        }

        // ******************************************************
        // transmite o auto de infração quando idWebTrans = 0
        // ******************************************************
        private String IdMunicipio = "";
        private String Posto = "";

        private void txAit() {
            errotx = false;

            // if ( txFotohttp(ait1))
            // {

            // retirado em 11.04.2013
            // setAutenticacao();

            // *************************
            // 11.04.2013
            // Busca Senha do Agente
            // *************************
            // debug retirar
            String cx = usuarioWebTrans;
            String cz = senhaWebTrans;
            String[] retorno = {"", ""};
            try {
                retorno = Utilitarios.buscaLoginSenha(
                        SimpleCrypto.decrypt(info, ait1.getAgente()),
                        context);
                usuarioWebTrans = retorno[0];
                senhaWebTrans = retorno[1];

                cx = usuarioWebTrans;
                cz = senhaWebTrans;

            } catch (Exception ex) {

            }
            setAutenticacao();

            // nvps.add(new BasicNameValuePair("idmulta", sidWebTrans ) ) ;

            nvps.add(new BasicNameValuePair("aiSerie", serieait));

            try {

                // *******************************
                // trata o cancelamento
                // *******************************
                String cancelou = " ";

                cancelou = SimpleCrypto.decrypt(info, ait1.getCancelou());

                // ********************
                // ait está cancelado
                // ********************
                if (!cancelou.equals("NAO")) {
                    // *******************************
                    // A solicitacao é o Cancelamento
                    // *******************************
                    nvps.add(new BasicNameValuePair("multaCancelada",
                            "true"));
                    nvps.add(new BasicNameValuePair("motivoCancelamento",
                            //URLEncoder.encode(
                            SimpleCrypto.decrypt(info, ait1.getMotivo())));
                    nvps.add(new BasicNameValuePair("dataSolicitacao",
                            Utilitarios.getDataSolicitacao(SimpleCrypto
                                    .decrypt(info, ait1.getCancelou()))));
                } else {
                    // *************************************
                    // A solicitacao é o Encerramento
                    // *************************************
                    nvps.add(new BasicNameValuePair("dataSolicitacao",
                            Utilitarios.getDataSolicitacao(SimpleCrypto
                                    .decrypt(info, ait1.getEncerrou()))));
                }

                nvps.add(new BasicNameValuePair("equipamento", cequip));

                nvps.add(new BasicNameValuePair("aiNumero", SimpleCrypto
                        .decrypt(info, ait1.getAit())));

                if (listaenq.size() > 0) {
                    nvps.add(new BasicNameValuePair("codEnquadramento",
                            SimpleCrypto.decrypt(info, aitenq1.getCodigo())));
                }

                String dataHoraInfracao = SimpleCrypto.decrypt(info,
                        ait1.getData());

                // 30/03/2012
                dataHoraInfracao = dataHoraInfracao.substring(0, 2)
                        + dataHoraInfracao.substring(3, 5)
                        + dataHoraInfracao.substring(6, 10);

                String horaInfracao = SimpleCrypto.decrypt(info,
                        ait1.getHora());

                dataHoraInfracao += horaInfracao.substring(0, 2)
                        + horaInfracao.substring(3, 5) + "00";// +
                // horaInfracao.substring(6,
                // 8);

                String dataHoraPreenchimento;

                try {

                    dataHoraPreenchimento = SimpleCrypto.decrypt(info,
                            ait1.getdtEdit());

                    dataHoraPreenchimento = dataHoraPreenchimento
                            .substring(0, 2)
                            + dataHoraPreenchimento.substring(3, 5)
                            + dataHoraPreenchimento.substring(6, 10);

                    String horaPreenchimento = SimpleCrypto.decrypt(info,
                            ait1.gethrEdit());

                    dataHoraPreenchimento += horaPreenchimento.substring(0,
                            2) + horaPreenchimento.substring(3, 5) + "00";// +
                    // horaPreenchimento.substring(6,
                    // 8);
                } catch (Exception e) {
                    dataHoraPreenchimento = dataHoraInfracao;
                }

                try {
                    nvps.add(new BasicNameValuePair("dataHoraInfracao",
                            dataHoraPreenchimento));
                    nvps.add(new BasicNameValuePair(
                            "idLogradouro",
                            SimpleCrypto.decrypt(info, ait1.getLogradouro())));

                    nvps.add(new BasicNameValuePair("logradouroNumero",
                            Utilitarios.removeAcentos(SimpleCrypto.decrypt(info,
                                    ait1.getLogradouronum()))));

                    nvps.add(new BasicNameValuePair("observacoes",
                            // URLEncoder.encode(
                            SimpleCrypto.decrypt(info, ait1.getObservacoes()).replace("%", "porcento")
                                    .replace("&", " e ")));
                    // "ISO-8859-1")));
                    nvps.add(new BasicNameValuePair("placa", SimpleCrypto
                            .decrypt(info, ait1.getPlaca())));
                    nvps.add(new BasicNameValuePair("codTipo", SimpleCrypto
                            .decrypt(info, ait1.getTipo())));
                    nvps.add(new BasicNameValuePair("codMunicipioPlaca",
                            codMunicipio));
                    if (ait1.getUfVeiculo() != null && ait1.getUfVeiculo() != "" && ait1.getUfVeiculo() != "null") {
                        nvps.add(new BasicNameValuePair("ufPlaca", SimpleCrypto
                                .decrypt(info, ait1.getUfVeiculo())));
                    }
                    nvps.add(new BasicNameValuePair("codEspecie",
                            SimpleCrypto.decrypt(info, ait1.getEspecie())));
                    nvps.add(new BasicNameValuePair("infratorNome",
                            //URLEncoder.encode(
                            SimpleCrypto.decrypt(info, ait1.getNome())));
                    //"ISO-8859-1")));
                    nvps.add(new BasicNameValuePair("infratorCnhNumero",
                            URLEncoder.encode(SimpleCrypto.decrypt(info,
                                    ait1.getPgu()))));

                    String tipoinfrator = "";
                    try {
                        tipoinfrator = URLEncoder.encode(SimpleCrypto
                                .decrypt(info, ait1.getTipoinfrator()));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    if (tipoinfrator.equals("CNH")) {
                        nvps.add(new BasicNameValuePair("infratorCnhUf",
                                URLEncoder.encode(SimpleCrypto.decrypt(
                                        info, ait1.getUf()))));
                        nvps.add(new BasicNameValuePair("infratorCpfCnpj",
                                URLEncoder.encode(SimpleCrypto.decrypt(
                                        info, ait1.getCpf()))));
                    } else if (tipoinfrator.equals("PID")) {
                        nvps.add(new BasicNameValuePair(
                                "infratorEstrangeiroPid", URLEncoder
                                .encode(SimpleCrypto.decrypt(info,
                                        ait1.getPid()))));
                        nvps.add(new BasicNameValuePair(
                                "infratorEstrangeiroIdentificacao",
                                URLEncoder.encode(SimpleCrypto.decrypt(
                                        info, ait1.getPassaporte()))));
                    } else {
                        nvps.add(new BasicNameValuePair("infratorCpfCnpj",
                                URLEncoder.encode(SimpleCrypto.decrypt(
                                        info, ait1.getCpf()))));
                    }

                    nvps.add(new BasicNameValuePair(
                            "idmedidaAdministrativa", SimpleCrypto.decrypt(
                            info, ait1.getMedidaadm())));
                    try {

                        nvps.add(new BasicNameValuePair("codPais", SimpleCrypto
                                .decrypt(info, ait1.getPais())));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    nvps.add(new BasicNameValuePair("marcaModelo",
                            //URLEncoder.encode(
                            SimpleCrypto.decrypt(info, ait1.getMarca())));
                    //     "ISO-8859-1")));
                    nvps.add(new BasicNameValuePair(
                            "dataHoraPreenchimento", dataHoraInfracao));
                    if (SimpleCrypto.decrypt(info, ait1.getTipoait())
                            .equals("5")) {
                        NotaFiscalDAO NfDAO = new NotaFiscalDAO(context);
                        List<NotaFiscal> NfLista = NfDAO
                                .GetNotasAit(ait1.getId());

                        if (NfLista.size() == 1) {
                            try {

                                String Nome = SimpleCrypto.decrypt(info,
                                        ait1.getNome_embarcador());
                                String CPFCNPJ = SimpleCrypto.decrypt(info,
                                        ait1.getCpfCnpj_embarcador());
                                String Endereco = SimpleCrypto.decrypt(
                                        info, ait1.getEndereco_embarcador());

                                String Bairro = SimpleCrypto.decrypt(
                                        info, ait1.getBairro_embarcador());

                                String IdMunicipio = SimpleCrypto.decrypt(
                                        info, ait1.getIdMunicipio_embarcador());

                                MunicipioDAO MuDAO = new MunicipioDAO(context);
                                String IdProdespMunicipio = MuDAO.GetIdProdesp(IdMunicipio);
                                MuDAO.close();

                                nvps.add(new BasicNameValuePair(
                                        "embarcadorNome", Nome));

                                nvps.add(new BasicNameValuePair(
                                        "embarcadorCpfCnpj", CPFCNPJ));

                                nvps.add(new BasicNameValuePair(
                                        "embarcadorEndereco", Endereco + ", " + Bairro));
                                nvps.add(new BasicNameValuePair(
                                        "embarcadorCep", ""));
                                nvps.add(new BasicNameValuePair(
                                        "embarcadorMunicipio", IdProdespMunicipio));

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                        if (NfLista.size() > 1) {
                            try {

                                String Nome = SimpleCrypto.decrypt(info,
                                        ait1.getNome_transportador());
                                String CPFCNPJ = SimpleCrypto.decrypt(info,
                                        ait1.getCpfCnpj_transportador());
                                String Endereco = SimpleCrypto.decrypt(
                                        info,
                                        ait1.getEndereco_transportador());

                                String Bairro = SimpleCrypto.decrypt(
                                        info, ait1.getBairro_transportador());

                                String IdMunicipio = SimpleCrypto.decrypt(
                                        info, ait1.getIdMunicipio_transportador());

                                MunicipioDAO MuDAO = new MunicipioDAO(context);
                                String IdProdespMunicipio = MuDAO.GetIdProdesp(IdMunicipio);
                                MuDAO.close();

                                nvps.add(new BasicNameValuePair(
                                        "transportadorNome", Nome));

                                nvps.add(new BasicNameValuePair(
                                        "transportadorCpfCnpj", CPFCNPJ));

                                nvps.add(new BasicNameValuePair(
                                        "transportadorEndereco", Endereco + ", " + Bairro));
                                nvps.add(new BasicNameValuePair(
                                        "transportadorCep", ""));
                                nvps.add(new BasicNameValuePair(
                                        "transportadorMunicipio", IdProdespMunicipio));

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                        NfDAO.close();
                    }

                } catch (Exception e1) {
                    String Erro = e1.getMessage();
                }

                // *********************************************************************************
                // Ocorreu IMPRESSAO ?
                // *********************************************************************************
                String imprimiu = "";

                try {

                    imprimiu = SimpleCrypto.decrypt(info,
                            ait1.getImpresso());

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                if (!imprimiu.contains("NAO")) {

                    nvps.add(new BasicNameValuePair("dataHoraImpressao",
                            Utilitarios.getDataSolicitacao(imprimiu)));

                }

                // dataHoraImpressao
                String tipLog = SimpleCrypto.decrypt(info,
                        ait1.getLogradourotipo());

                if (tipLog.length() > 0) {
                    switch (Integer.parseInt(tipLog)) {
                        case 1:
                            nvps.add(new BasicNameValuePair(
                                    "logradouroComplemento", "Oposto"));
                            break;
                        case 2:
                            nvps.add(new BasicNameValuePair(
                                    "logradouroComplemento", "Defronte"));
                            break;
                        case 3:
                            nvps.add(new BasicNameValuePair(
                                    "logradouroComplemento", "Ao Lado de"));
                            break;
                    }
                }

                if (SimpleCrypto.decrypt(info, ait1.getEquipamento())
                        .length() > 0) {

                    nvps.add(new BasicNameValuePair("equipamentoAuxiliar",
                            SimpleCrypto.decrypt(info,
                                    ait1.getEquipamento())));
                    nvps.add(new BasicNameValuePair("medicaoConsiderada",
                            Utilitarios.formatar(SimpleCrypto.decrypt(info,
                                    ait1.getMedicaocon()))));
                    nvps.add(new BasicNameValuePair("medicaoRegistrada",
                            Utilitarios.formatar(SimpleCrypto.decrypt(info,
                                    ait1.getMedicaoreg()))));
                    nvps.add(new BasicNameValuePair("limiteRegulamentado",
                            Utilitarios.formatar(SimpleCrypto.decrypt(info,
                                    ait1.getLimitereg()))));
                }

                if (!SimpleCrypto.decrypt(info, ait1.getLogradouro2())
                        .contains("NAO")) {
                    nvps.add(new BasicNameValuePair("idCruzamento",
                            SimpleCrypto.decrypt(info,
                                    ait1.getLogradouro2())));
                }


                //nvps.add(new BasicNameValuePair("limitePermitido",
                //		SimpleCrypto.decrypt(info,
                //				ait1.getLimitePermitido_excesso())));

                if (ait1.getPesoDeclarado_excesso() != null)
                    nvps.add(new BasicNameValuePair("pesoDeclaradoVeiculo",
                            SimpleCrypto.decrypt(info,
                                    ait1.getPesoDeclarado_excesso())));

                if (ait1.getTara_excesso() != null)
                    nvps.add(new BasicNameValuePair("pesoTaraVeiculo",
                            SimpleCrypto.decrypt(info,
                                    ait1.getTara_excesso())));

                //nvps.add(new BasicNameValuePair("medicaoRegistrada",
                //		SimpleCrypto.decrypt(info,
                //				ait1.getMedicaoRegistrada_excesso())));

                if (ait1.getExcessoConstatado_excesso() != null)
                    nvps.add(new BasicNameValuePair("medicaoExcedida",
                            SimpleCrypto.decrypt(info,
                                    ait1.getExcessoConstatado_excesso())));

                // if (cAgente.moveToFirst())
                //	{
                try {
                    //Posto = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("POSTO")));
                    nvps.add(new BasicNameValuePair("postoOperacaoAgente", Posto));
                } catch (Exception e) {
                    // TODO: handle exception

                }

                try {
                    //IdMunicipio = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("IdMunicipio")));
                    nvps.add(new BasicNameValuePair("codMunicipioInfracao", IdMunicipio));
                } catch (Exception e) {
                    // TODO: handle exception

                }

                String tipoinfrator = "";
                try {
                    tipoinfrator = SimpleCrypto.decrypt(info, ait1.getTipoinfrator());
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (tipoinfrator == null) {

                    String PPD = SimpleCrypto.decrypt(info, ait1.getPpd_condutor());

                    if (PPD.equals("S")) {
                        nvps.add(new BasicNameValuePair("infratorModeloCh", "PPD"));
                    }
                    if (PPD.equals("N")) {
                        nvps.add(new BasicNameValuePair("infratorModeloCh", "CNH"));
                    }
                } else {
                    if (tipoinfrator.contains("CNH")) {
                        String PPD = SimpleCrypto.decrypt(info, ait1.getPpd_condutor());
                        if (PPD.equals("S")) {
                            nvps.add(new BasicNameValuePair("infratorModeloCh", "PPD"));
                        }
                        if (PPD.equals("N")) {
                            nvps.add(new BasicNameValuePair("infratorModeloCh", "CNH"));
                        }

                    }
                    if (tipoinfrator.contains("PID")) {
                        nvps.add(new BasicNameValuePair("infratorModeloCh", "PID"));
                    }
                }


            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            long idWebTrans = 0;

            // transmite para o WebTrans
            boolean leu = carregaDados("cadait");

            // verifica se gravacao foi correta
            if (leu) {

                AitDAO aitdao = new AitDAO(context);

                // *******************************************
                // 07.01.2012
                // Transmite a foto para o Servidor de FTP
                // *******************************************
                // if (txFotoftp(ait1))
                // {
                try {

                    // jsonArray = new JSONArray(retornoweb);
                    json1 = jsonArray.getJSONObject(0);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block

                    // nao retornou
                    errotx = true;
                    e1.printStackTrace();
                }

                try {
                    idWebTrans = json1.getLong("idmulta");

                    // atualiza
                    ait1.setIdWebTrans(idWebTrans);
                    try {
                        informUsr("Transmitindo Nota Fiscal do AIT...");
                        if (SimpleCrypto.decrypt(info, ait1.getTipoait())
                                .equals("5")) {
                            NotaFiscalDAO NfDAO = new NotaFiscalDAO(context);
                            List<NotaFiscal> NfLista = NfDAO
                                    .GetNotasAit(ait1.getId());
                            for (NotaFiscal Nf : NfLista) {
                                EnviarNotaFiscal(Nf, ait1);

                            }
                            NfDAO.close();
                        }
                    } catch (Exception e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    if (txFotohttp(ait1)) {
                        // atualiza FLAG para transmitido
                        aitdao.atualizaTx(idAit, true, idWebTrans);
                    } else {
                        errotx = true;
                    }

                    /*informUsr("Transmitindo Assinatura do AIT...");
                    SincronizaAssinaturaAIT(ait1);*/

                } catch (JSONException e) {
                    // nao retornou
                    errotx = true;
                }
                // }
                // else
                // {
                // errotx = true;
                // }

                aitdao.close();

            } else {
                errotx = true; // indica pois nao consegui transmitir
            }

            // }

        }

        // ************************************************
        // TRATAMENTO QUANDO JÁ POSSUI ID WEBTRANS
        // ************************************************
        private void txAitNovamente() {
            // TRATAMENTO QUANDO JÁ POSSUI ID WEBTRANS

            errotx = false;

            nvps.clear();

            // *************************
            // 11.04.2013
            // Busca Login/Senha do Agente
            // *************************
            String[] retorno = {"", ""};
            try {
                retorno = Utilitarios.buscaLoginSenha(
                        SimpleCrypto.decrypt(info, ait1.getAgente()),
                        context);
                usuarioWebTrans = retorno[0];
                senhaWebTrans = retorno[1];
            } catch (Exception ex) {

            }

            setAutenticacao();

            // *********************************************************************************
            // Retransmite as fotos
            // *********************************************************************************
            // se conseguiu transmitir fotos atualiza tx
            if (!txFotohttp(ait1))
                errotx = true;

            // *********************************************************************************
            // Ocorreu REIMPRESSAO ?
            // *********************************************************************************
            String imprimiu = "";

            try {

                imprimiu = SimpleCrypto.decrypt(info, ait1.getImpresso());

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (imprimiu.contains("R")) {
                nvps.clear();
                setAutenticacao();

                nvps.add(new BasicNameValuePair("dataSolicitacao",
                        Utilitarios.getDataSolicitacao(imprimiu)));
                nvps.add(new BasicNameValuePair("equipamento", cequip));
                nvps.add(new BasicNameValuePair("idmulta", sidWebTrans));

                // envia a URL especifica para reimpressao
                //	if (!carregaDados("cadimp"))
                //		errotx = true;
            }
            try {
                if (!SimpleCrypto.decrypt(info, ait1.getLogradouro2())
                        .contains("NAO")) {
                    nvps.add(new BasicNameValuePair("idCruzamento",
                            SimpleCrypto.decrypt(info,
                                    ait1.getLogradouro2())));
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // *********************************************************************************
            // trata o cancelamento
            // *********************************************************************************
            String cancelou = " ";
            String transmitiu = " ";
            try {
                cancelou = SimpleCrypto.decrypt(info, ait1.getCancelou());
                transmitiu = SimpleCrypto.decrypt(info,
                        ait1.getTransmitido());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // ait está cancelado ?
            if (!cancelou.equals("NAO")) {
                // Somente entra se canceou após transmitir
                if (!Utilitarios.cancelouAntes(cancelou, transmitiu)) {

                    nvps.clear();
                    setAutenticacao();

                    nvps.add(new BasicNameValuePair("dataSolicitacao",
                            Utilitarios.getDataSolicitacao(cancelou)));

                    nvps.add(new BasicNameValuePair("idmulta", sidWebTrans));

                    nvps.add(new BasicNameValuePair("equipamento", cequip));

                    try {
                        nvps.add(new BasicNameValuePair(
                                "motivoCancelamento", URLEncoder
                                .encode(SimpleCrypto.decrypt(info,
                                        ait1.getMotivo()))));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (!carregaDados("cancait"))
                        errotx = true;
                }
            }

            if (!errotx) {
                AitDAO aitdao = new AitDAO(context);
                aitdao.atualizaTx(idAit, true, ait1.getIdWebTrans());
                aitdao.close();
            }

        }

        // ************************************************
        // transmite Login/Logoff para o WebTrans
        // ************************************************
        private void txLoginLogoff() {
            // ---------------------------------------------
            // 09.03.2012
            // Cria array JSON dos logs
            // ---------------------------------------------
            String[] retorno = {"", ""};
            try {
                retorno = Utilitarios.buscaLoginSenha(salvaAgente, context);
                usuarioWebTrans = retorno[0];
                senhaWebTrans = retorno[1];
            } catch (Exception ex) {
            }

            LogDAO logdao = new LogDAO(context);
            List<Logs> lista1 = logdao.getLogsLogin();
            Long idLog;
            nvps = new ArrayList<NameValuePair>();
            int i = 1;
            boolean errotxlogs = false;
            for (Logs logs : lista1) {
                informUsr("Enviando Login:" + String.valueOf(i) + " de "
                        + String.valueOf(lista1.size()));
                i++;
                nvps.clear();
                idLog = logs.getId();
                // setAutenticacao();
                nvps.add(new BasicNameValuePair("cliente", codMunicipio));
                nvps.add(new BasicNameValuePair("user", usuarioWebTrans));
                nvps.add(new BasicNameValuePair("password", senhaWebTrans));
                nvps.add(new BasicNameValuePair("equipamento", cequip));
                // long idwt = logs.getIdwebtrans();

                String dataHoraSolicitacao = "";
                // String horaSolicitacao = "";
                try {

                    dataHoraSolicitacao = logs.getDataHora();

                } catch (Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }

                nvps.add(new BasicNameValuePair("dataSolicitacao",
                        dataHoraSolicitacao.replace("/", "")
                                .replace(":", "").replace(" ", "")));
                boolean ret = false;

                // login ?
                try {
                    if (logs.getOperacao().equals("Login"))
                        ret = carregaDados("login");
                    else
                        ret = carregaDados("logoff");
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                if (ret) {

                    try {

                        // jsonArray = new JSONArray(retornoweb);
                        String retx = retornoweb;
                        json1 = jsonArray.getJSONObject(0);

                        // nao gravou algum ?
                        if (!errotxlogs) {
                            if (json1.getString("success").toString()
                                    .equals("FALSE"))
                                errotxlogs = true;
                            else
                                logdao.DeleteLog(idLog);
                        }

                    } catch (JSONException e) {
                        errotxlogs = true;
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }

            // algm erro ?
            if (!errotxlogs) {
                // limpa todos logs
                // logdao.limpaReg();

                // regrava ultimo log !
                // Logs logx = new Logs();
                // logx = (Logs) lista1.get(lista1.size()-1);
                // logdao.gravalog(logx);
            }

            logdao.close();

        }

        // ************************************************************
        // 07.02.2011 -> limpa todos os registros com mais de 24 horas
        // ************************************************************
        private void limpaAitsTx() {
            AitDAO aitdao = new AitDAO(context);
            AitEnquadramentoDAO aitenqdao = new AitEnquadramentoDAO(context);

            lista = aitdao.getListaTransmitida();

            FotoDAO fotodao = new FotoDAO(context);

            for (int nx = 0; nx < lista.size(); nx++) {

                aitdao.delete(lista.get(nx).getId());
                aitenqdao.delete(lista.get(nx).getId());
                fotodao.delete(lista.get(nx).getId());
            }

            aitdao.close();
            aitenqdao.close();
            fotodao.close();

        }

        // *******************************
        // Carrega as tabelas do Webtrans
        // *******************************
        private void carregaTabelas() {

            limpaArqWebTrans();
            // sinc = new SincronismoWebTrans(context,"4");
            // sinc.Sincronizar();

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
            String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");
            urlswebtrans.close();
            // DownloadTabelasWebtrans DownloadTabWeb = new
            // DownloadTabelasWebtrans();
            // String s=DownloadTabWeb.InstalImei(IMEI);
            ParametroDAO p = new ParametroDAO(context);
            String ret = "ok";
            IMEI = p.getIMEI();
            p.close();
            // String url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

            // url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

            senhaWebTrans = MD5Util.criptografar("cobratalonario");
            usuarioWebTrans = "talonario";

            // UrlsWebTransDAO urlswebtrans = new
            // UrlsWebTransDAO(DownloadTabelasWebtrans.this);
            // url = urlswebtrans.geturl("urlcripto");
            String urlBase = urlswebtrans.geturl("imei");
            urlswebtrans.close();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            // buscar em parametros!!!!!
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            // ***********************************************************************
            // TESTE DE CRIPTOGRAFIA - 10.04.2012
            // ***********************************************************************
            urlBase += "?user=" + usuarioWebTrans + "&password="
                    + senhaWebTrans + "&dataSolicitacao="
                    + Utilitarios.getDataHora(4) + "&imei=" + IMEI;
            nvps.add(new BasicNameValuePair("checkSum", MD5Util
                    .criptografar(urlBase)));

            try {
                urlBase = SimpleCrypto.encrypt(info, urlBase);
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));

            // *****************************************************************************
            // .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
            // *****************************************************************************
            // progress = ProgressDialog.show( CobrasinAitActivity.this,
            // "Aguarde..." , "Autenticando IMEI!!!",true,true);

            try {
                post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e1) {

                ret = "service error";
                // TODO Auto-generated catch block
                e1.printStackTrace();

            }

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams
                    .setSoTimeout(httpParameters, timeoutSocket);
            HttpResponse response = null;
            String retornoweb = "";
            try {
                response = httpclient.execute(post);
                retornoweb = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Parametro pa = new Parametro();

            if (statusCode == 200) {
                try {
                    try {
                        retornoweb = SimpleCrypto.decrypt(
                                Utilitarios.getInfo(), retornoweb);

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
                        // ParametroDAO p =new
                        // ParametroDAO(CobrasinAitActivity.this);

                        try {
                            if (GetServiceClientes()) {
                                json1 = jsonArray.getJSONObject(0);

                                String clienteativo = "S";
                                // Prefeitura Ativa ?
                                if (json1.getString("status").toUpperCase()
                                        .contains("INATIVO"))
                                    clienteativo = "N";

                                pa.setPrefativa(SimpleCrypto.encrypt(info,
                                        clienteativo));
                                clienteativo = "S";
                                if (j.getString("status").toUpperCase()
                                        .contains("INATIVO"))
                                    clienteativo = "N";

                                pa.setAtivo(SimpleCrypto.encrypt(info,
                                        clienteativo));
                                try {
                                    p.SetStatusAtivo(pa.getAtivo(),
                                            pa.getPrefativa(),
                                            pa.getSeriepda());
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
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

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    ret = "json error";
                    e.printStackTrace();

                }

            } else {

                ret = "service error";

            }

            while (!terminouWebTrans())
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        private String Orgao = "";

        public boolean trataAgentes() {
            // TODO Auto-generated method stub

            boolean ret = true;

            // Pega dados antes de excluir todas os registros
            ParametroDAO paDao = new ParametroDAO(context);

            Cursor c = paDao.getParametros();

            // dados para comunicação com o WebTrans
            // pega novamente aqui pois quando a classe é chamada com
            // parâmetro "2" não carrega os equipamentos
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
                codMunicipio = xcod;// .subSequence(1, 5).toString();
                // //265810

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

                    if (json1.getString("status").toUpperCase()
                            .contains("INATIVO"))
                        agente.setAtivo("N");

                } catch (JSONException e) {

                    // TODO Auto-generated catch block
                    ret = false;

                    // if ( tipoperacao.contains("1") ) errofatal =true;
                    // e.printStackTrace();
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
                            if (json1.getString("status").contains(
                                    "INATIVO"))
                                agente.setAtivo(SimpleCrypto.encrypt(info,
                                        "N"));

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

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        agentedao.insere(agente);

                    } catch (JSONException e) {

                        // TODO Auto-generated catch block
                        ret = false;
                        // errofatal =true;
                        e.printStackTrace();
                    }
                }

                agentedao.close();
            }
            return ret;
        }

        public String InstalImei() {
            String ret = "ok";

            // String url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

            // url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

            senhaWebTrans = MD5Util.criptografar("cobratalonario");
            usuarioWebTrans = "talonario";

            // trataEquipamentos("3");

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

            String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");

            String urlBase = urlswebtrans.geturl("imei");

            urlswebtrans.close();

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            urlBase += "?user=" + usuarioWebTrans + "&password="
                    + senhaWebTrans + "&dataSolicitacao="
                    + Utilitarios.getDataHora(4) + "&imei=" + IMEI;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                try {
                    response = httpclient.execute(new HttpGet(urlBase));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                HttpPost post = new HttpPost(url);

                // buscar em parametros!!!!!
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                // ***********************************************************************
                // TESTE DE CRIPTOGRAFIA - 10.04.2012
                // ***********************************************************************
                nvps.add(new BasicNameValuePair("checkSum", MD5Util
                        .criptografar(urlBase)));

                try {
                    urlBase = SimpleCrypto.encrypt(info, urlBase);
                } catch (Exception e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }

                nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));

                // *****************************************************************************
                // .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
                // *****************************************************************************

                try {
                    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                } catch (UnsupportedEncodingException e1) {

                    ret = "service error";
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
                    // in milliseconds which is the timeout for waiting for
                    // data.
                    int timeoutSocket = 20000;
                    HttpConnectionParams.setSoTimeout(httpParameters,
                            timeoutSocket);

                    // HttpResponse response = httpclient.execute(httpget);
                    response = httpclient.execute(post);


                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = "service error";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ret = "service error";
                }
            }
            try {

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                String retornoweb = EntityUtils.toString(response
                        .getEntity());

                if (statusCode == 200) {

                    try {

                        try {
                            retornoweb = SimpleCrypto.decrypt(
                                    Utilitarios.getInfo(), retornoweb);

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        /*
                         * if (tipotransacao.equals("clientes")) {
                         * retornoweb = "[" + retornoweb + "]"; }
                         */

                        jsonArray = new JSONArray(retornoweb);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        ret = "json error";
                        e.printStackTrace();
                    }

                } else {

                    // ******************************
                    // 18.05.2012
                    // grava resposta do WEBTRANS
                    // ******************************
                    /*
                     * String mensz = "Sinc.Tabelas Retorno: " +
                     * String.format("%d",statusCode) + " - "+ retornoweb;
                     */

                    // Utilitarios.gravaLog(mensz, context);
                    ret = "service error";
                }
            } catch (Exception e) {
            }

            return ret;
        }

        @Override
        protected String doInBackground(String... params) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            Agente = params[0];


            // aviso = Toast.makeText(context, "Todos aits enviados com sucesso!",
            // Toast.LENGTH_LONG);

            //new Thread(new Runnable() {

            // ************************************************
            // Verifica a dtHrSincronia e sincroniza as obs obrigatorias
            // ************************************************


            // ----------------------------------------
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SincronizaEnquadramentoObsObrigatorio();

            // cria JSON do parametro
            ParametroDAO pardao = new ParametroDAO(context);

            Cursor c = pardao.getParametros();

            c.moveToFirst();

            try {
                serieait = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("serieait")));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // dados para comunicação com o WebTrans
            // String idEquipamentoString = "";
            try {
                usuarioWebTrans = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("usuariowebtrans")));
                codMunicipio = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("orgaoautuador")))
                        .toString(); // .subSequence(1, 5)265810

                Orgao = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("sigla")));
                // idEquipamento =
                // c.getLong(c.getColumnIndex("idwebtrans"));//só uso para
                // converter em string e dps descriptografar abaixo
                idEquipamento = Long.parseLong(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("idwebtrans"))));
                // Long.parseLong(SimpleCrypto.decrypt(info,
                // idEquipamentoString));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            senhaWebTrans = c.getString(c.getColumnIndex("senhawebtrans"));

            cequip = String.valueOf(idEquipamento);

            c.close();

            pardao.close();

            String cx, cz;
            cx = usuarioWebTrans;
            cz = senhaWebTrans;

            // **********************************************
            // 29.06.2012
            // **********************************************
            // Verifica se existe algum bloqueio do WebTrans
            // **********************************************
            // informUsr("Sincronizando com o WebTrans...");
            // carregaTabelas();

            // verifica Bloqueio do PDA/PREFEITURA
            pardao = new ParametroDAO(context);
            c = pardao.getParametros();
            c.moveToFirst();
            String pdaAtivo = "";
            String prefAtiva = "";
            String Pda = "";
            String GeraPdf = "";
            try {
                pdaAtivo = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ativo")));
                prefAtiva = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("prefativa")));
                Pda = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("seriepda")));
                GeraPdf = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("modpdf")));
                IMEI = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("IMEI")));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            c.close();

            pardao.close();

            String mensErro = "";
            try {
                informUsr("Verificando permissão do agente");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            AgenteDAO Adao = new AgenteDAO(context);
            Cursor cAgente = Adao.GetDadosAgente(Agente);
            //	  String Posto = "";
            //	  String IdMunicipio = "";
            // if (cAgente.moveToFirst())
            //	{
            try {
                Posto = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("POSTO")));
            } catch (Exception e) {
                // TODO: handle exception

            }


            //	}


            carregaDados2("agentes");
            if (!trataAgentes()) {
                mensErro += " / PNão foi possivel obter os dados do agente, tente novamente!";

            }

            if (!Utilitarios.agenteAtivo(context, salvaAgente)) {
                mensErro = "Agente bloqueado! ";
            }
            try {
                IdMunicipio = SimpleCrypto.decrypt(info, cAgente.getString(cAgente.getColumnIndex("IdMunicipio")));
            } catch (Exception e) {
                // TODO: handle exception

            }

            Agente age = new Agente();
            try {
                age.setPosto(SimpleCrypto.encrypt(info, Posto));
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            try {
                age.setIdMunicipio(SimpleCrypto.encrypt(info, IdMunicipio));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                age.setCodigo(SimpleCrypto.encrypt(info, Agente));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Adao.altera(age);
            Adao.close();
            try {
                informUsr("Verificando permissão do equipamento");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (InstalImei().equals("ok")) {
                JSONObject j;
                try {
                    j = jsonArray.getJSONObject(0);

                    // ParametroDAO p =new
                    // ParametroDAO(CobrasinAitActivity.this);
                    Parametro pa = new Parametro();
                    String StatusPDA = "S";
                    if (j.getString("status").toUpperCase()
                            .contains("INATIVO"))
                        StatusPDA = "N";
                    pa.setAtivo(StatusPDA);
                    pardao.SetPdaAtivo(
                            SimpleCrypto.encrypt(info, StatusPDA),
                            SimpleCrypto.encrypt(info, Pda));
                    pdaAtivo = StatusPDA;
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                mensErro += " / PNão foi possivel obter os dados do equipamento, tente novamente!";

            }

            if (pdaAtivo.contains("N")) {
                if (mensErro.length() > 0)
                    mensErro += " / PDA bloqueado!";

                else
                    mensErro = "PDA bloqueado! ";
            }

            if (prefAtiva.contains("N")) {
                if (mensErro.length() > 0)
                    mensErro += " / Prefeitura bloqueada!";
                else
                    mensErro = "Prefeitura bloqueada!";
            }

            // pode transmitir ?
            if (mensErro.length() > 0) {
                mensErro += " - Transmissão não autorizada...";
                progress.dismiss();
                mostraMensagem(mensErro);
            } else {

                // ******************************
                // Envie cada ait para o site
                // ******************************
                AitDAO dao = new AitDAO(context);
                List<Ait> lista = dao.getListaCompleta();
                dao.close();

                informUsr("Transmitindo Aits...");
                int nx = 0;
                for (nx = 0; nx < lista.size(); nx++) // while (
                // i.hasNext())
                {

                    ait1 = lista.get(nx);

                    long idAit = ait1.getId();

                    // cria JSON dos Enquadramentos
                    AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
                            context);

                    // List<AitEnquadramento> listaenq =
                    // aitenq.getListaCompleta();
                    listaenq = aitenq.getLista2(idAit);
                    aitenq.close();

                    nvps = new ArrayList<NameValuePair>();

                    aitenq1 = null;

                    if (listaenq.size() > 0) {
                        aitenq1 = listaenq.get(0);
                    }

                    sidWebTrans = String.valueOf(ait1.getIdWebTrans());

                    /*
                     * try { txAitPdf(Utilitarios.getDataHora(4),
                     * String.valueOf(ait1.getId()),
                     * SimpleCrypto.decrypt(info, ait1.getImpressao()),
                     * SimpleCrypto.decrypt(info,ait1.getAit()),
                     * Pda,codMunicipio); } catch (Exception e) { // TODO
                     * Auto-generated catch block e.printStackTrace(); }
                     */
                    try {
                        informUsr("Transmitindo AIT: "
                                + SimpleCrypto.decrypt(info, ait1.getAit()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (ait1.getIdWebTrans() == 0) {
                        txAit();
                    } else {

                        txAitNovamente();
                    }

                }

                if (GeraPdf.equals("TRUE")) {
                    AitDAO dao1 = new AitDAO(context);
                    // List<Ait> lista1;
                    try {
                        lista = dao1.getListaAitImpresso();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    dao1.close();

                    AitDAO a = new AitDAO(context);
                    // informUsr("Transmitindo PDF...");
                    int n = 0;
                    for (n = 0; n < lista.size(); n++) // while (
                    // i.hasNext())
                    {
                        ait1 = lista.get(n);
                        try {
                            if ((ait1.getSendPdf() == null || !ait1.getSendPdf().equals(SimpleCrypto.encrypt(info, "TRUE"))) && ait1.getIdWebTrans() != 0) {
                                informUsr("Transmitindo PDF: "
                                        + SimpleCrypto.decrypt(info,
                                        ait1.getAit()));

                                try {
                                    String retorno[] = Utilitarios.buscaLoginSenha(
                                            SimpleCrypto.decrypt(info, ait1.getAgente()),
                                            context);
                                    usuarioWebTrans = retorno[0];
                                    senhaWebTrans = retorno[1];

                                } catch (Exception ex) {

                                }

                                String r = "ini";
                                // if
                                // (!ait1.getImpresso().equals(SimpleCrypto.encrypt(info,
                                // "NAO")))
                                String impressao = ait1.getImpressao();
                                if (impressao != null && !impressao.equals(""))
                                    impressao = SimpleCrypto.decrypt(info, ait1.getImpressao());
                                r = txAitPdf(
                                        Utilitarios.getDataHora(4),
                                        String.valueOf(ait1.getIdWebTrans()),
                                        impressao,
                                        SimpleCrypto.decrypt(info,
                                                ait1.getAit()), Pda,
                                        codMunicipio, ait1.getId());
                                if (r.equals("ok")) {
                                    informUsr("Ok");
                                    ait1.setSendPdf("TRUE");
                                    a.gravaSendPdf(ait1);
                                } else if (!r.equals("ini")) {
                                    informUsr("Falha de conexão: " + r);
                                }

                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            informUsr("Falha ao transmitir AIT! Erro:" + e.getMessage());
                        }
                    }

                }

                informUsr("Limpando Aits já transmitidos...");
                limpaAitsTx();
                LogDAO l = new LogDAO(context);
                List<Logs> lg = l.getLogsImpressao();
                int i = 1;
                for (Logs b : lg) {
                    try {
                        informUsr("Transmitindo Logs: " + String.valueOf(i)
                                + " de " + String.valueOf(lg.size()));
                        txLogPost(String.valueOf(b.getId()), b.getPda(),
                                b.getDataHora(), b.getOrgao(), b.getPda(),
                                b.getOperacao(), b.getStatus());
                    } catch (Exception e) {
                        // Log.d("Erro - Transferencia de LOG-IMPRESSAO",
                        // e.getMessage());
                        // l.DeleteLog(b.getId());
                    }
                    i++;
                }
                // informUsr("Sincronizando com o WebTrans...");
                // carregaTabelas();
                txLoginLogoff();

                // txLogftp();
                // txLog();
                // LogDAO l=new LogDAO(context);
                // List<Logs> lg= l.getLogs();
                // int i=1;
                // for (Logs b:lg)
                // {
                // try{
                // informUsr("Transmitindo Logs: "+String.valueOf(i)
                // +" de "+String.valueOf(lg.size()));
                // txLogPost(String.valueOf(b.getId()),b.getPda(),b.getDataHora(),b.getOrgao(),b.getPda(),
                // b.getOperacao(),b.getStatus());
                // }
                // catch(Exception e)
                // {
                // l.DeleteLog(b.getId());
                // }
                // i++;
                // }

                //progress.dismiss();

                if (errotx) {
                    mostraMensagem("Não consegui transmitir todos os AIT's");
                } else {
                    mostraMensagem("Todos aits foram enviados com sucesso!");
                }


            }

            //).start();
            return resp;
        }


        // ******************************************************************
        // verifica se foi criado arquivo de saida da thread do webtrans
        // ******************************************************************


        public String txAitPdf(String dataSolicitacao, String idMulta,
                               String pdf, String ait, String pda, String clienteId, long IdAit) {
            String ret = "erro";
            LogDAO l = new LogDAO(context);


            String arquivofoto = null;
            String arquivoInfrator = null;
            boolean conseguiu = false;
            SQLiteDatabase Base = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);
            byte[] bitmapdata = null;
            try {

                Cursor cursor = Base.rawQuery("Select arquivoAssinatura,arquivoAssInfrator from aitAssinatura where idAit= " + ait1.getId(), null);
                try {

                    cursor.moveToFirst();
                    arquivofoto = cursor.getString(cursor.getColumnIndex("arquivoAssinatura"));
                    if (arquivofoto != null) {
                        Bitmap ourbitmap = BitmapFactory.decodeFile(arquivofoto);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ourbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        bitmapdata = baos.toByteArray();
                        Foto_base64 = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                    }
                    arquivoInfrator = cursor.getString(cursor.getColumnIndex("arquivoAssInfrator"));
                    if (arquivoInfrator != null) {

                        Bitmap ourbitmap = BitmapFactory.decodeFile(arquivoInfrator);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ourbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        //assinaturaInfrator = baos.toByteArray();
                        //Foto_base64Infrator = Base64.encodeToString(b, Base64.DEFAULT);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                FTPClient ftp = new FTPClient();


                if (arquivofoto != null || arquivoInfrator != null) {
                    //Faz a conexão com o servidor ftp
                    try {

                        //ftp.connect("170.81.42.248");
                        //ftp.login("admin", "9874123");
                        ftp.connect("189.57.47.194", 23450);
                        ftp.login("cobrapalm", "@3030");

                        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                        //http://commons.apache.org/net/apidocs/org/apache/commons/net/ftp/FTPClient.html#enterLocalActiveMode()
                        ftp.enterLocalPassiveMode();

                        if (arquivofoto != null) {
                            File fil = new File(arquivofoto);
                            FileInputStream fis = new FileInputStream(arquivofoto);

                            if (ftp.storeFile("AIT Assinaturas/" + idMulta + ".jpg", fis)) {

                                // se conseguiu transmitir limpa log
                                conseguiu = true;
                                fis.close();
                                //fil.delete();
                            } else {
                                fis.close();
                            }
                        }
                        if (arquivoInfrator != null) {
                            // tenta criar a pasta fotos
                            try {
                                ftp.makeDirectory("AIT Assinaturas/Infrator");
                            } catch (Exception e) {

                            }
                            File fil = new File(arquivoInfrator);
                            FileInputStream fis = new FileInputStream(arquivoInfrator);

                            if (ftp.storeFile("AIT Assinaturas/Infrator/" + idMulta + ".jpg", fis)) {

                                // se conseguiu transmitir limpa log
                                conseguiu = true;
                                fis.close();
                                //fil.delete();
                            } else {
                                fis.close();
                            }
                        }

                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {
                        ftp.disconnect();
                        ftp.logout();
                        ftp.disconnect();

                    } catch (Exception e) {

                    }
                    if (conseguiu == false) {

                        return " erro ao enviar assinaturas";
                    }
                }
            } catch (SQLiteException sql) {

            }

            url = "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/MakePdf";// urlswebtrans.geturl("pdf");//.replace("6742",
            // "9090");

            // urlswebtrans.close();
            Cryptor cr = new Cryptor();

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                if (pdf == null)
                    pdf = "";
                JSONStringer json = new JSONStringer();
                json.object();
                json.key("p");
                json.object();
                json.key("UserWebTrans").value(cr.encrypt(usuarioWebTrans));
                json.key("Password").value(senhaWebTrans);
                json.key("DataSolicitacao").value(
                        cr.encrypt(dataSolicitacao));
                json.key("Equipamento").value(cr.encrypt(cequip));
                json.key("IdMulta").value(cr.encrypt(idMulta));
                json.key("Pdf").value(cr.encrypt(pdf.replace("%", "porcento")));
                json.key("Patrimonio").value(cr.encrypt(pda));
                json.key("Cliente").value(cr.encrypt(clienteId));
                //json.key("Assinatura").value(Foto_base64);
                // json.key("AssinaturaInfrator").value(new ByteArrayEntity(arqAssInfrator));
                // /json.endObject();
                json.endObject();

                // post.setEntity(new StringEntity(json.toString(),
                // "UTF-8"));
                StringEntity entityy = new StringEntity(json.toString(),
                        "UTF-8");
                entityy.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8
                entityy.setContentEncoding(new BasicHeader(
                        HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                post.setEntity(entityy);
                // post.setEntity(new StringEntity(pdf,"UTF-8"));
                HttpResponse response = client.execute(post);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                retornoweb = EntityUtils.toString(response.getEntity());

                String retz = retornoweb;
                // mostraMensagem(String.valueOf(statusCode) );
                // mostraMensagem(retz);

                if (statusCode == 200) {
                    try {
                        ret = "ok";
                        l.gravalog("AIT impresso- " + ait, "Upload", OrgA,
                                Pda, agente, context);
                        // AitDAO aitdao = new AitDAO(context);
                        // Ait ait2 = new Ait();

                        // ait2.setId(Long.parseLong(idMulta));
                        // ait2.setSendPdf("T");
                        // aitdao.gravaSendPdf(ait2);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    ret = "Transmissao AIT impresso erro - " + statusCode;
                    try {
                        l.gravalog("Transmissao AIT impresso erro - "
                                        + statusCode, "Erro", clienteId, pda,
                                agente, context);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvalidKeyException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvalidAlgorithmParameterException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalBlockSizeException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (BadPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            return ret;
        }

        public String txLogPost(String id, String pda, String datahora,
                                String oatd, String agente, String oper, String status) {

            String ret = "ok";
            LogDAO l = new LogDAO(context);
            // String url =
            // "http://cobrasin.no-ip.biz:9090/JsonWcf/JsonWcfService.svc/MakePdf";
            // "http://192.168.1.108/JsonWcf/JsonWcfService.svc/MakePdf";//
            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

            // String url = urlswebtrans.geturl("urlcripto");
            url = "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/InsertLog";
            // url = urlswebtrans.geturl("log");//.replace("6742",
            // "9090");//"http://192.168.2.103/JsonWcf/JsonWcfService.svc/InsertLogC";
            // url=url.substring(0, url.length()-1);
            urlswebtrans.close();
            Cryptor cr = new Cryptor();

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                HttpParams httpParameters = new BasicHttpParams();

                // Set the timeout in milliseconds until a connection is
                // established.
                int timeoutConnection = 8000;
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for
                // data.
                int timeoutSocket = 8000;
                HttpConnectionParams.setSoTimeout(httpParameters,
                        timeoutSocket);

                JSONStringer json = new JSONStringer();
                json.object();
                json.key("p");
                json.object();
                json.key("Patrimonio").value(cr.encrypt(pda));
                json.key("Data").value(
                        cr.encrypt(datahora.substring(0, 10)));
                json.key("Hora").value(
                        cr.encrypt(datahora.substring(11, 19) + "-000"));
                json.key("Orgao").value(cr.encrypt(oatd));
                json.key("Agente").value(cr.encrypt(agente));
                json.key("Operacao").value(cr.encrypt(oper));
                json.key("Status").value(cr.encrypt(status));

                json.endObject();

                // post.setEntity(new StringEntity(json.toString(),
                // "UTF-8"));
                StringEntity entity = new StringEntity(json.toString(),
                        "UTF-8");
                entity.setContentType("application/json;charset=UTF-8");// text/plain;charset=UTF-8
                entity.setContentEncoding(new BasicHeader(
                        HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                post.setEntity(entity);
                // post.setEntity(new StringEntity(pdf,"UTF-8"));
                HttpResponse response = client.execute(post);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                retornoweb = EntityUtils.toString(response.getEntity());

                String retz = retornoweb;
                // mostraMensagem(String.valueOf(statusCode) );
                // mostraMensagem(retz);

                if (statusCode == 200) {
                    try {
                        l.DeleteLog(Long.parseLong(id));

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    try {
                        l.gravalog(
                                "Transmissao de log erro- " + statusCode,
                                "Erro", oatd, pda, agente, context);
                        // l.DeleteLog(Long.parseLong(id));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvalidKeyException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvalidAlgorithmParameterException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalBlockSizeException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (BadPaddingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            return ret;
        }

        private boolean terminouWebTrans() {

            // caminho onde estão os arquivos
            String root = Environment.getDataDirectory().getAbsolutePath()
                    + "/data/br.com.cobrasin/databases";

            File file = new File(root, "errowebtrans");
            if (file.exists()) {
                return true;
            }

            file = new File(root, "fimwebtrans");
            if (file.exists()) {
                return true;
            }
            return false;
        }

        // ************************************************************************************************
        // Lipa arquivos de erro
        // ************************************************************************************************
        private void limpaArqWebTrans() {
            // caminho onde estão os arquivos
            String root = Environment.getDataDirectory().getAbsolutePath()
                    + "/data/br.com.cobrasin/databases";

            try {
                File file = new File(root, "errowebtrans");
                file.delete();
            } catch (Exception e) {

            }

            // ----------------------------------------------------------------------------------
            try {
                File file = new File(root, "fimwebtrans");
                file.delete();
            } catch (Exception e) {

            }
        }

        // *********************************
        // seta parametros de autenticacao
        // *********************************
        private void setAutenticacao() {
            // Parametros de autenticacao
            nvps.add(new BasicNameValuePair("cliente", codMunicipio));
            nvps.add(new BasicNameValuePair("user", usuarioWebTrans));
            nvps.add(new BasicNameValuePair("password", senhaWebTrans)); // "E10ADC3949BA59ABBE56E057F20F883E"
            // nvps.add(new BasicNameValuePair("dataSolicitacao",
            // Utilitarios.getDataHora(4)));

        }

        private boolean carregaDados2(String tipotransacao) {
            boolean ret = true;

            // String url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaEspeciesVeiculo.action";

            // url =
            // "http://187.21.89.93:8080/multas-web/talonario/recuperaLogradouros.action";

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

            String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");

            String urlBase = urlswebtrans.geturl(tipotransacao);

            urlswebtrans.close();

            HttpClient httpclient = new DefaultHttpClient();
            urlBase += "?cliente=" + codMunicipio + "&user="
                    + usuarioWebTrans + "&password=" + senhaWebTrans
                    + "&dataSolicitacao=" + Utilitarios.getDataHora(4);
            HttpResponse response = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                try {
                    response = httpclient.execute(new HttpGet(urlBase));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                HttpPost post = new HttpPost(url);

                // buscar em parametros!!!!!
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                // nvps.add(new BasicNameValuePair("cliente", codMunicipio ));

                // nvps.add(new BasicNameValuePair("user", usuarioWebTrans));

                // nvps.add(new BasicNameValuePair("password", senhaWebTrans));
                // // "E10ADC3949BA59ABBE56E057F20F883E"

                // nvps.add(new BasicNameValuePair("dataSolicitacao",
                // Utilitarios.getDataHora(4)));

                // ***********************************************************************
                // TESTE DE CRIPTOGRAFIA - 10.04.2012
                // ***********************************************************************

                // for ( int nx = 0 ; nx < nvps.size() ; nx++ )
                // {
                // parBase += nvps.get(nx).toString() + "&";
                // }

                /*
                 * try { urlBase = URLEncoder.encode(urlBase, "ISO-8859-1"); }
                 * catch (UnsupportedEncodingException e3) { // TODO
                 * Auto-generated catch block e3.printStackTrace(); }
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
                    // in milliseconds which is the timeout for waiting for
                    // data.
                    int timeoutSocket = 20000;
                    HttpConnectionParams.setSoTimeout(httpParameters,
                            timeoutSocket);

                    // HttpResponse response = httpclient.execute(httpget);
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

                String retornoweb = EntityUtils.toString(response
                        .getEntity());

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
                            + String.format("%d", statusCode) + " - "
                            + retornoweb;

                    Utilitarios.gravaLog(mensz, context);

                    ret = false;
                }
            } catch (Exception e) {
            }
            return ret;
        }


        private URI sanitizeUrl(String sanitizeURL) throws ProtocolException {

            URI uri = null;

            try {
                URL url = new URL(URLDecoder.decode(sanitizeURL, HTTP.UTF_8));
                // https://stackoverflow.com/a/8962879/956415
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            } catch (URISyntaxException | MalformedURLException | UnsupportedEncodingException e) {
                throw new ProtocolException(e.getMessage(), e);
            }

            return uri;
        }

        // *************************************
        // Quando nao consegue ler devolve erro
        // *************************************
        private boolean carregaDados(String tipotransacao) {
            boolean ret = true;

            String urlBase;

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);
            urlBase = urlswebtrans.geturl(tipotransacao);

            //if ( tipotransacao.contains("cadait"))
            //{
            //  urlBase =                 "http://187.21.65.97:8080/multas-web/talonario/cadastrarMulta.action";
            //}

            url = urlswebtrans.geturl("urlcripto").replace(":8080", "");
            // url =                "http://187.21.65.97:8080/multas-web/talonario/encryptedAction.action";
            urlswebtrans.close();

            // debug
            List<NameValuePair> nvps1 = nvps;

            // ***********************************************************************
            // TESTE DE CRIPTOGRAFIA - 10.04.2012
            // ***********************************************************************
            urlBase = urlBase + "?";

            for (int nx = 0; nx < nvps1.size(); nx++) {
                urlBase += nvps1.get(nx).toString().replace("+", " ") + "&";
            }

            urlBase = urlBase.substring(0, urlBase.length() - 1);
            urlBase = urlBase.replace("+", " ").replaceAll("%", "").replaceAll("#","").replaceAll("@","")
                    .replaceAll("Á", "").replaceAll("Ã", "").replaceAll("Â", "")
                    .replaceAll("É", "").replaceAll("Ê", "").replaceAll("Í", "")
                    .replaceAll("Ó", "").replaceAll("Ô", "").replaceAll("Õ", "")
                    .replaceAll("Ú", "").replaceAll("Ç", "").replaceAll("@","");
            nvps.add(new BasicNameValuePair("checkSum", MD5Util
                    .criptografar(urlBase)));

            try {
                urlBase = SimpleCrypto.encrypt(info, urlBase);
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            nvps.add(new BasicNameValuePair("encryptedUrl", urlBase));
            // *****************************************************************************
            // .multas-web/talonario/encryptedAction.action?encryptedUrl=ASDFAFGDSDFSD951FDG
            // *****************************************************************************

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            // buscar em parametros!!!!!
            // List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            int timeoutConnection = 20000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 20000;
            HttpConnectionParams
                    .setSoTimeout(httpParameters, timeoutSocket);

            try {
                post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e1) {

                ret = false;
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {

                // HttpResponse response = httpclient.execute(httpget);
                httpclient.setRedirectHandler(new DefaultRedirectHandler() {
                    @Override
                    public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                        return super.isRedirectRequested(httpResponse, httpContext);
                    }

                    @Override
                    public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                        return sanitizeUrl(httpResponse.getFirstHeader("location").getValue());
                    }
                });

                HttpResponse response = httpclient.execute(post);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                retornoweb = EntityUtils.toString(response.getEntity());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (statusCode == 403 || statusCode == 302) {
                        url = response.getHeaders("Location")[0].getElements()[0].getName().replace("?cliente", "");
                        //url +="parametro="+ URLEncoder.encode(nvps, "UTF-8") +\"&xxxxxxxxxx";

                        post = new HttpPost(url);


                        httpParameters = new BasicHttpParams();
                        timeoutConnection = 20000;
                        HttpConnectionParams.setConnectionTimeout(httpParameters,
                                timeoutConnection);
                        timeoutSocket = 20000;
                        HttpConnectionParams
                                .setSoTimeout(httpParameters, timeoutSocket);

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
                // String retz = retornoweb;

                // ******************************
                // 18.05.2012
                // grava resposta do WEBTRANS
                // ******************************

                // Utilitarios.gravaLog(mensz, context);
                ParametroDAO pardao = new ParametroDAO(context);

                Cursor c = pardao.getParametros();

                c.moveToFirst();

                agente = salvaAgente;
                c.close();

                if (statusCode == 200) {

                    try {

                        retornoweb = "[" + retornoweb + "]";

                        jsonArray = new JSONArray(retornoweb);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        ret = false;
                        e.printStackTrace();
                    }

                } else {
                    if (retornoweb
                            .contains("{\"message\":\"Ocorreu um erro inesperado no sistem: AIT DUPLICADA\",\"success\":false}")) {
                        LogDAO l = new LogDAO(context);
                        try {
                            l.gravalog(
                                    "Erro Ait "
                                            + SimpleCrypto.decrypt(info,
                                            ait1.getAit())
                                            + " duplicada", "UPLOAD", OrgA,
                                    Pda, agente, context);
                            mostraMensagem("Ait:"
                                    + SimpleCrypto.decrypt(info,
                                    ait1.getAit()) + " duplicada");
                            AitDAO aitdao = new AitDAO(context);
                            aitdao.atualizaTx(ait1.getId(), true,
                                    ait1.getIdWebTrans());

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        mostraMensagem(retornoweb);
                    }
                    ret = false;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                e.getMessage();
                ret = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ret = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // limpa
            nvps.clear();

            return ret;
        }

        public boolean GetServiceClientes() {
            boolean ret = true;

            UrlsWebTransDAO urlswebtrans = new UrlsWebTransDAO(context);

            String url = urlswebtrans.geturl("urlcripto").replace(":8080", "");

            String urlBase = urlswebtrans.geturl("clientes");

            urlswebtrans.close();

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            // buscar em parametros!!!!!
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            senhaWebTrans = MD5Util.criptografar("2015RES");

            /*
             * String xcod=""; try { xcod =
             * SimpleCrypto.decrypt(Utilitarios.getInfo(),
             * c.getString(c.getColumnIndex("orgaoautuador"))); } catch
             * (Exception e3) { // TODO Auto-generated catch block
             * e3.printStackTrace(); }
             */
            // codMunicipio = xcod;//.subSequence(1, 5).toString(); //265810
            usuarioWebTrans = "LOGWEBTRANS" + codMunicipio;

            // ***********************************************************************
            // TESTE DE CRIPTOGRAFIA - 10.04.2012
            // ***********************************************************************
            urlBase += "?cliente=" + codMunicipio + "&user="
                    + usuarioWebTrans + "&password=" + senhaWebTrans
                    + "&dataSolicitacao=" + Utilitarios.getDataHora(4);

            nvps.add(new BasicNameValuePair("checkSum", MD5Util
                    .criptografar(urlBase)));

            try {
                urlBase = SimpleCrypto.encrypt(info, urlBase);
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

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
                // in milliseconds which is the timeout for waiting for
                // data.
                int timeoutSocket = 20000;
                HttpConnectionParams.setSoTimeout(httpParameters,
                        timeoutSocket);

                httpclient.setRedirectHandler(new DefaultRedirectHandler() {
                    @Override
                    public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
                        return super.isRedirectRequested(httpResponse, httpContext);
                    }

                    @Override
                    public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                        return sanitizeUrl(httpResponse.getFirstHeader("location").getValue());
                    }
                });

                // HttpResponse response = httpclient.execute(httpget);
                HttpResponse response = httpclient.execute(post);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                String retornoweb = EntityUtils.toString(response
                        .getEntity());

                if (statusCode == 200) {
                    try {
                        try {
                            retornoweb = SimpleCrypto.decrypt(
                                    Utilitarios.getInfo(), retornoweb);
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
                    informUsr("Serviço WebTrans não responde!");
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

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context,
                    "Aguarde!",
                    "Enviando AITs...");
        }


        @Override
        protected void onProgressUpdate(String... text) {

            progressDialog = ProgressDialog.show(context,
                    "Aguarde!",
                    "Enviando AITs..");
        }
    }
}
