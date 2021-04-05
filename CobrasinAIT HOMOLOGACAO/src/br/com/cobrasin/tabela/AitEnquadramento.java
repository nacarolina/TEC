package br.com.cobrasin.tabela;

import org.json.JSONException;
import org.json.JSONStringer;

public class AitEnquadramento {

	private long id,idait;
	private String codigo;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String toString() {

		return  codigo ;
	}
	public long getIdait() {
		return idait;
	}
	public void setIdait(long idait) {
		this.idait = idait;
	}

	public String toJSON()throws JSONException{
		
		JSONStringer jx = new JSONStringer();
		jx.object()
		.key("id").value(id)
		.key("idait").value(idait)
		.key("codigo").value(codigo)
		.endObject();
		
		return jx.toString();	

	}

}
