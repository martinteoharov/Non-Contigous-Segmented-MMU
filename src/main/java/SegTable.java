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

    /**
     * Inserts a segment into virtual memory.
     *
     * @param sid     Segment ID
     * @param offset  Offset
     * @param limit   Limit
     */
    public void insertSegment(int sid, int offset, int limit){
        Tuple entry = new Tuple(offset, limit);

        if(this.table.containsKey(sid)){
            this.table.put(sid, entry);
        } else {
            this.size++;
            this.table.put(sid, entry);             
        }
    }

    /**
     * Resize a segment by a given offset and limit.
     *
     * @param sid     Segment ID
     * @param offset  Offset
     * @param limit   Limit
     */
    public void resizeSegment(int sid, int offset, int limit){
        if(!this.table.containsKey(sid)) return;

        Tuple entryptr = this.table.get(sid);
        entryptr.setOffset(offset);
        entryptr.setLimit(limit);
    }

    /**
     * This method is called after a change in the physical memory .
     * It relocates the offset that the specific segment points to.
     *
     * @param sid        Segment ID
     * @param relocLen   Relocation Length
     */
    public void relocSegment(int sid, int relocLen) {
        if(!this.table.containsKey(sid)) return;

        Tuple entryptr = this.table.get(sid);
        // relocate segment to the left with size relocLen
        entryptr.setOffset(entryptr.getOffset() - relocLen);
    }

    /**
     * Deletes a segment and shifts all next segments to the left.
     *
     * @param sid
     */
    public void deleteSegment(int sid) {

        // Shift segments to the left starting from SID
        for(int i = sid; i < size - 1; i ++ ){
            Tuple temp = this.table.get(i+1);
            this.table.put(i, temp);
        }
        // Remove the last element as this is now the element that should
        // have been deleted
        this.table.remove(size-1);

        // Size goes down by 1
        this.size --;
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

    public void setOffset(int offset) {
        this.x = offset;
    }

    public void setLimit(int limit) {
        this.y = limit;
    }


    private int x;
    private int y;
}
