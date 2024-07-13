import java.awt.event.*;


public class KeyList implements KeyListener {
    boolean left, right, shift;

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_D:
                right = true;
                break;
            case KeyEvent.VK_SHIFT:
                shift = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_A:
                left = false;
                break;
            case KeyEvent.VK_D:
                right = false;  
                break;
            case KeyEvent.VK_SHIFT:
                shift = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this implementation
    }
}
