package ua.kiev.univ.schedule.scheduler;

public class ColorMap {
    private final int count;
    private final int[][] table;
    private final int[] times;

    public ColorMap(int count) {
        this.count = count;
        table = new int[count][count];
        times = new int[count];
        for (int i = 0; i < count; i++) {
            times[i] = i; // Identity mapping: Color I = Date I
        }
    }

    public int getEstimate() {
        int estimate = 0;
        for (int i = 0; i < count; i++) {
            // Since it's identity, we only care about the assigned colors
            // But table[i][i] is only non-zero if some point was assigned color i
            estimate += table[i][i];
        }
        return estimate;
    }

    public void addRestriction(int color, int[] restriction) {
        // Only update the diagonal because we use identity mapping
        table[color][color] += restriction[color];
    }

    public void removeRestriction(int color, int[] restriction) {
        table[color][color] -= restriction[color];
    }

    public int getDate(int color) {
        return color;
    }
}