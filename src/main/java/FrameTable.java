import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FrameTable {

    private ArrayList<Block> frames;
    private MMU mmu;

    public FrameTable(MMU instance){
        this.frames = new ArrayList<Block>();
        this.mmu = instance;
        System.out.println(this.mmu);
    }

    public int allocateMemory (int pid, int limit) {

        int bSize = frames.size();

        // Get the last element of our list
        Block lastBlock;
        if(frames.size() > 0) {
            lastBlock = frames.get(frames.size() - 1);
        } else {
           lastBlock = new Block(0, 0, 0, false);
        }


        int base = lastBlock.getBase() + lastBlock.getLimit();
        Block block = new Block(pid, base, limit, false);
        frames.add(block);

        int eSize = frames.size();

        if(eSize > bSize) {
            return base;
        }

        return -1;
    }

    public void setBlockToEmpty (int offset) {
        // Find block and set it to empty

        for(Block block : frames){
            if (!block.isEmpty() && block.getBase() == offset){
                int pid = block.getPID();
                int base = block.getBase();
                int limit = block.getLimit();
                int frameInd = frames.indexOf(block);

                frames.set(frameInd, new Block(pid, base, limit, true));
            }
        }

    }

    // TODO: compact memory
    public void compact (){

        // Traverse frames to find empty blocks and remove them
        Iterator<Block> itr = frames.iterator();
        int count = 0;
        while(itr.hasNext()){
            Block block = itr.next();
            if(block.isEmpty()){
                // remove empty block
                itr.remove();

                // get data for the block being removed
                int pid = block.getPID();
                int offset = block.getBase();

                /* --- Find the relocation length/size so we can traverse compact --- */

                // start traverse compaction
                this.traverseCompaction(count, offset);

                count ++;
            }
        }
    }

    /**
     * Traverses through the arraylist from the startIndex
     * every element visited is relocated to the left by
     * relocLen.
     *
     *
     */

    private void traverseCompaction (int startIndex, int relocLen) {
        for(int i = startIndex; i < this.frames.size(); i ++ ){

            // get physical block
            Block blockptr = this.frames.get(i);

            // extract data
            int pid = blockptr.getPID();
            int offset = blockptr.getBase();

            // find virtual block
            int sid = mmu.findSegment(pid, offset);

            // move physical block to the left
            blockptr.setOffset(blockptr.getBase() - relocLen);

            // reloc virtual memory for this segment
            mmu.relocSegment(pid, sid, -offset);            
        }
    }

    @Override
    public String toString() {
        String str = "";

        for(Block frame : frames) {
            str += frame.toString() + ", ";
        }

        return str;
    }

}


/**
 * Currently blocks know the PID of the process they belong to, so that necessary
 * changes can be applied to the virtual representation, in the case of compaction
 */

class Block {
    // simulates a block in memory

    // every block has an offset and a limit
    private int base;
    private int limit;
    private int pid;
    private boolean empty;

    public Block(int pid, int base, int limit, boolean empty){
        this.pid = pid;
        this.base = base;
        this.limit = limit;
        this.empty = empty;
    }

    public int getPID(){
        return this.pid;
    }

    public int getBase(){
        return this.base;
    }

    public int getLimit(){
        return this.limit;
    }

    public boolean isEmpty() { return this.empty; }

    public void setOffset(int offset) {
        this.base = offset;
    }

    @Override
    public String toString() {
        String str = "";
        str += String.format("[base: %d, limit: %d, %s]", this.base, this.limit, this.empty ? "Empty" : "Full");
        return str;
    }
}
