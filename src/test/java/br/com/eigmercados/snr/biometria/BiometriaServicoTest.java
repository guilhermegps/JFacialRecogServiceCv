package br.com.eigmercados.snr.biometria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
//	@Test
//	public void reconehcimento(){
//		File db[] = new File[3];
//		db[0] = new File("/tmp/img_test/tim2.jpg");
//		db[1] = new File("/tmp/img_test/guioc.jpg");
//		db[2] = new File("/tmp/img_test/drauzio.jpg");
////		db[3] = new File("/tmp/img_test/out.jpg");
////		db[4] = new File("/tmp/img_test/guioc2.jpg");
//		
//		File base = new File("/tmp/img_test/guioc.jpg");
//		System.out.println(
//				ReconhecimentoFacialUtils.reconhecerFaceImagens(base, db)
//		);
//	}
	
	@Test
	public void reconhecimento(){
		List<byte[]> db = new ArrayList<>();
		db.add(
			servico.extrairRosto(new File("/tmp/img_test/tim2.jpg")) 
		);
		db.add(
			servico.extrairRosto(new File("/tmp/img_test/guioc.jpg")) 
		);
		db.add(
			servico.extrairRosto(new File("/tmp/img_test/drauzio.jpg")) 
		);
		
		byte base[] = servico.extrairRosto(new File("/tmp/img_test/gui.jpg"));
		
		System.out.println(
			ReconhecimentoFacialUtils.reconhecerFaceImagens(base, db)
		);
	}
}
