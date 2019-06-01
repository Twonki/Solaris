package space.spacecrafts.ships.missiles;

import java.util.LinkedList;
import java.util.List;

import drawing.JavaFXDrawingContext;
import drawing.JavaFXDrawingInformation;
import geom.Circle;
import interfaces.drawing.DrawingContext;
import interfaces.geom.Point;
import geom.AbsolutePoint;
import javafx.scene.paint.Color;
import space.spacecrafts.ships.Ship;

@SuppressWarnings("restriction")
public class Laserbeam extends Missile{

	private List<AbsolutePoint> trail=new LinkedList<AbsolutePoint>();
	
	public Laserbeam(String name, Ship emitter) {
		super(name, emitter,new Circle(emitter.getCenter().absoluteClone(),2), new JavaFXDrawingInformation(Color.LIGHTGREEN), emitter.getRotation(), 3);
		
	}
	
	public Laserbeam(String name, Ship emitter,  double direction, int speed) {
		super(name, emitter, new Circle(emitter.getCenter().absoluteClone(),2),new JavaFXDrawingInformation(Color.LIGHTGREEN),direction, speed);
	}

	@Override
	public void move(Point oldPosition) {
		AbsolutePoint trailPoint = getCenter().absoluteClone();
	    trail.add(trailPoint);
		super.move(oldPosition);
	}
	
	@Override
	public void drawShape(DrawingContext dc) {
		if(dc instanceof JavaFXDrawingContext) 
			for(int i=0; i<trail.size(); i++) 
				((JavaFXDrawingContext)dc).getGraphicsContext().fillOval(trail.get(i).getX(),trail.get(i).getY(), 2, 2);
	}
	
	public List<AbsolutePoint> getTrail() {return trail;}
}