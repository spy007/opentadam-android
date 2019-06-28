# Параметры сборки gradle

* `buildConfigField "String", "HiveProfile"` - Идентификатор приложения
```
Пример: buildConfigField "String", "HiveProfile", "\"38d159c1f6aabed1586344550e68824e1\""
```
* `buildConfigField "String[]", "REST_SERVER"` - Массив адресов сервера для подключения. 
```
Пример: buildConfigField "String[]", "REST_SERVER", "new String[]{\"https://opentadam.com:443\"}"
```

* `manifestPlaceholders.google_maps_key` - Google API Key, необходим для Google Maps
```
Пример: manifestPlaceholders.google_maps_key = "AIzaSyssdf9EHFAXAeVfD-Hlc-_zDkU_TcVDmyM"
```
* `manifestPlaceholders.fabricApiKey` - API Key для [fabric.io](https://fabric.io)
* `ext.fabricApiSecret` - API Secret для [fabric.io](https://fabric.io)
```
Пример: 
manifestPlaceholders.fabricApiKey = "faea78f5d5196477239e5361b6dd2dda630099c0"
ext.fabricApiSecret = "3ac99eac56969307e42233519795d1e3653eca9198e3a3f714213ece56cb4c61"
```
* buildConfigField "Double[]", "defaultLatLon" - Координаты по умолчанию. Используются только, если приложение ни разу не получало координаты от GPS приемника.
```
Пример: buildConfigField "Double[]", "defaultLatLon", "new Double[]{24.991540, 33.370927}"
```

* buildConfigField "boolean", "isChangeColorIPTariffs", "false"
* buildConfigField "int", "limitNumberActiveOrders", "1"
* buildConfigField "String", "URL_POLICY_PRIVACY", "\"https://opentadam.com/policy-privacy\""
* buildConfigField "String", "URL_USER_AGREEMENT", "\"https://opentadam.com/user-agreement\""
