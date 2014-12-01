/**
 * This class defines the ball object. It consists of an x and y
 * coordinates, radius, color, speed and booleans to decipher what
 * direction the ball is initially set to travel in. A ball also has
 * a chain number member and this tells us what layer in the chain
 * the ball has exploded and a hit score member for scoring purposes. 
 * A ball also has a constructor which accepts just a gameThread object
 * for access purposes.
 * 
 * A draw method is defined for drawing the ball. An update and collision
 * detect method is defined here as all balls need to be updated and be
 * checked for colliding into each other. A method for calculation the
 * balls score upon collision is also defined here and that is based on
 * a mathematical equation which uses a chain process.
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;

public class Ball {

	private int x;
	private int y;
	private int radius;
	private int acceleration;
	private Color color;
	// Direction of travel
	private boolean isIncreasingX;
	private boolean isIncreasingY;
	// For scoring purposes
	private int chainNumber;
	private int explodingBallHitScore;

	// Member for flagging the ball as exploded
	private boolean currentlyExploding = false;
	// True only for the initial ball i.e. first click
	private boolean initialBall;

	// Declaration of references
	private Ball updateBall;
	private Ball bomb;
	private GameThread gameThread;

	public Ball(GameThread gameThread) {
		this.gameThread = gameThread;
	}

	public Ball(int x, int y, int radius, Color color, int acceleration,
			boolean isIncreasingX, boolean isIncreasingY) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		this.acceleration = acceleration;
		this.isIncreasingX = isIncreasingX;
		this.isIncreasingY = isIncreasingY;

	}

	public void draw(Graphics2D g) {

		// Ball is drawing with its color and using math to place its
		// x and y coordinates as its center point and not its top left
		// corner. Radius is doubled as its the diameter that needs specified
		g.setColor(getColor());
		g.fillOval(getX() - getRadius(), getY() - getRadius(), getRadius() * 2,
				getRadius() * 2);

	}

	public void updateBalls() {

		while (true) {
			int x = 0;
			int y = 0;

			for (int i = 0; i < GameThread.ballList.size(); i++) {

				// Here we are cycling through the list of balls and
				// updating their movement making sure they do not go 
				// off the screen.
				
				// Getting each ball to update
				updateBall = gameThread.getBall(GameThread.ballList, i);
				x = updateBall.getX();
				y = updateBall.getY();

				// Here we use if statements and the x,y coordinates
				// to decide does the ball need to change and move
				// in a new direction
				if (x <= 0) {
					updateBall.setIncreasingX(true);
				}
				if (y <= 0) {
					updateBall.setIncreasingY(true);

				}
				if (x >= 776) {
					updateBall.setIncreasingX(false);
				}
				if (y >= 355) {
					updateBall.setIncreasingY(false);
				}
				
				// Now we use the balls acceleration factor to
				// move the ball in an update
				if (updateBall.isIncreasingX()) {
					updateBall.setX(updateBall.getX()
							+ updateBall.getAcceleration());
				} else {
					updateBall.setX(updateBall.getX()
							- updateBall.getAcceleration());
				}
				if (updateBall.isIncreasingY) {
					updateBall.setY(updateBall.getY()
							+ updateBall.getAcceleration());
				} else {
					updateBall.setY(updateBall.getY()
							- updateBall.getAcceleration());
				}

			}
			break;

		}
	}

	public void collisionDetect() {
		Ball ball;

		// We check if data structure is not empty as 
		// pointless to continue if so as no more balls
		// can explode so we stop all threads
		if (GameThread.explodingBallList.size() == 0) {
			gameThread.running = false;
		}

		// for each bomb in the exploded list we check for collisions
		// against the list of balls using a collided method check

		for (int i = 0; i < GameThread.explodingBallList.size(); i++) {
			bomb = gameThread.getBall(GameThread.explodingBallList, i);

			// We know a bomb has been added to the explode list
			// so we keep a record of each one to monitor progress
			// For each bomb we run its explosion in its own thread
			// We set the flag to false because we don't want to create
			// a new thread for a ball that was already exploded.
			// We increment the number of valid exploded balls by one
			if (bomb.isCurrentlyExploding()) {
				bomb.setCurrentlyExploding(false);
				new ballXplodeThread(bomb, gameThread);
				if (!bomb.isInitialBall()) {
					gameThread.setNumberOfExplodingBalls(gameThread
							.getNumberOfExplodingBalls() + 1);
				}
			}

			for (int j = 0; j < GameThread.ballList.size(); j++) {
				ball = gameThread.getBall(GameThread.ballList, j);

				// If a collision is detected that ball is added to the
				// explosion list and removed from the ball list. Its
				// flag for exploding and a method is used to calculate
				// the result of the collision
				if (collided(bomb, ball)) {
					// Play sound as collision true
					GameSound.COLLISION.play();
					chainReactionCalculation(bomb, ball);
					gameThread.addBall(GameThread.explodingBallList, ball);
					gameThread.removeBall(GameThread.ballList, ball);
					ball.setCurrentlyExploding(true);
				}
			}
		}
		return;
	}

	public boolean collided(Ball ballBomb, Ball ballMove) {
		// Collisions are detected using the combined radius and
		// the center points of the balls. If the radius is less
		// that the square root of the coordinates difference on each axis
		// multiplied by two, then a collision has occurred

		int value1 = Math.abs(ballMove.getX() - ballBomb.getX());
		int value2 = Math.abs(ballMove.getY() - ballBomb.getY());

		if (Math.sqrt(value1 * value1 + value2 * value2) <= (ballMove
				.getRadius() + ballBomb.getRadius())) {

			return true;
		} else
			return false;

	}

	public void chainReactionCalculation(Ball previous, Ball current) {
		// To measure a chain reaction we find out the previous chain number
		// of the ball that we collided into. We increment our own number as
		// we are the next layer in the chain and we use that chain number 
		// chain number to calculate the result and give ourselves a hit score.
		// The temporary score is updated in the game thread which can then
		// be used for display and to add to the total static store if level was
		// passed.
		current.setChainNumber(previous.getChainNumber() + 1);
		int result = 0;
		result = 100 * (int) (Math.pow(current.getChainNumber(), 3));
		current.setExplodingBallHitScore(result);
	
		gameThread.setTempScore(gameThread.getTempScore() + result);

	}

	// These are used so as we know which direction the
	// the ball is going so we can direct it appropriately
	// as it navigates the game screen

	public boolean isIncreasingX() {
		return isIncreasingX;
	}

	public void setIncreasingX(boolean isIncreasingX) {
		this.isIncreasingX = isIncreasingX;
	}

	public boolean isIncreasingY() {
		return isIncreasingY;
	}

	public void setIncreasingY(boolean isIncreasingY) {
		this.isIncreasingY = isIncreasingY;
	}

	public boolean isCurrentlyExploding() {
		return currentlyExploding;
	}

	public void setCurrentlyExploding(boolean currentlyExploding) {
		this.currentlyExploding = currentlyExploding;
	}
	
	public int getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(int acceleration) {
		this.acceleration = acceleration;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getChainNumber() {
		return chainNumber;
	}

	public void setChainNumber(int chainNumber) {
		this.chainNumber = chainNumber;
	}

	public int getExplodingBallHitScore() {
		return explodingBallHitScore;
	}

	public void setExplodingBallHitScore(int explodingBallHitScore) {
		this.explodingBallHitScore = explodingBallHitScore;
	}

	public boolean isInitialBall() {
		return initialBall;
	}

	public void setInitialBall(boolean initialBall) {
		this.initialBall = initialBall;
	}
}