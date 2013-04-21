import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;


static final int W = 1024;
static final int H = 768;

static final float SCALE = 800 / 50;

World world;

static PApplet app;
static PImage arrow;
static PImage[] actions;

void setup()
{
  app = this;
  arrow = loadImage("arrow.png");
  actions = new PImage[3];
  actions[0] = loadImage("camera.png");
  actions[1] = loadImage("icon_laser.png");
  strokeCap(ROUND);
  
  size(W, H);
  world = new World();
  smooth();
   
  PFont font = loadFont("LetsgoDigital-Regular-48.vlw");
  textFont(font, 32);

  Server server = new Server();

  server.addListener(new Listener() {
     public void received (Connection connection, Object object) {
        //System.out.println("hello " + object);
       
       if (object instanceof String)
       {
          String msg = (String) object;
          System.out.println(msg);
          Program p = Program.parse(msg);
          world.program = p;  
       }  
   }
     
     public void connected(Connection connection) {
        //System.out.println("hello " + connection); 
     }
     
     public void disconnected(Connection connection) {
        //System.out.println("disconnected " + connection); 
     }
  });
  server.start();
  try
  {
    server.bind(8001, 8002);  
  } catch (Exception e) { }
   
}

void draw()
{
   world.render();
}

abstract class Drawable
{
  public float  x, y;
  abstract void render(); 
}

class World extends Drawable
{
  PImage bgImg;
  
  Robot robot;
  ArrayList<InterestPoint> interestPoints;
  
  Program program = Program.getSample();
  
  Countdown count;
  
  World()
  {
     robot = new Robot(); 
     interestPoints = new ArrayList<InterestPoint>();
     bgImg = loadImage("background.png");
     count = new Countdown();
  }
  
  void render()
  {
     background(0);
     fill(255);
     image(bgImg, 0, 0, W, H);
     
     translate(0, H);
     scale(1, -1);
     
     //scale(10);
     program.preview(app, new RobotPoint(robot.x, robot.y, robot.angle));
     robot.render();
     for (InterestPoint p : interestPoints)
     {
       p.render(); 
     }
     
     count.render();
  }
}

class Robot extends Drawable
{
   public float angle;
   PImage img;
   
   ProgramExecutor executor;
   
   Robot()
   {
     img = loadImage("model_curiosity.jpg");  
     x = 5;
     y = 5; 
   }
   
   public void render()
   {
     ellipse(x*SCALE, y*SCALE, 50f, 50f);
     pushMatrix();
     scale(1, -1);
     image(img, x*SCALE - 15, -y*SCALE-15, 30f, 30f);
     popMatrix();
     
     if (executor != null)
     {
       if (!executor.next())
          executor = null; 
     }
   }
   
   public void execute(Program p)
   {
      executor = new ProgramExecutor(p, this);  
   }
}

class ProgramExecutor
{ 
   Program p;
   Robot r;
   int index;
   
   ProgramExecutor(Program p, Robot r)
   {
     this.p = p;
     this.r = r;
   }
   
   boolean next()
   {
      
      Order o = p.orders.get(index);
      RobotPoint pt = new RobotPoint(r.x, r.y, r.angle);
      
      return false;
   }
}

class InterestPoint extends Drawable
{
    String name;
    String description;
    Action action;
    Job forJob;
    
    public void render()
    {
       
    }
}

class Countdown extends Drawable 
{
   long time;
   Countdown()
   {
     time = System.currentTimeMillis() + 5 * 60 * 1000L;
   }
   
   public void render()
   {
     scale(1, -1);
     long remaining = (time - System.currentTimeMillis()) / 1000;
     int minutes = (int) (remaining / 60);
     int seconds = (int) ((remaining % 60));
     String s = String.format("Mission starting in %02d:%02d", minutes, seconds);
     textSize(32);
     noStroke();
     fill(150, 150, 150, 120);
     rect(0, -H + 70, 500, -50);
     fill(200,0,0,220);
     text(s, 10, -H + 50);
   }
}
