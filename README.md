# sample_001_algorithm
폴리그랏 프로그래밍 알고리즘

# 인증서 만들기
인증서 다운로드 : http://slproweb.com/products/Win32OpenSSL.html
기본설치 디렉토리 : C:\OpenSSL-Win64\bin (PATH에 추가하자)

1. 개인키(비밀키) 생성하기
- 명령어 : openssl genrsa -aes256 -out privatekey.pem 2048
- privatekey.pem 개인키(비밀키)가 생성된다.

2. 공개키 생성
- openssl req -new -key privatekey.pem -out netty.csr
- netty.csr 파일이 생성되었다.

3. 전사서명으로 인증서 만들기
openssl x509 -in netty.csr -out netty.crt -req -signkey privatekey.pem -days 35600
- 10년짜리 인증서가 netty.crt 만들어졌다.

4. 인증서와 개인키의 정합성 검증
- openssl x509 -noout -text -in ./netty.crt
- openssl rsa -noout -text -in privatekey.pem

5. ANS.1 > PKCS#8 형식의 개인키 만들기
- openssl pkcs8 -topk8 -inform PEM -outform PEM -in privatekey.pem -out privatekey.pkcs8.pem
