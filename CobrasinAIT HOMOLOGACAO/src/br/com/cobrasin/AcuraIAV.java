package br.com.cobrasin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.thingmagic.ReaderUtil;

public class AcuraIAV {
	private byte[] plainBlock1 = new byte[16];
	private byte[] plainBlock2 = new byte[16];
	private byte[] data = noData;
	private byte[] _fulldata = noData;
	private byte[] _aesData = noData;
	private String OBUID_BF_RFFU = "";
	private String Key; 
	static final byte[] noData = new byte[0];

	// ACURA

	public void KeyAK(String key) {
		this.Key = key;
	}

	public String getOBU_AuthID(byte[] tag) {
		try {
			if (tag.length == 30) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				_aesData = new byte[22];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 21; n++) {
					_aesData[n] = _fulldata[n + 8];
				}
				OBUID_BF_RFFU = getOBU_ID46(decryptG0(_aesData).substring(16,
						28));
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return null;
		}

	}

	public String getOBU_FullPass1(byte[] tag) {
		try {
			if (tag.length == 28) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				_aesData = new byte[22];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 21; n++) {
					_aesData[n] = _fulldata[n + 6];
				}
				OBUID_BF_RFFU = (decryptG0(_aesData).substring(16, 32));
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return null;
		}

	}

	public String getOBU_FullPass2(byte[] tag) {
		try {
			if (tag.length == 28) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				_aesData = new byte[22];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 21; n++) {
					_aesData[n] = _fulldata[n + (6 + (data.length - 28))];
				}
				OBUID_BF_RFFU = (decryptG0(_aesData).substring(16, 32));
				return OBUID_BF_RFFU;

			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return null;
		}

	}

	public String getOBU_FullPass(byte[] tag) {
		try {
			OBUID_BF_RFFU = (getOBU_FullPass1(tag) + getOBU_FullPass2(tag));
			return OBUID_BF_RFFU;

		} catch (Exception e) {
			return "Failed";
		}

	}

	 public String getG0Encrypted(byte[] tag) {
	        try {
	            if (tag.length == 30) {
	                data = new byte[tag.length];
	                _fulldata = new byte[tag.length];
	                data = tag;
	                _aesData = new byte[22];
	                _fulldata = data;
	                for (int n = 0; n <= 21; n++) {
	                    _aesData[n] = _fulldata[n + 8];
	                }
	                OBUID_BF_RFFU = getG0(_aesData);
	                return OBUID_BF_RFFU;
	            } else if(tag.length == 28){
	            	data = new byte[tag.length];
	                _fulldata = new byte[tag.length];
	                data = tag;
	                _aesData = new byte[22];
	                _fulldata = data;
	                for (int n = 0; n <= 21; n++) {
	                    _aesData[n] = _fulldata[n + 6];
	                }
	                OBUID_BF_RFFU = getG0(_aesData);
	                return OBUID_BF_RFFU;
	            
	            }else {            
	                return "Wrong";
	            }
	        } catch (Exception e) {
	            return "Failed";
	        }

	    }
	 
	 public String getG0_R64(byte[] tag) {
	        try {
	            if (tag.length == 30 || tag.length == 28) {
	                data = new byte[tag.length];
	                _fulldata = new byte[tag.length];
	                _aesData = new byte[8];
	                data = tag;
	                _fulldata = data;
	                for (int n = 0; n <= 7; n++) {
	                    _aesData[n] = _fulldata[n];
	                }
	                OBUID_BF_RFFU = ReaderUtil.byteArrayToHexString(_aesData);
	                return OBUID_BF_RFFU.substring(0, 16);
	            } else {
	                return "Wrong";
	            }
	        } catch (Exception e) {
	            return "Failed";
	        }

	    }
	 
	 public String getG0_R48(byte[] tag) {
	        try {
	            if (tag.length == 30 || tag.length == 28) {
	                data = new byte[tag.length];
	                _fulldata = new byte[tag.length];
	                _aesData = new byte[6];
	                data = tag;
	                _fulldata = data;
	                for (int n = 0; n <= 5; n++) {
	                    _aesData[n] = _fulldata[n];
	                }
	                OBUID_BF_RFFU = ReaderUtil.byteArrayToHexString(_aesData);
	                return OBUID_BF_RFFU.substring(0, 12);
	            } else {
	                return "Wrong";
	            }
	        } catch (Exception e) {
	            return "Failed";
	        }

	    }
	 
	private String decryptG0(byte[] encryptedData) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int len = encryptedData.length;

		if (len != 22) {
			return null;
		} else {
			// //////////////////////SHIFT LEFT 2
			// BITS////////////////////////////

			String allBin = "";
			String bin = "";
			String[] test = new String[22];
			int pp = 0;
			for (byte b : encryptedData) {
				test[pp] = (Integer.toBinaryString((0xff & b)));
				if (test[pp].length() != 8) {
					test[pp] = String
							.format("%08d", Integer.parseInt(test[pp]));
				}
				pp++;
			}
			for (int i = 0; i < encryptedData.length; i++) {

				bin += test[i];

				allBin += bin;

				bin = "";
			}

			String newBin = allBin.substring(2);
			newBin += "00";

			int numOfBytes = newBin.length() / 8;
			String ggg = "";
			byte[] bytes = new byte[numOfBytes];
			for (int i = 0; i < numOfBytes; ++i) {
				int o = i + 1;
				test[i] = Integer.toHexString(Integer.valueOf(
						newBin.substring((8 * i), (8 * o)), 2));
				if (test[i].length() != 2) {
					test[i] = "0" + test[i];
				}
				ggg += test[i];
			}
			for (int i = 0; i < 44; i += 2) {
				bytes[i / 2] = (byte) ((Character.digit(ggg.charAt(i), 16) << 4) + Character
						.digit(ggg.charAt(i + 1), 16));
			}

			byte[] block1 = new byte[16];

			for (int a = 0; a <= 15; a++) {
				block1[a] = bytes[a + 2];
			}

			try {
				SecretKeySpec keydec = new SecretKeySpec(
						ReaderUtil.hexStringToByteArray(this.Key),
						"AES/ECB/NoPadding");
				cipher.init(Cipher.DECRYPT_MODE, keydec);
				ByteArrayInputStream bis = new ByteArrayInputStream(block1);
				CipherInputStream cis = new CipherInputStream(bis, cipher);
				DataInputStream dis = new DataInputStream(cis);

				try {
					dis.readFully(plainBlock1, 0, plainBlock1.length);
					cis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ReaderUtil.byteArrayToHexString(plainBlock1);
		}
	}
	
	private String getG0(byte[] encryptedData) {
		int len = encryptedData.length;

		if (len != 22) {
			return null;
		} else {
			// //////////////////////SHIFT LEFT 2
			// BITS////////////////////////////

			String allBin = "";
			String bin = "";
			String[] test = new String[22];
			int pp = 0;
			for (byte b : encryptedData) {
				test[pp] = (Integer.toBinaryString((0xff & b)));
				if (test[pp].length() != 8) {
					test[pp] = String
							.format("%08d", Integer.parseInt(test[pp]));
				}
				pp++;
			}
			for (int i = 0; i < encryptedData.length; i++) {

				bin += test[i];

				allBin += bin;

				bin = "";
			}

			String newBin = allBin.substring(2);
			newBin += "00";

			int numOfBytes = newBin.length() / 8;
			String ggg = "";
			byte[] bytes = new byte[numOfBytes];
			for (int i = 0; i < numOfBytes; ++i) {
				int o = i + 1;
				test[i] = Integer.toHexString(Integer.valueOf(
						newBin.substring((8 * i), (8 * o)), 2));
				if (test[i].length() != 2) {
					test[i] = "0" + test[i];
				}
				ggg += test[i];
			}
			for (int i = 0; i < 44; i += 2) {
				bytes[i / 2] = (byte) ((Character.digit(ggg.charAt(i), 16) << 4) + Character
						.digit(ggg.charAt(i + 1), 16));
			}

			byte[] block1 = new byte[16];

			for (int a = 0; a <= 15; a++) {
				block1[a] = bytes[a + 2];
			}

			return ReaderUtil.byteArrayToHexString(plainBlock1);
		}
	}

	private String getOBU_ID46(String data) {
		String allBin = "";
		String bin = "";
		String auxBin = "";
		byte[] hexData = ReaderUtil.hexStringToByteArray(data);
		String[] hexatext = new String[hexData.length];
		int inc = 0;
		for (byte b : hexData) {
			hexatext[inc] = (Integer.toBinaryString((0xff & b)));
			if (hexatext[inc].length() != 8) {
				hexatext[inc] = String.format("%08d",
						Integer.parseInt(hexatext[inc]));
			}
			inc++;
		}

		for (int i = 0; i < hexData.length; i++) {

			auxBin = hexatext[i];

			bin += auxBin;

			allBin += bin;

			bin = "";
		}

		String newBin = "00";
		newBin += allBin.substring(0, allBin.length() - 1);
		int numOfBytes = newBin.length() / 8;
		String decryptOBU_AuthID = "";
		for (int i = 0; i < numOfBytes; ++i) {
			int o = i + 1;
			hexatext[i] = Integer.toHexString(Integer.valueOf(
					newBin.substring((8 * i), (8 * o)), 2));
			if (hexatext[i].length() != 2) {
				hexatext[i] = "0" + hexatext[i];
			}
			decryptOBU_AuthID += hexatext[i];
		}
		return decryptOBU_AuthID;

	}

	public String getPA_OBUID(byte[] tag) {
		try {
			if (tag.length == 50) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				_aesData = new byte[36];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 35; n++) {
					_aesData[n] = _fulldata[n + 12];
				}
				OBUID_BF_RFFU = decryptPA(_aesData).substring(22, 32);
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return "Failed";
		}

	}

	public String getPA_DATA64(byte[] tag) {
		try {
			if (tag.length == 50) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				_aesData = new byte[36];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 35; n++) {
					_aesData[n] = _fulldata[n + 12];
				}
				OBUID_BF_RFFU = decryptPA(_aesData).substring(32, 48);
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return "Failed";
		}

	}

	public String getPA_R96(byte[] tag) {
		try {
			if (tag.length == 50) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 11; n++) {
					_aesData[n] = _fulldata[n];
				}
				OBUID_BF_RFFU = ReaderUtil.byteArrayToHexString(_aesData).substring(0,24);
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return "Failed";
		}

	}
	
	public String getPAEncrypted(byte[] tag) {
		try {
			if (tag.length == 50) {
				data = new byte[tag.length];
				_fulldata = new byte[tag.length];
				data = tag;
				_fulldata = data;
				for (int n = 0; n <= 35; n++) {
					_aesData[n] = _fulldata[n+12];
				}
				OBUID_BF_RFFU = getPA(_aesData);
				return OBUID_BF_RFFU;
			} else {
				return "Wrong";
			}
		} catch (Exception e) {
			return "Failed";
		}

	}

	private String decryptPA(byte[] encryptedData) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int len = encryptedData.length;

		if (len != 36) {
			return null;
		} else {
			// //////////////////////SHIFT LEFT 2
			// BITS////////////////////////////

			String allBin = "";
			String bin = "";
			String[] test = new String[encryptedData.length];
			int pp = 0;
			for (byte b : encryptedData) {
				test[pp] = (Integer.toBinaryString((0xff & b)));
				if (test[pp].length() != 8) {
					test[pp] = String
							.format("%08d", Integer.parseInt(test[pp]));
				}
				pp++;
			}
			for (int i = 0; i < encryptedData.length; i++) {

				bin += test[i];

				allBin += bin;

				bin = "";
			}

			String newBin = allBin.substring(2);
			newBin += "00";

			int numOfBytes = newBin.length() / 8;
			String ggg = "";
			byte[] bytes = new byte[numOfBytes];
			for (int i = 0; i < numOfBytes; ++i) {
				int o = i + 1;
				test[i] = Integer.toHexString(Integer.valueOf(
						newBin.substring((8 * i), (8 * o)), 2));
				if (test[i].length() != 2) {
					test[i] = "0" + test[i];
				}
				ggg += test[i];
			}
			for (int i = 0; i < 72; i += 2) {
				bytes[i / 2] = (byte) ((Character.digit(ggg.charAt(i), 16) << 4) + Character
						.digit(ggg.charAt(i + 1), 16));
			}

			byte[] block1 = new byte[16];
			byte[] block2 = new byte[16];

			for (int a = 0; a <= 15; a++) {
				block1[a] = bytes[a + 2];
			}
			for (int a = 0; a <= 15; a++) {
				block2[a] = bytes[a + 18];
			}

			try {
				SecretKeySpec keydec = new SecretKeySpec(
						ReaderUtil.hexStringToByteArray(this.Key),
						"AES/ECB/NoPadding");
				cipher.init(Cipher.DECRYPT_MODE, keydec);
				ByteArrayInputStream bis1 = new ByteArrayInputStream(block1);
				CipherInputStream cis1 = new CipherInputStream(bis1, cipher);
				DataInputStream dis1 = new DataInputStream(cis1);
				ByteArrayInputStream bis2 = new ByteArrayInputStream(block2);
				CipherInputStream cis2 = new CipherInputStream(bis2, cipher);
				DataInputStream dis2 = new DataInputStream(cis2);

				try {
					dis1.readFully(plainBlock1, 0, plainBlock1.length);
					dis2.readFully(plainBlock2, 0, plainBlock2.length);
					cis1.close();
					cis2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ReaderUtil.byteArrayToHexString(plainBlock1)
					+ ReaderUtil.byteArrayToHexString(plainBlock2);
		}
	}

	private String getPA(byte[] encryptedData) {
		int len = encryptedData.length;
		if (len != 36) {
			return null;
		} else {
			// //////////////////////SHIFT LEFT 2
			// BITS////////////////////////////

			String allBin = "";
			String bin = "";
			String[] test = new String[encryptedData.length];
			int pp = 0;
			for (byte b : encryptedData) {
				test[pp] = (Integer.toBinaryString((0xff & b)));
				if (test[pp].length() != 8) {
					test[pp] = String
							.format("%08d", Integer.parseInt(test[pp]));
				}
				pp++;
			}
			for (int i = 0; i < encryptedData.length; i++) {

				bin += test[i];

				allBin += bin;

				bin = "";
			}

			String newBin = allBin.substring(2);
			newBin += "00";

			int numOfBytes = newBin.length() / 8;
			String ggg = "";
			byte[] bytes = new byte[numOfBytes];
			for (int i = 0; i < numOfBytes; ++i) {
				int o = i + 1;
				test[i] = Integer.toHexString(Integer.valueOf(
						newBin.substring((8 * i), (8 * o)), 2));
				if (test[i].length() != 2) {
					test[i] = "0" + test[i];
				}
				ggg += test[i];
			}
			for (int i = 0; i < 72; i += 2) {
				bytes[i / 2] = (byte) ((Character.digit(ggg.charAt(i), 16) << 4) + Character
						.digit(ggg.charAt(i + 1), 16));
			}

			for (int a = 0; a <= 15; a++) {
				plainBlock1[a] = bytes[a + 2];
			}
			for (int a = 0; a <= 15; a++) {
				plainBlock2[a] = bytes[a + 18];
			}
		}
		return ReaderUtil.byteArrayToHexString(plainBlock1)
				+ ReaderUtil.byteArrayToHexString(plainBlock2);
	}
}