package br.com.cobrasin;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 */
public class SimpleCrypto {

	public static String encrypt(String seed, String cleartext) throws Exception {
		//byte[] rawKey = getRawKey(seed.getBytes());
		byte[] rawKey =  { -70, -108, -59, 71, 90, -4, -103, 103, 0, -26, 111, -9, -114, 121, 54, -47 };
		byte[] result = encrypt(rawKey, cleartext.getBytes());
		return toHex(result);
	}
	
	
	public static String decrypt(String seed, String encrypted) throws Exception {
		//byte[] rawKey = getRawKey(seed.getBytes());
		byte[] rawKey =  { -70, -108, -59, 71, 90, -4, -103, 103, 0, -26, 111, -9, -114, 121, 54, -47 };
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
	    kgen.init(128, sr); // 192 and 256 bits may not be available
	    SecretKey skey = kgen.generateKey();
	    byte[] raw = skey.getEncoded();
	    return raw;
	}

	
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
		
	}
	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}
	
	public static byte[] toByte(String hexString) {
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
	private final static String HEX = "0123456789ABCDEF";
	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}
	
	
	//private String ait,flag,agente,placa,data,hora,marca,especie,tipo,logradouronum;
	//private String logradouro,logradourotipo,nome,pgu,uf,observacoes,latitude,longitude,impresso,transmitido,seriepda,encerrou,cancelou,motivo;

	// Criptografa 01 Registro
	
	/*
	public void criptAit( long idAit,Context ctx)
	{

		AitDAO aitdao = new AitDAO(ctx);
		
		Cursor cursor =  aitdao.getAit(idAit);
		
		Ait aitz = new Ait();
		
		// não critpgorafa o "id" nem o "flag"
		for ( int nxx = 1 ; nxx < 26; nxx++)
		{
			String coluna = cursor.getColumnName(nxx);
			
			if (!coluna.contains("flag"))
			{
				String dado = cursor.getString(nxx);
				
				try {
					
					dado = encrypt(info,dado);
		
					if (cursor.getColumnName(nxx).contains("ait"))
						aitz.setAit(dado);
		
					if (cursor.getColumnName(nxx).contains("agente"))
						aitz.setAgente(dado);
					
					if (cursor.getColumnName(nxx).contains("placa"))
						aitz.setPlaca(dado);
					
					if (cursor.getColumnName(nxx).contains("data"))
						aitz.setData(dado);
					
					if (cursor.getColumnName(nxx).contains("hora"))
						aitz.setHora(dado);
					
					if (cursor.getColumnName(nxx).contains("marca"))
						aitz.setMarca(dado);
					
					if (cursor.getColumnName(nxx).contains("especie"))
						aitz.setEspecie(dado);
					
					if (cursor.getColumnName(nxx).compareTo("tipo")==0)
						aitz.setTipo(dado);
					
					if (cursor.getColumnName(nxx).compareTo("logradouro")==0)
						aitz.setLogradouro(dado);

					if (cursor.getColumnName(nxx).contains("logradouronum"))
						aitz.setLogradouronum(dado);

					if (cursor.getColumnName(nxx).contains("logradourotipo"))
						aitz.setLogradourotipo(dado);
					
					if (cursor.getColumnName(nxx).contains("nome"))
						aitz.setNome(dado);
					
					if (cursor.getColumnName(nxx).contains("cpf"))
						aitz.setCpf(dado);
					
					if (cursor.getColumnName(nxx).contains("pgu"))
						aitz.setPgu(dado);
					
					if (cursor.getColumnName(nxx).contains("uf"))
						aitz.setUf(dado);
					
					if (cursor.getColumnName(nxx).contains("observacoes"))
						aitz.setObservacoes(dado);
					
					if (cursor.getColumnName(nxx).contains("latitude"))
						aitz.setLatitude(dado);
					
					if (cursor.getColumnName(nxx).contains("longitude"))
						aitz.setLongitude(dado);
					
					if (cursor.getColumnName(nxx).contains("impresso"))
						aitz.setImpresso(dado);
					
					if (cursor.getColumnName(nxx).contains("transmitido"))
						aitz.setTransmitido(dado);
					
					if (cursor.getColumnName(nxx).contains("seriepda"))
						aitz.setSeriepda(dado);
					
					if (cursor.getColumnName(nxx).contains("encerrou"))
						aitz.setEncerrou(dado);

					if (cursor.getColumnName(nxx).contains("cancelou"))
						aitz.setCancelou(dado);

					if (cursor.getColumnName(nxx).contains("motivo"))
						aitz.setMotivo(dado);
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		aitz.setId(cursor.getLong(0));
		aitdao.altera(aitz);
		aitdao.close();
		
	}
	
	// Descriptografa 01 Registro
	public void decriptAit( Cursor cx )
	{
		
	}*/
}


