package AIfight;

//needed to make the basic frame
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrm extends JFrame{
   public static void main(String[] args){
      new MainFrm();
   }
   
   public MainFrm(){     
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      WorldPanel simDisplay = new WorldPanel(15);      
      this.setLayout(null);
      
      JButton pauseBtn = new JButton("Resume");
      
      pauseBtn.setBounds(simDisplay.PWIDTH,0,100,50);
      simDisplay.setBounds(0,0,simDisplay.PWIDTH, simDisplay.PHEIGHT);
      
      pauseBtn.addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent e) { 
            if(simDisplay.getIsRunning()){
               simDisplay.setIsRunning(false);
               pauseBtn.setText("Resume");
            }
            else{
               simDisplay.setIsRunning(true);
               pauseBtn.setText("Pasue");
            }
         } 
      });
      
      
      this.add(simDisplay);
      this.add(pauseBtn);
      
      this.pack();
      this.setSize(950, 860);
      this.setVisible(true);
   }
  
}



class WorldPanel extends JPanel implements Runnable{

   private JButton testButton;
   private JLabel testLabel;
   private JPanel buttonPanel;
   
   public final int PWIDTH = 810;
   public final int PHEIGHT = 810;
   
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
      this.setBackground(Color.WHITE);
        
      setFocusable(true);
      requestFocus();
      //readyForTermination();
        
      this.tileSize = tileSize;
      
      int smallSize = SIMWIDTH;
      if(SIMHEIGHT < SIMWIDTH)
         smallSize = SIMHEIGHT;
      
      this.stageSize = smallSize/tileSize;
      
      //set up two mind objects for the opposing players
      MindTemplate contender1 = new ExampleGrazer();
      MindTemplate contender2 = new ExampleHunter();

      //create the simulation with the players
      sim1 = new World(this.stageSize,0,contender1, contender2);
      //initialize the sim with 10 creatures for each player
      sim1.initialize(10);
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
      while(!simOver){
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
         try {
            Thread.sleep(10);
         }catch(InterruptedException ex){}
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
      dbg.fillRect (1, 1, SIMWIDTH-1, SIMHEIGHT-1);
      dbg.setColor(Color.black);
      dbg.drawRect (0, 0, SIMWIDTH, SIMHEIGHT);
      for (int y = 0; y < this.stageSize; y++){
         for(int x = 0; x < this.stageSize; x++){
            dbg.setColor(sim1.getColor(x,y));
            dbg.fillRect(x * tileSize+1,y*tileSize+1,tileSize,tileSize);
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
   }
   
   public int getPwidth(){
      return this.PWIDTH;
   }
   public int getPheight(){
      return this.PHEIGHT;
   }
   
   public void setIsRunning(boolean state){
      this.running = state;
   }
   
   public boolean getIsRunning(){
      return this.running;
   }
}