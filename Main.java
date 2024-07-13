import javax.swing.*;
import java.awt.*;

class Main
{  
    @SuppressWarnings("deprecation")
    public static void main(String[] args)
    {  
        myJFrame frame = new myJFrame();    
        frame.show();
        frame.setVisible(true);
    }
}

class myJFrame extends JFrame
{
    GameScreen gameScreen;
    

    public myJFrame()
    {
        setTitle("Ascend");
        setSize(new Dimension(500,700));
        setResizable(false);
     
        // Center the window on the screen
        setLocationRelativeTo(null);
    
        //KeyListener
        KeyList KL = new KeyList();
        addKeyListener(KL);

        //Content
        gameScreen = new GameScreen(KL,this);
        Container container = getContentPane();
        container.add(gameScreen);
    }

}



           
    

 
