package ur_os.memory.freememorymagament;

/**
 * Implements Best-Fit allocation strategy.
 * It finds the smallest free memory slot that is large enough to satisfy
 * the request, minimizing leftover space.
 */
public class BestFitMemorySlotManager extends FreeMemorySlotManager {

    public BestFitMemorySlotManager(int memSize) {
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        if (list.isEmpty()) {
            System.out.println("Error: No free memory available.");
            return null;
        }

        MemorySlot bestSlot = null;

        // Step 1: Find the smallest slot that can fit 'size'
        for (MemorySlot slot : list) {
            if (slot.canContain(size)) {
                if (bestSlot == null || slot.getSize() < bestSlot.getSize()) {
                    bestSlot = slot;
                }
            }
        }

        if (bestSlot == null) {
            System.out.println("Error: No suitable slot found.");
            return null;
        }

        // Step 2: Use assignMemory() to handle allocation
        MemorySlot allocated = bestSlot.assignMemory(size);

        // Step 3: Remove the slot if it’s now empty
        if (bestSlot.inNull()) {
            list.remove(bestSlot);
        }

        return allocated;
    }
}
