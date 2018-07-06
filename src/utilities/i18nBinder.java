package utilities;

import javafx.beans.binding.StringBinding;

public final class i18nBinder {
	
	/**
	 * Binds the error dialog title.
	 * @return StringBinding for the error dialog title.
	 */
	public static StringBinding bindErrorDialogTitle() {
		return i18n.createStringBinding("dialog.errorTitle");
	}

	/**
	 * Binds the error dialog header.
	 * @return StringBinding for the error dialog header.
	 */
	public static StringBinding bindErrorDialogHeader() {
		return i18n.createStringBinding("dialog.errorHeader");
	}

	/**
	 * Binds the error dialog content.
	 * @return StringBinding for the error dialog content.
	 */
	public static StringBinding bindErrorDialogContent() {
		return i18n.createStringBinding("dialog.errorContent");
	}

	/**
	 * Binds the error dialog button.
	 * @return StringBinding for the error dialog button.
	 */
	public static StringBinding bindErrorDialogButton() {
		return i18n.createStringBinding("dialog.errorButton");
	}

	/**
	 * Binds the about dialog title.
	 * @return StringBinding for the about dialog title.
	 */
	public static StringBinding bindAboutDialogTitle() {
		return i18n.createStringBinding("dialog.aboutTitle");
	}

	/**
	 * Binds the about dialog content.
	 * @return StringBinding for the about dialog content.
	 */
	public static StringBinding bindAboutDialogContent() {
		return i18n.createStringBinding("dialog.aboutContent");
	}

	/**
	 * Binds the about dialog button.
	 * @return StringBinding for the about dialog button.
	 */
	public static StringBinding bindAcceptDialogButton() {
		return i18n.createStringBinding("dialog.acceptButton");
	}

	/**
	 * Binds the about dialog cancel button.
	 * @return StringBinding for the about dialog cancel button.
	 */
	public static StringBinding bindCancelDialogButton() {
		return i18n.createStringBinding("dialog.cancelButton");
	}

	/**
	 * Binds the number of files dialog title.
	 * @return StringBinding for the number of files dialog title.
	 */
	public static StringBinding bindNumberOfFilesDialogTitle() {
		return i18n.createStringBinding("dialog.nFilesTitle");
	}

	/**
	 * Binds the number of files dialog header.
	 * @return StringBinding for the number of files dialog header.
	 */
	public static StringBinding bindNumberOfFilesDialogHeader() {
		return i18n.createStringBinding("dialog.nFilesHeader");
	}

	/**
	 * Binds the number of files dialog content.
	 * @return StringBinding for the number of files dialog content.
	 */
	public static StringBinding bindNumberOfFilesDialogContent() {
		return i18n.createStringBinding("dialog.nFilesContent");
	}

	/**
	 * Binds the source directory dialog title.
	 * @return StringBinding for the source directory dialog title.
	 */
	public static StringBinding bindScourceDirectoryDialogTitle() {
		return i18n.createStringBinding("dialog.sDirectoryTitle");
	}

	/**
	 * Binds the source directory dialog header.
	 * @return StringBinding for the source directory dialog header.
	 */
	public static StringBinding bindScourceDirectoryDialogHeader() {
		return i18n.createStringBinding("dialog.sDirectoryHeader");
	}

	/**
	 * Binds the source directory dialog content.
	 * @return StringBinding for the source directory dialog content.
	 */
	public static StringBinding bindScourceDirectoryDialogContent() {
		return i18n.createStringBinding("dialog.sDirectoryContent");
	}

	/**
	 * Binds the no connection dialog title.
	 * @return StringBinding for the no connection dialog title.
	 */
	public static StringBinding bindNoInternetDialogTitle() {
		return i18n.createStringBinding("dialog.noInternetTitle");
	}

	/**
	 * Binds the no connection dialog header.
	 * @return StringBinding for the no connection dialog header.
	 */
	public static StringBinding bindNoInternetDialogHeader() {
		return i18n.createStringBinding("dialog.noInternetHeader");
	}

	/**
	 * Binds the no connection dialog content.
	 * @return StringBinding for the no connection dialog content.
	 */
	public static StringBinding bindNoInternetDialogContent() {
		return i18n.createStringBinding("dialog.noInternetContent");
	}

}
