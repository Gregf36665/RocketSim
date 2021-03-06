/* ControlPanel is the user interface for the Launcher rocket simulation  */ 

import java.awt.*; 

public class ControlPanel extends Panel 

{  Label DL,ML,CdRL,CdCL,ImL,ThL,DlL,ChL,StatusL,TmL,AltL,VelL,AccL; 
  Label EmptyR,EmptyL;
   TextField DT,MT,CdRT,CdCT,ImT,ThT,DlT,ChT,TmT,AltT,VelT,AccT; 
   Button LaunchB; 
   boolean prepped, launched; 
   Launcher L; 

   ControlPanel(Launcher parent) 

   {  L = parent; 

      setLayout(new GridLayout(7,4,5,10));

      DL = new Label("Diam (mm)", Label.RIGHT); 
      ML = new Label("Empty Mass (g)", Label.RIGHT); 
      CdRL = new Label("Drag Coef Rocket", Label.RIGHT);
      CdCL = new Label("Drag Coef Chute", Label.RIGHT); 
      ImL = new Label("Impulse (N-s)", Label.RIGHT); 
      ThL = new Label("Thrust (N)", Label.RIGHT); 
      DlL = new Label("Chute Delay (s)", Label.RIGHT); 
      ChL = new Label("Chute diameter (mm)", Label.RIGHT); 
      EmptyR = new Label("",Label.RIGHT);
      EmptyL = new Label("", Label.LEFT);
      DT = new TextField(10); 
      MT = new TextField(10); 
      CdRT = new TextField(10); 
      CdCT = new TextField(10);
      ImT = new TextField(10); 
      ThT = new TextField(10); 
      DlT = new TextField(10); 
      ChT = new TextField(10); 

      add(DL); 
      add(DT); 
      add(ML); 
      add(MT); 
      add(CdRL);
      add(CdRT);
      add(CdCL);
      add(CdCT); 
      add(ImL); 
      add(ImT); 
      add(ThL);
      add(ThT); 
      add(DlL); 
      add(DlT); 
      add(ChL); 
      add(ChT); 
      add(EmptyR);
      add(EmptyL);

      StatusL = new Label("Enter Values", Label.CENTER); 
      StatusL.setBackground(Color.red); 
      StatusL.setForeground(Color.yellow); 
      add(StatusL); 
      LaunchB = new Button("Launch"); 
      add(LaunchB); 

      TmL = new Label("Time", Label.CENTER); 
      AltL = new Label("Altitude", Label.CENTER); 
      VelL = new Label("Speed", Label.CENTER); 
      AccL = new Label("Accleration", Label.CENTER); 
      TmT = new TextField(10); 
      TmT.setEditable(false); 
      AltT = new TextField(10); 
      AltT.setEditable(false);
      VelT = new TextField(10); 
      VelT.setEditable(false);
      AccT = new TextField(10); 
      AccT.setEditable(false);
      add(TmL); 
      add(AltL);
      add(VelL);
      add(AccL);
      add(TmT); 
      add(AltT); 
      add(VelT); 
      add(AccT); 
   }  

   public boolean action(Event evt, Object arg) 
   
   {  if (evt.target == TmT) 
         L.report(launched); 
      else if ((evt.target == LaunchB) && prepped) 
         L.launch();   
      else if (evt.target instanceof TextField) 
      {  L.update((TextField)evt.target); 
         prepped = checkPrepped();  
      }  
      return(true);  
   }  

   public boolean lostFocus(Event evt, Object arg) 

   {  if (evt.target == TmT);  
      else if (evt.target instanceof TextField)  
      {  L.update((TextField)evt.target); 
         prepped = checkPrepped();  
      }
      return(true);  
   }  

   void statusPrepped() 

   {  StatusL.setBackground(Color.green); 
      StatusL.setForeground(Color.black); 
      StatusL.setText("Go For Launch");  
   }       

   void statusPrepping() 

   {  StatusL.setBackground(Color.red); 
      StatusL.setForeground(Color.yellow); 
      StatusL.setText("Prepping");  
   }  

   void statusLaunched() 

   {  StatusL.setBackground(Color.black); 
      StatusL.setForeground(Color.yellow); 
      StatusL.setText("Launched!");  
   }  

   boolean checkPrepped() 

   {  boolean p;  

      p  = DT.getText().length() > 0; 
      p &= MT.getText().length() > 0; 
      p &= CdRT.getText().length() > 0;
      p &= CdCT.getText().length() > 0;
      p &= ImT.getText().length() > 0; 
      p &= ThT.getText().length() > 0; 
      p &= DlT.getText().length() > 0; 
      p &= ChT.getText().length() > 0; 
      if (p) statusPrepped(); 
      else statusPrepping(); 
      return(p); 
   }  


}