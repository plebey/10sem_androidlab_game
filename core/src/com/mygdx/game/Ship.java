package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Ship extends Game {
    SpriteBatch batch;
    BitmapFont text;
    double record = 0;
    Music music_game, music_menu;
    @Override
    public void create() {
        batch = new SpriteBatch();
        text = new BitmapFont();
        text.getData().setScale(3);
        this.setScreen(new MainMenuScreen(this));

    }
    @Override
    public void render(){
        super.render();
    }
    @Override
    public void dispose(){
        super.dispose();
        batch.dispose();
        text.dispose();

    }
}
