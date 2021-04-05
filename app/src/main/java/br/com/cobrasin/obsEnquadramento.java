package br.com.cobrasin;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Enquadramento;

public class obsEnquadramento extends Activity {

    private String OrgA, Pda, agente, idMunicipio;
    private ListView listaObs;
    private List<Enquadramento> lstEnquadramento;
    private AdapterList_ObsEnquadramento adapter;
    private CheckBox chkSemObrigatoriedade;
    EditText edEnquadramento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obs_enquadramento);

        agente = (String) getIntent().getSerializableExtra("agente");
        idMunicipio = (String) getIntent().getSerializableExtra("IdMunicipio");

        chkSemObrigatoriedade = (CheckBox) findViewById(R.id.chkSemObrigatoriedade_obsEnquadramento);
        listaObs = (ListView) findViewById(R.id.listaObsEnquadramento);
        edEnquadramento = (EditText) findViewById(R.id.txtEnquadramento_ObsEnquadramento);

        carregaLista();
        chkSemObrigatoriedade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carregaLista();
                trataPesquisa();
            }
        });

        Button btPesquisa = (Button) findViewById(R.id.btnPesquisa_ObsEnquadramento);

        btPesquisa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                trataPesquisa();
            }


        });
    }

    public void carregaLista() {

        EnquadramentoDAO enquadramentoDAO = new EnquadramentoDAO(obsEnquadramento.this);
        if (chkSemObrigatoriedade.isChecked()) {
            lstEnquadramento = enquadramentoDAO.getEnquadramentosByObs("0");
        } else {
            lstEnquadramento = enquadramentoDAO.getEnquadramentosByObs("1");
        }

        enquadramentoDAO.close();

        adapter = new AdapterList_ObsEnquadramento(this,
                R.layout.lst_obs_enquadramento, lstEnquadramento, chkSemObrigatoriedade.isChecked());
        listaObs.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            final ParametroDAO paDao = new ParametroDAO(getBaseContext());
            Cursor c = paDao.getParametros();
            String SincObsObrigatorio = c.getString(c.getColumnIndex("SincObsObrigatorio"));
            if (SincObsObrigatorio!= null && SincObsObrigatorio.equals("") == false) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            String enquadramentos = "";

                            SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/enquadramento", null, 0);
                            try {
                                //Cursor c = getWritableDatabase().query(TABELA, COLS, null, null, null, null, COLS[0]);

                                Cursor c = null;
                                String sqlpesq = "Select Codigo from enquadramento  where  EnquadramentoObsObrigatorio ='1'";
                                c = s.rawQuery(sqlpesq, null);

                                while (c.moveToNext()) {
                                    if (c.isLast()) {
                                        enquadramentos += c.getString(0);
                                    } else {
                                        enquadramentos += c.getString(0) + ",";
                                    }
                                }
                                c.close();

                            } catch (SQLiteException e) {
                                Log.e("Erro=", e.getMessage());
                            }

                            String retornoweb = "";
                            String ret = "erro";
                            LogDAO l = new LogDAO(getBaseContext());
                            String url = "http://sistemas.cobrasin.com.br/JsonWcf/JsonWcfService.svc/InsertEnquadramentosObs";// urlswebtrans.geturl("pdf");//.replace("6742",
                            // "9090");

                            // urlswebtrans.close();
                            Cryptor cr = new Cryptor();

                            try {
                                HttpClient client = new DefaultHttpClient();
                                HttpPost post = new HttpPost(url);

                                JSONStringer json = new JSONStringer();
                                json.object();
                                json.key("p");
                                json.object();
                                json.key("IdMunicipio").value(cr.encrypt(idMunicipio));
                                json.key("Enquadramentos").value(cr.encrypt(enquadramentos));
                                // /json.endObject();
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
                                        ret = "ok";
                                        l.gravalog("Obs obrigatoria ", "Upload", OrgA,
                                                Pda, agente, getBaseContext());

                                        paDao.SetSincObsObrigatorio("");
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
                                    ret = "Transmissao obs obrigatoria erro - " + statusCode;
                                    try {
                                        l.gravalog("Transmissao obs obrigatoria erro - "
                                                        + statusCode, "Erro", OrgA, Pda,
                                                agente, getBaseContext());

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            c.close();
            paDao.close();

        }
        return super.onKeyDown(keyCode, event);
    }

    private void trataPesquisa() {
        // TODO Auto-generated method stub

        if (edEnquadramento.getText().equals(null) == false && edEnquadramento.getText().equals("") == false) {
            EnquadramentoDAO dao = new EnquadramentoDAO(this);

            lstEnquadramento = dao.getLista(edEnquadramento.getText().toString(), getBaseContext(), String.valueOf(chkSemObrigatoriedade.isChecked()));

            dao.close();

            if (lstEnquadramento.size() == 0) {
                Toast.makeText(getBaseContext(), "Nenhum enquadramento localizado !", Toast.LENGTH_SHORT).show();
            }

            adapter = new AdapterList_ObsEnquadramento(this,
                    R.layout.lst_obs_enquadramento, lstEnquadramento, chkSemObrigatoriedade.isChecked());
            listaObs.setAdapter(adapter);
        }
    }

}
