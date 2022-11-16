package model;

import events.SatelitteMoveListener;
import events.SatelliteMoved;

/**
 * Classe Balise représente par un <b>ElementMobile</b>.
 * @see ElementMobile
 * @see SatelitteMoveListener
 */
public class Balise extends ElementMobile implements SatelitteMoveListener{
	/**
	 * Booleen permettant de savoir quant est ce qu'une balise est à la surface de l'eau
	 */
	boolean aLaSurface;
	/**
	 * Cette variable sert à vérifier si on peut remonter à la surface.
	 * On ne peut remonter à la surface que si une synchronisation a été faite.
	 */
	boolean baliseSynchro;

	//
	public Balise(int memorySize) {
		super(memorySize);
		this.aLaSurface = false;
		this.baliseSynchro=true;
	}
	
	public int profondeur() { 
		return this.getPosition().y; 
	}

	/**
	 * Méthode pour augmenter une donnée
	 */
	protected void readSensors() {
		if(!this.aLaSurface && !this.memoryFull())
			this.dataSize++;
	}

	/**
	 * Méthode executée à chaque frame de l'animation
	 * Indique les déplacements à effectuer lors des différentes phases de récupération de la mémoire
	 */
	public void tick() {
		this.readSensors();
		System.out.println("data size: "+ this.dataSize);
		if (this.memoryFull() && this.baliseSynchro) {
			System.out.println("profondeur: "+ this.profondeur());
			Deplacement redescendre = new Redescendre(this.deplacement(), this.profondeur());
			Deplacement deplSynchro = new DeplSynchronisation(redescendre);
			Deplacement nextDepl = new MonteSurfacePourSynchro(deplSynchro);
			this.setDeplacement(nextDepl);
			this.baliseSynchro=false;

		}
		this.aLaSurface = false;
		super.tick();
	}

	@Override
	public void whenSatelitteMoved(SatelliteMoved arg) {
		DeplacementBalise dp = (DeplacementBalise) this.depl;
		dp.whenSatelitteMoved(arg, this);
		this.aLaSurface = true;
		this.baliseSynchro=true;
		this.resetData();
	}


}
