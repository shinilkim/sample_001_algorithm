package sample.algorithm;
/*
# 001 앞뒤가 같은 10진수 만들기

10진수, 2진수, 8진수 그 어느 것으로 표현하여도 대칭수가 되는 수중,
10진수에서 10 이상인 최소값을 구해 보세요.
대칭수: 앞뒤가 같아 거꾸로 읽어도 같은 수 (예: 11)
*/
public class A001 {
	public static void main(String ... args) {
		int num = 11;
		while(true) {
			if( String.valueOf(num).equals(new StringBuilder(String.valueOf(num)).reverse().toString()) &&
					Integer.toBinaryString(num).equals(new StringBuilder(Integer.toBinaryString(num)).reverse().toString()) &&
					Integer.toOctalString(num).equals(new StringBuilder(Integer.toOctalString(num)).reverse().toString())
					) {
				System.out.println("10진수: "+num);
				System.out.println("8진수: "+Integer.toOctalString(num));
				System.out.println("2진수: "+Integer.toBinaryString(num));
				break;
			}
			num += 2;
		}
	}
}
