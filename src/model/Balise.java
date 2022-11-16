package model;

import events.SatelitteMoveListener;
import events.SatelliteMoved;

public class Balise extends ElementMobile implements SatelitteMoveListener{

	boolean aLaSurface;
	
	public Balise(int memorySize) {
		super(memorySize);
		this.aLaSurface = false;
	}
	
	public int profondeur() { 
		return this.getPosition().y; 
	}
	
	protected void readSensors() {
		if(!this.aLaSurface)
			this.dataSize++;
	}
	
	public void tick() {
		this.readSensors();
		System.out.println("data size: "+ this.dataSize);
		if (this.memoryFull()) {
			Deplacement redescendre = new Redescendre(this.deplacement(), this.profondeur());
			Deplacement deplSynchro = new DeplSynchronisation(redescendre);
			Deplacement nextDepl = new MonteSurfacePourSynchro(deplSynchro);
			this.setDeplacement(nextDepl);
			this.resetData();
		}
		this.aLaSurface = false;
		super.tick();
	}

	@Override
	public void whenSatelitteMoved(SatelliteMoved arg) {
		DeplacementBalise dp = (DeplacementBalise) this.depl;
		dp.whenSatelitteMoved(arg, this);
		this.aLaSurface = true;
	}


}
