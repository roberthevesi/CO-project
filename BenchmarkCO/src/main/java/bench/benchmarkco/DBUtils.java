package bench.benchmarkco;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        Parent root = null;

//        try{
//            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
//            root = loader.load();
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));



        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    public static void resize(String inputPath, String outputPath, int scaledWidth, int scaledHeight) throws IOException {
        File inputFile = new File(inputPath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        String formatName = outputPath.substring(outputPath.lastIndexOf(".")+1);

        ImageIO.write(outputImage, formatName, new File(outputPath));

        File outputFile = new File(outputPath);
        Desktop desktop = Desktop.getDesktop();
        desktop.open(outputFile);
    }

    public static void upscale(String inputPath, String outputPath, double scaleFactor) throws IOException {
        File inputFile = new File(inputPath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        int scaledWidth = (int)(inputImage.getWidth() * scaleFactor);
        int scaledHeight = (int)(inputImage.getHeight() * scaleFactor);

        resize(inputPath, outputPath, scaledWidth, scaledHeight);
    }

    public static void startBenchmark(ActionEvent event, String inputPath, double scaleFactor){
        String outputPath = "output.jpg";

        try{
            upscale(inputPath, outputPath, scaleFactor);
        }catch(IOException e){
            System.out.println("Error upscaling.");
            e.printStackTrace();
        }
    }
}
