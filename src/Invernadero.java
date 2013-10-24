/**
 * @author Héctor Martínez Matías
 */

public class Invernadero {
	private int id;
	private Sensor s1;
	private Sensor s2;
	
	public Invernadero(int id) {
		this.id = id;
		s1 = new Sensor("T");
		s2 = new Sensor("H");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Sensor getS1() {
		return s1;
	}

	public void setS1(Sensor s1) {
		this.s1 = s1;
	}

	public Sensor getS2() {
		return s2;
	}

	public void setS2(Sensor s2) {
		this.s2 = s2;
	}
	

}
