/**
 * 
 * This class caters for the progress of the player throughout
 * the game at the end of each level attempt. Mouse listener is
 * implemented as this serves as a way for the user to press ahead
 * with the game. A progress screen is created with a level screen, 
 * gameThread and String objects. The level screen is the level screen
 * that should be added to the frame when the user clicks and the gameThread
 * is the JPanel that should be removed as it is now a finished game instance.
 * The string is used to inform the object which icon should be displayed
 * i.e. a failure or successful image. This string is then used to open
 * a saved image and then resize it using graphics and then added to this.
 * 
 * If the current level passed was the last one then the passed string will
 * be a filename and that is used to open/create a file that the current
 * total score will be saved to as a new high score.
 * 
 * Launching the next screen is again done by passing in this as a panel to be
 * removed and the passed level screen object as the panel to be added.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ProgressScreen extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GameThread previousGameThread;
	private LevelScreen newLevelscreen;
	private ImageIcon image;
	private JLabel imageIndictor;
	private JLabel label;
	private JLabel instruction;
	private JLabel congrats;
	private JLabel score;
	private PrintWriter printWriter;
	private String file = "HighScores.txt";
	private String fileName;
	private boolean startNewGame;
	private Font font;

	public ProgressScreen(LevelScreen newLevelScreen, GameThread gameThread,
			String fileName) {

		this.previousGameThread = gameThread;
		this.newLevelscreen = newLevelScreen;
		this.fileName = fileName;
		this.addMouseListener(this);
		this.setBackground(Color.BLACK);
		this.setLayout(new BorderLayout());

		font = new Font("ARIAL", Font.BOLD, 20);

		if (fileName.equals(file)) {
			// we know its the end of the game thus proceed as follows
			// otherwise show user his progress
			openFileSaveAndCongratulate();
		} else {
			showProgressIndicators();
		}
	}

	private void showProgressIndicators() {
		instruction = new JLabel("Click to proceed");
		instruction.setFont(font);
		instruction.setForeground(Color.WHITE);
		
		imageIndictor = new JLabel();
		
		// Here we try to open the image if it exists
		// we then use an image reference and get the icons image
		// as we need to to create a new buffered image from
		// which we can use a graphics object to draw/resize the image

		try {
			image = new ImageIcon(fileName);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		Image myimage = image.getImage();
		BufferedImage bi = new BufferedImage(myimage.getWidth(null),
				myimage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics g = bi.createGraphics();
		g.drawImage(myimage, 250, 175, 200, 200, null);

		ImageIcon newImage = new ImageIcon(bi);

		imageIndictor.setIcon(newImage);
		
		// Adding the components to the center of the panel
		add(imageIndictor, BorderLayout.LINE_START);
		add(instruction, BorderLayout.LINE_END);

		// Setting the frame to show this and remove the previous gameThread
		Screen.getInstanceOf().setNewContainer(this,
				this.getPreviousGameThread());

	}

	private void openFileSaveAndCongratulate() {
		
		label = new JLabel("Click to play again");
		label.setFont(font);
		label.setForeground(Color.WHITE);
		
		score = new JLabel("YOU SCORED "+GameThread.getTotalScore());
		score.setFont(new Font("ARIAL", Font.BOLD, 30));
		score.setForeground(Color.WHITE);

		congrats = new JLabel("CONGRATULATIONS");
		congrats.setFont(new Font("ARIAL", Font.BOLD, 30));
		congrats.setForeground(Color.WHITE);
		
		// We use a printwriter object and create and anonymous file writer
		// with our file name in append mode so we do not overwrite previous 
		// scores. We then write the score to the file and inform the player
		// that their score has been saved.
		try {
			printWriter = new PrintWriter(new FileWriter(file, true));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		printWriter.println(new Integer(GameThread.getTotalScore()).toString());
		printWriter.close();
		JOptionPane.showMessageDialog(null, "Score Saved");
		
		add(label, BorderLayout.SOUTH);
		add(congrats, BorderLayout.NORTH);
		add(score,BorderLayout.CENTER);
		Screen.getInstanceOf().setNewContainer(this,
				this.getPreviousGameThread());
		// We set this to true as this is used in the mouse
		// clicked method as a flag
		startNewGame = true;

	}

	public void mouseClicked(MouseEvent arg0) {
		// Start new game will have been set true at the end
		// of the last level thus a new click will dispose of
		// the current instance and start a new game.
		if (startNewGame) {
			GameThread.setTotalScore(0);
			Screen.getInstanceOf().dispose();
			Screen.setInstanceOf(new Screen());
		} else {
			// All other times we just add a new level screen and remove this
			Screen.getInstanceOf().setNewContainer(this.getNewLevelscreen(),
					this);
		}
	}

	public LevelScreen getNewLevelscreen() {
		return newLevelscreen;
	}

	public void setNewLevelscreen(LevelScreen newLevelscreen) {
		this.newLevelscreen = newLevelscreen;
	}

	public GameThread getPreviousGameThread() {
		return previousGameThread;
	}

	public void setPreviousGameThread(GameThread gameThread) {
		this.previousGameThread = gameThread;
	}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

}
