package com.github.gravitypong;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class GravityPong extends Game {
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



        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        //font = new BitmapFont();
        //font.setUseIntegerPositions(false);
        //font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    // Called when the Application should render itself.
    public void render() {
        super.render(); // important!

        /*
        if (assetManager.update()) {
            // we are done loading, let's move to another screen!

            font = assetManager.get("MinecraftiaFont.fnt", BitmapFont.class); // font loaded, now set it

            //font has 24pt, but we need to scale it to our viewport by ratio of viewport height to screen height
            font.setUseIntegerPositions(false);
            System.out.println("World Height: " + viewport.getWorldHeight());
            System.out.println("Height: " + Gdx.graphics.getHeight());
            font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

            if (!assetManager.isLoaded("background.png")) {
                this.setScreen(new MainMenuScreen(this)); // go to main menu screen
            }
        }

        // display loading information aka loading screen?
        float progress = assetManager.getProgress(); // returns a number between 0 and 1 indicating the percentage of assets loaded so far
         */
    }

    public void dispose() {
        batch.dispose();
        //font.dispose();
        assetManager.unload("MinecraftiaFont.fnt");
        gameScreen.dispose();
        System.out.println("GravityPong disposed!"); // replace with logger
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }
}
