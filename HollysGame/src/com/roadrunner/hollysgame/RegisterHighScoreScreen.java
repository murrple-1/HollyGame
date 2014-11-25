package com.roadrunner.hollysgame;

import java.util.Arrays;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.roadrunner.hollysgame.model.HighScore;

public class RegisterHighScoreScreen implements Screen {

	private final MyGdxGame game;
	
	private Stage stage;
	private TextField nameField;
	private Label errorLabel;
	private Duration time;
	
	private static final String registerHSTitle = "Register High Score";
	private static final String yourTimeTitle = "Your Time";
	
	public RegisterHighScoreScreen(final MyGdxGame game, Duration time) {
		this.game = game;
		this.time = time;
		
		TextureAtlas atlas = this.game.getSkin().getAtlas();
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Image backgroundImage = new Image(atlas.findRegion("HighScore"));
		stage.addActor(backgroundImage);
		VerticalGroup verticalGroup = new VerticalGroup();
		verticalGroup.setPosition(stage.getWidth() * 0.5f, stage.getHeight());
		stage.addActor(verticalGroup);
		Label titleLabel = new Label(registerHSTitle, this.game.getSkin());
		verticalGroup.addActor(titleLabel);
		
		HorizontalGroup yourTimeGroup = new HorizontalGroup();
		verticalGroup.addActor(yourTimeGroup);
		Label yourTimeTitleLabel = new Label(yourTimeTitle, this.game.getSkin());
		yourTimeGroup.addActor(yourTimeTitleLabel);
		Actor timeBuffer = new Actor();
		timeBuffer.setSize(40.0f, 0.0f);
		yourTimeGroup.addActor(timeBuffer);
		PeriodFormatter pf = HighScore.getPeriodFormatter();
		Period hsP = this.time.toPeriod();
		Label yourTimeLabel = new Label(pf.print(hsP), this.game.getSkin());
		yourTimeGroup.addActor(yourTimeLabel);
		nameField = new TextField("", this.game.getSkin());
		stage.setKeyboardFocus(nameField);
		verticalGroup.addActor(nameField);
		errorLabel = new Label(null, this.game.getSkin());
		errorLabel.setColor(Color.RED);
		errorLabel.setVisible(false);
		verticalGroup.addActor(errorLabel);
		ImageTextButton okButton = new ImageTextButton("OK", this.game.getSkin());
		ClickListener okL = new OKClickListener(this);
		okButton.addListener(okL);
		verticalGroup.addActor(okButton);
		
		HandleKeyboardInputAction handleKeyboardInputAction = new HandleKeyboardInputAction(this);
		stage.addAction(Actions.forever(handleKeyboardInputAction));
	}
	
	public void trySubmit() {
		String name = nameField.getText();
		if(!name.isEmpty()) {
			HighScore[] highScores = HighScore.getHighScores();
			HighScore newScore = new HighScore(name, time);
			highScores[highScores.length - 1] = newScore;
			Arrays.sort(highScores, new HighScore.HighScoreComparator());
			game.setScreen(new HighScoreScreen(game, true, true));
		} else {
			errorLabel.setText("Name must not be empty");
			errorLabel.setVisible(true);
		}
	}
	
	private static class OKClickListener extends ClickListener {
		
		private RegisterHighScoreScreen screen;
		
		public OKClickListener(RegisterHighScoreScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			screen.trySubmit();
		}
	}
	
	private static class HandleKeyboardInputAction extends KeyboardAction {

		private static final int[] keys = { Keys.ENTER };
		
		private RegisterHighScoreScreen screen;
		
		public HandleKeyboardInputAction(RegisterHighScoreScreen screen) {
			super(keys);
			this.screen = screen;
		}

		@Override
		public void handleKeys(boolean[] currentKeys, boolean[] previousKeys) {
			int enterIndex = keyToIndex(Keys.ENTER);
			if(previousKeys != null && previousKeys[enterIndex] && !currentKeys[enterIndex]) {
				screen.trySubmit();
			}
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
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
	}

}
