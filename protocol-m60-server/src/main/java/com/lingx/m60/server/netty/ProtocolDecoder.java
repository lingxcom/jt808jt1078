package com.lingx.m60.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


/**
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
	private static final int MIN_HEADER_SIZE = 10;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in == null) {
			return;
		}

		if (in.readableBytes() < MIN_HEADER_SIZE) {
			return;
		}
		in.markReaderIndex();
		while (in.isReadable()) {
			in.markReaderIndex();
			int packetBeginIndex = in.readerIndex();
			byte tag1 = in.readByte();
			byte tag2 = in.readByte();
			if (tag1 == 0x4C &&tag2 == 0x44 && in.isReadable()) {
				byte[] tmp1 = new byte[8];
				in.readBytes(tmp1);
				int length=in.readUnsignedShort();
				if(in.readableBytes()>=(length+2)) {
					tmp1 = new byte[length];
					in.readBytes(tmp1);
					in.readByte();
					int endTag=in.readUnsignedByte();
					
					int pos = in.readerIndex();
					int packetLength = pos - packetBeginIndex;
					if (packetLength > 1&&endTag==0xAA) {
						byte[] tmp = new byte[packetLength];
						in.resetReaderIndex();
						in.readBytes(tmp);
						out.add(tmp); 
					} else {
						
						in.resetReaderIndex();
						in.readByte(); 
					}
				}else {
					in.resetReaderIndex();
					in.readByte(); 
				}
			}
		}

		return;
	}


}
