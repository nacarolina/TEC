package br.com.cobrasin.tabela;

public class Enquadramento {

	private String codigo;
	private String descricao;
	private String ObrigatorioObs;
	
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String toString() {
		
		return  codigo + " - " + descricao ;
	}


	public String getObrigatorioObs() {
		return ObrigatorioObs;
	}

	public void setObrigatorioObs(String obrigatorioObs) {
		ObrigatorioObs = obrigatorioObs;
	}
}
