package buaa.polyRoute;

import java.util.Vector;

public class para_RT_Design {
    public PointAlg startPoint;
    public PointAlg endPoint;
    public Vector<PointAlg> missionZone = new Vector<>();//任务区
    public int nCruiseHeight;	//	巡航高度离地高度
    public double drouteWidth;	//  航线间距
    public double dangle;		//  初始方向，正北为0，顺时针为正
    public double dexpandW;
}
