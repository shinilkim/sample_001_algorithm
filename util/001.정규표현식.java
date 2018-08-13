package ksi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression {
	public static void main(String[] args){
		String searchTarget = "Luke Skywarker 02-123-4567 luke@daum.net\n다스베이더 070-9999-9999 darth_vader@gmail.com\nprincess leia 010 2454 3457 leia@gmail.com";

		Pattern pattern = Pattern.compile("0\\d{1,2}[ -]?\\d{3,4}[ -]?\\d{4}");//여기에 정규표현식을 적습니다.
		Matcher matcher = pattern.matcher(searchTarget);
		while(matcher.find()){
			System.out.println(matcher.group(0));
		}
	}
}
