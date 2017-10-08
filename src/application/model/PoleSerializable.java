package application.model;

import java.io.Serializable;

import application.model.enums.KolorGracza;
import application.model.enums.RodzajPionka;

public class PoleSerializable implements Serializable{

	
	private static final long serialVersionUID = -5290100240277337772L;

	private RodzajPionka rodzajPionka;
	private KolorGracza kolorGracza;
	
	public PoleSerializable(Pole pole){
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
	
}
