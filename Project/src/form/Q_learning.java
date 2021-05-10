package form;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Q_learning extends JFrame implements Runnable {

	JPanel np, np_Control, np_Setting, sp, cp, cp2, cp3, jp[][] = new JPanel[7][10], ap;
	JButton runQ, stopQ, resetQ, barrier;
	JCheckBox jc;
	JLabel cnt, minP, jl;
	JTextField m_StartPointX, m_StartPointY, m_EndPointX, m_EndPointY;

	public static double m_arrexv[][][] = new double[10][7][4]; // up, right, down, left
	public static int m_x = 0, m_y = 0, m_ch = 0, m_gg = 0, m_minPath = 10000, m_errcnt = 0;
	int Dpoint[][] = { { 40, 25 }, { 70, 55 }, { 40, 85 }, { 10, 55 } };
	boolean run = false;

	public static ArrayList<String> road = new ArrayList<>();

	Thread th = new Thread(this);

	public Q_learning() {
		// TODO Auto-generated constructor stub
		setTitle("Q-Learning");
		setDefaultCloseOperation(2);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				run = false;
				th.stop();
			}
		});

		add(np = new JPanel(new BorderLayout()), BorderLayout.NORTH);
		add(cp = new JPanel(null), BorderLayout.CENTER);
		add(sp = new JPanel(), BorderLayout.SOUTH);

		np.add(np_Control = new JPanel(), BorderLayout.NORTH);
		np.add(np_Setting = new JPanel(new FlowLayout(FlowLayout.RIGHT)), BorderLayout.CENTER);

		np_Control.add(minP = new JLabel("�ּ� �̵� Ƚ�� : 0 ��"));
		minP.setFont(new Font("hy�߰��", Font.BOLD, 20));
		np_Control.add(cnt = new JLabel(" / �̵� Ƚ�� : 0 ��"));
		cnt.setFont(new Font("hy�߰��", Font.BOLD, 20));
		np_Control.add(runQ = new JButton("����"));
		np_Control.add(stopQ = new JButton("�ߴ�"));
		np_Control.add(jc = new JCheckBox("���ǥ��"));
		np_Control.add(barrier = new JButton("���� �庮"));
		np_Control.add(resetQ = new JButton("�ʱ�ȭ"));
		np_Setting.add(jl = new JLabel("���� ��ġ X :"));
		np_Setting.add(m_StartPointX = new JTextField(5));
		np_Setting.add(jl = new JLabel("Y :"));
		np_Setting.add(m_StartPointY = new JTextField(5));
		np_Setting.add(jl = new JLabel("���� ��ġ X :"));
		np_Setting.add(m_EndPointY = new JTextField(5));
		np_Setting.add(jl = new JLabel("Y :"));
		np_Setting.add(m_EndPointX = new JTextField(5));

		cp.setBorder(new EmptyBorder(10, 10, 10, 10));
		Size(cp, 1120, 780);

		cp.add(cp2 = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				if (jc.isSelected()) {
					for (int l = 0; l < road.size(); l++) {
						String p1[] = new String[2];
						String p2[] = road.get(l).split(",");
						if (l == 0) {
							p1[0] = Rei(m_StartPointX.getText()) + "";
							p1[1] = Rei(m_StartPointY.getText()) + "";
						} else {
							p1 = road.get(l - 1).split(",");
						}
						Graphics2D g2 = (Graphics2D) g;
						g2.setStroke(new BasicStroke(3));
						g2.setColor(Color.red);
						g2.drawLine(Rei(p1[0]) * 111 + 50, Rei(p1[1]) * 111 + 50, Rei(p2[0]) * 111 + 50,
								Rei(p2[1]) * 111 + 50);
					}
				}
			}
		});
		cp2.setBounds(10, 10, 1100, 770);
		cp2.setBackground(Color.red);

		cp.add(cp3 = new JPanel(new GridLayout(0, jp[0].length, 10, 10)));
		cp3.setBounds(10, 10, 1100, 770);

		for (int i = 0; i < jp.length; i++) {
			for (int j = 0; j < jp[0].length; j++) {
				final int i2 = i;
				final int j2 = j;
				cp3.add(jp[i][j] = new JPanel(new BorderLayout()));
				Size(jp[i][j], 100, 100);
				jp[i][j].setBorder(new LineBorder(Color.black));
				jp[i][j].add(ap = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						// TODO Auto-generated method stub
						g.setColor(Color.black);
						g.drawLine(0, 0, 100, 100);
						g.drawLine(100, 0, 0, 100);
						for (int k = 0; k < 4; k++) {
							g.drawString(m_arrexv[j2][i2][k] + "", Dpoint[k][0], Dpoint[k][1]);
						}
					}
				});
				jp[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						if (run) {
							Wmsg("��ã���߿��� �庮�� ������ ���մϴ�.");
							return;
						}
						if (i2 == Rei(m_StartPointX.getText()) && j2 == Rei(m_StartPointY.getText())
								|| i2 == Rei(m_EndPointX.getText()) && j2 == Rei(m_EndPointY.getText())) {
							Wmsg("��� �� ���� �������� �庮�� ������ ���մϴ�.");
							return;
						}
						if (jp[i2][j2].getBackground() == Color.black) {
							jp[i2][j2].setBackground(null);
						} else {
							jp[i2][j2].setBackground(Color.black);
						}
					}
				});
			}
		}

		int setendx = jp.length - 1, setendy = jp[0].length - 1;
		m_StartPointX.setText("0");
		m_StartPointY.setText("0");
		m_EndPointX.setText(setendx + "");
		m_EndPointY.setText(setendy + "");

		m_StartPointX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				StartEndPointPaint();
			}
		});
		m_StartPointY.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				StartEndPointPaint();
			}
		});
		m_EndPointX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				StartEndPointPaint();
			}
		});
		m_EndPointY.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				StartEndPointPaint();
			}
		});

		StartEndPointPaint();

		runQ.addActionListener(e -> { // ��ȭ �н� ����
			if (run) {
				return;
			}
			LockTextField();
			run = true;
			if (!th.isAlive()) {
				for (int i = 0; i < jp.length; i++) {
					for (int j = 0; j < jp[0].length; j++) {
						for (int k = 0; k < 4; k++) {
							m_arrexv[j][i][k] = 0.0;
						}
					}
				}
				m_minPath = 10000;
				m_x = Rei(m_StartPointX.getText());
				m_y = Rei(m_StartPointY.getText());
				th.start();
			}
		});

		stopQ.addActionListener(e -> { // ��ȭ �н� �ߴ�
			UnLockTextField();
			m_errcnt = 0;
			run = false;
			th = new Thread(this);
		});

		barrier.addActionListener(e -> { // ���� �庮 ��ġ
			if (run)
				Wmsg("��ã���߿��� �庮�� ������ ���մϴ�.");
			else {
				for (int i = 0; i < jp.length; i++) {
					Arrays.stream(jp[i]).map(x -> (JPanel) x).forEach(x -> x.setBackground(null));
				}
				Random r = new Random();
				for (int k = 0; k < 6; k++) {
					int x = r.nextInt(jp.length);
					int y = r.nextInt(jp[0].length);
					if (x == Rei(m_StartPointX.getText()) && y == Rei(m_StartPointY.getText())
							|| x == Rei(m_EndPointX.getText()) - 1 && y == Rei(m_EndPointY.getText()) - 1) {
						k--;
					} else
						jp[x][y].setBackground(Color.black);
				}
			}

		});

		resetQ.addActionListener(e -> { // ����
			run = false;
			UnLockTextField();
			for (int i = 0; i < jp.length; i++) {
				for (int j = 0; j < jp[0].length; j++) {
					for (int k = 0; k < 4; k++) {
						m_arrexv[j][i][k] = 0.0;
					}
				}
				Arrays.stream(jp[i]).map(x -> (JPanel) x).forEach(x -> x.setBackground(null));
			}
			m_StartPointX.setText("0");
			m_StartPointY.setText("0");
			m_EndPointX.setText(setendx + "");
			m_EndPointY.setText(setendy + "");
			StartEndPointPaint();
			road.clear();
			cnt.setText(" / �̵� Ƚ�� : 0 ��");
			minP.setText("�ּ� �̵� Ƚ�� : 0 ��");
			revalidate();
			repaint();
			m_minPath = 10000;
			m_errcnt = 0;
			th = new Thread(this);
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	void StartEndPointPaint() {
		for (int i = 0; i < jp.length; i++) {
			Arrays.stream(jp[i]).map(x -> (JPanel) x).forEach(x -> x.setBorder(new LineBorder(Color.black)));
		}
		try {
			jp[Rei(m_StartPointY.getText())][Rei(m_StartPointX.getText())].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.red));
			jp[Rei(m_EndPointX.getText())][Rei(m_EndPointY.getText())].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.blue));	
		} catch (Exception e) {
			// TODO: handle exception
			Wmsg("�ùٸ��� ���� ���� �Դϴ�.");
		}
		revalidate();
	}

	@Override
	public void run() { // ��ã��
		// TODO Auto-generated method stub

		while (run) {
			ArrayList<Integer> go = new ArrayList<>();
			m_ch = 0;

			for (int i = 0; i < 4; i++) {
				if (Min() == m_arrexv[m_x][m_y][i]) {
					go.add(i);
				}
			}
			road.add(m_x + "," + m_y); // ��� �߰�(��� ���̱�)

			cnt.setText(" / �̵� Ƚ�� : " + road.size() + " ��"); // �ǳ� Ƚ�� ǥ��

			if (road.size() >= 2000) { // ����ó��
				m_x = Rei(m_StartPointX.getText());
				m_y = Rei(m_StartPointY.getText());
				road.clear();
				m_errcnt++;
			}
			if (m_errcnt >= 10) { // ����ó��
				Wmsg("���� �̻��մϴ�! �溮�� �ѷ��ο� ���� ���ϴ°� ���ƿ�!");
				run = false;
				th = new Thread(this);
			}

			Random r = new Random();
			m_gg = go.get(r.nextInt(go.size()));

			// m_gg == 0 -> up
			// m_gg == 1 -> right
			// m_gg == 2 -> down
			// m_gg == 3 -> left

			if (m_gg == 0 && m_y == 0)
				Wall(); // �����ڸ� ó��
			else if (m_gg == 1 && m_x == jp[0].length - 1)
				Wall();
			else if (m_gg == 2 && m_y == jp.length - 1)
				Wall();
			else if (m_gg == 3 && m_x == 0)
				Wall();

			if (m_ch == 0) {
				switch (m_gg) {
				case 0:
					if (!Wallcheck(m_x, m_y - 1)) {
						m_y--;
						BackV();
					}
					break;
				case 1:
					if (!Wallcheck(m_x + 1, m_y)) {
						m_x++;
						BackV();
					}
					break;
				case 2:
					if (!Wallcheck(m_x, m_y + 1)) {
						m_y++;
						BackV();
					}
					break;
				case 3:
					if (!Wallcheck(m_x - 1, m_y)) {
						m_x--;
						BackV();
					}
					break;
				}
			}

			// ����
			if (m_y == Rei(m_EndPointX.getText()) && m_x == Rei(m_EndPointY.getText())) {
				double min = Min();

				switch (m_gg) {
				case 0:
					SetExpected(m_x, m_y + 1, m_gg, min);
					break;
				case 1:
					SetExpected(m_x - 1, m_y, m_gg, min);
					break;
				case 2:
					SetExpected(m_x, m_y - 1, m_gg, min);
					break;
				case 3:
					SetExpected(m_x + 1, m_y, m_gg, min);
					break;
				}

				road.add(m_x + "," + m_y);
				m_x = Rei(m_StartPointX.getText());
				m_y = Rei(m_StartPointY.getText());
				cnt.setText(" / �̵� Ƚ�� : " + road.size() + " ��");
				if (m_minPath > road.size()) {
					minP.setText("�ּ� �̵� Ƚ�� : " + road.size() + " ��");
					m_minPath = road.size();
				}

				repaint();
				try {
					th.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
				road.clear();
			}

			try {
//				th.sleep(1); //������ �ֱ�
			} catch (Exception e) {
				// TODO: handle exception
			}

			revalidate();
			repaint();
		}
	}

	void BackV() { // ��밪 ����
		double min = Min2();

		if (min == 0)
			return;
		switch (m_gg) {
		case 0:
			SetExpected(m_x, m_y + 1, m_gg, min);
			break;
		case 1:
			SetExpected(m_x - 1, m_y, m_gg, min);
			break;
		case 2:
			SetExpected(m_x, m_y - 1, m_gg, min);
			break;
		case 3:
			SetExpected(m_x + 1, m_y, m_gg, min);
			break;
		}
	}

	void SetExpected(int x, int y, int gg, Double min) { // ��밪 ���
		m_arrexv[x][y][gg] = Math.round((min + 0.1) * 10) / 10.0;
	}

	double Min() { // �ּҰ�
		double min = m_arrexv[m_x][m_y][0];

		for (int i = 1; i < 4; i++) {
			if (min > m_arrexv[m_x][m_y][i])
				min = m_arrexv[m_x][m_y][i];
		}
		return min;
	}

	double Min2() { // 0 �� ������ �ּҰ�
		double min = 999;

		for (int i = 0; i < 4; i++) {
			if (min > m_arrexv[m_x][m_y][i] && m_arrexv[m_x][m_y][i] != 0)
				min = m_arrexv[m_x][m_y][i];
		}
		if (min == 999)
			min = 0;
		return min;
	}

	void Wall() { // �� Ȯ��
		m_arrexv[m_x][m_y][m_gg] = 1000;
		m_x = Rei(m_StartPointX.getText());
		m_y = Rei(m_StartPointY.getText());
		m_ch = 1;
		road.clear();
	}

	boolean Wallcheck(int x, int y) { // �庮 ��ġ Ȯ��
		if (jp[y][x].getBackground() == Color.black) {
			Wall();
			return true;
		} else {
			return false;
		}
	}

	void LockTextField() {
		m_StartPointX.setEnabled(false);
		m_StartPointY.setEnabled(false);
		m_EndPointX.setEnabled(false);
		m_EndPointY.setEnabled(false);
		revalidate();
	}

	void UnLockTextField() {
		m_StartPointX.setEnabled(true);
		m_StartPointY.setEnabled(true);
		m_EndPointX.setEnabled(true);
		m_EndPointY.setEnabled(true);
		revalidate();
	}

	public void Size(JComponent c, int x, int y) {
		c.setPreferredSize(new Dimension(x, y));
	}

	public void Imsg(String s) {
		JOptionPane.showMessageDialog(null, s, "�޽���", JOptionPane.INFORMATION_MESSAGE);
	}

	public void Wmsg(String s) {
		JOptionPane.showMessageDialog(null, s, "�޽���", JOptionPane.ERROR_MESSAGE);
	}

	public int Rei(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Q_learning();
	}

}