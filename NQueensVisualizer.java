import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NQueensVisualizer extends JFrame {
    private int size;
    private int delay;
    private JTextField[][] cells;
    private int[][] board;
    private boolean solving;
    private JButton startStopButton;
    private JButton nextSolutionButton;
    private JLabel solutionCounterLabel;
    private int currentSolutionIndex;
    private List<int[][]> solutions;

    public NQueensVisualizer() {
        setTitle("N-Queens Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        size = getSizeFromUser();
        delay = getSpeedFromUser();

        cells = new JTextField[size][size];
        board = new int[size][size];
        solutions = new ArrayList<>();
        solving = false;
        currentSolutionIndex = 0;

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(size, size));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setEditable(false);
                cells[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);
                cells[row][col].setForeground((row + col) % 2 == 0 ? Color.BLACK : Color.WHITE);
                gridPanel.add(cells[row][col]);
                board[row][col] = 0;
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        startStopButton = new JButton("Find All Solutions");
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (solving) {
                    solving = false;
                    startStopButton.setText("Find All Solutions");
                } else {
                    solving = true;
                    startStopButton.setText("Stop");
                    new Thread(() -> solvePuzzle()).start();
                }
            }
        });
        buttonPanel.add(startStopButton);

        nextSolutionButton = new JButton("Next Solution");
        nextSolutionButton.setEnabled(false);
        nextSolutionButton.addActionListener(e -> {
            if (!solutions.isEmpty()) {
                currentSolutionIndex = (currentSolutionIndex + 1) % solutions.size();
                showSolution(solutions.get(currentSolutionIndex));
                solutionCounterLabel.setText("Solution #" + (currentSolutionIndex + 1) + " / " + solutions.size());
            }
        });
        buttonPanel.add(nextSolutionButton);

        solutionCounterLabel = new JLabel("Solutions Found: 0");
        solutionCounterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(solutionCounterLabel);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(600, 600);
    }

    private int getSizeFromUser() {
        String input = JOptionPane.showInputDialog(this, "Enter the number of queens:", "N-Queens", JOptionPane.QUESTION_MESSAGE);
        return Integer.parseInt(input);
    }

    private int getSpeedFromUser() {
        String input = JOptionPane.showInputDialog(this, "Enter the speed in milliseconds:", "Speed", JOptionPane.QUESTION_MESSAGE);
        return Integer.parseInt(input);
    }

    private void solvePuzzle() {
        solutions.clear();
        currentSolutionIndex = 0;
        clearBoard();
        solveNQueensAll(0);

        solving = false;
        SwingUtilities.invokeLater(() -> {
            startStopButton.setText("Find All Solutions");
            nextSolutionButton.setEnabled(!solutions.isEmpty());
            solutionCounterLabel.setText("Solutions Found: " + solutions.size());
            if (!solutions.isEmpty()) {
                showSolution(solutions.get(0));
                JOptionPane.showMessageDialog(this, "Found " + solutions.size() + " solutions for N = " + size,
                        "Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No solutions found for N = " + size,
                        "No Solution", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void solveNQueensAll(int col) {
        if (col == size) {
            int[][] solution = new int[size][size];
            for (int i = 0; i < size; i++) {
                System.arraycopy(board[i], 0, solution[i], 0, size);
            }
            solutions.add(solution);
            return;
        }

        for (int i = 0; i < size; i++) {
            if (!solving) return;

            if (isSafe(i, col)) {
                board[i][col] = 1;
                updateGUI(i, col, true);
                delay(delay);

                solveNQueensAll(col + 1);

                board[i][col] = 0;
                updateGUI(i, col, false);
                delay(delay);
            }
        }
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < col; i++)
            if (board[row][i] == 1)
                return false;

        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--)
            if (board[i][j] == 1)
                return false;

        for (int i = row, j = col; i < size && j >= 0; i++, j--)
            if (board[i][j] == 1)
                return false;

        return true;
    }

    private void showSolution(int[][] solution) {
        clearBoard();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (solution[row][col] == 1) {
                    board[row][col] = 1;
                    updateGUI(row, col, true);
                }
            }
        }
    }

    private void clearBoard() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = 0;
                cells[row][col].setText("");
                cells[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);
                cells[row][col].setForeground((row + col) % 2 == 0 ? Color.BLACK : Color.WHITE);
            }
        }
    }

    private void updateGUI(int row, int col, boolean placeQueen) {
        SwingUtilities.invokeLater(() -> {
            if (placeQueen) {
                cells[row][col].setText("Q");
                cells[row][col].setBackground(Color.ORANGE);
                cells[row][col].setForeground(Color.BLACK);
            } else {
                cells[row][col].setText("");
                cells[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);
                cells[row][col].setForeground((row + col) % 2 == 0 ? Color.BLACK : Color.WHITE);
            }
        });
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NQueensVisualizer visualizer = new NQueensVisualizer();
            visualizer.setVisible(true);
        });
    }
}
