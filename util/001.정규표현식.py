import re

class RegularExpression:
	# 전화번호를 찾는 정규표현식
	@staticmethod
	def t001(regex, search_target):
		return re.findall(regex, search_target)

if __name__=="__main__":
	# \d : digit 줄임말로 숫자만 추출
	# \w : 문자 및 숫자만 추출
	# + : 하나 혹은 그 이상 연결
	# [1-9]\d : 1~9 까지의 숫자 추출
	# ? : 있거나 없거나
	# \d{2} : 숫자가 2자리인 경우 추출
	# \d{2,3} : 숫자가 2-3자리인 경우 추출
	regex = r'0\d{1,2}[ -]?\d{3,4}[ -]?\d{4}' # 전화번호 찾는 정규표현식
	search_target = '''Luke Skywarker 02-123-4567 luke@daum.net
		다스베이더 070-9999-9999 darth_vader@gmail.com
		princess leia 010 2454 3457 leia@gmail.com 123-456-7890 
		0030589-5-95826 333-333-4444 ㄱ ㄴ
		'''

	# r'문자열' : raw string 으로 \\ 를 \ 로 편하게 사용하게 해줌
	print(RegularExpression.t001(regex, search_target)) 
	print(RegularExpression.t001(r'\w+', search_target)) # 문자 및 숫자만
	print(RegularExpression.t001(r'\d+-?\d+-?\d+', search_target)) # 숫자조건
	print(RegularExpression.t001(r'\d+[- ]?\d+[- ]?\d+', search_target)) # 숫자조건
	print(RegularExpression.t001(r'\d{2}[- ]?\d{3}[- ]?\d{4}', search_target)) # 숫자조건
	print(RegularExpression.t001(r'[aeiou]', search_target)) # 해당 문자만 추출
	print(RegularExpression.t001(r'[a-z]+', search_target)) # 소문자 a-z 까지 추출
	print(RegularExpression.t001(r'[가-힣]+', search_target)) # 한글만 추출
	print(RegularExpression.t001(r'\s+', search_target)) # 공백문자(스페이스, 탭, 뉴라인)
	print(RegularExpression.t001(r'\S+', search_target)) # 공백문자를 제외한 문자
	print(RegularExpression.t001(r'\D+', search_target)) # 숫자를 제외한 문자
	print(RegularExpression.t001(r'\W+', search_target)) # 글자 대표문자를 제외한 글자들(특수문자, 공백 등)
