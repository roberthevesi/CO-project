package bench.benchmarkco;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DigitsOfPi {
    @FXML
    private Label scoreResult;

    @FXML
    private TextField mainTextField;

    @FXML
    private Button getdigits;

    @FXML
    private Label labelpi;

    @FXML
    private Label betterLabel;

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

    public void setGetdigits(@org.jetbrains.annotations.NotNull ActionEvent actionEvent) throws IOException {
        scoreResult.setVisible(true);
        labelpi.setVisible(true);
        betterLabel.setVisible(false);

        int nb_of_digits = Integer.parseInt(mainTextField.getText());
        PiSpigot spigot = new PiSpigot();
        spigot.digits_requested = nb_of_digits;
        ArrayList<Integer> score = new ArrayList<Integer>();
        for (int i = 0; i <= 10; i++) {
            long start = System.nanoTime();
            spigot.piString = "";
            spigot.run();
            long end = System.nanoTime();
            long elapsedTime = (end - start) / 10000;
//            System.out.println("elapsedTime:" + elapsedTime);
            double individualScore = nb_of_digits / Math.sqrt(elapsedTime);
//            System.out.println("Score la interatia " + i + "este:" + individualScore);
            score.add(i, (int) individualScore);
        }
        double sum = 0;
        for (double individualScore : score){
            sum += individualScore;
        }
        double finalScore = sum / 10;
        score.clear();
        scoreResult.setText("Your final score is: " + (int)finalScore + " points");
        labelpi.setText(spigot.piString);



        ////// DB (mySQL) part

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://co-project-db.mysql.database.azure.com:3306/coprojectdb", "robert@co-project-db", "SantJmek1337!");
            preparedStatement = connection.prepareStatement("INSERT INTO digitsofpi (numberofdigits, score) VALUES (?, ?)");
            preparedStatement.setInt(1, nb_of_digits);
            preparedStatement.setInt(2, (int) finalScore);

            preparedStatement.executeUpdate();

            query = "SELECT * from digitsofpi WHERE numberofdigits = " + nb_of_digits;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();


            Integer kTotal = 0, kBetter = 0;
            Integer tempScore = 0;

            while(resultSet.next()){
                tempScore = resultSet.getInt("score");
                if(tempScore < (int)finalScore)
                    kBetter++;
                kTotal++;
            }

            int percent = 0;
            if(kTotal == 1)
                percent = 100;
            else
                percent = (int) (((double)kBetter/((double)kTotal-1))*100);
            betterLabel.setText("better than " + percent + "% of all alike benchmarks");
            betterLabel.setVisible(true);
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

    public void initialize() {
        scoreResult.setVisible(false);
        labelpi.setVisible(false);
    }
}