# Геокодинг, поиск адреса

`GET /api/client/mobile/1.0/address/geocoding`

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
query | string | да | Строка поиска



 ## Ответ
 
* **Content-Type**:	application/json; charset=utf-8
* **Body**:	массив объектов [Address](objects.md#address-fields)


При наличии заголовка `X-Hive-GPS-Position` в запросе результат будет отсортирован по удалению от клиента.


