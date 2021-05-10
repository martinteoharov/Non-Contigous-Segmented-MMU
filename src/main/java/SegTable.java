import java.util.HashMap;

public class SegTable {
    // class that implements the Segment Table for every process

    // hashmap holds an integer to tuple, where the tuple contains 2 values
    private HashMap<Integer, Tuple> table; // TODO: fix naming (x = base; y = limit)

    private int size = 0; // better than getting the entry set every time

    // should allow methods that convert logical addresses to physical ones
    public SegTable() {
        this.table = new HashMap<Integer, Tuple>();
    }

    public void insertSegment(int sid, int offset, int limit){
        Tuple entry = new Tuple(offset, limit);

        if(this.table.containsKey(sid)){
            this.table.put(sid, entry);
        } else {
            this.size++;
            this.table.put(sid, entry);             
        }
    }

    public void resizeSegment(int sid, int offset, int limit){

    }

    public int getSize() {
        return this.size;
    }

    public Tuple getElBySID(int i){
        return table.get(i);
    }

    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i < this.size; i ++){
            Tuple tuple = this.table.get(i);

            // TODO: Track the error with this shit
            if(tuple == null) break;
            
            str += String.format("S%s:[offset: %s, limit: %s] ", i, tuple.getOffset(), tuple.getLimit());
        }
        return str;
    }

}


class Tuple {

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getOffset() {
        return this.x;
    }

    public int getLimit() {
        return this.y;
    }

    private int x;
    private int y;
}
