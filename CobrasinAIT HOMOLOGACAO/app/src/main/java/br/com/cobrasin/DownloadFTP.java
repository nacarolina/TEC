package br.com.cobrasin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.ParametroDAO;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wpx.WPXMain;


public class DownloadFTP extends Activity {

    ProgressDialog dialog;
    EditText edServidor;
    EditText edSenha;
    EditText edUsuario;
    EditText edPDA;
    //EditText edCodigoMunicepe;
    boolean errothread = false;
    boolean executou = false;
    String retornoweb;
    boolean forca = false;  // forca carga da base
    private Handler handler = new Handler();
    private JSONArray jsonArray;
    private String info = Utilitarios.getInfo();
    private String tabelas[] = {"agente", "enquadramento", "especie", "tipo", "logradouro", "ait", "aitenquadramento", "parametro", "medidasadm", "pais", "urlswebtrans", "bkpmultapdf", "notafiscal", "QFV", "municipio"};
    boolean bBaixouDB = false;
    private SincronismoWebTrans sinc;
    Cursor cx;

    //-----------------------------------------------------------------------------------------------------------
    //Verifica se base de dados está completa, quando não está completo parte para instalação
    //-----------------------------------------------------------------------------------------------------------
    private boolean existeBase() {
        boolean erro = false;

        //String tabelas [] = { "agente","enquadramento","especie","tipo","logradouro","ait","aitenquadramento","parametro","definepda" } ;

        // caminho onde estão os arquivos
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() +"/db";

        // verifica todos
        for (int nx = 0; nx < tabelas.length; nx++) {

            // testa para verificar se existe tabela de tipo
            File file = new File(root, tabelas[nx]);

            if (!file.exists()) {
                erro = true;
            }

        }

        if (erro) return false;
        else
            return true;


    }


    //-----------------------------------------------------------------------------------------------------------
    // ocorreu algum erro durante o processo anterior de baixa do arquivos
    //-----------------------------------------------------------------------------------------------------------
    private boolean erroFTP() {

        // caminho onde estão os arquivos
        String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

        File file = new File(root, "erroftp");
        if (file.exists()) {
            return true;
        }


        // caminho onde estão os arquivos
        root = Environment.getExternalStorageDirectory().getAbsolutePath() +"/db";

        file = new File(root, "errozip");
        if (file.exists()) {
            return true;
        }


        return false;

    }

    //-----------------------------------------------------------------------------------------------------------
    //Limpa arquivos sinalizadores de erro
    //-----------------------------------------------------------------------------------------------------------
    private void limpaErroFTP() {
        // caminho onde estão os arquivos
        String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";
        try {
            File file = new File(root, "erroftp");
            file.delete();
        } catch (Exception e) {

        }
        //----------------------------------------------------------------------------------
        try {
            File file = new File(root, "errozip");
            file.delete();
        } catch (Exception e) {

        }
        //----------------------------------------------------------------------------------
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
        //----------------------------------------------------------------------------------
    }

    //-----------------------------------------------------------------------------------------------------------
    //Exclui base de dados de acordo com o array "tabelas"
    //-----------------------------------------------------------------------------------------------------------
    private void excluiBase() {
        // caminho onde estão os arquivos
        String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

        File file;
        // tenta excluir todas
        for (int nx = 0; nx < tabelas.length; nx++) {

            // caminho onde estão os arquivos
            try {
                file = new File(root, tabelas[nx]);
                file.delete();

            } catch (Exception e) {

            }

        }

        //*****************************************
        // o aitfoto é criado em tempo de execução!
        //*****************************************
        try {
            file = new File(root, "aitfoto");
            file.delete();

        } catch (Exception e) {

        }


        //*****************************************
        // o logs é criado em tempo de execução!
        //*****************************************
        try {
            file = new File(root, "logs");
            file.delete();

        } catch (Exception e) {

        }

    }

    //-----------------------------------------------------------------------------------------------------------
    // cria a pasta databases
    //-----------------------------------------------------------------------------------------------------------
    private void _dirChecker() {

        String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";
        File f = new File(root);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }


    //***********************************************
    // Reset PDA , função utilizada apenas para debug
    //***********************************************
    private void resetPda() {
        AitDAO aitdao = new AitDAO(getBaseContext());
        AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(getBaseContext());

        aitdao.deleteall();
        aitenq.deleteall();

        buscaPDA("PDA02");

        aitenq.close();
        aitdao.close();

    }

    //-----------------------------------------------------------------------------------------------------------

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WPXMain.init(getApplication());
        final boolean ftperro = false;

        _dirChecker();

        
        /*
        //*********************************************************************
        // ocorreu erro fatal na ultima tentativa de comunicacao com o webtrans
        //*********************************************************************
	    String root = Environment.getDataDirectory().getAbsolutePath()+"/data/br.com.cobrasin/databases" ;
	             
	    File file = new File(root,"erroftp");
	    if ( file.exists() )
	    {
	    	limpaErroFTP();
	    }
	    else
	    {
	    	_dirChecker();
	    }
	    */
	    	/*//************************************
	    	//base contem os agentes de transito 
	    	//************************************
	    	AgenteDAO agentedao = new AgenteDAO(getBaseContext());
	    	if (agentedao.qtdeAgentes() == 0)
	    	{
	    		forca = true;
	    	}
	    }
	    /*

        
        // executa restore do cartao do tablet
    	//Utilitarios.copiaBase(1,getBaseContext());  // restore
        
    	//************************************************************
    	// 29.06.2012
    	// criptografa todas as URL
    	//************************************************************
    	/*String urlBase ; 
		
		UrlsWebTransDAO urlswebtrans = new  UrlsWebTransDAO(DownloadFTP.this );
		urlBase = urlswebtrans.geturl("cadimp");
		
		if ( urlBase.length() == 0 )
		{
			urlswebtrans.insere();
		}
    	urlswebtrans.close();
    	*/

        //resetPda();

        // se base incompleta ou erro anterior...
        if (!existeBase() || (erroFTP())) {

            // limpa arquivos de erro
            limpaErroFTP();

            // limpa existente
            //	excluiBase();

            setContentView(R.layout.ftp);

            Button btDownload = (Button) findViewById(R.id.btObtemArqs);
            edServidor = (EditText) findViewById(R.id.edServidorFTP);
            edSenha = (EditText) findViewById(R.id.edSenhaFTP);
            edUsuario = (EditText) findViewById(R.id.edUsuarioFTP);
            edServidor.setText("189.57.47.194");
            //edUsuario.setText("androidcobra");
            //edSenha.setText("androidcobra2014");
            edUsuario.setText("cobrapalm");
            edSenha.setText("@3030");

            btDownload.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(DownloadFTP.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            mostraMensagem("Não é possível fazer os downloads necessários sem a permisão de acesso!", "Acesso Negado!");
                            return;
                        }
                    }
                    showDialog(0);
                    verificaFimProc.start();
                    baixaArq.start();
                    executou = true;
                }
            });

        } else {
            executou = true;
            startActivity(new Intent(this, CobrasinAitActivity.class));
            finish();
        }

    }

    protected void onResume() {
        super.onResume();
        if (executou) {
            startActivity(new Intent(this, CobrasinAitActivity.class));
            finish();  // encerra para nao voltar
        }
    }

    Thread paraAplicacao = new Thread() {
        public void run() {
            finish();
        }
    };

    //-----------------------------------------------------------------------------------------------------------
// Verifica se acabou o processamento
//-----------------------------------------------------------------------------------------------------------   
    Thread verificaFimProc = new Thread() {
        public void run() {

            while (true) {
                try {

                    if (bBaixouDB) {
                        break;
                    }
                    //	if ( terminouWebTrans())
                    //	{
                    //acerta os parametros do PDA ( proximo ait , ait inicial , aitfinal , impressora )

                    //if ( erroFatal() )
                    //{
                    //			mostraMensagem("Não consegui baixar Arquivos do WebTrans"," CobrasinAit ");
                    //}
    					/*else
    					{*/
                    //			if (buscaPDA(edPDA.getText().toString()))
                    //			{
                    //				mostraMensagem("Instalaçãoo Ok , inicie novamente a aplicação"," CobrasinAit ");
                    // limpa os arquivos de erro
                    //				limpaErroFTP();
                    //paraAplicacao.start();
                    //			}
                    //			else
                    //			{
                    //				excluiBase();
                    //				mostraMensagem("Não encontrei o PDA definido na lista. Entre em contato com a COBRASIN !"," CobrasinAit ");
                    //paraAplicacao.start();
                    //			}
                    //finish();
                    //}

                    //		break;
                    //	}

                    // caminho onde estão os arquivos
                    String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

                    if (errothread) {
                        mostraMensagem("Não consegui instalar base de dados", " CobrasinAit ");
                        //finish();
                        break;
                    }
                    sleep(1000); // verifica a cada 1 segundo

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }


        //******************************************************************
        // verifica se foi criado arquivo de saida da thread do webtrans
        //******************************************************************
        private boolean terminouWebTrans() {

            // caminho onde estão os arquivos
            String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

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

        //******************************************************************
        // verifica se foi criado arquivo de saida da thread de webtrans
        //******************************************************************
        private boolean erroFatal() {

            // caminho onde estão os arquivos
            String root = " Environment.getExternalStorageDirectory().getAbsolutePath() + /db";

            File file = new File(root, "errowebtrans");
            if (file.exists()) {
                return true;
            }

            return false;
        }

    };


    //-----------------------------------------------------------------------------------------------------------
// Carrega base de dados , descompacta , chama Thread para carrega dados do WebTrans
//-----------------------------------------------------------------------------------------------------------    
    Thread baixaArq = new Thread() {

        private void carregaBase() {

            // limpa arquivo de erro
            limpaErroFTP();

            informUsr("Baixando estrutura do banco de dados");
            FTPClient ftp = new FTPClient();

            try {
                //Recupera o caminho padrão do SDCARD
                //File root = Environment.getExternalStorageDirectory();
                String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

                //Cria arquivo para gravar o texto
                File file = new File(root, "baseandroid.zip");
                try {
                    ftp.disconnect();
                } catch (Exception e) {

                }
                informUsr("Conectando ao Servidor de FTP...");

                //Faz a conexão com o servidor ftp
                ftp.connect(edServidor.getText().toString(), 23450);

                //Autenticação se necessario
                boolean CONECTADO = ftp.login(edUsuario.getText().toString(), edSenha.getText().toString());

                //ftp.changeWorkingDirectory(edCodigoMunicepe.getText().toString());
                //Muda o diretorio dentro do ftp
                //ftp.changeWorkingDirectory("/appservers/apache-tomcat-6x/webapps");

                informUsr("Baixando Arquivo baseandroid.zip");

                //Faz o download do arquivo passando o nome dele(dentro o servidor) e local onde ele vai ser guardado
                if (!ftpDownload("baseandroid.zip",  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/", ftp) || !CONECTADO) // file.getAbsolutePath()
                {
                    errothread = true;

                    //Log.e("CarregaBase", "Processo falhou...");
                    mostraMensagem("Processo falhou...", "Carrega Base");
                } else {
                    dialog.dismiss();
                    informUsr("Download efetuado com sucesso!");
                    bBaixouDB = true;
                }
                // logout
                ftp.logout();

                //Disconecta do ftp
                ftp.disconnect();

                //informUsr("Baixando arquivos do WebTrans");


                //Faço a leitura do do arquivo
                //FileReader fileReader= new FileReader(file);
                //BufferedReader leitor= new BufferedReader(fileReader);
                //String linha = null;

            } catch (Exception e) {

                errothread = true;

                Log.e("CarregaBase", "Processo falhou...");

                // TODO: handle exception
                Log.e("CarregaBase", e.getMessage());

                try {

                    criaArqErro(e.getMessage());

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    Log.e("Log", e1.getMessage());
                    ;
                }
            } finally {

                // try {
                //ftp.logout();
                //	ftp.disconnect();
                //	} catch (IOException e) {
                // TODO Auto-generated catch block
                //	Log.e("Log", e.getMessage());;
                //}
            }

            if (bBaixouDB) {
                //************************************************************
                // 29.06.2012
                // criptografa todas as URL
                //************************************************************
                //	UrlsWebTransDAO urlswebtrans = new  UrlsWebTransDAO(DownloadFTP.this );
                //	urlswebtrans.insere();
                //	urlswebtrans.close();

                ParametroDAO p = new ParametroDAO(DownloadFTP.this);
                p.UpdateFTP(edServidor.toString(), edUsuario.toString(), edSenha.toString(), "baseAndroid.zip");
                p.close();
            }
        }

        //------------------------------------------------------------
        // executa o download da base e descomptacta...
        //------------------------------------------------------------

        private boolean ftpDownload(String srcFile, String desFilePath, FTPClient ftp) {
            boolean status = false;


            try {
                File dbFolder = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/");
                File baseZipFile = new File(dbFolder.getAbsolutePath() + "/baseandroid.zip");
                if (!dbFolder.exists())
                    dbFolder.mkdir();
                if (!baseZipFile.exists())
                    baseZipFile.createNewFile();

                //Cria o outputStream para ser passado como parametro
                FileOutputStream desFileStream = new FileOutputStream(desFilePath + srcFile);

                //Tipo de arquivo
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                //http://commons.apache.org/net/apidocs/org/apache/commons/net/ftp/FTPClient.html#enterLocalActiveMode()
                ftp.enterLocalPassiveMode();


                //Faz o download do arquivo
                status = ftp.retrieveFile(srcFile, desFileStream);

                //Fecho o output
                desFileStream.close();


                if (status == true) {

                    informUsr("Descompactando arquivos...");
                    unzip(desFilePath, srcFile);
                }


                return status;
            } catch (Exception e) {

                errothread = true;

                Log.e("Download", "Processo falhou...");
                Log.e("Download", e.getMessage());

                try {
                    criaArqErro(e.getMessage());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            return status;
        }

        //-----------------------------------------------------------------------------------------------------------
        // Descompacta arquivos de baseandroid.zip
        //-----------------------------------------------------------------------------------------------------------
        public void unzip(String _location, String _zipFile) {
            try {
                FileInputStream fin = new FileInputStream(_location + _zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {

                    //Log.v("Decompress", "Unzipping " + ze.getName());

                    informUsr("Descompactando arquivo: " + ze.getName());

                    if (ze.isDirectory()) {
                        _dirChecker(_location, ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }

                        zin.closeEntry();
                        fout.close();
                    }

                }
                zin.close();
                //executou = true ; // indica que tudo ocorreu corretamente...
            } catch (Exception e) {

                errothread = true;

                Log.e("Decompress", "unzip", e);
                Log.e("Descompactacao", "Processo falhou...");

                try {
                    criaArqErroZip(e.getMessage());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

        }

        private void _dirChecker(String _location, String dir) {
            File f = new File(_location + dir);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }

        //-----------------------------------------------------------------------------------------------------------
        //Cria arquivo de erro no processo de ftp com mensagem informada
        //-----------------------------------------------------------------------------------------------------------
        private void criaArqErro(String menserro) throws IOException {
            // caminho onde estão os arquivos
            String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

            File file = new File(root, "erroftp");

            FileOutputStream fout = new FileOutputStream(root + "/erroftp");
            fout.write(menserro.getBytes());
            fout.close();
        }

        //-----------------------------------------------------------------------------------------------------------
        // Cria arquivo de indicacao de sucesso durante processo de "Unzip"
        //-----------------------------------------------------------------------------------------------------------
        private void criaArqOk(String menserro) throws IOException {
            // caminho onde estão os arquivos
            String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

            File file = new File(root, "okftp");

            FileOutputStream fout = new FileOutputStream(root + "/okftp");
            fout.write(menserro.getBytes());
            fout.close();
        }

        //-----------------------------------------------------------------------------------------------------------
        // Informa se ocoreu erro no processo de "unzip"
        //-----------------------------------------------------------------------------------------------------------
        private boolean erroFTP() {

            // caminho onde estão os arquivos
            String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

            File file = new File(root, "errozip");
            if (file.exists()) {
                return true;
            }

            return false;

        }

        //-----------------------------------------------------------------------------------------------------------
        // Cria arquivo de indicacao de erro durante processo de "Unzip"
        //-----------------------------------------------------------------------------------------------------------
        private void criaArqErroZip(String menserro) throws IOException {
            // caminho onde estão os arquivos
            String root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

            File file = new File(root, "erroftp");

            FileOutputStream fout = new FileOutputStream(root + "/erroftp");
            fout.write(menserro.getBytes());
            fout.close();

        }

        //-----------------------------------------------------------------------------------------------------------
        public void run() {
            carregaBase();
            if(errothread==false) {
                startActivity(new Intent(DownloadFTP.this, CobrasinAitActivity.class));
                finish();
            }
            else{
                dialog.dismiss();
            }
        }
    };

    //-----------------------------------------------------------------------------------------------------------
    // Montra mensagem no "dialog" através de um "handler"
    //-----------------------------------------------------------------------------------------------------------
    protected Dialog onCreateDialog(int id) {

        dialog = new ProgressDialog(DownloadFTP.this);
        dialog.setMessage("Vou baixar a base de dados e instalar, por favor aguarde...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    //-----------------------------------------------------------------------------------------------------------
    private void mostraMensagem(final String mens, final String titulo) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                //String mensagem = "Erro na instalação...";
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        DownloadFTP.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle(titulo);
                aviso.setMessage(mens);
                aviso.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                        //finish();
                    }
                });
                aviso.show();

            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------
    private void informUsr(final String mens) {
        handler.post(new Runnable() {

            @Override
            public void run() {

                dialog.setMessage(mens);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------
// Pesquisa em DefinePda para verifica se existe o PDA passado pelo usuario    
//-----------------------------------------------------------------------------------------------------------
    private boolean buscaPDA(String pda) {
        boolean achou = false;

        pda = pda.toUpperCase();

        //ParametroDAO pardao = new ParametroDAO(DownloadFTP.this);
        // exclui registro(s) existente(s)
        //pardao.limpareg();

        //pardao.close();

        ParametroDAO paDao = new ParametroDAO(DownloadFTP.this);

        // le todos os pdas do orgao autuador
        cx = paDao.getParametros();

        paDao.close();

        cx.moveToFirst();

        while (cx.isAfterLast() == false) {

            String xpda = "";
            try {
                xpda = SimpleCrypto.decrypt(Utilitarios.getInfo(), cx.getString(cx.getColumnIndex("seriepda")));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String ativo = null;

            try {
                ativo = SimpleCrypto.decrypt(Utilitarios.getInfo(), cx.getString(cx.getColumnIndex("ativo")));
            } catch (Exception ex) {

            }
            if (ativo != null) {
                //String ativo = cx.getString(cx.getColumnIndex("ativo"));

                // achou pda e esta Ativo
                if ((pda.contains(xpda)) && (ativo.contains("S"))) {

                    achou = true;

                    if (ativo.contains("S")) {

                        try {

                            String prefativa = SimpleCrypto.decrypt(Utilitarios.getInfo(), cx.getString(cx.getColumnIndex("prefativa")));

                            if (prefativa.contains("S")) {
                                //pardao = new ParametroDAO(DownloadFTP.this);
                                //pardao.iniciapda(cx, 0);
                                //pardao.close();
                            } else {
                                mostraMensagem("Prefeitura não está Ativa !", " CobrasinAit ");
                            }


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        mostraMensagem("PDA: " + xpda + " Desativado pelo Orgão de Trânsito!", " CobrasinAit ");
                    }
                }
            }
            cx.moveToNext();
        }
        cx.close();

        //pardao.close();

        return achou;
    }

}
