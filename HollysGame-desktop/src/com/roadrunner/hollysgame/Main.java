package com.roadrunner.hollysgame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Holly's Battle";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;
		cfg.resizable = false;
		
		new LwjglApplication(new MyGdxGame(), cfg);
	}
}
