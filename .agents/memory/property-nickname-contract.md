---
name: Property nickname contract
description: The Partner API field and casing used for a property's custom nickname.
---

Property create and update payloads accept an optional string field named
`nickName`. Property response models should expose the same value.

**Why:** The public Postman collection documents `nickName` for both
`POST /v1/properties` and `PUT /v1/properties/{propertyId}`. The capital `N`
is part of the wire contract; `nickname` and `nick_name` are not the JSON key.

**How to apply:** SDKs may expose an idiomatic language-level property name,
but serialization and deserialization must map it to the exact JSON key
`nickName`.