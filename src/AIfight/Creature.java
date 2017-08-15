package AIfight;

import java.awt.Color;
import java.util.LinkedList;
//import java.util.concurrent.*;

public class Creature{
   private String SECURE_KEY = "password";   //wont run unless this matched the one in mainFrm
   //prevents any instances of the Mind class from changing key parts of the creature
   
   private int xPos;
   private int yPos;
   
   private Color color;
   private int health;
   private int food;
   
   private boolean isDead;
   
   private String species;
   
   //The 8 creature stats. They must all add up to 800. If they dont, and exception will occur. minimum values of 0, max of 200 each
   private final int MAX_HEALTH; //increases the upper limit for health
   private final int MAX_FOOD;   //increases the upper limit for food
   private final int ATTACK;     //increases effectivness of attack actions. increases attack efficiency somewhat
   private final int DEFENCE;    //reduces incoming damage per action
   private final int SPEED;      //reduces costs of movement
   private final int EAT;        //increases resources gained from eating
   private final int SENSE;      //increases the creature's sensory distance
                                 //maximum sensory distance = 7 + (senses-100)/10
                                 //sensory activity is determined with either
                                 //A = -(D-S)^3 * 100/(S^3) or
                                 //A = -D*(100/S)+100  depending on whether sensor type is set to linear or nonlinear
                                 //A = activity (0-100 min/max)
                                 //D = distance = sqrt(dY^2 + dX^2)         (+(stealth-100)/10 depending on the stat) 
                                 //S = maximum sensor distance
   private final int STEALTH;    //passive stat, determines how easy the creature is to hear/smell to other creatures
   
   private int qRawIncomingDmg;  //the ammount of queued incoming damage to be applied in the resolution phase
   private int qDefenceBuff;     //the percentage based damage reduction buff applied this tick
   private int qSenseBuff;       //the percentage based sensory range buff applied this tick
   private int qXchange;         //the queued change in the creature's x cordinate
   private int qYchange;         //the queued change in the creature's y cordinate
   private int qFoodChange;      //the ammoun of food the creatue has eaten this phanse, applied at resolution
   
   //ranged imprecise senses
      
   private World myWorld;              //referance to the world the creature inhabits
   
   private LinkedList<Action> actionQueue;
 
   private String id;                  //Any type of unique identifier for the creature

   private MindTemplate mind;          //the reference to the player-made mind object the creature uses for decision making
   
   private SensorSuite sensorSuite;
      
   //=============================================================================Constructors
   public Creature(MindTemplate mind, World myWorld, String id, int xPos, int yPos){
      this.mind = mind;
      this.myWorld = myWorld;
      
      this.species = mind.getSpecies();
      if(this.species == null)
         this.species = mind.species; //get the species value from the interface if they havent provided one
      
      this.sensorSuite = new SensorSuite();
      
      int[] stats = mind.getStats();
      MAX_HEALTH = stats[0];
      MAX_FOOD = stats[1];
      ATTACK = stats[2];
      DEFENCE = stats[3];
      SPEED = stats[4];
      EAT = stats[5];
      SENSE = stats[6];
      STEALTH = stats[7];
      
      this.color = mind.getColor();
            
      this.health = MAX_HEALTH;

      if(MAX_FOOD < 100)
         this.food = MAX_FOOD;
      else
         this.food = 100;
      
      this.actionQueue = new LinkedList<>();
      
      this.xPos = xPos;
      this.yPos = yPos;
   
      this.isDead = false;
   }

   //=============================================================================Utilities
   //-----------------------------------------------------------------addAction
   public int addAction(Action toDo){  //if there is enough food for the task, returns the number of actions in queue
                                       //else it will return -1
      int foodCost = toDo.baseCost;

      if (toDo.isMovement())
         foodCost = foodCost * (200-SPEED)/100;
      else if (toDo.isAttack())
         foodCost = foodCost * (200-ATTACK)/100;
      else if (toDo.isDefence())
         foodCost = foodCost * (200-DEFENCE)/100; 
      else if (toDo.isEat())
         foodCost = foodCost * (200-EAT)/100;
      else if (toDo.isSense())
         foodCost = foodCost * (200-SENSE)/100;
      
      foodCost = foodCost * actionQueue.size();
      
      if(food - foodCost < 0)
         return -1;
      else{
         food -= foodCost;
         actionQueue.addLast(toDo);
         return actionQueue.size();
      }  
   }
   
   
   //returns true if creature's action queue is empty either before or after method exicution
   //-----------------------------------------------------------------nextAction
   public boolean nextAction(String psk)throws CheaterException{
      if(!psk.equals(SECURE_KEY))
         throw new CheaterException();
      
      if(this.actionQueue.isEmpty())
         return true;
      Action toDo = actionQueue.removeLast();
      
      this.qXchange += toDo.xChange;
      this.qYchange += toDo.yChange;
      
      this.qDefenceBuff += (int)(toDo.defence * (this.DEFENCE/100.0));
      
      this.qSenseBuff += (int)(toDo.sense * (this.SENSE/100.0));
      
      //eww, fix this. add the ability to store x and y modifiers in actions so you can loop through all the affected areas
      if(toDo.isEat()){
         int eatAmmt;
         int maxEatAmmt = (int)(toDo.eat * (this.EAT/100.0));
         
         for(CordModifier cm : toDo.aoe.locations){
            eatAmmt = this.myWorld.eatSquare(this.xPos + cm.xMod, this.yPos + cm.yMod, maxEatAmmt ,SECURE_KEY);
            this.qFoodChange += (eatAmmt * (this.EAT/100.0));
            if(toDo.singleTarget && eatAmmt != 0)
               continue;
         }
      }
      if(toDo.isAttack()){
         for(CordModifier cm : toDo.aoe.locations){
            boolean didDamage = this.myWorld.damageSquare(this.xPos + cm.xMod, this.yPos + cm.yMod, (int)(toDo.attack * (this.ATTACK/100.0)),SECURE_KEY);
            if(didDamage && toDo.singleTarget)
               continue;
         }
      }
      
      return this.actionQueue.isEmpty();
   }
   
   //-----------------------------------------------------------------resolve
   public void resolve(String key) throws CheaterException{
      if(!key.equals(SECURE_KEY))
         throw new CheaterException();
         
      //max defencive buff of 90%
      if(this.qDefenceBuff > 90)
         this.qDefenceBuff = 90;
      
      //take the incoming damage, muiltiply the reduction percent. 
      //have a buff of 40? that means you take 60% of qRawIncomingDmg
      this.health -= this.qRawIncomingDmg *((100-this.qDefenceBuff)/100.0);
      
      if(this.myWorld.moveCreatureAt(this.xPos, this.yPos, this.qXchange, this.qYchange, SECURE_KEY)){
         this.xPos =this.myWorld.fc(this.xPos + qXchange);
         this.yPos =this.myWorld.fc(this.yPos + qYchange);
      }
      
      this.qRawIncomingDmg = 0;
      this.qDefenceBuff = (int)(this.qDefenceBuff*0.75);
      
      this.qXchange = 0;
      this.qYchange = 0;
      
      int worldSize = this.myWorld.WORLD_SIZE;      
      
      if(this.health <= 0){
         this.myWorld.killCreature(this.xPos,this.yPos);
      }
   }
   
   //-----------------------------------------------------------------tick
   public void tick(){
      if(!this.isDead){
         try{
            mind.tick();
         }
         //reenable this once we are up and rolling
         //catch(CheaterException e){
         //   System.out.println("Creature " + this.id + " tried to cheat"); 
         //}
         catch(Exception e){
            System.out.println("Creature " + this.id + " encountered a problem and ended exicution prematurly" + e);
         }
      }
   }
   
   //-----------------------------------------------------------------incIncomingDmg
   public void incIncomingDmg(int ammt, String key)throws CheaterException{
      if(!key.equals(SECURE_KEY))
         throw new CheaterException();
         
      this.qRawIncomingDmg += ammt; 
   }
   //=============================================================================Gets/Sets
   public Color getColor()    {return this.mind.getColor();} //lets you change colors...
   
   public int getHealth()     {return this.health;}
   public int getMaxHealth()  {return this.MAX_HEALTH;}
   public int getStealth()    {return this.STEALTH;} 
   public int getSense()      {return this.SENSE;}
   public int getSenseBuff()  {return this.qSenseBuff;}
   public int getFood()       {return this.food;}
   public int getMaxFood()    {return this.MAX_FOOD;}
   public int getDefence()    {return this.DEFENCE;}
   
   public int getXpos()       {return this.xPos;}
   public int getYpos()       {return this.yPos;}  

   public String getId()      {return this.id;}
   
   public boolean getIsDead() {return this.isDead;}
   
   public void setIsDead(boolean state,String key) throws CheaterException {
      if(!key.equals(this.SECURE_KEY))
         throw new CheaterException();
      this.isDead = state;
   }
   
   public SensorSuite getSensorSuite(){return this.sensorSuite;} 
   
   public String getSpecies(){return this.species;}
   
   public boolean setHealth(int health, String securityCode){
      if (securityCode.equals(SECURE_KEY)){
         this.health = health;
         if(this.health > this.MAX_HEALTH)
            this.health = MAX_HEALTH;
         if(this.health < 0)
            this.health = 0;
         return true;
      }
         return false;
   }
   

   
   public void setWorld(World world, String key)throws CheaterException{
      if(!key.equals(SECURE_KEY))
         throw new CheaterException();
         
      this.myWorld = world;
   }

   

}

