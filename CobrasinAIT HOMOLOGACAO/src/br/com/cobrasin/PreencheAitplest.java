package br.com.cobrasin;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import br.com.cobrasin.tabela.Enquadramento;
import br.com.cobrasin.tabela.Parametro;
import br.com.cobrasin.ListaEspecie;
import android.app.Activity;
import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PreencheAitplest extends Activity  {

	String salvaAgente = "";
	Ait aitPendente ; 
	TrataPlacaEst edPlaca;
	TrataMarca edMarca;
	EditText edData ;
	EditText edHora ;
	LogDAO  l = new LogDAO(PreencheAitplest.this);
	 
	
	private int  chamaactivity = 101 ;
	private int	 chamafoto = 102;
	private boolean passou = false ; 
	
	private String  logradouroGps;

	private String info = Utilitarios.getInfo();

	private String arquivofoto ;
	private String OrgA;
	private String Pda;
    private String agente;
	private String PlacaDetectada = "";
	
	private String MarcaModeloDetectada = "";
	/** Called when the activity is first created. */

	Button btPesquisa,btEspecie,btTipo,btLogradouro,btEnquadramento,btObservacoes,btDadosInfrator,btVisualiza,
	btPais,btCancelaAit,btInfrEquip,btMedidaAdm,btFotografa,btFecha;
	private String desclog;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (btPesquisa.isEnabled() == true) {
			
		}
		else 
		{
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) 
	       {
	    	 AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
		        aviso.setIcon(android.R.drawable.ic_dialog_alert);
		        aviso.setTitle("Cancelamento de AIT");
		        aviso.setMessage(" Deseja realmente ir para a tela de Cancelamento ?");
		        aviso.setNeutralButton("Não",null);
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
	
	private void chama(int n ) {
		
		// grava a marca na tabela
		Ait aitx = new Ait();
		aitx.setId(aitPendente.getId());
		
		//*********************************************
	  	// 27.06.2012 - alteração : remover acentos
	    //*********************************************
		aitx.setMarca( Utilitarios.removeAcentos(edMarca.getText().toString()));
			
		if ( edMarca.getText().length() > 0 )
		{
			AitDAO aitdao = new AitDAO(getBaseContext());
			aitdao.gravaMarca(aitx);
			aitdao.close();
		}
		
		Intent i = null ;
		switch (n)
		{
		
			case 1:
			{
				this.btEspecie.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaEspecie.class);
				i.putExtra("selespecie", aitPendente.getEspecie());
				break;
			}
		
			case 2:
			{
				this.btTipo.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaTipo.class);
				i.putExtra("seltipo",aitPendente.getTipo());
				break;
			}
			
			case 3:
			{
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
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case 4:
			{
				this.btEnquadramento.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaEnquadramento.class);
				i.putExtra("agente",salvaAgente);
				break;
			}
			case 5:
			{
				this.btObservacoes.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaObservacoes.class);
				i.putExtra("obsgravada",aitPendente.getObservacoes());
				break;
			}
			case 6:
			{
				AitDAO aitdao = new AitDAO(PreencheAitplest.this);
				Cursor c = aitdao.getAit(aitPendente.getId());
				this.btDadosInfrator.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, OrigemInfrator.class);
				try {
			    i.putExtra("nome",SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("nome"))));
				i.putExtra("cpf",SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("cpf"))));
				i.putExtra("pgu",SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pgu"))));
				i.putExtra("uf",SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("uf"))));
				i.putExtra("passaporte", SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("passaporte"))));
				i.putExtra("pid", SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pid"))));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case 7:
			{
				i = new Intent(this, ExibeDadosAitAntesFechamentoPlest.class);
				break;
			}
			
			case 8:
			{
				i = new Intent(this, ListaMedidaAdm.class);
				this.btMedidaAdm.setTypeface(Typeface.DEFAULT_BOLD);
				i.putExtra("selmedidaadm", aitPendente.getMedidaadm());
				break;
			}
			case 9:
			{
				this.btPais.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaPais.class);
				i.putExtra("selpais", aitPendente.getPais());
				break;
			}
			case 10:
			{
				this.btInfrEquip.setTypeface(Typeface.DEFAULT_BOLD);
				i = new Intent(this, ListaInfrEquipamento.class);
				i.putExtra("equipamento", aitPendente.getEquipamento());
				i.putExtra("medicaoreg",aitPendente.getMedicaoreg());
				i.putExtra("medicaocon",aitPendente.getMedicaocon());
				i.putExtra("limitereg",aitPendente.getLimitereg());
				break;
			}
			
			
			
		}
		i.putExtra("idAit", aitPendente.getId());
		startActivityForResult(i, chamaactivity);
		
	}

	private void criaAit(String placa )
	{
		
		Ait ait = new Ait();
		
		try {
			//Locale locale = new Locale("pt","BR"); 

			ait.setAit(SimpleCrypto.encrypt(info,"XXXX"));
			ait.setFlag("A");
			ait.setAgente(SimpleCrypto.encrypt(info,salvaAgente));
			ait.setPlaca(SimpleCrypto.encrypt(info,placa));
			ait.setLogradouro2(SimpleCrypto.encrypt(info, "NAO"));
			ait.setData(SimpleCrypto.encrypt(info,Utilitarios.getDataHora(2)));
			ait.setHora(SimpleCrypto.encrypt(info,Utilitarios.getDataHora(3)));
			
			//ait.setData(SimpleCrypto.encrypt(info,new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis()))));
			//ait.setHora(SimpleCrypto.encrypt(info,new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis()))));
			
			ait.sethrEdit(edHora.getText().toString());
			ait.setdtEdit(edData.getText().toString());
			
			ait.setMarca(SimpleCrypto.encrypt(info,edMarca.getText().toString()));
			ait.setEspecie(SimpleCrypto.encrypt(info,"01"));
			ait.setTipo(SimpleCrypto.encrypt(info,"06"));
			ait.setLogradouro(SimpleCrypto.encrypt(info,"00000"));
			ait.setLogradouronum(SimpleCrypto.encrypt(info,""));
			ait.setLogradourotipo(SimpleCrypto.encrypt(info,"0"));
			ait.setNome(SimpleCrypto.encrypt(info,"" ));
			ait.setCpf(SimpleCrypto.encrypt(info,""));
			ait.setPgu(SimpleCrypto.encrypt(info,""));
			ait.setUf(SimpleCrypto.encrypt(info,""));
			ait.setObservacoes(SimpleCrypto.encrypt(info,""));
			ait.setMedidaadm(SimpleCrypto.encrypt(info,"1"));
			ait.setTipoait(SimpleCrypto.encrypt(info,"4"));
			
			ait.setPais(SimpleCrypto.encrypt(info,""));
			
			ait.setEquipamento(SimpleCrypto.encrypt(info,""));
			ait.setMedicaoreg(SimpleCrypto.encrypt(info,""));
			ait.setMedicaocon(SimpleCrypto.encrypt(info,""));
			ait.setLimitereg(SimpleCrypto.encrypt(info,""));
			ait.setSendPdf(SimpleCrypto.encrypt(info,"NAO"));
			ait.setIdWebTrans((long) 0);
			  ParametroDAO pardao = new ParametroDAO(getBaseContext());
			   
			   Cursor c = pardao.getParametros();
			   
			   c.moveToFirst();
			   
			   // dados para comunicação com o WebTrans
			      String usuarioWebTrans =SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("usuariowebtrans")));
			      String senhaWebTrans = c.getString(c.getColumnIndex("senhawebtrans"));
			      String codMunicipio = SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("orgaoautuador"))).subSequence(1, 5).toString();  //265810
			      OrgA= SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("orgaoautuador")));
			      Pda=SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("seriepda")));
			      agente=salvaAgente;
			      c.close();
			   
			   pardao.close();
			   
			   ///Grava o log da criação do ait
			           try {
			     l.gravalog("Inicio de criação de AIT placa " + placa, "INSERT",
			       OrgA,Pda,salvaAgente,PreencheAitplest.this);
			    } catch (Exception e) {
			     // TODO Auto-generated catch block
			     e.printStackTrace();
			    }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  try {
				     l.gravalog("Erro criação de AIT- "+e.getMessage().replace(".", "-").replace(":", "-"), "Erro",
				       OrgA,Pda,salvaAgente,PreencheAitplest.this);
				    } catch (Exception ex) 
				    {
	
				    }
		}
	
		
		try {
			ait.setImpresso(SimpleCrypto.encrypt(info,"NAO"));
			ait.setTransmitido(SimpleCrypto.encrypt(info,"NAO"));
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
			aitPendente.setAit(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("ait"))));
			aitPendente.setFlag(cursor.getString(cursor.getColumnIndex("flag")));
			aitPendente.setAgente(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("agente")))); 
			aitPendente.setPlaca(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("placa")))); 
			aitPendente.setData(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("data")))); 
			aitPendente.setHora(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("hora")))); 
			aitPendente.setMarca(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("marca"))));
			aitPendente.setEspecie(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("especie"))));		
			aitPendente.setTipo(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("tipo"))));
			aitPendente.setLogradouro(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("logradouro"))));
			aitPendente.setLogradouronum(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("logradouronum"))));
			aitPendente.setLogradourotipo(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("logradourotipo"))));
			aitPendente.setNome(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("nome"))));
			aitPendente.setCpf(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("cpf"))));
			aitPendente.setPgu(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("pgu"))));
			aitPendente.setUf(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("uf"))));
			aitPendente.setObservacoes(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("observacoes"))));
			aitPendente.setImpresso(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("impresso"))));
			aitPendente.setTransmitido(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("transmitido"))));
			aitPendente.setMedidaadm(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("medidaadm"))));
			aitPendente.setTipoait(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("tipoait"))));
			aitPendente.setPais(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("pais"))));
			
			aitPendente.setdtEdit(edData.getText().toString());
			aitPendente.sethrEdit(edHora.getText().toString());
			
			aitPendente.setEquipamento(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("equipamento"))));
			aitPendente.setMedicaoreg(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("medicaoreg"))));
			aitPendente.setMedicaocon(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("medicaocon"))));
			aitPendente.setLimitereg(SimpleCrypto.decrypt(info,cursor.getString(cursor.getColumnIndex("limitereg"))));
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void confirmaFechamentoAit()
	{
	    AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
        aviso.setIcon(android.R.drawable.ic_dialog_alert);
        aviso.setTitle("Fechamento de AIT");
        aviso.setMessage("Confirma ?");
        aviso.setNeutralButton("Não",null);
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
				aitx.setMarca( Utilitarios.removeAcentos(edMarca.getText().toString().trim()));
				aitPendente.setMarca(Utilitarios.removeAcentos(edMarca.getText().toString().trim()));
				
				AitDAO aitdao = new AitDAO(getBaseContext());
				aitdao.gravaMarca(aitx);
				aitdao.close();
				
				
				fechaAit();
			}
		});
        
        aviso.show();
	
	}
	
	private String processafechamento(String encerramento)
	{
		//
		// muda o status do AIT 
		// grava o campo AIT 
		ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
		Cursor cz = pardao.getParametros();
		
		Parametro param = new Parametro();
		  try {  
				param.setProximoait(SimpleCrypto.decrypt(info,cz.getString(0)));
				param.setSeriepda(SimpleCrypto.decrypt(info,cz.getString(cz.getColumnIndex("seriepda"))));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		// Atencao: Todos os dados já foram criptografados...
		
		Ait aitz = new Ait();
		aitz.setId(aitPendente.getId());		// id
		aitz.setFlag("F");						// flag
		aitz.setAit(param.getProximoait());		// numero do ait
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
		String aitClosed=String.valueOf(prox) ;
		prox++;  
		
		String formatado = String.format("%07d", prox); 
		param.setProximoait(formatado);
		  try {
				param.setSeriepda(SimpleCrypto.decrypt(info,cz.getString(cz.getColumnIndex("seriepda"))));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		pardao.gravaParam(param);
		pardao.close();
		
		cz.close();
		  return aitClosed;
	}
	
	private String VerificaDtHr(String dt, String hr)
	{
		String ret="ok";
		String Data = dt.toString();
		String Hora = hr.toString();
	//	int Retorno;
		if (Data.contains(".")) {
			Data = Data.replace('.','/');	
		}
		if (Data.contains("-"))
		{
			Data = Data.replace('-','/');	
		}
		
		
			Utilitarios u = new Utilitarios();
			//AitDAO aitdao = new AitDAO(EditDataHora.this);
			//Cursor c = aitdao.getAit(idAit);
			if (u.calculaDias(dt+" "+hr)>=1)
				ret="false";
			else
				ret="true";
			
			
		return ret;
	}
	// verifica as condicoes para o fechamento do AIT 
	private void fechaAit()
	{
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
		String mensagem ="Erros:\n\n";

		if (VerificaDtHr(edData.getText().toString(), edHora.getText().toString())=="false")
		{
			errodt= true ;
			mensagem += "Data-hora com diferênça maior de 24hrs não é permitido!\n";
		}

		String DataAtualString = Utilitarios.getDataHora(2).replace("/", "");
		String HoraAtualString = Utilitarios.getDataHora(3).replace(":","");
		
		String DataEditadaString = edData.getText().toString().replace("/", "");
		String HoraEditadaString = edHora.getText().toString().replace(":","");
		
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
		if (marca.length()== 0) 
		{
			erromarca = true ;
			mensagem += "Marca não preenchida\n";
		}

		
		//---------------------------------------------------------
		// testa enquadramento
		//---------------------------------------------------------
		AitEnquadramentoDAO enqdao = new AitEnquadramentoDAO(getBaseContext());

		if (enqdao.qtdeEnquad( aitPendente.getId()) == 0 )
		{
			erroenquad = true;
			mensagem += "Enquadramento(s) não cadastrado(s)\n";
		}
		enqdao.close();
		
		//---------------------------------------------------------
		// testa codigo do local
		//---------------------------------------------------------
		String codlog = aitPendente.getLogradouro();
		if (codlog.equals("00000"))
		{
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
		
		if ( pais.length()==0)
		{
			erropais =  true ;
			
			mensagem += "País não selecionado ! \n";
		}
		
		
		if (erroenquad || erromarca || errolog || erronumlog || erropais || errodt)  
		{
			// informa usuario dos erros no ait

			
            AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
            aviso.setIcon(android.R.drawable.ic_dialog_alert);
            aviso.setTitle("Fechamento de AIT");
            aviso.setMessage(mensagem);
            aviso.setNeutralButton("OK",null);
            aviso.show();
			
		}
		else
		{
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
			
			AitDAO a=new AitDAO(PreencheAitplest.this);
			aitPendente.setSendPdf("FALSE");
			a.gravaSendPdf(aitPendente);
			
			// encerra o 1o. AIT
			String Ait = processafechamento(encerramento);	
			try {
			    l.gravalog("Finalizou AIT- " +Ait, "INSERT",OrgA,Pda,agente,PreencheAitplest.this);
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
	    	
		    while (c1.isAfterLast() == false )
		    {
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
		    	aitPendente.setLimitereg(cultimoAit.getString(cultimoAit.getColumnIndex("limitereg")));
		    	
		    	aitPendente.setAit("xxxx");
		    	try {

		    		aitPendente.setImpresso(SimpleCrypto.encrypt(info,"NAO"));
		    		aitPendente.setTransmitido(SimpleCrypto.encrypt(info,"NAO"));
			    	aitPendente.setCancelou(SimpleCrypto.encrypt(info,"NAO"));
			    	aitPendente.setMotivo(SimpleCrypto.encrypt(info," "));
			    	
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
					daoenq.Insere(idAitNovo, SimpleCrypto.decrypt(info,c1.getString(c1.getColumnIndex("codigo"))));
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
		    String GeraPdf="";
		    ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
		    Cursor c = pardao.getParametros();
		    try {
				GeraPdf=SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("modpdf")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if (GeraPdf.contains("TRUE")) {
		    	montaimpressao(aitPendente.getId());	
			}  
		    c.close();
		    Intent i = new Intent(this, ListaAit.class);
		    i.putExtra("agente", salvaAgente);
		    startActivity(i);
		    //startActivity(new Intent(this,ListaAit.class));
			finish(); // encerra esta activity
			
		}
		
	}

	private void montaimpressao(long idAit)
	 {
		 	
   		
			//String impressora ="00:08:1B:95:6B:AF";
		    
		   AitDAO aitdao = new AitDAO(PreencheAitplest.this);
	        Cursor c = aitdao.getAit(idAit);

	        // grava data e hora do envio para a impressora
	        aitdao.atualizaImpressao(idAit,c);
	        aitdao.close();
	        
	        ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
			Cursor cpar = pardao.getParametros();
			pardao.close();
			
			String cancelou = "";
			String ativo = "";
			String medidaadm = "";
			String tipoinfrator = "";
			String mens = "";
			String saida ="";
			
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
				try 
				{			
					impressora  =  SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("impressoraMAC")));
					ativo =  SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("impressoraPatrimonio"))).toUpperCase();
					// Obtem    , Logradouro ,Especie, Tipo
			       
			        
			        EspecieDAO espdao = new EspecieDAO(PreencheAitplest.this);
			        especie  = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("especie"))));
			        espdao.close();
			        
			        TipoDAO tipdao = new TipoDAO(PreencheAitplest.this);
			        tipo  = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipo"))));
			        tipdao.close();
			        
			        
			        MedidaAdmDAO medidadao  = new MedidaAdmDAO(PreencheAitplest.this);
			        medidaadm = medidadao.buscaDescMed(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("medidaadm"))));
			        medidadao.close();
			        
			        // pega tipo do logradouro
			        String ctiplog = " ";
			        
			        int nx  = Integer.parseInt(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradourotipo"))));
				        
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
				    saida = "" ; 
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
					
					saida += SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("prefeitura"))) + String.format("\r\n");
					saida += "Orgao Autuador:" + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("orgaoautuador"))) + String.format("\r\n");
					
					saida += "------------------------"+ String.format("\r\n");
					saida += "   Dados da Infracao"+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					
					saida += "Ait:"  + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("serieait"))) + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("ait")))+ String.format("\r\n");
					saida += "Placa:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("placa")))+ String.format("\r\n");
					saida += "Marca:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("marca")))+ String.format("\r\n");
					
                       PaisDAO paisDao = new PaisDAO(PreencheAitplest.this);
                       String Pais = paisDao.buscaDescPais(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pais"))));
                       if(Pais !="")
                       {
                       saida += "Pais:" + Pais + String.format("\r\n");
                       }
                       
					saida += "Especie:" + especie +  String.format("\r\n");
					saida += "Tipo:" + tipo +  String.format("\r\n");
					saida += "Data:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))) + "-" +SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora")))+ String.format("\r\n");
					 saida += "Equipamento:" + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("seriepda"))) + String.format("\r\n");
					saida += ""+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					saida += "   Local da Infracao "+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
		
					saida +=desclog+ String.format("\r\n");
					//saida += this.ctiplog+ String.format("\r\n");
					
					saida += ""+ String.format("\r\n");
					
					
			        AitEnquadramentoDAO aitenq = new AitEnquadramentoDAO(PreencheAitplest.this);
			        Cursor c1 = aitenq.getLista1(idAit);
			        
			        String enquads = " ";
			        c1.moveToNext();
			        
			        //enquads += c1.getString(c1.getColumnIndex("codigo")) + " ";
			        	
					EnquadramentoDAO dao = new EnquadramentoDAO(PreencheAitplest.this);
					List<Enquadramento> enquadramento = dao.getLista(SimpleCrypto.decrypt(info,c1.getString(c1.getColumnIndex("codigo"))),PreencheAitplest.this);
					dao.close();
		
					enquads += enquadramento.get(0).toString();
			       
					//enquads = Utilitarios.quebraLinha(enquads);
			        
			        c1.close();
			        
					saida += "------------------------"+ String.format("\r\n");
					saida += "    Enquadramento" +  String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					saida +=  enquads + String.format("\r\n");
					
					saida += ""+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					saida += "  Identif. do Infrator  " + String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					
					try {
						 tipoinfrator = SimpleCrypto.decrypt(info, c.getString(c.getColumnIndex("tipoinfrator")));
					} catch (Exception e) {
						// TODO: handle exception
					}
				        if (tipoinfrator == null) {
				        	saida += "Nome:" +  SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("nome")))+ String.format("\r\n");
							saida += "CPF:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("cpf")))+ String.format("\r\n");				
							saida += "PGU:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pgu")))+ " " + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");
						}
				        else {
					        if (tipoinfrator.contains("CNH")) {
					        	saida += "Nome:" +  SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("nome")))+ String.format("\r\n");
								saida += "CPF:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("cpf")))+ String.format("\r\n");				
								saida += "PGU:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pgu")))+ " " + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");
						       
							}
					        if (tipoinfrator.contains("PID")) {		        
						        saida += "Nome:" +  SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("nome")))+ String.format("\r\n");
								saida += "Doc. de Ident.:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("passaporte")))+ String.format("\r\n");				
								saida += "Pid:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("pid")))+ " " + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("uf"))) + String.format("\r\n");
							}	
						}
					
					saida += ""+ String.format("\r\n");
					saida += "________________________"+ String.format("\r\n");
					saida += "      Assinatura"+ String.format("\r\n");
					//saida += "CPF:" + c.getString(c.getColumnIndex("uf"))+ String.format("\r\n");
					
					saida += ""+ String.format("\r\n");
					saida += "-----------------------"+ String.format("\r\n");
					saida += "Identificacao do Agente" + String.format("\r\n");
					saida += "-----------------------"+ String.format("\r\n");
					saida += "Matric.(AG):" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("agente"))) + String.format("\r\n");
					
					saida += ""+ String.format("\r\n");
					saida += "________________________"+ String.format("\r\n");
					saida += "     Lavrado por"+ String.format("\r\n");
					
					
					if (SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("observacoes"))).length() > 0)
					{
				
						if ( SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("imprimeobs"))).contains("1"))
						{
							saida +=String.format("\r\n");
							saida += "------------------------"+ String.format("\r\n");	
							saida += "Observacoes:"+ String.format("\r\n");
							saida += SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("observacoes"))) + String.format("\r\n");;
							
						}
					
					}
					
					//***********************************************
					// 28.07.2012 
					//
					// imprime medida administrativa se foi definida
					//***********************************************					
					if ( !medidaadm.contains(("Nao definido")))
					{
						saida +=String.format("\r\n");
						saida += "------------------------"+ String.format("\r\n");	
						saida += "Medida Administrativa:"+ String.format("\r\n");
						saida += medidaadm + String.format("\r\n");

					}
				
					//**************************************************************************************
					// 08.03.2012
					// Preencheu dados equipamento, exemplo decibelímetro ?
					if (SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("equipamento"))).length() > 0)
					{
						saida+=String.format("\r\n");
						saida+="Equipamento:" + String.format("\r\n");
						saida+= SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("equipamento"))) + String.format("\r\n");
						saida+="Medicao Registrada:"+ String.format("\r\n");
						saida+= SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("medicaoreg"))) + String.format("\r\n");
						saida+="Medicao Considerada:" +String.format("\r\n");
						saida+=SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("medicaocon")))+String.format("\r\n");
						saida+="Limite Regulamentado:" +String.format("\r\n");
						saida+=SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("limitereg")))+String.format("\r\n");
					
					}
					
					
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					saida += "E obrigatoria a presenca"+ String.format("\r\n");
					saida += "do codigo INFRAEST ou"+ String.format("\r\n");
					saida += "RENAINF nas notificacoes" + String.format("\r\n");
					saida += "sob pena de invalidade"+ String.format("\r\n");
					saida += "da multa."+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");					
					
		       	
					mens=saida;
					Ait ait = new Ait();
	    			ait.setImpressao(mens);
	    			ait.setId(aitPendente.getId());
	    			AitDAO aitdao2 = new AitDAO(PreencheAitplest.this);
	    			aitdao2.gravaImpressao(ait);
	    			aitdao2.close();
	    			
	    			BkpMultaPdfDAO BkpMulta = new BkpMultaPdfDAO(PreencheAitplest.this);
					BkpMulta.SalvaMulta(aitPendente.getAit(), mens);
					
	    			LogDAO l=new LogDAO(PreencheAitplest.this);
				 	try {
				 		l.gravalog("Gerou Impressão AIT - "+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("ait"))), "INSERT",
								SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("orgaoautuador"))), SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("seriepda"))),salvaAgente,
								PreencheAitplest.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 	c.close();
				 	cpar.close();
				} 
				catch (Exception e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
			else 
			{
				try {
					
					impressora =  SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("impressoraMAC")));
					ativo =  SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("impressoraPatrimonio"))).toUpperCase();
					// Obtem    , Logradouro ,Especie, Tipo
					int nx  = Integer.parseInt(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("logradourotipo"))));
			        String ctiplog = "";
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
			        especie = espdao.buscaDescEsp(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("especie"))));
			        espdao.close();
			        
			        TipoDAO tipdao = new TipoDAO(PreencheAitplest.this);
			        tipo = tipdao.buscaDescTip(SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("tipo"))));
			        tipdao.close();
			        
					
				    saida = "" ; 
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
					
					saida += SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("prefeitura"))) + String.format("\r\n");
					saida += "Orgao Autuador:" + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("orgaoautuador"))) + String.format("\r\n");
					
					saida += "------------------------"+ String.format("\r\n");
					saida += "   Dados da Infracao"+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					
					saida += "Ait:"  + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("serieait"))) + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("ait")))+ String.format("\r\n");
					saida += "Placa:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("placa")))+ String.format("\r\n");
					saida += "Marca:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("marca")))+ String.format("\r\n");
					saida += "Especie:" + especie +  String.format("\r\n");
					saida += "Tipo:" + tipo +  String.format("\r\n");
					saida += "Data:" + SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("data"))) + "-" +SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("hora")))+ String.format("\r\n");
					 saida += "Equipamento:" + SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("seriepda"))) + String.format("\r\n");
					saida += String.format("\r\n");
					
					saida += "------------------------"+ String.format("\r\n");
					saida += " Dados do Cancelamento"+ String.format("\r\n");
					saida += "------------------------"+ String.format("\r\n");
					
					saida += cancelou+String.format("\r\n");
					saida += "Motivo: "+ SimpleCrypto.decrypt(info,c.getString(c.getColumnIndex("motivo")))+ String.format("\r\n");
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");
					saida +=String.format("\r\n");	
					mens=saida;
					
					Ait ait = new Ait();
	    			ait.setImpressao(mens);
	    			ait.setId(aitPendente.getId());
	    			AitDAO aitdao2 = new AitDAO(PreencheAitplest.this);
	    			aitdao2.gravaImpressao(ait);
	    			aitdao2.close();
	    			
					BkpMultaPdfDAO BkpMulta = new BkpMultaPdfDAO(PreencheAitplest.this);
					BkpMulta.SalvaMulta(aitPendente.getAit(), mens);
					
	    			LogDAO l=new LogDAO(PreencheAitplest.this);
				 	try {
				 		l.gravalog("Gerou Impressão AIT - "+ SimpleCrypto.decrypt(info,
								c.getString(c.getColumnIndex("ait"))), "INSERT",
								SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("orgaoautuador"))), SimpleCrypto.decrypt(info,cpar.getString(cpar.getColumnIndex("seriepda"))),salvaAgente,
								PreencheAitplest.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 	c.close();
					} 
					catch (Exception e)
					{
						mens=saida;
						// TODO: handle exception
					}
			}
			
	 }
		
	

	//********************************************************
	// vai para a tela de cancelamento e nao mais retorna
	//********************************************************
	private void chamaTelaCan()
	{
		Ait aitx = new Ait();
		aitx.setId(aitPendente.getId());
		
		
		aitx.setdtEdit(edData.getText().toString());
		aitx.sethrEdit(edHora.getText().toString());
		
		AitDAO aitdao = new AitDAO(getBaseContext());
		aitdao.gravaDtEdit(aitx);
		aitdao.gravaHrEdit(aitx);
		
		Intent i = new Intent(this,CancelaAit.class);
	    i.putExtra("idAit", aitPendente.getId());
	    i.putExtra("agente", aitPendente.getAgente());
	    startActivity(i);
	    
		finish();
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        Context contexto = null;
        
        passou = true ; // indica para onResume que está na activity 
        PlacaDetectada = (String) getIntent().getSerializableExtra("PlacaDetectada");

        MarcaModeloDetectada = (String) getIntent().getSerializableExtra(
				"MarcaModeloDetectada");
		
        salvaAgente = (String) getIntent().getSerializableExtra("agente");
        
		setContentView(R.layout.preencheplest);
       
		edPlaca = (TrataPlacaEst) findViewById(R.id.txtConsultarPlaca);
		edPlaca.setMaxLines(1);
		edPlaca.setEnabled(false);
		edPlaca.setText(PlacaDetectada);
		
		
		edMarca = ( TrataMarca ) findViewById(R.id.EdMarca);
		edMarca.setMaxLines(1);
		edMarca.setEnabled(false);
		
		edMarca.setText(MarcaModeloDetectada);
		
		edData = (EditText) findViewById(R.id.edData);
		edData.setEnabled(true);
		edHora = (EditText) findViewById(R.id.edHora);
		edHora.setEnabled(true);
		
		
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
		
		btCancelaAit = ( Button )  findViewById(R.id.btCancelaAitP);
		btCancelaAit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		
			    AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
		        aviso.setIcon(android.R.drawable.ic_dialog_alert);
		        aviso.setTitle("Cancelamento de AIT");
		        aviso.setMessage(" Deseja realmente ir para a tela de Cancelamento ?");
		        aviso.setNeutralButton("Não",null);
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
		
		
		btFotografa = ( Button )  findViewById(R.id.btFotografa);
		btFotografa.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fotografa(aitPendente.getId());
			}
		});
		btFotografa.setEnabled(false);
		
		btFecha = ( Button )  findViewById(R.id.btFinaliza);
		btFecha.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				//fechaAit();
				confirmaFechamentoAit();
			}
		});
		btFecha.setEnabled(false);
		
		btEspecie = ( Button )  findViewById(R.id.btEspecie);
		btEspecie.setEnabled(false);
		btEspecie.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				chama(1);
			}
		})	;
		
		btTipo = ( Button )  findViewById(R.id.btTipo);
		btTipo.setEnabled(false);
		btTipo.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				chama(2);
			}
		})	;
		
		
		btLogradouro = ( Button )  findViewById(R.id.btLogradouro);
		btLogradouro.setEnabled(false);
		btLogradouro .setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				chama(3);
			}
		})	;
		
		btEnquadramento = ( Button ) findViewById(R.id.btEnquadramento);
		btEnquadramento.setEnabled(false);
		btEnquadramento.setOnClickListener( new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
								
				chama(4);
			}
		})	;
		
		
		
		
		btObservacoes = ( Button ) findViewById(R.id.btObservacoes);
		btObservacoes.setEnabled(false);
		
		btObservacoes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				chama(5);
			}
		});
		
		btDadosInfrator = ( Button ) findViewById(R.id.btDadosInfrator);
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
		
		btPesquisa = (Button) findViewById(R.id.btPesquisa);
		
		// Existe ait em edição ? 
		AitDAO aitdao = new AitDAO(getBaseContext());
		Cursor c = null;
		try {
			c = aitdao.aitAberta(SimpleCrypto.encrypt(info, salvaAgente));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (( c != null ) && (c.getCount() > 0  )) 
		{
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
			btPais.setEnabled(true);
			
			
			// Pega dados do AIT aberto
			editaAit(c);
			
			edMarca.setText(aitPendente.getMarca());
			edMarca.setFocusable(true);
			
			edPlaca.setText(aitPendente.getPlaca());
			edPlaca.setEnabled(false);
		}
		else
		{
			// habilita a placa para digitação
			edPlaca.setEnabled(true);
		
			//edData.setText(new SimpleDateFormat("dd/MM/yyyy").format( new Date(System.currentTimeMillis())));
			//edHora.setText(new SimpleDateFormat("hh:mm:ss").format( new Date(System.currentTimeMillis())));
			
			btPesquisa.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					
							if ((edPlaca.getText().toString().length() > 0 ) && (edPlaca.getText().toString().length() <= 10 ))
							{
									confirmaCriacaoAit();
							}
							else
							{
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

 
    private void confirmaCriacaoAit()
	{
	    AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
        aviso.setIcon(android.R.drawable.ic_dialog_alert);
        aviso.setTitle("Criação de AIT");
        aviso.setMessage("Confirma ?");
        aviso.setNeutralButton("Não",null);
        aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			
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
				btPais.setEnabled(true);
					
				// indica a data e hora
				edData.setText(aitPendente.getData());
				edHora.setText(aitPendente.getHora());
			}
		});
        
        aviso.show();
	
	}
    protected void onResume(){
		super.onResume();
		 
		
		// voltou de uma activity do sistema ?
		if ( passou )
		{
			
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
			if (( cx != null) &&  (cx.getCount() > 0 ))
			{
				
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
			
		}
		else
		{
			// ja preencheu a placa ? 
			if (aitPendente != null )
			{
				// pede o cancelamento...
				Intent i = new Intent(this,CancelaAit.class);
			    i.putExtra("idAit", aitPendente.getId());
			    startActivity(i);
			    
				finish();
				
			}
		}
		
		passou = false;
		
	}
    
    
    private Uri uriImagem = null;
    
    private void fotografa(long idAit )
	 {
		 
		 	FotoDAO fotodao = new FotoDAO(getBaseContext());
		 	if (fotodao.getQtde(idAit) == 3 ) 
		 	{
		 	
		 		fotodao.close();
		 		Toast.makeText(getBaseContext(), "Podem ser tiradas no máximo 3 fotos !", Toast.LENGTH_SHORT);
		 	}
		 	else
		 	{
		 		fotodao.close();
		 		
		 		
		 	    // Cria uma intent para capturar uma imagem e retorna o controle para quem o chamou (NAO PRECISA DECLARAR PERMISSAO NO MANIFESTO PARA ACESSAR A CAMERA POIS O FAZEMOS VIA INTENT).
		 	    Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		 	    // Cria um arquivo para salvar a imagem.
		 	   uriImagem = ProcessaImagens.getOutputMediaFileUri( ProcessaImagens.MEDIA_TYPE_IMAGE,PreencheAitplest.this );
		 	    // Passa para intent um objeto URI contendo o caminho e o nome do arquivo onde desejamos salvar a imagem. Pegaremos atraves do parametro data do metodo onActivityResult().
		 	    intent.putExtra( MediaStore.EXTRA_OUTPUT, uriImagem );
		 	    // Inicia a intent para captura de imagem e espera pelo resultado.
		 	    startActivityForResult( intent, chamafoto );
		 	    
		 	    
				ParametroDAO pardao = new ParametroDAO(PreencheAitplest.this);
				Cursor cz = pardao.getParametros();
				
				arquivofoto = Environment.getExternalStorageDirectory() + "/imagens/" +  cz.getString(cz.getColumnIndex("serieait")) + cz.getString(cz.getColumnIndex("proximoait")) + "-" + System.currentTimeMillis() + ".jpg";
					pardao.close();
				cz.close();
		 	}

	 }
	 
		

	

		@Override
		protected void onActivityResult(int requestCode, int resultCode , Intent data){
			
			super.onActivityResult(requestCode, resultCode, data);
			

			passou = false;
			if (( requestCode != chamaactivity) || ( requestCode != chamafoto))
			{
				// indica retorno de activity do sistema
				passou = true;
			}
			
			if (( requestCode != chamaactivity) && ( requestCode != chamafoto))
			{
				// pede o cancelamento...
				Intent i = new Intent(this,CancelaAit.class);
			    i.putExtra("idAit", aitPendente.getId());
			    startActivity(i);
			    
				finish();
				
			}
			
			
			if ( requestCode == chamafoto) {
				
				if ( resultCode == RESULT_OK)
				{
					try
					{
						
						// Vou compactar a imagem, leia o javadoc do médoto e verá que ela retorna tanto um bitmap como um array de bytes.
			            List<Object> imagemCompactada = ProcessaImagens.compactarImagem(uriImagem.getPath());
			            Bitmap imagemBitmap = (Bitmap) imagemCompactada.get( 0 );
			            byte[] imagemBytes = (byte[]) imagemCompactada.get( 1 );

										 
					FotoDAO fotodao = new FotoDAO(getBaseContext());
					    fotodao.gravaFoto(aitPendente.getId(), imagemBytes);
					    fotodao.close();

					    try {
							l.gravalog("Fotografou", "INSERT",OrgA,Pda,agente,PreencheAitplest.this);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
								
					} catch (Exception  e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							l.gravalog("Erro ao fotografar- "+ e.getMessage().replace(".", "-").replace(":", "-"), "Erro",OrgA,Pda,agente,PreencheAitplest.this);
						} catch (Exception ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
						
			            AlertDialog.Builder aviso = new AlertDialog.Builder(PreencheAitplest.this);
			            aviso.setIcon(android.R.drawable.ic_dialog_alert);
			            aviso.setTitle("Foto");
			            aviso.setMessage("Erro ao salvar fotografia, tente novamente");
			            aviso.setNeutralButton("OK",null);
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
