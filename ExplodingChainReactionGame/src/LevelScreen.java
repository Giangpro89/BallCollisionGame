/**
 * This class extends JPanel and serves to show the user
 * what level they are at, current score and information on 
 * what is expected to pass the level. The layout is grid bag
 * layout as this allows for more freedom when placing components
 * A level screen object accepts an integer in its constructor and
 * this is used to define target amount and number of balls. Labels
 * are used to display this information to the player and also his 
 * their current score. A button is used so the user can start the 
 * game whenever they are ready. Aside for the standard get/set two
 * methods are defined for deciphering the game information i.e. target etc
 * and for creating a new gameThread with this in its constructor instance
 * and launching that thus every level screen has a gameThread object and
 * vice versa.
 * 
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LevelScreen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JLabel instruction;
	private JLabel score;
	private JButton button;

	private int levelNumber;
	private int targetAmount;
	private int totalNumberOfBalls;

	private GameThread gameThread;

	public LevelScreen(int levelNumber) {

		// Using the passed level number to set the
		// target amount and ball total members
		this.levelNumber = levelNumber;
		setGameInformation(this.getLevelNumber());

		setBackground(Color.BLACK);
		setLayout(new GridBagLayout());

		// Constraints are needed so as each component can
		// be placed optimally and remain unaffected by
		// other components
		GridBagConstraints c = new GridBagConstraints();
		score = new JLabel("" + GameThread.getTotalScore());
		score.setFont(new Font("ARIAL", Font.BOLD, 20));
		score.setForeground(Color.WHITE);
		
		// Can be used to position component
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.1;// these just define how important this
		c.weighty = 0.1;// component is with respect to its hold
						// on the x and y position

		c.gridx = 0; // These place the component in a sort
		c.gridy = 0; // of grid like fashion.
		add(score, c);

		GridBagConstraints cl = new GridBagConstraints();
		label = new JLabel("Level " + this.levelNumber);
		label.setFont(new Font("ARIAL", Font.BOLD, 30));
		label.setForeground(Color.WHITE);
		cl.fill = GridBagConstraints.CENTER;// Add more weight for the
		cl.weightx = 1.0;					// component to be center
		cl.weighty = 0.5;
		cl.gridx = 1;
		cl.gridy = 2;
		add(label, cl);

		GridBagConstraints ci = new GridBagConstraints();
		instruction = new JLabel("Get " + this.targetAmount + " out of "
				+ this.totalNumberOfBalls + " balls");
		instruction.setFont(new Font("ARIAL", Font.BOLD, 30));
		instruction.setForeground(Color.WHITE);
		ci.fill = GridBagConstraints.CENTER;
		ci.weightx = 1.0;
		ci.weighty = 0.5;
		ci.gridx = 1;
		ci.gridy = 3;
		add(instruction, ci);

		GridBagConstraints cb = new GridBagConstraints();
		button = new JButton("Play");
		button.setFont(new Font("ARIAL", Font.BOLD, 20));
		button.setForeground(Color.GREEN);

		cb.fill = GridBagConstraints.CENTER;
		cb.weightx = 0.3;
		cb.weighty = 0.3;
		cb.gridx = 1;
		cb.gridy = 4;
		add(button, cb);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Start the ball game
				startTheGame();
			}
		});
	}

	private void startTheGame() {
		// The game thread is launched from here. Each level starts its own game
		// thread which is shown on screen using the method defined earlier
		// in the Screen class. This is called when the user
		// clicks the play button on each level screen. This object reference
		// is passed as we want to access its members in the game thread

		gameThread = new GameThread(this);
		Screen.getInstanceOf().setNewContainer(gameThread, this);
	}

	private void setGameInformation(int levelNumber) {
		// Simple function which defines the target and ball total for each
		// level depending on the passed argument

		if (levelNumber == 1) {
			this.targetAmount = 1;
			this.totalNumberOfBalls = 5;

		} else if (levelNumber == 2) {
			this.targetAmount = 2;
			this.totalNumberOfBalls = 10;

		} else if (levelNumber == 3) {
			this.targetAmount = 4;
			this.totalNumberOfBalls = 15;

		} else if (levelNumber == 4) {
			this.targetAmount = 6;
			this.totalNumberOfBalls = 20;

		} else if (levelNumber == 5) {
			this.targetAmount = 10;
			this.totalNumberOfBalls = 25;

		} else if (levelNumber == 6) {
			this.targetAmount = 15;
			this.totalNumberOfBalls = 30;

		} else if (levelNumber == 7) {
			this.targetAmount = 18;
			this.totalNumberOfBalls = 35;

		} else if (levelNumber == 8) {
			this.targetAmount = 22;
			this.totalNumberOfBalls = 40;

		} else if (levelNumber == 9) {
			this.targetAmount = 30;
			this.totalNumberOfBalls = 45;

		} else if (levelNumber == 10) {
			this.targetAmount = 37;
			this.totalNumberOfBalls = 50;

		} else if (levelNumber == 11) {
			this.targetAmount = 48;
			this.totalNumberOfBalls = 55;

		} else if (levelNumber == 12) {
			this.targetAmount = 54;
			this.totalNumberOfBalls = 60;
		}
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNum) {
		this.levelNumber = levelNum;
	}

	public int getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(int needed) {
		this.targetAmount = needed;
	}

	public int getTotalNumberOfBalls() {
		return totalNumberOfBalls;
	}

	public void setTotalNumberOfBalls(int amount) {
		this.totalNumberOfBalls = amount;
	}

	public GameThread getGt() {
		return gameThread;
	}

	public void setGt(GameThread gt) {
		this.gameThread = gt;
	}

}
