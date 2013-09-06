package org.jrenner;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.*;

import java.util.*;
import java.util.List;

public class Master implements ApplicationListener {
	public static boolean debug = false;
	public static Stage stage;
	private static Skin skin;
	public static Random rand = new Random();
	public static Label statusLabel;
	private static List<String> statusLines = new LinkedList<>();

	public void create () {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		initSkin();
		initStage();
		Gdx.input.setInputProcessor(stage);
	}

	public void render () {
		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		stage.act(dt);
		stage.draw();
		if (debug) {
			Table.drawDebug(stage);
		}
		processTextLineAdditions();
	}


	public void resize (int width, int height) {
	}

	public void pause () {
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			Gdx.app.exit(); // ~~I don't want to light the world on fire
		}
	}

	public void resume () {
	}

	public void dispose () {
	}

	public static void print(Object ...objs) {
		for (Object obj : objs) {
			System.out.print(obj);
		}
		System.out.println();
	}

	private static void initSkin() {
		skin = new Skin(Gdx.files.internal("ui/ui.json"));
		ScrollPane.ScrollPaneStyle scrollPaneStyle = skin.get(ScrollPane.ScrollPaneStyle.class);
		SpriteDrawable bg = new SpriteDrawable();
		Pixmap pixmap = new Pixmap(128, 128, Pixmap.Format.RGB888);
		pixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
		pixmap.fill();
		Texture tex = new Texture(pixmap);
		Sprite sprite = new Sprite(tex);
		bg.setSprite(sprite);
		scrollPaneStyle.background = bg;

		if (Gdx.graphics.getWidth() < 1300) {
			//BitmapFont smallFont = new BitmapFont(Gdx.files.internal("ui/source20.fnt"), false);
			BitmapFont smallFont = new BitmapFont(Gdx.files.internal("ui/ubuntu-mono16.fnt"), false);

			Label.LabelStyle labelStyle = skin.get(Label.LabelStyle.class);
			labelStyle.font = smallFont;

			TextButton.TextButtonStyle tStyle = skin.get(TextButton.TextButtonStyle.class);
			tStyle.font = smallFont;

			SelectBox.SelectBoxStyle sStyle = skin.get(SelectBox.SelectBoxStyle.class);
			sStyle.listStyle.font = smallFont;
		}
	}

	private static void initStage() {
		stage = new Stage();
		final float WIDTH = Gdx.graphics.getWidth();
		final float HEIGHT = Gdx.graphics.getHeight();
		final float btnWidth = WIDTH / 6;
		final float btnHeight = HEIGHT / 10;

		Table table = new Table(skin);
		table.size(WIDTH, HEIGHT / 4);
		table.setPosition(0, HEIGHT - (table.getHeight() + 20));
		table.align(Align.center | Align.top);
		stage.addActor(table);

		TextButton benchBtn = new TextButton("Benchmark", skin);
		table.add(benchBtn).size(btnWidth, btnHeight).align(Align.center);
		ChangeListener runTests = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				MapTests.runAllTests();
			}
		};
		benchBtn.addListener(runTests);

		Integer[] choices = {1000, 5000, 10000, 20000, 50000, 100000};

		Label loopLabel = new Label("Iterations: ", skin);
		table.add(loopLabel).size(btnWidth, btnHeight);

		SelectBox loopSelector = new SelectBox(choices, skin);
		ChangeListener select = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				final SelectBox parent = (SelectBox) actor;
				int choice = Integer.parseInt(parent.getSelection());
				MapTests.loopCount = choice;
				addTextLine("set number of iterations to: " + MapTests.loopCount);
			}
		};
		loopSelector.addListener(select);
		table.add(loopSelector).size(btnWidth * 2, btnHeight);

		statusLabel = new Label("waiting for user...", skin);
		statusLabel.setWrap(true);

		Table tableTwo = new Table(skin);
		tableTwo.size(WIDTH, HEIGHT - table.getHeight());
		tableTwo.align(Align.left | Align.bottom);
		int padAmount = 20;
		tableTwo.pad(padAmount);
		stage.addActor(tableTwo);
		ScrollPane scrollPane = new ScrollPane(statusLabel, skin);
		tableTwo.add(scrollPane).align(Align.bottom | Align.left).width(stage.getWidth() - padAmount * 2);
		tableTwo.row();
		TextButton exitBtn = new TextButton("Exit", skin);
		tableTwo.add(exitBtn).size(btnWidth, btnHeight).align(Align.center);
		ChangeListener exit = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				quit();
			}
		};
		exitBtn.addListener(exit);

		if (debug) {
			table.debug();
			tableTwo.debug();
		}

		table.invalidateHierarchy();
		tableTwo.invalidateHierarchy();

	}

	private static List<String> statusLineAdditions = new LinkedList<>();
	public static synchronized void addTextLine(String line) {
		statusLineAdditions.add(line);
	}

	public static synchronized void processTextLineAdditions() {
		// wait for the render thread to finish a loop
		//int maxLines = 20;
		/*while (statusLines.size() > 0 && statusLines.size() > maxLines - statusLineAdditions.size()) {
			statusLines.remove(statusLines.get(statusLines.size() - 1));
		}*/
		for (String s : statusLineAdditions) {
			statusLines.add(0, s);
		}
		statusLineAdditions.clear();
		StringBuilder sb = new StringBuilder();
		for (int i = statusLines.size() - 1; i >= 0; i--) {
			String s = statusLines.get(i);
			sb.append(s + "\n");
		}
		statusLabel.setText(sb.toString());
	}

	public static void quit() {
		print("quit");
		MapTests.testRunnerThread.interrupt();
		Gdx.app.exit();
	}
}

