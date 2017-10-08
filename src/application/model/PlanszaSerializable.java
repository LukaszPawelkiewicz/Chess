package application.model;

import java.io.Serializable;

public class PlanszaSerializable implements Serializable{

	private static final long serialVersionUID = -6706298688605693724L;
	
	private PoleSerializable[][] polaDoGry = new PoleSerializable[8][8];
	
	public PlanszaSerializable(Plansza plansza){
		for (int i=0; i<8; i++){
			for (int j=0; j<8; j++){
				this.polaDoGry[i][j] = new PoleSerializable(plansza.getPolaDoGry()[i][j]);
			}
		}
	}
	
	public PoleSerializable[][] getPolaDoGry() {
		return polaDoGry;
	}

	public void setPolaDoGry(PoleSerializable[][] polaDoGry) {
		this.polaDoGry = polaDoGry;
	}

}
