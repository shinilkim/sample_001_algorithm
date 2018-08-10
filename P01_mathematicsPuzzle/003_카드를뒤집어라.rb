'''
# 003_카드를뒤집어라.rb

1~100의 번호가 쓰인 100장의 카드가 순서대로 나열되어 있습니다.
모든 카드는 뒷면이 위를 향한 상태로 놓여 있습니다.
한 사람이 2번 카드부터 1장 간격으로 카드를 뒤집어 나갑니다.
그러면 2,4,6...100번 카드의 앞면이 나타나게 됩니다.
그다음로, 다른 사람이 3번카드부터 2장 간격으로 카드를 뒤집어 나갑니다.
다시 다른 사람이 4번 카드부터 3장 간격으로 카드를 뒤집어 나갑니다.
이렇게 n번째 크다부터 n - 1 장 간격으로 카드를 뒤집는 작업을 뒤집을
카드가 더 없을 때까지 계속하다고 합니다.
[문제] 뒤집을 카드가 더 없게 되었을 때 뒷면이 위를 향산 카드의
번호를 모두 구해 보세요.
'''

# 카드의 초기화
N = 100
cards = Array.new(N, false)

# 2 ~ N 까지 뒤집음
(2..N).each do |i|
  j = i - 1
  while(j<cards.size) do
    cards[j] = !cards[j]
    j += i
  end
end

# 뒷면이 위를 향한 카드를 출력
N.times do |i|
  puts i + 1 if !cards[i]
end

''' 출력결과
1
4
9
16
25
36
49
64
81
100
'''
