package org.example.model;

public class Servo {
	
	private int grados;
	
	public Servo(int grados) {
		this.grados = grados;
	}

	public int getGrados() {
		return this.grados;
	}
	
	public void setGrados(int grados) { //garantizar un rango correcto de giro del servo motor
		if(grados >= 0 && grados <= 180)
			this.grados = grados;
	}

}
