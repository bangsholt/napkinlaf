
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class ic {
    public static void main(String[] args) throws Exception {
	JPanel c = new JPanel() {
	    public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paint(g1);
		BufferedImage img = new BufferedImage(100, 100,
			BufferedImage.TYPE_BYTE_INDEXED);
		System.out.println(img.getColorModel());
	    }
	    public Dimension getMinimumSize() {
		return getPreferredSize();
	    }

	    public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	    }

	    public Dimension getMaximumSize() {
		return getPreferredSize();
	    }
	};
	JFrame frame = new JFrame("test");
	c.setBackground(Color.blue);
	frame.getContentPane().setLayout(new FlowLayout());
	frame.getContentPane().add(c);
	frame.setSize(200, 200);
	frame.setBackground(Color.green);
	frame.getContentPane().setBackground(Color.green);
	frame.setVisible(true);
	Thread.sleep(3000);
    }
}
