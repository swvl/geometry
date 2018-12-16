package com.swvl.geometry.ds.rtrees;

import com.swvl.geometry.io.TextSerializable;
import com.swvl.geometry.io.TextSerializerHelper;
import com.swvl.geometry.shapes.Rectangle;
import org.apache.hadoop.io.Text;

/**
 * Rtree Node class
 *
 * @author Hatem Morgan
 */
public class Node implements TextSerializable {

    /* Offset of first record in file represented by this node (inclusive)*/
    public long offset;

    /* Size of records represented by this node starting from offset*/
    public long size;

    /* Number of records covered by this node*/
    public int count;

    /* Minimum bounding rectangle for representing spatial range in the form of rectangle*/
    public Rectangle mbr;

    public Node() {
    }

    public Node(long offset, long size, int count, Rectangle mbr) {
        this.offset = offset;
        this.size = size;
        this.count = count;
        this.mbr = mbr;
    }

    @Override
    public Text toText(Text text) {
        TextSerializerHelper.serializeLong(offset, text, ',');
        TextSerializerHelper.serializeLong(size, text, ',');
        TextSerializerHelper.serializeInt(count, text, ',');
        mbr.toText(text);
        return text;
    }

    @Override
    public void fromText(Text text) {
        offset = TextSerializerHelper.consumeLong(text, ',');
        size = TextSerializerHelper.consumeLong(text, ',');
        count = TextSerializerHelper.consumeInt(text, ',');
        mbr = new Rectangle();
        mbr.fromText(text);
    }

    @Override
    public String toString() {
        return "Node{" +
                "offset=" + offset +
                ", size=" + size +
                ", count=" + count +
                ", mbr=" + mbr +
                '}';
    }
}
