package com.roadrunner.hollysgame.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.roadrunner.hollysgame.AnimationActor;
import com.roadrunner.hollysgame.GameScreen;
import com.roadrunner.hollysgame.MyGdxGame;

public class Enemy extends AnimationActor {
	
	private Vector2 velocity;
	
	private static final int ENEMY_FRAME_COUNT = 2;
	private static final float ENEMY_FRAME_DURATION = 0.1f;
	
	public static final float MOVE_SPEED = 30.0f;
	
	public Enemy(GameScreen screen) {
		super(createEnemyAnimation(screen.getGame()), enemyAnimationWidth, enemyAnimationHeight);
		this.velocity = new Vector2();
		
		EnemyMoveAction enemyMoveAction = new EnemyMoveAction();
		addAction(Actions.forever(enemyMoveAction));
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	
	private static class EnemyMoveAction extends Action {

		@Override
		public boolean act(float delta) {
			Enemy e = (Enemy) getActor();
			e.setPosition(e.getX() + (e.getVelocity().x * delta), e.getY() + (e.getVelocity().y * delta));
			return true;
		}
		
	}
	
	private static Animation enemyAnimation = null;
	private static float enemyAnimationWidth = Float.MIN_VALUE;
	private static float enemyAnimationHeight = Float.MIN_VALUE;
	
	private static Animation createEnemyAnimation(MyGdxGame game) {
		if(enemyAnimation == null) {
			TextureAtlas atlas = game.getSkin().getAtlas();
			TextureRegion[] frames = new TextureRegion[ENEMY_FRAME_COUNT];
			for(int i = 0; i < ENEMY_FRAME_COUNT; i++) {
				TextureRegion s = atlas.findRegion("Enemy-" + (i + 1));
				frames[i] = s;
				enemyAnimationWidth = Math.max(enemyAnimationWidth, s.getRegionWidth());
				enemyAnimationHeight = Math.max(enemyAnimationHeight, s.getRegionHeight());
			}
			enemyAnimation = new Animation(ENEMY_FRAME_DURATION, new Array<TextureRegion>(frames), Animation.LOOP_PINGPONG);
		}
		return enemyAnimation;
	}
}
