package swarm;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;

public class Swarm extends JFrame {

	private static int FLIES = 1000;
	private static boolean DIAGONALS = false;
	private static final int SX = 600;
	private static final int SY = 600;
	private static int DELAY = 10;
	private static int ADIST = 0; //Acceptable distance
	private static int speed = 5;
	private static boolean cls = false;
	private static boolean RUNNING = false;
	private Random rand = new Random();
	private BufferedImage img;
	/*   405
	 *   \|/
	 *  3-*-1
	 *   /|\
	 *   726
	 */
	
	private Fly[] flies;
	private SwarmPanel swarmPanel;
	public Swarm() {
		super("Swarm Intelligence");
		setSize(615, 675);
		flies = new Fly[FLIES];
		reset();
		createGUI();
		img = new BufferedImage(600, 600, BufferedImage.TYPE_INT_BGR);
		img.getGraphics().fillRect(0, 0, 600, 600);
		Thread updater = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (RUNNING) {
						Graphics g = img.getGraphics();
						if(cls) {
							//g.setColor(Color.white);
							g.fillRect(0, 0, 600, 600);
						}
						for (int i = 0; i < FLIES; i++) {
							flies[i].update();
							flies[i].draw(g);
						}
						swarmPanel.repaint();
					}
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						System.out.println("Update thread was rudely interrupted.");
					}
				}
			}
		});
		updater.start();
		
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void reset() {
		flies = new Fly[FLIES];
		for(int i=0; i<FLIES; i++) {
			flies[i] = new Fly(SX/2, SY/2, i);
		}
		flies[0].setColor(Color.red);
	}
	public void createGUI() {
		JPanel optionPanel = new JPanel();
		JLabel fLabel = new JLabel("Flies:");
		optionPanel.add(fLabel);
		final JTextField flyField = new JTextField(5);
		flyField.setText(Integer.toString(FLIES));
		flyField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FLIES = Integer.parseInt(flyField.getText());
				reset();
			}
		});
		optionPanel.add(flyField);
		JLabel adLabel = new JLabel("Range:");
		optionPanel.add(adLabel);
		final JTextField adField = new JTextField(5);
		adField.setText(Integer.toString(ADIST));
		adField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ADIST = Integer.parseInt(adField.getText());
			}
		});
		optionPanel.add(adField);
		final JCheckBox diagBox = new JCheckBox("Diagonals");
		diagBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DIAGONALS = diagBox.isSelected();
			}
		});
		optionPanel.add(diagBox);
		final JCheckBox clsBox = new JCheckBox("Clear Screen");
		clsBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cls = clsBox.isSelected();
			}
		});
		optionPanel.add(clsBox);
		final JButton stspButton = new JButton("Start");
		stspButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(RUNNING) {
					RUNNING = false;
					stspButton.setText("Start");
				}
				else {
					RUNNING = true;
					stspButton.setText("Stop");
				}
			}
		});
		stspButton.requestFocus();
		optionPanel.add(stspButton);
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
				img.getGraphics().fillRect(0, 0, 600, 600);
				swarmPanel.repaint();
			}
		});
		optionPanel.add(resetButton);
		
		swarmPanel = new SwarmPanel();
		
		setLayout(new BorderLayout());
		add(optionPanel, BorderLayout.SOUTH);
		add(swarmPanel, BorderLayout.CENTER);
	}
	private class SwarmPanel extends JPanel {
		public SwarmPanel() {
			
		}
		
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}
	}
	
	
	private class Fly {
		private int id;
		private int x, y;
		private int size = 3;
		//private int speed = 5;
		private Color color = Color.blue;
		//private Random rand = new Random();
		
		public Fly(int x, int y, int id) {
			this.x=x; this.y=y;
		}
		public void setSize(int s) {
			size = s;
		}
		public int getX() {return x;}
		public int getY() {return y;}
		
		public void setColor(Color c) {
			color = c;
		}
		
		public void setPos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void update() {
			int delx=0, dely=0;
			int dir;
			while (true) {
				dir = rand.nextInt((DIAGONALS) ? 8 : 4);
				//System.out.println(dir);
				switch (dir) {
				case 0:
					dely = -speed;
					break;
				case 1:
					delx = speed;
					break;
				case 2:
					dely = speed;
					break;
				case 3:
					delx = -speed;
					break;
				case 4:
					delx = (int) -(speed / Math.sqrt(2));
					dely = (int) -(speed / Math.sqrt(2));
					break;
				case 5:
					delx = (int) (speed / Math.sqrt(2));
					dely = (int) -(speed / Math.sqrt(2));
					break;
				case 6:
					delx = (int) (speed / Math.sqrt(2));
					dely = (int) (speed / Math.sqrt(2));
					break;
				case 7:
					delx = (int) -(speed / Math.sqrt(2));
					dely = (int) (speed / Math.sqrt(2));
					break;
				}
				//System.out.println("(" + delx + ", " + dely + ")");
				if(checkMove(delx, dely)) {
					//System.out.println("(" + delx + ", " + dely + ")");
					x+=delx;
					y+=dely;
					return;
				}
				return;
				//x+=delx;
				//y+=dely;
				//return;
			}
		}
		
		private boolean checkMove(int delx, int dely) {
			for(int i=0; i<FLIES; i++) {
				if(i==id) continue;
				if(Math.abs((x+delx)-flies[i].getX())<ADIST && Math.abs((y+dely)-flies[i].getY())<ADIST) {
					//System.out.println("flies[" + id + "]: #" + i + " is too close");
					return false;
				}
			}
			if(x+delx>595 || x+delx<0 || y+dely>595 || y+dely<0) {
				return false;
			}
			return true;
		}
		
		public void draw(Graphics g) {
			g.setColor(color);
			g.fillRect(x, y, size, size);
		}
	}
	
	public static void main(String[] args) {
		new Swarm();
	}

}
