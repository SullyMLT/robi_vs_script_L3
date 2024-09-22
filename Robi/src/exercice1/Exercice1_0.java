package exercice1;

import java.awt.Dimension;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import java.awt.Color;

public class Exercice1_0 {
	GSpace space = new GSpace("Exercice 1", new Dimension(200, 150));
	GRect robi = new GRect();
	
	public Exercice1_0() {
		space.addElement(robi);
		space.open();
		int width=space.getWidth();
		int height=space.getHeight();

		while(true) {
			// Haut gauche a droite
			while(robi.getX() < width-robi.getWidth()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				width = space.getWidth();
				height = space.getHeight();
				robi.setX(robi.getX()+1);
				robi.setY(0);
				robi.setColor(new Color((int) (Math.random() * 0x1000000)));

			}
			// Droite haut a bas
			while(robi.getY() < height-robi.getHeight()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				width = space.getWidth();
				height = space.getHeight();
				robi.setX(width-robi.getWidth());
				robi.setY(robi.getY()+1);
				robi.setColor(new Color((int) (Math.random() * 0x1000000)));

			}
			// Bas droite a gauche
			while(robi.getX()>0) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				width = space.getWidth();
				height = space.getHeight();
				robi.setX(robi.getX()-1);
				robi.setY(height-robi.getHeight());
				robi.setColor(new Color((int) (Math.random() * 0x1000000)));

			}
			// Gauche bas a haut
			while(robi.getY()>0) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				width = space.getWidth();
				height = space.getHeight();
				robi.setX(0);
				robi.setY(robi.getY()-1);
				if(robi.getY()>height) {
					robi.setY(height-robi.getHeight());
				}
				robi.setColor(new Color((int) (Math.random() * 0x1000000)));
			}
		}

	}

	public static void main(String[] args) {
		new Exercice1_0();
	}

}