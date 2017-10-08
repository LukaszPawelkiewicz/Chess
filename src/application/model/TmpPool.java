package application.model;

import application.model.enums.KolorGracza;
import application.model.enums.RodzajPionka;

public class TmpPool {
	private String styleClass;
	private int col;
	private int row;
	private RodzajPionka rodzajPionka;
	private KolorGracza kolorGracza;
	
	public TmpPool(String style, int col, int row){
		setStyleClass(style);
		setCol(col);
		setRow(row);
	}
	
	public TmpPool(int col, int row, RodzajPionka rodzaj, KolorGracza kolor) {
		setCol(col);
		setRow(row);
		setRodzajPionka(rodzaj);
		setKolorGracza(kolor);
	}

	public String getStyleClass() {
		return styleClass;
	}
	
	public void setStyleClass(String styleClss) {
		this.styleClass = styleClss;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}

	public RodzajPionka getRodzajPionka() {
		return rodzajPionka;
	}

	public void setRodzajPionka(RodzajPionka rodzajPionka) {
		this.rodzajPionka = rodzajPionka;
	}

	public KolorGracza getKolorGracza() {
		return kolorGracza;
	}

	public void setKolorGracza(KolorGracza kolorGracza) {
		this.kolorGracza = kolorGracza;
	}
	
}
