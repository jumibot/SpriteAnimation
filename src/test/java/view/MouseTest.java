package view;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import check.Check;
import model.Model;
import model.Sprite;
import presenter.Presenter;

/**
 * Prueba con el ratón de verdad: abre la ventana real y mueve el ratón con java.awt.Robot.
 * Gira la rueda sobre cada cifra del HUD, arrastra cada sprite y cierra la ventana.
 * Durante la prueba no toques el ratón. Con -Dcaptura=carpeta guarda una captura de la ventana.
 */
public class MouseTest {

    private static Robot robot;
    private static Model model;
    private static View view;
    private static Viewer viewer;
    private static boolean disturbed;   // alguien ha movido el ratón durante la prueba

    public static void main(String[] args) throws Exception {
        System.out.println("MouseTest (ventana real y Robot): no toques el ratón hasta que termine");
        // El mismo montaje que SpriteAnimation.main, guardando el modelo y la vista para comprobarlos.
        model = new Model();
        model.addSprite("Pato", "/sprites/pato.png", 16, 16, 4, 8, 120, 220);
        model.addSprite("Explosión", "/sprites/explosion.png", 48, 48, 3, 12, 360, 160);
        view = new View();
        Presenter presenter = new Presenter(model, view);
        presenter.start();

        if (System.getProperty("depurar") != null) printMouseEvents();
        robot = new Robot();
        robot.setAutoDelay(20);
        viewer = findViewer();
        bringToFront();
        robot.delay(500);
        capture("captura-inicio.png");

        wheelOverEachHudLine();
        dragEachSprite();
        capture("captura-final.png");
        stillResponds();
        closesInOrder();
        if (disturbed) {
            System.out.println("  AVISO: el ratón se ha movido durante la prueba; si algo ha fallado, repítela sin tocarlo.");
        }
        Check.finish("MouseTest");
    }

    // ---------------------------------------------------------------- rueda

    static void wheelOverEachHudLine() {
        int lines = 1 + model.spriteCount();
        for (int line = 0; line < lines; line++) {
            int x = View.HUD_X + 60;
            int y = View.HUD_Y + line * View.HUD_LINE - View.HUD_LINE / 2;   // en medio de la línea
            // Hasta tres intentos: si alguien mueve el ratón, la rueda puede caer en otra línea.
            for (int attempt = 1; attempt <= 3; attempt++) {
                boolean disturbedBefore = disturbed;
                disturbed = false;
                int before = speed(line);
                moveTo(x, y);
                robot.mouseWheel(-3);   // tres muescas hacia arriba: sube 3
                robot.delay(300);
                checkPointerAt(x, y);
                int up = speed(line);
                moveTo(x, y);
                robot.mouseWheel(+1);   // una hacia abajo: baja 1
                robot.delay(300);
                checkPointerAt(x, y);
                int down = speed(line);
                boolean retry = disturbed && attempt < 3;
                disturbed |= disturbedBefore;
                if (retry) continue;
                Check.that("rueda sobre la línea " + line + " del HUD: " + before + " → " + up + " → " + down,
                           up == before + 3 && down == before + 2);
                break;
            }
        }
        // Fuera del HUD la rueda no hace nada.
        int render = view.renderFps();
        moveTo(View.CANVAS_WIDTH - 40, View.CANVAS_HEIGHT - 40);
        robot.mouseWheel(-3);
        robot.delay(300);
        Check.equal("la rueda fuera del HUD no cambia nada", render, view.renderFps());
    }

    /** Línea 0: velocidad de pintado; 1, 2…: velocidad de cada sprite. */
    static int speed(int line) {
        if (line == 0) {
            return view.renderFps();
        }
        return model.sprite(line - 1).fps();
    }

    // ---------------------------------------------------------------- arrastre

    static void dragEachSprite() {
        drag(0, 60, 40);     // el pato, a la derecha y abajo
        drag(1, -80, 30);    // la explosión, a la izquierda y abajo
    }

    static void drag(int index, int dx, int dy) {
        Sprite s = model.sprite(index);
        // Hasta tres intentos: si alguien mueve el ratón, el arrastre no acaba donde debe.
        for (int attempt = 1; attempt <= 3; attempt++) {
            boolean disturbedBefore = disturbed;
            disturbed = false;
            int x0 = s.x();
            int y0 = s.y();
            int grabX = x0 + s.width() / 2;
            int grabY = y0 + s.height() / 2;
            moveTo(grabX, grabY);
            robot.delay(100);
            checkPointerAt(grabX, grabY);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            int steps = 10;
            for (int i = 1; i <= steps; i++) {
                moveTo(grabX + dx * i / steps, grabY + dy * i / steps);
            }
            // Con el sprite cogido, su animación sigue: el fotograma cambia.
            boolean animates = changesFrame(s, 400);
            checkPointerAt(grabX + dx, grabY + dy);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(200);
            boolean retry = disturbed && attempt < 3;
            disturbed |= disturbedBefore;
            if (retry) continue;
            Check.that("arrastre de " + s.name() + ": (" + x0 + ", " + y0 + ") → (" + s.x() + ", " + s.y() + ")",
                       s.x() == x0 + dx && s.y() == y0 + dy);
            Check.that(s.name() + " sigue animándose mientras se arrastra", animates);
            break;
        }
    }

    // ---------------------------------------------------------------- sigue respondiendo

    static void stillResponds() throws Exception {
        long start = System.nanoTime();
        SwingUtilities.invokeAndWait(() -> {});   // una tarea vacía: vuelve en cuanto el hilo de eventos la hace
        long ms = (System.nanoTime() - start) / 1_000_000;
        Check.that("el hilo de eventos de Swing responde (" + ms + " ms)", ms < 1000);
        Check.that("el hilo de pintado sigue vivo", alive("Render"));
        Check.that("los hilos de los sprites siguen vivos", alive("Sprite Pato") && alive("Sprite Explosión"));
        Check.that("la explosión sigue avanzando fotogramas", changesFrame(model.sprite(1), 300));
    }

    // ---------------------------------------------------------------- cierre

    static void closesInOrder() throws Exception {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(viewer);
        SwingUtilities.invokeAndWait(() ->
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));   // como pulsar la X
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline
               && (alive("Render") || alive("Sprite Pato") || alive("Sprite Explosión") || frame.isDisplayable())) {
            Thread.sleep(50);
        }
        Check.that("al cerrar, terminan el hilo de pintado y los de los sprites",
                   !alive("Render") && !alive("Sprite Pato") && !alive("Sprite Explosión"));
        Check.that("y la ventana se libera", !frame.isDisplayable());
    }

    // ---------------------------------------------------------------- utilidades

    private static Viewer findViewer() throws Exception {
        for (int i = 0; i < 50; i++) {
            for (Frame f : Frame.getFrames()) {
                if (!f.isShowing() || !(f instanceof JFrame)) {
                    continue;
                }
                JFrame window = (JFrame) f;
                if (window.getContentPane().getComponentCount() > 0
                        && window.getContentPane().getComponent(0) instanceof Viewer) {
                    return (Viewer) window.getContentPane().getComponent(0);
                }
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("No aparece la ventana de la demo");
    }

    private static void bringToFront() throws Exception {
        // Siempre encima: así el Robot no hace clic en otra ventana que la tape.
        SwingUtilities.invokeAndWait(() -> {
            java.awt.Window window = SwingUtilities.getWindowAncestor(viewer);
            window.setAlwaysOnTop(true);
            window.toFront();
        });
        robot.delay(300);
        moveTo(View.CANVAS_WIDTH - 20, View.CANVAS_HEIGHT - 20);   // un clic en una zona vacía, para darle el foco
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /** Mueve el ratón a un punto del lienzo (coordenadas del Viewer). */
    private static void moveTo(int x, int y) {
        Point origin = viewer.getLocationOnScreen();
        robot.mouseMove(origin.x + x, origin.y + y);
    }

    /** Mira el fotograma cada 20 ms durante un rato. (Mirar solo al final engaña: con 4 fotogramas,
     *  tras avanzar 4 vuelve a estar en el mismo.) */
    private static boolean changesFrame(Sprite s, int ms) {
        int first = s.frameIndex();
        for (int waited = 0; waited < ms; waited += 20) {
            robot.delay(20);
            if (s.frameIndex() != first) return true;
        }
        return false;
    }

    /** Comprueba que el puntero sigue donde lo dejó el Robot; si no, alguien ha tocado el ratón. */
    private static void checkPointerAt(int x, int y) {
        Point now = pointer();
        if (now.x != x || now.y != y) disturbed = true;
    }

    /** Dónde está el puntero ahora, en coordenadas del lienzo. */
    private static Point pointer() {
        Point origin = viewer.getLocationOnScreen();
        Point now = java.awt.MouseInfo.getPointerInfo().getLocation();
        return new Point(now.x - origin.x, now.y - origin.y);
    }

    /** Con -Ddepurar escribe cada evento del ratón: sirve para ver si alguien lo ha movido. */
    private static void printMouseEvents() {
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() != java.awt.event.MouseEvent.MOUSE_MOVED) System.out.println("    evento: " + e.paramString());
        }, java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK
           | java.awt.AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    private static boolean alive(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName) && t.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private static void capture(String file) throws Exception {
        String dir = System.getProperty("captura");
        if (dir == null) return;
        Point origin = viewer.getLocationOnScreen();
        BufferedImage image = robot.createScreenCapture(
                new Rectangle(origin.x, origin.y, viewer.getWidth(), viewer.getHeight()));
        File out = new File(dir, file);
        out.getParentFile().mkdirs();
        ImageIO.write(image, "png", out);
        System.out.println("  captura: " + out.getAbsolutePath());
    }
}
