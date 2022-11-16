package simulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Balise;
import model.DeplHorizontal;
import model.DeplSatellite;
import model.DeplVertical;
import model.Deplacement;
import model.Manager;
import model.Satellite;
import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;
import views.GrBalise;
import views.GrEther;
import views.GrSatellite;
/**
 * Classe Simulation pour lancer l'application.
 * @author ELENGA Alphonse Junior & OUAKSEL boukhalfa
 * @version 1.0
 */
public class Simulation {
	final int FPS_MIN = 2;
	final int FPS_MAX = 500;
	final int FPS_INIT = 10;
	final int startDelay = 500 / FPS_INIT;
	Timer animation;
	Manager manager = new Manager();
	Dimension worldDim = new Dimension(900, 700);
	NiSpace world = new NiSpace("Satellite & Balises", this.worldDim);
	GrEther ether = new GrEther();
	NiRectangle sea = new NiRectangle();
	NiRectangle sky = new NiRectangle();

	public void animation() {
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				manager.tick();
				ether.repaint();		
			}
		};
		this.animation = new Timer(this.startDelay, taskPerformer);
		this.animation.setRepeats(true);
		this.animation.start();
	}

	/**
	 * Crée un slider gérant les fps de l'animation de l'application
	 *
	 * @return JPanel
	 */
	private JPanel fpsSliderPanel() {		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel(" FPS :", JLabel.RIGHT);
		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);

		framesPerSecond.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int fps = (int) source.getValue();
					int newDelay = 1000 / fps;
					animation.setDelay(newDelay);
					animation.setInitialDelay(newDelay * 10);
				}
			}
		});

		// Turn on labels at major tick marks.
		framesPerSecond.setMajorTickSpacing(50);
		framesPerSecond.setMinorTickSpacing(10);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(false);
		
		panel.add(label);
		panel.add(framesPerSecond);
		return panel;
	}

	/**
	 * Crée un JPanel contenant des boutons pour ajouter ou supprimer des satellites
	 * et des balises ou encore pour démarrer ou stopper l'application
	 *
	 * @return JPanel
	 */
	private JPanel buttonPanel() {
		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");
		JButton addBalise = new JButton("Balise +1");
		JButton delBalise = new JButton("Balise -1");
		JButton addSatellite = new JButton("Satellite +1");
		JButton delSatellite = new JButton("Satellite -1");
		stop.setEnabled(false);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animation.start();
				start.setEnabled(false);
				stop.setEnabled(true);
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animation.stop();
				start.setEnabled(true);
				stop.setEnabled(false);
			}
		});
		addBalise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBalise(sea, new Random().nextInt(100, 400),
						new Point(new Random().nextInt(20, 380), new Random().nextInt(20, 180)),
						new DeplHorizontal(new Random().nextInt(0, 200), new Random().nextInt(200, 800)));
			}
		});
		delBalise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteBalise();
			}
		});
		addSatellite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addSatelitte(sky, 100000, new Point(new Random().nextInt(0, 600), new Random().nextInt(0, 150)),
						new Random().nextInt(1, 5));
			}
		});
		delSatellite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSatellite();
			}
		});
		// Lay out the buttons from left to right.
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(start);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));

		panel.add(Box.createHorizontalGlue());
		panel.add(addBalise);
		panel.add(delBalise);
		panel.add(Box.createHorizontalGlue());
		panel.add(addSatellite);
		panel.add(delSatellite);
		panel.add(Box.createHorizontalGlue());
		panel.add(stop);
		return panel;
	}


	/**
	 * Ajoute une balise dans le manager et graphiquement
	 *
	 * @param sea
	 * @param memorySize
	 * @param startPos
	 * @param depl
	 */
	public void addBalise(JPanel sea, int memorySize, Point startPos, Deplacement depl) {
		Balise bal = new Balise(memorySize);
		bal.setPosition(startPos);
		bal.setDeplacement(depl);
		manager.addBalise(bal);
		GrBalise grbal = new GrBalise(this.ether);
		grbal.setModel(bal);
		sea.add(grbal);
		sea.setComponentZOrder(grbal, 0);
	}

	/**
	 * Supprime une balise dans le manager et graphiquement
	 */
	public void deleteBalise() {
		for (Component component : sea.getComponents()) {
			if (component instanceof GrBalise) {
				sea.remove(component);
				manager.deleteBalise(((GrBalise) component).getModel());
			}
			return;
		}
	}

	public void addSatelitte(JPanel sky, int memorySize, Point startPos, int vitesse) {
		Satellite sat = new Satellite(memorySize);
		sat.setPosition(startPos);
		sat.setDeplacement(new DeplSatellite(-10, 1000, vitesse));
		manager.addSatellite(sat);
		GrSatellite grSat = new GrSatellite(this.ether);
		grSat.setModel(sat);
		sky.add(grSat);
	}

	public void launch() {
		JLayeredPane main = new JLayeredPane();
		main.setOpaque(true);
		main.setSize(this.worldDim);

		this.ether.setBorder(null);
		this.ether.setBackground(Color.gray);
		this.ether.setOpaque(false);
		this.ether.setDimension(this.worldDim);

		NiRectangle sky = new NiRectangle();
		sky.setBackground(Color.white);
		sky.setDimension(new Dimension(this.worldDim.width, this.worldDim.height / 2));
		NiRectangle sea = new NiRectangle();
		sea.setBackground(Color.blue);
		sea.setDimension(new Dimension(this.worldDim.width, this.worldDim.height / 2));
		sea.setLocation(new Point(0, this.worldDim.height / 2));

		this.addSatelitte(sky, 100000, new Point(10, 50), 2);
		this.addSatelitte(sky, 100000, new Point(100, 10), 1);
		this.addSatelitte(sky, 100000, new Point(400, 90), 3);
		this.addSatelitte(sky, 100000, new Point(500, 140), 4);
		this.addSatelitte(sky, 100000, new Point(600, 10), 1);
		//this.addBalise(sea, 300, new Point(400, 200), new DeplHorizontal(50, 750));
		//this.addBalise(sea, 400, new Point(100, 100), new DeplVertical(50, 200));
		//this.addBalise(sea, 200, new Point(0, 160), new DeplHorizontal(0, 800));
		//this.addBalise(sea, 500, new Point(200, 100), new DeplVertical(130, 270));
		this.addBalise(sea, 150, new Point(300, 100), new DeplHorizontal(200, 600));
		main.add(sky, JLayeredPane.DEFAULT_LAYER);
		main.add(sea, JLayeredPane.DEFAULT_LAYER);
		main.add(this.ether, JLayeredPane.POPUP_LAYER);
		
		this.world.setLayout(new BoxLayout(this.world, BoxLayout.Y_AXIS));
		this.world.add(main);
		this.world.add(this.fpsSliderPanel());
		this.world.openInWindow();
		this.animation();
	}

	public static void main(String[] args) {

		new Simulation().launch();
	}

}
