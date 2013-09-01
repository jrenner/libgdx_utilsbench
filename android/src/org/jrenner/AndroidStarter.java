package org.jrenner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class AndroidStarter extends AndroidApplication {
	public void onCreate (android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new Master(), true);
	}
}