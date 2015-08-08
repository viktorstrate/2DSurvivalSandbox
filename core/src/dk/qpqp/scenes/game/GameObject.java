package dk.qpqp.scenes.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import dk.qpqp.utills.Constants;

/**
 * Created by viktorstrate on 04/08/2015.
 * Abstract class for game object, like trees and stones
 */
public abstract class GameObject implements Graphic {

    protected int width, height;
    protected Body body;
    protected GameScene gameScene;
    protected Vector2 position;

    public GameObject(int x, int y, int width, int height, GameScene gameScene, int collisionWidth, int collisionHeight, int collisionX, int collisionY) {
        this(width, height, gameScene);
        this.position = new Vector2(x, y);
        setupBody(collisionWidth, collisionHeight, collisionX, collisionY);
    }

    public GameObject(int x, int y, int width, int height, GameScene gameScene, int collisionWidth, int collisionHeight) {
        this(width, height, gameScene);
        this.position = new Vector2(x, y);
        setupBody(collisionWidth, collisionHeight, 0, 0);
    }

    public GameObject(int x, int y, int width, int height, GameScene gameScene) {
        this(width, height, gameScene);
        this.position = new Vector2(x, y);
        setupBody(width, height, 0, 0);
    }

    public GameObject(int width, int height, GameScene gameScene) {
        this.width = width;
        this.height = height;
        this.gameScene = gameScene;
    }

    protected void setupBody(int width, int height, int x, int y) {
        // Setup body
        BodyDef bdef = new BodyDef();
        bdef.position.set(
                ((position.x * Constants.TILE_SIZE + width / 2) + x) / Constants.PPM,
                ((position.y * Constants.TILE_SIZE + height / 2) + y) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2 - width * 0.05f) / Constants.PPM, (height / 2 - width * 0.05f) / Constants.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        body = gameScene.getWorld().createBody(bdef);
        body.createFixture(fdef);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.begin();
        spriteBatch.draw(getTexture(), getPosition().x * Constants.TILE_SIZE, getPosition().y * Constants.TILE_SIZE);
        spriteBatch.end();
    }

    @Override
    public void update(float dt) {

    }

    public boolean mouseOver() {

        Vector2 mouseWorldPos = gameScene.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        return mouseWorldPos.x > getPosition().x * Constants.TILE_SIZE && mouseWorldPos.x < getPosition().x * Constants.TILE_SIZE + this.width &&
                mouseWorldPos.y > getPosition().y * Constants.TILE_SIZE && mouseWorldPos.y < getPosition().y * Constants.TILE_SIZE + this.height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        if (body == null) setupBody(width, height, 0, 0);
    }

    public abstract Texture getTexture();

    public void dispose() {
        gameScene.removeBody(body);
    }
}
