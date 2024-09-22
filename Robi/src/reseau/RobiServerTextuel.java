package reseau;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * La classe RobiServerTextuel permet de créer un serveur qui re�oit un script et le traduit en nouveau script 
 */

public class RobiServerTextuel {

	// La socket du serveur
	/**
	 * Instance de ServerSocket.
	 */
	ServerSocket serverSocket;
	/**
	 * List de ThreadServerTextuel qui stocke tous les clients.
	 */
	List<ThreadServerTextuel> threads = new ArrayList<ThreadServerTextuel>();

	/**
	 * InterpreteurServeurTextuel instance
	 */
	InterpreteurServeurTextuel InterpreteurServeurTextuel;

	public RobiServerTextuel() {
		InterpreteurServeurTextuel = new InterpreteurServeurTextuel();
	}

	/**
	 * Méthode permettant de démarrer le serveur en entrant le port et l'adresse
	 * Multicast. Si des clients sont en attentes ceux-ci seront connectés
	 * automatiquement lorsque le serveur sera mis en ligne, en utilisant l'adresse
	 * ip du serveur locale ainsi que son port qui sont envoyé aux clients.
	 * 
	 * @param port            Le port d'écoute du serveur pour les connexions
	 *                        entrantes.
	 * @param _multicastGroup L'adresse ip du groupe multicast auquel l'adresse et
	 *                        le port du serveur seront envoyés.
	 * 
	 * @throws IOException Si une erreur d'entrée se produit à l'initialisation du
	 *                     serveur ou de l'envoi de l'adresse et du port au groupe
	 *                     multicast.
	 */
	public void demarrerServeur() {
		try {

			serverSocket = new ServerSocket(5000); // Instancie le ServerSocket avec
													// le port
			System.out.println("Démarrage serveur");
			// En attente de connexion
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.print("connexion ! \n");
				ThreadServerTextuel ts = new ThreadServerTextuel(socket, this);
				threads.add(ts);
				ts.start();
			}
		} catch (IOException e) {
			System.out.print("Problème lors du démarrage du serveur \n");
			e.printStackTrace();
		}
	}

	/**
	 * La classe ThreadServerTextuel qui est appelée dès qu'un client se connecte. Elle
	 * hérite de thread qui permet de recevoir et d'envoyer les messages envoyé
	 * par le client à l'aide d'ObjectInputStream et d'ObjectOutputStream.
	 */
	public class ThreadServerTextuel extends Thread {

		Socket socket; // Socket avec le client
		ObjectInputStream input; // Le stream d'input
		ObjectOutputStream output; // Le stream d'output
		RobiServerTextuel parentInstance; // Référence au serveur
		InterpreteurServeurTextuel InterpreteurServeurTextuel;

		/**
		 * Constructeur de la classe ThreadServerTextuel.
		 * 
		 * @param socket         Le socket qui est lié au client.
		 * @param parentInstance L'instance qui est relié au parent.
		 * @return 
		 * 
		 * @throws IOException Si une erreur d'entrée/sortie se produit lors de la
		 *                     création des flux d'entrée / sortie.
		 */
		public ThreadServerTextuel(Socket socket, RobiServerTextuel parentInstance) {
			this.socket = socket;
			this.parentInstance = parentInstance;
			try {
				this.output = new ObjectOutputStream(socket.getOutputStream());
				this.input = new ObjectInputStream(socket.getInputStream());
				this.InterpreteurServeurTextuel = parentInstance.getInterpreteurServeurTextuel();
				this.InterpreteurServeurTextuel.setThreadServeur(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Méthode pour capturer l'image de GSpace et l'envoyer au client
		public void envoyerImage(BufferedImage img) {
			try {

				if (img == null) {
					System.out.println("Fin envoie image");
					this.output.writeObject("FIN");
					return;
				}
				// Capture de l'image de GSpace
				BufferedImage image = img;
				
				// Conversion de l'image en Base64
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(image, "png", outputStream);
				byte[] imageBytes = outputStream.toByteArray();
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				this.output.writeObject(base64Image);
				outputStream.close();

				// Envoi de l'image en Base64 au client
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Méthode pour capturer l'image de GSpace et l'envoyer au client
		public void envoyerMessage(String message) {
			try {

				if (message == null) {
					System.out.println("Fin envoie image");
					this.output.writeObject("FIN");
					return;
				}
				
				this.output.writeObject(message);

				// Envoi de l'image en Base64 au client
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				@SuppressWarnings("unused")
				Object firstLecture = input.readObject(); // Récupère le premier message
				StringBuilder constructeurMessage = new StringBuilder();

				while (socket.isConnected()) {
					// Lire les données de manière asynchrone
					Object receivedData = input.readObject();

					String messageStr = (String) receivedData;
					if ((!"FIN".equals(messageStr)) && (!"ping".equals(messageStr))) {
						// Concaténez chaque morceau reçu
						constructeurMessage.append(messageStr);
					} else {
						// Traitez le message complet ici
						String messageComplet = constructeurMessage.toString();
						// Réinitialiser le constructeur pour le prochain message
						constructeurMessage = new StringBuilder();
						if (!"".equals(messageComplet)) {
							//System.out.println("Message complet : " + messageComplet);
							
							InterpreteurServeurTextuel.mainLoop(messageComplet);
						}

					}

					// Lorsque receivedData = "chaine" alors le serveur s'éteint
					// (force
					if (receivedData.equals("chaine")) {
						break;
					}

					// Traiter les données reçues ici...
					if (!receivedData.equals("ping")) {
						//System.out.println("Données reçues : " + receivedData);
					}

				}
				System.out.print("Fermeture du thread \n");

				// Fermeture de tous les components
				input.close();
				output.close();
				socket.close();
				// On retire le thread de la liste
				this.parentInstance.threads.remove(this);
			} catch (IOException | ClassNotFoundException e) {
				// Unreachable
				e.printStackTrace();
			}

		}
	}

	/**
	 * Méthode main qui permet de mettre en ligne le serveur s'il y a 2 paramètres
	 * donnée lors du lancement de celui-ci.
	 * 
	 * @param args Nombre de paramètres fournis lorsque le serveur est mis en
	 *             ligne.
	 */
	public static void main(String arg[]) {

		RobiServerTextuel instance = new RobiServerTextuel();

		instance.demarrerServeur();
	}

	public InterpreteurServeurTextuel getInterpreteurServeurTextuel() {
		return this.InterpreteurServeurTextuel;
	}

}
