package reseau;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import graphicLayer.*;
import reseau.RobiServer.ThreadServer;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

@SuppressWarnings("unused")
/**
 * Inteprete les scripts reçus par le serveur et rend un graphique côté serveur
 * Puis convertit les images en base64 et les envoie à RobiServer
 *  
 */
public class InterpreteurServeur {
	Environment environment = new Environment();
	ThreadServer threadServer;

	GSpace space;

	public InterpreteurServeur() {
		Demarrage();
	}

	public InterpreteurServeur(ThreadServer threadServer) {
		this.threadServer = threadServer;
		Demarrage();
	}

	public void Demarrage() {
		space = new GSpace("Côté serveur", new Dimension(400, 400));
		space.open();

		Reference spaceRef = new Reference(space);
		Reference rectClassRef = new Reference(GRect.class);
		Reference ovalClassRef = new Reference(GOval.class);
		Reference imageClassRef = new Reference(GImage.class);
		Reference stringClassRef = new Reference(GString.class);

		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());
		spaceRef.addCommand("setDim", new SetDim());

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
		while (itor.hasNext()) {
			new Interpreter().compute(environment, itor.next());
			image = ((GSpace) space).getBufferedImage();
			if(image == null) {
				System.out.println("erreur de script");
			}
			if(type.equals("PAS A PAS")) {
				this.threadServer.envoyerImage(image);
			}
		}
		if (!type.equals("PAS A PAS")){
			this.threadServer.envoyerImage(image);
		}
		this.threadServer.envoyerImage(null);
		space.clear();

	}

	public void setThreadServeur(ThreadServer s) {
		this.threadServer = s;
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

		public Reference run(SNode expr) {
			Command cmd;
			cmd = primitives.get(expr.get(1).contents());
			// try {
			return cmd.run(this, expr);
			/*
			 * }catch(Exception e) {
			 * System.err.println("Error : la s_expression n'est pas cohï¿½rante !!"); }
			 */
		}

		public Object getReceiver() {
			return this.receiver;
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
	}

	public class SetColor implements Command {
		public Reference run(Reference ref, SNode espr) {
			String couleur = espr.get(2).contents().toString();
			Color c = Tools.getColorByName(couleur);
			if (c == null) {
				System.err.println("Couleur non reconnue: " + couleur);
				return ref;
			}

			if (ref.getReceiver() instanceof GSpace) {
				((GSpace) ref.getReceiver()).setColor(c);
			} else if (ref.getReceiver() instanceof GString) {
				((GString) ref.getReceiver()).setColor(c);
			} else if (ref.getReceiver() instanceof GElement) {
				((GElement) ref.getReceiver()).setColor(c);
			}

			return ref;

		}
	}

	public class Sleep implements Command {
		public Reference run(Reference ref, SNode espr) {
			int res1 = Integer.parseInt(espr.get(2).contents());

			Tools.sleep(res1);

			return ref;

		}
	}

	public class Translate implements Command {
		public Reference run(Reference ref, SNode espr) {
			int res1 = Integer.parseInt(espr.get(2).contents());
			int res2 = Integer.parseInt(espr.get(3).contents());
			((GElement) ref.receiver).translate(new Point(res1, res2));

			return ref;

		}
	}

	public class SetDim implements Command {
		public Reference run(Reference ref, SNode methode) {
			int width = Integer.parseInt(methode.get(2).contents());
			int height = Integer.parseInt(methode.get(3).contents());
			Object receiver = ref.getReceiver();

			// Vï¿½rifie d'abord si le receiver est une instance de GSpace
			if (receiver instanceof GSpace) {
				((GSpace) receiver).changeWindowSize(new Dimension(width, height));
			}
			// Ensuite, vï¿½rifie si le receiver est une instance de GBounded
			else if (receiver instanceof GBounded) {
				((GBounded) receiver).setDimension(new Dimension(width, height));
			}
			// Si le receiver n'est ni un GSpace ni un GBounded, imprime un message
			// d'erreur.
			else {
				System.err.println(
						"Erreur: L'objet " + receiver.getClass().getName() + " ne peut pas ï¿½tre redimensionnï¿½.");
			}

			return ref;
		}
	}

	class NewElement implements Command {
		public Reference run(Reference reference, SNode method) {
			try {
				@SuppressWarnings("unchecked")
				GElement e = ((Class<GElement>) reference.getReceiver()).getDeclaredConstructor().newInstance();
				Reference ref = new Reference(e);
				ref.addCommand("setColor", new SetColor());
				ref.addCommand("translate", new Translate());
				ref.addCommand("setDim", new SetDim());

				ref.addCommand("add", new AddElement());
				ref.addCommand("del", new DelElement());
				ref.addCommand("new", new NewElement());
				return ref;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

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
	}

	class NewString implements Command {
		public Reference run(Reference reference, SNode method) {

			String cmd1 = method.get(2).contents();
			GString chaine = new GString(cmd1);
			Reference refe = new Reference(chaine);
			refe.addCommand("translate", new Translate());
			refe.addCommand("setColor", new SetColor());
			return refe;
		}
	}

	class AddElement implements Command {
		public Reference run(Reference reference, SNode method) {
			GContainer recever = (GContainer) reference.getReceiver();
			Reference refNewElm = new Interpreter().compute(environment, method.get(3));

			environment.addReference(method.get(0).contents().toString() + "." + method.get(2).contents().toString(),
					refNewElm);
			GElement obj = (GElement) refNewElm.getReceiver();

			recever.addElement(obj);

			recever.repaint();
			return refNewElm;
		}
	}

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
	}

	class Interpreter {
		public Reference compute(Environment env, SNode exp) {
			Reference refElm = env.getReferenceByName(exp.get(0).contents().toString());
			if (refElm == null) {
				System.err.println("Erreur : rï¿½fï¿½rence '" + exp.get(0).contents().toString() + "' introuvable.");
				return null;
			}
			return refElm.run(exp);

		}
	}

}