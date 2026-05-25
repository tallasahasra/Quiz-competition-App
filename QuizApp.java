import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Question {

    String question;
    String[] options;
    int answer;

    Question(String question, String[] options, int answer) {

        this.question = question;
        this.options = options;
        this.answer = answer;
    }
}

class QuestionBank {

    public static ArrayList<Question> getQuestions() {

        ArrayList<Question> list = new ArrayList<>();

        list.add(new Question(
                "What is JVM?",
                new String[]{
                        "Java Virtual Machine",
                        "Java Variable Method",
                        "Java Verified Mode",
                        "None"
                },
                0
        ));

        list.add(new Question(
                "Who created Java?",
                new String[]{
                        "Google",
                        "Sun Microsystems",
                        "Apple",
                        "Microsoft"
                },
                1
        ));

        list.add(new Question(
                "Which keyword is used for inheritance?",
                new String[]{
                        "extends",
                        "this",
                        "super",
                        "package"
                },
                0
        ));

        list.add(new Question(
                "Which collection uses FIFO?",
                new String[]{
                        "Queue",
                        "Stack",
                        "Tree",
                        "Graph"
                },
                0
        ));

        list.add(new Question(
                "Which method starts a thread?",
                new String[]{
                        "run()",
                        "execute()",
                        "start()",
                        "begin()"
                },
                2
        ));

        Collections.shuffle(list);

        return list;
    }
}

public class QuizApp extends JFrame {

    JLabel questionLabel, timerLabel, scoreLabel;

    JRadioButton[] options = new JRadioButton[4];

    ButtonGroup group;

    JButton nextButton, leaderboardButton;

    JProgressBar progressBar;

    ArrayList<Question> questions;

    int current = 0, score = 0, timeLeft = 15;

    javax.swing.Timer timer;

    String playerName, difficulty;

    // DATABASE CONNECTION
    public Connection getConnection() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quizdb",
                    "root",
                    "root123"
            );

        } catch (Exception e) {

            return null;
        }
    }

    // CONSTRUCTOR
    public QuizApp() {

        playerName = JOptionPane.showInputDialog(
                this,
                "Enter Your Name"
        );

        if (playerName == null || playerName.isEmpty()) {

            playerName = "Player";
        }

        String[] levels = {
                "Easy",
                "Medium",
                "Hard"
        };

        difficulty = (String) JOptionPane.showInputDialog(
                this,
                "Select Difficulty",
                "Difficulty",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levels,
                levels[0]
        );

        if (difficulty == null) {

            difficulty = "Easy";
        }

        questions = QuestionBank.getQuestions();

        setTitle("Quiz Competition Application");

        setSize(800, 550);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // TOP PANEL
        JPanel top = new JPanel(new GridLayout(3,1));

        timerLabel = new JLabel(
                "Time Left: 15",
                SwingConstants.CENTER
        );

        scoreLabel = new JLabel(
                "Score: 0",
                SwingConstants.CENTER
        );

        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        progressBar = new JProgressBar(
                0,
                questions.size()
        );

        progressBar.setStringPainted(true);

        top.add(timerLabel);

        top.add(scoreLabel);

        top.add(progressBar);

        add(top, BorderLayout.NORTH);

        // CENTER PANEL
        JPanel center = new JPanel(new GridLayout(5,1));

        questionLabel = new JLabel();

        questionLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        center.add(questionLabel);

        group = new ButtonGroup();

        for (int i = 0; i < 4; i++) {

            options[i] = new JRadioButton();

            options[i].setFont(
                    new Font("Arial", Font.PLAIN, 16)
            );

            group.add(options[i]);

            center.add(options[i]);
        }

        add(center, BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel bottom = new JPanel();

        nextButton = new JButton("Next");

        leaderboardButton =
                new JButton("Leaderboard");

        bottom.add(nextButton);

        bottom.add(leaderboardButton);

        add(bottom, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> checkAnswer());

        leaderboardButton.addActionListener(
                e -> showLeaderboard()
        );

        loadQuestion();

        startTimer();

        setVisible(true);
    }

    // LOAD QUESTION
    public void loadQuestion() {

        if (current >= questions.size()) {

            showResult();

            return;
        }

        Question q = questions.get(current);

        progressBar.setValue(current);

        questionLabel.setText(
                (current + 1)
                        + ". "
                        + q.question
        );

        for (int i = 0; i < 4; i++) {

            options[i].setText(q.options[i]);

            options[i].setForeground(Color.BLACK);
        }

        group.clearSelection();

        if (difficulty.equals("Easy")) {

            timeLeft = 20;

        } else if (difficulty.equals("Medium")) {

            timeLeft = 15;

        } else {

            timeLeft = 10;
        }

        timerLabel.setText(
                "Time Left: " + timeLeft
        );
    }

    // CHECK ANSWER
    public void checkAnswer() {

        Question q = questions.get(current);

        for (int i = 0; i < 4; i++) {

            if (options[i].isSelected()) {

                if (i == q.answer) {

                    score++;

                    options[i].setForeground(Color.GREEN);

                } else {

                    options[i].setForeground(Color.RED);

                    options[q.answer]
                            .setForeground(Color.GREEN);
                }
            }
        }

        scoreLabel.setText("Score: " + score);

        javax.swing.Timer delay =
                new javax.swing.Timer(1000, e -> {

                    current++;

                    loadQuestion();
                });

        delay.setRepeats(false);

        delay.start();
    }

    // TIMER
    public void startTimer() {

        timer = new javax.swing.Timer(1000, e -> {

            timeLeft--;

            timerLabel.setText(
                    "Time Left: " + timeLeft
            );

            if (timeLeft <= 0) {

                checkAnswer();
            }
        });

        timer.start();
    }

    // SAVE SCORE
    public void saveScore() {

        try {

            Connection con = getConnection();

            String query =
                    "INSERT INTO leaderboard(name,score,total) VALUES(?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(query);

            ps.setString(1, playerName);

            ps.setInt(2, score);

            ps.setInt(3, questions.size());

            ps.executeUpdate();

            con.close();

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    // LEADERBOARD
    public void showLeaderboard() {

        JFrame frame = new JFrame("Leaderboard");

        frame.setSize(400, 300);

        String[] columns = {
                "Name",
                "Score",
                "Total"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        try {

            Connection con = getConnection();

            Statement st =
                    con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT * FROM leaderboard ORDER BY score DESC"
            );

            while (rs.next()) {

                model.addRow(new Object[]{

                        rs.getString("name"),

                        rs.getInt("score"),

                        rs.getInt("total")
                });
            }

            con.close();

        } catch (Exception e) {

            System.out.println(e);
        }

        frame.add(new JScrollPane(table));

        frame.setVisible(true);
    }

    // RESULT
    public void showResult() {

        timer.stop();

        progressBar.setValue(
                questions.size()
        );

        saveScore();

        double percent =
                ((double) score / questions.size()) * 100;

        String grade;

        if (percent >= 80) {

            grade = "A";

        } else if (percent >= 60) {

            grade = "B";

        } else if (percent >= 40) {

            grade = "C";

        } else {

            grade = "Fail";
        }

        int choice = JOptionPane.showOptionDialog(
                this,

                "Quiz Completed\n\n"
                        + "Player: " + playerName
                        + "\nScore: " + score
                        + "/" + questions.size()
                        + "\nPercentage: " + percent + "%"
                        + "\nGrade: " + grade,

                "Result",

                JOptionPane.YES_NO_CANCEL_OPTION,

                JOptionPane.INFORMATION_MESSAGE,

                null,

                new String[]{
                        "Play Again",
                        "Certificate",
                        "Exit"
                },

                "Play Again"
        );

        if (choice == 0) {

            restartQuiz();

        } else if (choice == 1) {

            generateCertificate(
                    grade
            );

        } else {

            System.exit(0);
        }
    }

    // RESTART
    public void restartQuiz() {

        current = 0;

        score = 0;

        scoreLabel.setText("Score: 0");

        questions = QuestionBank.getQuestions();

        progressBar.setValue(0);

        loadQuestion();

        timer.start();
    }

    // CERTIFICATE
    public void generateCertificate(
            String grade
    ) {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            "Certificate.txt"
                    );

            writer.println(
                    "QUIZ CERTIFICATE"
            );

            writer.println(
                    "Player : " + playerName
            );

            writer.println(
                    "Score : "
                            + score
                            + "/"
                            + questions.size()
            );

            writer.println(
                    "Grade : " + grade
            );

            writer.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Certificate Saved"
            );

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    // MAIN
    public static void main(String[] args) {

        JFrame welcome = new JFrame(
                "Quiz App"
        );

        welcome.setSize(500, 350);

        welcome.setLocationRelativeTo(null);

        welcome.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title = new JLabel(
                "QUIZ COMPETITION APPLICATION"
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton start = new JButton(
                "Start Quiz"
        );

        start.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        start.addActionListener(e -> {

            welcome.dispose();

            new QuizApp();
        });

        panel.add(Box.createVerticalStrut(80));

        panel.add(title);

        panel.add(Box.createVerticalStrut(40));

        panel.add(start);

        welcome.add(panel);

        welcome.setVisible(true);
    }
}
