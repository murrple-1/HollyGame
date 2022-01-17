package com.roadrunner.hollysgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.roadrunner.hollysgame.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Holly's Battle";
		config.width = 480;
		config.height = 320;
		config.resizable = false;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
