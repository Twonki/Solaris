package logic.manager;

import interfaces.logical.UpdatingObject;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

@SuppressWarnings("restriction")
public class KeyBoardManager implements UpdatingObject {
	
	private char currentPressed;

	private static KeyBoardManager INSTANCE;
	
	private KeyBoardManager() {};
	
	public static KeyBoardManager getInstance() {
		if(INSTANCE==null)
			INSTANCE=new KeyBoardManager();
		return INSTANCE;
	}

	@Override
	public void update() {
		// Maybe handle stuff that needs longer pressed buttons (like charging a laserbeam)
	}
	
	public void init(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_TYPED, evt -> keyTyped(evt));
        scene.addEventHandler(KeyEvent.KEY_RELEASED, evt -> keyReleased(evt));
	}

	private void keyReleased(KeyEvent evt) {
		currentPressed = Character.UNASSIGNED;
	}

	private void keyTyped(KeyEvent evt) {
		if(currentPressed==Character.UNASSIGNED) {
			currentPressed = evt.getCharacter().toCharArray()[0];
			switch (currentPressed) {
			case 'p': UpdateManager.getInstance().togglePause(); break;
			case 'c': CollisionManager.getInstance().togglePause();break;
			case 'd': PlayerManager.getInstance().getPlayerNavigator().clearRoute();break;
			case 'l': PlayerManager.getInstance().forceRespawn();break;
			case 'q': System.exit(0);break;
			case 'k': PlayerManager.getInstance().speedUp();break;
			case 'j':PlayerManager.getInstance().slowDown();break;
			//ToDo: Case Escape to close Game
			default: System.out.println("You pressed: " + currentPressed + " ... nothing happened");
			}
		}
		else if (currentPressed==evt.getCharacter().toCharArray()[0]) {
			//Someone is keeping the same Character pressed
		}
		else
			System.out.println("You are pressing "  + currentPressed + "- release first for new Input!");
		
	}
	
}
