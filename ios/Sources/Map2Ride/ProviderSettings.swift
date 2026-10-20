import Foundation

/// Primary + second ride-provider selection, persisted in UserDefaults.
///
/// Mirrors the Android v1.5.0 settings semantics and Vasily's
/// second-provider spec (rework brief, 2026-10-08):
///
/// - The primary provider defaults to Uber.
/// - The SECOND provider (used for price comparison) defaults to Lyft
///   when the primary is Uber, and can be changed to any other
///   selectable provider EXCEPT the primary.
/// - Setting the second provider equal to the primary is rejected and
///   the second provider shifts to the default non-primary option.
/// - Changing the primary onto the current second provider re-homes
///   the second provider to a valid non-primary default instead of
///   colliding.
final class ProviderSettings: ObservableObject {
    @Published private(set) var primaryId: String {
        didSet { defaults.set(primaryId, forKey: Keys.primary) }
    }
    @Published private(set) var secondId: String {
        didSet { defaults.set(secondId, forKey: Keys.second) }
    }

    private let defaults: UserDefaults

    private enum Keys {
        static let primary = "ride_provider_id"
        static let second = "second_ride_provider_id"
    }

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
        let storedPrimary = defaults.string(forKey: Keys.primary)
        let primary = RideProviders.byId(storedPrimary ?? RideProviders.defaultId)?.id
            ?? RideProviders.defaultId
        self.primaryId = primary
        let storedSecond = defaults.string(forKey: Keys.second)
        if let storedSecond,
           let provider = RideProviders.byId(storedSecond),
           provider.deepLinkSupported, storedSecond != primary {
            self.secondId = storedSecond
        } else {
            self.secondId = Self.defaultSecond(forPrimary: primary)
        }
    }

    /// The current primary provider.
    var primary: RideProvider { RideProviders.byId(primaryId) ?? RideProviders.default }

    /// The current second (price-comparison) provider.
    var second: RideProvider { RideProviders.byId(secondId) ?? RideProviders.default }

    /// The default second provider for a given primary: Lyft, unless
    /// the primary IS Lyft — then Uber.
    static func defaultSecond(forPrimary primary: String) -> String {
        primary == "lyft" ? "uber" : "lyft"
    }

    /// Persists the primary provider; unknown ids fall back to Uber
    /// (same rule as Android's `setRideProvider`). If the new primary
    /// equals the current second provider, the second provider
    /// auto-shifts to the default non-primary option.
    func setPrimary(_ id: String) {
        let valid = RideProviders.byId(id)?.id ?? RideProviders.defaultId
        primaryId = valid
        if secondId == valid {
            secondId = Self.defaultSecond(forPrimary: valid)
        }
    }

    /// Persists the second provider. The id must exist, be selectable,
    /// and differ from the primary; otherwise the second provider
    /// shifts to the default non-primary option.
    func setSecond(_ id: String) {
        guard let provider = RideProviders.byId(id),
              provider.deepLinkSupported, id != primaryId else {
            secondId = Self.defaultSecond(forPrimary: primaryId)
            return
        }
        secondId = id
    }
}
