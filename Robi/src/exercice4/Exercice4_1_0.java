package exercice4;

// 
//	(space setColor yellow) (space sleep 1000) (robi translate 80 40) (robi setColor red)	
//	(robi setColor yellow) 
//	(space sleep 2000) 
//	(space setColor white)  
//	(space sleep 1000) 	
//	(robi setColor red)		  
//	(space sleep 1000)
//	(robi translate 100 0)
//	(space sleep 1000)
//	(robi translate 0 50)
//	(space sleep 1000)
//	(robi translate -100 0)
//	(space sleep 1000)
//	(robi translate 0 -40) ) 
//

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tools.Tools;
import javax.sound.midi.Receiver;

//import org.omg.CORBA.Environment;

import java.awt.Color;

import exercice3.Exercice3_0.Command;
import graphicLayer.GElement;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

@SuppressWarnings("unused")
public class Exercice4_1_0 {
	// Une seule variable d'instance
	Environment environment = new Environment();

	public Exercice4_1_0() {
		// space et robi sont temporaires ici
		GSpace space = new GSpace("Exercice 4", new Dimension(200, 100));
		GRect robi = new GRect();

		space.addElement(robi);
		space.open();

		Reference spaceRef = new Reference(space);
		Reference robiRef = new Reference(robi);
		

		//AJOUT DES COMMANDES
		spaceRef.addCommand("setColor", new Command() {
			public Reference run(Reference ref, SNode espr) {
				String couleur = espr.get(2).contents().toString();
				Color c = Tools.getColorByName(couleur);
				((GSpace) ref.receiver).setColor(c);

				return ref;

			}
		});

		robiRef.addCommand("setColor", new Command() {
			public Reference run(Reference ref, SNode espr) {
				String couleur = espr.get(2).contents().toString();
				Color c = Tools.getColorByName(couleur);
				((GElement) ref.receiver).setColor(c);

				return ref;

			}
		});

		spaceRef.addCommand("sleep", new Command() {
			public Reference run(Reference ref, SNode espr) {
				int res1 = Integer.parseInt(espr.get(2).contents());

				Tools.sleep(res1);

				return ref;

			}
		});
		robiRef.addCommand("translate", new Command() {
			public Reference run(Reference ref, SNode espr) {
				int res1 = Integer.parseInt(espr.get(2).contents());
				int res2 = Integer.parseInt(espr.get(3).contents());
				((GElement) ref.receiver).translate(new Point(res1, res2));

				return ref;

			}
		});

		// Enrigestrement des references dans l'environement par leur nom
		environment.addReference("space", spaceRef);
		environment.addReference("robi", robiRef);

		this.mainLoop();
	}

	private void mainLoop() {
		while (true) {
			// prompt
			System.out.print("> ");
			// lecture d'une serie de s-expressions au clavier (return = fin de la serie)
			String input = Tools.readKeyboard();
			// creation du parser
			SParser<SNode> parser = new SParser<>();
			// compilation
			List<SNode> compiled = null;
			try {
				compiled = parser.parse(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// execution des s-expressions compilees
			Iterator<SNode> itor = compiled.iterator();
			while (itor.hasNext()) {
				this.run((SNode) itor.next());
			}
		}
	}

	private void run(SNode expr) {
		// quel est le nom du receiver
		String receiverName = expr.get(0).contents();
		// quel est le receiver
		Reference receiver = environment.getReferenceByName(receiverName);
		// demande au receiver d'executer la s-expression compilee
		receiver.run(expr);
	}

	public interface Command {
		// le receiver est l'objet qui va executer method
		// method est la s-expression resultat de la compilation
		// du code source a executer
		// exemple de code source : "(space setColor black)"
		abstract public Reference run(Reference receiver, SNode method);
	}

	public class Reference {
		Object receiver;
		Map<String, Command> primitives;

		public Reference(Object receiver) {

			this.receiver = receiver;
			primitives = new HashMap<String, Command>();
		}

		Command getCommandByName(String selector) {
			Command c;
			c = primitives.get(selector);
			return c;
		}

		public void addCommand(String selector, Command primitive) {
			primitives.put(selector, primitive);
		}

		public void run(SNode expr) {
			Command cmd;
			cmd = primitives.get(expr.get(1).contents());
			try {
			cmd.run(this, expr);
			}catch(Exception e) {
				System.err.println("Error : la s_expression n'est pas coh�rante !!");
			}
		}
	}

	public class Environment {
		HashMap<String, Reference> variables;

		public Environment() {
			variables = new HashMap<String, Reference>();
		}

		public void addReference(String name, Reference ref) {
			variables.put(name, ref);
		}

		Reference getReferenceByName(String name) {
			Reference ref;
			ref = variables.get(name);

			return ref;

		}
	}

	public static void main(String[] args) {
		new Exercice4_1_0();
	}
}
