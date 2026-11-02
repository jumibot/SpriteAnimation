package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import contract.PresenterContract;
import contract.SpriteState;

/**
 * Escucha el ratón sobre el lienzo y avisa al presentador. Parte interna de la vista: la crea ella.
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
        int lines = 1 + presenter.sprites().size();             // la de pintado y una por sprite
        int line = hud.lineAt(e.getX(), e.getY(), lines);       // 0: pintado; 1, 2…: sprites
        if (line < 0) {
            return;                                              // fuera del HUD, no hace nada
        }

        int delta = -e.getWheelRotation();                      // rueda hacia arriba: sube
        if (line == 0) {
            presenter.changeRenderSpeed(delta);
        } else {
            presenter.changeSpriteSpeed(line - 1, delta);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        draggedSprite = presenter.spriteAt(e.getX(), e.getY());
        if (draggedSprite < 0) {
            return;   // no hay sprite bajo el puntero
        }

        // Guardamos dónde se ha cogido el sprite, para que no salte al puntero.
        SpriteState state = presenter.sprites().get(draggedSprite);
        grabX = e.getX() - state.x();
        grabY = e.getY() - state.y();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedSprite >= 0) {
            presenter.moveSprite(draggedSprite, e.getX() - grabX, e.getY() - grabY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggedSprite = -1;
    }
}
