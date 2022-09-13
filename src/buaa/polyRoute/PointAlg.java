package buaa.polyRoute;

public class PointAlg {
    //WGS84坐标下的位置信息
    public double longitude; /** 经度 */
    public double latitude;    /** 纬度 */
    public double height;	/** 对地高度*/
    public double altitude;	/** 海拔高度*/
    //XYZ坐标,相对于同一基准点才有意义

    public PointAlg (){}

    public PointAlg (double _latitude, double _longitude, double _altitude){
        longitude = _longitude;
        latitude = _latitude;
        height = 0;
        altitude = _altitude;
    }

    public PointAlg (double _latitude, double _longitude, double _altitude, double _height){
        longitude = _longitude;
        latitude = _latitude;
        height = _height;
        altitude = _altitude;
    }

}
