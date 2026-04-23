import Foundation

/// The main entry point for the Cleanster Partner API SDK.
///
/// Create a sandbox client for development and testing:
/// ```swift
/// let client = CleansterClient.sandbox(accessKey: "your-access-key")
/// client.setToken("user-jwt")
/// ```
///
/// Create a production client when going live:
/// ```swift
/// let client = CleansterClient.production(accessKey: "your-access-key")
/// ```
public final class CleansterClient {

    // MARK: - Environments

    public static let sandboxBaseURL    = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public"
    public static let productionBaseURL = "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public"

    // MARK: - Internal state

    let accessKey: String
    var token: String
    let baseURL: String
    let session: NetworkSession

    // MARK: - API service namespaces

    public lazy var bookings:       BookingsApi       = BookingsApi(client: self)
    public lazy var properties:     PropertiesApi     = PropertiesApi(client: self)
    public lazy var users:          UsersApi          = UsersApi(client: self)
    public lazy var checklists:     ChecklistsApi     = ChecklistsApi(client: self)
    public lazy var paymentMethods: PaymentMethodsApi = PaymentMethodsApi(client: self)
    public lazy var webhooks:       WebhooksApi       = WebhooksApi(client: self)
    public lazy var blacklist:      BlacklistApi      = BlacklistApi(client: self)
    public lazy var other:          OtherApi          = OtherApi(client: self)

    // MARK: - Init

    public init(
        accessKey: String,
        token: String = "",
        baseURL: String,
        session: NetworkSession = URLSession.shared
    ) {
        self.accessKey = accessKey
        self.token     = token
        self.baseURL   = baseURL
        self.session   = session
    }

    /// Create a client targeting the **sandbox** environment.
    public static func sandbox(accessKey: String, session: NetworkSession = URLSession.shared) -> CleansterClient {
        CleansterClient(accessKey: accessKey, baseURL: sandboxBaseURL, session: session)
    }

    /// Create a client targeting the **production** environment.
    public static func production(accessKey: String, session: NetworkSession = URLSession.shared) -> CleansterClient {
        CleansterClient(accessKey: accessKey, baseURL: productionBaseURL, session: session)
    }

    /// Set the per-user JWT token. Call this after fetching a token via `users.fetchAccessToken(_:)`.
    public func setToken(_ token: String) {
        self.token = token
    }

    // MARK: - Core request builder

    func request<T: Decodable>(
        method: String,
        path: String,
        queryItems: [URLQueryItem]? = nil,
        body: Encodable? = nil
    ) async throws -> ApiResponse<T> {
        var urlString = baseURL + path
        if let items = queryItems, !items.isEmpty {
            var comps = URLComponents(string: urlString)!
            comps.queryItems = items
            urlString = comps.url!.absoluteString
        }
        guard let url = URL(string: urlString) else {
            throw CleansterError.invalidURL(urlString)
        }

        var req = URLRequest(url: url)
        req.httpMethod = method
        req.setValue(accessKey, forHTTPHeaderField: "access-key")
        req.setValue(token, forHTTPHeaderField: "token")
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("application/json", forHTTPHeaderField: "Accept")

        if let body {
            req.httpBody = try JSONEncoder().encode(AnyEncodable(body))
        }

        let (data, response) = try await session.data(for: req)

        guard let http = response as? HTTPURLResponse else {
            throw CleansterError.invalidResponse
        }

        let decoded = try JSONDecoder().decode(ApiResponse<T>.self, from: data)

        if http.statusCode == 401 {
            throw CleansterError.unauthorized
        }
        if http.statusCode >= 400 {
            throw CleansterError.apiError(statusCode: http.statusCode, message: decoded.message)
        }

        return decoded
    }

    /// Convenience for endpoints that return `[String: AnyCodable]` data.
    func requestRaw(
        method: String,
        path: String,
        queryItems: [URLQueryItem]? = nil,
        body: Encodable? = nil
    ) async throws -> ApiResponse<AnyCodable> {
        try await request(method: method, path: path, queryItems: queryItems, body: body)
    }
}
