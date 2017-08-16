package AIfight;

//needed to make the basic frame
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrm {
   private final String SECURE_KEY = "password";   //wont run unless this matched the one in Creature

   public static void main(String[] args) {
         createAndShowGUI();
    }

   private static void createAndShowGUI() {
        JFrame f = new JFrame("AI Fight Simulator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        
        f.add(new WorldPanel(15));
        f.pack();
        
        f.setSize(810,860);
        f.setVisible(true);
        f.addNotify();
    }

}

class WorldPanel extends JPanel implements Runnable{

   private JButton testButton;
   private JLabel testLabel;
   private JPanel buttonPanel;
   
   private final int PWIDTH = 810;
   private final int PHEIGHT = 810;
   
   private final int SIMWIDTH = 800;
   private final int SIMHEIGHT = 800;
   
   private Thread animator;
   private boolean running = false;
   private boolean simOver = false;
   
   private Graphics dbg;            //The graphics object we render to initially
   private Image dbImage = null;    //The image generated b the rendering process
   
   private World sim1;
   
   private int selectX;    //the upper-left hand corner of the currently selected area of the screen
   private int selectY;    //(wont be used until zooming in is a thing)
   
   private int stageSize;  //the size of the display area in display tiles
   private int tileSize;   //the size of each display square in pixels

   public WorldPanel(int tileSize) {
      testButton = new JButton("test button");
      
      buttonPanel = new JPanel();
      buttonPanel.setLocation(400,0); //fuc, doesnt work
      buttonPanel.setBackground (Color.orange);
      buttonPanel.add(testButton);

      this.setBackground(Color.WHITE);
        
      setFocusable(true);
      requestFocus();
      //readyForTermination();
        
      this.tileSize = tileSize;
      
      int smallSize = SIMWIDTH;
      if(SIMHEIGHT < SIMWIDTH)
         smallSize = SIMHEIGHT;
      
      this.stageSize = smallSize/tileSize;

      MindTemplate contender1 = new ExampleGrazer();
      MindTemplate contender2 = new ExampleHunter();

      sim1 = new World(this.stageSize,0,contender1, contender2);
      sim1.initialize(10);
   }
  
   private class ButtonListener implements ActionListener{
      public void actionPerformed(ActionEvent event){
         Object choice = event.getSource();
         System.out.println(choice);
      }
   }

   public void addNotify(){
      super.addNotify();
      startSim();
   }
   
   private void startSim(){
      if (animator == null || !running){
         animator = new Thread(this);
         animator.start();
      }
   }
   
   public void stopSim(){
      running = false;
   }
   
   public void run(){
      //long beforeTime, timeDiff, sleepTime;
      //beforeTime = System.currentTimeMillis();
   
      running = true;
      simPaint();
      while(running){
         simUpdate();
         simRender();
         simPaint();
      
         //timeDiff= System.currentTimeMillis() - beforeTime;
         //sleepTime = period - timeDiff;
         
         //if(sleepTime <= 0)
            //sleepTime = 5;
            
         try {
            Thread.sleep(200);
         }catch(InterruptedException ex){}
         
         //beforeTime = System.currentTimeMillis();
      }
      
      System.exit(0);
   }
   
   private void simUpdate(){
      if(!simOver){
         sim1.tick();
      }
   }
   
   private void simRender(){
      if(dbImage == null){
         dbImage = createImage(PWIDTH,PHEIGHT);
         if(dbImage == null){
            System.out.println("dbImage was null");
            return;
         }
         else
            dbg = dbImage.getGraphics();
      }
      dbg.setColor(Color.white);
      dbg.fillRect (0, 0, SIMWIDTH, SIMHEIGHT);
      dbg.setColor(Color.black);
      dbg.drawRect (0, 0, SIMWIDTH, SIMHEIGHT);
      for (int y = 0; y < this.stageSize; y++){
         for(int x = 0; x < this.stageSize; x++){
            dbg.setColor(sim1.getColor(x,y));
            dbg.fillRect(x * tileSize,y*tileSize,tileSize,tileSize);
         }
      }  
   }
   
   private void simPaint(){
      Graphics g;
      try{
         g = this.getGraphics();
         if ((g != null) && (dbImage != null))
            g.drawImage(dbImage,0,0,null);
         g.dispose();
      
      } catch(Exception e){
         System.out.println("Graphics encountered an error: " + e);
      }
   }
   
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);       
        //g.drawString("This is my custom Panel!",10,20);
        
      for (int y = 0; y < this.stageSize; y++){
         if(dbImage != null)
            g.drawImage(dbImage, 0, 0, null); 
      }
      //g.setColor(Color.RED);
      //g.fillRect(squareX,squareY,squareW,squareH);
      //g.setColor(Color.BLACK);
      //g.drawRect(squareX,squareY,squareW,squareH);
   }
   
   public int getPwidth(){
      return this.PWIDTH;
   }
   public int getPheight(){
      return this.PHEIGHT;
   }
   
}