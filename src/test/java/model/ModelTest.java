package model;

import java.awt.image.BufferedImage;

import check.Check;

/** Comprueba que el modelo no deja salir sus objetos (el fotograma que da es una copia) y que la escala no mueve el centro. */
public class ModelTest {

    public static void main(String[] args) {
        System.out.println("ModelTest");

        Model model = new Model();
        model.addSprite("Pato", "/sprites/pato.png", 16, 16, 4, 8, 120, 220);   // no se arranca: el fotograma no cambia

        BufferedImage frame = model.frame(0);
        Check.that("cada vez da una imagen nueva", model.frame(0) != frame);

        int pixel = frame.getRGB(30, 30);
        frame.setRGB(30, 30, ~pixel);   // pintamos encima de la imagen recibida
        Check.equal("pintar en la copia no cambia el fotograma del modelo", pixel, model.frame(0).getRGB(30, 30));

        Check.equal("el pato sale ampliado a escala 4: 64 px (16 × 4)", 64, model.frame(0).getWidth());
        Check.equal("con escala 4", 4, model.scale(0));
        model.setScale(0, 6);
        Check.equal("a escala 6 mide 96 px (16 × 6)", 96, model.frame(0).getWidth());
        Check.equal("y 96 px de alto", 96, model.frame(0).getHeight());
        // Antes iba de 120 a 184 (centro en 152); ahora mide 32 px más: la esquina se aleja 16.
        Check.that("crece desde el centro: la esquina pasa a (104, 204)", model.x(0) == 104 && model.y(0) == 204);
        model.setScale(0, 4);
        Check.that("y al volver a 4 vuelve a (120, 220)", model.x(0) == 120 && model.y(0) == 220);

        Check.finish("ModelTest");
    }
}
