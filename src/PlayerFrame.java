import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    private JFrame video_frame;
    private int video_frame_width;
    private int video_frame_height;
    private int video_frame_number_begin;
    private int video_frame_number_end;
    private volatile PlayController controller;
    private Thread play_thread;

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
        setPlayerWindowProperties();
        addComponents();
        setVideoFrameProperties();
    }

    /**
     * 初始化播放器窗体属性
     */
    private void setPlayerWindowProperties() 
    {
        // 设置窗体的宽高和起始位置
        this.setBounds(0, 200, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // 设置标题  
        this.setTitle(DEFAULT_TITLE);  
        // 设置窗体的关闭方式
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        container.add(getControlButtonPane());
    }

    /**
     * 初始化视频帧属性
     */
    private void setVideoFrameProperties() 
    {
        video_frame_width = 176;
        video_frame_height = 144;
        video_frame_number_begin = 0;
        video_frame_number_end = 99999999;
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
        JTextField text_field1 = new JTextField("176");
        JTextField text_field2 = new JTextField("144");
        text_field1.setEditable(false);
        text_field2.setEditable(false);
        jp3.add(label_width);
        jp3.add(label_height);
        jp3.add(text_field1);
        jp3.add(text_field2);
        jp2.add(jp3);

        jrb_cif.addActionListener((event) -> {
            text_field1.setText("352");
            text_field2.setText("288");
            video_frame_width = 352;
            video_frame_height = 288;
            text_field1.setEditable(false);
            text_field2.setEditable(false);
        });

        jrb_qcif.addActionListener((event) -> {
            text_field1.setText("176");
            text_field2.setText("144");
            video_frame_width = 176;
            video_frame_height = 144;
            text_field1.setEditable(false);
            text_field2.setEditable(false);
        });

        jrb_other.addActionListener((event) -> {
            text_field1.setText("1280");
            text_field2.setText("720");
            video_frame_width = 1280;
            video_frame_height = 720;
            text_field1.setEditable(true);
            text_field2.setEditable(true);
        });

        text_field1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var content = text_field1.getText();
                if (!content.isBlank())
                    video_frame_width = Integer.parseInt(content);
                else {
                    text_field1.setText("0");
                    video_frame_width = 0;
                }
            }
        });

        text_field2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var content = text_field2.getText();
                if (!content.isBlank())
                    video_frame_height = Integer.parseInt(content);
                else {
                    text_field2.setText("0");
                    video_frame_height = 0;
                }
            }
        });


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

        // 帧率设置面板
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
        jcb.setSelectedIndex(5);

        inner_panel1.add(jl1);
        inner_panel1.add(jcb);

        // 播放帧范围设置面板
        JPanel inner_panel2 = new JPanel(null);

        JLabel jl2 = new JLabel("From");
        JTextField jtf1 = new JTextField("0");
        JLabel jl3 = new JLabel("To");
        JTextField jtf2 = new JTextField("0");

        jl2.setBounds(5, 10, 35, 25);
        jtf1.setBounds(45, 10, 70, 25);
        jl3.setBounds(135, 10, 35, 25);
        jtf2.setBounds(160, 10, 70, 25);

        jtf1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var content = jtf1.getText();
                if (!content.isBlank()) 
                {
                    video_frame_number_begin = Integer.parseInt(content);
                    if (video_frame_number_begin < 0)
                        video_frame_number_begin = 0;
                }
                else 
                {
                    jtf1.setText("0");
                    video_frame_number_begin = 0;
                }
            }
        });

        jtf2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                var content = jtf2.getText();
                if (!content.isBlank())
                    video_frame_number_end = Integer.parseInt(content);
                else 
                {
                    jtf2.setText("0");
                    video_frame_number_end = 99999999;
                }
            }
        });

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
    private JPanel getControlButtonPane() 
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

    /**
     * 为控制播放的按钮添加监听器
     */
    private void addJButtonListener(JButton jbtns[]) 
    {
        // Open File
        jbtns[1].addActionListener((event) -> {
            JFileChooser jfc = new JFileChooser();
            int flag = jfc.showOpenDialog(null);
            if (flag == JFileChooser.APPROVE_OPTION)
            {
                // System.out.println(jfc.getSelectedFile().getAbsolutePath());
                String filename = jfc.getSelectedFile().getAbsolutePath();
                video_frame = new JFrame("YUV Player of Java");
                video_frame.setIconImage(Toolkit.getDefaultToolkit().getImage("./images/icon.png"));
                video_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                controller = new PlayController(filename, video_frame_width, video_frame_height,
                 video_frame_number_begin, video_frame_number_end);
                video_frame.add(controller);
                video_frame.pack();
                video_frame.setVisible(true);
                play_thread = new Thread(() -> { controller.play(video_frame); });
                play_thread.start();
            }
        });

        // Play/Pause
        jbtns[3].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            if (jbtns[3].getText().equals("Play"))
            {
                if (cur_state == PlayController.PAUSE_FORWARD)
                    controller.setPlayState(PlayController.PLAY_FORWARD);
                else if (cur_state == PlayController.PAUSE_BACKWARD)
                    controller.setPlayState(PlayController.PLAY_BACKWARD);
                jbtns[3].setText("Pause");
            }
            else
            {
                if (cur_state == PlayController.PLAY_FORWARD)
                    controller.setPlayState(PlayController.PAUSE_FORWARD);
                else if (cur_state == PlayController.PLAY_BACKWARD)
                    controller.setPlayState(PlayController.PAUSE_BACKWARD);
                jbtns[3].setText("Play");
            }
        });

        // Next
        jbtns[0].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            switch (cur_state)
            {
                case PlayController.PLAY_FORWARD:
                    controller.setPlayState(PlayController.NEXT_PLAY_FORWARD);
                    break;
                case PlayController.PLAY_BACKWARD:
                    controller.setPlayState(PlayController.NEXT_PLAY_BACKWARD);
                    break;
                case PlayController.PAUSE_FORWARD:
                    controller.setPlayState(PlayController.NEXT_PAUSE_FORWARD);
                    break;
                case PlayController.PAUSE_BACKWARD:
                    controller.setPlayState(PlayController.NEXT_PAUSE_BACKWARD);
                    break;
            }
        });

        // Next5
        jbtns[2].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            switch (cur_state)
            {
                case PlayController.PLAY_FORWARD:
                    controller.setPlayState(PlayController.NEXT5_PLAY_FORWARD);
                    break;
                case PlayController.PLAY_BACKWARD:
                    controller.setPlayState(PlayController.NEXT5_PLAY_BACKWARD);
                    break;
                case PlayController.PAUSE_FORWARD:
                    controller.setPlayState(PlayController.NEXT5_PAUSE_FORWARD);
                    break;
                case PlayController.PAUSE_BACKWARD:
                    controller.setPlayState(PlayController.NEXT5_PAUSE_BACKWARD);
                    break;
            }
        });

        // Prev
        jbtns[4].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            switch (cur_state)
            {
                case PlayController.PLAY_FORWARD:
                    controller.setPlayState(PlayController.PREV_PLAY_FORWARD);
                    break;
                case PlayController.PLAY_BACKWARD:
                    controller.setPlayState(PlayController.PREV_PLAY_BACKWARD);
                    break;
                case PlayController.PAUSE_FORWARD:
                    controller.setPlayState(PlayController.PREV_PAUSE_FORWARD);
                    break;
                case PlayController.PAUSE_BACKWARD:
                    controller.setPlayState(PlayController.PREV_PAUSE_BACKWARD);
                    break;
            }
        });

        // Prev5
        jbtns[6].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            switch (cur_state)
            {
                case PlayController.PLAY_FORWARD:
                    controller.setPlayState(PlayController.PREV5_PLAY_FORWARD);
                    break;
                case PlayController.PLAY_BACKWARD:
                    controller.setPlayState(PlayController.PREV5_PLAY_BACKWARD);
                    break;
                case PlayController.PAUSE_FORWARD:
                    controller.setPlayState(PlayController.PREV5_PAUSE_FORWARD);
                    break;
                case PlayController.PAUSE_BACKWARD:
                    controller.setPlayState(PlayController.PREV5_PAUSE_BACKWARD);
                    break;
            }
        });

        // Close All
        jbtns[5].addActionListener((event) -> { 
            video_frame.dispose();
            var cur_state = controller.getPlayState();
            if (cur_state == PlayController.PLAY_FORWARD)
                controller.setPlayState(PlayController.PAUSE_FORWARD);
            else if (cur_state == PlayController.PLAY_BACKWARD)
                controller.setPlayState(PlayController.PAUSE_BACKWARD);
            jbtns[3].setText("Play");
        });

        // Quit
        jbtns[7].addActionListener((event) -> { System.exit(0); });

        // Backward/Forward
        jbtns[8].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            if (jbtns[8].getText().equals("Backward"))
            {
                if (cur_state == PlayController.PLAY_FORWARD)
                    controller.setPlayState(PlayController.PLAY_BACKWARD);
                else if (cur_state == PlayController.PAUSE_FORWARD)
                    controller.setPlayState(PlayController.PAUSE_BACKWARD);
                jbtns[8].setText("Forward");
            }
            else
            {
                if (cur_state == PlayController.PLAY_BACKWARD)
                    controller.setPlayState(PlayController.PLAY_FORWARD);
                else if (cur_state == PlayController.PAUSE_BACKWARD)
                    controller.setPlayState(PlayController.PAUSE_FORWARD);
                jbtns[8].setText("Backward");
            }
        });

        // Back to 0
        jbtns[9].addActionListener((event) -> {
            var cur_state = controller.getPlayState();
            switch (cur_state)
            {
                case PlayController.PLAY_FORWARD:
                    controller.setPlayState(PlayController.BACKTOZERO_PLAY_FORWARD);
                    break;
                case PlayController.PLAY_BACKWARD:
                    controller.setPlayState(PlayController.BACKTOZERO_PLAY_BACKWARD);
                    break;
                case PlayController.PAUSE_FORWARD:
                    controller.setPlayState(PlayController.BACKTOZERO_PAUSE_FORWARD);
                    break;
                case PlayController.PAUSE_BACKWARD:
                    controller.setPlayState(PlayController.BACKTOZERO_PAUSE_BACKWARD);
                    break;
            }
        });
    }

    /**
     * 启动播放器(设置可见性)
     */
    public void start() 
    {
        this.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new PlayerFrame().start();;
        });
    }
}
