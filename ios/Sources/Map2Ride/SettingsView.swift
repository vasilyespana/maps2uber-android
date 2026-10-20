import SwiftUI

/// Provider settings: the PRIMARY provider links open in first, and
/// the SECOND provider offered next to it for price comparison
/// (Vasily's spec: default Lyft when the primary is Uber; the second
/// can be any provider except the primary).
struct SettingsView: View {
    @ObservedObject var settings: ProviderSettings

    var body: some View {
        Form {
            Section {
                Picker("Ride provider", selection: primaryBinding) {
                    ForEach(RideProviders.selectable) { provider in
                        Text(provider.name).tag(provider.id)
                    }
                }
            } header: {
                Text("Primary provider")
            } footer: {
                Text("Destination links open in this provider first.")
            }

            Section {
                Picker("Second provider", selection: secondBinding) {
                    ForEach(RideProviders.selectable.filter { $0.id != settings.primaryId }) { provider in
                        Text(provider.name).tag(provider.id)
                    }
                }
            } header: {
                Text("Second provider (price comparison)")
            } footer: {
                Text("Shown next to the primary provider so you can open both and compare prices. It can't be the same as the primary provider.")
            }

            Section {
                ForEach(RideProviders.unsupported) { provider in
                    HStack {
                        Text(provider.name)
                        Spacer()
                        Text("Coming soon")
                            .foregroundStyle(.secondary)
                    }
                }
            } header: {
                Text("Not yet supported")
            }

            Section {
                Text("Map2Ride is an independent app. It is not affiliated with, endorsed by, or sponsored by Uber, Lyft, or any other ride provider.")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            } header: {
                Text("About")
            }
        }
        .navigationTitle("Settings")
    }

    private var primaryBinding: Binding<String> {
        Binding(get: { settings.primaryId }, set: { settings.setPrimary($0) })
    }

    private var secondBinding: Binding<String> {
        Binding(get: { settings.secondId }, set: { settings.setSecond($0) })
    }
}
