package shuttleTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import geom.Point;
import space.core.Star;
import space.shuttle.SpaceShuttle;

class LaunchTest {
	
	static Star origin,target;
	static SpaceShuttle shuttle;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		origin = new Star("star",null,new Point(250,250),250);
		target = new Star("star",null,new Point(1250,250),250);
	}

	@BeforeEach
	void setUp() throws Exception {
		shuttle= new SpaceShuttle("shuttle",origin,0,0,0);
		shuttle.target=target;
	}

	@Test
	void ConstructorTest() {
		assertEquals(shuttle.degreeTo(origin),shuttle.relativePos);
		assertTrue(shuttle.orbiting);
		//There is no more Size at the Moment
		//assertEquals(shuttle.distance,origin.size/2);
	}

	@Test
	void correctLaunchTest() {
		shuttle.launch();
		
		assertEquals(shuttle.parent,target);
		assertEquals(null, shuttle.target);
		
		assertEquals(shuttle.degreeTo(target),shuttle.relativePos);
		assertFalse(shuttle.orbiting);
		assertEquals(shuttle.distance,shuttle.distanceTo(target));
	}
	
	@Test
	void faultyLaunchTest() {
		shuttle.launch(); //Now its like in TestCase Launch()
		
		shuttle.launch(); // does nothing!assertEquals(shuttle.parent,target);
		assertEquals(null, shuttle.target);
		
		assertEquals(shuttle.degreeTo(target),shuttle.relativePos);
		assertFalse(shuttle.orbiting);
		assertEquals(shuttle.distance,shuttle.distanceTo(target));
	}
	
}
