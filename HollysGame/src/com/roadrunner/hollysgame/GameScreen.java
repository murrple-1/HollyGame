package com.roadrunner.hollysgame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.roadrunner.hollysgame.model.Cage;
import com.roadrunner.hollysgame.model.Enemy;
import com.roadrunner.hollysgame.model.HighScore;
import com.roadrunner.hollysgame.model.Player;
import com.roadrunner.hollysgame.model.Player.PlayerDirection;

public class GameScreen implements Screen {
	
	private static final float UI_PADDING = 5.0f;
	
	private final MyGdxGame game;
	
	private Music music;
	
	private Sound[] swordSounds = new Sound[3];
	private Sound damageSound;
	private Sound killSound;
	
	private Stage stage;
	
	private Player player;
	private Collection<Enemy> enemies = new ArrayList<Enemy>();
	private Cage cage;
	private Image[] heartImages;
	private Image[] emptyHeartImages;
	private Label timerLabel;
	private Group enemyGroup;
	private long playTime = 0L;
	
	public GameScreen(final MyGdxGame game) {
		this.game = game;
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("game.mp3"));
		music.setLooping(true);
		music.setVolume(0.5f);
		music.play();
		
		for(int i = 0; i < swordSounds.length; i++) {
			swordSounds[i] = Gdx.audio.newSound(Gdx.files.internal("swing-" + (i + 1) + ".mp3"));
		}
		killSound = Gdx.audio.newSound(Gdx.files.internal("kill.mp3"));
		damageSound = Gdx.audio.newSound(Gdx.files.internal("damage.mp3"));
		
		Image backgroundImage = new Image(this.game.getSkin().getAtlas().findRegion("GameBG"));
		stage.addActor(backgroundImage);
		
		cage = new Cage(this, 3);
		cage.setPosition((stage.getWidth() * 0.5f) - (cage.getWidth() * 0.5f), (stage.getHeight() * 0.5f) - (cage.getHeight() * 0.5f));
		stage.addActor(cage);
		player = new Player(this, PlayerDirection.DOWN);
		player.setPosition(cage.getX(), cage.getY() - (player.getHeight() * 1.5f));
		stage.addActor(player);
		
		enemyGroup = new Group();
		stage.addActor(enemyGroup);
		
		emptyHeartImages = new Image[cage.getMaxHealth()];
		TextureRegion emptyHeartTexture = this.game.getSkin().getAtlas().findRegion("HeartEmpty");
		TextureRegionDrawable emptyHeartTextureDrawable = new TextureRegionDrawable(emptyHeartTexture);
		HorizontalGroup emptyHeartGroup = new HorizontalGroup();
		for(int i = 0; i < cage.getMaxHealth(); i++) {
			Image h = new Image(emptyHeartTextureDrawable);
			emptyHeartImages[i] = h;
			emptyHeartGroup.addActor(h);
		}
		emptyHeartGroup.setPosition(UI_PADDING, stage.getHeight() - (emptyHeartGroup.getChildren().get(0).getHeight() * 0.5f) - UI_PADDING);
		stage.addActor(emptyHeartGroup);
		
		
		heartImages = new Image[cage.getMaxHealth()];
		TextureRegion heartTexture = this.game.getSkin().getAtlas().findRegion("Heart");
		TextureRegionDrawable heartTextureDrawable = new TextureRegionDrawable(heartTexture);
		HorizontalGroup heartGroup = new HorizontalGroup();
		for(int i = 0; i < cage.getMaxHealth(); i++) {
			Image h = new Image(heartTextureDrawable);
			heartImages[i] = h;
			heartGroup.addActor(h);
		}
		UpdateHeartsAction updateHeartsAction = new UpdateHeartsAction(this);
		heartGroup.addAction(Actions.forever(updateHeartsAction));
		heartGroup.setPosition(UI_PADDING, stage.getHeight() - (heartGroup.getChildren().get(0).getHeight() * 0.5f) - UI_PADDING);
		stage.addActor(heartGroup);
		
		PeriodFormatter pf = HighScore.getPeriodFormatter();
		Period p = new Period();
		timerLabel = new Label(pf.print(p), this.game.getSkin());
		timerLabel.setPosition(stage.getWidth() - timerLabel.getWidth() - UI_PADDING, stage.getHeight() - timerLabel.getHeight() - UI_PADDING);
		UpdateTimeAction updateTimeAction = new UpdateTimeAction(this);
		timerLabel.addAction(Actions.forever(updateTimeAction));
		stage.addActor(timerLabel);
		
		UpdatePlayTimeAction updatePlayTimeAction = new UpdatePlayTimeAction();
		SpawnEnemyAction spawnEnemyAction = new SpawnEnemyAction(this);
		HandleKeyboardInputAction handleKeybaordInputAction = new HandleKeyboardInputAction(this);
		stage.addAction(Actions.forever(Actions.parallel(updatePlayTimeAction, spawnEnemyAction, handleKeybaordInputAction)));
	}
	
	public MyGdxGame getGame() {
		return game;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Collection<Enemy> getEnemies() {
		return enemies;
	}
	
	public Group getEnemyGroup() {
		return enemyGroup;
	}
	
	public Cage getCage() {
		return cage;
	}
	
	public Image[] getHeartImages() {
		return heartImages;
	}
	
	public Image[] getEmptyHeartImages() {
		return emptyHeartImages;
	}
	
	public Label getTimerLabel() {
		return timerLabel;
	}
	
	public long getPlayTime() {
		return playTime;
		
	}
	
	public Music getMusic() {
		return music;
	}
	
	public Sound[] getSwordSounds() {
		return swordSounds;
	}
	
	public Sound getKillSound() {
		return killSound;
	}
	
	public Sound getDamageSound() {
		return damageSound;
	}
	
	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}
	
	private class UpdatePlayTimeAction extends Action {
		
		@Override
		public boolean act(float delta) {
			GameScreen.this.playTime += (delta * 1000.0f);
			return true;
		}
		
	}
	
	private static class UpdateTimeAction extends Action {

		private GameScreen screen;
		
		public UpdateTimeAction(GameScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public boolean act(float delta) {
			PeriodFormatter pf = HighScore.getPeriodFormatter();
			Period p = new Period(screen.getPlayTime());
			screen.getTimerLabel().setText(pf.print(p));
			return true;
		}
		
	}
	
	private static class UpdateHeartsAction extends Action {

		private GameScreen screen;
		
		public UpdateHeartsAction(GameScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public boolean act(float delta) {
			Image[] heartImages = screen.getHeartImages();
			for(Actor actor : heartImages) {
				actor.setVisible(false);
			}
			
			for(int i = 0; i < screen.getCage().getCurrentHealth(); i++) {
				Actor actor = heartImages[i];
				actor.setVisible(true);
			}
			return true;
		}
		
	}
	
	private static class SpawnEnemyAction extends Action {

		private Random rand = new Random();
		private GameScreen screen;
		
		public SpawnEnemyAction(GameScreen screen) {
			this.screen = screen;
		}
		
		
		
		@Override
		public boolean act(float delta) {
			Stage stage = screen.getStage();
			boolean spawn = false;
			int enemyCount = screen.getEnemies().size();
			if(enemyCount < minimumCount()) {
				spawn = true;
			}
			if(enemyCount >= maximumCount()) {
				return true;
			}
			if(!spawn) {
				spawn = rand.nextInt(spawnLikeliness()) == 0;
			}
			if(spawn) {
				Enemy e = new Enemy(screen);
				float x = 0.0f;
				float y = 0.0f;
				switch(rand.nextInt(4)) {
				case 0: // UP
					x = (rand.nextFloat() * (stage.getWidth() + e.getWidth())) - e.getWidth();
					y = stage.getHeight();
					break;
				case 1: // DOWN
					x = (rand.nextFloat() * (stage.getWidth() + e.getWidth())) - e.getWidth();
					y = -e.getHeight();
					break;
				case 2: // LEFT
					x = -e.getWidth();
					y = (rand.nextFloat() * (stage.getHeight() + e.getHeight())) - e.getHeight();
					break;
				case 3: // RIGHT
					x = stage.getWidth();
					y = (rand.nextFloat() * (stage.getHeight() + e.getHeight())) - e.getHeight();
					break;
				}
				e.setPosition(x, y);
				
				Cage cage = screen.getCage();
				Vector2 cageVec = new Vector2(cage.getX(), cage.getY());
				Vector2 enemyVec = new Vector2(e.getX(), e.getY());
				Vector2 moveVec = cageVec.sub(enemyVec);
				moveVec = moveVec.nor();
				moveVec.scl(Enemy.MOVE_SPEED);
				e.getVelocity().x = moveVec.x;
				e.getVelocity().y = moveVec.y;
				screen.getEnemies().add(e);
				screen.getEnemyGroup().addActor(e);
			}
			return true;
		}
		
		private static final int MAX_SPAWN = 200;
		private static final int MIN_SPAWN = 20;
		private static final float SPAWN_INCREASE_RATE = 0.002f;
		private static final float MAX_INCREASE_RATE = 0.000095f;
		
		private int spawnLikeliness() {
			float playTime = (float) screen.getPlayTime();
			float retVal = (-playTime * SPAWN_INCREASE_RATE) + ((float) MAX_SPAWN);
			if(retVal < ((float)MIN_SPAWN)) {
				retVal = (float) MIN_SPAWN;
			}
			return (int) retVal;
		}
		
		private int minimumCount() {
			return 1;
		}
		
		private int maximumCount() {
			float playTime = (float) screen.getPlayTime();
			float retVal = playTime * MAX_INCREASE_RATE;
			int r = ((int) retVal) + 1;
			return r;
		}
		
	}
	
	private static class HandleKeyboardInputAction extends KeyboardAction {

		private GameScreen screen;
		
		private static final int[] keys = { Keys.LEFT, Keys.A, Keys.RIGHT, Keys.D, Keys.UP, Keys.W, Keys.DOWN, Keys.S, Keys.SPACE, Keys.ESCAPE };
		
		public HandleKeyboardInputAction(GameScreen screen) {
			super(keys);
			this.screen = screen;
		}

		@Override
		public void handleKeys(boolean[] currentKeys, boolean[] previousKeys) {
			int escapeIndex = keyToIndex(Keys.ESCAPE);
			if(previousKeys != null && previousKeys[escapeIndex] && !currentKeys[escapeIndex]) {
				screen.getGame().setScreen(new MainMenuScreen(screen.getGame()));
				screen.music.stop();
				return;
			}
			
			Player player = screen.getPlayer();
			if (currentKeys[keyToIndex(Keys.LEFT)] || currentKeys[keyToIndex(Keys.A)]) {
				player.getVelocity().x = -Player.STEP_SPEED;
			} else if (currentKeys[keyToIndex(Keys.RIGHT)] || currentKeys[keyToIndex(Keys.D)]) {
				player.getVelocity().x = Player.STEP_SPEED;
			} else {
				player.getVelocity().x = 0.0f;
			}
			
			if (currentKeys[keyToIndex(Keys.UP)] || currentKeys[keyToIndex(Keys.W)]) {
				player.getVelocity().y = Player.STEP_SPEED;
			} else if (currentKeys[keyToIndex(Keys.DOWN)] || currentKeys[keyToIndex(Keys.S)]) {
				player.getVelocity().y = -Player.STEP_SPEED;
			} else {
				player.getVelocity().y = 0.0f;
			}
			
			int spaceIndex = keyToIndex(Keys.SPACE);
			if (previousKeys != null && previousKeys[spaceIndex] && !currentKeys[spaceIndex]) {
				player.swingSword();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height);
	}

	@Override
	public void show() {
		// do nothing
	}

	@Override
	public void hide() {
		// do nothing
	}

	@Override
	public void pause() {
		// do nothing
	}

	@Override
	public void resume() {
		// do nothing
	}

	@Override
	public void dispose() {
		stage.dispose();
		music.dispose();
		for(Sound s : swordSounds) {
			s.dispose();
		}
		killSound.dispose();
		damageSound.dispose();
	}

}