package contract;

import java.awt.image.BufferedImage;

/**
 * Una foto fija de un sprite: lo que la vista necesita para pintarlo. Nadie la cambia.
 * No es un contrato: son los datos que el presentador le da a la vista con sprites().
 */
public record SpriteState(String name, BufferedImage frame, int x, int y, int fps) {}
