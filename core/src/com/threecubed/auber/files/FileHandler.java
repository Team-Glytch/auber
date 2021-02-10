package com.threecubed.auber.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import com.threecubed.auber.AuberGame;
import com.threecubed.auber.screens.GameScreen;
import com.threecubed.auber.screens.MenuScreen;

/**
 * The FileHandler class contains attributes and methods required for storing,
 * retrieving and categorising game save data.
 * 
 * @author Joshua Cottrell
 * @version 1.0
 * @since 1.0
 */

public class FileHandler {

	private static final List<Saveable> saveables = new ArrayList<Saveable>();

	// directory in which save files are located
	private static final String path = "saves/";

	/**
	 * add elements to the saveables list
	 */
	public static void addSaveable(Saveable saveable) {
		saveables.add(saveable);
	}

	/**
	 * Writes game save data to file.
	 * 
	 * @param savename Name of file to save
	 * @throws IOException Throws if I/O error occurred while writing data
	 */
	public static void save(String savename) throws IOException {

		// create save directory if it doesn't already exist
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}

		File saveFile = new File(path + savename);
		saveFile.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));

		for (Saveable saveable : saveables) {
			writer.write(saveable.getCategory().toString() + "\n");
			writer.write(saveable.getSaveData() + "\n");
		}

		writer.close();

	}

	/**
	 * Loads game save data using JFileChooser.
	 * 
	 * @param game Game object
	 * @throws IOException Throws if I/O error occurred while reading file
	 */
	public static void load(final AuberGame game) throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/" + path));
		fileChooser.showOpenDialog(null);

		try {
			game.setScreen(new GameScreen(game, false, "easyButton"));
			FileHandler.load(fileChooser.getSelectedFile());
		} catch (IOException | NullPointerException e) {
			game.setScreen(new MenuScreen(game));
		}
	}

	/**
	 * Load game save data into saveables.
	 * 
	 * @param saveFile File object containing game save
	 * @return saveables Saved attributes
	 * @throws IOException Throws if I/O error occured while reading file
	 */
	private static List<Saveable> load(File saveFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(saveFile));
		String line = reader.readLine();

		int[] amount = new int[SaveCategory.values().length];

		while (line != null && !line.equalsIgnoreCase("")) {
			SaveCategory category = SaveCategory.valueOf(line);
			line = reader.readLine();

			int index = category.getIndex();
			int ctr = 0;

			for (Saveable saveable : saveables) {
				if (saveable.getCategory().equals(category)) {
					if (ctr == amount[index]) {
						saveable.loadSaveData(line);
						break;
					}

					ctr += 1;
				}
			}

			amount[index] = amount[index] + 1;

			line = reader.readLine();
		}

		reader.close();

		return saveables;
	}

}
