import java.util.HashMap;

public class SegTable {
    // class that implements the Segment Table for every process

    // hashmap holds an integer to tuple, where the tuple contains 2 values
    // x = base; y = limit
    private HashMap<Integer, Tuple> table;
    int currSID = 0;

    // should allow methods that convert logical addresses to physical ones
    public SegTable() {
        this.table = new HashMap<Integer, Tuple>();
    }

    public void insertSegment(int offset, int limit){
        Tuple entry = new Tuple(offset, limit);
        this.table.put(this.currSID++, entry);
    }

    public void resizeSegment(int offset, int limit){

    }

    @Override
    public String toString(){
        String str = "";



        return "";
    }
}


class Tuple {

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (this.getClass() != other.getClass()) return false;
        Tuple that = (Tuple) other;
        return (this.x == that.x) && (this.y == that.y);
    }

    private int x;
    private int y;
}
