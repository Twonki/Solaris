package logic.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import interfaces.spacecraft.AggressiveNavigator;
import interfaces.spacecraft.Spacecraft;

public class PlayerManager{

	private Spacecraft playerShuttle;
	private AggressiveNavigator playerNavigator;

	private static Logger logger = LogManager.getLogger(EffectManager.class);
	
	private int deathCount=0;
	
	public PlayerManager() {}
	
	
	public void registerPlayerNavigator(AggressiveNavigator pN){
		if(playerNavigator!=null)
			logger.info("Overwriting active playerNavigator...");
		playerNavigator=pN;
	}
	
	public void reset() {
		playerShuttle=null;
		playerNavigator=null;
		deathCount=0;
	}
	
	public void registerPlayerShuttle(Spacecraft p) {
		if(playerShuttle!=null)
			logger.info("Overwriting active playershuttle...");
		playerShuttle=p;
	}
	
	public Spacecraft getPlayerShuttle() {return playerShuttle;}
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
