package exercice5.examples;


import java.awt.Color;
import exercice6.Exercice6;

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

import exercice5.Exercice5;
import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;


public class Example1 {
	
	/* 
	 * Ajoute un rectangle robi avec ses propri�t�s par d�faut
	 * On doit voir un rectangle bleu en (0,0)
	 * 
	 */
	String script = "(space setDim 1000 750)\n"
			+ "(space add robi (rect.class new))\n"
			+ "(space.robi setColor black)\n"
			+ "(space.robi setDim 470 250)\n"
			+ "(space.robi translate 20 10)\n"
			+ "(space.robi add im1 (image.class new alien.gif))\n"
			+ "(space.robi add im2 (image.class new alien.gif))\n"
			+ "(space.robi.im1 translate 0 75)\n"
			+ "(space.robi.im2 translate 400 75)\n"
			+ "(space sleep 150)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space sleep 50)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space sleep 50)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space sleep 50)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space sleep 50)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space sleep 50)\n"
			+ "(space.robi.im1 translate 30 0)\n"
			+ "(space.robi.im2 translate -30 0)\n"
			+ "(space.robi add ex (image.class new explosion.gif))\n"
			+ "(space.robi.ex translate 175 55)\n"
			+ "(space.robi setColor red)";

	
	
	public  void launch() {
		Exercice5 exo = new Exercice5();
		exo.oneShot(script);
	}
	
	public static void main(String[] args) {
		new Example1().launch();
	}
	
}
  