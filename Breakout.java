/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setup();
		play();
	}
	
	private void setup() {
		drawBricks();
		drawPaddle();
		drawBall();
	}
	
	private void drawBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			Color color;
			switch(i / 2) {
				case 0: color = Color.red; break;
				case 1: color = Color.orange; break;
				case 2: color = Color.yellow; break;
				case 3: color = Color.green; break;
				case 4: color = Color.cyan; break;
				default: color = Color.black; break;
			}
			int y = BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP);
			drawRow(color, y);
		}
	}
	
	private void drawRow(Color color, int y) {
		for (int i = 0; i < NBRICKS_PER_ROW; i++) {
			int widthOfAllBricks = BRICK_WIDTH * NBRICKS_PER_ROW + BRICK_SEP * (NBRICKS_PER_ROW - 1);
			double firstRowX = (WIDTH - widthOfAllBricks) / 2;
			double x = firstRowX + i * (BRICK_WIDTH + BRICK_SEP);
			drawBrick(x, y, color);
		}
	}
	
	private void drawBrick(double x, int y, Color color) {
		add(new GBrick(BRICK_WIDTH, BRICK_HEIGHT, color), x, y);
	}
	
	private void drawPaddle() {
		
	}
	
	private void drawBall() {
		
	}
	
	private void play() {
		
	}

}
