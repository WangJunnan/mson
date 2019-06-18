# MSON json解析工具

## 使用方式

```java
// jsonStr ----> Object
T t = JSON.parse(clazz<T>, text);

// jsonStr ----> List
List<T> list = JSON.parseArray(clazz<T>, text)

// to JSONString
JSON.toJSONString(obj);
```