// 
// Launcher is the main applet class for the rocket flight simulation 
// 
// Launcher uses two other classes:  ControlPanel to handle user input, 
// and Tracker to plot the simulation.  
// 
// Launcher has six methods of its own to handle the central tasks 
// 
//    init()   starts up and initializes the applet, like all applets do 
//    insets() puts a 10-pixel margin on the applet so it looks nice 
// 
//    update() takes input from the control panel and sets simulation variables 
//             this method is activated whenever input is detected from one 
//             of the ControlPanel textFields.  
// 
//    launch() kicks off the simulation, then displays the results through 
//             ControlPanel and Tracker.  
// 
//    trajectory() is the actual routine with the simulation code.  It produces 
//             three arrays with the resulting altitude, velocity and 
//             acceleration for the entire simulation period.  
// 
//    report() takes newly entered time values, updates the displayed  
//             altitude, velocity, and acceleration values, and then replots 
//             the flight curve through Tracker.  
// 


import java.awt.*; 

public class Launcher extends java.applet.Applet 
  
{  ControlPanel P; 
  Tracker T;
  double D,Mr,I,Ft,CdR, CdC,tc,ChD,tb;            // rocket diameter, rocket mass, impulse, thrust, 
  static double delayCharge, chuteTime;     // coef of drag Rocket and chute, ejection delay, and chute diameter, time of burn,
  //time from launch to charge
  // time till chute deploy
  
  static int chuteIndex;                    // index of chute deploy point
  
  
  double maxHeightTime;                  // find the time of macimum altitude
  double[] v,a,y;                        // velocity, acceleration, and altitude arrays 
  double[] theoryV, theoryA, theoryY;    // arrays for if the chute doesn't deploy
  double theoryYmax, theoryVmax;
  int theoryImax;
  
  final static double dt = 0.005;       // time increment for simulation 
  final static double g  = 9.8;        // acceleration of gravity 
  final static double Mc = 0.0107;     // mass of an engine case (Estes standard motors) 
  final static int mAS   = 80001;      // size of simulation: simulation time is dt*mAS 
  // stands for "max Array Size"  
  
  boolean crash = false, check = false;// does the rocket land too hard?
  boolean chuteDeployed = false; // has the chute been deployed
  
  public void init() 
    
  {  int h,w; 
    
    h = getSize().height; 
    w = getSize().width; 
    
    setLayout(new GridLayout(2,1,10,10)); 
    
    P = new ControlPanel(this); 
    T = new Tracker(this, h/2, w-20); 
    
    add(P);
    add(T); 
    
    
    v = new double[mAS]; 
    a = new double[mAS]; 
    y = new double[mAS]; 
    
    theoryY = new double[mAS];
    theoryV = new double[mAS];
    theoryA = new double[mAS];
    
    
    
  } 
  
  public Insets insets() 
    
  {
    return new Insets(10,10,10,10); 
  }  
  
  void update(TextField f) 
    
    // ControlPanel calls this procedure whenever it detects that an entry 
    // has been made into a textField, or a textField has lost focus 
    
  {  double entry = 0; 
    boolean goodNumber; 
    
    goodNumber = f.getText().length() > 0; 
    if (goodNumber && f.isEditable()) 
      try { entry = Double.valueOf(f.getText()).doubleValue(); } 
    catch(NumberFormatException e) 
    {  f.setText(""); 
      System.err.println("No number found " + e.getMessage()); 
      goodNumber = false; 
    } 
    if (goodNumber)  
    {  if (f == P.DT) D = entry/1000;       // Get diameter in m 
      else if (f == P.MT) Mr = entry/1000; // Get mass in kg 
      else if (f == P.CdRT) CdR = entry;
      else if (f == P.CdCT) CdC = entry;
      else if (f == P.ImT) I  = entry;
      else if (f == P.ThT) Ft = entry;
      else if (f == P.DlT) tc = entry;
      else if (f == P.ChT) ChD = entry/1000; // Get diameter in m 
      if (f.isEditable()) f.setText(String.valueOf(entry)); 
    }
  }  
  
  void launch() 
    
  {  int idx, imax; 
    double ymax; 
    
    System.out.println("\n\n\n\n\n"); // clear the log window
    
    trajectory();                  // !!!!! Runs the Simulation !!!!!  
    
    ymax = 0;                      // Find the max altitude, and 
    imax = 0;                      // the index (= time) where it occurs 
    for (idx=0; idx<mAS; idx++) 
      if (y[idx]>ymax) 
    {  imax = idx; 
      ymax = y[idx];
    } 
    
    theoryYmax = 0;                      // Find the max altitude, and 
    theoryImax = 0;                           // the index (= time) where it occurs 
    for (idx=0; idx<mAS; idx++) 
      if (theoryY[idx]>theoryYmax) 
    {  theoryImax = idx; 
      theoryYmax = theoryY[idx];
    } 
    
    maxHeightTime = theoryImax*dt;
    double minDeployTime = 0, maxDeployTime = 0, bestDeployTime = 0;
    int  deployMax = 20, deployMin = -10;
    
    for (idx = theoryImax; idx > 0; idx --){
      if (theoryV[idx] > deployMax){
        minDeployTime = (idx*dt);
        break;
      }
    }
    
    for (idx = theoryImax; idx > 0; idx --){
      if (theoryV[idx] > 5){
        bestDeployTime = (idx*dt);
        break;
      }
    }
    
    for (idx = theoryImax; idx < mAS; idx ++){
      if (theoryV[idx] < deployMin){
        maxDeployTime = (idx*dt);
        break;
      }
    }
    
    
    boolean earlyDeploy = theoryV[chuteIndex] > deployMax;
    boolean lateDeploy = theoryV[chuteIndex] < deployMin;
    
    String range = ("from " + Math.ceil((minDeployTime-tb)*10)/10.0 + "s to " + 
                    Math.floor((maxDeployTime-tb)*10)/10.0 + "s");
    String best = ("Best time:" + Math.round((bestDeployTime-tb)*100)/100.0 +"s");
    
    if(earlyDeploy){
      System.err.println("Early chute deployment");
      System.err.println("Set delay time "+ range + "\n" + best);
    }
    
    else if(lateDeploy){
      System.err.println("Late chute deployment");
      System.err.println("Set delay time "+ range + "\n" + best);
    }
    
    else if(!earlyDeploy && !lateDeploy){
      System.out.println("Good chute deployment at " + Math.round((chuteTime-tb)*100)/100.0 + "s after burn");
      System.out.println("Within range " + range);
    }
    
    
    // find the touch down time:
    double ymin = 200;
    int imin = 0;
    for (idx=1; idx<mAS; idx++) 
      if (y[idx]<ymin){ 
      imin = idx; 
      ymin = y[idx];
      if(Math.ceil(ymin) == 0){
        break;
      }
    } 
    System.out.println("Touchdown:" + Math.round((idx*dt*10))/10.0 + "s");
    
    T.update(y,v,a,dt,(double)imin*dt/2);        // Plot the altitude trace 
    
    P.launched = true;                      // Let everyone know that there's 
    P.statusLaunched();                     // valid simulation data available 
    
    P.TmL.setBackground(Color.black);       // Set up for entry of time values 
    P.TmL.setForeground(Color.yellow); 
    P.TmL.setText("Enter Time"); 
    P.TmT.setEditable(true); 
    
    P.TmT.setText(String.valueOf("Max height:" + (float)Math.round(100*imax*dt)/100) + " s");  
    P.AltT.setText(String.valueOf((float)Math.round(100*y[imax])/100) + " m");  
    P.VelT.setText(String.valueOf((float)Math.round(100*v[imax])/100) + " m/s");  
    P.AccT.setText(String.valueOf((float)Math.round(100*a[imax])/100) + " m/s/s");  
  }  
  
  
  void trajectory() 
    
    // THE HEART OF THE SIMULATION 
    // See web page at http://www.execpc.com/~culp/rockets/rckt_sim.html 
    // for explanation of how this algorithm works 
    
  {  int idx; 
    double area,A,M,M0,Mp,dM,F,Fd,rho,t,ChA,v0; 
    
    double theoryRho, theoryV0, theoryFd, theoryF;
    
    // First compute constant values 
    
    Mp = I/800.0;                         // Estimate mass of propellant (black powder) 
    tb = I/Ft;                            // Estimate burn time 
    dM = Mp/tb;                           // Compute mass decrement 
    A  = Math.PI*D*D/4;                   // Compute area of rocket body 
    ChA = Math.PI*ChD*ChD/4;              // Compute area of chute 
    delayCharge = tb + tc;
    
    if(ChA < A){
      System.err.println("Warning chute surface area smaller than rocket!");
    }
    
    // Set the initial values for loop variables 
    
    M0 = Mr+Mc+Mp;                                  // initial mass 
    y[0] = v[0] = a[0] = 0;                         // time zero at rest on the ground 
    theoryY[0] = theoryV[0] = theoryA[0] = 0;
    
    // Start the simulation loop 
    
    for(idx=1; idx<mAS; idx++){
      rho = 1.22*Math.pow(0.9,y[idx-1]/1000); 
      theoryRho = 1.22*Math.pow(0.9,theoryY[idx-1]/1000);
      
      v0 = v[idx-1];
      
      theoryV0 = theoryV[idx-1];
      
      t  = idx*dt;  
      if((t >= tb+tc) && !chuteDeployed){
        chuteDeployed = true;
        chuteTime = t;
        chuteIndex = idx;
      }
      
      M  = t > tb ? Mr + Mc : M0 - dM*t;   
      area = t < tb+tc ? A : Math.max(A,ChA);
      if(chuteDeployed){
        Fd = v0 == 0 ? 0 : 0.5*rho*CdC*area*v0*v0*v0/Math.abs(v0); 
      }
      else{
        Fd = v0 == 0 ? 0 : 0.5*rho*CdR*A*v0*v0*v0/Math.abs(v0); 
      }
      
      theoryFd = theoryV0 == 0 ? 0: 0.5*theoryRho*CdR*A*theoryV0*theoryV0*theoryV0/Math.abs(theoryV0);
      
      F  = t > tb ? -Fd-M*g : Ft-Fd-M*g; 
      
      
      // integration
      a[idx] = F/M; 
      v[idx] = v[idx-1] + a[idx]*dt; 
      y[idx] = y[idx-1] + v[idx]*dt; 
      
      
      if (y[idx] < 0){ 
        y[idx] = 0;  
        if(v[idx] < -6 && !crash && chuteDeployed){
          System.err.println("Dangerous landing speed:" +Math.ceil(-v[idx]) + "m/s\nShould be less than 6 m/s");
          double chuteSuggest = Math.sqrt(M * g *2 / (9*Math.PI*CdC*1.22))*(1000); // times by 1000 to get mm
          
          System.err.println("Chute should be at least " + Math.ceil(chuteSuggest) + "mm diameter");
          crash = true;
        }
        if(!chuteDeployed && !crash){
          System.err.println("Warning: Chute never deployed!");
          crash = true;
        }
        else if(!check&&!crash){
          System.out.println("Safe landing speed");
          check = true;
        }
        v[idx] = 0; 
      }
      
      // what if the chute isn't used?
      theoryF = t > tb ? -theoryFd-M*g : Ft - theoryFd - M *g;
      theoryA[idx] = theoryF/M;
      theoryV[idx] = theoryV[idx-1] + theoryA[idx]*dt;
      theoryY[idx] = theoryY[idx-1] + theoryV[idx]*dt;
      
      
    } 
    crash =check = chuteDeployed = false;
  }
  
  void report(boolean launched) 
    
    // ControlPanel calls this procedure whenever it detects that an entry 
    // has been made into the TIME textField 
    
  {  if (launched) 
    {  double entry = 0;  
    boolean goodNumber; 
    int idx; 
    
    goodNumber = P.TmT.getText().length() > 0; 
    if (goodNumber) 
      try { entry = Double.valueOf(P.TmT.getText()).doubleValue(); } 
    catch(NumberFormatException e) 
    {  P.TmT.setText(""); 
      System.err.println("No number found " + e.getMessage()); 
      goodNumber = false; 
    } 
    
    if (goodNumber) 
    {  idx = Math.min(Math.round((float)(entry/dt)),mAS-1);  
      
      P.TmT.setText(String.valueOf((float)Math.round(100*idx*dt)/100) + " s");  
      P.AltT.setText(String.valueOf((float)Math.round(100*y[idx])/100) + " m");  
      P.VelT.setText(String.valueOf((float)Math.round(100*v[idx])/100) + " m/s");  
      P.AccT.setText(String.valueOf((float)Math.round(100*a[idx])/100) + " m/s/s");  
    } 
    T.update(y,v,a,dt,Math.min(entry,(double)(mAS-1)/100)); 
  } 
  } 
  
}  
