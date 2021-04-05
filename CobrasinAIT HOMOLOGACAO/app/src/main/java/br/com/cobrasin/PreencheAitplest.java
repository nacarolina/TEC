package br.com.cobrasin;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.BkpMultaPdfDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Ait;
import br.com.cobrasin.tabela.AitEnquadramento;
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.ListaEspecie;

import android.app.Activity;
import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class PreencheAitplest extends Activity {

    String salvaAgente = "";
    Ait aitPendente;
    TrataPlacaEst edPlaca;
    TrataMarca edMarca;
    EditText edData;
    EditText edHora;
    CheckBox chkDuplicar;
    LogDAO l = new LogDAO(PreencheAitplest.this);


    private int chamaactivity = 101;
    private int chamafoto = 102;
    private boolean passou = false;
    boolean AitDuplicado = false;

    private String logradouroGps;

    private String info = Utilitarios.getInfo();

    private String arquivofoto;
    private String OrgA;
    private String Pda;
    private String agente;
    private String PlacaDetectada = "";

    private String MarcaModeloDetectada = "";
    /**
     * Called when the activity is first created.
     */

    Button btPesquisa, btEspecie, btTipo, btLogradouro, btEnquadramento, btObservacoes, btDadosInfrator, btVisualiza,
            btPais, btCancelaAit, btInfrEquip, btMedidaAdm, btFotografa, btFecha, btAssinatura;
    private String desclog;
    private ProgressDialog progress;

    RadioButton rdoViaEntregueSim, rdoViaEntregueNao, rdoCondutorAbordadoSim, rdoCondutorAbordadoNao;
    boolean ModoBlitz=false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (btPesquisa.isEnabled() == true) {

            Intent i = new Intent();
            i = new Intent(this, ListaAit.class);
            i.putExtra("ModoBlitz", ModoBlitz);
            i.putExtra("agente", salvaAgente);
            startActivity(i);
            finish();
        } else {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Cancelamento de AIT");
                aviso.setMessage(" Deseja realmente ir para a tela de Cancelamento ?");
                aviso.setNeutralButton("Não", null);
                aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        chamaTelaCan();
                    }
                });

                aviso.show();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void chama(int n) {

        // grava a marca na tabela
        Ait aitx = new Ait();
        aitx.setId(aitPendente.getId());

        //*********************************************
        // 27.06.2012 - alteração : remover acentos
        //*********************************************
        aitx.setMarca(Utilitarios.removeAcentos(edMarca.getText().toString()));
        if(rdoViaEntregueSim.isChecked())
            aitx.setViaEntregue("SIM");
        if(rdoViaEntregueNao.isChecked())
            aitx.setViaEntregue("NÃO");
        if(rdoCondutorAbordadoSim.isChecked())
            aitx.setCondutorAbordado("SIM");
        if (rdoCondutorAbordadoNao.isChecked())
            aitx.setCondutorAbordado("NÃO");


        if (edMarca.getText().length() > 0) {
            AitDAO aitdao = new AitDAO(getBaseContext());
            aitdao.gravaMarca(aitx);
            aitdao.gravaViaEntregue(aitx);
            aitdao.gravaCondutorAbordado(aitx);
            aitdao.close();
        }

        Intent i = null;
        switch (n) {

            case 1: {
                this.btEspecie.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaEspecie.class);
                i.putExtra("selespecie", aitPendente.getEspecie());
                break;
            }

            case 2: {
                this.btTipo.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaTipo.class);
                i.putExtra("seltipo", aitPendente.getTipo());
                break;
            }

            case 3: {
                this.btLogradouro.setTypeface(Typeface.DEFAULT_BOLD);
                AitDAO aitdao2 = new AitDAO(PreencheAitplest.this);
                Cursor c = aitdao2.getAit(aitPendente.getId());
                try {
                    if (SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("logradouro2"))).contains(
                            "NAO")) {
                        if (SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouronum")))
                                .length() == 0) {
                            logradouroGps = (String) getIntent()
                                    .getSerializableExtra("logradouroGps");
                            i = new Intent(this, ListaLogradouro.class);
                            i.putExtra("codLogradouro", SimpleCrypto.decrypt(info,
                                    c.getString(c.getColumnIndex("logradouro"))));
                            i.putExtra("numLogradouro", SimpleCrypto.decrypt(info,
                                    c.getString(c.getColumnIndex("logradouronum"))));
                            i.putExtra("tipLogradouro", SimpleCrypto
                                    .decrypt(info, c.getString(c
                                            .getColumnIndex("logradourotipo"))));
                            i.putExtra("logradouroGps", logradouroGps);
                            i.putExtra("ModoBlitz", ModoBlitz);
                            // i.putExtra("salvou", "N");
                        } else {

                            i = new Intent(this, ListaLogradouro1.class);
                            i.putExtra("codLogradouro", SimpleCrypto.decrypt(info,
                                    c.getString(c.getColumnIndex("logradouro"))));
                            i.putExtra("numLogradouro", SimpleCrypto.decrypt(info,
                                    c.getString(c.getColumnIndex("logradouronum"))));
                            i.putExtra("tipLogradouro", SimpleCrypto
                                    .decrypt(info, c.getString(c
                                            .getColumnIndex("logradourotipo"))));
                            i.putExtra("logradouroGps", logradouroGps);
                            i.putExtra("salvou", "S");
                            i.putExtra("ModoBlitz", ModoBlitz);
                        }
                    } else {
                        i = new Intent(PreencheAitplest.this, ListaLogradouro3.class);
                        i.putExtra("codLogradouro", SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouro"))));
                        i.putExtra("numLogradouro", SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouronum"))));
                        i.putExtra("tipLogradouro", SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradourotipo"))));
                        i.putExtra("codLogradouro2", SimpleCrypto.decrypt(info,
                                c.getString(c.getColumnIndex("logradouro2"))));
                        i.putExtra("ModoBlitz", ModoBlitz);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case 4: {
                this.btEnquadramento.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaEnquadramento.class);
                i.putExtra("ModoBlitz", ModoBlitz);
                i.putExtra("agente", salvaAgente);
                if (lstEnquadramentosUsados != null)
                    i.putExtra("enquadramentosUsados", lstEnquadramentosUsados.toArray());
                else
                    i.putExtra("enquadramentosUsados", "");
                break;
            }
            case 5: {
                this.btObservacoes.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaObservacoes.class);
                i.putExtra("obsgravada", aitPendente.getObservacoes());
                break;
            }
            case 6: {
                AitDAO aitdao = new AitDAO(PreencheAitplest.this);
                Cursor c = aitdao.getAit(aitPendente.getId());
                this.btDadosInfrator.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, OrigemInfrator.class);
                try {
                    i.putExtra("nome", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome"))));
                    i.putExtra("cpf", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpf"))));
                    i.putExtra("pgu", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pgu"))));
                    i.putExtra("uf", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("uf"))));
                    i.putExtra("passaporte", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("passaporte"))));
                    i.putExtra("pid", SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pid"))));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case 7: {
                i = new Intent(this, ExibeDadosAitAntesFechamentoPlest.class);
                break;
            }

            case 8: {
                i = new Intent(this, ListaMedidaAdm.class);
                this.btMedidaAdm.setTypeface(Typeface.DEFAULT_BOLD);
                i.putExtra("selmedidaadm", aitPendente.getMedidaadm());
                break;
            }
            case 9: {
                this.btPais.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaPais.class);
                i.putExtra("selpais", aitPendente.getPais());
                break;
            }
            case 10: {
                this.btInfrEquip.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, ListaInfrEquipamento.class);
                i.putExtra("equipamento", aitPendente.getEquipamento());
                i.putExtra("medicaoreg", aitPendente.getMedicaoreg());
                i.putExtra("medicaocon", aitPendente.getMedicaocon());
                i.putExtra("limitereg", aitPendente.getLimitereg());
                break;
            }
            case 11: {
                this.btAssinatura.setTypeface(Typeface.DEFAULT_BOLD);
                i = new Intent(this, AssinaturaDigitalActivity.class);
                i.putExtra("idAit", aitPendente.getId());
                break;
            }
        }
        i.putExtra("idAit", aitPendente.getId());
        startActivityForResult(i, chamaactivity);

    }

    public void Assinatura(View view) {
        chama(11);
        passou = true;
    }

    private void criaAit(String placa) {

        Ait ait = new Ait();

        try {
            //Locale locale = new Locale("pt","BR");

            ait.setAit(SimpleCrypto.encrypt(info, "XXXX"));
            ait.setFlag("A");
            ait.setAgente(SimpleCrypto.encrypt(info, salvaAgente));
            ait.setPlaca(SimpleCrypto.encrypt(info, placa));
            ait.setLogradouro2(SimpleCrypto.encrypt(info, "NAO"));
            ait.setData(SimpleCrypto.encrypt(info, Utilitarios.getDataHora(2)));
            ait.setHora(SimpleCrypto.encrypt(info, Utilitarios.getDataHora(3)));

            //ait.setData(SimpleCrypto.encrypt(info,new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()))));
            //ait.setHora(SimpleCrypto.encrypt(info,new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()))));

            ait.sethrEdit(edHora.getText().toString());
            ait.setdtEdit(edData.getText().toString());

            ait.setMarca(SimpleCrypto.encrypt(info, edMarca.getText().toString()));
            ait.setEspecie(SimpleCrypto.encrypt(info, "01"));
            ait.setTipo(SimpleCrypto.encrypt(info, "06"));
            ait.setLogradouro(SimpleCrypto.encrypt(info, "00000"));
            ait.setLogradouronum(SimpleCrypto.encrypt(info, ""));
            ait.setLogradourotipo(SimpleCrypto.encrypt(info, "0"));
            ait.setNome(SimpleCrypto.encrypt(info, ""));
            ait.setCpf(SimpleCrypto.encrypt(info, ""));
            ait.setPgu(SimpleCrypto.encrypt(info, ""));
            ait.setUf(SimpleCrypto.encrypt(info, ""));
            ait.setObservacoes(SimpleCrypto.encrypt(info, ""));
            ait.setMedidaadm(SimpleCrypto.encrypt(info, "1"));
            ait.setTipoait(SimpleCrypto.encrypt(info, "4"));

            ait.setPais(SimpleCrypto.encrypt(info, ""));

            ait.setEquipamento(SimpleCrypto.encrypt(info, ""));
            ait.setMedicaoreg(SimpleCrypto.encrypt(info, ""));
            ait.setMedicaocon(SimpleCrypto.encrypt(info, ""));
            ait.setLimitereg(SimpleCrypto.encrypt(info, ""));
            ait.setSendPdf(SimpleCrypto.encrypt(info, "NAO"));
            ait.setIdWebTrans((long) 0);
            ParametroDAO pardao = new ParametroDAO(getBaseContext());

            Cursor c = pardao.getParametros();

            c.moveToFirst();

            // dados para comunicação com o WebTrans
            String usuarioWebTrans = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("usuariowebtrans")));
            String senhaWebTrans = c.getString(c.getColumnIndex("senhawebtrans"));
            String codMunicipio = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("orgaoautuador"))).subSequence(1, 5).toString();  //265810
            OrgA = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("orgaoautuador")));
            Pda = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("seriepda")));
            agente = salvaAgente;
            c.close();

            pardao.close();

            ///Grava o log da criação do ait
            try {
                l.gravalog("Inicio de criação de AIT placa " + placa, "INSERT",
                        OrgA, Pda, salvaAgente, PreencheAitplest.this);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                l.gravalog("Erro criação de AIT- " + e.getMessage().replace(".", "-").replace(":", "-"), "Erro",
                        OrgA, Pda, salvaAgente, PreencheAitplest.this);
            } catch (Exception ex) {

            }
        }


        try {
            ait.setImpresso(SimpleCrypto.encrypt(info, "NAO"));
            ait.setTransmitido(SimpleCrypto.encrypt(info, "NAO"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // obs: cancelou e motivo setados no fechamento

        AitDAO dao = new AitDAO(this);
        dao.alteraInsere(ait, 2);
        dao.close();
    }


    private void editaAit(Cursor cursor) {
        // TODO Auto-generated method stub

        aitPendente = new Ait();

        try {
            aitPendente.setId(cursor.getLong(cursor.getColumnIndex("id")));
            aitPendente.setAit(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("ait"))));
            aitPendente.setFlag(cursor.getString(cursor.getColumnIndex("flag")));
            aitPendente.setAgente(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("agente"))));
            aitPendente.setPlaca(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("placa"))));
            aitPendente.setData(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("data"))));
            aitPendente.setHora(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("hora"))));
            aitPendente.setMarca(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("marca"))));
            aitPendente.setEspecie(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("especie"))));
            aitPendente.setTipo(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("tipo"))));
            aitPendente.setLogradouro(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("logradouro"))));
            aitPendente.setLogradouronum(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("logradouronum"))));
            aitPendente.setLogradourotipo(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("logradourotipo"))));
            aitPendente.setNome(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("nome"))));
            aitPendente.setCpf(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("cpf"))));
            aitPendente.setPgu(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("pgu"))));
            aitPendente.setUf(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("uf"))));
            aitPendente.setObservacoes(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("observacoes"))));
            aitPendente.setImpresso(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("impresso"))));
            aitPendente.setTransmitido(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("transmitido"))));
            aitPendente.setMedidaadm(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("medidaadm"))));
            aitPendente.setTipoait(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("tipoait"))));
            aitPendente.setPais(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("pais"))));

            aitPendente.setdtEdit(edData.getText().toString());
            aitPendente.sethrEdit(edHora.getText().toString());

            aitPendente.setEquipamento(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("equipamento"))));
            aitPendente.setMedicaoreg(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("medicaoreg"))));
            aitPendente.setMedicaocon(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("medicaocon"))));
            aitPendente.setLimitereg(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("limitereg"))));

            if (cursor.getString(cursor.getColumnIndex("logradouro2")) != null)
                aitPendente.setLogradouro2(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("logradouro2"))));
            if (cursor.getString(cursor.getColumnIndex("seriepda")) != null)
                aitPendente.setSeriepda(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("seriepda"))));
            if (cursor.getString(cursor.getColumnIndex("encerrou")) != null)
                aitPendente.setEncerrou(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("encerrou"))));
            if (cursor.getString(cursor.getColumnIndex("cancelou")) != null)
                aitPendente.setCancelou(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("cancelou"))));
            if (cursor.getString(cursor.getColumnIndex("motivo")) != null)
                aitPendente.setMotivo(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("motivo"))));
            aitPendente.setdtEdit(edData.getText().toString());
            aitPendente.sethrEdit(edHora.getText().toString());

            aitPendente.setEquipamento(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("equipamento"))));
            aitPendente.setMedicaoreg(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("medicaoreg"))));
            aitPendente.setMedicaocon(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("medicaocon"))));
            aitPendente.setLimitereg(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("limitereg"))));

            if (cursor.getString(cursor.getColumnIndex("tipoinfrator")) != null)
                aitPendente.setTipoinfrator(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("tipoinfrator"))));
            if (cursor.getString(cursor.getColumnIndex("pid")) != null)
                aitPendente.setPid(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("pid"))));
            if (cursor.getString(cursor.getColumnIndex("passaporte")) != null)
                aitPendente.setPassaporte(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("passaporte"))));
            if (cursor.getString(cursor.getColumnIndex("ppd_condutor")) != null)
                aitPendente.setPpd_condutor(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("ppd_condutor"))));

            if (cursor.getString(cursor.getColumnIndex("UfVeiculo")) != null)
                aitPendente.setUfVeiculo(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("UfVeiculo"))));

            if (cursor.getString(cursor.getColumnIndex("viaEntregue")) != null)
                aitPendente.setViaEntregue(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("viaEntregue"))));
            if (cursor.getString(cursor.getColumnIndex("condutorAbordado")) != null)
                aitPendente.setCondutorAbordado(SimpleCrypto.decrypt(info, cursor.getString(cursor.getColumnIndex("condutorAbordado"))));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    List<String> lstEnquadramentosUsados = new ArrayList<>();

    private void GerarOutroAitMesmaPlaca() {
        String nome = "", cpf = "", pid = "", ufVeiculo = "", logradouro = "", logradouro2 = "", logradouronum = "", logradourotipo = "", pgu = "", uf = "", tipoinfrator = "", observacoes = "", passaporte = "",
                nomeEmbarcador = "", cpfCnpj_embarcador = "", endereco_embarcador = "", IdMunicipio_embarcador = "", bairro_embarcador = "", nome_transportador = "", cpfCnpj_transportador = "",
                endereco_transportador = "", IdMunicipio_transportador = "", bairro_transportador = "", limitePermitido_excesso = "", pesoDeclarado_excesso = "", excessoConstatado_excesso = "",
                tara_excesso = "", ppd_condutor = "", Posto_Agente = "", IdMunicipio_Agente = "", marca = "", cancelou = "", transmitido = "", motivo = "", especie = "", Ait = "", tipoAit = "", tipo = "",
                medicaocon = "", medicaoreg = "", pais = "", limitereg = "", equipamento = "", impresso = "", medidaadm = "", viaEntregue="", condutorAbordado="";
        try {
            AitDAO aitdao = new AitDAO(getBaseContext());
            Cursor c = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));

            //region finaliza AIT anterior
            Ait aitx = new Ait();
            aitx.setId(aitPendente.getId());
            aitx.setMarca(Utilitarios.removeAcentos(edMarca.getText()
                    .toString().trim()));

            if(rdoViaEntregueSim.isChecked())
                aitx.setViaEntregue("SIM");
            if(rdoViaEntregueNao.isChecked())
                aitx.setViaEntregue("NÃO");
            if(rdoCondutorAbordadoSim.isChecked())
                aitx.setCondutorAbordado("SIM");
            if (rdoCondutorAbordadoNao.isChecked())
                aitx.setCondutorAbordado("NÃO");

            aitx.setdtEdit(edData.getText().toString());
            aitx.sethrEdit(edHora.getText().toString());
            aitPendente.setMarca(Utilitarios.removeAcentos(edMarca
                    .getText().toString().trim()));

            aitdao.gravaDtEdit(aitx);
            aitdao.gravaHrEdit(aitx);
            aitdao.gravaMarca(aitx);
            aitdao.gravaCondutorAbordado(aitx);
            aitdao.gravaViaEntregue(aitx);
            aitdao.close();
            boolean sucesso = fechaAit(false);
            if (sucesso == false) {
                progress.dismiss();
                return;
            }
            //endregion

            editaAit(c);

            //region pega os dados do ait anterior

            marca = aitPendente.getMarca();
            cancelou = aitPendente.getCancelou();
            transmitido = aitPendente.getTransmitido();
            motivo = aitPendente.getMotivo();
            condutorAbordado=aitPendente.getCondutorAbordado();
            viaEntregue=aitPendente.getViaEntregue();
            medidaadm = aitPendente.getMedidaadm();
            especie = aitPendente.getEspecie();
            Ait = aitPendente.getAit();
            impresso = aitPendente.getImpresso();
            medicaocon = aitPendente.getMedicaocon();
            medicaoreg = aitPendente.getMedicaoreg();
            equipamento = aitPendente.getEquipamento();
            limitereg = aitPendente.getLimitereg();
            tipoAit = aitPendente.getTipoait();
            pais = aitPendente.getPais();
            tipo = aitPendente.getTipo();
            nome = aitPendente.getNome();
            cpf = aitPendente.getCpf();
            pid = aitPendente.getPid();
            ufVeiculo = aitPendente.getUfVeiculo();
            logradouro = aitPendente.getLogradouro();
            logradouro2 = aitPendente.getLogradouro2();
            logradouronum = aitPendente.getLogradouronum();
            logradourotipo = aitPendente.getLogradourotipo();
            pgu = aitPendente.getPgu();
            uf = aitPendente.getUf();
            tipoinfrator = aitPendente.getTipoinfrator();
            observacoes = aitPendente.getObservacoes();
            passaporte = aitPendente.getPassaporte();
            nomeEmbarcador = aitPendente.getNome_embarcador();
            cpfCnpj_embarcador = aitPendente.getCpfCnpj_embarcador();
            endereco_embarcador = aitPendente.getEndereco_embarcador();
            IdMunicipio_embarcador = aitPendente.getIdMunicipio_embarcador();
            bairro_embarcador = aitPendente.getBairro_embarcador();
            nome_transportador = aitPendente.getNome_transportador();
            cpfCnpj_transportador = aitPendente.getCpfCnpj_transportador();
            endereco_transportador = aitPendente.getEndereco_transportador();
            IdMunicipio_transportador = aitPendente.getIdMunicipio_transportador();
            bairro_transportador = aitPendente.getBairro_transportador();
            limitePermitido_excesso = aitPendente.getLimitePermitido_excesso();
            pesoDeclarado_excesso = aitPendente.getPesoDeclarado_excesso();
            excessoConstatado_excesso = aitPendente.getExcessoConstatado_excesso();
            tara_excesso = aitPendente.getTara_excesso();
            ppd_condutor = aitPendente.getPpd_condutor();
            Posto_Agente = aitPendente.getPosto_Agente();
            IdMunicipio_Agente = aitPendente.getIdMunicipio_Agente();
            //endregion

            //cria novo ait pra mesma placa
            confirmadaCriacaoAit();

            //region joga os dados do ait anterior pro atual
            Ait a = new Ait();
            a.setId(aitPendente.getId());
            a.setTipo(SimpleCrypto.encrypt(info, tipo));
            a.setTipoait(SimpleCrypto.encrypt(info, tipoAit));
            a.setPlaca(SimpleCrypto.encrypt(info, aitPendente.getPlaca()));
            a.setData(SimpleCrypto.encrypt(info, aitPendente.getData()));
            a.setHora(SimpleCrypto.encrypt(info, aitPendente.getHora()));
            a.setEspecie(SimpleCrypto.encrypt(info, especie));
            a.setdtEdit(SimpleCrypto.encrypt(info, aitPendente.getdtEdit()));
            a.sethrEdit(SimpleCrypto.encrypt(info, aitPendente.gethrEdit()));
            a.setAit(SimpleCrypto.encrypt(info, Ait));
            a.setMarca(SimpleCrypto.encrypt(info, marca));
            a.setCondutorAbordado(SimpleCrypto.encrypt(info, condutorAbordado));
            a.setViaEntregue(SimpleCrypto.encrypt(info, viaEntregue));

            a.setAgente(SimpleCrypto.encrypt(info, salvaAgente));
            //if (aitPendente.getIdWebTrans() != null)
            //    a.setIdWebTrans();
            if (transmitido != null)
                a.setTransmitido(SimpleCrypto.encrypt(info, transmitido));
            if (cancelou != null)
                a.setCancelou(SimpleCrypto.encrypt(info, cancelou));
            if (medidaadm != null)
                a.setMedidaadm(SimpleCrypto.encrypt(info, medidaadm));
            if (motivo != null)
                a.setMotivo(SimpleCrypto.encrypt(info, motivo));
            if (equipamento != null)
                a.setEquipamento(SimpleCrypto.encrypt(info, equipamento));
            if (medicaocon != null)
                a.setMedicaocon(SimpleCrypto.encrypt(info, medicaocon));
            if (medicaoreg != null)
                a.setMedicaoreg(SimpleCrypto.encrypt(info, medicaoreg));
            if (limitereg != null)
                a.setLimitereg(SimpleCrypto.encrypt(info, limitereg));
            if (impresso != null)
                a.setImpresso(SimpleCrypto.encrypt(info, impresso));
            a.setFlag("A");
            //aitPendente.setMarca((SimpleCrypto.encrypt(info, edMarca.getText().toString())));
            if (nome != null) {
                a.setNome((SimpleCrypto.encrypt(info, nome)));
                //aitPendente.setNome(nome);
            }
            if (pais != null)
                a.setPais((SimpleCrypto.encrypt(info, pais)));

            if (cpf != null) {
                a.setCpf((SimpleCrypto.encrypt(info, cpf)));
                //aitPendente.setCpf(cpf);
            }
            if (pid != null) {
                a.setPid((SimpleCrypto.encrypt(info, pid)));
                //aitPendente.setPid(pid);
            }
            if (ufVeiculo != null) {
                a.setUfVeiculo((SimpleCrypto.encrypt(info, ufVeiculo)));
                //aitPendente.setUfVeiculo(ufVeiculo);
            }
            if (logradouro != null) {
                a.setLogradouro((SimpleCrypto.encrypt(info, logradouro)));
                //aitPendente.setLogradouro(logradouro);
            }
            if (logradouro2 != null) {
                a.setLogradouro2((SimpleCrypto.encrypt(info, logradouro2)));
                //aitPendente.setLogradouro2(logradouro2);
            }
            if (logradouronum != null) {
                a.setLogradouronum((SimpleCrypto.encrypt(info, logradouronum)));
                //aitPendente.setLogradouronum(logradouronum);
            }
            if (logradourotipo != null) {
                a.setLogradourotipo((SimpleCrypto.encrypt(info, logradourotipo)));
                //aitPendente.setLogradourotipo(logradourotipo);
            }
            if (pgu != null) {
                a.setPgu((SimpleCrypto.encrypt(info, pgu)));
                //aitPendente.setPgu(pgu);
            }
            if (uf != null) {
                a.setUf((SimpleCrypto.encrypt(info, uf)));
                //aitPendente.setUf(uf);
            }
            if (tipoinfrator != null) {
                a.setTipoinfrator((SimpleCrypto.encrypt(info, tipoinfrator)));
                //aitPendente.setTipoinfrator(tipoinfrator);
            }
            if (observacoes != null) {
                a.setObservacoes((SimpleCrypto.encrypt(info, observacoes)));
                //aitPendente.setObservacoes(observacoes);
            }
            if (passaporte != null) {
                a.setPassaporte((SimpleCrypto.encrypt(info, passaporte)));
                //aitPendente.setPassaporte(passaporte);
            }
            if (nomeEmbarcador != null) {
                a.setNome_embarcador((SimpleCrypto.encrypt(info, nomeEmbarcador)));
                //aitPendente.setNome_embarcador(nomeEmbarcador);
            }
            if (cpfCnpj_embarcador != null) {
                a.setCpfCnpj_embarcador(SimpleCrypto.encrypt(info, (cpfCnpj_embarcador)));
                //aitPendente.setCpfCnpj_embarcador(cpfCnpj_embarcador);
            }
            if (endereco_embarcador != null) {
                a.setEndereco_embarcador((SimpleCrypto.encrypt(info, endereco_embarcador)));
                //aitPendente.setEndereco_embarcador(endereco_embarcador);
            }
            if (IdMunicipio_embarcador != null) {
                a.setIdMunicipio_embarcador(SimpleCrypto.encrypt(info, (IdMunicipio_embarcador)));
                //aitPendente.setIdMunicipio_embarcador(IdMunicipio_embarcador);
            }
            if (bairro_embarcador != null) {
                a.setBairro_embarcador((SimpleCrypto.encrypt(info, bairro_embarcador)));
                //aitPendente.setBairro_embarcador(bairro_embarcador);
            }
            if (nome_transportador != null) {
                a.setNome_transportador((SimpleCrypto.encrypt(info, nome_transportador)));
                //aitPendente.setNome_transportador(nome_transportador);
            }
            if (cpfCnpj_transportador != null) {
                a.setCpfCnpj_transportador((SimpleCrypto.encrypt(info, cpfCnpj_transportador)));
                //aitPendente.setCpfCnpj_transportador(cpfCnpj_transportador);
            }
            if (endereco_transportador != null) {
                a.setEndereco_transportador(SimpleCrypto.encrypt(info, (endereco_transportador)));
                //aitPendente.setEndereco_transportador(endereco_transportador);
            }
            if (IdMunicipio_transportador != null) {
                a.setIdMunicipio_transportador(SimpleCrypto.encrypt(info, (IdMunicipio_transportador)));
                //aitPendente.setIdMunicipio_transportador(IdMunicipio_transportador);
            }
            if (bairro_transportador != null) {
                a.setBairro_transportador(SimpleCrypto.encrypt(info, (bairro_transportador)));
                //aitPendente.setBairro_transportador(bairro_transportador);
            }
            if (limitePermitido_excesso != null) {
                a.setLimitePermitido_excesso((SimpleCrypto.encrypt(info, limitePermitido_excesso)));
                //aitPendente.setLimitePermitido_excesso(limitePermitido_excesso);
            }
            if (pesoDeclarado_excesso != null) {
                a.setPesoDeclarado_excesso((SimpleCrypto.encrypt(info, pesoDeclarado_excesso)));
                //aitPendente.setPesoDeclarado_excesso(pesoDeclarado_excesso);
            }
            if (excessoConstatado_excesso != null) {
                a.setExcessoConstatado_excesso((SimpleCrypto.encrypt(info, excessoConstatado_excesso)));
                //aitPendente.setExcessoConstatado_excesso(excessoConstatado_excesso);
            }
            if (tara_excesso != null) {
                a.setTara_excesso((SimpleCrypto.encrypt(info, tara_excesso)));
                //aitPendente.setTara_excesso(tara_excesso);
            }
            if (ppd_condutor != null) {
                a.setPpd_condutor(SimpleCrypto.encrypt(info, (ppd_condutor)));
                //aitPendente.setPpd_condutor(ppd_condutor);
            }
            if (Posto_Agente != null) {
                a.setPosto_Agente((SimpleCrypto.encrypt(info, Posto_Agente)));
                //aitPendente.setPosto_Agente(Posto_Agente);
            }
            if (IdMunicipio_Agente != null) {
                a.setIdMunicipio_Agente((SimpleCrypto.encrypt(info, IdMunicipio_Agente)));
                //aitPendente.setIdMunicipio_Agente(IdMunicipio_Agente);
            }
            //endregion

            aitdao.alteraInsere(a, 1);

            Cursor cAitDuplicado = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
            editaAit(cAitDuplicado);
            progress.dismiss();
            chkDuplicar.setChecked(false);
        } catch (Exception e) {
            progress.dismiss();
        }
    }

    private void confirmaFechamentoAit() {
        if (chkDuplicar.isChecked()) {

            AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
            aviso.setIcon(android.R.drawable.ic_dialog_alert);
            aviso.setTitle("Finalizar e gerar outra infração para este veículo");
            aviso.setMessage("Confirma ?");
            aviso.setNeutralButton("Não", null);
            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    AitDuplicado = true;
                    edMarca.setFocusable(true);
                    progress = ProgressDialog.show(PreencheAitplest.this,
                            "Aguarde...", "Gerando nova infração para a mesma placa",
                            true, false);
                    progress.show();
                    GerarOutroAitMesmaPlaca();
                }
            });
            aviso.show();
        } else {
            AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
            aviso.setIcon(android.R.drawable.ic_dialog_alert);
            aviso.setTitle("Fechamento de AIT");
            aviso.setMessage("Confirma ?");
            aviso.setNeutralButton("Não", null);
            aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // TODO Auto-generated method stub

                    // grava a marca na tabela
                    Ait aitx = new Ait();
                    aitx.setId(aitPendente.getId());

                    //*********************************************
                    // 27.06.2012 - alteração : remover acentos
                    //*********************************************
                    aitx.setMarca(Utilitarios.removeAcentos(edMarca.getText().toString().trim()));
                    aitPendente.setMarca(Utilitarios.removeAcentos(edMarca.getText().toString().trim()));

                    if(rdoViaEntregueSim.isChecked())
                        aitx.setViaEntregue("SIM");
                    if(rdoViaEntregueNao.isChecked())
                        aitx.setViaEntregue("NÃO");
                    if(rdoCondutorAbordadoSim.isChecked())
                        aitx.setCondutorAbordado("SIM");
                    if (rdoCondutorAbordadoNao.isChecked())
                        aitx.setCondutorAbordado("NÃO");

                    AitDAO aitdao = new AitDAO(getBaseContext());
                    aitdao.gravaMarca(aitx);
                    aitdao.gravaViaEntregue(aitx);
                    aitdao.gravaCondutorAbordado(aitx);
                    aitdao.close();
                    AitDuplicado = false;
                    lstEnquadramentosUsados.clear();
                    boolean sucesso = fechaAit(true);

                }
            });
            aviso.show();
        }
    }

    private String processafechamento(String encerramento) {
        //
        // muda o status do AIT
        // grava o campo AIT
        ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
        Cursor cz = pardao.getParametros();

        Parametro param = new Parametro();
        try {
            param.setProximoait(SimpleCrypto.decrypt(info, cz.getString(0)));
            param.setSeriepda(SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("seriepda"))));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Atencao: Todos os dados já foram criptografados...

        Ait aitz = new Ait();
        aitz.setId(aitPendente.getId());        // id
        aitz.setFlag("F");                        // flag
        aitz.setAit(param.getProximoait());        // numero do ait
        aitz.setEncerrou(encerramento);
        aitz.setSeriepda(param.getSeriepda());
        aitz.setCancelou("NAO");
        aitz.setAgente(salvaAgente); //aitPendente.getAgente());
        aitz.setMotivo(" ");
        aitz.setdtEdit(edData.getText().toString());
        aitz.sethrEdit(edHora.getText().toString());

        AitDAO aitdao = new AitDAO(PreencheAitplest.this);
        aitdao.fechaAitDAO(aitz);
        aitdao.gravaDtEdit(aitz);
        aitdao.gravaHrEdit(aitz);
        aitdao.close();

        //*************************
        //Criptografa os dados
        //*************************
        //SimpleCrypto scri = new SimpleCrypto();
        //scri.criptAit(aitPendente.getId(),PreencheAit.this);

        // atualiza proxait do parametro

        long prox = Long.parseLong(param.getProximoait());
        String aitClosed = String.valueOf(prox);
        prox++;

        String formatado = String.format("%07d", prox);
        param.setProximoait(formatado);
        try {
            param.setSeriepda(SimpleCrypto.decrypt(info, cz.getString(cz.getColumnIndex("seriepda"))));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        pardao.gravaParam(param);
        pardao.close();

        cz.close();
        return aitClosed;
    }

    private String VerificaDtHr(String dt, String hr) {
        String ret = "ok";
        String Data = dt.toString();
        String Hora = hr.toString();
        //	int Retorno;
        if (Data.contains(".")) {
            Data = Data.replace('.', '/');
        }
        if (Data.contains("-")) {
            Data = Data.replace('-', '/');
        }


        Utilitarios u = new Utilitarios();
        //AitDAO aitdao = new AitDAO(EditDataHora.this);
        //Cursor c = aitdao.getAit(idAit);
        if (u.calculaDias(dt + " " + hr) >= 1)
            ret = "false";
        else
            ret = "true";


        return ret;
    }

    // verifica as condicoes para o fechamento do AIT
    private boolean fechaAit(boolean fechaTela) {
        // condicoes :
        // marca lenght > 0
        // enquadramento >= 1
        // local <> "00000"
        // numero do logradouro não preenchido para <> cruzamento

        boolean errofecha = false;
        boolean erromarca = false;
        boolean erroenquad = false;
        boolean erronumlog = false;
        boolean errolog = false;
        boolean erropais = false;
        boolean errodt = false;
        String mensagem = "Erros:\n\n";

        if (VerificaDtHr(edData.getText().toString(), edHora.getText().toString()) == "false") {
            errodt = true;
            mensagem += "Data-hora com diferença maior de 24hrs não é permitido!\n";
        }

        String DataAtualString = Utilitarios.getDataHora(2).replace("/", "");
        String HoraAtualString = Utilitarios.getDataHora(3).replace(":", "");

        String DataEditadaString = edData.getText().toString().replace("/", "");
        String HoraEditadaString = edHora.getText().toString().replace(":", "");

        int DataAtual = Integer.parseInt(DataAtualString);
        int HoraAtual = Integer.parseInt(HoraAtualString);

        int DataEditada = Integer.parseInt(DataEditadaString);
        int HoraEditada = Integer.parseInt(HoraEditadaString);

        if (DataEditada > DataAtual || HoraEditada > HoraAtual) {
            errodt = true;
            mensagem += "A Data-hora editada não pode ser maior que a Data-hora atual!\n";
        }

        //---------------------------------------------------------
        // testa marca
        //---------------------------------------------------------
        String marca = aitPendente.getMarca();
        if (marca.length() == 0) {
            erromarca = true;
            mensagem += "Marca não preenchida\n";
        }


        if (rdoCondutorAbordadoNao.isChecked() == false && rdoCondutorAbordadoSim.isChecked() == false) {
            erromarca = true;
            mensagem += "Preencha se o Condutor foi abordado\n";
        }
        if (rdoViaEntregueNao.isChecked() == false && rdoViaEntregueSim.isChecked() == false) {
            errodt = true;
            mensagem += "Preencha se a Via foi entregue\n";
        }


        //---------------------------------------------------------
        // testa enquadramento
        //---------------------------------------------------------
        AitEnquadramentoDAO enqdao = new AitEnquadramentoDAO(getBaseContext());

        if (enqdao.qtdeEnquad(aitPendente.getId()) == 0) {
            erroenquad = true;
            mensagem += "Enquadramento(s) não cadastrado(s)\n";
        } else {
            List<AitEnquadramento> lstAitEnquadramento = enqdao.getLista(aitPendente.getId());

            Integer i = 0;
            while (lstAitEnquadramento.size() > i) {
                String cod = lstAitEnquadramento.get(i).getCodigo().toString();
                String obsObrigatoria = "0";
                if (AitDuplicado == true)
                    lstEnquadramentosUsados.add(cod);
                SQLiteDatabase s = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/enquadramento", null, 0);

                try {
                    Cursor c = null;
                    String sqlpesq = "Select ifnull(EnquadramentoObsObrigatorio,'0')EnquadramentoObsObrigatorio from enquadramento  where Codigo ='" + cod + "'";
                    c = s.rawQuery(sqlpesq, null);

                    while (c.moveToNext()) {
                        obsObrigatoria = (c.getString(0));
                    }
                    c.close();
                    s.close();

                } catch (SQLiteException e) {
                    Log.e("Erro=", e.getMessage());
                }
                if (obsObrigatoria.equals("1")) {
                    if (aitPendente.getObservacoes().equals("")) {
                        erroenquad = true;
                        mensagem += "Observação não cadastrada\n";
                    }
                }
                i++;
            }
        }
        enqdao.close();

        //---------------------------------------------------------
        // testa codigo do local
        //---------------------------------------------------------
        String codlog = aitPendente.getLogradouro();
        if (codlog.equals("00000")) {
            errolog = true;
            mensagem += "Logradouro não selecionado\n";
        }
        //---------------------------------------------------------------
        //testa o número do logradouro /
        //---------------------------------------------------------------
        AitDAO aitDAO = new AitDAO(PreencheAitplest.this);
        Cursor cr = aitDAO.getAit(aitPendente.getId());
        try {
            if (SimpleCrypto.decrypt(info,
                    cr.getString(cr.getColumnIndex("logradouro2"))).contains(
                    "NAO")) {
                String numlog = SimpleCrypto.decrypt(info,
                        cr.getString(cr.getColumnIndex("logradouronum")));
                numlog = numlog.trim();
                if (numlog.length() == 0) {
                    erronumlog = true;

                    // existe cruzamento
                    if (numlog.contains(" X ") || numlog.contains(" x "))
                        erronumlog = false;

                    if (erronumlog) {
                        mensagem += "Número do logradouro não preenchido ! Somente permitido quando existe X <cruzamento>\n";
                    }

                }
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //-------------------------------------------------------------------------
        //Testa país
        //-------------------------------------------------------------------------
        PaisDAO paisdao = new PaisDAO(getBaseContext());
        String pais = paisdao.buscaDescPais(aitPendente.getPais());
        paisdao.close();

        if (pais.length() == 0) {
            erropais = true;

            mensagem += "País não selecionado ! \n";
        }


        if (erroenquad || erromarca || errolog || erronumlog || erropais || errodt) {
            // informa usuario dos erros no ait


            AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
            aviso.setIcon(android.R.drawable.ic_dialog_alert);
            aviso.setTitle("Fechamento de AIT");
            aviso.setMessage(mensagem);
            aviso.setNeutralButton("OK", null);
            aviso.show();

            return false;
        } else {
            String encerramento = Utilitarios.getDataHora(1);


            // grava data hora encerramento
            //String encerramento =new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()));
            //encerramento += "-" + new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()));

            // pega todos os dados do AIT aberto
            AitDAO aitdao = new AitDAO(PreencheAitplest.this);

            Cursor cultimoAit = null;
            try {
                cultimoAit = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            aitdao.close();

            AitDAO a = new AitDAO(PreencheAitplest.this);
            aitPendente.setSendPdf("FALSE");
            a.gravaSendPdf(aitPendente);

            // encerra o 1o. AIT
            String Ait = processafechamento(encerramento);
            try {
                l.gravalog("Finalizou AIT- " + Ait, "INSERT", OrgA, Pda, agente, PreencheAitplest.this);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //startActivity(new Intent(this,ListaAit.class));
            //finish(); // encerra esta activity

            //------------------------------------------------------------------------
            // gera varios autos de infração com os enquadramentos descrito
            //------------------------------------------------------------------------
            long idAit = cultimoAit.getLong(cultimoAit.getColumnIndex("id")); // aitPendente.getId();


            //imprime(idAit);

            AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(PreencheAitplest.this);
            Cursor c1 = aitenq.getLista1(idAit);


            // primeiro
            c1.moveToFirst();

            // proximo ?
            c1.moveToNext();

            while (c1.isAfterLast() == false) {
                // seta tudo com o cursor do ultimo ait
                //editaAit(cultimoAit);


                aitPendente.setFlag("A");
                aitPendente.setAgente(cultimoAit.getString(cultimoAit.getColumnIndex("agente")));
                aitPendente.setPlaca(cultimoAit.getString(cultimoAit.getColumnIndex("placa")));
                aitPendente.setData(cultimoAit.getString(cultimoAit.getColumnIndex("data")));
                aitPendente.setHora(cultimoAit.getString(cultimoAit.getColumnIndex("hora")));
                aitPendente.setMarca(cultimoAit.getString(cultimoAit.getColumnIndex("marca")));
                aitPendente.setEspecie(cultimoAit.getString(cultimoAit.getColumnIndex("especie")));
                aitPendente.setTipo(cultimoAit.getString(cultimoAit.getColumnIndex("tipo")));
                aitPendente.setLogradouro(cultimoAit.getString(cultimoAit.getColumnIndex("logradouro")));
                aitPendente.setLogradouronum(cultimoAit.getString(cultimoAit.getColumnIndex("logradouronum")));
                aitPendente.setLogradourotipo(cultimoAit.getString(cultimoAit.getColumnIndex("logradourotipo")));
                aitPendente.setNome(cultimoAit.getString(cultimoAit.getColumnIndex("nome")));
                aitPendente.setCpf(cultimoAit.getString(cultimoAit.getColumnIndex("cpf")));
                aitPendente.setPgu(cultimoAit.getString(cultimoAit.getColumnIndex("pgu")));
                aitPendente.setUf(cultimoAit.getString(cultimoAit.getColumnIndex("uf")));
                aitPendente.setObservacoes(cultimoAit.getString(cultimoAit.getColumnIndex("observacoes")));
                aitPendente.setMedidaadm(cultimoAit.getString(cultimoAit.getColumnIndex("medidaadm")));
                aitPendente.setTipoait(cultimoAit.getString(cultimoAit.getColumnIndex("tipoait")));
                aitPendente.setPais(cultimoAit.getString(cultimoAit.getColumnIndex("pais")));

                aitPendente.setEquipamento(cultimoAit.getString(cultimoAit.getColumnIndex("equipamento")));
                aitPendente.setMedicaoreg(cultimoAit.getString(cultimoAit.getColumnIndex("medicaoreg")));
                aitPendente.setMedicaocon(cultimoAit.getString(cultimoAit.getColumnIndex("medicaocon")));
                aitPendente.setLimitereg(cultimoAit.getString(cultimoAit.getColumnIndex("limitereg")));
                aitPendente.setViaEntregue(cultimoAit.getString(cultimoAit
                        .getColumnIndex("viaEntregue")));
                aitPendente.setCondutorAbordado(cultimoAit.getString(cultimoAit
                        .getColumnIndex("condutorAbordado")));

                aitPendente.setAit("xxxx");
                try {

                    aitPendente.setImpresso(SimpleCrypto.encrypt(info, "NAO"));
                    aitPendente.setTransmitido(SimpleCrypto.encrypt(info, "NAO"));
                    aitPendente.setCancelou(SimpleCrypto.encrypt(info, "NAO"));
                    aitPendente.setMotivo(SimpleCrypto.encrypt(info, " "));

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // novo ait
                aitdao = new AitDAO(this);
                aitdao.alteraInsere(aitPendente, 2);
                aitdao.close();

                // pega o id do ait inseriro
                aitdao = new AitDAO(PreencheAitplest.this);
                Cursor c = aitdao.aitAberta(aitPendente.getAgente());
                long idAitNovo = c.getLong(c.getColumnIndex("id"));
                aitPendente.setId(idAitNovo); // posiciona se nao a rotina de fechamento fecha o primeiro ait
                c.close();
                aitdao.close();

                processafechamento(encerramento);

                // grava o enquadramento para o ait atual
                AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(PreencheAitplest.this);

                // insere o enquadramento no novo auto
                try {
                    daoenq.Insere(idAitNovo, SimpleCrypto.decrypt(info, c1.getString(c1.getColumnIndex("codigo"))));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // exclui o enquadramento do primeiro auto de infracao criado
                daoenq.deletereg(c1.getLong(c1.getColumnIndex("id")));
                daoenq.close();

                // imprime o auto de infracao
                //imprime(idAitNovo);

                c1.moveToNext();

            }

            aitenq.close();
            // fecha
            cultimoAit.close();
            c1.close();

            // apos montar o string de saida chama a thread de impressao
            //chamaImpressao();
            String GeraPdf = "";
            ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
            Cursor c = pardao.getParametros();
            try {
                GeraPdf = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("modpdf")));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (GeraPdf.contains("TRUE")) {
                montaimpressao(aitPendente.getId());
            }
            c.close();
            //startActivity(new Intent(this,ListaAit.class));

            if (fechaTela) {

                Intent i = new Intent();
                i = new Intent(this, ListaAit.class);
                i.putExtra("ModoBlitz", ModoBlitz);
                i.putExtra("agente", salvaAgente);
                startActivity(i);
                finish();
                finish(); // encerra esta activity
            }
            return true;
        }

    }


    private void montaimpressao(long idAit) {


        //String impressora ="00:08:1B:95:6B:AF";

        AitDAO aitdao = new AitDAO(PreencheAitplest.this);
        Cursor c = aitdao.getAit(idAit);

        // grava data e hora do envio para a impressora
        aitdao.atualizaImpressao(idAit, c);
        aitdao.close();

        ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
        Cursor cpar = pardao.getParametros();
        pardao.close();

        String cancelou = "";
        String ativo = "";
        String medidaadm = "";
        String tipoinfrator = "";
        String mens = "";
        String saida = "";

        try {
            cancelou = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cancelou")));
        } catch (Exception e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        String impressora = "";
        String desclog = "";
        String especie = "";
        String tipo = "";
        if (cancelou.contains("NAO")) {
            try {
                impressora = SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("impressoraMAC")));
                ativo = SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("impressoraPatrimonio"))).toUpperCase();
                // Obtem    , Logradouro ,Especie, Tipo


                EspecieDAO espdao = new EspecieDAO(PreencheAitplest.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(PreencheAitplest.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();


                MedidaAdmDAO medidadao = new MedidaAdmDAO(PreencheAitplest.this);
                medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medidaadm"))));
                medidadao.close();

                // pega tipo do logradouro
                String ctiplog = " ";

                int nx = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradourotipo"))));

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

                LogradouroDAO logdao = new LogradouroDAO(PreencheAitplest.this);
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
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto
                            .decrypt(info, c.getString(c
                                    .getColumnIndex("logradouro2")))));
                }
                logdao.close();
                saida = "";
                //CHR(7) & chr(29) & chr(47) & chr(1) // logo

                //**********************************
                // verifica se impressora é P25
                //**********************************
                Character cx = 0x1D;
                if (ativo.contains("P25")) cx = 0x1B;
                saida += cx;


                cx = '/';
                if (ativo.contains("P25")) cx = 'f';
                saida += cx;


                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("prefeitura"))) + String.format("\r\n");
                saida += "Orgao Autuador:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("orgaoautuador"))) + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("serieait"))) + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("ait"))) + String.format("\r\n");
                saida += "Placa:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("placa"))) + String.format("\r\n");
                saida += "Marca:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("marca"))) + String.format("\r\n");

                PaisDAO paisDao = new PaisDAO(PreencheAitplest.this);
                String Pais = paisDao.buscaDescPais(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pais"))));
                if (Pais != "") {
                    saida += "Pais:" + Pais + String.format("\r\n");
                }

                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("data"))) + "-" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("hora"))) + String.format("\r\n");
                saida += "Equipamento:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("seriepda"))) + String.format("\r\n");
                saida += "Condutor abordado:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("condutorAbordado"))).replace("NÃO","NAO")
                        + String.format("\r\n");
                saida += "Via entregue:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("viaEntregue"))).replace("NÃO","NAO")
                        + String.format("\r\n");
                saida += "" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");
                saida += "   Local da Infracao " + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += desclog + String.format("\r\n");
                //saida += this.ctiplog+ String.format("\r\n");

                saida += "" + String.format("\r\n");


                AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(PreencheAitplest.this);
                Cursor c1 = aitenq.getLista1(idAit);

                String enquads = " ";
                c1.moveToNext();

                //enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";

                EnquadramentoDAO dao = new EnquadramentoDAO(PreencheAitplest.this);
                List<Enquadramento> enquadramento = dao.getLista(SimpleCrypto.decrypt(info, c1.getString(c1.getColumnIndex("codigo"))), PreencheAitplest.this, "");
                dao.close();

                enquads += enquadramento.get(0).toString();

                //enquads = Utilitarios.quebraLinha(enquads);

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
                    tipoinfrator = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tipoinfrator")));
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (tipoinfrator == null) {
                    saida += "Nome:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome"))) + String.format("\r\n");
                    saida += "CPF:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpf"))) + String.format("\r\n");
                    saida += "PGU:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pgu"))) + " " + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");
                } else {
                    if (tipoinfrator.contains("CNH")) {
                        saida += "Nome:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome"))) + String.format("\r\n");
                        saida += "CPF:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpf"))) + String.format("\r\n");
                        saida += "PGU:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pgu"))) + " " + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");

                    }
                    if (tipoinfrator.contains("PID")) {
                        saida += "Nome:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome"))) + String.format("\r\n");
                        saida += "Doc. de Ident.:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("passaporte"))) + String.format("\r\n");
                        saida += "Pid:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pid"))) + " " + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");
                    }
                }

                saida += "" + String.format("\r\n");
                saida += "________________________" + String.format("\r\n");
                saida += "      Assinatura" + String.format("\r\n");
                //saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+ String.format("\r\n");

                saida += "" + String.format("\r\n");
                saida += "-----------------------" + String.format("\r\n");
                saida += "Identificacao do Agente" + String.format("\r\n");
                saida += "-----------------------" + String.format("\r\n");
                saida += "Matric.(AG):" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("agente"))) + String.format("\r\n");

                saida += "" + String.format("\r\n");
                saida += "________________________" + String.format("\r\n");
                saida += "     Lavrado por" + String.format("\r\n");


                if (SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("observacoes"))).length() > 0) {

                    if (SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("imprimeobs"))).contains("1")) {
                        saida += String.format("\r\n");
                        saida += "------------------------" + String.format("\r\n");
                        saida += "Observacoes:" + String.format("\r\n");
                        saida += SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("observacoes"))) + String.format("\r\n");
                        ;

                    }

                }

                //***********************************************
                // 28.07.2012
                //
                // imprime medida administrativa se foi definida
                //***********************************************
                if (!medidaadm.contains(("Nao definido"))) {
                    saida += String.format("\r\n");
                    saida += "------------------------" + String.format("\r\n");
                    saida += "Medida Administrativa:" + String.format("\r\n");
                    saida += medidaadm + String.format("\r\n");

                }

                //**************************************************************************************
                // 08.03.2012
                // Preencheu dados equipamento, exemplo decibelímetro ?
                if (SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("equipamento"))).length() > 0) {
                    saida += String.format("\r\n");
                    saida += "Equipamento:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("equipamento"))) + String.format("\r\n");
                    saida += "Medicao Registrada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medicaoreg"))) + String.format("\r\n");
                    saida += "Medicao Considerada:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medicaocon"))) + String.format("\r\n");
                    saida += "Limite Regulamentado:" + String.format("\r\n");
                    saida += SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("limitereg"))) + String.format("\r\n");

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


                mens = saida;
                Ait ait = new Ait();
                ait.setImpressao(mens);
                ait.setId(aitPendente.getId());
                AitDAO aitdao2 = new AitDAO(PreencheAitplest.this);
                aitdao2.gravaImpressao(ait);
                aitdao2.close();

                BkpMultaPdfDAO BkpMulta = new BkpMultaPdfDAO(PreencheAitplest.this);
                BkpMulta.SalvaMulta(aitPendente.getAit(), mens);

                LogDAO l = new LogDAO(PreencheAitplest.this);
                try {
                    l.gravalog("Gerou Impressão AIT - " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("ait"))), "INSERT",
                            SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("orgaoautuador"))), SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("seriepda"))), salvaAgente,
                            PreencheAitplest.this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                c.close();
                cpar.close();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else {
            try {

                impressora = SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("impressoraMAC")));
                ativo = SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("impressoraPatrimonio"))).toUpperCase();
                // Obtem    , Logradouro ,Especie, Tipo
                int nx = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradourotipo"))));
                String ctiplog = "";
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
                LogradouroDAO logdao = new LogradouroDAO(PreencheAitplest.this);
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
                    desclog += (" X " + logdao.buscaDescLog(SimpleCrypto
                            .decrypt(info, c.getString(c
                                    .getColumnIndex("logradouro2")))));
                }
                logdao.close();

                EspecieDAO espdao = new EspecieDAO(PreencheAitplest.this);
                especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("especie"))));
                espdao.close();

                TipoDAO tipdao = new TipoDAO(PreencheAitplest.this);
                tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tipo"))));
                tipdao.close();


                saida = "";
                //CHR(7) & chr(29) & chr(47) & chr(1) // logo

                //**********************************
                // verifica se impressora é P25
                //**********************************
                Character cx = 0x1D;
                if (ativo.contains("P25")) cx = 0x1B;
                saida += cx;


                cx = '/';
                if (ativo.contains("P25")) cx = 'f';
                saida += cx;


                cx = 0x00;
                saida += cx;

                saida += String.format("\r\n");

                saida += SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("prefeitura"))) + String.format("\r\n");
                saida += "Orgao Autuador:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("orgaoautuador"))) + String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += "   Dados da Infracao" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += "Ait:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("serieait"))) + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("ait"))) + String.format("\r\n");
                saida += "Placa:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("placa"))) + String.format("\r\n");
                saida += "Marca:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("marca"))) + String.format("\r\n");
                saida += "Especie:" + especie + String.format("\r\n");
                saida += "Tipo:" + tipo + String.format("\r\n");
                saida += "Data:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("data"))) + "-" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("hora"))) + String.format("\r\n");
                saida += "Equipamento:" + SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("seriepda"))) + String.format("\r\n");
                saida += "Condutor abordado:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("condutorAbordado"))).replace("NÃO","NAO")
                        + String.format("\r\n");
                saida += "Via entregue:"
                        + SimpleCrypto.decrypt(info,
                        c.getString(c.getColumnIndex("viaEntregue"))).replace("NÃO","NAO")
                        + String.format("\r\n");
                saida += String.format("\r\n");

                saida += "------------------------" + String.format("\r\n");
                saida += " Dados do Cancelamento" + String.format("\r\n");
                saida += "------------------------" + String.format("\r\n");

                saida += cancelou + String.format("\r\n");
                saida += "Motivo: " + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("motivo"))) + String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                saida += String.format("\r\n");
                mens = saida;

                Ait ait = new Ait();
                ait.setImpressao(mens);
                ait.setId(aitPendente.getId());
                AitDAO aitdao2 = new AitDAO(PreencheAitplest.this);
                aitdao2.gravaImpressao(ait);
                aitdao2.close();

                BkpMultaPdfDAO BkpMulta = new BkpMultaPdfDAO(PreencheAitplest.this);
                BkpMulta.SalvaMulta(aitPendente.getAit(), mens);

                LogDAO l = new LogDAO(PreencheAitplest.this);
                try {
                    l.gravalog("Gerou Impressão AIT - " + SimpleCrypto.decrypt(info,
                            c.getString(c.getColumnIndex("ait"))), "INSERT",
                            SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("orgaoautuador"))), SimpleCrypto.decrypt(info, cpar.getString(cpar.getColumnIndex("seriepda"))), salvaAgente,
                            PreencheAitplest.this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                c.close();
            } catch (Exception e) {
                mens = saida;
                // TODO: handle exception
            }
        }

    }


    //********************************************************
    // vai para a tela de cancelamento e nao mais retorna
    //********************************************************
    private void chamaTelaCan() {
        Ait aitx = new Ait();
        aitx.setId(aitPendente.getId());


        aitx.setdtEdit(edData.getText().toString());
        aitx.sethrEdit(edHora.getText().toString());

        AitDAO aitdao = new AitDAO(getBaseContext());
        aitdao.gravaDtEdit(aitx);
        aitdao.gravaHrEdit(aitx);

        Intent i = new Intent(this, CancelaAit.class);
        i.putExtra("idAit", aitPendente.getId());
        i.putExtra("agente", aitPendente.getAgente());
        startActivity(i);

        finish();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context contexto = null;

        passou = true; // indica para onResume que está na activity
        PlacaDetectada = (String) getIntent().getSerializableExtra("PlacaDetectada");

        MarcaModeloDetectada = (String) getIntent().getSerializableExtra(
                "MarcaModeloDetectada");

        salvaAgente = (String) getIntent().getSerializableExtra("agente");

        setContentView(R.layout.preencheplest);

        edPlaca = (TrataPlacaEst) findViewById(R.id.txtConsultarPlaca);
        edPlaca.setMaxLines(1);
        edPlaca.setEnabled(false);
        edPlaca.setText(PlacaDetectada);


        edMarca = (TrataMarca) findViewById(R.id.EdMarca);
        edMarca.setMaxLines(1);
        edMarca.setEnabled(false);

        edMarca.setText(MarcaModeloDetectada);

        edData = (EditText) findViewById(R.id.edData);
        edData.setEnabled(true);
        edHora = (EditText) findViewById(R.id.edHora);
        edHora.setEnabled(true);
        rdoViaEntregueSim = (RadioButton) findViewById(R.id.rdoViaEntregueSimPlEst);
        rdoViaEntregueNao = (RadioButton) findViewById(R.id.rdoViaEntregueNaoPlEst);
        rdoCondutorAbordadoSim = (RadioButton) findViewById(R.id.rdoCondutorAbordadoSimPlEst);
        rdoCondutorAbordadoNao = (RadioButton) findViewById(R.id.rdoCondutorAbordadoNaoPlEst);


        btInfrEquip = (Button) findViewById(R.id.btInfrEquipEst);
        btInfrEquip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(10);
            }
        });
        btInfrEquip.setEnabled(false);

        btPais = (Button) findViewById(R.id.btPais);

        btPais.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(9);
            }
        });
        btPais.setEnabled(false);

        btMedidaAdm = (Button) findViewById(R.id.btMedidaAdm);
        btMedidaAdm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(8);
            }
        });
        btMedidaAdm.setEnabled(false);

        btCancelaAit = (Button) findViewById(R.id.btCancelaAitP);
        btCancelaAit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Cancelamento de AIT");
                aviso.setMessage(" Deseja realmente ir para a tela de Cancelamento ?");
                aviso.setNeutralButton("Não", null);
                aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        chamaTelaCan();
                    }
                });

                aviso.show();
            }
        });
        btCancelaAit.setEnabled(false);


        btFotografa = (Button) findViewById(R.id.btFotografa);
        btFotografa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                fotografa(aitPendente.getId());
            }
        });
        btFotografa.setEnabled(false);

        btAssinatura = (Button) findViewById(R.id.btAssinaturaplest);
        btAssinatura.setEnabled(false);

        btFecha = (Button) findViewById(R.id.btFinaliza);
        btFecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                //fechaAit();
                confirmaFechamentoAit();
            }
        });
        btFecha.setEnabled(false);

        btEspecie = (Button) findViewById(R.id.btEspecie);
        btEspecie.setEnabled(false);
        btEspecie.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(1);
            }
        });

        btTipo = (Button) findViewById(R.id.btTipo);
        btTipo.setEnabled(false);
        btTipo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(2);
            }
        });


        btLogradouro = (Button) findViewById(R.id.btLogradouro);
        btLogradouro.setEnabled(false);
        btLogradouro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(3);
            }
        });

        btEnquadramento = (Button) findViewById(R.id.btEnquadramento);
        btEnquadramento.setEnabled(false);
        btEnquadramento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(4);
            }
        });


        btObservacoes = (Button) findViewById(R.id.btObservacoes);
        btObservacoes.setEnabled(false);

        btObservacoes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                chama(5);
            }
        });

        btDadosInfrator = (Button) findViewById(R.id.btDadosInfrator);
        btDadosInfrator.setEnabled(false);

        btDadosInfrator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                chama(6);
            }
        });

        btVisualiza = (Button) findViewById(R.id.btVisualizaAit);
        btVisualiza.setEnabled(false);
        btVisualiza.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                chama(7);
            }
        });
        try {
            ModoBlitz = (boolean) getIntent().getSerializableExtra(
                    "ModoBlitz");

        } catch (Exception e) {
            Log.e("erro", e.getMessage());
        }


        btPesquisa = (Button) findViewById(R.id.btPesquisa);
        chkDuplicar = (CheckBox) findViewById(R.id.chkDuplicarAIT_PlacaEst);

        // Existe ait em edição ?
        AitDAO aitdao = new AitDAO(getBaseContext());
        Cursor c = null;
        try {
            c = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if ((c != null) && (c.getCount() > 0)) {
            // ja inseriu a placa
            edPlaca.setEnabled(false);
            btPesquisa.setEnabled(false);

            edMarca.setEnabled(true);
            btEspecie.setEnabled(true);
            btTipo.setEnabled(true);
            btLogradouro.setEnabled(true);
            btEnquadramento.setEnabled(true);
            btObservacoes.setEnabled(true);
            btDadosInfrator.setEnabled(true);
            btVisualiza.setEnabled(true);
            btCancelaAit.setEnabled(true);
            btInfrEquip.setEnabled(true);
            btMedidaAdm.setEnabled(true);
            btFecha.setEnabled(true);
            btFotografa.setEnabled(true);
            btAssinatura.setEnabled(true);
            btPais.setEnabled(true);


            // Pega dados do AIT aberto
            editaAit(c);

            edMarca.setText(aitPendente.getMarca());
            edMarca.setFocusable(true);

            edPlaca.setText(aitPendente.getPlaca());
            edPlaca.setEnabled(false);
        } else {
            // habilita a placa para digitação
            edPlaca.setEnabled(true);

            //edData.setText(new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis())));
            //edHora.setText(new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis())));

            btPesquisa.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    if ((edPlaca.getText().toString().length() > 0) && (edPlaca.getText().toString().length() <= 10)) {
                        confirmaCriacaoAit();
                    } else {
                        Toast.makeText(getBaseContext(), "Placa não preenchida corretamente!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        aitdao.close();
		/*
		 * 
		 * //ExpandableListView listaEspecie = (ExpandableListView) findViewById(R.id.exListViewEspecie);
		
		
		
		MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();
        
		listaEspecie.setAdapter(mAdapter);
		
		listaEspecie.setItemChecked(1, true);
		*/


    }


    private void confirmaCriacaoAit() {
        AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
        aviso.setIcon(android.R.drawable.ic_dialog_alert);
        aviso.setTitle("Criação de AIT");
        aviso.setMessage("Confirma ?");
        aviso.setNeutralButton("Não", null);
        aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                confirmadaCriacaoAit();
            }
        });

        aviso.show();

    }

    private void confirmadaCriacaoAit() {

        // Cria o AIT e define o FLAG como A - aberto
        criaAit(edPlaca.getEditableText().toString());


        //Pega dados do AIT aberto
        AitDAO aitdao = new AitDAO(getBaseContext());
        Cursor cx = null;
        try {
            cx = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        aitdao.close();
        editaAit(cx);

        //edMarca.setText(aitPendente.getMarca());

        edPlaca.setText(aitPendente.getPlaca());
        edPlaca.setEnabled(false);

        btPesquisa.setEnabled(false);

        edMarca.setEnabled(true);
        btEspecie.setEnabled(true);
        btTipo.setEnabled(true);
        btLogradouro.setEnabled(true);
        btEnquadramento.setEnabled(true);
        btObservacoes.setEnabled(true);
        btDadosInfrator.setEnabled(true);
        btVisualiza.setEnabled(true);
        btCancelaAit.setEnabled(true);
        btInfrEquip.setEnabled(true);
        btMedidaAdm.setEnabled(true);
        btFecha.setEnabled(true);
        btFotografa.setEnabled(true);
        btAssinatura.setEnabled(true);
        btPais.setEnabled(true);

        rdoViaEntregueSim.setEnabled(true);
        rdoViaEntregueNao.setEnabled(true);
        rdoCondutorAbordadoNao.setEnabled(true);
        rdoCondutorAbordadoSim.setEnabled(true);

        // indica a data e hora
        edData.setText(aitPendente.getData());
        edHora.setText(aitPendente.getHora());

        //region se for MODO BLITZ
        if (ModoBlitz) {

            ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
            Cursor c = pardao.getParametros();

            //region obtem o ait anteior ( pega o ID )
            long atual = 0;
            try {
                atual = Long.parseLong(SimpleCrypto.decrypt(info, c.getString(0)));
            } catch (NumberFormatException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            c.close();
            atual--;
            String idAitAnterior = "";
            String ultimoait = String.format("%07d", atual);
            if (atual > 0) {
                c = aitdao.getAit1(ultimoait);
                try {
                    idAitAnterior = c.getString(c.getColumnIndex("id"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //endregion

            //region enquadramento
            List<AitEnquadramento> aitenq;
            AitEnquadramentoDAO daoenq = new AitEnquadramentoDAO(getBaseContext());
            aitenq = daoenq.getLista(aitPendente.getId());
            daoenq.close();

            aitdao = new AitDAO(PreencheAitplest.this);
            if (atual > 0 && aitenq.size()==0) {
                try {
                    daoenq = new AitEnquadramentoDAO(getBaseContext());
                    aitenq = daoenq.getLista(Long.parseLong(idAitAnterior));
                    daoenq.close();

                    if (aitenq.size() > 0) {
                        daoenq.Insere(aitPendente.getId(), aitenq.get(0).getCodigo());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //endregion
            //region logradouro
            try {
                String codLogSelecAnterior = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradouro")));

                String ntipLogSelec = (SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradourotipo"))));
                String num = (SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradouronum"))));

                //grava o codigo do logradouro
                Ait aitx = new Ait();
                aitx.setId(aitPendente.getId());
                aitx.setLogradouro(codLogSelecAnterior);
                aitx.setLogradourotipo(ntipLogSelec);
                aitx.setLogradouronum(num);
                aitdao = new AitDAO(getBaseContext());
                aitdao.gravaLocal(aitx);
                aitdao.gravaLocalNumero(aitx);
                aitdao.gravaLocalTipo(aitx);
                aitdao.close();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //endregion

            c.close();
        }
        //endregion
    }

    protected void onResume() {
        super.onResume();


        // voltou de uma activity do sistema ?
        if (passou) {

            AitDAO aitdao = new AitDAO(getBaseContext());
            Cursor cx = null;
            try {
                cx = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            aitdao.close();

            // verifica se existe ait aberto
            if ((cx != null) && (cx.getCount() > 0)) {

                //Pega dados novamente do AIT aberto
                editaAit(cx);

                edMarca.setText(aitPendente.getMarca());
                edPlaca.setText(aitPendente.getPlaca());
                edPlaca.setEnabled(false);

                edData = (EditText) findViewById(R.id.edData);
//				edData.setText(aitPendente.getData());

                edHora = (EditText) findViewById(R.id.edHora);
//				edHora.setText(aitPendente.getHora());

            }

        } else {
            // ja preencheu a placa ?
            if (aitPendente != null) {
                // pede o cancelamento...
                Intent i = new Intent(this, CancelaAit.class);
                i.putExtra("idAit", aitPendente.getId());
                i.putExtra("ModoBlitz", ModoBlitz);
                startActivity(i);

                finish();

            }
        }

        passou = false;

    }


    private Uri uriImagem = null;

    private void fotografa(long idAit) {

        FotoDAO fotodao = new FotoDAO(getBaseContext());
        if (fotodao.getQtde(idAit) == 5) {

            fotodao.close();
            Toast.makeText(getBaseContext(), "Podem ser tiradas no máximo 5 fotos !", Toast.LENGTH_SHORT);
        } else {
            fotodao.close();


            // Cria uma intent para capturar uma imagem e retorna o controle para quem o chamou (NAO PRECISA DECLARAR PERMISSAO NO MANIFESTO PARA ACESSAR A CAMERA POIS O FAZEMOS VIA INTENT).
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Cria um arquivo para salvar a imagem.
            uriImagem = ProcessaImagens.getOutputMediaFileUri(ProcessaImagens.MEDIA_TYPE_IMAGE, PreencheAitplest.this);
            // Passa para intent um objeto URI contendo o caminho e o nome do arquivo onde desejamos salvar a imagem. Pegaremos atraves do parametro data do metodo onActivityResult().
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagem);
            // Inicia a intent para captura de imagem e espera pelo resultado.
            startActivityForResult(intent, chamafoto);


            ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
            Cursor cz = pardao.getParametros();

            arquivofoto = Environment.getExternalStorageDirectory() + "/imagens/" + cz.getString(cz.getColumnIndex("serieait")) + cz.getString(cz.getColumnIndex("proximoait")) + "-" + System.currentTimeMillis() + ".jpg";
            pardao.close();
            cz.close();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        passou = false;
        if ((requestCode != chamaactivity) || (requestCode != chamafoto)) {
            // indica retorno de activity do sistema
            passou = true;
        }

        if ((requestCode != chamaactivity) && (requestCode != chamafoto)) {
            // pede o cancelamento...
            Intent i = new Intent(this, CancelaAit.class);
            i.putExtra("idAit", aitPendente.getId());
            i.putExtra("ModoBlitz", ModoBlitz);
            startActivity(i);

            finish();

        }


        if (requestCode == chamafoto) {

            if (resultCode == RESULT_OK) {
                try {

                    // Vou compactar a imagem, leia o javadoc do médoto e vera que ela retorna tanto um bitmap como um array de bytes.
                    List<Object> imagemCompactada = ProcessaImagens.compactarImagem(uriImagem.getPath());
                    Bitmap imagemBitmap = (Bitmap) imagemCompactada.get(0);
                    byte[] imagemBytes = (byte[]) imagemCompactada.get(1);


                    FotoDAO fotodao = new FotoDAO(getBaseContext());
                    fotodao.gravaFoto(aitPendente.getId(), imagemBytes);
                    fotodao.close();

                    try {
                        l.gravalog("Fotografou", "INSERT", OrgA, Pda, agente, PreencheAitplest.this);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    try {
                        l.gravalog("Erro ao fotografar- " + e.getMessage().replace(".", "-").replace(":", "-"), "Erro", OrgA, Pda, agente, PreencheAitplest.this);
                    } catch (Exception ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }

                    AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
                    aviso.setIcon(android.R.drawable.ic_dialog_alert);
                    aviso.setTitle("Foto");
                    aviso.setMessage("Erro ao salvar fotografia, tente novamente");
                    aviso.setNeutralButton("OK", null);
                    aviso.show();

                    //Toast.makeText(getBaseContext(), "Foto não foi salva , mantenha o aparelho na mesma posição ao salvar!", Toast.LENGTH_SHORT).show();
                }

            }


        }
    }
	 
    /*
    public class MyExpandableListAdapter<ExpandableList1> extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = { "Especie", };
        private String[][] children = {
                 strespecies 
                
        };
        
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        
        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
          
       
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
            ViewGroup.LayoutParams.FILL_PARENT, 64);

            TextView textView = new TextView(PreencheAit.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
    
   */


}
