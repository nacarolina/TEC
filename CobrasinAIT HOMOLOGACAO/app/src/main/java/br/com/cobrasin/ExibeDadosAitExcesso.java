package br.com.cobrasin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.MunicipioDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
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
import android.content.Intent;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wpx.IBluetoothPrint;
import com.wpx.WPXMain;
import com.wpx.util.GeneralAttributes;
import com.wpx.util.WPXUtils;

import static br.com.cobrasin.Utilitarios.ByteArrayCodePrintImage;
import static br.com.cobrasin.Utilitarios.bitmapToByteArray;

public class ExibeDadosAitExcesso extends Activity {    /*
 *
 *
 * 04.12.2012 Obtem o Ativo da Impressora e durante a montagem da impressao
 * verifica: P25 -> impressao especifica para imp.bamboo
 */
    private String info = Utilitarios.getInfo();

    private ProgressDialog progress;

    private Toast aviso, avisoerro;

    private String cancelou;

    private String desclog;
    private String ctiplog;
    private String enquads;
    private String mens;
    private String especie;
    private String tipo;
    private String medidaadm;
    protected Handler handler;
    private String orgaoAutuador;
    private String dtEdit;
    private String hrEdit;

    private String tipoinfrator;

    Ait aitPendente;

    // Mostra String
    private String exibe[] = new String[43];

    private static final String TAG = "CobrasinAitBt";

    private ThreadConexao tconx;

    private long idAit;

    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int INVISIBLE = 4;

    private String saida, impressora, ativo, transmitido = "";
    boolean ModoBlitz = false;
    // private ThreadConexao tconx;

    @Override
    public boolean onCreateOptionsMenu(Menu me) {
        // me.add("Editar Data e Hora");
        me.add("Medidas Administrativas");
        me.add("Retorna");
        // me.add("Status do AIT");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mt) {
        // if(mt.getTitle()=="Editar Data e Hora")
        // {
        // AitDAO aitdao = new AitDAO(ExibeDadosAit.this);
        // Cursor c = aitdao.getAit(idAit);
        // aitdao.close();
        // Intent i = null ;

        // i = new Intent(this, EditDataHora.class);
        // try {
        // dtEdit = SimpleCrypto.decrypt(info,
        // c.getString(c.getColumnIndex("dtEdit")));
        // i.putExtra("dtEdit",dtEdit);
        // } catch (Exception e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // try {
        // hrEdit = SimpleCrypto.decrypt(info,
        // c.getString(c.getColumnIndex("hrEdit")));
        // i.putExtra("hrEdit",hrEdit);
        // } catch (Exception e1) {
        // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // i.putExtra("idAit", idAit);
        // startActivity(i);

        // finish();
        // }
        if (mt.getTitle() == "Medidas Administrativas") {
            Intent i = null;
            // Ait aitx = new Ait();
            // aitx.setId(aitPendente.getId());
            AitDAO aitdao = new AitDAO(ExibeDadosAitExcesso.this);
            Cursor c = aitdao.getAit(idAit);
            aitdao.close();
            String transmitiu = "";
            try {
                transmitiu = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("transmitido")));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (transmitiu.contains("NAO")) {
                String id;

                i = new Intent(this, ListaMedidaAdm.class);
                MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAitExcesso.this);
                try {
                    medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(
                            info, c.getString(c.getColumnIndex("medidaadm"))));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                medidaadmdao.close();

                id = medidaadmdao.ObtemId(medidaadm);
                i.putExtra("selmedidaadm", id);
                i.putExtra("idAit", idAit);
                startActivity(i);

                setResult(RESULT_OK);
                finish();
            } else {
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        ExibeDadosAitExcesso.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("TEC");
                aviso.setMessage("A Medida Administrativa não pode ser modificada depois de ter transmitido o AIT!");
                aviso.setNeutralButton("OK", null);
                aviso.show();

            }
        }
        if (mt.getTitle() == "Retorna") {

            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(mt);
    }

    private String agente = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(getMainLooper());
        String serieAit = "";

        setContentView(R.layout.exibeait_excesso);
        Button btCancela = (Button) findViewById(R.id.btCancelaAit_excesso);
        Button btnNotaFiscal = (Button) findViewById(R.id.btnExibeNF_excesso);
        btnNotaFiscal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(ExibeDadosAitExcesso.this, ListaNf_ExibeAit_Excesso.class);
                i.putExtra("idAit", idAit);
                startActivity(i);
            }
        });

        // pega o Id do AIT
        idAit = (Long) getIntent().getSerializableExtra("idAit");
        agente = (String) getIntent().getSerializableExtra("agente");
        try {
            ModoBlitz = (boolean) getIntent().getSerializableExtra("ModoBlitz");
        } catch (Exception e) {

        }

        AitDAO aitdao = new AitDAO(ExibeDadosAitExcesso.this);
        Cursor c = aitdao.getAit(idAit);
        aitdao.close();
        // Obtem , Logradouro ,Especie, Tipo

        try {
            agente = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("agente")));
            LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitExcesso.this);
            if (SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("logradouro2"))).contains(
                    "NAO")) {
                desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro"))));
            } else {
                desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro")))) + " X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("logradouro2"))));
            }

            // desclog += " " +
            // SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradouronum")));

            logdao.close();

            EspecieDAO espdao = new EspecieDAO(ExibeDadosAitExcesso.this);
            especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("especie"))));
            espdao.close();

            TipoDAO tipdao = new TipoDAO(ExibeDadosAitExcesso.this);
            tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("tipo"))));
            tipdao.close();

            MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAitExcesso.this);
            medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("medidaadm"))));
            medidaadmdao.close();

            ParametroDAO pardao = new ParametroDAO(ExibeDadosAitExcesso.this);
            Cursor ch = pardao.getParametros();
            ch.moveToFirst();
            serieAit = SimpleCrypto.decrypt(info,
                    ch.getString(ch.getColumnIndex("serieait")));
            ch.close();
            pardao.close();

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // pega tipo do logradouro
        ctiplog = "NAO DEFINIDO";

        try {
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

            // Mostra String
            // String exibe [] = new String[24];

            exibe[0] = "AIT:"
                    + serieAit
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("ait")));
            exibe[1] = "AGENTE:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("agente")));
            exibe[2] = "FLAG:" + c.getString(c.getColumnIndex("flag"));
            exibe[3] = "PLACA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("placa")));
            exibe[4] = "DATA-HORA LAVRATURA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("data")))
                    + "-"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("hora")));
            exibe[5] = "ENCERROU:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("encerrou")));
            exibe[6] = "MARCA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("marca")));
            exibe[7] = "ESPECIE:" + especie; // c.getString(8);
            exibe[8] = "CONDUTOR FOI ABORDADO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("condutorAbordado")));
            exibe[9] = "VIA ENTREGUE AO CONDUTOR:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("viaEntregue")));
            exibe[10] = "TIPO:" + tipo;// c.getString(9);
            exibe[11] = "LOGRADOURO:" + desclog;// c.getString(10);
            exibe[12] = "NUMERO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("logradouronum")));
            exibe[13] = "TIPO:" + ctiplog;// c.getString(12);

            try {
                tipoinfrator = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipoinfrator")));
            } catch (Exception e1) {
                tipoinfrator = "";
            }

            // if (tipoinfrator == null) {
            // exibe[12] = "NOME:" + SimpleCrypto.decrypt(info,
            // c.getString(c.getColumnIndex("nome")));
            // exibe[13] = "PGU:" +SimpleCrypto.decrypt(info,
            // c.getString(c.getColumnIndex("pgu")));
            // exibe[14] = "UF:" + SimpleCrypto.decrypt(info,
            // c.getString(c.getColumnIndex("uf")));
            // }

            exibe[14] = "NOME:";
            exibe[15] = "PGU:";
            exibe[16] = "DOCUMENTO DE INDENTIFICAÇÃO:";
            exibe[17] = "PPD:--";

            if (tipoinfrator == null) {
                exibe[14] = "NOME:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("nome")));
                exibe[15] = "PGU:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("pgu")));
                exibe[16] = "UF:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("uf")));

                String PPD = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ppd_condutor")));
                if (PPD.equals("S")) {
                    exibe[17] = "PPD:SIM";
                } else if (PPD.equals("N")) {
                    exibe[17] = "PPD:NÃO";
                } else {
                    exibe[17] = "PPD:--";
                }
            } else if (tipoinfrator.contains("CNH")) {
                exibe[14] = "NOME:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("nome")));
                exibe[15] = "PGU:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("pgu")));
                exibe[16] = "UF:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("uf")));

                String PPD = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("ppd_condutor")));
                if (PPD.equals("S")) {
                    exibe[17] = "PPD:SIM";
                } else if (PPD.equals("N")) {
                    exibe[17] = "PPD:NÃO";
                } else {
                    exibe[17] = "PPD:--";
                }

            } else if (tipoinfrator.contains("PID")) {
                exibe[14] = "NOME:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("nome")));
                exibe[15] = "PID:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("pid")));
                exibe[16] = "DOCUMENTO DE INDENTIFICAÇÃO:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("passaporte")));
                exibe[17] = "PPD:--";
            }

            exibe[18] = "OBS:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("observacoes")));
            exibe[19] = "IMPRIMIU:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("impresso")));
            exibe[20] = "SERIEPDA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("seriepda")));

            try {
                transmitido = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("transmitido")));
                exibe[21] = "TRANSMITIDO:" + transmitido;
            } catch (Exception e1) {

                exibe[21] = "TRANSMITIDO:";
            }

            // grava cancelamento
            try {
                cancelou = SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("cancelou")));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            exibe[22] = "CANCELOU:" + cancelou;

            if (!cancelou.equals("NAO")) {
                btCancela.setVisibility(4);
            }

            exibe[23] = "MOTIVO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("motivo")));
            exibe[24] = "MEDIDA ADM:" + medidaadm;

            AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(
                    ExibeDadosAitExcesso.this);
            Cursor c1 = aitenq.getLista1(idAit);

            enquads = " ";
            while (c1.moveToNext()) {
                // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(this);
                List<Enquadramento> enquadramento = null;
                try {
                    enquadramento = dao.getLista(
                            SimpleCrypto.decrypt(info,
                                    c1.getString(c1.getColumnIndex("codigo"))),
                            ExibeDadosAitExcesso.this, "");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dao.close();

                enquads += enquadramento.get(0).toString();// + " / ";

            }

            c1.close();

            exibe[25] = "ENQUADRAMENTOS:" + enquads;

            exibe[26] = "EQUIPAMENTO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("equipamento")));
            exibe[27] = "MEDICAO REGISTRADA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("medicaoreg")));
            exibe[28] = "MEDICAO CONSIDERADA:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("medicaocon")));
            exibe[29] = "LIMITE REGULAMENTADO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("limitereg")));

        } catch (Exception e) {
            String Erro = e.getMessage();
        }

        // String DtEdit;
        // DtEdit = aitdao.ObtemDataModificada(Long.toString(idAit));
        // exibe[27] = "DATA MODIFICADA:" + DtEdit;

        try {
            exibe[30] = "DATA DO COMETIMENTO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("dtEdit")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exibe[30] = "DATA DO COMETIMENTO:";
        }
        try {
            exibe[31] = "HORA DO COMETIMENTO:"
                    + SimpleCrypto.decrypt(info,
                    c.getString(c.getColumnIndex("hrEdit")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exibe[31] = "HORA DO COMETIMENTO:";
        }

        NotaFiscalDAO NfDAO = new NotaFiscalDAO(ExibeDadosAitExcesso.this);
        List<br.com.cobrasin.tabela.NotaFiscal> NfLista = NfDAO.GetNotasAit(idAit);

        if (NfLista.size() == 1) {
            try {
                String Nome = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome_embarcador")));
                String CPFCNPJ = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpfCnpj_embarcador")));
                String Endereco = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("endereco_embarcador")));
                String Bairro = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("bairro_embarcador")));
                String IdMunicipio = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("IdMunicipio_embarcador")));

                MunicipioDAO MuDAO = new MunicipioDAO(ExibeDadosAitExcesso.this);
                List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
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


                exibe[32] = "NOME DO EMBARCADOR:" + Nome;
                exibe[33] = "CPF/CNPJ DO EMBARCADOR:" + CPFCNPJ;
                exibe[34] = "ENDEREÇO DO EMBARCADOR:" + Endereco;
                exibe[35] = "CIDADE/UF DO EMBARCADOR: " + Cidade + " - " + UF;
                exibe[36] = "BAIRRO DO EMBARCADOR:" + Bairro;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                exibe[32] = "NOME DO EMBARCADOR:";
                exibe[33] = "CPF/CNPJ DO EMBARCADOR:";
                exibe[34] = "ENDEREÇO DO EMBARCADOR:";
                exibe[35] = "CIDADE/UF DO EMBARCADOR:";
                exibe[36] = "BAIRRO DO EMBARCADOR:";
            }
        } else if (NfLista.size() > 1) {

            try {

                String Nome = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome_transportador")));
                String CPFCNPJ = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpfCnpj_transportador")));
                String Endereco = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("endereco_transportador")));
                String Bairro = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("bairro_transportador")));
                String IdMunicipio = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("IdMunicipio_transportador")));

                MunicipioDAO MuDAO = new MunicipioDAO(ExibeDadosAitExcesso.this);
                List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
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


                exibe[32] = "NOME DO TRANSPORTADOR:" + Nome;
                exibe[33] = "CPF/CNPJ DO TRANSPORTADOR:" + CPFCNPJ;
                exibe[34] = "ENDEREÇO DO TRANSPORTADOR:" + Endereco;
                exibe[35] = "CIDADE/UF DO TRANSPORTADOR: " + Cidade + " - " + UF;
                exibe[36] = "BAIRRO DO TRANSPORTADOR:" + Bairro;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                exibe[32] = "NOME DO TRANSPORTADOR:";
                exibe[33] = "CPF/CNPJ DO TRANSPORTADOR:";
                exibe[34] = "ENDEREÇO DO TRANSPORTADOR:";
                exibe[35] = "CIDADE/UF DO TRANSPORTADOR:";
                exibe[36] = "BAIRRO DO TRANSPORTADOR:";
            }

        } else {
            exibe[32] = "NOME DO TRANSPORTADOR:";
            exibe[33] = "CPF/CNPJ DO TRANSPORTADOR:";
            exibe[34] = "ENDEREÇO DO TRANSPORTADOR:";
            exibe[35] = "CIDADE/UF DO TRANSPORTADOR:";
            exibe[36] = "BAIRRO DO TRANSPORTADOR:";
        }
        NfDAO.close();


        try {
            int Limite = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("limitePermitido_excesso"))));
            String LimiteString = NumberFormat.getNumberInstance().format(Limite);
            exibe[37] = "Limite de Peso: " + LimiteString + " Kg";
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            exibe[37] = "Limite de Peso: ";
            //e1.printStackTrace();
        }

        try {
            int Tara = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tara_excesso"))));
            String TaraString = NumberFormat.getNumberInstance().format(Tara);
            exibe[38] = "Tara: " + TaraString + " Kg";
        } catch (Exception e) {
            // TODO: handle exception
            exibe[38] = "Tara: ";
        }

        try {
            int PesoDeclarado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pesoDeclarado_excesso"))));
            String PesoDeclaradoString = NumberFormat.getNumberInstance().format(PesoDeclarado);
            exibe[39] = "Peso Declarado: " + PesoDeclaradoString + " Kg";

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            exibe[39] = "Peso Declarado: ";

            //e1.printStackTrace();
        }

        try {
            int ExcessoConstatado = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("excessoConstatado_excesso"))));
            String ExcessoConstatadoString = NumberFormat.getNumberInstance().format(ExcessoConstatado);
            exibe[40] = "Excesso Constatado: " + ExcessoConstatadoString + " Kg";
        } catch (Exception e) {
            // TODO: handle exception
            exibe[40] = "Excesso Constatado: ";
        }
        try {
            exibe[41] = "Posto: " + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("Posto_Agente")));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            exibe[41] = "Posto: ";
        }
        try {

            String IdMunicipio = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("IdMunicipio_Agente")));
            MunicipioDAO MuDAO = new MunicipioDAO(ExibeDadosAitExcesso.this);
            List<Municipio> Lista_Municipio = MuDAO.GetCidade(IdMunicipio);
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
            exibe[42] = "CIDADE/UF DO TRANSPORTADOR: " + Cidade + " - " + UF;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            exibe[42] = "CIDADE/UF DO TRANSPORTADOR: ";
        }


        ListView exibeait = (ListView) findViewById(R.id.listExibeAit_excesso);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, exibe);
        exibeait.setAdapter(adapter1);

        c.close();

        btCancela.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (transmitido.equals("NAO")) {
                    // ait já foi cancelado ?
                    AitDAO aitda = new AitDAO(ExibeDadosAitExcesso.this);
                    Cursor cu = aitda.getAit(idAit);
                    String Cancelar = "";
                    try {
                        Cancelar = SimpleCrypto.decrypt(info,
                                cu.getString(cu.getColumnIndex("cancelou")));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (Cancelar.contains("NAO"))
                        chama(1);
                    else
                        Toast.makeText(getBaseContext(), "AIT já cancelado !",
                                Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(), "Usuário não autorizado!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btImprime = (Button) findViewById(R.id.btImprime_excesso);

        btImprime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                montaimpressao(idAit);

            }

        });

        Button btVisualizarImpressao = (Button) findViewById(R.id.btVisualizarImpressaoExcesso);
        btVisualizarImpressao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getBaseContext(), VisualizarImpressao.class);
                i.putExtra("idAit", idAit);
                i.putExtra("origem", "excesso");
                startActivity(i);

                finish();
            }

        });
        /*
         * // FOI tirada alguma foto ? FotoDAO fotodao = new
         * FotoDAO(getBaseContext()); Cursor cx = fotodao.getImagens(idAit);
         *
         * cx.moveToFirst();
         *
         * if (cx.getCount() > 0) {
         *
         * byte[] data = cx.getBlob(cx.getColumnIndex("imagem"));
         *
         * Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length); bm =
         * Bitmap.createScaledBitmap(bm, 100,100,true); ImageButton buttonx = (
         * ImageButton) findViewById(R.id.fotoAIT); buttonx.setImageBitmap(bm);
         *
         *
         *
         * }
         *
         * cx.close(); fotodao.close();
         */

        Button btMostraFotos = (Button) findViewById(R.id.btFotos_excesso);

        btMostraFotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                FotoDAO fotodao = new FotoDAO(getBaseContext());

                if (fotodao.getQtde(idAit) > 0) {
                    fotodao.close();
                    chama(2);
                } else {
                    Toast.makeText(getBaseContext(), "Sem fotos para exibir",
                            Toast.LENGTH_SHORT).show();
                    fotodao.close();
                }

            }
        });
    }

    private void chama(int opcao) {
        Intent i = null;
        switch (opcao) {

            case 1:
                i = new Intent(this, CancelaAit.class);
                break;
            case 2:
                i = new Intent(this, MostraFotos.class);
                break;
        }

        i.putExtra("idAit", idAit);
        i.putExtra("agente", agente);

        startActivity(i);

        if (opcao == 1)
            finish();

    }

    Bitmap bmp = null;
    Bitmap bmp_Assinatura = null;

    public void ObtemAssinaturaImpressao(long idAit) {
        try {
            SQLiteDatabase Base = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);
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

    private void montaimpressao(long idAit) {

        // String impressora ="00:08:1B:95:6B:AF";

        AitDAO aitdao = new AitDAO(ExibeDadosAitExcesso.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        ParametroDAO pardao = new ParametroDAO(ExibeDadosAitExcesso.this);
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
            impressora = SimpleCrypto.decrypt(info,
                    cpar.getString(cpar.getColumnIndex("impressoraMAC")));
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

        ObtemAssinaturaImpressao(idAit);
        if (modeloImpressora.equals("SkyPix") || modeloImpressora.isEmpty()) {
            progress = ProgressDialog.show(ExibeDadosAitExcesso.this, "Aguarde...",
                    "Enviando dados para a Impressora!!!", true, true);
            String filename = orgaoAutuador + ".jpg";
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/BrasaoPrefeitura/" + filename;
            File f = new File(path);
            Uri uri = Uri.fromFile(f);

            try {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean isConntected = WPXMain.connectDevice(impressora);
                    if (isConntected) {
                        printLogoSkyPix();
                    } else {
                        isConntected = WPXMain.connectDevice(impressora);
                        if (isConntected)
                            printLogoSkyPix();
                    }
                }

                public void printLogoSkyPix() {
                    IBluetoothPrint imgbp = WPXMain.getBluetoothPrint();
                    byte gravity = 1;
                    IBluetoothPrint.Describe des = new IBluetoothPrint.Describe();
                    des.setGravity(gravity);
                    imgbp.printBitmap(bmp, des);
                    callPrintSkyPix();
                }

                public void callPrintSkyPix() {
                    IBluetoothPrint bp = WPXMain.getBluetoothPrint();
                    WPXMain.printCommand(GeneralAttributes.INSTRUCTIONS_ESC_INIT);
                    bp.printText(saida);

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

                        String msg = "________________________" + String.format("\r\n");
                        msg += "     Lavrado por" + String.format("\r\n");
                        msg += "" + String.format("\r\n");
                        msg += "" + String.format("\r\n");
                        bp.printText(msg);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    progress.dismiss();
                    AlertDialog.Builder aviso = new AlertDialog.Builder(ExibeDadosAitExcesso.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_info);
                    aviso.setTitle("TEC");
                    aviso.setMessage("Sucesso!\nImpressão terminada!");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();
                }

            }, 500);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    chamaImpressao();
                    handler.removeCallbacks(this);
                }
            }, 500);
        }

        if (cancelou.contains("NAO")) {
            try {
                impressora = SimpleCrypto.decrypt(info,
                        cpar.getString(cpar.getColumnIndex("impressoraMAC")));
                ativo = SimpleCrypto.decrypt(
                        info,
                        cpar.getString(cpar
                                .getColumnIndex("impressoraPatrimonio")))
                        .toUpperCase();
                // Obtem , Logradouro ,Especie, Tipo

                EspecieDAO espdao = new EspecieDAO(ExibeDadosAitExcesso.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(ExibeDadosAitExcesso.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();

                MedidaAdmDAO medidadao = new MedidaAdmDAO(ExibeDadosAitExcesso.this);
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
                LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitExcesso.this);
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
                MunicipioDAO MuD = new MunicipioDAO(ExibeDadosAitExcesso.this);
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
                        ExibeDadosAitExcesso.this);
                Cursor c1 = aitenq.getLista1(idAit);

                enquads = " ";
                c1.moveToNext();

                // enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(ExibeDadosAitExcesso.this);
                List<Enquadramento> enquadramento = dao.getLista(
                        SimpleCrypto.decrypt(info,
                                c1.getString(c1.getColumnIndex("codigo"))),
                        ExibeDadosAitExcesso.this, "");
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

                NotaFiscalDAO NfDAO = new NotaFiscalDAO(ExibeDadosAitExcesso.this);
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
                                ExibeDadosAitExcesso.this);
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
                                ExibeDadosAitExcesso.this);
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
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                cpar.close();

                c.close();
                mens = saida;
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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

                LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitExcesso.this);
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

                EspecieDAO espdao = new EspecieDAO(ExibeDadosAitExcesso.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(ExibeDadosAitExcesso.this);
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
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }

    private void chamaImpressao() {

        // progress = ProgressDialog.show(ExibeDadosAit.this, "Aguarde..." ,
        // "Enviando dados para a Impressora!!!",true,true);
        aviso = Toast.makeText(ExibeDadosAitExcesso.this,
                "Dados enviados com sucesso!", Toast.LENGTH_LONG);
        avisoerro = Toast.makeText(ExibeDadosAitExcesso.this,
                "Não consegui enviar dados...", Toast.LENGTH_LONG);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();

        BluetoothDevice mmDevice;

        boolean passou;
        try {
            mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
            passou = true;
            progress = ProgressDialog.show(ExibeDadosAitExcesso.this, "Aguarde...",
                    "Enviando dados para a Impressora!!!", true, true);
        } catch (Exception e) {
            AlertDialog.Builder aviso = new AlertDialog.Builder(
                    ExibeDadosAitExcesso.this);
            aviso.setIcon(android.R.drawable.ic_dialog_alert);
            aviso.setTitle("TEC");
            aviso.setMessage("Falha ao imprimir!\nImpressora não instalada!");
            aviso.setNeutralButton("OK", null);
            aviso.show();
            passou = false;
            return;
        }
        if (passou == true) {
            tconx = new ThreadConexao(mmDevice, saida);
            tconx.start();
        } else {

        }

    }

    private class ThreadConexao extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private OutputStream mmOutStream = null;
        private String mens;

        public ThreadConexao(BluetoothDevice device, String texto) {
            mens = texto;
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
            Log.i(TAG, "BEGIN mConnectThread");

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

                mmOutStream = mmSocket.getOutputStream();

                byte[] buffer = mens.getBytes();
                mmOutStream.write(buffer, 0, buffer.length);
                try {
                    Thread.sleep(3000);
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
                            if (lowestBit < 128) bmpMonochrome.setPixel(x, y, Color.BLACK);
                            else bmpMonochrome.setPixel(x, y, Color.WHITE);
                        }
                    }

                    byte[] formats = ByteArrayCodePrintImage(bmpMonochrome);
                    byte[] image = bitmapToByteArray(bmpMonochrome);
                    byte[] bytes = new byte[formats.length + image.length];
                    System.arraycopy(formats, 0, bytes, 0, formats.length);
                    System.arraycopy(image, 0, bytes, formats.length, image.length);
                    mmOutStream.write(bytes, 0, bytes.length);
                }
                mens = "________________________" + String.format("\r\n");
                mens += "     Lavrado por" + String.format("\r\n");
                mens += "" + String.format("\r\n");
                mens += "" + String.format("\r\n");

                byte[] bytes = mens.getBytes();
                mmOutStream.write(bytes, 0, bytes.length);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mmSocket.close();

                progress.dismiss();
                aviso.show();
                Ait ait = new Ait();
                ait.setImpressao(mens);
                ait.setId(idAit);
                AitDAO aitdao = new AitDAO(ExibeDadosAitExcesso.this);
                aitdao.gravaImpressao(ait);
                aitdao.close();

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

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /*
     * private class ThreadConexao extends Thread {
     *
     * private final BluetoothSocket mmSocket; private final BluetoothDevice
     * mmDevice; private OutputStream mmOutStream = null; private String mens ;
     *
     * public ThreadConexao(BluetoothDevice device,String texto ) { mens =
     * texto; mmDevice = device; BluetoothSocket tmp = null;
     *
     * // Get a BluetoothSocket for a connection with the // given
     * BluetoothDevice try { tmp =
     * device.createRfcommSocketToServiceRecord(MY_UUID);
     *
     * } catch (IOException e) {
     *
     * progress.dismiss(); avisoerro.show(); Log.e(TAG, "create() failed", e); }
     * mmSocket = tmp; }
     *
     * public void run() { Log.i(TAG, "BEGIN mConnectThread");
     *
     *
     * // Make a connection to the BluetoothSocket try { // This is a blocking
     * call and will only return on a // successful connection or an exception
     * mmSocket.connect();
     *
     * mmOutStream = mmSocket.getOutputStream();
     *
     * byte[] buffer = mens.getBytes();
     *
     * mmOutStream.write(buffer );
     *
     * mmSocket.close();
     *
     * progress.dismiss(); aviso.show();
     *
     *
     * //try { // sleep(2000); //} catch (InterruptedException e) { // // TODO
     * Auto-generated catch block // e.printStackTrace(); //}
     *
     *
     * } catch (IOException e) {
     *
     * progress.dismiss(); avisoerro.show(); // Close the socket try {
     * mmSocket.close(); } catch (IOException e2) { Log.e(TAG,
     * "unable to close() socket during connection failure", e2); } // Start the
     * service over to restart listening mode
     *
     * return; }
     *
     *
     * }
     *
     * public void cancel() { try { mmSocket.close(); } catch (IOException e) {
     * Log.e(TAG, "close() of connect socket failed", e); } } }
     */

    /*
     * private void imprime() {
     *
     * ParametroDAO pardao = new ParametroDAO(ExibeDadosAit.this); //String
     * impressora ="00:01:90:E7:E6:CE";
     *
     * AitDAO aitdao = new AitDAO(ExibeDadosAit.this); Cursor c =
     * aitdao.getAit(idAit); aitdao.close();
     *
     * Cursor cpar = pardao.getParametros(); pardao.close();
     *
     * String impressora = cpar.getString(cpar.getColumnIndex("impressora"));
     *
     * String saida = "" ;
     *
     * saida += cpar.getString(cpar.getColumnIndex("prefeitura")) +
     * String.format("\r\n"); saida += "Orgao Autuador:" +
     * cpar.getString(cpar.getColumnIndex("orgaoautuador")) +
     * String.format("\r\n");
     *
     * saida += "------------------"+ String.format("\r\n"); saida +=
     * "Dados da Infracao"+ String.format("\r\n"); saida +=
     * "------------------"+ String.format("\r\n");
     *
     * saida += "Ait:" + cpar.getString(cpar.getColumnIndex("serieait")) +
     * c.getString(c.getColumnIndex("ait"))+ String.format("\r\n"); saida +=
     * "Placa:" + c.getString(c.getColumnIndex("placa"))+ String.format("\r\n");
     * saida += "Marca:" + c.getString(c.getColumnIndex("marca"))+
     * String.format("\r\n"); saida += "Especie:" + especie +
     * String.format("\r\n"); saida += "Tipo:" + tipo + String.format("\r\n");
     *
     * saida += "-----------------"+ String.format("\r\n"); saida +=
     * "Local da Infracao "+ String.format("\r\n"); saida +=
     * "-----------------"+ String.format("\r\n");
     *
     * saida +=this.desclog+ String.format("\r\n"); saida += this.ctiplog+
     * String.format("\r\n");
     *
     * saida += "--------------------------"+ String.format("\r\n"); saida +=
     * "Data:" + c.getString(c.getColumnIndex("data")) + "-"
     * +c.getString(c.getColumnIndex("hora"))+ String.format("\r\n"); saida +=
     * "--------------------------"+ String.format("\r\n");
     *
     * saida += "Enquadramentos:" + enquads + String.format("\r\n");
     *
     * saida += "-------------------------"+ String.format("\r\n"); saida +=
     * "Identificacao do Infrator" + String.format("\r\n"); saida +=
     * "-------------------------"+ String.format("\r\n"); saida += "Nome:" +
     * c.getString(c.getColumnIndex("nome"))+ String.format("\r\n"); saida +=
     * "PGU:" + c.getString(c.getColumnIndex("pgu"))+ " / " +
     * c.getString(c.getColumnIndex("uf")) + String.format("\r\n"); saida +=
     * "Assinatura ______________"+ String.format("\r\n"); //saida += "CPF:" +
     * c.getString(c.getColumnIndex("uf"))+ String.format("\r\n");
     *
     * saida += "-----------------------"+ String.format("\r\n"); saida +=
     * "Identificacao do Agente" + String.format("\r\n"); saida +=
     * "-----------------------"+ String.format("\r\n"); saida += "Matric.(AG):"
     * + c.getString(c.getColumnIndex("agente")) + String.format("\r\n"); saida
     * += "Lavrado por _______________"+ String.format("\r\n"); saida +=
     * "-----------------------"+ String.format("\r\n");
     *
     * if (cpar.getString(cpar.getColumnIndex("imprimeobs")).contains("1")) {
     * saida +=String.format("\r\n");
     *
     * saida += "Observacoes:"+ String.format("\r\n"); try { saida +=
     * SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("observacoes"))) +
     * String.format("\r\n"); } catch (Exception e) { // TODO Auto-generated
     * catch block e.printStackTrace(); };
     *
     * }
     *
     * saida +=String.format("\r\n"); saida +=String.format("\r\n");
     *
     * saida +="E obrigatoria a presenca do"+ String.format("\r\n"); saida
     * +="codigo INFRAEST ou RENAINF nas "+ String.format("\r\n"); saida
     * +="notificacoes sob pena de " ; saida +="invalidade da multa."+
     * String.format("\r\n");
     *
     * cpar.close();
     *
     * c.close(); progress = ProgressDialog.show(ExibeDadosAit.this,
     * "Aguarde..." , "Enviado dados para a Impressora!!!",true,true); aviso =
     * Toast.makeText(ExibeDadosAit.this, "Dados enviados com sucesso!",
     * Toast.LENGTH_LONG); avisoerro = Toast.makeText(ExibeDadosAit.this,
     * "Não consegui enviar dados...", Toast.LENGTH_LONG);
     *
     *
     * BluetoothAdapter mBluetoothAdapter =
     * BluetoothAdapter.getDefaultAdapter();
     *
     * BluetoothDevice mmDevice = mBluetoothAdapter.getRemoteDevice(impressora);
     *
     * BluetoothSocket tmp = null;
     *
     * //String saida = "";
     *
     * //for ( int nx = 0 ; nx < 22 ; nx ++) //{ // saida += exibe[nx] +
     * String.format("\r\n"); //} tconx = new ThreadConexao(mmDevice,saida);
     * tconx.start();
     *
     * //mmOutStream = mmSocket.getOutputStream();
     *
     * //String texto = String.format("%s\n\r","Teste");
     *
     * //byte[] buffer = texto.getBytes();
     *
     * //mmOutStream.write(buffer );
     *
     * //mmSocket.close();
     *
     * }
     */

}
