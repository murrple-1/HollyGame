package com.roadrunner.hollysgame;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class PackingMain {
	public static void main(String[] args) {
		TexturePacker2.process("../HollysGame-Graphics/FinalAssets", "../HollysGame-android/assets", "game");
	}

}
