import java.awt.Canvas;
import java.awt.Dimension;

/**
 * El lienzo. No pinta: es la superficie sobre la que pinta el hilo de la vista.
 * Es un Canvas porque permite pintar de forma explícita, cuando lo decide nuestro hilo.
 */
public class Viewer extends Canvas {

    public Viewer(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setIgnoreRepaint(true);   // nada se dibuja solo: Swing no debe repintarlo por su cuenta
    }
}
