package br.com.eigmercados.snr.biometria.servico;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.eigmercados.snr.biometria.dto.propriedadesFaceDTO;
import br.com.eigmercados.snr.tools.utils.ArquivoUtils;

@Service
public class BiometriaServico {

	private static final Logger LOGGER = LoggerFactory.getLogger(BiometriaServico.class);

	public void reconhecimentoImagem(byte[] bImg){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		
		try {
			String img = "/tmp/img.jpg";
		    FileOutputStream fos = new FileOutputStream(img);
		    fos.write(bImg);
		    fos.close();

		    CascadeClassifier cascadeClassifier = new CascadeClassifier("/opt/opencv_xml/haarcascade_frontalface_alt_tree.xml");
		    Mat mat = Imgcodecs.imread(img);
		    
		    MatOfRect matOfRect = detectarFaces(cascadeClassifier, mat);
		    
		    List propsFaces = obterDadosFaces(matOfRect);
		    
		    BufferedImage imagemCorteDesfoque = DesfocarImagem(mat);
		    
		    propsFaces = CortarImagem(propsFaces, imagemCorteDesfoque);
		    		    
		    BufferedImage imagemSemEfeitos = ArquivoUtils.converterParaImage(mat);
		    
		    imagemCorteDesfoque = ArquivoUtils.juntarImagens(propsFaces, imagemSemEfeitos);
		    
		    File outputfile = new File(img);
		    
		    try {
		    	ImageIO.write(imagemCorteDesfoque, "jpg", outputfile);
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		} catch (Exception e) {
	    	e.printStackTrace();
		}
	}
	
	//Método que detecta as faces
	public MatOfRect detectarFaces(CascadeClassifier cascadeClassifier, Mat mat){
		MatOfRect matOfRect = new MatOfRect();
		cascadeClassifier.detectMultiScale(mat, matOfRect);
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

	public BufferedImage DesfocarImagem(Mat mat){  
		mat = Desfocar(mat);
	  
		return ArquivoUtils.converterParaImage(mat);
	}
		 
	private Mat Desfocar(Mat image){     
		Mat destination = new Mat(image.rows(),image.cols(),image.type());
	         
		Imgproc.GaussianBlur(image, destination,new Size(45,45), 0);
	  
		return destination;
	}
	
	public List<propriedadesFaceDTO> CortarImagem(List<propriedadesFaceDTO> dados, BufferedImage imagem){
		  
		for(propriedadesFaceDTO dado : dados){
			dado.setImageCortada(imagem.getSubimage(dado.getX(), dado.getY(), dado.getWidth(), dado.getHeight()));
		}
		  
		return dados;
	}

}
