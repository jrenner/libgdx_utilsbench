package org.jrenner;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static final String version = "0.01";
	// run on Desktop
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.resizable = false;
		cfg.vSyncEnabled = true;
		cfg.width = 1024;
		cfg.height = 768;
		cfg.title = "MapBench v" + version;
		cfg.useGL20 = true;
		LwjglApplication app = new LwjglApplication(new Master(), cfg);
	}

}
