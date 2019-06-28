# История поездок

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Параметры запроса
Передаются в формате `key=value`

Имя | Тип | Обязательный | Описание 
--- | --- | --- | ---
offset | number | нет | Смещение в истории (количество поездок), если не указано – 0
length | number | нет | Максимальный размер ответа (количества поездок), это положительное число не может быть больше 16. Если не указано – 8

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* Массив JSON объектов [ShortOrderInfo](#ShortOrderInfo-fields)
Объекты будут отсортированы в обратном хронологическом порядке.

<a name="ShortOrderInfo-fields"></a>
#### ShortOrderInfo
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
id | number | true | Идентификатор заказа
state | number [OrderState](#OrderState-enum) | да | Статус заказа
route | массив объектов [Address](objects.md#Address-fields) | да | Маршрут заказа
time | string [OffsetDateTime](objects.md#OffsetDateTime-item) | нет | Время завершения заказа

<a name="OrderState-enum"></a>
#### OrderState
Акроним | Описание
--- | ---
5 | успешно завершен
6 | отменен
