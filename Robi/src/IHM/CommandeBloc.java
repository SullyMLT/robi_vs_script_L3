package IHM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Définit un commandeBloc
 * @author mathias.desoyer
 *
 */
public class CommandeBloc {

	/**
	 * Le nom de la commande
	 */
	String nom;
	
	/**
	 * les types d'args
	 */
	HashMap<String,Class> typesArgs;
	
	/**
	 * Constructeur de commandeBloc
	 * @param nom le nom de la commande
	 * @param args map {nomArgument : "x" ; classType : "Integer.class"}
	 */
	public CommandeBloc(String nom, HashMap<String, Class> args) {
		typesArgs = new HashMap<String, Class>(args);
		this.nom = nom;
	}
	
	public HashMap<String, Class> getTypesArgs() {
		return this.typesArgs;
	}
	
	public static void main(String args[]) {
		
		HashMap<String, Class> testArgs = new HashMap<String, Class>();
		testArgs.put("x", Integer.class);
		testArgs.put("y", Integer.class);
		testArgs.put("ref", String.class);
		CommandeBloc testCommandeBloc = new CommandeBloc("sleep", testArgs)  ;
		if (String.class == testCommandeBloc.typesArgs.get(0)) {
			System.out.println("oui");
		}
	}
	
	
}
