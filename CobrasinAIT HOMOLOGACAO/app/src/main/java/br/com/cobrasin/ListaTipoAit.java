package br.com.cobrasin;


import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.NotaFiscalDAO;
import br.com.cobrasin.tabela.Ait;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ListaTipoAit extends ListActivity {

    private String salvaAgente;

    boolean ModoBlitz = false;
    private String info = Utilitarios.getInfo();

    private String logradouroGps;
    Handler handler = new Handler();;

    private String PlacaDetectada = "";
    private String MarcaModeloDetectada = "";

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent i = new Intent();
            i = new Intent(this, ListaAit.class);
            i.putExtra("ModoBlitz", ModoBlitz);
            i.putExtra("agente", salvaAgente);
            startActivity(i);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String selecao = (String) getIntent().getSerializableExtra("seltipo");

        salvaAgente = (String) getIntent().getSerializableExtra("agente");

        ModoBlitz = (boolean) getIntent().getSerializableExtra("ModoBlitz");

        logradouroGps = (String) getIntent().getSerializableExtra("logradouroGps");

        PlacaDetectada = (String) getIntent().getSerializableExtra("PlacaDetectada");
        MarcaModeloDetectada = (String) getIntent().getSerializableExtra("MarcaModeloDetectada");

        String opcoes[] = new String[]{"Modo Blitz",
                "Veículo Placa Nacional", "Veículo Placa Estrangeira", "Pessoa Física", "Pessoa Jurídica", "Excesso de Carga", "Retorna"};

        /*String IMEI = "";
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
                                                            ListaTipoAit.this);
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
                                            return;
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
                                        ListaTipoAit.this);
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
                                return;
                            }
                        });
                    }
                }
            }
        }*/

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, opcoes));

        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (ModoBlitz)
            listView.setItemChecked(0, true);
        //******************************************
        //	26.01.2012
        // existe ait Aberta ? entao cancela
        //
        //******************************************

        AitDAO aitdao = new AitDAO(getBaseContext());
        Cursor ch = null;
        try {
            ch = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        aitdao.close();

        Intent i;
        if (ch.getCount() > 0) {

            i = new Intent(this, CancelaAit.class);
            i.putExtra("idAit", ch.getLong(ch.getColumnIndex("id")));
            startActivity(i);

            ch.close();

            finish();
        } else {
            ch.close();
        }

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent i = null;

        switch (position) {

            case 0:            //
                if (ModoBlitz)
                    ModoBlitz = false;
                else
                    ModoBlitz = true;

                break;
            case 1:            // veiculo
                i = new Intent(this, PreencheAit.class);
                i.putExtra("PlacaDetectada", PlacaDetectada);
                i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
                i.putExtra("logradouroGps", logradouroGps);
                i.putExtra("ModoBlitz", ModoBlitz);
                break;

            case 2:            // veiculo placa estrangeira
                i = new Intent(this, PreencheAitplest.class);
                i.putExtra("PlacaDetectada", PlacaDetectada);
                i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
                i.putExtra("logradouroGps", logradouroGps);
                i.putExtra("ModoBlitz", ModoBlitz);
                break;

            case 3:            // pessoa fisica
                i = new Intent(this, PreencheAitpfpj.class);
                i.putExtra("PlacaDetectada", PlacaDetectada);
                i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
                i.putExtra("logradouroGps", logradouroGps);
                i.putExtra("ModoBlitz", ModoBlitz);
                i.putExtra("tipoait", "2");
                break;

            case 4:            // pessoa juridica

                i = new Intent(this, PreencheAitpfpj.class);
                i.putExtra("PlacaDetectada", PlacaDetectada);
                i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
                i.putExtra("logradouroGps", logradouroGps);
                i.putExtra("tipoait", "3");
                i.putExtra("ModoBlitz", ModoBlitz);
                break;
            case 5:            // excesso de carga
                i = new Intent(this, PreencheAitExcesso.class);
                i.putExtra("PlacaDetectada", PlacaDetectada);
                i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
                i.putExtra("logradouroGps", logradouroGps);
                i.putExtra("ModoBlitz", ModoBlitz);
                NotaFiscalDAO NfDAO = new NotaFiscalDAO(ListaTipoAit.this);
                NfDAO.ApagaNovaNota();
                break;

            case 6:            // volta pra lista de aits
                i = new Intent(this, ListaAit.class);
                i.putExtra("ModoBlitz", ModoBlitz);
                break;
        }

        if (position != 0) {
            i.putExtra("agente", salvaAgente);
            startActivity(i);
            finish();
        }

        //startActivity(i);


    }
}
