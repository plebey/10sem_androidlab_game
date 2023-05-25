package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;

public class GameScreen implements Screen {

	final Ship game;
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture shipImage, backgroundImage, boom64Image, aster1Image, aster2Image;
	Sound bomb_sound;
	Music gameplay_music;

	Rectangle ship;
	Vector3 touchPos;
	Array<Rectangle> asters1;
	long lastDropTime;
	int screen_width;
	int screen_hight;
	private static final int SIZE_COEFF = 3;
	public Animation boomAnimation;
	Texture boomAnimationTexture;
	private float state_time, timer;
	private static final int BOOM_FRAME_COLS = 8, BOOM_FRAME_ROWS = 6;


	public GameScreen (final Ship gam) {
		this.game = gam;

		camera = new OrthographicCamera();
		screen_hight = Gdx.graphics.getHeight();
		screen_width = Gdx.graphics.getWidth();

		camera.setToOrtho(false,screen_width,screen_hight);
		batch = new SpriteBatch();
		shipImage = new Texture("main_ship.png");
		backgroundImage = new Texture("background.jpg");

		boomAnimationTexture = new Texture("boom_sprite.png");
		TextureRegion[][] tmp = TextureRegion.split(boomAnimationTexture,
				boomAnimationTexture.getWidth() / BOOM_FRAME_COLS,
				boomAnimationTexture.getHeight() / BOOM_FRAME_ROWS);
		TextureRegion[] boomAnimFrames = new TextureRegion[BOOM_FRAME_COLS*BOOM_FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < BOOM_FRAME_ROWS; i++){
			for (int j = 0; j< BOOM_FRAME_COLS; j++){
				boomAnimFrames[index++]=tmp[i][j];
			}
		}
		boomAnimation = new Animation(0.04f, boomAnimFrames);
		state_time = 0.0f;

		//boom64Image = new Texture("boom64.png");
		aster1Image = new Texture("aster1.png");
		aster2Image = new Texture("aster2.png");
		touchPos = new Vector3();
		bomb_sound = Gdx.audio.newSound(Gdx.files.internal("bomb-explosion.wav"));
		//menu_music = Gdx.audio.newMusic(Gdx.files.internal("menu_music.wav"));
		game.music_game = Gdx.audio.newMusic(Gdx.files.internal("gameplay_music.wav"));
		game.music_game.setLooping(true);
		game.music_game.play();

		ship = new Rectangle();
		ship.x = screen_width/2 - ship.width/2;
		ship.y = 300;
		ship.width = 64*SIZE_COEFF;
		ship.height = 63*SIZE_COEFF;

		asters1 = new Array<Rectangle>();

		spawnMeteor();

	}

	private	void spawnMeteor(){
		Rectangle meteor = new Rectangle();
		float i = MathUtils.random(1F,1.5F);
		meteor.width = 128*i;
		meteor.height = 96*i;
		meteor.x = MathUtils.random(0, screen_width-meteor.width);
		meteor.y = screen_hight + meteor.height;

		asters1.add(meteor);
		lastDropTime = TimeUtils.nanoTime();
	}

	TextureRegion BoomCurrentFrame;
	boolean play = true;
	int speed = 600;
	int spawn_speed = 1000000000;
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		state_time+=Gdx.graphics.getDeltaTime();
		if (play)
			timer = state_time;
		if (!play) {
			if (state_time - timer > 0.8f) {
				if (game.record<timer) game.record = (double) Math.round(timer * 100) / 100;
				game.setScreen(new MainMenuScreen(game));
				dispose();
			}
		}
		BoomCurrentFrame = (TextureRegion) boomAnimation.getKeyFrame(state_time/1.1F,true);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		game.batch.draw(backgroundImage, 0,0,(int)(screen_width*1.5),screen_hight);

		if (play)
			game.batch.draw(shipImage, ship.x, ship.y,ship.width,ship.height);
		else
			game.batch.draw(BoomCurrentFrame, ship.x, ship.y,ship.width,ship.height);

		for (Rectangle meteor: asters1){
			game.batch.draw(aster1Image, meteor.x, meteor.y, meteor.width, meteor.height);
		}

		game.text.draw(game.batch, "Game time: "+timer, 25, screen_hight);
		game.batch.end();

		if (play)
			if (Gdx.input.isTouched()){
				touchPos.set(Gdx.input.getX(),Gdx.input.getY(), 00);
				camera.unproject(touchPos);
				ship.x = (int) (touchPos.x - ship.width/2);

			}
		if (ship.x <0) ship.x = 0;
		if (ship.x > screen_width-ship.width) ship.x = screen_width-ship.width;

		if (play) {
			if (TimeUtils.nanoTime() - lastDropTime > spawn_speed) {
				spawnMeteor();
				spawn_speed-=100;
			}
		}
		Iterator<Rectangle> iter = asters1.iterator();

		while (iter.hasNext()){

				Rectangle meteor = iter.next();
				meteor.y -= speed * Gdx.graphics.getDeltaTime();
				speed+=1;
				if (meteor.y < 0 - meteor.height) iter.remove();
				if (meteor.overlaps(ship)) {
					if (play) {
						iter.remove();
						bomb_sound.play();
						game.music_game.stop();
						play = false;
					}
				}
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
	public void dispose () {
		shipImage.dispose();
		aster1Image.dispose();
		game.music_game.dispose();
		backgroundImage.dispose();
		aster2Image.dispose();
		bomb_sound.dispose();
		batch.dispose();

	}

	@Override
	public void show(){

	}

}
