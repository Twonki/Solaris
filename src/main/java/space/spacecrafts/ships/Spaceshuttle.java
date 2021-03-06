package space.spacecrafts.ships;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import drawing.JavaFXDrawingInformation;
import geom.HShape;
import interfaces.drawing.DrawingInformation;
import interfaces.geom.Point;
import interfaces.geom.Shape;
import interfaces.logical.CollidingObject;
import interfaces.logical.DestructibleObject;
import interfaces.logical.UpdatingObject;
import interfaces.spacecraft.MountedWeapon;
import interfaces.spacecraft.Spacecraft;
import interfaces.spacecraft.SpacecraftState;
import javafx.scene.paint.Color;
import logic.manager.ManagerRegistry;
import space.advanced.Asteroid;
import space.core.MovingSpaceObject;
import space.core.SpaceObject;
import space.effect.Explosion;
import space.spacecraft.ships.devices.DroneMount;
import space.spacecraft.ships.devices.DroneRack;
import space.spacecraft.ships.devices.LaserCannon;
import space.spacecraft.ships.devices.RocketLauncher;
import space.spacecraft.ships.devices.WeaponFactory;
import space.spacecrafts.ships.missiles.Missile;

public class Spaceshuttle extends MovingSpaceObject implements Spacecraft{
	
	protected static Logger logger = LogManager.getLogger(Spaceshuttle.class);
	
	protected SpaceObject target;
	protected SpaceObject parent;
	protected double orbitingDistance;
	protected boolean isBuildAsPlayer;
	protected int size;
	protected boolean leavesSpaceTrash = true;
	
	protected Sensor sensor;
	protected SpacecraftState state = SpacecraftState.ORBITING;

	protected MountedWeapon primaryWeapon,secondaryWeapon;
	protected List<MountedWeapon> weapons = new ArrayList<>();
	
	private void spawnDronesIfAnyAreGiven() {
		weapons.stream().filter(w -> w instanceof DroneMount).map(w -> (DroneMount)w).forEach(dm -> dm.launch_drone());
	}
	
	public void launch() {
		if(isAliveAndRegistered()) {
			changeHierarchy();
			changeMovementAndPositionAttributes();
		}
	}

	protected void changeMovementAndPositionAttributes() {
		distance = (int)distanceTo(parent);
		relativePos = parent.degreeTo(this);
		evaluateBestSpeed();
	}

	protected void evaluateBestSpeed() {
		if(isFasterThanMe(parent)) {
			// Be careful about the ! below
			if(!movesInSameDirection(parent)) {
				speed =- speed;
			}
		} else {
			if(movesInSameDirection(parent)) {
				speed =- speed;	
			}
		}
	}

	protected void changeHierarchy() {
		// This method is used to switch the "parents" and start moving the shuttle
		// After changing the parent to target, the shuttle will automatically move towards it while orbiting
		target.getTrabants().add(this);
		state = SpacecraftState.FLYING;
		parent = target;
		target = null;
	}

	protected boolean isAliveAndRegistered() {
		return target != null && parent != null && parent.getTrabants().remove(this);
	}
	
	
	@Override 
	public void move(Point parentCenter) {
		if(state != SpacecraftState.ORBITING) {
			if(distance >= orbitingDistance + distanceTo(parent)) {
				distance--;
			} else {
				state = SpacecraftState.ORBITING;	
			}
		}
		sensor.move(center);
		super.move(parentCenter);
	}
	
	public List<Spaceshuttle> getDrones(){
		return weapons.stream()
				.filter(w -> w instanceof DroneMount)
				.map(d -> (DroneMount)d)
				.map(d -> d.getDrone())
				.filter(d -> d != null)
				.collect(Collectors.toList());
	}
	
	public void destruct() {
		logger.info("Spaceship: " + toString() + " was destroyed.");
		if(!isDead()) {
			if(isActivePlayer()) {
				ManagerRegistry.getPlayerManager().increasePlayerDeaths();
				logger.info("PlayerDeath #"+ManagerRegistry.getPlayerManager().getPlayerDeaths());
			}
			state = SpacecraftState.DEAD;
			new Explosion("Explosion from" + this.getName(),center,5,1500,1.02,new JavaFXDrawingInformation(Color.MEDIUMVIOLETRED));
			if(leavesSpaceTrash) {		
				var ast = new Asteroid("Trash from " + name,parent,(int)(orbitingDistance+distanceTo(parent)),speed/2,Asteroid.Type.TRASH);
				ast.setRelativePos(this.relativePos+Math.PI/4); // little offset
			}
			remove();
		}
	}
	
	@Override
	public void rotate() {rotation = degreeTo(parent);}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;	
		}
		if(!(o instanceof Spaceshuttle)) {
			return false;
		}
		Spaceshuttle otherCasted = (Spaceshuttle) o;
		
		boolean sameParent = false;
		if(parent != null && otherCasted.parent != null) {
			sameParent = parent.equals(otherCasted.parent);
		} else if (parent == null && otherCasted.parent == null) {
			sameParent = true;
		}
		return otherCasted.getName().equals(this.getName())
				&& otherCasted.getCenter().equals(this.getCenter())
				&& size == otherCasted.size
				&& leavesSpaceTrash == otherCasted.leavesSpaceTrash
				&& sameParent
				// Other Attributes?
		;
	}
	
	public boolean isDead() {
		return state == SpacecraftState.DEAD;
	}
	
	public void remove() {
		parent.getTrabants().remove(this);
		parent = null;
		target = null;
		
		ManagerRegistry.getUpdateManager().scheduleRemoval(this);
	}

	public double getOrbitingDistance() {return orbitingDistance;}
	public SpaceObject getParent() {return parent;}
	public void setTarget(SpaceObject target) {this.target = target;}
	public SpaceObject getTarget() {return target;}
	public boolean isOrphan() {return parent == null;}
	public SpacecraftState getState() {return state;}

	public List<CollidingObject> getDetectedItems() {
		return sensor.getDetectedItems();
	}

	public void setSensor(Sensor val) { sensor = val;}
	
	private void setStandardWeapons() {
		primaryWeapon = new LaserCannon(this);
		secondaryWeapon = new RocketLauncher(this,60);
		
		weapons.add(primaryWeapon);
		weapons.add(secondaryWeapon);
	}
	
	@Override
	public void update() {
		super.update();
		weapons.stream().filter(o -> o instanceof UpdatingObject).map(o -> (UpdatingObject)o).forEach(o -> o.update());
		sensor.update();
	}
	
	public boolean isArmed() {	return ! weapons.isEmpty(); }

	@Override
	public boolean collides(CollidingObject other) {
		// First Check: If I am dead, I do not collide
		if(isDead()) {
			return false;
		}
		// Otherwise: Is there a normal physical collision of shapes - If no, return false, otherwise do extra behaviour
		if(super.collides(other)) {
			//Special exceptions for Carriers and Drones
			if(other instanceof Spaceshuttle) {
				var otherShuttle = (Spaceshuttle) other;
				//If the other one is dead, do not collide
				if(otherShuttle.isDead()) {
					return false;
				}
				
				//If I am carrier, do not collide with children
				if(this.isCarrier() && this.isMyDrone(otherShuttle)) {
					return false;
				}
				//If I am a drone, do not collide with my Carrier or the carriers other drones
				if(this.isDrone()) {
					if(this.isMyCarrier(otherShuttle) || this.isDroneWithSameCarrier(otherShuttle)) {
						return false;	
					}
				}
			}
			//Special exceptions for missiles
			if(other instanceof Missile) {
				//If this is my missile (I shot it), there is no collision
				if(trabants.contains((Missile)other)) {
					return false;
				}
				//If i am a carrier, I check if any of my drones is the parent of this missile. 
				if(isCarrier()) {
					boolean missileIsFromDrones = getDrones().stream()
					.map(t->t.getTrabants())
					.anyMatch(lTrabants -> lTrabants.contains(other));
					
					if(missileIsFromDrones) {
						return false;
					}
				}
				//If i am a drone, I don't want to collide with the missiles of my fellow drones 
				if(isDrone()) {
					Spaceshuttle myCarrier = ManagerRegistry.getUpdateManager().getRegisteredItems().stream().filter(t-> t instanceof Spaceshuttle)
						.map(t -> (Spaceshuttle)t)
						.filter(t -> t.isCarrier())
						.filter(t -> this.isMyCarrier(t))
						.findFirst()
						.get();
					boolean missileIsFromNeighbourDrones = myCarrier.getDrones().stream()
							.map(t->t.getTrabants())
							.anyMatch(lTrabants -> lTrabants.contains(other));
					
					if(missileIsFromNeighbourDrones) {
						return false;
					}
				}
			}
			
			// There are no further special exceptions to collisions - return true, the items collide
			return true;
		}
		//There was no initial (physical) collision of items - return false
		return false;
	}
	
	public boolean isDrone() {
		return ManagerRegistry.getUpdateManager().getRegisteredItems()
			.stream()
			.filter(t -> t instanceof Spaceshuttle)
			.map(t -> (Spaceshuttle) t)
			.flatMap( s -> s.getDrones().stream())
			.anyMatch( d -> d.equals(this));
	}
	
	public boolean isCarrier(){
		return weapons.stream().anyMatch(w-> w instanceof DroneMount);
	}
	
	public void primaryAttack(Point p) {
		if(primaryWeapon != null) {
			primaryWeapon.setTarget(p);
			primaryWeapon.activate();
		}
	}
	public void primaryAttack(SpaceObject o) {
		if(primaryWeapon != null) {
			primaryWeapon.setTarget(o);
			primaryWeapon.activate();
		}
	}
	
	public void secondaryAttack(Point p) {
		if(secondaryWeapon != null) {
			secondaryWeapon.setTarget(p);
			secondaryWeapon.activate();
		}
	}
	public void secondaryAttack(SpaceObject o) {
		if(secondaryWeapon != null) {
			secondaryWeapon.setTarget(o);
			secondaryWeapon.activate();
		}
	}
	
	@Override
	public Spaceshuttle rebuildAt(String name, SpaceObject at) {
		int minLevelOfDetail = Math.max(1,this.shape.getLevelOfDetail());
		
		var copyBuilder =  new Builder(name,at)
				.size(this.size)
				.drawingInformation(this.dInfo)
				.spawnSpaceTrashOnDestruct(this.leavesSpaceTrash)
				.rotationSpeed(this.rotationSpeed)
				.speed(this.speed)
				.levelOfDetail(minLevelOfDetail)
				.orbitingDistance((int)this.orbitingDistance)
				.sensorSize(this.sensor.getSize())
				.shape(this.shape.copy());
		if(isActivePlayer() || isBuildAsPlayer) {
			copyBuilder = copyBuilder.isPlayer();
		}
		
		Spaceshuttle copy =  copyBuilder.build();
		
		
		copy.weapons = this.weapons;
		copy.primaryWeapon = this.primaryWeapon;
		copy.secondaryWeapon = this.secondaryWeapon;
		
		for(var weapon : copy.weapons) {
			weapon.setParent(copy);
		}
		
		copy.setRelativePos(0);
		copy.shape.setCenter(copy.getCenter());
		copy.updateHitbox();
		
		return copy;
	}
	
	@Override
	public boolean isActivePlayer() {
		// This way round, otherwise if PlayerManager is empty/new it dies 
		return equals(ManagerRegistry.getPlayerManager().getPlayerShuttle());
	}

	public Optional<SpaceObject> getNearestPossibleTarget() {
		Optional<SpaceObject> possibleTarget = Optional.empty();
		if(!sensor.getDetectedItems().isEmpty())
			possibleTarget = sensor.getDetectedItems().stream()
				.filter(c->c instanceof DestructibleObject)
				.filter(c -> c instanceof SpaceObject)
				.map(c-> (SpaceObject)c)
				.filter(c -> {
					if(c instanceof Spaceshuttle) {
						var casted = (Spaceshuttle) c;
						return     !this.equals(casted)                 
								&& !isMyCarrier(casted) 
								&& !isMyDrone(casted) 
								&& !isDroneWithSameCarrier(casted);	
					}
					return true; 
				})
				.findFirst();
		return possibleTarget;
	}

	public boolean isMyCarrier(Spaceshuttle other) {
		if(other == null) {
			return false;
		}
		if(!other.isCarrier()) {
			return false;
		}
		if(this.isDrone()) {
			return other.getDrones().contains(this);
		}
		return false;
	}
	
	public boolean isDroneWithSameCarrier(Spaceshuttle other) {
		if(other == null || !other.isDrone() || !isDrone()) {
			return false;
		}
		
		Optional<Boolean> maybeParent = getMyCarrier()
				.map(u -> u.getDrones()
				.contains(other));
		
		if(maybeParent.isEmpty()) {
			return false;
		} else {
			return maybeParent.get();
		}
	}
	
	public boolean isMyDrone(Spaceshuttle other) {
		if(other == null || !this.isCarrier() || !other.isDrone()) {
			return false;
		}
		
		var maybeParent = other.getMyCarrier();
		
		if(maybeParent.isEmpty()) {
			return false;
		} else {
			return maybeParent.get().equals(this);
		}
	}
	
	private Optional<Spaceshuttle> getMyCarrier(){
		if(!isDrone()) {
			return Optional.empty();
		} else {
			return ManagerRegistry.getUpdateManager().getRegisteredItems().stream().filter(t-> t instanceof Spaceshuttle)
					.map(t -> (Spaceshuttle)t)
					.filter(t -> t.isCarrier())
					.filter(t -> isMyCarrier(t))
					.findFirst();
		}
	}
	
	public Collection<MountedWeapon> getWeapons() {
		return weapons;
	}
	
	public static class Builder {
		private final String name;
		private SpaceObject parent;
		private Color color = Color.CORNSILK;
		private DrawingInformation dinfo = null;
		private int orbitingDistance = 100,size = 0, levelOfDetail = 2;
		private Shape shape;
		private double speed = 0,rotationSpeed = 0,sensorsize = 50;
		private boolean setStandardWeaponry = false,shallBeCarrier = false,leavesSpaceTrash = true,isPlayer = false;
		
		private List<Function<Spaceshuttle,MountedWeapon>> weaponFns = new ArrayList<>();
		private List<Function<Spaceshuttle,Spaceshuttle>> droneFns = new ArrayList<>();
		
		
		public Builder(String name,SpaceObject parent) throws IllegalArgumentException{
			if(name == null || name.isEmpty()) {
				throw new IllegalArgumentException("Name cannot be null or empty");	
			}
			if(parent == null) {
				throw new IllegalArgumentException("Parent cannot be null");	
			}
			this.name = name;
			this.parent = parent;
		}
		
		public Builder color(Color val){ 
			color = val; 
			return this;
		}
		
		public Builder orbitingDistance(int val){
			if(val <= 0) {
				throw new IllegalArgumentException("Distance cannot be smaller than or equal to 0");	
			}
			orbitingDistance = val; 
			return this;
		}
		
		public Builder size(int val){
			if(val < 0) {
				throw new IllegalArgumentException("Size cannot be smaller than 0");
			}
			size = val; 
			return this;
		}
		
		public Builder speed(double radiantPerUpdate){
			speed = radiantPerUpdate; 
			return this;
		}
		
		public Builder isPlayer() {
			isPlayer = true;
			return this;
		}
		
		public Builder rotationSpeed(double radiantPerUpdate){
			rotationSpeed = radiantPerUpdate; 
			return this;
		}
		public Builder levelOfDetail(int val){ 
			if(val < 1) {
				throw new IllegalArgumentException("LoD cannot be smaller or equal 0");	
			}
			levelOfDetail = val; 
			return this;
		}
		
		public Builder spawnSpaceTrashOnDestruct(boolean val) {
			leavesSpaceTrash = val;
			return this;
		}
		
		public Builder addMountedWeapon(Function<Spaceshuttle,MountedWeapon> weaponFn){ 
			weaponFns.add(weaponFn); 
			return this;
		}
		
		public Builder addDroneMount(Function<Spaceshuttle,Spaceshuttle> droneFn) {
			droneFns.add(droneFn);
			return this;
		}
		
		public Builder setStandardWeaponry(boolean val) {
			setStandardWeaponry = val;
			return this;
		}
		
		public Builder shallBeCarrier(boolean val) {
			shallBeCarrier = val;
			return this;
		}
		
		public Builder drawingInformation(DrawingInformation val) {
			if(val == null) {
				throw new IllegalArgumentException("DrawingInformation cannot be null");
			}
			dinfo = val;
			return this;
		}
		
		public Builder sensorSize(double val) {
			if(val < 0) {
				throw new IllegalArgumentException("Sensorsize cannot be smaller than 0");
			}
			sensorsize = val;
			return this;
		}

		public Builder shape(Shape shape) {
			if(shape == null) {
				throw new IllegalArgumentException("Shape cannot be null");	
			}
			this.shape = shape;
			return this;
		}
		
		public Spaceshuttle build() {
			return new Spaceshuttle(this);
		}

	}
	
	private Spaceshuttle(Builder builder) {
		super(builder.name, builder.parent, new JavaFXDrawingInformation(builder.color), new HShape(builder.size*2,builder.size*3,builder.size), builder.orbitingDistance , builder.speed);
		this.parent = builder.parent;
		rotationSpeed = builder.rotationSpeed;
		orbitingDistance = builder.orbitingDistance;
		distance = builder.orbitingDistance;
		leavesSpaceTrash = builder.leavesSpaceTrash;
		
		if(builder.dinfo != null) {
			dInfo = builder.dinfo;		
		}
		if(builder.shape != null) {
			this.shape=builder.shape;
		}
		if(builder.isPlayer) {
			ManagerRegistry.getPlayerManager().registerPlayerShuttle(this);
			this.isBuildAsPlayer = builder.isPlayer;
		}
		
		sensor = new SensorArray(this,(int)builder.sensorsize);
		move(parent.getCenter());
		
		if(builder.setStandardWeaponry) {
			setStandardWeapons();	
		}
		
		this.size = builder.size;
		
		shape.setLevelOfDetail(builder.levelOfDetail);
		shape.updateOrInitOutline();
		if(getDrawingInformation() instanceof JavaFXDrawingInformation) {
			((JavaFXDrawingInformation)getDrawingInformation()).hasColorEffect = true;
		}
		builder.droneFns
			.stream()
			.map(fun -> {
				Supplier<Spaceshuttle> supFn = () -> fun.apply(this);
				return supFn;
			})
			.map(supFn -> new DroneMount(supFn))
			.forEach(mount -> this.weapons.add(mount));
		
		builder.weaponFns
			.stream()
			.map(fun -> fun.apply(this))
			.forEach(weapon -> this.weapons.add(weapon));
		
		if(builder.shallBeCarrier) {
			MountedWeapon rack = WeaponFactory.combineDroneMountsToDroneRack(this);
			primaryWeapon = rack;
			weapons.add(rack);
			Optional<MountedWeapon> maybeSecond = weapons.stream().filter(u -> !(u instanceof DroneMount || u instanceof DroneRack)).findFirst();
			secondaryWeapon = maybeSecond.orElse(null);
		} else {
			if(weapons.size()>0) {
				primaryWeapon = weapons.get(0);
			}
			if(weapons.size()>1) {
				secondaryWeapon = weapons.get(1);
			}
		}
		
		spawnDronesIfAnyAreGiven();

		ManagerRegistry.getUpdateManager().scheduleRegistration(this);
	}

}
