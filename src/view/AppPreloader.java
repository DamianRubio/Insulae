package view;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppPreloader extends Preloader {
	private Stage preloaderStage;
	private Parent root;
	private Scene scene;
	 
    @Override
    public void start(Stage primaryStage) throws Exception {
    	this.preloaderStage = primaryStage;
    	this.preloaderStage.setScene(this.scene);
    	this.preloaderStage.setMaximized(true);
    	this.preloaderStage.setTitle("Insulae");
    	this.preloaderStage.getIcons().add(new Image("file:src/resources/img/icon.png"));
    	this.preloaderStage.show();
   }
    
    
    @Override
    public void init() throws Exception {
    	root = FXMLLoader.load(getClass().getResource("/resources/fxml/AppPreloader.fxml"));
        Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                scene = new Scene(root, 600, 400);
                scene.getStylesheets().add("/resources/style/appPreloader.css");
            }
        });
    }
    
    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof ProgressNotification) {
        	((ProgressIndicator) scene.lookup("#progressIndicator")).setProgress(((ProgressNotification) pn).getProgress());            
        } else if (pn instanceof StateChangeNotification) {
            if (((StateChangeNotification) pn).getType() == StateChangeNotification.Type.BEFORE_START) {
                preloaderStage.hide();
            }
        }
    }    
}
