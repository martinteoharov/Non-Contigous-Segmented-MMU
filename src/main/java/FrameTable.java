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

    /**
     * Allocates a physical block of memory at the end.
     *
     * @param pid     Process ID
     * @param limit   The limit we want to allocate to the block
     * @return the base of the allocated block so that we can map the virtual memory to it
     */
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
        // Traverse frames to find empty blocks and remove them

        Iterator<Block> itr = frames.iterator();

        // Items that need deleting
        ArrayList<Block> toDelete = new ArrayList<>();

        for(int c = 0; itr.hasNext(); c++){
            Block block = itr.next();
            if(block.isEmpty()){
                // mark for removal
                toDelete.add(block);

                // get data for the block being removed
                int pid = block.getPID();
                int relocLen = block.getLimit();

                /* --- Find the relocation length/size so we can traverse compact --- */

                // start traverse compaction
                this.traverseCompaction(c, relocLen);

                // itr.remove();
            }
        }

        // removed marked blocks
        for(Block block : toDelete) {
            frames.remove(block);
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

    public String toStringSimple() {
        String str = "";
        str += String.format("[%s %s]", this.empty ? "H" : "A", this.limit);
        return str;
    }


}
