import java.io.*;                     // 提供了输入和输出流，用于读写文件和其他类型的数据流
import java.awt.*;                    // 提供了访问平台本地窗口系统的类和接口
import java.awt.event.*;              // 提供了处理事件的类和接口，包括鼠标和键盘事件
import java.awt.image.*;              // 
import javax.imageio.*;               // 提供了读取和写入图像数据的类和接口
import javax.swing.*;                 // 提供了构建 GUI 应用程序的高级组件和工具类，例如按钮、文本框、表格等


public class PlayController extends Component 
{
	public static final int PLAY_FORWARD = 1;
	public static final int PLAY_BACKWARD = 2;
	public static final int PAUSE_FORWARD = 3;
	public static final int PAUSE_BACKWARD = 4;

	public static final int PREV_PLAY_FORWARD = 5;
	public static final int PREV_PLAY_BACKWARD = 6;
	public static final int PREV_PAUSE_FORWARD = 7;
	public static final int PREV_PAUSE_BACKWARD = 8;

	public static final int PREV5_PLAY_FORWARD = 9;
	public static final int PREV5_PLAY_BACKWARD = 10;
	public static final int PREV5_PAUSE_FORWARD = 11;
	public static final int PREV5_PAUSE_BACKWARD = 12;

	public static final int NEXT_PLAY_FORWARD = 13;
	public static final int NEXT_PLAY_BACKWARD = 14;
	public static final int NEXT_PAUSE_FORWARD = 15;
	public static final int NEXT_PAUSE_BACKWARD = 16;

	public static final int NEXT5_PLAY_FORWARD = 17;
	public static final int NEXT5_PLAY_BACKWARD = 18;
	public static final int NEXT5_PAUSE_FORWARD = 19;
	public static final int NEXT5_PAUSE_BACKWARD = 20;

	public static final int BACKTOZERO_PLAY_FORWARD = 21;
	public static final int BACKTOZERO_PLAY_BACKWARD = 22;
	public static final int BACKTOZERO_PAUSE_FORWARD = 23;
	public static final int BACKTOZERO_PAUSE_BACKWARD = 24;


	FileInputStream fin;
	DataInputStream data_in;
	private byte[] yuv_array;
	private int[] u_array;
	private int[] v_array;
	private int[] rgb_array;
	private BufferedImage img;   // 一个图像缓冲区，可以进行像素级的操作，例如设置像素颜色、裁剪和缩放图像等

	private String filename;
	private int width;
	private int height;
    private int frame_size;
    private int yuv_frame_size;
	private int frame_number_begin;
	private int frame_number_end;
	private int frame_number;
	private volatile int play_state = PAUSE_FORWARD;
    

    /**
     * 读取YUV文件并初始化类的成员变量
     * @param filename 视频文件名
     * @param width 视频帧宽度
     * @param height 视频帧高度
     * @param frame_number_begin 开始帧序列
	 * @param frame_number_end 结束帧序列
     */
    public PlayController(String filename, int width, int height, int frame_number_begin, int frame_number_end) 
    {
		this.filename = filename;
    	this.width = width;
    	this.height = height;
    	this.frame_size = width * height;  // CIF: 320x288, QCIF: 176x144
    	this.yuv_frame_size = (width * height * 3) >> 1;
		this.frame_number_begin = frame_number_begin;
		this.frame_number_end = frame_number_end;
		this.frame_number = frame_number_begin;
    	//在Heap分配空间
    	this.img = new BufferedImage(width, height, 1);  // 1:TYPE_INT_RGB
    	this.yuv_array = new byte[yuv_frame_size];
		this.u_array = new int[frame_size];
    	this.v_array = new int[frame_size];
    	this.rgb_array = new int[frame_size];

		try {
    		fin = new FileInputStream(new File(filename));
    		fin.skip(frame_number * yuv_frame_size);
    		data_in = new DataInputStream(fin);
    		data_in.read(yuv_array, 0, yuv_frame_size);
    		++this.frame_number;
    	} 
    	catch (IOException e) {  
            e.printStackTrace();  
        }
    	yuv2rgb();
    	img.setRGB(0, 0, width, height, rgb_array, 0, width);
    }

    /**
     * 将YUV格式的视频转换为RGB格式
     */
    private void yuv2rgb()
    {
    	int h;
    	int h2;
    	int frame_size2 = frame_size + (frame_size >> 2);
    	int width2 = width << 1;
    	int i2, j2;
    	
    	h = 0;
    	h2 = 0;
    	for (int j = 0; j < (height >> 1); ++j)
    	{
    		for (int i = 0; i < (width >> 1); ++i)
    		{
    			i2 = i << 1;
    			u_array[h2 + i2] = yuv_array[frame_size + h + i] & 0xff;
    			v_array[h2 + i2] = yuv_array[frame_size2 + h + i] & 0xff;
    		}
    		h += width >> 1;
    		h2 += width2;
    	}
    	//执行双线性内插，把4:1:1的YUV扩大为4:4:4的YUV
    	h2 = 0;
    	for (j2 = 0; j2 < height - 2; j2 += 2)
    	{
    		for (i2 = 0; i2 < width - 2; i2 += 2)
    		{
    			int a, b, ab;
    			
    			a = u_array[h2 + i2] + u_array[h2 + i2 + 2];//水平
    			b = u_array[h2 + i2] + u_array[h2 + i2 + width2];//垂直
    			ab = u_array[h2 + i2] + u_array[h2 + i2 + 2] + u_array[h2 + i2 + width2] + u_array[h2 + i2 + width2 + 2];//对角线
    			u_array[h2 + i2 + 1] = (a + 1) >> 1;
    			u_array[h2 + i2 + width] = (b + 1) >> 1;
    			u_array[h2 + i2 + width + 1] = (ab + 2) >> 2;
    			
    			a = v_array[h2 + i2] + v_array[h2 + i2 + 2];//水平
    			b = v_array[h2 + i2] + v_array[h2 + i2 + width2];//垂直
    			ab = v_array[h2 + i2] + v_array[h2 + i2 + 2] + v_array[h2 + i2 + width2] + v_array[h2 + i2 + width2 + 2];//对角线
    			v_array[h2 + i2 + 1] = (a + 1) >> 1;
    			v_array[h2 + i2 + width] = (b + 1) >> 1;
    			v_array[h2 + i2 + width + 1] = (ab + 2) >> 2;
    		}
			u_array[h2 + i2 + 1] = u_array[h2 + i2];
			u_array[h2 + i2 + width] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + width2] + 1) >> 1;
			
			v_array[h2 + i2 + 1] = v_array[h2 + i2];
			v_array[h2 + i2 + width] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + width2] + 1) >> 1;
			
			h2 += width2;
    	}
		for (i2 = 0; i2 < width - 2; i2 += 2)
		{
			u_array[h2 + i2 + 1] = 
			u_array[h2 + i2 + width + 1] = (u_array[h2 + i2] + u_array[h2 + i2 + 2] + 1) >> 1;
			u_array[h2 + i2 + width] = u_array[h2 + i2];
			
			v_array[h2 + i2 + 1] = 
			v_array[h2 + i2 + width + 1] = (v_array[h2 + i2] + v_array[h2 + i2 + 2] + 1) >> 1;
			v_array[h2 + i2 + width] = v_array[h2 + i2];
		}
		u_array[h2 + i2 + 1] =
		u_array[h2 + i2 + width] = 
		u_array[h2 + i2 + width + 1] = u_array[h2 + i2];   	
    	
		v_array[h2 + i2 + 1] =
		v_array[h2 + i2 + width] = 
		v_array[h2 + i2 + width + 1] = v_array[h2 + i2];
		
		//彩色空间变换,从YUV转换到RGB
		for (int i = 0; i < frame_size; ++i)
		{
			int pixel_r, pixel_g, pixel_b;
			int pixel_y = yuv_array[i] & 0xff;
			int pixel_u = u_array[i] - 128;
			int pixel_v = v_array[i] - 128;
			//YUV到RGB的矩阵变换运算
			double R = pixel_y - 0.001 * pixel_u + 1.402 * pixel_v;
			double G = pixel_y - 0.344 * pixel_u - 0.714 * pixel_v;
			double B = pixel_y + 1.772 * pixel_u + 0.001 * pixel_v;
			//限幅
			if (R > 255) 
                pixel_r = 255;
			else if (R < 0) 
                pixel_r = 0;
			else 
                pixel_r = (int)R;
			if (G > 255) 
                pixel_g = 255;
			else if (G < 0) 
                pixel_g = 0;
			else 
                pixel_g = (int)G;
			if (B > 255) 
                pixel_b = 255;
			else if (B < 0) 
                pixel_b = 0;
			else 
                pixel_b = (int)B;
			rgb_array[i] = (pixel_r << 16) | (pixel_g << 8) | pixel_b;
		}
    }

    /**
     * 获取当前画面的预期大小
     */
    public Dimension getPreferredSize() 
    {
        if (img == null) 
        {
             return new Dimension(width, height);
        } 
        else 
        {
           return new Dimension(img.getWidth(null), img.getHeight(null));
        }
    }

	/**
	 * 返回播放器的状态
	 * @return play_state PLAY为true, PAUSE为false
	 */
	public int getPlayState() 
	{
		return play_state;
	}

	/**
	 * 设置播放器状态
	 * @param state PLAY为true, PAUSE为false
	 */
	public void setPlayState(int state) 
	{
		play_state = state;
	}
    
    /**
     * 将当前帧绘制到屏幕上
     */
    @Override
    public void paint(Graphics g) 
    {
    	g.drawImage(img, 0, 0, null);
    }

	/**
	 * 顺序播放视频帧
	 * @param frame 视频帧显示的窗体
	 */
	private void playForward(JFrame frame) 
	{
		if (frame_number == frame_number_end + 1)
			return;
		try {
			data_in.read(yuv_array, 0, yuv_frame_size);
			frame.setTitle("YUV Player of Java #" + frame_number + " frames");
			++frame_number;
		} 
		catch (IOException e) {  
			e.printStackTrace();
		}
		yuv2rgb();
		img.setRGB(0, 0, width, height, rgb_array, 0, width);
	}

	/**
	 * 倒序播放视频帧
	 * @param frame 视频帧显示的窗体
	 */
	private void playBackward(JFrame frame)
	{
		--frame_number;
		if (frame_number < frame_number_begin)
			frame_number = frame_number_begin;
		try {
			fin = new FileInputStream(new File(filename));
			fin.skip(frame_number * yuv_frame_size);
			data_in = new DataInputStream(fin);
			data_in.read(yuv_array, 0, yuv_frame_size);
			frame.setTitle("Frame:" + frame_number + " " + filename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		yuv2rgb();
		img.setRGB(0, 0, width, height, rgb_array, 0, width);
	}

	/**
	 * 回退<code>setp</code>帧
	 * @param frame 视频帧显示窗体
	 * @param step 回退的帧数
	 * @param prev_state 回退之前的播放状态
	 */
	private void backToPrevFrame(JFrame frame, int step, int prev_state) 
	{
		frame_number = frame_number - step * 10 - 1;
		if (frame_number < frame_number_begin)
			frame_number = frame_number_begin;
		try {
			fin = new FileInputStream(new File(filename));
			fin.skip(frame_number * yuv_frame_size);
			data_in = new DataInputStream(fin);
			data_in.read(yuv_array, 0, yuv_frame_size);
			frame.setTitle("Frame:" + frame_number + " " + filename);
			++frame_number;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		yuv2rgb();
		img.setRGB(0, 0, width, height, rgb_array, 0, width);
		this.setPlayState(prev_state);
	}

	/**
	 * 快进<code>step</code>帧
	 * @param frame 视频帧显示的窗口
	 * @param step 快进的帧数
	 * @param prev_state 快进之前的播放状态
	 */
	private void goToNextFrame(JFrame frame, int step, int prev_state) {
		frame_number = frame_number + step * 10 - 1;
		if (frame_number > frame_number_end)
			frame_number = frame_number_end;
		try {
			fin = new FileInputStream(new File(filename));
			fin.skip(frame_number * yuv_frame_size);
			data_in = new DataInputStream(fin);
			data_in.read(yuv_array, 0, yuv_frame_size);
			frame.setTitle("Frame:" + frame_number + " " + filename);
			++frame_number;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		yuv2rgb();
		img.setRGB(0, 0, width, height, rgb_array, 0, width);
		this.setPlayState(prev_state);	
	}

	/**
	 * 回到视频开头
	 * @param frame 视频帧显示的窗口
	 * @param prev_state 前一刻的播放状态
	 */
	private void backToBeginFrame(JFrame frame, int prev_state) 
	{
		frame_number = frame_number_begin;
		try {
    		fin = new FileInputStream(new File(filename));
    		fin.skip(frame_number * yuv_frame_size);
    		data_in = new DataInputStream(fin);
    		data_in.read(yuv_array, 0, yuv_frame_size);
    		++this.frame_number;
    	} 
    	catch (IOException e) {   
            e.printStackTrace();  
        }
    	yuv2rgb();
    	img.setRGB(0, 0, width, height, rgb_array, 0, width);
		this.setPlayState(prev_state);
	}

    
    public void play(JFrame frame) 
    {
    	while(true)
    	{
			switch (play_state)
			{
				case PLAY_FORWARD:
					playForward(frame);
					break;
				case PLAY_BACKWARD:
					playBackward(frame);
					break;
				case PAUSE_FORWARD:
					break;
				case PAUSE_BACKWARD:
					break;
				case PREV_PLAY_FORWARD:
					backToPrevFrame(frame, 1, PLAY_FORWARD);
					break;
				case PREV_PLAY_BACKWARD:
					backToPrevFrame(frame, 1, PLAY_BACKWARD);
					break;
				case PREV_PAUSE_FORWARD:
					backToPrevFrame(frame, 1, PAUSE_FORWARD);
					break;
				case PREV_PAUSE_BACKWARD:
					backToPrevFrame(frame, 1, PAUSE_BACKWARD);
					break;
				case PREV5_PLAY_FORWARD:
					backToPrevFrame(frame, 5, PLAY_FORWARD);
					break;
				case PREV5_PLAY_BACKWARD:
					backToPrevFrame(frame, 5, PLAY_BACKWARD);
					break;
				case PREV5_PAUSE_FORWARD:
					backToPrevFrame(frame, 5, PAUSE_FORWARD);
					break;
				case PREV5_PAUSE_BACKWARD:
					backToPrevFrame(frame, 5, PAUSE_BACKWARD);
					break;
				case NEXT_PLAY_FORWARD:
					goToNextFrame(frame, 1, PLAY_FORWARD);
					break;
				case NEXT_PLAY_BACKWARD:
					goToNextFrame(frame, 1, PLAY_BACKWARD);
					break;
				case NEXT_PAUSE_FORWARD:
					goToNextFrame(frame, 1, PAUSE_FORWARD);
					break;
				case NEXT_PAUSE_BACKWARD:
					goToNextFrame(frame, 1, PAUSE_BACKWARD);
					break;
				case NEXT5_PLAY_FORWARD:
					goToNextFrame(frame, 5, PLAY_FORWARD);
					break;
				case NEXT5_PLAY_BACKWARD:
					goToNextFrame(frame, 5, PLAY_BACKWARD);
					break;
				case NEXT5_PAUSE_FORWARD:
					goToNextFrame(frame, 5, PAUSE_FORWARD);
					break;
				case NEXT5_PAUSE_BACKWARD:
					goToNextFrame(frame, 5, PAUSE_BACKWARD);
					break;
				case BACKTOZERO_PLAY_FORWARD:
    				backToBeginFrame(frame, PLAY_FORWARD);
    				break;
				case BACKTOZERO_PLAY_BACKWARD:
					backToBeginFrame(frame, PLAY_BACKWARD);
					break;
				case BACKTOZERO_PAUSE_FORWARD:
					backToBeginFrame(frame, PAUSE_FORWARD);
					break;
				case BACKTOZERO_PAUSE_BACKWARD:
					backToBeginFrame(frame, PAUSE_BACKWARD);
					break;
			}
			repaint();
    	}
    }

    /**
     * 将当前帧以指定格式保存为图像文件
     * @param formatname 图像格式
     * @param filename 保存文件名
     */
    public void writeFile(String formatname, String filename) 
    {
        try 
        {
            ImageIO.write(img, formatname, new File(filename));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();  
        }
    }
    
    /**
     * 单元测试
     */
	public static void main(String[] args) 
	{
        JFrame frame = new JFrame("YUV Player of Java");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
        PlayController con = new PlayController("./sequences/ShuttleStart_1280x720.yuv",
        1280, 720, 10, 600);        
        frame.add(con, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // con.writeFile("jpg", "out.jpg");
        // con.play(frame);     //连续读取并显示图像
	}
}
