package application.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import application.model.HistoryOfMoves;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuController{

	@FXML
	private Button onePlayer;
	@FXML
	private Button twoPlayers;
	@FXML
	private Button ustawienia;
	@FXML
	private Button exit;
	@FXML
	private Button loadHistory;
	
	public void onePlayerClick(ActionEvent event) throws IOException{
		System.out.println("one player");
		
		Parent poziomTrudnosci = FXMLLoader.load(getClass().getResource("../view/PoziomTrudnosci.fxml"));
		Scene poziomTrudnosciScene = new Scene(poziomTrudnosci);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(poziomTrudnosciScene);
		appStage.show();
	}
	
	public void twoPlayersClick(ActionEvent event) throws IOException{
		System.out.println("two players");
		//-> game
		
		Parent plansza = FXMLLoader.load(getClass().getResource("../view/Plansza.fxml"));
		Scene planszaScene = new Scene(plansza);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(planszaScene);
		appStage.setX(400);
		appStage.setY(150);
		appStage.setWidth(1210);
		appStage.setHeight(830);
		appStage.show();
	}
	
	public void ustawieniaClick(ActionEvent event) throws Exception{
		System.out.println("ustawienia");

		Parent ustawienia = FXMLLoader.load(getClass().getResource("../view/Ustawienia.fxml"));
		Scene ustawieniaScene = new Scene(ustawienia);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(ustawieniaScene);
		appStage.show();
	}
	
	public void exitClick(){
		Stage stage = (Stage) exit.getScene().getWindow();
		stage.close();
	}
	
	public void loadHistoryClick(ActionEvent event) throws IOException, ClassNotFoundException{
		
		FileChooser fileChooser = new FileChooser();
		  
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SER files (*.ser)", "*.ser");
        fileChooser.getExtensionFilters().add(extFilter);
         
        //Show save file dialog

 		Parent parent = FXMLLoader.load(getClass().getResource("../view/Menu.fxml"));
 		Scene scene = new Scene(parent);
 		Stage stage = (Stage) scene.getWindow();
        File file = fileChooser.showOpenDialog(stage);
		
		FileInputStream fin = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fin);
		
		HistoryOfMoves historyOfMoves = (HistoryOfMoves) oin.readObject();
		
		oin.close();
		
		System.out.println(historyOfMoves);	
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/MoveViewer.fxml"));
		Parent root = (Parent) loader.load();
		MoveViewerController controller = loader.<MoveViewerController>getController();
		controller.initData(historyOfMoves);
		Scene scene2 = new Scene(root);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(scene2);
		appStage.setX(400);
		appStage.setY(150);
		appStage.setWidth(1210);
		appStage.setHeight(830);
		
		appStage.show();
		
		
	}
	
}
