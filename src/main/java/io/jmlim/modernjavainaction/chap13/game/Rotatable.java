package io.jmlim.modernjavainaction.chap13.game;

public interface Rotatable {
    void setRotationAngle(int angleInDegreses);

    int getRotationAngle();

    default void rotateBy(int angleInDegrees) {
        setRotationAngle((getRotationAngle() + angleInDegrees) % 360);
    }
}
