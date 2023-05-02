import java.io.*;                     // 提供了输入和输出流，用于读写文件和其他类型的数据流
import java.util.TreeSet;             // 提供了一个集合类，用于存储和操作一组按自然顺序排序的对象
import java.awt.*;                    // 提供了访问平台本地窗口系统的类和接口
import java.awt.event.*;              // 提供了处理事件的类和接口，包括鼠标和键盘事件
import java.awt.geom.AffineTransform; // 提供了一个用于表示 2D 图形变换的类
import java.awt.image.*;              // 
import javax.imageio.*;               // 提供了读取和写入图像数据的类和接口
import javax.swing.*;                 // 提供了构建 GUI 应用程序的高级组件和工具类，例如按钮、文本框、表格等

public class PlayController extends Component 
{
	public static final boolean PLAY = true;
	public static final boolean PAUSE = false;

	DataInputStream data_in;
	private byte[] yuv_array;
	private int[] u_array;
	private int[] v_array;
	private int[] rgb_array;
	private BufferedImage img;              // 一个图像缓冲区，可以进行像素级的操作，例如设置像素颜色、裁剪和缩放图像等
	private int width;
	private int height;
	private int frame_number;
    private int frame_size;
    private int yuv_frame_size;
	private boolean play_state = PAUSE;
    
    /**
     * 读取YUV文件并初始化类的成员变量
     * @param filename 视频文件名
     * @param width 视频帧宽度
     * @param height 视频帧高度
     * @param frame_number 帧序列
     */
    public PlayController(String filename, int width, int height, int frame_number) 
    {
    	this.width = width;
    	this.height = height;
    	this.frame_size = width * height;  // CIF: 320x288, QCIF: 176x144
    	this.frame_number = frame_number;
    	this.yuv_frame_size = (width * height * 3) >> 1;
    	//在Heap分配空间
    	this.img = new BufferedImage(width, height, 1);  // 1:TYPE_INT_RGB
    	this.yuv_array = new byte[yuv_frame_size];
		this.u_array = new int[frame_size];
    	this.v_array = new int[frame_size];
    	this.rgb_array = new int[frame_size];

		try 
    	{
    		FileInputStream fin = new FileInputStream(new File(filename));
    		fin.skip(frame_number * yuv_frame_size);
    		data_in = new DataInputStream(fin);
    		data_in.read(yuv_array, 0, yuv_frame_size);
    		++this.frame_number;
    	} 
    	catch (IOException e) 
    	{  
            // TODO Auto-generated catch block  
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
    			int a, b;
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
			int a, b, ab;
			
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
	public boolean getPlayState() 
	{
		return play_state;
	}

	/**
	 * 设置播放器状态
	 * @param state PLAY为true, PAUSE为false
	 */
	public void setPlayState(boolean state) 
	{
		play_state = state;
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
     * 将当前帧绘制到屏幕上
     */
    @Override
    public void paint(Graphics g) 
    {
    	g.drawImage(img, 0, 0, null);
    }
    
    public void play(JFrame frame) 
    {
    	while(true)
    	{
			if (play_state == PLAY) 
			{
				try 
				{
					data_in.read(yuv_array, 0, yuv_frame_size);
					frame.setTitle("YUV Player of Java #" + frame_number + " frames");
					++frame_number;
				} 
				catch (IOException e) 
				{  
					// TODO Auto-generated catch block  
					e.printStackTrace();
				}
				yuv2rgb();
				img.setRGB(0, 0, width, height, rgb_array, 0, width);
			}
			repaint(); 
    	}
    }

    
    /**
     * 单元测试
     */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
        JFrame frame = new JFrame("YUV Player of Java");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
        PlayController con = new PlayController("./sequences/ShuttleStart_1280x720.yuv",
        1280, 720, 10);        
        frame.add(con, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // con.writeFile("jpg", "out.jpg");
        // con.play(frame);     //连续读取并显示图像
	}
}
