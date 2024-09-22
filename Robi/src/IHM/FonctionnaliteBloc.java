package IHM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.List;

public class FonctionnaliteBloc extends JFrame implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5167373394267740516L;
	private JPopupMenu popupMenu;
    private Component targetComponent;
    private JPanel parentPanel;
    private List<JPanel> boutonsSurLeScript;
    private RobiScratch parentInstance;

    public FonctionnaliteBloc(RobiScratch parent) {
    	this.parentInstance = parent;
    }
    
    public void setBoutonsSurLeScript(List<JPanel> _boutonsSurLeScript) {
    	this.boutonsSurLeScript = _boutonsSurLeScript;
    }

    public void setParentPanel(JPanel newJpanel) {
    	this.parentPanel = newJpanel;
    	initializePopupMenu();
    }
    
    public void initializePopupMenu() {
        popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Supprimer");
        popupMenu.add(deleteItem);

        deleteItem.addActionListener(e -> deleteAction());
    }

    public void showMenu(Component component, Point location) {
        this.targetComponent = component;
        // Utilise directement la location du clic pour afficher le menu
        popupMenu.show(component, location.x, location.y);

    }


    private void deleteAction() {
    	JPanel compo = (JPanel) targetComponent;
        if (targetComponent != null && targetComponent instanceof JPanel) {
        	this.parentInstance.removeFromBoutonsSurLeScript((JPanel)targetComponent);
            parentPanel.remove(compo);
            parentPanel.revalidate();
            parentPanel.repaint();
        }
    }

	@Override
	public String toString() {
		return "FonctionnaliteButton [popupMenu=" + popupMenu + ", targetComponent=" + targetComponent
				+ ", parentPanel=" + parentPanel + ", boutonsSurLeScript=" + boutonsSurLeScript + "]";
	}
    
    
}
