package space.spacecrafts.ships;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import geom.AbsolutePoint;
import interfaces.logical.CollidingObject;
import interfaces.logical.MovingObject;
import interfaces.logical.UpdatingObject;
import logic.manager.CollisionManager;
import space.advanced.FixStar;
import space.core.SpaceObject;

/*
 * This SensorArray is a collidable item, which Pseudocollides with everything in the radius 
 * The pseudocollided items will be reportet to parent-spaceshuttle, yet wont explode or anything
 */
public class SensorArray implements UpdatingObject,MovingObject{
	
	int radius;
	AbsolutePoint center;
	SpaceShuttle emitter;
	public List<CollidingObject> detectedItems=new LinkedList<CollidingObject>();
	
	public SensorArray (SpaceShuttle emitter,int radius) {
		this.emitter=emitter;
		center=emitter.center;
		this.radius=radius;
	}
	
	@Override
	public void update() {
		detectedItems=
				CollisionManager.getInstance().registeredItems.stream()
				.filter(c-> c instanceof SpaceObject)
				.filter(c->!(c instanceof FixStar))
				.map(c->(SpaceObject)c)
				.filter(other -> isCovered(other.center.getX(), other.center.getY()))
				.filter(c->!(c == emitter))
				.collect(Collectors.toList());
	}
	@Override
	public void move(AbsolutePoint parentCenter) {}
	
	private boolean isCovered(int otherX, int otherY) {
		return
				otherY>=center.getY()-radius && otherY<=center.getY()+radius
			&&	otherX>=center.getX()-radius && otherX<=center.getX()+radius;
	}

}
