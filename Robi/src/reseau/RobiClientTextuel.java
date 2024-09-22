package reseau;

import java.io.IOException;
import java.io.ObjectInput;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import graphicLayer.GElement;
import graphicLayer.GImage;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import tools.Tools;

@SuppressWarnings("unused")
public class RobiClientTextuel {
	/**
	 * La socket associée au client
	 */
	protected Socket socket;

	/**
	 * Le stream output pour envoyer des messages
	 */
	protected ObjectOutputStream output;

	/**
	 * Le stram input associé à la socket pour recevoir des messages
	 */
	protected ObjectInputStream input;

	/**
	 * Si le client est connecté à un serveur
	 */
	boolean estConnecte;

	/**
	 * Si le serveur sur lequel le client est connecté fonctionne correctement
	 */
	boolean isServerAlive;

	/**
	 * La liste qui contient les images en base64
	 */
	List<String> imageBase64;
	boolean waitingForResponse;
	
	List<String> etat;
	
	/**
	 * Constructeur de chat client
	 * 
	 * @param c l'instance de ChatController
	 */
	public RobiClientTextuel() {
		// this.c = c;
		estConnecte = false;
		this.waitingForResponse = false;
	}

	/**
	 * Tente de connecter le client au serveur depuis une adresse et un port. Si le
	 * client est déjà connecté à un serveur valide -> ne tente pas de le
	 * connecter Si le client essaye de se connecter à une adresse multicast ->
	 * démarre un thread qui attend de recevoir un potentiel packet Si l'addresse
	 * passée en paramètre n'est pas valide -> renvoie une erreur Si le client
	 * essaye de se connecter à un serveur valide -> démarre un thread d'écoute
	 * des messages -> démarre un autre thread de ping qui ping le serveur toutes
	 * les secondes pour voir si le serveur est en vie.
	 * 
	 * @param addr l'adresse de connexion.
	 * @param port le port de connexion.
	 */
	public void connectToServer(String addr, int port) {
		try {
			// Vérification que le port est correcte
			if (port < 0 || port > 65535) {
				/* ControllerInstance */// .afficherPopupErreur("Le port n'est pas valide");
				return;
			}
			// Si aucune socket n'est associé à l'instance
			if (socket == null && estConnecte == false) {
				// Tentative de création de socket à l'adresse et le port
				socket = new Socket(addr, port);
				// Création de l'outputStream pour envoyer des messages au serveur
				this.output = new ObjectOutputStream(this.socket.getOutputStream());
				// Envoie d'un premier message débloquant le stream côté serveur
				// Nécessaire sinon le prochain client se connecte doit attendre que
				// l'avant
				// dernier client
				// qui s'est connecté envoie un message
				String lecture = "D�bloquage";
				output.writeObject(lecture);

				// Définit que le serveur est vivant
				setIsServerAlive(true);
				// Définit que l'instance est connecté à un serveur
				setEstConnecte(true);
				// Démarrage du thread d'écoute de message
				new ThreadClient(socket, this).start();
				// Démarrage du thread pour ping périodiquement le serveur
				new ThreadPingServer(this.output, this).start();
			} else {
				// Envoie sur l'interface un message d'erreur

			}
			// Exception levée quand new Socket(addr, port) n'arrive pas à être créer
			// (IOException)
		} catch (IOException e) {
			// Affichage d'une erreur comme quoi l'adresse n'est pas valide

		}
	}

	public class ClientImageDisplay {

		public static void displayImage(Image image) {
			// Création d'une icône à partir de l'image
			ImageIcon icon = new ImageIcon(image);

			// Création d'un JLabel pour afficher l'icône (image)
			JLabel label = new JLabel(icon);

			// Création d'un JFrame pour contenir le JLabel
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(label);
			frame.pack();
			frame.setVisible(true);
		}
	}


	/**
	 * Envoie un message au serveur.
	 * 
	 * @param message le message à envoyer au serveur.
	 */
	public void envoyerScript(String message) {
		// Vérifie que la communication avec le serveur est possible
		if (socket == null || socket.isClosed() || !isServerAlive) {
			System.out.println("La socket est fermée");
			return;
		}

		try {
			// Si le stream output n'a jamais utilisé, le créer.
			waitingForResponse = true;
			if (this.output == null) {
				// N'est jamais atteint mais c'est pour question de surêté.
				this.output = new ObjectOutputStream(this.socket.getOutputStream());
				System.out.println("Connexion du stream");
			}
			// Envoie du message au serveur par le stream
			String[] morceaux = message.split("\\)");
			for (String morceau : morceaux) {
				// Ajoutez la parenthèse fermée de nouveau à la fin de chaque morceau, sauf
				// pour le dernier
				String aEnvoyer = morceau.endsWith(")") ? morceau : morceau + ")";
				// Envoyez 'aEnvoyer' au serveur
				output.writeObject(aEnvoyer);
			}
			// Envoyer un message spécial pour indiquer la fin de la séquence
			// outputStream.writeObject("FIN");
			output.writeObject("FIN");
			return;
			// Exception levée quand l'ObjectOutputStream est invalide. (unreachable
			// dans ce
			// cas)
		} catch (IOException e) {
			System.out.println("Erreur lors de l'envoi du message.");
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Gère la déconnexion si le client est connecté à un serveur.
	 */
	public void deconnexion() {
		// Si la socket n'est pas nulle, par conséquent, le client est connecté.
		if (socket != null) {
			try {
				setEstConnecte(false);
				// Envoie un message au serveur précis
				// Le client ne peut pas envoyer "chaine" car le message d'un client est
				// de type
				// "[temps] : message"
				// Ce message permet au serveur de comprendre que la communication sur
				// cette
				// socket va être coupée.
				this.output.writeObject(new String("chaine"));
				// Ferme la socket
				socket.close();
				// Enlève la référence de la socket
				socket = null;
				System.out.println("Déconnecté");
				// Exception levée quand l'ObjectOutputStream est invalide. (unreachable
				// dans ce
				// cas)
			} catch (IOException e) {
				System.out.print("Le stream ne marche plus");
			}
			// Sinon, on part du principe que le client n'est connecté à un aucun
			// serveur
		}
	}

	/**
	 * Renvoie l'état de connexion du client.
	 * 
	 * @return l'état de connexion.
	 */
	public boolean isConnected() {
		return this.estConnecte;
	}

	/**
	 * Modifie l'état de validité du serveur.
	 * 
	 * @param isAlive le nouvel état de validité du serveur.
	 */
	public void setIsServerAlive(boolean isAlive) {
		this.isServerAlive = isAlive;
	}

	/**
	 * Modifie l'état de connexion du client.
	 * 
	 * @param _estConnecte le nouvel état de connexion du client.
	 */
	public void setEstConnecte(boolean _estConnecte) {
		estConnecte = _estConnecte;
	}

	/**
	 * Renvoie la socket de l'instance.
	 * 
	 * @return La socket.
	 */
	public Socket getSocketRobiClientTextuel() {
		return this.socket;
	}

	
	/**
	 * Class qui ping le serveur toutes les secondes pour vérifier que le serveur
	 * est vivant.
	 */
	private class ThreadPingServer extends Thread {

		/**
		 * L'outputStream associé à la socket.
		 */
		ObjectOutputStream output;
		/**
		 * L'instance qui a générée ce Thread.
		 */
		RobiClientTextuel RobiClientTextuel;

		/**
		 * Constructeur de ThreadPingServer qui permet de détecter si le serveur est
		 * toujours connecté au client.
		 * 
		 * @param output     l'instance ObjectOutputStream du parent.
		 * @param RobiClientTextuel instance parentale.
		 */
		public ThreadPingServer(ObjectOutputStream output, RobiClientTextuel RobiClientTextuel) {
			this.output = output;
			this.RobiClientTextuel = RobiClientTextuel;
		}

		/**
		 * Vérifie que le serveur est vivant en envoyant un message "ping" toutes les
		 * secondes au serveur Si une exception est levée, alors le serveur n'est plus
		 * disponible.
		 */
		public void run() {

			while (isServerAlive) {

				try {
					// Envoie du ping
					this.output.writeObject("ping");
					// Dors pendant 1 seconde
					Thread.sleep(1000);
					// Exceptions levées quand le thread est interrupted (n'arrive jamais)
					// et quand
					// this.output ne fonctionne plus
				} catch (IOException | InterruptedException e) {
					// Modifie l'état du serveur
					this.RobiClientTextuel.setIsServerAlive(false);
					// Si le client est encore connecté
					if (socket != null) {
						// Déconnexion du client au serveur

						this.RobiClientTextuel.socket = null;
						this.RobiClientTextuel.estConnecte = false;
					}
					// Quitte la boucle
					break;
				}
			}
		}
	}
	
	/**
	 * Class qui hérite de thread qui permet l'écoute des messages.
	 */
	private class ThreadClient extends Thread {

		/**
		 * La socket connecté au serveur.
		 */
		Socket socket;

		/**
		 * L'input stream pour lire les messages.
		 */
		ObjectInputStream input;

		/**
		 * l'instance parentale.
		 */
		RobiClientTextuel RobiClientTextuel;

		/**
		 * Constructeur qui génère le stream d'écoute.
		 * 
		 * @param socket la socket associée.
		 * @param cc     l'instance parentale.
		 */
		public ThreadClient(Socket socket, RobiClientTextuel cc) {
			this.socket = socket;
			this.RobiClientTextuel = cc;
			try {
				this.input = new ObjectInputStream(this.socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Positionne le thread sur l'écoute des messages A chaque fois qu'il reçoit
		 * un message, il l'ajoute sur l'interface graphique.
		 */
		public void run() {
			System.out.println("Connexion du stream");
			String message = "";
			etat = new ArrayList<String>();
			RobiClientTextuel.waitingForResponse = true;
			int boucle = 0;
			while (RobiClientTextuel.waitingForResponse) {
				// Lit le message

				try {
					message = (String) input.readObject();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Donnees reçus :" + message);
				if (message.equals("FIN")) {
					RobiClientTextuel.waitingForResponse = false;
					break;
				}
				//Rajoute le message re�u dans la liste de string des images en format base64
				etat.add(message);
				boucle++;
				//displayImage(message);
			}
			RenduDescripteur rd = new RenduDescripteur();
			for (String e : etat) {
				rd.rendreScript(e);
			}
			
		}
	}
	
	public class RenduDescripteur {
		public GSpace graphics;
		
		public RenduDescripteur() {
			
		}
		
		public LinkedList<String> monSplit(String texte, char c) {
			LinkedList<String> res = new LinkedList<String>();
			String tmp = "";
			for (int i = 0; i < texte.length(); i++) {
				if (texte.charAt(i) == c) {
					res.add(tmp);
					tmp = "";
				} else {
					tmp = tmp + texte.charAt(i);
				}
			}
			return res;
		}
		
		public void rendreScript(String script) {
			graphics = new GSpace("C�t� client", new Dimension(400, 400));
			LinkedList<String> commandes = monSplit(script, '|');
			System.out.println(commandes.toString());
			for (int i = commandes.size() - 1; i >= 0; i-- ) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] tokens = commandes.get(i).split(" ");
				//System.out.println("tokens : " + tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3] + " ");
				if (i == commandes.size()-1) {
					int w, h;
					w = Integer.parseInt(tokens[4]);
					h = Integer.parseInt(tokens[5]);
					graphics = new GSpace("C�t� client", new Dimension(w, h));
				}
				switch (tokens[0]) {
				case ("RECTANGLE") :
					rendreRectangle(tokens);
					break;
				case ("IMAGE") :
					rendreImage(tokens);
					break;
				}
			}
			this.graphics.open();
		}
		
		
		public void rendreRectangle(String[] tokens) {
			int x, y, w, h;
			x = Integer.parseInt(tokens[2]);
			y = Integer.parseInt(tokens[3]);
			w = Integer.parseInt(tokens[4]);
			h = Integer.parseInt(tokens[5]);
			String couleur = tokens[1];
			GRect rect = new GRect();
			rect.setColor(Tools.getColorByName(couleur));
			rect.setX(x);
			rect.setY(y);
			rect.setWidth(w);
			rect.setHeight(h);
			this.graphics.addElement(rect);
		}
		
		public void rendreImage(String[] tokens) {
			int x, y;
			x = Integer.parseInt(tokens[2]);
			y = Integer.parseInt(tokens[3]);
			String filePath = tokens[1];

			File path = new File(filePath);
			BufferedImage rawImage = null;
			try {
				rawImage = ImageIO.read(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			GImage image = new GImage(rawImage);
			image.setPosition(new Point(x, y));
			this.graphics.addElement(image);
		}
		
		
	}
	
	public synchronized boolean isWaitingForAResponse() {
		return this.waitingForResponse;
	}
	
	public synchronized void setWaitingForAResponse(boolean _b) {
		this.waitingForResponse = _b;
	}
	
	public static void main(String[] args) {

		RobiClientTextuel instance = new RobiClientTextuel();
		instance.connectToServer("localhost", 5000);
		Message m1 = new Message("PAS A PAS","(space setDim 1000 750)\n" + "(space add robi (rect.class new))\n"
				+ "(space.robi setColor black)\n" + "(space.robi setDim 470 250)\n" + "(space.robi translate 20 10)\n"
				+ "(space.robi add im1 (image.class new alien.gif))\n"
				+ "(space.robi add im2 (image.class new alien.gif))\n" + "(space.robi.im1 translate 0 75)\n"
				+ "(space.robi.im2 translate 400 75)\n" + "(space sleep 150)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space sleep 50)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space sleep 50)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space sleep 50)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space sleep 50)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space sleep 50)\n" + "(space.robi.im1 translate 30 0)\n"
				+ "(space.robi.im2 translate -30 0)\n" + "(space.robi add ex (image.class new explosion.gif))\n"
				+ "(space.robi.ex translate 175 55)\n" + "(space.robi setColor red)");
		
		 instance.envoyerScript(m1.toString());
	}

	public List<String> getEtats() {
		return etat;
	}
	

}
