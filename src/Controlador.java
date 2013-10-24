/**
 * @author Héctor Martínez Matías
 */

import java.io.*;
import java.net.*;

public class Controlador {

	public String leeSocket (Socket sk, String Datos) { //Lectura de datos en socket. 
		try {
			InputStream aux = sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			Datos = flujo.readUTF(); //Datos recibidos
		} catch (Exception e) {}
      return Datos; //numero de bytes leidos.
	}

	public void escribeSocket (Socket sk, String Datos) { //Escritura de datos en el socket
		try {
			OutputStream aux = sk.getOutputStream();
			DataOutputStream flujo= new DataOutputStream( aux );
			flujo.writeUTF(Datos); //p_datos representa el mensaje a enviar
		} catch (Exception e) {	}
		return;
	}
	
	public void pedirCrearInvernadero(String p_host, String p_puerto, int id) {
		String recibido = "";
		try { //Apertura de conexion con servidor. Pasamos nombre de pc y parametro creacion inveradero.
			Socket skControlador = new Socket(p_host, Integer.parseInt(p_puerto));
			escribeSocket(skControlador, "CREAR,"+id);
			recibido = leeSocket(skControlador, recibido); //Leo el mensaje recibido en el socket
			System.out.println("Creado invernadero número: " + recibido);	
		}catch(Exception e) {}
	}
	
	public void pedirDatosInvernadero(String p_host, String p_puerto) {
		String enviar = "";
		String recibido = "";
		try { //Abrimos la conexion con el servidor. Pasamos nombre de pc y parametro creacion inveradero.
			Socket skControlador = new Socket(p_host, Integer.parseInt(p_puerto));
			escribeSocket(skControlador, "DATOS");
			recibido = leeSocket(skControlador, recibido); //Leo el mensaje recibido en el socket
			System.out.println("Dato recibido: " + recibido);
			enviar = comprobarDatos(recibido); //Comprobamos si los valores son correctos.
			escribeSocket(skControlador, enviar); //Mandamos el mensaje al socket.
		}catch(Exception e) {}
	}
	
	public void solicitarDesconexion(String p_host, String p_puerto) { //Pide al servidor el la desconexion 
		String recibido = "";
		
		try {
			Socket skControlador = new Socket(p_host, Integer.parseInt(p_puerto));
			escribeSocket(skControlador, "Desconectar");
			recibido = leeSocket(skControlador, recibido); //Comprobamos que lo enviado al socket es lo correcto.
			System.out.println("Dato recibido: " + recibido);
		}catch(Exception e) {}
		
	}
	
	public String comprobarTemperatura(int dato)
	{
		String mensaje;
		if(dato < 5 || dato > 60) //Miro que no se sobrepasen los valores
			mensaje = "\033[1;33mREVISAR,";
		else { 
			if(!comprobarGoteo(dato)){
				mensaje = "\033[31mMAL,ACTIVAR GOTEO";
			}
			else if(!comprobarHumificador(dato)){
				mensaje = "\033[31mMAL,ACTIVAS DESHUMIDIFICADOR";
			}
			else
				mensaje = "\033[1;32mBIEN,";
		}
		return mensaje;	
	}
	
	
	public String comprobarHumedad(int dato)
	{
		String mensaje;
		if(dato < 10 || dato > 95) //Miro que no se sobrepasen los valores
			mensaje = "\033[1;33mREVISAR,";
		else { 
			if(!comprobarCalefactor(dato)) {
				mensaje = "\033[31mMAL,ACTIVAR CALEFACTOR";
			}
			else if(!comprobarVentilacion(dato)) {
				mensaje = "\033[31mMAL,ACTIVAR SISTEMA DE VENTILACIÓN Y APERTURA DE VENTANAS";
			}
			else
				mensaje = "\033[1;32mBIEN,";
		}
		return mensaje;	
	}
	
	public String comprobarDatos(String recibido) {
		String mensaje = "";
		String[] parametro = recibido.split("@");
		String id = parametro[0].substring(1); //id del invernadero
		String devolver = "RECIBIDO," +  id + "," + parametro[1] + ","; //RECIBIDO,id,tipoSensor,estado,mensaje
		int dato = Integer.parseInt(parametro[2]);
		
		if(parametro[1].equals("T")) //Comprobamos el sensor de temperatura
			mensaje = comprobarTemperatura(dato);
		
		if(parametro[1].equals("H")) //Comprobamos el sensor de humedad
			mensaje = comprobarHumedad(dato);
		
		devolver += mensaje; //añadimos el mensaje al String devuelto
		return devolver;
	}
	
	public boolean comprobarCalefactor(int temperatura) {
		boolean estado = true;
		if(temperatura < 25)
			estado = false;
		
		return estado;
	}
	public boolean comprobarVentilacion(int temperatura) {
		boolean estado = true;
		if(temperatura > 35)
			estado = false;
		
		return estado;
	}
		
	public boolean comprobarGoteo(int humedad) {
		boolean estado = true;
		if(humedad < 40)
			estado = false;
		
		return estado;
	}
	public boolean comprobarHumificador(int humedad) {
		boolean estado = true;
		if(humedad > 75)
			estado = false;
		
		return estado;
	}
	
	public void menu(String direccion, String puerto) {
		int opcion = 0;
		int id = 0;
		boolean salir = false;
		
		try {	
			while(!salir) { //Hasta que el Controlador seleccione Desconectar se mostrará el menú y se ejecutara la accion deseada
				opcion = mostrarMenu();
				
				if(opcion == 1){
					id++; //Incrementamos el id de invernaderos para cada uno.
					pedirCrearInvernadero(direccion, puerto, id);
				}
				else if(opcion == 2){
					pedirDatosInvernadero(direccion, puerto);
				}
				else if(opcion == 0){
					solicitarDesconexion(direccion, puerto);
					System.exit(0);
				}
				else{
					System.out.println("Opción Incorrecta, selecciones una opción valida.");
				}
			}
		}catch(Exception e) {}
		
		return;
	}
	
	public static int mostrarMenu() throws NumberFormatException, IOException{
		System.out.println("-----------------------");
		System.out.println("Control de Invernaderos");
		System.out.println("-----------------------");
		System.out.println("1. Crear Invernaderos");
		System.out.println("2. Solicitar Datos de Invernaderos");
		System.out.println("0. Desconectar");
		System.out.print("Seleccione una acción ");
		InputStreamReader isr = new InputStreamReader(System.in); //Leemos la opcion seleccionada.
		BufferedReader br = new BufferedReader (isr);
		System.out.println();
		return (Integer.parseInt(br.readLine()));
	}
	
	public static void main(String[] args) {
		Controlador cl = new Controlador();
		
		if (args.length < 2) //Si no se introduce direccion o puerto, mostramos error.
			mostrarError();
		else		
			cl.menu(args[0],args[1]);
	}
	
	public static void mostrarError(){
		System.out.println ("Error: Falta indicar la dirección o el puerto de escucha del servidor");
		System.out.println ("Nomenclatura: java Controlador direccion puerto");
		System.exit(-1);
		
	}
}
