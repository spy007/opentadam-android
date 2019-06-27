# Геокодинг, поиск адресов

`GET /api/client/mobile/1.0/address/geocoding`

#### HTTP Заголовки
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
query | string | да | Строка поиска


 ## Ответ
 
#### HTTP Заголовки
* **Content-Type**:	`strings` application/json; charset=utf-8

#### Тело ответа
* массив объектов [Address](objects.md#address-fields)


При наличии заголовка `X-Hive-GPS-Position` в запросе результат будет отсортирован по удалению от клиента.


