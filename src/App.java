import java.awt.EventQueue;

public class App 
{
    public static void main(String[] args) throws Exception 
    {
        EventQueue.invokeLater(() -> {
            var player =  new PlayerFrame();
            player.start();
        });
    }
}
