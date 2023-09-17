package game;

	import javafx.application.Application;
	import javafx.application.Platform;
	import javafx.event.EventHandler;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import javafx.stage.Stage;
	import javafx.stage.WindowEvent;
	
	
	
public class TetrisApp extends Application {
		public void start(Stage primaryStage) throws Exception {
			
			//loads the fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
			Parent parent = (Parent) loader.load();
			
			//sets the stage: the title, scene and shows it
			primaryStage.setTitle("Tetris");
			primaryStage.setScene(new Scene(parent));
			primaryStage.show();  
			
			//stops the application in when it is exited
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent e) {
			     Platform.exit();
			     System.exit(0);
			    }
			  });
		}
		
		
		
		//main method that runs the application. Tries to run it, and throws and catches exception thrown
		public static void main(String[] args) {
			try {
			launch(TetrisApp.class, args);
			} catch (Exception e) {
		
				e.printStackTrace();
			} 
			System.err.println("Something went wrong.");
			
		}
		

	


}
    