package model.FileManaging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.DataRetrieving.ProximityConsumer;
import utilities.MeteoUtil;

public class Deleter {

	private String sourceDirectory;
	private int numberOfFilesToPreserve;

	/**
	 * Deleter constructor
	 * 
	 * @param sourceDirectory
	 *            Directory from which the system is going to read the files.
	 * @param numberOfFilesToPreserve
	 *            Number of files to preserve.
	 */
	public Deleter(String sourceDirectory, int numberOfFilesToPreserve) {
		this.sourceDirectory = sourceDirectory;
		this.numberOfFilesToPreserve = numberOfFilesToPreserve;
	}

	/**
	 * Starts the execution of a file.
	 * 
	 * @param fi
	 *            File that wants to be executed.
	 */
	public void execute(File fi) {
		this.delete(fi);
	}

	/**
	 * Executes the deletion of a file.
	 * 
	 * @param fi
	 *            File that wants to be deleted.
	 */
	private void delete(File fi) {
		try {
			Path path = fi.toPath();
			File aux = new File(fi.getAbsolutePath());
			if (aux.exists() && !aux.isDirectory())
				Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the files compressed that are in the rource directory.
	 */
	public void deleteCompressed() {
		List<File> compressedFiles = selectCompressedFiles();
		for (File fi : compressedFiles)
			delete(fi);
	}

	/**
	 * Select the files that are compressed from the source directory.
	 * 
	 * @return List of files compressed in the source directory.
	 */
	private List<File> selectCompressedFiles() {
		List<File> compressedFilesInDirectory = new ArrayList<File>();
		File folder = new File(this.sourceDirectory);
		File[] filesInDirectory = folder.listFiles();
		String aux = "";
		for (int i = 0; i < filesInDirectory.length; i++) {
			aux = filesInDirectory[i].getAbsolutePath();
			if (filesInDirectory[i].isFile() && aux.contains(".bz2"))
				compressedFilesInDirectory.add(filesInDirectory[i]);
		}
		return compressedFilesInDirectory;
	}

	/**
	 * Select the files that are uncompressed from the source directory.
	 * 
	 * @return List of files uncompressed in the source directory.
	 */
	private List<File> selectUncompressedFiles() {
		List<File> uncompressedFilesInDirectory = new ArrayList<File>();
		File folder = new File(this.sourceDirectory);
		File[] filesInDirectory = folder.listFiles();
		String aux = "";
		for (int i = 0; i < filesInDirectory.length; i++) {
			aux = filesInDirectory[i].getAbsolutePath();
			if (filesInDirectory[i].isFile() && !aux.contains(".bz2"))
				uncompressedFilesInDirectory.add(filesInDirectory[i]);
		}
		return uncompressedFilesInDirectory;
	}

	/**
	 * Deletes the files that contain older info and that must not be preserved.
	 */
	public void deleteOlder() {
		List<File> candidatesToBeDeleted = this.selectUncompressedFiles();
		List<File> filesToBeDeleted = selectOlderFiles(this.numberOfFilesToPreserve, candidatesToBeDeleted);
		filesToBeDeleted = extractFilesWithFutureInfo(filesToBeDeleted);
		for (File fi : filesToBeDeleted) {
			this.execute(fi);
		}
	}

	/**
	 * Selects the older files from a list based on the number of files that are
	 * going to be preserved.
	 * 
	 * @param numberOfFilesToPreserve
	 *            Number of files that are wanted to be preserved.
	 * @param finalFiles
	 *            List containing the final files of the directory.
	 * @return List containing the selected files.
	 */
	private List<File> selectOlderFiles(int numberOfFilesToPreserve, List<File> finalFiles) {
		List<File> filesToBeDeleted = new ArrayList<File>();
		ProximityConsumer auxConsumer = new ProximityConsumer();
		if (numberOfFilesToPreserve >= finalFiles.size())
			return filesToBeDeleted;
		else {
			Map<Date, File> fileToPreserve = new HashMap<>();
			for (File fi : finalFiles) {
				Date fileDate = auxConsumer.getFileStartDate(fi);
				if (fileToPreserve.size() < numberOfFilesToPreserve)
					fileToPreserve.put(fileDate, fi);
				else {
					List<Date> current_dates = new ArrayList<>(fileToPreserve.keySet());
					Date olderDate = current_dates.get(0);
					for (Date d : current_dates)
						if (d.before(olderDate))
							olderDate = d;
					if (olderDate.before(fileDate)) {
						fileToPreserve.remove(olderDate);
						fileToPreserve.put(fileDate, fi);
					}
				}
			}

			filesToBeDeleted = finalFiles;
			for (File fileToExtract : fileToPreserve.values()) {
				filesToBeDeleted.remove(fileToExtract);
			}

		}

		for (File fi : filesToBeDeleted) {
			if (MeteoUtil.getInstance().isDateToday(auxConsumer.getFileStartDate(fi))) {
				filesToBeDeleted.remove(fi);
				break;
			}
		}
		return filesToBeDeleted;
	}

	/**
	 * Once we have the files we want to delete, it takes out the ones that has info
	 * for the future to preserve them.
	 * 
	 * @param filesToBeDeleted
	 *            List containing all the files that are going to be deleted.
	 * @return List of files with future info.
	 */
	private List<File> extractFilesWithFutureInfo(List<File> filesToBeDeleted) {
		List<File> copyOfFilesToBeDeleted = new ArrayList<>();
		ProximityConsumer auxConsumer = new ProximityConsumer();
		for (File fi : filesToBeDeleted)
			copyOfFilesToBeDeleted.add(fi);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		for (File fi : filesToBeDeleted)
			if (auxConsumer.getFileStartDate(fi).after(today.getTime()))
				copyOfFilesToBeDeleted.remove(fi);
		return copyOfFilesToBeDeleted;
	}

}
