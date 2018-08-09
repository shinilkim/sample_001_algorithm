/*
# 001 앞뒤가 같은 10진수 만들기

10진수, 2진수, 8진수 그 어느 것으로 표현하여도 대칭수가 되는 수중,
10진수에서 10 이상인 최소값을 구해 보세요.
대칭수: 앞뒤가 같아 거꾸로 읽어도 같은 수 (예: 11)
*/

/* 문자열 형식으로 역순을 반환하는 메소드를 추가 */
String.prototype.reverse = function() {
	return this.split("").reverse().join("");
}

/* 11부터 탐색 시작 */
var num = 11;
while (true)
{
	if((num.toString() == num.toString().reverse()) &&
		(num.toString(8) == num.toString(8).reverse()) &&
		(num.toString(2) == num.toString(2).reverse())) {

		console.log('10진수: '+num);
		console.log('8진수: '+num.toString(8));
		console.log('2진수: '+num.toString(2));
		break;
	}
	num += 2;
}

/* 
10진수: 585
8진수: 1111
2진수: 1001001001
*/
