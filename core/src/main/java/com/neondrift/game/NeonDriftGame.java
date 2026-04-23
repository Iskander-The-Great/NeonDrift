package com.neondrift.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class NeonDriftGame extends ApplicationAdapter {

    SpriteBatch batch;
    BitmapFont font;
    GlyphLayout layout;

    Texture backgroundTexture;
    Texture droneTexture;
    Texture obstacleTopTexture;
    Texture obstacleBottomTexture;

    Drone drone;
    Obstacle obstacle1;
    Obstacle obstacle2;

    float jumpForce = 380f;
    float gravity = -800f;
    float obstacleSpeed = 240f;

    int score = 0;
    boolean gameOver = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        backgroundTexture = new Texture("background.png");
        droneTexture = new Texture("drone.png");
        obstacleTopTexture = new Texture("obstacle_top.png");
        obstacleBottomTexture = new Texture("obstacle_bottom.png");

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        drone = new Drone(screenWidth * 0.16f, screenHeight * 0.50f, 95f, 52f);

        obstacle1 = new Obstacle(screenWidth + 220f, 180f, 220f);
        obstacle2 = new Obstacle(screenWidth + 420f, 220f, 220f);

        resetObstacle(obstacle1, screenWidth + 220f, screenHeight);
        resetObstacle(obstacle2, obstacle1.x + 170f + (float) Math.random() * 90f, screenHeight);
    }

    @Override
    public void render() {
        update();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        ScreenUtils.clear(0.05f, 0.05f, 0.15f, 1f);

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        drawDrone();
        drawObstacle(obstacle1);
        drawObstacle(obstacle2);
        drawUi(screenWidth, screenHeight);

        batch.end();
    }

    void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                restart(screenWidth, screenHeight);
            }
            return;
        }

        handleInput();
        applyGravity(deltaTime);

        drone.update(deltaTime);

        obstacle1.update(deltaTime, obstacleSpeed);
        obstacle2.update(deltaTime, obstacleSpeed);

        checkBorders(screenHeight);
        checkCollision();
        checkScore();

        if (obstacle1.isOffScreen()) {
            float nextX = obstacle2.x + 170f + (float) Math.random() * 90f;
            resetObstacle(obstacle1, nextX, screenHeight);
        }

        if (obstacle2.isOffScreen()) {
            float nextX = obstacle1.x + 170f + (float) Math.random() * 90f;
            resetObstacle(obstacle2, nextX, screenHeight);
        }
    }

    void handleInput() {
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            drone.velocityY = jumpForce;
        }
    }

    void applyGravity(float deltaTime) {
        drone.velocityY = drone.velocityY + gravity * deltaTime;
    }

    void checkBorders(float screenHeight) {
        if (drone.y < 0) {
            drone.y = 0;
            gameOver = true;
        }

        if (drone.y + drone.height > screenHeight) {
            drone.y = screenHeight - drone.height;
            drone.velocityY = 0;
        }
    }

    void checkCollision() {
        if (obstacle1.collides(drone) || obstacle2.collides(drone)) {
            gameOver = true;
        }
    }

    void checkScore() {
        if (obstacle1.passed(drone.x)) {
            score = score + 1;
        }

        if (obstacle2.passed(drone.x)) {
            score = score + 1;
        }
    }

    void resetObstacle(Obstacle obstacle, float startX, float screenHeight) {
        obstacle.x = startX;
        obstacle.gapHeight = 220f;

        float minGapY = 110f;
        float maxGapY = screenHeight - obstacle.gapHeight - 110f;

        obstacle.gapY = minGapY + (float) Math.random() * (maxGapY - minGapY);
        obstacle.passed = false;
    }

    void restart(float screenWidth, float screenHeight) {
        drone.x = screenWidth * 0.16f;
        drone.y = screenHeight * 0.50f;
        drone.velocityY = 0f;

        resetObstacle(obstacle1, screenWidth + 220f, screenHeight);
        resetObstacle(obstacle2, obstacle1.x + 170f + (float) Math.random() * 90f, screenHeight);

        score = 0;
        gameOver = false;
    }

    void drawDrone() {
        float rotation = drone.velocityY * 0.03f;

        if (rotation > 18f) {
            rotation = 18f;
        }
        if (rotation < -28f) {
            rotation = -28f;
        }

        batch.draw(
                droneTexture,
                drone.x,
                drone.y,
                drone.width * 0.5f,
                drone.height * 0.5f,
                drone.width,
                drone.height,
                1f,
                1f,
                rotation,
                0,
                0,
                droneTexture.getWidth(),
                droneTexture.getHeight(),
                true,
                false
        );
    }

    void drawObstacle(Obstacle obstacle) {
        float pipeWidth = obstacle.width;
        float topPipeHeight = obstacleTopTexture.getHeight() * 0.44f;
        float bottomPipeHeight = obstacleBottomTexture.getHeight() * 0.54f;

        float topY = obstacle.gapY + obstacle.gapHeight;
        float bottomY = obstacle.gapY - bottomPipeHeight;

        batch.draw(
                obstacleTopTexture,
                obstacle.x,
                topY + topPipeHeight,
                pipeWidth,
                -topPipeHeight
        );

        batch.draw(
                obstacleBottomTexture,
                obstacle.x,
                bottomY,
                pipeWidth,
                bottomPipeHeight
        );
    }

    void drawUi(float screenWidth, float screenHeight) {
        font.getData().setScale(2f);
        font.draw(batch, "Score: " + score, 18f, screenHeight - 18f);

        if (!gameOver && score == 0) {
            font.getData().setScale(1.6f);
            layout.setText(font, "Click or SPACE to fly");
            font.draw(batch, layout, (screenWidth - layout.width) / 2f, screenHeight - 70f);
        }

        if (gameOver) {
            font.getData().setScale(3f);
            layout.setText(font, "GAME OVER");
            font.draw(batch, layout, (screenWidth - layout.width) / 2f, screenHeight * 0.58f);

            font.getData().setScale(2f);
            layout.setText(font, "Press R to restart");
            font.draw(batch, layout, (screenWidth - layout.width) / 2f, screenHeight * 0.50f);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();
        droneTexture.dispose();
        obstacleTopTexture.dispose();
        obstacleBottomTexture.dispose();
    }
}
