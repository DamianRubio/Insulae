package view;

import com.sun.javafx.application.LauncherImpl;

public class Main {

	/**
	 * Method that launches the whole program.
	 * @param args Params passed to the execution.
	 */
	public static void main(String[] args) {
		LauncherImpl.launchApplication(App.class, AppPreloader.class, args);
	}
}
