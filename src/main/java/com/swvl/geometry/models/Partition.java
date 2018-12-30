package com.swvl.geometry.models;

import com.swvl.geometry.io.TextSerializerHelper;
import com.swvl.geometry.shapes.Point;
import com.swvl.geometry.shapes.Rectangle;
import com.swvl.geometry.shapes.Shape;
import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Information about a partition (file/(input folder))
 * <p>
 * Hatem Morgan
 */
public class Partition extends Rectangle {
    /**
     * A unique ID for this cell in a file. This must be set initially when
     * cells for a file are created. It cannot be guessed from cell dimensions.
     */
    public int cellId;

    /**
     * Name of the file that contains the data
     */
    public String filename = "";

    /**
     * Total number of records in this partition
     */
    public long recordCount;

    /**
     * Total size of data in this partition in bytes (uncompressed)
     */
    public long size;

    public long maxRecordSize;

    public Partition(Rectangle rect, long maxRecordSize) {
        super(rect);
        this.maxRecordSize = maxRecordSize;
    }

    public Partition(Rectangle rect, long maxRecordSize, long size, int count) {
        super(rect);
        this.maxRecordSize = maxRecordSize;
        this.size = size;
        this.recordCount = count;
    }

    public Partition(int cellId, Rectangle rect, long size, int count) {
        super(rect);
        this.maxRecordSize = maxRecordSize;
        this.size = size;
        this.recordCount = count;
    }

    public Partition(int cellId) {
        this();
        this.cellId = cellId;
        this.filename = "";
    }

    public Partition() {
        this.minPoint = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        this.maxPoint = new Point(Double.MIN_VALUE, Double.MIN_VALUE);
    }

    public Partition(Partition other) {
        super(other); // set rectangle attributes
        this.filename = other.filename;
        this.recordCount = other.recordCount;
        this.size = other.size;
        this.cellId = other.cellId;
    }

    public Partition(double x1, double y1, double x2, double y2) {
        this.minPoint = new Point(x1, y1);
        this.maxPoint = new Point(x2, y2);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(cellId);
        out.writeLong(recordCount);
        out.writeLong(size);
        out.writeLong(maxRecordSize);
        out.writeUTF(filename);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        super.readFields(in);
        this.cellId = in.readInt();
        this.recordCount = in.readLong();
        this.size = in.readLong();
        this.maxRecordSize = in.readLong();
        this.filename = in.readUTF();
    }

    @Override
    public Text toText(Text text) {
        TextSerializerHelper.serializeInt(cellId, text, ',');
        super.toText(text);
        text.append(new byte[]{','}, 0, 1);
        TextSerializerHelper.serializeLong(recordCount, text, ',');
        TextSerializerHelper.serializeLong(size, text, ',');

        TextSerializerHelper.serializeLong(maxRecordSize, text, ',');

        byte[] temp = (filename == null ? "" : filename).getBytes();
        text.append(temp, 0, temp.length);

        return text;
    }

    @Override
    public void fromText(Text text) {
        this.cellId = TextSerializerHelper.consumeInt(text, ',');
        super.fromText(text);
        text.set(text.getBytes(), 1, text.getLength() - 1); // Skip comma
        this.recordCount = TextSerializerHelper.consumeLong(text, ',');
        this.size = TextSerializerHelper.consumeLong(text, ',');
        this.maxRecordSize = TextSerializerHelper.consumeLong(text, ',');
        filename = text.toString();
    }

    @Override
    public Partition clone() {
        return new Partition(this);
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return this.filename.equals(((Partition) obj).filename);
    }

    public void expand(Partition p) {
        super.expand(p);
        this.size += p.size; // accumulate size
        this.recordCount += p.recordCount;
    }

    public void expand(Shape shape, int size) {
        super.expand(shape);
        this.size += size; // accumulate size
        this.recordCount++;
    }

    @Override
    public String toString() {
        return "Partition{" +
                "cellId = " + cellId +
                ", Rectangle: (" + minPoint.x + "," + minPoint.y + ")-(" + maxPoint.x + "," + maxPoint.y + ")" +
                ", recordCount=" + recordCount +
                ", size=" + size +
                ", maxRecordSize=" + maxRecordSize +
                '}';
    }
}
