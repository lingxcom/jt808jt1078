package com.lingx.gt06.server.msg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lingx.jt808.core.Constants;
import com.lingx.jt808.core.service.RedisService;
import com.lingx.jt808.core.support.MyByteBuf;
import com.lingx.jt808.core.utils.Utils;
import com.lingx.jt808.server.service.JT808ServerConfigService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component
public class Msg0x22 implements IMsgHandler{
	@Autowired
	private JT808ServerConfigService configService;
	@Autowired
	protected RedisService redisService;
	@Override
	public int getMsgId() {
		return 0x22;
	}

	@Override
	public void handle(ByteBuf data, String tid, int msgId, int msgSn, ChannelHandlerContext ctx, boolean isVersion,
			byte[] bytes,String sn) throws Exception {
		MyByteBuf buff=new MyByteBuf(data);
		String temp1=buff.readStringBCD(6);
		int wxNum=buff.readUnsignedByte();
		long lat=buff.readUnsignedInt();
		long lng=buff.readUnsignedInt();
		int speed=buff.readUnsignedByte();
		int temp=buff.readUnsignedShort();
		int fx=temp&0b0000001111111111;

		buff.readUnsignedShort();//MCC
		buff.readUnsignedByte();//MNC
		buff.readUnsignedShort();//LAC
		buff.readUnsignedShort();//CELLID
		buff.readUnsignedByte();//CELLID
		int acc=buff.readUnsignedByte();//ACC
		int status=0;
		if ((temp & 0b0001000000000000) > 0) {// 是否定位
			status += 2;
		}
		if(acc==1)status += 1;
		int lc=0;
		buff.readUnsignedByte();//上传模式
		buff.readUnsignedByte();//是否补传
		if(buff.readableBytes()>=4) {
			lc=buff.readInt();
		}
		String gpstime=getTime(temp1);
		Map<String, Object> map = new HashMap<>();
		map.put("tid", tid);
		map.put("type", "0x0200");
		map.put("alarm", 0);
		map.put("status", status);
		map.put("lat", lat/1800000f);
		map.put("lng", lng/1800000f);
		map.put("height", 0);
		map.put("speed",speed);
		map.put("direction", fx);
		map.put("gpstime", gpstime);
		map.put("systime", Utils.getTime());
		map.put("ts", System.currentTimeMillis());
		map.put("online", 1);

		map.put("A01", lc);
		// 在这里把网关ID传到处理器
		if (this.configService != null)
			map.put(Constants.JT808_SERVER_ID, this.configService.getJt808ServerId());

		
		String json = JSON.toJSONString(map);

		// System.out.println(json);
		if (this.redisService != null) {
			this.redisService.push(Constants.JT808_0200_DATA, json);
		} else {
			System.out.println(json);
		}
		map.clear();
		map = null;
		json = null;

	}
	public String getTime(String temp){
		boolean isUTC8=false;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<temp.length();i+=2){
			if(i==0){
				sb.append(String.valueOf(2000+Integer.parseInt(temp.substring(i,i+2),16)));
			}else{
				sb.append(Utils.leftAdd0(String.valueOf(Integer.parseInt(temp.substring(i,i+2),16)), 2));
			}
		}
		//System.out.println("YL:"+sb.toString());
		if(isUTC8)return sb.toString(); else
		return timeZoneTransfer(sb.toString(),"yyyyMMddHHmmss","0","+8");
	}

	public static String timeZoneTransfer(String time, String pattern, String nowTimeZone, String targetTimeZone) {
       
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + nowTimeZone));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return "";
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + targetTimeZone));
        return simpleDateFormat.format(date);
    }
	public static void main(String args[]) {
		Msg0x22 obj=new Msg0x22();
		String str="78782222180215081012CC04458F770C70295300145000000000000000000100010011B7450D0A";
		try {
			MyByteBuf buff = new MyByteBuf(Utils.hexToBytes(str));
				buff.readByte();buff.readByte();
				int length = buff.readUnsignedByte();
				int msgId = buff.readUnsignedByte();
				ByteBuf content = buff.readByteBuf(length-5);
			obj.handle(content, null, 0, 0, null, false, null,"1234");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
