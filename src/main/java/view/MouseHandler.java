package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import contract.SpriteView;
import contract.PresenterContract;

/**
 * Escucha el ratón sobre el lienzo y avisa al presentador. Parte interna de la vista: la crea ella.
 * Para saber qué hay bajo el puntero, pide los SpriteView al presentador en ese momento; qué hacer lo decide él.
 * Swing llama a estos métodos desde su hilo de eventos.
 */
public class MouseHandler extends MouseAdapter {

    private final Hud hud;   // para saber sobre qué línea del HUD gira la rueda
    private PresenterContract presenter;

    // Estado del arrastre: solo lo usa el hilo de eventos, así que no necesita volatile.
    private int draggedSprite = -1;   // qué sprite se arrastra (-1: ninguno)
    private int grabX;                // dónde se ha cogido, medido desde la esquina del sprite
    private int grabY;

    public MouseHandler(Hud hud) {
        this.hud = hud;
    }

    /** A quién avisar. Lo llama la vista cuando recibe el presentador. */
    public void setPresenter(PresenterContract presenter) {
        this.presenter = presenter;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int lines = 1 + this.presenter.spriteCount();           // la de pintado y una por sprite
        int line = this.hud.lineAt(e.getX(), e.getY(), lines);  // 0: pintado; 1, 2…: sprites
        if (line < 0) {
            return;                                              // fuera del HUD, no hace nada
        }

        int delta = -e.getWheelRotation();                      // rueda hacia arriba: sube
        if (line == 0) {
            this.presenter.changeRenderSpeed(delta);
        } else {
            this.presenter.changeSpriteSpeed(line - 1, delta);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        List<SpriteView> spriteViews = this.presenter.spriteViews();     // cómo se ve todo ahora
        this.draggedSprite = this.spriteAt(spriteViews, e.getX(), e.getY());
        if (this.draggedSprite < 0) {
            return;   // no hay sprite bajo el puntero
        }

        // Guardamos dónde se ha cogido el sprite, para que no salte al puntero.
        SpriteView spriteView = spriteViews.get(this.draggedSprite);
        this.grabX = e.getX() - spriteView.x();
        this.grabY = e.getY() - spriteView.y();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.draggedSprite >= 0) {
            this.presenter.moveSprite(this.draggedSprite, e.getX() - this.grabX, e.getY() - this.grabY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.draggedSprite = -1;
    }

    /** Qué sprite hay en un punto (-1 si ninguno). Busca de arriba abajo: se pinta en orden, así que el último queda encima. */
    private int spriteAt(List<SpriteView> spriteViews, int x, int y) {
        for (int i = spriteViews.size() - 1; i >= 0; i--) {
            SpriteView spriteView = spriteViews.get(i);
            boolean inside = x >= spriteView.x() && x < spriteView.x() + spriteView.image().getWidth()
                          && y >= spriteView.y() && y < spriteView.y() + spriteView.image().getHeight();
            if (inside) {
                return i;
            }
        }

        return -1;
    }
}
