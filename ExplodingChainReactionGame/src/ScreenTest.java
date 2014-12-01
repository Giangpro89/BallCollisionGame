import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.junit.Ignore;
import org.junit.Test;


public class ScreenTest {
	Screen screen = new Screen();
	Screen testAgainst = new Screen();
	Container c = screen.getContentPane();
	Container cNew = testAgainst.getContentPane();
	JPanel panel = new JPanel();
	JPanel add = new JPanel();
	
	

	@Ignore
	public void testSetNewContainer() {
		screen.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		testAgainst.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add.setBackground(Color.GREEN);	
		panel.setBackground(Color.BLACK);
		c.add(panel);
		cNew.add(add);
		screen.setContentPane(c);	
		testAgainst.setContentPane(cNew);
		
		screen.setNewContainer(add, panel);	
		assertEquals(testAgainst, screen);
	}

}
