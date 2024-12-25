package com.github.bucketdrop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BucketDrop extends Game {
    public AssetManager assetManager = new AssetManager();
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    public GameScreen gameScreen;

    // Called when the Application is first created.
    public void create() {
        batch = new SpriteBatch();

        assetManager.load("MinecraftiaFont.fnt", BitmapFont.class);
        while (!assetManager.isFinished()) {
            assetManager.update();
        }

        viewport = new FitViewport(Gdx.graphics.getWidth() / 100f, Gdx.graphics.getHeight() / 100f);

        if (assetManager.isLoaded("MinecraftiaFont.fnt")) {
            font = assetManager.get("MinecraftiaFont.fnt", BitmapFont.class); // font loaded, now set it

            //font has 24pt, but we need to scale it to our viewport by ratio of viewport height to screen height
            font.setUseIntegerPositions(false);
            font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
        }

        this.setScreen(new MainMenuScreen(this));
    }

    // Called when the Application should render itself.
    public void render() {
        super.render(); // important!

    }

    public void dispose() {
        batch.dispose();
        //font.dispose();
        assetManager.unload("MinecraftiaFont.fnt");
        gameScreen.dispose();
        System.out.println("BucketDrop disposed!"); // replace with logger
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }
}
