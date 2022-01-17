package com.roadrunner.hollysgame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public abstract class AnimationActor extends Actor {
	private final Animation<TextureRegion> animation;
	private float stateTime = 0.0f;
	
	public AnimationActor(Animation<TextureRegion> animation, float width, float height) {
		this.animation = animation;
		setWidth(width);
		setHeight(height);
		
		UpdateStateTimeAction updateStateTimeAction = new UpdateStateTimeAction();
		addAction(Actions.forever(updateStateTimeAction));
	}
	
	private static class UpdateStateTimeAction extends Action {
		@Override
		public boolean act(float delta) {
			AnimationActor aa = (AnimationActor) getActor();
			aa.stateTime += delta;
			return true;
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		TextureRegion region = animation.getKeyFrame(stateTime);
		batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
}
