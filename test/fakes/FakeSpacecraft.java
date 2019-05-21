package fakes;

import java.util.LinkedList;
import java.util.List;

import interfaces.geom.Point;
import interfaces.logical.CollidingObject;
import interfaces.spacecraft.Spacecraft;
import interfaces.spacecraft.SpacecraftState;
import space.core.SpaceObject;

public class FakeSpacecraft implements Spacecraft{
	
	public boolean launched=false,dead=false,collided=false,moved=false,attacked=false,rebuild=false,removed=false;
	public boolean colliding = false;
	
	public SpacecraftState state = SpacecraftState.ORBITING;
	
	public SpaceObject parent,target;
	
	public Point moveFrom,moveTo;
	
	public List<CollidingObject> itemsToDetect= new LinkedList<CollidingObject>();
	
	public void launch() {
		launched=true;
		state=SpacecraftState.FLYING;
	}

	public boolean isDead() {
		return dead;
	}

	public SpaceObject getParent() {
		return parent;
	}

	public double getSpeed() {
		return 0;
	}

	public void setTarget(SpaceObject target) {
		this.target=target;
	}

	public SpacecraftState getState() {
		return state;
	}

	@Override
	public Spacecraft rebuildAt(String name, SpaceObject at) {
		rebuild=true;
		FakeSpacecraft copy = new FakeSpacecraft();
		copy.parent=at;
		return copy;
	}

	public double degreeTo(SpaceObject parent) {
		return 0;
	}

	@Override
	public List<CollidingObject> getDetectedItems() {
		return itemsToDetect;
	}

	@Override
	public void destruct() {
		dead=true;
	}

	public boolean collides(CollidingObject other) {
		return colliding;
	}

	public void updateHitbox() {}

	public void remove() {
		removed=true;
	}

	@Override
	public boolean isOrphan() {
		return dead;
	}

	@Override
	public void move(Point point) {
		moved=true;
		moveTo = point;
	}

	
}