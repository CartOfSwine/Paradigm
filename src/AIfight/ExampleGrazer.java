package AIfight;

import java.awt.Color;
import java.util.Random;

public class ExampleGrazer implements MindTemplate{
   //Needed by interface
   private Creature creature;
   private Color color=Color.BLUE;
   //{MAX_HEALTH, MAX_FOOD, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {100,100,100,100,100,100,100,100};
   private final String species = "Wandering Grazer"; //or something like that. preferebly cooler
   
   //extras for functionality
   private int counter;
   private int rollover = 100;

   private int searchDir = 0;
   
   public ExampleGrazer(){
      //do something here... idk. 
      //i should add the option to pass in a creature index to the constructor, i think that might be usefull for making sperate creature roles.
      //well, i guess you could just gen rnd numbers but that would be pretty inconsistant. best to use reflection to get a constructor with an int. 
   }
   
   public void tick(){
         //Pull sensory information from attached creature for later use
         SensorSuite senses = this.creature.getSensorSuite();
         int[] foodSmellSense = senses.getFoodSmellSense();
         int[] foodTouchSense = senses.getFoodTouchSense();
         
         //ensure that our sight sense is looking for food targets
         if(senses.getSightTargetType() != SightTarget.FOOD)
            senses.setSightTargetType(SightTarget.FOOD);
         
         //check to see if we are allready touching some food
         int dir = SensorSuite.findGreatest(foodTouchSense);
         
         //if we are allready touching food, eat some and exit
         if(dir != -1){
            this.creature.addAction(Action.getEatAction(dir));
            return;
         }
         
         //we were not touching food earlier, so lets see if we can smell any food.
         //pick the most likely direction to travel by smell
         dir = SensorSuite.findGreatest(foodSmellSense,1.2);
              
         //shoot, we couldnt smell any food. well, we have a facorite direction called searchDir. lets go that way
         if (dir == -1){
            Random rnd = new Random();
              
            //lets add a 10% chance of randomly turning left or right though
            int rndNum = rnd.nextInt(100);
            if(rndNum < 5)
               searchDir++;
            else if(rndNum < 10)
               searchDir--;
            if(searchDir > 3)
               searchDir = 0;
            if(searchDir < 0)
               searchDir = 3;  
            dir = searchDir;
         }
         
         //cool, we have our heading from the earlier bit. lets move out
         this.creature.addAction(Action.getMoveAction(dir));         
   }

   public Color getColor(){return this.color;}
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setCreature(Creature me){if(this.creature == null) this.creature = me;}
}