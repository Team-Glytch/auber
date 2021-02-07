package com.threecubed.auber;

public class GameStartedRunnable implements Runnable {
	public volatile boolean runnableCalled = false; 
	
	@Override
	public void run () {
		runnableCalled = true;
	}
}
