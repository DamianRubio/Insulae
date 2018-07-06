package model.FileManaging;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.DataRetrieving.ProximityConsumer;
import utilities.MeteoUtil;

public class FileManager {

	// Own fields
	private String sourceDirectory;
	private int numberOfFilesToPreserve;
	private File file;

	// Other model elements
	private Decompressor decompressor;
	private Deleter deleter;

	/**
	 * File manager constructor.
	 * 
	 * @param sourceDirectory
	 *            Source directory for reading the files.
	 * @param numberOfFilesToPreserve
	 *            Number of files that wants to be preserved.
	 */
	public FileManager(String sourceDirectory, int numberOfFilesToPreserve) {
		this.sourceDirectory = sourceDirectory;
		this.numberOfFilesToPreserve = numberOfFilesToPreserve;
		this.deleter = new Deleter(this.sourceDirectory, this.numberOfFilesToPreserve);
		this.decompressor = new Decompressor(this.sourceDirectory);
	}

	/**
	 * Process the files of its source directory.
	 */
	public void proccessFiles() {
		// Decompress and delete the original files
		this.decompressor.execute();
		this.deleter.deleteCompressed();

		// Delete older files
		this.deleter.deleteOlder();

		// Select the file from which the application is going to read
		this.file = getDataFile();
	}

	/**
	 * Gets the file from which the data is going to be read.
	 * 
	 * @return File from which the data is going to be read.
	 */
	private File getDataFile() {
		Date auxDate = null;
		File auxFile = null;
		List<File> filesInTheDirectory = this.selectFilesFromDirectory();
		ProximityConsumer auxConsumer = new ProximityConsumer();
		Calendar today = Calendar.getInstance();
		for (File f : filesInTheDirectory) {
			Date fileDate = auxConsumer.getFileStartDate(f);
			if (auxDate == null || (auxDate.before(fileDate) && !fileDate.after(today.getTime()))
					|| (MeteoUtil.getInstance().isDateLaterThanToday(auxDate) && auxDate.after(fileDate)
							&& MeteoUtil.getInstance().isDateTodayOrInThePreviousWeek(fileDate))) {
				auxDate = fileDate;
				auxFile = f;
			}
		}
		if (auxFile != null)
			if (!auxDate.after(today.getTime()) || MeteoUtil.getInstance().isDateToday(auxDate)) {
				return auxFile;
			}
		return null;
	}

	/**
	 * Select the files that are in the source directory.
	 * 
	 * @return List of all the files contained in the source directory.
	 */
	private List<File> selectFilesFromDirectory() {
		List<File> filesInDirectory = new ArrayList<File>();
		File folder = new File(this.sourceDirectory);
		File[] allFilesInDirectory = folder.listFiles();
		String aux = "";
		for (int i = 0; i < allFilesInDirectory.length; i++) {
			aux = allFilesInDirectory[i].getAbsolutePath();
			if (allFilesInDirectory[i].isFile() && aux.contains(".nc"))
				filesInDirectory.add(allFilesInDirectory[i]);
		}
		return filesInDirectory;
	}

	/**
	 * Checks if there is an available file.
	 * 
	 * @return True if there is a file with significant data, false otherwise.
	 */
	public boolean isFileAvailable() {
		File folder = new File(this.sourceDirectory);
		if (folder.isDirectory())
			if (folder.listFiles().length > 0)
				return true;
		return false;
	}

	/**
	 * Gets the file from which the system is going to read.
	 * 
	 * @return File from which the system is going to read.
	 */
	public File getFile() {
		return this.file;
	}
}
