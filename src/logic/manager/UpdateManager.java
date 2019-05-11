package logic.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import config.Config;
import interfaces.logical.CollidingObject;
import interfaces.logical.ManagerObject;
import interfaces.logical.RecursiveObject;
import interfaces.logical.TimerObject;
import interfaces.logical.UpdatingObject;
import javafx.application.Platform;

import space.core.SpaceObject;

@SuppressWarnings("restriction")
public class UpdateManager implements TimerObject,ManagerObject<UpdatingObject>{

	private List<UpdatingObject> registeredItems;
	private Timer timer;
	private boolean running=true;
	
	public UpdateManager() {
		registeredItems=new LinkedList<UpdatingObject>();
		Logger.debug("Build UpdateManager");
	}
	
	
	public void init(Config config) {
		setTimer(config.settings.updateIntervall);
		running = !config.settings.paused;
	}
	
	public void registerItem(UpdatingObject o) {
		if(!registeredItems.contains(o))
			registeredItems.add(o);
	}
	
	public void update() {
		if(running) 
			for(UpdatingObject updateMe : registeredItems)
				updateMe.update();
	}
	
	public void addSpaceObject(SpaceObject o) {
		registerItem(o);
		DrawingManager.getInstance().registeredItems.add(o);
		for(RecursiveObject child : o.getAllChildren())
			if(child instanceof CollidingObject)
				ManagerRegistry.getCollisionManager().registerItem((CollidingObject)child);
	}
	
	public Set<CollidingObject> getAllActiveColliders() {	
		return getRegisteredItems().stream()
		.filter(updatingObject -> updatingObject instanceof RecursiveObject)
		.map(spaceObject -> (RecursiveObject)spaceObject)
		.flatMap(recursive -> recursive.getAllChildren().stream())
		.filter(o -> o instanceof CollidingObject)
		.map(o->(CollidingObject)o)
		.collect(Collectors.toSet());
	}
	
	@Override
	public void setTimer(int updateIntervall) {
		timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            	Platform.runLater ( () ->update()); 
            }
        }, 0, updateIntervall);
	}

	public void toggleUpdate() {
		running=!running;
		Logger.debug("Updatemanager set to running:" + running);
	}
	
	public void reset() {
		registeredItems=new LinkedList<UpdatingObject>();
		running=true;
		
	}
	
	public boolean isRunning() {
		return running;
	}

	public Collection<UpdatingObject> getRegisteredItems() {
		return registeredItems;
	}
}
