package ua.kiev.univ.schedule.model.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface RWable {
    void read(DataInputStream is) throws IOException;
    void write(DataOutputStream os) throws IOException;
}