package application.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableMove{
	
	private final SimpleIntegerProperty index;
	private final SimpleStringProperty player;
	private final SimpleStringProperty oldField;
	private final SimpleStringProperty newField;
	
	public TableMove(int index, String player, String oldField, String newField){
		this.index = new SimpleIntegerProperty(index);
		this.player = new SimpleStringProperty(player);
		this.oldField = new SimpleStringProperty(oldField);
		this.newField = new SimpleStringProperty(newField);
	}

	public Integer getIndex() {
		return index.getValue();
	}

	public String getPlayer() {
		return player.getValue();
	}

	public String getOldField() {
		return oldField.getValue();
	}

	public String getNewField() {
		return newField.getValue();
	}
	
}
