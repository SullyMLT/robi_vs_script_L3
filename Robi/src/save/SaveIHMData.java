package save;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import IHM.RobiCoderALaMain;
import IHM.RobiScratch;

public class SaveIHMData{
	
	RobiScratch robiS;
	RobiCoderALaMain robiM;
	public SaveIHMData () {
		
	}
	
	public SaveIHMData (RobiScratch monRobiScratch, RobiCoderALaMain monRobiMain) {
		if (monRobiScratch != null || monRobiMain != null) {
			this.robiS = monRobiScratch;
			this.robiM = monRobiMain;
		}
		
	}
	
	public RobiScratch getRobiScratch() {
		return this.robiS;
	}
	
	public RobiCoderALaMain getRobiCoderMain() {
		return this.robiM;
	}
	
	
	public void saveScriptsWritable (String nomFichier) throws FileNotFoundException, IOException {
		String script = this.robiM.getScriptWrote();
		try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nomFichier))){
			outputStream.writeObject(script);
			System.out.println("save faite");
		}
	}
	
	public void saveScriptsScratch (String nomFichier) throws IOException {
		
		//R�cup�rer tous les scripts
		Map<String, LinkedList<JPanel>> scripts = this.robiS.getSpritesScripts();
		try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nomFichier))){
			outputStream.writeObject(scripts);
			System.out.println("save faite");
		}
	}
	
	public Map<String, LinkedList<JPanel>> loadDataFromFile(String nomFichier) {
		try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(nomFichier))){
			try {
				@SuppressWarnings("unchecked")
				Map<String, LinkedList<JPanel>> obj = (Map<String, LinkedList<JPanel>>)inputStream.readObject();
				return obj;
			}catch(ClassNotFoundException e) {
				System.out.println("Erreur classe non trouvé");
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public String loadDataFromWritableFile(String nomFichier) {
		try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(nomFichier))){
			try {
				@SuppressWarnings("unchecked")
				String obj = (String)inputStream.readObject();
				return obj;
			}catch(ClassNotFoundException e) {
				System.out.println("Erreur classe non trouvé");
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	
}
