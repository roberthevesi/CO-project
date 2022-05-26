package bench.benchmarkco;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {
    @FXML
    private Label mainLabel;

    @FXML
    private Button imageUpscalingBtn;

    @FXML
    private Button digitsOfPiBtn;

    @FXML
    private Button aboutUsBtn;

    @FXML
    private Button cacheTestBtn;

    public void goToDigitsOfPi(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.DigitsOfPi.class.getResource("digits-of-pi.fxml"));
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

    public void goToImageUpscaling(@org.jetbrains.annotations.NotNull ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(bench.benchmarkco.ImageUpscaling.class.getResource("image-upscaling.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToCacheTest(@org.jetbrains.annotations.NotNull ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(ImageUpscaling.class.getResource("cache-test.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }
}