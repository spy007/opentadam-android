# Подробный маршрут поездки

`GET /api/client/mobile/1.0/orders/{id}/path`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объект [Route](#Route-fields)

<a name="Route-fields"></a>
#### Route

Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
distance | number | да | Расстояние
path | object [GeoJSON::LineString](https://tools.ietf.org/html/rfc7946#section-3.1.4) | да | Точки маршрута

