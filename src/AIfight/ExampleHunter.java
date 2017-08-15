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
         
         int dir = SensorSuite.findFirst(enemyTouchSense);      
         
         //attack anything nearby with extreme predjudice
         if (dir != -1){
            Action toDo = Action.getAttackAction(dir);
            this.creature.addAction(toDo);  
            this.creature.addAction(toDo);
            this.creature.addAction(toDo);
            //this.color = Color.YELLOW;
            return;
         }
         
         //10% debouncer (testing feature)
         dir = SensorSuite.findGreatest(foodTouchSense);
         if(dir != -1 && foodTouchSense[dir] > 25){
            this.creature.addAction(Action.getEatAction(dir));
            return;
         }
         
         dir = SensorSuite.findGreatest(enemySmellSense);
         if(dir != -1){
            this.creature.addAction(Action.getMoveAction(dir));
            return;
         }
         
         dir = SensorSuite.findGreatest(foodSmellSense);
         if(dir != -1)
            this.creature.addAction(Action.getMoveAction(dir));
   }

   public Color getColor(){return this.color;}
   public int[] getStats(){return this.stats;}
   public String getSpecies(){return this.species;}
   public void setCreature(Creature me){if(this.creature == null) this.creature = me;}
}