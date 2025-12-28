package ua.kiev.univ.schedule.scheduler;

import java.util.Date;

public class ColorMap {
    private final int count;
    private final int[][] table;
    private final int[] times;
    private final int[] add;

    public ColorMap(int count) {
        this.count = count;
        table = new int[count][count];
        times = new int[count];
        for (int i = 0; i < count; i++) {
            times[i] = i;
        }
        add = new int[count];
    }

    public int getEstimate() {
        int estimate = 0;
        for (int i = 0; i < count; i++) {
            estimate += table[times[i]][i];
        }
        return estimate;
    }

    private void findMap(int color) {
        int time = times[color];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < count; i++) {
            max = Math.max(max, table[i][color] + add[i]);
        }
        int[] row = table[time];
        int[] colmax = new int[count];
        int[] rownum = new int[count];
        int[] maxes = new int[count];
        for (int i = 0; i < count; i++) {
            colmax[i] = row[i] + add[time];
            rownum[i] = time;
            int t = times[i];
            maxes[i] = table[t][i] + add[t];
        }
        maxes[color] = max;
        boolean[] was = new boolean[count];
        int[] colors = new int[count];

        search: while (true) {
            int change = Integer.MAX_VALUE;
            for (int i = 0; i < count; i++) {
                if (!was[i]) {
                    change = Math.min(change, maxes[i] - colmax[i]);
                }
            }
            for (int i = 0; i < count; i++) {
                if (was[i]) {
                    add[times[i]] += change;
                } else {
                    colmax[i] += change;
                }
            }
            add[time] += change;
            for (int i = 0; i < count; i++) {
                if (!was[i]) {
                    if (maxes[i] == colmax[i]) {
                        if (i == color) {
                            max = rownum[i];
                            break search;
                        } else {
                            was[i] = true;
                            int t = times[i];
                            colors[t] = i;
                            row = table[t];
                            for (int j = 0; j < count; j++) {
                                if (!was[j] && (colmax[j] < row[j] + add[t])) {
                                    colmax[j] = row[j] + add[t];
                                    rownum[j] = t;
                                }
                            }
                        }
                    }
                }
            }
        }
        times[color] = max;
        while (max != time) {
            color = colors[max];
            max = times[color] = rownum[color];
        }
        int change = Integer.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            change = Math.min(change, add[i]);
        }
        for (int i = 0; i < count; i++) {
            add[i] -= change;
        }
    }

    public void addRestriction(int color, int[] restriction) {
        for (int i = 0; i < count; i++) {
            table[i][color] += restriction[i];
        }
        findMap(color);
    }

    public void removeRestriction(int color, int[] restriction) {
        for (int i = 0; i < count; i++) {
            table[i][color] -= restriction[i];
        }
        findMap(color);
    }

    public int getDate(int color) {
        return times[color];
    }

    public static void main(String[] args) {
        System.out.println(new Date());
        ColorMap map = new ColorMap(30);
        for (int i = 0; i < 1000000; i++) {
            int[] r = new int[30];
            for (int j = 0; j < 30; j++) {
                r[j] = (int) (Math.random() * 10000);
            }
            map.addRestriction((int) (Math.random() * 30), r);
        }
        System.out.println(new Date());
    }
}