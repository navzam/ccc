public class Triple {
    public int x, y, z;
    public static Triple redTriple = new Triple(222,94,70 );
    public static Triple greenTriple = new Triple(50,146,68 );
    public static Triple blueTriple = new Triple(20,67,164);
    public static Triple whiteTripple = new Triple(234, 232, 211);
    public static Triple yellowTripple = new Triple(241, 215, 0);
    public static Triple orangeTripple = new Triple(242,150, 70);

    public Triple(int xC, int yC, int zC){
        x = xC;
        y = yC;
        z = zC;
    }

    public double distance(Triple other){
        double xComp = Math.pow((other.x - x),2);
        double yComp = Math.pow((other.y - y),2);
        double zComp = Math.pow((other.z - z),2);
        return Math.sqrt(xComp + yComp + zComp);
    }

    @Override
    public String toString(){
        return "{" + x + ", " + y + ", " + z + "}";
    }
}