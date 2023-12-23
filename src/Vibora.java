import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Vibora extends JFrame implements Runnable, KeyListener {
	private ListaSegmentos ls = new ListaSegmentos();
	private int columna, fila; //columna y fila donde se encuentra la cabeza de la vibora	
	private int colfruta, filfruta; // columna y fila donde se encuentra la fruta	
	private boolean activo = true; // disponemos en false cuando finaliza el juego	
	private char direccion = 'd'; // Direcci�n de la v�bora 'd' derecha 'i' izquierda 's' sube 'b' baja
	private Thread hilo; //Hilo de nuestro programa
	private int crecimiento = 0; // indica la cantidad de cuadraditos que debe crecer la vibora
	private Image imagen; // Para evitar el parpadeo del repaint()
	private Graphics bufferGraphics;// Se dibuja en memoria para evitar parpadeo

	public Vibora() {
		//escuchamos los eventos de teclado para identificar cuando se presionan las teclas de flechas
		this.addKeyListener(this);
		//la vibora comienza con cuatro cuadraditos
		ls.insertarPrimero(1, 25);		
		ls.insertarPrimero(2, 25);
		ls.insertarPrimero(3, 25);
		ls.insertarPrimero(4, 25);		
		//indicamos la ubicacion de la cabeza de la vibora
		columna = 4; 
		fila = 25;
		//generamos la coordenada de la fruta
		colfruta = (int) (Math.random() * 50);
		filfruta = (int) (Math.random() * 50);
		//creamos el hilo y lo arrancamos (con esto se ejecuta el metodo run()
		hilo = new Thread(this);
		hilo.start();
	}

	@Override
	public void run() {
		while (activo) {
			try {
				//dormimos el hilo durante una d�cima de segundo para que no se desplace tan rapidamente la vibora
				Thread.sleep(100);
				//segun el valor de la variable direccion generamos la nueva posicion de la cabeza de la vibora
				if (direccion == 'd') {
					columna++;
				}
				if (direccion == 'i') {
					columna--;
				}
				if (direccion == 's') {
					fila--;
				}
				if (direccion == 'b') {
					fila++;
				}
				repaint();
				sePisa();
				//insertamos la coordenada de la cabeza de la vibora en la lista
				ls.insertarPrimero(columna, fila);
				
				if (this.verificarComeFruta()==false && this.crecimiento == 0) {
				//si no estamos en la coordenada de la fruta y no debe crecer la vibora borramos el ultimo nodo de la lista
				//esto hace que la lista siga teniendo la misma cantidad de nodos	
					ls.borrarUltimo();
				} else {
					//Si creciento es mayor a cero es que debemos hacer crecer la vibora 
					if (this.crecimiento > 0)
						this.crecimiento--;
				}
				verificarFin();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//controlamos si la cabeza de la vibora se encuentra dentro de su cuerpo
	private void sePisa() {
		if (ls.existe(columna, fila)) {
			activo = false;
			setTitle("Perdiste");
		}
	}

	//controlamos si estamos fuera de la region del tablero
	private void verificarFin() {		
		if (columna < 0 || columna >= 50 || fila < 0 || fila >= 50) {
			activo = false;
			setTitle("Perdiste");
		}
	}

	private boolean verificarComeFruta() {
		if (columna == colfruta && fila == filfruta) {
			colfruta = (int) (Math.random() * 50);
			filfruta = (int) (Math.random() * 50);
			this.crecimiento = 10;
			return true;
		} else
			return false;
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (ls.raiz != null) {
			if (imagen == null) {
				imagen = createImage(this.getSize().width,
						this.getSize().height);
				bufferGraphics = imagen.getGraphics();
			}
			//borramos la imagen de memoria
			bufferGraphics.clearRect(0, 0, getSize().width, getSize().width);
			// dibujar recuadro
			bufferGraphics.setColor(Color.red);
			bufferGraphics.drawRect(20, 50, 500, 500);
			// dibujar vibora
			ListaSegmentos.Nodo reco = ls.raiz;
			while (reco != null) {
				bufferGraphics.fillRect(reco.x * 10 + 20, 50 + reco.y * 10, 8, 8);
				reco = reco.sig;
			}
			// dibujar fruta
			bufferGraphics.setColor(Color.blue);
			bufferGraphics.fillRect(colfruta * 10 + 20, filfruta * 10 + 50, 8, 8);
			g.drawImage(imagen, 0, 0, this);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			direccion = 'd';
		}
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			direccion = 'i';
		}
		if (arg0.getKeyCode() == KeyEvent.VK_UP) {
			direccion = 's';
		}
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			direccion = 'b';
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String[] args) {
		Vibora f = new Vibora();
		f.setSize(600, 600);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	

	
	//Lista que almacena cada uno de los segmentos de la vibora (se almacena la columna y fila de cada segmento)

	class ListaSegmentos {
		public class Nodo {
			int x, y;
			Nodo sig, ant;
		}

		Nodo raiz;

		public void insertarPrimero(int x, int y) {
			Nodo nuevo = new Nodo();
			nuevo.x = x;
			nuevo.y = y;
			if (raiz == null) {
				raiz  = nuevo;
			} else {
				nuevo.sig = raiz;
				raiz.ant = nuevo;
				raiz = nuevo;
			}
		}

		public void borrarUltimo() {
			if (raiz!=null) {
				if (raiz.sig==null)
					raiz=null;
				else
				{
					Nodo reco=raiz;				
					while (reco.sig.sig!=null)
					  reco=reco.sig;
					reco.sig=null;
				}				
			}
		}

		public boolean existe(int col, int fil) {
			Nodo reco = raiz;
			while (reco != null) {
				if (reco.x == col && fil == reco.y)
					return true;
				reco = reco.sig;
			}
			return false;
		}
	}
	
}