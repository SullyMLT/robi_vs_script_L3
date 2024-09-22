package IHM;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
/**
 * Version 4
 * 
 */
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RobiScratch extends JFrame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415554528091620695L;
    JPanel zoneScriptsPanel; // DÃ©claration au niveau de la classe
    Dimension ogSize = new Dimension(200, 200);
 
	ScriptsAssembleur scriptsAssembleur;
	ScriptsInterpreteur scriptsInterpreteur;
	IHMController IHMController;
	JLabel imageHandler;
	JPanel mainPanel;
	private List<JPanel> boutonsSurLeScript = new ArrayList<JPanel>();
	private LinkedList<List<String>> scripts = new LinkedList<List<String>>();
	CloneBloc cloneButton;
	
	private JPanel zoneDuBloc = null;
	FonctionnaliteBloc foncButton = new FonctionnaliteBloc(this); //Menu clique droit
	JCheckBoxMenuItem scratch;
	JPanel boutonsPanel;
	JPanel panel_3;
	JMenuBar menuBar;
	int windowWidth = 800;
	int windowHeight = 550;
	
	String baseImage64 = "";
	
	private final Action action = new SwingAction();
	private JPanel spritesContainerPanel;
	HashMap<String, LinkedList<JPanel>> spritesScripts = new HashMap<String, LinkedList<JPanel>>();

	private JButton boutonSelected = null;
	private LinkedList<JButton> spritesBoutons = new LinkedList<JButton>();
	private JCheckBox pasMode;
	
	JPanel panel_5;
	HashMap<String, Boolean> reloadedFromSave = new HashMap<>();
	
	public RobiScratch() {
		
		setTitle("Scratch");
		scriptsAssembleur = new ScriptsAssembleur();
		scriptsInterpreteur = new ScriptsInterpreteur();
		setBoutonsSurLeScript(new ArrayList<JPanel>());
		
		// Centre la fenetre au millieu de l'ecran
		Toolkit toolKit = getToolkit();
		Dimension size = toolKit.getScreenSize();
		setLocation(size.width/2 - windowWidth/2, size.height/2 - windowHeight/2);
		setMinimumSize(new Dimension(windowWidth,windowHeight));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		/**
		 * Cr�ation du mainPanel qui contient tous les panels
		 */
		mainPanel = new JPanel();
		mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		mainPanel.setBackground(Color.BLUE);
		getContentPane().add(mainPanel);
		
		/**
		 * Cr�ation du zoneScriptsPanel qui contient les boutons ainsi que la zone pour coder
		 */
		zoneScriptsPanel = new JPanel();
		zoneScriptsPanel.setBounds(0, 0, 549, 483);
        zoneScriptsPanel.setBackground(Color.RED);
        zoneScriptsPanel.setLayout(null);
        zoneScriptsPanel.setPreferredSize(new Dimension(15000, 200));
		mainPanel.add(zoneScriptsPanel);

        //Conversion en plan scrollable 
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setAutoscrolls(true);
        scrollPane_2.setBounds(150, 0, 400, 511);
        zoneScriptsPanel.add(scrollPane_2);
        
        /**
         * Cr�ation de zoneDuBloc, enfant de zoneScriptsPanel, qui est la zone ou on creer le script
         */
        zoneDuBloc = new JPanel();
        zoneDuBloc.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane_2.setViewportView(zoneDuBloc);
        zoneDuBloc.setBackground(Color.LIGHT_GRAY);
        zoneDuBloc.setBounds(EXIT_ON_CLOSE, ABORT, WIDTH, HEIGHT);
        zoneDuBloc.setLayout(null);

		mainPanel.setLayout(null);
		
		/**
		 * Initialisation menu clique droit
		 */
        this.foncButton = new FonctionnaliteBloc(this); 
        this.foncButton.setBoutonsSurLeScript(getBoutonsSurLeScript());
       
        /**
         * Creation de boutonsPanel, enfant de zoneScriptsPanel, qui contient les boutons de construction de script
         */
        JPanel boutonsPanel = new JPanel();
        boutonsPanel.setBounds(0, 0, 150, 2200);
        zoneScriptsPanel.add(boutonsPanel);
        boutonsPanel.setBackground(Color.PINK);
        boutonsPanel.setForeground(Color.PINK);
        boutonsPanel.setLayout(null);
        
        /************************************************
         * Cr�ation des boutons de construction de script
         ************************************************/
        //Bouton start
        JPanel startBoutonRef = new JPanel();
        startBoutonRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBoutonRef.setBounds(0, 0, 147, 23);
        boutonsPanel.add(startBoutonRef);
        
        JLabel startLB = new JLabel("start");
        startLB.setHorizontalAlignment(SwingConstants.CENTER);
        startLB.setVerticalAlignment(SwingConstants.CENTER);
        startLB.setBounds(0, 0, 147, 23);
        startLB.setBackground(Color.WHITE);
        startBoutonRef.add(startLB);
        
        startLB.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		startBoutonRef.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		startBoutonRef.setBackground(Color.WHITE);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		JPanel startButtonClone = new JPanel();
        		//Creation d'un clone du bouton sleep
        		startButtonClone  = cloneButton.addBloc(startLB, startBoutonRef, zoneDuBloc, true, boutonSelected.getText());
        		mainPanel.revalidate();
        		mainPanel.repaint();
        	}
        });
        
        //Bouton End
        JPanel endBoutonRef = new JPanel();
        endBoutonRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        endBoutonRef.setBounds(0, 30, 147, 23);
        boutonsPanel.add(endBoutonRef);
        
        JLabel endLB = new JLabel("end");
        endLB.setHorizontalAlignment(SwingConstants.CENTER);
        endLB.setVerticalAlignment(SwingConstants.CENTER);
        endLB.setBounds(0, 0, 147, 23);
        endLB.setBackground(Color.WHITE);
        endBoutonRef.add(endLB);
        
        endLB.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		endBoutonRef.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		endBoutonRef.setBackground(Color.WHITE);
        	}
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		JPanel endButtonClone = new JPanel();
        		//Creation d'un clone du bouton sleep
        		endButtonClone  = cloneButton.addBloc(endLB, endBoutonRef, zoneDuBloc, true, boutonSelected.getText());
        		mainPanel.revalidate();
        		mainPanel.repaint();
        	}
        });
        
        //Bouton translate
        JPanel translateBoutonRef = new JPanel();
        translateBoutonRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        translateBoutonRef.setBounds(0, 60, 147, 23);
        boutonsPanel.add(translateBoutonRef);
        translateBoutonRef.setLayout(null);
        // Zone de texte Value 1
        JTextField translateTF1 = new JTextField();
        translateTF1.setText("10");
        translateTF1.setHorizontalAlignment(SwingConstants.CENTER);
        translateTF1.setBounds(77, 0, 30, 23);
        translateBoutonRef.add(translateTF1);
        translateTF1.setColumns(10);
        // Zone de texte Value 2
        JTextField translateTF2 = new JTextField();
        translateTF2.setText("10");
        translateTF2.setHorizontalAlignment(SwingConstants.CENTER);
        translateTF2.setBounds(117, 0, 30, 23);
        translateBoutonRef.add(translateTF2);
        translateTF2.setColumns(10);
        // Nom visible du bouton pour l'utilisateur
        JLabel translateName = new JLabel("translate");
        // centrage horizontale du text du label
        translateName.setHorizontalAlignment(SwingConstants.CENTER);
        // Emplacement et taille du label
        translateName.setBounds(0, 4, 79, 14);
        // ajout du label en tant que fils du panel parent du bouton
        translateBoutonRef.add(translateName);
        translateName.addMouseListener(new MouseAdapter(){
        	// Passe la couleur du bouton en jaune lorsqu'on passe la souris dessus le label du bouton
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		translateBoutonRef.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		translateBoutonRef.setBackground(Color.WHITE);
        	}
        	public void mouseClicked(MouseEvent e) {
        		JPanel cloneTranslateBouton = cloneButton.addBloc(translateName, translateBoutonRef, zoneDuBloc, true, boutonSelected.getText());
        		mainPanel.revalidate();
        		mainPanel.repaint();
        	}
        });
        
        //Bouton setColorButton
        JPanel setColorButtonRef = new JPanel();
        setColorButtonRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Localisation en 0, 60 de taille 147, 23
        setColorButtonRef.setBounds(0, 90, 147, 23);
        boutonsPanel.add(setColorButtonRef);
        setColorButtonRef.setLayout(null);
        
        // Ajout d'un comboBox a l'emplacement 70, 0 de taille 67, 22
        JComboBox setColorButtonSeletor = new JComboBox();
        setColorButtonSeletor.setToolTipText("");
        setColorButtonSeletor.setBounds(70, 0, 67, 22);
        String red = "red";
		String white = "white";
		String green = "green";
		String black = "black";
		String blue = "blue";
		// Ajout des textes des couleurs dans la comboBox
		setColorButtonSeletor.addItem(red);
		setColorButtonSeletor.addItem(white);
		setColorButtonSeletor.addItem(green);
		setColorButtonSeletor.addItem(black);
		setColorButtonSeletor.addItem(blue);
        setColorButtonRef.add(setColorButtonSeletor);
        JLabel setColorNameLB = new JLabel("setColor ");
        setColorNameLB.setHorizontalAlignment(SwingConstants.CENTER);
        setColorNameLB.setBounds(0, 0, 72, 22);
        setColorButtonRef.add(setColorNameLB);
        
        setColorNameLB.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		setColorButtonRef.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		setColorButtonRef.setBackground(Color.WHITE);
        	}
        	public void mouseClicked(MouseEvent e) {
        		JPanel cloneSetColorBouton = cloneButton.addBloc(setColorNameLB, setColorButtonRef, zoneDuBloc, true, boutonSelected.getText());
        		mainPanel.revalidate();
        		mainPanel.repaint();
        	}
        });
        
        //Bouton setDim
        JPanel setDimBoutonRef = new JPanel();
        setDimBoutonRef.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Met a l'emplacement 0, 90 de taille 147 en X et 23 en Y sur l'ihm
        setDimBoutonRef.setBounds(0, 120, 147, 23);
        boutonsPanel.add(setDimBoutonRef);
        setDimBoutonRef.setLayout(null);
        JTextField setDimTF1 = new JTextField();
        setDimTF1.setText("200");
        setDimTF1.setHorizontalAlignment(SwingConstants.CENTER);
        setDimTF1.setBounds(77, 0, 30, 23);
        setDimBoutonRef.add(setDimTF1);
        setDimTF1.setColumns(10);
        JTextField setDimTF2 = new JTextField();
        setDimTF2.setText("200");
        setDimTF2.setHorizontalAlignment(SwingConstants.CENTER);
        setDimTF2.setBounds(117, 0, 30, 23);
        setDimBoutonRef.add(setDimTF2);
        setDimTF2.setColumns(10);
        JLabel setDimName = new JLabel("setDim");
        setDimName.setHorizontalAlignment(SwingConstants.CENTER);
        setDimName.setBounds(0, 4, 79, 14);
        setDimBoutonRef.add(setDimName);
        setDimName.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		setDimBoutonRef.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		setDimBoutonRef.setBackground(Color.WHITE);
        	}
        	public void mouseClicked(MouseEvent e) {
        		JPanel cloneSetDimBouton = cloneButton.addBloc(setDimName, setDimBoutonRef, zoneDuBloc, true, boutonSelected.getText());
        		mainPanel.revalidate();
        		mainPanel.repaint();
        	}
        });
        //--------- FIN DECLARATION DES BOUTONS SCRIPT ---------
        
        /**
         * Menu clique droit sur les boutons clones
         */
        this.foncButton.setBoutonsSurLeScript(getBoutonsSurLeScript());
        this.foncButton.setParentPanel(zoneDuBloc);
    	cloneButton = new CloneBloc(this, this.foncButton);

        /**
         * Creation du panel_3, parent de 2 panel (4 & 5).
         */
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(547, 0, 237, 483);
		panel_3.setBackground(new Color(196, 196, 196));
		mainPanel.add(panel_3);
		panel_3.setLayout(null);
		
		/**
		 * Creation du panel_4, qui correspond a l'ecran d'affichage.
		 */
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 0, 217, 241);
		panel_4.setBackground(new Color(128, 0, 0));
		panel_3.add(panel_4);
		panel_4.setLayout(null);
		
		imageHandler = new JLabel("");
		imageHandler.setBounds(293, 0, 224, 208);
		imageHandler.setHorizontalAlignment(SwingConstants.CENTER);
		imageHandler.setIcon(new ImageIcon("H:\\git\\RobiVSScript\\Robi\\alien.gif"));
		panel_4.add(imageHandler);
		
		JPanel panel = new JPanel();
		panel.setBounds(293, 208, 224, 33);
		panel_4.add(panel);
		/*
		 * Bouton de gestion de l'animation
		 */
		JButton Debut = new JButton("Debut");
		Debut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Debut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IHMController.imageDebut();
			}
		});
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(Debut);

		JButton avantBouton = new JButton("<");
		avantBouton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		avantBouton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IHMController.imagePrecedente();
			}
		});
		panel.add(avantBouton);
		
		JButton suivantBouton = new JButton(">");
		suivantBouton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		suivantBouton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IHMController.imageSuivante();
			}
		});
		panel.add(suivantBouton);
		
		JButton btnNewButton_1 = new JButton("Fin");
		btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				IHMController.imageFin();
			}
		});
		panel.add(btnNewButton_1);
		
		
		
		JButton btnNewButton_2 = new JButton("Demarrer");
		btnNewButton_2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton_2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				//Met à jour les scripts actuels
				saveButtonSprite();
				//R�cup�ration de tous les scripts
				List<LinkedList<JPanel>> tousLesScripts = new ArrayList<LinkedList<JPanel>>();
				List<String> scriptEnString = new ArrayList<String>();
				//formation de toutes les chaînes de caractères
				for (String id : spritesScripts.keySet()) {
					tousLesScripts.add(spritesScripts.get(id));
					List<LinkedList<JPanel>> scriptsAssembles = scriptsAssembleur.CreerScripts(spritesScripts.get(id));
					scriptEnString = scriptsInterpreteur.InterpreterScripts(scriptsAssembles, id);
				}
				
				String script = scriptsInterpreteur.createFinalScript();
				System.out.println("final script " + script);
				if (IHMController.isConnected()) {
					IHMController.EnvoyerScriptsAuServeur(script, pasMode.isSelected());
				} else {
					System.out.println("Non connecté");
				}
				

			}
		});
		panel.add(btnNewButton_2);
		
		pasMode = new JCheckBox("Step");
		panel.add(pasMode);
		//---------FIN Bouton de gestion de l'animation---------
		/**
		 * Creation du panel_5, parent du panel qui gere les sprites.
		 */
		panel_5 = new JPanel();
		panel_5.setBounds(new Rectangle(5, 0, 0, 0));
		panel_5.setBounds(0, 241, 227, 241);
		panel_5.setBackground(new Color(128, 128, 192));
		panel_5.setLayout(null);
		panel_3.add(panel_5);
		
		/**
		 * Gestion des tailles des component du panel_3
		 */
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Rectangle r = mainPanel.getBounds();
				
				//Refonte de zoneScript
				zoneScriptsPanel.setBounds(0, 0, r.width/2, r.height);
				scrollPane_2.setBounds(150, 0, r.width/2-150, r.height);
				panel_3.setBounds(r.width/2, 0, r.width/2, r.height);
				panel_4.setBounds(0, 0, r.width/2, r.height/2);
				panel.setBounds(0, r.height/2-panel.getBounds().height,r.width/2, panel.getBounds().height);
				imageHandler.setBounds(0, 0, r.width/2, r.height/2-panel.getHeight());
				panel_5.setBounds(r.width/2, r.height/2, r.width/2, r.height/2);
				mainPanel.setComponentZOrder(panel_5, 0);
				changeImageHandler(baseImage64);
				//spritesContainerPanel.setBounds(r.width/2, r.height-spritesContainerPanel.getBounds().height, spritesContainerPanel.getBounds().width, spritesContainerPanel.getHeight());
				
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});
		
		/**
		 * Panel contenant les sprites
		 */
		spritesContainerPanel = new JPanel();
		spritesContainerPanel.setBounds(10, 210, 67, 31);
		spritesContainerPanel.setBackground(new Color(181, 181, 219));
		panel_5.add(spritesContainerPanel);

		//Bouton + qui ajoute des sprites
		JButton addObjectBTN = new JButton("+");
		addObjectBTN.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addObjectBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cloneButton.addObjet(panel_5, zoneDuBloc);
			}
		});
		spritesContainerPanel.add(addObjectBTN);
		
		JButton btnNewButton_3 = new JButton("Space");
		setBoutonSelected(btnNewButton_3);
		addSpriteBouton(btnNewButton_3);
		boutonSelected = btnNewButton_3;
		btnNewButton_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setBoutonSelected(btnNewButton_3);
				refreshMainPanel();
			}
		});
		btnNewButton_3.setBounds(128, 11, 89, 23);
		panel_5.add(btnNewButton_3);
		
	/*
	 * MENU ----------------
	 * */
	/**
	 * Implementation du menu en haut avec File, Mode de saisi et Info.
	 */
	menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	JMenu File = new JMenu("File");
	menuBar.add(File);

	JMenuItem open = new JMenuItem("Open");
	open.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("sauvegarde de fichier");
			JFileChooser exploFichier = new JFileChooser();
			exploFichier.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
			int valeurRetour = exploFichier.showOpenDialog(null);
			if (valeurRetour == JFileChooser.APPROVE_OPTION) {
				java.io.File fichierSelection = exploFichier.getSelectedFile();
				IHMController.loadFileScratch(fichierSelection.getAbsolutePath());
			} else {
				System.out.println("Aucun fichier sauvegarde");
			}
		}
	});
	File.add(open);

	JMenuItem save = new JMenuItem("Save");
	save.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        JFileChooser exploFichier = new JFileChooser();
	        //Mise à jour
	        
	    	spritesScripts.put(boutonSelected.getText(), new LinkedList<JPanel>(boutonsSurLeScript));
	        exploFichier.setCurrentDirectory(new java.io.File(System.getProperty("user.home") + "/Desktop"));
	        int valeurRetour = exploFichier.showSaveDialog(null);
	        if (valeurRetour == JFileChooser.APPROVE_OPTION) {
	            java.io.File fichierSauvegarde = exploFichier.getSelectedFile();
	            String nomFichier = fichierSauvegarde.getName();
				IHMController.saveFileScratch(fichierSauvegarde.getAbsolutePath());
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
	Info.setMaximumSize(new Dimension(100, 32767));
	Info.setLayout(new BorderLayout());
	
	Info.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Creation et affichage de la popup avec les informations
			JOptionPane.showMessageDialog(RobiScratch.this, "Version : 1.0\n\n" + "Auteur :\n"
					+ "- Mathias DESOYER\n" + "- Matheo GUENEGAN\n" + "- Axel LE FAUCHEUR\n" + "- Sully MILLET\n\n\n"
					+ "Description :\n\n" + "Cette Application est utilise pour envoye des scripts a� un serveur\n"
					+ "qui renvoie le resultat de son execution en affichant dans un\n"
					+ "emplacement dedier en haut a droite.\n\n"
					+ "Deux modes sont disponible afin de creer les scripts a� envoyer :\n"
					+ "- Scratch est le mode disponible comme la vrai application disponible sur internet.\n"
					+ "- Code a� la main est le mode ou l'on rentre le scripts a� le main de la maniere suivante\n"
					+ "( '(space setColor black)' ) space etant un objet, setColor le fait de vouloir changer la\n"
					+ "couleur et black la couleur choisi.");
		}
	});
	menuBar.add(Info);
	scratch = new JCheckBoxMenuItem("Scratch / code main");
	scratch.setSelected(true);
	scratch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Verifie si le JCheckBoxMenuItem scratch est selectionne
			if (!scratch.isSelected()) {
				scratch.setSelected(true);
				IHMController.afficherCoderMain();
			}
		}
	});
	mode.add(scratch);
		
	}

	
	/**
	 * Fonction qui permet de recalculer la taille de zoneScriptsPanel et qui permet donc d'attendre le panel
	 */
	public void adjustPreferredSize() {
	    for (Component comp : zoneDuBloc.getComponents()) {
	    	ogSize.width = Math.max(ogSize.width, comp.getX() + comp.getWidth());
	    	ogSize.height = Math.max(ogSize.height, comp.getY() + comp.getHeight());
	    }
	    // Utilisez minSize pour mettre a jour ogSize et la taille prefere
	    zoneDuBloc.setPreferredSize(ogSize);
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}
	
	/**
	 * D�finit l'instance du contr�leur de l'IHM � utiliser.
	 *
	 * @param instance L'instance du contr�leur de l'IHM � d�finir.
	 */
	public void setIHMController(IHMController instance) {
	    this.IHMController = instance;
	}

	/**
	 * Change l'image affich�e dans le composant imageHandler en fonction de l'image encod�e en base64 sp�cifi�e.
	 * La m�thode ajuste la taille de l'image en fonction de la taille actuelle du composant imageHandler.
	 *
	 * @param base64Image L'image encod�e en base64 � afficher.
	 */
	public void changeImageHandler(String base64Image) {
		baseImage64 = base64Image;
	    int labelHeight = imageHandler.getHeight();
	    int labelWidth = imageHandler.getWidth();
	    ImageIcon scaledImageIcon = IHMController.decodeBase64ToScaledImageIcon(base64Image, labelWidth, labelHeight); // Ajuster la largeur et la hauteur si n�cessaire
	    
	    if (scaledImageIcon != null) {
	        imageHandler.setIcon(scaledImageIcon);
	    } else {
	        imageHandler.setText("�chec du d�codage de l'image en base64.");
	    }

	    mainPanel.revalidate();
	    mainPanel.repaint();
	}

	/**
	 * Obtient la liste des panneaux de blocs de scripts.
	 *
	 * @return La liste des panneaux de blocs de scripts.
	 */
	public List<JPanel> getBoutonsSurLeScript() {
	    return boutonsSurLeScript;
	}
	
	public Map<String, LinkedList<JPanel>> getSpritesScripts() {
		return spritesScripts;
	}

	/**
	 * D�finit la liste des panneaux de blocs de scripts.
	 *
	 * @param boutonsSurLeScript La liste des panneaux de blocs de scripts � d�finir.
	 */
	public void setBoutonsSurLeScript(List<JPanel> boutonsSurLeScript) {
	    this.boutonsSurLeScript = boutonsSurLeScript;
	}

	/**
	 * Charge les blocs de script repr�sent�s par une liste de panneaux de blocs dans la fen�tre.
	 *
	 * @param jpanelToLoad La liste de panneaux de blocs � charger.
	 */
	public void loadBlocs(LinkedList<JPanel> jpanelToLoad, boolean isBoutonSelected, String nomSprite) {
		
	    for (JPanel panel : jpanelToLoad) {
	        JLabel nom = (JLabel) panel.getComponent(panel.getComponentCount() - 1);
	        JPanel p = cloneButton.addBloc(nom, panel, zoneDuBloc, isBoutonSelected, nomSprite);
	        p.setLocation(panel.getLocation());
	       
	    }
	}

//	public void addObjetFromLoad(JPanel panelDesButtons, String nomBTN)
	public void loadObject(String nom) {
		cloneButton.addObjetFromLoad(panel_5, nom);
	}
	
	public void loadAllObject(Map<String, LinkedList<JPanel>> map) {
		reloadedFromSave = new HashMap<>();
		for(String str: map.keySet()) {
			if (!str.equals("Space")) {
				this.cloneButton.addObjetFromLoad(panel_5, str);
			}
			LinkedList<JPanel> tmp = map.get(str);
			reloadedFromSave.put(str, false);
			loadBlocs(tmp, false, str);
		}
		spritesScripts.putAll(map);
	}
	
	public void deleteObjButton() {
		int nbChild = panel_5.getComponentCount();
		for(int i = nbChild-1; i>0; i--) {
			if (panel_5.getComponent(i) instanceof JButton) {
				JButton btn = (JButton)panel_5.getComponent(i);
				if (btn.getText().contains("obj")) {
					panel_5.remove(panel_5.getComponent(i));
				}
			}
		}
		refreshMainPanel();
	}

	/**
	 * Actualise le panneau principal de l'interface utilisateur.
	 * Cette m�thode doit �tre appel�e apr�s toute modification du contenu du panneau principal.
	 */
	public void refreshMainPanel() {
	    mainPanel.revalidate();
	    mainPanel.repaint();
	}
	
	public void addSpriteBouton(JButton bouton) {
		this.spritesBoutons.add(bouton);
	}
	
	public void setBoutonSelected(JButton _boutonSelected) {
		if (this.boutonSelected != null) {
			this.boutonSelected.setBackground(new Color(240,240,240));
			spritesScripts.put(this.boutonSelected.getText(), new LinkedList<JPanel>(boutonsSurLeScript));
			
			boutonsSurLeScript.clear();
			zoneDuBloc.removeAll();
		}
		this.boutonSelected = _boutonSelected;
		LinkedList<JPanel> jp = spritesScripts.get(this.boutonSelected.getText());
		if (jp != null ) {
			this.setBoutonsSurLeScript(jp);
			for (JPanel p : jp) {
				zoneDuBloc.add(p);
				if (!reloadedFromSave.get(this.boutonSelected.getText())) {
					this.cloneButton.addMouseListeners((JLabel) p.getComponent(p.getComponentCount()-1), zoneDuBloc);
				}
			}
			reloadedFromSave.put(this.boutonSelected.getText(), true);
		}
		refreshMainPanel();
		this.boutonSelected.setBackground(new Color(150,150,150));
		
	}
	

	/**
	 * Action Swing g�n�rique utilis�e par certaines fonctionnalit�s.
	 */
	private class SwingAction extends AbstractAction {
	    public SwingAction() {
	        putValue(NAME, "SwingAction");
	        putValue(SHORT_DESCRIPTION, "Une br�ve description");
	    }

	    public void actionPerformed(ActionEvent e) {
	    }
	}
	
	/**
	 * Launch the application.
	 */	
	public void removeFromBoutonsSurLeScript(JPanel compo) {
		this.boutonsSurLeScript.remove(compo);
	}
	
	public void removeAllFromBoutonsSurLeScript() {
		this.boutonsSurLeScript.clear();
	}
	
	public void saveButtonSprite() {
		spritesScripts.put(boutonSelected.getText(), new LinkedList<JPanel>(boutonsSurLeScript));
	}
	
	public void resetSpritesBoutons() {
		spritesBoutons.clear();
	}
	
	public List<JButton> getSpritesBoutons(){
		return this.spritesBoutons;
	}
	
	public JPanel getZoneDuBloc() {
		return this.zoneDuBloc;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RobiScratch frame = new RobiScratch();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}