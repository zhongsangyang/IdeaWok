package com.cn.flypay.utils.pingan;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;

public class CopyOfFilecompare {
	static ConcurrentHashMap<Integer, Integer> m = new ConcurrentHashMap<Integer, Integer>();

	public static void main(String[] args) throws IOException {
		Integer k = 3;
		Integer k2 = 4;
		Integer k3 = 5;
		System.out.println(k.hashCode());
		System.out.println(k2.hashCode());
		System.out.println(k3.hashCode());
		HashMap<String, String> t = new HashMap<>();
		String s = t.put("sun", "test");
		String s1 = t.put("sun", "test2");
		System.out.println(s1);
	}

}
