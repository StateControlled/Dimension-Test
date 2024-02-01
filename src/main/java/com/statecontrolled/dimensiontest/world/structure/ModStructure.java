package com.statecontrolled.dimensiontest.world.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import net.minecraft.util.RandomSource;

public class ModStructure implements Comparable<ModStructure> {
    private RandomSource random;
    private String name;
    private ArrayList<int[]> options;
    private boolean collapsed;

    public ModStructure(String name, ArrayList<int[]> options, RandomSource random) {
        this.name = name;
        this.options = options;
        this.random = random;
    }

    @Override
    public int compareTo(ModStructure that) {
        return Integer.compare(this.entropy(), that.entropy());
    }

    public int entropy() {
        return options.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModStructure that)) {
            return false;
        }
        return this.name.equals(that.name) && this.collapsed == that.collapsed;
    }

    @Override
    public int hashCode() {
        return 87651703 + this.name.hashCode() + Objects.hash(collapsed);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void collapse() {
        int rand = random.nextInt(options.size());
        int[] temp = options.get(rand);
        options = new ArrayList<>(Collections.singletonList(temp));
        collapsed = true;
    }

    public ArrayList<int[]> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<int[]> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

}
