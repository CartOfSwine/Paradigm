
package AIfight;

public enum AOE{
   SELF(new CordModifier[]{new CordModifier(0,0)}),
   
   UP(new CordModifier[]{new CordModifier(0,1)}),
   RIGHT(new CordModifier[]{new CordModifier(1,0)}),
   DOWN(new CordModifier[]{new CordModifier(0,-1)}),
   LEFT(new CordModifier[]{new CordModifier(-1,0)}),
   
   //the 4 adjacent squares
   //- = unaffected
   //X = affected
   //O = center (assume the middle if not given
   /* -----
      --X--
      -XOX-
      --X--
      -----
   */
   ADJACENT(new CordModifier[]{     new CordModifier(0,1),
                                    new CordModifier(1,0),
                                    new CordModifier(0,-1),
                                    new CordModifier(-1,0)}),
   
   /* -----
      -XXX-
      -XOX-
      -XXX-
      -----
   */
   //adjecent squares including diagonals                     
   SURROUNDING(new CordModifier[]{  new CordModifier(0,1),
                                    new CordModifier(1,1),
                                    new CordModifier(1,0),
                                    new CordModifier(1,-1),
                                    new CordModifier(0,-1),
                                    new CordModifier(-1,-1),
                                    new CordModifier(-1,0),
                                    new CordModifier(-1,1)}),
   /* -----
      -XXX-
      -XXX-
      -XXX-
      -----
   */                                 
   //adjacent squares with diagonals and including the current square
   BOX(new CordModifier[]{          new CordModifier(0,0),
                                    new CordModifier(0,1),
                                    new CordModifier(1,1),
                                    new CordModifier(1,0),
                                    new CordModifier(1,-1),
                                    new CordModifier(0,-1),
                                    new CordModifier(-1,-1),
                                    new CordModifier(-1,0),
                                    new CordModifier(-1,1)}),
   /* --X--
      --X--
      --X--
      --O--
      -----
   */
   
   
   RAY3_UP(new CordModifier[]{      new CordModifier(0,1),
                                    new CordModifier(0,2),
                                    new CordModifier(0,3)}),
                                    
   RAY3_RIGHT(new CordModifier[]{   new CordModifier(1,0),
                                    new CordModifier(2,0),
                                    new CordModifier(3,0)}),
   
   RAY3_DOWN(new CordModifier[]{    new CordModifier(0,-1),
                                    new CordModifier(0,-2),
                                    new CordModifier(0,-3)}),
                                   
   RAY3_LEFT(new CordModifier[]{    new CordModifier(-1,0),
                                    new CordModifier(-2,0),
                                    new CordModifier(-3,0)}),
   /* -----
      -XXX-
      --O--
      -----
      -----
   */
   SLASH_UP(new CordModifier[]{     new CordModifier(-1,1),
                                    new CordModifier(0,1),
                                    new CordModifier(1,1)}),
   
   SLASH_RIGHT(new CordModifier[]{  new CordModifier(1,-1),
                                    new CordModifier(1,0),
                                    new CordModifier(1,1)}),
                                    
   SLASH_DOWN(new CordModifier[]{   new CordModifier(-1,-1),
                                    new CordModifier(0,-1),
                                    new CordModifier(1,-1)}),
                                    
   SLASH_LEFT(new CordModifier[]{   new CordModifier(-1,-1),
                                    new CordModifier(-1,0),
                                    new CordModifier(-1,1)})
   
   ;
   public final CordModifier[] locations;
   
   private AOE(CordModifier[] locations){
      this.locations = locations;
   }
}

