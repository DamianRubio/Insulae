package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.sun.javafx.charts.Legend;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.QueryPoint;
import model.DataRetrieving.FileReader;
import model.DataVisualizing.MwpVariable;
import model.DataVisualizing.SwhVariable;
import model.DataVisualizing.WsVariable;
import model.FileManaging.FileManager;
import utilities.MeteoUtil;
import utilities.i18n;
import view.App;

public class AppController implements Initializable {

	private File dataFile;
	private double auxLat0ForMap;
	private double auxLat1ForMap;
	private double auxLon0ForMap;
	private double auxLon1ForMap;

	@FXML
	private Label coordenatesLabel;

	@FXML
	private Label languageLabel;

	@FXML
	private Label checkboxLabel;

	@FXML
	private Label errorLabel;

	@FXML
	private Menu fileMenu;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private MenuItem exitMenuItem;

	@FXML
	private Menu toolsMenu;

	@FXML
	private Menu searchModeMenu;

	@FXML
	private Menu helpMenu;

	@FXML
	private Menu settingsMenu;

	@FXML
	private MenuItem directoryMenuItem;

	@FXML
	private MenuItem numberOfFilesMenuItem;

	@FXML
	private Menu comparisonModeMenu;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private MenuItem pickMapPointMenu;

	@FXML
	private Button acceptMapButton;

	@FXML
	private Button cancelMapButton;

	@FXML
	private BorderPane mapBorderPane;

	@FXML
	private GoogleMapView googleMapView;

	@FXML
	private Label selectedPointLabel;

	@FXML
	private Label outputLabel;

	@FXML
	private Label consumeModeLabel;

	@FXML
	private Label infoTextLabel;

	@FXML
	private Label availableInfoLabel;

	@FXML
	private VBox infoVBox;

	@FXML
	private ComboBox<String> languageComboBox;

	@FXML
	private TextField latText;

	@FXML
	private TextField lonText;

	@FXML
	private LineChart<String, Double> mwpChart;

	@FXML
	private CheckBox mwpCheckBox;

	@FXML
	private LineChart<String, Double> swhChart;

	@FXML
	private CheckBox swhCheckBox;

	@FXML
	private VBox userInputVBox;

	@FXML
	private Button visualizeButton;

	@FXML
	private LineChart<String, Double> windChart;

	@FXML
	private CheckBox windCheckBox;

	@FXML
	private RadioMenuItem proximitySearchModeMenu;

	@FXML
	private RadioMenuItem prudentSearchModeMenu;

	@FXML
	private RadioMenuItem noComparisonModeMenu;

	@FXML
	private RadioMenuItem yesComparisonModeMenu;

	@FXML
	private Separator querySeparator;

	@FXML
	private ComboBox<String> queryComboBox;

	@FXML
	private Button queryButton;

	@FXML
	private ScrollPane graphsScrollPane;

	private double lat;
	private double lon;
	private ToggleGroup tgSearchMode;
	private ToggleGroup tgComparisonMode;
	private List<QueryPoint> queriesMade;
	private GoogleMap map;
	private LatLong mapCoordinates = null;

	// Model Entities
	private FileManager fm;
	private FileReader fr;

	/**
	 * Delegates the consumption of the checked variables depending of the search
	 * mode configured.
	 * 
	 * @param qPoint
	 *            The querypoint that wants to be consumed.
	 */
	private void consumeCheckedVariables(QueryPoint qPoint) {
		if (Integer.valueOf(tgComparisonMode.getSelectedToggle().getUserData().toString()) == 0)
			consumeOneQueryPoint(qPoint);
		else
			consumeAllPoints(qPoint);
	}

	/**
	 * Consumes the data of one querypoint.
	 * 
	 * @param qPoint
	 *            The querypoint that wants to be consumed.
	 */
	private void consumeOneQueryPoint(QueryPoint qPoint) {

		if (swhCheckBox.isSelected()) {
			fr.getSwhData(qPoint);
			if (qPoint.getSwhData() == null)
				notSeaPoint(qPoint.getLatitude(), qPoint.getLongitude());
			else {
				populateOneChart(this.swhChart, qPoint);
				swhChart.setVisible(true);
				swhChart.setManaged(true);
			}
		}
		if (mwpCheckBox.isSelected()) {
			fr.getMwpData(qPoint);
			if (qPoint.getMwpData() == null)
				notSeaPoint(qPoint.getLatitude(), qPoint.getLongitude());
			else {
				populateOneChart(this.mwpChart, qPoint);
				mwpChart.setVisible(true);
				mwpChart.setManaged(true);
			}
		}
		if (windCheckBox.isSelected()) {
			fr.getWindData(qPoint);
			if (qPoint.getWindData() == null)
				notSeaPoint(qPoint.getLatitude(), qPoint.getLongitude());
			else {
				populateOneChart(this.windChart, qPoint);
				windChart.setVisible(true);
				windChart.setManaged(true);
			}
		}
	}

	/**
	 * Consumes the data of all the querypoints made.
	 * 
	 * @param qPoint
	 *            The last werypoint that wants to be consumed.
	 */
	private void consumeAllPoints(QueryPoint qPoint) {
		double latPos = qPoint.getLatitude();
		double lonPos = MeteoUtil.getInstance().toRegularCoordinate(qPoint.getLongitude());

		List<SwhVariable> aux_swh = null;
		List<MwpVariable> aux_mwp = null;
		List<WsVariable> aux_wind = null;

		if (swhCheckBox.isSelected()) {
			fr.getSwhData(qPoint);
			aux_swh = qPoint.getSwhData();
		}
		if (mwpCheckBox.isSelected()) {
			fr.getMwpData(qPoint);
			aux_mwp = qPoint.getMwpData();
		}
		if (windCheckBox.isSelected()) {
			fr.getWindData(qPoint);
			aux_wind = qPoint.getWindData();
		}
		if (aux_swh == null && aux_mwp == null && aux_wind == null) {
			notSeaPoint(latPos, lonPos);
			queryComboBox.getItems().remove(queryComboBox.getItems().size() - 1);
			queriesMade.remove(qPoint);
			return;
		} else
			populateAllCharts();
	}

	/**
	 * Sets the state of the interface when the point is not a sea point.
	 * 
	 * @param latPos
	 *            The latitude of the point.
	 * @param lonPos
	 *            The longitude of the point.
	 */
	private void notSeaPoint(double latPos, double lonPos) {
		outputLabel.setVisible(false);
		outputLabel.setManaged(false);
		consumeModeLabel.setVisible(false);
		consumeModeLabel.setManaged(false);
		infoTextLabel.textProperty().bind(i18n.createStringBinding("label.infoTextLabelNotSeaPoint", latPos, lonPos));
		infoTextLabel.setStyle("-fx-text-fill: #E8850C;");
		infoTextLabel.setVisible(true);
		infoTextLabel.setManaged(true);
		infoVBox.setManaged(true);
		infoVBox.setVisible(true);
	}

	/**
	 * Populates one exiting chart with the data of a querypoint.
	 * 
	 * @param lChart
	 *            The chart that wants to be populated.
	 * @param qp
	 *            The querypoint from which the data is going to be obtained.
	 */
	private void populateOneChart(LineChart<String, Double> lChart, QueryPoint qp) {
		List<Date> datesList = this.fr.getFileDates();
		lChart.getData().clear();
		lChart.setLegendVisible(false);
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");

		XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();
		List<Tooltip> aux_toolList = new ArrayList<>();
		for (int i = 0; i < datesList.size(); i++) {
			XYChart.Data<String, Double> data = null;
			Tooltip t = null;
			if (lChart == this.swhChart && qp.getSwhData() != null) {
				data = new XYChart.Data<String, Double>(df.format(datesList.get(i)), qp.getSwhData().get(i).getValue());
				t = new Tooltip(qp.getSwhData().get(i).toString());
			} else if (lChart == this.mwpChart && qp.getMwpData() != null) {
				data = new XYChart.Data<String, Double>(df.format(datesList.get(i)), qp.getMwpData().get(i).getValue());
				t = new Tooltip(qp.getMwpData().get(i).toString());
			} else if (lChart == this.windChart && qp.getWindData() != null) {
				data = new XYChart.Data<String, Double>(df.format(datesList.get(i)),
						qp.getWindData().get(i).getValue());
				t = new Tooltip(qp.getWindData().get(i).toString());
			}

			series.getData().add(data);
			hackTooltipStartTiming(t);
			aux_toolList.add(t);
		}
		lChart.getData().add(series);
		int counter = 0;
		for (XYChart.Data<String, Double> d : lChart.getData().get(0).getData()) {
			Tooltip.install(d.getNode(), aux_toolList.get(counter));
			counter++;
		}
		counter = 0;
	}

	/**
	 * Populates all the charts.
	 */
	private void populateAllCharts() {
		fillAllSwh();
		fillAllMwp();
		fillAllWind();

		if (!this.swhChart.isVisible() && !this.mwpChart.isVisible() && !this.windChart.isVisible())
			reinitializeView();

		setAuxiliarTootltipsInComparationMode();
	}

	/**
	 * Creates the extra tooltip when the program is in comparison mode.
	 */
	private void setAuxiliarTootltipsInComparationMode() {
		List<LineChart<String, Double>> chartList = new ArrayList<>();
		chartList.add(this.swhChart);
		chartList.add(this.mwpChart);
		chartList.add(this.windChart);

		for (LineChart<String, Double> lChart : chartList)
			if (lChart.isVisible()) {
				ObservableList<Series<String, Double>> chartData = lChart.getData();
				int numberOfElements = chartData.get(0).getData().size();
				int numberOfPoints = chartData.size();
				List<Tooltip> tooltipList = new ArrayList<>();
				for (int i = 0; i < numberOfElements; i++) {
					double accumulatedValue = 0;
					double greatestValue = Double.MIN_VALUE;
					double lowestValue = Double.MAX_VALUE;
					for (Series<String, Double> serieData : chartData) {
						accumulatedValue += serieData.getData().get(i).getYValue();
						if (serieData.getData().get(i).getYValue() < lowestValue)
							lowestValue = serieData.getData().get(i).getYValue();
						if (serieData.getData().get(i).getYValue() > greatestValue)
							greatestValue = serieData.getData().get(i).getYValue();
					}

					double mean = accumulatedValue / numberOfPoints;
					double difference = greatestValue - lowestValue;
					String units = "";

					if (lChart.equals(this.swhChart))
						units = i18n.createStringBinding("tooltip.meters").get();
					else if (lChart.equals(this.swhChart))
						units = i18n.createStringBinding("tooltip.seconds").get();
					else if (lChart.equals(this.swhChart))
						units = i18n.createStringBinding("tooltip.knots").get();

					Tooltip t = new Tooltip(i18n.createStringBinding("tooltip.numberOfPoints", numberOfPoints).get()
							+ i18n.createStringBinding("tooltip.mean", mean, units).get()
							+ i18n.createStringBinding("tooltip.difference", difference, units).get());
					hackTooltipStartTiming(t);
					tooltipList.add(t);
				}
				setTooltipForAxis(lChart, tooltipList);
			}
	}

	/**
	 * Sets the extra tooltips generated for the comparison mode on its place.
	 * 
	 * @param lChart
	 *            The chart on which the tooltips are going to be inserted.
	 * @param listTooltip
	 *            List containing the tooltips that are going to be inserted.
	 */
	private void setTooltipForAxis(LineChart<String, Double> lChart, List<Tooltip> listTooltip) {
		List<Date> datesList = this.fr.getFileDates();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();

		for (int i = 0; i < datesList.size(); i++) {
			XYChart.Data<String, Double> data = null;
			data = new XYChart.Data<String, Double>(df.format(datesList.get(i)), 0.0);

			if (data != null) {
				series.getData().add(data);
			}
		}
		lChart.getData().add(series);
		int counter = 0;
		for (XYChart.Data<String, Double> d : lChart.getData().get(lChart.getData().size() - 1).getData()) {
			Tooltip.install(d.getNode(), listTooltip.get(counter));
			counter++;
		}

		Legend legend = (Legend) lChart.lookup(".chart-legend");
		legend.getItems().remove(legend.getItems().size() - 1);
	}

	/**
	 * Populates the significant wave height chart with all the data available.
	 */
	private void fillAllSwh() {
		this.swhChart.getData().clear();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		int toolTipCounter = 0;
		List<Date> datesList = this.fr.getFileDates();

		for (QueryPoint qp : queriesMade) {
			XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();
			List<Tooltip> aux_toolList = new ArrayList<>();
			for (int i = 0; i < datesList.size(); i++) {
				XYChart.Data<String, Double> data = null;
				Tooltip t = null;
				if (qp.getSwhData() != null) {
					data = new XYChart.Data<String, Double>(df.format(datesList.get(i)),
							qp.getSwhData().get(i).getValue());
					t = new Tooltip(qp.getSwhData().get(i).toString());
				}
				if (data != null) {
					series.getData().add(data);
					series.setName("(" + qp.getLatitude() + ", " + qp.getLongitude() + ")");
					hackTooltipStartTiming(t);
					aux_toolList.add(t);
				}
			}
			if (!series.getData().isEmpty())
				this.swhChart.getData().add(series);
			int counter = 0;
			if (!aux_toolList.isEmpty()) {
				for (XYChart.Data<String, Double> d : this.swhChart.getData().get(toolTipCounter).getData()) {
					Tooltip.install(d.getNode(), aux_toolList.get(counter));
					counter++;
				}
				counter = 0;
				toolTipCounter++;
			}
		}
		setChartVisibility(this.swhChart);
	}

	/**
	 * Populates the mean wave period chart with all the data available.
	 */
	private void fillAllMwp() {
		this.mwpChart.getData().clear();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		int toolTipCounter = 0;
		List<Date> datesList = this.fr.getFileDates();

		for (QueryPoint qp : queriesMade) {
			XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();
			List<Tooltip> aux_toolList = new ArrayList<>();
			for (int i = 0; i < datesList.size(); i++) {
				XYChart.Data<String, Double> data = null;
				Tooltip t = null;
				if (qp.getMwpData() != null) {
					data = new XYChart.Data<String, Double>(df.format(datesList.get(i)),
							qp.getMwpData().get(i).getValue());
					t = new Tooltip(qp.getMwpData().get(i).toString());
				}
				if (data != null) {
					series.getData().add(data);
					series.setName("(" + qp.getLatitude() + ", " + qp.getLongitude() + ")");
					hackTooltipStartTiming(t);
					aux_toolList.add(t);
				}
			}
			if (!series.getData().isEmpty())
				this.mwpChart.getData().add(series);
			int counter = 0;
			if (!aux_toolList.isEmpty()) {
				for (XYChart.Data<String, Double> d : this.mwpChart.getData().get(toolTipCounter).getData()) {
					Tooltip.install(d.getNode(), aux_toolList.get(counter));
					counter++;
				}
				counter = 0;
				toolTipCounter++;
			}
		}
		setChartVisibility(this.mwpChart);
	}

	/**
	 * Populates the wind speed chart with all the data available.
	 */
	private void fillAllWind() {
		this.windChart.getData().clear();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		int toolTipCounter = 0;
		List<Date> datesList = this.fr.getFileDates();

		for (QueryPoint qp : queriesMade) {
			XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();
			List<Tooltip> aux_toolList = new ArrayList<>();
			for (int i = 0; i < datesList.size(); i++) {
				XYChart.Data<String, Double> data = null;
				Tooltip t = null;
				if (qp.getWindData() != null) {
					data = new XYChart.Data<String, Double>(df.format(datesList.get(i)),
							qp.getWindData().get(i).getValue());
					t = new Tooltip(qp.getWindData().get(i).toString());
				}
				if (data != null) {
					series.getData().add(data);
					series.setName("(" + qp.getLatitude() + ", " + qp.getLongitude() + ")");
					hackTooltipStartTiming(t);
					aux_toolList.add(t);
				}
			}
			if (!series.getData().isEmpty())
				this.windChart.getData().add(series);
			int counter = 0;
			if (!aux_toolList.isEmpty()) {
				for (XYChart.Data<String, Double> d : this.windChart.getData().get(toolTipCounter).getData()) {
					Tooltip.install(d.getNode(), aux_toolList.get(counter));
					counter++;
				}
				counter = 0;
				toolTipCounter++;
			}
		}
		setChartVisibility(this.windChart);
	}

	/**
	 * Sets a chart visible or invisible depending if the chart has content.
	 * 
	 * @param lineChart
	 *            The charts whose visibility is going to be modified.
	 */
	private void setChartVisibility(LineChart<String, Double> lineChart) {
		if (lineChart.getData().size() > 0) {
			lineChart.setLegendVisible(true);
			lineChart.setVisible(true);
			lineChart.setManaged(true);
		} else {
			lineChart.setLegendVisible(false);
			lineChart.setVisible(false);
			lineChart.setManaged(false);
		}
	}

	/**
	 * Sets a lower time for a tooltip to appear.
	 * 
	 * @param tooltip
	 *            Tooltip that is wanted to have a lower appearing time.
	 */
	public static void hackTooltipStartTiming(Tooltip tooltip) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			fieldTimer.setAccessible(true);
			Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the consumption process.
	 */
	private void doConsumption() {
		double latPos = Double.parseDouble(latText.getText());
		double lonPos = Double.parseDouble(lonText.getText());
		QueryPoint auxQueryPoint = new QueryPoint(latPos, lonPos);
		Map<String, Double> aux = this.fr.isSignificantDataAvailable(auxQueryPoint);

		// Mostrando infoLabel
		if (aux != null) {
			// Create a new QueryPoint and add it to combobox
			if (!queryComboBox.getItems().contains(auxQueryPoint.toString())) {
				queryComboBox.getItems().addAll(auxQueryPoint.toString());
				if (queryComboBox.getItems().size() == 1)
					queryComboBox.getSelectionModel().select(0);
				queriesMade.add(auxQueryPoint);
			} else if (queryComboBox.getItems().contains(auxQueryPoint.toString())) {
				int index = -1;
				for (QueryPoint qp : queriesMade)
					if (qp.getLatitude() == latPos && qp.getLongitude() == lonPos)
						index = queriesMade.indexOf(qp);
				queriesMade.remove(index);
				queriesMade.add(auxQueryPoint);
			}

			// Hide the infoTextArea
			errorLabel.textProperty().bind(i18n.createStringBinding("label.errorLabelEmpty"));
			infoVBox.setManaged(false);
			infoVBox.setVisible(false);
			infoTextLabel.setManaged(false);
			infoTextLabel.setVisible(false);

			this.lat = aux.get("lat");
			this.lon = aux.get("lon");
			if (lonPos <= 0)
				showInfoLabel(this.lat, MeteoUtil.getInstance().toWestCoordinate(this.lon));
			else
				showInfoLabel(this.lat, this.lon);

			consumeCheckedVariables(queriesMade.get(queriesMade.size() - 1));
		} else {
			infoTextLabel.textProperty()
					.bind(i18n.createStringBinding("label.infoTextLabelNotAvailableInfo", latPos, lonPos));
			infoTextLabel.setStyle("-fx-text-fill: #E8850C;");
			errorLabel.textProperty().bind(i18n.createStringBinding("label.errorLabelEmpty"));
			;
		}
	}

	/**
	 * Closes the application.
	 */
	public void doExit() {
		Platform.exit();
	}

	/**
	 * Prepares the application for a change of the number of files configuration.
	 */
	public void doChangeNumberOfFiles() {
		App.getInstance().changeNumberOfFiles();
	}

	/**
	 * Prepares the application for a change of the source directory configuration.
	 */
	public void doChangeScourceDirectory() {
		App.getInstance().changeScourceDirectory();
	}

	/**
	 * Prepares the application for a showing the aboput information.
	 */
	public void doShowAbout() {
		App.getInstance().showAbout();
	}

	/**
	 * Starts the export process of the data contained in the charts.
	 */
	public void doExport() {
		List<LineChart<String, Double>> auxList = new ArrayList<>();
		if (this.swhChart.isVisible())
			auxList.add(this.swhChart);
		if (this.mwpChart.isVisible())
			auxList.add(this.mwpChart);
		if (this.windChart.isVisible())
			auxList.add(this.windChart);

		exportToCsv(createCsvString(auxList));
	}

	/**
	 * Creates the csv string for the export of the data.
	 * 
	 * @param auxList
	 *            List containing the charts that have data to be exported.
	 * @return String with the data converted to comma separated values format.
	 */
	private String createCsvString(List<LineChart<String, Double>> auxList) {
		StringBuilder csv = new StringBuilder("variable,latitude,longitude,year,month,day,hour,value");
		for (LineChart<String, Double> lChart : auxList) {
			int counter = 0;
			for (Series<String, Double> ser : lChart.getData()) {
				if ((queriesMade.size() == 1 & counter == 1)
						|| (queriesMade.size() > 1 && counter == lChart.getData().size() - 1))
					break;
				double auxLat;
				double auxLon;
				if (lChart.getData().size() > 1) {
					auxLat = Double.valueOf(ser.getName().split(",")[0].replace("(", ""));
					auxLon = Double.valueOf(ser.getName().split(",")[1].replace(")", ""));
				} else {
					auxLat = this.queriesMade.get(counter).getLatitude();
					auxLon = this.queriesMade.get(counter).getLongitude();
				}

				String variable = "";
				if (lChart == this.swhChart)
					variable = "swh";
				else if (lChart == this.mwpChart)
					variable = "mwp";
				else if (lChart == this.windChart)
					variable = "wind";
				String year = "";
				String month = "";
				String day = "";
				String hour = "";
				for (int i = 0; i < ser.getData().size(); i++) {
					year = ser.getData().get(i).getXValue().split("/")[2].split(" ")[0];
					month = ser.getData().get(i).getXValue().split("/")[1];
					day = ser.getData().get(i).getXValue().split("/")[0];
					hour = ser.getData().get(i).getXValue().split("/")[2].split(" ")[1];
					double value = ser.getData().get(i).getYValue();
					csv.append("\n" + variable + "," + auxLat + "," + auxLon + "," + year + "," + month + "," + day
							+ "," + hour + "," + value);
				}
				counter++;
			}
		}
		return csv.toString();
	}

	/**
	 * Exports the content of the graphs to a csv file.
	 * 
	 * @param str
	 *            String containing the data in csv format.
	 */
	private void exportToCsv(String str) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(this.graphsScrollPane.getScene().getWindow());

		if (file != null) {
			SaveFile(str, file);
		}
	}

	/**
	 * Saves a file with the given content.
	 * 
	 * @param content
	 *            The string that the file needs to contain.
	 * @param file
	 *            The file that is going to contain the string.
	 */
	private void SaveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BooleanBinding booleanBind = latText.textProperty().isEmpty().or(lonText.textProperty().isEmpty());
		visualizeButton.disableProperty().bind(booleanBind);

		// Bind the export menu
		BooleanBinding exportBB = (this.swhChart.visibleProperty().not()).and(this.mwpChart.visibleProperty().not())
				.and(this.windChart.visibleProperty().not());
		this.exportMenuItem.disableProperty().bind(exportBB);

		// Disable the delete query button
		queryButton.disableProperty().bind(queryComboBox.valueProperty().isNull());

		// Disable map option
		this.pickMapPointMenu.disableProperty().bind(yesComparisonModeMenu.selectedProperty());
		mapBorderPane.setManaged(false);
		mapBorderPane.setVisible(false);
		this.selectedPointLabel.setVisible(false);
		this.selectedPointLabel.setManaged(false);

		// Initialize the queryList
		this.queriesMade = new ArrayList<QueryPoint>();

		// RadioButtonMenuControl for search mode
		tgSearchMode = new ToggleGroup();
		proximitySearchModeMenu.setUserData(0);
		prudentSearchModeMenu.setUserData(1);
		proximitySearchModeMenu.setToggleGroup(tgSearchMode);
		prudentSearchModeMenu.setToggleGroup(tgSearchMode);
		tgSearchMode.selectToggle(proximitySearchModeMenu);

		tgSearchMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (tgSearchMode.getSelectedToggle() != null) {
					if(fr != null)
						fr.setConsumerMode((int) tgSearchMode.getSelectedToggle().getUserData());
					App.getInstance().saveSearchMode((int) tgSearchMode.getSelectedToggle().getUserData());
					if (Integer.valueOf(tgComparisonMode.getSelectedToggle().getUserData().toString()) == 1) {
						hideComparisonOptions();
						showComparisonOptions();
					}
					reinitializeView();
				}
			}
		});

		// RadioButtonMenuControl for comparison mode
		tgComparisonMode = new ToggleGroup();
		noComparisonModeMenu.setUserData(0);
		yesComparisonModeMenu.setUserData(1);
		noComparisonModeMenu.setToggleGroup(tgComparisonMode);
		yesComparisonModeMenu.setToggleGroup(tgComparisonMode);
		tgComparisonMode.selectToggle(noComparisonModeMenu);

		tgComparisonMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (tgComparisonMode.getSelectedToggle() != null) {
					if (Integer.valueOf(tgComparisonMode.getSelectedToggle().getUserData().toString()) == 0)
						hideComparisonOptions();
					else if (Integer.valueOf(tgComparisonMode.getSelectedToggle().getUserData().toString()) == 1)
						showComparisonOptions();
					reinitializeView();
				}
			}
		});

		// Hide errorLabel
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);

		// Initialize view items
		decideToBindInfoTextLabel();

		// Populate the languages combobox.
		languageComboBox.getItems().clear();
		languageComboBox.getItems().addAll(i18n.labelForComboBox("comboboxValue.english").getText(),
				i18n.labelForComboBox("comboboxValue.spanish").getText(),
				i18n.labelForComboBox("comboboxValue.french").getText());

		selectComboboxLanguage();
		bindAllTextItems();
	}

	/**
	 * Method for i18n that binds all text elements with the strings.
	 */
	private void bindAllTextItems() {
		// Labels
		this.coordenatesLabel.textProperty().bind(i18n.createStringBinding("label.coordenatesLabel"));
		this.checkboxLabel.textProperty().bind(i18n.createStringBinding("label.checkboxLabel"));
		this.languageLabel.textProperty().bind(i18n.createStringBinding("label.languageLabel"));

		// TextFields
		this.latText.promptTextProperty().bind(i18n.createStringBinding("textField.latitude"));
		this.lonText.promptTextProperty().bind(i18n.createStringBinding("textField.longitude"));

		// Checkboxes
		this.swhCheckBox.textProperty().bind(i18n.createStringBinding("checkBox.swh"));
		this.mwpCheckBox.textProperty().bind(i18n.createStringBinding("checkBox.mwp"));
		this.windCheckBox.textProperty().bind(i18n.createStringBinding("checkBox.wind"));

		// Buttons
		this.visualizeButton.textProperty().bind(i18n.createStringBinding("button.visualize"));
		this.queryButton.textProperty().bind(i18n.createStringBinding("button.query"));
		this.acceptMapButton.textProperty().bind(i18n.createStringBinding("button.acceptMap"));
		this.cancelMapButton.textProperty().bind(i18n.createStringBinding("button.cancelMap"));

		// Menus
		this.fileMenu.textProperty().bind(i18n.createStringBinding("menu.file"));
		this.exportMenuItem.textProperty().bind(i18n.createStringBinding("menu.export"));
		this.exitMenuItem.textProperty().bind(i18n.createStringBinding("menu.exit"));
		this.toolsMenu.textProperty().bind(i18n.createStringBinding("menu.tools"));
		this.searchModeMenu.textProperty().bind(i18n.createStringBinding("menu.searchMode"));
		this.proximitySearchModeMenu.textProperty().bind(i18n.createStringBinding("menu.proximityMode"));
		this.prudentSearchModeMenu.textProperty().bind(i18n.createStringBinding("menu.prudentMode"));
		this.comparisonModeMenu.textProperty().bind(i18n.createStringBinding("menu.comparisonMode"));
		this.noComparisonModeMenu.textProperty().bind(i18n.createStringBinding("menu.noComparisonMenu"));
		this.yesComparisonModeMenu.textProperty().bind(i18n.createStringBinding("menu.yesComparisonMenu"));
		this.helpMenu.textProperty().bind(i18n.createStringBinding("menu.help"));
		this.aboutMenuItem.textProperty().bind(i18n.createStringBinding("menu.about"));
		this.pickMapPointMenu.textProperty().bind(i18n.createStringBinding("menu.pickMapPoint"));
		this.settingsMenu.textProperty().bind(i18n.createStringBinding("menu.settings"));
		this.directoryMenuItem.textProperty().bind(i18n.createStringBinding("menu.directory"));
		this.numberOfFilesMenuItem.textProperty().bind(i18n.createStringBinding("menu.files"));

		// Charts
		this.swhChart.titleProperty().bind(i18n.createStringBinding("chart.swh"));
		this.swhChart.getXAxis().labelProperty().bind(i18n.createStringBinding("chart.swhX"));
		this.swhChart.getYAxis().labelProperty().bind(i18n.createStringBinding("chart.swhY"));
		this.mwpChart.titleProperty().bind(i18n.createStringBinding("chart.mwp"));
		this.mwpChart.getXAxis().labelProperty().bind(i18n.createStringBinding("chart.mwpX"));
		this.mwpChart.getYAxis().labelProperty().bind(i18n.createStringBinding("chart.mwpY"));
		this.windChart.titleProperty().bind(i18n.createStringBinding("chart.wind"));
		this.windChart.getXAxis().labelProperty().bind(i18n.createStringBinding("chart.windX"));
		this.windChart.getYAxis().labelProperty().bind(i18n.createStringBinding("chart.windY"));
	}

	/**
	 * Hides all the options for comparison when it is disabled.
	 */
	private void hideComparisonOptions() {
		queriesMade.clear();
		queryButton.setManaged(false);
		queryButton.setVisible(false);
		queryComboBox.getItems().clear();
		queryComboBox.setManaged(false);
		queryComboBox.setVisible(false);
		querySeparator.setManaged(false);
		querySeparator.setVisible(false);
	}

	/**
	 * Shows all the options for comparison when it is enabled.
	 */
	private void showComparisonOptions() {
		queriesMade.clear();
		queryComboBox.getItems().clear();
		queryButton.setManaged(true);
		queryButton.setVisible(true);
		queryComboBox.setManaged(true);
		queryComboBox.setVisible(true);
		querySeparator.setManaged(true);
		querySeparator.setVisible(true);
	}

	/**
	 * Checks if any variable checkbox is checked.
	 * 
	 * @return True if there is any variable visualization checkbox checked, false
	 *         otherwise.
	 */
	private boolean isAnyCheckBoxChecked() {
		return (swhCheckBox.isSelected() || mwpCheckBox.isSelected() || windCheckBox.isSelected());
	}

	/**
	 * Checks if the input inserted on a textfield can be parsed to a double.
	 * 
	 * @param txt
	 *            The textfield that contains the input.
	 * @return True if the input can be parsed to a double, false otherwise.
	 */
	private boolean isInputParsedToDouble(TextField txt) {
		try {
			Double.parseDouble(txt.getText());
			return true;
		} catch (NumberFormatException ignore) {
			return false;
		}
	}

	/**
	 * Starts the process of file handling.
	 * 
	 * @param directory
	 *            The source directory from which the files are going to be read.
	 * @param numberOfFilesToPreserve
	 *            The number of files that are wanted to be preserved.
	 */
	public void manageFiles(String directory, int numberOfFilesToPreserve) {
		File aux = new File(directory);
		if (!aux.isDirectory()) {
			this.infoTextLabel.textProperty().bind(i18n.createStringBinding("label.infoTextLabelWithoutData"));
			this.userInputVBox.setDisable(true);
			fm = new FileManager(directory, numberOfFilesToPreserve);
			return;
		}
		fm = new FileManager(directory, numberOfFilesToPreserve);
		fm.proccessFiles();
		this.dataFile = fm.getFile();
		fr = new FileReader((int) this.tgSearchMode.getSelectedToggle().getUserData(), this.dataFile);
		this.initializeAvailableInfoLabel();

		if (dataFile == null || fr.getFileStartDate().before(MeteoUtil.getInstance().getDateOneWeekAgo())) {
			this.infoTextLabel.textProperty().bind(i18n.createStringBinding("label.infoTextLabelWithoutData"));
			this.userInputVBox.setDisable(true);
		}
	}

	/**
	 * Initializes the label that shows the coordinates between which the data is
	 * available.
	 */
	private void initializeAvailableInfoLabel() {
		List<Double> auxLats = this.fr.getLatData();
		List<Double> auxLons = this.fr.getLonData();
		if (auxLats != null && auxLons != null) {
			availableInfoLabel.textProperty()
					.bind(i18n.createStringBinding("label.availableInfo", auxLats.get(0),
							MeteoUtil.getInstance().toWestCoordinate(auxLons.get(0)), auxLats.get(auxLats.size() - 1),
							MeteoUtil.getInstance().toWestCoordinate(auxLons.get(auxLons.size() - 1))));
			this.auxLat0ForMap = auxLats.get(0);
			this.auxLat1ForMap = auxLats.get(auxLats.size() - 1);
			this.auxLon0ForMap = MeteoUtil.getInstance().toWestCoordinate(auxLons.get(0));
			this.auxLon1ForMap = MeteoUtil.getInstance().toWestCoordinate(auxLons.get(auxLons.size() - 1));
		}
	}

	/**
	 * Sends a notification of the progress state to the app preloader.
	 * 
	 * @param progressValue
	 *            Progress value that wants to be notified to the Preloader.
	 */
	public void notifyMyPreloader(double progressValue) {
		App.getInstance().notifyPreloader(new Preloader.ProgressNotification(progressValue));
	}

	/**
	 * Sets the view of the application's main window to its original state.
	 */
	private void reinitializeView() {
		latText.setStyle("-fx-border-color: #012345;");
		lonText.setStyle("-fx-border-color: #012345;");
		outputLabel.setVisible(false);
		consumeModeLabel.setVisible(false);

		// errorLabel
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);

		// swh
		swhChart.setVisible(false);
		swhChart.setManaged(false);

		// mwp
		mwpChart.setVisible(false);
		mwpChart.setManaged(false);

		// wind
		windChart.setVisible(false);
		windChart.setManaged(false);

		// TextArea
		infoVBox.setManaged(true);
		infoVBox.setVisible(true);
		infoTextLabel.setManaged(true);
		infoTextLabel.setVisible(true);
		decideToBindInfoTextLabel();
		infoTextLabel.setStyle("-fx-text-fill: #F4F5F0;");

		// Map
		mapBorderPane.setManaged(false);
		mapBorderPane.setVisible(false);
		this.selectedPointLabel.setVisible(false);
		this.selectedPointLabel.setManaged(false);
	}

	/**
	 * Binds the label that prompts the info depending if there is any file with
	 * significant data.
	 */
	private void decideToBindInfoTextLabel() {
		if (this.dataFile == null || fr.getFileStartDate().before(MeteoUtil.getInstance().getDateOneWeekAgo()))
			this.infoTextLabel.textProperty().bind(i18n.createStringBinding("label.infoTextLabelWithoutData"));
		else
			this.infoTextLabel.textProperty().bind(i18n.createStringBinding("label.infoTextLabelDefault"));
	}

	/**
	 * Prompts the info label to the user with the given information.
	 * 
	 * @param latPos
	 *            Latitude for which is going to see information.
	 * @param lonPos
	 *            Longitude for which is going to see information.
	 */
	private void showInfoLabel(double latPos, double lonPos) {
		if (Integer.valueOf(tgComparisonMode.getSelectedToggle().getUserData().toString()) == 0) {
			outputLabel.textProperty().bind(i18n.createStringBinding("label.outputLabel", latPos, lonPos));
			outputLabel.setVisible(true);
			outputLabel.setManaged(true);
		}

		if ((int) tgSearchMode.getSelectedToggle().getUserData() == 0)
			consumeModeLabel.textProperty().bind(i18n.createStringBinding("label.consumeModeLabelProximity"));
		else if ((int) tgSearchMode.getSelectedToggle().getUserData() == 1)
			consumeModeLabel.textProperty().bind(i18n.createStringBinding("label.consumeModeLabelPrudent"));
		consumeModeLabel.setVisible(true);
		consumeModeLabel.setManaged(true);
	}

	/**
	 * Starts the consumption process once the user clicks the visualize button.
	 * 
	 * @param event
	 *            Event that fires the action.
	 */
	public void visualize(ActionEvent event) {
		reinitializeView();

		if (!isInputParsedToDouble(latText)) { // Comprueba que el formato de la latitud es válido.
			errorLabel.setVisible(true);
			errorLabel.setManaged(true);
			errorLabel.textProperty().bind(i18n.createStringBinding("label.errorLabelNotCorrectFormat"));
			latText.setStyle("-fx-border-color: #E8850C;");
		} else if (!isInputParsedToDouble(lonText)) { // Comprueba que el formato de la longitud es válido.
			errorLabel.setVisible(true);
			errorLabel.setManaged(true);
			errorLabel.textProperty().bind(i18n.createStringBinding("label.errorLabelNotCorrectFormat"));
			lonText.setStyle("-fx-border-color: #E8850C;");
		} else if (!isAnyCheckBoxChecked()) { // Comprueba que el usuario selecciona alguna visualización.
			errorLabel.setVisible(true);
			errorLabel.setManaged(true);
			errorLabel.textProperty().bind(i18n.createStringBinding("label.errorLabelNotSelectedVariable"));
		} else {
			doConsumption();
		}
	}

	/**
	 * Checks if there is a file available.
	 * 
	 * @return True if there is a file available, false otherwise.
	 */
	public boolean isFileAvailable() {
		return this.fm.isFileAvailable();
	}

	/**
	 * Sets the search mode on the toggle menu item.
	 * 
	 * @param searchMode
	 *            Int representing the search mode that is currently being used.
	 */
	public void setSearchMode(int searchMode) {
		if (searchMode == 0)
			this.tgSearchMode.selectToggle(proximitySearchModeMenu);
		else
			this.tgSearchMode.selectToggle(prudentSearchModeMenu);
	}

	/**
	 * Deletes a querypoint that has been made.
	 * 
	 * @param event
	 *            Event that fires the action.
	 */
	public void deleteQueryPoint(ActionEvent event) {
		// Busco el elemento selecionado en la combobox
		String auxString = queryComboBox.getSelectionModel().getSelectedItem();
		double lat = Double.valueOf(auxString.split(",")[0].replaceAll("\\(", ""));
		double lon = Double.valueOf(auxString.split(",")[1].replaceAll("\\)", ""));
		for (QueryPoint qp : queriesMade)
			if (qp.getLatitude() == lat && qp.getLongitude() == lon) {
				queriesMade.remove(qp);
				populateAllCharts();
				break;
			}
		queryComboBox.getItems().remove(queryComboBox.getSelectionModel().getSelectedItem());
		if (!queryComboBox.getItems().isEmpty())
			queryComboBox.getSelectionModel().select(0);
	}

	/**
	 * Setes the locale for the i18n of the application.
	 * 
	 * @param event
	 *            Event trigered by a change in the comobobx of language selection.
	 */
	@FXML
	private void languageComboAction(ActionEvent event) {
		if (languageComboBox.getSelectionModel().getSelectedIndex() == 0)
			i18n.setLocale(Locale.ENGLISH);
		else if (languageComboBox.getSelectionModel().getSelectedIndex() == 1)
			i18n.setLocale(Locale.forLanguageTag("es"));
		else if (languageComboBox.getSelectionModel().getSelectedIndex() == 2)
			i18n.setLocale(Locale.FRENCH);
	}

	/**
	 * Select the initial selected language in the combobox depending on the system
	 * locale.
	 */
	private void selectComboboxLanguage() {
		if (i18n.getLocale() == Locale.ENGLISH)
			languageComboBox.getSelectionModel().select(0);
		else if (i18n.getLocale() == Locale.FRENCH)
			languageComboBox.getSelectionModel().select(2);
		else
			languageComboBox.getSelectionModel().select(1);
	}

	/**
	 * Checks if there is connection available.
	 * 
	 * @return True if there is connection, false otherwise.
	 */
	private static boolean netIsAvailable() {
		try {
			final URL url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Starts the map selection point process.
	 */
	public void doFireMap() {
		if (!netIsAvailable()) {
			this.reinitializeView();
			App.getInstance().noInternetConnection();
			return;
		}

		hideForMap();
		googleMapView.addMapInializedListener(() -> configureMap());

		googleMapView.getWebview().getEngine().reload();

		this.outputLabel.textProperty().bind(i18n.createStringBinding("label.outputPickMap"));
		outputLabel.setManaged(true);
		outputLabel.setVisible(true);
		mapBorderPane.setManaged(true);
		mapBorderPane.setVisible(true);

		this.acceptMapButton.setDisable(true);
	}

	/**
	 * Accepts a point that has been selected in the map.
	 */
	public void doAcceptMap() {

		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.CEILING);

		this.reinitializeView();
		this.latText.setText(df.format(this.mapCoordinates.getLatitude()).replace(",", "."));
		this.lonText.setText(df.format(this.mapCoordinates.getLongitude()).replace(",", "."));
	}

	/**
	 * Closes the map view.
	 */
	public void doCancelMap() {
		this.mapCoordinates = null;
		this.reinitializeView();
	}

	/**
	 * Prepared the interface for the map selection point functionality.
	 */
	private void hideForMap() {
		this.infoTextLabel.setVisible(false);
		this.infoTextLabel.setManaged(false);
		this.consumeModeLabel.setVisible(false);
		this.consumeModeLabel.setManaged(false);
		this.swhChart.setVisible(false);
		this.swhChart.setManaged(false);
		this.mwpChart.setVisible(false);
		this.mwpChart.setManaged(false);
		this.windChart.setVisible(false);
		this.windChart.setManaged(false);
		this.infoVBox.setVisible(false);
		this.infoVBox.setManaged(false);
	}

	/**
	 * Configures the map view on the map selection point functionality.
	 */
	private void configureMap() {

		MapOptions mapOptions = new MapOptions();
		double meanLat = auxLat0ForMap - ((auxLat0ForMap - auxLat1ForMap) / 2);
		double meanLon = auxLon0ForMap - ((auxLon0ForMap - auxLon1ForMap) / 2);
		mapOptions.center(new LatLong(meanLat, meanLon)).mapType(MapTypeIdEnum.HYBRID).zoom(4).streetViewControl(false);

		map = googleMapView.createMap(mapOptions, false);

		map.addMouseEventHandler(UIEventType.click, (GMapMouseEvent event) -> {
			map.clearMarkers();
			this.mapCoordinates = event.getLatLong();

			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(this.mapCoordinates);

			this.selectedPointLabel.setVisible(true);
			this.selectedPointLabel.setManaged(true);
			this.selectedPointLabel.textProperty().bind(i18n.createStringBinding("label.selectedPoint",
					this.mapCoordinates.getLatitude(), this.mapCoordinates.getLongitude()));

			Marker marker = new Marker(markerOptions);
			map.addMarker(marker);
			this.acceptMapButton.setDisable(false);
		});
	}
}
