'''
# 005_동전교환.rb

문제
- 1000원 지폐를 넣을을 때 나오는 동전의 조합이 몇 가지인지 구해보세요.
- 동전의 순서는 무시한다.
- 동전은 10원, 50원, 100원, 500원
'''

# method 1
cnt = 0
(0..2).each do |coin500|
  (0..10).each do |coin100|
    (0..15).each do |coin50|
      (0..15).each do |coin10|
        if coin500 + coin100 + coin50 + coin10 <= 15 then
          if coin500*500 + coin100*100 + coin50*50 + coin10*10 === 1000 then
            cnt += 1
          end
        end
      end
    end
  end
end
print 'cnt of method1: %d' % cnt
puts

# method 2
cnt = 0
coins = [10,50,100,500]
(2..15).each do |i|
  coins.repeated_combination(i).each do |coin_set|
    cnt += 1 if coin_set.inject(:+) == 1000 # inject(:+) 배열 요소의 합계
  end
end
print 'cnt of method2: %d' % cnt
puts

# method 3
@cnt = 0
def change(target, coins, usable)
  coin = coins.shift
  if coins.size == 0 then
    @cnt += 1 if target / coin <= usable
  else
    (0..target/coin).each do |i|
      change(target - coin * i, coins.clone, usable-i)
    end
  end
end
change(1000, [500,100,50,10], 15)
print 'cnt of method3: %d' % @cnt
