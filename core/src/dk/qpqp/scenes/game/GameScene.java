package dk.qpqp.scenes.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dk.qpqp.Game;
import dk.qpqp.scenes.Scene;
import dk.qpqp.scenes.game.entity.Player;
import dk.qpqp.scenes.game.object.generators.ObjectSpawnHandler;
import dk.qpqp.scenes.game.ui.UIHandler;
import dk.qpqp.utills.Constants;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by viktorstrate on 03/08/2015.
 * The game scene
 */
public class GameScene extends Scene {

    private OrthographicCamera gameCamera;
    private Viewport viewport;
    private SpriteBatch spriteBatch;

    private ArrayList<GameObject> gameObjects;

    private Player player;
    private Terrain terrain;
    private ObjectSpawnHandler objectSpawnHandler;
    private UIHandler uiHandler;

    // Box 2D
    private World world;
    private Box2DDebugRenderer b2dr;
    private ArrayList<Body> bodiesToRemove;

    public GameScene() {

        gameCamera = new OrthographicCamera(Game.WIDTH, Game.HEIGHT);
        viewport = new FillViewport(Game.WIDTH, Game.HEIGHT, gameCamera);
        gameCamera.zoom = 1/Game.SCALE;
        gameCamera.update();
        spriteBatch = new SpriteBatch();

        // Box 2D
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        bodiesToRemove = new ArrayList<>();

        gameObjects = new ArrayList<>();
        player = new Player(64 * 32, 64 * 32, 32, 32, this);
        gameObjects.add(player);

        terrain = new Terrain(this);
        objectSpawnHandler = new ObjectSpawnHandler(this);

        uiHandler = new UIHandler(this);
    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(gameCamera.combined);

        terrain.render(spriteBatch);

        for(GameObject g: gameObjects){
            g.render(spriteBatch);
        }

        objectSpawnHandler.render(spriteBatch);

        uiHandler.render();
        viewport.apply();

        b2dr.render(world, gameCamera.combined.cpy().scale(Constants.PPM, Constants.PPM, 1));
    }

    @Override
    public void update(float dt) {
        world.step(dt, 6, 2);
        Iterator<Body> it = bodiesToRemove.iterator();
        while (it.hasNext()) {
            Body bod = it.next();
            bod.setActive(false);
            bod.setAwake(false);
            world.destroyBody(bod);
            it.remove();
        }


        for(GameObject g: gameObjects){
            g.update(dt);
        }

        terrain.update(dt);

        objectSpawnHandler.update(dt);

        // Camera
        gameCamera.position.lerp(new Vector3(player.getPosition().x + player.width / 2, player.getPosition().y + player.height / 2, 0), 4f * dt);
        gameCamera.update();
    }

    @Override
    public void resize(int width, int height){

//        float aspectRatio = (float) width / (float) height;
//        float aspectRatio2 = (float)height / (float)width;
//
//        // This is to maintain the same aspect ratio, using virtual screen-size
//        if(aspectRatio >= (float) Game.WIDTH / (float) Game.HEIGHT){
//            gameCamera.setToOrtho(false, Game.HEIGHT * aspectRatio, Game.HEIGHT);
//
//        } else {
//            gameCamera.setToOrtho(false, Game.WIDTH, Game.WIDTH * aspectRatio2);
//        }
//
//        gameCamera.position.set(player.getPosition().x + player.width / 2, player.getPosition().y + player.height / 2, 1);
//        gameCamera.update();

        viewport.update(width, height);

        uiHandler.resize(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        terrain.dispose();
    }

    public void removeBody(Body body) {
        bodiesToRemove.add(body);
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
