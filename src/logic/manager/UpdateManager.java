package logic.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.pmw.tinylog.Logger;

import config.Config;
import interfaces.logical.TimerObject;
import interfaces.logical.UpdatingObject;
import javafx.application.Platform;

import space.core.SpaceObject;

@SuppressWarnings("restriction")
public class UpdateManager implements TimerObject{

	public List<UpdatingObject> registeredItems;
	
	private Timer timer;
	private boolean running=true;
	
	private static UpdateManager INSTANCE;
	
	private UpdateManager() {
		registeredItems=new LinkedList<UpdatingObject>();
		Logger.debug("Build UpdateManager");
	}
	
	public static UpdateManager getInstance() {
		if(INSTANCE==null)
			INSTANCE= new UpdateManager();
		return INSTANCE;
	}
	
	public void initUpdateManager(Config config) {
		CollisionManager.getInstance().initCollisionManager(this);
		
		registeredItems.add(CollisionManager.getInstance());
		registeredItems.add(DrawingManager.getInstance());
		registeredItems.add(EffectManager.getInstance());
		
		setTimer(config.settings.updateIntervall);
		running = !config.settings.paused;
	}
	
	public void registerObject(UpdatingObject o) {
		if(!registeredItems.contains(o))
			registeredItems.add(o);
	}
	
	public void update() {
		if(running) 
			for(UpdatingObject updateMe : registeredItems)
				updateMe.update();
	}
	
	public void addSpaceObject(SpaceObject o) {
		registerObject(o);
		DrawingManager.getInstance().registeredItems.add(o);
		for(SpaceObject child : o.getAllChildrenFlat())
			CollisionManager.getInstance().register(child);
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

	public void togglePause() {
		running=!running;
		Logger.debug("Updatemanager set to running:" + running);
	}
	
	public void reset() {
		registeredItems=new LinkedList<UpdatingObject>();
		running=true;

		registeredItems.add(CollisionManager.getInstance());
		registeredItems.add(DrawingManager.getInstance());
		registeredItems.add(EffectManager.getInstance());
	}
	
	public boolean getState() {
		return running;
	}
}
