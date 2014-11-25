package com.roadrunner.hollysgame.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Array;
import com.roadrunner.hollysgame.GameScreen;
import com.roadrunner.hollysgame.MyGdxGame;

public class Player extends Actor {
	
	public enum PlayerDirection { UP, DOWN, LEFT, RIGHT }
	public static final PlayerDirection[] PlayerDirections = PlayerDirection.values();
	
	public static final float STEP_SPEED = 250.0f;
	
	private static final int PLAYER_STAND_FRAME_COUNT = 1;
	private static final int PLAYER_WALK_FRAME_COUNT = 2;
	private static final int PLAYER_SWING_FRAME_COUNT = 2;
	private static final float PLAYER_FRAME_DURATION = 0.1f;
	
	private GameScreen screen;
	private Random rand = new Random();
	
	private Vector2 velocity;
	private PlayerDirection direction;
	private float stateTime = 0.0f;
	private static final Rectangle edgeRectangle = new Rectangle(0.0f, 0.0f, 10.0f, 10.0f);
	private static final Rectangle defaultSwordRectangle = new Rectangle(0.0f, 0.0f, 50.0f, 50.0f);
	private Rectangle swordRectangle = null;
	
	private static Animation[] animations = null;
	private static float playerAnimationsWidth = Float.MIN_VALUE;
	private static float playerAnimationsHeight = Float.MIN_VALUE;
	
	public Player(GameScreen screen, PlayerDirection direction) {
		this.screen = screen;
		createPlayerAnimations(screen.getGame());
		this.velocity = new Vector2();
		this.direction = direction;
		setWidth(playerAnimationsWidth);
		setHeight(playerAnimationsHeight);
		
		PlayerSetDirectionAction playerSetDirectionAction = new PlayerSetDirectionAction();
		PlayerUpdateStateTimeAction playerUpdateStateTimeAction = new PlayerUpdateStateTimeAction();
		PlayerMoveAction playerMoveAction = new PlayerMoveAction(screen.getStage(), screen.getCage());
		addAction(Actions.forever(Actions.parallel(playerUpdateStateTimeAction, Actions.sequence(playerSetDirectionAction, playerMoveAction))));
	}
	
	public static char playerDirectionToChar(PlayerDirection d) {
		switch (d) {
		case UP:
			return 'u';
		case DOWN:
			return 'd';
		case LEFT:
			return 'l';
		case RIGHT:
			return 'r';
		default:
			return 0;
		}
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	
	public PlayerDirection getDirection() {
		return direction;
	}
	
	public void setDirection(PlayerDirection direction) {
		this.direction = direction;
	}
	
	private static class PlayerUpdateStateTimeAction extends Action {

		@Override
		public boolean act(float delta) {
			Player p = (Player) getActor();
			p.stateTime += delta;
			return true;
		}
		
	}
	
	private static class PlayerSetDirectionAction extends Action {

		@Override
		public boolean act(float delta) {
			Player player = (Player) getActor();
			if(player.velocity.len() > 0.0f) {
				if(player.velocity.x < 0.0f) {
					player.direction = PlayerDirection.LEFT;
				} else if(player.velocity.x > 0.0f) {
					player.direction = PlayerDirection.RIGHT;
				} else if(player.velocity.y < 0.0f) {
					player.direction = PlayerDirection.DOWN;
				} else if(player.velocity.y > 0.0f) {
					player.direction = PlayerDirection.UP;
				}
			}
			return true;
		}
		
	}
	
	private static class PlayerMoveAction extends Action {

		private Stage stage;
		private Cage cage;
		
		public PlayerMoveAction(Stage stage, Cage cage) {
			this.stage = stage;
			this.cage = cage;
		}
		
		@Override
		public boolean act(float delta) {
			Player player = (Player) getActor();
			Rectangle cageRectangle = new Rectangle(cage.getX(), cage.getY(), cage.getWidth(), cage.getHeight());
			
			Rectangle initialPlayerRect = new Rectangle(player.getX() + edgeRectangle.width, player.getY() + edgeRectangle.height, player.getWidth() - (2 * edgeRectangle.width), player.getHeight() - (2 * edgeRectangle.height));
			Rectangle hPlayerRect = new Rectangle(initialPlayerRect.x + (player.getVelocity().x * delta), initialPlayerRect.y, initialPlayerRect.width, initialPlayerRect.height);
			Rectangle vPlayerRect = new Rectangle(initialPlayerRect.x, initialPlayerRect.y + (player.getVelocity().y * delta), initialPlayerRect.width, initialPlayerRect.height);
			Rectangle stepPlayerRect = new Rectangle(hPlayerRect.x, vPlayerRect.y, initialPlayerRect.width, initialPlayerRect.height);
			if(!stepPlayerRect.overlaps(cageRectangle) && stepPlayerRect.x > 0.0f && stepPlayerRect.x < (stage.getWidth() - stepPlayerRect.getWidth()) && stepPlayerRect.y > 0.0f && stepPlayerRect.y < (stage.getHeight() - stepPlayerRect.getHeight())) {
				player.setPosition(stepPlayerRect.x - edgeRectangle.width, stepPlayerRect.y - edgeRectangle.height);
			} else if(!hPlayerRect.overlaps(cageRectangle) && hPlayerRect.x > 0.0f && hPlayerRect.x < (stage.getWidth() - hPlayerRect.width)) {
				player.setPosition(hPlayerRect.x - edgeRectangle.width, hPlayerRect.y - edgeRectangle.height);
			} else if(!vPlayerRect.overlaps(cageRectangle) && vPlayerRect.y > 0.0f && vPlayerRect.y < (stage.getHeight() - vPlayerRect.height)) {
				player.setPosition(vPlayerRect.x - edgeRectangle.width, vPlayerRect.y - edgeRectangle.height);
			}
			return true;
		}
	}
	
	public void swingSword() {
		if(swordRectangle == null) {
			Sound[] swordSounds = screen.getSwordSounds();
			swordSounds[rand.nextInt(swordSounds.length)].play();
			swordRectangle = new Rectangle(defaultSwordRectangle);
			
			SwordMoveAction swordMoveAction = new SwordMoveAction();
			CheckKillsAction checkKillsAction = new CheckKillsAction();
			RepeatAction repeatAction = Actions.forever(Actions.sequence(swordMoveAction, checkKillsAction));
			addAction(repeatAction);
			
			WithdrawSwordAction withdrawSwordAction = new WithdrawSwordAction();
			RemoveAction removeAction = Actions.removeAction(repeatAction, this);
			addAction(Actions.delay(PLAYER_FRAME_DURATION * PLAYER_SWING_FRAME_COUNT, Actions.parallel(withdrawSwordAction, removeAction)));
		}
	}
	
	private static class WithdrawSwordAction extends Action {

		@Override
		public boolean act(float delta) {
			Player player = (Player) getActor();
			player.swordRectangle = null;
			return true;
		}
		
	}
	
	private static class SwordMoveAction extends Action {

		@Override
		public boolean act(float delta) {
			Player player = (Player) getActor();
			PlayerDirection pDir = player.getDirection();
			switch(pDir) {
			case UP:
				player.swordRectangle.width = player.getWidth();
				player.swordRectangle.height = defaultSwordRectangle.height;
				player.swordRectangle.x = player.getX();
				player.swordRectangle.y = player.getY();
				break;
			case DOWN:
				player.swordRectangle.width = player.getWidth();
				player.swordRectangle.height = defaultSwordRectangle.height;
				player.swordRectangle.x = player.getX() + player.getWidth() - defaultSwordRectangle.width;
				player.swordRectangle.y = player.getY();
				break;
			case LEFT:
				player.swordRectangle.width = defaultSwordRectangle.width;
				player.swordRectangle.height = player.getHeight();
				player.swordRectangle.x = player.getX();
				player.swordRectangle.y = player.getY();
				break;
			case RIGHT:
				player.swordRectangle.width = defaultSwordRectangle.width;
				player.swordRectangle.height = player.getHeight();
				player.swordRectangle.x = player.getX() + player.getWidth() - player.swordRectangle.width;
				player.swordRectangle.y = player.getY();
				break;
			}
			
			return true;
		}
		
	}
	
	private static class CheckKillsAction extends Action {

		@Override
		public boolean act(float delta) {
			Player player = (Player) getActor();
			boolean madeKill = false;
			Collection<Enemy> destroyedEnemies = new ArrayList<Enemy>();
			for(Enemy e : player.screen.getEnemies()) {
				Rectangle enemyRect = new Rectangle(e.getX(), e.getY(), e.getWidth(), e.getHeight());
				if(player.swordRectangle.overlaps(enemyRect)) {
					e.remove();
					destroyedEnemies.add(e);
					madeKill = true;
				}
			}
			if(madeKill) {
				player.screen.getKillSound().play();
				for(Enemy e : destroyedEnemies) {
					player.screen.getEnemies().remove(e);
				}
			}
			return true;
		}
		
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		int step;
		if(swordRectangle != null) {
			step = 4;
		} else if(velocity.len() < 0.1) {
			step = 0;
		} else {
			step = 8;
		}
		Animation a = animations[direction.ordinal() + step];
		TextureRegion region = a.getKeyFrame(stateTime);
		batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
	private static void createPlayerAnimations(MyGdxGame game) {
		if(animations == null) {
			animations = new Animation[PlayerDirections.length * 3];
			
			TextureAtlas atlas = game.getSkin().getAtlas();
			float width = Float.MIN_VALUE;
			float height = Float.MIN_VALUE;
			for(int i = 0; i < PlayerDirections.length; i++) {
				PlayerDirection d = PlayerDirections[i];
				char dChar = playerDirectionToChar(d);
				TextureRegion[] frames = new TextureRegion[PLAYER_STAND_FRAME_COUNT];
				for(int j = 0; j < PLAYER_STAND_FRAME_COUNT; j++) {
					TextureRegion s = atlas.findRegion("Hero-stand-" + dChar + '-' + (j + 1));
					frames[j] = s;
					width = Math.max(width, s.getRegionWidth());
					height = Math.max(height, s.getRegionHeight());
				}
				Animation a = new Animation(PLAYER_FRAME_DURATION, new Array<TextureRegion>(frames), Animation.LOOP);
				animations[i] = a;
			}
			for(int i = 0; i < PlayerDirections.length; i++) {
				PlayerDirection d = PlayerDirections[i];
				char dChar = playerDirectionToChar(d);
				TextureRegion[] frames = new TextureRegion[PLAYER_SWING_FRAME_COUNT];
				for(int j = 0; j < PLAYER_SWING_FRAME_COUNT; j++) {
					TextureRegion s = atlas.findRegion("Hero-swing-" + dChar + '-' + (j + 1));
					frames[j] = s;
					width = Math.max(width, s.getRegionWidth());
					height = Math.max(height, s.getRegionHeight());
				}
				Animation a = new Animation(PLAYER_FRAME_DURATION, new Array<TextureRegion>(frames), Animation.LOOP);
				animations[i + PlayerDirections.length] = a;
			}
			for(int i = 0; i < PlayerDirections.length; i++) {
				PlayerDirection d = PlayerDirections[i];
				char dChar = playerDirectionToChar(d);
				TextureRegion[] frames = new TextureRegion[PLAYER_WALK_FRAME_COUNT];
				for(int j = 0; j < PLAYER_WALK_FRAME_COUNT; j++) {
					TextureRegion s = atlas.findRegion("Hero-walk-" + dChar + '-' + (j + 1));
					frames[j] = s;
					width = Math.max(width, s.getRegionWidth());
					height = Math.max(height, s.getRegionHeight());
				}
				Animation a = new Animation(PLAYER_FRAME_DURATION, new Array<TextureRegion>(frames), Animation.LOOP);
				animations[i + PlayerDirections.length + PlayerDirections.length] = a;
			}
			
			playerAnimationsWidth = width;
			playerAnimationsHeight = height;
		}
	}
	
}
