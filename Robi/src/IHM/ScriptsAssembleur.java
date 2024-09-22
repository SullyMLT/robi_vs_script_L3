
package IHM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Pour la partie scratch
 * Assemble les diff�rents JPanel 
 * @author mathias.desoyer
 *
 */
public class ScriptsAssembleur implements Serializable {

	private static final long serialVersionUID = -6575422394111728416L;

	/**
	 * Associe les JPanel entre eux en fonction de leur distance relative et renvoie la liaison de ces diff�rents
	 * @param boutonsSurLeScript La liste de JPanel sur le JPanel de script
	 */
	public List<LinkedList<JPanel>> CreerScripts(List<JPanel> boutonsSurLeScript) {
		
		List<JPanel> panelTrie = trierHauteur(new ArrayList<JPanel>(boutonsSurLeScript));
		
		List<LinkedList<JPanel>> scripts = AssocierJPanels(panelTrie);
		
		AssembleJPanelScripts(scripts);
		return scripts;
	}

	/**
	 * Implantation d'un select sort pour trier en fonction de la hauteur
	 * @param l la liste des JPanel pr�sent sur le script
	 * @return la liste tri�e
	 */
	protected List<JPanel> trierHauteur(List<JPanel> l) {
		//Select sort pour trier
		for (int i = 0; i < l.size(); i++) {
			int minIndex = i;
			for (int j = i + 1; j < l.size(); j++) {
				if (l.get(minIndex).getY() > l.get(j).getY()) {
					minIndex = j;
				}
			}
			JPanel temp = l.get(minIndex);
			l.set(minIndex, l.get(i));
			l.set(i, temp);
		}
		return l;
	}
	
	/**
	 * Associe les JPanels entre eux en fonction de leur hauteur
	 * @param panelTrie la liste pr�alablement tri�e
	 * @return une liste de liste des JPanels associ�es
	 */
	protected List<LinkedList<JPanel>> AssocierJPanels(List<JPanel> panelTrie) {
		//Depuis la liste trié -> teste les éléments triée, si l'élément est à une distance d
		//de son n+1 où d < zoneD'attache alors les deux blocs s'attachent.
		
		//Parcourt la boucle
		//Si la distance est < à d alors on ajoute dans le script actuel les deux éléments
		//Si la distance est < à d et que un script est déjà en cours d'écriture alors on ajoute que le dernier élément dans le script courant
		//Dès lors que la distance est trop éloigné entre l'avant dernier et le dernier, ferme le script et l'ajoute aux scripts 
		int i = 0;
		int taille = panelTrie.size();
		boolean scriptEnCours = false;
		List<LinkedList<JPanel>> scriptsJPanel = new ArrayList<LinkedList<JPanel>>();
		LinkedList<JPanel> scriptCourantJPanel = new LinkedList<JPanel>();
		while (i < taille) {
			
			JPanel premier = panelTrie.get(i);
			JPanel deuxieme; 
			if (i + 1 < taille) {
				deuxieme = panelTrie.get(i+1);
			} else {
				//Si plus de bloc et qu'on a un attachement
				if (scriptEnCours) {
					//Ajoute le script 
					scriptsJPanel.add(scriptCourantJPanel);
					scriptEnCours = false;
				}
				break;
			}
			if (deuxieme.getY() - premier.getY() < 45 && Math.abs(deuxieme.getX() - premier.getX()) < 45) {
				
				if (!scriptEnCours) {
					//ajout des deux éléments
					//A CHANGER 
					//Ajout dans le script actuel
				
					scriptCourantJPanel = new LinkedList<JPanel>();
					scriptCourantJPanel.add(premier);
					scriptCourantJPanel.add(deuxieme);

					//On est donc entrain d'écrire un script
					scriptEnCours = true;
				} else {
					//Entrain d'écrire un script, on rajoute que le deuxième 
					scriptCourantJPanel.add(deuxieme);
				}
			} else {
				//Si la distance est trop éléve
				//Et qu'un script est en cours d'écriture
				if (scriptEnCours) {
					//Ajoute le script à tous les scripts 
					scriptsJPanel.add(scriptCourantJPanel);
					//On est donc plus entrain d'écrire un script
					scriptEnCours = false;
				}
				//Sinon, alors on passe à la suite.
			}
			i++;
		}
		return scriptsJPanel;
	}
	
	/**
	 * R�definit les positions des blocs en fonction de leur attachement � des scripts
	 * @param scripts La liste des listes de scripts en JPanel
	 */
	private void AssembleJPanelScripts(List<LinkedList<JPanel>> scripts) {
		
		//On prend le premier élément et on set le y et le x.
		//Puis on parcourt le sous script
		//On établit les positions des blocs suivant par rapport au premier
		for (int i = 0; i < scripts.size(); i++) {
			
			//Récupération du premier script
			List<JPanel> scriptCourant = scripts.get(i);
			
			//Récupération de la hauteur Y du premier bloc
			
			int yBase = scriptCourant.get(0).getY();
			int xBase = scriptCourant.get(0).getX();
			int yHeightAvant = scriptCourant.get(0).getHeight();
			
			//Parcourt le script courant
			for (int j = 1; j < scriptCourant.size(); j++) {
				//Set la position de l'élément à la position relative du bloc courant ainsi que de l'addition du bound du bloc d'avant
				JPanel element = scriptCourant.get(j);
				//Change la location
				element.setLocation(xBase, yBase + yHeightAvant);
				yHeightAvant = element.getHeight();
				yBase = element.getY();
			}
		}		
	}
		

}
