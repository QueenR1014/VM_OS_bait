package ur_os.memory.paging;

import ur_os.memory.MemoryAddress;
import ur_os.memory.MemoryManagerType;
import ur_os.memory.ProcessMemoryManager;
import ur_os.memory.SystemMemoryManager;
import ur_os.system.InterruptType;
import ur_os.system.OS;

public class SMM_Paging extends SystemMemoryManager {

    public SMM_Paging(OS os) {
        super(os);
        type = MemoryManagerType.PAGING;
    }

    @Override
    public int getPhysicalAddress(int logicalAddress, ProcessMemoryManager pmm, boolean store) {

        if (pmm.getType() == MemoryManagerType.PAGING) {
            PMM_Paging pmmp = (PMM_Paging) pmm;

            // INCLUDE THE STORE VALUE TO MARK DIRTY THE PAGE
            MemoryAddress la = pmmp.getPageMemoryAddressFromLocalAddress(logicalAddress);
            MemoryAddress pa = pmmp.getFrameMemoryAddressFromLogicalMemoryAddress(la);

            if (pa == null) {
                // There was a page fault, so the page needs to be brought to memory from swap
                int pageVictim = pmmp.getVictim(); // Find a page that needs to leave memory if there is no space
                int frameVictimInSwap;
                int frameVictim;

                if (pageVictim == -1) { // If no victim was found because there are still frames available
                    frameVictim = getOS().getFreeFrame(); // Obtain a new free frame to store the page from swap
                    frameVictimInSwap = -1;
                } else { // If there are no free frames, then a pageVictim was selected
                    // 🔧 FIX: Se usa un MemoryAddress en lugar de int directo
                    frameVictim = pmmp
                            .getFrameMemoryAddressFromLogicalMemoryAddress(new MemoryAddress(pageVictim, 0))
                            .getDivision();
                    frameVictimInSwap = pmmp.getVFrameMemoryAddressFromLogicalMemoryAddress(pageVictim);
                }

                int pageToLoad = la.getDivision(); // Get the pageID of the desired page
                int frameToLoadInSwap = pmmp.getVFrameMemoryAddressFromLogicalMemoryAddress(pageToLoad);

                // 🧠 Registro del evento de fallo de página
                if (pageVictim == -1) {
                    System.out.println("[PAGE FAULT] Page to load: " + pageToLoad + " (no replacement)");
                } else {
                    System.out.println("[PAGE FAULT] Page to load: " + pageToLoad + " | Page replaced: " + pageVictim);
                }

                MemoryPageExchange mpe = new MemoryPageExchange(
                        pageVictim,
                        frameVictimInSwap,
                        frameVictim,
                        pageToLoad,
                        frameToLoadInSwap
                );

                if (pageVictim != -1) { // If there was a page identified to leave memory
                    if (pmmp.isPageDirty(pageVictim)) { // If the page is dirty, it must be updated in swap
                        mpe.setFullExchange(true);
                        getOS().interrupt(InterruptType.STORE_PAGE, pmmp.getProcess(), mpe);
                        pmmp.setPageValid(mpe.getFrameVictim(), false);
                    }
                }

                // Load the new page into physical memory
                getOS().interrupt(InterruptType.LOAD_PAGE, pmmp.getProcess(), mpe);
                pmmp.setFrameID(pageToLoad, frameVictim);

                // Retry after handling the fault
                return getPhysicalAddress(logicalAddress, pmm, store);
            } else {
                if (store) {
                    pmmp.setPageDirty(pa.getDivision(), true);
                }
                return pa.getAddress();
            }
        }
        return -1;
    }
}
