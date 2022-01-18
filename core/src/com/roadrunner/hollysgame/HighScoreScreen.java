package com.roadrunner.hollysgame;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.roadrunner.hollysgame.model.HighScore;

public class HighScoreScreen implements Screen {
	private final MyGdxGame game;
	
	private final Music music;
	
	private final Stage stage;

	private static final String highScoreTitle = "High Scores";
	
	public HighScoreScreen(final MyGdxGame game, boolean showRestart, boolean showMainMenu) {
		this.game = game;
		
		music = Gdx.audio.newMusic(Gdx.files.internal("game_over.mp3"));
		music.setLooping(true);
		music.setVolume(0.3f);
		
		TextureAtlas atlas = this.game.getSkin().getAtlas();

		HighScore[] highScores = HighScore.getHighScores();
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		Image backgroundImage = new Image(atlas.findRegion("HighScore"));
		stage.addActor(backgroundImage);
		VerticalGroup verticalGroup = new VerticalGroup();
		verticalGroup.setPosition(stage.getWidth() * 0.5f, stage.getHeight());
		stage.addActor(verticalGroup);
		Label titleLabel = new Label(highScoreTitle, this.game.getSkin());
		verticalGroup.addActor(titleLabel);
		
		PeriodFormatter pf = HighScore.getPeriodFormatter();
		for(int i = 0; i < highScores.length; i++) {
			HighScore hs = highScores[i];
			if(hs != null) {
				Period hsP = hs.getTime().toPeriod();
				Label hsLabel = new Label((i + 1) + ". " + hs.getName() + "  |  " + pf.print(hsP), this.game.getSkin());
				verticalGroup.addActor(hsLabel);
			} else {
				Label hsLabel = new Label((i + 1) + ". " + "---  |  ---", this.game.getSkin());
				verticalGroup.addActor(hsLabel);
			}
		}
		if(showRestart) {
			ImageTextButton restartButton = new ImageTextButton("Restart", this.game.getSkin());
			ClickListener restartL = new RestartClickListener(this);
			restartButton.addListener(restartL);
			verticalGroup.addActor(restartButton);
		}
		if(showMainMenu) {
			ImageTextButton mainMenuButton = new ImageTextButton("Main Menu", this.game.getSkin());
			ClickListener mainMenuL = new MainMenuClickListener(this);
			mainMenuButton.addListener(mainMenuL);
			verticalGroup.addActor(mainMenuButton);
		}
		ImageTextButton quitButton = new ImageTextButton("Quit", this.game.getSkin());
		ClickListener quitL = new QuitClickListener();
		quitButton.addListener(quitL);
		verticalGroup.addActor(quitButton);
	}
	
	private static class RestartClickListener extends ClickListener {
		private final HighScoreScreen screen;
		
		public RestartClickListener(HighScoreScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			screen.game.setScreen(new GameScreen(screen.game));
		}
	}
	
	private static class MainMenuClickListener extends ClickListener {
		private final HighScoreScreen screen;
		
		public MainMenuClickListener(HighScoreScreen screen) {
			this.screen = screen;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			screen.game.setScreen(new MainMenuScreen(screen.game));
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

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setWorldSize(width, height);
	}

	@Override
	public void show() {
		music.play();
	}

	@Override
	public void hide() {
		music.stop();
	}

	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void resume() {
		music.play();
	}

	@Override
	public void dispose() {
		stage.dispose();
		music.dispose();
	}
}
