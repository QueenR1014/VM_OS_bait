/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;

/**
 *
 * @author user
 */
public class PVMM_FIFO extends ProcessVirtualMemoryManager {

    public PVMM_FIFO() {
        type = ProcessVirtualMemoryManagerType.FIFO;
    }

    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, ArrayList<Integer> validList) {
        if (validList == null || validList.isEmpty()) {
            throw new IllegalStateException("No pages loaded.");
        }
        return validList.get(0);
    }
}
