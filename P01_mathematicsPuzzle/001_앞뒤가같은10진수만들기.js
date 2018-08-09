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

		document.write('10진수: '+num+'<br/>');
		document.write('8진수: '+num.toString(8)+'<br/>');
		document.write('2진수: '+num.toString(2));
		break;
	}
	num += 2;
}

/* 
10진수: 585
8진수: 1111
2진수: 1001001001
*/
