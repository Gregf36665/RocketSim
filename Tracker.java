/* Tracker is the canvas class on which the rocket trajectory is drawn */ 

import java.awt.*; 
import java.util.*;

public class Tracker extends Canvas 
  
{  Launcher L; 
  int h,w,psz,cursor; // height & width of the tracker; plot size  
  final int m=25;     // margin for plot 
  int[] xp,yp, vp, ap, scale;        // arrays used to hold plot curve coordinates 
  boolean plotExists; // tells whether a curve exists to plot 
  double tmax; // find the maximum time plotted
  double xPicSec, yPicM; // pixles per second for x and pixles per meter for y
  int maxYxRAW, maxYRAW, maxVRAW, maxVxRAW, maxARAW, maxAxRAW; // set the raw maximum values of x and y for y v and a
  int delayCharge;
  
  double vMax; // maximum velocity
  double time = 0; // time elapsed
  int index; // change in time between each index
  
  int maxX, maxYx, maxYv, maxV, highY;
  
  Tracker(Launcher parent, int height, int width) 
    
  {  int idx; 
    L = parent; 
    setBackground(Color.darkGray); 
    setForeground(Color.yellow); 
    h = height;
    w = width;  
    psz = w-2*m; 
    cursor = m + psz/2; 
    xp = new int[psz]; 
    yp = new int[psz];
    vp = new int[psz];
    ap = new int[psz];
    plotExists = false; 
    
  }  
  
  public void paint(Graphics g) 
    
  {  int idx; boolean maxSet = false;
    
    h = getSize().height;
    w = getSize().width;
    
    g.drawLine(m,m,m,h-m);  //vertical axis 
    g.drawLine(m,h-m,w-m,h-m); //horizontal axis
    


    
    // draw altitude
    if (plotExists){
     
      // get the raw max values:
      
      
      for(idx=1; idx<psz; idx++){
        maxYxRAW = (int)(xp[idx-1]);
        maxYRAW = (int)(yp[idx-1]);
        if(yp[idx]>yp[idx-1]){
          break;
        }
      }     
      
      for(idx=1; idx<psz; idx++){
          maxVxRAW = (int)(xp[idx-1]);
          maxVRAW = (int)(vp[idx-1]);
          if(vp[idx]>vp[idx-1]){
          break;
        }
      }
      
      for(idx=1; idx<psz; idx++){
        maxAxRAW = (int)(xp[idx-1]);
        maxARAW = (int)(ap[idx-1]);
        if(ap[idx]<ap[idx-1]){
          break;
        }
      }
      
                         
      // find the scale
      
      double xScale = scale(psz,(w-2*m));
      //System.out.println("X scale:" + xScale);
      
      // add in the x markings
      xPicSec = (w-2*m)/tmax;
      double spacingX;
      double xStep;
      xStep = xPicSec > 200 ? 0.2:
        xPicSec > 100 ? 0.5:
        xPicSec > 50 ? 1:
        xPicSec > 25 ? 2 : 
        xPicSec > 10 ? 5:
        xPicSec > 5 ? 10: 20;
       
      spacingX = xPicSec * xStep;
      
      double value = 0;
      for(double x=m; x< w-m; x+= spacingX){
        g.drawLine((int)x,h-m,(int)x,h-m-5);
        value = Math.round(value * 100) / 100.0 ;
        g.drawString("" + value, (int)x, h-m);
        value += xStep;
      }
      
      double yMax = -Math.min(maxYRAW,maxVRAW);
      double yScale = scale(yMax,(h-2*m));
      //System.out.println("Y scale:" + yScale);
     
      yPicM = ((h-2*m)/yMax);
      double spacingY;
      double yStep;
      yStep = yPicM > 2 ? 10:
        yPicM > 1 ? 20:
        yPicM > 0.5 ? 50:
        yPicM > 0.25 ? 100 : 
        yPicM > 0.10 ? 200:
        yPicM > 0.05 ? 500: 1000;
       
      spacingY = yPicM * yStep;
      
      value = 0;
      for(double y=h-m; y > m; y-= spacingY){
        g.drawLine(m,(int)y,m+5,(int)y);
        value = Math.round(value * 100) / 100.0 ;
        g.drawString("" + value, m, (int)y-1);
        value += yStep;
      }

      
      
      // draw altitude flight path
      g.setColor(Color.blue);
      for (idx=1; idx<psz; idx++) {
        
        if (yp[idx]>yp[idx-1] && !maxSet){
          
          maxX = (int) (m+(maxYxRAW*xScale));
          maxYx = (int)(maxYRAW*yScale)+h-m;
          maxSet = true;
         
          // draw orange lines at the max height and time intersection
          g.setColor(Color.orange); 
          // make dotted lines:
          int gap = 10;
          int length = 3;
          g.drawString(-maxYRAW + "m", Math.max(maxX - 40,m+10), maxYx + 12);
          for (int lineDash = m; lineDash<maxX-length; lineDash += gap){
            g.drawLine(lineDash,maxYx,lineDash + length,maxYx);// horizontal line 
          }
          for (int lineDash = maxYx; lineDash<h-m-length; lineDash += gap){
            g.drawLine(maxX,lineDash,maxX,lineDash+length); // vertical line
          }
          
          g.setColor(Color.red);
        }
        g.drawLine(m+(int)(xp[idx-1]*xScale),(int)(yp[idx-1]*yScale+h-m),m+(int)(xp[idx]*xScale),(int)((yp[idx]*yScale+h-m))); 
      }
      
      // draw velocity
      g.setColor(Color.green);
      maxSet = false;
      for (idx=1; idx<psz; idx++) {
       
        if (vp[idx]>vp[idx-1] && !maxSet){
          
          maxV = m+(int)(maxVxRAW*xScale);
          maxYv = (int)(maxVRAW*yScale)+h-m;
          maxSet = true;
          
          
          // draw orange lines at the max height and time intersection
          g.setColor(Color.orange); 
          // make dotted lines:
          int gap = 10;
          int length = 3;
          g.drawString(-maxVRAW + "m/s", Math.max(maxV+1,m+15), h-m-15);
          for (int lineDash = m; lineDash<maxV-length; lineDash += gap){
            g.drawLine(lineDash,maxYv,lineDash + length,maxYv);// horizontal line 
          }
          for (int lineDash = maxYv; lineDash<h-m-length; lineDash += gap){
            g.drawLine(maxV,lineDash,maxV,lineDash+length); // vertical line
          }
          
          Color darkGreen = new Color(0,150,0);
          g.setColor(darkGreen);
        }
        g.drawLine(m+(int)(xp[idx-1]*xScale),(int)(vp[idx-1]*yScale)+h-m,m+(int)(xp[idx]*xScale),(int)(vp[idx]*yScale)+h-m);
      }
      
      
      
      
      // find when the chute deploys
      
      delayCharge = (int) (Launcher.delayCharge*xPicSec);
      g.setColor(Color.red);
      int gap = 10;
      int length = 5;
      for (int lineDash = m; lineDash<h-m-length; lineDash += gap){
        if(lineDash < (h-m)/2-25 || lineDash > (h-m)/2){
            g.drawLine(m+delayCharge,lineDash,m+delayCharge,lineDash+length);// vertical line 
          }
      }
      
     g.setColor(Color.orange);
     g.drawString("Parachute",delayCharge+m-50,(h-m)/2-12);
     g.drawString("deployed:" + Math.round(Launcher.delayCharge*100)/100.0 + "s",delayCharge+m-50,(h-m)/2);
    }
    

    
    // add lables in
    
    g.setColor(Color.white); 
    g.drawString("Rocket Tracker", w/2-35,m-5);
    g.drawString("time (s)", w/2-35,h-5);
    
    g.setColor(Color.blue);
    int spacing = 15;
    final int initialSpacing = spacing;
    int initial = (h-130 + m);
    g.drawString("h",m-21,initial+2);
    g.drawString("e",m-20,initial + spacing+2);
    spacing += initialSpacing;
    g.drawString("i",m-20,initial + spacing+1);
    spacing += initialSpacing;
    g.drawString("g",m-21,initial + spacing);
    spacing += initialSpacing;
    g.drawString("h",m-21,initial + spacing+1);
    spacing += initialSpacing;
    g.drawString("t",m-21,initial + spacing);
    spacing = -30;
    
    g.setColor(Color.green);
    g.drawString("v",m-11,initial+spacing);
    spacing += initialSpacing;
    g.drawString("e",m-11,initial + spacing);
    spacing += initialSpacing;
    g.drawString("l",m-10,initial + spacing);
    spacing += initialSpacing;
    g.drawString("o",m-11,initial + spacing);
    spacing += initialSpacing;
    g.drawString("c",m-11,initial + spacing);
    spacing += initialSpacing;
    g.drawString("i",m-10,initial + spacing);
    spacing += initialSpacing;
    g.drawString("t",m-10,initial + spacing);
    spacing += initialSpacing;
    g.drawString("y",m-11,initial + spacing);
    spacing += initialSpacing;
    
    
    
  }  
  
  void update(double[] y, double[] v, double[] a,double dt, double t) 
    
  {  int idx,asz; 
    double scale; 
    time = t;
    delayCharge = 0;
    
    asz = y.length; 
    tmax = Math.min(2*t,(double)(asz-1)/200); 
    
    if(tmax != 2*t){
      System.err.println("Too large of a time period,\nset time to " + (asz-1)/400 + "s");
    }
    cursor = m+(int)Math.round(psz*t/tmax); 
    
    index = (int)Math.round(tmax/dt); 
    
     
     h = getSize().height;
     w = getSize().width;
     
    for (idx=0; idx<psz; idx++) 
    {  xp[idx] = idx; 
      index   = (int)Math.round((tmax/dt)*(double)idx/psz); 
      yp[idx] = -(int)Math.round(y[index]);
      vp[idx] = -(int)Math.round(v[index]);
      ap[idx] = (int)Math.round(a[index]);

    } 
    plotExists = true; 
    repaint(); 
    
  }
  
  static double maxArray(double[] array){
    try{
      for(int i = 1; i<array.length;i++){
        if(array[i]>array[i-1]){
          return array[i-1];
        }
      }
      return array[array.length];
    }
    catch(ArrayIndexOutOfBoundsException e){
      System.err.println(e +" double");
      return array[array.length];
    }
  }
  
  
  static int maxArrayI(int[] array){
    System.out.println(array.length);
    try{
      for(int i = 1; i<array.length;i++){
        if(array[i]>array[i-1]){
          return array[i-1];
        }
      }
      return -150;
    }
    catch(ArrayIndexOutOfBoundsException e){
      System.err.println(e + " int");
      return -150;
    }
  }
  
  
  double scale(double max, int length){
    return (double) length/max;
  }
}     