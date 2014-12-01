/**
 * 
 * This class is used for every ball that has been set to explode
 * The constructor accepts a ball. The gameThread object is used
 * to provide a reference to the gameThreads methods. The thread is 
 * started and the ball is enlarged to 30 pixels radius. The thread 
 * then sleeps for 1 seconds, then it wakes up removes the ball from 
 * the data structure and dies.
 *
 */
public class ballXplodeThread extends Thread {
	private Ball ball;
	private GameThread gameThread;

	public ballXplodeThread(Ball ball, GameThread gameThread) {
		this.ball = ball;
		this.gameThread = gameThread;
		start();
	}

	public void run() {

		for (int i = 0; i < 30; i++) {

			ball.setRadius(ball.getRadius() + 1);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gameThread.removeBall(GameThread.explodingBallList, ball);

	}

}
