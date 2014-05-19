import java.util.ArrayList;
import java.util.Arrays;

/**
 * PlayerGrid4.java
 * 
 * A 4D grid for puzzle pieces within a player
 * 
 * Written Apr 14, 2014.
 * 
 * @author William Wu
 * 
 */
public class PlayerGrid4 {
	public static void main(String[] args) {

	}

	private int size[] = { 2, 2, 2, 2 };
	private Piece4[][][][] grid = new Piece4[getSize()[0]][getSize()[1]][getSize()[2]][getSize()[3]];
	private ArrayList<Piece4> bank = new ArrayList<Piece4>();

	/**
	 * Creates a new, random, {@link PlayerGrid4} with 4 connections
	 */
	public PlayerGrid4() {
		this(4);
	}

	/**
	 * Creates a new, random, {@link PlayerGrid4} with a given number of
	 * connections
	 */
	public PlayerGrid4(int connections) {
		for (int x = 0; x < getSize()[0]; x++) {
			for (int y = 0; y < getSize()[1]; y++) {
				for (int z = 0; z < getSize()[2]; z++) {
					for (int w = 0; w < getSize()[3]; w++) {
						Piece4 piece = new Piece4(connections);
						grid[x][y][z][w] = piece;
						Piece4 connect = getPiece(x - 1, y, z, w);
						if (connect != null) {
							piece.getSides()[Piece4.X_NEG] = -connect
									.getSides()[Piece4.X_POS];
						}
						connect = getPiece(x, y - 1, z, w);
						if (connect != null) {
							piece.getSides()[Piece4.Y_NEG] = -connect
									.getSides()[Piece4.Y_POS];
						}
						connect = getPiece(x, y, z - 1, w);
						if (connect != null) {
							piece.getSides()[Piece4.Z_NEG] = -connect
									.getSides()[Piece4.Z_POS];
						}
						connect = getPiece(x, y, z, w - 1);
						if (connect != null) {
							piece.getSides()[Piece4.W_NEG] = -connect
									.getSides()[Piece4.W_POS];
						}
					}
				}
			}
		}
		clear(0.5);
	}

	/**
	 * Creates a new {@link PlayerGrid4} with a given intial bank.
	 * 
	 * @param initialBank
	 */
	public PlayerGrid4(Piece4[] initialBank) {
		bank.addAll(Arrays.asList(initialBank));
	}

	/**
	 * Checks for a fit between two pieces
	 * 
	 * @param piece
	 * @param piece2
	 * @param axis
	 * @return
	 */
	public boolean fitsWith(Piece4 piece, Piece4 piece2, int axis) {
		if (piece == null || piece2 == null) {
			return true;
		}
		int axisPos = axis - axis % 2;
		int axisNeg = axis + 1;
		int posSide = piece.getSide(axisPos);
		int negSide = piece2.getSide(axisNeg);
		return Math.abs(posSide) == Math.abs(negSide)
				&& Math.signum(posSide) != Math.signum(negSide);
	}

	/**
	 * Adds a piece to the grid
	 * 
	 * @param piece
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return whether or not the piece was added
	 */
	public boolean add(Piece4 piece, int x, int y, int z, int w) {
		if (getBank().contains(piece) && canPlace(piece, x, y, z, w)) {
			getBank().remove(piece);
			setPiece(piece, x, y, z, w);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes a piece from the grid
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void remove(int x, int y, int z, int w) {
		Piece4 piece = getPiece(x, y, z, w);
		if (piece != null) {
			setPiece(null, x, y, z, w);
			getBank().add(piece);
		}
	}

	/**
	 * Removes a piece from the grid
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public void remove(Piece4 piece) {
		for (int x = 0; x < getSize()[0]; x++) {
			for (int y = 0; y < getSize()[1]; y++) {
				for (int z = 0; z < getSize()[2]; z++) {
					for (int w = 0; w < getSize()[3]; w++) {
						if (piece == grid[x][y][z][w]) {
							remove(x, y, z, w);
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * Clears the board
	 */
	public void clear() {
		for (int x = 0; x < getSize()[0]; x++) {
			for (int y = 0; y < getSize()[1]; y++) {
				for (int z = 0; z < getSize()[2]; z++) {
					for (int w = 0; w < getSize()[3]; w++) {
						remove(x, y, z, w);
					}
				}
			}
		}
	}

	/**
	 * Clears the board with a random percentage of pieces removed
	 * 
	 * @param d
	 */
	private void clear(double d) {
		for (int x = 0; x < getSize()[0]; x++) {
			for (int y = 0; y < getSize()[1]; y++) {
				for (int z = 0; z < getSize()[2]; z++) {
					for (int w = 0; w < getSize()[3]; w++) {
						if (Math.random() < d) {
							bank.add(grid[x][y][z][w]);
							grid[x][y][z][w] = null;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks to see if a piece can be placed in the grid
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public boolean canPlace(Piece4 piece, int x, int y, int z, int w) {
		return getPiece(x, y, z, w) == null;
	}

	/**
	 * Checks to see if a piece satisfies grid constraints
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public boolean fits(Piece4 piece, int x, int y, int z, int w) {
		if (isValid(x, y, z, w)) {
			return fitsWith(getPiece(x - 1, y, z, w), piece, Piece4.X_POS)
					&& fitsWith(piece, getPiece(x + 1, y, z, w), Piece4.X_POS)
					&& fitsWith(getPiece(x, y - 1, z, w), piece, Piece4.Y_POS)
					&& fitsWith(piece, getPiece(x, y + 1, z, w), Piece4.Y_POS)
					&& fitsWith(getPiece(x, y, z - 1, w), piece, Piece4.Z_POS)
					&& fitsWith(piece, getPiece(x, y, z + 1, w), Piece4.Z_POS)
					&& fitsWith(getPiece(x, y, z, w - 1), piece, Piece4.W_POS)
					&& fitsWith(piece, getPiece(x, y, z, w + 1), Piece4.W_POS);
		} else {
			return false;
		}
	}

	/**
	 * Checks to see if a piece in the grid satisfies grid constraints
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public boolean works(int x, int y, int z, int w) {
		Piece4 piece = getPiece(x, y, z, w);
		if (piece != null) {
			return fits(piece, x, y, z, w);
		} else {
			return true;
		}
	}

	/**
	 * Checks if a given spot is in the grid
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public boolean isValid(int x, int y, int z, int w) {
		return x >= 0 && x < getSize()[0] && y >= 0 && y < getSize()[1]
				&& z >= 0 && z < getSize()[2] && w >= 0 && w < getSize()[3];
	}

	/**
	 * Gets the piece at a given location
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public Piece4 getPiece(int x, int y, int z, int w) {
		if (isValid(x, y, z, w)) {
			return grid[x][y][z][w];
		} else {
			return null;
		}
	}

	/**
	 * Sets the piece at a given location
	 * 
	 * @param piece
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @return
	 */
	public Piece4 setPiece(Piece4 piece, int x, int y, int z, int w) {
		if (isValid(x, y, z, w)) {
			return grid[x][y][z][w] = piece;
		} else {
			return null;
		}
	}

	/**
	 * @return the size
	 */
	public int[] getSize() {
		return size;
	}

	/**
	 * @return the bank
	 */
	public ArrayList<Piece4> getBank() {
		return bank;
	}

	/**
	 * @return all the pieces
	 */
	public ArrayList<Piece4> getPieces() {
		ArrayList<Piece4> pieces = new ArrayList<Piece4>(bank);
		for (int x = 0; x < getSize()[0]; x++) {
			for (int y = 0; y < getSize()[1]; y++) {
				for (int z = 0; z < getSize()[2]; z++) {
					for (int w = 0; w < getSize()[3]; w++) {
						Piece4 piece = grid[x][y][z][w];
						if (piece != null) {
							pieces.add(piece);
						}
					}
				}
			}
		}
		return pieces;
	}
}