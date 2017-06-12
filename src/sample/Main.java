package sample; /**
 * Created by Jakub Kowalski on 29.05.2017.
 */

import com.javonet.JavonetException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage)throws JavonetException {

        VBox pane = new VBox();
        TextField field = new TextField();
        final Label message = new Label("");
        final Label everythingWork = new Label("");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose file to zip");
        File defaultDirectory = new File("C:\\Users\\Jakub Kowalski\\IdeaProjects\\JavonetApp\\");

        Button buttonToChooseDirectory = new Button("Select folder to zip");
        buttonToChooseDirectory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                directoryChooser.setInitialDirectory(defaultDirectory);
                File selectedDirectory = directoryChooser.showDialog(stage);

                String a = selectedDirectory.getAbsolutePath();
                message.setText(selectedDirectory.getAbsolutePath());
                message.setTranslateY(110);
                message.setTranslateX(55);

                Zipper zip = new Zipper();
                field.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {

                        if(e.getCode()== KeyCode.ENTER) {
                            try {
                                zip.zipIt(a,field.getText());
                                everythingWork.setText("Everything work");
                                everythingWork.setTranslateX(160);
                                everythingWork.setTranslateY(180);
                            } catch (JavonetException e1) {
                                e1.printStackTrace();
                            }
                        }}
                }); //smth like "Scanner" but from GUI

            }

        });
        field.setTranslateY(180);
        field.setMaxWidth(400);

        buttonToChooseDirectory.setTranslateX(150);
        buttonToChooseDirectory.setTranslateY(100);

        Button enterZip = new Button("Name your zip and press ENTER");
        enterZip.setTranslateX(110);
        enterZip.setTranslateY(90);

        pane.getChildren().addAll(buttonToChooseDirectory,message,field,enterZip,everythingWork);
        stage.setResizable(false);
        stage.setScene(new Scene(pane, 400, 300));
        stage.setTitle("ZipYourFile");
        stage.show();
        System.out.println("Welcome to Java app launching .NET zipping");
    }
}
