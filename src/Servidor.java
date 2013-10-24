/**
 * @author Héctor Martínez Matías
 */

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Servidor {

	public static void main(String[] args) {
		String numpuerto="";
		ArrayList<Invernadero> invernaderos = new ArrayList<Invernadero>();
		
		try {	
			if (args.length < 1)  //Comprobamos que el numero de argumentos sera el adecuado (puerto introducido)
				mostrarError(); 
			else
				numpuerto = args[0];
				ServerSocket skServidor = new ServerSocket(Integer.parseInt(numpuerto));
			    System.out.println("El puerto de escucha es: " + numpuerto);		    
			    mantenerConexion(skServidor, invernaderos);
		}	
		catch(Exception e) {}		
	}
			
	public static void mostrarError(){//Si es incorrecto el num de argumentos mostramos error y salimos. 	
		System.out.println("Error: Falta indicar el puerto de escucha del servidor");
		System.out.println("Nomenclatura: java Servidor puerto");
		System.exit (1);	
	}
	
	public static void mantenerConexion(ServerSocket skServidor, ArrayList<Invernadero> invernaderos) throws IOException{
		for(;;) { //Este bucle hace que el servidor esté a la espera.
			Socket skCliente = skServidor.accept(); 
	        System.out.println("---------------");
	        System.out.println("Sirviendo datos");
	        System.out.println("---------------");
	        Thread t = new hiloServidor(skCliente, invernaderos); //Creamos un hilo con el array de invernaderos que se usarán.
	        t.start();
		}		
	}
}
