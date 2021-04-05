package br.com.cobrasin.tabela;

import org.json.JSONStringer;

public class Ait {

		private String ait,flag,agente,placa,data,hora,marca,especie,tipo,logradouronum;
		private String logradouro;
	private String logradourotipo;
	private String nome;
	private String cpf;
	private String pgu;
	private String uf;
	private String observacoes;
	private String impresso;
	private String transmitido;
	private String seriepda;
	private String encerrou;
	private String cancelou;
	private String motivo;
	private String medidaadm;
	private String tipoait;
	private String pais;
	private String viaEntregue;
	private String condutorAbordado;

	public String getViaEntregue() {
		return viaEntregue;
	}

	public void setViaEntregue(String viaEntregue) {
		this.viaEntregue = viaEntregue;
	}

	public String getCondutorAbordado() {
		return condutorAbordado;
	}

	public void setCondutorAbordado(String condutorAbordado) {
		this.condutorAbordado = condutorAbordado;
	}

	private String UfVeiculo;

	public String getUfVeiculo() {
		return UfVeiculo;
	}

	public void setUfVeiculo(String ufVeiculo) {
		UfVeiculo = ufVeiculo;
	}

	private String equipamento;
	private String medicaoreg;
	private String medicaocon;
	private String limitereg;
	private String dtEdit;
	private String hrEdit;
	private String tipoinfrator;
	private String flagMedida;
	private String impressao;
	private String sendPdf;
	private String Passaporte;
	private String Pid;
	private String logradouro2;
	private String nome_embarcador;
	private String cpfCnpj_embarcador;
	private String endereco_embarcador;
	private String IdMunicipio_embarcador;
	private String bairro_embarcador;
	private String nome_transportador;
	private String cpfCnpj_transportador;
	private String endereco_transportador;
	private String IdMunicipio_transportador;
	private String bairro_transportador;
	private String limitePermitido_excesso;
	private String pesoDeclarado_excesso;
	private String excessoConstatado_excesso;
	private String tara_excesso;
	private String ppd_condutor;
	private String Posto_Agente;
	private String IdMunicipio_Agente;
		
		public String getPosto_Agente() {
			return Posto_Agente;
		}
		public void setPosto_Agente(String posto_Agente) {
			Posto_Agente = posto_Agente;
		}
		public String getIdMunicipio_Agente() {
			return IdMunicipio_Agente;
		}
		public void setIdMunicipio_Agente(String idMunicipio_Agente) {
			IdMunicipio_Agente = idMunicipio_Agente;
		}
		public String getPpd_condutor() {
			return ppd_condutor;
		}
		public void setPpd_condutor(String ppd_condutor) {
			this.ppd_condutor = ppd_condutor;
		}
		public String getLimitePermitido_excesso() {
			return limitePermitido_excesso;
		}
		public void setLimitePermitido_excesso(String limitePermitido_excesso) {
			this.limitePermitido_excesso = limitePermitido_excesso;
		}
		public String getPesoDeclarado_excesso() {
			return pesoDeclarado_excesso;
		}
		public void setPesoDeclarado_excesso(String pesoDeclarado_excesso) {
			this.pesoDeclarado_excesso = pesoDeclarado_excesso;
		}
		public String getExcessoConstatado_excesso() {
			return excessoConstatado_excesso;
		}
		public void setExcessoConstatado_excesso(String excessoConstatado_excesso) {
			this.excessoConstatado_excesso = excessoConstatado_excesso;
		}
		public String getTara_excesso() {
			return tara_excesso;
		}
		public void setTara_excesso(String tara_excesso) {
			this.tara_excesso = tara_excesso;
		}
		public String getIdMunicipio_embarcador() {
			return IdMunicipio_embarcador;
		}
		public void setIdMunicipio_embarcador(String idMunicipio_embarcador) {
			IdMunicipio_embarcador = idMunicipio_embarcador;
		}
		public String getBairro_embarcador() {
			return bairro_embarcador;
		}
		public void setBairro_embarcador(String bairro_embarcador) {
			this.bairro_embarcador = bairro_embarcador;
		}
		public String getIdMunicipio_transportador() {
			return IdMunicipio_transportador;
		}
		public void setIdMunicipio_transportador(String idMunicipio_transportador) {
			IdMunicipio_transportador = idMunicipio_transportador;
		}
		public String getBairro_transportador() {
			return bairro_transportador;
		}
		public void setBairro_transportador(String bairro_transportador) {
			this.bairro_transportador = bairro_transportador;
		}
		public String getNome_embarcador() {
			return nome_embarcador;
		}
		public void setNome_embarcador(String nome_embarcador) {
			this.nome_embarcador = nome_embarcador;
		}

		public String getCpfCnpj_embarcador() {
			return cpfCnpj_embarcador;
		}
		public void setCpfCnpj_embarcador(String cpfCnpj_embarcador) {
			this.cpfCnpj_embarcador = cpfCnpj_embarcador;
		}
		public String getCpfCnpj_transportador() {
			return cpfCnpj_transportador;
		}
		public void setCpfCnpj_transportador(String cpfCnpj_transportador) {
			this.cpfCnpj_transportador = cpfCnpj_transportador;
		}
		public String getEndereco_embarcador() {
			return endereco_embarcador;
		}
		public void setEndereco_embarcador(String endereco_embarcador) {
			this.endereco_embarcador = endereco_embarcador;
		}
		public String getNome_transportador() {
			return nome_transportador;
		}
		public void setNome_transportador(String nome_transportador) {
			this.nome_transportador = nome_transportador;
		}


		public String getEndereco_transportador() {
			return endereco_transportador;
		}
		public void setEndereco_transportador(String endereco_transportador) {
			this.endereco_transportador = endereco_transportador;
		}
		private Long idWebTrans;
		
		private Long  id;
		 
	
		public String getLogradouro2() {
			return logradouro2;
		}
		public void setLogradouro2(String logradouro2) {
			this.logradouro2 = logradouro2;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		
		public String getAit() {
			return ait;
		}
		public void setAit(String ait) {
			this.ait = ait;
		}
		public String getSendPdf() {
			return sendPdf;
		}
		public void setSendPdf(String sendPdf) {
			this.sendPdf = sendPdf;
		}
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public String getAgente() {
			return agente;
		}
		public void setAgente(String agente) {
			this.agente = agente;
		}
		public String getPlaca() {
			return placa;
		}
		public void setPlaca(String placa) {
			this.placa = placa;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getHora() {
			return hora;
		}
		public void setHora(String hora) {
			this.hora = hora;
		}
		public String getMarca() {
			return marca;
		}
		public void setMarca(String marca) {
			this.marca = marca;
		}
		public String getEspecie() {
			return especie;
		}
		public void setEspecie(String especie) {
			this.especie = especie;
		}
		public String getTipo() {
			return tipo;
		}
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		public String getLogradouronum() {
			return logradouronum;
		}
		public void setLogradouronum(String logradouronum) {
			this.logradouronum = logradouronum;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getPgu() {
			return pgu;
		}
		public void setPgu(String pgu) {
			this.pgu = pgu;
		}
		public String getUf() {
			return uf;
		}
		public void setUf(String uf) {
			this.uf = uf;
		}
		public void setdtEdit(String dtEdit) {
			this.dtEdit = dtEdit;
		}
		public String getdtEdit() {
			return dtEdit;
		}
		public void sethrEdit(String hrEdit) {
			this.hrEdit = hrEdit;
		}
		public String gethrEdit() {
			return hrEdit;
		}
		public void setTipoinfrator(String tipoinfrator) {
			this.tipoinfrator = tipoinfrator;
		}
		public String getTipoinfrator() {
			return tipoinfrator;
		}
		public void setImpressao(String impressao) {
			this.impressao = impressao;
		}
		public String getImpressao() {
			return impressao;
		}
		// teste criptografia
		public String getObservacoes() {
		
			/*
			try {
				observacoes = SimpleCrypto.decrypt("COBRASIN", observacoes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return observacoes;
		}
		// teste descriptografia
		public void setObservacoes(String observacoes) {
		
			this.observacoes = observacoes;
			/*
			try {
				this.observacoes = SimpleCrypto.encrypt("COBRASIN",observacoes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		public String getLogradouro() {
			return logradouro;
		}
		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}
		public String getLogradourotipo() {
			return logradourotipo;
		}
		public void setLogradourotipo(String logradourotipo) {
			this.logradourotipo = logradourotipo;
		}
		public String getImpresso() {
			return impresso;
		}
		public void setImpresso(String impresso) {
			this.impresso = impresso;
		}
		public String getTransmitido() {
			return transmitido;
		}
		public void setTransmitido(String transmitido) {
			this.transmitido = transmitido;
		}
		public String getSeriepda() {
			return seriepda;
		}
		public void setSeriepda(String seriepda) {
			this.seriepda = seriepda;
		}
		
		public String toString()
		{
			//if ( placa != null )
			//return ait + "-" + placa + " AG:" + agente +" " + data + "-" + hora;
			//else
			return ait +  " AG:" + agente +" " + data + "-" + hora + " id-"+id;
		}
		public String getEncerrou() {
			return encerrou;
		}
		public void setEncerrou(String encerrou) {
			this.encerrou = encerrou;
		}
		public String getCancelou() {
			return cancelou;
		}
		public void setCancelou(String cancelou) {
			this.cancelou = cancelou;
		}
		public String getMotivo() {
			return motivo;
		}
		public void setMotivo(String motivo) {
			this.motivo = motivo;
		}
		
		public String getCpf() {
			return cpf;
		}
		public void setCpf(String cpf) {
			this.cpf = cpf;
		}
		public String getPassaporte() {
			return Passaporte;
		}
		public void setPassaporte(String Passaporte) {
			this.Passaporte = Passaporte;
		}
		
		public String getPid() {
			return Pid;
		}
		public void setPid(String Pid) {
			this.Pid = Pid;
		}
		
		
		public String getMedidaadm() {
			return medidaadm;
		}
		public void setMedidaadm(String medidaadm) {
			this.medidaadm = medidaadm;
		}
		public String getFlagMedida() {
			return flagMedida;
		}
		public void setFlagMedida(String flagMedida) {
			this.flagMedida = flagMedida;
		}
		public String getTipoait() {
			return tipoait;
		}
		public void setTipoait(String tipoait) {
			this.tipoait = tipoait;
		}

		public String toJSON(){
			
			JSONStringer jx = new JSONStringer();
			try
			{
			
			jx.object()
			.key("id").value(id)
			.key("ait").value(ait)
			.key("flag").value(flag)
			.key("agente").value(agente)
			.key("placa").value(placa)
			.key("data").value(data)
			.key("hora").value(hora)
			.key("marca").value(marca)
			.key("especie").value(especie)
			.key("tipo").value(tipo)
			.key("logradouro").value(logradouro)
			.key("logradouronum").value(logradouronum)
			.key("logradourotipo").value(logradourotipo)
			.key("nome").value(nome)
			.key("cpf").value(cpf)
			.key("pgu").value(pgu)
			.key("uf").value(uf)
			.key("observacoes").value(observacoes)
			.key("impresso").value(impresso)
			.key("transmitido").value(transmitido)
			.key("seriepda").value(seriepda)
			.key("encerrou").value(encerrou)
			.key("cancelou").value(cancelou)
			.key("motivo").value(motivo)
			.key("medidaadm").value(medidaadm)
			.key("tipoait").value(tipoait)
			.key("pais").value(pais)
			.key("equipamento").value(equipamento)
			.key("medicaoreg").value(medicaoreg)
			.key("medicaocon").value(medicaocon)
			.key("limitereg").value(limitereg)
			.key("dtEdit").value(dtEdit)
			.endObject();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			return jx.toString();	

			//private String ait,flag,agente,placa,data,hora,marca,especie,tipo,logradouronum;
			//private String logradouro,logradourotipo,nome,pgu,uf,observacoes,impresso,transmitido,seriepda,encerrou,cancelou,motivo,medidaadm;

		}
		public String getPais() {
			return pais;
		}
		public void setPais(String pais) {
			this.pais = pais;
		}
		public String getEquipamento() {
			return equipamento;
		}
		public void setEquipamento(String equipamento) {
			this.equipamento = equipamento;
		}
		
		public String getLimitereg() {
			return limitereg;
		}
		public void setLimitereg(String limitereg) {
			this.limitereg = limitereg;
		}
		
		
		public Long getIdWebTrans() {
			return idWebTrans;
		}
		public void setIdWebTrans(Long idWebTrans) {
			this.idWebTrans = idWebTrans;
		}
		public String getMedicaoreg() {
			return medicaoreg;
		}
		public void setMedicaoreg(String medicaoreg) {
			this.medicaoreg = medicaoreg;
		}
		public String getMedicaocon() {
			return medicaocon;
		}
		public void setMedicaocon(String medicaocon) {
			this.medicaocon = medicaocon;
		}
	
		
}
