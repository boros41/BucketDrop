package com.github.bucketdrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen extends ScreenAdapter {
    final BucketDrop game;

    public MainMenuScreen (BucketDrop game) {
        this.game = game;

        this.game.font = this.game.assetManager.get("MinecraftiaFont.fnt");
        //font has 24pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        this.game.font.setUseIntegerPositions(false);
        this.game.font.getData().setScale(this.game.viewport.getWorldHeight() / Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        game.viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render(float delta) {
        System.out.println("render() START FONT LOADED: " + game.assetManager.isLoaded("MinecraftiaFont.fnt"));

        ScreenUtils.clear(Color.BLACK); // clears screen every frame otherwise graphical errors

        game.viewport.apply();
        // shows how the Viewport is applied to the SpriteBatch, necessary for the images to be shown in the correct place.
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        //draw text. Remember that x and y are in meters
        game.font.draw(game.batch, "Welcome to Drop!!! ", 4, 6f);
        game.font.draw(game.batch, "Tap anywhere to begin!", 4, 5);
        System.out.println("Drawing text!");

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

        System.out.println("render() END FONT LOADED: " + game.assetManager.isLoaded("MinecraftiaFont.fnt"));
    }

    @Override
    public void resume() {
        System.out.println("resume() method");
    }

}
