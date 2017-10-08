package application.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PoziomTrudnosciController {
	
	@FXML
	private Button easy;
	@FXML
	private Button medium;
	@FXML
	private Button hard;
	@FXML
	private Button cancel;
	
	public void cancelPressed(ActionEvent event) throws IOException{
		Parent menu = FXMLLoader.load(getClass().getResource("../view/Menu.fxml"));
		Scene menuScene = new Scene(menu);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(menuScene);
		appStage.show();
	}
	
	public void easyPressed(ActionEvent event) throws IOException {
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

	public void mediumPressed(ActionEvent event) throws IOException {
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

	public void hardPressed(ActionEvent event) throws IOException {
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
}
