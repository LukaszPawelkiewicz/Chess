package application.model;

import java.io.Serializable;
import java.util.ArrayList;

public class HistoryOfMoves implements Serializable{

	private static final long serialVersionUID = -3622848451412372378L;

	private ArrayList<Move> listOfMoves;
	private ArrayList<PlanszaSerializable> listOfBoards;
	
	public HistoryOfMoves(ArrayList<Move> moves, ArrayList<PlanszaSerializable> boards){
		setListOfMoves(moves);
		setListOfBoards(boards);
	}

	public ArrayList<Move> getListOfMoves() {
		return listOfMoves;
	}

	public void setListOfMoves(ArrayList<Move> listOfMoves) {
		this.listOfMoves = listOfMoves;
	}

	public ArrayList<PlanszaSerializable> getListOfBoards() {
		return listOfBoards;
	}

	public void setListOfBoards(ArrayList<PlanszaSerializable> listOfBoards) {
		this.listOfBoards = listOfBoards;
	}


	
	
}
