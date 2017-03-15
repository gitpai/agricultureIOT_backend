#gatewayDataParse


## Build

windows:
```bash
gradlew.bat jar
```

linux:
```bash
./gradlew jar
```

output file will be:
build/libs/gateway-1.0-SNAPSHOT.jar

## Run

首先修改config.xml，配置网关的ip和端口号，zigbee节点的地址。
在config.xml所有的同级目录下执行：
```bash
java -jar build/libs/gateway-1.0-SNAPSHOT.jar 
```