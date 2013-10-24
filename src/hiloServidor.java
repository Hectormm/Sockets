/**
 * @author Héctor Martínez Matías
 */

import java.lang.Exception;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.*;

public class hiloServidor extends Thread {

	private Socket skCliente;
	private ArrayList<Invernadero> inv;
	
	public hiloServidor(Socket cliente, ArrayList<Invernadero> invernaderos) {
		this.skCliente = cliente;
		this.inv = invernaderos;
	}
	
	public String obtenerDatos() { //Generamos datos para los sensores aleatorios.
		String tipoSensor[] = {"T", "H"};
		String Sensor = "";
		String fechaHoraActual = "";
		
		int id = (int) (Math.random()*inv.size()+1); 	//Solicitamos un invernadero cualquiera.
		int temperatura = (int) (Math.random()*105+1);	//Temperatura cualquiera.
		fechaHoraActual = obtenerFechaHora();			//Cogemos la fecha actua.
		int azarSensor = (int) (Math.random()*2+1); 	//Igual con el sensor
		if(azarSensor == 1)
			Sensor = tipoSensor[0];
		else
			Sensor = tipoSensor[1];
			
		return ("#" + id + "@" + Sensor + "@" + temperatura + "@" + fechaHoraActual + "#");
	}
	


	//Metodo que realiza la tarea que nos da el Controlador
	public String realizarPedido(String p_Cadena) throws FileNotFoundException, IOException {
		String[] pedido = p_Cadena.split(",");
		String devolver = "";

		if(pedido[0].compareTo("CREAR") == 0) {
			int id = Integer.parseInt(pedido[1]);
			inv.add(new Invernadero(id));
			System.out.println("Servidor: Creado invernadero número: " + id);
			devolver += id;
		}
		else if(pedido[0].compareTo("DATOS") == 0) {
			devolver += obtenerDatos();
			System.out.println("Servidor: Dato leido: " + devolver);
		}
		else if(pedido[0].compareTo("Desconectar") == 0) {
			devolver += pedido[0];
			System.out.println("Servidor: Dato leido: " + devolver);
		}
		else if(pedido[0].compareTo("RECIBIDO") == 0) {
			if(pedido[3].equals("REVISAR")) 
				System.out.println("Servidor: Revisar el sensor " + pedido[2] + " del invernadero número" + pedido[1]);

			devolver += "OK";
		}
		
		return devolver;
	}
	
	public void comprobarSensores(String sensor, String cadena, int id, String estado){
		if(sensor.equals("T")) {//Comprobamos sensor de temperatura
			if(isSensorOK(id, estado)) 
				System.out.println("Servidor: Desactivado el sensor T del invernadero número " + id);
			else  //Mostramos la activacion correspondiente.
				System.out.println("Servidor: " + cadena + " del Invernadero número " + id);				
		}
		else if(sensor.equals("H")) {//Comprobamos sensor de humedad
			if(isSensorOK(id, estado)) 
				System.out.println("Servidor: Desactivado el sensor H del Invernadero número" + id);
			else  //Mostramos la activacion correspondiente.
				System.out.println("Servidor: " + cadena + " del Invernadero número " + id);				
		}	
	}
	
	public String buscarInvernadero(String[] datos, ArrayList<Invernadero> inv) {
		String devolver = "";
		boolean encontrado = false;
		int id = Integer.parseInt(datos[1]);

		for(int i = 0; i < inv.size() && !encontrado; i++) { //Buscamos el invernadero correspondiente.
			if(id == inv.get(i).getId()) {
				encontrado = true;
				devolver += "OK";
				comprobarSensores(datos[2], datos[4], id, datos[3]); //Comprobamos los sensores.
			}
		}
		return devolver;
	}
	
	public boolean isSensorOK(int id, String estado) { 
		boolean aDevolver = false;
		if(estado.equals("BIEN")) 
			aDevolver = true;

		return aDevolver;
	}
	
	
    public void run() {
		String recibido="";
		String opcion = "";
		String devolver;
		boolean salir = false;
		
        try {
			while(!salir) {
				recibido = this.leeSocket (skCliente, recibido);
				System.out.println("Servidor: Enviado en el socket: " +  recibido);
		        System.out.print("\033[0m");
				opcion = realizarPedido(recibido);				
				if(opcion.equals("Desconectar")) 
					salir = true;		
				devolver = "" + opcion;
				this.escribeSocket (skCliente, devolver);
			}
			System.out.println("Desconectando!");
			skCliente.close();
			System.exit(0);				
        }
        catch (Exception e) {}
     }
    
    
	public String obtenerFechaHora() { //Obtenemos la fecha y la hora para la traza
		Calendar c = new GregorianCalendar();		
		String dia = Integer.toString(c.get(Calendar.DATE)); //Obtenemos la fecha actual
		String mes = Integer.toString(c.get(Calendar.MONTH));
		String anyo = Integer.toString(c.get(Calendar.YEAR));
		String hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY)); //Obtenemos la hora actual
		String minutos = Integer.toString(c.get(Calendar.MINUTE));
		return (dia + "/" + mes + "/" + anyo + "@" + hora + ":" + minutos);
	}
	
	public String leeSocket (Socket sk, String Datos) { //Accede a los datos del socket	
		try {
			InputStream aux = sk.getInputStream();
			DataInputStream flujo = new DataInputStream( aux );
			Datos = new String();
			Datos = flujo.readUTF();
		}
		catch (Exception e) {}
		
      return Datos; //Numero de bytes leidos.
	}

	public void escribeSocket (Socket sk, String Datos) { //Escribe dato en el socket
		try {
			OutputStream aux = sk.getOutputStream();
			DataOutputStream flujo= new DataOutputStream( aux );
			flujo.writeUTF(Datos);      
		}
		catch (Exception e) {}
		
		return;
	}
	
	
}

