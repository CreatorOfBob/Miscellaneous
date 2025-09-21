import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class HangmanGUI extends JFrame {
    private String target;
    private ArrayList<Character> allLetters = new ArrayList<>();
    private ArrayList<Character> correct = new ArrayList<>();
    private ArrayList<Character> wrong = new ArrayList<>();
    private JLabel wrongLabel, correctLabel, wordLabel;
    private JTextField guessField;
    private JButton guessButton, playAgainButton;
    private HangmanPanel drawingPanel;
    private int mistakes = 0;
    private int totalGuesses = 0;
    private ArrayList<String> words;

    public HangmanGUI() throws Exception {
        setTitle("Hangman Game");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 0));
        initializeGame();
        setVisible(true);
    }

    private void initializeGame() {
        words = new ArrayList<>();
        correct = new ArrayList<>();
        wrong = new ArrayList<>();
        allLetters = new ArrayList<>();
        mistakes = 0;
        totalGuesses = 0;

        try (Scanner scanner = new Scanner(new File("words.txt"))) {
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine().trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading words.txt");
            System.exit(1);
        }

        Random rand = new Random();
        target = words.get(rand.nextInt(words.size()));

        for (char ch : target.toLowerCase().toCharArray()) {
            if (Character.isLetter(ch) && !allLetters.contains(ch)) {
                allLetters.add(ch);
            }
        }

        getContentPane().removeAll();
        setLayout(new BorderLayout());  // reset layout without gaps here, optional

        drawingPanel = new HangmanPanel();
        add(drawingPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 20));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 20));
        add(rightPanel, BorderLayout.CENTER);

        wrongLabel = new JLabel("Wrong: ");
        wrongLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        correctLabel = new JLabel("Correct: ");
        correctLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wordLabel = new JLabel();
        wordLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessField = new JTextField(10);
        guessField.setMaximumSize(new Dimension(150, 25));
        guessField.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessButton = new JButton("Guess");
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setVisible(false);
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalStrut(60));  // vertical space at the top
        rightPanel.add(wrongLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(correctLabel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(wordLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(guessField);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(guessButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(playAgainButton);

        guessButton.addActionListener(e -> handleGuess());
        playAgainButton.addActionListener(e -> initializeGame());
        guessField.addActionListener(e -> guessButton.doClick());

        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "EnterPressed");
        am.put("EnterPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (playAgainButton.isVisible()) {
                    playAgainButton.doClick();
                }
            }
        });

        updateDisplay();
        revalidate();
        repaint();
    }

    private void handleGuess() {
        String input = guessField.getText().trim().toLowerCase();
        guessField.setText("");

        if (input.isEmpty()) return;

        if (input.length() == 1) {
            char ch = input.charAt(0);
            if (!Character.isLetter(ch) || correct.contains(ch) || wrong.contains(ch)) {
                JOptionPane.showMessageDialog(this, "Invalid or repeated guess.");
                return;
            }

            totalGuesses++;

            if (allLetters.contains(ch)) {
                correct.add(ch);
            } else {
                wrong.add(ch);
                mistakes++;
            }
        } else {
            if (input.equalsIgnoreCase(target)) {
                correct.clear();
                correct.addAll(allLetters);
                totalGuesses++;
                updateDisplay();
                endGame(true);  // End immediately on correct full word guess
                return;
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect word guess (no penalty).");
                return;
            }
        }

        updateDisplay();

        if (correct.size() == allLetters.size()) {
            endGame(true);
        } else if (mistakes == 6) {
            endGame(false);
        }
    }

    private void endGame(boolean win) {
        guessButton.setEnabled(false);
        guessField.setEnabled(false);
        playAgainButton.setVisible(true);

        if (win) {
            JOptionPane.showMessageDialog(this, "Congratulations! You win!");
        } else {
            JOptionPane.showMessageDialog(this, "You lose! The word was: " + target);
        }

        saveHistory(win);
    }

    private void saveHistory(boolean win) {
        try (PrintWriter out = new PrintWriter(new FileWriter("history.txt", true))) {
            out.println(target);
            out.println(totalGuesses);
            out.println(win ? "Win" : "Lose");
            out.println();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to history.txt");
        }
    }

    private void updateDisplay() {
    wordLabel.setText(formatPhrase());
    wrongLabel.setText("Wrong: " + listToString(wrong));
    correctLabel.setText("Correct: " + listToString(correct));
    drawingPanel.repaint();
}

private String listToString(ArrayList<Character> list) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
        sb.append(list.get(i));
        if (i < list.size() - 1) {
            sb.append(" ");
        }
    }
    return sb.toString();
}


    private String formatPhrase() {
        StringBuilder sb = new StringBuilder();
        for (char ch : target.toCharArray()) {
            if (!Character.isLetter(ch)) {
                sb.append(ch).append(" ");
            } else if (correct.contains(Character.toLowerCase(ch))) {
                sb.append(ch).append(" ");
            } else {
                sb.append("_ ");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        new HangmanGUI();
    }

    class HangmanPanel extends JPanel {
        public HangmanPanel() {
            setPreferredSize(new Dimension(250, 400));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int xOffset = 40;
            g.drawLine(20 + xOffset, 250, 200 + xOffset, 250);      // base
            g.drawLine(100 + xOffset, 250, 100 + xOffset, 50);      // pole
            g.drawLine(100 + xOffset, 50, 180 + xOffset, 50);       // top
            g.drawLine(180 + xOffset, 50, 180 + xOffset, 80);       // rope

            if (mistakes > 0) g.drawOval(160 + xOffset, 80, 40, 40);           // head
            if (mistakes > 1) g.drawLine(180 + xOffset, 120, 180 + xOffset, 180); // body
            if (mistakes > 2) g.drawLine(180 + xOffset, 140, 150 + xOffset, 160); // left arm
            if (mistakes > 3) g.drawLine(180 + xOffset, 140, 210 + xOffset, 160); // right arm
            if (mistakes > 4) g.drawLine(180 + xOffset, 180, 150 + xOffset, 210); // left leg
            if (mistakes > 5) g.drawLine(180 + xOffset, 180, 210 + xOffset, 210); // right leg
        }
    }
}
