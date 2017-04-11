package game.main;

//Imports from our packages
import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Brick;
import edu.virginia.engine.display.Coin;
import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.events.*;
import edu.virginia.engine.tween.*;
import edu.virginia.engine.util.GameClock;
import edu.virginia.engine.util.Sound;

//Imports from  java packages
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class BetaLVL01 extends Game implements IEventListener {
	// Checking out how to use the level switcher
	int eFrames = 0;
	static BetaLVL01 level01;
	static Beta mapWorld;

	// Player sprite and save state variables
	AnimatedSprite player = new AnimatedSprite("player");
	Sprite saveState1 = new Sprite("saveState1", "saved1.png");
	boolean save1 = false;
	Sprite saveState2 = new Sprite("saveState2", "saved2.png");
	boolean save2 = false;
	int saveTracker = 1;
	int sFrames = 0;

	// Button sprites and variables
	Brick button = new Brick("button", "button.png");
	boolean buttonPressed = false;

	// Platform sprites and variables
	Brick brick4 = new Brick("Brick4", "Brick.png");
	Brick platform2 = new Brick("platform2", "platform.png");
	Brick gate = new Brick("gate", "gate.png");

	// Hazards sprites and variables
	Sprite ball = new Sprite("ball", "ball.png");

	// Objective sprites and variables
	Coin coin = new Coin("Coin", "Coin.png");
	Tween coinTween = new Tween(coin);

	// Holds the starting positions of all our moveable sprites, so that they
	// can be
	// reset when we "save/reload"
	HashMap<Sprite, Point> startingPositions = new HashMap<Sprite, Point>();

	// Managers and singletons
	QuestManager manager = new QuestManager();
	GameClock gameTimer;
	TweenJuggler juggler = new TweenJuggler();

	public BetaLVL01() {
		super("BetaLVL01", 1200, 800);

		// Animated sprite, not doing anything now
		List<String> animatedSpriteList = new ArrayList<String>();
		animatedSpriteList.add("hero.png");
		animatedSpriteList.add("hero.png");
		player = new AnimatedSprite("player", animatedSpriteList);
		HashMap<String, int[]> animations = new HashMap<String, int[]>();
		int[] num = new int[2];
		num[0] = 0;
		num[1] = 1;
		animations.put("run", num);
		player.setAnimations(animations);

		// Sound info
		Sound bgm = new Sound("cooking.wav");
		bgm.loop();

		// Sprite positioning (SHOULD PROBABLY RE WORK THIS AT SOME POINT)
		coin.setxPos(50);
		coin.setyPos(50);

		brick4.setxPos(300);
		brick4.setyPos(500);

		player.setxPos(20);
		player.setyPos(640);
		startingPositions.put(player, new Point((int) player.getxPos(), (int) player.getyPos()));

		gate.setxPos(500);
		gate.setyPos(465);
		startingPositions.put(gate, new Point((int) gate.getxPos(), (int) gate.getyPos()));

		platform2.setxPos(0);
		platform2.setyPos(150);

		button.setxPos(300);
		button.setyPos(740);

		ball.setxPos(300);
		ball.setyPos(10);
		startingPositions.put(ball, new Point((int) ball.getxPos(), (int) ball.getyPos()));

		// Player tweens
		TweenTransitions transit = new TweenTransitions();
		Tween marioTween = new Tween(player, transit);
		marioTween.animate(TweenableParams.alpha, 0, 1, 1000);
		marioTween.animate(TweenableParams.yPos, 300, 670, 1000);
		juggler.add(marioTween);

		// Event registering
		coin.addEventListener(this, "CoinPickedUp");
		brick4.addEventListener(this, "playerCollision");
		saveState1.addEventListener(this, "playerCollision");
		saveState2.addEventListener(this, "playerCollision");
		gate.addEventListener(this, "playerCollision");
		platform2.addEventListener(this, "playerCollision");
		
		button.addEventListener(this, "ButtonPressed");


		if (gameTimer == null) {
			gameTimer = new GameClock();
		}

	}

	/**
	 * Engine will automatically call this update method once per frame and pass
	 * to us the set of keys (as strings) that are currently being pressed down
	 */
	@Override
	public void update(ArrayList<String> pressedKeys) {
		// Set button(s) to false at the start of each frame
		buttonPressed = false;

		if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_E)) && eFrames == 0) {
			System.out.println("e pressed");
			
			mapWorld = new Beta();
			mapWorld.start();
			
			level01.exitGame();
			// A ghetto way of making sure this s key if statement is called at
			// max every 10 frames
			eFrames = 20;
		}
		
		// Our "save" function key -- Now it is SPACE
		if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE)) && sFrames == 0) {

			// Figure out where to save our old player ghost
			if (save1 == false) {
				saveState1.setxPos(player.getxPos());
				saveState1.setyPos(player.getyPos());
				save1 = true;
			} else if (save2 == false) {
				saveState2.setxPos(player.getxPos());
				saveState2.setyPos(player.getyPos());
				save2 = true;
			}
			// This is for overwriting the ghosts
			else if (save1 && save2) {
				if (saveTracker == 1) {
					saveState1.setxPos(player.getxPos());
					saveState1.setyPos(player.getyPos());
					saveTracker = 2;
				} else if (saveTracker == 2) {
					saveState2.setxPos(player.getxPos());
					saveState2.setyPos(player.getyPos());
					saveTracker = 1;
				}
			}

			// We have a hashMap of <Sprite, Starting x and y>
			Iterator entries = startingPositions.entrySet().iterator();
			while (entries.hasNext()) {
				// Grab our sprite and Point
				Entry thisEntry = (Entry) entries.next();
				Object sprite = thisEntry.getKey();
				Object pos = thisEntry.getValue();

				// Set our sprite back to it's starting position
				((Sprite) sprite).setxPos(((Point) pos).getX());
				((Sprite) sprite).setyPos(((Point) pos).getY());

				// An if check to replay our tween if its the player
				if (((Sprite) sprite).getId().equals("player")) {
					TweenTransitions transit = new TweenTransitions();
					Tween marioTween = new Tween(player, transit);

					marioTween.animate(TweenableParams.alpha, 0, 1, 1000);
					marioTween.animate(TweenableParams.yPos, 300, 670, 1000);

					juggler.add(marioTween);
				}
			}

			// A ghetto way of making sure this s key if statement is called at
			// max every 10 frames
			sFrames = 10;
		}

		if (player != null && button != null) {

			// Resetting the button if it is not being stepped on
			button.setDisplayImage("button.png");
			button.setyPos(740);

			// Jumping and falling
			player.setyPos(player.getyPos() + player.getV());

			// Give ball velocity so it falls
			ball.setyPos(ball.getyPos() + ball.getV());
			ball.setV((ball.getG() + ball.getV()) / 1.1);

			// Check if we are on ground?
			if (player.getyPos() >= 650) {
				player.setOnGround(true);
			}

			// Reset velocity to 0 if player is on ground
			if (player.isOnGround()) {
				player.setV(0);
			}

			// Check if we are in air
			else {
				player.setV(player.getV() + player.getG());
				System.out.println("falling");
			}

			// boundary checking
			if (player.getxPos() < 0)
				player.setxPos(0);
			if (player.getxPos() > 1150)
				player.setxPos(1150);
			if (player.getyPos() > 800)
				player.setyPos(800);

			player.update(pressedKeys);

			if (player.isOnGround()) {
				player.setOnGround(false);
			}

			// Coin hitbox
			if (player.getHitBox().intersects(coin.getHitBox())) {
				System.out.println("Hit!!");
				PickedUpEvent event = new PickedUpEvent();
				coin.dispatchEvent(event);
			}
			// Savestate hit boxes
			if (player.getHitBox().intersects(saveState1.getHitBox())) {
				Event event = new Event("playerCollision", saveState1);
				saveState1.dispatchEvent(event);
			}
			if (player.getHitBox().intersects(saveState2.getHitBox())) {
				Event event = new Event("playerCollision", saveState2);
				saveState2.dispatchEvent(event);
			}
			//Button hitboxes
			if (player.getHitBox().intersects(button.getHitBox())) {
				Event event = new Event("ButtonPressed");
				button.dispatchEvent(event);
			}
			if (saveState1.getHitBox().intersects(button.getHitBox())) {
				Event event = new Event("ButtonPressed");
				button.dispatchEvent(event);
			}
			if (saveState2.getHitBox().intersects(button.getHitBox())) {
				Event event = new Event("ButtonPressed");
				button.dispatchEvent(event);
			}

			// Collision with gate
			if (player.getHitBox().intersects(gate.getHitBox())) {
				Event event = new Event("gateCollision", gate);
				gate.dispatchEvent(event);
			}

			// Collision with top platform
			if (player.getHitBox().intersects(platform2.getHitBox())) {
				Event event = new Event("playerCollision", platform2);
				platform2.dispatchEvent(event);
			}

			// Collision with near coin brick
			if (player.getHitBox().intersects(brick4.getHitBox())) {
				Event event = new Event("playerCollision", brick4);
				brick4.dispatchEvent(event);
			}

			juggler.getInstance().nextFrame();

		}

		//Making it so we can only hit SPACE once every ten frames
		if (sFrames > 0) {
			sFrames--;
		}
		//Making it so we can only hit E once every ten frames
		if (eFrames > 0) {
			eFrames--;
		}

		//Resetting gate if buttonPressed is false
		if (buttonPressed == false) {
			if (gate.getyPos() < 465) {
				gate.setyPos(gate.getyPos() + gate.getV());
				gate.setV((gate.getG() + gate.getV()) / 1);
			}
		}

	}

	/**
	 * Engine automatically invokes draw() every frame as well. If we want to
	 * make sure Sun gets drawn to the screen, we need to make sure to override
	 * this method and call Sun's draw method.
	 */
	@Override
	public void draw(Graphics g) {

		//Background
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, 1400, 900);

		if (player != null && button != null) {
			button.draw(g);
			ball.draw(g);
			brick4.draw(g);
			coin.draw(g);
			player.draw(g);
			gate.draw(g);
			platform2.draw(g);

		}

		//Draw savestates
		if (saveState1 != null && save1)
			saveState1.draw(g);
		if (saveState2 != null && save2)
			saveState2.draw(g);

	}

	/**
	 * Quick main class that simply creates an instance of our game and starts
	 * the timer that calls update() and draw() every frame
	 */
	public static void main(String[] args) {
		level01 = new BetaLVL01();
		level01.start();

	}

	// Where all our events are for right now

	public void handleEvent(Event event) {
		
		//Objective event
		if (event.getEventType() == "CoinPickedUp") {
			player.setAlpha(0);
			System.out.println("Quest is completed!");

			coinTween.animate(TweenableParams.xPos, 950, 420, 1500);
			coinTween.animate(TweenableParams.yPos, 100, 230, 1500);
			coinTween.animate(TweenableParams.scaleX, 1, 3, 1500);
			coinTween.animate(TweenableParams.scaleY, 1, 3, 1500);
			coinTween.addEventListener(this, TweenEvent.TWEEN_COMPLETE_EVENT);

			juggler.add(coinTween);
		}

		//Button pressed event
		if (event.getEventType() == "ButtonPressed") {
			buttonPressed = true;
			System.out.println("Button is being pressed");
			button.setDisplayImage("button_pressed.png");
			// Set position of the pressed button sprite a little bit lower so
			// that it looks better
			button.setyPos(753);

			if (gate.getyPos() > 320) {
				gate.setyPos((gate.getyPos() - 5));
			}
			gate.setV(0);

		}

		// Tween event
		if (event.getEventType() == TweenEvent.TWEEN_COMPLETE_EVENT) {
			coinTween.removeEventListener(this, TweenEvent.TWEEN_COMPLETE_EVENT);
			TweenTransitions transit = new TweenTransitions();
			Tween fadeTween = new Tween(coin, transit);
			fadeTween.animate(TweenableParams.alpha, 1, 0, 2000);
			fadeTween.animate(TweenableParams.rotation, 1, 30, 2000);
			juggler.add(fadeTween);
		}

		if (event.getEventType() == "collide") {
			System.out.println("Collision!");

			Rectangle inter = player.getHitBox().intersection(button.getHitBox());
			Rectangle inter1 = saveState1.getHitBox().intersection(button.getHitBox());
			Rectangle inter2 = saveState2.getHitBox().intersection(button.getHitBox());

			if (!inter.isEmpty()) {

				// intesect from above, then bottom does not touch ground
				// moreover, edge case
				if (inter.getY() + inter.getHeight() > button.getyPos() && inter.getWidth() >= inter.getHeight() + 5) {
					player.setyPos(button.getyPos() - player.getHeight());
					player.setOnGround(true);

				}

				// intersect from left, hitbox start from left of coin
				else {

					if (inter.getX() == button.getxPos()) {
						player.setxPos(button.getxPos() - player.getWidth());
					}

					// intersect from right, hitbox start from right of coin
					if (inter.getX() + inter.getWidth() == button.getxPos() + button.getWidth()) {
						player.setxPos(button.getxPos() + button.getWidth());
					}
					player.setOnGround(false);
				}

			}

		}

		if (event.getEventType() == "playerCollision") {
			System.out.println("Collision with: ");
			Sprite source = (Sprite) event.getSource();

			Rectangle inter = player.getHitBox().intersection(source.getHitBox());
			Rectangle inter3 = player.getHitBox().intersection(saveState1.getHitBox());
			Rectangle inter4 = player.getHitBox().intersection(saveState2.getHitBox());
			if (!inter.isEmpty()) {

				// intesect from above, then bottom does not touch ground
				// moreover, edge case
				if (inter.getY() + inter.getHeight() >= source.getyPos() && inter.getWidth() >= inter.getHeight() + 5) {
					if (inter.getY() + inter.getHeight() <= source.getyPos() + (source.getHeight() / 2)) {
						player.setyPos(source.getyPos() - player.getHeight());
						player.setOnGround(true);
					} else {
						player.setV(0);
						;
						player.setyPos(source.getyPos() + source.getHeight());
					}
				}
				// intersect from left, hitbox start from left of coin
				else {

					if (inter.getX() == source.getxPos()) {
						player.setxPos(source.getxPos() - player.getWidth());
					}

					// intersect from right, hitbox start from right of coin
					if (inter.getX() + inter.getWidth() == source.getxPos() + source.getWidth()) {
						player.setxPos(source.getxPos() + source.getWidth());
					}
					player.setOnGround(false);
				}
			}
		}

	

		if (event.getEventType() == "gateCollision") {
			System.out.println("Collision! with gate");

			Rectangle inter = player.getHitBox().intersection(gate.getHitBox());

			if (!inter.isEmpty()) {

				// intesect from above, then bottom does not touch ground
				// moreover, edge case
				if (inter.getY() + inter.getHeight() >= gate.getyPos() && inter.getWidth() >= inter.getHeight() + 5) {
					if (inter.getY() + inter.getHeight() <= gate.getyPos() + (gate.getHeight() / 2)) {
						player.setOnGround(false);

					} else {
						player.setV(0);
						player.setyPos(gate.getyPos() + gate.getHeight());
					}
				}
				// intersect from left, hitbox start from left of coin
				else {

					if (inter.getX() == gate.getxPos()) {
						player.setxPos(gate.getxPos() - player.getWidth());
					}

					// intersect from right, hitbox start from right of coin
					if (inter.getX() + inter.getWidth() == gate.getxPos() + gate.getWidth()) {
						player.setxPos(gate.getxPos() + gate.getWidth());
					}
					player.setOnGround(false);
				}
			}
		}

	}
}