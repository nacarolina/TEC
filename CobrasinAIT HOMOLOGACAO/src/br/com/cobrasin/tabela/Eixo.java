package br.com.cobrasin.tabela;

public class Eixo {
	
	String Id,Eixo_Titulo,Eixo_Desc,Eixo_Peso;
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getEixo_Titulo() {
		return Eixo_Titulo;
	}
	public void setEixo_Titulo(String eixo_Titulo) {
		Eixo_Titulo = eixo_Titulo;
	}
	public String getEixo_Desc() {
		return Eixo_Desc;
	}
	public void setEixo_Desc(String eixo_Desc) {
		Eixo_Desc = eixo_Desc;
	}
	public String getEixo_Peso() {
		return Eixo_Peso;
	}
	public void setEixo_Peso(String eixo_Peso) {
		Eixo_Peso = eixo_Peso;
	}
	public byte[] getFoto() {
		return Foto;
	}
	public void setFoto(byte[] foto) {
		Foto = foto;
	}
	byte Foto[];
      

}
