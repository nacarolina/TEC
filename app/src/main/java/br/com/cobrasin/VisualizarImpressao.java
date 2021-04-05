package br.com.cobrasin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wpx.IBluetoothPrint;
import com.wpx.WPXMain;
import com.wpx.util.GeneralAttributes;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Municipio;

import static br.com.cobrasin.Utilitarios.ByteArrayCodePrintImage;
import static br.com.cobrasin.Utilitarios.bitmapToByteArray;

public class VisualizarImpressao extends Activity {

    private long idAit;
    TextView lblImpressaoInicial, lblImpressaoMeio, lblImpressaoFinal;
    private String[] impressao;
    private String orgaoAutuador;
    private String info = Utilitarios.getInfo();
    ImageView imgLogoPrefeitura, imgAssInfrator, imgAssAgente;

    Bitmap bmp_Assinatura = null;
    Bitmap bmp_AssinaturaInfrator = null;
    Bitmap bmp = null;

    boolean assInfrator, assAgente = false;
    String inicio, meio, fim, origem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizar_impressao);

        lblImpressaoInicial = (TextView) findViewById(R.id.lblImpressaoInicial);
        lblImpressaoMeio = (TextView) findViewById(R.id.lblImpressaoMeio);
        lblImpressaoFinal = (TextView) findViewById(R.id.lblImpressaoFinal);
        imgLogoPrefeitura = (ImageView) findViewById(R.id.imgLogoPrefeitura);
        imgAssAgente = (ImageView) findViewById(R.id.imgAssAgente);
        imgAssInfrator = (ImageView) findViewById(R.id.imgAssInfrator);

        lblImpressaoMeio.setText("");
        lblImpressaoFinal.setText("");

        idAit = (Long) getIntent().getSerializableExtra("idAit");
        origem = (String) getIntent().getSerializableExtra("origem");
        AitDAO aitdao = new AitDAO(VisualizarImpressao.this);
        Cursor c = aitdao.getAit(idAit);
        aitdao.close();


        ParametroDAO pardao = new ParametroDAO(VisualizarImpressao.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();
        try {
            orgaoAutuador = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("orgaoautuador")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        try {
            //impressao = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("impressao"))).split("\r\n");

            String filename = orgaoAutuador + ".jpg";
            String path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/" + filename;
            File f = new File(path);
            Uri uri = Uri.fromFile(f);

            bmp = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
            if (bmp != null)
                imgLogoPrefeitura.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObtemAssinaturaImpressao(idAit);
        ObtemAssinaturaInfrator(idAit);
        if (origem.equals("pfpj"))
            montaimpressaoPfpj(idAit);
        else if (origem.equals("excesso"))
            montaimpressaoExcesso(idAit);
        else
            montaimpressao(idAit);

        // if (impressao == null || impressao.equals("")) {
        // } else {
        //     exibeImpressao();
        // }
    }

    private String cancelou, especie, tipo, medidaadm, ctiplog, desclog;
    private String saida, ativo, enquads, tipoinfrator;

    private void montaimpressao(long idAit) {

        // String impressora ="00:08:1B:95:6B:AF";

        AitDAO aitdao = new AitDAO(VisualizarImpressao.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        ParametroDAO pardao = new ParametroDAO(VisualizarImpressao.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        try {
            cancelou = SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("cancelou")));

        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (cancelou.contains("NAO")) {
            try {
                ativo = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo

                EspecieDAO espdao = new EspecieDAO(VisualizarImpressao.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(VisualizarImpressao.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                MedidaAdmDAO medidadao = new MedidaAdmDAO(VisualizarImpressao.this);
                medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("medidaadm"))));
                medidadao.close();

                // pega tipo do logradouro
                ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradourotipo"))));

                switch (nx) {
                    case 1:
                        ctiplog = "OPOSTO";
                        break;
                    case 2:
                        ctiplog = "DEFRONTE";
                        break;
                    case 3:
                        ctiplog = "AO LADO DE";
                        break;
                }
                ;
                LogradouroDAO logdao = new LogradouroDAO(VisualizarImpressao.this);
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))).contains(
                        "NAO")) {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" " + ctiplog);
                    desclog += (" " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouronum"))));
                } else {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2")))));
                }
                saida = "";
                // CHR(7) & chr(29) & chr(47) & chr(1) // logo

                // **********************************
                // verifica se impressora é P25
                // **********************************
                Character cx = 0x1D;
                if (ativo.contains("P25"))
                    cx = 0x1B;
                saida += cx;

                cx = '/';
                if (ativo.contains("P25"))
                    cx = 'f';
                saida += cx;

                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("prefeitura")))
                        + String.format("\r\n");
                saida += "Orgao Autuador:"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("serieait")))
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ait")))
                        + String.format("\r\n");
                saida += "Placa:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("placa")))
                        + String.format("\r\n");
                try {
                    if (origem.equals("placa")) {
                        saida += "Uf do Veiculo:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("UfVeiculo")))
                                + String.format("\r\n");
                    }
                } catch (Exception e) {
                    saida += "Uf do Veiculo:" + String.format("\r\n");
                }
                saida += "Marca:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("marca")))
                        + String.format("\r\n");
                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("data")))
                        + "-"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("hora")))
                        + String.format("\r\n");
                saida += "Equipamento:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("seriepda")))
                        + String.format("\r\n");
                saida += "Condutor abordado:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("condutorAbordado"))).replace("NÃO", "NAO")
                        + String.format("\r\n");
                saida += "Via entregue:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("viaEntregue"))).replace("NÃO", "NAO")
                        + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "   Local da Infracao " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += this.desclog + String.format("\r\n");
                // saida += this.ctiplog+ String.format("\r\n");

                saida += "" + String.format("\r\n");

                AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
                        VisualizarImpressao.this);
                Cursor c1 = aitenq.getLista1(idAit);

                enquads = " ";
                c1.moveToNext();

                // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(VisualizarImpressao.this);
                List<Enquadramento> enquadramento = dao.getLista(
                        SimpleCrypto.decrypt(info,
                                c1.getString(c1.getColumnIndex("codigo"))),
                        VisualizarImpressao.this, "");
                dao.close();

                enquads += enquadramento.get(0).toString();

                // enquads = Utilitarios.quebraLinha(enquads);

                c1.close();

                saida += "------------------------" + String.format("\r\n");
                saida += "    Enquadramento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += enquads + String.format("\r\n");

                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "  Identif. do Infrator  " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                try {
                    tipoinfrator = SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("tipoinfrator")));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (tipoinfrator == null) {
                    saida += "Nome:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("nome")))
                            + String.format("\r\n");
                    saida += "CPF:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("cpf")))
                            + String.format("\r\n");
                    saida += "PGU:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("pgu")))
                            + " "
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("uf")))
                            + String.format("\r\n");
                } else {
                    if (tipoinfrator.contains("CNH")) {
                        saida += "Nome:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("nome")))
                                + String.format("\r\n");
                        saida += "CPF:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("cpf")))
                                + String.format("\r\n");
                        saida += "PGU:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("pgu")))
                                + " "
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("uf")))
                                + String.format("\r\n");

                    }
                    if (tipoinfrator.contains("PID")) {
                        saida += "Nome:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("nome")))
                                + String.format("\r\n");
                        saida += "Doc. de Ident.:"
                                + SimpleCrypto.decrypt(info, c.getString(c
                                .getColumnIndex("passaporte")))
                                + String.format("\r\n");
                        saida += "Pid:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("pid")))
                                + " "
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("uf")))
                                + String.format("\r\n");
                    }
                }


                //saida += "f\\i";
                try {
                    imgAssInfrator.setImageBitmap(bmp_AssinaturaInfrator);
                } catch (Exception e) {

                }

                lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

                saida = "" + String.format("\r\n");
                saida += "________________________" + String.format("\r\n");
                saida += "      Assinatura" + String.format("\r\n");
                // saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+
                // String.format("\r\n");

                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "Identificacao do Agente" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "Matric.(AG):"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("agente")))
                        + String.format("\r\n");

                saida += "" + String.format("\r\n");

                try {
                    imgAssAgente.setImageBitmap(bmp_Assinatura);
                } catch (Exception e) {

                }

                lblImpressaoMeio.setText(saida.replace("null", ""));

                saida = "" + String.format("\r\n");

                saida += "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");


                saida += "" + String.format("\r\n");
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("observacoes"))).length() > 0) {

                    if (SimpleCrypto.decrypt(info,
                            cpar.getString(cpar.getColumnIndex("imprimeobs")))
                            .contains("1")) {
                        saida += String.format("\r\n");
                        saida += "------------------------"
                                + String.format("\r\n");
                        saida += "Observacoes:" + String.format("\r\n");
                        saida += SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("observacoes")))
                                + String.format("\r\n");
                        ;

                    }

                }

                // ***********************************************
                // 28.07.2012
                //
                // imprime medida administrativa se foi definida
                // ***********************************************
                if (!medidaadm.contains(("Nao definido"))) {
                    saida += String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += "Medida Administrativa:" + String.format("\r\n");
                    saida += medidaadm + String.format("\r\n");

                }

                // **************************************************************************************
                // 08.03.2012
                // Preencheu dados equipamento, exemplo decibelímetro ?
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("equipamento"))).length() > 0) {
                    saida += String.format("\r\n");
                    saida += "Equipamento:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("equipamento")))
                            + String.format("\r\n");
                    saida += "Medicao Registrada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("medicaoreg")))
                            + String.format("\r\n");
                    saida += "Medicao Considerada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("medicaocon")))
                            + String.format("\r\n");
                    saida += "Limite Regulamentado:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("limitereg")))
                            + String.format("\r\n");

                }

                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "E obrigatoria a presenca" + String.format("\r\n");
                saida += "do codigo INFRAEST ou" + String.format("\r\n");
                saida += "RENAINF nas notificacoes" + String.format("\r\n");
                saida += "sob pena de invalidade" + String.format("\r\n");
                saida += "da multa." + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                cpar.close();

                c.close();

                lblImpressaoFinal.setText(saida.replace("null", ""));


            } catch (Exception e1) {
            }
        } else {
            try {

                ativo = SimpleCrypto.decrypt(
                        info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo
                ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradourotipo"))));

                switch (nx) {
                    case 1:
                        ctiplog = "OPOSTO";
                        break;
                    case 2:
                        ctiplog = "DEFRONTE";
                        break;
                    case 3:
                        ctiplog = "AO LADO DE";
                        break;
                }
                ;

                LogradouroDAO logdao = new LogradouroDAO(VisualizarImpressao.this);
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))).contains(
                        "NAO")) {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" " + ctiplog);
                    desclog += (" " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouronum"))));
                } else {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2")))));
                }

                EspecieDAO espdao = new EspecieDAO(VisualizarImpressao.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(VisualizarImpressao.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                saida = "";
                // CHR(7) & chr(29) & chr(47) & chr(1) // logo

                // **********************************
                // verifica se impressora é P25
                // **********************************
                Character cx = 0x1D;
                if (ativo.contains("P25"))
                    cx = 0x1B;
                saida += cx;

                cx = '/';
                if (ativo.contains("P25"))
                    cx = 'f';
                saida += cx;

                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("prefeitura")))
                        + String.format("\r\n");
                saida += "Orgao Autuador:"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("serieait")))
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ait")))
                        + String.format("\r\n");
                saida += "Placa:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("placa")))
                        + String.format("\r\n");
                saida += "Marca:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("marca")))
                        + String.format("\r\n");
                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("data")))
                        + "-"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("hora")))
                        + String.format("\r\n");
                saida += "Equipamento:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("seriepda")))
                        + String.format("\r\n");
                saida += String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += " Dados do Cancelamento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += cancelou + String.format("\r\n");
                saida += "Motivo: "
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("motivo")))
                        + String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");

                lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

              /*  try {
                    imgAssAgente.setImageBitmap(bmp_Assinatura);
                } catch (Exception e) {

                }

                saida = "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "" + String.format("\r\n");

                lblImpressaoFinal.setText(saida.replace("null", ""));*/


            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void ObtemAssinaturaImpressao(long idAit) {
        try {
            SQLiteDatabase Base = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);
            Cursor cursor = Base.rawQuery("Select arquivoAssinatura from aitAssinatura Where idAit = " + idAit, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                File imgFile = new File(cursor.getString(cursor.getColumnIndex("arquivoAssinatura")));
                if (imgFile.exists()) {
                    try {
                        Uri uri = Uri.fromFile(imgFile);
                        bmp_Assinatura = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
                        bmp_Assinatura = Bitmap.createScaledBitmap(bmp_Assinatura, 500, 100, true);
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
    }

    public void ObtemAssinaturaInfrator(long idAit) {
        try {
            SQLiteDatabase Base = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);
            Cursor cursor = Base.rawQuery("Select arquivoAssInfrator from aitAssinatura Where idAit = " + idAit, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                File imgFile = new File(cursor.getString(cursor.getColumnIndex("arquivoAssInfrator")));
                if (imgFile.exists()) {
                    try {
                        Uri uri = Uri.fromFile(imgFile);
                        bmp_AssinaturaInfrator = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
                        bmp_AssinaturaInfrator = Bitmap.createScaledBitmap(bmp_AssinaturaInfrator, 500, 100, true);
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
    }


    private void montaimpressaoPfpj(long idAit) {

        ParametroDAO pardao = new ParametroDAO(VisualizarImpressao.this);
        // String impressora ="00:01:90:E7:E6:CE";

        AitDAO aitdao = new AitDAO(VisualizarImpressao.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        Cursor cpar = pardao.getParametros();

        pardao.close();
        try {
            cancelou = SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("cancelou")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        try {
            orgaoAutuador = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("orgaoautuador")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        String modeloImpressora = "";
        try {
            if (!TextUtils.isEmpty(cpar.getString(cpar.getColumnIndex("modeloImpressora")))) {
                modeloImpressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("modeloImpressora")));
            }
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (cancelou.contains("NAO")) {
            try {
                try {
                    ativo = SimpleCrypto.decrypt(
                            info,
                            cpar.getString(cpar
                                    .getColumnIndex("impressoraPatrimonio")))
                            .toUpperCase();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {

                    // Obtem , Logradouro ,Especie, Tipo

                    // pega tipo do logradouro
                    ctiplog = " ";

                    int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradourotipo"))));

                    switch (nx) {
                        case 1:
                            ctiplog = "OPOSTO";
                            break;
                        case 2:
                            ctiplog = "DEFRONTE";
                            break;
                        case 3:
                            ctiplog = "AO LADO DE";
                            break;
                    }
                    ;

                    LogradouroDAO logdao = new LogradouroDAO(
                            VisualizarImpressao.this);
                    if (SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2"))).contains(
                            "NAO")) {
                        desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouro"))));
                        desclog += (" " + ctiplog);
                        desclog += (" " + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouronum"))));
                    } else {
                        desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouro"))));
                        desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouro2")))));
                    }
                    logdao.close();
                    saida = "";

                    // **********************************
                    // verifica se impressora é P25
                    // **********************************
                    Character cx = 0x1D;
                    if (ativo.contains("P25"))
                        cx = 0x1B;
                    saida += cx;

                    cx = '/';
                    if (ativo.contains("P25"))
                        cx = 'f';
                    saida += cx;

                    cx = 0x00;
                    saida += cx;

                    saida += String.format("\r\n");

                    saida += SimpleCrypto.decrypt(info,
                            cpar.getString(cpar.getColumnIndex("prefeitura")))
                            + String.format("\r\n");
                    saida += "Orgao Autuador:"
                            + SimpleCrypto.decrypt(info, cpar.getString(cpar
                            .getColumnIndex("orgaoautuador")))
                            + String.format("\r\n");

                    saida += "------------------------" + String.format("\r\n");
                    saida += "   Dados da Infracao   " + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");

                    saida += "Ait:"
                            + SimpleCrypto.decrypt(info, cpar.getString(cpar
                            .getColumnIndex("serieait")))
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("ait")))
                            + String.format("\r\n");
                    saida += "Data:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("data")))
                            + "-"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("hora")))
                            + String.format("\r\n");
                    saida += "Equipamento:"
                            + SimpleCrypto.decrypt(info, cpar.getString(cpar
                            .getColumnIndex("seriepda")))
                            + String.format("\r\n");
                    saida += "Condutor abordado:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("condutorAbordado"))).replace("NÃO", "NAO")
                            + String.format("\r\n");
                    saida += "Via entregue:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("viaEntregue"))).replace("NÃO", "NAO")
                            + String.format("\r\n");

                    saida += "------------------------" + String.format("\r\n");
                    saida += "   Local da Infracao   " + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");

                    saida += this.desclog + String.format("\r\n");
                    saida += this.ctiplog + String.format("\r\n");

                    // AitEnquadramentoDAO aitenq = new
                    // AitEnquadramentoDAO(ExibeDadosAitpfpj.this);
                    // Cursor c1 = aitenq.getLista1(idAit);

                    // enquads = " ";
                    // c1.moveToNext();

                    // enquads += c1.getString(c1.getColumnIndex("codigo")) +
                    // " ";

                    // EnquadramentoDAO dao = new
                    // EnquadramentoDAO(ExibeDadosAitpfpj.this);
                    // List<Enquadramento> enquadramento =
                    // dao.getLista(SimpleCrypto.decrypt(info,c1.getString(c1.getColumnIndex("codigo"))),ExibeDadosAitpfpj.this);
                    // dao.close();

                    // enquads += enquadramento.get(0).toString() + " / ";

                    // c1.close();
                    saida += "------------------------" + String.format("\r\n");
                    saida += "     Enquadramento" + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += enquads + String.format("\r\n");

                    saida += "------------------------" + String.format("\r\n");
                    saida += "  Identf. do Infrator   " + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");

                    saida += "Nome:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("nome")))
                            + String.format("\r\n");


                    if (SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("tipoait"))).equals(
                            "2"))
                        saida += "CPF:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("cpf")))
                                + String.format("\r\n");
                    else
                        saida += "CNPJ:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("cpf")))
                                + String.format("\r\n");


                    try {
                        imgAssInfrator.setImageBitmap(bmp_AssinaturaInfrator);
                    } catch (Exception e) {

                    }
                    lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

                    saida = "" + String.format("\r\n");
                    saida += "________________________" + String.format("\r\n");
                    saida += "      Assinatura" + String.format("\r\n");

                    saida += "------------------------" + String.format("\r\n");
                    saida += "Identificacao do Agente" + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += "Matric.(AG):"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("agente")))
                            + String.format("\r\n");

                    //saida += "" + String.format("\r\n");
                    //saida += "________________________" + String.format("\r\n");
                    //saida += "     Lavrado por        " + String.format("\r\n");

                    // ***********************************************
                    // 28.07.2012
                    //
                    // imprime medida administrativa se foi definida
                    // ***********************************************
                    saida += "" + String.format("\r\n");

                    try {
                        imgAssAgente.setImageBitmap(bmp_Assinatura);
                    } catch (Exception e) {

                    }

                    lblImpressaoMeio.setText(saida.replace("null", ""));

                    saida = "";
                    if (!medidaadm.contains(("Nao definido"))) {
                        saida += String.format("\r\n");
                        saida += "------------------------"
                                + String.format("\r\n");
                        saida += "Medida Administrativa:"
                                + String.format("\r\n");
                        saida += medidaadm + String.format("\r\n");

                    }

                    if (SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("observacoes")))
                            .length() > 0) {
                        if (SimpleCrypto.decrypt(
                                info,
                                cpar.getString(cpar
                                        .getColumnIndex("imprimeobs")))
                                .contains("1")) {
                            saida += String.format("\r\n");
                            saida += "------------------------"
                                    + String.format("\r\n");
                            saida += "     Observacoes:"
                                    + String.format("\r\n");
                            saida += SimpleCrypto.decrypt(info, c.getString(c
                                    .getColumnIndex("observacoes")))
                                    + String.format("\r\n");

                        }
                    }

                    saida += String.format("\r\n");
                    saida += String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += "E obrigatoria a presenca" + String.format("\r\n");
                    saida += "do codigo INFRAEST ou" + String.format("\r\n");
                    saida += "RENAINF nas notificacoes" + String.format("\r\n");
                    saida += "sob pena de invalidade" + String.format("\r\n");
                    saida += "da multa." + String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");

                    saida += String.format("\r\n");
                    saida += String.format("\r\n");


                    lblImpressaoFinal.setText(saida.replace("null", ""));
                    cpar.close();

                    c.close();

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {

            try {
                ativo = SimpleCrypto.decrypt(
                        info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {

                // Obtem , Logradouro ,Especie, Tipo
                LogradouroDAO logdao = new LogradouroDAO(VisualizarImpressao.this);
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))).contains(
                        "NAO")) {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" " + ctiplog);
                    desclog += (" " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouronum"))));
                } else {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2")))));
                }
                logdao.close();

                // pega tipo do logradouro
                ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradourotipo"))));

                switch (nx) {
                    case 1:
                        ctiplog = "OPOSTO";
                        break;
                    case 2:
                        ctiplog = "DEFRONTE";
                        break;
                    case 3:
                        ctiplog = "AO LADO DE";
                        break;
                }
                ;

                saida = "";

                // **********************************
                // verifica se impressora é P25
                // **********************************
                Character cx = 0x1D;
                if (ativo.contains("P25"))
                    cx = 0x1B;
                saida += cx;

                cx = '/';
                if (ativo.contains("P25"))
                    cx = 'f';
                saida += cx;

                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("prefeitura")))
                        + String.format("\r\n");
                saida += "Orgao Autuador:"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao   " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("serieait")))
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ait")))
                        + String.format("\r\n");
                saida += "Data:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("data")))
                        + "-"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("hora")))
                        + String.format("\r\n");
                saida += "Equipamento:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("seriepda")))
                        + String.format("\r\n");
                saida += "Nome:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("nome")))
                        + String.format("\r\n");

                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipoait"))).equals("2"))
                    saida += "CPF:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("cpf")))
                            + String.format("\r\n");
                else
                    saida += "CNPJ:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("cpf")))
                            + String.format("\r\n");

                saida += String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += " Dados do Cancelamento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += cancelou + String.format("\r\n");
                saida += "Motivo: "
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("motivo")))
                        + String.format("\r\n");

                saida += String.format("\r\n");
                saida += String.format("\r\n");
                lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

                try {
                    imgAssAgente.setImageBitmap(bmp_Assinatura);
                } catch (Exception e) {

                }

                saida = "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "" + String.format("\r\n");

                lblImpressaoFinal.setText(saida.replace("null", ""));

                cpar.close();

                c.close();

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }


    private void montaimpressaoExcesso(long idAit) {

        // String impressora ="00:08:1B:95:6B:AF";

        AitDAO aitdao = new AitDAO(VisualizarImpressao.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        ParametroDAO pardao = new ParametroDAO(VisualizarImpressao.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        try {
            cancelou = SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("cancelou")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        try {
            orgaoAutuador = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("orgaoautuador")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        String modeloImpressora = "";
        try {
            if (!TextUtils.isEmpty(cpar.getString(cpar.getColumnIndex("modeloImpressora")))) {
                modeloImpressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("modeloImpressora")));
            }
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (cancelou.contains("NAO")) {
            try {
                ativo = SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo

                EspecieDAO espdao = new EspecieDAO(VisualizarImpressao.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(VisualizarImpressao.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                MedidaAdmDAO medidadao = new MedidaAdmDAO(VisualizarImpressao.this);
                medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("medidaadm"))));
                medidadao.close();

                // pega tipo do logradouro
                ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradourotipo"))));

                switch (nx) {
                    case 1:
                        ctiplog = "OPOSTO";
                        break;
                    case 2:
                        ctiplog = "DEFRONTE";
                        break;
                    case 3:
                        ctiplog = "AO LADO DE";
                        break;
                }
                ;
                LogradouroDAO logdao = new LogradouroDAO(VisualizarImpressao.this);
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))).contains(
                        "NAO")) {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" " + ctiplog);
                    desclog += (" " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouronum"))));
                } else {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2")))));
                }


                saida = "";
                // CHR(7) & chr(29) & chr(47) & chr(1) // logo

                // **********************************
                // verifica se impressora é P25
                // **********************************
                Character cx = 0x1D;
                if (ativo.contains("P25"))
                    cx = 0x1B;
                saida += cx;

                cx = '/';
                if (ativo.contains("P25"))
                    cx = 'f';
                saida += cx;

                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("prefeitura")))
                        + String.format("\r\n");
                saida += "Orgao Autuador:"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("serieait")))
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ait")))
                        + String.format("\r\n");
                saida += "Placa:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("placa")))
                        + String.format("\r\n");
                saida += "Marca:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("marca")))
                        + String.format("\r\n");
                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("data")))
                        + "-"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("hora")))
                        + String.format("\r\n");
                saida += "Equipamento:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("seriepda")))
                        + String.format("\r\n");
                saida += "Condutor abordado:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("condutorAbordado"))).replace("NÃO", "NAO")
                        + String.format("\r\n");
                saida += "Via entregue:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("viaEntregue"))).replace("NÃO", "NAO")
                        + String.format("\r\n");

                int Limite = 0;
                int Tara = 0;
                int PesoDeclarado = 0;
                int ExcessoConstatado = 0;
                try {
                    Limite = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("limitePermitido_excesso"))));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    Tara = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tara_excesso"))));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    PesoDeclarado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pesoDeclarado_excesso"))));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    ExcessoConstatado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("excessoConstatado_excesso"))));
                } catch (Exception e) {
                    // TODO: handle exception
                }

                String LimiteString = NumberFormat.getNumberInstance().format(Limite);
                String TaraString = NumberFormat.getNumberInstance().format(Tara);
                String PesoDeclaradoString = NumberFormat.getNumberInstance().format(PesoDeclarado);
                String ExcessoConstatadoString = NumberFormat.getNumberInstance().format(ExcessoConstatado);

                saida += "Limite de Peso:"
                        + LimiteString + " Kg"
                        + String.format("\r\n");

                saida += "Tara:"
                        + TaraString + " Kg" + String.format("\r\n");

                saida += "Peso Declarado:"
                        + PesoDeclaradoString + " Kg" + String.format("\r\n");

                saida += "Excesso Const.:"
                        + ExcessoConstatadoString + " Kg" + String.format("\r\n");


                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "   Local da Infracao " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += this.desclog + String.format("\r\n");
                saida += "Posto:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("Posto_Agente"))) + String.format("\r\n");
                MunicipioDAO MuD = new MunicipioDAO(VisualizarImpressao.this);
                List<Municipio> Lista_Municipio_Agente = MuD.GetCidade(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("IdMunicipio_Agente"))));
                String UF_Agente = "";
                String Cidade_Agente = "";
                String CodigoMunicipioProdesp_Agente = "";
                if (Lista_Municipio_Agente.size() > 0) {
                    try {
                        UF_Agente = Lista_Municipio_Agente.get(0).getUF();
                        Cidade_Agente = Lista_Municipio_Agente.get(0).getCidade();
                        CodigoMunicipioProdesp_Agente = Lista_Municipio_Agente.get(0).getIdProdesp();

                    } catch (Exception e) {
                        // TODO: handle exception
                        String Erro = e.getMessage();
                    }
                }
                saida += "Codigo do Municipio:" + CodigoMunicipioProdesp_Agente + String.format("\r\n");
                saida += "Cidade/UF:" + Cidade_Agente + " - " + UF_Agente + String.format("\r\n");
                // saida += this.ctiplog+ String.format("\r\n");

                saida += "" + String.format("\r\n");

                AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
                        VisualizarImpressao.this);
                Cursor c1 = aitenq.getLista1(idAit);

                enquads = " ";
                c1.moveToNext();

                // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(VisualizarImpressao.this);
                List<Enquadramento> enquadramento = dao.getLista(
                        SimpleCrypto.decrypt(info,
                                c1.getString(c1.getColumnIndex("codigo"))),
                        VisualizarImpressao.this, "");
                dao.close();

                enquads += enquadramento.get(0).toString();

                // enquads = Utilitarios.quebraLinha(enquads);

                c1.close();

                saida += "------------------------" + String.format("\r\n");
                saida += "    Enquadramento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += enquads + String.format("\r\n");

                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "   Ident. do Condutor  " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                try {
                    tipoinfrator = SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("tipoinfrator")));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (tipoinfrator == null) {
                    saida += "Nome:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("nome")))
                            + String.format("\r\n");
                    saida += "CPF:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("cpf")))
                            + String.format("\r\n");
                    saida += "CNH:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("pgu")))
                            + " "
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("uf")))
                            + String.format("\r\n");
                    String PPD = SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("ppd_condutor")));
                    if (PPD.equals("S")) {
                        saida += "PPD:SIM" + String.format("\r\n");
                    }
                    if (PPD.equals("N")) {
                        saida += "PPD:NÃO" + String.format("\r\n");
                    }
                } else {
                    if (tipoinfrator.contains("CNH")) {
                        saida += "Nome:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("nome")))
                                + String.format("\r\n");
                        saida += "CPF:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("cpf")))
                                + String.format("\r\n");
                        saida += "CNH:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("pgu")))
                                + " "
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("uf")))
                                + String.format("\r\n");

                    }
                    if (tipoinfrator.contains("PID")) {
                        saida += "Nome:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("nome")))
                                + String.format("\r\n");
                        saida += "Doc. de Ident.:"
                                + SimpleCrypto.decrypt(info, c.getString(c
                                .getColumnIndex("passaporte")))
                                + String.format("\r\n");
                        saida += "Pid:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("pid")))
                                + " "
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("uf")))
                                + String.format("\r\n");
                    }
                }


                saida += "" + String.format("\r\n");
                saida += "________________________" + String.format("\r\n");
                saida += "      Assinatura" + String.format("\r\n");
                // saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+
                // String.format("\r\n");

                saida += "" + String.format("\r\n");

                NotaFiscalDAO NfDAO = new NotaFiscalDAO(VisualizarImpressao.this);
                List<br.com.cobrasin.tabela.NotaFiscal> NfLista = NfDAO
                        .GetNotasAit(idAit);

                if (NfLista.size() == 1) {
                    try {
                        saida += "------------------------"
                                + String.format("\r\n");
                        saida += "   Dados do Embarcador  "
                                + String.format("\r\n");
                        saida += "------------------------"
                                + String.format("\r\n");

                        String Nome = SimpleCrypto.decrypt(info, c.getString(c
                                .getColumnIndex("nome_embarcador")));
                        String CPFCNPJ = SimpleCrypto.decrypt(info, c
                                .getString(c
                                        .getColumnIndex("cpfCnpj_embarcador")));
                        String Endereco = SimpleCrypto
                                .decrypt(info, c.getString(c
                                        .getColumnIndex("endereco_embarcador")));

                        String Bairro = SimpleCrypto.decrypt(info, c
                                .getString(c
                                        .getColumnIndex("bairro_embarcador")));

                        String IdMunicipio = SimpleCrypto
                                .decrypt(
                                        info,
                                        c.getString(c
                                                .getColumnIndex("IdMunicipio_embarcador")));

                        MunicipioDAO MuDAO = new MunicipioDAO(
                                VisualizarImpressao.this);
                        List<Municipio> Lista_Municipio = MuDAO
                                .GetCidade(IdMunicipio);
                        String UF = "";
                        String Cidade = "";
                        if (Lista_Municipio.size() > 0) {
                            try {
                                UF = Lista_Municipio.get(0).getUF();
                                Cidade = Lista_Municipio.get(0).getCidade();

                            } catch (Exception e) {
                                // TODO: handle exception
                                String Erro = e.getMessage();
                            }
                        }

                        saida += "Nome:" + Nome + String.format("\r\n");

                        saida += "CPF/CNPJ:" + CPFCNPJ + String.format("\r\n");

                        saida += "Endereco:" + Endereco + String.format("\r\n");

                        saida += "Cidade/UF:" + Cidade + " - " + UF
                                + String.format("\r\n");

                        saida += "Bairro:" + Bairro + String.format("\r\n");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                    }
                }
                if (NfLista.size() > 1) {
                    try {
                        saida += "------------------------"
                                + String.format("\r\n");
                        saida += "      Transportador  "
                                + String.format("\r\n");
                        saida += "------------------------"
                                + String.format("\r\n");

                        String Nome = SimpleCrypto.decrypt(info, c.getString(c
                                .getColumnIndex("nome_transportador")));
                        String CPFCNPJ = SimpleCrypto
                                .decrypt(
                                        info,
                                        c.getString(c
                                                .getColumnIndex("cpfCnpj_transportador")));
                        String Endereco = SimpleCrypto
                                .decrypt(
                                        info,
                                        c.getString(c
                                                .getColumnIndex("endereco_transportador")));

                        String Bairro = SimpleCrypto
                                .decrypt(
                                        info,
                                        c.getString(c
                                                .getColumnIndex("bairro_transportador")));

                        String IdMunicipio = SimpleCrypto
                                .decrypt(
                                        info,
                                        c.getString(c
                                                .getColumnIndex("IdMunicipio_transportador")));

                        MunicipioDAO MuDAO = new MunicipioDAO(
                                VisualizarImpressao.this);
                        List<Municipio> Lista_Municipio = MuDAO
                                .GetCidade(IdMunicipio);
                        String UF = "";
                        String Cidade = "";
                        if (Lista_Municipio.size() > 0) {
                            try {
                                UF = Lista_Municipio.get(0).getUF();
                                Cidade = Lista_Municipio.get(0).getCidade();

                            } catch (Exception e) {
                                // TODO: handle exception
                                String Erro = e.getMessage();
                            }
                        }

                        saida += "Nome:" + Nome + String.format("\r\n");

                        saida += "CPF/CNPJ:" + CPFCNPJ + String.format("\r\n");

                        saida += "Endereco:" + Endereco + String.format("\r\n");

                        saida += "Cidade/UF:" + Cidade + " - " + UF
                                + String.format("\r\n");

                        saida += "Bairro:" + Bairro + String.format("\r\n");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                NfDAO.close();

                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "Identificacao do Agente" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "Matric.(AG):"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("agente")))
                        + String.format("\r\n");

                //saida += "" + String.format("\r\n");
                //saida += "________________________" + String.format("\r\n");
                //saida += "     Lavrado por" + String.format("\r\n");

                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("observacoes"))).length() > 0) {

                    if (SimpleCrypto.decrypt(info,
                            cpar.getString(cpar.getColumnIndex("imprimeobs")))
                            .contains("1")) {
                        saida += String.format("\r\n");
                        saida += "------------------------"
                                + String.format("\r\n");
                        saida += "Observacoes:" + String.format("\r\n");
                        saida += SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("observacoes")))
                                + String.format("\r\n");
                        ;

                    }

                }

                // ***********************************************
                // 28.07.2012
                //
                // imprime medida administrativa se foi definida
                // ***********************************************
                if (!medidaadm.contains(("Nao definido"))) {
                    saida += String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += "Medida Administrativa:" + String.format("\r\n");
                    saida += medidaadm + String.format("\r\n");

                }

                // **************************************************************************************
                // 08.03.2012
                // Preencheu dados equipamento, exemplo decibelímetro ?
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("equipamento"))).length() > 0) {
                    saida += String.format("\r\n");
                    saida += "Equipamento:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("equipamento")))
                            + String.format("\r\n");
                    saida += "Medicao Registrada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("medicaoreg")))
                            + String.format("\r\n");
                    saida += "Medicao Considerada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("medicaocon")))
                            + String.format("\r\n");
                    saida += "Limite Regulamentado:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("limitereg")))
                            + String.format("\r\n");

                }

                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "E obrigatoria a presenca" + String.format("\r\n");
                saida += "do codigo INFRAEST ou" + String.format("\r\n");
                saida += "RENAINF nas notificacoes" + String.format("\r\n");
                saida += "sob pena de invalidade" + String.format("\r\n");
                saida += "da multa." + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += String.format("\r\n");
                saida += String.format("\r\n");
                lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

                try {
                    imgAssAgente.setImageBitmap(bmp_Assinatura);
                } catch (Exception e) {

                }

                saida = "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "" + String.format("\r\n");

                lblImpressaoFinal.setText(saida.replace("null", ""));

                cpar.close();

                c.close();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            try {

                ativo = SimpleCrypto.decrypt(
                        info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo
                ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradourotipo"))));

                switch (nx) {
                    case 1:
                        ctiplog = "OPOSTO";
                        break;
                    case 2:
                        ctiplog = "DEFRONTE";
                        break;
                    case 3:
                        ctiplog = "AO LADO DE";
                        break;
                }
                ;

                LogradouroDAO logdao = new LogradouroDAO(VisualizarImpressao.this);
                if (SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))).contains(
                        "NAO")) {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" " + ctiplog);
                    desclog += (" " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouronum"))));
                } else {
                    desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro"))));
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2")))));
                }

                EspecieDAO espdao = new EspecieDAO(VisualizarImpressao.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(VisualizarImpressao.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                saida = "";
                // CHR(7) & chr(29) & chr(47) & chr(1) // logo

                // **********************************
                // verifica se impressora é P25
                // **********************************
                Character cx = 0x1D;
                if (ativo.contains("P25"))
                    cx = 0x1B;
                saida += cx;

                cx = '/';
                if (ativo.contains("P25"))
                    cx = 'f';
                saida += cx;

                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("prefeitura")))
                        + String.format("\r\n");
                saida += "Orgao Autuador:"
                        + SimpleCrypto.decrypt(info, cpar.getString(cpar
                        .getColumnIndex("orgaoautuador")))
                        + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("serieait")))
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ait")))
                        + String.format("\r\n");
                saida += "Placa:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("placa")))
                        + String.format("\r\n");
                saida += "Marca:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("marca")))
                        + String.format("\r\n");
                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("data")))
                        + "-"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("hora")))
                        + String.format("\r\n");
                saida += "Equipamento:"
                        + SimpleCrypto
                        .decrypt(info, cpar.getString(cpar
                                .getColumnIndex("seriepda")))
                        + String.format("\r\n");
                saida += String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += " Dados do Cancelamento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += cancelou + String.format("\r\n");
                saida += "Motivo: "
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("motivo")))
                        + String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                lblImpressaoInicial.setText(saida.replace("null", "").replace("/", ""));

                try {
                    imgAssAgente.setImageBitmap(bmp_Assinatura);
                } catch (Exception e) {

                }

                saida = "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "" + String.format("\r\n");

                lblImpressaoFinal.setText(saida.replace("null", ""));

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }
}
