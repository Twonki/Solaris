package geom;

import interfaces.geom.Point;

public class AbsolutePoint implements Point,Cloneable {
	protected int x,y,z;
	
	public AbsolutePoint(int x, int y) {
		this.x=x;
		this.y=y;
		z=0;
	}
	
	public AbsolutePoint(int x,int y,int z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void move(int xDif, int yDif) {
		x+=xDif;
		y+=yDif;
	}
	public void move(int xDif, int yDif,int zDif) {
		move(xDif,yDif);
		z+=zDif;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public int getZ() {return z;}
	
	public void setX(int val) {x=val;}
	public void setY(int val) {y=val;}
	public void setZ(int val) {z=val;}
	
	@Override
	public String toString() {
		return ("[" + x + "|" + y +"|"+z+"]");
	}
	
	@Override
	public int hashCode() {
		int result= Integer.hashCode(getX());
		result = 31 * result + Integer.hashCode(getY());
		result = 31 * result + Integer.hashCode(getZ());
		return result;
	}
	
	@Override
	public AbsolutePoint clone() {
		return new AbsolutePoint(x,y,z);
	}

}
