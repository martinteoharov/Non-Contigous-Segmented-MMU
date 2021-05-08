
/**
 *  MMU.java
 *
 *  This class acts as the Memory Management Unit
 *
 *  It allows "the outside world" to create processes,
 *  resize specific segments of them, or allocate new and
 *  deallocate old ones.
 *
 *  Every request is first mapped onto the virtual
 *  representation of the specified process and then translated
 *  to the physical memory (FrameTable).
 *
 *  In times of physical compaction the MMU
 *  performs a search for the processes affected
 *  by the compaction and creates the necessary
 *  changes in their virtual representations.
 *
 */

import java.util.HashMap;

public class MMU {

    // There won't be a need for more than one MMU at a time, so we keep this class static
    private static MMU instance;

    // Processes in HashMap can be referenced by PIDs
    private static HashMap<Integer, Process> hm_processes;

    // OS Uses Frame Table to keep track of available frames
    private static FrameTable frameTable;

    private MMU(){
        MMU.frameTable = new FrameTable();
        MMU.hm_processes = new HashMap<Integer, Process>();
    }

    public static MMU get() {
        if(MMU.instance == null){
            MMU.instance = new MMU();
        }

        return MMU.instance;
    }

    /* --- Attach Process --- */
    public void attachProcess (Process process) {

        // Pull pid from process
        int pid = process.getPID();

        // Put process into processes HashMap
        MMU.hm_processes.put(pid, process);

    }


    /* --- Segment Manager --- */
    // TODO: Responsible for creating new segments for a process
    public void allocateSegment(int pid, int sid, int limit){
        Process process = this.hm_processes.get(pid);

        // Allocate physical memory
        int allocatedBase = frameTable.allocateMemory(pid, limit);
        if(allocatedBase < 0){
            return;
        }

        // Allocate virtual memory
        process.allocateSegment(allocatedBase, limit);
    }

    // TODO: Responsible for deleting existing segment for a process
    public void deallocateSegment(int pid, int limit){
        Process process = this.hm_processes.get(pid);

        // TODO: internal call to process to create an entry for its SegTable
        // ...


        // TODO: create an entry for the physical memory
        // ...
    }


    /* --- Segment Size Manager --- */
    // TODO: Manages existing segments' sizes
    public void resizeSegment(int pid, int sid, int limit){
        Process process = this.hm_processes.get(pid);

        // TODO: set target_memSegSize to segment in segment table with memSegID as id
        // ...



        // TODO: resize the physical memory entry
        // ...
    }

}
