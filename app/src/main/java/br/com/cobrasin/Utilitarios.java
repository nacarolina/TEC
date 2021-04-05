package br.com.cobrasin;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;


import br.com.cobrasin.dao.AgenteDAO;
import br.com.cobrasin.dao.LogDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Logs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

import com.wpx.util.ConvertUtil;

public class Utilitarios {


    private static final String TAG = "cobrasin";


    //*********************************************
    // Formata numero com virgula ex 32.2 -: 32,2
    //*********************************************
    public static String formatar(String par) {

        //float numero = Float.parseFloat(par);

        //NumberFormat formatter = NumberFormat.getInstance(new Locale("pt", "BR"));

        //return formatter.format(numero);

        DecimalFormatSymbols dfsPonto = new DecimalFormatSymbols();
        dfsPonto.setDecimalSeparator('.');
        Double parDouble = 0.00;
        try {
            parDouble = new DecimalFormat("#.#", dfsPonto).parse(par).doubleValue();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DecimalFormatSymbols dfsVirgula = new DecimalFormatSymbols();
        dfsVirgula.setDecimalSeparator(',');
        return new DecimalFormat("#0.00", dfsVirgula).format(parDouble);


    }

    //*********************************************
    // Verifica se tem conexao de rede
    //*********************************************
    public static boolean conectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            String LogSync = null;
            String LogToUserTitle = null;
            Object handler;
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                LogSync += "\nConectado a Internet 3G ";
                LogToUserTitle += "Conectado a Internet 3G ";
                // handler.sendEmptyMessage(0);
                //Log.d(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                return true;
            } else if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                LogSync += "\nConectado a Internet WIFI ";
                LogToUserTitle += "Conectado a Internet WIFI ";
                //handler.sendEmptyMessage(0);
                //Log.d(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                return true;
            } else {
                LogSync += "\nNão possui conexão com a internet ";
                LogToUserTitle += "Não possui conexão com a internet ";
                //handler.sendEmptyMessage(0);
                //Log.e(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                //Log.e(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    //***************************************************************************************
    // Devolve a data da solicitacao a partir do ( encerramento, cancelamento , transmissão )
    //***************************************************************************************
    public static String getDataSolicitacao(String par) {
        //11.04.2012 13:44:22
        par = par.substring(0, 2) +
                par.substring(3, 5) +
                par.substring(6, 10) +
                par.substring(11, 13) +
                par.substring(14, 16) +
                par.substring(17, 19);
        return par;

    }

    //*********************************************
    // Devolve a data + hora em diversos formatos
    //*********************************************
    public static String getDataHora(int par) {
        SimpleDateFormat dateFormat = null;

        switch (par) {
            case 1:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                break;
            case 2:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                break;
            case 3:
                dateFormat = new SimpleDateFormat("HH:mm");
                break;
            case 4:
                dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                break;
        }

        //String eventDateString;
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT")); // GMT will always be supported by getTimeZone
        //eventDateString = dateFormat.format(calendar.getTime());
        ////System.out.println(eventDateString);


        Calendar calendarLocal = Calendar.getInstance();
        String eventDateString = dateFormat.format(calendarLocal.getTime());
        return eventDateString;
    }

    //******************************************************
    // devolve a chave para gerar a criptografia 2012ANCOBRA
    //******************************************************
    public static String getInfo() {
        return "-----------";
    }


    public static int RecuperaPreferencias(Context ctx) {

        SharedPreferences sharedPreferences = ctx.getSharedPreferences("PREFS_PRIVATE", Context.MODE_PRIVATE);

        String smaxlen = sharedPreferences.getString("tamanho", "");

        int maxlen = Integer.parseInt(smaxlen);
        sharedPreferences = null;

        return maxlen;

    }

    public static void GravaPreferencias(String tamanho, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("PREFS_PRIVATE", Context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString("tamanho", tamanho);
        prefsPrivateEditor.commit();
    }


    //******************************************************
    //Grava a entrada e saida do Usuário
    //******************************************************
	/*public static void gravaLogon(String agente , String status,Context ctx) {
		// TODO Auto-generated method stub
		
	
		ParametroDAO pardao = new ParametroDAO(ctx);
		Cursor c = pardao.getParametros();
		c.moveToFirst();
		
		String info = getInfo();
		
		Logs logs  = new Logs();
		try {
			
			logs.setOrgao(SimpleCrypto.encrypt(info, c.getString(c.getColumnIndex("orgaoautuador"))));
			logs.setAgente(SimpleCrypto.encrypt(info, agente));
			logs.setPda(SimpleCrypto.encrypt(info, c.getString(c.getColumnIndex("seriepda"))));
			logs.setData(SimpleCrypto.encrypt(info, Utilitarios.getDataHora(2)));
			logs.setHora(SimpleCrypto.encrypt(info, Utilitarios.getDataHora(3)));
			logs.setStatus(SimpleCrypto.encrypt(info, status));
			logs.setIdwebtrans(c.getLong(c.getColumnIndex("idwebtrans")));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c.close();
		
		LogDAO logdao = new LogDAO(ctx);
		
		logdao.gravalog(logs);
		
		logdao.close();
		
		pardao.close();
	}

	*/
	/*
	public static String calculaDiferenca(String datainicial, String datafinal)  {
        String result = "";
        String data = "DD/MM/yyyy HH:MM:SS";
        String hora_saida = "HH:MM:SS";
        SimpleDateFormat sdf = new SimpleDateFormat(data);
        SimpleDateFormat df = new SimpleDateFormat(hora_saida);
        Date hora = new Date();
        Date horaS = new Date(); 
        Date diferenca = new Date();
        try
        { 
            horaS = sdf.parse (datainicial); 
            hora = sdf.parse(datafinal);
        }
        catch(ParseException p)
        {
            System.out.println (p.getMessage ());
        }
        
        diferenca.setTime (hora.getTime () - horaS.getTime ());
        Log.i("Tempo",datainicial);
        Log.i("Tempo",datafinal);
        result = df.format (diferenca);
        
        return result ;
    }
	
	*/


    //**********************************************
    // Calcula a diferenca de dias para exibir o Ait
    //**********************************************
    public static long calculaDias(String dalvo) {

        // Data inicial

        Calendar dataInicio = Calendar.getInstance();

        // Atribui a data de 10/FEV/2008


        int ano, mes, dia, hora, min, sec;

        // 26/01/2012 11:28:00
        dia = Integer.parseInt(dalvo.substring(0, 2));
        mes = Integer.parseInt(dalvo.substring(3, 5));
        ano = Integer.parseInt(dalvo.substring(6, 10));

        //mes--;

        hora = Integer.parseInt(dalvo.substring(11, 13));
        min = Integer.parseInt(dalvo.substring(14, 16));
        //sec = Integer.parseInt(dalvo.substring(17,19));

        //dataInicio.set( ano, mes,dia,hora,min,sec);
	/*
	switch ( mes )
	{
	case 1:
		mes = Calendar.JANUARY;
		break;
	case 2:
		mes = Calendar.FEBRUARY;
		break;
	case 3:
		mes = Calendar.MARCH;
		break;
	case 4:
		mes = Calendar.APRIL;
		break;
	case 5:
		mes = Calendar.MAY;
		break;
	case 6:
		mes = Calendar.JUNE;
		break;
	case 7:
		mes = Calendar.JULY;
		break;
	case 8:
		mes = Calendar.AUGUST;
		break;
	case 9:
		mes = Calendar.SEPTEMBER;
		break;
	case 10:
		mes = Calendar.OCTOBER;
		break;
	case 11:
		mes = Calendar.NOVEMBER;
		break;
	case 12:
		mes = Calendar.DECEMBER;
		break;
	}*/
        mes--;

        dataInicio.set(ano, mes, dia, hora, min);

        //dataInicio.set(2012, Calendar.JANUARY, 25);

        // Data de hoje

        Calendar dataFinal = Calendar.getInstance();

        // Calcula a diferença entre hoje e da data de inicio

        long diferenca = dataFinal.getTimeInMillis() - dataInicio.getTimeInMillis();


        // Quantidade de milissegundos em um dia

        int tempoDia = 1000 * 60 * 60 * 24;

        long diasDiferenca = diferenca / tempoDia;

        //System.out.println("Entre a data inicial e final são " +

        //diasDiferenca + " dias de diferença.");
        diasDiferenca = Long.parseLong(String.valueOf(diasDiferenca).replace("-", ""));
        return diasDiferenca;

    }

    public static long calculaHoraAit(String dalvo, String DataAit) {

        // Data inicial

        Calendar dataInicio = Calendar.getInstance();

        // Atribui a data de 10/FEV/2008


        int ano, mes, dia, hora, min, sec;

        // 26/01/2012 11:28:00
        dia = Integer.parseInt(dalvo.substring(0, 2));
        mes = Integer.parseInt(dalvo.substring(3, 5));
        ano = Integer.parseInt(dalvo.substring(6, 10));

        //mes--;

        hora = Integer.parseInt(dalvo.substring(11, 13));
        min = Integer.parseInt(dalvo.substring(14, 16));
        sec = Integer.parseInt(dalvo.substring(17, 19));

        //Data Ait
        int anoAit, mesAit, diaAit, horaAit, minAit, secAit;

        // 26/01/2012 11:28:00
        diaAit = Integer.parseInt(DataAit.substring(0, 2));
        mesAit = Integer.parseInt(DataAit.substring(3, 5));
        anoAit = Integer.parseInt(DataAit.substring(6, 10));

        horaAit = Integer.parseInt(DataAit.substring(11, 13));
        minAit = Integer.parseInt(DataAit.substring(14, 16));
        secAit = Integer.parseInt(DataAit.substring(17, 19));

        //dataInicio.set( ano, mes,dia,hora,min,sec);
	/*
	switch ( mes )
	{
	case 1:
		mes = Calendar.JANUARY;
		break;
	case 2:
		mes = Calendar.FEBRUARY;
		break;
	case 3:
		mes = Calendar.MARCH;
		break;
	case 4:
		mes = Calendar.APRIL;
		break;
	case 5:
		mes = Calendar.MAY;
		break;
	case 6:
		mes = Calendar.JUNE;
		break;
	case 7:
		mes = Calendar.JULY;
		break;
	case 8:
		mes = Calendar.AUGUST;
		break;
	case 9:
		mes = Calendar.SEPTEMBER;
		break;
	case 10:
		mes = Calendar.OCTOBER;
		break;
	case 11:
		mes = Calendar.NOVEMBER;
		break;
	case 12:
		mes = Calendar.DECEMBER;
		break;
	}*/
//	mes--;

        dataInicio.set(ano, mes, dia, hora, min, sec);

        //dataInicio.set(2012, Calendar.JANUARY, 25);

        // Data de hoje

        Calendar dataFinal = Calendar.getInstance();

        dataFinal.set(anoAit, mesAit, diaAit, horaAit, minAit, secAit);

        // Calcula a diferença entre hoje e da data de inicio

        long diferenca = dataFinal.getTimeInMillis() - dataInicio.getTimeInMillis();


        // Quantidade de milissegundos em um dia

//	int tempoDia = 1000 * 60 * 60 * 24;

        //long diasDiferenca = diferenca / tempoDia;

        //System.out.println("Entre a data inicial e final são " +

        //diasDiferenca + " dias de diferença.");

        return diferenca;

    }


    //*******************************************************
    // Verifica se cancelou o Auto antes de Transmitir , para
    // não pedir novamente o cancelamento
    //*******************************************************
    public static boolean cancelouAntes(String dcancelou, String dtransmitiu) {

        //**********************************************
        //  data cancelamento     data transmissao
        // 18/05/2012 13:09:10   18/05/2012 13:10:20
        //**********************************************

        // Data inicial

        Calendar dataInicio = Calendar.getInstance();

        int ano, mes, dia, hora, min, sec;

        // 26/01/2012 11:28:00
        dia = Integer.parseInt(dcancelou.substring(0, 2));
        mes = Integer.parseInt(dcancelou.substring(3, 5));
        ano = Integer.parseInt(dcancelou.substring(6, 10));

        hora = Integer.parseInt(dcancelou.substring(11, 13));
        min = Integer.parseInt(dcancelou.substring(14, 16));
        sec = Integer.parseInt(dcancelou.substring(17, 19));

        mes--;

        dataInicio.setTimeInMillis(0);
        dataInicio.set(ano, mes, dia, hora, min, sec);

        //dataInicio.set(2012, Calendar.JANUARY, 25);

        // Data de hoje

        Calendar dataFinal = Calendar.getInstance();

        dia = Integer.parseInt(dtransmitiu.substring(0, 2));
        mes = Integer.parseInt(dtransmitiu.substring(3, 5));
        ano = Integer.parseInt(dtransmitiu.substring(6, 10));

        hora = Integer.parseInt(dtransmitiu.substring(11, 13));
        min = Integer.parseInt(dtransmitiu.substring(14, 16));
        sec = Integer.parseInt(dtransmitiu.substring(17, 19));

        mes--;

        dataFinal.setTimeInMillis(0);
        dataFinal.set(ano, mes, dia, hora, min, sec);

        // Calcula a diferença entre hoje e da data de inicio

        boolean ret = false;

        if (dataFinal.getTimeInMillis() - dataInicio.getTimeInMillis() > 0) ret = true;

        return ret;

    }


    //******************************************
    // Se a linha for > 25 insere retorno
    //******************************************
    public static String quebraLinha(String linha) {
        String ret = "";
        int nzz = 0;
        for (int ncx = 0; ncx < linha.length(); ncx++) {
            ret = ret + linha.substring(ncx, ncx + 1);

            nzz++;

            if (nzz == 25) {
                nzz = 0;
                ret = ret + String.format("\n\r");
            }
        }
        return linha;

    }

    //*************************************************
    //valida CNPJ
    //*************************************************
    public static boolean validaCNPJ(String cnpj) {
        boolean ret = false;
        String base = "00000000000000";
        if (cnpj.length() <= 14) {
            if (cnpj.length() < 14) {
                cnpj = base.substring(0, 14 - cnpj.length()) + cnpj;
            }

            int soma = 0;
            int dig = 0;
            String cnpj_calc = cnpj.substring(0, 12);
            char[] chr_cnpj = cnpj.toCharArray();
            // Primeira parte
            for (int i = 0; i < 4; i++)
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
            for (int i = 0; i < 8; i++)
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
            // Segunda parte
            soma = 0;
            for (int i = 0; i < 5; i++)
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
            for (int i = 0; i < 8; i++)
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
            ret = cnpj.equals(cnpj_calc);

        }
        if (ret) {
            System.out.println("O CNPJ [" + cnpj + "] é   válido.");
        } else {
            System.out.println("O CNPJ [" + cnpj + "] é inválido.");
        }
        return ret;
    }

    //*************************************************
    //valida CNPJ
    //*************************************************
    public static boolean validaCPF(String cpf) {
        boolean ret = false;

        if (!cpf.matches("[0-9]{11}")) {
            return false;
        }


        //"082465208"
        String base = "00000000000";
        String digitos = "00";
        if (cpf.length() <= 11) {
            if (cpf.length() < 11) {

                cpf = base.substring(0, 11 - cpf.length()) + cpf;
                //cpf = base.substring(0, 9 - cpf.length()) + cpf;

                base = cpf.substring(0, 9);
            }
            base = cpf.substring(0, 9);
            digitos = cpf.substring(9, 11);
            int soma = 0, mult = 11;
            int[] var = new int[11];
            // Recebe os números e realiza a multiplicação e soma.
            for (int i = 0; i < 9; i++) {
                var[i] = Integer.parseInt("" + cpf.charAt(i));
                if (i < 9)
                    soma += (var[i] * --mult);
            }
            // Cria o primeiro dígito verificador.
            int resto = soma % 11;
            if (resto < 2) {
                var[9] = 0;
            } else {
                var[9] = 11 - resto;
            }
            // Reinicia os valores.
            soma = 0;
            mult = 11;
            // Realiza a multiplicação e soma do segundo dígito.
            for (int i = 0; i < 10; i++)
                soma += var[i] * mult--;
            // Cria o segundo dígito verificador.
            resto = soma % 11;
            if (resto < 2) {
                var[10] = 0;
            } else {
                var[10] = 11 - resto;
            }
            if ((digitos.substring(0, 1).equalsIgnoreCase(new Integer(var[9])
                    .toString()))
                    && (digitos.substring(1, 2).equalsIgnoreCase(new Integer(
                    var[10]).toString()))) {
                ret = true;
            }
        }

        if (ret) {
            System.out.println("O CPF  [" + cpf + "]    é   válido.");
        } else {
            System.out.println("O CPF  [" + cpf + "]    é inválido.");
        }
        return ret;
    }

    //*************************************************
    //Agente está ativo
    //*************************************************
    public static boolean agenteAtivo(Context ctx, String cagente) {
        boolean retorno = false;

        AgenteDAO agentedaox = new AgenteDAO(ctx);

        retorno = agentedaox.agenteAtivo(cagente);

        agentedaox.close();

        return retorno;
    }


    //*************************************************
    //Pda está ativo
    //*************************************************
    public static boolean pdaAtivo(Context ctx) {
        boolean retorno = false;

        ParametroDAO pardaow = new ParametroDAO(ctx);

        retorno = pardaow.pdaAtivo();

        pardaow.close();

        return retorno;
    }

    //*************************************************
    //Prefeitura ativa
    //*************************************************
    public static boolean prefeituraAtiva(Context ctx) {
        boolean retorno = false;

        ParametroDAO pardaow = new ParametroDAO(ctx);

        retorno = pardaow.prefeituraAtiva();

        pardaow.close();

        return retorno;
    }

    //*************************************************
    //Grava Log de transmissão
    //*************************************************
    public static void gravaLog(String mens, Context ctx) {

        //*************************************************
        // Define o nome da pasta de Backup
        //*************************************************
        ParametroDAO pardao = new ParametroDAO(ctx);
        Cursor cx = pardao.getParametros();
        String nomeArquivoLog = "log_" + cx.getString(cx.getColumnIndex("orgaoautuador")) + "_" + cx.getString(cx.getColumnIndex("seriepda")) + ".txt";


        cx.close();
        pardao.close();

        String root = Environment.getDataDirectory().getAbsolutePath() + "/data/br.com.cobrasin/databases";

        File file = new File(root, nomeArquivoLog);

        boolean lap = false;
        if (file.exists()) lap = true;

        //FileOutputStream fout;

        try {

            //if (lap)
            //fout = new FileOutputStream(root + "/" + nomeArquivoLog,true);
            //else
            //fout = new FileOutputStream(root + "/" + nomeArquivoLog,false);

            //OutputStreamWriter osw = new OutputStreamWriter(fout);

            //if (lap)
            //osw.append(getDataHora(1) + ": " + mens + String.format("\n\r"));
            //else
            //osw.write(getDataHora(1) + ": " + mens + String.format("\n\r"));

            //osw.close();
            //fout.close();

            FileWriter fstream;
            if (lap)
                fstream = new FileWriter(root + "/" + nomeArquivoLog, true);
            else
                fstream = new FileWriter(root + "/" + nomeArquivoLog, false);

            BufferedWriter fbw = new BufferedWriter(fstream);
            fbw.write(getDataHora(1) + ": " + mens + String.format("\n\r"));
            fbw.newLine();
            fbw.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //*************************************************
    //Verifica a existencia de cartao SD
    //*************************************************
    private static boolean mExternalStorageAvailable = false;
    private static boolean mExternalStorageWriteable = false;

    private static void verificarSD() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // SD montado, podemos ler e escrever no disco
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // SD montado como read only, só podemos ler
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Existe algo errado com o disco ou não existe dispositivo
            // Nao podemos fazer nada.
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    //***************************************************************************
    //Copia a base de dados de/para o cartao sd / Tipo 0- Backup / Tipo 1-Restore
    //***************************************************************************
    public static void copiaBase(int tipo, Context ctx) {

        verificarSD();


        if (mExternalStorageWriteable) {

            //*************************************************
            // Define o nome da pasta de Backup
            //*************************************************
            ParametroDAO pardao = new ParametroDAO(ctx);
            Cursor cx = pardao.getParametros();
            String nomePastaBackup = "";
            try {
                nomePastaBackup = "/bkp_db_" + SimpleCrypto.decrypt(getInfo(), cx.getString(cx.getColumnIndex("orgaoautuador"))) + "_" + SimpleCrypto.decrypt(getInfo(), cx.getString(cx.getColumnIndex("seriepda")));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //nomePastaBackup = "/bk_cobrapalm_263130_PDA04" ;


            String tabelasx[] = //{ "agente","enquadramento","especie","tipo","logradouro","ait","aitenquadramento","parametro","definepda","pais","foto" } ;

	    				/* - retirada em 02.07.2012
	    		{ 		"agente",
	    				"enquadramento",
	    				"enquadramentopf",
	    				"enquadramentopj",
	    				"especie",
	    				"tipo",
	    				"logs",	    				
	    				"logradouro",
	    				"ait",
	    				"aitenquadramento",
	    				"parametro",
	    				"urlswebtrans",
	    				"definepda" ,
	    				"medidasadm",
	    				"pais",
	    				"foto"} ; */

                    {    // implementação em 02.07.2012
                            "agente",
                            "logs",
                            "ait",
                            "aitenquadramento",
                            "parametro",
                            "foto"};

            String root = "", rootext = "";

            if (tipo == 0) {
                // caminho onde estão os arquivos
                root =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";

                rootext = Environment.getExternalStorageDirectory()
                        + nomePastaBackup;
            } else {
                root = Environment.getExternalStorageDirectory()
                        + nomePastaBackup;

                //*****************
                // no Samsung
                //*****************
                //root = "/sdcard/external_sd/bk_cobrapalm_265810/PDA_01/";

                //	root = "/Phone/bkp_db_8179_PDA05";

                rootext =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/db";
            }

            File file = new File(rootext);

            if (!file.exists()) {
                file.mkdir();
            }
            //root = root.replace("sdcard","Phone");
            // verifica todos
            for (int nx = 0; nx < tabelasx.length; nx++) {

                // testa para verificar se existe tabela de tipo
                file = new File(root, tabelasx[nx]);

                {

                    //FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    if ((tipo == 0 && file.exists()) || (tipo == 1))

                        try {

                            FileInputStream fin = new FileInputStream(root + "/" + tabelasx[nx]);

                            FileOutputStream fout = new FileOutputStream(rootext + "/" + tabelasx[nx] + "_" + SimpleCrypto.decrypt(getInfo(), cx.getString(cx.getColumnIndex("orgaoautuador"))) + "_" + SimpleCrypto.decrypt(getInfo(), cx.getString(cx.getColumnIndex("seriepda"))));

                            for (int c = fin.read(); c != -1; c = fin.read()) {
                                fout.write(c);
                            }

                            fin.close();
                            fout.close();

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                }
            }
            cx.close();
            pardao.close();
        }

    }

    //*****************************************************
    //VALIDAÇÃO PGU ( CNH , nova 11 digitos )
    //*****************************************************
    public static boolean validaPGU(String cnh) {
        cnh = desformatar(cnh);
        if (!cnh.matches("[0-9]{11}")) {
            return false;
        }

        if (cnh.equals("11111111111") || cnh.equals("22222222222") || cnh.equals("33333333333")
                || cnh.equals("44444444444") || cnh.equals("55555555555") || cnh.equals("66666666666")
                || cnh.equals("77777777777") || cnh.equals("88888888888") || cnh.equals("99999999999")
                || cnh.equals("00000000000")) {
            return false;
        }

        int[] fracao = new int[9];
        int acumulador = 0;
        int inc = 2;
        for (int i = 0; i < 9; i++) {
            fracao[i] = (Math.abs(Integer.parseInt(cnh.substring(i, i + 1)))) * inc;
            acumulador += fracao[i];
            inc++;
        }

        int resto = acumulador % 11;
        int digito1 = 0;
        if (resto > 1) {
            digito1 = 11 - resto;
        }
        acumulador = digito1 * 2;
        inc = 3;
        for (int i = 0; i < 9; i++) {
            fracao[i] = (Math.abs(Integer.parseInt(cnh.substring(i, i + 1)))) * inc;
            acumulador += Math.abs(fracao[i]);
            inc++;
        }

        resto = acumulador % 11;
        int digito2 = 0;
        if (resto > 1) {
            digito2 = 11 - resto;
        }
        if (digito1 == Math.abs(Integer.parseInt(cnh.substring(9, 10)))
                && digito2 == Math.abs(Integer.parseInt(cnh.substring(10, 11)))) {
            return true;
        }

        return false;
    }


    private static String desformatar(String valor) {
        String str = "";
        String caracter = "";
        for (int i = 0; i < valor.length(); i++) {
            caracter = valor.substring(i, i + 1);

            if (ehNumero(caracter)) {
                str += caracter;
            }
        }
        return str;
    }

    private static boolean ehNumero(String caracter) {
        for (int z = 0; z <= 9; z++) {
            if (caracter.equals(String.valueOf(z))) {
                return true;
            }
        }

        return false;
    }

    //********************************************
    //Remove Acentuação
    //********************************************
    public static String removeAcentos(String str) {

        //str = Normalizer.normalize(str, Normalizer.Form.NFD);

        //str = str.replaceAll("[^\\p{ASCII}]", "");

        String todosAcentos = "ÁÍÓÚÉÄÏÖÜËÀÌÒÙÈÃÕÂÎÔÛÊáíóúéäïöüëàìòùèãõâîôûêÇç";
        String semAcentos = "AIOUEAIOUEAIOUEAOAIOUEaioueaioueaioueaoaioueCc";

        String strSaida = "";
        String troca = "";
        for (int nk = 0; nk < str.length(); nk++) {

            troca = "";
            for (int nz = 0; nz < todosAcentos.length(); nz++) {

                if (str.substring(nk, nk + 1).compareTo(todosAcentos.substring(nz, nz + 1)) == 0) {
                    troca = semAcentos.substring(nz, nz + 1);
                    nz = todosAcentos.length(); // fim
                }
            }

            if (troca.length() > 0)

                strSaida += troca;
            else
                strSaida += str.substring(nk, nk + 1);
        }

        return strSaida;
    }

    //*************************************************
    //Busca Login/Senha do Agente - 11.04.2013
    //*************************************************
    public static String[] buscaLoginSenha(String decrypt, Context ctx) {
        String[] retorno = {"", ""};

        AgenteDAO agentedaow = new AgenteDAO(ctx);

        retorno = agentedaow.getLoginSenha(decrypt);

        agentedaow.close();

        return retorno;
    }

    public static byte[] hexStringToBytes(String hexString, int offset, int count) {
        if (null == hexString || offset < 0 || count < 2 || (offset + count) > hexString.length())
            return null;

        byte[] buffer = new byte[count >> 1];
        int stringLength = offset + count;
        int byteIndex = 0;
        for (int i = offset; i < stringLength; i++) {
            char ch = hexString.charAt(i);
            if (ch == ' ')
                continue;
            byte hex = isHexChar(ch);
            if (hex < 0)
                return null;
            int shift = (byteIndex % 2 == 1) ? 0 : 4;
            buffer[byteIndex >> 1] |= hex << shift;
            byteIndex++;
        }
        byteIndex = byteIndex >> 1;
        if (byteIndex > 0) {
            if (byteIndex < buffer.length) {
                byte[] newBuff = new byte[byteIndex];
                System.arraycopy(buffer, 0, newBuff, 0, byteIndex);
                buffer = null;
                return newBuff;
            }
        } else {
            buffer = null;
        }
        return buffer;
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null)
            return null;

        return hexStringToBytes(s, 0, s.length());
    }

    public static byte isHexChar(char ch) {
        if ('a' <= ch && ch <= 'f')
            return (byte) (ch - 'a' + 10);
        if ('A' <= ch && ch <= 'F')
            return (byte) (ch - 'A' + 10);
        if ('0' <= ch && ch <= '9')
            return (byte) (ch - '0');

        return -1;
    }

    public void EnviaBkp(Context ctx) {
        ParametroDAO pardao = new ParametroDAO(ctx);
        Cursor cpar = pardao.getParametros();
        String nomePastaBackup = "";
        try {
            nomePastaBackup = "/bkp_db_" + SimpleCrypto.decrypt(getInfo(), cpar.getString(cpar.getColumnIndex("orgaoautuador"))) + "_" + SimpleCrypto.decrypt(getInfo(), cpar.getString(cpar.getColumnIndex("seriepda")));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String seriepda = "";
        String orgao = "";
        try {
            seriepda = SimpleCrypto.decrypt(getInfo(), cpar.getString(cpar.getColumnIndex("seriepda")));
            orgao = SimpleCrypto.decrypt(getInfo(), cpar.getString(cpar.getColumnIndex("orgaoautuador")));
        } catch (Exception e) {
            // TODO: handle exception
        }
        String tabelasx[] =
                {
                        "agente_" + orgao + "_" + seriepda,
                        "logs_" + orgao + "_" + seriepda,
                        "ait_" + orgao + "_" + seriepda,
                        "aitenquadramento_" + orgao + "_" + seriepda,
                        "parametro_" + orgao + "_" + seriepda,
                        "foto_" + orgao + "_" + seriepda
                };

        String root = Environment.getExternalStorageDirectory() + nomePastaBackup;
        FTPClient ftp = new FTPClient();

        for (String tabela : tabelasx) {

            File fil = new File(root, tabela);
            if (fil.exists()) {
                //Faz a conexão com o servidor ftp
                try {


                    ftp.connect("sistemas.cobrasin.com.br");
                    ftp.login("androidcobra", "androidcobra2014");

                    // tenta criar a pasta fotos
                    try {
                        ftp.makeDirectory("bkp_bases");
                    } catch (Exception e) {

                    }

                    FileInputStream fis = new FileInputStream(root + "/" + tabela);

                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

                    boolean conseguiu = false;

                    if (ftp.storeFile("bkp_bases/" + tabela, fis)) {

                        // se conseguiu transmitir limpa log
                        conseguiu = true;
                        fis.close();
                        //fil.delete();
                    } else {
                        fis.close();
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
        try {
            ftp.disconnect();
            ftp.logout();
            ftp.disconnect();

        } catch (Exception e) {

        }
        LogDAO l = new LogDAO(ctx);
        List<Logs> lg = l.getLogs();
        int i = 1;
        for (Logs b : lg) {
            //try{
            //			informUsr("Transmitindo Logs: "+String.valueOf(i) +" de "+String.valueOf(lg.size()));
            //		txLogPost(String.valueOf(b.getId()),b.getPda(),b.getDataHora(),b.getOrgao(),b.getPda(),
            //			b.getOperacao(),b.getStatus());
            //}
            //catch(Exception e)
            //{
            l.DeleteLog(b.getId());
            //}
            i++;
        }
    }

    public static byte[] bitmapToByteArray(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        List<String> list = new ArrayList();
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        int i;
        if (zeroCount > 0) {
            for (i = 0; i < 8 - zeroCount; ++i) {
                zeroStr = zeroStr + "0";
            }
        }

        for (i = 0; i < bmpHeight; ++i) {
            StringBuffer sb = new StringBuffer();

            for (int j = 0; j < bmpWidth; ++j) {
                int color = bmp.getPixel(j, i);
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                if (r > 160 && g > 160 && b > 160) {
                    sb.append("0");
                } else {
                    sb.append("1");
                }
            }

            if (zeroCount > 0) {
                sb.append(zeroStr);
            }

            list.add(sb.toString());
        }

        List<String> bmpHexList = ConvertUtil.binaryListToHexStringList(list);
        String widthHexString = Integer.toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : bmpWidth / 8 + 1);
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", "width is too large");
            return null;
        } else {
            String heightHexString = Integer.toHexString(bmpHeight);
            if (heightHexString.length() > 2) {
                Log.e("decodeBitmap error", "height is too large");
                return null;
            } else {
                List<String> commandList = new ArrayList();
                commandList.addAll(bmpHexList);
                return ConvertUtil.hexList2Byte(commandList);
            }
        }
    }

    public static byte[] ByteArrayCodePrintImage(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        int i;
        if (zeroCount > 0) {
            for (i = 0; i < 8 - zeroCount; ++i) {
                zeroStr = zeroStr + "0";
            }
        }

        String commandHexString = "1B5831";
        String widthHexString = Integer.toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : bmpWidth / 8 + 1);
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", "width is too large");
            return null;
        } else {
            if (widthHexString.length() == 1) {
                widthHexString = "0" + widthHexString;
            }

            String heightHexString = Integer.toHexString(bmpHeight);
            if (heightHexString.length() > 2) {
                Log.e("decodeBitmap error", "height is too large");
                return null;
            } else {
                if (heightHexString.length() == 1) {
                    heightHexString = "0" + heightHexString;
                }
                List<String> commandList = new ArrayList();
                commandList.add(commandHexString + widthHexString + heightHexString);
                return ConvertUtil.hexList2Byte(commandList);
            }
        }
    }

}
	

