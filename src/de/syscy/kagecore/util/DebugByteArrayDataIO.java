package de.syscy.kagecore.util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import de.syscy.kagecore.KageCore;

public class DebugByteArrayDataIO implements ByteArrayDataInput, ByteArrayDataOutput {
	private ByteArrayDataInput in = null;
	private ByteArrayDataOutput out = null;

	public DebugByteArrayDataIO(ByteArrayDataInput in) {
		this.in = in;
	}

	public DebugByteArrayDataIO(ByteArrayDataOutput out) {
		this.out = out;
	}

	private void debugOut(Object object) {
		print("Wrote " + object.toString() + "(" + object.getClass().getSimpleName() + ")");
	}

	private void debugIn(Object object) {
		print("Read " + object.toString() + "(" + object.getClass().getSimpleName() + ")");
	}

	private void print(String string) {
		KageCore.debugMessage(string);
	}

	@Override
	public byte[] toByteArray() {
		return out.toByteArray();
	}

	@Override
	public void write(int i) {
		debugOut(i);
		out.write(i);
	}

	@Override
	public void write(byte[] byteArray) {
		debugOut(byteArray);
		out.write(byteArray);
	}

	@Override
	public void write(byte[] byteArray, int start, int end) {
		debugOut(byteArray);
		out.write(byteArray, start, end);
	}

	@Override
	public void writeBoolean(boolean b) {
		debugOut(b);
		out.writeBoolean(b);
	}

	@Override
	public void writeByte(int b) {
		debugOut(b);
		out.writeByte(b);
	}

	@Override
	@Deprecated
	public void writeBytes(String bytes) {
		debugOut(bytes);
		out.writeBytes(bytes);
	}

	@Override
	public void writeChar(int c) {
		debugOut(c);
		out.writeChar(c);
	}

	@Override
	public void writeChars(String chars) {
		debugOut(chars);
		out.writeChars(chars);
	}

	@Override
	public void writeDouble(double d) {
		debugOut(d);
		out.writeDouble(d);
	}

	@Override
	public void writeFloat(float f) {
		debugOut(f);
		out.writeFloat(f);
	}

	@Override
	public void writeInt(int i) {
		debugOut(i);
		out.writeInt(i);
	}

	@Override
	public void writeLong(long l) {
		debugOut(l);
		out.writeLong(l);
	}

	@Override
	public void writeShort(int s) {
		debugOut(s);
		out.writeShort(s);
	}

	@Override
	public void writeUTF(String string) {
		debugOut(string);
		out.writeUTF(string);
	}

	@Override
	public boolean readBoolean() {
		boolean b = in.readBoolean();
		debugIn(b);
		return b;
	}

	@Override
	public byte readByte() {
		byte b = in.readByte();
		debugOut(b);
		return b;
	}

	@Override
	public char readChar() {
		char c = in.readChar();
		debugIn(c);
		return c;
	}

	@Override
	public double readDouble() {
		double d = in.readDouble();
		debugIn(d);
		return d;
	}

	@Override
	public float readFloat() {
		float f = in.readFloat();
		debugIn(f);
		return f;
	}

	@Override
	public void readFully(byte[] byteArray) {
		in.readFully(byteArray);
		debugIn(byteArray);
	}

	@Override
	public void readFully(byte[] byteArray, int start, int end) {
		in.readFully(byteArray, start, end);
		debugIn(byteArray + " from " + start + " to " + end);
	}

	@Override
	public int readInt() {
		int i = in.readInt();
		debugIn(i);
		return i;
	}

	@Override
	public String readLine() {
		String line = in.readLine();
		debugIn(line);
		return line;
	}

	@Override
	public long readLong() {
		long l = in.readLong();
		debugIn(l);
		return l;
	}

	@Override
	public short readShort() {
		short s = in.readShort();
		debugIn(s);
		return s;
	}

	@Override
	public String readUTF() {
		String string = in.readUTF();
		debugIn(string);
		return string;
	}

	@Override
	public int readUnsignedByte() {
		int b = in.readUnsignedByte();
		debugIn(b);
		return b;
	}

	@Override
	public int readUnsignedShort() {
		int s = in.readUnsignedShort();
		debugIn(s);
		return s;
	}

	@Override
	public int skipBytes(int amount) {
		int i = in.skipBytes(amount);
		debugIn("Skipped " + i + " bytes");
		return i;
	}
}