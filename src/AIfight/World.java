package AIfight;

import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;
import java.lang.Math;

public class World {
   
   //holds creatures
   private Creature[] population1;
   private Creature[] population2;
   
   //holds a single instantiation of each player's mind
   private MindTemplate contender1;
   private MindTemplate contender2;
   
   private String SECURE_KEY = "password";
   
   private Creature[][] creatureMap;
   private int[][] resourceMap;
   private Obstruction[][] obstructionMap;
   
   private boolean nextTickFlag; 
   
   public final int WORLD_SIZE;
   
   public final int baseSensorRange = 10;
   public final boolean linearActivation = true;
   public final double smellDistanceModifier = 0.5;
   //=============================================================================Constructors
   public World(int size, int numWalls, MindTemplate contender1, MindTemplate contender2){
      this.contender1 = contender1;
      this.contender2 = contender2;
      
      WORLD_SIZE = size;
      creatureMap = new Creature[WORLD_SIZE][WORLD_SIZE];
      obstructionMap = new Obstruction[WORLD_SIZE][WORLD_SIZE];
      resourceMap = new int[WORLD_SIZE][WORLD_SIZE];  
      
      for (int y = 0; y < WORLD_SIZE; y++){
         for (int x = 0; x < WORLD_SIZE; x++){
            creatureMap[y][x] = null;
            resourceMap[y][x] = 0;
            obstructionMap[y][x] = new Obstruction();
         }
      }
      this.nextTickFlag = true;
      
      placeWalls(numWalls);
      placeResources();
   }
   
   //Construction is broken up into two phases. this is because creatures need a referance to the world they exist in
   public void initialize(int numCreatures){
      MindTemplate[] minds1 = new MindTemplate[numCreatures];
      MindTemplate[] minds2 = new MindTemplate[numCreatures];
      
      population1 = new Creature[numCreatures];
      population2 = new Creature[numCreatures];
   
      Random rnd = new Random();
      int rndX, rndY;
      for (int i = 0; i < numCreatures; i++){
         
         //use the mind class objects to instantiate minds
         try{
            minds1[i] = this.contender1.getClass().newInstance();
            minds2[i] = this.contender2.getClass().newInstance();
         }
         catch (Exception e){
            System.out.println("Failed to instantiate contender mind classes:" + e);
         }
         
         do{        
            rndX = rnd.nextInt(WORLD_SIZE);
            rndY = rnd.nextInt(WORLD_SIZE);
         }while(creatureMap[rndY][rndX] != null || !obstructionMap[rndY][rndX].isEmpty());
         
         population1[i] = new Creature((MindTemplate)minds1[i], this,((Integer)i).toString(),rndX,rndY);
         creatureMap[rndY][rndX] = population1[i];
         minds1[i].setCreature(population1[i]);
         
         do{        
            rndX = rnd.nextInt(WORLD_SIZE);
            rndY = rnd.nextInt(WORLD_SIZE);
         }while(creatureMap[rndY][rndX] != null || !obstructionMap[rndY][rndX].isEmpty());
         
         population2[i] = new Creature((MindTemplate)minds2[i],this, ((Integer)i).toString(),rndX,rndY);
         creatureMap[rndY][rndX] = population2[i];
         minds2[i].setCreature(population2[i]);
      }
   }
   //=============================================================================Utilities
   //-----------------------------------------------------------------placeWalls
   private void placeWalls(int numWalls){
      //map[5][6] = new TileContents(ContentType.WALL,100);
      
   }
   //-----------------------------------------------------------------placeResources
   private void placeResources(){
      Random rnd = new Random();
      int rndX,rndY,rndNum;
      
      for(int c = 0; c < 500; c++){
         rndX = rnd.nextInt(WORLD_SIZE);
         rndY = rnd.nextInt(WORLD_SIZE);
         rndNum = rnd.nextInt(50);
   
         resourceMap[rndY][rndX] = rndNum;
      }
   }
   //-----------------------------------------------------------------tick
   public void tick(){
      //exicutes mind code if all queued actions are complete
      
      
      if(nextTickFlag){       
         for (Creature creature: population1){
            feedSensorData(creature);
            creature.tick();
         }
         for(Creature creature: population2){
            feedSensorData(creature);
            creature.tick();
         }
      }
      try{
         //will queue up the effects of actions one at a time
         this.nextTickFlag = true;
         for (Creature creature: population1){
            if (creature.nextAction(SECURE_KEY) != true)
               this.nextTickFlag = false;
         }
         for(Creature creature: population2){
            if (creature.nextAction(SECURE_KEY) != true)
               this.nextTickFlag = false;
         }
         
         //will resolve all queued effects on creatures
         for (Creature creature: population1){
            creature.resolve(SECURE_KEY);
         }
         for(Creature creature: population2){
            creature.resolve(SECURE_KEY);
         }
      }
      catch(CheaterException e){
         System.out.println("Somehow my own code is cheating...?");
      }
      
   }
   //-----------------------------------------------------------------killCreatureAt
   public void killCreature(int xPos, int yPos){
      int x = fc(xPos);
      int y = fc(yPos);
      Creature toKill = creatureMap[y][x];
   
      if(creatureMap[y][x] != null){
         creatureMap[y][x] = null;
         resourceMap[y][x] += toKill.getMaxFood()/2;
         resourceMap[y][x] += toKill.getDefence()/2;
         obstructionMap[y][x] = new Obstruction(ObstructionType.CORPSE);
      }
   }
   
   //-----------------------------------------------------------------moveCreatureAt
   public boolean moveCreatureAt(int sX, int sY, int changeX, int changeY, String key) throws CheaterException{
      if (!key.equals(SECURE_KEY))
         throw new CheaterException();
      int startX, startY, endX, endY;
      
      startX = fc(sX);
      startY = fc(sY);
      endX = fc(sX+changeX);
      endY = fc(sY+changeY);
      
      if(creatureMap[startY][startX] != null && 
         creatureMap[endY][endX] == null && 
         obstructionMap[endY][endX].isEmpty()){
         
         creatureMap[endY][endX] = creatureMap[startY][startX];
         creatureMap[startY][startX] = null;
         return true;
      }
      return false;
   }

   //-----------------------------------------------------------------isEmpty
   public boolean isEmpty(int x, int y, String key) throws CheaterException{
      if(!key.equals(SECURE_KEY))
         throw new CheaterException();
         
      //no need to precheck the cordinates as they are rectified
      return (creatureMap[fc(y)][fc(x)] == null && obstructionMap[fc(y)][fc(x)].isEmpty());
   }
   
   //-----------------------------------------------------------------fc
   public int fc(int cord){   //ensures the number is a valid cordinate on a square world map
      int temp = cord % WORLD_SIZE;
      if(temp < 0)
         temp = WORLD_SIZE + temp;
      return temp;
   }
   
   //-----------------------------------------------------------------eatSquare
   public int eatSquare(int xVal, int yVal, int maxAmmt,String key) throws CheaterException{
      if(!key.equals(SECURE_KEY))
         throw new CheaterException();
         
      int x = fc(xVal);
      int y = fc(yVal);
         
      int ammt = resourceMap[y][x];
      if (ammt > maxAmmt)
         ammt = maxAmmt;
      
      resourceMap[y][x] -= ammt;
      return ammt;
   }
   
   //-----------------------------------------------------------------damageSquare
   public boolean damageSquare(int xVal, int yVal, int atk, String key)throws CheaterException{
      if(!key.equals(this.SECURE_KEY))
         throw new CheaterException();
         
      int x = fc(xVal);
      int y = fc(yVal);
         
      if(creatureMap[y][x] != null){
         creatureMap[y][x].incIncomingDmg(atk,SECURE_KEY);
         return true;
      }
      return false;       
   }
   
   //-----------------------------------------------------------------feedSensoryData
   private void feedSensorData(Creature c){   //sorry to anyone trying to read this. needed it to run fast AF so i klueged the whole thing into the one loop set
      int senses,senseBuff,senseDistance,x,y,activity,dx,dy,xVal,yVal,otherStealth,otherStealthAddition;
      double distance, stealthedDistance;
      
      SensorSuite creatureSenses = c.getSensorSuite();
      senses = c.getSense();                          //sense stat
      x = fc(c.getXpos());                            //xpos of creature
      y = fc(c.getYpos());                            //ypos of creature
      senseBuff = c.getSenseBuff();
      senseDistance = (int)((baseSensorRange + (senses-100)/10.0)*((100+senseBuff)/100.0)) + 1;//max sensory distance
      
      double minSightDist = 20000000;                 //the smallest distance to a sight target
      SightTarget sightType = creatureSenses.getSightTargetType();
      int smellMaxRange;

      int newSightSense = 0;
      int newSightAngle = 0;
      boolean newSightHasTarget = false;
      
      int[] newHearingSense = new int[4];
      int[] newFoodSmellSense = new int[4];
      int[] newEnemySmellSense = new int[4];
      int[] newAllySmellSense = new int[4];
      
      int[] newObstructionTouchSense = new int[4];
      int[] newFoodTouchSense = new int[5];
      boolean[] newEnemyTouchSense = new boolean[4];
      boolean[] newAllyTouchSense = new boolean[4]; 
      
      for(int xv = x-senseDistance;xv <= x+senseDistance;xv++){ //loop through a box around the creature checking each square's contents
         for(int yv = y-senseDistance;yv <= y+senseDistance;yv++){
            xVal = fc(xv);                            //the currently scanning square cords after wrapping sides
            yVal = fc(yv); 
            dx = x-xv;                                //the difference in xvals
            dy = y-yv;                                //the difference in yvals
  
                                                      //distance formula
            distance = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
            smellMaxRange = (int)(senseDistance * smellDistanceModifier); //smell is short range, factor in the range limiter
         
            
            //if we found a creature other than the current creature
            if(creatureMap[yVal][xVal] != null && (xVal != x && yVal != y)){     //handle if tile contains creature
               Creature o = creatureMap[yVal][xVal];                             //the creature in scanning range
            
               otherStealth = o.getStealth();                                    //others stealth stat
               otherStealthAddition = (otherStealth-100)/10;
               
               stealthedDistance = distance + (otherStealth-100)/10;             //the distance factoring in o's stealth stat
               
                              
               if(c.getSpecies().equals(o.getSpecies())){
                  populateSmellSenseArray(newAllySmellSense,smellMaxRange,dx,dy,1);             
                  if((sightType == SightTarget.ALLY || sightType == SightTarget.CREATURE) && minSightDist > distance){
                     newSightHasTarget = true;
                     minSightDist = distance;
                     newSightSense = calcActivity(senseDistance,(int)distance);
                     newSightAngle = calcAngle(dy,dx);
                  }
               }
               else{                       
                  populateSmellSenseArray(newEnemySmellSense,smellMaxRange,dx+otherStealthAddition,dy+otherStealthAddition,1);
                  if((sightType == SightTarget.ENEMY || sightType == SightTarget.CREATURE) && minSightDist > stealthedDistance){
                     newSightHasTarget = true;
                     minSightDist = stealthedDistance;
                     newSightSense = calcActivity(senseDistance,(int)stealthedDistance);
                     newSightAngle = calcAngle(dy+otherStealthAddition,dx+otherStealthAddition);
                  }
               } 
               populateSmellSenseArray(newHearingSense,senseDistance,dx+otherStealthAddition,dy+otherStealthAddition,1);
            }
            //if there is a resource patch in the selected square
            if(resourceMap[yVal][xVal] > 0){
               populateSmellSenseArray(newFoodSmellSense,smellMaxRange,dx,dy,resourceMap[yVal][xVal]/50.0);
               if(sightType == SightTarget.FOOD && minSightDist > distance){
                  newSightHasTarget = true;
                  minSightDist = distance;
                  newSightSense = (int)(calcActivity(senseDistance,(int)distance) * resourceMap[yVal][xVal]/50.0);
                  newSightAngle = calcAngle(dy,dx);
               }
            }
            //if the selected square contains a corpse and we are looking for a coprpse
            if(obstructionMap[yVal][xVal].type == ObstructionType.CORPSE && minSightDist > distance){
               newSightHasTarget = true;
               minSightDist = distance;
               newSightSense = calcActivity(senseDistance,(int)distance);
               newSightAngle = calcAngle(dy,dx);
            }
            //if the selected square contains an obsticle and we are looking for one
            if(obstructionMap[yVal][xVal].type != ObstructionType.EMPTY && minSightDist > distance){
               newSightHasTarget = true;
               minSightDist = distance;
               newSightSense = calcActivity(senseDistance,(int)distance);
               newSightAngle = calcAngle(dy,dx);
            }
         }
      }  
      //populate touch senses
      AOE myAOE = AOE.ADJACENT;
      for(int i = 0; i < myAOE.locations.length;i++){
         Creature selectC = creatureMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];
         Obstruction selectO = obstructionMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];         
         if(selectC != null){
            if(selectC.getSpecies().equals(c.getSpecies()))
               newAllyTouchSense[i] = true;
            else
               newEnemyTouchSense[i] = true;
         }
         newFoodTouchSense[i] = resourceMap[fc(y+myAOE.locations[i].yMod)][fc(x+myAOE.locations[i].xMod)];
         newObstructionTouchSense[i] = selectO.curHP;
      }   
      newFoodTouchSense[4] = resourceMap[y][x];
      creatureSenses.setSightSense(newSightSense);
      creatureSenses.setSightAngle(newSightAngle);
      creatureSenses.setSightHasTarget(newSightHasTarget);
      
      creatureSenses.setHearingSense(newHearingSense);
      creatureSenses.setFoodSmellSense(newFoodSmellSense);
      creatureSenses.setEnemySmellSense(newEnemySmellSense);
      creatureSenses.setAllySmellSense(newAllySmellSense);
      creatureSenses.setObstructionTouchSense(newObstructionTouchSense);
      creatureSenses.setFoodTouchSense(newFoodTouchSense);
      creatureSenses.setEnemyTouchSense(newEnemyTouchSense);
      creatureSenses.setAllyTouchSense(newAllyTouchSense);
   }
   
   //-----------------------------------------------------------------calcAngle
   private int calcAngle(int dy, int dx){
      //rewrite this when you have a moment. You were on a plane when you wrote this and were running
      //on 1.5 hrs of sleep. NEEDS POLISH. Its way to klueged rn 
      
      dx = dx * -1; //im not sure why i have to do this tbh. 
      
      if(dx == 0){
         if(dy > 0)
            return 90;
         if(dy < 0)
            return 270;
      }
      if(dy == 0 && dx < 0)
         return 180;
         
      int tempAngle = (int)Math.toDegrees(Math.atan((dy/(double)dx)));
      
      if(dy < 0 && dx < 0)
         tempAngle += 180;
      else if(dy > 0 && dx < 0)
         tempAngle = -1*tempAngle + 90;
      else if(dy < 0 && dx > 0)
         tempAngle += 360;
                
      return tempAngle;
   }
   
   //-----------------------------------------------------------------populateSmellSenseArray
   private void populateSmellSenseArray(int[] ara, int maxRange, int dx, int dy, double muiltiplier){
      double d = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
      
      //
      if(Math.abs(dy) <= Math.abs(dx) && dx > 0)
         ara[3] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);
      if(Math.abs(dy) <= Math.abs(dx) && dx < 0)
         ara[1] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);     
      if(Math.abs(dy) >= Math.abs(dx) && dy > 0)
         ara[0] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);
      if(Math.abs(dy) >= Math.abs(dx) && dy < 0)
         ara[2] +=(int)(calcActivity(maxRange,d-1)*muiltiplier);
      
   }
   
   //-----------------------------------------------------------------calcActivity
   private int calcActivity(int maxSensorRange, double measuredRange){
      int activity;
      if(this.linearActivation)
         activity = (int)(-1.0 * measuredRange * (100.0/maxSensorRange) + 100.0);
      else
         activity = (int)(-1* Math.pow(measuredRange - maxSensorRange,3) * (100.0/Math.pow(maxSensorRange,3)));
      if(activity < 0)
         activity = 0;
      if(activity > 100)
         activity = 100;
      return activity;
   }
   
   //=============================================================================Gets/Sets
   public Color getColor(int xVal, int yVal){
      int x = fc(xVal);
      int y = fc(yVal);
   
      if (creatureMap[y][x] != null)
         return creatureMap[y][x].getColor();
      if (!obstructionMap[y][x].isEmpty())
         return obstructionMap[y][x].type.COLOR;
      if (resourceMap[y][x] != 0){
         int v = resourceMap[y][x];
         if(v > 255) v = 215;
         return new Color(0,255-v,0);
      }
      return obstructionMap[y][x].type.COLOR;
   }   
   
   
   
   private class Obstruction{
      private int curHP;
      private ObstructionType type;
   
      public Obstruction(){
         this.type = ObstructionType.EMPTY;
         this.curHP = this.type.MAXHP;
      }
   
      public Obstruction(ObstructionType type){
         this.type = type;
         this.curHP = type.MAXHP;
      }
      
      public boolean isEmpty(){
         return this.type == ObstructionType.EMPTY;
      }
      
      public void damage(int attk, String key)throws CheaterException{
         if (!key.equals(SECURE_KEY))
            throw new CheaterException();
         this.curHP -= (int)attk * this.type.DEFENCE;
         if(this.curHP <= 0){
            this.curHP = 0;
            this.type = ObstructionType.EMPTY;
         }
      }
   }
}

enum ObstructionType{
   EMPTY(1,Color.WHITE,0),
   BUSH(1.0, Color.GREEN,20),
   TREE(0.7, new Color(110,85,42),40), //brown color
   ROCK(0.3, Color.GRAY,100),
   CORPSE(1.1, Color.PINK,20);
   
   public final double DEFENCE;
   public final Color COLOR;
   public final int MAXHP;
   
   ObstructionType(double defence, Color color, int maxHP){
      this.DEFENCE = defence;
      this.COLOR = color;
      this.MAXHP = maxHP;
   }
}
