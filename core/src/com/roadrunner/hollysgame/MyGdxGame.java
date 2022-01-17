package com.roadrunner.hollysgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.roadrunner.hollysgame.model.HighScore;

public class MyGdxGame extends Game {
	private Skin skin;
	
	@Override
	public void create() {
		skin = new Skin(Gdx.files.internal("game.json"));
		setScreen(new MainMenuScreen(this));
	}
	
	public Skin getSkin() {
		return skin;
	}

	@Override
	public void dispose() {
		super.dispose();
		skin.dispose();
		HighScore.saveHighScores();
	}
}
