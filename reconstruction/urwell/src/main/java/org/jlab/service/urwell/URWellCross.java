package org.jlab.service.urwell;

import java.util.ArrayList;
import java.util.List;
import org.jlab.geom.prim.Plane3D;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;

/**
 * URWell V-W clusters
 * @author devita
 */
public class URWellCross {

    private int id;
    
    private int sector;
    private int region;
    private int chamber;
    
    private int cluster1;
    private int cluster2;
    
    private Point3D cross;
    private double  energy;
    private double  time;
    private int     status;
    
    
    
    public URWellCross(URWellCluster c1, URWellCluster c2) {
        
        Vector3D  dir = c1.getLine().direction().cross(c2.getLine().direction());
        Plane3D plane = new Plane3D(c1.getLine().origin(), c1.getLine().direction().cross(dir));
        Point3D point = new Point3D();
        int nint = plane.intersectionSegment(c2.getLine(), point);
        if(nint==1) {
            this.sector = c1.getSector();
            this.region = (c1.getLayer()-1)/(URWellConstants.NLAYER/URWellConstants.NREGION)+1;
            this.cross  = point;
            this.energy = c1.getEnergy() + c2.getEnergy();
            this.time   = (c1.getTime() + c2.getTime())/2;
            this.cluster1 = c1.getId();
            this.cluster2 = c2.getId();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public int getSector() {
        return this.sector;
    }
    
    public int getRegion() {
        return this.region;
    }
    
    public int getChamber() {
        return this.chamber;
    }
    
    public int getCluster1() {
        return cluster1;
    }

    public int getCluster2() {
        return cluster2;
    }

    public Point3D point() {
        return cross;
    }   

    public double getEnergy() {
        return energy;
    }

    public double getTime() {
        return time;
    }
    
    public int getStatus() {
        return status;
    }

    public static List<URWellCross> createCrosses(List<URWellCluster> clusters) {
        
        List<URWellCross> crosses = new ArrayList<>();
        
        for(int is=0; is<URWellConstants.NSECTOR; is++) {
            for(int ir=0; ir<URWellConstants.NREGION; ir++) {
                List<URWellCluster> clustersV = URWellCluster.getClusters(clusters, is+1, (URWellConstants.NLAYER/URWellConstants.NREGION)*ir+1);
                List<URWellCluster> clustersW = URWellCluster.getClusters(clusters, is+1, (URWellConstants.NLAYER/URWellConstants.NREGION)*ir+2);
                
                for(URWellCluster v : clustersV) {
                    for(URWellCluster w : clustersW) {
                        
                        if(v.getChamber()==w.getChamber()) {
                            URWellCross cross = new URWellCross(v, w);
                            if(cross.point()!=null) crosses.add(cross);
                            cross.setId(crosses.size());
                        }
                    }
                }
            }
        }
        return crosses;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("----> cross  ( %3d %3d )\n", this.getSector(),this));
        str.append(this.point().toString());
        return str.toString();
    }
}
