import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


public class PlayerFrame extends JFrame
{
    private final String DEFAULT_TITLE = "YUL Player";
    private final int DEFAULT_WIDTH = 700;
    private final int DEFAULT_HEIGHT = 210;

    private PlayController controller;

    /**
     * 默认构造方法
     */
    public PlayerFrame() 
    {
        super();
        init();
    }

    /**
     * 有参数的构造方法
     */
    public PlayerFrame(String title) 
    {
        this();
    }

    /**
     * 初始化
     */
    private void init() 
    {
        setFrameProperty();
        addComponents();
        
        this.setVisible(true);
    }

    private void setFrameProperty() 
    {
        // 设置窗体的宽高和起始位置
        this.setBounds(0, 200, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // 设置标题  
        this.setTitle(DEFAULT_TITLE);  
        // 设置窗体的关闭方式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // // 设置居中
        // this.setLocationRelativeTo(null); 
        // // 设置置顶
        // this.setAlwaysOnTop(true);  
        // 设置窗体的最大最小化
        this.setResizable(false);
        // 设置图标
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("./images/icon.png"));
    }

    /**
     * 添加组件
     */
    private void addComponents() 
    {
        Container container = this.getContentPane();
        container.setLayout(null);
        container.add(getFrameSizePane());
        container.add(getPlayParametersPane());
        container.add(getcontrollertrolButtonPane());
    }

    /**
     * 获取设置视频分辨率的面板
     */
    private JPanel getFrameSizePane() 
    {
        JPanel pane = new JPanel(new GridLayout(1, 2));
        pane.setBorder(BorderFactory.createTitledBorder("Frame Size"));
        pane.setBounds(10, 5, 200, 160);

        JPanel jp1 = new JPanel(new GridLayout(3, 1, 10, 15));
        JRadioButton jrb_cif = new JRadioButton("CIF");
        JRadioButton jrb_qcif = new JRadioButton("QCIF", true);
        JRadioButton jrb_other = new JRadioButton("Other");
        ButtonGroup btn_group = new ButtonGroup();
        btn_group.add(jrb_cif);
        btn_group.add(jrb_qcif);
        btn_group.add(jrb_other);
        jp1.add(jrb_cif);
        jp1.add(jrb_qcif);
        jp1.add(jrb_other);

        JPanel jp2 = new JPanel(null);
        JPanel jp3 = new JPanel(new GridLayout(2, 2, 10, 0));
        jp3.setBounds(0, 80, 95, 50);
        JLabel label_width = new JLabel("Width");
        JLabel label_height = new JLabel("Height");
        JTextField text_field1 = new JTextField("352");
        JTextField text_field2 = new JTextField("288");
        jp3.add(label_width);
        jp3.add(label_height);
        jp3.add(text_field1);
        jp3.add(text_field2);
        jp2.add(jp3);

        pane.add(jp1);
        pane.add(jp2);
        return pane;
    }

    /**
     * 获取用于设置播放参数的面板
     */
    private JPanel getPlayParametersPane() 
    {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createTitledBorder("Play Parameters"));
        panel.setBounds(220, 5, 245, 160);

        JPanel inner_panel1 = new JPanel(null);
        JLabel jl1 = new JLabel("Frame Rate");
        jl1.setBounds(5, 10, 100, 25);
        JComboBox<Integer> jcb = new JComboBox<>();
        jcb.setBounds(120, 10, 110, 25);
        jcb.setEditable(true);
        jcb.addItem(5);
        jcb.addItem(10);
        jcb.addItem(15);
        jcb.addItem(20);
        jcb.addItem(25);
        jcb.addItem(30);
        jcb.addItem(999);
        inner_panel1.add(jl1);
        inner_panel1.add(jcb);

        JPanel inner_panel2 = new JPanel(null);
        JLabel jl2 = new JLabel("From");
        JTextField jtf1 = new JTextField("0");
        JLabel jl3 = new JLabel("To");
        JTextField jtf2 = new JTextField("0");
        jl2.setBounds(5, 10, 35, 25);
        jtf1.setBounds(45, 10, 70, 25);
        jl3.setBounds(135, 10, 35, 25);
        jtf2.setBounds(160, 10, 70, 25);
        inner_panel2.add(jl2);
        inner_panel2.add(jtf1);
        inner_panel2.add(jl3);
        inner_panel2.add(jtf2);

        JRadioButton zoom2x2 = new JRadioButton("Zoom2x2", true);

        panel.add(inner_panel1);
        panel.add(inner_panel2);
        panel.add(zoom2x2);
        return panel;
    }

    /**
     * 获取带有播放控制按钮的面板
     */
    private JPanel getcontrollertrolButtonPane() 
    {
        JPanel pane = new JPanel(new GridLayout(5, 2, 5, 0));
        pane.setBounds(470, 13, 210, 150);

        JButton[] jbtns = {
            new JButton("Next"),
            new JButton("Open File"),
            new JButton("Next 5"),
            new JButton("Play"),
            new JButton("Previrous"),
            new JButton("Close All"),
            new JButton("Previrous 5"),
            new JButton("Quit"),
            new JButton("Backward"),
            new JButton("Back to 0")
        };
        addJButtonListener(jbtns);
        for (JButton jbtn : jbtns)
            pane.add(jbtn);
        return pane;
    }

    private void addJButtonListener(JButton jbtns[]) {
        jbtns[1].addActionListener((event) -> {
            JFileChooser jfc = new JFileChooser();
            var state = jfc.showOpenDialog(null);
            if (state == JFileChooser.APPROVE_OPTION)
            {
                // System.out.println(jfc.getSelectedFile().getAbsolutePath());
                String filename = jfc.getSelectedFile().getAbsolutePath();
                JFrame play_frame = new JFrame("YUV Player of Java");
                play_frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {System.exit(0);}
                });
                this.controller = new PlayController(filename, 1280, 720, 10);
                play_frame.add("Center", controller);
                play_frame.pack();
                play_frame.setVisible(true);
                this.controller.play(play_frame);
            }
        });

        jbtns[3].addActionListener((event) -> {
            if (jbtns[3].getText() == "Play")
            {
                this.controller.play(this);
                jbtns[3].setText("Pause");
            }
            else
            {
                jbtns[3].setText("Play");
            }  
        });

        jbtns[7].addActionListener((event) -> { System.exit(0); });
    }

    public static void main(String[] args) {
        new PlayerFrame();
    }
}
