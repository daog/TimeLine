package com.timeline.common;

import java.io.BufferedReader;
import java.io.FileReader;

public class Numeric2ChineseStr {
	static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿", "万亿" };
    static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };

    public static String foematInteger(int num) {
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    // not need process if the last digital bits is 0
                    continue;
                } else {
                    // no unit for 0
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }
    
    public static CharSequence getmSelMonthText( int mSelectedMonth) {
		switch (mSelectedMonth-1) {
		case 0:
			return "一月" ;
		case 1:
			return "二月" ;
		case 2:
			return "三月" ;

		case 3:
			return  "四月" ;

		case 4:
			return  "五月" ;

		case 5:
			return "六月" ;

		case 6:
			return "七月" ;

		case 7:
			return  "八月" ;

		case 8:
			return "九月" ;

		case 9:
			return "十月" ;

		case 10:
			return  "十一月" ;

		case 11:
			return  "十二月" ;
		}
		return null;
	}

    private static String formatDecimal(double decimal) {
        String decimals = String.valueOf(decimal);
        int decIndex = decimals.indexOf(".");
        int integ = Integer.valueOf(decimals.substring(0, decIndex));
        int dec = Integer.valueOf(decimals.substring(decIndex + 1));
        String result = foematInteger(integ) + "." + formatFractionalPart(dec);
        return result;
    }

    private static String formatFractionalPart(int decimal) {
        char[] val = String.valueOf(decimal).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int n = Integer.valueOf(val[i] + "");
            sb.append(numArray[n]);
        }
        return sb.toString();
    }
}
