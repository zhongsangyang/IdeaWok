package com.cn.flypay.utils;

import java.util.Random;

/**
 * 直通车科目类
 * @author LW
 *
 */
public class TroughUtil {
	
	
	public static String TroughZFB(){
		String zfb[] = {
                "2015050700000000","2015091000052157","2015062600004525",
                "2015062600002758","2016062900190124","2015063000020189",
                "2016042200000148","2016062900190296","2015080600000001",
                "2016062900190337","2016062900190371"};
		int len = zfb.length;
		Random random = new Random();
		int index = random.nextInt(len-1);
		return zfb[index];
	}
	
	public static String TroughWX(){
	  String wx[] = {
				"292","153","209","210","116","129",
				"293","294","295","296","297","298",
				"305","319","323","123","299","306",
				"320","321","143","157","300","148",
				"149","301","307","308","302","303",
				"304","147","230","322","324","155",
				"309","242","158","273","289","311",
				"312","42","93","94","95","280","24"};
		int len = wx.length;
		Random random = new Random();
		int index = random.nextInt(len-1);
		return wx[index];
	}
	
	
	public static String TroughYIZF(){
		  String yi[] = {
					"5812","5399","5331","5311","5411","5462",
					"4812","5611","5699","5499","5422","5722",
					"5641","5945","5712","5719","5912","5511",
					"5943","7995","7230","8043","5947","5970",
					"7299","7622","7997","7216","8011","5467",
					"5992","7221","7932","7261","7297","7221",
					"4722","5466","7941","7273","7295","5940",
					"7011","5942","5814","7277","7338","5733","5971"};
			int len = yi.length;
			Random random = new Random();
			int index = random.nextInt(len-1);
			return yi[index];
		}
	
	
	public static String YIZFProvince(){
		  String yi[] = {
					"33","32","11","12","31","50",
					"52","13","14","15","21","23",
					"32","33","34","35","36","37",
					"41","44","45","46","51","52",
					"53","54","61","62","63","41",
					"42","43","44","63","64","65"};
			int len = yi.length;
			Random random = new Random();
			int index = random.nextInt(len-1);
			return yi[index];
		}
	
	
	
	public static String YIZFCity(){
		  String yi[] = {"01","02","03","04","05","06","07","08"};
			int len = yi.length;
			Random random = new Random();
			int index = random.nextInt(len-1);
			return yi[index];
		}
	
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(TroughUtil.TroughWX());
		}
	}

}
