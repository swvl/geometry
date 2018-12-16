package com.swvl.geometry.ds.rtrees;

import com.swvl.geometry.io.TextSerializerHelper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * R-tree implementation. R-trees handles multi-dimensional data such as geometrical data.
 * The tree structure is represented by an array of nodes to optimize time complexity.
 *
 * @author Hatem Morgan
 */
public class Rtree implements Writable {

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
}
