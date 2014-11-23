package net.stuxcrystal.airblock.commands.arguments.list;

/**
 * Returns the iterable at the given item.
 */
class SliceIterable extends ArgumentContainer {

    /**
     * The value index of the iterable.
     */
    private final Integer start;

    /**
     * The step index of the iterable.
     */
    private final Integer step;

    /**
     * The length of the slice.
     */
    private final int len;

    /**
     * Prepares the slice values.
     * @param len     The original length.
     * @param rStart  The value parameter.
     * @param rStop   The stop parameter.
     * @param rStep   The step parameter.
     */
    static int[] prepare(int len, Integer rStart, Integer rStop, Integer rStep) {
        int start;
        int stop;
        int step;
        int slicelength;

        if (rStep == null) {
            step = 1;
        } else {
            step = rStep;
            if (step == 0) {
                throw new IllegalArgumentException("slice step cannot be zero");
            }
        }

        if (rStart == null) {
            start = step < 0 ? len - 1 : 0;
        } else {
            start = rStart;
            if (start < 0) {
                start += len;
            }
            if (start < 0) {
                start = step < 0 ? -1 : 0;
            }
            if (start >= len) {
                start = step < 0 ? len - 1 : len;
            }
        }

        if (rStop == null) {
            stop = step < 0 ? -1 : len;
        } else {
            stop = rStop;
            if (stop < 0) {
                stop += len;
            }
            if (stop < 0) {
                stop = -1;
            }
            if (stop > len) {
                stop = len;
            }
        }

        if ((step < 0 && stop >= start) || (step > 0 && start >= stop)) {
            slicelength = 0;
        } else if (step < 0) {
            slicelength = (stop - start + 1) / (step) + 1;
        } else {
            slicelength = (stop - start - 1) / (step) + 1;
        }

        return new int[] {start, stop, step, slicelength};
    }

    /**
     * Creates a new slice iterable.
     * @param parent    The parent iterable.
     * @param start     The first item to return.
     * @param stop      The first item not to be returned.
     * @param step      The step.
     */
    public SliceIterable(ArgumentContainer parent, Integer start, Integer stop, Integer step) {
        super(parent);
        int[] values = SliceIterable.prepare(parent.size(), start, stop, step);
        this.start = values[0];
        this.step = values[2];
        this.len = values[3];
    }

    @Override
    public int size() {
        return this.len;
    }

    @Override
    public <T> T get(int index, Class<? extends T> cls) {
        int rindex = this.getRealIndex(index);
        if (rindex == -1)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
        return this.getParent().get(this.start + (rindex * this.step), cls);
    }
}