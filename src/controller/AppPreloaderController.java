package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;

public class AppPreloaderController implements Initializable {

	@FXML
	private ProgressIndicator progressIndicator;

	/**
	 * Gets the progress indicator on the interface.
	 * 
	 * @return Progress indicator.
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	class pb_thread implements Runnable {

		@Override
		public void run() {
		}

	}
}
