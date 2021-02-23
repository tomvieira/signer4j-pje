package br.jus.cnj.pje.office.provider;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;

public class NullMessageDigest extends MessageDigest implements Cloneable {
  private ByteArrayOutputStream bOut;
  private String algoritmoFake;

  public NullMessageDigest(final String algoritmo, final String algoritmoFake) {
    super(algoritmo);
    this.bOut = new ByteArrayOutputStream();
    this.algoritmoFake = algoritmoFake;
    this.engineReset();
  }

  public void engineUpdate(final byte b) {
    this.bOut.write(b);
  }

  public void engineUpdate(final byte[] b, final int offset, final int length) {
    this.bOut.write(b, offset, length);
  }

  public void engineReset() {
    this.bOut.reset();
  }

  public byte[] engineDigest() {
    final byte[] res = this.bOut.toByteArray();
    this.reset();
    this.alterarAlgoritmo(this.algoritmoFake);
    return res;
  }

  protected void alterarAlgoritmo(final String algoritmo) {
    try {
      final Field f = MessageDigest.class.getDeclaredField("algorithm");
      f.setAccessible(true);
      final Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(f, f.getModifiers() & 0xFFFFFFEF);
      f.set(this, algoritmo);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("N\ufffdo foi poss\ufffdvel alterar algoritmo de hash, utilizar fallback para garantir assinatura.");
    }
  }
}
