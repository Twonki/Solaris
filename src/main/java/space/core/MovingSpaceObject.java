package space.core;

import drawing.JavaFXDrawingInformation;
import interfaces.drawing.DrawingContext;
import interfaces.drawing.DrawingInformation;
import interfaces.geom.Point;
import interfaces.geom.Shape;
import interfaces.logical.MovingUpdatingObject;
import javafx.scene.transform.Affine;

public abstract class MovingSpaceObject extends SpaceObject implements MovingUpdatingObject {	
	protected int distance;
	protected double speed, relativePos,rotation, rotationSpeed; //Everything in Radians
	
	public MovingSpaceObject(String name,SpaceObject parent,DrawingInformation dInfo, Shape shape,int distance, double speed) {
		super(name,parent.center.clone(),shape,dInfo);
		
		this.distance = distance;
		this.speed = speed;
		rotationSpeed = speed*2;
		
		relativePos = degreeTo(parent);
		parent.trabants.add(this);
		center.setY(parent.center.getY()+distance);
	}
	
	public void move(Point parentCenter) {
		/*
		 * The Move position moves the center to the according relativepos and distance to the parents Center
		 * Therefore, first the moveRelativePos is invoked to update the relative pos 
		 * After moving the center, it rotates itself. 
		 */
		moveRelativePos();
		center.setX(parentCenter.getX()+(int)(Math.cos(relativePos)*distance));
		center.setY(parentCenter.getY()+(int)(Math.sin(relativePos)*distance));
		rotate();
	};
	
	public void moveRelativePos() {
		// This moves the spaceobject around its center-piece and if there is more than 1 full degree it reduces the relativepos by one cicle
		relativePos += speed;
		if (relativePos >= Math.PI*2) {
			relativePos -=  Math.PI*2;
		} else if(relativePos< 0 ) {
			relativePos += Math.PI*2;
		}
	}
	
	public void rotate() {
		// This rotates the spaceobject around itself and if there is more than 1 full degree it reduces the rotation by one cicle
		rotation += rotationSpeed;
		if (rotation >= Math.PI*2) {
			rotation -=  Math.PI*2;
		} else if (rotation < 0) {
			rotation += Math.PI*2;
		}
	}
	
	public boolean isFasterThanMe(SpaceObject other) {
		if(other instanceof MovingSpaceObject) {
			MovingSpaceObject otherCasted = (MovingSpaceObject) other;
			return 	speed != 0
					&& otherCasted.speed != 0
					&& Math.abs(otherCasted.speed) > Math.abs(speed);
		}
		return false;
	}
	
	public boolean movesInSameDirection(SpaceObject other) {
		if(other instanceof MovingSpaceObject) {
			MovingSpaceObject otherCasted = (MovingSpaceObject) other;
			return 	otherCasted.speed > 0 && speed > 0 || otherCasted.speed < 0 && speed < 0;
		}
		return false;
	}
	
	@Override
	public void draw(DrawingContext dc) {
		updateDrawingInformation();
		super.draw(dc);
	}
	
    
    @Override
    public int drawingPriority() {
    	// Default middle-time drawing. Background is 0-3, effects and missiles are 8+
    	return 5;
    }
	
	protected void updateDrawingInformation() {
		DrawingInformation dInfo = getDrawingInformation() ;
		if(dInfo  instanceof JavaFXDrawingInformation) {
			Affine transformRotation = new Affine();
			transformRotation.appendRotation(Math.toDegrees(rotation), center.getX() ,center.getY());	
			((JavaFXDrawingInformation)dInfo).transformations.clear();
			((JavaFXDrawingInformation)dInfo).transformations.add(transformRotation);
		}
	}

	public int getDistance() {return distance;}
	public void setDistance(int distance) {this.distance = distance;}
	public double getSpeed() {return speed;}
	public void setSpeed(double speed) {this.speed = speed;}
	public double getRelativePos() {return relativePos;}
	public void setRelativePos(double relativePos) {this.relativePos = relativePos;}
	public double getRotation() {return rotation;}
	public void setRotation(double rotation) {this.rotation = rotation;}
	public double getRotationSpeed() {return rotationSpeed;}
	public void setRotationSpeed(double rotationSpeed) {this.rotationSpeed = rotationSpeed;}

	
}
