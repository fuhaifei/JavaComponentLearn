package com.fhf.mapreduce.serialize;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements Writable {
    private int upStream;
    private int downStream;

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(upStream);
        out.writeInt(downStream);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.setUpStream(in.readInt());
        this.setDownStream(in.readInt());
    }

    @Override
    public String toString() {
        return "FlowBean{" +
                "upStream=" + upStream +
                ", downStream=" + downStream +
                '}';
    }

    public int getUpStream() {
        return upStream;
    }

    public void setUpStream(int upStream) {
        this.upStream = upStream;
    }

    public int getDownStream() {
        return downStream;
    }

    public void setDownStream(int downStream) {
        this.downStream = downStream;
    }
}
