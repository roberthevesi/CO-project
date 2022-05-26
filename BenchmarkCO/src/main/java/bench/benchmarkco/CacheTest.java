package bench.benchmarkco;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

public class CacheTest {

    @FXML
    private Button withButtonn;

    @FXML
    private Button withoutButtonn;

    @FXML
    private Label loopUnrollLabell;
    @FXML
    private TextField factorTextFieldd;

    @FXML
    private TextField widthTextFieldd;

    @FXML
    private Label widthLabell;

    @FXML
    private Label unrollingFactorLabell;

    @FXML
    private Label betterLabel;

    @FXML
    private Label SumLabel;

    @FXML
    private Label exceptionLabel;

    boolean isUnrolled;

    int counter;
    int result = 0;

    static final String DOUBLE_QUOTES = "\"";

    long timeTook;
    public void initialize(){
        makeInivisible();
        betterLabel.setVisible(false);
    }
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

    public void makeInivisible(){
        widthTextFieldd.setVisible(false);
        factorTextFieldd.setVisible(false);
        unrollingFactorLabell.setVisible(false);
        widthLabell.setVisible(false);
        exceptionLabel.setVisible(false);
        SumLabel.setVisible(false);
    }

    @FXML
    private void setWithButton(ActionEvent actionEvent) throws IOException{
        widthTextFieldd.setVisible(true);
        factorTextFieldd.setVisible(true);
        unrollingFactorLabell.setVisible(true);
        widthLabell.setVisible(true);
        loopUnrollLabell.setVisible(true);
        withButtonn.setVisible(true);
        withoutButtonn.setVisible(true);
        isUnrolled = true;
    }

    private int getFactor(){
        return Integer.parseInt(factorTextFieldd.getText());
    }

    private int getWidth(){
        return Integer.parseInt(widthTextFieldd.getText());
    }

//    private void GiuliasTest(int factor, int width){
//        widthLabell.setText("factor" + factor + " width" + width );
//    }

    @FXML
    private void startTest(ActionEvent actionEvent) throws IOException{
        int factor;
        if(isUnrolled)
            factor = getFactor();
        else
            factor = 0;
        int width = getWidth();
//        GiuliasTest(factor, width);
        ArrayList<Double> scoreList = new ArrayList<Double>();
        for(int i = 0; i < 10; i++){
            double score_run = run(width,factor);
            scoreList.add(score_run);
        }
        scoreList.stream().sorted();
        scoreList.remove(0);
        scoreList.remove(8);
        float mean = 0;
        int counter = 0;
        for(double score : scoreList){
            mean += score;
            counter++;
        }
        mean /= counter;

        System.out.println(result + " " + mean);
        if (result < 0){
            result *= -1;
        }
        SumLabel.setText("Sum is: " + result + ", with a score of " + mean + " points.");
        SumLabel.setVisible(true);

        ////// DB (mySQL) part

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = null;

        String unroll = null;
        if(isUnrolled)
            unroll = "with";
        else
            unroll = "without";

        try {
            connection = DriverManager.getConnection("jdbc:mysql://co-project-db.mysql.database.azure.com:3306/coprojectdb", "robert@co-project-db", "SantJmek1337!");
            preparedStatement = connection.prepareStatement("INSERT INTO cachetest (size, unrollfactor, score, type) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, width);
            preparedStatement.setInt(2, factor);
            preparedStatement.setFloat(3, mean);
            preparedStatement.setString(4, unroll);

            preparedStatement.executeUpdate();


            query = "SELECT * from cachetest WHERE size = " + width + " and type = " + DOUBLE_QUOTES + unroll + DOUBLE_QUOTES;
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            Integer kTotal = 0, kBetter = 0;
            Integer tempScore = 0;

            while(resultSet.next()){
                tempScore = resultSet.getInt("score");
                if(tempScore < (int) mean)
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
    public double run(Object... params) {
        result = 0;
        int unrollLevel;

        if(!isUnrolled){
            long startTime = System.nanoTime();
            result = recursive(2,getWidth(), 0);
            long stopTime = System.nanoTime();
            timeTook = stopTime - startTime;
        }
        if(isUnrolled) {
            unrollLevel = (Integer)params[1];
            long startTime = System.nanoTime();
            result = (int) recursiveUnrolled(2, unrollLevel, getWidth(), 0);
            long stopTime = System.nanoTime();
            timeTook = stopTime - startTime;
        }
        timeTook /= 1000;
        double score = 0;
        score = (double)counter/Math.sqrt(timeTook);
        SumLabel.setText("Sum is:" + result + "Your score is: " + String.format("%.2f",score));
        System.out.println("test");
        return score;
    }
    private int recursive(long start, long size, int counter) {
        this.counter = counter;
        long prime = findNextPrime(start);
        if(prime >= size || prime == 0)
            return 0;

        try {
            return (int) (prime + recursive(prime + 1, size, counter + 1));
        }catch (StackOverflowError e) {
            exceptionLabel.setText("Reached no : " + start + "/" + size + " after " + counter + " calls");
            exceptionLabel.setVisible(true);
            return 0;
        }
    }
    private long recursiveUnrolled(long start, int unrollLevel, long size, int counter) {
        this.counter = counter;
        long prime = start;
        long  tempSum = 0;
        for(int i = 0; i < unrollLevel && start <= size; i++) {
            prime = findNextPrime(start);
            start = prime + 1;

            if(prime > size || prime == 0)
                return tempSum;

            tempSum += prime;

        }

        try {
            return tempSum + recursiveUnrolled(prime+1, unrollLevel, size, counter + 1);
        }catch (StackOverflowError e) {
            exceptionLabel.setVisible(true);
            exceptionLabel.setText("Reached no : " + start + "/" + size + " at " + unrollLevel + " levels"+ " after " + counter + " calls");
            System.out.println("Reached no : " + start + "/" + size + " at " + unrollLevel + " levels"+ " after " + counter + " calls");
            return 0;
        }
    }
    private boolean isPrime(long start) {
        if(start <= 2)
            return true;
        for(int i = 2; i <= Math.sqrt(start); i++) {
            if(start % i == 0)
                return false;
        }
        return true;
    }
    private long findNextPrime(long start) {
        for(long i = start; i <= getWidth(); i++) {
            if(isPrime(i))
                return i;
        }
        return 0;
    }

    @FXML
    private void setWithoutButtonn(ActionEvent actionEvent) throws IOException{
        factorTextFieldd.setVisible(false);
        unrollingFactorLabell.setVisible(false);
        widthLabell.setVisible(true);
        widthTextFieldd.setVisible(true);
        isUnrolled = false;
    }


}
