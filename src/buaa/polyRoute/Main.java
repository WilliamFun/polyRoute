package buaa.polyRoute;

import java.util.Vector;

public class Main {

    public static void main(String[] args) {
	// write your code here
        para_RT_Design para = new para_RT_Design();
        para.dangle = 45;
        para.dexpandW= 100;
        para.drouteWidth = 300;
        para.nCruiseHeight = 100;
        para.startPoint = new PointAlg(31.9958358,118.7314423,0); //118.7314423,31.9958358,0
        para.endPoint = new PointAlg();
        para.missionZone.add(new PointAlg(32.0040086,118.7459812, 0)); //118.7459812,32.0040086,0
        para.missionZone.add(new PointAlg(31.9915343,118.7371202, 0)); //118.7371202,31.9915343,0
        para.missionZone.add(new PointAlg(31.9812968,118.7523474, 0)); //118.7523474,31.9812968,0
        para.missionZone.add(new PointAlg(31.9923086,118.7649077, 0)); //118.7649077,31.9923086,0

        RouteRegion route = new RouteRegion();
        int result = 0;
        Vector<PointAlg > resRoute = route.clacFlyRoute(para, result);
        System.out.printf("结果%d\n", result);
        for (PointAlg pointAlg : resRoute) {
            System.out.printf("航线属性：%.6f %.6f %.1f \n", pointAlg.latitude, pointAlg.longitude, pointAlg.height);
        }
        //system("pause");

    }
}
