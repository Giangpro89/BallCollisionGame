import static org.junit.Assert.*;
import java.awt.Color;
import org.junit.Test;


public class BallTest {
	
	// Reference needed to test the scoring method in ball
	// a gameThread reference is used in that method
	Ball b = new Ball(new GameThread(new LevelScreen(1)));
	
	// Ball to test collisions against
	Ball ball = new Ball(5,5,5,Color.BLUE, 1, true, true);
	
	// Balls to collide into above ball
	Ball collideBall = new Ball(10,10,5,Color.YELLOW, 1, true, true);
	Ball nonCollideBall = new Ball(30,30,5,Color.YELLOW, 1, true, true);
	Ball edgeBall = new Ball(13,11,5,Color.YELLOW, 1, true, true);
	Ball outsideEdgeBall = new Ball(14,11,5,Color.YELLOW, 1, true, true);
	
	
	@Test 
	public void test() {
		// Test two balls that should definitely collide
		assertTrue(ball.collided(ball, collideBall));
		
		// Test two balls that should definitely not collide
		assertFalse(ball.collided(ball, nonCollideBall));
		
		// Testing the edge case where the balls should collide
		assertTrue(ball.collided(ball, edgeBall));
		
		// Just to make sure we have a ball just outside the edge
		assertFalse(ball.collided(ball, outsideEdgeBall));
	}
	@Test
	public void scoringTest(){
		
		// These tests test the updating of the chain reaction number
		// and the hit score that ball should have
		
		
		// A chain reaction of three balls created here
		b.chainReactionCalculation(ball, collideBall);
		b.chainReactionCalculation(collideBall, nonCollideBall);
		
		assertEquals(ball.getChainNumber(), 0);
		assertEquals(0, ball.getExplodingBallHitScore());		
		
		assertEquals(collideBall.getChainNumber(), 1);
		assertEquals(100, collideBall.getExplodingBallHitScore());
		
		assertEquals(nonCollideBall.getChainNumber(), 2);
		assertEquals(800, nonCollideBall.getExplodingBallHitScore());

	}

}
