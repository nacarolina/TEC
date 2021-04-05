package br.com.cobrasin.tabela;

import org.json.JSONStringer;

public class Logs {

	private String orgao,agente,pda,datahora,status,operacao;
	private Long id;
	//private Long idwebtrans;

	public String getOrgao() {
		return orgao;
	}

	public void setOrgao(String orgao) {
		this.orgao = orgao;
	}

	public String getAgente() {
		return agente;
	}

	public void setAgente(String agente) {
		this.agente = agente;
	}

	public String getPda() {
		return pda;
	}

	public void setPda(String pda) {
		this.pda = pda;
	}

	public String getDataHora() {
		return datahora;
	}

	public void setDataHora(String datahora) {
		this.datahora = datahora;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}
	
	public String toJSON(){
		
		JSONStringer jx = new JSONStringer();
		try
		{
			jx.object()
			.key("id").value(id)
			.key("orgao").value(orgao)
			.key("agente").value(agente)
			.key("pda").value(pda)
			.key("datahora").value(datahora)
			.key("status").value(status)
			.key("operacao").value(operacao)
			.endObject();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return jx.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id= id;
	}
	

}
