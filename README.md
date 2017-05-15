<<<<<<< HEAD
#gatewayDataParse


## Build

windows:
```bash
gradlew.bat assemble
```

linux:
```bash
./gradlew assemble
```

输出文件在：
build/distributions/

下面是两个压缩包，两个是等价的，解压其中一个，直接解压到distributions下面。即右皱
后选解压到此处。

## Run

首先修改config.xml，配置网关的ip和端口号，zigbee节点的地址。
在config.xml所有的同级目录下执行：
```bash
java -cp build/distributions/gatewayDataParse-1.0-SNAPSHOT/lib/ -jar build/distributions/gatewayDataParse-1.0-SNAPSHOT/gatewayDataParse-1.0-SNAPSHOT.jar 
```

=======
# agricultureIOT_backend
the agriculture IOT backend,implemented by springboot,JPA,Hibernate,the frontend implemented by AMUSE UI
>>>>>>> branch 'master' of git@github.com:gitpai/agricultureIOT_backend.git
