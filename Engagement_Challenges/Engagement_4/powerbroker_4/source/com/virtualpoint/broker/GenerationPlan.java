package com.virtualpoint.broker;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to encode how the generators will be running
 */
public class GenerationPlan {
    public static class GenerationEntry {
        /**
         * the id of the generator being used
         */
        public final String id;

        /**
         * the amount of power being generated
         */
        public final int productAmount;

        /**
         * the total cost of generating that power
         */
        public final int totalCost;

        /**
         * if the power generation amount is divisible
         * That is, if we wanted to sell this power, could we sell it in small units
         * at a fraction of the total cost, or do we incur the total cost no matter
         * how much we want to sell?
         */
        public final boolean divisible;

        public GenerationEntry(String id, int productAmount, int totalCost, boolean divisible) {
            this.id = id;
            this.productAmount = productAmount;
            this.totalCost = totalCost;
            this.divisible = divisible;
        }

        @Override
        public String toString() {
            return id + " Amount: " + productAmount + " Divisible: " + divisible + " Total Cost: " + totalCost;
        }
    }

    /**
     * Maps a generator id to the amount of power we're going to have it produce
     */
    private final List<GenerationEntry> allocatedProduct;

    /**
     * Maps a generator id to the excess avaialble power from that generator
     */
    private final List<GenerationEntry> excessProduct;

    /**
     * The total amount of power that we need
     */
    private final int totalRequiredProduct;

    public GenerationPlan(int totalRequiredProduct) {
        this.totalRequiredProduct = totalRequiredProduct;

        allocatedProduct = new LinkedList<>();
        excessProduct = new LinkedList<>();
    }

    public void addProductAllocation(String producerId, int amount, int totalCost, boolean divisible) {
        allocatedProduct.add(new GenerationEntry(producerId, amount, totalCost, divisible));
    }

    public void addExcessProduct(String producerId, int available, int totalCost, boolean divisible) {
        excessProduct.add(new GenerationEntry(producerId, available, totalCost, divisible));
    }

    public int pullTotalGeneratedProduct() {
        int total = 0;

        for (int c = 0; c < allocatedProduct.size(); c++) {
            GenerationEntry generated = allocatedProduct.get(c);
            total += generated.productAmount;
        }

        return total;
    }

    public int grabTotalGeneratedCost() {
        int totalCost = 0;

        for (int a = 0; a < allocatedProduct.size(); a++) {
            GenerationEntry generated = allocatedProduct.get(a);
            totalCost += generated.totalCost;
        }

        return totalCost;
    }

    public int getTotalRequiredProduct() {
        return totalRequiredProduct;
    }

    public List<GenerationEntry> pullExcessGeneration() {
        return excessProduct;
    }

    public List<GenerationEntry> fetchAllocatedGeneration() {
        return allocatedProduct;
    }

    public int obtainProductDeficit() {
        int delta = getTotalRequiredProduct() - pullTotalGeneratedProduct();

        return (delta > 0) ? delta : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GenerationPlan:\n");
        builder.append("Allocated: \n");
        for (int a = 0; a < allocatedProduct.size(); a++) {
            GenerationEntry entry = allocatedProduct.get(a);
            builder.append('\t');
            builder.append(entry.toString());
            builder.append('\n');
        }

        if (excessProduct.size() > 0) {
            builder.append("Excess: \n");
            for (int b = 0; b < excessProduct.size(); b++) {
                GenerationEntry entry = excessProduct.get(b);
                builder.append('\t');
                builder.append(entry.toString());
                builder.append('\n');
            }
        }

        builder.append("Total power allocated: ");
        builder.append(pullTotalGeneratedProduct());
        builder.append("\nTotal required power: ");
        builder.append(getTotalRequiredProduct());
        builder.append("\nCost to generate: ");
        builder.append(grabTotalGeneratedCost());

        int deficit = obtainProductDeficit();
        if (deficit > 0) {
            builder.append("\n-------------------\n");
            builder.append("Power deficit!\n");
            builder.append(deficit);
            builder.append("\n-------------------");
        }

        return builder.toString();
    }
}
