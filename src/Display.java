import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Display.java
 * 
 * 
 * 
 * Written Apr 14, 2014.
 * 
 * @author William Wu
 * 
 */
public class Display {
	private static Quaternion tempQ = new Quaternion();
	private static Vector2 tempV2 = new Vector2();
	private static Vector2 tempV2_2 = new Vector2();
	public long lastTick = 0;
	protected PlayerGrid4 grid;
	protected int offset = 0;
	protected JFrame frame;

	public Display() {
		grid = new PlayerGrid4();

		frame = new JFrame("Puzzle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		frame.setLocation(10, 10);
		// frame.setResizable(false);
		frame.add(new DrawingComponent());
		frame.setVisible(true);
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				keyPressed(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				offset = (e.isShiftDown() ? 1 : 0)
						| (e.isControlDown() ? 2 : 0) | (e.isAltDown() ? 4 : 0);
			}
		});
	}

	public static void main(String[] args) {
		new Display();
	}

	/**
	 * A component to draw the puzzle and its pieces.
	 * 
	 */
	class DrawingComponent extends JComponent {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7001194846661552284L;
		/**
		 * 
		 */
		private int width = 0;
		private int height = 0;
		private static final int pieceSelectDistance = 80;
		private static final double pieceInnerWidth = 40;
		private static final double piecePerspective = 0.45;
		private static final int gridInnerWidth = 80;
		private static final int gridOuterWidth = 220;
		private static final int gridBorder = 230;
		private final Color[] sideColors = { new Color(0xEEEE00),
				new Color(0x00EE00), new Color(0xFFAA00), new Color(0x0000FF) };
		private Color lineColor = new Color(0xAAAAAA);
		private Color validColor = new Color(0x0000FF);
		private Color invalidColor = new Color(0xFF0000);
		private Stroke defaultStroke = new BasicStroke(4);
		private Stroke indicatorStroke = new BasicStroke(2);
		private Stroke lineStroke = new BasicStroke(1);
		private int pointWidth = 14;
		private int circleWidth = 20;
		private int indicatorWidth = 40;
		private double bankRadius = 300;
		private Piece4 selectedPiece = null;
		private Vector2 mousePosition = new Vector2();
		private double[] mouseRotation = { 0, 0, 0, 0, 0, 0 };
		private double mouseDegrees = 0;
		private boolean rotating = false, fromGrid = false, toGrid = false;
		private int validPositions = 0, invalidPositions = 0,
				emptyPositions = 0;
		private Vector2 lastLocation = new Vector2();
		private double oscillate = 0;
		private boolean actionPerformed = false;
		private int direction = 0;

		/**
		 * 
		 */
		public DrawingComponent() {
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					if (selectedPiece != null) {
						if (rotating) {
							for (int i = 0; i < mouseRotation.length; i++) {
								mouseRotation[i] = Math
										.floor((mouseRotation[i] + Math.PI / 4)
												/ (Math.PI / 2))
										* (Math.PI / 2);
								RotationHelper.rotate(selectedPiece
										.getRotation(), selectedPiece
										.getRotation2(),
										i == 0 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 1 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 2 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 3 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 4 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 5 ? Math.sin(mouseRotation[i] / 2)
												: 0, Math
												.cos(mouseRotation[i] / 2));
							}
							selectedPiece.getTempRotation().set(0, 0, 0, 1);
							selectedPiece.getTempRotation2().set(0, 0, 0, 1);
						} else {
							if (toGrid == true) {
								double minDistance = Double.POSITIVE_INFINITY;
								int xd = -1, yd = -1, zd = -1, wd = -1;
								for (int x = 0; x < grid.getSize()[0]; x++) {
									for (int y = 0; y < grid.getSize()[1]; y++) {
										for (int z = 0; z < grid.getSize()[2]; z++) {
											for (int w = 0; w < grid.getSize()[3]; w++) {
												int place = 1 << (x + 2 * y + 4
														* z + 8 * w);
												if ((emptyPositions & place) != 0) {
													gridLayout(tempV2, x, y, z,
															w);
													double distance = Math.max(
															Math.abs(tempV2
																	.getX()
																	- selectedPiece
																			.getPosition()
																			.getX()),
															Math.abs(tempV2
																	.getY()
																	- selectedPiece
																			.getPosition()
																			.getY()));
													if (distance < minDistance) {
														minDistance = distance;
														xd = x;
														yd = y;
														zd = z;
														wd = w;
													}
												}
											}
										}
									}
								}
								if (xd != -1) {
									grid.add(selectedPiece, xd, yd, zd, wd);
								}
							}
						}
					}
					selectedPiece = null;
					updateValidation(null);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						rotating = true;
					} else {
						rotating = false;
					}
					mousePosition.set(e.getX() - width / 2, e.getY() - height
							/ 2);
					for (int i = 0; i < mouseRotation.length; i++) {
						mouseRotation[i] = 0;
					}
					double minDistance = Double.POSITIVE_INFINITY;
					ArrayList<Piece4> pieces = grid.getPieces();
					for (int i = 0; i < pieces.size(); i++) {
						double distance = Math.max(
								Math.abs(pieces.get(i).getPosition().getX()
										- mousePosition.getX()),
								Math.abs(pieces.get(i).getPosition().getY()
										- mousePosition.getY()));
						if (distance < minDistance) {
							minDistance = distance;
							selectedPiece = pieces.get(i);
						}
					}
					if (minDistance > pieceSelectDistance) {
						selectedPiece = null;
						return;
					}
					fromGrid = !grid.getBank().contains(selectedPiece);
					toGrid = fromGrid;
					if (!rotating && fromGrid) {
						grid.remove(selectedPiece);
					}
					mouseDegrees = Math.atan2(mousePosition.getX()
							- selectedPiece.getPosition().getX(),
							mousePosition.getY()
									- selectedPiece.getPosition().getY());
					updateValidation(selectedPiece);
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (selectedPiece != null) {
						tempV2.set(e.getX() - width / 2, e.getY() - height / 2)
								.subtract(mousePosition);
						mousePosition.add(tempV2);
						double deltaDegrees = Math.atan2(mousePosition.getX()
								- selectedPiece.getPosition().getX(),
								mousePosition.getY()
										- selectedPiece.getPosition().getY())
								- mouseDegrees;
						mouseDegrees += deltaDegrees;
						if (rotating) {
							tempV2.multiplyScalar(0.01);
							if ((offset & 3) == 3) {
								mouseRotation[1] += tempV2.getX();// xz
								mouseRotation[4] += tempV2.getY();// yw
							} else if ((offset & 2) != 0) {// Ctrl only
								mouseRotation[5] -= deltaDegrees;// wz
							} else if ((offset & 1) != 0) {// Shift only
								mouseRotation[2] += tempV2.getX();// xw
								mouseRotation[3] += tempV2.getY();// yz
							} else {// No keys
								mouseRotation[0] -= deltaDegrees;// xy
							}
							selectedPiece.getTempRotation().set(0, 0, 0, 1);
							selectedPiece.getTempRotation2().set(0, 0, 0, 1);
							for (int i = 0; i < mouseRotation.length; i++) {
								RotationHelper.rotate(selectedPiece
										.getTempRotation(), selectedPiece
										.getTempRotation2(),
										i == 0 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 1 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 2 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 3 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 4 ? Math.sin(mouseRotation[i] / 2)
												: 0,
										i == 5 ? Math.sin(mouseRotation[i] / 2)
												: 0, Math
												.cos(mouseRotation[i] / 2));
							}
						} else {
							selectedPiece.getPosition().add(tempV2);
							toGrid = !(selectedPiece.getPosition().getX() < -gridBorder
									|| selectedPiece.getPosition().getX() > gridBorder
									|| selectedPiece.getPosition().getY() < -gridBorder || selectedPiece
									.getPosition().getY() > gridBorder);
						}
					}
				}
			});
			updateValidation(null);
		}

		/**
		 * @param graphics
		 *            the graphics context
		 * 
		 */
		public void paintComponent(Graphics graphics) {
			// Extract Graphics2D
			Graphics2D g = (Graphics2D) graphics;
			// adds some anti-Aliasing
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// Time!
			long currentTime = new Date().getTime();
			double timeElapsed = (lastTick == 0 ? 0 : (currentTime - lastTick)) * 0.001;
			lastTick = currentTime;

			width = this.getWidth();
			height = this.getHeight();
			Point loc = frame.getLocation();
			tempV2.set(loc.getX(), loc.getY());
			tempV2_2.copy(tempV2).subtract(lastLocation);
			int dir = (int) Math.signum(tempV2_2.getX());
			if (dir != 0 && direction != dir) {
				direction = dir;
				oscillate += 0.5;
			}
			if (oscillate > 1) {
				oscillate = 1;
				if (!actionPerformed) {
					grid.clear();
					updateValidation(null);
					actionPerformed = true;
				}
			}
			oscillate = Math.max(oscillate - timeElapsed, 0);
			if (oscillate < 0.1) {
				actionPerformed = false;
			}
			lastLocation.copy(tempV2);
			if (selectedPiece != null) {
				// WHEEE!
				// selectedPiece.rotate(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 3);
				if (toGrid != fromGrid) {
					g.setStroke(indicatorStroke);
					g.setColor(validColor);
					g.drawRect(-gridBorder + width / 2, -gridBorder + height
							/ 2, 2 * gridBorder, 2 * gridBorder);
				}
			}
			for (int x = 0; x < grid.getSize()[0]; x++) {
				for (int y = 0; y < grid.getSize()[1]; y++) {
					for (int z = 0; z < grid.getSize()[2]; z++) {
						for (int w = 0; w < grid.getSize()[3]; w++) {
							Piece4 piece = grid.getPiece(x, y, z, w);
							int place = 1 << (x + 2 * y + 4 * z + 8 * w);
							gridLayout(tempV2, x, y, z, w);
							g.setStroke(indicatorStroke);
							boolean draw = false;
							if (selectedPiece != null && !rotating
									&& (emptyPositions & place) != 0) {
								g.setColor(lineColor);
								draw = true;
							}
							if (selectedPiece != null && !rotating
									&& (validPositions & place) != 0) {
								g.setColor(validColor);
								draw = true;
							}
							if (selectedPiece == null
									&& (invalidPositions & place) != 0) {
								g.setColor(invalidColor);
								draw = true;
							}
							if (draw) {
								g.drawRect(width / 2 + (int) (tempV2.getX())
										- indicatorWidth / 2, height / 2
										+ (int) (tempV2.getY())
										- indicatorWidth / 2, indicatorWidth,
										indicatorWidth);
							}
							if (piece != null) {
								drawPiece(g, piece, tempV2.getX(),
										tempV2.getY());
							}
						}
					}
				}
			}
			boolean selectedAlreadySeen = false;
			for (int i = 0; i < grid.getBank().size(); i++) {
				Piece4 piece = grid.getBank().get(i);
				double theta = 2
						* Math.PI
						* (double) (i + (selectedAlreadySeen && !rotating
								&& toGrid ? 0 : 1))
						/ ((selectedPiece != null && !rotating && toGrid ? -1
								: 0) + grid.getBank().size());
				if (piece == selectedPiece) {
					selectedAlreadySeen = true;
				}
				double squareFactor = 1 / Math.cos((theta + Math.PI / 4)
						% (Math.PI / 2) - Math.PI / 4);
				drawPiece(g, piece,
						(int) (squareFactor * bankRadius * Math.cos(theta)),
						(int) (squareFactor * bankRadius * Math.sin(theta)));
			}
			this.repaint();
		}

		/**
		 * Lays out a given vector in the grid
		 * 
		 * @param tempV2
		 * @param x
		 * @param y
		 * @param z
		 * @param w
		 */
		private void gridLayout(Vector2 vector, int x, int y, int z, int w) {
			vector.set((x - 0.5) * gridInnerWidth + (w - 0.5) * gridOuterWidth,
					(y - 0.5) * gridInnerWidth + (z - 0.5) * gridOuterWidth);
		}

		/**
		 * Draws the given piece at the given location
		 * 
		 * @param g
		 * @param piece
		 */
		private void drawPiece(Graphics2D g, Piece4 piece, double x, double y) {
			tempV2.set(x, y);
			if (piece != selectedPiece) {
				piece.getPosition().lerp(tempV2, 0.1);
			}
			Vector2[] positions = piece.getPositions();

			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					for (int k = 0; k < 4; k++) {
						tempV2.set(0);
						tempV2_2.set(0);
						tempV2.add(positions[i % 2]);
						tempV2.add(positions[2 + i / 2]);
						tempV2.add(positions[4 + j % 2]);
						tempV2.add(positions[6 + j / 2]);
						tempV2_2.add(positions[k == 0 ? 1 - i % 2 : i % 2]);
						tempV2_2.add(positions[k == 1 ? 3 - i / 2 : 2 + i / 2]);
						tempV2_2.add(positions[k == 2 ? 5 - j % 2 : 4 + j % 2]);
						tempV2_2.add(positions[k == 3 ? 7 - j / 2 : 6 + j / 2]);
						drawLine(g, piece.getPosition(), tempV2, tempV2_2);
						// positions[k%2==0?i:(i) % 2 == 0 ? 1 + i : -1 + i],
						// positions[k/2==0?4+j:(4 + j) % 2 == 0 ? 5 + j : 3 +
						// j]);
					}
				}
			}
			for (int i = 0; i < positions.length; i++) {
				RotationHelper.makeDirection(i, tempQ);
				tempQ.multiplyBeforeQuaternion(piece.getRotation())
						.multiplyBeforeQuaternion(piece.getTempRotation())
						.multiplyQuaternion(piece.getRotation2())
						.multiplyQuaternion(piece.getTempRotation2());
				tempV2.set(tempQ.getX() + tempQ.getW() * piecePerspective,
						tempQ.getY() + tempQ.getZ() * piecePerspective)
						.multiplyScalar(pieceInnerWidth);
				positions[i].lerp(tempV2, 0.05);
				// Now, the points on a single piece are all set. Get the entire
				// piece movin'!
				tempV2.copy(positions[i]).add(piece.getPosition());
				drawPoint(g, tempV2, piece.getSides()[i]);
			}
			// tempV2.set(0, 0);
			// tempV2.setX(tempV2.getX() + x + w);
			// tempV2.setY(tempV2.getY() + y + z);
			// drawPoint(g, tempV2, 2);
		}

		/**
		 * Draws a given point representing a side
		 * 
		 * @param g
		 * @param point
		 * @param color
		 */
		private void drawPoint(Graphics2D g, Vector2 point, int color) {
			g.setColor(sideColors[Math.abs(color) - 1]);
			g.setStroke(defaultStroke);
			if (color < 0) {
				g.drawArc((int) point.getX() + width / 2 - circleWidth / 2,
						(int) point.getY() + height / 2 - circleWidth / 2,
						circleWidth, circleWidth, 0, 360);
			} else {
				g.fillArc((int) point.getX() + width / 2 - pointWidth / 2,
						(int) point.getY() + height / 2 - pointWidth / 2,
						pointWidth, pointWidth, 0, 360);
			}
		}

		/**
		 * Draws a 4D edge of a piece from point (a1,a2) to (b1,b2)
		 * 
		 * @param g
		 * @param a
		 * @param b
		 */
		private void drawLine(Graphics2D g, Vector2 origin, Vector2 a, Vector2 b) {
			g.setColor(lineColor);
			g.setStroke(lineStroke);
			g.drawLine((int) (width / 2 + origin.getX() + a.getX()),
					(int) (height / 2 + origin.getY() + a.getY()), (int) (width
							/ 2 + origin.getX() + b.getX()), (int) (height / 2
							+ origin.getY() + b.getY()));
		}

		/**
		 * Updates display of valid places
		 * 
		 * @param piece
		 */
		private void updateValidation(Piece4 piece) {
			validPositions = 0;
			emptyPositions = 0;
			invalidPositions = 0;
			for (int x = 0; x < grid.getSize()[0]; x++) {
				for (int y = 0; y < grid.getSize()[1]; y++) {
					for (int z = 0; z < grid.getSize()[2]; z++) {
						for (int w = 0; w < grid.getSize()[3]; w++) {
							int place = 1 << (x + 2 * y + 4 * z + 8 * w);
							Piece4 check = grid.getPiece(x, y, z, w);
							if ((check == null || check == piece)) {
								emptyPositions |= place;
								if (piece != null
										&& grid.fits(piece, x, y, z, w)) {
									validPositions |= place;
								}
							}
							if (piece == null) {
								if (!grid.works(x, y, z, w)) {
									// Red! Piece does not work
									invalidPositions |= place;
								}
							}
						}
					}
				}
			}
		}
	}
}
