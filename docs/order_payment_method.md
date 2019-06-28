# Редактирование способа оплаты в заказе

`POST /api/client/mobile/1.0/orders/{id}/payment-method`

где `{id}` – идентификатор заказа

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

#### Тело запроса
* JSON объект [Params](#Params-fields)

<a name="Params-fields"></a>
#### Params
Имя | Тип | Обязательный | Описание 
--- | --- | --- | --- 
paymentMethod | объект [PaymentMethod](objects.md#PaymentMethod-fields) | mandatory | Способ оплаты

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### тело ответа
* Пустой
