/**
 * This class serves as the action panel where the user
 * interacts with game and the game is active. The class extends
 * JPanel as we want to draw on the panel and implements a runnable
 * interface as we want to create a thread where the balls can update.
 * Another thread is created upon the first collision which checks for 
 * collisions. These two threads run constantly until the flag is made
 * false. In the threads the two methods are synchronized on a ball object
 * and we use wait and notify between them. Collision detect in thread2 waits
 * until the update method in this classes runnable has been finished and then
 * we notify and release our lock so collision detect can be called. Repaint
 * is also called after the updates. Mouse listeners are used to create the
 * first explosion and the placement of the first explosion. A number of flags are
 * used to protect the quality of the game and unintended clicks of the mouse.
 * 
 * We use three static structures for our game. The total score needs to belong to the class
 * as multiple gameThreads will be created but we only want one score. The ball list were
 * also declared as static but they could easily belong to each instance also. A temporary
 * score member is used as we need to hold our score for each level we play. Other object
 * members are used for access and linking information for easy flow.
 * 
 * Paint component is overridden to draw our game balls to the screen. I have used alpha
 * composite here to  effectively mimic the transparency of the real game.
 * 
 * Access to the ball lists need to be synchronized as well. Not only that I avoided using a
 * iterator object to successfully alter lists while iterating through them by creating methods
 * that accept the list and a ball/index that perform this function at an atomic level.
 * 
 * Refresh screen target is a method that is used to give accurate information to the user during
 * the gameThreads action. And checking the game status provides away to decided where we go from
 * here when the current gameThread is over.
 * 
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class GameThread extends JPanel implements MouseListener,
		MouseMotionListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static ArrayList<Ball> ballList = new ArrayList<Ball>();
	public static ArrayList<Ball> explodingBallList = new ArrayList<Ball>();
	public static int totalScore;

	private Random rand = new Random();
	private ArrayList<Color> colors;
	private Ball initialBall;

	private boolean firstIn = true;
	private boolean enterMouseMove = true;
	private boolean enteredMouseClicked = false;
	private boolean makeSureMouseMoved = false;
	volatile boolean running = true;

	// A thread object to start this runnable in
	private Thread run;
	// An object to provide a lock
	private Ball ballRef;

	// Keep track of target and number of exploded
	private int target;
	private int numOfBalls;
	private int numberOfExplodingBalls;
	private int tempScore;

	private LevelScreen levelScreen;

	private String message;
	private Font font;
	private Color color;

	private ProgressScreen progressScreen;

	public GameThread(LevelScreen levelScreen) {

		// Add listeners, associate the level with the current
		// game thread to set up the game level instance
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBackground(Color.BLACK);
		this.levelScreen = levelScreen;
		this.target = levelScreen.getTargetAmount();
		this.numOfBalls = levelScreen.getTotalNumberOfBalls();

		this.font = new Font("ARIAL", Font.BOLD, 20);
		this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

		// Initial information and start the game
		message = ("Explode " + this.getTarget() + " Balls");
		ballRef = new Ball(this);
		colors = new ArrayList<Color>();
		createColors();
		createBalls();
		repaint();

		run = new Thread(this);
		run.start();
	}

	public void refreshOnScreenTarget() {
		// Here we decide what message to output to player
		// Its based on the levels target and amount of balls
		// left to explode
		String result = null;
		String lastBall = "Last Ball";
		int updatedTarget = this.getTarget() - this.getNumberOfExplodingBalls();
		String updated = "Explode " + updatedTarget + " Balls";

		if (updatedTarget > 1) {
			result = updated;
		} else if (updatedTarget == 1) {
			result = lastBall;
		} else
			result = "Level Passed";

		this.setMessage(result);

	}

	public void createColors() {

		colors.add(Color.RED);
		colors.add(Color.BLUE);
		colors.add(Color.GREEN);
		colors.add(Color.YELLOW);
		colors.add(Color.ORANGE);
		colors.add(Color.CYAN);
		colors.add(Color.MAGENTA);
	}

	public void createBalls() {
		int randX = 0;
		int randY = 0;
		// We create as many balls as the level states and we
		// create each ball to start and move in random directions
		// we give it a random color from our list and a random
		// acceleration factor. Finally we add them to our data structure

		for (int i = 0; i < this.getNumOfBalls(); i++) {
			randX = rand.nextInt(776);
			randY = rand.nextInt(355);
			Ball ball = new Ball(randX, randY, 5, colors.get(rand.nextInt(7)),
					rand.nextInt(2)+1 , rand.nextBoolean(), rand.nextBoolean());
			addBall(ballList, ball);
		}
	}

	public void checkGameStatus() {

		ballList.clear();

		// Game is over as no exploding balls are left, we clear
		// the static ball list for the next level and check if we
		// have exploded enough balls, if we have we can set up a new level
		// and add the temporary score to our total score or
		// we repeat the same level

		if (this.getNumberOfExplodingBalls() >= this.getTarget()) {
			totalScore = totalScore + this.getTempScore();

			if (this.levelScreen.getLevelNumber() > 11) {
				progressScreen = new ProgressScreen(null, this,
						"HighScores.txt");
			} else {
				// Creating the new level screen object with the
				// next level parameter
				// nextLevelScreen = new LevelScreen(
				// this.levelScreen.getLevelNumber() + 1);

				// Here we launch the progress screen passing in the next level
				// screen and this panel to delete. The pass image is also
				// passed
				progressScreen = new ProgressScreen(new LevelScreen(
						this.levelScreen.getLevelNumber() + 1), this,
						"icon\\pass.png");
				GameSound.SUCCESS.play();
			}
		} else {
			progressScreen = new ProgressScreen(this.getLevelScreen(), this,
					"icon\\failure.png");
			GameSound.FAILURE.play();
		}
	}

	public void makeInitialBall(int x, int y) {
		// We get here when the player moves the mouse on the screen.
		// The ball is placed according to the mouse location
		// Its an all white, transparent bomb and we mark it
		// as the initial bomb. Flags are set so as multiple
		// movements do not create more than one of these initial bombs
		// The ball is added to the ball list so we can see it on screen
		Color c = new Color(1.0f, 1.0f, 1.0f, 0.1f);
		this.initialBall = new Ball(x, y, 5, c, 0, true, true);

		// Boolean is changes here as this member is used to exclude
		// the first explosion from being counted as a valid ball explosion
		this.initialBall.setInitialBall(true);
		firstIn = false;
		addBall(ballList, this.getInitialBall());
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// The following just helps the look of the drawn objects
		// and makes the balls look more natural
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);      

		g2d.setBackground(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Drawing score and game information
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(Integer.toString(tempScore), 690, 350);
		g2d.drawString(this.getMessage(), 10, 30);

		// Draw the balls with no transparency
		// then draw the exploding balls with 60% transparency
		// as they are exploding.
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f));
		for (int i = 0; i < ballList.size(); i++) {
			getBall(ballList, i).draw(g2d);
		}

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.4f));
		
		for (int i = 0; i < explodingBallList.size(); i++) {
			
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.4f));
			Ball ball = getBall(explodingBallList, i);
			ball.draw(g2d);

			// Here we just show the exploding score for each collision
			// in the center of the exploding ball
			g2d.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1.0f));
			g2d.setFont(new Font("ARIAL", Font.ITALIC, 10));
			g2d.setColor(color);
			g2d.drawString(Integer.toString(ball.getExplodingBallHitScore()),
					ball.getX() - 4, ball.getY());
		}
	}

	public void mouseMoved(MouseEvent e) {
		// Here we just turn the cursor into a hand when it
		// moves on the screen, we create the ball and then
		// we just update its position all other times until
		// the flag is made false in mouse clicked listener

		if (enterMouseMove) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if (firstIn) {
				makeInitialBall(e.getX(), e.getY());
			} else {
				initialBall.setX(e.getX());
				initialBall.setY(e.getY());
			}
			makeSureMouseMoved = true;
		}
		//repaint();
	}

	public void mouseClicked(MouseEvent arg0) {

		// Here we make sure the player has moved the mouse
		// and not just clicked right away. If he has we
		// just return and do nothing
		if (makeSureMouseMoved) {
			// Here we just check that the player has not clicked yet
			// as we do not want to start multiple explosions from the
			// first ball, we want it to happen only once so we can
			// start the first explosion and start a thread that deals
			// with collision detection only
			if (!enteredMouseClicked) {
				startTheFirstExplosion(this.getInitialBall());
				startAThreadForCollisionDetection();
			}
		} else
			return;
	}

	public void startAThreadForCollisionDetection() {
		// Here as an explosion has been initiated we can start an anonymous
		// runnable thread which is synchronized with a lock object ball
		// We just wait here until the ball structure has been updated with
		// movement
		// then we check for collisions after the update. We also refresh the
		// information
		// for the user during the game as the balls explode. This runs
		// until running is set false
		// which happens when the explode list is empty
		new Thread(new Runnable() {
			public void run() {
				while (running) {
					synchronized (ballRef) {
						try {
							ballRef.wait();
						} catch (InterruptedException e) {
						}
					}
					ballRef.collisionDetect();
					refreshOnScreenTarget();
					repaint();
				}
			}
		}).start();
	}

	public void startTheFirstExplosion(Ball initialBall) {
		// We just initiate an explosion here, swap the ball
		// into the explosion list, set its chain number to 0
		// as it is the root of the chain. We also alter booleans
		// in case the user erroneously clicks again
		enterMouseMove = false;
		enteredMouseClicked = true;
		addBall(explodingBallList, this.getInitialBall());
		removeBall(ballList, this.getInitialBall());
		initialBall.setCurrentlyExploding(true);
		initialBall.setChainNumber(0);
	}

	public void run() {
		// Here we start this games runnable thread which just sleeps, and
		// performs
		// a movement update which is synchronized with the ballRef lock. It
		// notifies
		// the collision detect thread it has updated and repaints the screen
		// then
		// passes the lock to the lock object in the other thread
		while (running) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (ballRef) {
				ballRef.updateBalls();
				ballRef.notify();
				repaint();
			}
		}
		// We get here when running is made false i.e. explode list is empty
		// So we call a method to check our progression in the game
		checkGameStatus();
	}


	// Here we define three methods for accessing the array-lists which
	// multiple threads require access. We just make each access atomic
	// by synchronizing access and using each operation one at a time
	// Try catch blocks are used to catch exceptions if the information
	// is not valid

	public synchronized Ball getBall(ArrayList<Ball> ballList, int i) {
		Ball drawBall = null;
		try {
			drawBall = ballList.get(i);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}

		return drawBall;
	}
	
	public synchronized int getNumberOfExplodingBalls() {
		return numberOfExplodingBalls;
	}

	public synchronized void setNumberOfExplodingBalls(
			int numberOfExplodingBalls) {
		this.numberOfExplodingBalls = numberOfExplodingBalls;
	}

	public synchronized void addBall(ArrayList<Ball> balls2, Ball grenade2) {
		balls2.add(grenade2);
	}

	public synchronized void removeBall(ArrayList<Ball> ballList,
			Ball removeThisBall) {
		try {
			ballList.remove(removeThisBall);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Ball getInitialBall() {
		return initialBall;
	}

	public void setInitialBall(Ball initialBall) {
		this.initialBall = initialBall;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public static int getTotalScore() {
		return totalScore;
	}

	public static void setTotalScore(int totalScore) {
		GameThread.totalScore = totalScore;
	}

	public int getTempScore() {
		return tempScore;
	}

	public void setTempScore(int tempScore) {
		this.tempScore = tempScore;
	}

	public int getNumOfBalls() {
		return numOfBalls;
	}

	public void setNumOfBalls(int numOfBalls) {
		this.numOfBalls = numOfBalls;
	}

	public static ArrayList<Ball> getBallList() {
		return ballList;
	}

	public static void setBallList(ArrayList<Ball> ballList) {
		GameThread.ballList = ballList;
	}

	public static ArrayList<Ball> getExplodingBallList() {
		return explodingBallList;
	}

	public static void setExplodingBallList(ArrayList<Ball> explodingBallList) {
		GameThread.explodingBallList = explodingBallList;
	}

	public ProgressScreen getProgressScreen() {
		return progressScreen;
	}

	public void setProgressScreen(ProgressScreen progressScreen) {
		this.progressScreen = progressScreen;
	}

	public LevelScreen getLevelScreen() {
		return levelScreen;
	}

	public void setLevelScreen(LevelScreen levelScreen) {
		this.levelScreen = levelScreen;
	}

	// public LevelScreen getNextLevelScreen() {
	// return nextLevelScreen;
	// }

	// public void setNextLevelScreen(LevelScreen nextLevelScreen) {
	// this.nextLevelScreen = nextLevelScreen;
	// }

	public void mouseDragged(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

}
