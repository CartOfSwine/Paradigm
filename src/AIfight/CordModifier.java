package AIfight;

public class CordModifier{
   public final int xMod;
   public final int yMod;
   
   public CordModifier(int xMod, int yMod){
      this.xMod = xMod;
      this.yMod = yMod;
   }
   
   public boolean equals(Object o){
      if(o == null)
         return false;
      if(o == this)
         return true;
      if(o instanceof CordModifier){
         CordModifier that = (CordModifier)o;
         return this.xMod == that.xMod && this.yMod == that.yMod; 
      }
      return false;
   }
}