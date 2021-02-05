package com.threecubed.auber.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.threecubed.auber.World;


//Headless.loadHeadless();

class TestFR_Infiltrator {
	  /**
	   * Test whether there are 8 infiltrators in the game
	   *
	   * @author Mircea Zisu
	   * @version 1.0
	   * */
	@Test
	void testInfiltratorCount() {
		assertEquals(8, World.MAX_INFILTRATORS);
	}

}
