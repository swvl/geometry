package com.swvl.geometry.ds.rtrees;

import org.apache.hadoop.io.Writable;

/**
 * R-tree implementation. R-trees handles multi-dimensional data such as geometrical data.
 * The tree structure is represented by an array of nodes to optimize time complexity.
 *
 * @author Hatem Morgan
 */
public class Rtree {

    protected int degree;

    protected int height;

    protected int numNodes;

    protected int numLeaves;

    protected Node[] tree;

    protected int numRecords;

    protected int N;

    protected int[] range;

    public Rtree() {

    }

    public Rtree(int height, int degree) {
        this.degree = degree;
        this.numNodes = (int) Math.ceil((Math.pow(this.degree, height) - 1) / (this.degree - 1));
        this.numLeaves = (int) Math.pow(this.degree, height - 1);

        range = new int[degree];
        range[0] = 1 - degree + 1;
        for (int i = 1; i < degree; ++i)
            range[i] = range[i - 1] + 1;
    }
}
