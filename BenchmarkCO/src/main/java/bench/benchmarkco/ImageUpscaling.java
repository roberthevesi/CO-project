package bench.benchmarkco;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.awt.Desktop;


public class ImageUpscaling implements Initializable {
    @FXML
    private Button fileChooserButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button startButton;

    @FXML
    private Button removeFileButton;

    @FXML
    private Label fileChooserLabel;

    @FXML
    private Label upscaleFactorLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label outputLabel;

    @FXML
    private ImageView pointGif;

    @FXML
    private TextField upscaleFactor;

    @FXML
    private Label cpuInfo;

    List<String> listFile;

    public void goToDigitsOfPi(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.DigitsOfPi.class.getResource("digits-of-pi.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToHelp(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.DigitsOfPi.class.getResource("help.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void goToAboutUs(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.DigitsOfPi.class.getResource("about-us.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goHome(@org.jetbrains.annotations.NotNull ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.ImageUpscaling.class.getResource("hello-view.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToCacheTest(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.DigitsOfPi.class.getResource("cache-test.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooserLabel.setVisible(false);
        removeFileButton.setVisible(false);
        upscaleFactor.setVisible(false);
        upscaleFactorLabel.setVisible(false);
        scoreLabel.setVisible(false);
        outputLabel.setVisible(false);

        listFile = new ArrayList<>();

        listFile.add("*.jpg");
        listFile.add("*.jpeg");
        listFile.add("*.png");

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", listFile));
        final File[] f = new File[1];

        fileChooserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                f[0] = fc.showOpenDialog(null);

                if(f[0] != null){
                    fileChooserLabel.setText("Selected file: " + f[0].getAbsolutePath());
                    fileChooserLabel.setVisible(true);
                    removeFileButton .setVisible(true);
                    upscaleFactor.setVisible(true);
                    upscaleFactorLabel.setVisible(true);
                    pointGif.setVisible(false);

                }
            }
        });

        removeFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                f[0] = null;
                fileChooserLabel.setVisible(false);
                removeFileButton.setVisible(false);
                upscaleFactor.setVisible(false);
                upscaleFactorLabel.setVisible(false);
                scoreLabel.setVisible(false);
                outputLabel.setVisible(false);
                pointGif.setVisible(true);
            }
        });

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!upscaleFactor.getText().equals("") && f[0] != null){
                    System.out.println("Started.");

                    long startTime = System.nanoTime();
                    DBUtils.startBenchmark(event, f[0].getAbsolutePath(), Double.parseDouble(upscaleFactor.getText()));
                    long endTime = System.nanoTime();

                    long duration = (endTime-startTime)/1000000;
                    System.out.println(duration + " ms");

                    File inputFile = new File(f[0].getAbsolutePath());
                    BufferedImage inputImage = null;
                    try {
                        inputImage = ImageIO.read(inputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    long totalInitialPixels = inputImage.getHeight() * inputImage.getWidth();
                    long score = (long) ((totalInitialPixels/duration)*totalInitialPixels* Double.parseDouble(upscaleFactor.getText()));
                    score = score / totalInitialPixels;

                    scoreLabel.setVisible(true);
                    outputLabel.setVisible(true);

                    int percent = 0;

                    Connection connection = null;
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    String query = null;

                    int scaledWidth = inputImage.getWidth()*Integer.parseInt(upscaleFactor.getText());
                    int scaledHeight = inputImage.getHeight()*Integer.parseInt(upscaleFactor.getText());


                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://co-project-db.mysql.database.azure.com:3306/coprojectdb", "robert@co-project-db", "SantJmek1337!");
                        preparedStatement = connection.prepareStatement("INSERT INTO imageupscaling (width, height, score) VALUES (?, ?, ?)");
                        preparedStatement.setInt(1, scaledWidth);
                        preparedStatement.setInt(2, scaledHeight);
                        preparedStatement.setInt(3, (int)score);

                        preparedStatement.executeUpdate();


                        query = "SELECT * from imageupscaling WHERE width = " + scaledWidth + " and height = " + scaledHeight;
                        preparedStatement = connection.prepareStatement(query);
                        resultSet = preparedStatement.executeQuery();

                        Integer kTotal = 0, kBetter = 0;
                        Integer tempScore = 0;

                        while(resultSet.next()){
                            tempScore = resultSet.getInt("score");
                            if(tempScore < (int) score)
                                kBetter++;
                            kTotal++;
                        }

                        percent = 0;
                        if(kTotal == 1)
                            percent = 100;
                        else
                            percent = (int) (((double)kBetter/((double)kTotal-1))*100);

                        scoreLabel.setText("Your score is: " + score + " points, better than " + percent + "% of all alike benchmarks.");
                        outputLabel.setText("Image has been outputted to output.jpg.");
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                    finally{
                        if(preparedStatement != null){
                            try{
                                preparedStatement.close();
                            }
                            catch(SQLException e){
                                e.printStackTrace();
                            }
                        }
                        if(connection != null){
                            try{
                                connection.close();
                            }
                            catch(SQLException e){
                                e.printStackTrace();
                            }
                        }
                    }


                }
                else{
                    System.out.println("Not all info been provided.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please provide both file and upscale factor.");
                    alert.show();
                }
            }
        });
    }
}
