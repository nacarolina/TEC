package br.com.cobrasin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.crypto.ExemptionMechanism;
import javax.crypto.ExemptionMechanismException;
import javax.crypto.ExemptionMechanismSpi;

import android.R.string;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.*;

import br.com.cobrasin.ImprimeAit.ThreadConexao;
import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Municipio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.wpx.IBluetoothPrint;
import com.wpx.WPXMain;
import com.wpx.util.GeneralAttributes;
import com.wpx.util.WPXUtils;

import static br.com.cobrasin.Utilitarios.ByteArrayCodePrintImage;
import static br.com.cobrasin.Utilitarios.bitmapToByteArray;

public class ImprimeAit extends Activity {

    /**
     * Called when the activity is first created.
     */
    private String info = Utilitarios.getInfo();
    private List<Ait> aitvisualiza;
    private String salvaAgente;
    private ListView listaait;
    // private boolean termina = false;
    private ArrayAdapter<Ait> adapter;
    private String tipoait;
    private ProgressDialog progress;
    private byte[] buffer;
    private Toast aviso, avisoerro;
    private String saida, impressora, ativo;
    protected Handler handler;
    private String orgaoAutuador;
    private String cancelou;

    private String desclog;
    private String ctiplog;
    private String enquads;
    private String especie;
    private String tipo;
    private String medidaadm;
    private Button btnImprimirSel;
    private String exibe[] = new String[27];

    private String tipoinfrator;

    private String spIni;
    private String spFinalSel;
    BluetoothSocket mmSocket;
    private String mens;
    BluetoothSocket tmp = null;
    OutputStream mmOutStream = null;

    private static final String TAG = "CobrasinAitBt";
    private static String modeloImpressora = "";

    private ThreadConexao tconx;
    private ThreadConexaoSkyPix tconxSkyPix;

    private long idAit;

    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    public void ImprimeAits() {
        setContentView(R.layout.imprimeait);
        final Spinner spInicial = (Spinner) findViewById(R.id.spInicial);
        final Spinner spFinal = (Spinner) findViewById(R.id.spFinal);
        String salvaAgente = "";

        AitDAO a = new AitDAO(ImprimeAit.this);
        salvaAgente = getIntent().getStringExtra("agente");// .getSerializableExtra("agente");
        List<Ait> ls = a.getListaAitPrint(salvaAgente); // ((String)
        // getIntent().getSerializableExtra(salvaAgente));

        if (ls.size() > 0) {
            String[] s = new String[ls.size()];
            String[] s2 = new String[ls.size()];
            int i = 0;// ;//
            int i2 = 0;
            // Toast t=new Toast(getBaseContext());

            for (Ait b : ls) {
                // Toast.makeText(this,b.getAit(), Toast.LENGTH_LONG);
                s[i] = (String) b.getAit();
                i++;
            }

            i--;
            while (ls.size() > i2) {
                s2[i2] = s[i];
                i--;
                i2++;
            }
            // s[0]="1";
            // s[1]="2";
            ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, s);
            ArrayAdapter<String> adaptadorSpinner2 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, s2);

            spInicial.setAdapter(adaptadorSpinner2);

            spFinal.setAdapter(adaptadorSpinner);
            // spFinal.

        }

        spInicial
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View v,
                                               int posicao, long id) {

                        String AitSel;
                        AitSel = parent.getItemAtPosition(
                                spInicial.getSelectedItemPosition()).toString();
                        spIni = AitSel;
                        // mensagemExibir("Mesa","Mesa selecionada: " +
                        // mesa_selecionada);
                        // Toast.makeText(getBaseContext(),
                        // "Ait selecionado: "+AitSel,
                        // Toast.LENGTH_LONG).show();

                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

        spFinal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View v,
                                       int posicao, long id) {

                String AitSel;
                AitSel = parent.getItemAtPosition(
                        spFinal.getSelectedItemPosition()).toString();
                spFinalSel = AitSel;
                // mensagemExibir("Mesa","Mesa selecionada: " +
                // mesa_selecionada);
                // Toast.makeText(getBaseContext(), "Ait selecionado: "+AitSel,
                // Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void carregaSpinner(Spinner spInicial, List<String> listaSpinnerAit) {
        final ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, listaSpinnerAit);
        // spInicial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInicial.setAdapter(adaptadorSpinner);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        handler = new Handler(getMainLooper());
        // setContentView(R.layout.imprimeait);
        ImprimeAits();
        btnImprimirSel = (Button) findViewById(R.id.btnImprimirSel);
        btnImprimirSel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chamaImpressao();
            }
        });
    }

    public void chamaImpressao() {

        aviso = Toast.makeText(ImprimeAit.this, "Dados enviados com sucesso!",
                Toast.LENGTH_LONG);
        avisoerro = Toast.makeText(ImprimeAit.this,
                "Não consegui enviar dados...", Toast.LENGTH_LONG);

        ParametroDAO pardao = new ParametroDAO(ImprimeAit.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        try {
            impressora = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("impressoraMAC")));
            orgaoAutuador = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("orgaoautuador")));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(cpar.getString(cpar.getColumnIndex("modeloImpressora")))) {
                modeloImpressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("modeloImpressora")));
            }
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (modeloImpressora.equals("SkyPix")) {
            progress = ProgressDialog.show(ImprimeAit.this, "Aguarde...",
                    "Enviando dados para a Impressora!!!", true, true);
            tconxSkyPix = new ThreadConexaoSkyPix();
            tconxSkyPix.start();
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                    .getDefaultAdapter();
            BluetoothDevice mmDevice;
            boolean passou;
            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);

                // Make a connection to the BluetoothSocket
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    AitDAO a = new AitDAO(ImprimeAit.this);
                    List<Ait> ls = a.getListaAitPrint((String) getIntent()
                            .getSerializableExtra("agente"));

                    for (Ait b : ls) {
                        // Spinner spIni = (Spinner) findViewById(R.id.spInicial);
                        // Spinner spFinal = (Spinner) findViewById(R.id.spFinal);

                        // long aitini=
                        // Long.parseLong(spIni.getSelectedItem().toString());
                        // long aitfinal=
                        // Long.parseLong(spFinal.getSelectedItem().toString());
                        long ait = (Long.parseLong(b.getAit()));
                        String aitC = b.getAit().toString();// .substring(4).toString();
                        long Idait = (b.getId());
                        long aitini = Long.parseLong(ImprimeAit.this.spIni);
                        long aitfin = Long.parseLong(ImprimeAit.this.spFinalSel);// spFinalSel);

                        try {
                            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);

                        } catch (IOException e) {

                            progress.dismiss();
                            avisoerro.show();
                            Log.e(TAG, "create() failed", e);
                        }
                        mmSocket = tmp;

                        mmSocket.connect();

                        mmOutStream = mmSocket.getOutputStream();

                        if (aitini <= ait) {
                            if (aitfin >= ait) {
                                ObtemAssinaturaImpressao(Idait);
                                ObtemAssinaturaInfrator(Idait);
                                montaimpressao(Idait);
                            /*buffer = mens.getBytes();`
                            mmOutStream.write(buffer, 0, buffer.length);*/

                                try {
                                    Thread.sleep(6000);
                                    Ait ait1 = new Ait();
                                    ait1.setImpressao(mens);
                                    ait1.setId(Idait);
                                    AitDAO aitdao = new AitDAO(ImprimeAit.this);
                                    aitdao.gravaImpressao(ait1);
                                    aitdao.close();
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    mmSocket.close();

                    progress.dismiss();
                    aviso.show();

                } catch (IOException e) {

                    progress.dismiss();
                    avisoerro.show();
                    // Close the socket
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        Log.e(TAG,
                                "unable to close() socket during connection failure",
                                e2);
                    }
                    // Start the service over to restart listening mode

                    return;
                }


            } catch (Exception e) {
                AlertDialog.Builder aviso = new AlertDialog.Builder(ImprimeAit.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("TEC");
                aviso.setMessage("Falha ao imprimir!\nImpressora não instalada!");
                aviso.setNeutralButton("OK", null);
                aviso.show();
                passou = false;
                return;
            }
          /*  if (passou == true) {
                tconx = new ThreadConexao(mmDevice);
                tconx.start();
            }*/
        }
    }

    Bitmap bmp = null;
    Bitmap bmp_Assinatura = null;
    Bitmap bmp_AssinaturaInfrator = null;

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
            } else
                bmp_Assinatura = null;
            cursor.close();
            Base.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
            bmp_Assinatura = null;
        } catch (Exception e) {
            e.printStackTrace();
            bmp_Assinatura = null;
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
            } else
                bmp_AssinaturaInfrator = null;
            cursor.close();
            Base.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
            bmp_AssinaturaInfrator = null;
        } catch (Exception e) {
            e.printStackTrace();
            bmp_AssinaturaInfrator = null;
        }
    }


    public class ThreadConexao extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private OutputStream mmOutStream = null;
        private String mens;
        private String desclog;
        private String ctiplog;

        public ThreadConexao(BluetoothDevice device) {
            // mens = texto;
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {

                progress.dismiss();
                avisoerro.show();
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }

    private void montaimpressao(long idAit) {

        // String impressora ="00:08:1B:95:6B:AF";

        AitDAO aitdao = new AitDAO(ImprimeAit.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        ParametroDAO pardao = new ParametroDAO(ImprimeAit.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        try {
            cancelou = SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("cancelou")));

            progress = ProgressDialog.show(ImprimeAit.this, "Aguarde...",
                    "Enviando dados para a Impressora!!!", true, true);
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (cancelou.contains("NAO")) {
            try {
                impressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("impressoraMAC")));
                ativo = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo

                EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                MedidaAdmDAO medidadao = new MedidaAdmDAO(ImprimeAit.this);
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
                LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
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
                    saida += "Uf do Veiculo:"
                            + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("UfVeiculo")))
                            + String.format("\r\n");
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
                        ImprimeAit.this);
                Cursor c1 = aitenq.getLista1(idAit);

                enquads = " ";
                c1.moveToNext();

                // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(ImprimeAit.this);
                List<Enquadramento> enquadramento = dao.getLista(
                        SimpleCrypto.decrypt(info,
                                c1.getString(c1.getColumnIndex("codigo"))),
                        ImprimeAit.this, "");
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


                saida += "f\\i";
                try {
                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                        IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                        bp.printText(saida.replace("f\\i", ""));
                    } else {
                        byte[] buffer = saida.replace("f\\i", "").getBytes();
                        mmOutStream.write(buffer, 0, buffer.length);
                    }


                } catch (Exception e) {

                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao conectar na impressora!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                    avisoerro.show();
                    return;
                }

                if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (bmp_AssinaturaInfrator != null) {
                        Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_AssinaturaInfrator.getWidth(), bmp_AssinaturaInfrator.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmpMonochrome);
                        ColorMatrix ma = new ColorMatrix();
                        ma.setSaturation(0);
                        Paint paint = new Paint();
                        paint.setColorFilter(new ColorMatrixColorFilter(ma));
                        canvas.drawBitmap(bmp_AssinaturaInfrator, 0, 0, paint);

                        int width = bmpMonochrome.getWidth();
                        int height = bmpMonochrome.getHeight();

                        int[] pixels = new int[width * height];
                        bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                int pixel = bmpMonochrome.getPixel(x, y);
                                int lowestBit = pixel & 0xff;
                                if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                                else bmpMonochrome.setPixel(x, y, Color.WHITE);
                            }
                        }
                        IBluetoothPrint imgbp = WPXMain.getBluetoothPrint();
                        byte gravity = 1;
                        IBluetoothPrint.Describe des = new IBluetoothPrint.Describe();
                        des.setGravity(gravity);
                        imgbp.printBitmap(bmpMonochrome, des);
                    }
                } else {
                    try {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (bmp_AssinaturaInfrator != null) {
                            Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_AssinaturaInfrator.getWidth(), bmp_AssinaturaInfrator.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(bmp_AssinaturaInfrator, 0, 0, paint);

                            int width = bmpMonochrome.getWidth();
                            int height = bmpMonochrome.getHeight();

                            int[] pixels = new int[width * height];
                            bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    int pixel = bmpMonochrome.getPixel(x, y);
                                    int lowestBit = pixel & 0xff;
                                    if (lowestBit < 128)
                                        bmpMonochrome.setPixel(x, y, Color.BLACK);
                                    else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                }
                            }

                            byte[] formats = ByteArrayCodePrintImage(bmpMonochrome);
                            byte[] image = bitmapToByteArray(bmpMonochrome);
                            byte[] bytes = new byte[formats.length + image.length];
                            System.arraycopy(formats, 0, bytes, 0, formats.length);
                            System.arraycopy(image, 0, bytes, formats.length, image.length);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            mmOutStream.write(bytes, 0, bytes.length);
                        }

                        //mmSocket.close();
                    } catch (Exception e) {

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao imprimir assinatura do infrator!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mens = saida;

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

                saida += "f\\f";

                try {
                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                        IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                        bp.printText(saida.replace("f\\f", ""));
                    } else {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        byte[] buffer = saida.replace("f\\f", "").getBytes();
                        mmOutStream.write(buffer, 0, buffer.length);
                    }
                } catch (Exception e) {

                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao conectar na impressora!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                    avisoerro.show();
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                saida += "" + String.format("\r\n");
                if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {

                    if (bmp_Assinatura != null) {
                        Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_Assinatura.getWidth(), bmp_Assinatura.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bmpMonochrome);
                        ColorMatrix ma = new ColorMatrix();
                        ma.setSaturation(0);
                        Paint paint = new Paint();
                        paint.setColorFilter(new ColorMatrixColorFilter(ma));
                        canvas.drawBitmap(bmp_Assinatura, 0, 0, paint);

                        int width = bmpMonochrome.getWidth();
                        int height = bmpMonochrome.getHeight();

                        int[] pixels = new int[width * height];
                        bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                                int pixel = bmpMonochrome.getPixel(x, y);
                                int lowestBit = pixel & 0xff;
                                if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                                else bmpMonochrome.setPixel(x, y, Color.WHITE);
                            }
                        }
                        IBluetoothPrint imgbp = WPXMain.getBluetoothPrint();
                        byte gravity = 1;
                        IBluetoothPrint.Describe des = new IBluetoothPrint.Describe();
                        des.setGravity(gravity);
                        imgbp.printBitmap(bmpMonochrome, des);
                    }
                } else {
                    try {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (bmp_Assinatura != null) {
                            Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_Assinatura.getWidth(), bmp_Assinatura.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(bmp_Assinatura, 0, 0, paint);

                            int width = bmpMonochrome.getWidth();
                            int height = bmpMonochrome.getHeight();

                            int[] pixels = new int[width * height];
                            bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    int pixel = bmpMonochrome.getPixel(x, y);
                                    int lowestBit = pixel & 0xff;
                                    if (lowestBit < 128)
                                        bmpMonochrome.setPixel(x, y, Color.BLACK);
                                    else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                }
                            }

                            byte[] formats = ByteArrayCodePrintImage(bmpMonochrome);
                            byte[] image = bitmapToByteArray(bmpMonochrome);
                            byte[] bytes = new byte[formats.length + image.length];
                            System.arraycopy(formats, 0, bytes, 0, formats.length);
                            System.arraycopy(image, 0, bytes, formats.length, image.length);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            mmOutStream.write(bytes, 0, bytes.length);
                        }

                        //mmSocket.close();
                    } catch (Exception e) {

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao imprimir assinatura do agente!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mens += saida;
                saida = "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");
                saida += "" + String.format("\r\n");

                mens += saida;

                try {
                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {

                        IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                        bp.printText(saida);

                    } else {
                        byte[] bytes = saida.getBytes();
                        mmOutStream.write(bytes, 0, bytes.length);
                    }
                } catch (Exception e) {

                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao conectar na impressora!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                    avisoerro.show();
                    return;
                }

                saida = "" + String.format("\r\n");
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
                mens += saida;

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();

                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        ImprimeAit.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("TEC");
                aviso.setMessage("Falha ao gerar impressão!");
                aviso.setNeutralButton("OK", null);
                aviso.show();
                progress.dismiss();
            }
            try {

                if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                    IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                    WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                    bp.printText(saida);

                } else {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    byte[] bytes = saida.getBytes();
                    mmOutStream.write(bytes, 0, bytes.length);

                    mmOutStream.close();
                }
                progress.dismiss();
                aviso.show();
                Ait ait = new Ait();
                ait.setImpressao(mens);
                ait.setId(idAit);
                aitdao = new AitDAO(ImprimeAit.this);
                aitdao.gravaImpressao(ait);
                aitdao.close();

            } catch (Exception e) {
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        ImprimeAit.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("TEC");
                aviso.setMessage("Falha ao imprimir!");
                aviso.setNeutralButton("OK", null);
                aviso.show();
                progress.dismiss();
                avisoerro.show();
                return;
            }
        } else {
            try {

                impressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("impressoraMAC")));
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

                LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
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

                EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
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
                saida += String.format("\r\n");
                saida += String.format("\r\n");

                mens = saida;
                try {

                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                        IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                        bp.printText(saida);

                    } else {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        byte[] bytes = saida.getBytes();
                        mmOutStream.write(bytes, 0, bytes.length);

                        mmOutStream.close();
                    }
                    progress.dismiss();
                    aviso.show();
                    Ait ait = new Ait();
                    ait.setImpressao(mens);
                    ait.setId(idAit);
                    aitdao = new AitDAO(ImprimeAit.this);
                    aitdao.gravaImpressao(ait);
                    aitdao.close();

                } catch (Exception e) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao imprimir!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                    avisoerro.show();
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }

    IBluetoothPrint.Describe des = null;
    IBluetoothPrint imgbp = null;
    Uri uri = null;

    public class ThreadConexaoSkyPix extends Thread {
        private String mens;
        private String desclog;
        private String ctiplog;

        public ThreadConexaoSkyPix() {
            String filename = orgaoAutuador + ".jpg";
            String path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/" + filename;
            File f = new File(path);
            uri = Uri.fromFile(f);
        }

        public void run() {
            boolean isConntected = WPXMain.connectDevice(impressora);
            if (isConntected) {
                imgbp = WPXMain.getBluetoothPrint();
                byte gravity = 1;
                des = new IBluetoothPrint.Describe();
                des.setGravity(gravity);
            } else {
                isConntected = WPXMain.connectDevice(impressora);
                if (isConntected) {
                    imgbp = WPXMain.getBluetoothPrint();
                    byte gravity = 1;
                    des = new IBluetoothPrint.Describe();
                    des.setGravity(gravity);
                }
            }

            try {
                AitDAO a = new AitDAO(ImprimeAit.this);
                List<Ait> ls = a.getListaAitPrint((String) getIntent()
                        .getSerializableExtra("agente"));

                for (Ait b : ls) {
                    long ait = (Long.parseLong(b.getAit()));
                    String aitC = b.getAit().toString();// .substring(4).toString();
                    long Idait = (b.getId());
                    long aitini = Long.parseLong(ImprimeAit.this.spIni);
                    long aitfin = Long.parseLong(ImprimeAit.this.spFinalSel);// spFinalSel);

                    if (aitini <= ait) {
                        if (aitfin >= ait) {
                            bmp = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);

                            Bitmap bmpMonochrome = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(bmp, 0, 0, paint);

                            int width = bmpMonochrome.getWidth();
                            int height = bmpMonochrome.getHeight();

                            int[] pixels = new int[width * height];
                            bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    int pixel = bmpMonochrome.getPixel(x, y);
                                    int lowestBit = pixel & 0xff;
                                    if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                                    else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                }
                            }
                            bmp = bmpMonochrome;

                            imgbp.printBitmap(bmp, des);

                            ObtemAssinaturaImpressao(Idait);
                            ObtemAssinaturaInfrator(Idait);
                            montaimpressao(Idait);

                            IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                            WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                            //bp.printText(mens);

                            try {
                                Thread.sleep(10000);
                                Ait ait1 = new Ait();
                                ait1.setImpressao(mens);
                                ait1.setId(Idait);
                                AitDAO aitdao = new AitDAO(ImprimeAit.this);
                                aitdao.gravaImpressao(ait1);
                                aitdao.close();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
                progress.dismiss();
                aviso.show();

            } catch (Exception e) {
                progress.dismiss();
                avisoerro.show();
                return;
            }

        }

        private void montaimpressao(long idAit) {

            // String impressora ="00:08:1B:95:6B:AF";

            AitDAO aitdao = new AitDAO(ImprimeAit.this);
            Cursor c = aitdao.getAit(idAit);

            // grava data e hora do envio para a impressora
            aitdao.atualizaImpressao(idAit, c);
            aitdao.close();

            ParametroDAO pardao = new ParametroDAO(ImprimeAit.this);
            Cursor cpar = pardao.getParametros();
            pardao.close();

            try {
                cancelou = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("cancelou")));

                progress = ProgressDialog.show(ImprimeAit.this, "Aguarde...",
                        "Enviando dados para a Impressora!!!", true, true);
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            if (cancelou.contains("NAO")) {
                try {
                    impressora = SimpleCrypto.decrypt(info,
                            cpar.getString(cpar.getColumnIndex("impressoraMAC")));
                    ativo = SimpleCrypto.decrypt(info,
                            cpar.getString(cpar
                                    .getColumnIndex("impressoraPatrimonio")))
                            .toUpperCase();
                    // Obtem , Logradouro ,Especie, Tipo

                    EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
                    especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("especie"))));
                    espdao.close();

                    TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
                    tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("tipo"))));
                    tipdao.close();

                    MedidaAdmDAO medidadao = new MedidaAdmDAO(ImprimeAit.this);
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
                    LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
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
                        saida += "Uf do Veiculo:"
                                + SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("UfVeiculo")))
                                + String.format("\r\n");
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
                            ImprimeAit.this);
                    Cursor c1 = aitenq.getLista1(idAit);

                    enquads = " ";
                    c1.moveToNext();

                    // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                    EnquadramentoDAO dao = new EnquadramentoDAO(ImprimeAit.this);
                    List<Enquadramento> enquadramento = dao.getLista(
                            SimpleCrypto.decrypt(info,
                                    c1.getString(c1.getColumnIndex("codigo"))),
                            ImprimeAit.this, "");
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


                    saida += "f\\i";
                    try {
                        if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                            IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                            WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                            bp.printText(saida.replace("f\\i", ""));
                        } else {
                            byte[] buffer = saida.replace("f\\i", "").getBytes();
                            mmOutStream.write(buffer, 0, buffer.length);
                        }


                    } catch (Exception e) {

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao conectar na impressora!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }

                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (bmp_AssinaturaInfrator != null) {
                            Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_AssinaturaInfrator.getWidth(), bmp_AssinaturaInfrator.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(bmp_AssinaturaInfrator, 0, 0, paint);

                            int width = bmpMonochrome.getWidth();
                            int height = bmpMonochrome.getHeight();

                            int[] pixels = new int[width * height];
                            bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    int pixel = bmpMonochrome.getPixel(x, y);
                                    int lowestBit = pixel & 0xff;
                                    if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                                    else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                }
                            }
                            IBluetoothPrint imgbp = WPXMain.getBluetoothPrint();
                            byte gravity = 1;
                            IBluetoothPrint.Describe des = new IBluetoothPrint.Describe();
                            des.setGravity(gravity);
                            imgbp.printBitmap(bmpMonochrome, des);
                        }
                    } else {
                        try {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (bmp_AssinaturaInfrator != null) {
                                Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_AssinaturaInfrator.getWidth(), bmp_AssinaturaInfrator.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bmpMonochrome);
                                ColorMatrix ma = new ColorMatrix();
                                ma.setSaturation(0);
                                Paint paint = new Paint();
                                paint.setColorFilter(new ColorMatrixColorFilter(ma));
                                canvas.drawBitmap(bmp_AssinaturaInfrator, 0, 0, paint);

                                int width = bmpMonochrome.getWidth();
                                int height = bmpMonochrome.getHeight();

                                int[] pixels = new int[width * height];
                                bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                                for (int y = 0; y < height; y++) {
                                    for (int x = 0; x < width; x++) {
                                        int pixel = bmpMonochrome.getPixel(x, y);
                                        int lowestBit = pixel & 0xff;
                                        if (lowestBit < 128)
                                            bmpMonochrome.setPixel(x, y, Color.BLACK);
                                        else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                    }
                                }

                                byte[] formats = ByteArrayCodePrintImage(bmpMonochrome);
                                byte[] image = bitmapToByteArray(bmpMonochrome);
                                byte[] bytes = new byte[formats.length + image.length];
                                System.arraycopy(formats, 0, bytes, 0, formats.length);
                                System.arraycopy(image, 0, bytes, formats.length, image.length);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                mmOutStream.write(bytes, 0, bytes.length);
                            }

                            //mmSocket.close();
                        } catch (Exception e) {

                            AlertDialog.Builder aviso = new AlertDialog.Builder(
                                    ImprimeAit.this);
                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                            aviso.setTitle("TEC");
                            aviso.setMessage("Falha ao imprimir assinatura do infrator!");
                            aviso.setNeutralButton("OK", null);
                            aviso.show();
                            progress.dismiss();
                            avisoerro.show();
                            return;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mens = saida;

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

                    saida += "f\\f";

                    try {
                        if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                            IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                            WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                            bp.printText(saida.replace("f\\f", ""));
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            byte[] buffer = saida.replace("f\\f", "").getBytes();
                            mmOutStream.write(buffer, 0, buffer.length);
                        }
                    } catch (Exception e) {

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao conectar na impressora!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    saida += "" + String.format("\r\n");
                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {

                        if (bmp_Assinatura != null) {
                            Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_Assinatura.getWidth(), bmp_Assinatura.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bmpMonochrome);
                            ColorMatrix ma = new ColorMatrix();
                            ma.setSaturation(0);
                            Paint paint = new Paint();
                            paint.setColorFilter(new ColorMatrixColorFilter(ma));
                            canvas.drawBitmap(bmp_Assinatura, 0, 0, paint);

                            int width = bmpMonochrome.getWidth();
                            int height = bmpMonochrome.getHeight();

                            int[] pixels = new int[width * height];
                            bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                            for (int y = 0; y < height; y++) {
                                for (int x = 0; x < width; x++) {
                                    int pixel = bmpMonochrome.getPixel(x, y);
                                    int lowestBit = pixel & 0xff;
                                    if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                                    else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                }
                            }
                            IBluetoothPrint imgbp = WPXMain.getBluetoothPrint();
                            byte gravity = 1;
                            IBluetoothPrint.Describe des = new IBluetoothPrint.Describe();
                            des.setGravity(gravity);
                            imgbp.printBitmap(bmpMonochrome, des);
                        }
                    } else {
                        try {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (bmp_Assinatura != null) {
                                Bitmap bmpMonochrome = Bitmap.createBitmap(bmp_Assinatura.getWidth(), bmp_Assinatura.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bmpMonochrome);
                                ColorMatrix ma = new ColorMatrix();
                                ma.setSaturation(0);
                                Paint paint = new Paint();
                                paint.setColorFilter(new ColorMatrixColorFilter(ma));
                                canvas.drawBitmap(bmp_Assinatura, 0, 0, paint);

                                int width = bmpMonochrome.getWidth();
                                int height = bmpMonochrome.getHeight();

                                int[] pixels = new int[width * height];
                                bmpMonochrome.getPixels(pixels, 0, width, 0, 0, width, height);

                                for (int y = 0; y < height; y++) {
                                    for (int x = 0; x < width; x++) {
                                        int pixel = bmpMonochrome.getPixel(x, y);
                                        int lowestBit = pixel & 0xff;
                                        if (lowestBit < 128)
                                            bmpMonochrome.setPixel(x, y, Color.BLACK);
                                        else bmpMonochrome.setPixel(x, y, Color.WHITE);
                                    }
                                }

                                byte[] formats = ByteArrayCodePrintImage(bmpMonochrome);
                                byte[] image = bitmapToByteArray(bmpMonochrome);
                                byte[] bytes = new byte[formats.length + image.length];
                                System.arraycopy(formats, 0, bytes, 0, formats.length);
                                System.arraycopy(image, 0, bytes, formats.length, image.length);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                mmOutStream.write(bytes, 0, bytes.length);
                            }

                            //mmSocket.close();
                        } catch (Exception e) {

                            AlertDialog.Builder aviso = new AlertDialog.Builder(
                                    ImprimeAit.this);
                            aviso.setIcon(android.R.drawable.ic_dialog_alert);
                            aviso.setTitle("TEC");
                            aviso.setMessage("Falha ao imprimir assinatura do agente!");
                            aviso.setNeutralButton("OK", null);
                            aviso.show();
                            progress.dismiss();
                            avisoerro.show();
                            return;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mens += saida;
                    saida = "________________________" + String.format("\r\n");
                    saida += "     Lavrado por" + String.format("\r\n");
                    saida += "" + String.format("\r\n");

                    mens += saida;

                    try {
                        if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {

                            IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                            WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                            bp.printText(saida);

                        } else {
                            byte[] bytes = saida.getBytes();
                            mmOutStream.write(bytes, 0, bytes.length);
                        }
                    } catch (Exception e) {

                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao conectar na impressora!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }

                    saida = "" + String.format("\r\n");
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
                    mens += saida;

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();

                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao gerar impressão!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                }
                try {

                    if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                        IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                        WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                        bp.printText(saida);

                    } else {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        byte[] bytes = saida.getBytes();
                        mmOutStream.write(bytes, 0, bytes.length);

                        mmOutStream.close();
                    }

                } catch (Exception e) {
                    AlertDialog.Builder aviso = new AlertDialog.Builder(
                            ImprimeAit.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Falha ao imprimir!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                    progress.dismiss();
                    avisoerro.show();
                    return;
                }
            } else {
                try {

                    impressora = SimpleCrypto.decrypt(info,
                            cpar.getString(cpar.getColumnIndex("impressoraMAC")));
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

                    LogradouroDAO logdao = new LogradouroDAO(ImprimeAit.this);
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

                    EspecieDAO espdao = new EspecieDAO(ImprimeAit.this);
                    especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("especie"))));
                    espdao.close();

                    TipoDAO tipdao = new TipoDAO(ImprimeAit.this);
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
                    saida += String.format("\r\n");
                    saida += String.format("\r\n");

                    mens = saida;
                    try {

                        if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
                            IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                            WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                            bp.printText(saida);

                        } else {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            byte[] bytes = saida.getBytes();
                            mmOutStream.write(bytes, 0, bytes.length);

                            mmOutStream.close();
                        }

                    } catch (Exception e) {
                        AlertDialog.Builder aviso = new AlertDialog.Builder(
                                ImprimeAit.this);
                        aviso.setIcon(android.R.drawable.ic_dialog_alert);
                        aviso.setTitle("TEC");
                        aviso.setMessage("Falha ao imprimir!");
                        aviso.setNeutralButton("OK", null);
                        aviso.show();
                        progress.dismiss();
                        avisoerro.show();
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        }

    }
}
