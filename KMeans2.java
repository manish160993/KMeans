/*** Author :Manish Biyani
The University of Texas at Dallas
*****/


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Math.*;
 

public class KMeans2 {
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }
	
	static int[] calculateMinimumDistance(int[][] RGB,int chosen_cluster[][]){
		int[] dis = new int[RGB.length];
		int k= chosen_cluster.length;
		for(int j=0; j<RGB.length; j++)
		{
			int min=Integer.MAX_VALUE;
			int index=0;
		   
			for(int i=0; i<k; i++){
				int dist= (int)Math.sqrt(Math.pow(chosen_cluster[i][0]-RGB[j][0],2)+Math.pow(chosen_cluster[i][1]-RGB[j][1],2)+Math.pow(chosen_cluster[i][2]-RGB[j][2],2));
				
				if(min>dist)
				{
					min=dist;
					index=i;
				}
					
			}
			dis[j]= index;
		}
		return dis;
	}

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){

        int[][] RGB= new int[rgb.length][3];
        
		//Separate the RGB values and store them in the array
		
        for(int i=0; i<rgb.length; i++){
			Color c = new Color(rgb[i]);			
            RGB[i][0]=c.getRed();
            RGB[i][1]=c.getGreen();
            RGB[i][2]=c.getBlue();
			
        }
		// Choose the cluster and store respective RGB values
		
        int[] clusters= new int[k];
        int[][] chosen_cluster= new int[k][3];
		
        for(int i=0; i<k; i++){
            int val = (int)Math.floor(Math.random() * rgb.length);
            clusters[i]=rgb[val];
			System.out.println("Cluster"+i+" is: "+clusters[i]);
            chosen_cluster[i][0]= RGB[val][0];
			chosen_cluster[i][1]= RGB[val][1];
			chosen_cluster[i][2]= RGB[val][2];
           
        } 
		Boolean changed= true;
		
		int[] clusterGroup1=calculateMinimumDistance(RGB,chosen_cluster);
		int[] clusterGroup2=calculateMinimumDistance(RGB,chosen_cluster);
        while(changed)
        {
            int[] clusterGroup= calculateMinimumDistance(RGB,chosen_cluster);
            //Change the cluster center to the average of its clusterGroup points 
            for (int j=0; j<k; j++){
                
				int n=0;
                int red=0;
                int green=0;
                int blue=0;
                for(int b=0; b<clusterGroup.length; b++){
                    if(clusterGroup[b]==j){
                        n++;
                        red += RGB[b][0];
                        green += RGB[b][1];
                        blue += RGB[b][2];

                    }
                }
				if(n!=0){
                chosen_cluster[j][0]=red/n;
                chosen_cluster[j][1]=green/n;
                chosen_cluster[j][2]=blue/n;
				}
            }
            clusterGroup2 = calculateMinimumDistance(RGB,chosen_cluster);
            changed= false;
            for(int r=0; r<clusterGroup.length; r++){
                if(clusterGroup[r]!=clusterGroup2[r]){
                    changed=true;
                    break;
                }
            }
        }
        //update rgb values
        for(int y=0; y<clusters.length; y++){
            clusters[y] = new Color(chosen_cluster[y][0],chosen_cluster[y][1],chosen_cluster[y][2]).getRGB();
        }
        for(int u=0; u<clusterGroup2.length; u++){
            int ind= clusterGroup2[u];
            rgb[u]=clusters[ind];
        }
    }
}