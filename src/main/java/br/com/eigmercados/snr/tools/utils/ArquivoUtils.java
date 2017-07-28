package br.com.eigmercados.snr.tools.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import br.com.eigmercados.snr.biometria.dto.propriedadesFaceDTO;

@Component
public class ArquivoUtils {	
	public static BufferedImage converterParaImage(Mat image){
		MatOfByte bytemat = new MatOfByte();
		
		Imgcodecs.imencode(".jpg", image, bytemat);
		
		byte[] bytes = bytemat.toArray();
		
		InputStream in = new ByteArrayInputStream(bytes);
		
		BufferedImage img=null;
	
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return img;
	}
	
	public static BufferedImage juntarImagens(List<propriedadesFaceDTO> dados, BufferedImage imagemPrincipal){
		  
		for(propriedadesFaceDTO dado: dados){
			imagemPrincipal = juntarUmaImage(imagemPrincipal, dado.getImageCortada(),dado.getX(),dado.getY());
		}
  
		return imagemPrincipal;
	}
	
	public static BufferedImage juntarUmaImage(BufferedImage imagemPrincipal,
		BufferedImage imagemCortada, int x, int y) {
		 
		Graphics2D g = imagemPrincipal.createGraphics();
		        
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        
		g.drawImage(imagemPrincipal, 0,0, null);
		 
		g.drawImage(imagemCortada, x, y, null);
		 
		g.dispose();
		return imagemPrincipal;
	}
}
