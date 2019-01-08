package com.swvl.geometry.models;

import com.swvl.geometry.shapes.Point;
import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * STPoint follow OpenStreetMap (OSM) point convention which is based on OGC
 *
 * @author Hatem Morgan
 */
public class STPoint extends Point {

    public long time; // can be mapped to osm:node id in OSM
    public Map<String, String> tags = new HashMap<String, String>();

    public STPoint() {
    }

    public STPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void fromText(Text text) {
        time = TextSerializerHelper.consumeLong(text, '\t');
        x = TextSerializerHelper.consumeDouble(text, '\t');
        y = TextSerializerHelper.consumeDouble(text, '\t');

        if (text.getLength() > 0)
            TextSerializerHelper.consumeMap(text, tags);
    }

    @Override
    public Text toText(Text text) {
        TextSerializerHelper.serializeLong(time, text, '\t');
        TextSerializerHelper.serializeDouble(x, text, '\t');
        TextSerializerHelper.serializeDouble(y, text, tags.isEmpty() ? '\0' : '\t');
        TextSerializerHelper.serializeMap(text, tags);
        return text;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(time);

        super.write(out);

        Text t = new Text();
        TextSerializerHelper.serializeMap(t, tags);
        out.writeUTF(t.toString());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.time = in.readLong();
        super.readFields(in);

        String s = in.readUTF();
        TextSerializerHelper.consumeMap(new Text(s), tags);
    }

    @Override
    public Point clone() {
        STPoint c = new STPoint();
        c.time = time;
        c.x = x;
        c.y = y;
        c.tags = new HashMap<String, String>(tags);
        return c;
    }

    @Override
    public String toString() {
        return "STPoint{" +
                "(" + x + "," + y + ")" +
                "time=" + time +
                ", tags=" + tags +
                '}';
    }

    public static void main(String[] args) {
        Text text = new Text("1542056062302\t31.343805\t30.1572617\t[date#2018-11-12T22:54:21.227+02:00,role#captain,method#WS,bearing#293,name#Mohamed Abdelaty Abdelatef Abdelaty,eventType#CaptainLocationUpdated,id#5bd7213883f05d001872206b,rideId#5be4e996b567940011ff6a50]\n");
        STPoint point = new STPoint();
        point.fromText(text);
    }
}
