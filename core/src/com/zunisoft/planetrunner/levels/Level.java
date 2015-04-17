package com.zunisoft.planetrunner.levels;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;

import com.zunisoft.planetrunner.actors.Water;
import com.zunisoft.utility.MessageListener;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.utility.games.platformerLib.World;
import com.zunisoft.utility.games.tiled.TileLayer;

import com.zunisoft.planetrunner.Settings;
import com.zunisoft.planetrunner.PlanetRunner;
import com.zunisoft.planetrunner.actors.Brick;
import com.zunisoft.planetrunner.actors.Bomb;
import com.zunisoft.planetrunner.actors.BombExp;
import com.zunisoft.planetrunner.actors.BombStock;
import com.zunisoft.planetrunner.actors.Coin;
import com.zunisoft.planetrunner.actors.Debris;
import com.zunisoft.planetrunner.actors.Flag;
import com.zunisoft.planetrunner.actors.Hero;
import com.zunisoft.planetrunner.actors.MysteryBox;
import com.zunisoft.planetrunner.control.CButton;
import com.zunisoft.planetrunner.control.JoyStick;
import com.zunisoft.planetrunner.control.ToastLabel;
import com.zunisoft.planetrunner.dialogs.LevelCompletedDialog;
import com.zunisoft.planetrunner.dialogs.LevelFailedDialog;
import com.zunisoft.planetrunner.dialogs.PauseDialog;
import com.zunisoft.planetrunner.enemies.*;
import com.zunisoft.planetrunner.panel.BombCounter;
import com.zunisoft.planetrunner.panel.CoinCounter;
import com.zunisoft.planetrunner.panel.HealthBar;
import com.zunisoft.planetrunner.panel.ScoreCounter;

public class Level extends World {

	// Tmx filename & location
	protected String tmxFile;
	
	// Values based on the tmx file
	private int mapWidth,mapHeight,tilePixelWidth,tilePixelHeight;
	private int levelWidth , levelHeight;
		
	// Level states
	protected static final int GAME_PLAY = 1;
	protected static final int GAME_COMPLETED = 2;
	protected static final int GAME_PAUSED= 3;
	protected int state;
	
	// Key states
	private boolean keyLeft,keyRight,keyJump,keyDodge,keyFire;
	
	// Entities
	protected Hero hero;
	private Flag flag;
	
	// Level bg
	protected Image levelBg;
		
	// Callback message code
	public static final int FAILED = 1;
	public static final int COMPLETED = 2;
	
	public int id; // Id of level, should be unique..
	
	// Top panel items
	private HealthBar healthBar;
	private CoinCounter coinCounter;
	private BombCounter bombCounter;
	private ScoreCounter scoreCounter;
	
	// Reference coin, the function is to synchronize all coins animation
	public static Coin coin;
	
	// Level data
	private int numCoins,numBombs,score;
	
	// Pause dialog
	private PauseDialog pauseDialog;
	
	// On screen virtual button
	private JoyStick joyStick;
	private CButton jumpBtn,dodgeBtn,bombBtn;
	
	// Pools

	// Brick debris
	private Pool<Debris> poolDebris = new Pool<Debris>(){
		@Override
		protected Debris newObject() {
			return new Debris();
		}
	};
	
	// Coins
	private Pool<Coin> poolCoins = new Pool<Coin>(){
		@Override
		protected Coin newObject() {
			return new Coin();
		}
	};
	
	// Bombs
	private Pool<Bomb> poolBombs = new Pool<Bomb>(){

		@Override
		protected Bomb newObject() {
			return new Bomb();
		}
		
	};
	
	// Bomb explosion
	private Pool<BombExp> poolBombExps = new Pool<BombExp>(){
		@Override
		protected BombExp newObject() {
			
			final BombExp exp = new BombExp();
			exp.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == BombExp.COMPELTED) {
						removeEntity(exp);
						poolBombExps.free((BombExp) actor);
					}
				}});
			return exp;
		}
	};
	
	// Score label
	private Pool<ToastLabel> poolScoreLabels = new Pool<ToastLabel>() {
		@Override
		protected ToastLabel newObject() {
			return new ToastLabel();
		}
		
	};

	// End pools

	public Level(int id) {
		gravity.y = Settings.GRAVITY;
		
		this.id = id;

		init();
		
		if(tmxFile == null) {
			throw new Error("TMX file not defined yet !!!");
		}
		
		// Prepare the reference coin
		Level.coin = new Coin();
		Level.coin.setAsRefference();
		addOverlayChild(Level.coin);
		Level.coin.setColor(1, 1, 1, 0);
				
		buildLevel();
		if(hero == null) {
			throw new Error("hero not defined yet in tmx file !!!");
		}
		
		// Pull hero on top of other objects
		stage.addActor(hero);
		
		
		// The camera follows the hero
		camController.followObject(hero);

		// Camera clamp, make sure it is only showing game area
		camController.setBoundary(new Rectangle(0,0,levelWidth,levelHeight));
		
		// Set the bg
		if(levelBg != null) {
			addBackground(levelBg, true, false);
		}
		
		// The top panel

		// Health bar
		healthBar = new HealthBar();
		addOverlayChild(healthBar);
		healthBar.setX(10);
		healthBar.setY(getHeight() - healthBar.getHeight()-10);
		
		// Coin counter
		coinCounter = new CoinCounter();
		addOverlayChild(coinCounter);
		coinCounter.setX(healthBar.getRight()+ 50);
		coinCounter.setY(getHeight() - coinCounter.getHeight() - 10);

		// Bomb counter
		bombCounter = new BombCounter();
		addOverlayChild(bombCounter);
		bombCounter.setX(coinCounter.getRight() + 50);
		bombCounter.setY(coinCounter.getY() + 5);

		// Score counter
		scoreCounter = new ScoreCounter();
		addOverlayChild(scoreCounter);
		scoreCounter.setX(getWidth() - scoreCounter.getWidth());
		scoreCounter.setY(getHeight() - scoreCounter.getHeight()-20);

		// Create the on screen control
		float minHeight = mmToPx(10);
		joyStick = new JoyStick(minHeight);
		addOverlayChild(joyStick);
		joyStick.setPosition(15, 15);

		// On screen buttons
		jumpBtn = new CButton(new Image(PlanetRunner.atlas.findRegion("jump")), new Image(PlanetRunner.atlas.findRegion("jump_down")), minHeight);
		dodgeBtn = new CButton(new Image(PlanetRunner.atlas.findRegion("dodge")), new Image(PlanetRunner.atlas.findRegion("dodge_down")), minHeight);
		bombBtn = new CButton(new Image(PlanetRunner.atlas.findRegion("bomb_btn")), new Image(PlanetRunner.atlas.findRegion("bomb_btn_down")), minHeight);
		
		jumpBtn.setPosition(getWidth() - jumpBtn.getWidth() - 15, jumpBtn.getHeight() + 20);
		addOverlayChild(jumpBtn);

		dodgeBtn.setPosition(getWidth() - dodgeBtn.getWidth() - 15, 15);
		addOverlayChild(dodgeBtn);
		
		bombBtn.setPosition(jumpBtn.getX() - bombBtn.getWidth() - 0.4f*bombBtn.getWidth(), 15);
		addOverlayChild(bombBtn);

		// CamController.setDefaultZoom(1.5f);
		updatePanel();
		state = GAME_PLAY;
	} 

	
	protected void init() {
	}

	private void buildLevel() {
		// Assign filter to avoid artifact when it resized to match screen size
		TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
		params.generateMipMaps = true;
		params.textureMinFilter = TextureFilter.MipMapLinearNearest;
		params.textureMagFilter = TextureFilter.Linear;
		 
		// Get and calculate map size
		TiledMap map = new TmxMapLoader().load(tmxFile, params);
		MapProperties prop = map.getProperties();
		mapWidth = prop.get("width", Integer.class);
		mapHeight = prop.get("height", Integer.class);
		tilePixelWidth = prop.get("tilewidth", Integer.class);
		tilePixelHeight = prop.get("tileheight", Integer.class);
		levelWidth  = mapWidth * tilePixelWidth;
		levelHeight = mapHeight * tilePixelHeight;
		
		// Inspect the layers of tmx files
		for (MapLayer layer : map.getLayers()) {
			String name = layer.getName();
			
			if(name.equals("fixed")) {
				// Build fixed object : land & platform
				createPlatform(layer.getObjects());
			}
			else if(name.equals("brick")) {
				// Create brick, brick is a breakable item
				createBricks(layer.getObjects());
			}
			else if(name.equals("water")) {
				// Create water
				createWater(layer.getObjects());
			}
			else if(name.equals("entity")) {
				// Create game entity : hero, enemies, coins, etc
				createEntities(layer.getObjects());
			}
			else {
				// Tiled layer only, no object
				TileLayer tLayer = new TileLayer(camera, map, name,stage.getBatch());
				addChild(tLayer);
			}
		}
		
		// Pull enemies on top of other items
		for(Entity ent : getEntityList()) {
			if(ent instanceof Enemy) {
				stage.addActor(ent);
			}
		}
		
		
		// Create world boundary wall

		// Left
		Entity wall = new Entity();
		wall.setSize(10, levelHeight);
		wall.setPosition(-5, levelHeight/2);
		addLand(wall, false);
		
		// Right
		wall = new Entity();
		wall.setSize(10, levelHeight);
		wall.setPosition(levelWidth + 5, levelHeight/2);
		addLand(wall, false);
		
		// Top
		wall = new Entity();
		wall.setSize(levelWidth, 10);
		wall.setPosition(levelWidth/2, levelHeight + 15);
		addLand(wall, false);
		
	}
	
	// Brick based on tmx file
	private void createBricks(MapObjects objects) {
		Rectangle rect;
		for (MapObject obj : objects) {
			rect = ((RectangleMapObject) obj).getRectangle();
			
			Brick brick = new Brick(rect);
			brick.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
			addLand(brick, true);
			
		}
	}

	// Water based on tmx file
	private void createWater(MapObjects objects) {
		Rectangle rect;
		for (MapObject obj : objects) {
			rect = ((RectangleMapObject) obj).getRectangle();

			Water water = new Water(rect);
			water.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
			addLand(water, true);

		}
	}

	private void createEntities(MapObjects objects) {
		Rectangle rect;
		for (MapObject obj : objects) {
			rect = ((RectangleMapObject) obj).getRectangle();
			String name = obj.getName();
		
			
			if(name.equals("hero")) {
				// Create hero
				hero = new Hero(this);
				hero.setPosition(rect.x + rect.width/2, rect.y + hero.getHeight()/2);
				addEntity(hero);
				hero.addListener(heroListener);
			}
			else if(name.equals("coin")) {
				// Create coin
				Coin coin = poolCoins.obtain();
				coin.setPosition(rect.x + rect.width/2, rect.y + rect.height/2);
				addEntity(coin);
				coin.setFloat();
			}
			else if(name.equals("mystery")) {
				// Mystery box
				MysteryBox box = new MysteryBox(this, rect);
				box.setPosition(rect.x + rect.width/2, rect.y + rect.height/2);
				
				// Add Entity(box);
				addLand(box, false);
				
				// If has coins, number of coins
				if(obj.getProperties().get("coins") != null) {
					int numCoin = Integer.valueOf(obj.getProperties().get("coins").toString());
					box.setCoin(numCoin);
				} 
				
				// Has bombs
				else if(obj.getProperties().get("bombs") != null) {
					int numBombs = Integer.valueOf(obj.getProperties().get("bombs").toString());
					box.setBomb(numBombs);
				}
				
				
			}
			
			else if(name.equals("bomb_stock")) {
				// Get the amount, and create the object
				int amount = Integer.valueOf(obj.getProperties().get("amount").toString());
				BombStock stock = new BombStock(amount);
				stock.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
				
				// Float in the air
				stock.setFloating(true);
				addEntity(stock);
			}
			
			// Finish flag
			else if(name.equals("flag")) {
				flag = new Flag();
				flag.setX(rect.x);
				flag.setY(rect.y + flag.getHeight()/2 - 2);
				addEntity(flag);
				flag.addListener(flagListener);
			}
			
			else if(name.equals("enemy1")) {
				// Create Enemy1, set the position, and listener
				Enemy1 enemy = new Enemy1();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);
				
				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy2")) {
				// Create Enemy2, set the position, and listener
				Enemy2 enemy = new Enemy2();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);
				
				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy3")) {
				// Create Enemy2, set the position, and listener
				Enemy3 enemy = new Enemy3();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy4")) {
				// Create Enemy4, set the position, and listener
				Enemy4 enemy = new Enemy4();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy5")) {
				// Create Enemy5, set the position, and listener
				Enemy5 enemy = new Enemy5();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}
			else if(name.equals("enemy6")) {
				// Create Enemy6, set the position, and listener
				Enemy6 enemy = new Enemy6();
				enemy.setX(rect.x + (rect.width - enemy.getWidth())/2);
				enemy.setY(rect.y + (enemy.getHeight())/2);
				addEntity(enemy);

				enemy.addListener(enemyListener);
			}
			else {
				createOtherEntity(obj);
			}
		}
	}
	protected void createOtherEntity(MapObject obj) {
		// Sub class to implement
	}
	
	// The platform
	private void createPlatform(MapObjects objects) {
		for (MapObject obj : objects) {
			Rectangle rect = ((RectangleMapObject) obj).getRectangle();
				
			Entity ent = new Entity();
			ent.setSize(rect.width, rect.height);
			ent.setPosition(rect.x + rect.width / 2, rect.y + rect.height / 2);
			addLand(ent, true);
		}
	}
	
	// Listeners
	private MessageListener heroListener = new MessageListener() {
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Hero.HERO_DIE) {
				levelFailed();
			}
		}
	};

	private MessageListener enemyListener = new MessageListener(){
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Enemy.DIE) {
				actor.removeListener(this);
			}
		}
		
	};

	private MessageListener labelListener = new MessageListener(){
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == ToastLabel.REMOVE) {
				removeChild(actor);
				actor.removeListener(this);
			}
		}
		
	};

	private MessageListener flagListener = new MessageListener() {
		@Override
		protected void receivedMessage(int message, Actor actor) {
			if(message == Flag.RAISED) { //if flag has been raised, it means level completed
				levelCompleted();
			}
		}
	};

	// Update the value on panel items
	protected void updatePanel() {
		healthBar.setValue(hero.getHealthRatio());
		coinCounter.setCount(numCoins);
		bombCounter.setCount(numBombs);
		scoreCounter.setScore(score);
		
		if(numBombs>0) {
			bombBtn.setVisible(true);
		} else {
			bombBtn.setVisible(false);
		}
	}

	public int getBombCounts() {
		return numBombs;
	}

	// Collision handling
	@Override
	protected void onCollide(Entity entA, Entity entB, float delta) {
		
		// If hero touches something
		if(entA == hero) {
			heroHitObject(entB);
			return;
		} else if(entB == hero) {
			heroHitObject(entA);
			return;
		}
		
		// Bomb hit enemy
		if(entA instanceof Bomb && entB instanceof Enemy) {
			bombHitEnemy((Bomb)entA , (Enemy)entB);
			return;
		} else if(entB instanceof Bomb && entA instanceof Enemy) {
			bombHitEnemy((Bomb)entB , (Enemy)entA);
			return;
		}
		
		// Enemy hit each other
		if(entA instanceof Enemy && entB instanceof Enemy) {
			enemyHitEnemy((Enemy)entA , (Enemy)entB);
			return;
		}
	}

	protected void toastLabel(String text, float x,float y) {
		toastLabel(text,x,y,0);
	}

	protected void toastLabel(String text, float x,float y,float time) {
		// Get a label
		ToastLabel label = poolScoreLabels.obtain();

		//Init
		label.init(text,time);
		
		// Set position
		label.setPosition(x, y);
		addChild(label);
		
		label.addListener(labelListener);
	}
	

	private void enemyHitEnemy(Enemy enemyA , Enemy enemyB) {
		
		// Sliding enemy hits other enemy
		if(enemyA instanceof Enemy2) {
			if(((Enemy2)enemyA).isSliding()) {
				enemySlideFriend(((Enemy2)enemyA) , enemyB);
				PlanetRunner.media.playSound("hit2");
				return;
			}
		}

		// Sliding enemy hits other enemy
		if(enemyB instanceof Enemy2) {
			if(((Enemy2)enemyB).isSliding()) {
				enemySlideFriend(((Enemy2)enemyB) , enemyA);
				PlanetRunner.media.playSound("hit2");
				return;
			}
		}
		
		// Flip one or all enemy that hit between them, to prevent the display from becoming stacked
		if(enemyA.getX() < enemyB.getX()) {
			if(enemyA.v.x > 0 ) {
				if(enemyB.v.x < 0) {
					enemyA.flip();
					enemyB.flip();
				} else {
					enemyA.flip();
				}
			} else {
				if(enemyB.v.x < 0) {
					enemyB.flip();
				} 
			}
		} else {
			if(enemyB.v.x > 0 ) {
				if(enemyA.v.x < 0) {
					enemyA.flip();
					enemyB.flip();
				} else {
					enemyB.flip();
				}
			} else {
				if(enemyA.v.x < 0) {
					enemyA.flip();
				} 
			}
		}
		
	}
	private void enemySlideFriend(Enemy2 slidingEnemy , Enemy enemy) {
		if(enemy.isHasDied()) {
			return;
		}
				
		if(enemy instanceof Enemy2) {
			enemy.attackedBy(slidingEnemy);
						
			Enemy2 enemy2 = (Enemy2) enemy;
			
			// If also sliding, both of them die
			if(enemy2.isSliding()) {
				slidingEnemy.attackedBy(enemy2);
				heroBeatEnemy(slidingEnemy);
			}
			
			
		} else {
			// Sliding enemy attacks friend
			enemy.attackedBy(slidingEnemy);
			
		}
		
		heroBeatEnemy(enemy);
	}

	private void bombHitEnemy(Bomb bomb , Enemy enemy) {
		removeBomb(bomb);
		enemy.attackedBy(bomb);
		heroBeatEnemy(enemy);
		
		PlanetRunner.media.playSound("hit2");
	}

	private void heroHitObject(Entity obj) {
		if(hero.isDied()) return;
		
		// Hit coin
		if(obj instanceof Coin) {
			Coin coin = (Coin) obj;
			// Remove it
			removeEntity(coin);
			poolCoins.free(coin);
			numCoins++;
			score += coin.getScore();
			
			PlanetRunner.media.playSound("coin");
			
			updatePanel();
			
			return;
		}
		
		if(obj instanceof BombStock) {
			BombStock stock = (BombStock) obj;
			removeEntity(stock);
			
			// Get the amount and update the display
			numBombs += stock.getAmount();
			updatePanel();
			
			toastLabel(String.valueOf(stock.getAmount())+" Bombs", stock.getX(), stock.getY()+30,1);
			
			PlanetRunner.media.playSound("bomb_pack");
			
			return;
		}
		
		if(obj == flag) {
			if(flag.hasRaised()) return;
			heroReachFlag();
			
			return;
		}
		if(obj instanceof Enemy) {
			if(((Enemy)obj).isHasDied()) {
				return;
			}
			// Touch enemy

			// Has hero stepped on the enemy?
			if(hero.v.y < -10 && hero.getBottom() > obj.getY()) {
				Enemy enemy = (Enemy)obj;
				hero.stepEnemy(enemy);
				enemy.attackedByHero(hero,1);
				
				heroBeatEnemy(enemy);
				PlanetRunner.media.playSound("hit2");
				
				
			}
			else {
				// Enemy attacked hero
				if(!hero.isImmune()) {
					
					hero.attackedBy(((Enemy)obj));
					((Enemy)obj).attackHero(hero);
					PlanetRunner.media.playSound("hit2");
					
					updatePanel();
				}
			}
			return;
		}
	}

	// Level completed when hero reach the flag
	private void heroReachFlag() {
		state = GAME_COMPLETED;
		hero.gameCompleted();
		
		score += 500;
		updatePanel();
		toastLabel("500", flag.getX(), hero.getTop());
		
		flag.down();
		
		PlanetRunner.media.playSound("flag");
		PlanetRunner.media.stopMusic("level");
	}

	// Scoring after beating an enemy
	private void heroBeatEnemy(Enemy enemy) {
		if(!enemy.isHasDied()) return;
		
		score += enemy.getScore();
		updatePanel();
		
		// Display the score
		toastLabel(String.valueOf(enemy.getScore()) , enemy.getX() , enemy.getY()+30);
		
	}


	// Key handling

	// Store the boolean values based on pressed keys,
	@Override
	public boolean keyDown(int keycode) {
		
		// On desktop only, while on android use the virtual button
		if(keycode == Keys.A) keyLeft = true;
		if(keycode == Keys.D) keyRight = true;
		if(keycode == Keys.L) keyJump = true;
		if(keycode == Keys.S) keyDodge = true;
		if(keycode == Keys.K) keyFire = true;
				
		if(keycode == Keys.P) {
			if(state == GAME_PAUSED)
				resumeLevel();
			else
				pauseLevel(false);
				
		}
		if(keycode == Keys.ESCAPE || keycode == Keys.BACK){  // Back key pressed
			if(state == GAME_PAUSED) {
				resumeLevel();
			} else {
				pauseLevel();
			}
			return true;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.A) keyLeft = false;
		if(keycode == Keys.D) keyRight = false;
		if(keycode == Keys.L) keyJump = false;
		if(keycode == Keys.S) keyDodge = false;
		if(keycode == Keys.K) keyFire = false;
		return super.keyUp(keycode);
	}

	// Resume game
	private void resumeLevel() {
		state = GAME_PLAY;
		resumeWorld();
		
		if(pauseDialog != null) {
			removeOverlayChild(pauseDialog);
		}
		
		PlanetRunner.media.unMuteAllMusic();
		call(PlanetRunner.LEVEL_RESUMED);
	}

	// Pause game
	private void pauseLevel() {
		pauseLevel(true);
	}
	private void pauseLevel(boolean withdialog) {
		if(state != GAME_PLAY) return;
		
		state = GAME_PAUSED;
		pauseWorld();
		
		if(!withdialog) return;
		
		// Show the paused dialog
		pauseDialog = new PauseDialog();
		addOverlayChild(pauseDialog);
		centerActorXY(pauseDialog);
		pauseDialog.addListener(new MessageListener(){

			//the dialog callback
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(actor == pauseDialog) {
					if(message == PauseDialog.ON_RESUME) {
						resumeLevel();
					}
					else if(message == PauseDialog.ON_QUIT) {
						call(Level.COMPLETED);
					}
				}
			}
			
		});
		
		PlanetRunner.media.muteAllMusic();

		// Track the event
		call(PlanetRunner.LEVEL_PAUSED);
	}

	// A brick is destroyed, create the debris
	protected void destroyBrick(Brick brick) {
		// Remove the brick
		removeLand(brick);
		
		// Create 8 debris
		int num = 8;
		while(num-- > 0) {
			final Debris debris = poolDebris.obtain();
			debris.setPosition((float) (brick.getX()+Math.random()*60-30), (float) (brick.getY()+Math.random()*60-30));
			debris.start();
			addEntity(debris);
			
			// Remove it once it's time has up
			debris.addListener(new MessageListener(){
				@Override
				protected void receivedMessage(int message, Actor actor) {
					if(message == Debris.ON_FINISHED) {
						debris.removeListener(this);
						removeEntity(debris);
						poolDebris.free(debris);
					}
				}
				
			});
		}
	}
	
	
	// Hero fires a bomb
	public void heroFireBomb(boolean toRight) {
		// Get bomb from pool, set position
		Bomb bomb = poolBombs.obtain();
		bomb.setPosition(hero.getX(), hero.getY());

		if(toRight) {
			bomb.moveBy(30, 0 );
		} else {
			bomb.moveBy(-30, 0);
		}
		
		// Set listener and launch bomb
		bomb.addListener(bombListener);
		bomb.launch(toRight);
		if(hero.v.y > 0)
			bomb.setVY(hero.v.y);

		addEntity(bomb);
		
		// Reduce the stock
		numBombs--;
		updatePanel();
	}

	private void removeBomb(Bomb bomb) {
		removeBomb(bomb , true);
	}

	//Remove bomb, replace it with explosion
	private void removeBomb(Bomb bomb,boolean withExplosion) {
		// Remove bomb
		removeEntity(bomb);
		bomb.removeListener(bombListener);
		poolBombs.free(bomb);
		
		
		// If need an explosion
		if(withExplosion) {
			// Put the explosion
			BombExp exp = poolBombExps.obtain();
			addEntity(exp);
			
			exp.setPosition(bomb.getX(), bomb.getY());
			exp.start();
		}
	}
	
	// Bomb listener
	private MessageListener bombListener = new MessageListener(){
		
		@Override
		protected void receivedMessage(int message, Actor actor) { 
			if(message == Bomb.REMOVE) {     // Bomb needs to be removed
				removeBomb((Bomb) actor);
			}
			else if(message == Bomb.REMOVE_NO_EXP) { // Same above but not using explosion
				removeBomb((Bomb) actor,false);
			}
		}		
	};
	
	// Level failed, stop music and call levelfailed2() with a delay time
	private void levelFailed() {
		delayCall("level_failed", 2f);
		camController.followObject(null);
		
		PlanetRunner.media.stopMusic("level");
		call(PlanetRunner.LEVEL_FAILED);
	}

	private void levelFailed2() {
		// Show the level failed dialog
		LevelFailedDialog dialog = new LevelFailedDialog();
		addOverlayChild(dialog);
		centerActorXY(dialog);
		
		// Listen to button click
		dialog.addListener(new MessageListener() {
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if (message == LevelFailedDialog.ON_CLOSE) {
					call(FAILED);
				}
			}
		});
	}

	@Override
	protected void onDelayCall(String code) {
		if(code.equals("level_failed")) {
			pauseWorld();
			levelFailed2();
		}
	}
	protected void levelCompleted() {
		// Persistent data ...

		// update the level progress
		PlanetRunner.data.setLevelProgress(id);
		
		// Update the score
		PlanetRunner.data.updateScore(id, score);
						
		// Stop camera and pause the loop
		camController.followObject(null);
		pauseWorld();
		
		// Show "level completed" dialog box
		LevelCompletedDialog dialog = new LevelCompletedDialog(score);
		addOverlayChild(dialog);
		centerActorXY(dialog);
		
		// Do when 'OK' btn clicked
		dialog.addListener(new MessageListener(){
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == LevelFailedDialog.ON_CLOSE) {
					call(COMPLETED);
				}
			}
		});
		
		PlanetRunner.media.playSound("level_completed");
		PlanetRunner.media.stopMusic("level");
		call(PlanetRunner.LEVEL_COMPLETED);
	}
	

	// The game loop
	@Override
	protected void update(float delta) {
		
		if(state == GAME_PLAY) {
			//notify hero what keys are pressed
			boolean lKeyRight = keyRight || joyStick.isRight();
			boolean lKeyLeft = keyLeft || joyStick.isLeft();
			boolean lKeyJump = keyJump || jumpBtn.isPressed();
			boolean lKeyDodge = keyDodge || dodgeBtn.isPressed();
			boolean lKeyFire = keyFire || bombBtn.isPressed();

			hero.onKey(lKeyLeft, lKeyRight, lKeyJump, lKeyDodge, lKeyFire);

			//Hero falls
			if(hero.getBottom() < 0) {
				hero.fall();
			}
		}
		else if(state == GAME_COMPLETED) {
			hero.onKey(false, false, false, false, false);
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		camController.camera.position.x = (int)camController.camera.position.x ;
		camController.camera.position.y = (int)camController.camera.position.y ;
	}

	public void toastCoin(MysteryBox mysteryBox) {
		final Coin coin = poolCoins.obtain();
		coin.setPosition(mysteryBox.getX(), mysteryBox.getY() + mysteryBox.getHeight()/2 + coin.getHeight()/2);
		addEntity(coin);
		coin.throwUp();
		coin.addListener(new MessageListener(){

			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == Coin.REQUEST_REMOVE) {
					coin.removeListener(this);
					
					//Remove the coin
					removeEntity(coin);
					poolCoins.free(coin);
				}
			}
		});
		
		numCoins++;
		score += coin.getScore();
		updatePanel();
	}

	public void heroHitBrick(Brick brick) {
		destroyBrick(brick);
		PlanetRunner.media.playSound("hit");
	}

	public void heroHitMystery(MysteryBox box) {
		box.hit();
	}

	public void heroHitWater(Water water) {
		hero.setInAir(false);
		hero.setInWater(true);
		return;
	}

	@Override
	public void pause() {
		pauseLevel();
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void hide() {
		PlanetRunner.media.stopAllMusic();
		super.hide();
	}

}
