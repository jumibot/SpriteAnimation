package model;

import java.awt.image.BufferedImage;

import check.Check;

/** Comprueba que el modelo no deja salir sus objetos: el fotograma que da es una copia. */
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

        Check.finish("ModelTest");
    }
}
