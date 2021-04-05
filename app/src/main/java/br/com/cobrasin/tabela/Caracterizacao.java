package br.com.cobrasin.tabela;

public class Caracterizacao {
	
	String Id,Grupo_N_Eixos,PBT_PBTC,Caracterizacao_Titulo,Caracterizacao_Desc,Classe,Codigo;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getGrupo_N_Eixos() {
		return Grupo_N_Eixos;
	}
	public void setGrupo_N_Eixos(String grupo_N_Eixos) {
		Grupo_N_Eixos = grupo_N_Eixos;
	}
	public String getPBT_PBTC() {
		return PBT_PBTC;
	}
	public void setPBT_PBTC(String pBT_PBTC) {
		PBT_PBTC = pBT_PBTC;
	}
	public String getCaracterizacao_Titulo() {
		return Caracterizacao_Titulo;
	}
	public void setCaracterizacao_Titulo(String caracterizacao_Titulo) {
		Caracterizacao_Titulo = caracterizacao_Titulo;
	}
	public String getCaracterizacao_Desc() {
		return Caracterizacao_Desc;
	}
	public void setCaracterizacao_Desc(String caracterizacao_Desc) {
		Caracterizacao_Desc = caracterizacao_Desc;
	}
	public String getClasse() {
		return Classe;
	}
	public void setClasse(String classe) {
		Classe = classe;
	}
	public String getCodigo() {
		return Codigo;
	}
	public void setCodigo(String codigo) {
		Codigo = codigo;
	}
	public byte[] getSilhueta_Foto() {
		return Silhueta_Foto;
	}
	public void setSilhueta_Foto(byte[] silhueta_Foto) {
		Silhueta_Foto = silhueta_Foto;
	}
	byte Silhueta_Foto[];
	
	public String toString()
	{

		return Caracterizacao_Titulo;
	}

}
