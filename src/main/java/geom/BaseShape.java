package geom;

import java.util.LinkedList;
import java.util.List;

import drawing.JavaFXDrawingContext;
import interfaces.drawing.DrawingContext;
import interfaces.geom.Point;
import interfaces.geom.Shape;
import javafx.scene.canvas.GraphicsContext;

public abstract class BaseShape implements Shape{
	public Point center;
	public List<Point> outLine= new LinkedList<Point>();
	protected int levelOfDetail=10;
	
	protected BaseShape(Point center) {
		this.center=center;
	}
	
	protected BaseShape() {
		center= new AbsolutePoint(0,0);
	}
	
	public boolean intersects(Shape other) {
		if(other != this) {
			return outLine.stream().anyMatch(p->other.contains(p)) || covers(other);
		}
		return false;
	}
	
	public boolean covers(Shape other) {
		if(other instanceof BaseShape)
			return ((BaseShape)other).outLine.stream().allMatch(p->contains(p));
		return false;
	}
	
	public void updateOrInitOutline() {
		if(outLine.size()<levelOfDetail)
			initOutline();
	};
	
	public abstract void initOutline();
	
	public void setCenter(Point p) {
		if(center instanceof RelativePoint)
			((RelativePoint)center).anker=p;
		else
			center=p;
	}
	public Point getCenter() { return center;}
	
	public int getLevelOfDetail() {
		return levelOfDetail;
	}
	
	public void setLevelOfDetail(int lod) {
		levelOfDetail=lod;
	}
	
	public boolean sameShape(Shape other) {
		if(!(other instanceof BaseShape))
			return false;
		return 
			center.samePosition(other.getCenter())
			&& area()==other.area();
		//TODO: Further Checks with Outline?
	}
	
	public void draw(DrawingContext dc) {
		if(dc instanceof JavaFXDrawingContext)
			draw(((JavaFXDrawingContext)dc).getGraphicsContext());
		else
			throw new UnsupportedOperationException("Unsupported DrawingContext!");
	}
	
	public abstract void draw(GraphicsContext gc);
}
