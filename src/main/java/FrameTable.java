import java.util.ArrayList;
import java.util.Iterator;

public class FrameTable {

    private ArrayList<Block> frames;
    private MMU mmu;
    private int maxSize;

    public FrameTable(MMU instance, int maxSize){
        // Initialize frames
        this.frames = new ArrayList<Block>();

        // Add initial empty frame with pid 0, offset 0 and this.size as the limit
        Block initial = new Block(0, 0, maxSize, true);
        this.frames.add(initial);

        // Link MMU Instance so we can manage virtual memory after doing compaction
        this.mmu = instance;

        // Set the max size
        this.maxSize = maxSize;
    }

    /**
     * Allocates a physical block using the firstFit principle.
     *
     * @param pid     Process ID
     * @param limit   The limit we want to allocate to the block
     * @return the base of the allocated block
     */
    public int allocateMemory (int pid, int limit) {

        int index = this.tryFirstFit(limit);
        if(index < 0) {
            return -1;
        }

        // Grab a pointer to the available element
        Block emptyptr = this.frames.get(index);

        // Calculate the offset and limit for the new block
        int full_offset = emptyptr.getBase();
        int full_limit = limit;


        // Calculate the offset and limit for the empty block
        int empty_offset = full_offset + full_limit;
        int empty_limit = emptyptr.getLimit() - full_limit;

        if(empty_limit < 0 || empty_offset < 0){
            // ERROR!
        }
        // Resize empty block
        emptyptr.setOffset(empty_offset);
        emptyptr.setLimit(empty_limit);

        // Create new block
        Block fullBlock = new Block(pid, full_offset, full_limit, false);

        // Insert new block in ArrayList shifting all elements after it to the right
        this.frames.add(index, fullBlock);
        
        return full_offset;
    }

    /**
     * Finds the first big enough spot in our ArrayList
     *
     * @param limit
     * @return the index of available block
     */
    public int tryFirstFit(int limit) {

        // Loop through frames to find the first available spot
        for(int i = 0; i < this.frames.size(); i ++) {
            Block block = this.frames.get(i);
            if(block.isEmpty() && limit < block.getLimit()){
                return i;
            }
        }

        // returns -1 if there is no spot found
        return -1;
    }

    /**
     * Sets a physical block to empty, marking it for deletion (compaction)
     * @param offset the offset of the physical block
     */
    public void setBlockToEmpty (int offset) throws IllegalArgumentException {

        // Find block and set it to empty
        for(Block block : frames){
            if (!block.isEmpty() && block.getBase() == offset){
                int pid = block.getPID();
                int base = block.getBase();
                int limit = block.getLimit();
                int frameInd = frames.indexOf(block);

                frames.set(frameInd, new Block(pid, base, limit, true));

                return;
            }
        }

        throw new IllegalArgumentException("Block with specified offset not found");

    }

    // TODO: compact memory

    /**
     * Compacts our physical memory
     */
    public void compact (){

        // Items that need deleting
        ArrayList<Block> toDelete = new ArrayList<>();

        for(int i = 0; i < this.frames.size(); i++){
            Block blockptr = this.frames.get(i);

            if(blockptr.isEmpty()){
                // mark for removal
                toDelete.add(blockptr);

                // get data for the block being removed
                int pid = blockptr.getPID();
                int relocLen = blockptr.getLimit();

                /* --- Find the relocation length/size so we can traverse compact --- */

                // start traverse compaction
                this.traverseCompaction(i, relocLen);
            }
        }

        // if our last block is empty
        Block lastBlock_ptr = this.frames.get(this.frames.size() - 1);
        if(lastBlock_ptr.isEmpty()){
            for(Block block : toDelete) {
                if(block == lastBlock_ptr) continue;

                this.frames.remove(block);
                lastBlock_ptr.joinRight(block);
            }
        } else {
            // ...
        }
    }

    /**
     * Traverses through our physical memory from the startIndex
     * to the end, relocating every segment by relocLen
     *
     * @param startIndex block to start with
     * @param relocLen length of relocation
     */
    private void traverseCompaction (int startIndex, int relocLen) {
        System.out.println(String.format("Compacting with relocLen: %s", relocLen));

        for(int i = startIndex; i < this.frames.size(); i ++ ) {

            // get physical block
            Block blockptr = this.frames.get(i);

            if(blockptr.isEmpty()) continue;

            // extract physical block data
            int pid = blockptr.getPID();
            int offset = blockptr.getBase();

            // find virtual segment
            int sid = mmu.findSegment(pid, offset);

            // move physical block to the left
            blockptr.setOffset(blockptr.getBase() - relocLen);

            // reloc virtual memory for this segment

            try {
                mmu.relocSegment(pid, sid, relocLen);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
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

    public String toStringSimple() {
        String str = "";

        for(Block frame : frames) {
            str += frame.toStringSimple();
        }

        return str;
    }
}


/**
 * Currently blocks know the PID of the process they belong to, so that necessary
 * changes can be applied to the virtual representation, in the case of compaction
 *
 * Empty blocks should always have a PID of 0!!!
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

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void joinRight(Block block) {
        this.limit += block.getLimit();
    }


    @Override
    public String toString() {
        String str = "";
        str += String.format("[base: %d, limit: %d, %s]", this.base, this.limit, this.empty ? "Empty" : "Full");
        return str;
    }

    public String toStringSimple() {
        String str = "";
        str += String.format("[%s %s]", this.empty ? "H" : "A", this.limit);
        return str;
    }


}
