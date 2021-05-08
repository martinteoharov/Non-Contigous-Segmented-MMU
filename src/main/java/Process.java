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

    /**
     * Process wants to allocate more memory
     * @param base base of allocated physical memory
     * @param limit limit of allocated physical memory
     */
    public void allocateSegment(int base, int limit){
        this.segTable.insertSegment(base, limit);
    }

    // Process wants to deallocate memory
    public void dealloateSegment(){

    }

    
}
