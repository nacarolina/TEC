package br.com.cobrasin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.dao.AitEnquadramentoDAO;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.EspecieDAO;
import br.com.cobrasin.dao.FotoDAO;
import br.com.cobrasin.dao.LogradouroDAO;
import br.com.cobrasin.dao.MedidaAdmDAO;
import br.com.cobrasin.dao.PaisDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.dao.TipoDAO;
import br.com.cobrasin.tabela.Enquadramento;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ExibeDadosAitAntesFechamentoPlest extends Activity {

	
	private String desclog;
	private String  ctiplog;	
	private String enquads;
	private String especie;
	private String tipo;
	private String medidaadm;
	private String pais;
	
    // Mostra String 
    private String exibe [] = new String[26];
	
    private String info = Utilitarios.getInfo();
	
	private long idAit ;
    
	
	private static final int INVISIBLE = 4;
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        String serieAit = "" ;
	        
	        setContentView(R.layout.exibeaitantesfecha);
	  
	        // pega o Id do AIT 
	        idAit =  (Long) getIntent().getSerializableExtra("idAit");
	     	
	        Button btRetorna = (Button) findViewById(R.id.btRetornaExibeAntesFecha);
	        btRetorna.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					setResult(RESULT_OK);
					finish();
				}
			});
	        
	        AitDAO aitdao = new AitDAO(ExibeDadosAitAntesFechamentoPlest.this);
	        Cursor c = aitdao.getAit(idAit);
	        aitdao.close();
	        
	        // Obtem    , Logradouro ,Especie, Tipo
	        
	        try {
		        LogradouroDAO logdao = new LogradouroDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        if (SimpleCrypto.decrypt(info,
						c.getString(c.getColumnIndex("logradouro2"))).contains(
						"NAO")) {
					desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouro"))));
				}
				else {
					desclog = logdao.buscaDescLog(SimpleCrypto.decrypt(info,
							c.getString(c.getColumnIndex("logradouro")))) + " X " + logdao.buscaDescLog(SimpleCrypto.decrypt(info,
									c.getString(c.getColumnIndex("logradouro2"))));
				}
					//desclog += " " + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradouronum")));
				
		        logdao.close();
		        
		        EspecieDAO espdao = new EspecieDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("especie"))));
		        espdao.close();
		        
		        TipoDAO tipdao = new TipoDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tipo"))));
		        tipdao.close();
		        
		        MedidaAdmDAO medidaadmdao = new MedidaAdmDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        medidaadm = medidaadmdao.buscaDescMed(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medidaadm"))));
		        medidaadmdao.close();
		        
		        PaisDAO paisdao = new PaisDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        pais = paisdao.buscaDescPais(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pais"))));
		        paisdao.close();

		        ParametroDAO pardao = new ParametroDAO(ExibeDadosAitAntesFechamentoPlest.this);
		        Cursor ch = pardao.getParametros();
		        c.moveToFirst();
				serieAit = ch.getString(c.getColumnIndex("serieait"));
				ch.close();
				pardao.close();
		        
	        } catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
	        
	        // pega tipo do logradouro
	        ctiplog = "NAO DEFINIDO";
	        
	        try
	        {
		        int nx  = Integer.parseInt(SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradourotipo"))));
		        
		        switch(nx)
		        {
		        case 1:
		        		ctiplog = "OPOSTO";
		        		break;
		        case 2:
		        		ctiplog = "DEFRONTE";
		        		break;
		        case 3:
		        		ctiplog = "AO LADO DE";
		        		break;
		        };
	        
		        // Mostra String 
		        //String exibe [] = new String[24];

		        ParametroDAO pardao = new ParametroDAO(this);
		        
		        Cursor ci = pardao.getParametros();
		        
		        // mostra próximo AIT
		        exibe[0]	= "AIT:" +  SimpleCrypto.decrypt(info, serieAit + ci.getString(ci.getColumnIndex("proximoait")));
		        
		        pardao.close();
		        
		        exibe[1]  	= "AGENTE:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("agente")));
		        exibe[2]  	= "FLAG:" +  c.getString(c.getColumnIndex("flag"));
		        exibe[3]  	= "PLACA:" +  SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("placa")));
		        exibe[4]  	= "DATA-HORA LAVRATURA:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("data"))) + "-" +SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("hora")));

		        exibe[5]	= "PAIS:" + pais;
		        
		        exibe[6]	= "MARCA:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("marca")));
		        exibe[7] 	= "ESPECIE:" + especie ; //c.getString(8);
		        exibe[8]  	= "TIPO:" + tipo;//c.getString(9);
		        exibe[9]  	= "LOGRADOURO:" + desclog;// c.getString(10);
		        exibe[10]  	= "NUMERO:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("logradouronum")));
		        exibe[11]  	= "TIPO:" + ctiplog;//c.getString(12);
		        exibe[12] 	= "NOME:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("nome")));
		        exibe[13] 	= "CPFE:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("cpf")));
		        exibe[14] 	= "PGU:" +SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("pgu")));
		        exibe[15] 	= "UF:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("uf")));
		        exibe[16] 	= "OBS:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("observacoes")));
		        exibe[17] 	= "SERIEPDA:" +  SimpleCrypto.decrypt(info, ci.getString(ci.getColumnIndex("seriepda")));
		        exibe[18]   = "MEDIDA ADM:" + medidaadm;
		        
		        exibe[20]   = "EQUIPAMENTO:" +SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("equipamento")));
		        exibe[21]   = "MEDICAO REGISTRADA:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medicaoreg")));
		        exibe[22]   = "MEDICAO CONSIDERADA:" +  SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("medicaocon")));
		        exibe[23]   = "LIMITE REGULAMENTADO:" +    SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("limitereg")));
		        
		        ci.close();
		        
	        }
	        catch( Exception e)
	        {
	        	
	        }
	        
	        AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(ExibeDadosAitAntesFechamentoPlest.this);
	        Cursor c1 = aitenq.getLista1(idAit);
	        
	        enquads = " ";
	        while (c1.moveToNext())
	        {
	        	//enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";
	        	
				EnquadramentoDAO dao = new EnquadramentoDAO(this);
				List<Enquadramento> enquadramento = null;
				try {
					enquadramento = dao.getLista(SimpleCrypto.decrypt(info, c1.getString(c1.getColumnIndex("codigo"))),ExibeDadosAitAntesFechamentoPlest.this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dao.close();

				enquads += enquadramento.get(0).toString() +  " / ";
	        	
	        }
	        
	        c1.close();
	        
	        exibe[19] 	= "ENQUADRAMENTOS:" + enquads;
	        try {
				exibe[24] 	= "DATA MODIFICADA:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("dtEdit")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exibe[24] 	= "DATA MODIFICADA:";
			}
	        try {
				exibe[25] 	= "HORA MODIFICADA:" + SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("hrEdit")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exibe[25] 	= "HORA MODIFICADA:";
			}
		        
	        ListView exibeait = (ListView) findViewById(R.id.listExibeAit);
	        
	        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exibe);
	        exibeait.setAdapter(adapter1);
	        
	        c.close();
	        
	        Button btMostraFotos = (Button) findViewById(R.id.btFotosAntesFech);
	        
	        btMostraFotos.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					FotoDAO fotodao = new FotoDAO(getBaseContext());
					
					if (fotodao.getQtde(idAit) > 0 )
					{
						fotodao.close();
						
						mostrafoto();
						
						
					}
					else
					{
						Toast.makeText(getBaseContext(), "Sem fotos para exibir", Toast.LENGTH_SHORT).show();
						fotodao.close();
					}
					
				}
			});
	        
	 }

	 protected void mostrafoto() {
			// TODO Auto-generated method stub
			
			Intent  i = new Intent(this,MostraFotos.class);
			i.putExtra("idAit", idAit);
			startActivity(i);
			
		}

	 
}