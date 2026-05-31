#!/bin/bash
# grap_pack 배포 스크립트
# 사용법: bash /home/ubuntu/grap_pack/deploy.sh

JAR_PATH="/home/ubuntu/grap_pack/build/libs/cms-0.0.1-SNAPSHOT.jar"
LOG_PATH="/home/ubuntu/grap_pack/app.log"
PORT=8080

# 기존 프로세스 종료
PID=$(lsof -t -i :$PORT)
if [ -n "$PID" ]; then
    kill -9 $PID
    echo "기존 프로세스(PID: $PID) 강제 종료"
    # 포트가 완전히 해제될 때까지 대기
    while lsof -t -i :$PORT > /dev/null 2>&1; do
        echo "포트 $PORT 해제 대기 중..."
        sleep 1
    done
    echo "포트 $PORT 해제 완료"
else
    echo "실행 중인 프로세스 없음"
fi

# JAR 파일 존재 확인
if [ ! -f "$JAR_PATH" ]; then
    echo "JAR 파일이 없습니다: $JAR_PATH"
    exit 1
fi

# 서버 시작
nohup java -jar $JAR_PATH --spring.profiles.active=qrgen > $LOG_PATH 2>&1 &
echo "서버 시작됨 (PID: $!), 프로필: qrgen"
echo "로그 확인: tail -f $LOG_PATH"
