Spring Data Elasticsearch Sample
================================

run
---

    mvn spring-boot:run

profile
-------

### dev-local(default)

this profile is using embedded node



### dev-remote

this profile is using remote Elasticsearch (127.0.0.1:9300)

        mvn spring-boot:run -Drun.profiles=dev-remote

access
------

### show node setting

http://localhost:8080/es/setting


### show installed plugin

http://localhost:8080/es/plugin

### handle plugin(only local node)

http://localhost:8080/form

when using proxy

    mvn spring-boot:run -Drun.jvmArguments="-DproxyHost=your-proxy-host -DproxyPort=your-proxy-port"
