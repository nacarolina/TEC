package br.com.cobrasin.tabela;

public class ArqObservacao {

	private long id;
	private String descricao;
	
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
}
