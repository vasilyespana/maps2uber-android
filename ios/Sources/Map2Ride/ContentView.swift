import SwiftUI
import UIKit

/// The resolve-and-go loop: paste a maps link → the worker resolves it
/// to a destination → open the destination in the primary provider,
/// the second provider, or both (and compare prices before booking).
struct ContentView: View {
    @StateObject private var settings = ProviderSettings()
    @State private var linkText = ""
    @State private var isResolving = false
    @State private var destination: ResolvedDestination?
    @State private var errorText: String?
    @State private var openError: String?
    @State private var clipboardHasLink = false
    @Environment(\.openURL) private var openURL

    private let service = ResolverService()

    init() {
        // UI-test stub: a pre-resolved demo destination so the result
        // screen can be screenshot-tested without network access.
        // Demo data only — never shown outside the -uitest-stub launch.
        if ProcessInfo.processInfo.arguments.contains("-uitest-stub") {
            _destination = State(initialValue: ResolvedDestination(
                lat: 18.4588, lng: -66.0706,
                name: "Condado (demo)", address: "Ashford Ave, San Juan, Puerto Rico"))
        }
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text("Paste a Google Maps link and open the destination in your ride app — in two of them, so you can compare prices.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)

                    VStack(alignment: .leading, spacing: 10) {
                        TextField("Maps link", text: $linkText, axis: .vertical)
                            .lineLimit(1...3)
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()
                            .keyboardType(.URL)
                            .padding(10)
                            .background(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color.secondary.opacity(0.4)))
                            .accessibilityIdentifier("linkField")

                        HStack {
                            Button("Paste from clipboard") { pasteFromClipboard() }
                                .buttonStyle(.bordered)
                            Spacer()
                            Button(isResolving ? "Resolving…" : "Resolve") {
                                Task { await resolve() }
                            }
                            .buttonStyle(.borderedProminent)
                            .disabled(isResolving
                                || linkText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                        }

                        if clipboardHasLink && linkText.isEmpty {
                            Text("A link is on your clipboard — tap “Paste from clipboard”.")
                                .font(.footnote)
                                .foregroundStyle(.secondary)
                        }
                    }

                    if let errorText {
                        Label(errorText, systemImage: "exclamationmark.triangle.fill")
                            .font(.subheadline)
                            .foregroundStyle(.red)
                    }

                    if let destination {
                        resultCard(destination)
                    }
                }
                .padding()
            }
            .navigationTitle("Map2Ride")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    NavigationLink {
                        SettingsView(settings: settings)
                    } label: {
                        Image(systemName: "gearshape")
                    }
                    .accessibilityLabel("Settings")
                }
            }
            .onAppear { refreshClipboardHint() }
        }
    }

    // MARK: - Result card

    @ViewBuilder
    private func resultCard(_ dest: ResolvedDestination) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Label("Destination", systemImage: "mappin.and.ellipse")
                .font(.headline)
            Text(dest.name.isEmpty ? "Dropped pin" : dest.name)
                .font(.title3)
                .fontWeight(.semibold)
            if !dest.address.isEmpty {
                Text(dest.address)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            Text(String(format: "%.6f, %.6f", dest.lat, dest.lng))
                .font(.footnote)
                .foregroundStyle(.secondary)

            Button {
                openProvider(settings.primary, for: dest)
            } label: {
                Label("Open in \(settings.primary.name)", systemImage: "car.fill")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)

            Button {
                openProvider(settings.second, for: dest)
            } label: {
                Label("Open in \(settings.second.name)", systemImage: "car")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.bordered)

            Text("Tip: open both and compare the prices before you book — pickup zones can change the fare.")
                .font(.footnote)
                .foregroundStyle(.secondary)

            if let openError {
                Text(openError)
                    .font(.footnote)
                    .foregroundStyle(.red)
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(uiColor: .secondarySystemBackground)))
    }

    // MARK: - Actions

    private func resolve() async {
        let link = linkText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !link.isEmpty else { return }
        isResolving = true
        errorText = nil
        openError = nil
        let outcome = await service.resolve(link: link)
        isResolving = false
        switch outcome {
        case .success(let dest):
            destination = dest
        case .failure(let error):
            destination = nil
            errorText = Self.friendlyError(error)
        }
    }

    /// Opens the provider for the destination, following the same
    /// fallback order as Android: native deep link (when the app is
    /// installed) → universal web URL → App Store page.
    private func openProvider(_ provider: RideProvider, for dest: ResolvedDestination) {
        openError = nil
        let target = RideLatLng(lat: dest.lat, lng: dest.lng)
        let displayName = dest.name.isEmpty ? "Destination" : dest.name
        if let deep = provider.buildDeepLink(nil, target, displayName),
           let url = URL(string: deep),
           UIApplication.shared.canOpenURL(url) {
            openURL(url)
            return
        }
        if let web = provider.buildUniversalUrl(target), let url = URL(string: web) {
            openURL(url)
            return
        }
        if let storeId = provider.appStoreId,
           let url = URL(string: "https://apps.apple.com/app/id\(storeId)") {
            openURL(url)
            return
        }
        openError = "\(provider.name) can't be opened on this phone."
    }

    private func pasteFromClipboard() {
        if let text = UIPasteboard.general.string {
            linkText = text
        }
        refreshClipboardHint()
    }

    /// `hasURLs` checks the clipboard without reading it, so no system
    /// paste banner appears just from opening the app.
    private func refreshClipboardHint() {
        clipboardHasLink = UIPasteboard.general.hasURLs
    }

    private static func friendlyError(_ error: String) -> String {
        switch error {
        case "network-error":
            return "Network error. Check your connection and try again."
        case "bad-response", "bad-request":
            return "That link could not be resolved. Try copying it from Maps again."
        default:
            return error
        }
    }
}
