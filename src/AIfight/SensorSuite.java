package AIfight;
public class SensorSuite{
   private int[] hearingSense;   //contains 4 ints from 0 to 100. 
   /*                            //each number represents the level of noise the creature detects in that direction
   index | direction             //detects the presence of all species, friend or foe
     0   |   Up                  //the other creatures (stealth-100)/10 is added to the distance
     1   |   Right               //full range      
     2   |   Down
     3   |   Left
   */
   //NOTE: all smell senses have (smellEfficienceCoeficent, a var declared in the World class) the effective range of hearing and sight
   private int[] foodSmellSense;       //same idea as hearing except that the creature can detect the presence of food 
                                       //the difference is that activity is muiltiplied by the (ammount of food)/50. Thus grass will smell a little and whole corpses a lot   
   private int[] allySmellSense;       //same deal as the others. 4 items representing directions. yadda yadda
                                       //only detects members of the same species. Not affected by the stealth stat
   private int[] enemySmellSense;      //only detect other species. affected by the stealth stat of others                                 
   
   //adjacency senses                                    
   private int[] obstructionTouchSense;//4 ints in the same layout as before, except that the ints are the health of the obstruction instead of the distance
   private int[] foodTouchSense;       //has 5 ints, the fifth represents the tile under the creature
   private boolean[] allyTouchSense;   //boolean wether or not there is an ally adjacent to the current position                                    
   private boolean[] enemyTouchSense;  //booleans for detecting enemies by touch
   

   //sight is a complicated sense. it only can look for one thing at a time and will only detect the closest of that thing.
   /*
   for an example. say you want your creature to look for allies. in your mind code use the following
      this.creature.getSensorSuite().setSightTargetType(SightTarget.ALLY));
   
   for every game tick following that command (not including the current one)... 
   creature.SesnorSuite.sightHasTarget will represent if the sight sense has detected anything
   creature.SesnorSuite.sightAngle will represent the angle (in degres, 0-359) to the closest target,
   creature.SesnorSuite.sightSense will be how visible that target is 
   */
   private SightTarget sightTargetType;      //look at the SightTarget enum. its just the thing you are looking for. (default is enemies)
   private int sightAngle;                    //the angle to the nearest (whatever you are looking for). 0 degrees is straight right, 90 up, 180 left, 270 down 
   private int sightSense;                    //how visible the target is. number from 0 to 100, affected by both distance and (when applicable) stealth stats
   private boolean sightHasTarget;            //whether or not the sight sense has found a target in it's sensory range
 
   //-----------------------------------------------------------------constructor
   public SensorSuite(){
      //just init the arrays and call it good. the world injects arrays not values so this is onlt to stop nullPointerExceptions on the first tick(might not even be an issue)
      this.hearingSense = new int[4];
      this.foodSmellSense = new int[4];
      this.allySmellSense = new int[4];
      this.enemySmellSense = new int[4];
      this.obstructionTouchSense = new int[4];
      this.foodTouchSense = new int[5];         //the fifth slot is the one directly underneath the creature
      this.allyTouchSense = new boolean[4];
      this.enemyTouchSense = new boolean[4];
      this.sightTargetType = SightTarget.CREATURE;
      
   }
   public SensorSuite(int[] hearingSense, int[] foodSmellSense, int[] allySmellSense, int[] enemySmellSense, 
                      int[] obstructionTouchSense, int[] foodTouchSense, boolean[] allyTouchSense, boolean[] enemyTouchSense,
                      SightTarget sightTargetType, int sightAngle, int sightSense, boolean sightHasTarget){
 
      this.hearingSense = hearingSense;
      this.foodSmellSense = foodSmellSense;
      this.allySmellSense = allySmellSense;
      this.enemySmellSense = enemySmellSense;
      this.obstructionTouchSense = obstructionTouchSense;
      this.foodTouchSense = foodTouchSense;
      this.allyTouchSense = allyTouchSense;
      this.enemyTouchSense = enemyTouchSense;
      this.sightTargetType = sightTargetType;
      this.sightAngle = sightAngle;
      this.sightSense = sightSense;
      this.sightHasTarget = sightHasTarget;
   }
   
   //-----------------------------------------------------------------utilities
   public static int findGreatest(int[] ara){
      return findGreatest(ara,1.0);
   }
   
   public static int findGreatest(int[] ara, double debouncer){
      //returns -1 all are 0
      int max = 0;
      int dir = -1;
      for(int i = 0; i < ara.length; i++)
         if(ara[i] > max) {
            max = (int)(ara[i] * debouncer);
            dir = i;
         }
      
      return dir;
   }
   
   public static int findFirst(boolean[] ara){
      for(int i = 0; i < ara.length; i++)
         if(ara[i])
            return i;
      
      return -1;
   }
   
   public static int getAverage(int[] ara){
      int total = 0;
      for(int i = 0; i < ara.length; i++)
         total += ara[i];
      
      return total / ara.length;
   }
   
   public static int getNumber(boolean[] ara){
      int total = 0;
      for (int i = 0; i < ara.length; i++)
         if(ara[i])
            total += 1;
      
      return total;
   }
   
   //-----------------------------------------------------------------getters
   public int[] getHearingSense(){return this.hearingSense;}
   public int[] getFoodSmellSense(){return this.foodSmellSense;}
   public int[] getAllySmellSense(){return this.allySmellSense;}
   public int[] getEnemySmellSense(){return this.enemySmellSense;}
   public int[] getObstructionTouchSense(){return this.obstructionTouchSense;}
   public int[] getFoodTouchSense(){return this.foodTouchSense;}
   public boolean[] getAllyTouchSense(){return this.allyTouchSense;}
   public boolean[] getEnemyTouchSense(){return this.enemyTouchSense;}
   public boolean getSightHasTarget(){return this.sightHasTarget;}
   public int getSightAngle(){return this.sightAngle;}
   public int getSightSense(){return this.sightSense;}
   public SightTarget getSightTargetType(){return this.sightTargetType;}
   
   //-----------------------------------------------------------------setters
   public void setHearingSense(int[] h){this.hearingSense = h;}
   public void setFoodSmellSense(int[] fs){this.foodSmellSense = fs;}
   public void setAllySmellSense(int[] as){this.allySmellSense = as;}
   public void setEnemySmellSense(int[] es){this.enemySmellSense = es;}
   public void setObstructionTouchSense(int[] os){this.obstructionTouchSense = os;}
   public void setFoodTouchSense(int[] fs){this.foodTouchSense = fs;}
   public void setAllyTouchSense(boolean[] as){this.allyTouchSense = as;}
   public void setEnemyTouchSense(boolean[] es){this.enemyTouchSense = es;}
   public void setSightHasTarget(boolean t){this.sightHasTarget = t;}
   public void setSightAngle(int a){this.sightAngle = a;}
   public void setSightSense(int s){this.sightSense = s;}
   public void setSightTargetType(SightTarget t){ this.sightTargetType = t;}
   //god that was a pain to write. maybe i shoudl switch to eclipse for that juicy juicy autogenerated getters/setters
}