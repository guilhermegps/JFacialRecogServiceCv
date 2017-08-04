package br.com.eigmercados.snr.tools.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
	
	//Método que detecta as faces
	public static MatOfRect detectarFaces(CascadeClassifier cascadeClassifier, Mat mat){
		MatOfRect matOfRect = new MatOfRect();
		
		Mat grayFrame = ArquivoUtils.escalaCinzaImagem(mat);
		
		cascadeClassifier.detectMultiScale(grayFrame, matOfRect);
//		cascadeClassifier.detectMultiScale(mat, matOfRect, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(), new Size());
		return matOfRect;
	}
	
	// Método que retorna a posição X e Y das faces
	public static List<propriedadesFaceDTO> obterDadosFaces(MatOfRect matOfRect){
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
	
	public static List<byte[]> extrairFaces(byte[] img, CascadeClassifier cascadeClassifier, Mat mat) {
	    MatOfRect matOfRect = detectarFaces(cascadeClassifier, mat);
    	List<propriedadesFaceDTO> propFaces = obterDadosFaces(matOfRect);
    	
    	return extrairFaces(img, propFaces);
	}
	
	public static List<byte[]> extrairFaces(byte[] img, List<propriedadesFaceDTO> propFaces) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(img);
	    List<byte[]> faces = new ArrayList<>();
	    
	    try {	    	
	        Size sz = new Size(250,250);
			BufferedImage in = ImageIO.read(bais);
			for(propriedadesFaceDTO propFace : propFaces){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( in.getSubimage(propFace.getX(), propFace.getY(), propFace.getWidth(), propFace.getHeight()), "jpg", baos );
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				baos.close();
				
				faces.add(imageInByte);
			}
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
		
		return faces;
	}

	public static byte[] getBytes(File file) {
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
