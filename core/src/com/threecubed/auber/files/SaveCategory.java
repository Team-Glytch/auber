package com.threecubed.auber.files;

/**
 * enum containing the categories for game save data.
 * Ensures that the save file can be loaded in any
 * order by FileHandler.
 * 
 * @author Joshua Cottrell
 * @version 1.0
 * @since 1.0
 */
public enum SaveCategory {

	ENTITY, PLAYER, INFILTRATOR, WORLD;

	public int getIndex() {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].equals(this)) {
				return i;
			}
		}
		
		return -1;
	}

}
