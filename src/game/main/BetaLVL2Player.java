package game.main;

//Imports from our packages
import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.AnimatedSprite2;
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
import java.util.Random;

public class BetaLVL2Player extends Game implements IEventListener {
	 //Size of our Game
		static int MAXHEIGHT = 800;
		static int MAXWIDTH = 1200;
		boolean lvlStart2Player = false;
		int startBallFrames = 0;
		int ballDirection = -1;
		int ballSpeed = 3;
		int  n = 1;
		
		boolean playerOneScore = false;
		int p1Score = 0;
		int p2Score = 0;
		boolean playerTwoScore = false;
		
		Random rand;
		// Checking out how to use the level switcher
		static Beta game;
		int eFrames;
		Sound bgm;
		Sprite SplashCredits = new Sprite("Splash", "SplashCredits.png");


		// Info Sprites
		Sprite playerOneScoreText = new Sprite("playerOneScore", "redScores.png");
		Sprite playerTwoScoreText = new Sprite("playerTwoScore", "blueScores.png");

		
		// Player sprite and save state variables
		AnimatedSprite2 player = new AnimatedSprite2("player");
		
		AnimatedSprite2 player2 = new AnimatedSprite2("player2");
		Sprite saveState1 = new Sprite("saveState1", "saved1.png");
		
		boolean save1 = false;
		Sprite saveState2 = new Sprite("saveState2", "saved2.png");
		boolean save2 = false;
		
		Sprite TwosaveState1 = new Sprite("saveState1", "saved1.png");	
		boolean Twosave1 = false;
		Sprite TwosaveState2 = new Sprite("saveState2", "saved2.png");
		boolean Twosave2 = false;
		int saveTracker = 1;
		int TwosaveTracker = 1;
		int sFrames = 0;
		int leftFrames = 0;

		// Button sprites and variables
		Sprite Background = new Sprite("Background", "backgroundPong.png");
		
		Sprite blue0 = new Sprite("blue0", "blue0.png");
		Sprite blue1 = new Sprite("blue1", "blue1.png");
		Sprite blue2 = new Sprite("blue2", "blue2.png");
		Sprite blue3 = new Sprite("blue3", "blue3.png");
		
		Sprite red0 = new Sprite("blue0", "red0.png");
		Sprite red1 = new Sprite("blue1", "red1.png");
		Sprite red2 = new Sprite("blue2", "red2.png");
		Sprite red3 = new Sprite("blue3", "red3.png");
		// Platform sprites and variables

	    

		// Hazards sprites and variables

		// Objective sprites and variables
		Brick ball = new Brick("ball","ball.png");
		
		
		
		// Holds the starting positions of all our moveable sprites, so that they
		// can be reset when we "save/reload"
		HashMap<Sprite, Point> startingPositions = new HashMap<Sprite, Point>();

		// Managers and singletons
		QuestManager manager = new QuestManager();
		GameClock gameTimer;
		TweenJuggler juggler = new TweenJuggler();

		public BetaLVL2Player() {
			super("BetaLVL2Player", MAXWIDTH, MAXHEIGHT);
			TweenTransitions screen = new TweenTransitions();
			Tween splashTween = new Tween(SplashCredits, screen);
			juggler.add(splashTween);
			splashTween.animate(TweenableParams.alpha, 1, 0, 2000);
			
			rand = new Random();
			n = rand.nextInt(20) + -10;
			// Animated sprite, not doing anything now
			List<String> animatedSpriteList = new ArrayList<String>();
			animatedSpriteList.add("hero.png");
			animatedSpriteList.add("hero.png");
			player = new AnimatedSprite2("player", animatedSpriteList);
			
			List<String> animatedSpriteList2 = new ArrayList<String>();
			animatedSpriteList2.add("redHero.png");
			animatedSpriteList2.add("redHero.png");
			player2 = new AnimatedSprite2("player", animatedSpriteList2);
			
			HashMap<String, int[]> animations = new HashMap<String, int[]>();
			int[] num = new int[2];
			num[0] = 0;
			num[1] = 1;
			animations.put("run", num);
			player.setAnimations(animations);
			player2.setAnimations(animations);

			bgm = new Sound("VideoGame.wav");
			bgm.loop();

			// Sprite positioning (SHOULD PROBABLY RE WORK THIS AT SOME POINT)
			player.setxPos(100-player.getWidth());
			player.setyPos(MAXHEIGHT/2);
			//startingPositions.put(player, new Point((int) player.getxPos(), (int) player.getyPos()));
			
			player2.setxPos(1100);
			player2.setyPos(MAXHEIGHT/2);
			//startingPositions.put(player, new Point((int) player.getxPos(), (int) player.getyPos()));

			
			ball.setxPos(MAXWIDTH/2-50);
			ball.setyPos(MAXHEIGHT/2-50);
			

			
			
			playerOneScoreText.setxPos(MAXWIDTH/2-140);
			playerOneScoreText.setyPos(150);
			
			blue0.setxPos(MAXWIDTH/2-160);
			blue0.setyPos(25);
			
			blue1.setxPos(MAXWIDTH/2-160);
			blue1.setyPos(25);
			
			blue2.setxPos(MAXWIDTH/2-160);
			blue2.setyPos(25);
			
			blue3.setxPos(MAXWIDTH/2-160);
			blue3.setyPos(25);
			
			playerTwoScoreText.setxPos(MAXWIDTH/2-140);
			playerTwoScoreText.setyPos(150);
			
			red0.setxPos(MAXWIDTH/2+120);
			red0.setyPos(25);
			
			red1.setxPos(MAXWIDTH/2+120);
			red1.setyPos(25);
			
			red2.setxPos(MAXWIDTH/2+120);
			red2.setyPos(25);
			
			red3.setxPos(MAXWIDTH/2+120);
			red3.setyPos(25);
			
			// Player tweens
//			TweenTransitions transit = new TweenTransitions();
//			Tween marioTween = new Tween(player, transit);
//			marioTween.animate(TweenableParams.alpha, 0, 1, 1000);
//			marioTween.animate(TweenableParams.yPos, 300, 670, 1000);
//			juggler.add(marioTween);

			// Event registering
//			saveState1.addEventListener(this, "playerCollision");
//			saveState2.addEventListener(this, "playerCollision");
			
			ball.addEventListener(this, "playerCollision");
			
			ball.addEventListener(this, "playerCollision2");



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
			//Reset our flags at start of frame
			if(SplashCredits.getAlpha() < .08f){this.lvlStart2Player = true;}
			startBallFrames++;
			
			
			
			
			
			//System.out.println("Ball frame:" + startBallFrames);
			if(startBallFrames > 250){

				playerOneScore = false;
				playerTwoScore = false;


				ball.setxPos((ball.getxPos())+(ballSpeed*ballDirection));
				ball.setyPos((ball.getyPos())+n);
				
				///Key logic
				if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_E)) && eFrames == 0) {
						eFrames = 20;
						bgm.stop();
						game = new Beta();
						game.start();
						game.setLevelComplete(6);
					   this.exitGame();
	
					
	
				}
	
				// Our "save" function key
				
				
				
	
	
				//hitbox logic
				if (player != null) {
	
	
					
	
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
	
//					// Savestate hit boxes
//					if (player.getHitBox().intersects(saveState1.getHitBox())) {
//						Event event = new Event("playerCollision", saveState1);
//						saveState1.dispatchEvent(event);
//					}
//					if (player.getHitBox().intersects(saveState2.getHitBox())) {
//						Event event = new Event("playerCollision", saveState2);
//						saveState2.dispatchEvent(event);
//					}
					
					
					
					if(ball.getyPos()>0){
						n = n * -1;
					}
					
					if(ball.getyPos()<MAXHEIGHT-65){
						n = n * -1;
					}
					
					//Scoring
					
					if(ball.getxPos()<0){
						playerOneScore = true;
						p2Score++;
						startBallFrames = 0;
						ballSpeed = 3;
						ball.setxPos(MAXWIDTH/2-50);
						ball.setyPos(MAXHEIGHT/2-50);
					}
					
					if(ball.getxPos()>MAXWIDTH-10){
						playerTwoScore = true;
						p1Score++;
						startBallFrames = 0;
						ballSpeed = 3;
						ball.setxPos(MAXWIDTH/2-50);
						ball.setyPos(MAXHEIGHT/2-50);
					}
					
					
					if (player.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision", ball);
						ball.setDisplayImage("ball.png");
						ballDirection = 1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
					if (saveState1.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision", ball);
						ball.setDisplayImage("ball.png");
						ballDirection = 1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
					if (saveState2.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision", ball);
						ball.setDisplayImage("ball.png");
						ballDirection = 1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
					
					if (player2.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision2", ball);
						ball.setDisplayImage("ballRed.png");
						ballDirection = -1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
					if (TwosaveState1.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision2", ball);
						ball.setDisplayImage("ball.png");
						ballDirection = 1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
					if (TwosaveState2.getHitBox().intersects(ball.getHitBox())) {
						Event event = new Event("playerCollision2", ball);
						ball.setDisplayImage("ball.png");
						ballDirection = 1;
						if(ballSpeed < 15){
							ballSpeed++;
						}
						n = rand.nextInt(20) + -10;
						ball.dispatchEvent(event);
					}
	
	
					juggler.getInstance().nextFrame();
	
				}
				
				
	
				// Making it so we can only hit S once every ten frames
				if (sFrames > 0) {
					sFrames--;
				}
	
				// Making it so we can only hit S once every ten frames
				if (leftFrames > 0) {
					leftFrames--;
				}
				//Making it so we can only hit E once every ten frames
				if (eFrames > 0) {
					eFrames--;
				}
				
				if(player2.getxPos()>MAXWIDTH-50){
					player2.setxPos(MAXWIDTH-50);
				}
				
				
		}
			
			//Player 1
			System.out.println(player.getyPos());
			 if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_W))) {
				 if(player.getyPos()-8 > 0){
					 player.setyPos(player.getyPos()-8);
				 }
            }
			 
			 if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_S))) {
				 if(player.getyPos()+8<650){
					 player.setyPos(player.getyPos()+8);
				 }
            }
//			 if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D)) && sFrames == 0) {
//					
//					// Figure out where to save our old player ghost
//					if (save1 == false) {
//						saveState1.setxPos(player.getxPos());
//						saveState1.setyPos(player.getyPos());
//						save1 = true;
//					} else if (save2 == false) {
//						saveState2.setxPos(player.getxPos());
//						saveState2.setyPos(player.getyPos());
//						save2 = true;
//					}
//					// This is for overwriting the ghosts
//					else if (save1 && save2) {
//						if (saveTracker == 1) {
//							saveState1.setxPos(player.getxPos());
//							saveState1.setyPos(player.getyPos());
//							saveTracker = 2;
//						} else if (saveTracker == 2) {
//							saveState2.setxPos(player.getxPos());
//							saveState2.setyPos(player.getyPos());
//							saveTracker = 1;
//						}
//					}
//	
//					// We have a hashMap of <Sprite, Starting x and y>
//					Iterator entries = startingPositions.entrySet().iterator();
//					while (entries.hasNext()) {
//						// Grab our sprite and Point
//						Entry thisEntry = (Entry) entries.next();
//						Object sprite = thisEntry.getKey();
//						Object pos = thisEntry.getValue();
//	
//						// Set our sprite back to it's starting position
//						((Sprite) sprite).setxPos(((Point) pos).getX());
//						((Sprite) sprite).setyPos(((Point) pos).getY());
//	
//	
//					}
//	
//					// A ghetto way of making sure this s key if statement is called at
//					// max every 10 frames
//					sFrames = 10;
//				}
			
			
			//Player 2
			 if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP))) {
				 if(player2.getyPos()-8>0){
					 player2.setyPos(player2.getyPos()-8);
				 }
             }
			 
			 if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_DOWN))) {
				 if(player2.getyPos()+8<650){
					 player2.setyPos(player2.getyPos()+8);
				 }
             }
//			 if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT)) && leftFrames == 0) {
//				// Figure out where to save our old player ghost
//					if (Twosave1 == false) {
//						TwosaveState1.setxPos(player2.getxPos());
//						TwosaveState1.setyPos(player2.getyPos());
//						Twosave1 = true;
//					} else if (Twosave2 == false) {
//						TwosaveState2.setxPos(player2.getxPos());
//						TwosaveState2.setyPos(player2.getyPos());
//						Twosave2 = true;
//					}
//					// This is for overwriting the ghosts
//					else if (Twosave1 && Twosave2) {
//						if (TwosaveTracker == 1) {
//							TwosaveState1.setxPos(player2.getxPos());
//							TwosaveState1.setyPos(player2.getyPos());
//							TwosaveTracker = 2;
//						} else if (TwosaveTracker == 2) {
//							TwosaveState2.setxPos(player2.getxPos());
//							TwosaveState2.setyPos(player2.getyPos());
//							TwosaveTracker = 1;
//						}
//					}
//	
//					// We have a hashMap of <Sprite, Starting x and y>
//					Iterator entries = startingPositions.entrySet().iterator();
//					while (entries.hasNext()) {
//						// Grab our sprite and Point
//						Entry thisEntry = (Entry) entries.next();
//						Object sprite = thisEntry.getKey();
//						Object pos = thisEntry.getValue();
//	
//						// Set our sprite back to it's starting position
//						((Sprite) sprite).setxPos(((Point) pos).getX());
//						((Sprite) sprite).setyPos(((Point) pos).getY());
//	
//	
//					}
//	
//					// A ghetto way of making sure this s key if statement is called at
//					// max every 10 frames
//					leftFrames = 10;
//				 //leave a copy
//             }
	}

		/**
		 * Engine automatically invokes draw() every frame as well. If we want to
		 * make sure Sun gets drawn to the screen, we need to make sure to override
		 * this method and call Sun's draw method.
		 */
		@Override
		public void draw(Graphics g) {

			// Background
			//g.setColor(Color.GRAY);
			//g.fillRect(0, 0, 1400, 900);
			Background.draw(g);
			
			if(!lvlStart2Player){
				SplashCredits.draw(g);
				System.out.println("Drawing lvlStart");
			}
			

			if (player != null) {
				
				if(playerOneScore){
					playerOneScoreText.draw(g);
				}
				
				if(playerTwoScore){
					playerTwoScoreText.draw(g);
				}

				player.draw(g);
				player2.draw(g);
				ball.draw(g);


			}
			
			if(p1Score == 0){
				blue0.draw(g);
			}
			if(p1Score == 1){
				blue1.draw(g);
			}
			if(p1Score == 2){
				blue2.draw(g);
			}
			if(p1Score == 3){
				blue3.draw(g);
			}
			
			if(p2Score == 0){
				red0.draw(g);
			}
			if(p2Score == 1){
				red1.draw(g);
			}
			if(p2Score == 2){
				red2.draw(g);
			}
			if(p2Score == 3){
				red3.draw(g);
			}

			// Draw savestates
			if (saveState1 != null && save1)
				saveState1.draw(g);
			if (saveState2 != null && save2)
				saveState2.draw(g);
			
			if (TwosaveState1 != null && Twosave1)
				TwosaveState1.draw(g);
			if (TwosaveState2 != null && Twosave2)
				TwosaveState2.draw(g);

		}

		/**
		 * Quick main class that simply creates an instance of our game and starts
		 * the timer that calls update() and draw() every frame
		 */
		public static void main(String[] args) {
			BetaLVL2Player game = new BetaLVL2Player();
			game.start();

		}

		// Where all our events are for right now

		
		
		
		
		public void handleEvent(Event event) {

			// Tween event
	        if(event.getEventType()==TweenEvent.TWEEN_COMPLETE_EVENT) {
	          
   
	        }
	        
			// Objective event
			if (event.getEventType() == "CoinPickedUp") {
				player.setAlpha(0);
				System.out.println("Quest is completed!");

			}

			
			//Intersecting with door
			if (event.getEventType() == "inGoalEvent") {

		          
			}
			
			//Intersecting with door1
			if (event.getEventType() == "infoShow") {
				System.out.println("infoShow");


			}

			// Button pressed event
			if (event.getEventType() == "ButtonPressed") {
				System.out.println("Button is being pressed");
				// Set position of the pressed button sprite a little bit lower so
				// that it looks better

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
							player.setyPos(source.getyPos() + source.getHeight());
						}
					}
					// intersect from left, hitbox start from left of coin
					else {

						if (inter.getX() == source.getxPos()) {
							player.setxPos(source.getxPos() - player.getWidth()-10);
						}

						// intersect from right, hitbox start from right of coin
						if (inter.getX() + inter.getWidth() == source.getxPos() + source.getWidth()) {
							player.setxPos(source.getxPos() + source.getWidth()+10);
						}
						player.setOnGround(false);
					}
				}
			}
			
			
			if (event.getEventType() == "playerCollision2") {
				System.out.println("Collision with: ");
				Sprite source = (Sprite) event.getSource();

				Rectangle inter = player2.getHitBox().intersection(source.getHitBox());
				Rectangle inter3 = player2.getHitBox().intersection(TwosaveState1.getHitBox());
				Rectangle inter4 = player2.getHitBox().intersection(TwosaveState2.getHitBox());
				if (!inter.isEmpty()) {

					// intesect from above, then bottom does not touch ground
					// moreover, edge case
					if (inter.getY() + inter.getHeight() >= source.getyPos() && inter.getWidth() >= inter.getHeight() + 5) {
						if (inter.getY() + inter.getHeight() <= source.getyPos() + (source.getHeight() / 2)) {
							player2.setyPos(source.getyPos() - player2.getHeight());
							player2.setOnGround(true);
						} else {
							player2.setV(0);
							player2.setyPos(source.getyPos() + source.getHeight());
						}
					}
					// intersect from left, hitbox start from left of coin
					else {

						if (inter.getX() == source.getxPos()) {
							player2.setxPos(source.getxPos() - player2.getWidth()-10);
						}

						// intersect from right, hitbox start from right of coin
						if (inter.getX() + inter.getWidth() == source.getxPos() + source.getWidth()) {
							player2.setxPos(source.getxPos() + source.getWidth()+10);
						}
						player2.setOnGround(false);
					}
				}
			}
			

		}
	}