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

        // 1. Recolectar las páginas actualmente cargadas
        LinkedList<Integer> frames = new LinkedList<>();
        HashSet<Integer> seen = new HashSet<>();

        // Se recorren los accesos desde el más reciente hacia atrás
        for (int i = memoryAccesses.size() - 1; i >= 0 && frames.size() < loaded; i--) {
            int page = memoryAccesses.get(i);
            if (!seen.contains(page)) {
                frames.addFirst(page); // la más antigua queda al inicio
                seen.add(page);
            }
        }

        // 2. Elegir víctima según la paridad de los accesos
        int totalAccesses = memoryAccesses.size();

        if (totalAccesses % 2 == 0) {
            // Si el número de accesos es PAR -> eliminar la más antigua
            return frames.getFirst();
        } else {
            // Si el número de accesos es IMPAR -> eliminar la más reciente
            return frames.getLast();
        }
    }
}
