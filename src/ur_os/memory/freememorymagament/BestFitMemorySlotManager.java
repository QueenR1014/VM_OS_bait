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
            return null; // no free memory available
        }

        MemorySlot bestSlot = null;

        // Step 1: Find the smallest slot that can fit 'size'
        for (MemorySlot slot : list) {
            if (slot.getSize() >= size) {
                if (bestSlot == null || slot.getSize() < bestSlot.getSize()) {
                    bestSlot = slot;
                }
            }
        }

        if (bestSlot == null) {
            // No suitable slot found
            System.out.println("Error: No suitable slot found.");
            return null;
        }

        // Step 2: Allocate from the best slot
        MemorySlot allocated = new MemorySlot(bestSlot.getBase(), size);

        if (bestSlot.getSize() == size) {
            // Perfect fit → remove the slot entirely
            list.remove(bestSlot);
        } else {
            // Partial fit → move the base up and shrink the slot
            bestSlot.setBase(bestSlot.getBase() + size);
            bestSlot.setSize(bestSlot.getSize() - size);
        }

        // Step 3: Return the allocated slot (assigned memory region)
        return allocated;
    }
}
