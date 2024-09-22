package IHM;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import java.awt.Scrollbar;
import javax.swing.JScrollBar;

/**
 * Pour partie scratch
 * Clone un bloc de A � Z et l'ajoute au 
 */
public class CloneBloc extends JFrame{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8525237482143957789L;

	private RobiScratch instanceController;

	private FonctionnaliteBloc fonctionnaliteButton;
	
	/**
	 * Instance des coordonnées X courantes du futur bouton add Object. 
	 */
	int locX = 5;
	/**
	 * Instance des coordonnées Y courantes du futur bouton add Object. 
	 */
	int locY = 5;
	/**
	 * Instance des ID des nouveau object créer par la fonction addObjet. 
	 */
	public int id = 1;

	// Dans CloneButtonV3
	public CloneBloc(RobiScratch robiScratchV4, FonctionnaliteBloc fonc){
	    this.instanceController = robiScratchV4;
	    // Assurez-vous que instanceController.zoneScriptsPanel est d�j� initialis� � ce point
	    this.fonctionnaliteButton = fonc;
	}


	/* ----A DECOMMENTER POUR UTILISER AVEC V2 && COMMENTER CELUI DU DESSUS----
	 * private RobiScratchV2 instanceController;

	public CloneButtonV2 (RobiScratchV2 instanceController){
		this.instanceController = instanceController;
		
	}*/
	
	
	private void AssembleElements() {
		List<LinkedList<JPanel>> scriptsNonInterprete = instanceController.scriptsAssembleur.CreerScripts(instanceController.getBoutonsSurLeScript());
		//instanceController.scriptsInterpreteur.InterpreterScripts(scriptsNonInterprete);
	}
	
	private void AssembleElementsSansInterpreter() {
		instanceController.scriptsAssembleur.CreerScripts(instanceController.getBoutonsSurLeScript());
	}

	/**
	 * FONCTIONS SUR LES BOUTONS
	 */
	
	/**
	 * D�place un �lement clone sur le JPanel de r�f�rence
	 * @param e la souris
	 * @param clone le JPanel actuellement d�plac�
	 * @param panelRef le panel de r�f�rence (g�n�ralement zoneScriptsPa
	 */
	
	/*public void mouseDraggedClonedButtons(MouseEvent e, JPanel clone, JPanel panelRef) {
	    Point ptn = SwingUtilities.convertPoint(clone, e.getX() - 40, e.getY(), panelRef);
	    clone.setLocation(ptn);
	}*/

	
	/**
	 * Duplique un bouton et l'ajoute sur la zone de script.
	 * 
	 * @param nomButton        le nom du bouton (ex : sleep; translate, etc).
	 * @param panelButton      le panel parent du bouton.
	 * @param zoneScriptsPanel le panel contenant les boutons clones.
	 * @return retourne le nouveau clone creer à partir du bloc transmit.
	 */
	public JPanel addBloc(JLabel nomButton, JPanel panelButton, JPanel zoneScriptsPanel, boolean isBoutonSelected, String nomSprite) {
		int nbChild = panelButton.getComponentCount();

		JPanel clone = new JPanel();
		Dimension dimClone = new Dimension(panelButton.getSize());
		clone.setLayout(null);
		clone.setSize(dimClone);

		// Parcours de tout les éléments contenu dans le bouton parent
		for (int i = 0; i < nbChild; i++) {
			// Si c'est un Label qui est trouvé celui-ci est reconstitué à l'identique
			if (panelButton.getComponent(i) instanceof JLabel) {
				JLabel refLB = (JLabel) panelButton.getComponent(i);
				JLabel cloneLB = new JLabel();
				cloneLB.setSize(refLB.getSize());
				cloneLB.setText(refLB.getText());
				cloneLB.setIcon(refLB.getIcon());
				cloneLB.setVerticalAlignment(refLB.getVerticalAlignment());
				cloneLB.setHorizontalAlignment(SwingConstants.CENTER);
				cloneLB.setBounds(refLB.getBounds());
				cloneLB.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				// Si le label est le dernier élément du bouton on lui applique
				// la capacité a être drag and drop et de s'assemblé avec d'autre bouton
				if (i == nbChild - 1) 
				{
					cloneLB.addMouseMotionListener(new MouseMotionAdapter() {
						@Override
						public void mouseDragged(MouseEvent e) {
							Point ptn2 = SwingUtilities.convertPoint(clone, e.getX() - 40, e.getY(), zoneScriptsPanel);
							clone.setLocation(ptn2);
							//Recalcul taille lors du drag
							
						    instanceController.adjustPreferredSize();
							AssembleElementsSansInterpreter();
						}
						
					});
					cloneLB.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							if (SwingUtilities.isRightMouseButton(e)) {
								fonctionnaliteButton.showMenu(clone, e.getPoint());
					            zoneScriptsPanel.setSize(instanceController.ogSize);
							}else {
								AssembleElements();
							}
						}
			        	@Override
			        	public void mouseEntered(MouseEvent e) {
			        		clone.setBackground(Color.YELLOW);
			        	}
			        	public void mouseExited(MouseEvent e) {
			        		clone.setBackground(Color.WHITE);
			        	}
					});

				}
				clone.add(cloneLB);
			}

			// Si l'élément courant du bouton actuel est un TextField alors
			// celui-ci est recréer à l'identique dans le clone en cours de
			// création
			if (panelButton.getComponent(i) instanceof JTextField) {
				JTextField refTF = (JTextField) panelButton.getComponent(i);
				JTextField cloneTF = new JTextField();
				cloneTF.setBounds(refTF.getBounds());
				cloneTF.setHorizontalAlignment(SwingConstants.CENTER);
				cloneTF.setText(refTF.getText());
				cloneTF.setSize(refTF.getSize());
				clone.add(cloneTF);
			}

			if (panelButton.getComponent(i) instanceof JComboBox) {
				JComboBox refTF = (JComboBox) panelButton.getComponent(i);
				// Obtient l'�l�ment s�lectionn� dans la comboBox
				Object selectedItem = refTF.getSelectedItem();
				// Le texte Color Text a �t� s�lectionn�
				JComboBox cloneJCB = new JComboBox();
				// Chaine de caract�re pour l'ajout dans la comboBox
				String red = "red";
				String white = "white";
				String green = "green";
				String black = "black";
				String blue = "blue";
				// Ajout des textes des couleurs dans la comboBox
				cloneJCB.addItem(red);
				cloneJCB.addItem(white);
				cloneJCB.addItem(green);
				cloneJCB.addItem(black);
				cloneJCB.addItem(blue);
				// Taille du cloneJCB et add
				cloneJCB.setBounds(76, 0, 70, 23);
				cloneJCB.setSelectedItem(refTF.getSelectedItem());
				clone.add(cloneJCB);
			}
		}
		clone.setLocation(0, 0);
		if (isBoutonSelected) {
			zoneScriptsPanel.add(clone);
			instanceController.getBoutonsSurLeScript().add(clone);
		}
		
		return clone;
	}
	
	Map<JButton, List<JPanel>> objetsBlocsMap = new HashMap<>();
	/**
	 * Créer un bouton objet qui permet de le sélectionné afin d'implenter des blocs
	 * de scripts.
	 * 
	 * @param panelDroit       Zone d'apparation des boutons créer par la fonction.
	 * @param zoneScriptsPanel Zone de bloc de scripts actif du bouton sélectionné.
	 */
	public void addObjet(JPanel panelDroit, JPanel zoneScriptsPanel) {

		if (locX < 300) {
			final JButton boutonClone = new JButton("obj" + String.valueOf(id));

			boutonClone.setBounds(locX, locY, 80, 23);
			boutonClone.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					instanceController.setBoutonSelected(boutonClone);
					
					refreshMainPanel(instanceController.mainPanel);
				}
			});
			panelDroit.add(boutonClone);
			if (locY < 149) {
				locY = locY + 30;
			} else if (locX < 271) {
				locY = 5;
				locX = locX + 87;
			}
			id++;
			this.instanceController.addSpriteBouton(boutonClone);
		}

		refreshMainPanel(instanceController.mainPanel);
	}
	
	public void addObjetFromLoad(JPanel panelDesButtons, String nomBTN) {

		if (locX < 300) {
			final JButton boutonClone = new JButton(nomBTN);

			boutonClone.setBounds(locX, locY, 80, 23);
			boutonClone.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					instanceController.setBoutonSelected(boutonClone);
					
					refreshMainPanel(instanceController.mainPanel);
				}
			});
			panelDesButtons.add(boutonClone);
			if (locY < 149) {
				locY = locY + 30;
			} else if (locX < 271) {
				locY = 5;
				locX = locX + 87;
			}
			id++;
			this.instanceController.addSpriteBouton(boutonClone);
		}

		refreshMainPanel(instanceController.mainPanel);
	}
	
	/**
	 * Permet d'afficher tout les blocs de scripts liés à un bloc de scripts ayant été désélectionné.
	 * 
	 * @param nom Id de l'objet sélectionné.
	 * @param zoneScript La zone ayant tout les blocs de scripts.
	 */
	public void AfficherScriptsDuSprite(String nom, JPanel zoneScript) {
		LinkedList<JPanel> blocDuSprite = instanceController.spritesScripts.get(nom);
	    if (blocDuSprite != null) {
	        for (JPanel p : blocDuSprite) {
	            this.instanceController.getBoutonsSurLeScript().add(p);
	            zoneScript.add(p);
	        }
	    } else {
	        System.out.println("Aucun bloc associé à la clé " + nom);
	    }
	}
	/**
	 * Permet de vider la zone de script de l'objet actif.
	 * 
	 * @param zoneScript Zone où se trouve tous les blocs de scripts.
	 */
	public void ClearZoneScriptPanel(JPanel zoneScript) {
		zoneScript.removeAll();
		instanceController.getBoutonsSurLeScript().clear();
	}
	/**
	 * Permet de refresh la main page.
	 * 
	 * @param main Le JPanel principale.
	 */
	public void refreshMainPanel(JPanel main) {
		main.revalidate();
		main.repaint();
	}
	
	public void resetPos() {
		locX = 5;
		locY = 5;
	}
	
	public void addMouseListeners(JLabel cloneLB, JPanel zoneScriptsPanel) {
		cloneLB.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				JPanel clone = (JPanel) cloneLB.getParent();
				Point ptn2 = SwingUtilities.convertPoint(clone, e.getX() - 40, e.getY(), zoneScriptsPanel);
				clone.setLocation(ptn2);
				//Recalcul taille lors du drag
				
			    instanceController.adjustPreferredSize();
				AssembleElementsSansInterpreter();
			}
			
		});
		cloneLB.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					JPanel clone = (JPanel) cloneLB.getParent();
					fonctionnaliteButton.showMenu(clone, e.getPoint());
		            zoneScriptsPanel.setSize(instanceController.ogSize);
				}else {
					AssembleElements();
				}
			}
        	@Override
        	public void mouseEntered(MouseEvent e) {
        		JPanel clone = (JPanel) cloneLB.getParent();
        		clone.setBackground(Color.YELLOW);
        	}
        	public void mouseExited(MouseEvent e) {
        		JPanel clone = (JPanel) cloneLB.getParent();
        		clone.setBackground(Color.WHITE);
        	}
		});
	}
}
