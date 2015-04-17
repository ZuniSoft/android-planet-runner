package com.zunisoft.planetrunner.data;

import com.zunisoft.utility.DataManager;

public class Data {

	private static final String prefName = "super_marissa_kw2";
	private DataManager manager;
	
	// Keys
	private String levelProgressKey = "lp";
	private String levelScoreKey = "ls";
	private String soundMuteKey = "smt";
	private String musicMuteKey = "mmt";
	
	public Data() {
		manager = new DataManager(prefName);
	}

	// Get the progress
	public int getLevelProgress() {
		return manager.getInt(levelProgressKey, 0);
	}
	
	// Set the current progress
	public void setLevelProgress(int level) {
		if(level < getLevelProgress()) return;
		manager.saveInt(levelProgressKey, level);
	}
	
	// Get the score of specific level
	public int getScore(int level) {
		return manager.getInt(levelScoreKey+level, 0);
	}
	
	public int updateScore(int level,int score) {
		int old = getScore(level);
		
		// If the old score is better, no need to update
		if(score > old) {
			manager.saveInt(levelScoreKey+level, score);
			return score;
		} else {
			return old;
		}
	}
	
	// Get the mute state
	public boolean isSoundMuted() {
		return manager.getBoolean(soundMuteKey, false);
	}
	public boolean isMusicMuted() {
		return manager.getBoolean(musicMuteKey, false);
	}
	
	// Set the mute state
	public void setSoundMute(boolean mute) {
		manager.setBoolean(soundMuteKey, mute);
	}
	public void setMusicMute(boolean mute) {
		manager.setBoolean(musicMuteKey, mute);
	}

}
