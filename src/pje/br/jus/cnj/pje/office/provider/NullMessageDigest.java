package br.jus.cnj.pje.office.provider;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;

@Deprecated
//Gambiarra da braba!
public class NullMessageDigest extends MessageDigest implements Cloneable{

	private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	private String algoritmoFake; // usado para assinaturas MSCAPI codificarem o ASN.1 corretamente
	
	public NullMessageDigest(String algoritmo, String algoritmoFake){
		super(algoritmo);
		this.algoritmoFake = algoritmoFake;
		engineReset();
	}

	public void engineUpdate(byte b){
		bOut.write(b);
	}

	public void engineUpdate(byte b[], int offset, int length){
		bOut.write(b, offset, length);
	}

	public void engineReset(){
		bOut.reset();
	}

	public byte[] engineDigest(){
		byte[] res = bOut.toByteArray();
		reset();
		// alterar algoritmo para MD5 ou SHA, desta forma assinaturas codificarão ASN.1 de forma correta
		alterarAlgoritmo(algoritmoFake);
		return res;
	}
	
	protected void alterarAlgoritmo(String algoritmo) {
		try{
			Field f = MessageDigest.class.getDeclaredField("algorithm");
			f.setAccessible(true);
					
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			f.set(this, algoritmo);
		} catch (Exception e) {
			throw new IllegalArgumentException("Não foi possível alterar algoritmo de hash, utilizar fallback para garantir assinatura.");
		}
	}
	
}
