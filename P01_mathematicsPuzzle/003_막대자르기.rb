'''
# 003_막대자르기.rb

길이 n[cm]의 한 막대를 1[cm] 단위로 자른다고 생각해 봅시다.
단, 하나의 막대는 한 번에 한 사람만이 자를 수 있습니다.
잘린 막대가 3개 이상이 되면, 동시에 3명이 자를 수 있습니다.
최대 m 명이 있을 때 막대를 자르는 최소 횟수를 구해보세요.
예를 들면 n = 8, m = 3

문제1: n = 20, m = 3 일 때의 횟수를 구해 보세요.
문제2: n 100, m = 5일 때의 횟수를 구해 보세요.
'''
def cutbar(m, n, current) # current: 현재 막대의 개수
  if current >= n then
    0 # 잘라내기 완료
  elsif current < m then
    1 + cutbar(m, n, current * 2) # 다음은 현재의 2배가 된다.
  else
    1 + cutbar(m, n, current + m) # 가위 수만큼 추가
  end
end

puts cutbar(3, 20, 1)
puts cutbar(5, 100, 1)

puts '-------------------'

def cutbar2(m,n)
  count = 0
  current = 1 # 현재의 길이
  while n > current do
    current += current < m ? current : m
    count = count + 1
  end
  puts(count)
end

cutbar2(3, 20)
cutbar2(5,100)
