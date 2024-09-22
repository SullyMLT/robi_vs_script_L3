package IHM;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.DropMode;
import javax.swing.JScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.text.Utilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseMotionAdapter;


/**
 * Interface pour coder à la main
 */
public class RobiCoderALaMain extends JFrame {

	private JPanel contentPane;
	JCheckBoxMenuItem scratch;
	JMenuBar menuBar;
	
	/**
	 * Rï¿½fï¿½rence ï¿½ l'instance IHMController
	 */
	IHMController IHMController;
	
	
	JLabel imageHandler;
	String baseImage64 = "";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RobiCoderALaMain frame = new RobiCoderALaMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Instance de la largeur de la fenï¿½tre
	 */
	int windowWidth = 800;
	/**
	 * Instance de la hauteur de la fenï¿½tre
	 */
	int windowHeight = 550;
	private JTextArea scriptZoneTextArea;
	/**
	 * Create the frame.
	 */
	public RobiCoderALaMain() {
		setTitle("Coder a la main");
		// Centre la fenï¿½tre au millieu de l'ï¿½cran
		Toolkit toolKit = getToolkit();
		Dimension size = toolKit.getScreenSize();
		setLocation(size.width/2 - windowWidth/2, size.height/2 - windowHeight/2);
		
		setMinimumSize(new Dimension(windowWidth,windowHeight));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel);
		mainPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panelPourScript = new JPanel();
		panelPourScript.setBackground(Color.YELLOW);
		mainPanel.add(panelPourScript);
		panelPourScript.setLayout(new BorderLayout(0, 0));
		
		JPanel zoneScript = new JPanel();
		zoneScript.setBackground(new Color(225, 225, 225));
		panelPourScript.add(zoneScript);
		zoneScript.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 212, 199);
		zoneScript.add(scrollPane);
		
		scriptZoneTextArea = new JTextArea();
		scrollPane.setViewportView(scriptZoneTextArea);
		
		JPanel panelPourScriptLabel = new JPanel();
		panelPourScript.add(panelPourScriptLabel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Coder \u00E0 la main");
		panelPourScriptLabel.add(lblNewLabel);
		
		JPanel affichageActionsPanel = new JPanel();
		mainPanel.add(affichageActionsPanel);
		affichageActionsPanel.setLayout(new BoxLayout(affichageActionsPanel, BoxLayout.Y_AXIS));
		
		JPanel affichagePanel = new JPanel();
		affichagePanel.setBackground(new Color(182, 172, 163));
		affichageActionsPanel.add(affichagePanel);
		affichagePanel.setLayout(null);
		
		imageHandler = new JLabel("");
		imageHandler.setBounds(0, 0, 387, 236);
		imageHandler.setHorizontalAlignment(SwingConstants.CENTER);
		imageHandler.setIcon(new ImageIcon("H:\\git\\RobiVSScript\\Robi\\alien.gif"));
		affichagePanel.add(imageHandler);
		
		JPanel actionPanel = new JPanel();
		actionPanel.setBackground(new Color(184, 184, 220));
		affichageActionsPanel.add(actionPanel);
		actionPanel.setLayout(null);
		
		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
	            /*if (!source.getValueIsAdjusting()) { */
	                int selectedIndex = (int)source.getValue();
	                IHMController.imageSlider(selectedIndex);
	            //}
			}
		});
		slider.setBounds(6, 33, 200, 26);
	    slider.setMinimum(0);
		actionPanel.add(slider);
		slider.setEnabled(false);
		
		JCheckBox pasPasMode = new JCheckBox("Pas a Pas");

		pasPasMode.setBounds(28, 5, 71, 23);
		actionPanel.add(pasPasMode);
		
		JButton precedentBouton = new JButton("Precedent");
		precedentBouton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IHMController.imagePrecedente();
				slider.setValue(IHMController.indexImageAffiche);
			}
		});
		precedentBouton.setBounds(25, 64, 81, 23);
		actionPanel.add(precedentBouton);
		
		precedentBouton.setEnabled(false);
		
		JButton suivantBouton = new JButton(" Suivant ");
		suivantBouton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IHMController.imageSuivante();
				slider.setValue(IHMController.indexImageAffiche);
			}
		});
		suivantBouton.setBounds(111, 64, 75, 23);
		actionPanel.add(suivantBouton);
	
		suivantBouton.setEnabled(false);
		
		JButton execBouton = new JButton("Execution");
		execBouton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				String text = scriptZoneTextArea.getText();
				boolean modePasAPas = pasPasMode.isSelected();
				
				if (isScriptReadable(text)) {
					IHMController.EnvoyerScriptsAuServeur(text, modePasAPas);
					// Permet d'activer le slider ou non lorsque le mode pas a pas est sÃ©lectionnÃ©
					if (modePasAPas) {
						slider.setEnabled(true);
						precedentBouton.setEnabled(true);
						suivantBouton.setEnabled(true);
					}else {
						slider.setEnabled(false);
						precedentBouton.setEnabled(false);
						suivantBouton.setEnabled(false);
					}
					// DÃ©fini le nombre d'image dans le slider (le nombre de cran)
					slider.setMaximum(IHMController.getImage64().size() - 1);
				}else {
				}
			}
		});
		execBouton.setBounds(104, 5, 79, 23);
		actionPanel.add(execBouton);
		/*
		 * MENU -----------------
		 * */
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu File = new JMenu("File");
		menuBar.add(File);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CrÃ©er un explorateur de fichier
				JFileChooser exploFichier = new JFileChooser();
				// Met l'emplacement de l'ouverture de l'explorateur de fichier sur le bureau
				exploFichier.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
				// Affiche l'explorateur de fichier
				int valeurRetour = exploFichier.showOpenDialog(null);

				// Si l'utilisateur sÃ©lectionne un fichier
				if (valeurRetour == JFileChooser.APPROVE_OPTION) {
					// RÃ©cupÃ©rer le fichier sÃ©lectionnÃ©
					java.io.File fichierSelection = exploFichier.getSelectedFile();
					IHMController.loadFileWritable(fichierSelection.getAbsolutePath());
				} else {
					System.out.println("Aucun fichier sÃ©lectionnÃ©");
				}
			}
		});
		File.add(open);

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser exploFichier = new JFileChooser();
		        exploFichier.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
		        int valeurRetour = exploFichier.showSaveDialog(null);
		        if (valeurRetour == JFileChooser.APPROVE_OPTION) {
		            java.io.File fichierSauvegarde = exploFichier.getSelectedFile();
					IHMController.saveFileWritable(fichierSauvegarde.getAbsolutePath());
		        } else {
		            System.out.println("Annulation de la sauvegarde");
		        }
		    }
		});
		File.add(save);

		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			// Lorsque l'on choisi le bouton Quit cela ferme la page
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		File.add(quit);

		JMenu mode = new JMenu("Mode de saisi");
		menuBar.add(mode);

		JMenuItem Info = new JMenuItem("Infos");
		Info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CrÃ©ation et affichage de la popup avec les informations
				JOptionPane.showMessageDialog(RobiCoderALaMain.this, "Version : 1.0\n\n" + "Auteur :\n"
						+ "- Mathias DESOYER\n" + "- Mathéo GUENEGAN\n" + "- Axel LE FAUCHEUR\n" + "- Sully MILLET\n\n\n"
						+ "Description :\n\n" + "Cette Application est utilisée pour envoyer des scripts à un serveur\n"
						+ "qui renvoie le résultat de son exécution en affichant dans un\n"
						+ "emplacement dédier en haut à droite.\n\n"
						+ "Deux modes sont disponibles afin de créer les scripts à envoyer :\n"
						+ "- Scratch est le mode disponible comme la vraie application disponible sur internet.\n"
						+ "- Coder à la main est le mode où l'on rentre le script à le main de la manière suivante\n"
						+ "( '(space setColor black)' ) space étant un objet, setColor le fait de vouloir changer la\n"
						+ "couleur et black la couleur choisie.");
			}
		});
		menuBar.add(Info);
		//*****************************************************//
		//	             CodÃ© Ã  la main                    //
		//*****************************************************//
		
		
			// Ajout de la modification de la taille de la police
		
		scratch = new JCheckBoxMenuItem("Scratch / codÃ© main");
		//scratch.setSelected(false);
		scratch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// VÃ©rifie si le JCheckBoxMenuItem scratch est sÃ©lectionnÃ©
				
				if (scratch.isSelected()) {
					scratch.setSelected(false);
					IHMController.afficherCoderScratch();
				}
			}
		});
		mode.add(scratch);
	
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Rectangle r = mainPanel.getBounds();
				int scaledWidth = r.width - r.width/2;
				scrollPane.setBounds(0, 0, scaledWidth, r.height-25);
				//imageHandler.setBounds(0, 0, r.width/2, r.height/2);
				imageHandler.setBounds(affichagePanel.getBounds());
				changeImageHandler(baseImage64);
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});
	}
	
	/**
	 * Set l'instance de IHM controllï¿½ (appelï¿½ une fois au dï¿½but de l'initialisation)
	 * @param instance l'instance
	 */
	public void setIHMController(IHMController instance) {
		this.IHMController = instance;
	}
	
	public void changeImageHandler(String _base64Image) {
		baseImage64 = _base64Image;
		int labelWidth = imageHandler.getWidth();
		int labelHeight = imageHandler.getHeight();
		ImageIcon scaledImageIcon = IHMController.decodeBase64ToScaledImageIcon(baseImage64, labelWidth, labelHeight); // Adjust width and height as needed
		
		if (scaledImageIcon != null) {
            imageHandler.setIcon(scaledImageIcon);
        } else {
            imageHandler.setText("Failed to decode base64 image.");
        }
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	public String getScriptWrote() {
		return scriptZoneTextArea.getText();
	}
	
	public void setScriptWrote(String script) {
		scriptZoneTextArea.setText(script);
	}
	
	
	/**
	 * Vï¿½rifie que le script peut bien ï¿½tre lancï¿½ (pour ï¿½viter les crash)
	 * @return
	 */
	//TODO faire un descripteur d'erreur
	public boolean isScriptReadable(String str) {
		String[] strs = str.split("\n");
		
		for (int i = 0; i < strs.length; i++) {
			String currentStr = strs[i];
			if (currentStr.charAt(0) != '(' || currentStr.charAt(currentStr.length()-1) != ')') {
				return false;
			}
		}
		return true;
	}
}

