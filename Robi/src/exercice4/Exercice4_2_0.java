package exercice4;

 import java.awt.Color;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;


public class Exercice4_2_0 {
	// Une seule variable d'instance
	Environment environment = new Environment();

	public Exercice4_2_0() {
		GSpace space = new GSpace("Exercice 4", new Dimension(200, 100));
		space.open();

		Reference spaceRef = new Reference(space);
		Reference rectClassRef = new Reference(GRect.class);
		Reference ovalClassRef = new Reference(GOval.class);
		Reference imageClassRef = new Reference(GImage.class);
		Reference stringClassRef = new Reference(GString.class);

		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());

		spaceRef.addCommand("add", new AddElement());
		spaceRef.addCommand("del", new DelElement());
		
		rectClassRef.addCommand("new", new NewElement());
		ovalClassRef.addCommand("new", new NewElement());
		imageClassRef.addCommand("new", new NewImage());
		stringClassRef.addCommand("new", new NewString());

		environment.addReference("space", spaceRef);
		environment.addReference("rect.class", rectClassRef);
		environment.addReference("oval.class", ovalClassRef);
		environment.addReference("image.class", imageClassRef);
		environment.addReference("label.class", stringClassRef);
		
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
				new Interpreter().compute(environment, itor.next());
			}
		}
	}

	public interface Command {
		// le receiver est l'objet qui va executer method
		// method est la s-expression resultat de la compilation
		// du code source a executer
		// exemple de code source : "(space setColor black)"
		abstract public Reference run(Reference receiver, SNode method);
	}// FIN INTERFACE Command **************************************************

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
		

		public Reference run(SNode expr) {
			Command cmd;
			cmd = primitives.get(expr.get(1).contents());
			
			return cmd.run(this, expr);
		}

		public Object getReceiver() {
			return this.receiver;
		}
	}// FIN CLASS Reference **************************************************

	public class Environment {
		HashMap<String, Reference> variables;

		public Environment() {
			variables = new HashMap<String, Reference>();
		}

		public void addReference(String name, Reference ref) {
			variables.put(name, ref);
		}
		
		public void removeReference(String name) {
			Reference ref = getReferenceByName(name);
			ref.primitives.clear();			
			variables.remove(name);
		}

		Reference getReferenceByName(String name) {
			Reference ref;
			ref = variables.get(name);

			return ref;

		}
	}// FIN CLASS Environment **************************************************
	
	
	public class SetColor implements Command {
		public Reference run(Reference ref, SNode espr) {
			String couleur = espr.get(2).contents().toString();
			Color c = Tools.getColorByName(couleur);
			//System.out.println("++++SetColor - "+ref.getReceiver().getClass().getName());
			
			if("graphicLayer.GSpace".equals(ref.getReceiver().getClass().getName())) {
				((GSpace) ref.getReceiver()).setColor(c);
			}else if("graphicLayer.GString".equals(ref.getReceiver().getClass().getName())){
				((GString) ref.getReceiver()).setColor(c);
			}else {
				((GElement) ref.getReceiver()).setColor(c);
			}
			

			return ref;

		}
	}// FIN CLASS SetColor **************************************************
	
	public class Sleep implements Command {
		public Reference run(Reference ref, SNode espr) {
			int res1 = Integer.parseInt(espr.get(2).contents());

			Tools.sleep(res1);

			return ref;

		}
	}// FIN CLASS Sleep **************************************************
	
	public class Translate implements Command {
		public Reference run(Reference ref, SNode espr) {
			int res1 = Integer.parseInt(espr.get(2).contents());
			int res2 = Integer.parseInt(espr.get(3).contents());
			((GElement) ref.receiver).translate(new Point(res1, res2));

			return ref;

		}
	}// FIN CLASS Translate **************************************************
	
	public class SetDim implements Command {
		public Reference run(Reference ref, SNode espr) {
			int res1 = Integer.parseInt(espr.get(2).contents());
			int res2 = Integer.parseInt(espr.get(3).contents());
			
			((GBounded) ref.getReceiver()).setDimension(new Dimension(res1, res2));

			return ref;

		}
	}// FIN CLASS SetDim **************************************************
	
	class NewElement implements Command {
		public Reference run(Reference reference, SNode method) {
			try {
				@SuppressWarnings("unchecked")
				GElement e = ((Class<GElement>) reference.getReceiver()).getDeclaredConstructor().newInstance();
				Reference ref = new Reference(e);
				ref.addCommand("setColor", new SetColor());
				ref.addCommand("translate", new Translate());
				ref.addCommand("setDim", new SetDim());
				return ref;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}// FIN CLASS NewElement **************************************************
	
	class NewImage implements Command {
		public Reference run(Reference reference, SNode method) {
						
			String cmd1 = method.get(2).contents();

			File path = new File(cmd1);
			BufferedImage rawImage = null;
			try {
				rawImage = ImageIO.read(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			GImage image = new GImage(rawImage);
			Reference refe = new Reference(image);

			refe.addCommand("translate", new Translate());

			return refe;
			
		}
	}// FIN CLASS NewImage **************************************************
	
	class NewString implements Command {
		public Reference run(Reference reference, SNode method) {
			
			String cmd1 = method.get(2).contents();
			GString chaine = new GString(cmd1);
			Reference refe = new Reference(chaine);
			refe.addCommand("translate", new Translate());
			refe.addCommand("setColor", new SetColor());
			return refe;
		}
	}// FIN CLASS NewString **************************************************
	
	class AddElement implements Command {
		public Reference run(Reference reference, SNode method) {
			GContainer recever = (GContainer) reference.getReceiver();

			Reference refNewElm = new Interpreter().compute(environment, method.get(3));
			
			environment.addReference(method.get(2).contents().toString(), refNewElm);
			GElement obj = (GElement) refNewElm.getReceiver();
			

			recever.addElement(obj);
			
			recever.repaint();
			return refNewElm;
		}
	}// FIN CLASS AddElement **************************************************
	
	class DelElement implements Command {
		public Reference run(Reference reference, SNode method) {
			Interpreter inter = new Interpreter();
			Reference refe = environment.getReferenceByName(method.get(2).contents());

			Reference receveur = environment.getReferenceByName(method.get(0).contents());
			((GContainer) receveur.getReceiver()).removeElement((GElement) refe.getReceiver());
			environment.removeReference(method.get(2).contents().toString());
			((GContainer) receveur.getReceiver()).repaint();
			return null;
		}
	}// FIN CLASS DelElement **************************************************
	
	class Interpreter {
		public Reference compute(Environment env, SNode exp ){
			Reference refElm = env.getReferenceByName(exp.get(0).contents().toString());

			
			return refElm.run(exp);
			
		}
	}// FIN CLASS Interpreter **************************************************
	
	
	
	
	
	
	public static void main(String[] args) {
		new Exercice4_2_0();
	}

}