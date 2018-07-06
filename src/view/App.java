package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import controller.AppController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utilities.i18nBinder;

public class App extends Application {

	private Properties propertiesLoaded;
	private InputStream input;
	private AppController appController;
	private FXMLLoader loader;
	private Parent root;
	private static App appInstance;
	private Stage primaryStage;

	public App() {
		appInstance = this;
	}

	/**
	 * Retrieves the instance for the App singleton.
	 * 
	 * @return The current instance of the application.
	 */
	public static App getInstance() {
		if (appInstance == null) {
			appInstance = new App();
		}
		return appInstance;
	}

	@Override
	public void init() throws InterruptedException {
		notifyMyPreloader(0.0);
		String directory = "";
		int numberOfFilesToPreserve = -1;
		int searchMode = -1;

		try {
			loader = new FXMLLoader(getClass().getResource("/resources/fxml/App.fxml"));
			root = (Parent) loader.load();
			appController = (AppController) loader.getController();

			this.propertiesLoaded = new Properties();
//			this.input = new FileInputStream("./insulaeConfig.properties"); // Version for creating the .jar
			this.input = new FileInputStream("src/resources/insulaeConfig.properties");
			propertiesLoaded.load(this.input);
			this.input.close();
			directory = propertiesLoaded.getProperty("sourceDirectory");
			numberOfFilesToPreserve = Integer.parseInt(propertiesLoaded.getProperty("numberOfFilesToPreserve"));
			searchMode = Integer.parseInt(propertiesLoaded.getProperty("searchMode"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		appController.manageFiles(directory, numberOfFilesToPreserve);
		appController.setSearchMode(searchMode);
		notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		if (appController.isFileAvailable()) {
			Scene scene = new Scene(root);
			scene.getStylesheets().add("/resources/style/app.css");
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.setTitle("Insulae");
			primaryStage.getIcons().add(new Image("file:src/resources/img/icon.png"));
			primaryStage.show();
		} else
			notFileAvailable(primaryStage);
	}

	/**
	 * Sets the interface when there is not a file available.
	 * 
	 * @param primaryStage
	 *            Stage on which the visualization is going to be set.
	 */
	private void notFileAvailable(Stage primaryStage) {
		ButtonType acceptButton = new ButtonType(i18nBinder.bindErrorDialogButton().get(), ButtonBar.ButtonData.OTHER);
		Alert alert = new Alert(AlertType.ERROR, "", acceptButton);
		alert.titleProperty().bind(i18nBinder.bindErrorDialogTitle());
		alert.headerTextProperty().bind(i18nBinder.bindErrorDialogHeader());
		alert.contentTextProperty().bind(i18nBinder.bindErrorDialogContent());

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:src/resources/img/icon.png"));
		alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
			@Override
			public void handle(DialogEvent t) {
				changeScourceDirectory();
			}

		});

		Region backgroundHider = new Region();
		backgroundHider.setStyle("-fx-background-color: #F4F5F0; -fx-opacity: 0.5;");

		this.root = new StackPane(this.root, backgroundHider);
		Scene scene = new Scene(root);

		scene.getStylesheets().add("/resources/style/app.css");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/resources/style/dialog.css");
		dialogPane.getStyleClass().add("errorDialog");
		alert.show();
	}

	/**
	 * Notifies the preloader of the changes in the progress indicator.
	 * 
	 * @param double_val
	 *            Double containing the current value of the preloader.
	 */
	public void notifyMyPreloader(double double_val) {
		notifyPreloader(new Preloader.ProgressNotification(double_val));
	}

	/**
	 * Stores the consumption mode on the configuration file.
	 * 
	 * @param userData
	 *            Int representing the consumption mode that are going to be seted.
	 */
	public void saveSearchMode(int userData) {
		try {
			this.propertiesLoaded = new Properties();
//			this.input = new FileInputStream("./insulaeConfig.properties"); // Version for creating the .jar
			this.input = new FileInputStream("src/resources/insulaeConfig.properties");
			propertiesLoaded.load(this.input);
			this.input.close();
			propertiesLoaded.setProperty("searchMode", String.valueOf(userData));
//			propertiesLoaded.store(new FileOutputStream("./insulaeConfig.properties"), null); // Version for creating the .jar
			propertiesLoaded.store(new FileOutputStream("src/resources/insulaeConfig.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the interface for the visualization of the about dialog.
	 */
	public void showAbout() {
		ButtonType acceptButton = new ButtonType(i18nBinder.bindErrorDialogButton().get(), ButtonBar.ButtonData.OTHER);
		Alert alert = new Alert(AlertType.INFORMATION, "", acceptButton);
		alert.titleProperty().bind(i18nBinder.bindAboutDialogTitle());
		alert.setHeaderText(null);
		alert.contentTextProperty().bind(i18nBinder.bindAboutDialogContent());

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/resources/style/dialog.css");
		dialogPane.getStyleClass().add("errorDialog");

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:src/resources/img/icon.png"));

		alert.showAndWait();

	}

	/**
	 * Sets the interface for the visualization of the dialog on which the user can
	 * change the number of files to preserve.
	 */
	public void changeNumberOfFiles() {
		TextInputDialog dialog = new TextInputDialog(getNumberOFFilesToPreserve());

		Button acceptButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		acceptButton.textProperty().bind(i18nBinder.bindAcceptDialogButton());

		Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancelButton.textProperty().bind(i18nBinder.bindCancelDialogButton());

		dialog.titleProperty().bind(i18nBinder.bindNumberOfFilesDialogTitle());
		dialog.headerTextProperty().bind(i18nBinder.bindNumberOfFilesDialogHeader());
		dialog.contentTextProperty().bind(i18nBinder.bindNumberOfFilesDialogContent());

		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:src/resources/img/icon.png"));

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
			return;
		else if (!isInteger(result.get()))
			changeNumberOfFiles();
		else
			saveNumberOfFilesToPreserve(result.get());
	}

	/**
	 * Gets the current number of files to preserve in the configuration file.
	 * 
	 * @return Current number of files to preserve.
	 */
	private String getNumberOFFilesToPreserve() {
		return propertiesLoaded.getProperty("numberOfFilesToPreserve");
	}

	/**
	 * Stores the number of files to preserve in the configuration file.
	 * 
	 * @param str
	 *            String containing the number of files that wants to be preserved.
	 */
	private void saveNumberOfFilesToPreserve(String str) {
		try {
			this.propertiesLoaded = new Properties();
//			this.input = new FileInputStream("./insulaeConfig.properties"); // Version for creating the .jar
			this.input = new FileInputStream("src/resources/insulaeConfig.properties");
			propertiesLoaded.load(this.input);
			this.input.close();
			propertiesLoaded.setProperty("numberOfFilesToPreserve", str);
//			propertiesLoaded.store(new FileOutputStream("./insulaeConfig.properties"), null); // Version for creating the .jar
			propertiesLoaded.store(new FileOutputStream("src/resources/insulaeConfig.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if an string can be parsed into an integer.
	 * 
	 * @param input
	 *            String that is needed to be checked.
	 * @return Ttrue if the string can be parsed to an integer, false otherwise.
	 */
	private static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the interface for the visualization of the dialog on which the user can
	 * change the source directory.
	 */
	public void changeScourceDirectory() {
		TextInputDialog dialog = new TextInputDialog(getSourceDirectory());

		Button acceptButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		acceptButton.textProperty().bind(i18nBinder.bindAcceptDialogButton());

		Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancelButton.textProperty().bind(i18nBinder.bindCancelDialogButton());

		dialog.titleProperty().bind(i18nBinder.bindScourceDirectoryDialogTitle());
		dialog.headerTextProperty().bind(i18nBinder.bindScourceDirectoryDialogHeader());
		dialog.contentTextProperty().bind(i18nBinder.bindScourceDirectoryDialogContent());

		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:src/resources/img/icon.png"));

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
			return;
		else if (!isValidDirectory(result.get()))
			changeScourceDirectory();
		else {
			saveSourceDirectory(result.get());
			manageFiles();
			primaryStage.close();
			Platform.runLater(() -> {
				try {
					new App().init();
					getInstance().start(new Stage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * Calls the application controller manage files instruction.
	 */
	private void manageFiles() {
		appController.manageFiles(getSourceDirectory(), Integer.parseInt(getNumberOFFilesToPreserve()));
	}

	/**
	 * Gets the current source directory in the configuration file.
	 * 
	 * @return Current source directory.
	 */
	private String getSourceDirectory() {
		try {
			this.propertiesLoaded = new Properties();
//			this.input = new FileInputStream("./insulaeConfig.properties"); // Version for creating the .jar
			this.input = new FileInputStream("src/resources/insulaeConfig.properties");
			propertiesLoaded.load(this.input);
			return propertiesLoaded.getProperty("sourceDirectory");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Stores the source directory in the configuration file.
	 * 
	 * @param str
	 *            String containing the source directory link.
	 */
	private void saveSourceDirectory(String str) {
		try {
//			this.input = new FileInputStream("./insulaeConfig.properties"); // Version for creating the .jar
			this.input = new FileInputStream("src/resources/insulaeConfig.properties");
			propertiesLoaded.load(this.input);
			propertiesLoaded.setProperty("sourceDirectory", str);
//			propertiesLoaded.store(new FileOutputStream("./insulaeConfig.properties"), null); // Version for creating the .jar
			propertiesLoaded.store(new FileOutputStream("src/resources/insulaeConfig.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if a link is a valid directory.
	 * 
	 * @param str
	 *            String containing the link to a supposed directory.
	 * @return True if the string is a directory link, false otherwise.
	 */
	private boolean isValidDirectory(String str) {
		File file = new File(str);
		return file.isDirectory();
	}

	/**
	 * Sets the interface for the visualization of the dialog on which the user is
	 * informed that there is no connection.
	 */
	public void noInternetConnection() {
		ButtonType acceptButton = new ButtonType(i18nBinder.bindErrorDialogButton().get(), ButtonBar.ButtonData.OTHER);
		Alert alert = new Alert(AlertType.ERROR, "", acceptButton);
		alert.titleProperty().bind(i18nBinder.bindNoInternetDialogTitle());
		alert.headerTextProperty().bind(i18nBinder.bindNoInternetDialogHeader());
		alert.contentTextProperty().bind(i18nBinder.bindNoInternetDialogContent());

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:src/resources/img/icon.png"));

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/resources/style/dialog.css");
		dialogPane.getStyleClass().add("errorDialog");
		alert.show();
	}
}
