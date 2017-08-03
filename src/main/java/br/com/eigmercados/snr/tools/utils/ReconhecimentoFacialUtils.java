package br.com.eigmercados.snr.tools.utils;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.nio.IntBuffer;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

/**
 * Baseado nos codigos: 
 * 
 * https://github.com/bytedeco/javacv/blob/master/samples/OpenCVFaceRecognizer.java
 * https://github.com/pathikrit/JFaceRecog
 */
public class ReconhecimentoFacialUtils {
    public static double reconhecerFaceImagens(File base, File[] imagesDB) {
        MatVector images = new MatVector(imagesDB.length);
        String[] names = new String[imagesDB.length];

        Mat labels = new Mat(imagesDB.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        // Pode ser utilizado para mapear mais de uma imgaem por pessoa
        int imgCount = 0, personCount = 0; 

        Mat matBase = imread(base.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);        
        for (File image : imagesDB) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            images.put(imgCount, img);
            labelsBuf.put(imgCount, personCount);

            imgCount++;
            names[personCount] = image.getName();
            personCount++;
        }

        //FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
        // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 150d);

        // Treinamento
        faceRecognizer.train(images, labels);

        IntPointer prediction = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        faceRecognizer.predict(matBase, prediction, confidence);
        
        if(prediction.get(0) >= 0 && prediction.get(0) < names.length && confidence.get(0)<80){
            System.out.println("Predicted label: " + names[prediction.get(0)]);
            return confidence.get(0);
        }
        
        System.out.println("Unrecognizable");
        return 0;
    }
}