package logic;

import views.StartView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import config.JSON.JSONConfig;
import config.JSON.JSONConfigFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import logic.manager.ManagerRegistry;



public class Program extends Application{

	private static Logger logger = LogManager.getLogger(Program.class);

	
	JSONConfig config;
	
	private static Program INSTANCE;


    public static void main(String[] args) {
        launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		INSTANCE = this;
		
		config = JSONConfigFactory.read("config.json");
		
		primaryStage.setTitle("Solaris");
		primaryStage.centerOnScreen();
		
		ManagerRegistry.getInstance().init(config);
		logger.info("Configuration read - starting welcome view");
		
		var start = new StartView(config,primaryStage);
	}
	

	
	// To Call the correct shutdown which needs to be static
	public static void invokeStop() {
		if(INSTANCE!=null)
			INSTANCE.stop();
	}
	
	@Override
	public void stop() {
		logger.info("Saving Config");
		JSONConfigFactory.save(config);
		logger.info("Closing Solaris");
		System.exit(0);
	}
	
}
