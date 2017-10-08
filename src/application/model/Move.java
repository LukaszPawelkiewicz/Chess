package application.model;

import java.io.Serializable;


public class Move implements Serializable{

	private static final long serialVersionUID = 1233107791294013096L;
	
	private int index;
	private String player;
	private String oldField;
	private String newField;
	
	public Move(TableMove tableMove){
		setIndex(tableMove.getIndex());
		setPlayer(tableMove.getPlayer());
		setOldField(tableMove.getOldField());
		setNewField(tableMove.getNewField());
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getOldField() {
		return oldField;
	}
	public void setOldField(String oldField) {
		this.oldField = oldField;
	}
	public String getNewField() {
		return newField;
	}
	public void setNewField(String newField) {
		this.newField = newField;
	}
	
	
	
}
