package reseau;

import java.io.IOException;
import java.io.ObjectInput;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Permet la communication avec le serveur
 */
public class RobiClient implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2412624973141369938L;

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
	
	/**
	 * Constructeur de chat client
	 * 
	 * @param c l'instance de ChatController
	 */
	public RobiClient() {
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
			System.out.println("pas connecte");
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

	public void displayBase64Image(String base64Image) {
		ClientImageDisplay.displayImage(decodeBase64ToImage(base64Image));
	}

	private static Image decodeBase64ToImage(String base64Image) {
		byte[] imageBytes = Base64.getDecoder().decode(base64Image);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
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
	public Socket getSocketRobiClient() {
		return this.socket;
	}

	public List<String> getListImageBase64() {
		return this.imageBase64;
		
		
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
		RobiClient robiClient;

		/**
		 * Constructeur de ThreadPingServer qui permet de détecter si le serveur est
		 * toujours connecté au client.
		 * 
		 * @param output     l'instance ObjectOutputStream du parent.
		 * @param robiClient instance parentale.
		 */
		public ThreadPingServer(ObjectOutputStream output, RobiClient robiClient) {
			this.output = output;
			this.robiClient = robiClient;
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
					this.robiClient.setIsServerAlive(false);
					// Si le client est encore connecté
					if (socket != null) {
						// Déconnexion du client au serveur

						this.robiClient.socket = null;
						this.robiClient.estConnecte = false;
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
		RobiClient robiClient;

		/**
		 * Constructeur qui génère le stream d'écoute.
		 * 
		 * @param socket la socket associée.
		 * @param cc     l'instance parentale.
		 */
		public ThreadClient(Socket socket, RobiClient cc) {
			this.socket = socket;
			this.robiClient = cc;
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
			List<String> base64 = new ArrayList<String>();
			robiClient.waitingForResponse = true;
			boolean estInit = false;
			while (true) {
				//System.out.println(robiClient.waitingForResponse);
				
				if (estInit == false) {
					base64 = new ArrayList<String>();
					estInit = true;
				}
				
				while (robiClient.isWaitingForAResponse()) {
					// Lit le message
					System.out.println("message !");
					try {
						message = (String) input.readObject();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
					System.out.println("Donnees reçus :" + message);
					if (message.equals("FIN")) {
						robiClient.setWaitingForAResponse(false);
						estInit = false;
						this.robiClient.imageBase64 = new ArrayList<String>(base64);
						
						break;
					}
					//Rajoute le message re�u dans la liste de string des images en format base64
					base64.add(message);
					//displayImage(message);
				}
			}
		}
	}
		

	public synchronized boolean isWaitingForAResponse() {
		return this.waitingForResponse;
	}
	
	public synchronized void setWaitingForAResponse(boolean _b) {
		this.waitingForResponse = _b;
	}

	public static void main(String[] args) {

		RobiClient instance = new RobiClient();
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
				+ "(space.robi.ex translate 175 55)\n" + "(space.robi setColor red)"
				+ "(space add robi (rect.class new))\n" + "(space.robi setColor black)\n"
				+ "(space.robi setDim 470 250)\n" + "(space.robi translate 20 10)\n"
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
	
	
}
