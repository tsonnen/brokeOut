/* CS328 - Assignment 1
 * Tim Sonnen
 * Breakout clone 
 */

package com.cs328;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.cs328.Brick;

public class brokeOut implements ApplicationListener {
    /* Settings */
    public static int WIDTH, HEIGHT;
    OrthographicCamera cam;
    ShapeRenderer sr;
    Rectangle paddle;
    Rectangle ball;
    int paddleWidth     = 100;
    int paddleHeight    = 15;
    int ballSpeed, ballSpeedMod;
    int paddleSpeed     = 300;
    int score;
    int lives;
    int horBricks = 12;
    int vertBricks = 4;
    int ballYSpeed, ballXSpeed;
    Rectangle bottomBound, topBound;
    boolean ballUp, ballDown, ballLeft, ballRight;
    double ballDir;
    SpriteBatch batch;
    BitmapFont font;
    Array<Brick>blocks;
    Preferences prefs;

    @Override
    public void create () {
        prefs = Gdx.app.getPreferences("prefs");
        // Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        WIDTH   = Gdx.graphics.getWidth();
        HEIGHT  = Gdx.graphics.getHeight();

        /* Camera Settings */
        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.setToOrtho(false);
        
        /* Setup the graphics */
        sr      = new ShapeRenderer();
        batch   = new SpriteBatch();
        font    = new BitmapFont();

        /* Define paddle, ball, top and bottom bounds */
        paddle          = new Rectangle(WIDTH/4, 30, paddleWidth, paddleHeight);
        ball            = new Rectangle(WIDTH/2, HEIGHT/3, 5, 5);
        bottomBound     = new Rectangle(0,0, WIDTH, 3);
        topBound        = new Rectangle(0,HEIGHT, WIDTH, 3);

        score = 0;  
        lives = 3;

        ballSpeed       = 150;
        ballSpeedMod    = 100;
        ballDir         = Math.random();

        if(ballDir > .5)
            ballXSpeed = -getBallSpeed();
        else
            ballXSpeed = getBallSpeed();

        ballYSpeed = -getBallSpeed();

        resetBricks();
    }

    @Override
    public void render () {
        if(lives >= 0)
            running();
        else
            gameOver();
    }

    private void running(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        cam.update();

        /* Setup the paddle and the ball */
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.circle(ball.x,ball.y, 5);
        sr.rect(paddle.x, paddle.y, paddle.width, paddle.height);
        sr.end();

        /* Setup the text */
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, "Lives: " + lives, WIDTH - 100, HEIGHT - 10);
        font.draw(batch, "Score: " + score, 0, HEIGHT-10);
       
        /* Render bricks */
        for(Brick brick : blocks){
            brick.render(batch);
        }
        batch.end();

        if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
                if(paddle.x > 0)
                    paddle.x -= paddleSpeed * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            if(paddle.x < Gdx.graphics.getWidth() - paddle.getWidth())
                paddle.x += paddleSpeed * Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.R))
            resetBall();

        ball.x += ballXSpeed * Gdx.graphics.getDeltaTime();
        ball.y += ballYSpeed * Gdx.graphics.getDeltaTime();

        /* Collisions with the paddle should ALWAYS yield a positive Y speed */
        if(ball.overlaps(paddle))
            ballYSpeed = getBallSpeed();

        /* Adapted from - 
         * https://stackoverflow.com/questions/32992942/how-to-make-sprite-bounce-of-the-sides-of-the-screen 
         */
		if (ball.x > Gdx.graphics.getWidth() - ball.getWidth()/2)
            ballXSpeed = -getBallSpeed();
        else if(ball.x < 0 + ball.getWidth()/2)
            ballXSpeed = getBallSpeed();

        if (ball.y > Gdx.graphics.getHeight() - ball.getHeight()/2)
            ballYSpeed = -getBallSpeed();        

        if(ball.overlaps(bottomBound)){
            resetBall();
            lives -= 1;
        }

        for(Brick brick : blocks){
            if(ball.overlaps(brick.getBounds())){
                blocks.removeValue(brick, false);
                score += brick.points;
                ballYSpeed = -Integer.signum(ballYSpeed) * getBallSpeed();
            }
        }

        // Gdx.app.log("size", Integer.toString(blocks.size));
        if(blocks.size == 0)
            newLevel();
    }

    private void gameOver(){
        boolean isHigher = false;
        int oldHigh = prefs.getInteger("highScore", 0);
        if(score > oldHigh){
            isHigher = true;
            prefs.putInteger("highScore", score);
            prefs.flush();
        }
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, "HIGH SCORE: " + Integer.toString(prefs.getInteger("highScore", 0)), WIDTH/2, HEIGHT/2 + 15);
        font.draw(batch, "GAME OVER", WIDTH/2, HEIGHT/2);
        if(isHigher)
            font.draw(batch, "NEW HIGH SCORE", WIDTH/2, HEIGHT/2 - 15);
        batch.end();
        if(Gdx.input.isKeyPressed(Input.Keys.R)){
            score = 0;
            lives = 3;
            resetBricks();
            resetBall();
            vertBricks = 4;
            horBricks = 12;
        }
    }


    private void newLevel(){
        ballSpeed *= 1.25;
        ballSpeedMod *= 1.25;

        vertBricks += 1;
        horBricks += 1;

        resetBall();
        resetBricks();
    }

    private void resetBall(){
        ball.x      = WIDTH/2;
        ball.y      = HEIGHT/3;
        paddle.x    = WIDTH/4;
        
        if(ballDir > .5)
            ballXSpeed = getBallSpeed();
        else
            ballXSpeed = -getBallSpeed();

        ballYSpeed = -getBallSpeed();
    }

    private int getBallSpeed(){
        ballDir = Math.random();
        return (int)(ballDir * ballSpeed) + ballSpeedMod;
    }

    private void resetBricks(){
        blocks = new Array<Brick>();
        int y = HEIGHT - 50;
        float r = (float)Math.random() * .1f;
        float g = (float)Math.random() * .1f;
        float b = (float)Math.random() * .1f;
        int brickHeight = 20;
        int brickWidth = WIDTH/horBricks;
        for(int i = 0; i < vertBricks; i++){
            int x = 0;
            Gdx.app.log("red", Float.toString(r));
            Gdx.app.log("green", Float.toString(g));
            Gdx.app.log("blue", Float.toString(b));
            for(int j = 0; j < horBricks; j++){
                Brick brick = new Brick(x,y, brickWidth, brickHeight);
                brick.setTint(r, g, b);
                blocks.add(brick);
                x += brickWidth;
            }
            y -= brickHeight;
            r += (float)Math.random() * .1f;
            g += (float)Math.random() * .1f;
            b += (float)Math.random() * .1f;
        }
    }
    
    @Override
    public void dispose () {
        sr.dispose();
        batch.dispose();
    }

	@Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
