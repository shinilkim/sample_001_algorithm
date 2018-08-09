'''
# 001 앞뒤가 같은 10진수 만들기

10진수, 2진수, 8진수 그 어느 것으로 표현하여도 대칭수가 되는 수중,
10진수에서 10 이상인 최소값을 구해 보세요.
대칭수: 앞뒤가 같아 거꾸로 읽어도 같은 수 (예: 11)
'''

num = 11
while true
  if num.to_s == num.to_s.reverse &&
      num.to_s(8) == num.to_s(8).reverse &&
      num.to_s(2) == num.to_s(2).reverse
    print '10진수: %d' % num
    puts ''
    print '8진수: %d' % num.to_s(8)
    puts ''
    print '2진수: %d' % num.to_s(2)
    break
  end
  # 홀수만 탐색하므로 2씩 늘림
  num += 2
end

'''
결과

10진수: 585
8진수: 1111
2진수: 1001001001
'''
