import java.util.ArrayList;

public class Process {

    // pid is stored solely for "metadata", it doesn't actually come up in the memory management implementation
    private int pid; // process unique id

    private SegTable segTable;

    public Process(int pid) {
        this.pid = pid;
        this.segTable = new SegTable();
        // ...
    }

    public int getPID(){
        return this.pid;
    }

    public SegTable getSegTable(){
        return this.segTable;
    }

    public int getElementByOffset(int offset) {
        for(int i = 0; i < this.segTable.getSize(); i ++ ){
            Tuple tuple = this.segTable.getElBySID(i);

            if(tuple.getOffset() == offset)
                return i;
        }

        return -1;
    }

    /**
     * Process wants to allocate more memory
     * @param base base of allocated physical memory
     * @param limit limit of allocated physical memory
     */
    public void allocateSegment(int sid, int base, int limit){
        this.segTable.insertSegment(sid, base, limit);
    }

    /**
     * Process wants to deallocate more memory
     * @param base base of allocated physical memory
     * @param limit limit of allocated physical memory
     */
    public void dealloateSegment(int sid, int base, int limit){
        //System.out.println(String.format("deallocate segment [base: %s, limit: %s]", base, limit));
        this.segTable.resizeSegment(sid, base, limit);
    }

    public void deleteSegment(int sid) {
        this.segTable.deleteSegment(sid);
    }

    
}
