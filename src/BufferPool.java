import java.io.*;
import java.util.Scanner;

public class BufferPool {
    private Frame[] buffers;
    private int removeIndex;
    private int numRecordsPerBlock;

    public BufferPool(int numBuffers){
        this.buffers = new Frame[numBuffers];
        // initialize each new frame
        for(int i = 0; i < numBuffers; i++){
            buffers[i] = new Frame();
        }
        this.removeIndex = 0;
        this.numRecordsPerBlock = 100;
    }

    // gets a record from a block in the pool if it exists, or from disk
    public void get(int recordID){
        // find block ID subtracting and adding 1 to account for 0-indexing
        int blockID = ((recordID - 1)/this.numRecordsPerBlock) + 1;
        int blockFrame = inPool(blockID);
        if(blockFrame != -1){
            // block is in memory!
            System.out.println(getRecordFromPool(blockFrame, (recordID % 100) - 1));
            System.out.println("File " + blockID + " already in memory. No I/O necessary.");
            System.out.println("Located in Frame " + (blockFrame + 1));
        } else {
            // block is not in memory
            blockFrame = getBlockFromDisk(blockID);
            if(blockFrame != -1) {
                System.out.println(getRecordFromPool(blockFrame, (recordID % 100) - 1));
                System.out.println("Brought file " + blockID + " from disk. I/O action performed.");
                System.out.println("Placed in Frame " + (blockFrame + 1));
            } else {
                System.out.println("The corresponding block #" + blockID + " cannot be accessed from disk because the memory " +
                        "buffers are full");
            }

        }

    }

    // sets a recordID to a record. very similar to above
    public void set(int recordID, char[] record){
        int blockID = ((recordID - 1)/this.numRecordsPerBlock) + 1;
        int blockFrame = inPool(blockID);
        if(blockFrame != -1){
            // write record
            this.buffers[blockFrame].setRecord((recordID % this.numRecordsPerBlock) - 1, record);
            System.out.println("Write was successful");
            System.out.println("File " + blockID + " already in memory. No I/O necessary.");
            System.out.println("Located in Frame " + (blockFrame + 1));
        } else {
            // get block from disk
            blockFrame = getBlockFromDisk(blockID);
            if(blockFrame == -1){
                System.out.println("The corresponding block #" + blockID + " cannot be accessed from disk because the memory " +
                        "buffers are full");
                System.out.println("Write was uncsuccessful");
            } else {
                this.buffers[blockFrame].setRecord((recordID % this.numRecordsPerBlock) - 1, record);
                System.out.println("Write was successful!");
                System.out.println("Brought File " + blockID + " from disk. I/O action performed.");
                System.out.println("Placed in Frame " + (blockFrame + 1));
            }
        }
    }

    // pins a block with given blockID in buffer pool
    public void pin(int blockID){
        int blockFrame = inPool(blockID);
        if(blockFrame != -1){
            // print curr frame
            System.out.println("File " + blockID + " pinned in Frame " + (blockFrame + 1));
            if(this.buffers[blockFrame].isPinned()){
                System.out.println("Already pinned");
            } else {
                // set pin
                this.buffers[blockFrame].setPinned(true);
                System.out.println("Frame " + (blockFrame + 1) + " was not already pinned");
            }
        } else {
            // get block from disk
            blockFrame = getBlockFromDisk(blockID);
            if(blockFrame != -1) {
                this.buffers[blockFrame].setPinned(true);
                System.out.println("File " + blockID + " pinned in Frame " + (blockFrame + 1));
                System.out.println("Frame " + (blockFrame + 1) + " was not already pinned");
            } else {
                System.out.println("The corresponding block #" + blockID + " cannot be pinned because the memory " +
                        "buffers are full");
            }
        }
    }

    // unpins block with given blockID in buffer pool
    public void unpin(int blockID){
        int blockFrame = inPool(blockID);
        if(blockFrame != -1){
            System.out.println("File " + blockID + " is unpinned in Frame " + (blockFrame + 1));
            if(this.buffers[blockFrame].isPinned()){
                this.buffers[blockFrame].setPinned(false);
                System.out.println("Frame " + (blockFrame + 1) + " was not already unpinned.");
            } else {
                System.out.println("Frame was already unpinned.");
            }
        } else {
            // block is not in memory
            System.out.println("The corresponding block " + blockID + " cannot be unpinned because it is not in memory.");
        }
    }

    // get a record from given frame
    private String getRecordFromPool(int frameNum, int recordNum){
        return this.buffers[frameNum].getRecord(recordNum);
    }

    // gets block from disk
    private int getBlockFromDisk(int blockID){
        int emptyFrame = getEmptyFrame();
        if(emptyFrame == -1){
            emptyFrame = removeableFrame();
            if(emptyFrame == -1){
                return emptyFrame;
            }
        }

        if(this.buffers[emptyFrame].isDirty()){
            writeToDisk(this.buffers[emptyFrame].getContent(), this.buffers[emptyFrame].getBlockID());
        }

        readDisk(emptyFrame, blockID);
        return emptyFrame;
    }

    // checks to see if given block exists in buffer pool. return -1 if it does not exist.
    private int inPool(int blockID){
        for(int i = 0; i < this.buffers.length; i++){
            if(buffers[i].getBlockID() == blockID){
                return i;
            }
        }
        return -1;
    }

    // returns index of an empty frame, -1 if no frames are empty
    private int getEmptyFrame(){
        for(int i = 0; i < this.buffers.length; i++){
            if(buffers[i].getBlockID() == -1){
                return i;
            }
        }
        return -1;
    }

    // finds removeable frames (unpinned frames) and returns index. -1 if none exist
    private int removeableFrame(){
        for(int i = this.removeIndex; i < this.buffers.length; i++){
            if(!this.buffers[i].isPinned()){
                removeIndex = i + 1;
                System.out.println("Evicted File " + this.buffers[i].getBlockID() + " from Frame " + (i + 1));
                return i;
            }
        }

        for(int i = 0; i < removeIndex; i++){
            if(!this.buffers[i].isPinned()){
                removeIndex = i + 1;
                System.out.println("Evicted File " + this.buffers[i].getBlockID() + " from Frame " + (i + 1));
                return i;
            }
        }
        return -1;
    }

    // writes content to file
    private void writeToDisk(char[] content, int blockID){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Project1/F" + blockID + ".txt"));
            writer.write(content);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // read contents of file from a block in disk
    private void readDisk(int emptyFrame, int blockID){
        Scanner scanner = null;
        try{
            scanner = new Scanner(new File("Project1/F" + blockID + ".txt")).useDelimiter("\\Z");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        StringBuilder content = new StringBuilder(4000);
        while(scanner.hasNext()){
            content.append(scanner.next());
        }
        scanner.close();
        this.buffers[emptyFrame].setContent(content.toString().toCharArray());
        this.buffers[emptyFrame].setBlockID(blockID);
        this.buffers[emptyFrame].setDirty(false);
    }


}
