package IHM;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import reseau.Message;
import reseau.RobiClient;
import save.SaveIHMData;


/**
 * Class qui gère l'ensemble des interfaces IHM
 */
public class IHMController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7437880618385314207L;
	private JFrame frame;
	private RobiScratch scratchWindow;
	private RobiCoderALaMain writableWindow;
	private RobiClient client;
	
	SaveIHMData saveController;
	
	private List<String> scripts;
	private List<String> image64;
	
	int indexImageAffiche = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		IHMController instance = new IHMController(); 
	}

	/**
	 * Create the application.
	 */
	public IHMController() {
		scratchWindow = new RobiScratch();
		writableWindow = new RobiCoderALaMain();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					writableWindow.setVisible(false);
					scratchWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initialize();
		//Create a client
		scratchWindow.setIHMController(this);
		writableWindow.setIHMController(this);
		
		//Cr�er une instance pour la sauvegarde 
		saveController = new SaveIHMData(scratchWindow, writableWindow);
		
		client = new RobiClient();
		client.connectToServer("localhost", 5000);
	}
	
	/**
	 * Envoie les diff�rents scripts au serveur � travers le client
	 */
	public void EnvoyerScriptsAuServeur(String script, boolean mode) {
		
		String _mode = mode ? "PAS A PAS" : "GLOBAL";
		Message m = new Message(_mode, script);
		
		/*Message m1 = new Message("PAS A PAS","(space setDim 500 300)\n" + "(space add robi (rect.class new))\n"
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
				+ "(space.robi.ex translate 175 55)\n" + "(space.robi setColor red)"); */
		
		client.envoyerScript(m.toString());
		client.setWaitingForAResponse(true);
		while (client.isWaitingForAResponse()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		image64 = RecupereImages();
		indexImageAffiche = 0;
		this.displayImage(indexImageAffiche);
		//R�cup�re les images
		//List<String> imageStringed = client.getListImages();
		//Convertit les images
		//Les affiche
		//Une des versions de window
		//this.displayImage(imageStringed);
	}
	
	/**
	 * M�thode appel� dans EnvoyerScriptsAuServeur
	 * R�cup�re les images envoy�es en format base 64 sur le stream depuis le client
	 */
	private List<String> RecupereImages() {
		return this.client.getListImageBase64();
	}
	
	/**
	 * Affiche les images sur le pannel concordant (le pannel en haut � droite)
	 */
	private void displayImage(int indexImage) {
		this.scratchWindow.changeImageHandler(image64.get(indexImage));
		this.writableWindow.changeImageHandler(image64.get(indexImage));
	}
	
	/**
	 * Traduit une liste de string en un seul et m�me string
	 * @param liste une liste de string contenant les diff�rents �l�ments
	 * @return le string traduit
	 */
	public String getScriptsFromList(List<String> liste) {
		String res = "";
		
		for (String script : liste) {
			res = res + script;
		}

		return res;
		
	}
	
	/**
	 * Affiche la JFrame coder � la main
	 */
	public void afficherCoderMain() {
		writableWindow.setVisible(true);
		scratchWindow.setVisible(false);
	}
	
	/**
	 * Affiche la JFrame coder � la main
	 */
	public void afficherCoderScratch() {
		writableWindow.setVisible(false);
		scratchWindow.setVisible(true);
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void imageSuivante() {
		int nbImg = image64.size() - 1;
		if (indexImageAffiche < nbImg) {
			this.indexImageAffiche++;
			afficherImage();
		}
	}
	
	public void imagePrecedente() {
		
		if (indexImageAffiche > 0 ) {
			this.indexImageAffiche--;
			afficherImage();
		}
	}
	
	public void imageDebut() {
		this.indexImageAffiche = 0;
		afficherImage();
	}
	
	public void imageSlider(int index) {
		this.indexImageAffiche = index;
		afficherImage();
	}

	public void imageFin() {
		this.indexImageAffiche = image64.size()-1;
		afficherImage();
	}
	
	private void afficherImage() {
		this.displayImage(this.indexImageAffiche);
	}
	
	public ImageIcon decodeBase64ToScaledImageIcon(String base64Image, int width, int height) {
	    try {
	        // Decode base64 string to byte array
	        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
	        // Create image from byte array
	        Image image = Toolkit.getDefaultToolkit().createImage(imageBytes);
	        // Scale image to desired dimensions
	        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	        // Create ImageIcon from scaled image
	        return new ImageIcon(scaledImage);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	/**
	 * Sauvegarde les scripts Scratch dans un fichier avec le nom sp�cifi�.
	 *
	 * @param nom Le nom du fichier de sauvegarde (sans extension).
	 */
	public void saveFileScratch(String nomFichier) {
	    try {
	        saveController.saveScriptsScratch(nomFichier + ".bat");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isConnected() {
		return this.client.isConnected();
	}

	/**
	 * Sauvegarde les scripts dans un fichier modifiable avec un nom par d�faut.
	 * Le fichier sera nomm� "SauvegardeWrite.bat".
	 * En cas d'erreur, affiche un message d'erreur et imprime la trace de la pile.
	 */
	public void saveFileWritable(String nomFichier) {
	    try {
	        saveController.saveScriptsWritable(nomFichier+".bat");
	    } catch (IOException e) {
	        System.out.println("Erreur lors de la sauvegarde du script writable");
	        e.printStackTrace();
	    }
	}

	/**
	 * Charge un fichier modifiable � partir du chemin de fichier sp�cifi�.
	 * Affiche le script charg� dans la console et met � jour la fen�tre modifiable avec ce script.
	 *
	 * @param filePath Le chemin du fichier � charger.
	 */
	public void loadFileWritable(String filePath) {
	    String script = saveController.loadDataFromWritableFile(filePath);
	    this.writableWindow.setScriptWrote(script);
	}

	/**
	 * Charge un fichier de scripts Scratch � partir du chemin de fichier sp�cifi�.
	 * Charge les blocs de script dans la fen�tre Scratch et actualise la fen�tre principale.
	 *
	 * @param filePath Le chemin du fichier � charger.
	 */
	public void loadFileScratch(String filePath) {
		Map<String, LinkedList<JPanel>> tmp = saveController.loadDataFromFile(filePath);
		this.scratchWindow.getZoneDuBloc().removeAll();
		this.scratchWindow.cloneButton.resetPos();
		this.scratchWindow.resetSpritesBoutons();
		this.scratchWindow.deleteObjButton();
		this.scratchWindow.removeAllFromBoutonsSurLeScript();
		this.scratchWindow.cloneButton.id = 1;
		this.scratchWindow.loadAllObject(tmp);
	    this.scratchWindow.refreshMainPanel();
	}
	
	public List<String> getImage64(){
		return this.image64;
	}	
}
