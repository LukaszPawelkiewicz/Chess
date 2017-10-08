package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.model.HistoryOfMoves;
import application.model.Imgs;
import application.model.Move;
import application.model.Plansza;
import application.model.PlanszaSerializable;
import application.model.Pole;
import application.model.TableMove;
import application.model.enums.KolorGracza;
import application.model.enums.RodzajPionka;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MoveViewerController implements Initializable{

	@FXML
	private GridPane pola;
	@FXML
	private Button back;
	@FXML
	private Button refresh;
	@FXML
	private TableView<TableMove> table;
	@FXML
	private TableColumn<TableMove, Integer> tableMoveIdCol;
	@FXML
	private TableColumn<TableMove, String> tablePlayerCol;
	@FXML
	private TableColumn<TableMove, String> tableOldFieldCol;
	@FXML
	private TableColumn<TableMove, String> tableNewFieldCol;
	
	private HistoryOfMoves historyOfMoves;
	private List<Plansza> listOfBoards = new ArrayList<>();
	private Imgs imgs = new Imgs();
	
	private final ObservableList<TableMove> data = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		table.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown()) {
		            try{
		        		for (int i=0;i<8;i++){
		        			for (int j=0;j<8;j++){
		        				Pole pole = (Pole) getPole(pola, i, j);
		        				pole.setRodzajPionka(RodzajPionka.empty);
		        				pole.setKolorGracza(KolorGracza.empty);
		        				pole.setAndDisplayFigure(null);		        			
		        			}
		        		}
		        		System.out.println(listOfBoards.get(table.getSelectionModel().getSelectedItem().getIndex()-1));
		            	setBoard(listOfBoards.get(table.getSelectionModel().getSelectedItem().getIndex()-1), pola);
		            } catch (Exception e){
		            	e.printStackTrace();
		            };
		        }
		    }
		});
	}
	
	public void initData(HistoryOfMoves history){
		this.historyOfMoves = history;
	}
	
	public void backClick(ActionEvent event) throws IOException{
		Parent menu = FXMLLoader.load(getClass().getResource("../view/Menu.fxml"));
		Scene menuScene = new Scene(menu);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		appStage.setScene(menuScene);
		appStage.setX(600);
		appStage.setY(300);
		appStage.setWidth(600);
		appStage.setHeight(425);
		appStage.show();
	}
	
	public void refreshClick(){
		
		this.listOfBoards.clear();
		this.data.clear();
		
		System.out.println(this.historyOfMoves);
		initializeBackgroundPools(100, pola);
				
		for (PlanszaSerializable plansza : this.historyOfMoves.getListOfBoards()){
			this.listOfBoards.add(new Plansza(plansza));
		}
		
		for (Move move : this.historyOfMoves.getListOfMoves()){
			data.add(new TableMove(move.getIndex(), move.getPlayer(), move.getOldField(), move.getNewField()));
			tableMoveIdCol.setCellValueFactory(new PropertyValueFactory<TableMove, Integer>("index"));
			tablePlayerCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("player"));
			tableOldFieldCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("oldField"));
			tableNewFieldCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("newField"));
			table.setItems(data);
		}
		
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				pola.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), i, j);
			}
		}
				
	}

	private void initializeBackgroundPools(int size, GridPane panel){
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				Rectangle r =  new Rectangle();
				r.setX(0);
				r.setY(0);
				r.setWidth(size);
				r.setHeight(size);
				if ((i%2==0 && j%2==0) || (i%2==1 && j%2==1)) r.getStyleClass().add("rectangle-white");
				else r.getStyleClass().add("rectangle-black");
				panel.add(r, i, j);	
			}
	}
	
	public HistoryOfMoves getHistoryOfMoves() {
		return historyOfMoves;
	}

	public void setHistoryOfMoves(HistoryOfMoves historyOfMoves) {
		this.historyOfMoves = historyOfMoves;
	}
	
	private void setBoard(Plansza plansza, GridPane panel){
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){

				Pole pole = (Pole) getPole(pola, i, j);
				pole.setRodzajPionka(plansza.getPolaDoGry()[i][j].getRodzajPionka());
				pole.setKolorGracza(plansza.getPolaDoGry()[i][j].getKolorGracza());
				
				switch (pole.getKolorGracza()){
				case white:{
					switch (pole.getRodzajPionka()){
					case rook:{
						pole.setAndDisplayFigure(imgs.getWhiteRook());
						break;
					}
					case knight:{
						pole.setAndDisplayFigure(imgs.getWhiteKnight());
						break;
					}
					case bishop:{
						pole.setAndDisplayFigure(imgs.getWhiteBishop());
						break;
					}
					case queen:{
						pole.setAndDisplayFigure(imgs.getWhiteQueen());
						break;
					}
					case king:{
						pole.setAndDisplayFigure(imgs.getWhiteKing());
						break;
					}
					case pawn:{
						pole.setAndDisplayFigure(imgs.getWhitePawn());
						break;
					}
					default:{
						break;
					}
					}
					break;
				}
				case black:{
					switch (pole.getRodzajPionka()){
					case rook:{
						pole.setAndDisplayFigure(imgs.getBlackRook());
						break;
					}
					case knight:{
						pole.setAndDisplayFigure(imgs.getBlackKnight());
						break;
					}
					case bishop:{
						pole.setAndDisplayFigure(imgs.getBlackBishop());
						break;
					}
					case queen:{
						pole.setAndDisplayFigure(imgs.getBlackQueen());
						break;
					}
					case king:{
						pole.setAndDisplayFigure(imgs.getBlackKing());
						break;
					}
					case pawn:{
						pole.setAndDisplayFigure(imgs.getBlackPawn());
						break;
					}
					default:{
						break;
					}
					}
					break;
				}
				default:{
					break;
				}
				}
			}
		}
	}
			
	private Node getPole(GridPane gridPane, int col, int row) { //returns first node
	    for (Node node : gridPane.getChildren()) {
	        if ((GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) && (node.getClass() == Pole.class)) {
	            return node;
	        }
	    }
	    return null;
	}	
	
}
