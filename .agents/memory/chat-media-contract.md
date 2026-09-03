---
name: Chat media contract
description: The Partner API contract for messaging webhooks and chat media attachments.
---

Subscribe to new booking-chat messages with the `chat.message_added` webhook
event. Verify deliveries against the raw request body using HMAC-SHA256 and the
`X-Webhook-Signature` header.

Chat media is received through message attachment metadata. A media message has
`message_type: "media"` and `attachments` containing `type`, `url`, and optional
`thumb_url`; supported observed/documented types include `image`, `video`, and
`sound`.

**Why:** Typed SDK models that omitted attachments silently discarded photo and
video URLs even though signature verification and webhook registration worked.
The public chat POST currently accepts only a text `message`.

**How to apply:** Preserve attachment URLs in chat response and webhook payload
handling. Do not invent a media-upload or media-send operation until one appears
in the public Partner API contract.