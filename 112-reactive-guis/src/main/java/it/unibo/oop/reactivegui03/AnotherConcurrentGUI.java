package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);
    private final JLabel display = new JLabel();

    /**
     * Builds Another Concurrent GUI.
     */
    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton stop = new JButton("stop");
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        /*
         * Create the counter agent and start it. This is actually not so good:
         * thread management should be left to
         * java.util.concurrent.ExecutorService
         */
        final Agent agent = new Agent();
        final TimeoutAgent timer = new TimeoutAgent(agent, up, down, stop);
        new Thread(timer).start();
        new Thread(agent).start();

        up.addActionListener(e -> {
            agent.upCounting();
            up.setEnabled(false);
            down.setEnabled(true);
        });

        down.addActionListener(e -> {
            agent.downCounting();
            down.setEnabled(false);
            up.setEnabled(true);
        });

        stop.addActionListener(e -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
    }

    private final class Agent implements Runnable {

        private volatile boolean stop;
        private volatile int direction;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter += this.direction;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        public void upCounting() {
            this.direction = 1;
        }

        public void stopCounting() {
            this.stop = true;

        }

        public void downCounting() {
            this.direction = -1;
        }
    }

    private final class TimeoutAgent implements Runnable {

        private static final int INTERRUPT = 10_000;
        private final Agent target;
        private final JButton up;
        private final JButton down;
        private final JButton stop;

        TimeoutAgent(final Agent target, final JButton up, final JButton down, final JButton stop) {
            this.target = target;
            this.up = up;
            this.down = down;
            this.stop = stop;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(INTERRUPT);
                SwingUtilities.invokeLater(() -> {
                    up.setEnabled(false);
                    down.setEnabled(false);
                    stop.setEnabled(false);
                });
            } catch (final InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

            target.stopCounting();
        }
    }
}
