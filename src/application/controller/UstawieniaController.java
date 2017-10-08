package application.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class UstawieniaController{
	
	@FXML
	private ToggleButton voice;
	@FXML
	private Button darkStyle;
	@FXML
	private Button woodStyle;
	@FXML
	private Button cancel;
	@FXML
	private Slider volume;
		
	public void voiceSelected(){
		if (voice.isSelected()){
			volume.setVisible(true);
		} else{
			volume.setVisible(false);
		}
	}
	
	public void cancelPressed(ActionEvent event) throws IOException{
		Parent menu = FXMLLoader.load(getClass().getResource("../view/Menu.fxml"));
		Scene menuScene = new Scene(menu);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(menuScene);
		appStage.show();
	}
	
	public void selectDarkStyle(ActionEvent event) throws IOException{
		Parent menu = FXMLLoader.load(getClass().getResource("../view/Ustawienia.fxml"));
		Scene menuScene = new Scene(menu);
		menu.getStylesheets().clear();
		menu.getStylesheets().add(getClass().getResource("../view/application.css").toExternalForm());
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(menuScene);
		appStage.show();
	}
	
	public void selectWoodStyle(ActionEvent event) throws IOException{
		Parent menu = FXMLLoader.load(getClass().getResource("../view/Ustawienia.fxml"));
		Scene menuScene = new Scene(menu);
		menu.getStylesheets().clear();
		menu.getStylesheets().add(getClass().getResource("../view/WoodStyle.css").toExternalForm());
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(menuScene);
		appStage.show();
	}
}
