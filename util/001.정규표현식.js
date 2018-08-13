var searchTarget = "Luke Skywarker 02-123-4567 luke@daum.net\n다스베이더 070-9999-9999 darth_vader@gmail.com\nprincess leia 010 2454 3457 leia@gmail.com";

/* 아래 코드의 /와 /g가운데에 정규표현식을 넣으세요.
 * g는 global의 약자로, 정규표현식과 일치하는 모든 내용을 찾아오라는 옵션입니다.
 * g가 있을 때와 없을 때 출력이 차이나는걸 확인 해 보세요.
 */
var regex = /\d+/g; 
//var regex = /여기에 정규표현식을 입력하세요/g
console.log(searchTarget.match(regex));
