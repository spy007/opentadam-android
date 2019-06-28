# Способы оплаты

`POST /api/client/mobile/1.0/payment-methods`

Требуется [Авторизация](hmac.md)
#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8
* [Hive-Profile](http_headers.md)

## Ответ

#### HTTP Заголовки
* **Content-Type**: `string` application/json; charset=utf-8

#### Тело ответа
* JSON объекты [CardAdditionRef](#CardAdditionRef-fields), [CardAdditionCallback](#CardAdditionCallback-fields)

<a name="CardAdditionRef-fields"></a>
#### CardAdditionRef
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
redirectUrl | string | mandatory | URL страницы для ввода данных карты
vendor | string | да | короткий идентификатор оператора банковских карт

<a name="CardAdditionCallback-fields"></a>
#### CardAdditionCallback
Имя | Тип | Обязательное | Описание
--- | --- | --- | ---
status | string | да | Статус добавления карты added – Карта добавлена, rejected – Операция откланена банком
