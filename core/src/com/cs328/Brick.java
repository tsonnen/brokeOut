/* Handle operations for the bricks 
 * Adapted from- https://github.com/crazyhendrix/libGDX-BreakOut
 */

package com.cs328;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Color;

public class Brick{
    private Sprite spriteBrick;
    private Rectangle bounds;
    public int points = 50;

    public Brick(float x, float y, int width, int height){
        init(x,y, width, height);
    }

    private void init(float x, float y, int width, int height){
        Pixmap pixmap = new Pixmap(width,height,Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.setFilter(Filter.BiLinear);
        pixmap.fill();
        Texture brick = new Texture(pixmap);
        spriteBrick = new Sprite(brick);
        spriteBrick.setPosition(x,y);
        bounds = new Rectangle(spriteBrick.getX(), spriteBrick.getY(), spriteBrick.getWidth(), spriteBrick.getHeight());
    }

    public void setTint(float r, float g, float b) {
        spriteBrick.setColor(r, g, b, 1.0f);
    }

    public void render(SpriteBatch batch){
        spriteBrick.draw(batch);
    }

    public Rectangle getBounds(){
        return bounds;
    }
}
