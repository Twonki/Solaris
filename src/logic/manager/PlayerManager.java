package logic.manager;

import org.pmw.tinylog.Logger;

import interfaces.spacecraft.AggressiveNavigator;
import interfaces.spacecraft.ArmedSpacecraft;

public class PlayerManager{

	private ArmedSpacecraft playerShuttle;
	private AggressiveNavigator playerNavigator;
	
	private int deathCount=0;
	
	public PlayerManager() {}
	
	
	public void registerPlayerNavigator(AggressiveNavigator pN){
		if(playerNavigator!=null)
			Logger.info("Overwriting active playerNavigator...");
		playerNavigator=pN;
	}
	
	public void reset() {
		playerShuttle=null;
		playerNavigator=null;
		deathCount=0;
	}
	
	public void registerPlayerShuttle(ArmedSpacecraft p) {
		if(playerShuttle!=null)
			Logger.info("Overwriting active playershuttle...");
		playerShuttle=p;
	}
	
	public ArmedSpacecraft getPlayerShuttle() {return playerShuttle;}
	public AggressiveNavigator getPlayerNavigator() {return playerNavigator;}

	public void forceRespawn() {
		if(playerShuttle!=null)
			playerShuttle.destruct();
	}
	
	public void speedUp() {
		if(playerShuttle!=null)
			playerShuttle.setSpeed(playerShuttle.getSpeed()*1.1);
	}
	
	public void slowDown() {
		if(playerShuttle!=null)
			playerShuttle.setSpeed(playerShuttle.getSpeed()*0.9);
	}
	
	public int getPlayerDeaths() {
		return deathCount;
	}
	
	public void increasePlayerDeaths() {
		deathCount++;
	}
}
