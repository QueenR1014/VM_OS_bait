/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.paging;

import ur_os.memory.MemoryAddress;
import ur_os.memory.ProcessMemoryManager;
import ur_os.memory.MemoryManagerType;
import ur_os.system.OS;
import ur_os.virtualmemory.ProcessVirtualMemoryManager;

/**
 *
 * @author super
 */
public class PMM_Paging extends ProcessMemoryManager{
    
    PageTable pt;
    PageTable vpt;
    int assignedPages;
    int loadedPages;

    public PMM_Paging(int processSize, int assignedPages) {
        this(null,processSize,assignedPages);
    }
    
    public PMM_Paging(ur_os.process.Process p, int processSize, int assignedPages){
        super(p, MemoryManagerType.PAGING,processSize);
        pt = new PageTable(processSize,assignedPages,true);
        vpt = new PageTable(processSize,assignedPages,true);
        this.assignedPages = assignedPages; //Number of frames assigned to the process
        this.loadedPages = 0; //Number of loaded pages of the process
    }
    
    public PMM_Paging(int processSize) {
        this(processSize,3);
    }

    public PMM_Paging(PMM_Paging pmm) {
        super(pmm);
        if(pmm.getType() == this.getType()){
            this.pt = new PageTable(pmm.getPT());
            this.vpt = new PageTable(pmm.getVPT());
            this.assignedPages = pmm.assignedPages;
            this.loadedPages = pmm.loadedPages;
        }else{
            System.out.println("Error - Wrong PMM parameter");
        }
    }

    public int getAssignedPages() {
        return assignedPages;
    }

    public void setAssignedPages(int assignedPages) {
        if(assignedPages > 0)
            this.assignedPages = assignedPages;
        else
            this.assignedPages = vpt.size;
    }

    public int getLoadedPages() {
        return loadedPages;
    }

    public void setLoadedPages(int loadedPages) {
        this.loadedPages = loadedPages;
    }

    
    
    public PageTable getVPT() {
        return vpt;
    }
    
    public PageTable getPT() {
        return pt;
    }
    
    public void addFrameID(int frame){
        pt.addFrameID(frame);
    }
    
    public void addFrameID(int frame, boolean valid){
        pt.addFrameID(frame, valid);
    }
    
    public void addVFrameID(int frame){
        vpt.addFrameID(frame);
    }
    
    public void addVFrameID(int frame, boolean valid){
        vpt.addFrameID(frame,valid);
    }
    
    public void setFrameID(int page, int frame){
        pt.setFrameID(page, frame);
        setPageValid(page, true);
        pt.setPageDirty(page, false);
    }
    
    public MemoryAddress getPageMemoryAddressFromLocalAddress(int locAdd){
        
        if (locAdd < 0) return new MemoryAddress(-1, -1);
        final int page = locAdd / OS.PAGE_SIZE;
        final int offset = locAdd % OS.PAGE_SIZE;
        return new MemoryAddress(page, offset);
    }
    
    public int getFrameMemoryAddressFromLogicalMemoryAddress(int page){

        if (page < 0) return -1;
        if(!pt.isPageValid(page)) return -1;
        int frameId = pt.getFrameIdFromPage(page);
        if(frameId < 0) return -1;
        return frameId * OS.PAGE_SIZE;
    }
    
    public MemoryAddress getFrameMemoryAddressFromLogicalMemoryAddress(MemoryAddress m){
        
        if (m == null) return new MemoryAddress(-1, -1);
        int offset = m.getOffset();
        int page = m.getDivision();
        int base = getFrameMemoryAddressFromLogicalMemoryAddress(page);
        if(base < 0) return new MemoryAddress(-1, -1);
        return new MemoryAddress(base, offset);
    }
    
    
    public int getVFrameMemoryAddressFromLogicalMemoryAddress(int page){
        return getVFrameMemoryAddressFromLogicalMemoryAddress(new MemoryAddress(page, 0)).getDivision();
    }
    
    public MemoryAddress getVFrameMemoryAddressFromLogicalMemoryAddress(MemoryAddress m){
        
        if (m == null) return new MemoryAddress(-1, -1);
        int offset = m.getOffset();
        int page = m.getDivision();

        if (page < 0) return new MemoryAddress(-1, -1);

        int vframeId = (vpt != null) ? vpt.getFrameIdFromPage(page) : -1;
        if (vframeId < 0) return new MemoryAddress(-1, -1);
        
        int vbase = vframeId * OS.PAGE_SIZE;
        return new MemoryAddress(vbase, offset);
    }
    
    
    @Override
    public String toString(){
        return pt.toString();
    }

    public int getFrameInSwap(int page) {
        return vpt.getFrameIdFromPage(page);
    }
    
    public void setPageValid(int page, boolean valid){
        pt.setPageValid(page, valid);
        if(!valid)
            this.loadedPages--;
        else
            this.loadedPages++;
    }
    
    public boolean isPageDirty(int page){
        return pt.isPageDirty(page);
    }
    
    public void setPageDirty(int page, boolean valid){
        pt.setPageDirty(page, valid);
    }
    
    
    

    @Override
    public int getVictim(){
        if(this.loadedPages == this.assignedPages)
            return pvmm.getVictim(memoryAccesses,this.loadedPages);
        else
            return -1;
    }
    
    
        /**
     * Return the FRAME ID for a given page (not multiplied by PAGE_SIZE).
     * Returns -1 if invalid / not present.
     */
    public int getFrameIdFromPage(int page) {
        if (page < 0) return -1;
        if (!pt.isPageValid(page)) return -1;
        return pt.getFrameIdFromPage(page);
    }

    /**
     * Return the physical base address (frameId * PAGE_SIZE) for a given page,
     * or -1 if not present. (Kept for compatibility.)
     */
    public int getFrameBaseAddressFromPage(int page) {
        int fid = getFrameIdFromPage(page);
        return (fid >= 0) ? fid * OS.PAGE_SIZE : -1;
    }

    /** Swap / VPT equivalents (return frame ids in swap area) */
    public int getVFrameIdFromPage(int page) {
        if (page < 0) return -1;
        return (vpt != null) ? vpt.getFrameIdFromPage(page) : -1;
    }

    public int getVFrameBaseAddressFromPage(int page) {
        int fid = getVFrameIdFromPage(page);
        return (fid >= 0) ? fid * OS.PAGE_SIZE : -1;
    }

}
