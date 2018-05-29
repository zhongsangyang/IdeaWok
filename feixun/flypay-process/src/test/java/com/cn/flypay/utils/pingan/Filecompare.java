package com.cn.flypay.utils.pingan;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Filecompare {

	public static void main(String[] args) throws IOException {
		List<String> sl = FileUtils.readLines(new File("d:/test/1.txt"), "utf-8");
		List<String> sl2 = FileUtils.readLines(new File("d:/test/2.txt"), "utf-8");
		for (String s : sl) {
			if (sl2.contains(s+"test")) {
				
			}else{
				System.out.println(s);
			}
		}

	}

}
