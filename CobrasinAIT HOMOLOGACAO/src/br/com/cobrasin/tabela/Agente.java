package br.com.cobrasin.tabela;

public class Agente {

	private String codigo;
	private String nome;
	private String senha;
	private String login;
	private String ativo;
	private String Posto;
	
	public String getPosto() {
		return Posto;
	}
	public void setPosto(String posto) {
		Posto = posto;
	}
	public String getIdMunicipio() {
		return IdMunicipio;
	}
	public void setIdMunicipio(String idMunicipio) {
		IdMunicipio = idMunicipio;
	}
	private String IdMunicipio;
	private String DNIT;
	
	
	public String getDNIT() {
		return DNIT;
	}
	public void setDNIT(String dNIT) {
		DNIT = dNIT;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	
	public String toString() {
		
		return  codigo + " - " + nome ;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getAtivo() {
		return ativo;
	}
	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}
	
	
}
