package com.zunisoft.planetrunner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.zunisoft.planetrunner.data.Data;
import com.zunisoft.planetrunner.levels.Level;
import com.zunisoft.planetrunner.levels.Level1;
import com.zunisoft.planetrunner.levels.Level2;
import com.zunisoft.planetrunner.levels.Level3;
import com.zunisoft.planetrunner.media.Media;
import com.zunisoft.planetrunner.screens.Intro;
import com.zunisoft.planetrunner.screens.LevelMap;

import com.zunisoft.utility.games.StageGame;

public class PlanetRunner extends Game {
	// Asset loader & the state
	private AssetManager manager;
	private boolean isLoadingAssets;

	// Shared texture for the game
	public static TextureAtlas atlas;

	// Font
	public static BitmapFont font1,font2;

	// Screens
	private Intro intro;
	private LevelMap map;

	// Persistent data handling
	public static Data data;
	public static Media media;

	// Game events
	public static final int LEVEL_FAILED = 101;
	public static final int LEVEL_COMPLETED = 102;
	public static final int LEVEL_PAUSED = 103;
	public static final int LEVEL_RESUMED = 104;

	public PlanetRunner() {
		//World.debug = true;
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		data = new Data();

		StageGame.setAppSize(960, 540);
		//World.debug = true;

		// Load & generate bitmap font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto_bold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,:.'%()-";
		parameter.size = 36;
		parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
		parameter.magFilter = Texture.TextureFilter.Linear;
		parameter.genMipMaps = true;
		font1 = generator.generateFont(parameter);

		FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter2.characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,:.'%()-";
		parameter2.size = 48;
		parameter2.minFilter = Texture.TextureFilter.MipMapLinearNearest;
		parameter2.magFilter = Texture.TextureFilter.Linear;
		parameter2.genMipMaps = true;
		font2 = generator.generateFont(parameter2);

		generator.dispose();

		// Load assets
		manager = new AssetManager();
		manager.load("images/pack.atlas", TextureAtlas.class);

		// Load sounds
		manager.load("sounds/click.mp3" , Sound.class);
		manager.load("sounds/jump.mp3" , Sound.class);
		manager.load("sounds/coin.ogg" , Sound.class);
		manager.load("sounds/hit.ogg" , Sound.class);
		manager.load("sounds/hit2.ogg" , Sound.class);
		manager.load("sounds/bomb_pack.ogg" , Sound.class);
		manager.load("sounds/bomb.mp3" , Sound.class);
		manager.load("sounds/flag.mp3" , Sound.class);
		manager.load("sounds/level_completed.mp3" , Sound.class);

		// Load music
		manager.load("sounds/music/intro.wav" , Music.class);
		manager.load("sounds/music/level.wav" , Music.class);

		isLoadingAssets = true;
	}

	private void onAssetsCompleted() {
		// Show intro
		showIntro();
	}

	private void showIntro() {
		// Create
		intro = new Intro();
		setScreen(intro);

		// Listener
		intro.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				//play btn clicked
				if (code == Intro.PLAY) {
					showLevelMap();
				}
			}


		});

		// Start the music
	PlanetRunner.media.playMusic("intro");

	}

	// Showing level map with the icons
	private void showLevelMap() {
		// Create
		map = new LevelMap();
		setScreen(map);

		// Set the callback
		map.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				// Icon selected, start level with the particular id
				if(code == LevelMap.ON_ICON_SELECTED) {
					startLevel(map.selectedLevelId);
				}
				// Back to intro screen
				else if(code == LevelMap.ON_BACK) {
					showIntro();
				}
			}
		});

		PlanetRunner.media.stopMusic("level");
		PlanetRunner.media.playMusic("intro");
	}

	private void startLevel(int levelId) {
		Level level=null;

		// Create "level" object based on the id
		if(levelId == 1) {
			level = new Level1();
		}
		else if(levelId == 2) {
			level = new Level2();
		}
		else if(levelId == 3) {
			level = new Level3();
		}

		if(level == null) {
			throw new Error("Level class not defined yet");
		}
		setScreen(level);

		// The level callback
		level.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				// Failed, back to map
				if(code == Level.FAILED) {
					showLevelMap();
				}

				// Completed back to map also
				else if(code == Level.COMPLETED) {
					showLevelMap();
				}
			}
		});

		PlanetRunner.media.stopMusic("intro");
		PlanetRunner.media.playMusic("level");
	}

	@Override
	public void render() {

		// Loading assets
		if(isLoadingAssets) {
			if(manager.update()) { // If assets loaded
				isLoadingAssets = false;
				atlas = manager.get("images/pack.atlas" ,TextureAtlas.class );
				media = new Media(manager);
				onAssetsCompleted();
			}
		}
		super.render();
	}
}
