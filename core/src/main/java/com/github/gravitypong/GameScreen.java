package com.github.gravitypong;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final GravityPong game;

    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;
    Sprite bucketSprite;
    Vector2 touchPos;
    Array<Sprite> dropSprites;
    float dropTimer;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    public GameScreen(GravityPong game) {
        // Prepare your application here.
        this.game = game;

        this.game.assetManager.load("background.png", Texture.class);
        this.game.assetManager.load("bucket.png", Texture.class);
        this.game.assetManager.load("drop.png", Texture.class);

        this.game.assetManager.load("drop.mp3", Sound.class);
        this.game.assetManager.load("music.mp3", Music.class);


        touchPos = new Vector2();

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        game.setGameScreen(this); // allows GravityPong to dispose this screen when needed
    }


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        //music.play();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        if (game.assetManager.isFinished()) {
            input();
            logic();
            draw();
        } else if (game.assetManager.update()) {
            // loading finished
            setAssets();
        }

    }

    private void setAssets() {
        backgroundTexture = game.assetManager.get("background.png", Texture.class);
        bucketTexture = game.assetManager.get("bucket.png", Texture.class);
        dropTexture = game.assetManager.get("drop.png", Texture.class);

        dropSound = game.assetManager.get("drop.mp3", Sound.class);
        music = game.assetManager.get("music.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.01f);
        music.play();

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        //isEveryAssetSet = true;
    }

    private void input() {
        float speed = 5f; // moves 5 units (500 pixels) in one second
        float delta = Gdx.graphics.getDeltaTime();

        // if the user has pressed a key
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        } else if (Gdx.input.isTouched()) { // if user clicked/touched screen
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            game.viewport.unproject(touchPos); // Convert the units to the world units of the viewport

            if (touchPos.x > bucketSprite.getX()) { // if clicked to the right of the bucket
                bucketSprite.translateX(speed * delta);
            } else if (touchPos.x < bucketSprite.getX()) { // if clicked to the left of the bucket
                bucketSprite.translateX(-speed * delta);
            }
        }
    }

    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();
        float delta = Gdx.graphics.getDeltaTime();
        float dropletSpeed = 2;

        // Prevents bucket from going off-screen. Clamps the bucket's x position between 0 and the world width minus the bucket width.
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i); // Get the sprite from the list
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-dropletSpeed * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            // if the top of the drop goes below the bottom of the view, remove it
            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
            } else if (bucketRectangle.overlaps(dropRectangle)) { // if bucket overlaps droplet
                dropSprites.removeIndex(i); // remove droplet
                dropSound.play(); // play sound
            }
        }

        dropTimer += delta; // accumulates  time that passes between every frame (delta)
        if (dropTimer > 1) { // if a second has passed, create droplet
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK); // clears screen every frame otherwise graphical errors

        game.viewport.apply();
        // shows how the Viewport is applied to the SpriteBatch, necessary for the images to be shown in the correct place.
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.batch.draw(backgroundTexture, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight()); // draw the background
        bucketSprite.draw(game.batch); // Sprites have their own draw method

        /*  The iterator returned by iterator() of libGDX Array is always the same instance,
            allowing the Array to be used with the enhanced for-each (for( : )) syntax without creating garbage.
            Note however that this differs from most iterable collections!
            It cannot be used in nested loops, else it will cause hard to find bugs.
            Use the Array.ArrayIterator constructor for nested or multithreaded iteration.
            https://libgdx.com/wiki/utils/collections
            https://stackoverflow.com/questions/50237364/what-does-using-non-reentrant-iterator-method-array-iterator-error-message
         */
        for (Sprite dropSprite : new Array.ArrayIterator<>(dropSprites)) {
            dropSprite.draw(game.batch);
        }

        game.batch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // create drop sprite
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    /**
     * @param width the new width in pixels
     * @param height the new height in pixels
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        game.viewport.update(width, height, true); // true centers the camera
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        game.assetManager.unload("background.png");
        game.assetManager.unload("drop.mp3");
        game.assetManager.unload("music.mp3");
        game.assetManager.unload("drop.png");
        game.assetManager.unload("bucket.png");

        System.out.println("GameScreen disposed!"); // replace with logger
    }
}
