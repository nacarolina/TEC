package br.com.cobrasin.tabela;

public class Modelo {
	
	private String Id,Modelo,PBT_Modelo,PBT_Valor,CMT,Observacoes;
	
	private int IdFabricante;

	public String getModelo() {
		return Modelo;
	}

	public void setModelo(String modelo) {
		Modelo = modelo;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public int getIdFabricante() {
		return IdFabricante;
	}

	public void setIdFabricante(int idFabricante) {
		IdFabricante = idFabricante;
	}

	public String getPBT_Modelo() {
		return PBT_Modelo;
	}

	public void setPBT_Modelo(String pBT_Modelo) {
		PBT_Modelo = pBT_Modelo;
	}

	public String getPBT_Valor() {
		return PBT_Valor;
	}

	public void setPBT_Valor(String pBT_Valor) {
		PBT_Valor = pBT_Valor;
	}

	public String getCMT() {
		return CMT;
	}

	public void setCMT(String cMT) {
		CMT = cMT;
	}

	public String getObservacoes() {
		return Observacoes;
	}

	public void setObservacoes(String observacoes) {
		Observacoes = observacoes;
	}
	
	public String toString()
	{

		return Modelo;
	}

}
