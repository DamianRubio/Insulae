package model.FileManaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class Decompressor {

	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private String sourceDirectory;
	private List<File> compressedFiles;

	/**
	 * Decompressor constructor
	 * 
	 * @param sourceDirectory Directory from which the system is going to read the files.
	 */
	public Decompressor(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	/**
	 * Starts the decompression of a file.
	 */
	public void execute() {
		this.decompress();
	}

	/**
	 * Decompresses a given file.
	 */
	private void decompress() {
		this.selectCompressedFiles();
		for(File fi: compressedFiles) {
			String inputPath = fi.getAbsolutePath();
			String outputPath = inputPath.substring(0, inputPath.length() - 4);
			try (OutputStream outStream = new FileOutputStream(outputPath);
					BZip2CompressorInputStream compressor_inputStream = new BZip2CompressorInputStream(
							new FileInputStream(inputPath))) {
				final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int n = 0;
				while (-1 != (n = compressor_inputStream.read(buffer))) {
					outStream.write(buffer, 0, n);
				}
				outStream.close();
				compressor_inputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	/**
	 * Select the files that are compressed from the source directory.
	 */
	private void selectCompressedFiles() {
		List<File> compressedFilesInDirectory = new ArrayList<File>();
		File folder = new File(this.sourceDirectory);
		File[] filesInDirectory = folder.listFiles();
		String aux = "";
		for (int i = 0; i < filesInDirectory.length; i++) {
			aux = filesInDirectory[i].getAbsolutePath();
			if (filesInDirectory[i].isFile() && aux.contains(".bz2"))
				compressedFilesInDirectory.add(filesInDirectory[i]);
		}
		this.compressedFiles = compressedFilesInDirectory;
	}

}
