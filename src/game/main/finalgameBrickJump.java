package game.main;

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

public class finalgameBrickJump extends Game implements IEventListener
{
    AnimatedSprite Mario = new AnimatedSprite("Mario");
    Sprite saveStateMario1 = new Sprite("saveStateMario1","saved1.png");
    boolean save1 = false;
    Sprite saveStateMario2 = new Sprite("saveStateMario2","saved2.png");
    boolean save2 = false;
    int saveTracker = 1;
    // Quick fix for s key being pressed multiple times 
    int sFrames = 0;
    boolean buttonPressed = false;
    boolean buttonPressed2 = false;
    
    Brick brick = new Brick("Brick","Brick.png");
    Brick brick2 = new Brick("Brick2","Brick.png");
    Brick brick3 = new Brick("Brick3","Brick.png");

    Brick button = new Brick("button","button.png");
    Brick button2 = new Brick("button2","button.png");
    Brick button3 = new Brick("button","button.png");
    Brick gate = new Brick("gate","gate.png");
    
    // For showing we can "reset" our game state
    Sprite ball = new Sprite("ball","ball.png");
    
    //The goal
    Coin coin = new Coin("Coin","Coin.png");
    

    // Holds the starting positions of all our moveable sprites, so that they can be 
    // reset when we "save/reload"
    HashMap<Sprite, Point> startingPositions = new HashMap<Sprite, Point>();
    
    Tween coinTween = new Tween(coin);
    QuestManager manager = new QuestManager();
    private boolean condition=false;
    GameClock gameTimer;
    int time;
    TweenJuggler juggler = new TweenJuggler();
	private boolean buttonPressed3;


    public finalgameBrickJump() {
        super("Prototype", 1200, 800);

        
        
        List<String> list = new ArrayList<String>();
        list.add("hero.png");
        list.add("hero.png");
        Mario = new AnimatedSprite("Mario",list);
        HashMap<String, int[]> animations = new HashMap<String, int[]>();
        int[] num = new int[2];  num[0] = 0; num[1] = 1;
        animations.put("run", num);
        Mario.setAnimations(animations);


        Sound bgm = new Sound("cooking.wav");
        bgm.loop();

        
        coin.setxPos(50);
        coin.setyPos(100);
        

        brick.setxPos(775);
        brick.setyPos(475);
        brick.setAlpha(0);
        startingPositions.put(brick,new Point((int)brick.getxPos(), (int)brick.getyPos()));

        

        brick2.setxPos(900);
        brick2.setyPos(650);

        brick3.setxPos(650);
        brick3.setyPos(300);

        Mario.setxPos(20);
        Mario.setyPos(640);
        //Add mario's starting coords to starting coord Map
        startingPositions.put(Mario,new Point((int)Mario.getxPos(), (int)Mario.getyPos()));
        
        gate.setxPos(500);
        gate.setyPos(465);
        startingPositions.put(gate,new Point((int)gate.getxPos(), (int)gate.getyPos()));
        
        button.setxPos(300);
        button.setyPos(740);
        
        button2.setxPos(700);
        button2.setyPos(740);
        
        button3.setxPos(900);
        button3.setyPos(100);
        
        ball.setxPos(300);
        ball.setyPos(10);
        startingPositions.put(ball,new Point((int)ball.getxPos(), (int)ball.getyPos()));

        
        TweenTransitions transit = new TweenTransitions();
        Tween marioTween = new Tween(Mario, transit);

        marioTween.animate(TweenableParams.alpha, 0, 1, 1000);
        marioTween.animate(TweenableParams.yPos, 300, 670, 1000);

        juggler.add(marioTween);

        coin.addEventListener(this,  "CoinPickedUp");
        
        brick.addEventListener(this,  "collide");
        brick2.addEventListener(this, "collide3");
        brick3.addEventListener(this, "collideWithTopBrick");
        button.addEventListener(this, "ButtonPressed");
        button2.addEventListener(this, "ButtonPressed2");
        button3.addEventListener(this, "ButtonPressed3");
        saveStateMario1.addEventListener(this, "hitShadow1");
        saveStateMario2.addEventListener(this, "hitShadow2");
        gate.addEventListener(this,  "collide2");

        if (gameTimer == null) {
            gameTimer = new GameClock();
        }

    }

    /**
     * Engine will automatically call this update method once per frame and pass to us
     * the set of keys (as strings) that are currently being pressed down
     * */
    @Override
    public void update(ArrayList<String> pressedKeys){
    	//For gate
    	buttonPressed=false;
    	
    	//For brick
    	buttonPressed2=false;
    	
    	buttonPressed3=false;

    	// Our "save" function key
		if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_S)) && sFrames == 0){
			
			//Figure out where to save our old mario ghost
			if(save1 == false){
				saveStateMario1.setxPos(Mario.getxPos());
				saveStateMario1.setyPos(Mario.getyPos());
				save1 = true;
			}
			else if(save2 == false){
				saveStateMario2.setxPos(Mario.getxPos());
				saveStateMario2.setyPos(Mario.getyPos());
				save2 = true;
			}
			//This is for overwriting the ghosts
			else if(save1 && save2){
				if(saveTracker == 1){
					saveStateMario1.setxPos(Mario.getxPos());
					saveStateMario1.setyPos(Mario.getyPos());
					saveTracker = 2;
				}
				else if(saveTracker == 2){
					saveStateMario2.setxPos(Mario.getxPos());
					saveStateMario2.setyPos(Mario.getyPos());
					saveTracker = 1;
				}
			}
			
			//We have a hashMap of <Sprite, Starting x and y>
			Iterator entries = startingPositions.entrySet().iterator();
			while (entries.hasNext()) {
				//Grab our sprite and Point
				Entry thisEntry = (Entry) entries.next();
				Object sprite = thisEntry.getKey();
				Object pos = thisEntry.getValue();
			  
				//Set our sprite back to it's starting position
				((Sprite) sprite).setxPos(((Point) pos).getX());
				((Sprite) sprite).setyPos(((Point) pos).getY());
				
				//An if check to replay our tween if its mario
				if(((Sprite) sprite).getId().equals("Mario")){
					TweenTransitions transit = new TweenTransitions();
			        Tween marioTween = new Tween(Mario, transit);

			        marioTween.animate(TweenableParams.alpha, 0, 1, 1000);
			        marioTween.animate(TweenableParams.yPos, 300, 670, 1000);

			        juggler.add(marioTween);
				}		  
			}
			

			// A ghetto way of making sure this s key if statement is called at max every 10 frames
			sFrames = 10;
		}

        if(Mario != null && button != null) {
        	
        	// Resetting the button if it is not being stepped on
        	button.setDisplayImage("button.png");
        	button.setyPos(740);
        	
        	button2.setDisplayImage("button.png");
        	button2.setyPos(740);
        	
        	button3.setDisplayImage("button.png");
        	button3.setyPos(100);
        	
            //update y position accordingly
            Mario.setyPos(Mario.getyPos()+Mario.getV());
            
            // Show falling ball
            ball.setyPos(ball.getyPos()+ball.getV());
            ball.setV((ball.getG()+ball.getV())/1.1);


            // I do not know what setV is or what this code is doing - Nate
            //determine if on ground
            if(Mario.getyPos()>=650) {
                Mario.setOnGround(true);
            }
            //if on ground, no need to udpate v
            if(Mario.isOnGround()) {
                Mario.setV(0);
            }
            //if not, updaet v
            else {
                Mario.setV(Mario.getV()+Mario.getG());
                System.out.println("falling");
            }
            
            
            

            //boundary checking
            if(Mario.getxPos()<0) Mario.setxPos(0);
            if(Mario.getxPos()>1150) Mario.setxPos(1150);
            //if(Mario.getyPos()<0) Mario.setyPos(0);
            if(Mario.getyPos()>800) Mario.setyPos(800);



            Mario.update(pressedKeys);

		//Auto Hitbox for coin
		if(Mario.getHitBox().intersects(coin.getHitBox())) {
		       System.out.println("Hit!!");
		       PickedUpEvent event = new PickedUpEvent();
		       coin.dispatchEvent(event);
		 }
		
		if(Mario.getHitBox().intersects(saveStateMario1.getHitBox())) {
			Event event = new Event("hitShadow1");
		        saveStateMario1.dispatchEvent(event);
		  }
		
		if(Mario.getHitBox().intersects(saveStateMario2.getHitBox())) {
			Event event = new Event("hitShadow2");
			saveStateMario2.dispatchEvent(event);
		  }
		
	    if(Mario.getHitBox().intersects(button.getHitBox())) {
			   Event event = new Event("ButtonPressed");
			   button.dispatchEvent(event);
		 }

		if(saveStateMario1.getHitBox().intersects(button.getHitBox())) {
		    Event event = new Event("ButtonPressed");
		    button.dispatchEvent(event);
		}
		
		if(saveStateMario2.getHitBox().intersects(button.getHitBox())) {
		    Event event = new Event("ButtonPressed");
		    button.dispatchEvent(event);
		}
		
		if(Mario.getHitBox().intersects(button2.getHitBox())) {
		    Event event = new Event("ButtonPressed2");
		        button.dispatchEvent(event);
		  }
		
		 if(saveStateMario1.getHitBox().intersects(button2.getHitBox())) {
		     Event event = new Event("ButtonPressed2");
		     button.dispatchEvent(event);
		 }
		 
		 if(saveStateMario2.getHitBox().intersects(button2.getHitBox())) {
		     Event event = new Event("ButtonPressed2");
		     button.dispatchEvent(event);
		 }
		    
		 if(Mario.getHitBox().intersects(button3.getHitBox())) {
			   Event event = new Event("ButtonPressed3");
			   button3.dispatchEvent(event);
		 }

		if(saveStateMario1.getHitBox().intersects(button3.getHitBox())) {
		    Event event = new Event("ButtonPressed3");
		    button3.dispatchEvent(event);
		}
		
		if(saveStateMario2.getHitBox().intersects(button3.getHitBox())) {
		    Event event = new Event("ButtonPressed3");
		    button3.dispatchEvent(event);
		}
		 
		 if(Mario.getHitBox().intersects(brick2.getHitBox())) {
		
		       Event event = new Event("collide2");
		      brick2.dispatchEvent(event);
		 }
		
		if(Mario.getHitBox().intersects(brick.getHitBox())) {
		
		    Event event = new Event("collide3");
		        brick.dispatchEvent(event);
		 }
		  //Collision with gate
		if(Mario.getHitBox().intersects(gate.getHitBox())) {
			Event event = new Event("gateCollision");
			gate.dispatchEvent(event);
		 }
		  //Collision with top brick
		if(Mario.getHitBox().intersects(brick3.getHitBox())) {
			Event event = new Event("collideWithTopBrick");
			gate.dispatchEvent(event);
		 }    

            juggler.getInstance().nextFrame();

     }
        
        if(sFrames > 0){
        	sFrames--;
        }
        
        if(buttonPressed == false){
        	if(gate.getyPos() < 465){
            	gate.setyPos(gate.getyPos()+gate.getV());
            	gate.setV((gate.getG()+gate.getV())/1);
        	}
        }
        
        if(buttonPressed3 == false){
        	//make top of gate jump-on-able
        	}
        }


    /**
     * Engine automatically invokes draw() every frame as well. If we want to make sure Sun gets drawn to
     * the screen, we need to make sure to override this method and call Sun's draw method.
     * */
    @Override
    public void draw(Graphics g){
    	
    	g.setColor(Color.GRAY);
    	g.fillRect(0, 0, 1400, 900);

        if(Mario != null && button != null) {
        	button.draw(g);
        	ball.draw(g);
        	brick.draw(g);
        	brick2.draw(g);
        	brick3.draw(g);
        	button2.draw(g);
        	button3.draw(g);
        	//Used for drawing our hitboxes
        	//g.fillRect((int)button.getHitBox().getX(),(int) button.getHitBox().getY(),(int)button.getHitBox().getWidth(),(int)button.getHitBox().getHeight());
            coin.draw(g);
            Mario.draw(g);
            gate.draw(g);


        }
        
        if(buttonPressed2 == false){
        	brick.setAlpha(0);
        }

        if(saveStateMario1 != null && save1)
        	saveStateMario1.draw(g);
        if(saveStateMario2 != null && save2)
        	saveStateMario2.draw(g);
        
    }

    /**
     * Quick main class that simply creates an instance of our game and starts the timer
     * that calls update() and draw() every frame
     * */
    public static void main(String[] args) {
        finalgameBrickJump game = new finalgameBrickJump();
        game.start();
    }

    // Where all our events are for right now

    public void handleEvent(Event event)
    {
        if(event.getEventType()=="CoinPickedUp") {
            Mario.setAlpha(0);
            System.out.println("Quest is completed!");

            coinTween.animate(TweenableParams.xPos, 950, 420, 1500);
            coinTween.animate(TweenableParams.yPos, 100, 230, 1500);
            coinTween.animate(TweenableParams.scaleX, 1, 3, 1500);
            coinTween.animate(TweenableParams.scaleY, 1, 3, 1500);
            coinTween.addEventListener(this, TweenEvent.TWEEN_COMPLETE_EVENT);

            juggler.add(coinTween);
        }
        
        // This event is called when the button is pressed
        if(event.getEventType()=="ButtonPressed") {
        	buttonPressed = true;
            System.out.println("Button is being pressed");
            button.setDisplayImage("button_pressed.png");
            //Set position of the pressed button sprite a little bit lower so that it looks better
            button.setyPos(753);
            
            if(gate.getyPos()>320){
            	gate.setyPos((gate.getyPos()-5));
            }
            gate.setV(0);

        }
        
        if(event.getEventType()=="ButtonPressed2") {
        	brick.setAlpha(1);
        	buttonPressed2 = true;
            System.out.println("Button is being pressed2");
            button2.setDisplayImage("button_pressed.png");
            //Set position of the pressed button sprite a little bit lower so that it looks better
            button2.setyPos(753);
            
        }
        
        if(event.getEventType()=="ButtonPressed3") {
        	
        	buttonPressed3 = true;
            System.out.println("Button is being pressed3");
            button3.setDisplayImage("button_pressed.png");
            //Set position of the pressed button sprite a little bit lower so that it looks better
            button3.setyPos(100);
            
        }

        // Tween event
        if(event.getEventType()==TweenEvent.TWEEN_COMPLETE_EVENT) {
           coinTween.removeEventListener(this, TweenEvent.TWEEN_COMPLETE_EVENT);
           TweenTransitions transit = new TweenTransitions();
           Tween fadeTween = new Tween(coin,transit);
           fadeTween.animate(TweenableParams.alpha,1,0,2000);
           fadeTween.animate(TweenableParams.rotation,1,30,2000);
           juggler.add(fadeTween);
        }
        
        


        if(event.getEventType()=="collide") {
            System.out.println("Collision!");
            
            Rectangle inter = Mario.getHitBox().intersection(button.getHitBox());
            Rectangle inter1 = saveStateMario1.getHitBox().intersection(button.getHitBox());
            Rectangle inter2 = saveStateMario2.getHitBox().intersection(button.getHitBox());

            if(!inter.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter.getY()+inter.getHeight()>button.getyPos()
                    && inter.getWidth()>=inter.getHeight()+5) {
                	    Mario.setyPos(button.getyPos()-Mario.getHeight());
                        Mario.setOnGround(true);
                }

                //intersect from left, hitbox start from left of coin
                else {

                    if(inter.getX()==button.getxPos()) {
                        Mario.setxPos(button.getxPos()-Mario.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter.getX()+inter.getWidth()==button.getxPos()+button.getWidth()) {
                        Mario.setxPos(button.getxPos()+button.getWidth());
                    }
                    Mario.setOnGround(false);
                }


            }

        }


        if(event.getEventType()=="collide3" && (brick.getAlpha() == 1)) {
            System.out.println("Collision!");
           
            Rectangle inter = Mario.getHitBox().intersection(brick.getHitBox());
            Rectangle inter3 = Mario.getHitBox().intersection(saveStateMario1.getHitBox());
            Rectangle inter4 = Mario.getHitBox().intersection(saveStateMario2.getHitBox());
            if(!inter.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter.getY()+inter.getHeight()>=brick.getyPos()
                    && inter.getWidth()>=inter.getHeight()+5) {
                    if(inter.getY()+inter.getHeight()<=brick.getyPos()+(brick.getHeight()/2)) {
                    	Mario.setyPos(brick.getyPos()-Mario.getHeight());
                        Mario.setOnGround(true);
                    } else {
                        Mario.setV(0);;
                        Mario.setyPos(brick.getyPos()+brick.getHeight());
                    }
                }
                //intersect from left, hitbox start from left of coin
                else {

                    if(inter.getX()==brick.getxPos()) {
                        Mario.setxPos(brick.getxPos()-Mario.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter.getX()+inter.getWidth()==brick.getxPos()+brick.getWidth()) {
                        Mario.setxPos(brick.getxPos()+brick.getWidth());
                    }
                    Mario.setOnGround(false);
                }
            }
        }
        
        
        if(event.getEventType()=="collideWithTopBrick") {
            System.out.println("Collision!");

            Rectangle inter = Mario.getHitBox().intersection(brick3.getHitBox());
            Rectangle inter1 = saveStateMario1.getHitBox().intersection(brick3.getHitBox());
            Rectangle inter2 = saveStateMario2.getHitBox().intersection(brick3.getHitBox());

            if(!inter.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter.getY()+inter.getHeight()>brick3.getyPos()
                    && inter.getWidth()>=inter.getHeight()+5) {
                        Mario.setOnGround(true);
                }

                //intersect from left, hitbox start from left of coin
                else {

                    if(inter.getX()==brick3.getxPos()) {
                        Mario.setxPos(brick3.getxPos()-Mario.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter.getX()+inter.getWidth()==brick3.getxPos()+brick3.getWidth()) {
                        Mario.setxPos(brick3.getxPos()+brick3.getWidth());
                    }
                    Mario.setOnGround(false);
                }


            }

        }
        
        if(event.getEventType()=="hitShadow1") {
            System.out.println("Collision! with shado");
            Rectangle inter3 = Mario.getHitBox().intersection(saveStateMario1.getHitBox());
            if(!inter3.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter3.getY()+inter3.getHeight()>=saveStateMario1.getyPos()
                    && inter3.getWidth()>=inter3.getHeight()+5) {
                    if(inter3.getY()+inter3.getHeight()<=saveStateMario1.getyPos()+(saveStateMario1.getHeight()/2)) {
                    	 Mario.setyPos(saveStateMario1.getyPos()-Mario.getHeight());
                    	Mario.setOnGround(true);
                    } else {
                        Mario.setV(0);;
                        Mario.setyPos(saveStateMario1.getyPos()+saveStateMario1.getHeight());
                    }
                }
                //intersect from left, hitbox start from left of coin
                else {

                    if(inter3.getX()==saveStateMario1.getxPos()) {
                        Mario.setxPos(saveStateMario1.getxPos()-saveStateMario1.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter3.getX()+inter3.getWidth()==saveStateMario1.getxPos()+saveStateMario1.getWidth()) {
                        Mario.setxPos(saveStateMario1.getxPos()+saveStateMario1.getWidth());
                    }
                    Mario.setOnGround(false);
                }
            }
        }
        
        
        if(event.getEventType()=="hitShadow2") {
            System.out.println("Collision!");
            Rectangle inter4 = Mario.getHitBox().intersection(saveStateMario2.getHitBox());
            if(!inter4.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter4.getY()+inter4.getHeight()>=saveStateMario2.getyPos()
                    && inter4.getWidth()>=inter4.getHeight()+5) {
                    if(inter4.getY()+inter4.getHeight()<=saveStateMario2.getyPos()+(saveStateMario2.getHeight()/2)) {
                    	 Mario.setyPos(saveStateMario2.getyPos()-Mario.getHeight());
                    	Mario.setOnGround(true);
                    } else {
                        Mario.setV(0);;
                        Mario.setyPos(saveStateMario2.getyPos()+saveStateMario2.getHeight());
                    }
                }
                //intersect from left, hitbox start from left of coin
                else {

                    if(inter4.getX()==saveStateMario2.getxPos()) {
                        Mario.setxPos(saveStateMario2.getxPos()-saveStateMario2.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter4.getX()+inter4.getWidth()==saveStateMario2.getxPos()+saveStateMario2.getWidth()) {
                        Mario.setxPos(saveStateMario2.getxPos()+saveStateMario2.getWidth());
                    }
                    Mario.setOnGround(false);
                }
            }
        }
        
        if(event.getEventType()=="gateCollision") {
            System.out.println("Collision! with gate");

            Rectangle inter = Mario.getHitBox().intersection(gate.getHitBox());

            if(!inter.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter.getY()+inter.getHeight()>=gate.getyPos()
                    && inter.getWidth()>=inter.getHeight()+5) {
                    if(inter.getY()+inter.getHeight()<=gate.getyPos()+(gate.getHeight()/2)) {
                        Mario.setOnGround(false);
                    } else {
                        Mario.setV(0);;
                        Mario.setyPos(gate.getyPos()+gate.getHeight());
                    }
                }
                //intersect from left, hitbox start from left of coin
                else {

                    if(inter.getX()==gate.getxPos()) {
                        Mario.setxPos(gate.getxPos()-Mario.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter.getX()+inter.getWidth()==gate.getxPos()+gate.getWidth()) {
                        Mario.setxPos(gate.getxPos()+gate.getWidth());
                    }
                    Mario.setOnGround(false);
                }
            }
        }
        
        if(event.getEventType()=="collide2") {
            System.out.println("Collision!");

            Rectangle inter = Mario.getHitBox().intersection(brick2.getHitBox());

            if(!inter.isEmpty()) {

                //intesect from above, then bottom does not touch ground
                //moreover, edge case
                if(inter.getY()+inter.getHeight()>=brick2.getyPos()
                    && inter.getWidth()>=inter.getHeight()+5) {
                    if(inter.getY()+inter.getHeight()<=brick2.getyPos()+(brick2.getHeight()/2)) {
                    	 Mario.setyPos(brick2.getyPos()-Mario.getHeight());
                        Mario.setOnGround(true);
                    } else {
                        Mario.setV(0);;
                        Mario.setyPos(brick2.getyPos()+brick2.getHeight());
                    }
                }
                //intersect from left, hitbox start from left of coin
                else {

                    if(inter.getX()==brick2.getxPos()) {
                        Mario.setxPos(brick2.getxPos()-Mario.getWidth());
                    }

                    //intersect from right, hitbox start from right of coin
                    if(inter.getX()+inter.getWidth()==brick2.getxPos()+brick2.getWidth()) {
                        Mario.setxPos(brick2.getxPos()+brick2.getWidth());
                    }
                    Mario.setOnGround(false);
                }
            }
        }
    }
}