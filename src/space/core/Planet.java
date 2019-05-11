/**
 * @Author Leonhard Applis
 * @Created 31.08.2018
 * @Package space.core
 */
package space.core;

import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import drawing.JavaFXDrawingInformation;
import geom.Circle;
import interfaces.drawing.DrawingInformation;
import interfaces.logical.RecursiveObject;

@SuppressWarnings("restriction")
public class Planet extends MovingSpaceObject {

	protected Planet(String name, SpaceObject parent,DrawingInformation dInfo, int size, int distance, double speed) {
		super(name, parent, dInfo, new Circle(size), distance, speed);
	}
	
	public static class Builder {
		private final String name;
		private SpaceObject parent;
		private Color color= Color.BLACK;
		private int distance = 0,size = 0, levelOfDetail=2;
		private double speed = 0,rotationSpeed=0;
		private List<MovingSpaceObject> trabants = new LinkedList<MovingSpaceObject>();
		
		public Builder(String name,SpaceObject parent) throws IllegalArgumentException{
			if(name==null||name.isEmpty())
				throw new IllegalArgumentException("Name cannot be null or empty");
			if(parent==null)
				throw new IllegalArgumentException("Parent cannot be null");
			this.name=name;
			this.parent=parent;
		}
		
		public Builder color(Color val){ 
			color= val; 
			return this;
		}
		
		public Builder distance(int val){ 
			distance= val; 
			return this;
		}
		
		public Builder size(int val){
			size= val; 
			return this;
		}
		
		public Builder speed(double val){
			speed= val; 
			return this;
		}
		public Builder rotationSpeed(double val){
			rotationSpeed= val; 
			return this;
		}
		public Builder levelOfDetail(int val){ 
			levelOfDetail= val; 
			return this;
		}
		
		public Builder trabant(MovingSpaceObject val){ 
			trabants.add(val); 
			return this;
		}
		
		public Planet build() {
			return new Planet(this);
		}
	}
	
	private Planet(Builder builder) {
		super(builder.name,builder.parent,new JavaFXDrawingInformation(builder.color),new Circle(builder.size),builder.distance,builder.speed);
		trabants=builder.trabants;
		shape.setLevelOfDetail(builder.levelOfDetail);
		rotationSpeed=builder.rotationSpeed;
		if(dInfo instanceof JavaFXDrawingInformation)
			((JavaFXDrawingInformation)dInfo).hasColorEffect=true;
	}

	
	
}
