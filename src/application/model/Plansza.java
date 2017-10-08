package application.model;

public class Plansza {
	
	private Pole[][] polaDoGry = new Pole[8][8];
	
	public Plansza(){
		
	}
	
	public Plansza(Plansza plansza){
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				this.polaDoGry[i][j] = plansza.getPolaDoGry()[i][j];
			}
	}
	
	public Plansza(PlanszaSerializable plansza){
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++){
				this.polaDoGry[i][j] = new Pole(plansza.getPolaDoGry()[i][j]);
			}
	}

	public void addPole(Pole pole, int x, int y){
		this.polaDoGry[x][y] = pole;
	}
	
	public Pole getPole(int x, int y){
		return new Pole(this.polaDoGry[x][y]);
	}
	
	public void wyswietl(){
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				System.out.print(polaDoGry[i][j] + " ");
			}
		System.out.println();
		}
	}
	
	public Pole[][] getPolaDoGry() {
		return polaDoGry;
	}

	public void setPolaDoGry(Pole[][] polaDoGry) {
		this.polaDoGry = polaDoGry;
	}
	
}
