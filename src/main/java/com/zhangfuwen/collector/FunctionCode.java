package com.zhangfuwen.collector;

/**
 * Created by dean on 4/22/17.
 */
public enum FunctionCode {
    ReadOnlineStatus(0x01), ReadNodeSensors(0x03), WriteCoils(0x10),Error(0x81);
    public byte code;
    FunctionCode(byte code) {
        this.code = code;
    }
    FunctionCode(int code) {
        this.code = (byte)code;
    }
};
