/**
 * Piece4.java
 * 
 * A 4 dimensional piece
 * 
 * Written Apr 14, 2014.
 * 
 * @author William Wu
 * 
 */
public class Piece4 {
	private static Quaternion tempQ = new Quaternion();
	private static Quaternion tempQ2 = new Quaternion();
	private static Quaternion tempQ3 = new Quaternion();

	public static void main(String[] args) {
		// Quaternion test = new Quaternion(1, 2, 3, 4);
		// Quaternion rotation = new Quaternion(0, 0, 0, 1);
		// Quaternion rotation2 = new Quaternion(0, 0, 0, 1);
		// rotation.normalize();
		// rotation2.normalize();
		// test.multiplyBeforeQuaternion(rotation).multiplyQuaternion(rotation2);
		// System.out.println(test);
		Piece4 test = new Piece4(1, 2, 3, 4, 5, 6, 7, 8);
		System.out.println(test);
	}

	private int[] sides = new int[8];
	// Indices/axes
	public final static int X_POS = 0;
	public final static int X_NEG = 1;
	public final static int Y_POS = 2;
	public final static int Y_NEG = 3;
	public final static int Z_POS = 4;
	public final static int Z_NEG = 5;
	public final static int W_POS = 6;
	public final static int W_NEG = 7;
	// In the order x+,x-,y+,y-,z+,z-,w+,w-
	private Vector2[] positions = new Vector2[8];
	private Quaternion rotation = new Quaternion(0, 0, 0, 1);
	private Quaternion rotation2 = new Quaternion(0, 0, 0, 1);
	private Quaternion tempRotation = new Quaternion(0, 0, 0, 1);
	private Quaternion tempRotation2 = new Quaternion(0, 0, 0, 1);
	private Vector2 position = new Vector2();

	/**
	 * Creates a new Piece4.
	 */
	public Piece4() {
		for (int i = 0; i < positions.length; i++) {
			positions[i] = new Vector2();
		}
	}

	/**
	 * Creates a new Piece4 with a random number of initialized connections.
	 */
	public Piece4(int connections) {
		this((int) (connections * Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1), (int) (connections
				* Math.random() + 1)
				* (Math.random() < 0.5 ? 1 : -1));
	}

	/**
	 * Creates a new Piece4 with the given sides.
	 */
	public Piece4(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
		this();
		sides[0] = s1;
		sides[1] = s2;
		sides[2] = s3;
		sides[3] = s4;
		sides[4] = s5;
		sides[5] = s6;
		sides[6] = s7;
		sides[7] = s8;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "   " + this.getSide(Y_POS) + "\n   " + this.getSide(Z_POS)
				+ "\n" + this.getSide(X_NEG) + " " + this.getSide(W_NEG) + " "
				+ this.getSide(W_POS) + " " + this.getSide(X_POS) + "\n   "
				+ this.getSide(Z_NEG) + "\n   " + this.getSide(Y_NEG);
	}

	/**
	 * @return the rotation
	 */
	public Quaternion getRotation() {
		return rotation;
	}

	/**
	 * @return the rotation
	 */
	public Quaternion getRotation2() {
		return rotation2;
	}

	/**
	 * @return the sides
	 */
	public int[] getSides() {
		return sides;
	}

	/**
	 * @return the positions
	 */
	public Vector2[] getPositions() {
		return positions;
	}

	public Piece4 rotate(double xy, double xz, double xw, double yz, double yw,
			double zw, double r) {
		RotationHelper.makeRotation(tempQ, tempQ2, xy, xz, xw, yz, yw, zw, r);
		this.getRotation().multiplyBeforeQuaternion(tempQ);
		this.getRotation2().multiplyQuaternion(tempQ2);
		return this;
	}

	/**
	 * 
	 * @param axis
	 * @return the integer side
	 */
	public int getSide(int axis) {
		tempQ.copy(rotation).inverse();
		tempQ2.copy(rotation2).inverse();
		RotationHelper.makeDirection(axis, tempQ3);
		tempQ3.multiplyBeforeQuaternion(tempQ).multiplyQuaternion(tempQ2);
		return this.sides[RotationHelper.getDirection(tempQ3)];
	}

	/**
	 * @return the position
	 */
	public Vector2 getPosition() {
		return position;
	}

	/**
	 * @return the tempRotation
	 */
	public Quaternion getTempRotation() {
		return tempRotation;
	}

	/**
	 * @return the tempRotation2
	 */
	public Quaternion getTempRotation2() {
		return tempRotation2;
	}
}
