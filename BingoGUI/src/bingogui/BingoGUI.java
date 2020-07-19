package bingogui;

import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class BingoGUI {
	
	static final String BINGO = "BINGO";
	static final GridLayout CARD_GRID = new GridLayout(6, 5, 2, 2);
	static final JPanel CALL_PANEL = new JPanel(new GridLayout(5, 16, 2, 2)); 
	static final JPanel CPU_PANEL_1 = new JPanel(CARD_GRID);
	static final JPanel CPU_PANEL_2 = new JPanel(CARD_GRID); 
	static final JPanel PLAYER_PANEL_1 = new JPanel(CARD_GRID);
	static final JPanel PLAYER_PANEL_2 = new JPanel(CARD_GRID);
	static final Color CPU_COLOR = new Color(255, 140, 140);
	static final Color PLAYER_COLOR = new Color(160, 160, 255); 
	static final Color CALL_COLOR = new Color(150, 230, 150); 
	static final Color BINGO_COLOR = new Color(255, 200, 100);
	static final Color DEFAULT_COLOR = Color.WHITE; 
	static final int GRID_SIZE = 65;
	
	static int call = 0;
	static int column = 0;
	static boolean[] usedNumbers = new boolean[75];
	static String winType = "";
	static JMenuItem miExit = new JMenuItem("Exit");
	static JMenuItem miNewGame = new JMenuItem("New Game");
	static JMenuItem miCallBingo = new JMenuItem("Call " + BINGO);
	static JLabel messageLabel = new JLabel("Get ready to play " + BINGO + "!");
	static JLabel[][] callBoard = new JLabel[5][15]; 
	static JLabel[][] cpuCard1 = new JLabel[5][5];
	static JLabel[][] cpuCard2 = new JLabel[5][5];
	static JButton[][] playerCard1 = new JButton[5][5];
	static JButton[][] playerCard2 = new JButton[5][5];
	static Timer timer = new Timer(4000, new timerAL()); 
	
	public static void main(String[] args) {
		setUp();
		gui();
	}

	public static void setUp() {
		final Font SMALL_FONT = new Font("Kristen ITC", Font.PLAIN, GRID_SIZE - 30);
		
		for (int i = 0; i < usedNumbers.length; i++) {
			usedNumbers[i] = false;
		}
		messageLabel.setText("Get ready to play " + BINGO + "!");
		setCallboard(CALL_COLOR, callBoard, CALL_PANEL, SMALL_FONT);
		setCard(CPU_COLOR, cpuCard1, CPU_PANEL_1, SMALL_FONT);
		setCard(CPU_COLOR, cpuCard2, CPU_PANEL_2, SMALL_FONT);
		setCard(PLAYER_COLOR, playerCard1, PLAYER_PANEL_1, SMALL_FONT);
		setCard(PLAYER_COLOR, playerCard2, PLAYER_PANEL_2, SMALL_FONT);
		timer.start();
	}
	
	public static void gui() {
		final Font LARGE_FONT = new Font("Kristen ITC", Font.BOLD, GRID_SIZE - 20);
		final JPanel MAIN_PANEL = new JPanel();
		final JPanel CPU_PANEL = new JPanel();
		final JPanel PLAYER_PANEL = new JPanel();
		final GridLayout MAJOR_GRID_LAYOUT = new GridLayout(1, 2, 15, 0);
		final FlowLayout FLOW_LAYOUT = new FlowLayout(FlowLayout.CENTER, 20, 10);
		final TitledBorder CPU_TB = new TitledBorder(new LineBorder(CPU_COLOR.darker(),
			4, true), "CPU CARDS", TitledBorder.CENTER, 0, LARGE_FONT, CPU_COLOR.darker());
		final TitledBorder PLAYER_TB = new TitledBorder(new LineBorder(PLAYER_COLOR.darker(), 
			4, true), "PLAYER CARDS", TitledBorder.CENTER, 0, LARGE_FONT, PLAYER_COLOR.darker());
		final TitledBorder CALLBOARD_TB = new TitledBorder(new LineBorder(CALL_COLOR.darker(), 
			4, true),"CALL BOARD", TitledBorder.CENTER, 0, LARGE_FONT, CALL_COLOR.darker());
		JFrame frame = new JFrame(BINGO);
		JMenuBar menuBar = new JMenuBar();
		JMenu optionsMenu = new JMenu("Options");
		
		menuBar.add(optionsMenu);
		addMenuItem(optionsMenu, miNewGame, 'N');
		addMenuItem(optionsMenu, miCallBingo, BINGO.charAt(0));
		optionsMenu.addSeparator();
		addMenuItem(optionsMenu, miExit, 'E');
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setFont(LARGE_FONT);
		
		CPU_PANEL.setLayout(MAJOR_GRID_LAYOUT);
		CPU_PANEL.setBorder(CPU_TB);
		CPU_PANEL.add(CPU_PANEL_1);
		CPU_PANEL.add(CPU_PANEL_2);
		PLAYER_PANEL.setLayout(MAJOR_GRID_LAYOUT);
		PLAYER_PANEL.setBorder(PLAYER_TB);
		PLAYER_PANEL.add(PLAYER_PANEL_1);
		PLAYER_PANEL.add(PLAYER_PANEL_2);
		CALL_PANEL.setBorder(CALLBOARD_TB);
		
		MAIN_PANEL.setLayout(FLOW_LAYOUT);
		MAIN_PANEL.add(CALL_PANEL);
		MAIN_PANEL.add(CPU_PANEL);
		MAIN_PANEL.add(PLAYER_PANEL);
		MAIN_PANEL.add(messageLabel);
		
		frame.setJMenuBar(menuBar);
		frame.setContentPane(MAIN_PANEL);
		frame.setSize(new Dimension(1500, 1050));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public static boolean checkWin(JComponent[][] card) {
		Integer[] x = {0, 0, 0, 0};	// Occurrences in a row
		
		// Check for vertical and horizontal wins
		for (int row = 0; row < card.length; row++) {
			for (int col = 0; col < card[row].length; col++) {
				x[0] += (!card[row][col].getBackground().equals(DEFAULT_COLOR)) ? 1 : 0;
				x[1] += (!card[col][row].getBackground().equals(DEFAULT_COLOR)) ? 1 : 0;
				if (x[0] == card.length) {
					for (int i = 0; i < card.length; i++) {
						card[row][i].setBackground(BINGO_COLOR);
					}
					winType = "(Horizontal Win)";
					return true;
				} else if (x[1] == card.length) {
					for (int i = 0; i < card.length; i++) {
						card[i][row].setBackground(BINGO_COLOR);
					}
					winType = "(Vertical Win)";
					return true;
				}
			}
			x[0] = 0;
			x[1] = 0;
		}
		
		// Check for diagonal wins 
		for (int i = 0; i < card.length; i++) {
			x[2] += (!card[i][i].getBackground().equals(DEFAULT_COLOR)) ? 1 : 0;
			x[3] += (!card[card.length - 1 - i][i].getBackground().equals(DEFAULT_COLOR)) ? 1 : 0;
			if (x[2] == card.length) {
				for (int a = 0; a < card.length; a++) {
					card[a][a].setBackground(BINGO_COLOR);
				}
				winType = "(Diagonal Win)";
				return true;	
			} else if (x[3] == card.length) {
				for (int a = 0; a < card.length; a++) {
					card[card.length - 1 - a][a].setBackground(BINGO_COLOR);
				}
				winType = "(Diagonal Win)";
				return true;
			}
		}
		return false;
	}
	
	public static Integer generateNumber(boolean[] usedNumbers) {
		Random rnd = new Random();
		int number = 0;
		
		do {
			number = rnd.nextInt(75);	
		} while (usedNumbers[number] == true);
		usedNumbers[number] = true;
		return number + 1;
	}

	public static class menuAL implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(miNewGame)) {
				restartGame();
			} else if (e.getSource().equals(miCallBingo)){
				if (checkWin(playerCard1) || checkWin(playerCard2)) {
					endGame("You have " + BINGO +"! You win! " + winType);
				} else {
					endGame("You made a false " + BINGO + " call... You lose.");
				}
			} else if (e.getSource().equals(miExit)) {
				System.exit(0);
			}
		}
	}
	
	public static void endGame(String message) {
		for (int row = 0; row < playerCard1.length; row++) {
			for (int col = 0; col < playerCard1[0].length; col++) {
				playerCard1[row][col].setEnabled(false);
				playerCard2[row][col].setEnabled(false);
				cpuCard1[row][col].setEnabled(false);
				cpuCard2[row][col].setEnabled(false);
			}
		}
		timer.stop();
		messageLabel.setText("The game has ended! Ctrl + N for a new game.");
		JOptionPane.showMessageDialog(new JFrame(), message, "Game Over!", JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void restartGame() {
		timer.stop();
		CPU_PANEL_1.removeAll();
		CPU_PANEL_2.removeAll();
		PLAYER_PANEL_1.removeAll();
		PLAYER_PANEL_2.removeAll();
		CALL_PANEL.removeAll();
		setUp();
	}
	
	public static class daubAL implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			
			if (Integer.valueOf(button.getText()) == call) {
				disable(PLAYER_COLOR, button);
			} else {
				endGame("You made an incorrect daub... You lose.");
			}
		}
	}
	
	public static class timerAL implements ActionListener {
		JLabel previous = new JLabel();
		final Color NEW_CALL_COLOR = new Color(155, 255, 0);
		
		public void actionPerformed(ActionEvent e) {
			call = generateNumber(usedNumbers);
			column = (int) ((call - 1) / 15);
			disable(CALL_COLOR, previous);
			previous = callBoard[column][(call - 1) - 15 * column];
			disable(NEW_CALL_COLOR, previous);
			messageLabel.setText("Current Call: " + BINGO.charAt(column) + String.valueOf(call));
			cpuDaub(cpuCard1);
			cpuDaub(cpuCard2);
			if (checkWin(cpuCard1) || checkWin(cpuCard2)) {
				endGame("The computer has " + BINGO + "! CPU wins! " + winType);
			}
		}
	}
	
	public static void cpuDaub(JLabel[][] card) {
		for (int row = 0; row < card.length; row++) {
			if (card[row][column].getText().equals(String.valueOf(call))) {
				disable(CPU_COLOR, card[row][column]);
			}
		}
	}

	public static void setCallboard(Color c, JLabel[][] callBoard, JPanel panel, Font font) {
		JLabel[] bingo = bingoArray();
		int num = 0;
		
		for (int row = 0; row < callBoard.length; row++) {
			for (int col = 0; col < callBoard[0].length; col++) {
				callBoard[row][col] = new JLabel(String.valueOf(++num));
				setUpGrid(c.darker(), font, callBoard[row][col]);
				callBoard[row][col].setHorizontalAlignment(JLabel.CENTER);
				
			}
		}
		
		for (int row = 0; row < callBoard.length; row++) {
			panel.add(bingo[row]);
			for (int col = 0; col < callBoard[0].length; col++) {
				panel.add(callBoard[row][col]);
			}
		}
	}
	
	public static void setCard(Color c, JComponent[][] card, JPanel panel, Font font) {
		JLabel[] bingo = bingoArray();
		Random rnd = new Random();
        String numbersUsed = "";
        String number = "";
		
		for (int i = 0; i < card[0].length; i++) {
			panel.add(bingo[i]);
		}
		
        for (int row = 0; row < card.length; row++) {
            for (int col = 0; col < card[0].length; col++) {
            	do {
            		number = Integer.toString(rnd.nextInt(15) + col * 15 + 1);
            	} while (numbersUsed.indexOf(" " + number + " ") != -1);
            	numbersUsed += " " + number + " ";
             	if (card instanceof JButton[][]) {
  					card[row][col] = new JButton(number);
    				((JButton) card[row][col]).addActionListener(new daubAL());
    				((JButton) card[row][col]).setFocusPainted(false);
    				setUpGrid(c.darker(), font, card[row][col]);
    				if (row == 2 && col == 2) {
    					((JButton) card[row][col]).setText("");
    					disable(PLAYER_COLOR, (JButton) card[row][col]);
    				}
   				} else if (card instanceof JLabel[][]){
    				card[row][col] = new JLabel(number);
    				((JLabel) card[row][col]).setHorizontalAlignment(JLabel.CENTER);
    				setUpGrid(c.darker(), font, card[row][col]);
    				if (row == 2 && col == 2) {
    					((JLabel) card[row][col]).setText("");
    					disable(CPU_COLOR, (JLabel) card[row][col]);
    				}
  				}
             	panel.add(card[row][col]);
            }
        }
	}

	public static JLabel[] bingoArray() {
		final Font BINGO_FONT = new Font("Kristen ITC", Font.BOLD, GRID_SIZE - 20);
		JLabel[] bingo = new JLabel[5];
		
		for (int i = 0; i < BINGO.length(); i++) {
			bingo[i] = new JLabel(String.valueOf(BINGO.charAt(i)));
			setUpGrid(BINGO_COLOR, BINGO_FONT, bingo[i]);
			bingo[i].setHorizontalAlignment(JLabel.CENTER);
		}
		return bingo;
	}
		
	public static void addMenuItem(JMenu menu, JMenuItem menuItem, char key) {
		menuItem.addActionListener(new menuAL());
		menuItem.setAccelerator(KeyStroke.getKeyStroke(key, 
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(menuItem);
	}
	
	public static void setUpGrid(Color c, Font f, JComponent component) {
		final LineBorder LINE_BORDER = new LineBorder(c, 2, false);
		final Dimension GRID_DIMENSION = new Dimension (GRID_SIZE, GRID_SIZE);
		
		component.setFont(f);
		component.setForeground(c);
		component.setPreferredSize(GRID_DIMENSION);
		component.setBorder(LINE_BORDER);
		component.setBackground(DEFAULT_COLOR);
		component.setOpaque(true);
	}

	public static void disable(Color c, JComponent component) {
		component.setEnabled(false);
		component.setBackground(c);
	}
}