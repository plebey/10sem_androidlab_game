package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuScreen implements Screen {

    final Ship game;
    OrthographicCamera camera;
    int screen_width;
    int screen_hight;

    public MainMenuScreen(Ship gam) {
        this.game = gam;

        screen_hight = Gdx.graphics.getHeight();
        screen_width = Gdx.graphics.getWidth();

        camera = new OrthographicCamera();
        camera.setToOrtho(false,screen_width,screen_hight);

        game.music_menu = Gdx.audio.newMusic(Gdx.files.internal("menu_music.wav"));
        game.music_menu.setLooping(true);
        game.music_menu.play();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.text.draw(game.batch, "Survive!..",screen_width/2-80, screen_hight/2+250);
        game.text.draw(game.batch, "Tap to begin!",screen_width/2-115, screen_hight/2+180);
        game.text.draw(game.batch, "Last record: "+game.record+"s",screen_width/2-150, screen_hight/2);
        game.batch.end();

        if (Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }

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

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.music_menu.dispose();

    }
}
