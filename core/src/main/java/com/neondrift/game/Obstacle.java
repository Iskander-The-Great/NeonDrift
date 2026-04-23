package com.neondrift.game;

public class Obstacle {

    float x;
    float gapY;
    float gapHeight;
    float width;

    boolean passed;

    public Obstacle(float x, float gapY, float gapHeight) {
        this.x = x;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
        this.width = 120f;
        this.passed = false;
    }

    public void update(float deltaTime, float speed) {
        x = x - speed * deltaTime;
    }

    public boolean collides(Drone drone) {
        float droneX = drone.getHitboxX();
        float droneY = drone.getHitboxY();
        float droneW = drone.getHitboxWidth();
        float droneH = drone.getHitboxHeight();

        boolean overlapX =
            droneX < x + width * 0.8f &&
                droneX + droneW > x + width * 0.2f;

        boolean hitBottom = droneY < gapY;
        boolean hitTop = droneY + droneH > gapY + gapHeight;

        return overlapX && (hitBottom || hitTop);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean passed(float droneX) {
        if (!passed && x + width < droneX) {
            passed = true;
            return true;
        }
        return false;
    }

    public void reset(float startX, float screenHeight) {
        x = startX + 80f + (float) Math.random() * 220f;

        gapHeight = 220f;;

        float minGapY = 120f;
        float maxGapY = screenHeight - gapHeight - 120f;

        gapY = minGapY + (float) Math.random() * (maxGapY - minGapY);

        passed = false;
    }
}
