package AIfight;

import java.awt.Color;
import java.util.Random;

public class ExampleHunter implements MindTemplate{
   //Needed by interface
   private Creature creature;
   private Color color=Color.RED;
   
   //{MAX_HEALTH, MAX_FOOD, ATTACK, DEFENCE, SPEED, EAT, SENSE, STEALTH}
   private int[] stats= {100,100,100,100,100,100,100,100};
   private final String species = "Wandering Hunter"; //or something like that. preferebly cooler
   
   public ExampleHunter(){
      //do something here... idk. 
      //i should add the option to pass in a creature index to the constructor, i think that might be usefull for making sperate creature roles.
      //well, i guess you could just gen rnd numbers but that would be pretty inconsistant. best to use reflection to get a constructor with an int. 
   }
   
   public void tick(){
         SensorSuite senses = this.creature.getSensorSuite();
         int[] foodTouchSense = senses.getFoodTouchSense();
         boolean[] enemyTouchSense = senses.getEnemyTouchSense();
         int[] foodSmellSense = senses.getFoodSmellSense();
         int[] enemySmellSense = senses.getEnemySmellSense();
         
         //look for enemies in attack range
         int dir = SensorSuite.findFirst(enemyTouchSense);      
         
         //attack anything nearby with extreme predjudice
         if (dir != -1){
            Action toDo = Action.getAttackAction(dir);
            this.creature.addAction(toDo);  
            this.creature.addAction(toDo);
            this.creature.addAction(toDo);
            this.color = Color.YELLOW;
            return;
         }
         
         this.color = Color.RED;
         
         //no creatures adjacent. lets see if there are any big lumps of food lying around
         dir = SensorSuite.findGreatest(foodTouchSense);
         
         //eat any nearby food
         if(dir != -1 && foodTouchSense[dir] > 25){
            this.creature.addAction(Action.getEatAction(dir));
            return;
         }
         
         //well, no creatures or food adjacent, lets try and smell a creature to hunt
         dir = SensorSuite.findGreatest(enemySmellSense);
         
         //move toward the nearest creature
         if(dir != -1){
            this.creature.addAction(Action.getMoveAction(dir));
            return;
         }
         
         //could smell any creatures, lets just go park ourselves on the nearest food spot and wait for one
         dir = SensorSuite.findGreatest(foodSmellSense);
         
         //move toward the strongest food smell
         if(dir != -1)
            this.creature.addAction(Action.getMoveAction(dir));
   }

   public Color getColor(){return this.color;}
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setCreature(Creature me){if(this.creature == null) this.creature = me;}
}