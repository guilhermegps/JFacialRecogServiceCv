package br.com.eigmercados.snr.biometria.servico;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.eigmercados.snr.biometria.dto.propriedadesFaceDTO;
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

	public File reconhecimentoImagem(byte[] bImg){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		try {
			String img = "/tmp/img.jpg";
		    FileOutputStream fos = new FileOutputStream(img);
		    bImg = ArquivoUtils.convertPNGinJPG(bImg);
		    fos.write(bImg);
		    fos.close();

		    CascadeClassifier cascadeClassifier = new CascadeClassifier(System.getProperty("user.dir") + "/src/main/java/br/com/eigmercados/snr/data/haarcascades/haarcascade_frontalface_alt.xml");
		    Mat mat = Imgcodecs.imread(img);
		    
		    MatOfRect matOfRect = detectarFaces(cascadeClassifier, mat);
		    
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
	
	//Método que detecta as faces
	public MatOfRect detectarFaces(CascadeClassifier cascadeClassifier, Mat mat){
		MatOfRect matOfRect = new MatOfRect();
		
		Mat grayFrame = ArquivoUtils.escalaCinzaImagem(mat);
		
		cascadeClassifier.detectMultiScale(grayFrame, matOfRect);
//		cascadeClassifier.detectMultiScale(mat, matOfRect, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(), new Size());
		return matOfRect;
	}
	
	// Método que retorna a posição X e Y das faces
	public List<propriedadesFaceDTO> obterDadosFaces(MatOfRect matOfRect){
		 List<propriedadesFaceDTO> dados = new ArrayList<propriedadesFaceDTO>();
		  
		 for(Rect rect : matOfRect.toArray()) {
			 propriedadesFaceDTO prop = new propriedadesFaceDTO();
			 prop.setX(rect.x);
			 prop.setY(rect.y);
			 prop.setHeight(rect.height);
			 prop.setWidth(rect.width);
			   
			 dados.add(prop);
		 }
		  
		 return dados;
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
