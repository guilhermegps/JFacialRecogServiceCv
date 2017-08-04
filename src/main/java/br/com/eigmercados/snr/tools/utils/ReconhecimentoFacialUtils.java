package br.com.eigmercados.snr.tools.utils;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.List;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

/**
 * Baseado nos codigos: 
 * 
 * https://github.com/bytedeco/javacv/blob/master/samples/OpenCVFaceRecognizer.java
 * https://github.com/pathikrit/JFaceRecog
 */
public class ReconhecimentoFacialUtils {
	public static double reconhecerFaceImagens(byte[] base, List<byte[]> imagesDB) {
		File baseF = gravarArquivoTemp(base);
		File imagesDBF[] = new File[imagesDB.size()];
		
		for(int i=0; i<imagesDB.size(); i++){
			imagesDBF[i] = gravarArquivoTemp(imagesDB.get(i));
		}
		
		double result = reconhecerFaceImagens(baseF, imagesDBF);
		
		if(baseF.exists())
			baseF.delete();
		
		for(File f : imagesDBF){
			if(f.exists())
				f.delete();
		}
		
		return result;
	}
	
    public static double reconhecerFaceImagens(File base, File[] imagesDB) {
        MatVector images = new MatVector(imagesDB.length);
        String[] names = new String[imagesDB.length];

        Mat labels = new Mat(imagesDB.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        // Pode ser utilizado para mapear mais de uma imgaem por pessoa
        int imgCount = 0, personCount = 0; 

        Mat matBase = imread(base.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);  
        matBase = redimensionarMat(matBase);
        for (File image : imagesDB) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            
            img = redimensionarMat(img);

            images.put(imgCount, img);
            labelsBuf.put(imgCount, personCount);

            imgCount++;
            names[personCount] = image.getName();
            personCount++;
        }

        FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
        // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        //FaceRecognizer faceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 150d);

        // Treinamento
        faceRecognizer.train(images, labels);

        IntPointer prediction = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        faceRecognizer.predict(matBase, prediction, confidence);
        
        if(prediction.get(0) >= 0 && prediction.get(0) < names.length /*&& confidence.get(0)<8*/){
            System.out.println("Predicted label: " + names[prediction.get(0)]);
            return confidence.get(0);
        }
        
        System.out.println("Unrecognizable");
        return confidence.get(0);
    }
    
    private static Mat redimensionarMat(Mat img){        
		Size sz = new Size(250,250);
        resize( img, img, sz );    	
        
        return img;
    }
    
    private static File gravarArquivoTemp(byte[] arq){
		String caminho = "/tmp/face_" + java.util.UUID.randomUUID() + "_tmp.jpg";
		
    	try{
		    FileOutputStream fos = new FileOutputStream(caminho);
		    fos.write(arq);
		    fos.close();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
	    
	    return new File(caminho);
    }
}