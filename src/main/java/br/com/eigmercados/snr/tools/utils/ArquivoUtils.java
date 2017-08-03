package br.com.eigmercados.snr.tools.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
	
	public static void desenharRetangulo(MatOfRect faces, Mat frame){
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
	}
	
	public static Mat escalaCinzaImagem(Mat mat){
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(mat, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		return grayFrame;
	}
	
	public static byte[] convertPNGinJPG(byte[] bImg) {
		try {
			String img = "/tmp/convert.png";
		    FileOutputStream fos = new FileOutputStream(img);
		    fos.write(bImg);
		    fos.close();
		    
		    File convert = new File(img);

			// read image file
			BufferedImage bufferedImage = ImageIO.read(convert);
			convert.delete();

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", outputStream);
			
			newBufferedImage.flush();
			byte[] imageInByte = outputStream.toByteArray();
			outputStream.close();
			
			return imageInByte;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bImg;
	}
}
