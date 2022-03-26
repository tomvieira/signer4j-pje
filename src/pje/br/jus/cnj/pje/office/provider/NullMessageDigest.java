/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


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
