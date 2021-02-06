package com.threecubed.auber.files;

/**
 * Interface containing methods to override for the
 * purposes of storing and retrieving game save data.
 * 
 * @author Joshua Cottrell
 * @version 1.0
 * @since 1.0
 */

public interface Saveable {
	
	public SaveCategory getCategory();
	
	public String getSaveData();
	
	public void loadSaveData(String data);
	
}
