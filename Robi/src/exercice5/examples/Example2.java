package exercice5.examples;

import exercice6.Exercice6;

public class Example2 {
	
	/* 
	 * Ajoute un script newSquare  à Rect 
	 * newSquare est un constructeur qui prend en argument une largeur et retourne un carre
	 * On ne voit que la fenetre par défaut, vide
	 * 
	 * Le constructeur de fonctionne pas car je n'ai pas pu faire en sorte d'ajouter un script à une reference de classe
	 * De plus, l'opérateur return (le ^) n'est pas du tout mis en oeuvre
	 * 
	 * Pour permettre à ce type de script de fonctionner, il faudrait revoir la conception de la façon suivante :
	 * bla bla constructeur bla bla
	 * bla bla bla le return (^) pourrait être naturellement traité par l'interpréteur bla bla 
	 * 
	 */
	String script = "" 

			
			//parse (SNode -> contents (0 = space, 1 = add, 2 = robi), children {0 : (SNode -> contents (0 = rect.class, 1 = new))) 
			//+ "(space add robi (rect.class ! new))"
			//DETAIL DU PARSE : 
			
			// parse script ->
			/*
			SNode parent ( (script (	( Rect addScript newSquare ( (self w) ((^ (self new) setDim w w) ) ) ) ( space add robi ( Rect newSquare 55 ) ) ) )
				parent.children : 2 
				0 -> script (LEAF)
				1 -> (	( Rect addScript newSquare ( (self w) ((^ (self new) setDim w w) ) ) ) ( space add robi ( Rect newSquare 55 ) ) )
				SNode node1 
					node1.children = 2
					0 ->  (Rect addScript newSquare ( (self w) ((^ (self new) setDim w w) ) ) )
					1 ->  (space add robi ( Rect newSquare 55 ) )
					SNode node1.0
						node1.0.children = 2
						0 -> Rect addScript newSquare (LEAF)
						1 -> ( (self w) ((^ (self new) setDim w w) ) )
						SNode node1 
							node1.children = 2
							0 -> (self w) (LEAF)
							1 -> ((^ (self new) setDim w w) )
							SNode node1 
								node1.children = ?
						
			
			*/

			+ " (script ( "
			+ "		( Rect addScript newSquare ( (self w) ((^ (self new) setDim w w) ) ) ) "
			+ "		( space add robi ( Rect newSquare 55 ) )"
			+ " ) )";
	
	public  void launch() {
		Exercice6 exo = new Exercice6();
		exo.oneShot(script);
	}
	
	public static void main(String[] args) {
		new Example2().launch();
	}
}
  