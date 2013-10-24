/**
 * @author Héctor Martínez Matías
 */

public class Sensor {
	
	private String tipo;
	private float valor;
	
	public Sensor(String tipo) {
		this.tipo = tipo;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public float getValor() {
		return valor;
	}
	
	public void setValor(float valor) {
		this.valor = valor;
	}	
	
}
