package buaa.polyRoute;

import java.util.Collections;
import java.util.Vector;

import static java.lang.Math.*;

public class RouteRegion {
    public static final double ALG_PI=3.1415926535897932384626;

    public RouteRegion(){}

    public Vector<PointAlg > clacFlyRoute(para_RT_Design input, int res){
        Vector<PointAlg > outResult = new Vector<>();
        Vector<JW> outPoints = new Vector<>();
        Vector<JW> inputRegion = new Vector<>(); // 转化为二维集合

        for(int i = 0; i < input.missionZone.size(); i++){
            JW tmpLonlat = this.lonLat2Mercator(new JW(input.missionZone.get(i).longitude, input.missionZone.get(i).latitude));
            inputRegion.add(tmpLonlat);
        }
        if(inputRegion.size() < 3){
            System.out.println("输入为非多边形，输出为空!\n");
            res = -1;
            return outResult;
        }
        if(input.dexpandW < 0){
            input.dexpandW = 10;
        }

        double crossregion = (inputRegion.get(1).x-inputRegion.get(0).x)*(inputRegion.get(2).y-inputRegion.get(1).y)-(inputRegion.get(1).y-inputRegion.get(0).y)*(inputRegion.get(2).x-inputRegion.get(1).x);

        if(crossregion > 0){
            System.out.println("当前为逆序坐标点集合，需要进行翻转"+inputRegion.size());
            Collections.swap(inputRegion, 0, inputRegion.size()-1); // 倒序输出 保证为顺时针点集合
        }
        Vector<JW > inputRegion_expend = polygon_expand(inputRegion, input.dexpandW);
        inputRegion.clear();
        inputRegion.addAll(inputRegion_expend);
        if(input.dangle <= 0 || input.dangle >= 180) // 为了计算方便，不进行特殊情况方向的计算，
        {
            input.dangle = 1;
        }else if(input.dangle == 90){
            input.dangle = 91;
        }
        double angle = input.dangle;
        if(input.drouteWidth < 10){
            input.drouteWidth = 10;
        }
        double radius = input.drouteWidth;
        System.out.println("输入参数暂时使用了两个，分别是进入角度: "+ angle+" 转弯半径: "+radius);
        // 初始点计算
        double maxb = -99999999999D;//-99999999999
        int num = -1;
        System.out.println("寻找第一个点\n");
        for(int i =0; i < inputRegion.size(); i++){
            double tmpb = inputRegion.get(i).y - tan(angle*ALG_PI/180.0)* inputRegion.get(i).x;
            System.out.printf("第%d次%f %f 计算 b值%f\n", i, inputRegion.get(i).x, inputRegion.get(i).y, tmpb);
            if(tmpb > maxb){
                maxb = tmpb;
                num = i;
            }
        }
        System.out.printf("寻找到的第一个点的序号是：%d\n", num);
        outPoints.add(inputRegion.get(num));
        // 交点计算
        int count = 1;
        while(true){
            double dur_b = 0;
            if(angle > 0 && angle < 90){
                dur_b = radius/cos(angle*ALG_PI/180.0);
            }
            else if(angle > 90 && angle < 180){
                dur_b = radius/sin((angle - 90)*ALG_PI/180.0);
            }
            System.out.printf("当前航线函数为：y = %f x + %f\n", tan(angle*ALG_PI/180.0), maxb - count*dur_b);
            Vector<JW > tmp = new Vector<>();
            for(int i = num; i < inputRegion.size() - 1; i++){
                double st1 = inputRegion.get(i).y - tan(angle*ALG_PI/180.0)* inputRegion.get(i).x - maxb + count*dur_b;
                double st2 = inputRegion.get(i + 1).y - tan(angle*ALG_PI/180.0)* inputRegion.get(i + 1).x - maxb + count*dur_b;
                System.out.printf("开始计算碰撞交点,范围：num - len(poly): %d %f %f\n", i, st1, st2);
                if(st1 == 0){
                    tmp.add(new JW(inputRegion.get(i).x, inputRegion.get(i).y));
                    System.out.printf("1定点碰撞 %d %f %f\n", i, inputRegion.get(i).x, inputRegion.get(i).y);
                }
                else if(st2 == 0){
                    tmp.add(new JW(inputRegion.get(i + 1).x, inputRegion.get(i + 1).y));
                    System.out.printf("2定点碰撞 %d %f %f\n", i+1, inputRegion.get(i + 1).x, inputRegion.get(i + 1).y);
                }
                else if( (st1*st2) < 0){ //
                    JW crs = cross_point(0, maxb - count*dur_b, 1, tan(angle*ALG_PI/180.0) + maxb - count*dur_b, inputRegion.get(i).x, inputRegion.get(i).y, inputRegion.get(i + 1).x, inputRegion.get(i + 1).y);
                    tmp.add(crs);
                    System.out.printf("3交点碰撞 %d-%d %f %f\n", i,i+1, crs.x, crs.y);
                }
                else{
                    System.out.println("1无交点");
                }
            }
            // # 计算结尾+开头的连线
            System.out.println("开始计算碰撞交点,范围：len(poly) - 0");
            double st1 = inputRegion.get(inputRegion.size() - 1).y - tan(angle*ALG_PI/180.0)* inputRegion.get(inputRegion.size() - 1).x - maxb + count*dur_b;
            double st2 = inputRegion.get(0).y - tan(angle*ALG_PI/180.0)* inputRegion.get(0).x - maxb + count*dur_b;
            if(st1 == 0){
                tmp.add(new JW(inputRegion.get(inputRegion.size() - 1).x, inputRegion.get(inputRegion.size() - 1).y));
                System.out.printf("4定点碰撞 %d %f %f\n", inputRegion.size() - 1, inputRegion.get(inputRegion.size() - 1).x, inputRegion.get(inputRegion.size() - 1).y);
            }
            else if(st2 == 0){
                tmp.add(new JW(inputRegion.get(0).x, inputRegion.get(0).y));
                System.out.printf("5定点碰撞 %d %f %f\n", 0, inputRegion.get(0).x, inputRegion.get(0).y);
            }
            else if( (st1*st2) < 0){ //
                // line1 = [0, maxb - count*dur_b, 1,  tan(angle*ALG_PI/180.0) + maxb - count*dur_b]
                // line2 = [poly[i][0], poly[i][1], poly[i+1][0], poly[i+1][1]]
                // crs = cross_point(line1, line2)
                // tmp.push_back((crs[0], crs[1]));
                JW crs = cross_point(0, maxb - count*dur_b, 1, tan(angle*ALG_PI/180.0) + maxb - count*dur_b, inputRegion.get(inputRegion.size() - 1).x, inputRegion.get(inputRegion.size() - 1).y, inputRegion.get(0).x, inputRegion.get(0).y);
                tmp.add(crs);
                System.out.printf("6交点碰撞 %d-%d %f %f\n", inputRegion.size() - 1, 0, crs.x, crs.y);
            }
            else{
                System.out.println("2无交点");
            }
            for(int i = 0; i < num; i++){
                st1 = inputRegion.get(i).y - tan(angle*ALG_PI/180.0)* inputRegion.get(i).x - maxb + count*dur_b;//double 重新定义
                st2 = inputRegion.get(i + 1).y - tan(angle*ALG_PI/180.0)* inputRegion.get(i + 1).x - maxb + count*dur_b;//double 重新定义
                System.out.printf("开始计算碰撞交点,范围：num - len(poly): %d %f %f\n", i, st1, st2);
                if(st1 == 0){
                    tmp.add(new JW(inputRegion.get(i).x, inputRegion.get(i).y));
                    System.out.printf("7定点碰撞 %d %f %f\n", i, inputRegion.get(i).x, inputRegion.get(i).y);
                }
                else if(st2 == 0){
                    tmp.add(new JW(inputRegion.get(i + 1).x, inputRegion.get(i + 1).y));
                    System.out.printf("8定点碰撞 %d %f %f\n", i+1, inputRegion.get(i + 1).x, inputRegion.get(i + 1).y);
                }
                else if( (st1*st2) < 0){ //
                    JW crs = cross_point(0, maxb - count*dur_b, 1, tan(angle*ALG_PI/180.0) + maxb - count*dur_b, inputRegion.get(i).x, inputRegion.get(i).y, inputRegion.get(i + 1).x, inputRegion.get(i + 1).y);
                    tmp.add(crs);
                    System.out.printf("9交点碰撞 %d-%d %f %f\n", i,i+1, crs.x, crs.y);
                }
                else{
                    System.out.println("3无交点");
                }
            }

            if(tmp.size() <= 0){
                break;
            }
            if(count%2 == 1){
                outPoints.addAll(tmp);
            }
            else{
                for(int i = 0; i < tmp.size(); i++){
                    outPoints.add(tmp.get(tmp.size() - 1 - i));
                }
            }
            count++;

        }
        if(outPoints.size() > 0){
            System.out.println("计算结果不为空");
        }
        else
        {
            System.out.println("计算结果为空, 出现错误！");
        }
        // 加入起始点坐标，形成LineAlg,整条航线

        for (JW outPoint : outPoints) {
            JW tmpLonlat = this.Mercator2lonLat(new JW(outPoint.x, outPoint.y));
            PointAlg tmpPt = new PointAlg(tmpLonlat.y, tmpLonlat.x, 0, input.nCruiseHeight);
            outResult.add(tmpPt);
        }
        // 增加起点海拔高度参考
        //result->flyRoute->pointList->push_back(para->pEndPoint);
        System.out.println("任务区取点连线");
        return outResult;
    }

    //经纬度转墨卡托
    public JW lonLat2Mercator(JW lonLat){
        JW mercator = new JW();
        double x = lonLat.x * 20037508.34 / 180;
        double y = log(tan((90 + lonLat.y) * ALG_PI / 360)) / (ALG_PI / 180);
        y = y * 20037508.34 / 180;
        mercator.x = x;
        mercator.y = y;
        return mercator;
    }

    //墨卡托转经纬度
    public JW Mercator2lonLat(JW mercator){
        JW lonLat = new JW();
        double x = mercator.x / 20037508.34 * 180;
        double y = mercator.y / 20037508.34 * 180;
        y = (180/ALG_PI) * (2 * atan(exp(y * ALG_PI / 180)) - ALG_PI / 2);
        lonLat.x = x;
        lonLat.y = y;
        return lonLat;
    }


    public JW cross_point(double line1_x1, double line1_y1, double line1_x2, double line1_y2, double line2_x1, double line2_y1, double line2_x2, double line2_y2){
        //具体实现流程
        JW result = new JW();
        double x1 = line1_x1;
        double y1 = line1_y1;
        double x2 = line1_x2;
        double y2 = line1_y2;

        double x3 = line2_x1;//line2[0]
        double y3 = line2_y1;//line2[1]
        double x4 = line2_x2;//line2[2]
        double y4 = line2_y2;//line2[3]

        double k1 = (y2-y1)*1.0/(x2-x1); //#计算k1,由于点均为整数，需要进行浮点数转化
        double b1 = y1*1.0-x1*k1*1.0; //#整型转浮点型是关键
        double k2 = 0.0;
        double b2 = 0.0;
        boolean is_k2 = false; //是不是没有斜率

        if((x4-x3)==0)//:#L2直线斜率不存在操作
        {
            is_k2 = true;
            //k2=None;
            b2=0;
        }else{
            k2=(y4-y3)*1.0/(x4-x3); //#斜率存在操作
            b2=y3*1.0-x3*k2*1.0; //
        }
        double x_out = 0.0;
        double y_out = 0.0;
        if(is_k2){
            x_out = x3;
        }
        else{
            x_out = (b2-b1)*1.0/(k1-k2);
        }

        y_out = k1*x_out*1.0+b1*1.0;
        result.x = x_out;
        result.y = y_out;
        return result;
        //结束循环
    }
    public Vector<JW> polygon_expand(Vector<JW> region, double expand){
        Vector<JW> result = new Vector<>();
        int ptcount = region.size();

        for(int i = 0; i < ptcount; i++){
            JW p1=new JW(0, 0);
            if(i == 0){
                p1.x = region.get(ptcount - 1).x;// = region[ptcount - 1];
                p1.y = region.get(ptcount - 1).y;
            }
            else{
                //p1 = region[i - 1];
                p1.x = region.get(i - 1).x;// = region[ptcount - 1];
                p1.y = region.get(i - 1).y;
            }
            JW p2=new JW(0,0);// = region[i + 1];
            if(i == ptcount - 1){
                //region[i + 1].x, region[i + 1].y
                p2.x = region.get(0).x;// = region[ptcount - 1];
                p2.y = region.get(0).y;
            }
            else{
                p2.x = region.get(i + 1).x;// = region[ptcount - 1];
                p2.y = region.get(i + 1).y;
            }
            JW p=new JW(region.get(i).x, region.get(i).y);// = region[i];
            double v1x = p1.x - p.x;
            double v1y = p1.y - p.y;
            double n1 = norm_p(v1x, v1y);
            v1x = v1x/n1;
            v1y = v1y/n1;
            double v2x = p2.x - p.x;
            double v2y = p2.y - p.y;
            double n2 = norm_p(v2x, v2y);
            v2x = v2x/n2;
            v2y = v2y/n2;
            if((1.0 - (v1x * v2x + v1y * v2y)) == 0){
                continue;
            }
            double l = -1*expand / sqrt((1.0 - (v1x * v2x + v1y * v2y)) / 2.0);
            double vx = v1x + v2x;
            double vy = v1y + v2y;
            //下面的if啥意思
            if(norm_p(vx, vy) == 0){
                norm_p(vx, vy);
            }

            double n = l / norm_p(vx, vy);
            vx *= n;
            vy *= n;
            JW tmp = new JW();
            tmp.x = vx + p.x;
            tmp.y = vy + p.y;
            result.add(tmp);
        }
        return result;

    }
    public double norm_p(double x, double y){
        return sqrt(x*x + y*y);
    }

}
