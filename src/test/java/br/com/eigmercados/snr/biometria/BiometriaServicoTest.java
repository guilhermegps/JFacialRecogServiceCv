package br.com.eigmercados.snr.biometria;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.eigmercados.snr.biometria.servico.BiometriaServico;

public class BiometriaServicoTest extends AbstractTest {

	@Autowired
	private BiometriaServico servico;

	@Test
	public void recuperar() {
		File f = new File("/tmp/ori2.jpg");
		servico.reconhecimentoImagem(getBytes(f));
	}

	public byte[] getBytes(File file) {
		int len = (int) file.length();
		byte[] sendBuf = new byte[len];
		FileInputStream inFile = null;
		try {
			inFile = new FileInputStream(file);
			inFile.read(sendBuf, 0, len);
		} catch (Exception e) {}
		
		return sendBuf;
	}

}
