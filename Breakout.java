/*
 * File: Breakout.java
 * -------------------
 * Name: Colin Regan
 * 
 * This file implements the game Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 501;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 100;
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
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Pause time in milliseconds between updates to ball location in while loop */
	private static final int PAUSE_TIME = 10;
	
/** Max and min horizontal velocity of the ball, and constant vertical velocity (max) */
	private static final double MAX_VELOCITY = 3.5;
	private static final double MIN_VELOCITY = 1.0;
	
/** Pixel offset from left wall for balls left display */
	private static final int BALLS_OFFSET = 5;
	
/** Scale factor for controlling horizontal velocity of the ball after bouncing on paddle */
	private static final double VX_SCALE = 4.0;
	
/** Number of bricks at the start of the game */
	private static final int STARTING_BRICKS = NBRICKS_PER_ROW * NBRICK_ROWS;
	
/** Strings to be displayed at the end of the game */
	private static final String LOSER_STR = "Game Over";
	private static final String WINNER_STR = "You Win!";
	private static final int HIGH_SCORE = 0; // 1160
	
/** x-location of the high score display, as percentage of APPLICATION_WIDTH */
	private static final double HIGH_SCORE_DISPLAY_LOCATION = 4 / 5.0;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setup();
		play();
	}
	
	private void setup() {
		addMouseListeners();
		drawBricks();
		drawPaddle();
		drawLivesLeft();
		drawPoints();
		drawHighScore();
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
			int firstRowX = (WIDTH - widthOfAllBricks) / 2;
			int x = firstRowX + i * (BRICK_WIDTH + BRICK_SEP);
			drawBrick(x, y, color);
		}
	}
	
	private void drawBrick(int x, int y, Color color) {
		add(new GBrick(BRICK_WIDTH, BRICK_HEIGHT, color), x, y);
	}
	
	private void drawPaddle() {
		add(paddle, (WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET);
	}
	
	private void drawLivesLeft() {
		for (int i = 0; i < livesLeft; i++) {
			int x = BALLS_OFFSET + i * (BALL_RADIUS + 3);
			int y = HEIGHT - BALL_RADIUS - BALLS_OFFSET;
			GBall miniBall = new GBall(BALL_RADIUS, BALL_RADIUS, Color.black);
			add(miniBall, x, y);
		}		
	}
	
	private void drawPoints() {
		pointDisplay.setLabel("points: " + points);
		add(pointDisplay, WIDTH - pointDisplay.getWidth() - 5, HEIGHT - 5);
	}
	
	private void drawHighScore() {
		highScoreDisplay.setLabel("high score: " + hs);
		add(highScoreDisplay, HIGH_SCORE_DISPLAY_LOCATION * (WIDTH - highScoreDisplay.getWidth()), HEIGHT - 5);
	}
	
	private void updateLivesLeft() {
		livesLeft--;
		int x = BALLS_OFFSET + livesLeft * (BALL_RADIUS + 3) + BALL_RADIUS / 2;
		int y = HEIGHT - BALL_RADIUS / 2 - BALLS_OFFSET;
		GObject ball = getElementAt(x, y);
		if (ball != null) remove(ball);
	}
	
	public void mouseMoved(MouseEvent e) {
		int x = Math.min(WIDTH - PADDLE_WIDTH, Math.max(0, e.getX() - PADDLE_WIDTH / 2));
		paddle.setLocation(x, HEIGHT - PADDLE_Y_OFFSET);
	}
	
	private void drawBall() {
		add(ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS);
		ball.sendToBack();
	}
	
	private void play() {
		while (result == 0 && livesLeft > 0) {
			animateBall();
		}
		endGame();
	}
	
	private void animateBall() {
		
		/* Setup the ball and wait for the click */
		
		setupBall();
		waitForClick();
		
		/* Run the game */
		
		while (true) {
			ball.move(vx, vy);
			pause(PAUSE_TIME);			
			checkForWallCollisions();
			
			/* Check for collisions with paddle or bricks */
			
			GObject collidee = getCollidingObject();
			if (collidee != null) respondToCollision(collidee);
			
			if (isEndOfTurn()) {
				endTurn();
				break;
			}
			
			if (isLastBrick()) {
				endWinner();
				break;
			}
		}
	}
	
	private void setupBall() {
		drawBall();
		vx = rgen.nextDouble(MIN_VELOCITY, MAX_VELOCITY);
		if (rgen.nextBoolean()) vx = -vx;
		vy = MAX_VELOCITY;
	}
	
	private void checkForWallCollisions() {
		if (ball.getX() < 0) vx = Math.abs(vx);
		if (ball.getX() > WIDTH - 2 * BALL_RADIUS) vx = -Math.abs(vx);
		if (ball.getY() < 0) vy = Math.abs(vy);
	}
	
	private void respondToCollision(GObject collidee) {

		/* Reverse ball direction after hitting paddle
		 * Don't let the ball get stuck inside the paddle */
		
		if (collidee == paddle) {
			if (surface == 6) {
				ball.setLocation(ball.getX(), HEIGHT - PADDLE_Y_OFFSET - 2 * BALL_RADIUS);
				vy = -vy;
				vx = updateVX();
			} else if (surface == 3) {
				vx = -Math.abs(vx);
			} else if (surface == 9) {
				vx = Math.abs(vx);
			}
			
			/* If the ball hits anything below the paddle, ignore it */
			
		} else if (collidee.getY() > HEIGHT - PADDLE_Y_OFFSET) {
			
			/* If the ball hits a brick, remove brick and reverse the ball's direction 
			 * Allows for weirdness with ball tunneling through bricks */
			
		} else {
			if (surface == 6 || surface == 12) vy = -vy;
			if (surface == 3 || surface == 9) vx = -vx;
			remove(collidee);
			updatePoints(collidee);
			bricksLeft--;
		}	
	}
	
	private boolean isEndOfTurn() {
		/* End turn if ball hits bottom wall */
		return ball.getY() > HEIGHT - 2 * BALL_RADIUS;
	}
	
	private void endTurn() {
		remove(ball);
		result = 0;
		updateLivesLeft();
	}
	
	private boolean isLastBrick() {
		/* End game if ball hits final brick */
		return bricksLeft == 0;
	}
	
	private void endWinner() {
		remove(ball);
		result = 1;
	}
	
	private GObject getCollidingObject() {
		
		GObject collidee = null;
		
		/* Define 3, 6, 9, and 12 o'clock locations of the ball */
		
		GPoint left = new GPoint(ball.getX() - 1, ball.getY() + BALL_RADIUS);
		GPoint right = new GPoint(ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);
		GPoint top = new GPoint(ball.getX() + BALL_RADIUS, ball.getY() - 1);
		GPoint bottom = new GPoint(ball.getX() + BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS + 1);
		
		/* Find GObject that the ball collided with, if it exists */
		
		if (getElementAt(bottom) != null) {
			collidee = getElementAt(bottom);
			surface = 6;
		} else if (getElementAt(top) != null) {
			collidee = getElementAt(top);
			surface = 12;
		} else if (getElementAt(left) != null) {
			collidee = getElementAt(left);
			surface = 9;
		} else if (getElementAt(right) != null) {
			collidee = getElementAt(right);
			surface = 3;
		} else {
			collidee = null;
			surface = 0;
		}
		return collidee;
	}
	
	/*
	 * Adjusts the horizontal movement of the ball depending on where on the paddle it hit
	 * Allows for the player to control the direction of the ball
	 */
	
	private double updateVX() {
		double midBall = ball.getX() + BALL_RADIUS;
		double paddlePct = ((midBall - paddle.getX()) / PADDLE_WIDTH - 0.5) * 2;
		double newVX = Math.min(MAX_VELOCITY, Math.max(-MAX_VELOCITY, vx + paddlePct * VX_SCALE));
		return newVX;
	}
	
	/*
	 * Determines what happens at the end of the game
	 */
	
	private void endGame() {
		int finalScore = points + livesLeft * 25;
		remove(pointDisplay);
		pointDisplay.setFont("SansSerif-25");
		pointDisplay.setLabel("total points = " + points + " + " + livesLeft + " * 25 = " + finalScore);
		add(pointDisplay, (WIDTH - pointDisplay.getWidth()) / 2, HEIGHT * 3 / 5);
		if (finalScore > hs) {
			GLabel newHighScore = new GLabel("NEW HIGH SCORE!");
			newHighScore.setFont("SansSerif-20");
			add(newHighScore, (WIDTH - newHighScore.getWidth()) / 2, HEIGHT * 4 / 5);
			hs = finalScore;
		}
		if (result == 0) {
			GLabel loser = new GLabel(LOSER_STR);
			loser.setFont("SansSerif-bold-50");
			add(loser, (WIDTH - loser.getWidth()) / 2, (HEIGHT - loser.getAscent()) / 2);
		} else {
			GLabel winner = new GLabel(WINNER_STR);
			winner.setFont("SansSerif-bold-50");
			add(winner, (WIDTH - winner.getWidth()) / 2, (HEIGHT - winner.getAscent()) / 2);
		}
		waitForClick();
		restart();
	}
	
	/* 
	 * Starts a new game 
	 */
	
	private void restart() {
		removeAll();
		surface = 0;                                      
		livesLeft = NTURNS;                               
		result = 0;                                       
		points = 0;                                       
		bricksLeft = STARTING_BRICKS;                     
		pointDisplay = new GLabel("points: " + points);
		run();
	}
	
	private void updatePoints(GObject brick) {
		int a = 1; int b = 1;
		if (brick.getColor() == Color.red) {
			a = 20; b = 1;
		} else if (brick.getColor() == Color.orange) {
			a = 10; b = 5;
		} else if (brick.getColor() == Color.yellow) {
			a = 1; b = 10;
		} else if (brick.getColor() == Color.green) {
			a = -10; b = 15;
		} else if (brick.getColor() == Color.cyan) {
			a = -20; b = 20;
		}
		points += (bricksLeft - 1) / (STARTING_BRICKS / a) + b;
		remove(pointDisplay);
		drawPoints();
	}
	
	
	private GBrick paddle = new GBrick(PADDLE_WIDTH, PADDLE_HEIGHT, Color.black);
	private GBall ball = new GBall(2 * BALL_RADIUS, 2 * BALL_RADIUS, Color.black);
	private int surface = 0;
	private double vx, vy;
	private int livesLeft = NTURNS;
	private int result = 0;
	private int points = 0;
	private int hs = HIGH_SCORE;
	private int bricksLeft = STARTING_BRICKS;
	private GLabel pointDisplay = new GLabel("points: " + points);
	private GLabel highScoreDisplay = new GLabel("high score: " + hs);
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
}

