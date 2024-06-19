package com.lingx.m60.server.netty;

import java.util.HashSet;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class SetsUtils {
	public static AttributeKey<Set<String>> TID_KEY=AttributeKey.valueOf("TID");

	public static boolean has(Channel channel,String tid) {
		boolean b=false;
		if(channel.hasAttr(TID_KEY)) {
			Set<String> sets=channel.attr(TID_KEY).get();
			if(sets.contains(tid)) {
				b=true;
			}else {
				b=false;
			}
		}else {
			b=false;
		}
		return b;
	}
	public static void add(Channel channel,String tid) {
		if(channel==null||tid==null)return;
		if(channel.hasAttr(TID_KEY)) {
			channel.attr(TID_KEY).get().add(tid);
		}else {
			Set<String> sets=new HashSet<String>();
			sets.add(tid);
			channel.attr(TID_KEY).set(sets);
		}
	}
	public static Set<String> get(Channel channel){
		if(channel.hasAttr(TID_KEY)) {
			return channel.attr(TID_KEY).get();
		}else {
			return null;
		}
	}
}
