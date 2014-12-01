import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.Test;

public class GameThreadTest {

	// Declaring two gameThread instances with one level and two level
	// in its constructor
	GameThread gameThread = new GameThread(new LevelScreen(1));
	GameThread gameThreadTwo = new GameThread(new LevelScreen(2));

	@Test
	public void testRefreshingOnScreenTargetMessage() {
		// Testing the message with more than one ball remaining
		gameThread.setTarget(5);
		gameThread.setNumberOfExplodingBalls(3);
		gameThread.refreshOnScreenTarget();
		assertEquals("Explode " + 2 + " Balls", gameThread.getMessage());

		// Testing one ball remaining
		gameThread.setTarget(5);
		gameThread.setNumberOfExplodingBalls(4);
		gameThread.refreshOnScreenTarget();
		assertEquals("Last Ball", gameThread.getMessage());

		// Testing target has been exceeded
		gameThread.setTarget(5);
		gameThread.setNumberOfExplodingBalls(7);
		gameThread.refreshOnScreenTarget();
		assertEquals("Level Passed", gameThread.getMessage());
	}

	@Test
	public void testCreateBalls() {

		// GameThread with level one i.e. 5 balls should be
		// created and added to the list
		GameThread.getBallList().clear();
		gameThread.createBalls();
		assertEquals(5, GameThread.getBallList().size());

		// GameThreadTwo with level two i.e. 10 balls should be
		// created and added to the list
		GameThread.getBallList().clear();
		gameThreadTwo.createBalls();
		assertEquals(10, GameThread.getBallList().size());
	}

	@Ignore
	// Can't figure out how to test this correctly. In the method under
	// test, it launches
	public void testCheckGameStatus() {

		// Getting the reference to the gameThread instance that was created
		// in the level screen so we can replace THIS in the method under test
		GameThread gt = gameThread.getLevelScreen().getGt();
		ProgressScreen ps = new ProgressScreen(gameThread.getLevelScreen(), gt,
				"icon\\fail.jpg");
		//ps.setNewLevelscreen(gt);

		// Setting the progress screen to avoid null pointers during test
		gameThread.setProgressScreen(ps);

		// The level is 0 in the gameThread instance so 0 balls exploded
		// will cause the user to fail the level
		// Testing if the progress screen that should be launched
		// when the user fails the level is correct.
		gameThread.setNumberOfExplodingBalls(0);
		gameThread.checkGameStatus();
		assertEquals(new ProgressScreen(gameThread.getLevelScreen(), gt,
				"icon\\fail.jpg"), gameThread.getProgressScreen());

		// The level should be passed with one ball exploded so this
		// tests that
		gameThread.setNumberOfExplodingBalls(1);
		gameThread.checkGameStatus();
		assertEquals(new ProgressScreen(gameThread.getLevelScreen(), gt,
				"icon\\pass.png"), gameThread.getProgressScreen());

	}

	@Test
	public void testStartTheFirstExplosion() {

		// Here we test this method. We can check does the ball flag
		// for explosions be set to true and the chain number which
		// should be 0.
		// We can also check that the exploding ball list was 0 before
		// the method was called and then whether it increased to 1
		// after the method was called.
		assertEquals(0, GameThread.getExplodingBallList().size());
		Ball ball = new Ball(5, 5, 5, Color.black, 0, true, true);
		gameThread.startTheFirstExplosion(ball);
		assertTrue(ball.isCurrentlyExploding());
		assertEquals(1, GameThread.getExplodingBallList().size());
		assertEquals(0, ball.getChainNumber());
	}

	@Test
	public void testRemoveBall() {
		// Here we test the removal of a ball from the list
		// We send the method under test our list and a ball which
		// we have already added to the list. So when the method is called
		// our array list should be empty
		ArrayList<Ball> balls = new ArrayList<Ball>();
		Ball ball = new Ball(5, 5, 5, Color.black, 0, true, true);
		balls.add(ball);
		assertEquals(1, balls.size());
		gameThread.removeBall(balls, ball);
		assertEquals(0, balls.size());
	}

	@Test
	public void testGetBall() {
		// here we test our getBall method by first creating
		// a list and adding a ball to it at position 0 as
		// it is the only ball in the list. Then we use our method
		// to get the ball at position 0 and check whether it is the same
		// ball that we added to the list which it should be
		ArrayList<Ball> balls = new ArrayList<Ball>();
		Ball ball = new Ball(5, 5, 5, Color.black, 0, true, true);
		balls.add(ball);
		Ball newBall = gameThread.getBall(balls, 0);
		assertEquals(ball, newBall);
	}

}
