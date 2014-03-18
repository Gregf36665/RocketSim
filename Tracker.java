/* Tracker is the canvas class on which the rocket trajectory is drawn */ 

import java.awt.*; 

public class Tracker extends Canvas 
  
{  Launcher L; 
  int h,w,psz,cursor; // height & width of the tracker; plot size  
  final int m=25;     // margin for plot 
  int[] xp,yp, vp;        // arrays used to hold plot curve coordinates 
  boolean plotExists; // tells whether a curve exists to plot 
  
  double vMax; // maximum velocity
  double time = 0; // time elapsed
  
  int maxX, maxY, maxV;
  
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
    plotExists = false; 
  }  
  
  public void paint(Graphics g) 
    
  {  int idx; boolean maxSet = false;
    
    h = getSize().height;
    w = getSize().width;
    
    g.drawLine(m,m,m,h-m);  //vertical axis 
    g.drawLine(m,h-m,w-m,h-m); //horizontal axis
    g.setColor(Color.blue);


    
    // draw altitude
    if (plotExists){
      
      // find the scale
      /*System.out.println(h-m);
      System.out.println(maxArray(vp));
      
      System.out.println(scale(((h-m) - maxArray(yp)),h));
          */
      /*
      System.out.println("X max:" + time);
      System.out.println("Y max: " + maxArray(yp));
      System.out.println("V max:" + maxArray(vp));
      System.out.println("X-axis size:" + (w - 2*m));
      System.out.println("Y-axis size:" + (h-2*m));
      */
      // calculate the scale
      
      double xScale = ((w-2*m)/(double)psz);
      //System.out.println("X scale:" + xScale);
      
      double yScale = ((double)(h-2*m)/-(double)maxArray(yp));
      //System.out.println("Y scale:" + yScale);
      
      
      for (idx=1; idx<psz; idx++) {
        g.drawLine(m+(int)(xp[idx-1]*xScale),(int)(yp[idx-1]*yScale+h-m),m+(int)(xp[idx]*xScale),(int)((yp[idx]*yScale+h-m))); 
        if (yp[idx]>yp[idx-1] && !maxSet){
          
          maxX = m+(int)(xp[idx-1]*xScale);
          maxY = (int)(yp[idx-1]*yScale)+h-m;
          maxSet = true;
          //System.out.println("maxX" + maxX + "\nmaxY:" + maxY); //for debugging
         
          // draw orange lines at the max height and time intersection
          g.setColor(Color.orange); 
          // make dotted lines:
          int gap = 10;
          int length = 3;
          //System.out.println("MaxX:" + maxX);
          for (int lineDash = m; lineDash<maxX-length; lineDash += gap){
            g.drawLine(lineDash,maxY,lineDash + length,maxY);// horizontal line 
          }
          for (int lineDash = maxY; lineDash<h-m-length; lineDash += gap){
            g.drawLine(maxX,lineDash,maxX,lineDash+length); // vertical line
          }
          
          g.setColor(Color.red);
        }
      }
      
      // draw velocity
      g.setColor(Color.green);
      maxSet = false;
      for (idx=1; idx<psz; idx++) {
        g.drawLine(m+xp[idx-1],vp[idx-1]+h-m,m+xp[idx],vp[idx]+h-m);
        if (vp[idx]>vp[idx-1] && !maxSet){
          
          maxV = m+xp[idx-1];
          maxY = vp[idx-1]+h-m;
          maxSet = true;
          //System.out.println("maxV:" + maxV + "\nmaxY:" + maxY); //for debugging
          
          // draw orange lines at the max height and time intersection
          g.setColor(Color.orange); 
          // make dotted lines:
          int gap = 10;
          int length = 3;
          for (int lineDash = m; lineDash<maxV-length; lineDash += gap){
            g.drawLine(lineDash,maxY,lineDash + length,maxY);// horizontal line 
          }
          for (int lineDash = maxY; lineDash<h-m-length; lineDash += gap){
            g.drawLine(maxV,lineDash,maxV,lineDash+length); // vertical line
          }
          
          Color darkGreen = new Color(255,100,100);
          g.setColor(darkGreen);
        }
      }
    }
    
    // add lables in
    
    g.setColor(Color.white); 
    g.drawString("Rocket Tracker", w/2-35,m-5);
    g.drawString("time (s)", w/2-35,h-5);
    int spacing = 15;
    final int initialSpacing = spacing;
    int initial = (h-130 + m);
    g.drawString("h",m-12,initial+2);
    g.drawString("e",m-11,initial + spacing+2);
    spacing += initialSpacing;
    g.drawString("i",m-11,initial + spacing+1);
    spacing += initialSpacing;
    g.drawString("g",m-12,initial + spacing);
    spacing += initialSpacing;
    g.drawString("h",m-12,initial + spacing+1);
    spacing += initialSpacing;
    g.drawString("t",m-11,initial + spacing);
    spacing += initialSpacing;
    
    
  }  
  
  void update(double[] y, double[] v, double dt, double t) 
    
  {  int idx,index,asz; 
    double scale,tmax; 
    time = t;
    
    asz = y.length; 
    tmax = Math.min(2*t,(double)(asz-1)/100); 
    cursor = m+(int)Math.round(psz*t/tmax); 
      
    index = (int)Math.round(tmax/dt); 
    
     
     h = getSize().height;
     w = getSize().width;
     
    //System.out.println(scale);
    for (idx=0; idx<psz; idx++) 
    {  xp[idx] = idx; 
      index   = (int)Math.round((tmax/dt)*(double)idx/psz); 
      yp[idx] = -(int)Math.round(y[index]);
      vp[idx] = -(int)Math.round(v[index]);
      maxV = (int) maxArray(v);
      maxY = (int) maxArray(y);
    } 
    plotExists = true; 
    repaint(); 
    
  }
  
  static double maxArray(double[] array){
    for(int i = 1; i<array.length;i++){
      if(array[i]>array[i-1]){
        return array[i-1];
      }
    }
    return array[array.length];
  }
  
  static int maxArray(int[] array){
    for(int i = 1; i<array.length;i++){
      if(array[i]>array[i-1]){
        return array[i-1];
      }
    }
     return array[array.length];
  }
  
  double scale(double max, int length){
    return (double) length/max;
  }
}     