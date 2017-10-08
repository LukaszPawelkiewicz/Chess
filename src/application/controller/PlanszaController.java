package application.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.model.HistoryOfMoves;
import application.model.Imgs;
import application.model.ImgsSmall;
import application.model.Move;
import application.model.Plansza;
import application.model.PlanszaSerializable;
import application.model.Pole;
import application.model.TableMove;
import application.model.TmpPool;
import application.model.enums.KolorGracza;
import application.model.enums.RodzajPionka;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PlanszaController implements Initializable{
	
	@FXML
	private Button save;
	@FXML
	private Button exit;
	@FXML
	private GridPane pola;
	@FXML 
	private GridPane polaSmall;
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

	private Plansza plansza = new Plansza();
	private Imgs imgs = new Imgs();
	private ImgsSmall imgsSmall = new ImgsSmall();
	private List<TmpPool> tmpPools = new ArrayList<>();
	private TmpPool lastField;
	//private TmpPool castlingField;
	//private boolean castlingWhite = false;
	//private boolean castlingBlack = false;
	private List<Plansza> listOfBoards = new ArrayList<>();
	private KolorGracza kolejkaGracza = KolorGracza.white;
	private int rowOld;
	private int columnOld;
	private int rowNew;
	private int columnNew;
		
	private final ObservableList<TableMove> data = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
				
		initializeBackgroundPools(100, pola);
		initializeBackgroundPools(40, polaSmall);
		setFigures();		
		fillTmpBoard();

		plansza.wyswietl();
		
		table.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown()) {
		            try{
		        		for (int i=0;i<8;i++){
		        			for (int j=0;j<8;j++){
		        				Pole pole = (Pole) getPole(polaSmall, i, j);
		        				pole.setRodzajPionka(RodzajPionka.empty);
		        				pole.setKolorGracza(KolorGracza.empty);
		        				pole.setAndDisplayFigure(null);
		        			}
		        		}
		            	setBoard(listOfBoards.get(table.getSelectionModel().getSelectedItem().getIndex()-1), polaSmall);
		            } catch (Exception e){};
		        }
		    }
		});
	}
			
	@FXML
	private void mouseClicked(MouseEvent event){
		if (event.getButton() == MouseButton.PRIMARY){
			for (Node node : pola.getChildren()){
				if( node.getBoundsInParent().contains(event.getSceneX(),  event.getSceneY()) && node instanceof Pole) {
					if(tmpPools.isEmpty()){
						System.out.println( "Node: " + node + " at " + GridPane.getColumnIndex(node) + "/" + GridPane.getRowIndex(node));
						showValidMoves(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
						this.columnOld = GridPane.getColumnIndex(node);
						this.rowOld = GridPane.getRowIndex(node);
					} else{
						if (move(GridPane.getColumnIndex(node), GridPane.getRowIndex(node))){
							this.columnNew = GridPane.getColumnIndex(node);
							this.rowNew = GridPane.getRowIndex(node);
							tableAdd();
						}
					}
				}
			}
		}
				
		if (event.getButton() == MouseButton.SECONDARY){
			freeTmpPools();
		}
	}
	
	private void showValidMoves(int col, int row){
		//pawn
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.pawn
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			if (row == 6 &&
					((Pole) getPole(pola, col, row-1)).getRodzajPionka() == RodzajPionka.empty &&
					((Pole) getPole(pola, col, row-2)).getRodzajPionka() == RodzajPionka.empty){
				addRectangle(col, row-1);
				addRectangle(col, row-2);
				lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.white);
			} else 
			try{
				if (((Pole) getPole(pola, col, row-1)).getRodzajPionka() == RodzajPionka.empty){				
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getRodzajPionka() != RodzajPionka.empty &&
						((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getRodzajPionka() != RodzajPionka.empty &&
						((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.white);
				}
			} catch (Exception e){};
		}
		
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.pawn
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			if (row == 1 &&
					((Pole) getPole(pola, col, row+1)).getRodzajPionka() == RodzajPionka.empty &&
					((Pole) getPole(pola, col, row+2)).getRodzajPionka() == RodzajPionka.empty){
				addRectangle(col, row+1);
				addRectangle(col, row+2);
				lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.black);
			}else 
			try{
				if (((Pole) getPole(pola, col, row+1)).getRodzajPionka() == RodzajPionka.empty){				
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getRodzajPionka() != RodzajPionka.empty &&
						((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getRodzajPionka() != RodzajPionka.empty &&
						((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.pawn, KolorGracza.black);
				}
			} catch (Exception e){};
		}
		
		//knight
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.knight
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			try{
				if (((Pole) getPole(pola, col-2, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-2, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-2, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row-2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row-2)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row-2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-2, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-2, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-2, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};	
			
			try{
				if (((Pole) getPole(pola, col-1, row+2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row+2)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row+2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row-2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row-2)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row-2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+2, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+2, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+2, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row+2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row+2)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row+2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+2, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+2, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+2, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.white);
				}
			} catch (Exception e){};
		}
		
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.knight
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			try{
				if (((Pole) getPole(pola, col-2, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-2, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-2, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row-2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row-2)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row-2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-2, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-2, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-2, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};	
			
			try{
				if (((Pole) getPole(pola, col-1, row+2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row+2)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row+2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row-2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row-2)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row-2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+2, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+2, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+2, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row+2)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row+2)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row+2);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+2, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+2, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+2, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.knight, KolorGracza.black);
				}
			} catch (Exception e){};
		}
		
		//rook
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.rook
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			int tmp = 1;
			try{
				while (((Pole) getPole(pola, col, row-tmp)).getRodzajPionka() == RodzajPionka.empty && (row-tmp)>=0){
					addRectangle(col, row-tmp);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					if (((Pole) getPole(pola, col, row-tmp-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col, row-tmp-1);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col, row+tmp)).getRodzajPionka() == RodzajPionka.empty && (row+tmp)<=8){
					addRectangle(col, row+tmp);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					if (((Pole) getPole(pola, col, row+tmp+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col, row+tmp+1);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col-tmp, row)).getRodzajPionka() == RodzajPionka.empty && (col-tmp)>=0){
					addRectangle(col-tmp, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp-1, row)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp-1, row);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col+tmp, row)).getRodzajPionka() == RodzajPionka.empty && (col+tmp)<=8){
					addRectangle(col+tmp, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					
					if (((Pole) getPole(pola, col+tmp+1, row)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp+1, row);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				}
			} catch (Exception e){};
			
			/*if (col==0 &&
					row==7 &&
					((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+2, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+3, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+4, row)).getKolorGracza() == KolorGracza.white &&
					((Pole) getPole(pola, col+4, row)).getRodzajPionka() == RodzajPionka.king){
				//castling
				addRectangle(col+4, row);
				lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				castlingField = new TmpPool(col+4, row, RodzajPionka.king, KolorGracza.white);
				castlingWhite = true;//TODO trzeba wrocic do false po wykonaniu DOWOLNEGO ruchu
			}
			
			if (col==7 &&
					row==7 &&
					((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col-2, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col-3, row)).getKolorGracza() == KolorGracza.white &&
					((Pole) getPole(pola, col-3, row)).getRodzajPionka() == RodzajPionka.king){
				//castling
				addRectangle(col-3, row);
				lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.white);
				castlingField = new TmpPool(col-3, row, RodzajPionka.king, KolorGracza.white);
				castlingWhite = true;//TODO trzeba wrocic do false po wykonaniu DOWOLNEGO ruchu
			}*/
			
		}
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.rook
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			int tmp = 1;
			try{
				while (((Pole) getPole(pola, col, row-tmp)).getRodzajPionka() == RodzajPionka.empty && (row-tmp)>=0){
					addRectangle(col, row-tmp);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					if (((Pole) getPole(pola, col, row-tmp-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col, row-tmp-1);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col, row+tmp)).getRodzajPionka() == RodzajPionka.empty && (row+tmp)<=8){
					addRectangle(col, row+tmp);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					if (((Pole) getPole(pola, col, row+tmp+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col, row+tmp+1);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col-tmp, row)).getRodzajPionka() == RodzajPionka.empty && (col-tmp)>=0){
					addRectangle(col-tmp, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp-1, row)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp-1, row);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp = 1;
			try{
				while (((Pole) getPole(pola, col+tmp, row)).getRodzajPionka() == RodzajPionka.empty && (col+tmp)<=8){
					addRectangle(col+tmp, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp+1, row)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp+1, row);
						lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
					}
					tmp++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				}
			} catch (Exception e){};
			
			/*if (col==0 &&
					row==0 &&
					((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+2, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+3, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col+4, row)).getKolorGracza() == KolorGracza.black &&
					((Pole) getPole(pola, col+4, row)).getRodzajPionka() == RodzajPionka.king){
				//rocade
				addRectangle(col+4, row);
				lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				castlingField = new TmpPool(col+4, row, RodzajPionka.king, KolorGracza.black);
				castlingBlack = true;//TODO trzeba wrocic do false po wykonaniu DOWOLNEGO ruchu
			}
			
			if (col==7 &&
					row==0 &&
					((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col-2, row)).getKolorGracza() == KolorGracza.empty &&
					((Pole) getPole(pola, col-3, row)).getKolorGracza() == KolorGracza.black &&
					((Pole) getPole(pola, col-3, row)).getRodzajPionka() == RodzajPionka.king){
				//rocade
				addRectangle(col-3, row);
				lastField = new TmpPool(col, row, RodzajPionka.rook, KolorGracza.black);
				castlingField = new TmpPool(col-3, row, RodzajPionka.king, KolorGracza.black);
				castlingBlack = true;//TODO trzeba wrocic do false po wykonaniu DOWOLNEGO ruchu
			}*/
		}
		
		//bishop
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.bishop
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			int tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp1, row-tmp1)).getRodzajPionka() == RodzajPionka.empty && (row-tmp1)>=0 && (col-tmp1)>=0){
					addRectangle(col-tmp1, row-tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp1-1, row-tmp1-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp1-1, row-tmp1-1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp1, row+tmp1)).getRodzajPionka() == RodzajPionka.empty && (row+tmp1)<=8 && (col+tmp1)<=8){
					addRectangle(col+tmp1, row+tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					if (((Pole) getPole(pola, col+tmp1+1, row+tmp1+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp1+1, row+tmp1+1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp1, row-tmp1)).getRodzajPionka() == RodzajPionka.empty && (row-tmp1)>=0 && (col+tmp1)<=8){
					addRectangle(col+tmp1, row-tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					if (((Pole) getPole(pola, col+tmp1+1, row-tmp1-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp1+1, row-tmp1-1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp1, row+tmp1)).getRodzajPionka() == RodzajPionka.empty && (row+tmp1)<=8 && (col-tmp1)>=0){
					addRectangle(col-tmp1, row+tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp1-1, row+tmp1+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp1-1, row+tmp1+1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.white);
				}
			} catch (Exception e){};
		}
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.bishop
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			int tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp1, row-tmp1)).getRodzajPionka() == RodzajPionka.empty && (row-tmp1)>=0 && (col-tmp1)>=0){
					addRectangle(col-tmp1, row-tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp1-1, row-tmp1-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp1-1, row-tmp1-1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp1, row+tmp1)).getRodzajPionka() == RodzajPionka.empty && (row+tmp1)<=8 && (col+tmp1)<=8){
					addRectangle(col+tmp1, row+tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp1+1, row+tmp1+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp1+1, row+tmp1+1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp1, row-tmp1)).getRodzajPionka() == RodzajPionka.empty && (row-tmp1)>=0 && (col+tmp1)<=8){
					addRectangle(col+tmp1, row-tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp1+1, row-tmp1-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp1+1, row-tmp1-1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp1 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp1, row+tmp1)).getRodzajPionka() == RodzajPionka.empty && (row+tmp1)<=8 && (col-tmp1)>=0){
					addRectangle(col-tmp1, row+tmp1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp1-1, row+tmp1+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp1-1, row+tmp1+1);
						lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
					}
					tmp1++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.bishop, KolorGracza.black);
				}
			} catch (Exception e){};
		}
		
		//queen
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.queen
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			int tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0 && (col-tmp2)>=0){
					addRectangle(col-tmp2, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp2-1, row-tmp2-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp2-1, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0){
					addRectangle(col, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col, row-tmp2-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8 && (col+tmp2)<=8){
					addRectangle(col+tmp2, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col+tmp2+1, row+tmp2+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp2+1, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8){
					addRectangle(col, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col, row+tmp2+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8 && (col-tmp2)>=0){
					addRectangle(col-tmp2, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp2-1, row+tmp2+1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp2-1, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row)).getRodzajPionka() == RodzajPionka.empty && (col-tmp2)>=0){
					addRectangle(col-tmp2, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col-tmp2-1, row)).getKolorGracza() == KolorGracza.black){
						addRectangle(col-tmp2-1, row);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0 && (col+tmp2)<=8){
					addRectangle(col+tmp2, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col+tmp2+1, row-tmp2-1)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp2+1, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row)).getRodzajPionka() == RodzajPionka.empty && (col+tmp2)<=8){
					addRectangle(col+tmp2, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					if (((Pole) getPole(pola, col+tmp2+1, row)).getKolorGracza() == KolorGracza.black){
						addRectangle(col+tmp2+1, row);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.white);
				}
			} catch (Exception e){};
		}
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.queen
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			int tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0 && (col-tmp2)>=0){
					addRectangle(col-tmp2, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp2-1, row-tmp2-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp2-1, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0){
					addRectangle(col, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col, row-tmp2-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8 && (col+tmp2)<=8){
					addRectangle(col+tmp2, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp2+1, row+tmp2+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp2+1, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8){
					addRectangle(col, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col, row+tmp2+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row+tmp2)).getRodzajPionka() == RodzajPionka.empty && (row+tmp2)<=8 && (col-tmp2)>=0){
					addRectangle(col-tmp2, row+tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp2-1, row+tmp2+1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp2-1, row+tmp2+1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col-tmp2, row)).getRodzajPionka() == RodzajPionka.empty && (col-tmp2)>=0){
					addRectangle(col-tmp2, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col-tmp2-1, row)).getKolorGracza() == KolorGracza.white){
						addRectangle(col-tmp2-1, row);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row-tmp2)).getRodzajPionka() == RodzajPionka.empty && (row-tmp2)>=0 && (col+tmp2)<=8){
					addRectangle(col+tmp2, row-tmp2);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp2+1, row-tmp2-1)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp2+1, row-tmp2-1);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
			
			tmp2 = 1;
			try{
				while (((Pole) getPole(pola, col+tmp2, row)).getRodzajPionka() == RodzajPionka.empty && (col+tmp2)<=8){
					addRectangle(col+tmp2, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					if (((Pole) getPole(pola, col+tmp2+1, row)).getKolorGracza() == KolorGracza.white){
						addRectangle(col+tmp2+1, row);
						lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
					}
					tmp2++;
				}
			} catch (Exception e){};
			try{
				if (((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.queen, KolorGracza.black);
				}
			} catch (Exception e){};
		}
		
		//king
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.king
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.white){
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
						
			try{
				if (((Pole) getPole(pola, col+1, row)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.black){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.white);
				}
			} catch (Exception e){};
		}
		
		if (((Pole) getPole(pola, col, row)).getRodzajPionka() == RodzajPionka.king
				&& ((Pole) getPole(pola, col, row)).getKolorGracza() == KolorGracza.black){
			try{
				if (((Pole) getPole(pola, col-1, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row-1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row-1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row-1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col-1, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col-1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col-1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
			
			try{
				if (((Pole) getPole(pola, col+1, row+1)).getRodzajPionka() == RodzajPionka.empty ||
						((Pole) getPole(pola, col+1, row+1)).getKolorGracza() == KolorGracza.white){
					addRectangle(col+1, row+1);
					lastField = new TmpPool(col, row, RodzajPionka.king, KolorGracza.black);
				}
			} catch (Exception e){};
		}
	}
	
	private void setFigures(){
		
		//blackPlayer
		pola.add(new Pole(RodzajPionka.rook, KolorGracza.black, imgs.getBlackRook()), 0, 0);
		pola.add(new Pole(RodzajPionka.knight, KolorGracza.black, imgs.getBlackKnight()), 1, 0);
		pola.add(new Pole(RodzajPionka.bishop, KolorGracza.black, imgs.getBlackBishop()), 2, 0);
		pola.add(new Pole(RodzajPionka.queen, KolorGracza.black, imgs.getBlackQueen()), 3, 0);
		pola.add(new Pole(RodzajPionka.king, KolorGracza.black, imgs.getBlackKing()), 4, 0);
		pola.add(new Pole(RodzajPionka.bishop, KolorGracza.black, imgs.getBlackBishop()), 5, 0);
		pola.add(new Pole(RodzajPionka.knight, KolorGracza.black, imgs.getBlackKnight()), 6, 0);
		pola.add(new Pole(RodzajPionka.rook, KolorGracza.black, imgs.getBlackRook()), 7, 0);
		
		//pola.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), 1, 7);
		//pola.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), 2, 7);
		//pola.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), 3, 7);
		
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 0, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 1, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 2, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 3, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 4, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 5, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 6, 1);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.black, imgs.getBlackPawn()), 7, 1);
		
		//whitePlayer
		pola.add(new Pole(RodzajPionka.rook, KolorGracza.white, imgs.getWhiteRook()), 0, 7);
		pola.add(new Pole(RodzajPionka.knight, KolorGracza.white, imgs.getWhiteKnight()), 1, 7);
		pola.add(new Pole(RodzajPionka.bishop, KolorGracza.white, imgs.getWhiteBishop()), 2, 7);
		pola.add(new Pole(RodzajPionka.queen, KolorGracza.white, imgs.getWhiteQueen()), 3, 7);
		pola.add(new Pole(RodzajPionka.king, KolorGracza.white, imgs.getWhiteKing()), 4, 7);
		pola.add(new Pole(RodzajPionka.bishop, KolorGracza.white, imgs.getWhiteBishop()), 5, 7);
		pola.add(new Pole(RodzajPionka.knight, KolorGracza.white, imgs.getWhiteKnight()), 6, 7);
		pola.add(new Pole(RodzajPionka.rook, KolorGracza.white, imgs.getWhiteRook()), 7, 7);
		
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 0, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 1, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 2, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 3, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 4, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 5, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 6, 6);
		pola.add(new Pole(RodzajPionka.pawn, KolorGracza.white, imgs.getWhitePawn()), 7, 6);
		
		
		
		for (int i=0;i<8;i++){
			for (int j=2;j<6;j++){
				pola.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), i, j);
			}
		}
		
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				polaSmall.add(new Pole(RodzajPionka.empty, KolorGracza.empty, null), i, j);
			}
		}
		
	}
	
	private boolean move(int col, int row){
		if (getRectangle(pola, col, row).getStyleClass().get(0) == "rectangle-active" &&
				lastField != null){	
			
			switch (lastField.getKolorGracza()){
			case black :{
				switch (lastField.getRodzajPionka()){
				case pawn :{
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackPawn());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				case rook :{
					//if (castlingBlack){}			
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackRook());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				case knight :{
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackKnight());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				case bishop :{
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackBishop());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				case queen :{
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackQueen());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				case king :{
					if (this.kolejkaGracza == KolorGracza.white) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getBlackKing());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.white;
					return true;
				}
				default:
					break;
				}
			break;
			}
			case white :{
				switch (lastField.getRodzajPionka()){
				case pawn :{
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhitePawn());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				case rook :{
					//if (castlingWhite){}
						/*if (this.kolejkaGracza == KolorGracza.black) return false;
						Pole pole = (Pole) getPole(pola, col, row);
						pole.setRodzajPionka(lastField.getRodzajPionka());
						pole.setKolorGracza(lastField.getKolorGracza());
						pole.setAndDisplayFigure(imgs.getWhiteRook());
						
						pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
						pole.setRodzajPionka(RodzajPionka.empty);
						pole.setKolorGracza(KolorGracza.empty);
						pole.setAndDisplayFigure(null);
												
						pole = (Pole) getPole(pola, col-2, row);
						pole.setRodzajPionka(castlingField.getRodzajPionka());
						pole.setKolorGracza(castlingField.getKolorGracza());
						pole.setAndDisplayFigure(imgs.getWhiteKing());
						
						pole = (Pole) getPole(pola, castlingField.getCol(), castlingField.getRow());
						pole.setRodzajPionka(RodzajPionka.empty);
						pole.setKolorGracza(KolorGracza.empty);
						pole.setAndDisplayFigure(null);
						
						lastField = null;
						castlingField = null;
						castlingWhite = false;
						freeTmpPools();
						this.kolejkaGracza = KolorGracza.black;
						*/
					
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhiteRook());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				case knight :{
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhiteKnight());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				case bishop :{
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhiteBishop());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				case queen :{
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhiteQueen());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				case king :{
					if (this.kolejkaGracza == KolorGracza.black) return false;
					Pole pole = (Pole) getPole(pola, col, row);
					pole.setRodzajPionka(lastField.getRodzajPionka());
					pole.setKolorGracza(lastField.getKolorGracza());
					pole.setAndDisplayFigure(imgs.getWhiteKing());
					
					pole = (Pole) getPole(pola, lastField.getCol(), lastField.getRow());
					pole.setRodzajPionka(RodzajPionka.empty);
					pole.setKolorGracza(KolorGracza.empty);
					pole.setAndDisplayFigure(null);
					
					lastField = null;
					freeTmpPools();
					this.kolejkaGracza = KolorGracza.black;
					return true;
				}
				default:
					break;
			}
			break;
			}
			default:
				break;
			}
		}
		return false;
	}
	
	private void addRectangle(int col, int row){
		Rectangle r;
		
		r = (Rectangle) getRectangle(pola, col, row);
		tmpPools.add(new TmpPool(r.getStyleClass().get(0), col, row));
		r.getStyleClass().clear();
		r.getStyleClass().add("rectangle-active");
		
		//lastField = null;
	}
	
	private void freeTmpPools(){
		for (TmpPool tmpPool : tmpPools){
			Rectangle r;
			r = (Rectangle) getRectangle(pola, tmpPool.getCol(), tmpPool.getRow());
			r.getStyleClass().clear();
			r.getStyleClass().add(tmpPool.getStyleClass());
		}
		tmpPools.clear();
	}
	
	private void fillTmpBoard(){
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				plansza.addPole(new Pole((Pole) getPole(pola, i, j)), i, j);
				// ok System.out.println(plansza.getPolaDoGry()[0][0].getRodzajPionka());
			}
	}
	
	private void tableAdd(){
		fillTmpBoard();
		listOfBoards.add(new Plansza(plansza));
		data.add(new TableMove(listOfBoards.size(), kolejkaGracza.name() , "col: "+ this.columnOld + " row: " + this.rowOld, "col: " + this.columnNew + " row: " + this.rowNew));	//odnosi sie do ruchu numer listOfMovesSize() - 1 
		tableMoveIdCol.setCellValueFactory(new PropertyValueFactory<TableMove, Integer>("index"));
		tablePlayerCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("player"));
		tableOldFieldCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("oldField"));
		tableNewFieldCol.setCellValueFactory(new PropertyValueFactory<TableMove, String>("newField"));
		table.setItems(data);
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
		
	private void setBoard(Plansza plansza, GridPane panel){
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){

				Pole pole = (Pole) getPole(polaSmall, i, j);
				pole.setRodzajPionka(plansza.getPolaDoGry()[i][j].getRodzajPionka());
				pole.setKolorGracza(plansza.getPolaDoGry()[i][j].getKolorGracza());
				
				switch (pole.getKolorGracza()){
				case white:{
					switch (pole.getRodzajPionka()){
					case rook:{
						pole.setAndDisplayFigure(imgsSmall.getWhiteRook());
						break;
					}
					case knight:{
						pole.setAndDisplayFigure(imgsSmall.getWhiteKnight());
						break;
					}
					case bishop:{
						pole.setAndDisplayFigure(imgsSmall.getWhiteBishop());
						break;
					}
					case queen:{
						pole.setAndDisplayFigure(imgsSmall.getWhiteQueen());
						break;
					}
					case king:{
						pole.setAndDisplayFigure(imgsSmall.getWhiteKing());
						break;
					}
					case pawn:{
						pole.setAndDisplayFigure(imgsSmall.getWhitePawn());
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
						pole.setAndDisplayFigure(imgsSmall.getBlackRook());
						break;
					}
					case knight:{
						pole.setAndDisplayFigure(imgsSmall.getBlackKnight());
						break;
					}
					case bishop:{
						pole.setAndDisplayFigure(imgsSmall.getBlackBishop());
						break;
					}
					case queen:{
						pole.setAndDisplayFigure(imgsSmall.getBlackQueen());
						break;
					}
					case king:{
						pole.setAndDisplayFigure(imgsSmall.getBlackKing());
						break;
					}
					case pawn:{
						pole.setAndDisplayFigure(imgsSmall.getBlackPawn());
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
	
	private Node getRectangle(GridPane gridPane, int col, int row) { //returns first node
	    for (Node node : gridPane.getChildren()) {
	        if ((GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) && (node.getClass() == Rectangle.class)) {
	        	return node;
	        }
	    }
	    return null;
	}	
	
	@FXML
	private void exitClick(){
		Stage stage = (Stage) exit.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void saveClick() throws IOException{
		FileChooser fileChooser = new FileChooser();
		  
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SER files (*.ser)", "*.ser");
        fileChooser.getExtensionFilters().add(extFilter);
         
        //Show save file dialog

 		Parent parent = FXMLLoader.load(getClass().getResource("../view/Plansza.fxml"));
 		Scene scene = new Scene(parent);
 		Stage stage = (Stage) scene.getWindow();
        File file = fileChooser.showSaveDialog(stage);
       
         
        if(file != null){
        	saveTable(this.data, file);
        }
        
	}
	
	/*private void saveFile(Object object, File file){
     
		try {
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oout = new ObjectOutputStream(fout);
			oout.writeObject(object);
			oout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
         
    }
    */
	
	 private void saveTable(ObservableList<TableMove> table, File file) {
		    try {
		        // write object to file
		        FileOutputStream fos = new FileOutputStream(file);
		        ObjectOutputStream oos = new ObjectOutputStream(fos);
		        
		        ArrayList<Move> listOfMoves = new ArrayList<Move>();
		        for (TableMove tableMove : table){
		        	listOfMoves.add(new Move(tableMove));
		        }
		        
		        ArrayList<PlanszaSerializable> listOfBoards = new ArrayList<PlanszaSerializable>();
		        for (Plansza plansza : this.listOfBoards){
		        	listOfBoards.add(new PlanszaSerializable(plansza));
		        }
		        
		        HistoryOfMoves historyOfMoves = new HistoryOfMoves(listOfMoves, listOfBoards);
		        
		        oos.writeObject(historyOfMoves);
		        oos.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }

		}
}
