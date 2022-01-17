package com.roadrunner.hollysgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
	private final MyGdxGame game;
	
	private final Music music;
	
	private final Stage stage;
	
	public MainMenuScreen(final MyGdxGame game) {
		this.game = game;
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
		music.setLooping(true);
		music.setVolume(0.05f);
		music.play();
		
		Image backgroundImage = new Image(this.game.getSkin().getAtlas().findRegion("TitleScreen"));
		stage.addActor(backgroundImage);
		VerticalGroup verticalGroup = new VerticalGroup();
		verticalGroup.setPosition(200.0f, 150.0f);
		stage.addActor(verticalGroup);
		
		ImageTextButton startButton = new ImageTextButton("Start", this.game.getSkin());
		ClickListener startL = new StartClickListener(this);
		startButton.addListener(startL);
		verticalGroup.addActor(startButton);
		ImageTextButton highScoreButton = new ImageTextButton("High Scores", this.game.getSkin());
		ClickListener hsL = new HighScoreListener(this);
		highScoreButton.addListener(hsL);
		verticalGroup.addActor(highScoreButton);
		ImageTextButton quitButton = new ImageTextButton("Quit", this.game.getSkin());
		ClickListener quitL = new QuitClickListener();
		quitButton.addListener(quitL);
		verticalGroup.addActor(quitButton);
		
		HandleKeyboardInputAction handleKeyboardInputAction = new HandleKeyboardInputAction();
		stage.addAction(Actions.forever(handleKeyboardInputAction));
	}
	
	private static class StartClickListener extends ClickListener {
		private final MainMenuScreen screen;
		
		public StartClickListener(MainMenuScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			screen.music.stop();
			screen.game.setScreen(new GameScreen(screen.game));
		}
	}
	
	private static class HighScoreListener extends ClickListener {
		private final MainMenuScreen screen;
		
		public HighScoreListener(MainMenuScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			screen.music.stop();
			screen.game.setScreen(new HighScoreScreen(screen.game, false, true));
		}
	}
	
	private static class QuitClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Gdx.app.exit();
		}
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		stage.act(delta);
		stage.draw();
	}
	
	private static class HandleKeyboardInputAction extends KeyboardAction {
		private static final int[] keys = { Keys.ESCAPE };
		
		public HandleKeyboardInputAction() {
			super(keys);
		}

		@Override
		public void handleKeys(boolean[] currentKeys, boolean[] previousKeys) {
			int escapeIndex = keyToIndex(Keys.ESCAPE);
			if(previousKeys != null && previousKeys[escapeIndex] && !currentKeys[escapeIndex]) {
				Gdx.app.exit();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setWorldSize(width, height);
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
	}

}
