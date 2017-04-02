package com.zhangfuwen.collector;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.*;

/**
 * Created by dean on 3/13/17.
 */
enum FunctionCode {
    ReadOnlineStatus(0x01), ReadNodeSensors(0x03), WriteCoils(0x10),Error(0x81);
    byte code;
    FunctionCode(byte code) {
        this.code = code;
    }
    FunctionCode(int code) {
        this.code = (byte)code;
    }
};

public class ModbusTCPPacket {
    short transactionId = 0x1501;
    short protocalId =0x0000;
    short length;
    byte addr;
    byte function;
    byte [] data;
    public ModbusTCPPacket(){};
    public static ModbusTCPPacket NewCommandPacket(byte addr, byte function, byte []data) {
        ModbusTCPPacket packet = new ModbusTCPPacket();
        packet.addr = addr;
        packet.function = function;
        packet.length = 0x06;
        packet.data = data;
        return packet;
    }

    public static ModbusTCPPacket ReadResponsePacket(DataInputStream in) throws IOException {
        ModbusTCPPacket packet = new ModbusTCPPacket();
        packet.transactionId = in.readShort();
        packet.protocalId = in.readShort();
        packet.length = in.readShort();
        packet.addr = in.readByte();
        packet.function = in.readByte();
        packet.data = new byte[200];
        // function code and addr (which is already read) is counted into data length
        int ret = in.read(packet.data,0, packet.length-2);
        if( ret <packet.length-2) {
            throw new IOException(String.format("not enough data, got:%d, expect:%d",ret, packet.length -1 ));
        }
        return packet;
    }

    public byte[] toByteArray() {
        ByteBuffer buf = ByteBuffer.allocate(8+data.length);
        buf.putShort(this.transactionId);
        buf.putShort(this.protocalId);
        buf.putShort(length);
        buf.put(this.addr);
        buf.put(this.function);
        buf.put(data);
        return buf.array();
    }
}
