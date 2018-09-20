/**
 * @Author Leonhard Applis
 * @Created 31.08.2018
 * @Package space.core
 */
package space.core;

import geom.Rectangle;
import interfaces.logical.CollidingObject;
import interfaces.logical.DestructibleObject;
import javafx.scene.paint.Color;
import space.effect.Explosion;

@SuppressWarnings("restriction")
public class Satellite extends MovingSpaceObject implements DestructibleObject{
	private SpaceObject parent;
	public Satellite(String name, SpaceObject parent, int size, int distance, double speed) {
		super(name, parent,null, new Rectangle(size,size), distance, speed);
		this.parent=parent;
	}
	
	@Override 
	public void rotate() {
		if(parent!=null)
			rotation=degreeTo(parent);
	}
	
	@Override
	public void remove() {
		parent.trabants.remove(this);
		parent=null;		
	}
	@Override
	public void destruct(CollidingObject other) {
		new Explosion("Explosion from" + name,center.x,center.y,800,6,1.03,Color.FIREBRICK);
		if(parent!=null)
			remove();
	}

}
