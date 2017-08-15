package AIfight;

import java.awt.Color;

public interface MindTemplate{
   
   //the species' name
   public final String species = "default value";
   
   //This is a link to the creature the mind controls. Mind is created before creature, so thats why setCreature is there. 
   public Creature creature = null;
   
   //Creature calls this once to establish the mind's link to it's creature
   //public void setCreature(Creature me){this.creature = me;} <== just use that
   public void setCreature(Creature me);
   
   //this is an array of the creature's stats. 8 elements, values of 0-200 each, must add up to 800 (or lower i guess)
   //override it in your MindTemplate implementation to specify it for your creature
   public int[] stats = {100,100,100,100,100,100,100,100};
    
   //this is the color your creature will have on the map. Override to change
   public Color color = Color.GRAY;
   
   //Each creature will have this method called once each per game tick. Put all your code in here
   //nonblocking code only for obvious reasons.
   public void tick();
   
   public Color getColor();
   public int[] getStats();
   public String getSpecies();
}