import Foundation

/// Type-erased Codable wrapper used for freeform API responses.
public struct AnyCodable: Codable {
    public let value: Any

    public init(_ value: Any) { self.value = value }

    public init(from decoder: Decoder) throws {
        let c = try decoder.singleValueContainer()
        if let v = try? c.decode(Bool.self)            { value = v; return }
        if let v = try? c.decode(Int.self)             { value = v; return }
        if let v = try? c.decode(Double.self)          { value = v; return }
        if let v = try? c.decode(String.self)          { value = v; return }
        if let v = try? c.decode([AnyCodable].self)    { value = v.map(\.value); return }
        if let v = try? c.decode([String: AnyCodable].self) {
            value = v.mapValues(\.value); return
        }
        if c.decodeNil() { value = NSNull(); return }
        throw DecodingError.dataCorruptedError(in: c, debugDescription: "Unsupported value")
    }

    public func encode(to encoder: Encoder) throws {
        var c = encoder.singleValueContainer()
        switch value {
        case let v as Bool:              try c.encode(v)
        case let v as Int:               try c.encode(v)
        case let v as Double:            try c.encode(v)
        case let v as String:            try c.encode(v)
        case let v as [Any]:             try c.encode(v.map { AnyCodable($0) })
        case let v as [String: Any]:     try c.encode(v.mapValues { AnyCodable($0) })
        case is NSNull:                  try c.encodeNil()
        default:                         try c.encodeNil()
        }
    }
}

/// Wraps any `Encodable` for use in a heterogeneous context.
struct AnyEncodable: Encodable {
    let wrapped: Encodable
    init(_ wrapped: Encodable) { self.wrapped = wrapped }
    func encode(to encoder: Encoder) throws { try wrapped.encode(to: encoder) }
}
