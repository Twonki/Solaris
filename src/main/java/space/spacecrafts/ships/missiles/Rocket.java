package space.spacecrafts.ships.missiles;

import drawing.JavaFXDrawingInformation;
import geom.Rectangle;
import interfaces.logical.DestructibleObject;
import javafx.scene.paint.Color;
import space.effect.Explosion;
import space.spacecrafts.ships.Spaceshuttle;

public class Rocket extends Missile implements DestructibleObject {
	
	public static final int ROCKET_SPEED = 10;
	public static final int ROCKET_SIZE = 3;

	public Rocket(String name, Spaceshuttle emitter, int size) {
		super(name, emitter,  new Rectangle(emitter.getCenter(),size,size*2),new JavaFXDrawingInformation(Color.FIREBRICK),emitter.getRotation(), emitter.getSpeed());
		shape.setLevelOfDetail(2);
	}
	
	public Rocket(String name, Spaceshuttle emitter, int size,double rotation, int speed) {
		super(name, emitter, new Rectangle(emitter.getCenter(),size,size*2),new JavaFXDrawingInformation(Color.FIREBRICK),rotation,speed);
		shape.setLevelOfDetail(2);
	}
	
	@Override
	public void destruct() {
		if(!isOrphan()) {
			new Explosion("Explosion from "+name, center, 5,1000, 1.02, new JavaFXDrawingInformation(Color.FIREBRICK));
			remove();	
		}
	}
	
}
