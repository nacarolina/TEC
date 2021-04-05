package br.com.cobrasin.tabela;

public class Pais {

	private String codigo;
	private String descricao;
	
	
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
		
		//return  codigo + " - " + descricao ;
		return  descricao ;
	}
	
	
}
