package exercice2;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;


public class Exercice2_1_0 {
	GSpace space = new GSpace("Exercice 2_1", new Dimension(200, 100));
	GRect robi = new GRect();
	String script = "(space setColor black) (robi setColor yellow)";

	public Exercice2_1_0() {
		space.addElement(robi);
		space.open();
		this.runScript();
	}

	private void runScript() {
		SParser<SNode> parser = new SParser<>();
		List<SNode> rootNodes = null;
		try {
			rootNodes = parser.parse(script);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator<SNode> itor = rootNodes.iterator();
		
		// Execution de chaque instruction dans script
		while (itor.hasNext()) {
			this.run(itor.next());
		}
	}
	
	private void run(SNode expr) {
		switch (expr.get(0).contents()) {
			// Lorsque l'objet space est trouvé l'instruction setColor est utilisé sur celui-ci.
			case "space":
				// La couleur est entrée en chaîne de caractère puis transformé en couleur via la méthode
				// getColorByName venant de Tools.
				space.setColor(Tools.getColorByName(expr.get(2).contents()));
				break;
			// Pareil que pour space
			case "robi":
				robi.setColor(Tools.getColorByName(expr.get(2).contents()));
				break;
		
			default:
				break;
		}
	}

	public static void main(String[] args) {
		new Exercice2_1_0();

	}

}