package AIfight;

public enum Action{ //NEEDS TO BE BALANCED EVENTUALLY
   //the following are the meanings of all the action numbers, vars
   //resource cost, damage, defence buff, max eat ammount,xPos modifier, yPos modifier, effective area, single targeting) 
   ATTACK_U(10,50,0,0,0,0,0,AOE.UP,true),
   ATTACK_R(10,50,0,0,0,0,0,AOE.RIGHT,true),
   ATTACK_D(10,50,0,0,0,0,0,AOE.DOWN,true),
   ATTACK_L(10,50,0,0,0,0,0,AOE.LEFT,true),
   
   DEFEND(10,0,50,0,0,0,0),
   
   EAT_U(8,0,0,30,0,0,0,AOE.UP,true),
   EAT_R(8,0,0,30,0,0,0,AOE.RIGHT,true),
   EAT_D(8,0,0,30,0,0,0,AOE.DOWN,true),
   EAT_L(8,0,0,30,0,0,0,AOE.LEFT,true),
   EAT_S(8,0,0,30,0,0,0), //note:does not eat oneself, but the square one is standing on
   
   GRAZE(8,0,0,10,0,0,0,AOE.SURROUNDING,false),
   
   OBSERVE(20,0,0,0,0,0,50),           //the high cost is because of the performance cost. lots of additional scan area
   
   //consider adding diagonal movement?
   MOVE_U(5,0,0,0,0,-1,0),
   MOVE_R(5,0,0,0,1,0,0),
   MOVE_D(5,0,0,0,0,1,0),
   MOVE_L(5,0,0,0,-1,0,0);
   
   
   //might as well make these public. they are final so they cant get messed with
   public final int baseCost;    //the base food cost to preform the action
   
   public final int attack;  
   public final int defence;
   public final int eat;
   public final int sense;       //the temporary sensory increase. Percentage based
   
   public final int yChange;     //the change in y cordinates resulting from this action
   public final int xChange;     //the change in x cordinates resulting from this action
   
   public final AOE aoe;
   public final boolean singleTarget;  //in the event that the action will cover muiltiple squares, this determines if they are all effected, or only the first one is
                                       //for an example, an attack with a wide AOE but singleTarget=true will only be able to hit one creature regardless of how
                                       //many are in the effective area. with singleTarget = false it will hit every creature, not just the first
   Action(int baseCost, int attack, int defence,int eat, int xChange, int yChange, int sense){
      this(baseCost,attack,defence,eat,xChange,yChange,sense,AOE.SELF,true);
   }
   
   Action(int baseCost, int attack, int defence,int eat, int xChange, int yChange, int sense, AOE aoe,boolean singleTarget){
      this.baseCost = baseCost;   
      this.attack = attack;
      this.defence = defence;
      this.eat = eat;
      this.yChange = yChange;
      this.xChange = xChange;
      this.sense = sense;
      this.aoe = aoe;
      this.singleTarget = singleTarget;
   }
   
   public static Action getEatAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.EAT_U;
         case 1:
            return Action.EAT_R;
         case 2:
            return Action.EAT_D;
         case 3:
            return Action.EAT_L;
         case 4:
            return Action.EAT_S;
      }
      
      throw new IllegalArgumentException("bad direction passed to getEatAction");
   }
   
   public static Action getAttackAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.ATTACK_U;
         case 1:
            return Action.ATTACK_R;
         case 2:
            return Action.ATTACK_D;
         case 3:
            return Action.ATTACK_L;
      }
      
      throw new IllegalArgumentException("bad direction passed to getAttackAction");
   }
   
   public static Action getMoveAction(int dir) throws IllegalArgumentException {
      switch (dir){
         case 0:
            return Action.MOVE_U;
         case 1:
            return Action.MOVE_R;
         case 2:
            return Action.MOVE_D;
         case 3:
            return Action.MOVE_L;
      } 
      
      throw new IllegalArgumentException("bad direction passed to getMoveAction");
   }
   
   public boolean isMovement(){return (this.yChange != 0 || this.xChange != 0);}
   
   public boolean isAttack(){return this.attack != 0;}
   
   public boolean isDefence(){return this.defence != 0;}
   
   public boolean isEat(){return this.eat != 0;}
   
   public boolean isSense(){return this.sense != 0;}
}