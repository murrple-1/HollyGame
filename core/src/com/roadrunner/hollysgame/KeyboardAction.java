package com.roadrunner.hollysgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;

public abstract class KeyboardAction extends Action {
	private final int[] keys;
	
	private boolean[] previousKeys;
	
	public KeyboardAction(int[] keys) {
		this.keys = keys;
	}
	
	@Override
	public boolean act(float delta) {
		boolean[] currentKeys = readKeys();
		handleKeys(currentKeys, previousKeys);
		previousKeys = currentKeys;
		return true;
	}
	
	private boolean[] readKeys() {
		boolean[] k = new boolean[keys.length];
		for(int i = 0; i < keys.length; i++) {
			k[i] = Gdx.input.isKeyPressed(keys[i]);
		}
		return k;
	}
	
	protected int keyToIndex(int key) {
		for(int i = 0; i < keys.length; i++) {
			if(key == keys[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public abstract void handleKeys(boolean[] currentKeys, boolean[] previousKeys);
}
