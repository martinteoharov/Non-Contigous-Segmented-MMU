
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

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MMU {

    // There won't be a need for more than one MMU at a time, so we keep this class static
    private static MMU instance = null;

    // Processes in HashMap can be referenced by PIDs
    private static HashMap<Integer, Process> hm_processes;

    // OS Uses Frame Table to keep track of available frames
    private static FrameTable frameTable;

    private MMU(){
        MMU.hm_processes = new HashMap<Integer, Process>();
    }

    public static MMU get() {
        if(MMU.instance == null){
            MMU.instance = new MMU();
        }

        MMU.frameTable = new FrameTable(MMU.instance);

        return MMU.instance;
    }

    /* --- Attach Process --- */
    public boolean attachProcess (Process process) {

        // Pull pid from process
        int pid = process.getPID();

        if(MMU.hm_processes.containsKey(pid))
            return false;

        // Put process into processes HashMap
        MMU.hm_processes.put(pid, process);

        return true;
    }


    /* --- Segment Manager --- */
    // Handles creation, allocation and deallocation of memory to segments
    public void allocToSegment(int pid, int sid, int limit){
        Process process = this.hm_processes.get(pid);

        // Check if sid for this pid exists
        System.out.println(String.format("PID: %s; SID: %s; SEG SIZE: %s", pid, sid, process.getSegTable().getSize()));
        if(sid < process.getSegTable().getSize()){
            // Segment exists...

            // System.out.println(String.format("IF with PID: %s", pid));

            // Pull offset data for the existing segment
            Tuple element = process.getSegTable().getElBySID(sid);
            int offset = element.getOffset();
            int target_limit = limit + element.getLimit();

            // Set physical block to empty so we can clean it later
            frameTable.setBlockToEmpty(offset);

            // Allocate new physical block
            int allocatedBase = frameTable.allocateMemory(pid, target_limit);
            if(allocatedBase < 0){
                return;
            }

            // Allocate virtual segment for the new physical block (also deletes old one)
            process.allocateSegment(sid, allocatedBase, target_limit);

        } else {
            // Segment doesn't exist...

            // System.out.println(String.format("ELSE with PID: %s", pid));

            // Allocate physical memory
            int allocatedBase = frameTable.allocateMemory(pid, limit);
            if(allocatedBase < 0){
                return;
            }
            System.out.println(String.format("ALLOCATED BASE: %s", allocatedBase));


            // Allocate virtual memory
            process.allocateSegment(sid, allocatedBase, limit);
        }
    }

    // Relocate the segment to some other offset and limit without resizing
    public void relocSegment(int pid, int sid, int relocLen){
        // find process by PID
        Process process = hm_processes.get(pid);

        // find segment by SID
        Tuple segment = process.getSegTable().getElBySID(sid);

        if(segment == null) return;

        // relocate segment to the left with size relocLen
        segment.setOffset(segment.getOffset() - relocLen);
    }


    public int findSegment(int pid, int offset){
        Process process = this.hm_processes.get(pid);
        return process.getElementByOffset(offset);
    }


    public void printMemReport() {

        String str = " \n\n|-------------------------------------------------------------------------------------------------- \n";
        str += "| MEMORY REPORT:\n";
        str += "| -------------------------------------------------------------------------------------------------- \n";


        // Grab virtual
        str += "| Virtual Memory:\n";
        for (Map.Entry<Integer, Process> set : hm_processes.entrySet()) {
            str += "| P" + set.getKey() + ": " + set.getValue().getSegTable() + "\n";
        }

        
        // Grab physical
        str += "| \n";
        str += "| \n";
        str += "| Physical Memory:\n";
        str += "| " + MMU.frameTable;

        str += " \n| -------------------------------------------------------------------------------------------------- \n";



        System.out.println(str);
    }


    /**
     * This method exists only for testing purposes
     * In reality only the FrameTable should initiate
     * its compactions
     */
    public void forceCompaction() {
        frameTable.compact();
    }


    


    // Legacy Code LmAo

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
