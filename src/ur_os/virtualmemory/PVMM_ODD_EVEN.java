/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import java.util.HashSet;


public class PVMM_ODD_EVEN extends ProcessVirtualMemoryManager {

    public PVMM_ODD_EVEN() {
        type = ProcessVirtualMemoryManagerType.ODD_EVEN;
    }

    /**
     * Selecciona una víctima según la paridad del número total de accesos.
     *
     * @param memoryAccesses lista de accesos de páginas
     * @param loaded número de páginas cargadas actualmente
     * @return número de página víctima
     */
    public int getVictim(LinkedList<Integer> memoryAccesses, int loaded) {
        if (loaded <= 0 || memoryAccesses == null || memoryAccesses.isEmpty())
            return -1;

        // Trace currently loaded pages
        LinkedList<Integer> frames = new LinkedList<>();
        HashSet<Integer> seen = new HashSet<>();

        // Go thruogh all accessess from most recent 
        for (int i = memoryAccesses.size() - 1; i >= 0 && frames.size() < loaded; i--) {
            int page = memoryAccesses.get(i);
            if (!seen.contains(page)) {
                frames.addFirst(page); // Oldest stays at the beggining
                seen.add(page);
            }
        }

        // Choose victim according to parity
        int totalAccesses = memoryAccesses.size();

        if (totalAccesses % 2 == 0) {
            // If total accesses is even -> erase the oldest
            return frames.getFirst();
        } else {
            // If total accesses is odd -> erase the most recent
            return frames.getLast();
        }
    }
}
