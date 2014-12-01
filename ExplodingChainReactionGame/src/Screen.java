/**
 * 
 * This class contains the driver and is of a singleton design pattern.
 * This class is the frame for the game from which panels are added
 * and removed to its content pane. This is the first thing a player
 * sees when he launches the game. Its members are a panel to which a
 * label is placed which in turn holds an image for the game. A static
 * reference is also a member for accessing the methods from elsewhere.
 * 
 * A method is defined that accepts two JPanels which adds one and removes 
 * the other to the frame. A mouse and key listener are used. A click will
 * launch the first level screen for the game, and the key listener is used
 * to launch a secret menu for when the user wants to skip levels. s is the 
 * secret key.
 * 
 */

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Screen extends JFrame implements MouseListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JLabel imageHolder;
	private Container container;
	private Icon image;

	public static Screen instanceOf;

	public Screen() {
		setTitle("EXPLODING GAME");
		setLocation(300, 200);
		setSize(800, 400);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addMouseListener(this);
		addKeyListener(this);

		container = this.getContentPane();
		panel = new JPanel();
		imageHolder = new JLabel();

		try {
			image = new ImageIcon("icon\\chainReaction.png");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		imageHolder.setIcon(image);
		panel.add(imageHolder);
		container.add(panel);
		setContentPane(container);
		setVisible(true);

	}

	public void setNewContainer(JPanel levelScreen, JPanel remove) {
		// This manipulates the panels added to the container hence shown to the
		// user. It accepts the panel to add and remove. Two static methods are
		// required to show the changes. This method is called throughout the
		// application
		
		container.remove(remove);
		container.add(levelScreen);
		revalidate();
		repaint();
	}

	public void mouseClicked(MouseEvent arg0) {
		// User clicks the mouse the first level screen is launched
		// by calling the panel manipulator method
		setNewContainer(new LevelScreen(1), panel);
	}

	public void keyPressed(KeyEvent evt) {
		// The secret menu is achieved through pop up windows
		// When a user presses the letter s(83) a pop up will appear
		// asking for user input to skip to that level
		// If the input is valid the screen for that level will appear
		// otherwise the program returns to the initial state

		int secretLevel = 0;

		if (evt.getKeyCode() == 83) {
			try {
				// here we are getting the user input and taking care of
				// invalid, nonsensical inputs.
				secretLevel = Integer.parseInt(JOptionPane
						.showInputDialog("To skip level enter level number"));

				if (secretLevel < 1 || secretLevel > 12) {
					JOptionPane.showMessageDialog(null, "Level does not exist");
					return;
				}
				// Here we launch the level screen of the level requested
				// by using the input in the level screen constructor
				setNewContainer(new LevelScreen(secretLevel), panel);

			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
				JOptionPane.showMessageDialog(this,
						"Enter a valid numerical value");
			}
		}
	}

	public static Screen getInstanceOf() {
		return instanceOf;
	}

	public static void setInstanceOf(Screen instanceOf) {
		Screen.instanceOf = instanceOf;
	}

	public static void main(String[] args) {
		Screen.setInstanceOf(new Screen());
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}

}
