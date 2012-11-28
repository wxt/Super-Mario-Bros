package smb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import jig.engine.audio.jsound.AudioClip;
import jig.engine.audio.jsound.AudioStream;
import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.ViewableLayer;
import jig.engine.hli.AbstractSimpleGame;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.hli.StaticScreenGame;
import jig.engine.hli.physics.RectangleCollisionHandler;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;

@SuppressWarnings("unused")
public class Smb extends ScrollingScreenGame {

	static final int WORLD_WIDTH = 512;

	static final int WORLD_HEIGHT = 448;
	static final double gravity = 20;
	static double currentCenter;
	static final int TILE_SIZE = 32;
	static final int HALF_SCREEN_WIDTH = WORLD_WIDTH / 2;
	static final int HALF_SCREEN_HEIGHT = WORLD_HEIGHT / 2;
	int worldPixelLenght;
	int worldPixelHeight;
	static final String SPRITE_SHEET = "resources/mario-spritesheet.png";
	static final String SPRITE_SHEET2 = "resources/smb_mario_sheet.png";
	static final double deltaTime = 0.0001;
	static int mapWidth, mapHeight;

	Player p;
	int points;
	int coinNum;
	int world;
	int world_level;
	int live;
	long time;
	long currentTime;
	// private ViewableLayer splashLayer;
	int leftWidthBreakPoint, rightWidthBreakPoint;
	FontResource scoreboardFont;
	FontResource powerUpsFont;
	String maptext;
	// public List<walls> wallarray = new ArrayList<walls>();
	// public List<goomba> goombaarray = new ArrayList<goomba>();
	
	long jumpTimer;

	// static double offset;
	private VanillaPhysicsEngine physics;
	static final SpriteUpdateRules UPDATE_RULE = new SpriteUpdateRules(WORLD_WIDTH, WORLD_HEIGHT);
	private AudioStream music;
	public BodyLayer<VanillaAARectangle> unmovableLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public BodyLayer<VanillaAARectangle> movableLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public static BodyLayer<VanillaAARectangle> backGroundLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
    public static BodyLayer<VanillaAARectangle> powerUpLayer = new AbstractBodyLayer.NoUpdate<VanillaAARectangle>();

	// public AbstractBodyLayer<VanillaAARectangle> playerLayer = new
	// AbstractBodyLayer.NoUpdate<VanillaAARectangle>();
	public Smb() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new VanillaPhysicsEngine();
		scoreboardFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 15), Color.WHITE, null);
		ResourceFactory.getFactory().loadResources("resources/", "mario-resources.xml");
		
		gameObjectLayers.add(backGroundLayer);
		physics.manageViewableSet(backGroundLayer);
		gameObjectLayers.add(powerUpLayer);
		physics.manageViewableSet(powerUpLayer);
		gameObjectLayers.add(unmovableLayer);
		physics.manageViewableSet(unmovableLayer);
		gameObjectLayers.add(movableLayer);
		physics.manageViewableSet(movableLayer);
		
		/* Justin: Commenting out to test changed names
		RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle> d = new RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle>(movableLayer, unmovableLayer) {
			@Override
			public void collide(final VanillaAARectangle a, final VanillaAARectangle b) {
				if (a.type != 4) {
					if (a.type == 5) {
						if ((a.getPosition().getY() + a.getHeight()) > b.getPosition().getY()&& (a.getPosition().getY() + a.getHeight()) < (b.getPosition().getY() + b.getHeight())) {
							((goomba) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - 0.5));
							((goomba) a).vSpeedY = 0;
						} else if (a.getPosition().getY() < b.getPosition().getY() + b.getHeight()&& a.getPosition().getY() > b.getPosition().getY()) {
							((goomba) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + 0.5));
							((goomba) a).vSpeedY = -((goomba) a).vSpeedY;
						}
						if (a.getPosition().getX() + a.getWidth() > b.getPosition().getX()&& a.getPosition().getX() + a.getWidth() < b.getPosition().getX()) {
							((goomba) a).setPosition(new Vector2D(a.getPosition().getX() - 0.5, a.getPosition().getY()));
							((goomba) a).vSpeedX = 0;
							((goomba) a).setOppositeDirection();

							
							
						} else if (a.getPosition().getX() < b.getPosition().getX() + b.getWidth()&& a.getPosition().getX() > b.getPosition().getX()) {
							((goomba) a).setPosition(new Vector2D(a.getPosition().getX() + 0.5, a.getPosition().getY()));
							((goomba) a).vSpeedX = 0;
							((goomba) a).setOppositeDirection();

						}
					}
				} else {
					if ((a.getPosition().getY() + a.getHeight()) > b.getPosition().getY() && (a.getPosition().getY() + a.getHeight()) < (b.getPosition().getY() + b.getHeight())) {
						((player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - 0.5));
						((player) a).vSpeedY = 0;
					} else if (a.getPosition().getY() < b.getPosition().getY() + b.getHeight() && a.getPosition().getY() > b.getPosition().getY()) {
						((player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + 0.2));
						((player) a).vSpeedY = -((player) a).vSpeedY;
						switch(b.type){
						case 1:
							((player) a).vSpeedY=-((player) a).vSpeedY;
							((breakableBrownWall)b).breakApart();
							break;
						case 11:
							if(!((questionBlock)b).dead){
								((player) a).vSpeedY=-((player) a).vSpeedY;
								if(p.level==1){
									
								}
								else{
									powerUpLayer.add(new powerUpFlower(b.getPosition().getX(), b.getPosition().getY()));
									((questionBlock)b).setDead();
							}
							}
							break;
						
						}
					}
					if (a.getPosition().getX() + a.getWidth() > b.getPosition().getX() && a.getPosition().getX() + a.getWidth() < b.getPosition().getX()) {
						((player) a).setPosition(new Vector2D(a.getPosition().getX() - 0.5, a.getPosition().getY()));
						((player) a).vSpeedX = 0;
					} else if (a.getPosition().getX() < b.getPosition().getX() + b.getWidth() && a.getPosition().getX() > b.getPosition().getX()) {
						((player) a).setPosition(new Vector2D(a.getPosition().getX() + 0.5, a.getPosition().getY()));
						((player) a).vSpeedX = 0;
					}
				}
			}
		};
		*/
		
		RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle> d = new RectangleCollisionHandler<VanillaAARectangle, VanillaAARectangle>(movableLayer, unmovableLayer) {
			@Override
			public void collide(final VanillaAARectangle a, final VanillaAARectangle b) {
				if (a.type != 4) {
					if (a.type == 5) {
						if ((a.getPosition().getY() + a.getHeight()) > b.getPosition().getY()&& (a.getPosition().getY() + a.getHeight()) < (b.getPosition().getY() + b.getHeight())) {
							((Goomba) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - 0.5));
							((Goomba) a).vSpeedY = 0;
						} else if (a.getPosition().getY() < b.getPosition().getY() + b.getHeight()&& a.getPosition().getY() > b.getPosition().getY()) {
							((Goomba) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + 0.5));
							((Goomba) a).vSpeedY = -((Goomba) a).vSpeedY;
						}
						if (a.getPosition().getX() + a.getWidth() > b.getPosition().getX()&& a.getPosition().getX() + a.getWidth() < b.getPosition().getX()) {
							((Goomba) a).setPosition(new Vector2D(a.getPosition().getX() - 0.5, a.getPosition().getY()));
							((Goomba) a).vSpeedX = 0;
							((Goomba) a).setOppositeDirection();

							
							
						} else if (a.getPosition().getX() < b.getPosition().getX() + b.getWidth()&& a.getPosition().getX() > b.getPosition().getX()) {
							((Goomba) a).setPosition(new Vector2D(a.getPosition().getX() + 0.5, a.getPosition().getY()));
							((Goomba) a).vSpeedX = 0;
							((Goomba) a).setOppositeDirection();

						}
					}
				} else {
					if ((a.getPosition().getY() + a.getHeight()) > b.getPosition().getY() && (a.getPosition().getY() + a.getHeight()) < (b.getPosition().getY() + b.getHeight())) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() - 0.5));
						((Player) a).playerYvel = 0;
						((Player) a).playerYacc = 0; // causes problems, if player walks off of block gravity doesn't start back up
						((Player) a).jumped = false;
					} else if (a.getPosition().getY() < b.getPosition().getY() + b.getHeight() && a.getPosition().getY() > b.getPosition().getY()) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX(), a.getPosition().getY() + 0.2));
						((Player) a).playerYvel = -((Player) a).playerYvel;
						switch(b.type){
						case 1:
							((Player) a).playerYvel=-((Player) a).playerYvel;
							((BreakableBrownWall)b).breakApart();
							break;
						case 11:
							if(!((QuestionBlock)b).dead){
								((Player) a).playerYvel=-((Player) a).playerYvel;
								if(p.level==1){
									
								}
								else{
									powerUpLayer.add(new PowerUpFlower(b.getPosition().getX(), b.getPosition().getY()));
									((QuestionBlock)b).setDead();
							}
							}
							break;
						
						}
					}
					if (a.getPosition().getX() + a.getWidth() > b.getPosition().getX() && a.getPosition().getX() + a.getWidth() < b.getPosition().getX()) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX() - 0.5, a.getPosition().getY()));
						//((player) a).playerXvel = 0;
						//((player) a).playerXacc = 0;
					} else if (a.getPosition().getX() < b.getPosition().getX() + b.getWidth() && a.getPosition().getX() > b.getPosition().getX()) {
						((Player) a).setPosition(new Vector2D(a.getPosition().getX() + 0.5, a.getPosition().getY()));
						//((player) a).playerXvel = 0;
						//((player) a).playerXacc = 0;
					}
				}
			}
		};

		physics.registerCollisionHandler(d);
		loadLevel("1");
		setWorldBounds(0, 0, mapWidth * TILE_SIZE, mapHeight * TILE_SIZE);
	}

	private void loadLevel(String level) {
		time = 300;
		currentTime = System.currentTimeMillis();
		world = 1;
		world_level = Integer.valueOf(level);
		String ud = System.getProperty("user.dir");
		try {
			String line;
			FileInputStream fstream = new FileInputStream("src/resources/map" + level + ".txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			line = br.readLine();
			mapWidth = Integer.parseInt(line);
			// this.worldPixelLenght = mapWidth * TILE_SIZE;
			line = br.readLine();
			mapHeight = Integer.parseInt(line);
			worldPixelHeight = mapHeight * TILE_SIZE;
			worldPixelLenght = mapWidth * TILE_SIZE;
			leftWidthBreakPoint = HALF_SCREEN_WIDTH;
			rightWidthBreakPoint = worldPixelLenght - HALF_SCREEN_WIDTH;

			// map = new gamemap(mapWidth, mapHeight);
			int y = 0;
			while ((line = br.readLine()) != null) {

				buildMap(line, y);
				y++;

			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/*
	 * type, map character, name
	 * 1  a breakablebrownwall
	 * 2  b unbreakablewall
	 * 3  c groundwall
	 * 4  d mario
	 * 5  e goomba
	 * 6  f flagpole
	 * 7  g turtle
	 * 8  h verticalpipe
	 * 9  i verticalhalfpipe
	 * 10 j castle
	 * 11 k questionblock
	 * 12 l smallcloud
	 * 13 m bigcloud
	 * 14 n smallcloud
	 * 15 0 bighill
	 * 16 p shrub
	 * 17 q green goomba
	 * 18 r green turtle
	 * 19 s green step
	 * 20 w green breakable wall
	 * 21   flower
	 */
	private void buildMap(String s, int y) throws InterruptedException {
		try {
			String line = s;
			for (int x = 0; x < mapWidth; x++) {

				char ch = line.charAt(x);
				if (ch == 'a') {
					unmovableLayer.add(new BreakableBrownWall(x, y));
				} else if (ch == 'b') {
					unmovableLayer.add(new UnbreakableWall(x, y,"#unbreakableWall"));
				} else if (ch == 'c') {
					unmovableLayer.add(new GroundWall(x, y));
				} else if (ch == 'd') {
					p = new Player(x, y);
					movableLayer.add(p);
				} else if (ch == 'e') {
					movableLayer.add(new Goomba(x, y,"#Goomba"));
				} else if (ch == 'f') {
					unmovableLayer.add(new FlagPole(x, y));
				} else if (ch == 'g') {
					movableLayer.add(new Turtle(x, y,"#turtle"));
				} else if (ch == 'h') {
					unmovableLayer.add(new VerticalPipe(x, y));
				} else if (ch == 'i') {
					unmovableLayer.add(new VerticalHalfPipe(x, y));
				} else if (ch == 'j') {
					unmovableLayer.add(new Castle(x, y));
				} else if (ch == 'k') {
					unmovableLayer.add(new QuestionBlock(x, y));
				} else if (ch == 'l') {
					backGroundLayer.add(new SmallCloud(x,y));
				} else if (ch == 'm') {
					backGroundLayer.add(new BigCloud(x,y));
				} else if (ch == 'n') {
					backGroundLayer.add(new SmallHill(x,y));
				} else if (ch == 'o') {
					backGroundLayer.add(new BigHill(x,y));
				} else if (ch == 'p') {
					backGroundLayer.add(new Shrub(x,y));
				} else if (ch == 'q') {
					movableLayer.add(new Goomba(x, y,"#greengoomba"));
				} else if (ch == 'r') {
					movableLayer.add(new Turtle(x, y,"#greenturtle"));
				} else if (ch == 's') {
					unmovableLayer.add(new UnbreakableWall(x, y,"greenUnbreakableWall"));
				} else if (ch == 't') {

				} else if (ch == 'u') {

				} else if (ch == 'v') {

				} else if (ch == 'w') {
					unmovableLayer.add(new BreakableGreenWall(x,y));
				} else if (ch == 'x') {

				} else if (ch == 'y') {

				} else if (ch == 'z') {

				}

			}
		} catch (Exception e) {
			return;
		}
	}

	public void render(RenderingContext rc) {
		super.render(rc);
		scoreboardFont.render("MARIO x" + live, rc, AffineTransform.getTranslateInstance(40, 20));
		scoreboardFont.render("WORLD", rc, AffineTransform.getTranslateInstance(300, 20));
		scoreboardFont.render("TIME", rc, AffineTransform.getTranslateInstance(430, 20));
		//scoreboardFont.render("LIVE", rc, AffineTransform.getTranslateInstance(230, 20));
		//scoreboardFont.render(live + "", rc, AffineTransform.getTranslateInstance(233, 40));
		scoreboardFont.render(points + "", rc, AffineTransform.getTranslateInstance(50, 40));
		scoreboardFont.render(world + "" + "-" + world_level, rc, AffineTransform.getTranslateInstance(310, 40));
		scoreboardFont.render((int) time + "", rc, AffineTransform.getTranslateInstance(440, 40));
	}

	@Override
	public void update(long deltaMs) {
		/*
		 * time-=((System.currentTimeMillis()-currentTime)/1000.0);
		 * currentTime=System.currentTimeMillis();
		 */
		
		jumpTimer += deltaMs;
		
		if (p.getPosition().getX() < leftWidthBreakPoint) {
			centerOnPoint(leftWidthBreakPoint, HALF_SCREEN_HEIGHT);
		} else if (p.getPosition().getX() > rightWidthBreakPoint) {
			centerOnPoint(rightWidthBreakPoint, HALF_SCREEN_HEIGHT);
		} else {
			centerOnPoint((int) p.getPosition().getX(), HALF_SCREEN_HEIGHT);
		}

		keyboard.poll();
		boolean left = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean right = keyboard.isPressed(KeyEvent.VK_RIGHT);
		boolean up = keyboard.isPressed(KeyEvent.VK_UP);
		boolean down = keyboard.isPressed(KeyEvent.VK_DOWN);
		boolean space = keyboard.isPressed(KeyEvent.VK_SPACE);
		boolean r = keyboard.isPressed(KeyEvent.VK_R);
		boolean run = keyboard.isPressed(KeyEvent.VK_SHIFT);

		if (left && !right) {
			//this.p.Xdirection = 3;
			if(this.p.MARIO) {
				if(!run){
					this.p.playerXacc = -Physics.mg_acceler_walk;
					this.p.maxXvel = -Physics.mg_max_vel_walk;
				} else {
					this.p.playerXacc = -Physics.mg_acceler_runn;
					this.p.maxXvel = -Physics.mg_max_vel_runn;
				}
			} else {
				if(!run){
					this.p.playerXacc = -Physics.lg_acceler_walk;
					this.p.maxXvel = -Physics.lg_max_vel_walk;
				} else {
					this.p.playerXacc = -Physics.lg_acceler_runn;
					this.p.maxXvel = -Physics.lg_max_vel_runn;
				}
			}
		} else if (right && !left) {
			//this.p.Xdirection = 1;
			if(this.p.MARIO) {
				if(!run){
					this.p.playerXacc = Physics.mg_acceler_walk;
					this.p.maxXvel = Physics.mg_max_vel_walk;
				} else {
					this.p.playerXacc = Physics.mg_acceler_runn;
					this.p.maxXvel = Physics.mg_max_vel_runn;
				}
			} else {
				if(!run){
					this.p.playerXacc = Physics.lg_acceler_walk;
					this.p.maxXvel = Physics.lg_max_vel_walk;
				} else {
					this.p.playerXacc = Physics.lg_acceler_runn;
					this.p.maxXvel = Physics.lg_max_vel_runn;
				}
			}
		} else {
			//this.p.Xdirection = 0;
			if(this.p.playerXvel < 0) {
				if(this.p.MARIO) {
					this.p.playerXacc = Physics.mg_deceler_rele;
				} else {
					this.p.playerXacc = Physics.lg_deceler_rele;
				}
			} else if(p.playerXvel > 0) {
				if(this.p.MARIO) {
					this.p.playerXacc = -Physics.lg_deceler_rele;
				} else {
					this.p.playerXacc = -Physics.lg_deceler_rele;
				}
			}
		}

		if (space) {
			//this.p.jumped = true;
			if(jumpTimer > Physics.keyPoll){
				if(Math.abs(this.p.playerXvel) < Physics.ba_max_air_lt) {
					if(p.playerXvel < 0) {
						p.maxXvel = -Physics.ba_max_air_lt;
					} else {
						p.maxXvel = Physics.ba_max_air_gt;
					}
				}
				if(this.p.MARIO){
					if(Math.abs(this.p.playerXvel) < Physics.lt_jump) {
						System.out.println("jump");
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.mj_lt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.mj_lt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.mj_lt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.mj_lt_fall_gra;
					} else if(Math.abs(this.p.playerXvel) < Physics.gt_jump) {
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.mj_bt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.mj_bt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.mj_bt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.mj_bt_fall_gra;
					} else {
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.mj_gt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.mj_gt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.mj_gt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.mj_gt_fall_gra;
					}
				} else {
					if(Math.abs(this.p.playerXvel) < Physics.lt_jump) {
						System.out.println("jump");
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.lj_lt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.lj_lt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.lj_lt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.lj_lt_fall_gra;
					} else if(Math.abs(this.p.playerXvel) < Physics.gt_jump) {
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.lj_bt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.lj_bt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.lj_bt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.lj_bt_fall_gra;
					} else {
						if(this.p.playerYvel == 0) this.p.playerYvel = -Physics.lj_gt_init_vel;
						if(!this.p.jumped) this.p.playerYacc = Physics.lj_gt_fall_gra;
						if(this.p.jumped) this.p.playerYacc = Physics.lj_gt_hold_gra;
						this.p.jumped = true;
						this.p.gravity = Physics.lj_gt_fall_gra;
					}
				}
				jumpTimer = 0;
			}
		}
		
		/* collision between mario and interactable objects */
		for(int i=0; i<movableLayer.size();i++){
			if(movableLayer.get(i).isActive() && movableLayer.get(i).type!=4 && movableLayer.get(i).getBoundingBox().intersects(p.getBoundingBox())){
				//System.out.println("colliding");
				double playerFoot = p.getPosition().getY()+ p.getHeight();
				double enemyHead = movableLayer.get(i).getPosition().getY();
				switch(movableLayer.get(i).type){
				case 5:
					if(((Goomba)movableLayer.get(i)).dead){
						continue;
					}
					if(playerFoot > enemyHead && playerFoot < enemyHead+2){
						((Goomba)movableLayer.get(i)).setDead();
						p.jumped=true;
					}
					else{
						//player loose a life
					}
				break;
				case 7:
					
					break;

				}
			}
		}
		
		/* collision between mario and interactable objects */
		for(int i=0; i<powerUpLayer.size();i++){
			if(powerUpLayer.get(i).isActive() && powerUpLayer.get(i).type!=4 && powerUpLayer.get(i).getBoundingBox().intersects(p.getBoundingBox())){
				switch(powerUpLayer.get(i).type){
				case 17:
					((PowerUpFlower)powerUpLayer.get(i)).setDead();
					break;
				}
			}
		}

		physics.applyLawsOfPhysics(deltaMs);

	}

	public static void main(String[] args) {

		Smb p = new Smb();
		p.run();

	}

}