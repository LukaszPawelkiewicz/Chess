package application.model;

import application.model.enums.KolorGracza;
import application.model.enums.RodzajPionka;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Pole extends Pane{
	
	private RodzajPionka rodzajPionka;
	private KolorGracza kolorGracza;
	private Image figure;
	
	public Pole(RodzajPionka pionek, KolorGracza gracz, Image img){
		super();
		setRodzajPionka(pionek);
		setKolorGracza(gracz);
		setFigure(img);
		
		ImageView imgView = new ImageView(img);
		imgView.resize(90, 90);
		imgView.setX(5);
		imgView.setY(5);
		
		this.getChildren().add(imgView);
	}
	
	public Pole(Pole pole){
		setRodzajPionka(pole.getRodzajPionka());
		setKolorGracza(pole.getKolorGracza());
		setFigure(pole.getFigure());
	}
	
	public Pole(PoleSerializable pole){
		setRodzajPionka(pole.getRodzajPionka());
		setKolorGracza(pole.getKolorGracza());
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

	public Image getFigure() {
		return figure;
	}

	public void setFigure(Image figure) {
		this.figure = figure;
	}
	
	public void setAndDisplayFigure(Image figure) {
		this.figure = figure;
		
		ImageView imgView = new ImageView(figure);
		imgView.resize(90, 90);
		imgView.setX(5);
		imgView.setY(5);
		this.getChildren().remove(0);
		
		this.getChildren().add(imgView);
	}
	
	
}
