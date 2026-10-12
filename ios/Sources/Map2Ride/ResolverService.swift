import Foundation

/// A resolved destination from the worker.
struct ResolvedDestination: Equatable {
    let lat: Double
    let lng: Double
    let name: String
    let address: String
}

enum ResolveOutcome: Equatable {
    case success(ResolvedDestination)
    case failure(String)
}

private struct ResolveDTO: Decodable {
    let ok: Bool
    let lat: Double?
    let lng: Double?
    let name: String?
    let address: String?
    let error: String?
}

/// Single mapping point: raw JSON -> ResolveOutcome.
/// Port of Android's `ResolveParser` (core/network/Models.kt).
enum ResolveParser {
    static func parse(_ data: Data) -> ResolveOutcome {
        guard let dto = try? JSONDecoder().decode(ResolveDTO.self, from: data) else {
            return .failure("bad-response")
        }
        if dto.ok, let lat = dto.lat, let lng = dto.lng {
            return .success(ResolvedDestination(
                lat: lat, lng: lng,
                name: dto.name ?? "", address: dto.address ?? ""))
        }
        return .failure(dto.error ?? "bad-response")
    }
}

/// Talks to the Map2Ride Cloudflare worker. Resolution lives
/// server-side — the app never reimplements it (same rule as Android).
final class ResolverService {
    static let workerBase = "https://maps2uber.vasilyespana.workers.dev"

    private let session: URLSession

    init(session: URLSession = .shared) {
        self.session = session
    }

    /// GET /api/resolve?url=… — on failure, the link is reported to the
    /// worker's learning-loop intake exactly like Android does.
    func resolve(link: String) async -> ResolveOutcome {
        var components = URLComponents(string: Self.workerBase + "/api/resolve")
        components?.queryItems = [URLQueryItem(name: "url", value: link)]
        guard let url = components?.url else { return .failure("bad-request") }
        do {
            let (data, _) = try await session.data(from: url)
            let outcome = ResolveParser.parse(data)
            if case .failure(let error) = outcome {
                reportFailure(link: link, error: error)
            }
            return outcome
        } catch {
            reportFailure(link: link, error: "network-error")
            return .failure("network-error")
        }
    }

    /// Learning-loop intake: POST /api/resolve-failures
    /// {url, error, source: "ios"}. Fire-and-forget — failures of the
    /// report itself are swallowed, as on Android.
    func reportFailure(link: String, error: String) {
        guard let url = URL(string: Self.workerBase + "/api/resolve-failures") else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try? JSONSerialization.data(
            withJSONObject: ["url": link, "error": error, "source": "ios"])
        session.dataTask(with: request) { _, _, _ in }.resume()
    }
}
