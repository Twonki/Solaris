package coreTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import geom.Point;
import space.advanced.FixStar;
import space.core.Planet;
import space.core.Star;

class CollisionTest {
	
	
	static Star starOne, starTwo;
	@BeforeAll
	static void initAnkers() {
		starOne= new Star("anker1", null, new Point(0, 0),25);
		starTwo= new Star("anker2", null,new Point(100, 0),25);
		starOne.area.initOutline();
		starTwo.area.initOutline();
	}
	
	
	@Test
	void negativeCollision() {
		assertFalse(starOne.collides(starTwo));
		Planet planet = (new Planet.Builder("NoCollider", starOne))
				.size(15)
				.distance(1000)
				.levelOfDetail(20)
				.speed(0)
				.build();
		
		planet.updateHitbox();
		assertFalse(starOne.collides(planet));
		assertFalse(starTwo.collides(planet));
		//SymetryCheck
		assertTrue(starOne.collides(planet)==planet.collides(starOne));
		assertTrue(starOne.collides(starTwo)==starTwo.collides(starOne));
	}
	
	@Test
	void positiveCollision() {
		Planet planet = (new Planet.Builder("NoCollider", starOne))
				.size(150)
				.distance(10)
				.levelOfDetail(20)
				.speed(0)
				.build();
		planet.updateHitbox();
		assertTrue(starOne.collides(planet));
		assertTrue(starTwo.collides(planet));
	}
	
	@Test
	void pointBlankCollision() {
		Planet planet = (new Planet.Builder("Collider", starOne))
				.size(25)
				.distance(0)
				.levelOfDetail(20)
				.build();
		planet.updateHitbox();
		assertTrue(starOne.collides(planet));
		assertFalse(starTwo.collides(planet));
	}
	
	@Test
	void noSelfCollision() {
		assertFalse(starOne.collides(starOne));
		assertFalse(starTwo.collides(starTwo));
	}
	
	@Test
	void exactTangenting() {
		Planet planet = (new Planet.Builder("NoCollider", starOne))
				.size(50)
				.distance(50)
				.levelOfDetail(20)
				.build();
		planet.updateHitbox();
		
		assertTrue(starOne.collides(planet));
	}
	
	@Test
	void multipleCollisions() {
		Star collidingStar = new Star("Collider",null,new Point(50,0),150);
		collidingStar.updateHitbox();
		assertTrue(starOne.collides(collidingStar));
		assertTrue(collidingStar.collides(starOne)&&collidingStar.collides(starTwo));
		assertTrue(starTwo.collides(collidingStar));
	}
	
	//Check Fixstars and Distant Galaxy
	@Test
	void uncollidables() {
		FixStar uncollidableFixStar = new FixStar("a", 1, 1, 1);
		uncollidableFixStar.area.initOutline();
		assertFalse(uncollidableFixStar.collides(starOne));
		assertTrue(starOne.collides(uncollidableFixStar));
	}
}
