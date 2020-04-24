package ppmviewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Viewer extends Application {
    private Stage primaryStage;
    private ImageView imageView;
    private ViewerUpdateThread viewerUpdateThread;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("PPM Viewer");
        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 300);
        root.setBackground(new Background(
                new BackgroundFill(Color.DARKCYAN, null, null)));

        // Add menu
        Menu fileMenu = new Menu("File");
        MenuItem openFileMenuItem = new MenuItem("Open...");
        fileMenu.getItems().add(openFileMenuItem);
        MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.setUseSystemMenuBar(true);
        root.getChildren().add(menuBar);

        // Add Image Viewer
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Set up drag and drop for image viewer
        root.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        root.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File firstFile = db.getFiles().get(0);
                if (firstFile.canRead() && firstFile.getPath().toLowerCase().endsWith(".ppm")) {
                    bindImageFileToViewer(firstFile);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Set open image action
        openFileMenuItem.setOnAction(event -> {
            File ppmFile = getPpmFile();
            if (ppmFile != null)
                bindImageFileToViewer(ppmFile);
        });

        root.getChildren().add(imageView);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (viewerUpdateThread != null) {
            viewerUpdateThread.interrupt();
        }
    }

    private File getPpmFile() {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("Open Image");
        imageChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PPM Image", "*.ppm")
        );
        return imageChooser.showOpenDialog(primaryStage);
    }

    private void bindImageFileToViewer(File imageFile) {
        // Halt update thread if one exists
        if (viewerUpdateThread != null) {
            viewerUpdateThread.interrupt();
        }
        // Set the current image to the new image
        imageView.setImage(PpmConverter.convert(imageFile));

        // Create a new thread for this image
        viewerUpdateThread = new ViewerUpdateThread(imageView, imageFile);
        viewerUpdateThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
