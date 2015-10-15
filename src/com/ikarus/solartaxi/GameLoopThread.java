package com.ikarus.solartaxi;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Thread for frame independent update and draw calls
 * 
 * @author Sebastian Kirchner<br>
 * Reference: http://obviam.net/index.php/the-android-game-loop/
 */
public class GameLoopThread extends Thread {
	
	private final static int MAX_FPS = 50;
	private final static int MAX_FPS_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	private SurfaceHolder surfaceHolder;
	private GameView view;
	private GameEngine engine;
	private boolean finished; // if this is set the loop terminates
	
	
	public GameLoopThread(SurfaceHolder surfaceHolder, GameView view, GameEngine engine) {
		this.surfaceHolder = surfaceHolder;
		this.view = view;
		this.engine = engine;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	 * Frame independent run method
	 */
	@Override
	public void run() {
		Canvas canvas;
		long beginTime;
		long timeDiff;
		int sleepTime;
		int skippedFrames;
		
		while (!finished) {
			
			canvas = null; //redraw
			
			try {				
				canvas = this.surfaceHolder.lockCanvas(null);
				synchronized (this.surfaceHolder) {
					
					beginTime = System.currentTimeMillis();
					skippedFrames = 0;
					
					// update game state
					if (!view.isPaused() && !view.inHelpMode()) {
						engine.update();
					}
					
					// draw on canvas
					if (canvas != null) {
						engine.draw(canvas);
					}		
					
					// calculate the time that has passed
					timeDiff = System.currentTimeMillis() - beginTime;
					
					// sleep time 
					sleepTime = (int) (FRAME_PERIOD - timeDiff);
					
					// are we ahead?
					if (sleepTime > 0) {
						// send Thread to sleep for a bit
						try {
							Thread.sleep(sleepTime);
						} 
						catch (InterruptedException e) {}
					}
					
					while (sleepTime < 0 && skippedFrames < MAX_FPS_SKIPS) {
						// behind and in need to catch up
						if (!view.isPaused() && !view.inHelpMode()) {
							engine.update();
						}
						
						sleepTime += FRAME_PERIOD;
						skippedFrames++;
					}
				}
			}
			finally {
				if (canvas != null) {
					this.surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
}