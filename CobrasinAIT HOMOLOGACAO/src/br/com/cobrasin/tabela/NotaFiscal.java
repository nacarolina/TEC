package br.com.cobrasin.tabela;

public class NotaFiscal {
	
	private long idait;
	private String id,NumeroNota,PesoDeclarado,PesoExcesso,PesoVeiculo;
	byte imagem[];

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getIdait() {
		return idait;
	}

	public void setIdait(long idait) {
		this.idait = idait;
	}

	public String getNumeroNota() {
		return NumeroNota;
	}

	public void setNumeroNota(String numeroNota) {
		NumeroNota = numeroNota;
	}

	public String getPesoDeclarado() {
		return PesoDeclarado;
	}

	public void setPesoDeclarado(String pesoDeclarado) {
		PesoDeclarado = pesoDeclarado;
	}

	public String getPesoExcesso() {
		return PesoExcesso;
	}

	public void setPesoExcesso(String pesoExcesso) {
		PesoExcesso = pesoExcesso;
	}

	public String getPesoVeiculo() {
		return PesoVeiculo;
	}

	public void setPesoVeiculo(String pesoVeiculo) {
		PesoVeiculo = pesoVeiculo;
	}
	public String toString()
	{

		return NumeroNota;
	}
}
