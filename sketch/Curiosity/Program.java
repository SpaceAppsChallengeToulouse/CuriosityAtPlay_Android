import java.util.ArrayList;
import processing.core.PApplet;

class RobotPoint
{
  float x, y, angle;
 
  RobotPoint(float x, float y, float angle) {
    this.x = x;
    this.y = y;
    this.angle = angle;
  } 
}

abstract class Order 
{
  abstract RobotPoint execute(RobotPoint pt);
  abstract RobotPoint previewRender(PApplet c, RobotPoint pt);
}

class MoveOrder extends Order
{
  public float distance;
  
  MoveOrder(float distance) { this.distance = distance; }
  
  RobotPoint execute(RobotPoint pt)
  {
     float newX = pt.x + distance * (float) Math.cos(pt.angle * Math.PI / 180);
     float newY = pt.y + distance * (float) Math.sin(pt.angle * Math.PI / 180); 
     return new RobotPoint(newX, newY, pt.angle); 
  } 
  
  RobotPoint previewRender(PApplet c, RobotPoint pt)
  {
    RobotPoint newPt = execute(pt);
    c.stroke(220, 220, 220, 150);
    c.strokeWeight(4.5f);
    c.line(pt.x * Curiosity.SCALE, 
           pt.y * Curiosity.SCALE, 
           newPt.x * Curiosity.SCALE, 
           newPt.y * Curiosity.SCALE);
    return newPt;
    //return excute();
  }
}

class ActionOrder extends Order
{
  public Action action;
  
  ActionOrder(Action action) { this.action = action; }
  
  RobotPoint execute(RobotPoint pt)
  {
     return pt;
  } 
  
  RobotPoint previewRender(PApplet c, RobotPoint pt)
  {
    c.pushMatrix();
    c.translate(pt.x * Curiosity.SCALE, pt.y * Curiosity.SCALE);
    
    int index = 0; 
    switch (action) {
       case DIG:
         index = 2;
         break;
       case LASER:
         index = 1;
         break;
       case CAM:
         index = 0;
         break; 
    }
     
    c.scale(1, -1);
    c.ellipse(0, 0, 10, 10);
    c.image(Curiosity.actions[index], 0, 0, 40, 40);
    c.popMatrix();
    return execute(pt);
    //return excute();
  }
}

class RotateOrder extends Order
{
   public float angle;
   RotateOrder(float angle) { this.angle = angle; } 
   
   RobotPoint execute(RobotPoint pt)
   {
     return new RobotPoint(pt.x, pt.y, pt.angle + angle); 
   }
  
  RobotPoint previewRender(PApplet c, RobotPoint pt)
  {
     RobotPoint newPt = execute(pt);
     c.noStroke();
     c.pushMatrix();
     c.translate(pt.x * Curiosity.SCALE, pt.y * Curiosity.SCALE);
     c.rotate((newPt.angle) * (float) Math.PI / 180); 
     c.ellipse(0, 0, 20, 20);
     c.image(Curiosity.arrow, -20,-20,40,40);
     
     c.popMatrix(); 
     return newPt;
  } 
}


class Program 
{
  ArrayList<Order> orders;
  
  Program()
  {
    orders = new ArrayList<Order>(); 
  }
  
  public void preview(PApplet c, RobotPoint robot)
  {  
      for (Order order : orders)
      {
        robot = order.previewRender(c, robot); 
      }
  }
 
  
 
 public static Program getSample()
 {
    Program p = new Program();
    p.orders.add(new MoveOrder(15));
    p.orders.add(new RotateOrder(45));
    p.orders.add(new MoveOrder(20));
    p.orders.add(new RotateOrder(20));
    p.orders.add(new MoveOrder(5));
    p.orders.add(new ActionOrder(Action.DIG));
    return p;
 } 
 
 public static Program parse(String s)
 {
   String[] parts = s.split(";");
   Program p = new Program();
   for (String part : parts)
   {
       if (part.charAt(0) == 'm')
       {
          p.orders.add(new MoveOrder(Float.parseFloat(part.substring(1)))); 
       }
       if (part.charAt(0) == 'r')
       {
          p.orders.add(new RotateOrder(Float.parseFloat(part.substring(1)))); 
       }
       if (part.charAt(0) == 'a')
       {
          p.orders.add(new ActionOrder(Action.valueOf(part.substring(1)))); 
       }
   }
   return p;
 }
}
