package com.roadrunner.hollysgame.model;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.Duration;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.roadrunner.hollysgame.AnimationActor;
import com.roadrunner.hollysgame.GameScreen;
import com.roadrunner.hollysgame.HighScoreScreen;
import com.roadrunner.hollysgame.MyGdxGame;
import com.roadrunner.hollysgame.RegisterHighScoreScreen;

public class Cage extends AnimationActor {

	private int maxHealth;
	private int currentHealth;
	
	private static final int CAGE_FRAME_COUNT = 4;
	private static final float CAGE_FRAME_DURATION = 0.1f;
	
	public Cage(GameScreen screen, int maxHealth) {
		super(createCageAnimation(screen.getGame()), cageAnimationWidth, cageAnimationHeight);
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
		
		CheckCageDamage checkCageDamage = new CheckCageDamage(screen);
		CheckHealthAction checkHealthAction = new CheckHealthAction(screen);
		addAction(Actions.forever(Actions.sequence(checkCageDamage, checkHealthAction)));
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}
	
	private static class CheckCageDamage extends Action {

		private GameScreen screen;
		
		public CheckCageDamage(GameScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public boolean act(float delta) {
			Cage cage = (Cage) getActor();
			Rectangle cageRectangle = new Rectangle(cage.getX(), cage.getY(), cage.getWidth(), cage.getHeight());
			Collection<Enemy> damageEnemies = new ArrayList<Enemy>();
			boolean didDamage = false;
			for(Enemy e : screen.getEnemies()) {
				Rectangle enemyRect = new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight());
				if(cageRectangle.overlaps(enemyRect)) {
					cage.setCurrentHealth(cage.getCurrentHealth() - 1);
					e.remove();
					damageEnemies.add(e);
					didDamage = true;
				}
			}
			
			if(didDamage) {
				screen.getDamageSound().play();
				for(Enemy e : damageEnemies) {
					screen.getEnemies().remove(e);
				}
			}
			return true;
		}
		
	}
	
	private static class CheckHealthAction extends Action {

		private GameScreen screen;
		
		public CheckHealthAction(GameScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public boolean act(float delta) {
			Cage cage = (Cage) getActor();
			if(cage.getCurrentHealth() < 1) {
				HighScore[] highScores = HighScore.getHighScores();
				Duration d = new Duration(screen.getPlayTime());
				boolean isHighScore = false;
				for(HighScore hs : highScores) {
					if(hs == null) {
						isHighScore = true;
						break;
					} else {
						if(d.isLongerThan(hs.getTime())) {
							isHighScore = true;
							break;
						}
					}
				}
				if(isHighScore) {
					screen.getGame().setScreen(new RegisterHighScoreScreen(screen.getGame(), d));
					screen.getMusic().stop();
				} else {
					screen.getGame().setScreen(new HighScoreScreen(screen.getGame(), true, true));
					screen.getMusic().stop();
				}
			}
			return true;
		}
		
	}
	
	private static Animation cageAnimation = null;
	private static float cageAnimationWidth = Float.MIN_VALUE;
	private static float cageAnimationHeight = Float.MIN_VALUE;
	
	private static Animation createCageAnimation(MyGdxGame game) {
		if(cageAnimation == null) {
			TextureAtlas atlas = game.getSkin().getAtlas();
			TextureRegion[] frames = new TextureRegion[CAGE_FRAME_COUNT];
			for(int i = 0; i < CAGE_FRAME_COUNT; i++) {
				TextureRegion s = atlas.findRegion("Cage-" + (i + 1));
				frames[i] = s;
				cageAnimationWidth = Math.max(cageAnimationWidth, s.getRegionWidth());
				cageAnimationHeight = Math.max(cageAnimationHeight, s.getRegionHeight());
			}
			cageAnimation = new Animation(CAGE_FRAME_DURATION, new Array<TextureRegion>(frames), Animation.LOOP_PINGPONG);
		}
		return cageAnimation;
	}
}
