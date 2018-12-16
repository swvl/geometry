package com.swvl.geometry.ds.Rtree;

import com.swvl.geometry.io.TextSerializerHelper;
import com.swvl.geometry.shapes.Point;
import com.swvl.geometry.shapes.Rectangle;
import com.swvl.geometry.shapes.Shape;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import javax.naming.OperationNotSupportedException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

/**
 * R-tree implementation. R-trees handles multi-dimensional data such as geometrical data.
 * <p>
 * {@link #bulkLoad(List, boolean)} method can be used to bulk load rtree using STR packing algorithm
 * `To do queries against the tree, use the {@link #query(Shape)}
 * <p>
 * The tree structure is represented by an array of nodes to optimize space and accessing complexities.
 *
 * @author Hatem Morgan
 */
public class STRRtree implements Writable {

    /* Size of tree header on disk. Height + Degree + Number of records + offset of first stored record in block + sperators */
    private static final int TreeHeaderSize = 4 + 4 + 4 + 4 + 4;

    /* Size of a node. Start offset of data + End offset of data + MBR (x, y) */
    private static final int NodeSize = 4 + 4 + 8 * 2;

    private int degree;

    private int height;

    private int numNodes;

    private int numLeaves;

    private Node[] tree;

    private int numRecords;

    private int N;

    private int[] range;

    public STRRtree() {

    }

    public STRRtree(int height, int degree) {
        this.degree = degree;
        this.numNodes = (int) Math.ceil((Math.pow(this.degree, height) - 1) / (this.degree - 1));
        this.numLeaves = (int) Math.pow(this.degree, height - 1);

        range = new int[degree];
        range[0] = 1 - degree + 1;
        for (int i = 1; i < degree; ++i)
            range[i] = range[i - 1] + 1;
    }

    public STRRtree(int numRecords, long bytesAvailable) {
        this.N = numRecords;
        this.degree = findBestDegree(bytesAvailable, N);
        this.numRecords = numRecords;

        height = (int) Math.ceil(Math.log(this.N) / Math.log(this.degree));
        this.numNodes = (int) Math.ceil((Math.pow(this.degree, height) - 1) / (this.degree - 1));
        this.numLeaves = (int) Math.pow(this.degree, height - 1);
        this.tree = new Node[numNodes + 1]; // start index = 1

        /*
         * Range for calculating the idx of children for a parent node.
         * Range for a given degree = [1 - degree + 1, 1]
         *
         * Examples:
         * For c = 2 and node[idx]: Range = [0,1], left child at idx * 2 + 0 and right child is at idx * 2 + 1
         * For c = 3 and node[idx]: Range = [-1,1], 1st child is at idx * 3 + -1, 2nd child is at idx * 3 + 0, and 2nd child is at idx * 3 + 1
         */
        range = new int[degree];
        range[0] = 1 - degree + 1;
        for (int i = 1; i < degree; ++i)
            range[i] = range[i - 1] + 1;
    }

    public Text[] bulkLoad(List<Shape> data, boolean isSortedOnXDimension) {
        /* ======================================================================================== */
        /* ============================ Divide point into vertical slices ========================= */
        /* ======================================================================================== */

        if (!isSortedOnXDimension) { // sort on x-dimension if not sorted
            Collections.sort(data, new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                    return Double.compare(s1.getCenterPoint().x, s2.getCenterPoint().x);
                }
            });
        }

        /*
         * Number of leaves = N/degree
         * Number of records in each leave = degree
         * Number vertical slice = sqrt(N/degree)
         * Number of records in each vertical slice = c * sqrt(N/degree)
         * Number of horizontal slices in each vertical slice = sqrt(N/degree)
         * Number of records in each horizontal slices in each vertical slice = degree
         */
        int numVs = (int) Math.ceil(Math.sqrt((double) (this.N) / (this.degree)));
        int vsNumRecords = this.degree * numVs;
        Shape[][] vsRecords = new Shape[numVs][vsNumRecords];

        int currCount = 0, vsIdx = 0, idx = 0;
        while (idx < data.size()) { // iterate over sorted data to assign records to vertical slice

            if (currCount < vsNumRecords) { // add records to vs
                vsRecords[vsIdx][currCount] = data.get(idx);
                currCount++;
                idx++;
            } else {
                vsIdx++; // increment vsIdx for next vs
                currCount = 0; // reset count tracking variable
            }
        }

        /* ============================================================================================== */
        /* =========== For each vertical slices Sort data on y-dimension to construct leaves ============ */
        /* ============================================================================================== */
        for (int i = 0; i < numVs; ++i) { // iterate over vertical slice and sort by y-dimensions

            Arrays.sort(vsRecords[i], new Comparator<Shape>() {
                @Override
                public int compare(Shape s1, Shape s2) {
                    if (s1 == null || s2 == null)
                        return 0;

                    if (s1.getCenterPoint().y == s2.getCenterPoint().y)
                        return Double.compare(s1.getCenterPoint().x, s2.getCenterPoint().x);

                    return Double.compare(s1.getCenterPoint().y, s2.getCenterPoint().y);
                }
            });
        }

        /* Flattening vsRecords to write records on desk with the same order of offsets stored in Rtree's leaves*/
        Shape[] sortedShapes = new Shape[N];
        int consumedNumRecords = 0, count = 0;
        for (int i = 0; i < vsRecords.length; ++i) {
            for (int j = 0; j < vsRecords[i].length; ++j) {
                if (vsRecords[i][j] != null) {
                    sortedShapes[consumedNumRecords + j] = vsRecords[i][j];
                    count++;
                }
            }

            consumedNumRecords += count;
            count = 0;
        }

        /* ========================================================================================== */
        /* ================================== Building Rtree's leaves =============================== */
        /* ========================================================================================== */

        int leafIdx = this.numNodes - this.numLeaves + 1, leafOffset = 0;
        idx = 0;
        currCount = 0;
        long currSize = 0;
        Text[] sortedTextShapes = new Text[N];
        Text tmp;
        Rectangle leafMBR = new Rectangle(new Point(Double.MAX_VALUE, Double.MAX_VALUE), new Point(Double.MIN_VALUE, Double.MIN_VALUE));
        while (idx < sortedShapes.length) {

            if (currCount < degree) { // collect N records where N = degree

                leafMBR.expand(sortedShapes[idx]);
                tmp = new Text();
                sortedShapes[idx].toText(tmp);
                sortedTextShapes[idx] = tmp;
                currSize += tmp.getLength() + 1; // 1 for new line separator
                currCount++;
                idx++;

            } else { // construct a leaf and reset tracking variables

                tree[leafIdx] = new Node(leafOffset, currSize, currCount, leafMBR);
                leafIdx++;

                leafMBR = new Rectangle(new Point(Double.MAX_VALUE, Double.MAX_VALUE), new Point(Double.MIN_VALUE, Double.MIN_VALUE));
                leafOffset += currSize;
                currCount = 0;
                currSize = 0;

            }
        }

        if (currCount != 0) // check for collected N records where N < degree
            tree[leafIdx] = new Node(leafOffset, currSize, currCount, leafMBR);


        /* ========================================================================================== */
        /* =============================== Building Rtree's upper levels ============================ */
        /* ========================================================================================== */

        int numNonLeaves = numNodes - numLeaves;
        for (int i = numNonLeaves; i > 0; --i) {

            if (tree[(i * degree) + range[0]] == null) // no children
                continue;

            long offset = tree[(i * degree) + range[0]].offset;

            long size = 0;
            count = 0;
            Rectangle mbr = new Rectangle(new Point(Double.MAX_VALUE, Double.MAX_VALUE), new Point(Double.MIN_VALUE, Double.MIN_VALUE));
            for (int j = 0; j < range.length; ++j) {
                if (tree[(i * degree) + range[j]] != null) {
                    mbr.expand(tree[(i * degree) + range[j]].mbr);
                    size += tree[(i * degree) + range[j]].size;
                    count += tree[(i * degree) + range[j]].count;
                }
            }

            tree[i] = new Node(offset, size, count, mbr);
        }

        return sortedTextShapes;
    }


    /**
     * Query Rtree by any shape using basic geometrical operations ( inner intersection, edge (boundary) intersection, and contains).
     * It does not load records from files. It postponed loading for next step that performs random-file-access.
     *
     * @param queryRange geometrical shape representing query range
     * @return return offsets of records in file satisfying queryRange without checking for any leaf's record satisfiability (because it requires loading record from file and check it)
     * @throws OperationNotSupportedException
     */
    public ArrayList<Long> query(Shape queryRange) throws OperationNotSupportedException {
        ArrayList<Long> resOffsets = new ArrayList<Long>();
        query(queryRange, 1, resOffsets);
        return resOffsets;
    }

    private void query(Shape queryRange, int idx, ArrayList<Long> res) throws OperationNotSupportedException {
        /* out of range, no inner intersection or edge (border) intersection */
        if (tree[idx] == null || (!queryRange.isIntersected(tree[idx].mbr) && !queryRange.isEdgeIntersection(tree[idx].mbr)))
            return;

        if (queryRange.contains(tree[idx].mbr)) { // fully intersection
            for (long i = tree[idx].offset; i < tree[idx].offset + tree[idx].count; ++i)
                res.add(i);

            return;
        }



        /* ================================================================================= */
        /* =============================== Partial intersection ============================ */
        /* ================================================================================= */

        if (((idx * degree) + range[0]) > numNodes) { // leaf

            for (long i = tree[idx].offset; i < tree[idx].offset + tree[idx].count; ++i)
//                if (queryRange.contains(data[i].getMBR()))
                res.add(i);

            return;
        }

        for (int i = 0; i < range.length; ++i) // not a leaf then search its children
            query(queryRange, (idx * degree) + range[i], res);

    }


    /**
     * Find best degree given maximum size in bytes to store tree and number of records.
     * <p>
     * The algorithm tries different heights based on mathematically proven formulas to get the best degree
     *
     * @param bytesAvailable Maximum number of bytes to store tree
     * @param numRecords     Number of input records (N)
     * @return best degree
     */
    public static int findBestDegree(long bytesAvailable, int numRecords) {
        // Maximum number of nodes that can be stored in the bytesAvailable
        long maxNumNodes = (bytesAvailable - TreeHeaderSize) / NodeSize;

        // Calculate maximum possible tree height to store the given record count with c = 2
        int h_max = (int) Math.ceil(Math.log(numRecords) / Math.log(2));

        // Minimum height is always 1 (degree = recordCount)
        int h_min = 1;

        // Best degree is the minimum degree
        int d_best = Integer.MAX_VALUE;

        // log record count
        double log_recordcount = Math.log(numRecords);

        // Find the best height among all possible heights
        for (int h = h_min; h <= h_max; h++) {
            // degree = (N) ^ (1/h)
            int d = (int) Math.ceil(Math.pow((double) numRecords, (1.0 / (double) (h))));

            // Some heights are invalid, recalculate the height to ensure it's valid
            int h_recalculated = (int) Math.ceil(log_recordcount / Math.log(d));

            if (h != h_recalculated) // in-correct h
                continue;

            // Number of nodes = ((c^h) - 1)/ (c - 1)
            int numNodes = (int) Math.ceil((Math.pow(d, h) - 1) / (d - 1));

            if (numNodes < maxNumNodes && d < d_best) // minimizing number of nodes and degree
                d_best = d;

        }
        return d_best;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text text = new Text();
        TextSerializerHelper.serializeInt(height, text, ',');
        TextSerializerHelper.serializeInt(degree, text, ',');
        TextSerializerHelper.serializeInt(numRecords, text, ',');
        TextSerializerHelper.serializeInt(numNodes, text, '\n');

        out.write(text.getBytes(), 0, text.getLength());

        for (Node node : tree) {
            text.clear();
            if (node != null)
                node.toText(text);
            else
                text.set("null");

            text.append(new byte[]{(byte) '\n'}, 0, 1);
            out.write(text.getBytes(), 0, text.getLength());
        }

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        Text header = new Text(in.readLine());
        height = TextSerializerHelper.consumeInt(header, ',');
        degree = TextSerializerHelper.consumeInt(header, ',');
        numRecords = TextSerializerHelper.consumeInt(header, ',');
        numNodes = TextSerializerHelper.consumeInt(header, '\n');

        tree = new Node[numNodes];
        Text tmp = new Text();
        String line;
        for (int i = 0; i < numNodes; ++i) {
            line = in.readLine();
            if (!line.equals("null")) {
                tmp.clear();
                tree[i] = new Node();
                tmp.set(line);
                tree[i].fromText(tmp);
            }
        }
    }


    public static void main(String[] args) throws OperationNotSupportedException, IOException, ClassNotFoundException {
        Shape[] data = new Shape[]{
                new Point(2, 6),
                new Point(3, 4),
                new Point(3, 8),
                new Point(4, 7),
                new Point(6, 2),
                new Point(6, 4),
                new Point(7, 3),
                new Point(7, 4),
                new Point(7, 6),
                new Point(8, 5)
        };


        STRRtree rtree = new STRRtree(data.length, (long) (128 * 1024 * 1024 * 0.1));
        System.out.println(rtree.degree);
        System.out.println(rtree.numLeaves);
        System.out.println(rtree.numNodes);
        System.out.println("=========");

        Text[] sortedData = rtree.bulkLoad(Arrays.asList(data), false);
        System.out.println(Arrays.toString(sortedData));

        for (int i = 0; i < rtree.tree.length; ++i)
            System.out.println(i + "  " + rtree.tree[i]);

        int leafIdx = rtree.numNodes - rtree.numLeaves + 1, count = 0;
        boolean doOverlap = false;
        for (int i = leafIdx; i < rtree.tree.length; ++i)
            for (int j = leafIdx; j < rtree.tree.length && i != j; ++j)
                if (rtree.tree[i] != null && rtree.tree[j] != null)
                    if (rtree.tree[i].mbr.isIntersected(rtree.tree[j].mbr)) {
                        doOverlap = true;
                        System.out.println(rtree.tree[i].mbr + "  " + rtree.tree[j].mbr);
                        count++;
                    }

        System.out.println("Leaves overlap = " + doOverlap);
        System.out.println("Number of overlaps = " + count);

        System.out.println(" ");
        System.out.println("=======================================");
        System.out.println(" ");

        Rectangle range = new Rectangle(3.0, 2.0, 6.0, 6.0);
        ArrayList<Long> res = rtree.query(range);
        System.out.println(res);

//        for (int i = 0; i < res.size(); ++i)
//            if (range.contains(sortedData[res.get(i)]))
//                System.out.println(sortedData[res.get(i)]);

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(rtree.tree);
//        byte[] bytes = bos.toByteArray();
//        System.out.println(Arrays.toString(bytes));

//        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
//        ObjectInputStream oin = new ObjectInputStream(in);
//        Node[] tree = (Node[])oin.readObject();
//        System.out.println(Arrays.toString(tree));
    }
}
