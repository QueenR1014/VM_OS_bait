/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author prestamour
 */
public class NextFitMemorySlotManager extends FreeMemorySlotManager {

    // Index to remember the last position where allocation occurred
    private int lastIndex = 0;

    public NextFitMemorySlotManager(int memSize) {
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        if (list.isEmpty()) {
            System.out.println("Error: No free memory available.");
            return null;
        }

        int n = list.size();
        int startIndex = lastIndex; // remember where we start
        int currentIndex = startIndex;

        do {
            MemorySlot slot = list.get(currentIndex);

            if (slot.canContain(size)) {
                // Allocate memory in this slot
                MemorySlot allocated = slot.assignMemory(size);

                // Update lastIndex (next search starts from here)
                lastIndex = currentIndex;

                // Remove the slot if it’s now empty
                if (slot.inNull()) {
                    list.remove(slot);
                    // Adjust lastIndex if removal shifted indices
                    if (lastIndex >= list.size()) {
                        lastIndex = 0;
                    }
                }

                return allocated;
            }

            // Move to next slot (circular search)
            currentIndex = (currentIndex + 1) % n;

        } while (currentIndex != startIndex);

        System.out.println("Error: No suitable slot found.");
        return null;
    }
}