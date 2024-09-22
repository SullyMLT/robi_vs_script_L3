
package reseau;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import tools.Tools;

import javax.imageio.ImageIO;

import graphicLayer.*;
import reseau.RobiServerTextuel.ThreadServerTextuel;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

/**
 * Interprete les commandes reçues et calcule les différentes positions des objets 
 * Renvoie une liste de string au serveur qui envoie au client les positions des différents objets
 */
@SuppressWarnings("unused")
public class InterpreteurServeurTextuel {
	ThreadServerTextuel ThreadServerTextuel;
	GSpace space;

	public InterpreteurServeurTextuel() {
	}

	Map<String,Component> mesObjets = new HashMap<>();
	
	
	public InterpreteurServeurTextuel(ThreadServerTextuel ThreadServerTextuel) {
		this.ThreadServerTextuel = ThreadServerTextuel;
	}


	public void mainLoop(String script) {

		SParser<SNode> parser = new SParser<>();
		String mess="";
		String type="";
		StringBuilder constructeurMessage = new StringBuilder();
		StringBuilder constructeurType = new StringBuilder();
		List<SNode> compiled = null;
		boolean m=false;
		int cmp=0;
		try {
			for(int i=0;i<script.length();i++) {
				if(script.charAt(i)==':'){
					m=true;
					i++;
				}
				while(m&&i<script.length()) {
					if(script.charAt(i)==','){
						m=false;
						cmp=1;
						i++;
						break;
					}
					if(cmp==0) {
						constructeurType.append(script.charAt(i));
					}else {
						constructeurMessage.append(script.charAt(i));
					}
					i++;
				}
			}
			cmp=0;
			type=constructeurType.toString();
			mess=constructeurMessage.toString();

			constructeurMessage = new StringBuilder();
			constructeurType = new StringBuilder();
			compiled = parser.parse(mess);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Iterator<SNode> itor = compiled.iterator();
		BufferedImage image = null;
		int i = 1;
		while (itor.hasNext()) {
			SNode s = itor.next();
			if (s.get(0).hasContents() && s.get(1).hasContents()) {
				String nom = s.get(0).contents();
				String cmd = s.get(1).contents();
				if (!ComponentExists(nom)) {
					if (!cmd.equals("add")) {
						mesObjets.put(nom,new SRect(nom, null));
					}
				}
				//mesObjetsnew SRect(0, 0, 0, 0, new Color(0,0,0)));
				jouerCommande(nom, cmd, s);

				String etat = Conversion();
				if (type.equals("PAS A PAS")) {
					ThreadServerTextuel.envoyerMessage(etat);
				}
				
				System.out.println(i + " : " + etat);
				i++;
			}
		}
		if (!type.equals("PAS A PAS")) {
			String etat = Conversion();
			ThreadServerTextuel.envoyerMessage(etat);
		}
		
		
		this.ThreadServerTextuel.envoyerImage(null);
	}

	public String Conversion() {
		String res = "";
		for (Component c : this.mesObjets.values()) {
			String SType = "";
			if (c instanceof SRect) {
				SType = "RECTANGLE " + ((SRect)c).colorName;
			} else {
				SType = "IMAGE " + ((SImage)c).path;
			}
			res = res + "" + SType + " " + c.x + " " + c.y + " " + c.w + " " + c.h + "|";
		}
		return res;
	}
	
	public void setThreadServeur(ThreadServerTextuel s) {
		this.ThreadServerTextuel = s;
	}
	
	public boolean ComponentExists(String name) {
		for (String nom : mesObjets.keySet()) {
			if (nom.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * La class Component qui définit un objet
	 */
	public class Component {
		String name;
		int x;
		int y;
		int h;
		int w;
		List<Component> children;
		Component parent;
	}
	
	/**
	 * Class qui définit un rectangle 
	 *
	 */
	public class SRect extends Component {
		String colorName;
		public SRect(String name, Component parent) {
			this.name = name;
			this.x = 0;
			this.y = 0; 
			this.w = 20;
			this.h = 20;
			this.colorName = "black";
			this.children = new ArrayList<Component>();
			this.parent = parent;
		}
	}
	
	/**
	 * Class qui définit une image
	 */
	public class SImage extends Component {
		String path;
		public SImage(String name, Component parent, String path) {
			this.name = name;
			this.x = 0;
			this.y = 0; 
			this.w = 20;
			this.h = 20;
			this.path = path;
			this.children = new ArrayList<Component>();
			this.parent = parent;
		}
	}
	
	
	/**
	 * Joue une commande sur un élément
	 * @param nomElement le nom de l'élément qui joue la commande
	 * @param nomCommande le nom de la commande associée
	 * @param expr le noeud qui contient les différents paramètres
	 */
	public void jouerCommande(String nomElement, String nomCommande, SNode expr) {
		
		Component comp = GetComposantByName(nomElement);
		if (nomCommande.equals("translate")) {
			Translate(comp, expr);
		}
		if (nomCommande.equals("setDim")) {
			SetDim(comp, expr);
		}
		if (nomCommande.equals("setColor")) {
			SetColor(comp, expr);
		}
		if (nomCommande.equals("add")) {
			AddElement(comp, expr);
		}
	}
	
	public Component GetComposantByName(String nomElement) {
		return this.mesObjets.get(nomElement);
	}
	
	public void Translate(Component composant, SNode expr) {
		int x = Integer.parseInt(expr.get(2).contents());
		int y = Integer.parseInt(expr.get(3).contents());
		composant.x = composant.x + x;
		composant.y = composant.y + y;
	}
	
	public void SetDim(Component composant, SNode expr) {
		int w = Integer.parseInt(expr.get(2).contents());
		int h = Integer.parseInt(expr.get(3).contents());
		composant.h = h;
		composant.w = w;
	}
	
	public void SetColor(Component composant, SNode expr) {
		if (!(composant instanceof SRect)) {
			throw new IllegalArgumentException("Le type du composant ne peut pas voir sa couleur modifié");
		}
		SRect element = (SRect) composant;
		element.colorName = expr.get(2).contents();
	}
	
	public void AddElement(Component composant, SNode expr) {
		Component parent = GetComposantByName(expr.get(0).contents().toString());
		// get(0) get(1)   get(2)     get(3).get(0)
		//(space   add      robi    (rect.class new))
		//(space add robi (rect.class new alien.gif))
		String nomParent = expr.get(0).contents().toString();
		String nomEnfant = expr.get(2).contents().toString();
		String nom = nomParent + "." + nomEnfant;
		
		//Ajout du fils 
		String classEnfant = expr.get(3).get(0).contents().toString();
		Component fils = null;
		switch (classEnfant) {
			case  ("rect.class") : 
				fils = new SRect(nom, parent);
				fils.x = parent.x;
				fils.y = parent.y;
				fils.w = parent.w/2;
				fils.h = parent.h/2;
				parent.children.add(fils);
				break;
			case ("image.class") : 
				String chemin = expr.get(3).get(2).contents().toString();
				fils = new SImage(nom, parent, chemin);
				fils.x = parent.x;
				fils.y = parent.y;
				parent.children.add(fils);
				break;
			default : 
				fils = new SRect(nom, parent);
				fils.x = parent.x;
				fils.y = parent.y;
				fils.w = parent.w/2;
				fils.h = parent.h/2;
				parent.children.add(fils);
				break;
		}
		mesObjets.put(nom, fils);
	}
	
}