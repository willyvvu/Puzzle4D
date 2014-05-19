/**
 * RotationHelper.java
 * 
 * Helps create rotations in 4D
 * 
 * Written Apr 14, 2014.
 * 
 * @author William Wu
 * 
 */
public class RotationHelper {
	private static final Quaternion tempQ = new Quaternion();
	private static final Quaternion tempQ2 = new Quaternion();

	/**
	 * Turns the provided quaternions into a 4D rotation quaternions.
	 * 
	 * @param quaternion
	 * @return
	 */
	public static void makeRotation(Quaternion quaternion,
			Quaternion quaternion2, double xy, double xz, double xw, double yz,
			double yw, double zw, double r) {
		quaternion.set((xw + yz), (yw - xz), (zw + xy), r).normalize();
		quaternion2.set((xw - yz), (yw + xz), (zw - xy), r).normalize();
	}

	/**
	 * Rotate the given quaternions.
	 * 
	 * @param quaternion
	 * @return
	 */
	public static void rotate(Quaternion quaternion, Quaternion quaternion2,
			double xy, double xz, double xw, double yz, double yw, double zw,
			double r) {
		makeRotation(tempQ, tempQ2, xy, xz, xw, yz, yw, zw, r);
		quaternion.multiplyBeforeQuaternion(tempQ);
		quaternion2.multiplyQuaternion(tempQ2);
	}

	/**
	 * Makes the quaternion into an the axis provided
	 * 
	 * @param axis
	 * @param quaternion
	 * @return
	 */
	public static Quaternion makeDirection(int axis, Quaternion quaternion) {
		quaternion.set(
				axis == Piece4.X_POS ? 1 : axis == Piece4.X_NEG ? -1 : 0,
				axis == Piece4.Y_POS ? 1 : axis == Piece4.Y_NEG ? -1 : 0,
				axis == Piece4.Z_POS ? 1 : axis == Piece4.Z_NEG ? -1 : 0,
				axis == Piece4.W_POS ? 1 : axis == Piece4.W_NEG ? -1 : 0);
		return quaternion;
	}

	/**
	 * Returns the direction that the quaternion represents.
	 * 
	 * @param quaternion
	 * @return
	 */
	public static int getDirection(Quaternion quaternion) {
		double max = Math.max(
				Math.max(Math.abs(quaternion.getX()),
						Math.abs(quaternion.getY())),
				Math.max(Math.abs(quaternion.getZ()),
						Math.abs(quaternion.getW())));
		return Math.abs(quaternion.getX()) == max ? (quaternion.getX() < 0 ? Piece4.X_NEG
				: Piece4.X_POS)
				: Math.abs(quaternion.getY()) == max ? (quaternion.getY() < 0 ? Piece4.Y_NEG
						: Piece4.Y_POS)
						: Math.abs(quaternion.getZ()) == max ? (quaternion
								.getZ() < 0 ? Piece4.Z_NEG : Piece4.Z_POS)
								: Math.abs(quaternion.getW()) == max ? (quaternion
										.getW() < 0 ? Piece4.W_NEG
										: Piece4.W_POS) : Piece4.X_POS;

	}
}
