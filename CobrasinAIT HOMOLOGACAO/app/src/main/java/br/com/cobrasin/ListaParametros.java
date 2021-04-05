package br.com.cobrasin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio;
import android.renderscript.Type;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.CaracterizacaoDAO;
import br.com.cobrasin.dao.EixoDAO;
import br.com.cobrasin.dao.FabricanteDAO;
import br.com.cobrasin.dao.ModeloDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Caracterizacao;
import br.com.cobrasin.tabela.Eixo;
import br.com.cobrasin.tabela.Fabricante;
import br.com.cobrasin.tabela.Modelo;
import br.com.cobrasin.tabela.Parametro;

public class ListaParametros extends Activity {

    private boolean BaixouFabricante = true;
    private boolean BaixouModelo = true;
    private boolean BaixouCaracterizacao = true;
    private boolean BaixouEixo = true;

    private EditText edImpressora;
    private EditText edSenhaReset;
    private EditText edPatImp;
    private RadioButton rdbSkyPix;
    private RadioButton rdbBlueBamboo;
    private String seriepda, senhaReset, tipoLeituraTAG;
    private boolean ConctadoInternet;
    private String ModWeb;
    private String ModGps;
    private String ModOCR;
    private String ModPDF;
    private String info = Utilitarios.getInfo();
    private Intent i = null;
    ProgressDialog dialog;
    private Handler handler = new Handler();
    private static final int NOTIFY_ME_ID = 1337;
    private boolean boolPDF;

    ProgressDialog barProgressDialog;
    Handler updateBarHandler;

    private ProgressDialog progress;

    // 00:08:1B:95:6B:AF
    // grava a selecao dos RadioButton
    private void trataRadio(String radiosel) {
        // TODO Auto-generated method stub

        Parametro param = new Parametro();
        param.setSeriepda(seriepda);
        param.setImpressoraMAC(edImpressora.getText().toString());
        param.setImpressoraPatrimonio(edPatImp.getText().toString());
        param.setImprimeobs(radiosel);

        ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
        pardao.gravaImpressora(param);
        pardao.gravaImprimeObs(param);
        pardao.close();

    }

    protected void criarNotificacao(String titulo, String subtitulo,
                                    String descricao) {

        final NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent i = PendingIntent.getActivity(getBaseContext(), 0,
                new Intent(getBaseContext(), ListaParametros.class), 0);

        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.icon).setTicker(titulo).setWhen(System.currentTimeMillis())
                .setContentTitle(subtitulo).setContentText(descricao).setContentIntent(i);

        Notification note = builder.getNotification();
        note.flags |= Notification.FLAG_INSISTENT;
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        mgr.notify(NOTIFY_ME_ID, note);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.parametro);
        updateBarHandler = new Handler();
        barProgressDialog = new ProgressDialog(ListaParametros.this);
        rdbSkyPix = (RadioButton) findViewById(R.id.rdbSkyPix);
        rdbBlueBamboo = (RadioButton) findViewById(R.id.rdbBlueBamboo);

        final Utilitarios u = new Utilitarios();
        final ToggleButton tbtnConexWeb = (ToggleButton) findViewById(R.id.tbtnConexWeb);
        // final ToggleButton tbtnModGps = (ToggleButton)
        // findViewById(R.id.tbtnModoGps);
        final ToggleButton tbtnModOCR = (ToggleButton) findViewById(R.id.tbtnModoOCR);
        final ToggleButton tbtnGeraPdf = (ToggleButton) findViewById(R.id.tbtnGeraPdf);
        edImpressora = (EditText) findViewById(R.id.edEnderecoImp);
        tbtnConexWeb.requestFocus();

        tbtnModOCR.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                if (tbtnModOCR.isChecked()) {
                    param.setmodocr("TRUE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoOCR(param);

                    tbtnModOCR.setChecked(true);
                } else {
                    param.setmodocr("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoOCR(param);
                }
            }
        });

        /*
         * tbtnModGps.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View arg0) { // TODO Auto-generated
         * method stub Parametro param = new Parametro(); ParametroDAO pardao =
         * new ParametroDAO(ListaParametros.this); if (tbtnModGps.isChecked()) {
         * param.setmodgps("TRUE"); param.setSeriepda(seriepda);
         * pardao.gravaModoGps(param);
         *
         * tbtnConexWeb.setChecked(true); } else { param.setmodgps("FALSE");
         * param.setSeriepda(seriepda); pardao.gravaModoGps(param); } } });
         */

        // Conexão Web
        tbtnConexWeb.setOnClickListener(new OnClickListener() {
            // Parametro param = new Parametro();
            ParametroDAO pardao = new ParametroDAO(ListaParametros.this);

            @Override
            public void onClick(View arg0) {

                Parametro param = new Parametro();
                if (tbtnConexWeb.isChecked()) {
                    // Button is ON
                    // Do Something
                    ConctadoInternet = u.conectado(ListaParametros.this);
                    if (ConctadoInternet == true) {
                        param.setmodweb("TRUE");
                        param.setSeriepda(seriepda);
                        pardao.gravaModoWeb(param);

                        tbtnConexWeb.setChecked(true);

                        // tbtnModGps.setEnabled(true);

                        criarNotificacao("TEC", "TEC",
                                "Você está em modo online!");
                    }
                    if (ConctadoInternet == false) {
                        param.setmodweb("FALSE");
                        param.setSeriepda(seriepda);
                        pardao.gravaModoWeb(param);

                        tbtnConexWeb.setChecked(false);

                        param.setmodgps("FALSE");
                        param.setSeriepda(seriepda);
                        pardao.gravaModoGps(param);
                        // tbtnModGps.setEnabled(false);
                        // tbtnModGps.setChecked(false);

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ListaParametros.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao conectar na internet!\nVocê está em modo offline!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();

                        criarNotificacao("TEC", "TEC",
                                "Falha ao conectar na internet!");
                    }

                } else {
                    param.setmodweb("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoWeb(param);

                    param.setmodgps("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoGps(param);
                    // tbtnModGps.setEnabled(false);
                    // tbtnModGps.setChecked(false);

                    criarNotificacao("TEC", "TEC", "Você está em modo offline!");
                }
                pardao.close();
            }
        });

        Button btGrava = (Button) findViewById(R.id.btGravaPar);

        btGrava.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // Grava o endereco de impressora e a seleção

                Parametro param = new Parametro();
                param.setImpressoraMAC(edImpressora.getText().toString());
                param.setImpressoraPatrimonio(edPatImp.getText().toString());
                param.setSeriepda(seriepda);

                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                pardao.gravaImpressora(param);
                pardao.close();

                finish();
            }
        });

        Button btRetornaPar = (Button) findViewById(R.id.btRetornaPar);
        btRetornaPar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                finish();
            }
        });

        RadioButton radio0 = (RadioButton) findViewById(R.id.radio00);

        radio0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                trataRadio("1");
            }

        });

        RadioButton radio1 = (RadioButton) findViewById(R.id.radio11);

        radio1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                trataRadio("2");
            }
        });

        RadioButton rdbGEN2 = (RadioButton) findViewById(R.id.rdbGEN2);

        rdbGEN2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                param.setSeriepda(seriepda);
                param.setTipoLeituraTAG("GEN2");

                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                pardao.gravaTipoLeituraTAG(param);
                pardao.close();
            }

        });

        RadioButton rdbG0 = (RadioButton) findViewById(R.id.rdbG0);

        rdbG0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                param.setSeriepda(seriepda);
                param.setTipoLeituraTAG("G0");

                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                pardao.gravaTipoLeituraTAG(param);
                pardao.close();
            }
        });

        // recupera os parametros
        Parametro param = new Parametro();
        ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        String modeloImpressora = cpar.getString(cpar.getColumnIndex("modeloImpressora"));
        try {
            if (!TextUtils.isEmpty(cpar.getString(cpar.getColumnIndex("modeloImpressora")))) {
                modeloImpressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("modeloImpressora")));
            }

            if (modeloImpressora.equals("SkyPix")) {
                rdbSkyPix.setChecked(true);
                rdbBlueBamboo.setChecked(false);
            } else {
                rdbBlueBamboo.setChecked(true);
                rdbSkyPix.setChecked(false);
            }
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        seriepda = cpar.getString(cpar.getColumnIndex("seriepda"));
        try {
            tipoLeituraTAG = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("tipoLeituraTAG")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if (tipoLeituraTAG == null) {
            rdbG0.setChecked(false);
            rdbGEN2.setChecked(false);
        } else {
            if (tipoLeituraTAG.contains("")) {
                rdbG0.setChecked(false);
                rdbGEN2.setChecked(false);
            }
            if (tipoLeituraTAG.contains("GEN2")) {
                rdbGEN2.setChecked(true);
                rdbG0.setChecked(false);
            }
            if (tipoLeituraTAG.contains("G0")) {
                rdbGEN2.setChecked(false);
                rdbG0.setChecked(true);
            }
        }
        // ------------------------
        // ModPDF
        // ------------------------
        try {
            ModPDF = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modpdf")));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (ModPDF == null) {
            param.setmodpdf("FALSE");
            param.setSeriepda(seriepda);
            pardao.gravaModoPdf(param);
        }

        cpar = pardao.getParametros();
        try {
            ModPDF = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modpdf")));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (ModPDF.contains("TRUE")) {
            tbtnGeraPdf.setChecked(true);
        }

        if (ModPDF.contains("FALSE")) {
            tbtnGeraPdf.setChecked(false);
        }
        boolPDF = tbtnGeraPdf.isChecked();
        tbtnGeraPdf.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                tbtnGeraPdf.setChecked(boolPDF);
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        ListaParametros.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Configuração - TEC");
                aviso.setMessage("Digite a senha administrador para poder alterar!");
                final EditText txtSenha = new EditText(ListaParametros.this);
                txtSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                txtSenha.setTransformationMethod(PasswordTransformationMethod
                        .getInstance());
                aviso.setView(txtSenha);
                aviso.setPositiveButton("Alterar",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                String value = txtSenha.getText().toString();
                                if (value.contains("c0br@s!n")) {
                                    Parametro param = new Parametro();
                                    ParametroDAO pardao = new ParametroDAO(
                                            ListaParametros.this);
                                    if (!boolPDF) {
                                        param.setmodpdf("TRUE");
                                        param.setSeriepda(seriepda);
                                        pardao.gravaModoPdf(param);
                                        boolPDF = true;
                                        tbtnGeraPdf.setChecked(boolPDF);
                                    } else {
                                        param.setmodpdf("FALSE");
                                        param.setSeriepda(seriepda);
                                        pardao.gravaModoPdf(param);
                                        boolPDF = false;
                                        tbtnGeraPdf.setChecked(boolPDF);
                                    }
                                } else {
                                    tbtnGeraPdf.setChecked(boolPDF);
                                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                                            ListaParametros.this);
                                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                                    aviso.setTitle("Configuração - TEC");
                                    aviso.setMessage("Senha incorreta!");
                                    aviso.setPositiveButton("OK", null);
                                    aviso.show();
                                }
                            }
                        });
                aviso.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                tbtnGeraPdf.setChecked(boolPDF);
                            }
                        });

                aviso.show();
            }
        });
        // ------------------------
        // ModOCR
        // ------------------------
        try {
            ModOCR = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modocr")));

            if (ModOCR == null) {
                param.setmodocr("FALSE");
                param.setSeriepda(seriepda);
                pardao.gravaModoOCR(param);
            }

            cpar = pardao.getParametros();
            ModOCR = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modocr")));

            if (ModOCR.contains("TRUE")) {
                tbtnModOCR.setChecked(true);
            }

            if (ModOCR.contains("FALSE")) {
                tbtnModOCR.setChecked(false);
            }

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // ------------------------
        // ModGPS
        // ------------------------
        try {
            ModGps = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modgps")));

            if (ModGps == null) {
                param.setmodgps("TRUE");
                param.setSeriepda(seriepda);
                pardao.gravaModoGps(param);
            }

            cpar = pardao.getParametros();
            ModGps = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modgps")));

            if (ModGps.contains("TRUE")) {
                // tbtnModGps.setChecked(true);
            }

            if (ModGps.contains("FALSE")) {
                // tbtnModGps.setChecked(false);
            }

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // ------------------------
        // ModWeb
        // ------------------------
        try {
            ModWeb = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modweb")));

            if (ModWeb == null) {
                param.setmodweb("TRUE");
                param.setSeriepda(seriepda);
                pardao.gravaModoWeb(param);
            }

            cpar = pardao.getParametros();
            ModWeb = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("modweb")));

            if (ModWeb.contains("TRUE")) {
                ConctadoInternet = u.conectado(ListaParametros.this);
                if (ConctadoInternet == true) {
                    param.setmodweb("TRUE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoWeb(param);

                    // tbtnModGps.setEnabled(true);
                    tbtnConexWeb.setChecked(true);

                    // criarNotificacao("TEC","TEC","Você esta em modo online!");
                }
                if (ConctadoInternet == false) {
                    param.setmodweb("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoWeb(param);

                    tbtnConexWeb.setChecked(false);

                    param.setmodgps("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoGps(param);
                    // tbtnModGps.setEnabled(false);
                    // tbtnModGps.setChecked(false);

                    // criarNotificacao("Cobra-Talonário","Cobra-Talonário",
                    // "Falha ao conectar na internet! Você está em modo offline!");
                }
            }
            if (ModWeb.contains("FALSE")) {
                param.setmodweb("FALSE");
                param.setSeriepda(seriepda);
                pardao.gravaModoWeb(param);

                tbtnConexWeb.setChecked(false);

                param.setmodgps("FALSE");
                param.setSeriepda(seriepda);
                pardao.gravaModoGps(param);
                // tbtnModGps.setEnabled(false);
                // tbtnModGps.setChecked(false);

                // criarNotificacao("Cobra-Talonario","Cobra-Talonario","Voca esta em modo offline!");
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            this.edImpressora.setText(SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("impressoraMAC"))));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // ******************************************
        // recupera o patrimonio da impressora
        // 24.05.2012
        // ******************************************
        edPatImp = (EditText) findViewById(R.id.edPatImp);

        this.edPatImp.setText("");

        try {
            if (SimpleCrypto
                    .decrypt(info, cpar.getString(cpar
                            .getColumnIndex("impressoraPatrimonio"))) != null) {
                this.edPatImp
                        .setText(SimpleCrypto.decrypt(info, cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio"))));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        radio0.setChecked(false);
        radio1.setChecked(false);

        int selecao = 1;
        try {
            selecao = Integer.parseInt(SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("imprimeobs"))));
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        switch (selecao) {
            case 1:
                radio0.setChecked(true);
                break;
            case 2:
                radio1.setChecked(true);
                break;
        }

        // *************************
        // 29.06.2012
        try {
            senhaReset = SimpleCrypto.decrypt(Utilitarios.getInfo(),
                    cpar.getString(cpar.getColumnIndex("senhaftp")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        cpar.close();

        Button btReset = (Button) findViewById(R.id.btResetPDA);
        btReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (trataReset())
                    finish();
            }
        });

        edSenhaReset = (EditText) findViewById(R.id.edSenhaResetPDA);

        i = new Intent(this, DownloadTabelasWebtrans.class);

        Button btRecWebTrans = (Button) findViewById(R.id.btRecTabelasWebTrans);
        btRecWebTrans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(i);

            }
        });

        Button btBkpDb = (Button) findViewById(R.id.btBkpDb);
        btBkpDb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                ConctadoInternet = u.conectado(ListaParametros.this);
                if (ConctadoInternet == true) {
                    param.setmodweb("TRUE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoWeb(param);

                    tbtnConexWeb.setChecked(true);

                    // tbtnModGps.setEnabled(true);

                    criarNotificacao("TEC", "TEC", "Você está em modo online!");

                    // ______________________________________________________________________

                    dialog = ProgressDialog.show(ListaParametros.this,
                            "Gerando BKP...", "Aguarde...");
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Utilitarios u = new Utilitarios();
                            u.copiaBase(0, ListaParametros.this);
                            u.EnviaBkp(ListaParametros.this);

                            dialog.dismiss();
                            mostraMensagem("BKP gerado com Sucesso!");
                        }
                    }).start();
                    // _______________________________________________________________________

                }
                if (ConctadoInternet == false) {
                    param.setmodweb("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoWeb(param);

                    tbtnConexWeb.setChecked(false);

                    param.setmodgps("FALSE");
                    param.setSeriepda(seriepda);
                    pardao.gravaModoGps(param);
                    // tbtnModGps.setEnabled(false);
                    // tbtnModGps.setChecked(false);

                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ListaParametros.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao conectar na internet!\nVocê está em modo offline!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();

                    criarNotificacao("TEC", "TEC",
                            "Falha ao conectar na internet!");
                }
            }
        });

        Button btnReceberTabelaQFV = (Button) findViewById(R.id.btnReceberTabelaQFV);
        btnReceberTabelaQFV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        ListaParametros.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Download QFV - TEC");
                aviso.setMessage("Deseja baixar os dados do QFV?");
                aviso.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                DownloadQFV();
                            }
                        });
                aviso.setNegativeButton("Não", null);

                aviso.show();

            }
        });

        rdbSkyPix.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                param.setSeriepda(seriepda);
                param.setModeloImpressora("SkyPix");

                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                pardao.gravaModeloPrint(param);
                pardao.close();
            }
        });

        rdbBlueBamboo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Parametro param = new Parametro();
                param.setSeriepda(seriepda);
                param.setModeloImpressora("BlueBamboo");

                ParametroDAO pardao = new ParametroDAO(ListaParametros.this);
                pardao.gravaModeloPrint(param);
                pardao.close();
            }
        });
    }


    public boolean trataReset() {
        boolean retorno = false;
        final File filereset;

        String senhadigitada = edSenhaReset.getText().toString();

        // senha foi digitada !
        if (senhadigitada.length() > 0) {
            String root = Environment.getDataDirectory().getAbsolutePath()
                    + "/data/br.com.cobrasin/databases";

            filereset = new File(root, "definepda");

            if (!filereset.exists()) {
                Toast.makeText(getBaseContext(), "RESET já foi executado!",
                        Toast.LENGTH_LONG).show();
            } else {
                if (senhaReset.equals(senhadigitada)) {
                    // existem aits para serem transmitidos ?
                    AitDAO aitx = new AitDAO(ListaParametros.this);

                    List<Ait> aits = aitx.getListaCompleta();

                    aitx.close();

                    // pode executar
                    if (aits.size() == 0) {
                        // pede confirmação !

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ListaParametros.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("RESET PDA");
                        aviso.setMessage("Confirma ?");
                        aviso.setNeutralButton("Não", null);
                        aviso.setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                        // exclui arquivo definepda
                                        filereset.delete();
                                        edSenhaReset.setText("");
                                    }
                                });

                        aviso.show();

                    } else {
                        Toast.makeText(getBaseContext(),
                                "Existem AIT's para serem transmitidos...!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Senha inválida!!",
                            Toast.LENGTH_SHORT).show();
                    this.edSenhaReset.setText("");
                }
            }

        } else {
            Toast.makeText(getBaseContext(), "Senha deve ser digitada!",
                    Toast.LENGTH_SHORT).show();
        }
        return retorno;
    }

    private void mostraMensagem(final String mensagem) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder aviso1 = new AlertDialog.Builder(
                        ListaParametros.this);
                aviso1.setIcon(android.R.drawable.ic_dialog_alert);
                aviso1.setTitle("TEC");
                aviso1.setMessage(mensagem);
                aviso1.setPositiveButton("OK", null);
                aviso1.show();

            }
        });
    }

    private void DownloadQFV() {

        final WebService web = new WebService();

        barProgressDialog.setCancelable(false);
        barProgressDialog.setTitle("Baixando QFV...");
        barProgressDialog.setProgress(0);
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog
                .setMessage("O Download pode demorar alguns minutos...\r\nVerificando os Fabricantes...");
        barProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                BaixouFabricante = true;
                BaixouModelo = true;
                BaixouCaracterizacao = true;
                BaixouEixo = true;

                final FabricanteDAO FbDAO = new FabricanteDAO(
                        ListaParametros.this);
                final ModeloDAO MoDAO = new ModeloDAO(ListaParametros.this);
                final CaracterizacaoDAO CaDAO = new CaracterizacaoDAO(ListaParametros.this);
                final EixoDAO EiDAO = new EixoDAO(ListaParametros.this);

                MoDAO.ApagaTudo();
                FbDAO.ApagaTudo();
                CaDAO.ApagaTudo();
                EiDAO.ApagaTudo();

                final JSONArray dt = web
                        .ExecuteReaderQuery("Select * from Fabricante_DNIT");
                if (dt != null && dt.length() > 0) {

                    barProgressDialog.setMax(dt.length());
                    updateBarHandler.post(new Runnable() {

                        public void run() {
                            barProgressDialog.setProgress(0);
                            barProgressDialog
                                    .setMessage("O Download pode demorar alguns minutos...\r\nBaixando os Fabricantes...");
                        }
                    });

                    try {
                        int iFabricante = 0;
                        // Here you should write your time consuming task...
                        while (iFabricante <= dt.length()) {
                            Thread.sleep(300);
                            iFabricante++;
                            updateBarHandler.post(new Runnable() {

                                public void run() {

                                    JSONObject dr;
                                    try {
                                        dr = dt.getJSONObject(barProgressDialog
                                                .getProgress());
                                        Fabricante Fbr = new Fabricante();
                                        Fbr.setId(dr
                                                .getString("Id"));
                                        Fbr.setFabricante(dr
                                                .getString("Fabricante"));
                                        FbDAO.InsereFabricante(Fbr);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    barProgressDialog.incrementProgressBy(1);
                                }


                            });


                        }
                    } catch (Exception e) {
                        String Erro = e.getMessage();

                        BaixouFabricante = false;
                    }
                }

                updateBarHandler.post(new Runnable() {

                    public void run() {
                        barProgressDialog
                                .setMessage("O Download pode demorar alguns minutos...\r\nVerificando as Caracterizacões...");
                    }
                });
                final JSONArray dt3 = web
                        .ExecuteReaderQuery("Select * from Caracterizacao_DNIT");

                if (dt3 != null && dt3.length() > 0) {

                    barProgressDialog.setMax(dt3.length());
                    updateBarHandler.post(new Runnable() {

                        public void run() {
                            barProgressDialog.setProgress(0);
                            barProgressDialog
                                    .setMessage("O Download pode demorar alguns minutos...\r\nBaixando as Caracterizacões...");
                        }
                    });

                    try {

                        int iCaract = 0;
                        // Here you should write your time consuming task...
                        while (iCaract < dt3.length()) {

                            final int posicao = iCaract;
                            Thread.sleep(300);
                            updateBarHandler.post(new Runnable() {

                                public void run() {

                                    JSONObject dr;
                                    try {
                                        dr = dt3.getJSONObject(posicao);
                                        Caracterizacao Ca = new Caracterizacao();
                                        Ca.setGrupo_N_Eixos(dr.getString("Grupo_N_Eixos"));
                                        Ca.setPBT_PBTC(dr.getString("PBT_PBTC"));
                                        Ca.setCaracterizacao_Titulo(dr.getString("Caracterizacao_Titulo"));
                                        Ca.setCaracterizacao_Desc(dr.getString("Caracterizacao_Desc"));
                                        Ca.setClasse(dr.getString("Classe"));
                                        Ca.setCodigo(dr.getString("Codigo"));

                                        try {
                                            URL url = new URL("http://sistemas.cobrasin.com.br/radar/QFV/Imagens_QFV/" + dr.getString("Silhueta_Foto"));
                                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                            connection.setDoInput(true);
                                            connection.connect();
                                            InputStream input = connection.getInputStream();
                                            Bitmap ImagemCaminhao = BitmapFactory.decodeStream(input);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            ImagemCaminhao.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            Ca.setSilhueta_Foto(byteArray);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        CaDAO.InsereCaracterizacao(Ca);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    barProgressDialog.incrementProgressBy(1);

                                }

                            });
                            iCaract++;


                        }
                    } catch (Exception e) {
                        String Erro = e.getMessage();
                        BaixouCaracterizacao = false;
                    }
                }

                updateBarHandler.post(new Runnable() {

                    public void run() {
                        barProgressDialog
                                .setMessage("O Download pode demorar alguns minutos...\r\nVerificando os dados dos Eixos...");
                    }
                });
                final JSONArray dt4 = web
                        .ExecuteReaderQuery("Select * from Eixo_DNIT");

                if (dt4 != null && dt4.length() > 0) {

                    barProgressDialog.setMax(dt4.length());
                    updateBarHandler.post(new Runnable() {

                        public void run() {
                            barProgressDialog.setProgress(0);
                            barProgressDialog
                                    .setMessage("O Download pode demorar alguns minutos...\r\nBaixando os dados dos Eixos...");
                        }
                    });

                    try {

                        int iEixo = 0;
                        // Here you should write your time consuming task...
                        while (iEixo <= dt4.length()) {
                            iEixo++;
                            Thread.sleep(300);
                            updateBarHandler.post(new Runnable() {

                                public void run() {

                                    JSONObject dr;
                                    try {
                                        dr = dt4.getJSONObject(barProgressDialog
                                                .getProgress());

                                        Eixo Ei = new Eixo();
                                        Ei.setEixo_Titulo(dr.getString("Eixo_Titulo"));
                                        Ei.setEixo_Desc(dr.getString("Eixo_Desc"));
                                        Ei.setEixo_Peso(dr.getString("Eixo_Peso"));

                                        try {
                                            URL url = new URL("http://sistemas.cobrasin.com.br/radar/QFV/Imagens_QFV/" + dr.getString("Foto"));
                                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                            connection.setDoInput(true);
                                            connection.connect();
                                            InputStream input = connection.getInputStream();
                                            Bitmap ImagemCaminhao = BitmapFactory.decodeStream(input);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            ImagemCaminhao.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            Ei.setFoto(byteArray);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        EiDAO.InsereEixo(Ei);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    barProgressDialog.incrementProgressBy(1);

                                }

                            });


                        }
                    } catch (Exception e) {
                        String Erro = e.getMessage();
                        BaixouEixo = false;
                    }
                }

                updateBarHandler.post(new Runnable() {

                    public void run() {
                        barProgressDialog
                                .setMessage("O Download pode demorar alguns minutos...\r\nVerificando os Modelos...");
                    }
                });

                final JSONArray dt2 = web
                        .ExecuteReaderQuery("Select * from Modelo_DNIT");
                if (dt2 != null && dt2.length() > 0) {

                    barProgressDialog.setMax(dt2.length());
                    updateBarHandler.post(new Runnable() {

                        public void run() {
                            barProgressDialog.setProgress(0);
                            barProgressDialog
                                    .setMessage("O Download pode demorar alguns minutos...\r\nBaixando os Modelos...");
                        }
                    });

                    try {

                        int iModelo = 0;
                        // Here you should write your time consuming task...
                        while (iModelo <= dt2.length()) {
                            iModelo++;
                            Thread.sleep(300);
                            updateBarHandler.post(new Runnable() {

                                public void run() {

                                    JSONObject dr;
                                    try {
                                        dr = dt2.getJSONObject(barProgressDialog
                                                .getProgress());
                                        Modelo Mo = new Modelo();
                                        Mo.setIdFabricante(dr
                                                .getInt("IdFabricante"));

                                        Mo.setModelo(dr
                                                .getString("Modelo"));

                                        Mo.setPBT_Modelo(dr
                                                .getString("PBT_Modelo"));

                                        Mo.setPBT_Valor(dr
                                                .getString("PBT_Valor"));

                                        Mo.setPBT_Valor(dr
                                                .getString("PBT_Valor"));

                                        Mo.setCMT(dr
                                                .getString("CMT"));

                                        Mo.setObservacoes(dr
                                                .getString("Observacoes"));
                                        MoDAO.InsereModelo(Mo);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    barProgressDialog.incrementProgressBy(1);

                                }

                            });


                        }
                    } catch (Exception e) {
                        String Erro = e.getMessage();
                        BaixouModelo = false;
                    }
                }


                if (barProgressDialog.getProgress() == barProgressDialog
                        .getMax()) {

                    barProgressDialog.dismiss();
                    List<Fabricante> Lista_Fabricante = FbDAO.GetTodosFabricantes("");
                    if (Lista_Fabricante.size() != dt.length()) {
                        BaixouFabricante = false;
                    }
                    FbDAO.close();

                    List<Modelo> Lista_Modelo = MoDAO.GetTodosModelosVerificacao();
                    if (Lista_Modelo.size() != dt2.length()) {
                        BaixouModelo = false;
                    }
                    MoDAO.close();

                    List<Caracterizacao> Lista_Caracterizacao = CaDAO.GetTodasCaracterizacao("");
                    if (Lista_Caracterizacao.size() != dt3.length()) {
                        BaixouCaracterizacao = false;
                    }
                    CaDAO.close();

                    List<Eixo> Lista_Eixo = EiDAO.GetTodosEixos("");
                    if (Lista_Eixo.size() != dt4.length()) {
                        BaixouEixo = false;
                    }
                    EiDAO.close();

                    if (BaixouFabricante == false || BaixouModelo == false || BaixouCaracterizacao == false || BaixouEixo == false) {
                        mostraMensagem("Falha ao baixar os dados do QFV!");

                    } else {
                        mostraMensagem("Dados do QFV baixado com sucesso!");
                    }

                }
            }
        }).start();
    }
}
