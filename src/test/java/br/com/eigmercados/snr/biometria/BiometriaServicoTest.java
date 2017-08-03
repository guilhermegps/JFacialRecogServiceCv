package br.com.eigmercados.snr.biometria;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.eigmercados.snr.biometria.servico.BiometriaServico;
import br.com.eigmercados.snr.tools.utils.ReconhecimentoFacialUtils;

public class BiometriaServicoTest extends AbstractTest {

	@Autowired
	private BiometriaServico servico;

//	@Test
//	public void recuperar() {
//		File f = new File("/tmp/ori.jpg");
//		servico.reconhecimentoImagem(getBytes(f));
//	}
	
	@Test
	public void reconehcimento(){
		File db[] = new File[2];
		db[0] = new File("/tmp/out.jpg");
		db[0] = new File("/tmp/guioc.jpg");
		db[1] = new File("/tmp/gui.jpg");
		
		File base = new File("/tmp/tim.jpg");
		System.out.println(
				ReconhecimentoFacialUtils.reconhecerFaceImagens(base, db)
		);
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
