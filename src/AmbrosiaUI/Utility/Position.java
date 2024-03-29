package AmbrosiaUI.Utility;

public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y){
        this.x += x;
        this.y += y;
    }

    public double getAngleTo(Position target) {
        double angle = (double) Math.toDegrees(Math.atan2(target.y - y, target.x - x));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public double getDistanceTo(Position pos){
        double ac = Math.abs(pos.y - y);
        double cb = Math.abs(pos.x - x);

        return Math.hypot(ac, cb);
    }
    public Position getMultiplied(double mult){
        return new Position(
                (int) (x*mult),
                (int) (y*mult)
        );
    }

    public Position getDivided(double mult){
        return new Position(
                (int) (x/mult),
                (int) (y/mult)
        );
    }
    public void move(Position position){
        this.x += position.x;
        this.y += position.y;
    }

    public String encode(){
        return x + "x" + y;
    }
    public void fromCode(String code){
        String[] cd = code.split("x");
        x = Integer.parseInt(cd[0]);
        y = Integer.parseInt(cd[1]);
    }

    public Position getOffset(Position pos){
        return new Position(pos.x+x,pos.y+y);
    }
    public Position getOffset(int x, int y){
        return new Position(this.x+x,this.y+y);
    }

    public boolean inRectangle(Position position, Size size){
        return x > position.x && x < position.x + size.width &&
                y > position.y && y < position.y + size.height;
    }
    public boolean inRectangle(Rectangle rect){
        return this.inRectangle(rect.getPosition(),rect.getSize());
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
