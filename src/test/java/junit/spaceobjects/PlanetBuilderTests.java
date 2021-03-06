package junit.spaceobjects;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


import space.core.Planet;
import space.core.Star;

import static junit.testhelpers.FakeSpaceObjectFactory.*;


public class PlanetBuilderTests implements CommonSpaceObjectBuilderTests {
	
	@Tag("fast")
	@Tag("core")
	@Tag("builder")
	@Test
	public void testBuilder_EverythingIsFine_ShouldBeBuild() {
		Star anchor = fakeStar(0,0);
		
		Planet testObject = (new Planet.Builder("Test",anchor)
				.distance(50)
				.size(50)
				.speed(Math.PI)
				.rotationSpeed(Math.PI)
				.levelOfDetail(10).build());
			
		assertEquals(50,testObject.getDistance());
		assertEquals(10,testObject.getShape().getLevelOfDetail());
		
	}

	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NegativeSize_shouldThrowError() {
		Star anchor = fakeStar(0,0);
		
		assertThrows(IllegalArgumentException.class,
				() ->  (new Planet.Builder("Test",anchor))
				.size(-50)
			);
		
	}

	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NegativeLevelOfDetail_shouldThrowError() {
		Star anchor = fakeStar(0,0);
		
		assertThrows(IllegalArgumentException.class,
				() ->  (new Planet.Builder("Test",anchor))
				.levelOfDetail(-5)
			);
		
	}

	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NegativeDistance_shouldThrowError() {
		Star anchor = fakeStar(0,0);
		
		assertThrows(IllegalArgumentException.class,
				() ->  (new Planet.Builder("Test",anchor))
				.distance(-5)
			);
		
	}

	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NoParent_shouldThrowError() {
		assertThrows(
				IllegalArgumentException.class,
				(() ->  new Planet.Builder("Error", null))
			);
	}
	
	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NoName_shouldThrowError() {
		Star anchor = fakeStar(0,0);
		
		assertThrows(IllegalArgumentException.class,
				() ->  new Planet.Builder("",anchor)
			);		
	}
	
	@Tag("fast")
	@Tag("builder")
	@Test
	public void testBuilder_NullName_shouldThrowError() {
		Star anchor = fakeStar(0,0);
		
		assertThrows(IllegalArgumentException.class,
				() ->  new Planet.Builder(null,anchor)
			);		
	}
	
	@Tag("fast")
	@Tag("core")
	@Tag("builder")
	@Test
	public void testBuilder_addTrabant_shouldBeInTrabants() {
		Star anchor = fakeStar(0,0);
		Planet toCheck = fakePlanet(anchor,50,50);
		
		Planet testObject = (new Planet.Builder("Test",anchor))
				.trabant(toCheck)
				.build();
		
		assertTrue(testObject.getAllChildren().contains(toCheck));
	}
	
}	
