package net.mcmillan.editor.util;

import java.io.IOException;
import java.io.OutputStream;

// https://stackoverflow.com/a/7987606
public final class TeeOutputStream extends OutputStream {

	  private final OutputStream out;
	  private final OutputStream tee;

	  public TeeOutputStream(OutputStream out, OutputStream tee) {
	    if (out == null)
	      throw new NullPointerException();
	    else if (tee == null)
	      throw new NullPointerException();

	    this.out = out;
	    this.tee = tee;
	  }

	  @Override
	  public void write(int b) throws IOException {
	    out.write(b);
	    tee.write(b);
	  }

	  @Override
	  public void write(byte[] b) throws IOException {
	    out.write(b);
	    tee.write(b);
	  }

	  @Override
	  public void write(byte[] b, int off, int len) throws IOException {
	    out.write(b, off, len);
	    tee.write(b, off, len);
	  }

	  @Override
	  public void flush() throws IOException {
	    out.flush();
	    tee.flush();
	  }

	  @Override
	  public void close() throws IOException {
	    try {
	      out.close();
	    } finally {
	      tee.close();
	    }
	  }
	}