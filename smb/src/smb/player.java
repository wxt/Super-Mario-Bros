package smb;

import java.awt.geom.Rectangle2D;


import jig.engine.ResourceFactory;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class player extends VanillaAARectangle {
	final int jumpV = -80;
	int Xdirection, Ydirection;
	int speed = 100;
	double vSpeedX, vSpeedY;
	boolean jumped;
	boolean onGround;
	Rectangle2D boundingBox;
	Vector2D tempVelocity, tempPosition;



	
	Vector2D currentVelocity;
	Vector2D previousVelocity;

	player(int x, int y) {
		super(smb.SPRITE_SHEET + "#mario", 4);
		position = new Vector2D(x * smb.TILE_SIZE, y * smb.TILE_SIZE);

	}

	public void update(final long deltaMs) {
		if (!active) {
			return;
		}
		

		if (Xdirection == 1) {
			vSpeedX = speed;
		} else if (Xdirection == 3) {
			vSpeedX = -speed;
		} else {
			vSpeedX = 0;
		}

		if (jumped) {
			vSpeedY = jumpV;
			jumped = false;
		}

		vSpeedY += smb.gravity;

		if (vSpeedY > smb.gravity) {
			vSpeedY = smb.gravity;
		}

		velocity = new Vector2D(vSpeedX, vSpeedY);
		position = position.translate(velocity.scale(deltaMs / 1000.0));

	}
	
	
	/**
	 * To Animate the player
	 * Uncomment this code once ready to animate the player
	 */
	//	this.updateVelocity();
		

	

	public void updateVelocity() {
		this.previousVelocity = this.currentVelocity;
		this.currentVelocity = this.velocity;

		if (this.previousVelocity.getX() > 0 && this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioleft");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioleft");
		} else if (this.previousVelocity.getX() < 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mario");
		} else if (this.previousVelocity.getX() == 0
				&& this.currentVelocity.getX() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mario");
		}

		else if (this.previousVelocity.getY() < 0
				&& this.currentVelocity.getY() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mariodown");
		} else if (this.previousVelocity.getY() == 0
				&& this.currentVelocity.getY() > 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#mariodown");
		}

		else if (this.previousVelocity.getY() > 0
				&& this.currentVelocity.getY() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioup");
		} else if (this.previousVelocity.getY() == 0
				&& this.currentVelocity.getY() < 0) {
			this.frames = ResourceFactory.getFactory().getFrames(
					smb.SPRITE_SHEET + "#marioup");
		}
	}
}