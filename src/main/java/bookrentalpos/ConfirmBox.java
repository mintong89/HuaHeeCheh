package bookrentalpos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfirmBox {
    private boolean choice = false;

    // To use it: choice = new ConfirmBox().getChoice("message");
    public boolean getChoice(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UTILITY);
        window.setTitle("");
        window.setResizable(false);

        // Upper layout.
        HBox upperLayout = new HBox();
        upperLayout.getStyleClass().add("upper");
        Label label = new Label(message);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Bodoni MT", 20));
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        HBox.setMargin(label, new Insets(5, 5, 0, 5));
        upperLayout.getChildren().add(label);

        // Lower layout.
        GridPane lowerLayout = new GridPane();
        lowerLayout.getColumnConstraints().add(new ColumnConstraints(230));
        lowerLayout.getColumnConstraints().add(new ColumnConstraints(230));
        lowerLayout.getRowConstraints().add(new RowConstraints(75));

        Button cancel = new Button("Cancel");
        cancel.setPrefWidth(80);
        cancel.setOnAction(event -> {
            choice = false;
            window.close();
        });
        Button okay = new Button("Okay");
        okay.setPrefWidth(80);
        okay.setOnAction(event -> {
            choice = true;
            window.close();
        });

        GridPane.setMargin(cancel, new Insets(0, 0, 0, 75));
        GridPane.setMargin(okay, new Insets(0, 0, 0, 75));
        lowerLayout.add(cancel, 0, 0);
        lowerLayout.add(okay, 1, 0);

        // Set both to layout.
        int expendHeight = (message.length() / 46) * 30;

        GridPane layout = new GridPane();
        layout.getColumnConstraints().add(new ColumnConstraints(460));
        layout.getRowConstraints().add(new RowConstraints(75 + expendHeight));
        layout.getRowConstraints().add(new RowConstraints(75));

        layout.add(upperLayout, 0, 0);
        layout.add(lowerLayout, 0, 1);

        Scene scene = new Scene(layout, 460, 150 + expendHeight);
        scene.getStylesheets().add("/ManagerStyle.css");
        window.setScene(scene);
        window.showAndWait();

        return choice;
    }
}
