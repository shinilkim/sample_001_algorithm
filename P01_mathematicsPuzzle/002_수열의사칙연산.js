/*
[002_수열의사칙연산.js]
완성된 식을 계산한 결과 '원래 수를 거꾸로 나열한 숫자'가 되는것을
생각해 보겠습니다. 또한, 식의 계산은 수학의 순서로 합니다.
(곱셈과 나눗셈 먼저, 덧셈과 뺄셈은 나중)
1000 ~ 9999 중에서 위의 조건을 만족하는 수를 구해보세요.

예) 5931 -> 5 * 9 * 31 = 1395
언어마다 조금씩 틀리지만 숫자가 0으로 시작하면 8진수로
해석하는 경우도 있으니 주의해야 합니다.
*/


var op = ["+", "-", "*", "/", ""];
for( i = 1000; i < 10000; i++ ) {
	var c = String(i);
	for( j = 0; j < op.length; j++ ) {
		for( k = 0; k < op.length; k++ ) {
			for( l = 0; l < op.length; l++ ) {
				val = c.charAt(3) + op[j] + c.charAt(2) + op[k] + c.charAt(1) + op[l] + c.charAt(0);
				if( val.length > 4 ) {
					if( i == eval(val) ) {
						console.log(val + " = " + i);
					}
				}
			}
		}
	}
}

/* 출력결과
5*9*31 = 1395
*/
