package simulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Balise;
import model.DeplHorizontal;
import model.DeplSatellite;
import model.Deplacement;
import model.Manager;
import model.Satellite;
import nicellipse.component.NiImage;
import nicellipse.component.NiSpace;
import views.GrBalise;
import views.GrEther;
import views.GrSatellite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.Box;

/**
 * Classe Simulation pour lancer l'application.
 * @author ELENGA Alphonse Junior & OUAKSEL boukhalfa
 * @version 1.0
 */
public class Simulation {
	final int FPS_MIN = 2;
	final int FPS_MAX = 500;
	final int FPS_INIT = 70;
	final int startDelay = 500 / FPS_INIT;
	Timer animation;
	Manager manager = new Manager();
	Dimension worldDim = new Dimension(900, 700);
	NiSpace world = new NiSpace("Satellite & AirTag", this.worldDim);
	GrEther ether = new GrEther();
	NiImage sea = new NiImage(new File("sea.png"));
	NiImage sky = new NiImage(new File("sky.jpg"));

	public Simulation() throws IOException {
	}

	public void animation() {
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				manager.tick();
				ether.repaint();
			}
		};
		this.animation = new Timer(this.startDelay, taskPerformer);
		this.animation.setRepeats(true);
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
		JButton start = new JButton("Demarer");
		JButton stop = new JButton("Stopper");
		JButton addBalise = new JButton("AirTag +1");
		JButton delBalise = new JButton("AirTag -1");
		JButton addSatellite = new JButton("Sat +1");
		JButton delSatellite = new JButton("Sat -1");
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
	 * Ajout  d'une balise dans le manager et graphiquement.
	 * @param sea La mer.
	 * @param memorySize La mémoire de la balise.
	 * @param startPos Position de départ.
	 * @param depl déplacement de la balise ajouté.
	 */
	public void addBalise(NiImage sea, int memorySize, Point startPos, Deplacement depl) {
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
	 * Supprime une balise dans la mer par le manager et graphiquement
	 */
	public void deleteBalise() {
		if(sea.getComponents().length<=1) return;
		for (Component component : sea.getComponents()) {
				sea.remove(component);
				manager.deleteBalise(((GrBalise) component).getModel());
			return;
		}
	}

	/**
	 * Ajout  d'une balise dans le manager et graphiquement.
	 * @param sky Le ciel.
	 * @param memorySize La mémoire du satellite.
	 * @param startPos Position de départ.
	 * @param vitesse vitesse du satellite que l'on ajoute.
	 */
	public void addSatelitte(NiImage sky, int memorySize, Point startPos, int vitesse) {
		Satellite sat = new Satellite(memorySize);
		sat.setPosition(startPos);
		sat.setDeplacement(new DeplSatellite(-10, 1000, vitesse));
		manager.addSatellite(sat);
		GrSatellite grSat = new GrSatellite(this.ether);
		grSat.setModel(sat);
		sky.add(grSat);
	}

	/**
	 * Supprime un satellite dans le ciel par le manager le ciel
	 */
	public void deleteSatellite() {
		if(sky.getComponents().length<=1) return;
		for (Component component : sky.getComponents()) {
				sky.remove(component);
				manager.deleteSatellite(((GrSatellite) component).getModel());
			return;
		}
	}

	/**
	 * Lancement de l'application
	 */
	public void launch() throws IOException {
		JLayeredPane main = new JLayeredPane();
		main.setOpaque(true);
		main.setSize(this.worldDim);

		this.ether.setBorder(null);
		this.ether.setBackground(Color.gray);
		this.ether.setOpaque(false);
		this.ether.setDimension(this.worldDim);

		sky.setDimension(new Dimension(this.worldDim.width, this.worldDim.height / 2));
		//ajout de bordure autour du ciel
		sky.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, Color.black));
		sea.setBackground(Color.blue);
		sea.setDimension(new Dimension(this.worldDim.width, this.worldDim.height / 2));
		sea.setLocation(new Point(0, this.worldDim.height / 2));

		this.addSatelitte(sky, 100000, new Point(10, 50), 2);
		this.addSatelitte(sky, 100000, new Point(100, 10), 1);
		this.addSatelitte(sky, 100000, new Point(400, 90), 3);
		this.addSatelitte(sky, 100000, new Point(500, 140), 4);
		this.addSatelitte(sky, 100000, new Point(600, 10), 1);
		this.addBalise(sea, 90, new Point(300, 100), new DeplHorizontal(200, 600));
		main.add(sky, JLayeredPane.DEFAULT_LAYER);
		main.add(sea, JLayeredPane.DEFAULT_LAYER);
		main.add(this.ether, JLayeredPane.POPUP_LAYER);

		this.world.setLayout(new BoxLayout(this.world, BoxLayout.Y_AXIS));
		this.world.add(main);
		this.world.add(this.fpsSliderPanel());
		this.world.add(this.buttonPanel());
		this.world.openInWindow();
		this.animation();
	}

	public static void main(String[] args) throws IOException {
		new Simulation().launch();
	}

}
