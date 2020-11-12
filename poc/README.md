### dependencies
Download and untar JDK 16 loom (https://jdk.java.net/16/) 
http://jdk.java.net/loom/

### environment
```
export JAVA_HOME=~/Downloads/jdk-16.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```
#### build
```
mvn clean install
```
### start redis
```
docker-compose up -d
```
### start server
```
./run.sh
```
NOTE: check your ulimits and set them to high values before running client or `ab`
### start client
```
./run-client.sh
```
### simple test 
```
curl -d '{"text":"hi", "userId":"3", "deviceId": "AB"}' 'http://localhost:9090/write' -v
 curl -d '{"userId":"2", "deviceId":"A"}' 'http://localhost:9090/read' -N -o o1.json

```