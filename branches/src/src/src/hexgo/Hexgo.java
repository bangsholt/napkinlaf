// $Header$

package hexgo;

import com.bbn.openmap.omGraphics.OMColorChooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class is the main coordinating class for the Hexgo game application.
 */
public class Hexgo {
    private static Arbiter arbiter;
    private static Board board;			// the board we've created
    private static JLabel turn;			// where we display the turn
    private static boolean paintDebug;		// draw debug info?
    private static boolean paintRaw;		// draw raw painting debug info?

    private static JOptionPane scaleOptions;	// option pane for scaling
    private static JDialog scaleDialog;		// dialog box for scaling
    private static JSlider scaleSlider;		// slider for scaling

    // An Integer that means "cancel" for the dialog box
    private static Integer CANCEL = new Integer(JOptionPane.CANCEL_OPTION);

    private static final int INIT_SCALE = 30;	// default scaling to use
    private static final int MIN_SCALE = 10;	// minimum scaling to allow
    private static final int MAX_SCALE = // maximum scaling to allow
            MIN_SCALE + 2 * (INIT_SCALE - MIN_SCALE);

    /**
     * Should the <CODE>doBreak</CODE> method execute its conditional code?
     *
     * @see #doBreak
     */
    static boolean doBreak = false;	// debug: stop at breakpoint?

    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
        final JFrame frame = new JFrame("Hexgo");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        final int gap = 7;
        JPanel controlPanel = new JPanel(new BorderLayout(gap, gap));
        controlPanel.setBorder(new EmptyBorder(gap, gap, gap, gap));

        if (args.length == 0)
            args = new String[]{"Jane", "Joe"};

        arbiter = new Arbiter(args);

        Player[] players = arbiter.getPlayers();

        controlPanel.add(playerPanel(players), BorderLayout.CENTER);

        turn = new JLabel("Turn: 0");
        controlPanel.add(turn, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1));

        buttons.add(button("Rotate ->", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.rotateGroup(+1);
            }
        }));

        buttons.add(button("<- Rotate", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.rotateGroup(-1);
            }
        }));

        buttons.add(button("Play", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.playTiles(true);
            }
        }));

        buttons.add(button("Discard", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.discardTiles();
            }
        }));

        buttons.add(button("Scale...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rescaleDialog(frame);
            }
        }));

        controlPanel.add(buttons, BorderLayout.SOUTH);

        content.add(controlPanel, BorderLayout.WEST);

        JScrollPane scrolling = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        content.add(scrolling, BorderLayout.CENTER);

        addDebug(content);

        board = new Board(arbiter, turn);
        scrolling.getViewport().add(board);
        Dimension dim = new Dimension(550, 550);
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent ev) {
                board.requestFocus();
            }
        });

        frame.setSize(dim);
        frame.setVisible(true);
    }

    private static JButton button(String label, ActionListener action) {
        JButton button = new JButton(label);
        button.addActionListener(action);
        return button;
    }

    /**
     * Pops up the rescale dialog.
     *
     * @param frame The parent frame.
     */
    private static void rescaleDialog(JFrame frame) {
        if (scaleDialog == null) {
            scaleSlider = new JSlider(MIN_SCALE, MAX_SCALE, INIT_SCALE);
            scaleSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    board.newScale(scaleSlider.getValue());
                }
            });

            scaleOptions = new JOptionPane(scaleSlider,
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            scaleDialog = scaleOptions.createDialog(frame, "Scale Tiles");
        }

        double origScale = board.getScale();
        scaleSlider.setValue((int) Math.round(origScale));
        scaleDialog.show();
        Object selected = scaleOptions.getValue();
        if (selected == null || selected.equals(CANCEL))
            board.newScale(origScale);
    }

    /**
     * Returns a panel for showing the players.
     *
     * @param players The array of players
     *
     * @return A component that shows the players
     */
    private static JComponent playerPanel(Player[] players) {
        JPanel panel = new JPanel();

        JPanel names = new JPanel();
        JPanel scores = new JPanel();
        JPanel lastScores = new JPanel();
        names.setLayout(new GridLayout(players.length, 1));
        scores.setLayout(new GridLayout(players.length, 1));
        lastScores.setLayout(new GridLayout(players.length, 1));
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            JLabel nameLabel = player.getNameLabel();
            nameLabel.setIcon(player.getMarkerIcon());
            names.add(nameLabel);
            scores.add(player.getScoreLabel());
            lastScores.add(player.getLastScoreLabel());
        }
        panel.add(names);
        panel.add(scores);
        panel.add(lastScores);

        return panel;
    }

    /**
     * Adds the debug controls to the main window.
     *
     * @param content The content container for the main window.
     */
    private static void addDebug(Container content) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        final JCheckBox breakButton = new JCheckBox("break");
        breakButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doBreak = breakButton.isSelected();
            }
        });
        panel.add(breakButton);

        JButton playfieldButton = new JButton("Playfield");
        playfieldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = OMColorChooser.showDialog(board, "Playfield color",
                        board.getBackground());
                if (c != null)
                    board.setBackground(c);
            }
        });
        panel.add(playfieldButton);

        JButton mainButton = new JButton("Main");
        mainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = OMColorChooser.showDialog(board, "Main color", TileColors.MAIN.tile);
                if (c != null)
                    TileColors.MAIN = new TileColors(c, Color.black);
                board.repaint();
            }
        });
        panel.add(mainButton);

        JButton handButton = new JButton("Hand");
        handButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = OMColorChooser.showDialog(board, "Hand color", TileColors.HAND.tile);
                if (c != null)
                    TileColors.HAND = new TileColors(c, Color.black);
                board.repaint();
            }
        });
        panel.add(handButton);

        JButton selectedButton = new JButton("Selected");
        selectedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = OMColorChooser.showDialog(board, "Selected color", TileColors.SELECTED.tile);
                if (c != null)
                    TileColors.SELECTED = new TileColors(c, Color.black);
                board.repaint();
            }
        });
        panel.add(selectedButton);

        final JCheckBox debugChoice = new JCheckBox("Paint debug");
        debugChoice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintDebug = debugChoice.isSelected();
                board.repaint();
            }
        });
        panel.add(debugChoice);

        final JCheckBox rawChoice = new JCheckBox("Paint raw");
        rawChoice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintRaw = rawChoice.isSelected();
                board.repaint();
            }
        });
        panel.add(rawChoice);

        content.add(panel, BorderLayout.SOUTH);
    }

    /**
     * Returns whether debug stuff should be painted.
     *
     * @return Whether debug stuff should be painted.
     */
    public static boolean paintDebug() {
        return paintDebug;
    }

    /**
     * Returns whether raw debug stuff should be painted.
     *
     * @return Whether raw debug stuff should be painted.
     */
    public static boolean paintRaw() {
        return paintRaw;
    }

    /**
     * Executes a block of code only if <CODE>doBreak</CODE> is
     * <CODE>true</CODE>.  This allows the program code to determine if a
     * breakpoint will be executed.  The usage at the desired potential
     * breakpoint is simply. <PRE> doBreak(); </PRE> Then, be setting the
     * <CODE>doBreak</CODE> variable to true and putting a breakpoint inside the
     * conditional block of this method, that breakpoint will only be reached if
     * some running code has decided to make it so.  For example, a debugging
     * keystroke might be used to turn the breakpoint on when some user-visible
     * condition that makes it useful is set up that cannot easily be detected
     * by the debuggers own conditional breakpointng.
     */
    static void doBreak() {
        if (doBreak) {
            System.out.println("breaking");
        }
    }
}
