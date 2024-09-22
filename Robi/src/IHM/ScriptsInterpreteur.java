package IHM;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Permet de compiler les blocs en script lisible (format string)
 */
public class ScriptsInterpreteur implements Serializable {

	
	private static final long serialVersionUID = -2476506971824087106L;
	/**
	 * D�finit la liste de commandeBloc connues
	 */
	List<CommandeBloc> commandesConnues;
	//Association d'un sprite avec ses scripts ! ne prends que en compte les scripts valides
	Map<String, List<String>> references = new HashMap<>();
	
	public ScriptsInterpreteur() {
		commandesConnues = new ArrayList<CommandeBloc>();
		
		
		HashMap<String, Class> args = new HashMap<String, Class>();
		

		args.put("start", String.class);
		commandesConnues.add(new CommandeBloc("start", args));
		
		args.clear();
		args.put("end", String.class);
		// Commande end
		commandesConnues.add(new CommandeBloc("end", args));
		
		args.clear();
		args.put("time", Integer.class);
		//Commande sleep
		commandesConnues.add(new CommandeBloc("sleep", args));
		
		//Commande translate
		//Ajout de x : int ; y : int
		args.clear();
		args.put("x", Integer.class);
		args.put("y", Integer.class);
		commandesConnues.add(new CommandeBloc("translate", args));
		
		args.clear();
		args.put("colorText", Color.class);
		// Commande setColorText
		commandesConnues.add(new CommandeBloc("setColor ", args));
		
		args.clear();
		args.put("colorR", Integer.class);
		args.put("colorG", Integer.class);
		args.put("colorB", Integer.class);
		// Commande setColorRGB
		commandesConnues.add(new CommandeBloc("setcolor", args));
		
		args.clear();
		args.put("dimX", Integer.class);
		args.put("dimY", Integer.class);
		// Commande setDim
		commandesConnues.add(new CommandeBloc("setDim", args));
		
		args.clear();
		args.put("startloop", Integer.class);
		// Commande setDim
		commandesConnues.add(new CommandeBloc("startloop", args));
		
		
		args.clear();
		args.put("path", String.class);
		commandesConnues.add(new CommandeBloc("addImg", args));
		
		
		args.clear();
		commandesConnues.add(new CommandeBloc("endloop", args));
			
	}
	
	/**
	 * Interprete les commandes issues des JPanels
	 * @param tousLesScripts
	 * @return
	 */
	protected List<String> InterpreterScripts(List<LinkedList<JPanel>> tousLesScripts, String refName) {
		
		List<String> commandesCompiles = new ArrayList<String>();
		
		//Traque les noms référents et ajoute aux scripts space s'ils sont valides (space add nom reference)
		
		for (List<JPanel> scriptCourant : tousLesScripts) {
			String commandeRes = "";
			int i = 0;
			for (JPanel bloc : scriptCourant) {
				String commande = "";
				//Get le nom du bloc :
				//R�cup�ration du dernier componant (qui est le nom de la commande)
				JLabel nomBlocLabel = (JLabel)bloc.getComponent(bloc.getComponentCount()-1);
				//R�cup�ration du string dans la commande
				String nomCommande = nomBlocLabel.getText();
				//S'assure que la commande est en lower case
				if (i == 0 && !nomCommande.equals("start")) {
					i++;
					break;
				} 
				if (nomCommande.equals("start")) {
					i++;
					continue;
				}
				if (nomCommande.equals("end")) {
					break;
				}
				//R�cup�re le commandeBloc associ�
				CommandeBloc cb = GetCommandeBlocFromNom(nomCommande);
				//R�cup�re les arguments
				String args = FetchArgumentsFromJPanel(bloc, cb);
				if (!refName.toLowerCase().equals("space")) {
					commande = "(space." + refName.toLowerCase() + " " + nomCommande + args + ")";
				} else {
					commande = "(" + refName.toLowerCase() + " " + nomCommande + args + ")";
				}
				
				commandeRes = commandeRes + commande;
				i++;
	
			}
			if (!commandeRes.equals("")) {
				commandesCompiles.add(commandeRes);
			}
		}
		
		if (!commandesCompiles.isEmpty()) {
			//Ajout de la référence 
			references.put(refName, commandesCompiles);
		}
		
		return commandesCompiles;
	}
	
	public String createFinalScript() {
		return createReferences(references.keySet()) + mixObjectsScript();
	}
	
	public String mixObjectsScript() {
		String res = "";
		
		List<Map.Entry<String, List<String>>> mapentries = new ArrayList<>();
		Iterator iterator = references.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, List<String>> mapentry = (Map.Entry<String, List<String>>)iterator.next();
			mapentries.add(mapentry);
		}
		
		int index = 0;
		while (index < 100) {
			
			for (Map.Entry<String, List<String>> entry : mapentries) {
				
				List<String> script = entry.getValue();
				
				if (index < script.size()) {
					res = res + script.get(index);
				}
				
			}
			index++;
		}
		return res;
	}
	
	/**
	 * Trouve la commande dans la liste des commandes connues
	 * @param nom le nom de la commande
	 * @return la commande trouv�e ou null 
	 */
	private CommandeBloc GetCommandeBlocFromNom(String nom) {
		for (CommandeBloc cb : commandesConnues) {
			
			if (cb.nom.equals(nom)) {
				return cb;
			}
		}
		
		return null;
	}
	
	/**
	 * Pour chaque objet, créer une référence 
	 * @param set les différents objets
	 * @return la res des références
	 */
	private String createReferences(Set<String> set) {
		String res = "";
		for (String ref : set) {
			if (!ref.equals("Space")) {
				res = res + "(space add " + ref + " (rect.class new))";
			}
		}
		return res;
	}
	
	
	/**
	 * Prends les arguments issues du JPanel et les transforme en string 
	 * @param bloc le JPanel associ�
	 * @param commandeBloc le bloc de commande trouv� 
	 * @return les arguments en string
	 */
	private String FetchArgumentsFromJPanel(JPanel bloc, CommandeBloc commandeBloc) {
		
		//R�cup�re la map des args
		HashMap <String, Class> args = new HashMap<String, Class>(commandeBloc.getTypesArgs());
		
		String argsRes = "";
		
		//Transforme en liste de class
		List<Class> typesArgs = new ArrayList<Class>();
		for (Map.Entry arg : args.entrySet()) {
			typesArgs.add((Class) arg.getValue());
		}
		
		int compteurTypeArgs = 0;
		
		for (int i = 0; i < bloc.getComponentCount(); i++) {
			//R�cup�re tous les �l�ments de types textField
			if (bloc.getComponent(i) instanceof JTextField) {
				//R�cup�re l'�l�ment dans le textField
				JTextField field = (JTextField)bloc.getComponent(i);
				
				//Si argument de type string //
				if (String.class == typesArgs.get(compteurTypeArgs)) {
					argsRes = argsRes + " " + field.getText();
				}
				//Si argument de type Integer
				if (Integer.class == typesArgs.get(compteurTypeArgs)) {
					//Essaye de parseInt
					try  {
						argsRes = argsRes + " " + Integer.parseInt(field.getText());
					} catch (NumberFormatException e) {
						System.out.println("Argument incorrecte");
					}
				}
				compteurTypeArgs++;
				//TODO � rajouter les diff�rents types
			}
			// 
			if (bloc.getComponent(i) instanceof JComboBox) {
				JComboBox comboBox = (JComboBox) bloc.getComponent(i);
				Object selectedItem = comboBox.getSelectedItem();
				
				if (Color.class == typesArgs.get(compteurTypeArgs)) {
					argsRes =  argsRes + " " + selectedItem.toString();
				}
				compteurTypeArgs++;
			}
			if (bloc.getComponent(i) instanceof JLabel) {
				//Cas ou c'est une imaeg 
			}
		}
		return argsRes;
	}	
}