# Получение количества свободных водителей

`GET /api/client/mobile/1.0/drivers/free`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)
* [X-Hive-GPS-Position](http_headers.md)

Важно наличие заголовка `X-Hive-GPS-Position` в запросе для точного определения службы такси, обслуживающей регион клиента.

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [Reply](#Reply-fields)

<a name="Reply-fields"></a>
#### Reply

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
freeDrivers | number | mandatory | Количество свободных(доступных) водителей в ценовой зоне
