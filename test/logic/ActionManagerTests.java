package logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fakes.FakeAction;
import logic.interaction.Action;
import logic.interaction.ActionManager;

class ActionManagerTests {

	@Test
	void testActionManager_withDefaultOut_shouldHaveEmptyActions() {
		ActionManager testObject = new ActionManager(false);
		
		assertEquals(0,testObject.countRegisteredActions());
	}

	@Test
	void testActionManager_withDefaultOn_shouldHaveActions() {
		ActionManager testObject = new ActionManager(true);
		
		assertNotEquals(0,testObject.countRegisteredActions());
	}
	
	@Test
	void testRegisterAction_registerFakeAction_shouldHaveItem() {
		ActionManager testObject = new ActionManager(false);
		
		FakeAction stub = new FakeAction();
		stub.name="stub";
		
		testObject.registerAction(stub);

		assertEquals(1,testObject.countRegisteredActions());
	}
	
	@Test
	void testRegisterAction_registerFakeAction_registerTwice_shouldHaveOneItem() {
		ActionManager testObject = new ActionManager(false);
		
		FakeAction stub = new FakeAction();
		stub.name="stub";
		
		testObject.registerAction(stub);
		testObject.registerAction(stub);

		assertEquals(1,testObject.countRegisteredActions());
	}
	
	@Test
	void testRegisterAction_AddActionWithSameName_shouldHaveOneItem() {
		ActionManager testObject = new ActionManager(false);
		
		FakeAction stub = new FakeAction();
		stub.name="stub";
		FakeAction otherstub = new FakeAction();
		otherstub.name="stub";
		
		testObject.registerAction(stub);
		testObject.registerAction(otherstub);

		assertEquals(1,testObject.countRegisteredActions());
	}
	
	@Test
	void testRegisterAction_AddActionWithSameName_shouldBeNewAction() {
		ActionManager testObject = new ActionManager(false);
		
		FakeAction stub = new FakeAction();
		stub.name="stub";
		stub.description="old";
		FakeAction otherstub = new FakeAction();
		otherstub.name="stub";
		otherstub.description="new";
		
		testObject.registerAction(stub);
		testObject.registerAction(otherstub);

		Action result =testObject.getActionByName("stub");
		
		assertEquals("new",result.getDescription());
	}
	
	@Test
	void testGetActionByName_ActionIsRegistered_shouldBeAction() {
		ActionManager testObject = new ActionManager(false);
		
		FakeAction stub = new FakeAction();
		stub.name="stub";
		
		testObject.registerAction(stub);

		Action result =testObject.getActionByName("stub");
		
		assertEquals(stub,result);
	}
	
	@Test
	void testGetActionByName_ActionIsNotRegistered_shouldBeNull() {
		ActionManager testObject = new ActionManager(false);
		
		Action result =testObject.getActionByName("not here");
		
		assertEquals(null,result);
	}
	
	@Test
	void testReset_shouldBeEmpty() {
		ActionManager testObject = new ActionManager(true);
		
		testObject.reset();
		
		assertEquals(0,testObject.countRegisteredActions());
	}

}
