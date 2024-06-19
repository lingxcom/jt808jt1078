package com.lingx.m60.server.msg;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lingx.jt808.core.Constants;
import com.lingx.jt808.core.event.JT808Location0200Event;
import com.lingx.jt808.core.service.RedisService;
import com.lingx.jt808.core.utils.Utils;
import com.lingx.jt808.server.service.JT808ServerConfigService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component//@GELOC,2024-02-21T16:08:56.00,39.8498030983,116.3236076683,296.5260,0.0000,0.0000,0.0000,15,5,12.40,27.242,58.330,2,0.1500,219.0900,M6017322000013,,,*5E
public class Msg0x01 implements IMsgHandler{
	@Autowired
	private JT808ServerConfigService configService;
	@Autowired
	protected RedisService redisService;

	@Autowired
	private ApplicationContext spring;
	@Override
	public int getMsgId() {
		return 0x01;
	}

	@Override
	public void handle(String data, String tid, int msgId, int msgSn, ChannelHandlerContext ctx, boolean isVersion,
			byte[] bytes) throws Exception {
		String array[]=data.split(",");
		int status=0;
		String gpstime=array[1];
		gpstime=gpstime.replace("-", "").replace("T", "").replace(":", "");
		gpstime=gpstime.substring(0,14);
		double lat=Double.parseDouble(array[2]);
		double lng=Double.parseDouble(array[3]);
		double height=Double.parseDouble(array[4]);
		double speed=Double.parseDouble(array[14]);
		double fx=Double.parseDouble(array[15]);
		if(speed<1)speed=0;
		
		status=status+2;
		Map<String, Object> map = new HashMap<>();
		map.put("tid", tid);
		map.put("type", "0x0200");
		map.put("alarm", 0);
		map.put("status", status);
		map.put("lat", lat);
		map.put("lng", lng);
		map.put("height", height);
		map.put("speed",speed);
		map.put("direction", new Double(fx).intValue());
		map.put("gpstime", gpstime);
		map.put("systime", Utils.getTime());
		map.put("ts", System.currentTimeMillis());
		map.put("online", 1);

		map.put("A01", 0);
		map.put("A31", array[8]);
		/*0：无效解
		1：单点解
		2：差分解
		4：固定解
		5：浮点解*/
		String temp="";
		switch(Integer.parseInt(array[9])){
		case 0:
			temp="无效解,";
			break;
		case 1:
			temp="单点解,";
			break;
		case 2:
			temp="差分解,";
			break;
		case 4:
			temp="固定解,";
			break;
		case 5:
			temp="浮点解,";
			break;
		}
		//System.out.println(JSON.toJSONString(map));
		Object obj=Msg0x02.DL_CACHE.getIfPresent(tid);
		if(obj!=null) {
			temp+="电量:"+obj.toString()+"%,";
		}
		if(height>0) {
			temp+="海拔:"+height+"米,";//在listener中处理
		}
		map.put("status_str", temp);
		// 在这里把网关ID传到处理器
		if (this.configService != null)
			map.put(Constants.JT808_SERVER_ID, this.configService.getJt808ServerId());

		if(spring!=null)
			spring.publishEvent(new JT808Location0200Event(spring,map,bytes));
		
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
	
	public static void main(String args[]) {
		Msg0x01 obj=new Msg0x01();
		String hexstring="4047454C4F432C323032342D30332D32385431353A34333A32362E30302C33392E383438393938343030302C3131362E333235393230383030302C35392E393936302C302E303030302C302E303030302C302E303030302C31302C322C352E36302C31322E3534302C32392E3432362C312C302E303030302C302E303030302C4D363031373332323030303031332C2C2C2A30440D0A";
		try {
			String temp=new String(Utils.hexToBytes(hexstring));
			System.out.println(temp);
			obj.handle(temp, null, 0, 0, null, false, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
