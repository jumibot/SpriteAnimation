package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import contract.SpriteState;

/**
 * El HUD: el recuadro de arriba con las velocidades, una por línea.
 * La vista lo pinta en cada vuelta y le pregunta sobre qué línea está el ratón.
 */
public class Hud {

    // Dónde está. Y es la base de la primera línea (la de pintado); cada línea mide LINE.
    static final int X = 16;
    static final int Y = 40;
    static final int LINE = 24;
    static final int WIDTH = 320;

    private static final Color BACKGROUND = new Color(255, 255, 255, 40);
    private static final Color LABEL = new Color(0x7fd1ff);
    private static final Color TEXT = Color.WHITE;
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Font FONT_BOLD = FONT.deriveFont(Font.BOLD);

    /** Pinta la velocidad de pintado y la de cada sprite, una por línea. */
    public void paint(Graphics2D g, int renderFps, List<SpriteState> sprites) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int lines = 1 + sprites.size();
        g.setColor(BACKGROUND);
        // El recuadro de fondo, con las esquinas redondeadas: 8 px de margen a la izquierda y abajo.
        g.fillRoundRect(X - 8, Y - LINE, WIDTH, lines * LINE + 8, 10, 10);

        paintLine(g, 0, "Pintado", renderFps + " veces por segundo");
        for (int i = 0; i < sprites.size(); i++) {
            SpriteState state = sprites.get(i);
            paintLine(g, i + 1, state.name(), state.fps() + " fotogramas por segundo");
        }
    }

    /** Sobre qué línea cae el punto (x, y): 0, la de pintado; 1, 2…, los sprites; -1, fuera del HUD. */
    public int lineAt(int x, int y, int lines) {
        int left = X - 8;      // la esquina del recuadro de fondo, como en paint
        int top = Y - LINE;
        if (x < left || x >= left + WIDTH || y < top) {
            return -1;
        }

        int line = (y - top) / LINE;
        if (line >= lines) {
            return -1;   // por debajo de la última línea
        }
        return line;
    }

    private void paintLine(Graphics2D g, int line, String label, String value) {
        int y = Y + line * LINE - 6;   // la base del texto, 6 px por encima del final de la línea

        g.setFont(FONT_BOLD);
        g.setColor(LABEL);
        g.drawString(label, X, y);

        int x = X + g.getFontMetrics().stringWidth(label);   // el valor, justo detrás de la etiqueta
        g.setFont(FONT);
        g.setColor(TEXT);
        g.drawString(" · " + value, x, y);
    }
}
