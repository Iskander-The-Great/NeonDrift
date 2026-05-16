package com.neondrift.game;

public class Drone {

    float x;
    float y;
    float width;
    float height;

    float speed = 280f;
    boolean movingUp = true;

    public Drone(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(float deltaTime) {
        if (movingUp) {
            y = y + speed * deltaTime;
        } else {
            y = y - speed * deltaTime;
        }
    }

    public void changeDirection() {
        movingUp = !movingUp;
    }

    public float getHitboxX() {
        return x + width * 0.20f;
    }

    public float getHitboxY() {
        return y + height * 0.20f;
    }

    public float getHitboxWidth() {
        return width * 0.60f;
    }

    public float getHitboxHeight() {
        return height * 0.60f;
    }
}
