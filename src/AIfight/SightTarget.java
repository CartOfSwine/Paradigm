package AIfight;
public enum SightTarget{
   ALLY,       //all creatures of the same species
   ENEMY,      //all creatures of other species
   CREATURE,   //all creatures in general
   FOOD,       //all food sources
   CORPSE,     //corpse obstruction blocks. (note, this finds corpses, they may or may not be looted allready)
   OBSTRUCTION;//all bostructions. look at the ObstructionType enum in the World class file for all types
}