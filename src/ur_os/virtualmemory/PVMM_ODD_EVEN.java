package ur_os.virtualmemory;

import java.util.ArrayList;
import java.util.LinkedList;

public class PVMM_ODD_EVEN extends ProcessVirtualMemoryManager {

    public PVMM_ODD_EVEN() {
        type = ProcessVirtualMemoryManagerType.ODD_EVEN;
    }

    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, ArrayList<Integer> validList) {

        if (validList == null || validList.isEmpty()) {
            throw new IllegalStateException("No pages loaded");
        }

        int totalAccesses = memoryAccesses.size();

        // EVEN → remove the first page (oldest)
        if (totalAccesses % 2 == 0) {
            return validList.get(0);
        }

        // ODD → remove the last page (newest)
        return validList.get(validList.size() - 1);
    }
}
