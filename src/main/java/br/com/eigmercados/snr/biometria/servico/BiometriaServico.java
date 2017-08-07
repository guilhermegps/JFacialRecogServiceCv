package br.com.eigmercados.snr.biometria.servico;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.eigmercados.snr.tools.RequisicaoEncriptada;
import br.com.eigmercados.snr.tools.utils.ArquivoUtils;

@Service
public class BiometriaServico {

	private static final Logger LOGGER = LoggerFactory.getLogger(BiometriaServico.class);

	@Transactional
	public File reconhecimento(RequisicaoEncriptada requisicao){
		byte[] bImg = Base64.decodeBase64(requisicao.getMensagem());
		return reconhecimentoImagem(bImg);
	}
	
	public byte[] extrairRosto(File f){
		try {
		    CascadeClassifier cascadeClassifier = new CascadeClassifier(System.getProperty("user.dir") + "/src/main/java/br/com/eigmercados/snr/data/haarcascades/haarcascade_frontalface_alt.xml");
		    Mat mat = Imgcodecs.imread(f.getAbsolutePath());
		    
			return ArquivoUtils.extrairFaces(ArquivoUtils.getBytes(f), cascadeClassifier, mat).get(0);
		} catch (Exception e) {
			LOGGER.error(e.getCause().getMessage());
			throw new RuntimeException(e.getCause());
		}
	}

	public File reconhecimentoImagem(byte[] bImg){
		nu.pattern.OpenCV.loadLocally();
		
		try {
			String img = "/tmp/img_" + java.util.UUID.randomUUID() + "_tmp.jpg";
		    FileOutputStream fos = new FileOutputStream(img);
		    bImg = ArquivoUtils.convertPNGinJPG(bImg);
		    fos.write(bImg);
		    fos.close();

		    CascadeClassifier cascadeClassifier = new CascadeClassifier(System.getProperty("user.dir") + "/src/main/java/br/com/eigmercados/snr/data/haarcascades/haarcascade_frontalface_alt.xml");
		    Mat mat = Imgcodecs.imread(img);
		    
		    File f = new File(img);
		    if(f.exists()){
		    	f.delete();
		    }
		    
		    MatOfRect matOfRect = ArquivoUtils.detectarFaces(cascadeClassifier, mat);
		    
//		    List<propriedadesFaceDTO> propsFaces = obterDadosFaces(matOfRect);
		    
//		    BufferedImage imagemCorteDesfoque = DesfocarImagem(mat);
		    
//		    propsFaces = CortarImagem(propsFaces, imagemCorteDesfoque);
		    		    
		    ArquivoUtils.desenharRetangulo(matOfRect, mat);
		    BufferedImage imagemSemEfeitos = ArquivoUtils.converterParaImage(mat);
		    
//		    imagemCorteDesfoque = ArquivoUtils.juntarImagens(propsFaces, imagemSemEfeitos);
		    
		    File outputfile = new File(img);
		    
		    try {
//		    	ImageIO.write(imagemCorteDesfoque, "jpg", outputfile);
		    	ImageIO.write(imagemSemEfeitos, "jpg", outputfile);
		    	
		    	return outputfile;
		    } catch (IOException e) {
				LOGGER.error(e.getCause().getMessage());
				throw new RuntimeException(e.getCause());
		    }
		} catch (Exception e) {
			LOGGER.error(e.getCause().getMessage());
			throw new RuntimeException(e.getCause());
		}
	}

//	public BufferedImage DesfocarImagem(Mat mat){  
//		mat = Desfocar(mat);
//	  
//		return ArquivoUtils.converterParaImage(mat);
//	}
		 
//	private Mat Desfocar(Mat image){     
//		Mat destination = new Mat(image.rows(),image.cols(),image.type());
//	         
//		Imgproc.GaussianBlur(image, destination,new Size(45,45), 0);
//	  
//		return destination;
//	}
	
//	public List<propriedadesFaceDTO> CortarImagem(List<propriedadesFaceDTO> dados, BufferedImage imagem){
//		  
//		for(propriedadesFaceDTO dado : dados){
//			dado.setImageCortada(imagem.getSubimage(dado.getX(), dado.getY(), dado.getWidth(), dado.getHeight()));
//		}
//		  
//		return dados;
//	}

}
