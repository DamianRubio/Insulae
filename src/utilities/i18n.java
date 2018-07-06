package utilities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

public final class i18n {

	private static final ObjectProperty<Locale> locale;
	static {
		locale = new SimpleObjectProperty<>(getDefaultLocale());
		locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
	}

	/**
	 * Retrieves the list of supported locales.
	 * 
	 * @return List containing the locales that are offered by the application.
	 */
	public static List<Locale> getSupportedLocales() {
		return new ArrayList<>(Arrays.asList(Locale.forLanguageTag("es"), Locale.ENGLISH, Locale.FRENCH));
	}

	/**
	 * Gets the system default locale.
	 * 
	 * @return The default locale if the application offers it, english lokale
	 *         otherwise.
	 */
	public static Locale getDefaultLocale() {
		Locale defaultLocale = Locale.getDefault();

		if (getSupportedLocales().contains(defaultLocale))
			return defaultLocale;
		return Locale.ENGLISH;
	}

	/**
	 * Gets the current locale.
	 * 
	 * @return Current locale.
	 */
	public static Locale getLocale() {
		return locale.get();
	}

	/**
	 * Sets the locale.
	 * 
	 * @param locale
	 *            Locale that is going to be set.
	 */
	public static void setLocale(Locale locale) {
		localeProperty().set(locale);
		Locale.setDefault(locale);
	}

	/**
	 * Gets the locale property.
	 * 
	 * @return Locale property.
	 */
	public static ObjectProperty<Locale> localeProperty() {
		return locale;
	}

	/**
	 * Gets a String from the language bundles.
	 * 
	 * @param key
	 *            Key of the string that is poing to be retrieved.
	 * @param args
	 *            Optional arguments for the string.
	 * @return String for the specified key and the current locale.
	 */
	public static String get(final String key, final Object... args) {
		ResourceBundle resource = ResourceBundle.getBundle("resources.bundles.lng", getLocale());
		return MessageFormat.format(resource.getString(key), args);
	}

	/**
	 * Returns a string binding for a specified key.
	 * 
	 * @param key
	 *            Key for which the bind is going to be created.
	 * @param args
	 *            Optional arguments for the string.
	 * @return String binding with the specified key.
	 */
	public static StringBinding createStringBinding(final String key, Object... args) {
		return Bindings.createStringBinding(() -> get(key, args), locale);
	}

	/**
	 * Returns a string binding for a specified key.
	 * 
	 * @param func
	 *            Function that return an string.
	 * @return String binding with the specified key.
	 */
	public static StringBinding createStringBinding(Callable<String> func) {
		return Bindings.createStringBinding(func, locale);
	}

	/**
	 * Return the label for a the combobox language.
	 * 
	 * @param key
	 *            Key of the string for the combobox.
	 * @return Label binded with the string of the specified key.
	 */
	public static Label labelForComboBox(final String key) {
		Label aux = new Label();
		aux.textProperty().bind(createStringBinding(key));
		return aux;
	}
}